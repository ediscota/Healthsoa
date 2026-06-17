package it.disim.univaq.sose.healthsoa.laboratorio.dto;

public class TestOrderRequest {

    private String patientId;
    private String examCode;

    public TestOrderRequest() {
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

}
