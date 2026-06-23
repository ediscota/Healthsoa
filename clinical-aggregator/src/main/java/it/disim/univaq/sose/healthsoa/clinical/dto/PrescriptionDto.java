package it.disim.univaq.sose.healthsoa.clinical.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO representing an active pharmaceutical prescription.
 *
 * <p>Sourced from the Farmacia Service (REST, Provider 3) via the {@code FarmaciaClient}
 * Feign client. Used by the Care Coordinator's {@code RiskAnalyzer} to detect:
 * <ul>
 *   <li>Nephrotoxic drugs (e.g., NSAIDs, aminoglycosides) in a patient with
 *       impaired renal function - specification §6, rule 1;</li>
 *   <li>Prescriptions that contain an allergen the patient is known to react to
 *       - specification §6, rule 2.</li>
 * </ul>
 */
@Schema(description = "An active pharmaceutical prescription from the Farmacia Service")
public class PrescriptionDto {

    /** Database-assigned identifier for the prescription. */
    @Schema(description = "Prescription identifier", example = "5")
    private Long id;

    /** Patient identifier this prescription belongs to. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /**
     * Name of the prescribed drug (e.g., "Ibuprofene", "Amoxicillina").
     * Compared against the patient's allergen list and the nephrotoxic-drug
     * catalogue in {@code RiskAnalyzer}.
     */
    @Schema(description = "Drug name", example = "Ibuprofene")
    private String drugName;

    /** ATC (Anatomical Therapeutic Chemical) classification code. */
    @Schema(description = "ATC classification code", example = "M01AE01")
    private String atcCode;

    /** Dosage instructions as free text (e.g., "500mg"). */
    @Schema(description = "Dosage amount", example = "500mg")
    private String dosage;

    /** Administration frequency (e.g., "twice daily"). */
    @Schema(description = "Frequency of administration", example = "twice daily")
    private String frequency;

    /** Date when the prescription was issued. */
    @Schema(description = "Prescription start date")
    private LocalDate startDate;

    /** Expected end date of the prescription (may be null for indefinite therapies). */
    @Schema(description = "Expected end date (null for indefinite therapies)")
    private LocalDate expectedEndDate;

    /** Name or identifier of the prescribing physician. */
    @Schema(description = "Prescribing doctor's name or identifier", example = "Dr. Bianchi")
    private String prescribingDoctor;

    public PrescriptionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getAtcCode() { return atcCode; }
    public void setAtcCode(String atcCode) { this.atcCode = atcCode; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getExpectedEndDate() { return expectedEndDate; }
    public void setExpectedEndDate(LocalDate expectedEndDate) { this.expectedEndDate = expectedEndDate; }
    public String getPrescribingDoctor() { return prescribingDoctor; }
    public void setPrescribingDoctor(String prescribingDoctor) { this.prescribingDoctor = prescribingDoctor; }
}
