package it.disim.univaq.sose.healthsoa.clinical.dto;

public class ConditionDto {

    private String description;
    private String icdCode;
    private String diagnosisDate;
    private String status;

    public ConditionDto() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcdCode() { return icdCode; }
    public void setIcdCode(String icdCode) { this.icdCode = icdCode; }
    public String getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(String diagnosisDate) { this.diagnosisDate = diagnosisDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
