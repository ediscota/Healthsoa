package it.disim.univaq.sose.healthsoa.anagrafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * JPA entity representing a single clinical condition (diagnosis or past admission)
 * in a patient's medical history.
 *
 * <p>Mapped to the {@code condition_entry} table (table name chosen to avoid the
 * reserved word {@code condition} in MySQL). Linked to {@link PatientEntity} via a
 * many-to-one foreign key. Fetched lazily to avoid loading the full history when
 * only demographics are needed.
 *
 * <p>Exposed via the SOAP {@code getMedicalHistory} operation.
 */
@Entity
@Table(name = "condition_entry")
public class ConditionEntity {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Back-reference to the owning patient. Loaded lazily. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    /** ICD-10 classification code (e.g., {@code I10} for hypertension). */
    @Column(name = "icd10_code", nullable = false)
    private String icd10Code;

    /** Human-readable description of the condition. */
    @Column(nullable = false)
    private String description;

    /** Date the condition was first recorded. */
    @Column(name = "onset_date", nullable = false)
    private LocalDate onsetDate;

    /** Current lifecycle status: ACTIVE or RESOLVED. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionStatusEnum status;

    public ConditionEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PatientEntity getPatient() { return patient; }
    public void setPatient(PatientEntity patient) { this.patient = patient; }

    public String getIcd10Code() { return icd10Code; }
    public void setIcd10Code(String icd10Code) { this.icd10Code = icd10Code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getOnsetDate() { return onsetDate; }
    public void setOnsetDate(LocalDate onsetDate) { this.onsetDate = onsetDate; }

    public ConditionStatusEnum getStatus() { return status; }
    public void setStatus(ConditionStatusEnum status) { this.status = status; }

}
