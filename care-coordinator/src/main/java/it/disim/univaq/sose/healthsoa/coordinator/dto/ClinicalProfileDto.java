package it.disim.univaq.sose.healthsoa.coordinator.dto;

import java.util.List;

public class ClinicalProfileDto {

    private PatientDto patient;
    private List<ConditionDto> medicalHistory;
    private List<AllergyDto> allergies;
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
