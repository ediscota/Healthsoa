package it.disim.univaq.sose.healthsoa.anagrafe.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a patient in the Anagrafe Pazienti database.
 *
 * <p>Mapped to the {@code patient} table. Serves as the root aggregate for the
 * patient's clinical data: conditions are stored in the {@code condition_entry}
 * table, allergies in the {@code allergy} table, both linked by foreign key.
 *
 * <p>Exposed externally only via the SOAP endpoint ({@code AnagrafeEndpoint});
 * never returned directly from a REST controller.
 */
@Entity
@Table(name = "patient")
public class PatientEntity {

    /** Auto-generated primary key, referenced by consumers as the patient identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Italian fiscal code (codice fiscale) - a unique 16-character alphanumeric
     * identifier assigned by the Italian government. Used as the natural key in
     * search operations (SOAP {@code getPatientByFiscalCode}).
     */
    @Column(name = "fiscal_code", nullable = false, unique = true)
    private String fiscalCode;

    /** Patient's given name. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** Patient's family name. */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** Date of birth; used to compute age for risk analysis. */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /** Biological sex - see {@link GenderEnum}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderEnum gender;

    /** Optional contact phone number. */
    @Column
    private String phone;

    /** Medical history: all diagnoses and past admissions linked to this patient. */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConditionEntity> conditions = new ArrayList<>();

    /** Allergy registry: all known allergic reactions for this patient. */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AllergyEntity> allergies = new ArrayList<>();

    public PatientEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFiscalCode() { return fiscalCode; }
    public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public GenderEnum getGender() { return gender; }
    public void setGender(GenderEnum gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<ConditionEntity> getConditions() { return conditions; }
    public void setConditions(List<ConditionEntity> conditions) { this.conditions = conditions; }

    public List<AllergyEntity> getAllergies() { return allergies; }
    public void setAllergies(List<AllergyEntity> allergies) { this.allergies = allergies; }

}
