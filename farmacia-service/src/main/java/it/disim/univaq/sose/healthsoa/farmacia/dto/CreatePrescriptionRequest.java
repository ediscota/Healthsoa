package it.disim.univaq.sose.healthsoa.farmacia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Request body for {@code POST /patients/{patientId}/prescriptions} (UC-4).
 *
 * <p>The {@code patientId} is taken from the URL path variable and is not repeated here.
 * All fields are validated by Jakarta Bean Validation before the controller delegates
 * to the service layer.
 */
@io.swagger.v3.oas.annotations.media.Schema(description = "Request body for creating a new prescription (UC-4)")
public class CreatePrescriptionRequest {

    @NotBlank(message = "Drug name is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "Drug name (INN)", example = "Ibuprofene", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String drugName;

    @NotBlank(message = "ATC code is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "ATC classification code", example = "M01AE01", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String atcCode;

    @NotBlank(message = "Dosage is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "Dose per administration", example = "200mg", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "Administration frequency", example = "twice daily", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String frequency;

    @NotNull(message = "Start date is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "Prescription issue date")
    private LocalDate startDate;

    @io.swagger.v3.oas.annotations.media.Schema(description = "Expected end date (omit for indefinite therapy)")
    private LocalDate expectedEndDate;

    @NotBlank(message = "Prescribing doctor is required")
    @io.swagger.v3.oas.annotations.media.Schema(description = "Prescribing doctor's name", example = "Dr. Bianchi", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
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
