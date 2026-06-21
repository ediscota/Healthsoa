package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO representing the full result of a completed laboratory test order.
 *
 * <p>Returned by {@code GET /tests/orders/{id}/result} and also embedded inside
 * the {@code DiagnosticBundle} that the Diagnostic Aggregator returns to the
 * Care Coordinator in UC-1.
 *
 * <p>Contains the list of individual {@link MeasurementDto} objects, one per
 * parameter measured in the exam panel.
 */
@Schema(description = "Full result of a completed laboratory test order")
public class TestResultDto {

    /** Identifier of the completed order. */
    @Schema(description = "Order identifier", example = "42")
    private Long orderId;

    /** Patient for whom the order was processed. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /** Exam panel that was processed. */
    @Schema(description = "Exam panel code", example = "PANEL_RENAL")
    private String examCode;

    /** List of parameter measurements produced by the analysis. */
    @Schema(description = "List of individual parameter measurements")
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
