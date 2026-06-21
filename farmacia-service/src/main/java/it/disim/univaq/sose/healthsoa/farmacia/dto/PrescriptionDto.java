package it.disim.univaq.sose.healthsoa.farmacia.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO representation of a {@link it.disim.univaq.sose.healthsoa.farmacia.model.Prescription}.
 *
 * <p>Returned by all read endpoints of the Farmacia Service (
 * {@code GET /patients/{patientId}/prescriptions} and
 * {@code GET /patients/{patientId}/prescriptions/{id}}).
 * Consumed by the Clinical Aggregator (Prosumer 2) when building the
 * {@code ClinicalProfile.activePrescriptions} list.
 */
@Schema(description = "A pharmaceutical prescription issued by the Farmacia Service")
public class PrescriptionDto {

    @Schema(description = "Prescription identifier", example = "1")
    private Long id;

    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    @Schema(description = "Drug name (INN)", example = "Ibuprofene")
    private String drugName;

    @Schema(description = "ATC classification code", example = "M01AE01")
    private String atcCode;

    @Schema(description = "Dose amount per administration", example = "200mg")
    private String dosage;

    @Schema(description = "Administration schedule", example = "twice daily")
    private String frequency;

    @Schema(description = "Prescription issue date")
    private LocalDate startDate;

    @Schema(description = "Expected end date (null for indefinite therapy)")
    private LocalDate expectedEndDate;

    @Schema(description = "Prescribing doctor's name", example = "Dr. Bianchi")
    private String prescribingDoctor;

    public PrescriptionDto() {
    }

    public PrescriptionDto(Long id, String patientId, String drugName, String atcCode,
                            String dosage, String frequency,
                            LocalDate startDate, LocalDate expectedEndDate, String prescribingDoctor) {
        this.id = id;
        this.patientId = patientId;
        this.drugName = drugName;
        this.atcCode = atcCode;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.prescribingDoctor = prescribingDoctor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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
