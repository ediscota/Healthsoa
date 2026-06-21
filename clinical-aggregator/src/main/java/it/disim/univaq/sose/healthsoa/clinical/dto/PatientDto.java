package it.disim.univaq.sose.healthsoa.clinical.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO carrying patient demographic data sourced from the Anagrafe SOAP service.
 *
 * <p>Populated by {@code ClinicalAggregatorService.toPatientDto()} from the
 * JAX-WS generated {@code Patient} type returned by {@code getPatientById}.
 */
@Schema(description = "Patient demographic data retrieved from the Anagrafe SOAP service")
public class PatientDto {

    /** Database primary key of the patient in the Anagrafe. */
    @Schema(description = "Patient internal identifier", example = "1")
    private Long id;

    /** Italian fiscal code (codice fiscale), unique patient identifier. */
    @Schema(description = "Italian fiscal code (unique)", example = "RSSMRA80A01H501Z")
    private String fiscalCode;

    /** Patient's given name. */
    @Schema(description = "First name", example = "Mario")
    private String firstName;

    /** Patient's family name. */
    @Schema(description = "Last name", example = "Rossi")
    private String lastName;

    /** Date of birth in ISO-8601 format (YYYY-MM-DD). */
    @Schema(description = "Date of birth (ISO-8601)", example = "1980-01-01")
    private String dateOfBirth;

    /** Biological sex: M, F, or OTHER. */
    @Schema(description = "Gender (M / F / OTHER)", example = "M")
    private String gender;

    /** ABO blood type (not always populated). */
    @Schema(description = "Blood type (if available)", example = "A+")
    private String bloodType;

    /** Residential address (not always populated). */
    @Schema(description = "Residential address")
    private String address;

    /** Contact phone number. */
    @Schema(description = "Contact phone number", example = "3331234567")
    private String phone;

    public PatientDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFiscalCode() { return fiscalCode; }
    public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
