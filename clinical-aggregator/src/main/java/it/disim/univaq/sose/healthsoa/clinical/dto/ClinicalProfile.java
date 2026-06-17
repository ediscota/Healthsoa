package it.disim.univaq.sose.healthsoa.clinical.dto;

import java.util.List;

/**
 * Oggetto di risposta del Clinical Aggregator: aggrega dati anagrafici (SOAP),
 * storico patologico, allergie e terapie attive (REST) in un unico DTO REST.
 */
public class ClinicalProfile {

    private PatientDto patient;
    private List<ConditionDto> medicalHistory;
    private List<AllergyDto> allergies;
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
