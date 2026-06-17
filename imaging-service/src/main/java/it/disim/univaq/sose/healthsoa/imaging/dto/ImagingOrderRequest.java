package it.disim.univaq.sose.healthsoa.imaging.dto;

public class ImagingOrderRequest {

    private String patientId;
    private String examType;

    public ImagingOrderRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
}
