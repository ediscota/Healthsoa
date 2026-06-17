package it.disim.univaq.sose.healthsoa.coordinator.dto;

import java.util.List;

public class DiagnosticBundleDto {

    private String patientId;
    private TestResultDto labResult;
    private List<ImagingReportDto> imagingReports;

    public DiagnosticBundleDto() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public TestResultDto getLabResult() { return labResult; }
    public void setLabResult(TestResultDto labResult) { this.labResult = labResult; }
    public List<ImagingReportDto> getImagingReports() { return imagingReports; }
    public void setImagingReports(List<ImagingReportDto> imagingReports) { this.imagingReports = imagingReports; }
}
