package it.disim.univaq.sose.healthsoa.coordinator.dto;

public class RiskFlag {

    private String type;      // CONTROINDICAZIONE_FARMACO, VALORE_CRITICO, ALLERGIA_RILEVANTE, ALTRO
    private String description;
    private String severity;  // INFO, WARNING, CRITICAL
    private String source;

    public RiskFlag() {}

    public RiskFlag(String type, String description, String severity, String source) {
        this.type = type;
        this.description = description;
        this.severity = severity;
        this.source = source;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
