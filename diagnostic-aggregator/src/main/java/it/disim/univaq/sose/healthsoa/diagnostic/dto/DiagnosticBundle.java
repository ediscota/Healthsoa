package it.disim.univaq.sose.healthsoa.diagnostic.dto;

import java.util.List;

public class DiagnosticBundle {

    private String patientId;
    private TestResultDto labResult;
    private List<ImagingReportDto> imagingReports;

    public DiagnosticBundle() {}

    public DiagnosticBundle(String patientId, TestResultDto labResult, List<ImagingReportDto> imagingReports) {
        this.patientId = patientId;
        this.labResult = labResult;
        this.imagingReports = imagingReports;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public TestResultDto getLabResult() { return labResult; }
    public void setLabResult(TestResultDto labResult) { this.labResult = labResult; }
    public List<ImagingReportDto> getImagingReports() { return imagingReports; }
    public void setImagingReports(List<ImagingReportDto> imagingReports) { this.imagingReports = imagingReports; }
}
