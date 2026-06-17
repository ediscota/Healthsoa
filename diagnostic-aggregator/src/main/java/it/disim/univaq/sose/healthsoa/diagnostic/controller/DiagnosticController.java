package it.disim.univaq.sose.healthsoa.diagnostic.controller;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticBundle;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.DiagnosticOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TestResultDto;
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

    /**
     * Ordina un pannello diagnostico per un paziente.
     * Risponde 202 Accepted con trackingId per il polling successivo.
     */
    @PostMapping("/patients/{patientId}/diagnostics")
    public ResponseEntity<DiagnosticOrderResponse> orderDiagnostics(
            @PathVariable String patientId,
            @RequestBody DiagnosticOrderRequest request) {
        DiagnosticOrderResponse response = diagnosticService.orderDiagnostics(patientId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Polling: stato corrente dell'ordine diagnostico identificato dal trackingId.
     */
    @GetMapping("/tracking/{trackingId}/status")
    public ResponseEntity<?> getStatus(@PathVariable String trackingId) {
        try {
            TrackingStatusDto status = diagnosticService.getStatus(trackingId);
            return ResponseEntity.ok(status);
        } catch (DiagnosticService.TrackingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Risultato completo: restituisce il DiagnosticBundle quando lo stato è COMPLETED.
     * Risponde 409 Conflict se il laboratorio non ha ancora completato l'ordine.
     */
    @GetMapping("/tracking/{trackingId}/result")
    public ResponseEntity<?> getResult(@PathVariable String trackingId) {
        try {
            DiagnosticBundle bundle = diagnosticService.getResult(trackingId);
            return ResponseEntity.ok(bundle);
        } catch (DiagnosticService.TrackingNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DiagnosticService.OrderNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "currentStatus", e.getCurrentStatus()));
        }
    }

    /**
     * Endpoint sincrono per il Care Coordinator: ordina PANEL_RENAL, aspetta il
     * completamento del lab tramite polling interno, restituisce DiagnosticBundle completo.
     */
    @GetMapping("/patients/{patientId}/bundle")
    public ResponseEntity<DiagnosticBundle> getBundle(@PathVariable String patientId) {
        return ResponseEntity.ok(diagnosticService.getBundle(patientId));
    }

    /**
     * Endpoint di callback interno: invocato dal laboratorio-service quando l'esame è COMPLETED.
     * Non esposto al client esterno — è chiamato esclusivamente dal laboratorio via webhook.
     */
    @PostMapping("/internal/callback/{trackingId}")
    public ResponseEntity<Void> receiveCallback(@PathVariable String trackingId,
                                                @RequestBody TestResultDto testResult) {
        try {
            diagnosticService.receiveCallback(trackingId, testResult);
            return ResponseEntity.ok().build();
        } catch (DiagnosticService.TrackingNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
