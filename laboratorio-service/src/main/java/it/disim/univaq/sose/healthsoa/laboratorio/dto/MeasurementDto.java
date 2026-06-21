package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a single parameter measurement within a completed lab result.
 *
 * <p>Produced by {@code LabService.toResultDto()} from a {@code Measurement} entity.
 * Consumed by the Care Coordinator's {@code RiskAnalyzer} to evaluate critical values
 * (specification §6, rule 3: anomalyFlag + distance from reference range bounds).
 */
@Schema(description = "A single measured parameter from a laboratory test order")
public class MeasurementDto {

    /** Human-readable name of the parameter (e.g., "Creatinina", "Emoglobina"). */
    @Schema(description = "Name of the measured parameter", example = "Creatinina")
    private String parameter;

    /** Numeric measurement value. */
    @Schema(description = "Measured value", example = "3.5")
    private Double value;

    /** Unit of measure (e.g., "mg/dL", "g/dL", "mEq/L"). */
    @Schema(description = "Unit of measure", example = "mg/dL")
    private String unit;

    /**
     * Normal reference range in "low-high" textual format (e.g., "0.6-1.2").
     * Parsed by {@code RiskAnalyzer.parseRange()} to compute severity thresholds.
     */
    @Schema(description = "Normal reference range in low-high format", example = "0.6-1.2")
    private String referenceRange;

    /**
     * {@code true} if {@code value} falls outside {@code referenceRange}.
     * This flag triggers risk analysis in the Care Coordinator.
     */
    @Schema(description = "True if the value is outside the reference range", example = "true")
    private boolean anomalyFlag;

    public MeasurementDto(String parameter, Double value, String unit,
                          String referenceRange, boolean anomalyFlag) {
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.referenceRange = referenceRange;
        this.anomalyFlag = anomalyFlag;
    }

    public String getParameter() { return parameter; }
    public Double getValue() { return value; }
    public String getUnit() { return unit; }
    public String getReferenceRange() { return referenceRange; }
    public boolean isAnomalyFlag() { return anomalyFlag; }

}
