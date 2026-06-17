package it.disim.univaq.sose.healthsoa.clinical.dto;

public class AllergyDto {

    private String allergen;
    private String severity;

    public AllergyDto() {}

    public String getAllergen() { return allergen; }
    public void setAllergen(String allergen) { this.allergen = allergen; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
