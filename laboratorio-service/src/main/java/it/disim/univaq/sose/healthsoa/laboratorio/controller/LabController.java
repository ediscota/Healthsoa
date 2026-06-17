package it.disim.univaq.sose.healthsoa.laboratorio.controller;

import it.disim.univaq.sose.healthsoa.laboratorio.dto.CallbackRequest;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.OrderStatusDto;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.TestOrderRequest;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.TestOrderResponse;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.TestResultDto;
import it.disim.univaq.sose.healthsoa.laboratorio.model.TestOrder;
import it.disim.univaq.sose.healthsoa.laboratorio.service.LabService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/tests/orders")
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    /**
     * Accetta l'ordine, ritorna 202 Accepted immediatamente e avvia
     * la lavorazione in background. Pattern Async Request-Reply (slide 47+).
     */
    @PostMapping
    public ResponseEntity<TestOrderResponse> submitOrder(@RequestBody TestOrderRequest request) {
        TestOrder order = labService.createOrder(request.getPatientId(), request.getExamCode());
        labService.processOrderAsync(order.getId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new TestOrderResponse(order.getId(), order.getStatus().name(),
                        "Ordine accettato. Usa GET /tests/orders/" + order.getId() + "/status per il polling."));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusDto> getStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(labService.getStatus(id));
        } catch (LabService.OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getResult(@PathVariable Long id) {
        try {
            TestResultDto result = labService.getResult(id);
            return ResponseEntity.ok(result);
        } catch (LabService.OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LabService.OrderNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "currentStatus", e.getCurrentStatus()));
        }
    }

    /**
     * Registra (o aggiorna) la URL di callback per l'ordine.
     * Può essere chiamato subito dopo il POST o mentre l'ordine è ancora in lavorazione.
     */
    @PostMapping("/{id}/callback")
    public ResponseEntity<?> registerCallback(@PathVariable Long id,
                                              @RequestBody CallbackRequest body) {
        try {
            labService.registerCallback(id, body.getCallbackUrl());
            return ResponseEntity.ok(Map.of("message", "Callback registrata per ordine " + id));
        } catch (LabService.OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
