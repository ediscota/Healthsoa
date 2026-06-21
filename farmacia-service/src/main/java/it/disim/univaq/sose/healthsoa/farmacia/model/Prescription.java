package it.disim.univaq.sose.healthsoa.farmacia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * JPA entity representing a pharmaceutical prescription in the Farmacia Service.
 *
 * <p>Mapped to the {@code prescription} table. One row per patient-drug combination.
 * The {@code patientId} is a string (not a foreign key) because the Farmacia Service
 * does not co-locate the patient database — patient existence is assumed to be
 * guaranteed by the caller (the clinical workstation via UC-4, or the seed script).
 *
 * <p>Exposed via REST:
 * <ul>
 *   <li>Read-only: {@code GET /patients/{patientId}/prescriptions} (consumed by Clinical Aggregator);</li>
 *   <li>Write: {@code POST /patients/{patientId}/prescriptions} (UC-4, direct via Gateway).</li>
 * </ul>
 */
@Entity
@Table(name = "prescription")
public class Prescription {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numeric patient identifier referencing the Anagrafe patient table. */
    @Column(name = "patient_id", nullable = false)
    private String patientId;

    /** International non-proprietary name of the drug (e.g., "Ibuprofene"). */
    @Column(name = "drug_name", nullable = false)
    private String drugName;

    /** ATC (Anatomical Therapeutic Chemical) classification code (e.g., "M01AE01"). */
    @Column(name = "atc_code", nullable = false)
    private String atcCode;

    /** Dose amount per administration (e.g., "200mg"). */
    @Column(nullable = false)
    private String dosage;

    /** Administration schedule (e.g., "twice daily", "once daily"). */
    @Column(nullable = false)
    private String frequency;

    /** Date the prescription was issued and treatment started. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Planned end date; {@code null} for indefinite therapies (e.g., antihypertensives). */
    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    /** Name or identifier of the physician who issued the prescription. */
    @Column(name = "prescribing_doctor", nullable = false)
    private String prescribingDoctor;

    public Prescription() {
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
