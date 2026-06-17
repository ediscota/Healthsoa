package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import java.util.List;

public class TestResultDto {

    private Long orderId;
    private String patientId;
    private String examCode;
    private List<MeasurementDto> measurements;

    public TestResultDto(Long orderId, String patientId, String examCode,
                         List<MeasurementDto> measurements) {
        this.orderId = orderId;
        this.patientId = patientId;
        this.examCode = examCode;
        this.measurements = measurements;
    }

    public Long getOrderId() { return orderId; }
    public String getPatientId() { return patientId; }
    public String getExamCode() { return examCode; }
    public List<MeasurementDto> getMeasurements() { return measurements; }

}
