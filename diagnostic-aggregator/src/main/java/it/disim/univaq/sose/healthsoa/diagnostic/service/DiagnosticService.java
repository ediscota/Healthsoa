package it.disim.univaq.sose.healthsoa.diagnostic.service;

import it.disim.univaq.sose.healthsoa.diagnostic.client.ImagingClient;
import it.disim.univaq.sose.healthsoa.diagnostic.client.LaboratorioClient;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticBundle;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabStatusDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TestResultDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TrackingStatusDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class DiagnosticService {

    private final LaboratorioClient laboratorioClient;
    private final ImagingClient imagingClient;

    public DiagnosticService(LaboratorioClient laboratorioClient, ImagingClient imagingClient) {
        this.laboratorioClient = laboratorioClient;
        this.imagingClient = imagingClient;
    }

    /**
     * Ordina un pannello di esami per un paziente.
     * Il trackingId è un token Base64URL che codifica patientId:panelCode:labOrderId.
     * Nessuno stato in-memory: qualsiasi istanza può gestire le successive richieste
     * di polling decodificando il token e interrogando direttamente il laboratorio.
     */
    public DiagnosticOrderResponse orderDiagnostics(String patientId, DiagnosticOrderRequest request) {
        String panelCode = request.getPanelCode();

        LabOrderResponse labResponse = laboratorioClient.submitOrder(
                new LabOrderRequest(patientId, panelCode));
        Long labOrderId = labResponse.getOrderId();

        String trackingId = encodeTrackingId(patientId, panelCode, labOrderId);

        return new DiagnosticOrderResponse(trackingId, "PENDING",
                "Ordine accettato. Usa GET /tracking/" + trackingId + "/status per il polling.");
    }

    /**
     * Stato corrente dell'ordine diagnostico.
     * Decodifica il trackingId e interroga direttamente il laboratorio.
     * Stateless: può essere gestito da qualsiasi istanza.
     */
    public TrackingStatusDto getStatus(String trackingId) {
        TrackingTokenParts parts = decodeTrackingId(trackingId);
        LabStatusDto labStatus = laboratorioClient.getStatus(parts.labOrderId);
        return new TrackingStatusDto(trackingId, parts.patientId, parts.panelCode, labStatus.getStatus());
    }

    /**
     * Risultato completo dell'ordine diagnostico.
     * Disponibile solo se il laboratorio ha completato l'elaborazione.
     * Recupera il risultato di laboratorio e i referti di imaging in modo stateless.
     */
    public DiagnosticBundle getResult(String trackingId) {
        TrackingTokenParts parts = decodeTrackingId(trackingId);

        LabStatusDto labStatus = laboratorioClient.getStatus(parts.labOrderId);
        if (!"COMPLETED".equals(labStatus.getStatus())) {
            throw new OrderNotCompletedException(trackingId, labStatus.getStatus());
        }

        TestResultDto testResult = laboratorioClient.getResult(parts.labOrderId);
        List<ImagingReportDto> imagingReports = imagingClient.getReports(parts.patientId, parts.panelCode);
        return new DiagnosticBundle(parts.patientId, testResult, imagingReports);
    }

    /**
     * Endpoint sincrono per il Care Coordinator (UC-1).
     * Crea un ordine di laboratorio, attende il completamento tramite polling interno,
     * recupera imaging archiviato e restituisce un DiagnosticBundle completo.
     * Chiamato da CompletableFuture.supplyAsync() nel coordinator.
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
        List<ImagingReportDto> imagingReports = imagingClient.getReports(patientId, null);
        return new DiagnosticBundle(patientId, testResult, imagingReports);
    }

    // ── Encoding / decoding del trackingId ────────────────────────────────────

    private String encodeTrackingId(String patientId, String panelCode, Long labOrderId) {
        String raw = patientId + ":" + panelCode + ":" + labOrderId;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private TrackingTokenParts decodeTrackingId(String trackingId) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(trackingId), StandardCharsets.UTF_8);
            String[] parts = raw.split(":", 3);
            if (parts.length != 3) throw new IllegalArgumentException();
            return new TrackingTokenParts(parts[0], parts[1], Long.parseLong(parts[2]));
        } catch (Exception e) {
            throw new InvalidTrackingIdException(trackingId);
        }
    }

    private static class TrackingTokenParts {
        final String patientId;
        final String panelCode;
        final Long labOrderId;

        TrackingTokenParts(String patientId, String panelCode, Long labOrderId) {
            this.patientId = patientId;
            this.panelCode = panelCode;
            this.labOrderId = labOrderId;
        }
    }

    // ── Eccezioni ─────────────────────────────────────────────────────────────

    public static class InvalidTrackingIdException extends RuntimeException {
        public InvalidTrackingIdException(String id) {
            super("TrackingId non valido o malformato: " + id);
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
