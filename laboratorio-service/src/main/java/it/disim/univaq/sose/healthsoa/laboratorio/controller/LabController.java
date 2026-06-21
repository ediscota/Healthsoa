package it.disim.univaq.sose.healthsoa.laboratorio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * REST controller for the Laboratory Analysis Service (Provider 2).
 *
 * <p>Implements the <em>Asynchronous Request-Reply</em> pattern described in the
 * project specification (§3.1 Provider 2):
 * <ol>
 *   <li>Client submits an order → 202 Accepted + orderId;</li>
 *   <li>Client polls GET /{id}/status until COMPLETED;</li>
 *   <li>Client fetches results via GET /{id}/result.</li>
 * </ol>
 * Alternatively, the client can register a webhook via POST /{id}/callback and
 * receive a push notification when the result is ready.
 *
 * <p>All endpoints are accessible through the API Gateway at
 * {@code /api/lab/**} (internal path: {@code /tests/orders/**}).
 */
@RestController
@RequestMapping("/tests/orders")
@Tag(name = "Laboratory Orders", description = "Management of laboratory test orders and results (async pattern)")
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    /**
     * Accepts a new laboratory order and immediately returns 202 Accepted.
     *
     * <p>The actual processing runs asynchronously on a dedicated thread pool
     * ({@code labExecutor}), simulating the real-world delay of biological sample
     * analysis. The response body contains the {@code orderId} needed for
     * subsequent polling or callback registration.
     *
     * <p>Pattern: Asynchronous Request-Reply (slide 47+, Dr. Filippone).
     *
     * @param request JSON body containing {@code patientId} and {@code examCode}
     * @return 202 Accepted with order details and polling instructions
     */
    @Operation(
        summary = "Submit a laboratory test order",
        description = "Accepts the order immediately (202 Accepted) and starts asynchronous processing. " +
                      "Use the returned orderId to poll for status or register a callback webhook."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Order accepted; processing started in background"),
        @ApiResponse(responseCode = "400", description = "Malformed request body")
    })
    @PostMapping
    public ResponseEntity<TestOrderResponse> submitOrder(@RequestBody TestOrderRequest request) {
        TestOrder order = labService.createOrder(request.getPatientId(), request.getExamCode());
        labService.processOrderAsync(order.getId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new TestOrderResponse(order.getId(), order.getStatus().name(),
                        "Order accepted. Use GET /tests/orders/" + order.getId() + "/status for polling."));
    }

    /**
     * Returns the current processing status of a laboratory order.
     *
     * <p>Possible statuses: {@code PENDING}, {@code PROCESSING}, {@code COMPLETED}, {@code ERROR}.
     * The client should poll this endpoint until the status transitions to COMPLETED
     * before retrieving the full result.
     *
     * @param id the order identifier returned by the submit endpoint
     * @return 200 with status details, or 404 if the order does not exist
     */
    @Operation(
        summary = "Get order status",
        description = "Returns the current processing status of a laboratory order. " +
                      "Possible values: PENDING, PROCESSING, COMPLETED, ERROR."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status returned successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusDto> getStatus(
            @Parameter(description = "Laboratory order identifier") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(labService.getStatus(id));
        } catch (LabService.OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Returns the full result of a completed laboratory order.
     *
     * <p>This endpoint must only be called after the order status is COMPLETED.
     * If the order is still being processed, 409 Conflict is returned with the
     * current status so the client can retry later.
     *
     * @param id the order identifier
     * @return 200 with measurements list, 404 if not found, 409 if not yet completed
     */
    @Operation(
        summary = "Get order result",
        description = "Returns the full list of measurements for a completed order. " +
                      "Returns 409 Conflict if the order is not yet in COMPLETED status."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Result returned successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "409", description = "Order not yet completed — retry after polling status")
    })
    @GetMapping("/{id}/result")
    public ResponseEntity<?> getResult(
            @Parameter(description = "Laboratory order identifier") @PathVariable Long id) {
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
     * Registers or updates a webhook callback URL for an order.
     *
     * <p>When the laboratory finishes processing, it will POST the full
     * {@link TestResultDto} to the registered URL. This call can be made immediately
     * after order submission or while the order is still in PROCESSING state.
     * The callback is best-effort: a failure to deliver the notification does not
     * affect the order's final status.
     *
     * @param id   the order identifier
     * @param body JSON body containing the {@code callbackUrl}
     * @return 200 on successful registration, 404 if the order does not exist
     */
    @Operation(
        summary = "Register a callback webhook",
        description = "Registers (or replaces) a callback URL for an order. " +
                      "The service will POST the completed TestResultDto to the given URL when processing finishes. " +
                      "Callback delivery is best-effort; use polling as a fallback."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Callback URL registered successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/callback")
    public ResponseEntity<?> registerCallback(
            @Parameter(description = "Laboratory order identifier") @PathVariable Long id,
            @RequestBody CallbackRequest body) {
        try {
            labService.registerCallback(id, body.getCallbackUrl());
            return ResponseEntity.ok(Map.of("message", "Callback registered for order " + id));
        } catch (LabService.OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
