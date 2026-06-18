package it.disim.univaq.sose.healthsoa.imaging.service;

import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingStatusDto;
import it.disim.univaq.sose.healthsoa.imaging.model.ImagingOrderStatus;
import it.disim.univaq.sose.healthsoa.imaging.model.ImagingReport;
import it.disim.univaq.sose.healthsoa.imaging.repository.ImagingReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ImagingService {

    private final ImagingReportRepository reportRepository;
    private final RestTemplate restTemplate;

    @Value("${imaging.processing.delay-ms:10000}")
    private long processingDelayMs;

    /** Findings simulati per tipo di esame, orientati alla logica di rischio del Care Coordinator. */
    private static final Map<String, String[]> EXAM_FINDINGS = Map.of(
        "RX_TORACE", new String[]{
            "Opacità parailare destra compatibile con addensamento polmonare. " +
            "Silhouette cardiaca nei limiti. Seni costofrenici liberi.",
            "Quadro radiologico suggestivo di polmonite lobare destra."
        },
        "TC_ADDOME", new String[]{
            "Fegato di dimensioni nei limiti, struttura omogenea. " +
            "Reni normoconformati. Milza nella norma. " +
            "Modesta falda di versamento libero in sede pelvica.",
            "Versamento peritoneale di modesta entità. Rivalutazione clinica consigliata."
        },
        "RM_CRANIO", new String[]{
            "Esame eseguito in assenza di mezzo di contrasto. " +
            "Strutture della linea mediana in asse. " +
            "Segnale parenchimale nei limiti della norma per età. " +
            "Non evidenza di lesioni focali in sequenze T1, T2 e FLAIR.",
            "Esame nei limiti della norma."
        },
        "ECO_ADDOME", new String[]{
            "Fegato di dimensioni aumentate (diametro longitudinale 18 cm), " +
            "ecostruttura iperecogena compatibile con steatosi epatica. " +
            "Colecisti distesa, pareti regolari. Pancreas e milza nella norma.",
            "Steatosi epatica di grado moderato. Follow-up raccomandato."
        }
    );

    private static final Map<String, Boolean> CRITICAL_BY_EXAM = Map.of(
        "RX_TORACE", true,
        "TC_ADDOME", true,
        "RM_CRANIO", false,
        "ECO_ADDOME", false
    );

    public ImagingService(ImagingReportRepository reportRepository) {
        this.reportRepository = reportRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional(readOnly = true)
    public List<ImagingReportDto> getReportsByPatient(String patientId) {
        return reportRepository.findByPatientId(patientId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ImagingReportDto> getReportsByPatientAndExamType(String patientId, String examType) {
        return reportRepository.findByPatientIdAndExamType(patientId, examType).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImagingReportDto getReport(Long reportId) {
        return toDto(findReport(reportId));
    }

    @Transactional
    public ImagingReport createOrder(String patientId, String examType) {
        ImagingReport report = new ImagingReport();
        report.setPatientId(patientId);
        report.setExamType(examType);
        report.setStatus(ImagingOrderStatus.PENDING);
        return reportRepository.save(report);
    }

    @Transactional
    public void registerCallback(Long reportId, String callbackUrl) {
        ImagingReport report = findReport(reportId);
        report.setCallbackUrl(callbackUrl);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public ImagingStatusDto getStatus(Long reportId) {
        ImagingReport r = findReport(reportId);
        return new ImagingStatusDto(r.getId(), r.getPatientId(), r.getExamType(), r.getStatus().name());
    }

    @Transactional(readOnly = true)
    public ImagingReportDto getResult(Long reportId) {
        ImagingReport r = findReport(reportId);
        if (r.getStatus() != ImagingOrderStatus.COMPLETED) {
            throw new ReportNotCompletedException(reportId, r.getStatus().name());
        }
        return toDto(r);
    }

    @Async("imagingExecutor")
    @Transactional
    public void processOrderAsync(Long reportId) {
        ImagingReport report = findReport(reportId);
        try {
            report.setStatus(ImagingOrderStatus.PROCESSING);
            reportRepository.save(report);

            Thread.sleep(processingDelayMs);

            String[] findings = EXAM_FINDINGS.getOrDefault(report.getExamType(), new String[]{
                "Esame eseguito. Referto in corso di validazione.", "Nessuna anomalia di rilievo."
            });
            report.setFindings(findings[0]);
            report.setConclusion(findings[1]);
            report.setCriticalFlag(CRITICAL_BY_EXAM.getOrDefault(report.getExamType(), false));
            report.setReportDate(LocalDate.now());
            report.setStatus(ImagingOrderStatus.COMPLETED);
            reportRepository.save(report);

            if (report.getCallbackUrl() != null && !report.getCallbackUrl().isBlank()) {
                fireCallback(report);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            report.setStatus(ImagingOrderStatus.ERROR);
            reportRepository.save(report);
        }
    }

    private void fireCallback(ImagingReport report) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ImagingReportDto payload = toDto(report);
            HttpEntity<ImagingReportDto> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(report.getCallbackUrl(), request, Void.class);
        } catch (Exception ignored) {
            // best-effort
        }
    }

    private ImagingReport findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
    }

    private ImagingReportDto toDto(ImagingReport r) {
        return new ImagingReportDto(r.getId(), r.getPatientId(), r.getExamType(),
                r.getStatus().name(), r.getFindings(), r.getConclusion(),
                r.isCriticalFlag(), r.getReportDate());
    }

    public static class ReportNotFoundException extends RuntimeException {
        public ReportNotFoundException(Long id) { super("Referto non trovato: " + id); }
    }

    public static class ReportNotCompletedException extends RuntimeException {
        private final String currentStatus;
        public ReportNotCompletedException(Long id, String status) {
            super("Referto " + id + " non ancora completato. Stato: " + status);
            this.currentStatus = status;
        }
        public String getCurrentStatus() { return currentStatus; }
    }
}
