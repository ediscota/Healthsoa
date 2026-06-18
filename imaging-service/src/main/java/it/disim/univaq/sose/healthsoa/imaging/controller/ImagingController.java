package it.disim.univaq.sose.healthsoa.imaging.controller;

import it.disim.univaq.sose.healthsoa.imaging.dto.CallbackRequest;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingOrderRequest;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingOrderResponse;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingStatusDto;
import it.disim.univaq.sose.healthsoa.imaging.model.ImagingReport;
import it.disim.univaq.sose.healthsoa.imaging.service.ImagingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ImagingController {

    private final ImagingService imagingService;

    public ImagingController(ImagingService imagingService) {
        this.imagingService = imagingService;
    }

    /** Referti archiviati per un paziente. Filtra per examType se il parametro è presente. */
    @GetMapping("/patients/{patientId}/reports")
    public ResponseEntity<List<ImagingReportDto>> getReportsByPatient(
            @PathVariable String patientId,
            @RequestParam(name = "examType", required = false) String examType) {
        List<ImagingReportDto> reports = (examType != null && !examType.isBlank())
                ? imagingService.getReportsByPatientAndExamType(patientId, examType)
                : imagingService.getReportsByPatient(patientId);
        return ResponseEntity.ok(reports);
    }

    /** Referto singolo per ID (archiviato o in lavorazione). */
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ImagingReportDto> getReport(@PathVariable Long reportId) {
        try {
            return ResponseEntity.ok(imagingService.getReport(reportId));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Avvia una nuova richiesta di imaging. Risponde 202 immediatamente. */
    @PostMapping("/imaging/orders")
    public ResponseEntity<ImagingOrderResponse> submitOrder(@RequestBody ImagingOrderRequest request) {
        ImagingReport report = imagingService.createOrder(request.getPatientId(), request.getExamType());
        imagingService.processOrderAsync(report.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ImagingOrderResponse(report.getId(), report.getStatus().name(),
                        "Richiesta accettata. Usa GET /imaging/orders/" + report.getId() + "/status per il polling."));
    }

    @GetMapping("/imaging/orders/{id}/status")
    public ResponseEntity<ImagingStatusDto> getStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(imagingService.getStatus(id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/imaging/orders/{id}/result")
    public ResponseEntity<?> getResult(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(imagingService.getResult(id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ImagingService.ReportNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "currentStatus", e.getCurrentStatus()));
        }
    }

    @PostMapping("/imaging/orders/{id}/callback")
    public ResponseEntity<?> registerCallback(@PathVariable Long id, @RequestBody CallbackRequest body) {
        try {
            imagingService.registerCallback(id, body.getCallbackUrl());
            return ResponseEntity.ok(Map.of("message", "Callback registrata per referto " + id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
