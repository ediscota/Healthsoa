package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class TrackingStatusDto {

    private String trackingId;
    private String patientId;
    private String panelCode;
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
