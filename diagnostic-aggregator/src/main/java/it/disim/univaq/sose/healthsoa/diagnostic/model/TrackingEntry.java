package it.disim.univaq.sose.healthsoa.diagnostic.model;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TestResultDto;

import java.util.List;

public class TrackingEntry {

    private final String trackingId;
    private final String patientId;
    private final String panelCode;
    private final Long labOrderId;
    private volatile String status;
    private volatile TestResultDto testResult;
    private volatile List<ImagingReportDto> imagingReports;

    public TrackingEntry(String trackingId, String patientId, String panelCode, Long labOrderId) {
        this.trackingId = trackingId;
        this.patientId = patientId;
        this.panelCode = panelCode;
        this.labOrderId = labOrderId;
        this.status = "PENDING";
    }

    public String getTrackingId() { return trackingId; }
    public String getPatientId() { return patientId; }
    public String getPanelCode() { return panelCode; }
    public Long getLabOrderId() { return labOrderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public TestResultDto getTestResult() { return testResult; }
    public void setTestResult(TestResultDto testResult) { this.testResult = testResult; }
    public List<ImagingReportDto> getImagingReports() { return imagingReports; }
    public void setImagingReports(List<ImagingReportDto> imagingReports) { this.imagingReports = imagingReports; }
}
