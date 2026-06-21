package it.disim.univaq.sose.healthsoa.imaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body for {@code POST /imaging/orders} (202 Accepted).
 *
 * <p>Returned immediately after an imaging order is accepted. The {@code reportId}
 * must be stored by the caller to poll processing status or register a callback.
 */
@Schema(description = "Response returned when an imaging order is accepted (202 Accepted)")
public class ImagingOrderResponse {

    /** Database-assigned identifier for the newly created imaging report record. */
    @Schema(description = "Unique identifier of the created imaging report", example = "7")
    private Long reportId;

    /** Initial status, always PENDING at submission time. */
    @Schema(description = "Initial processing status", example = "PENDING")
    private String status;

    /** Human-readable instructions for tracking the order. */
    @Schema(description = "Informational message with polling instructions")
    private String message;

    public ImagingOrderResponse(Long reportId, String status, String message) {
        this.reportId = reportId;
        this.status = status;
        this.message = message;
    }

    public Long getReportId() { return reportId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
