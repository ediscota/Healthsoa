package it.disim.univaq.sose.healthsoa.imaging.dto;

public class ImagingStatusDto {

    private Long reportId;
    private String patientId;
    private String examType;
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
