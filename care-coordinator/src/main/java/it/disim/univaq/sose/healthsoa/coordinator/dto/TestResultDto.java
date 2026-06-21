package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Lab test result as deserialized from the Diagnostic Aggregator response.
 * Contains the ordered list of {@link MeasurementDto} objects, each carrying
 * an {@code anomalyFlag} that the {@code RiskAnalyzer} evaluates.
 */
@Schema(description = "Lab test result from the Diagnostic Aggregator")
public class TestResultDto {

    @Schema(description = "Lab order identifier", example = "12")
    private Long orderId;

    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    @Schema(description = "Exam panel code", example = "PANEL_RENAL")
    private String examCode;

    @Schema(description = "List of individual measurements, each with an anomaly flag")
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
