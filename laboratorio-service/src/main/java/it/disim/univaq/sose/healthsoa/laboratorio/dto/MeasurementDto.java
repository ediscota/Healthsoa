package it.disim.univaq.sose.healthsoa.laboratorio.dto;

public class MeasurementDto {

    private String parameter;
    private Double value;
    private String unit;
    private String referenceRange;
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
