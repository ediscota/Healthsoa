package it.disim.univaq.sose.healthsoa.coordinator.dto;

public class PatientDto {

    private long id;
    private String fiscalCode;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String phone;

    public PatientDto() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
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
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
