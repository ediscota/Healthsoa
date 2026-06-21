package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A known allergy as deserialized from the Clinical Aggregator response.
 * The {@code allergen} name is matched against active prescription drug names
 * by {@code RiskAnalyzer} to detect allergy-prescription conflicts.
 */
@Schema(description = "A known allergy from the patient's clinical profile")
public class AllergyDto {

    /** Allergenic substance name (e.g., "Penicillina"). */
    @Schema(description = "Allergenic substance", example = "Penicillina")
    private String allergen;

    /** Reaction severity: MILD, SEVERE, or ANAPHYLACTIC. */
    @Schema(description = "Reaction severity", allowableValues = {"MILD","SEVERE","ANAPHYLACTIC"})
    private String severity;

    public AllergyDto() {}

    public String getAllergen() { return allergen; }
    public void setAllergen(String allergen) { this.allergen = allergen; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
