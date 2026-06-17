package it.disim.univaq.sose.healthsoa.imaging.dto;

import java.time.LocalDate;

public class ImagingReportDto {

    private Long id;
    private String patientId;
    private String examType;
    private String status;
    private String findings;
    private String conclusion;
    private boolean criticalFlag;
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
