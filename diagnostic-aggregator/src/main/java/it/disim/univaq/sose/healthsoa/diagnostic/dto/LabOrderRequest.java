package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class LabOrderRequest {

    private String patientId;
    private String examCode;

    public LabOrderRequest() {}

    public LabOrderRequest(String patientId, String examCode) {
        this.patientId = patientId;
        this.examCode = examCode;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }
}
