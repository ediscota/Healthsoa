package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** An ICD-10 coded condition as deserialized from the Clinical Aggregator response. */
@Schema(description = "An ICD-10 coded condition from the patient's medical history")
public class ConditionDto {

    @Schema(description = "Condition description", example = "Ipertensione arteriosa essenziale")
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
