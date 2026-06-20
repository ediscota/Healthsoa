package it.disim.univaq.sose.healthsoa.farmacia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Corpo della richiesta POST /patients/{patientId}/prescriptions.
 * Il patientId è ricevuto dal path variable; i restanti campi obbligatori
 * vengono validati da Jakarta Bean Validation prima che il controller
 * invochi il service layer.
 */
public class CreatePrescriptionRequest {

    @NotBlank(message = "Il nome del farmaco è obbligatorio")
    private String drugName;

    @NotBlank(message = "Il codice ATC è obbligatorio")
    private String atcCode;

    @NotBlank(message = "Il dosaggio è obbligatorio")
    private String dosage;

    @NotBlank(message = "La frequenza di somministrazione è obbligatoria")
    private String frequency;

    @NotNull(message = "La data di inizio è obbligatoria")
    private LocalDate startDate;

    private LocalDate expectedEndDate;

    @NotBlank(message = "Il medico prescrittore è obbligatorio")
    private String prescribingDoctor;

    public CreatePrescriptionRequest() {
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public String getPrescribingDoctor() {
        return prescribingDoctor;
    }

    public void setPrescribingDoctor(String prescribingDoctor) {
        this.prescribingDoctor = prescribingDoctor;
    }
}
