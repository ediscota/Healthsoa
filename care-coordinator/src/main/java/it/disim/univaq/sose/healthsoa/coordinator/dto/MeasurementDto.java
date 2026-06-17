package it.disim.univaq.sose.healthsoa.coordinator.dto;

public class MeasurementDto {

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
