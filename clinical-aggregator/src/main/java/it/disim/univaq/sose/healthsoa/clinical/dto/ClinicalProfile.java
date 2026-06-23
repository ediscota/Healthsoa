package it.disim.univaq.sose.healthsoa.clinical.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Aggregated clinical profile of a patient, assembled by the Clinical Aggregator.
 *
 * <p>This is the top-level response object returned by
 * {@code GET /patients/{patientId}/profile}. It combines data from two providers:
 * <ul>
 *   <li>Anagrafe Pazienti (SOAP, Provider 1) - patient demographics, medical
 *       history (ICD-10 conditions), and known allergies;</li>
 *   <li>Farmacia Service (REST, Provider 3) - active pharmaceutical prescriptions.</li>
 * </ul>
 *
 * <p>Consumed by:
 * <ul>
 *   <li>The clinical workstation web client (UC-2 - history consultation);</li>
 *   <li>The Care Coordinator (UC-1 - embedded in the {@code FitnessReport} and
 *       used as input for risk analysis in {@code RiskAnalyzer}).</li>
 * </ul>
 */
@Schema(description = "Complete clinical profile of a patient aggregated from the Anagrafe SOAP service and the Farmacia REST service")
public class ClinicalProfile {

    /** Demographic information sourced from the Anagrafe SOAP service. */
    @Schema(description = "Patient demographic data")
    private PatientDto patient;

    /**
     * Chronological list of diagnoses and hospital admissions (ICD-10 coded),
     * sourced from the Anagrafe SOAP {@code getMedicalHistory} operation.
     */
    @Schema(description = "List of ICD-10 coded conditions and past hospital admissions")
    private List<ConditionDto> medicalHistory;

    /**
     * Known allergies with reaction severity, sourced from the Anagrafe SOAP
     * {@code getAllergies} operation. Used by {@code RiskAnalyzer} to detect
     * prescription conflicts (specification §6, rule 2).
     */
    @Schema(description = "List of known allergies with reaction severity")
    private List<AllergyDto> allergies;

    /**
     * Active pharmaceutical prescriptions sourced from the Farmacia REST service.
     * Used by {@code RiskAnalyzer} to detect nephrotoxic drugs and allergy conflicts.
     */
    @Schema(description = "List of currently active prescriptions")
    private List<PrescriptionDto> activePrescriptions;

    public ClinicalProfile() {}

    public ClinicalProfile(PatientDto patient, List<ConditionDto> medicalHistory,
                           List<AllergyDto> allergies, List<PrescriptionDto> activePrescriptions) {
        this.patient = patient;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.activePrescriptions = activePrescriptions;
    }

    public PatientDto getPatient() { return patient; }
    public void setPatient(PatientDto patient) { this.patient = patient; }
    public List<ConditionDto> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<ConditionDto> medicalHistory) { this.medicalHistory = medicalHistory; }
    public List<AllergyDto> getAllergies() { return allergies; }
    public void setAllergies(List<AllergyDto> allergies) { this.allergies = allergies; }
    public List<PrescriptionDto> getActivePrescriptions() { return activePrescriptions; }
    public void setActivePrescriptions(List<PrescriptionDto> activePrescriptions) { this.activePrescriptions = activePrescriptions; }
}
