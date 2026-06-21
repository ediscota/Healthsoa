package it.disim.univaq.sose.healthsoa.diagnostic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body for {@code POST /patients/{patientId}/diagnostics} (202 Accepted).
 *
 * <p>Returns a {@code trackingId} that the client uses to poll the order status
 * ({@code GET /tracking/{trackingId}/status}) and eventually retrieve the result
 * ({@code GET /tracking/{trackingId}/result}).
 *
 * <p>The {@code trackingId} is a Base64URL-encoded token containing
 * {@code patientId:panelCode:labOrderId}, which allows any aggregator replica to
 * serve subsequent polling requests without shared server-side state.
 */
@Schema(description = "Response for a diagnostic order submission (202 Accepted)")
public class DiagnosticOrderResponse {

    /**
     * Stateless Base64URL tracking token encoding {@code patientId:panelCode:labOrderId}.
     * Must be stored by the client for subsequent status and result queries.
     */
    @Schema(description = "Base64URL tracking token for polling status and result")
    private String trackingId;

    /** Initial status of the lab order: always PENDING at submission time. */
    @Schema(description = "Initial order status", example = "PENDING")
    private String status;

    /** Human-readable message with polling instructions. */
    @Schema(description = "Informational message")
    private String message;

    public DiagnosticOrderResponse() {}

    public DiagnosticOrderResponse(String trackingId, String status, String message) {
        this.trackingId = trackingId;
        this.status = status;
        this.message = message;
    }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
