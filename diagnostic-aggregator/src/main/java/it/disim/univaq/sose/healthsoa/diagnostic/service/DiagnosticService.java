package it.disim.univaq.sose.healthsoa.diagnostic.service;

import it.disim.univaq.sose.healthsoa.diagnostic.client.ImagingClient;
import it.disim.univaq.sose.healthsoa.diagnostic.client.LaboratorioClient;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticBundle;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabCallbackRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabStatusDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TestResultDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TrackingStatusDto;
import it.disim.univaq.sose.healthsoa.diagnostic.model.TrackingEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DiagnosticService {

    private final LaboratorioClient laboratorioClient;
    private final ImagingClient imagingClient;

    /** URL base di questo aggregatore, usata per costruire il callback verso il laboratorio. */
    @Value("${diagnostic.aggregator.callback-base-url:http://localhost:9201}")
    private String callbackBaseUrl;

    /** Stato in-memory degli ordini diagnostici: trackingId → TrackingEntry. */
    private final ConcurrentHashMap<String, TrackingEntry> trackingMap = new ConcurrentHashMap<>();

    public DiagnosticService(LaboratorioClient laboratorioClient, ImagingClient imagingClient) {
        this.laboratorioClient = laboratorioClient;
        this.imagingClient = imagingClient;
    }

    /**
     * Ordina un pannello di esami per un paziente.
     * 1. Delega a laboratorio-service (POST /tests/orders) → ottiene labOrderId.
     * 2. Registra callback sul laboratorio puntando a /internal/callback/{trackingId}.
     * 3. Recupera i referti di imaging già archiviati (sincrono).
     * 4. Crea TrackingEntry in-memory e ritorna trackingId al client.
     */
    public DiagnosticOrderResponse orderDiagnostics(String patientId, DiagnosticOrderRequest request) {
        String panelCode = request.getPanelCode() != null ? request.getPanelCode() : "PANEL_RENAL";

        LabOrderResponse labResponse = laboratorioClient.submitOrder(
                new LabOrderRequest(patientId, panelCode));
        Long labOrderId = labResponse.getOrderId();

        String trackingId = UUID.randomUUID().toString();

        TrackingEntry entry = new TrackingEntry(trackingId, patientId, panelCode, labOrderId);

        String callbackUrl = callbackBaseUrl + "/internal/callback/" + trackingId;
        laboratorioClient.registerCallback(labOrderId, new LabCallbackRequest(callbackUrl));

        List<ImagingReportDto> imagingReports = imagingClient.getReports(patientId, panelCode);
        entry.setImagingReports(imagingReports);

        trackingMap.put(trackingId, entry);

        return new DiagnosticOrderResponse(trackingId, "PENDING",
                "Ordine accettato. Usa GET /tracking/" + trackingId + "/status per il polling.");
    }

    /**
     * Stato corrente di un ordine diagnostico (per polling da parte del client).
     * Sincronizza lazily con il laboratorio se l'ordine non è ancora completato:
     * garantisce aggiornamento anche se la callback best-effort non è arrivata.
     */
    public TrackingStatusDto getStatus(String trackingId) {
        TrackingEntry entry = findEntry(trackingId);
        if (!"COMPLETED".equals(entry.getStatus())) {
            syncWithLab(entry);
        }
        return new TrackingStatusDto(trackingId, entry.getPatientId(),
                entry.getPanelCode(), entry.getStatus());
    }

    /**
     * Risultato completo dell'ordine diagnostico.
     * Disponibile solo se lo stato è COMPLETED.
     * Ritorna un DiagnosticBundle con esito di laboratorio + referti di imaging.
     */
    public DiagnosticBundle getResult(String trackingId) {
        TrackingEntry entry = findEntry(trackingId);
        if (!"COMPLETED".equals(entry.getStatus())) {
            syncWithLab(entry);
        }
        if (!"COMPLETED".equals(entry.getStatus())) {
            throw new OrderNotCompletedException(trackingId, entry.getStatus());
        }
        return new DiagnosticBundle(entry.getPatientId(), entry.getTestResult(),
                entry.getImagingReports());
    }

    /**
     * Sincronizzazione lazy con il laboratorio: interroga il lab direttamente per
     * aggiornare lo stato della TrackingEntry. Usato come fallback quando la callback
     * best-effort del laboratorio non è stata ricevuta (race condition su callbackUrl
     * nella transazione del lab o errori di rete transitori).
     */
    private void syncWithLab(TrackingEntry entry) {
        try {
            LabStatusDto labStatus = laboratorioClient.getStatus(entry.getLabOrderId());
            entry.setStatus(labStatus.getStatus());
            if ("COMPLETED".equals(labStatus.getStatus()) && entry.getTestResult() == null) {
                TestResultDto result = laboratorioClient.getResult(entry.getLabOrderId());
                entry.setTestResult(result);
            }
        } catch (Exception ignored) {
            // Polling best-effort: un errore transatorio non deve bloccare la risposta.
        }
    }

    /**
     * Riceve la callback dal laboratorio quando l'ordine è COMPLETED.
     * Aggiorna la TrackingEntry con il TestResult e transita lo stato a COMPLETED.
     */
    public void receiveCallback(String trackingId, TestResultDto testResult) {
        TrackingEntry entry = findEntry(trackingId);
        entry.setTestResult(testResult);
        entry.setStatus("COMPLETED");
    }

    /**
     * Endpoint sincrono per il Care Coordinator (UC-1).
     * Crea un ordine di laboratorio, attende il completamento tramite polling,
     * recupera imaging archiviato e restituisce un DiagnosticBundle completo.
     * Chiamato da CompletableFuture.supplyAsync() nel coordinator: blocca il thread
     * dell'executor, non il thread HTTP del coordinator.
     */
    public DiagnosticBundle getBundle(String patientId) {
        LabOrderResponse labResponse = laboratorioClient.submitOrder(
                new LabOrderRequest(patientId, "PANEL_RENAL"));
        Long labOrderId = labResponse.getOrderId();

        String status = "PENDING";
        int attempts = 0;
        while (attempts < 30 && !"COMPLETED".equals(status) && !"ERROR".equals(status)) {
            try { Thread.sleep(2000); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            status = laboratorioClient.getStatus(labOrderId).getStatus();
            attempts++;
        }

        TestResultDto testResult = "COMPLETED".equals(status)
                ? laboratorioClient.getResult(labOrderId) : null;
        List<ImagingReportDto> imagingReports = imagingClient.getReports(patientId, "PANEL_RENAL");
        return new DiagnosticBundle(patientId, testResult, imagingReports);
    }

    private TrackingEntry findEntry(String trackingId) {
        TrackingEntry entry = trackingMap.get(trackingId);
        if (entry == null) {
            throw new TrackingNotFoundException(trackingId);
        }
        return entry;
    }

    public static class TrackingNotFoundException extends RuntimeException {
        public TrackingNotFoundException(String id) {
            super("Tracking non trovato: " + id);
        }
    }

    public static class OrderNotCompletedException extends RuntimeException {
        private final String currentStatus;
        public OrderNotCompletedException(String id, String status) {
            super("Ordine " + id + " non ancora completato. Stato: " + status);
            this.currentStatus = status;
        }
        public String getCurrentStatus() { return currentStatus; }
    }
}
