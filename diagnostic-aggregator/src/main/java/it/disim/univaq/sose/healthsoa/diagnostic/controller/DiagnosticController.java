package it.disim.univaq.sose.healthsoa.diagnostic.controller;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticBundle;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TrackingStatusDto;
import it.disim.univaq.sose.healthsoa.diagnostic.service.DiagnosticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping("/patients/{patientId}/diagnostics")
    public ResponseEntity<DiagnosticOrderResponse> orderDiagnostics(
            @PathVariable String patientId,
            @RequestBody DiagnosticOrderRequest request) {
        DiagnosticOrderResponse response = diagnosticService.orderDiagnostics(patientId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/tracking/{trackingId}/status")
    public ResponseEntity<?> getStatus(@PathVariable String trackingId) {
        try {
            TrackingStatusDto status = diagnosticService.getStatus(trackingId);
            return ResponseEntity.ok(status);
        } catch (DiagnosticService.InvalidTrackingIdException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tracking/{trackingId}/result")
    public ResponseEntity<?> getResult(@PathVariable String trackingId) {
        try {
            DiagnosticBundle bundle = diagnosticService.getResult(trackingId);
            return ResponseEntity.ok(bundle);
        } catch (DiagnosticService.InvalidTrackingIdException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (DiagnosticService.OrderNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "currentStatus", e.getCurrentStatus()));
        }
    }

    @GetMapping("/patients/{patientId}/bundle")
    public ResponseEntity<DiagnosticBundle> getBundle(@PathVariable String patientId) {
        return ResponseEntity.ok(diagnosticService.getBundle(patientId));
    }
}
