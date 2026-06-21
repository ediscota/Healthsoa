package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response body for {@code GET /tests/orders/{id}/status}.
 *
 * <p>Carries the current lifecycle state of a laboratory order together with
 * metadata useful for logging and debugging (timestamps, patient and exam identifiers).
 * The client should poll this DTO until {@code status} equals {@code COMPLETED}
 * before attempting to fetch the full result.
 */
@Schema(description = "Current status of a laboratory test order, returned by the polling endpoint")
public class OrderStatusDto {

    /** Unique identifier of the order. */
    @Schema(description = "Order identifier", example = "42")
    private Long orderId;

    /** Patient for whom the order was placed. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /** Exam panel associated with this order. */
    @Schema(description = "Exam panel code", example = "PANEL_RENAL")
    private String examCode;

    /**
     * Current lifecycle state: {@code PENDING}, {@code PROCESSING},
     * {@code COMPLETED}, or {@code ERROR}.
     */
    @Schema(description = "Current processing status", example = "PROCESSING",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "ERROR"})
    private String status;

    /** Timestamp when the order was first submitted. */
    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;

    /** Timestamp of the last status transition. */
    @Schema(description = "Timestamp of the last status update")
    private LocalDateTime updatedAt;

    public OrderStatusDto(Long orderId, String patientId, String examCode,
                          String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.patientId = patientId;
        this.examCode = examCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getOrderId() { return orderId; }
    public String getPatientId() { return patientId; }
    public String getExamCode() { return examCode; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}
