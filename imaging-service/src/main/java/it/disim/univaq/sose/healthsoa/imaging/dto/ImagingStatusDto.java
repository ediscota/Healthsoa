package it.disim.univaq.sose.healthsoa.imaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO returned by {@code GET /imaging/orders/{id}/status}.
 *
 * <p>Provides the current lifecycle state of an imaging order without exposing the
 * full report content. The client should poll this DTO until {@code status} reaches
 * {@code COMPLETED} before calling the result endpoint.
 */
@Schema(description = "Current processing status of an imaging order")
public class ImagingStatusDto {

    /** Identifier of the imaging report/order. */
    @Schema(description = "Report identifier", example = "7")
    private Long reportId;

    /** Patient to whom the order belongs. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /** Exam type code (e.g., {@code RX_TORACE}). */
    @Schema(description = "Exam type code", example = "RX_TORACE")
    private String examType;

    /**
     * Current lifecycle status: {@code PENDING}, {@code PROCESSING},
     * {@code COMPLETED}, or {@code ERROR}.
     */
    @Schema(description = "Current processing status",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "ERROR"})
    private String status;

    public ImagingStatusDto(Long reportId, String patientId, String examType, String status) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.examType = examType;
        this.status = status;
    }

    public Long getReportId() { return reportId; }
    public String getPatientId() { return patientId; }
    public String getExamType() { return examType; }
    public String getStatus() { return status; }
}
