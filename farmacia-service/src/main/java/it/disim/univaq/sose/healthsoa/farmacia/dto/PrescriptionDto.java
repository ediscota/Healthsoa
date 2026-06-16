package it.disim.univaq.sose.healthsoa.farmacia.dto;

import java.time.LocalDate;

public class PrescriptionDto {

    private String drugName;
    private String atcCode;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private String prescribingDoctor;

    public PrescriptionDto() {
    }

    public PrescriptionDto(String drugName, String atcCode, String dosage, String frequency,
                            LocalDate startDate, LocalDate expectedEndDate, String prescribingDoctor) {
        this.drugName = drugName;
        this.atcCode = atcCode;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.prescribingDoctor = prescribingDoctor;
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
