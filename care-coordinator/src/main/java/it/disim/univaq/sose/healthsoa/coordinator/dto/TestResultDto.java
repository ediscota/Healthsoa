package it.disim.univaq.sose.healthsoa.coordinator.dto;

import java.util.List;

public class TestResultDto {

    private Long orderId;
    private String patientId;
    private String examCode;
    private List<MeasurementDto> measurements;

    public TestResultDto() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public List<MeasurementDto> getMeasurements() { return measurements; }
    public void setMeasurements(List<MeasurementDto> measurements) { this.measurements = measurements; }
}
