package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class LabStatusDto {

    private Long orderId;
    private String patientId;
    private String examCode;
    private String status;

    public LabStatusDto() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
