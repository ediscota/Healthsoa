package it.disim.univaq.sose.healthsoa.diagnostic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * REST controller for the Diagnostic Aggregator (Prosumer 1).
 *
 * <p>This prosumer is the entry point for use case UC-3 (lab panel order and
 * asynchronous monitoring) and also provides the synchronous diagnostic bundle
 * consumed by the Care Coordinator in UC-1.
 *
 * <p>Architecture role:
 * <ul>
 *   <li>Consumes {@code laboratorio-service} (Provider 2) via Feign for lab orders;</li>
 *   <li>Consumes {@code imaging-service} (Provider 4) via Feign for imaging reports;</li>
 *   <li>Exposes a stateless tracking API based on a Base64URL {@code trackingId} token
 *       that encodes {@code patientId:panelCode:labOrderId}, enabling any replica to
 *       handle polling requests without shared in-memory state.</li>
 * </ul>
 *
 * <p>All endpoints are accessible through the API Gateway at {@code /api/diagnostic/**}.
 */
@RestController
@Tag(name = "Diagnostic Aggregator", description = "Prosumer 1 — orchestrates lab and imaging providers; exposes async tracking for UC-3 and sync bundle for UC-1")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    /**
     * Submits a diagnostic panel order for a patient (UC-3 entry point).
     *
     * <p>The aggregator forwards the order to the Laboratory Service (202 Accepted)
     * and returns a stateless {@code trackingId} to the caller. The trackingId is a
     * Base64URL encoding of {@code patientId:panelCode:labOrderId} — no server-side
     * session is required to track the request.
     *
     * @param patientId numeric patient identifier (path variable)
     * @param request   JSON body containing the {@code panelCode} (e.g., CBC, PANEL_RENAL)
     * @return 202 Accepted with trackingId and polling instructions
     */
    @Operation(
        summary = "Order a diagnostic panel (UC-3)",
        description = "Submits a lab order to laboratorio-service and returns a Base64URL trackingId " +
                      "that encodes patientId:panelCode:labOrderId. Use the trackingId to poll status " +
                      "and retrieve the final result without server-side session."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Order accepted by laboratory; use trackingId to track progress"),
        @ApiResponse(responseCode = "400", description = "Malformed request body")
    })
    @PostMapping("/patients/{patientId}/diagnostics")
    public ResponseEntity<DiagnosticOrderResponse> orderDiagnostics(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId,
            @RequestBody DiagnosticOrderRequest request) {
        DiagnosticOrderResponse response = diagnosticService.orderDiagnostics(patientId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Returns the current status of a diagnostic order by its trackingId.
     *
     * <p>Decodes the trackingId to extract the {@code labOrderId}, then queries
     * the Laboratory Service for the current order status. This endpoint is
     * stateless and can be served by any replica of the aggregator.
     *
     * @param trackingId Base64URL token returned by the order endpoint
     * @return 200 with status (PENDING / PROCESSING / COMPLETED / ERROR),
     *         or 400 if the trackingId is malformed
     */
    @Operation(
        summary = "Poll diagnostic order status",
        description = "Decodes the trackingId and proxies a status check to laboratorio-service. " +
                      "Stateless: any aggregator replica can serve this request."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or malformed trackingId")
    })
    @GetMapping("/tracking/{trackingId}/status")
    public ResponseEntity<?> getStatus(
            @Parameter(description = "Base64URL tracking token returned by the order endpoint") @PathVariable String trackingId) {
        try {
            TrackingStatusDto status = diagnosticService.getStatus(trackingId);
            return ResponseEntity.ok(status);
        } catch (DiagnosticService.InvalidTrackingIdException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Returns the complete diagnostic result for a completed order.
     *
     * <p>Combines the lab {@code TestResultDto} from the Laboratory Service with
     * the list of {@link it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto}
     * from the Imaging Service into a unified {@link DiagnosticBundle}. Only
     * available once the lab order reaches COMPLETED status.
     *
     * @param trackingId Base64URL tracking token
     * @return 200 with {@link DiagnosticBundle}, 400 for invalid token,
     *         409 if the order is not yet completed
     */
    @Operation(
        summary = "Retrieve diagnostic result",
        description = "Returns a DiagnosticBundle combining lab results and imaging reports. " +
                      "Returns 409 Conflict if the lab order is not yet COMPLETED."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "DiagnosticBundle returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or malformed trackingId"),
        @ApiResponse(responseCode = "409", description = "Lab order not yet completed — retry after polling status")
    })
    @GetMapping("/tracking/{trackingId}/result")
    public ResponseEntity<?> getResult(
            @Parameter(description = "Base64URL tracking token returned by the order endpoint") @PathVariable String trackingId) {
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

    /**
     * Returns a complete synchronous {@link DiagnosticBundle} for a patient (UC-1 internal endpoint).
     *
     * <p>This endpoint is called by the Care Coordinator from within a
     * {@code CompletableFuture.supplyAsync()} in order to collect the diagnostic
     * data in parallel with the clinical profile retrieval. It submits a lab order
     * for {@code PANEL_RENAL}, polls internally until the result is ready, then
     * combines it with archived imaging reports and returns the full bundle.
     *
     * <p><strong>Note:</strong> This call is blocking on the server side (polling loop
     * with up to 30 attempts × 2 s) and may take 8–10 seconds. It is designed to be
     * called on a dedicated coordinator thread, not on the Tomcat HTTP thread.
     *
     * @param patientId numeric patient identifier
     * @return 200 with a complete {@link DiagnosticBundle} (lab results + imaging reports)
     */
    @Operation(
        summary = "Get synchronous diagnostic bundle (UC-1 internal)",
        description = "Submits a PANEL_RENAL lab order, waits for completion via internal polling, " +
                      "then combines the lab result with archived imaging reports into a DiagnosticBundle. " +
                      "Called by the Care Coordinator in parallel during the UC-1 fitness assessment."
    )
    @ApiResponse(responseCode = "200", description = "DiagnosticBundle returned successfully")
    @GetMapping("/patients/{patientId}/bundle")
    public ResponseEntity<DiagnosticBundle> getBundle(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId) {
        return ResponseEntity.ok(diagnosticService.getBundle(patientId));
    }
}
