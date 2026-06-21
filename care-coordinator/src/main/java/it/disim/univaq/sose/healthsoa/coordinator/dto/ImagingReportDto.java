package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A radiology report as deserialized from the Diagnostic Aggregator response.
 * The {@code criticalFlag} is used by {@code RiskAnalyzer} to generate an
 * {@code ALTRO} risk flag when the radiologist flagged a critical finding.
 */
@Schema(description = "A radiology report from the Diagnostic Aggregator")
public class ImagingReportDto {

    @Schema(description = "Report identifier", example = "3")
    private Long id;
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;
    @Schema(description = "Exam type code", example = "RX_TORACE")
    private String examType;
    @Schema(description = "Report status", allowableValues = {"PENDING","PROCESSING","COMPLETED","ERROR"})
    private String status;
    @Schema(description = "Radiologist's findings text")
    private String findings;
    @Schema(description = "Radiologist's conclusion")
    private String conclusion;
    /** {@code true} when the radiologist flagged a critical/urgent finding. Triggers a risk flag in {@code RiskAnalyzer}. */
    @Schema(description = "True if the radiologist flagged a critical finding", example = "false")
    private boolean criticalFlag;
    @Schema(description = "Report completion date")
    private String reportDate;

    public ImagingReportDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    public boolean isCriticalFlag() { return criticalFlag; }
    public void setCriticalFlag(boolean criticalFlag) { this.criticalFlag = criticalFlag; }
    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
}
