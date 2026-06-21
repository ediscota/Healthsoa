package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A single lab measurement as deserialized from the Diagnostic Aggregator response.
 * The {@code anomalyFlag} is the primary input for the {@code VALORE_CRITICO} rule
 * in {@code RiskAnalyzer}.
 */
@Schema(description = "A single lab measurement with anomaly flag from the Diagnostic Aggregator")
public class MeasurementDto {

    /** Lab parameter name (e.g., "Creatinina", "Glicemia"). */
    @Schema(description = "Parameter name", example = "Creatinina")
    private String parameter;
    private Double value;
    private String unit;
    private String referenceRange;
    private boolean anomalyFlag;

    public MeasurementDto() {}

    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }
    public boolean isAnomalyFlag() { return anomalyFlag; }
    public void setAnomalyFlag(boolean anomalyFlag) { this.anomalyFlag = anomalyFlag; }
}
