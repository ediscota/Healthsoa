package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A pharmaceutical prescription as deserialized from the Clinical Aggregator response.
 * The {@code drugName} is matched against the patient's allergen list and the
 * nephrotoxic-drug catalogue in {@code RiskAnalyzer}.
 */
@Schema(description = "A pharmaceutical prescription from the patient's clinical profile")
public class PrescriptionDto {

    @Schema(description = "Prescription identifier", example = "5")
    private Long id;
    private String patientId;
    private String drugName;
    private String atcCode;
    private String dosage;
    private String frequency;
    private String startDate;
    private String expectedEndDate;
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
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getExpectedEndDate() { return expectedEndDate; }
    public void setExpectedEndDate(String expectedEndDate) { this.expectedEndDate = expectedEndDate; }
    public String getPrescribingDoctor() { return prescribingDoctor; }
    public void setPrescribingDoctor(String prescribingDoctor) { this.prescribingDoctor = prescribingDoctor; }
}
