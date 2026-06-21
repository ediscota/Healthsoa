package it.disim.univaq.sose.healthsoa.clinical.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a known allergy for a patient.
 *
 * <p>Sourced from the Anagrafe SOAP {@code getAllergies} operation and mapped
 * from the JAX-WS generated {@code Allergy} type. The {@code allergen} field
 * is used by the Care Coordinator's {@code RiskAnalyzer} to detect cross-reactions
 * with active prescriptions (specification §6, rule 2).
 */
@Schema(description = "A known allergy with the causative substance and reaction severity")
public class AllergyDto {

    /**
     * Name of the allergenic substance (e.g., "Penicillina", "Amoxicillina").
     * Compared against active prescription drug names by the {@code RiskAnalyzer}.
     */
    @Schema(description = "Allergenic substance name", example = "Penicillina")
    private String allergen;

    /**
     * Clinical severity of the allergic reaction:
     * {@code MILD} (minor symptoms), {@code SEVERE} (significant reaction),
     * or {@code ANAPHYLACTIC} (life-threatening reaction).
     */
    @Schema(description = "Reaction severity",
            allowableValues = {"MILD", "SEVERE", "ANAPHYLACTIC"}, example = "SEVERE")
    private String severity;

    public AllergyDto() {}

    public String getAllergen() { return allergen; }
    public void setAllergen(String allergen) { this.allergen = allergen; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
