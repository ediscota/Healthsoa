package it.disim.univaq.sose.healthsoa.diagnostic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO returned by {@code GET /tracking/{trackingId}/status}.
 *
 * <p>Reflects the current processing state of a diagnostic order by proxying
 * the status from the Laboratory Service. The {@code trackingId} is echoed back
 * so the client can correlate the response with its request.
 */
@Schema(description = "Current processing status of a diagnostic order, derived from the Lab Service status")
public class TrackingStatusDto {

    /**
     * The Base64URL tracking token supplied by the client.
     * Echoed back for client-side correlation.
     */
    @Schema(description = "Base64URL tracking token")
    private String trackingId;

    /** Patient whose order is being tracked. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /** Exam panel code included in the order. */
    @Schema(description = "Exam panel code", example = "PANEL_RENAL")
    private String panelCode;

    /**
     * Current lab order status proxied from the Laboratory Service:
     * {@code PENDING}, {@code PROCESSING}, {@code COMPLETED}, or {@code ERROR}.
     */
    @Schema(description = "Lab order status",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "ERROR"})
    private String status;

    public TrackingStatusDto() {}

    public TrackingStatusDto(String trackingId, String patientId, String panelCode, String status) {
        this.trackingId = trackingId;
        this.patientId = patientId;
        this.panelCode = panelCode;
        this.status = status;
    }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPanelCode() { return panelCode; }
    public void setPanelCode(String panelCode) { this.panelCode = panelCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
