package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO mirror of {@code ClinicalProfile} from the Clinical Aggregator, used
 * internally by the Care Coordinator after deserialization from the Feign response.
 *
 * <p>Carries the patient's demographics, medical history, known allergies, and
 * active prescriptions. Passed directly to {@code RiskAnalyzer} and embedded in
 * the returned {@code FitnessReport}.
 */
@Schema(description = "Clinical profile as received from the Clinical Aggregator")
public class ClinicalProfileDto {

    @Schema(description = "Patient demographics")
    private PatientDto patient;

    @Schema(description = "ICD-10 coded conditions in the patient's history")
    private List<ConditionDto> medicalHistory;

    @Schema(description = "Known allergies with severity")
    private List<AllergyDto> allergies;

    @Schema(description = "Currently active prescriptions")
    private List<PrescriptionDto> activePrescriptions;

    public ClinicalProfileDto() {}

    public PatientDto getPatient() { return patient; }
    public void setPatient(PatientDto patient) { this.patient = patient; }
    public List<ConditionDto> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<ConditionDto> medicalHistory) { this.medicalHistory = medicalHistory; }
    public List<AllergyDto> getAllergies() { return allergies; }
    public void setAllergies(List<AllergyDto> allergies) { this.allergies = allergies; }
    public List<PrescriptionDto> getActivePrescriptions() { return activePrescriptions; }
    public void setActivePrescriptions(List<PrescriptionDto> activePrescriptions) { this.activePrescriptions = activePrescriptions; }
}
