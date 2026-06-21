package it.disim.univaq.sose.healthsoa.clinical.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a single clinical condition (diagnosis or past admission)
 * in a patient's medical history.
 *
 * <p>Sourced from the Anagrafe SOAP {@code getMedicalHistory} operation and
 * mapped from the JAX-WS generated {@code Condition} type. The ICD-10 code
 * provides a standardised, internationally recognised classification.
 */
@Schema(description = "A single ICD-10 coded clinical condition in the patient's medical history")
public class ConditionDto {

    /** Free-text description of the condition (e.g., "Ipertensione arteriosa essenziale"). */
    @Schema(description = "Human-readable description of the condition", example = "Ipertensione arteriosa essenziale")
    private String description;

    /**
     * ICD-10 classification code (e.g., {@code I10} for essential hypertension,
     * {@code E11} for type-2 diabetes). See docs/specifica_applicativa.md §5.
     */
    @Schema(description = "ICD-10 code identifying the condition", example = "I10")
    private String icdCode;

    /** Date the condition was first diagnosed, in ISO-8601 format. */
    @Schema(description = "Date of first diagnosis (ISO-8601)", example = "2015-06-10")
    private String diagnosisDate;

    /**
     * Current status of the condition: {@code ACTIVE} (ongoing) or
     * {@code RESOLVED} (no longer affecting the patient).
     */
    @Schema(description = "Condition status", allowableValues = {"ACTIVE", "RESOLVED"}, example = "ACTIVE")
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
