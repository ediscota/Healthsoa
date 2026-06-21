package it.disim.univaq.sose.healthsoa.imaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO representing an archived or completed radiology report.
 *
 * <p>Returned by {@code GET /patients/{patientId}/reports},
 * {@code GET /reports/{reportId}}, and {@code GET /imaging/orders/{id}/result}.
 * Also consumed by the Diagnostic Aggregator (Prosumer 1) when building the
 * {@code DiagnosticBundle} for the Care Coordinator in UC-1.
 *
 * <p>Pre-existing reports (loaded from {@code data.sql}) already have
 * {@code status=COMPLETED} and populated {@code findings}/{@code conclusion} fields.
 * Reports created via {@code POST /imaging/orders} start with {@code status=PENDING}
 * and are completed asynchronously.
 */
@Schema(description = "Radiology report — either archived (pre-existing) or the result of an async imaging order")
public class ImagingReportDto {

    /** Database-assigned identifier of this report. */
    @Schema(description = "Report identifier", example = "3")
    private Long id;

    /** Patient to whom the report belongs. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /**
     * Imaging exam type (e.g., {@code RX_TORACE}, {@code TC_ADDOME}).
     * Matches the panel code used by the Diagnostic Aggregator as a filter.
     */
    @Schema(description = "Exam type code", example = "RX_TORACE")
    private String examType;

    /**
     * Lifecycle status: {@code PENDING}, {@code PROCESSING}, {@code COMPLETED},
     * or {@code ERROR}. Only COMPLETED reports have non-null findings and conclusion.
     */
    @Schema(description = "Processing status of this report",
            allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "ERROR"})
    private String status;

    /**
     * Full textual description of the radiologist's observations.
     * {@code null} until the report reaches COMPLETED status.
     */
    @Schema(description = "Radiologist's findings (null if not yet completed)")
    private String findings;

    /**
     * Concise conclusion of the radiologist.
     * {@code null} until the report reaches COMPLETED status.
     */
    @Schema(description = "Radiologist's conclusion (null if not yet completed)")
    private String conclusion;

    /**
     * {@code true} if the radiologist flagged a critical or urgent finding
     * (e.g., suspected pneumonia, pleural effusion). Used by the Care Coordinator
     * to generate WARNING/CRITICAL risk flags.
     */
    @Schema(description = "True if the radiologist flagged a critical finding", example = "false")
    private boolean criticalFlag;

    /** Date on which the report was completed (null for in-progress orders). */
    @Schema(description = "Date the report was completed")
    private LocalDate reportDate;

    public ImagingReportDto() {}

    public ImagingReportDto(Long id, String patientId, String examType, String status,
                            String findings, String conclusion, boolean criticalFlag,
                            LocalDate reportDate) {
        this.id = id;
        this.patientId = patientId;
        this.examType = examType;
        this.status = status;
        this.findings = findings;
        this.conclusion = conclusion;
        this.criticalFlag = criticalFlag;
        this.reportDate = reportDate;
    }

    public Long getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getExamType() { return examType; }
    public String getStatus() { return status; }
    public String getFindings() { return findings; }
    public String getConclusion() { return conclusion; }
    public boolean isCriticalFlag() { return criticalFlag; }
    public LocalDate getReportDate() { return reportDate; }
}
