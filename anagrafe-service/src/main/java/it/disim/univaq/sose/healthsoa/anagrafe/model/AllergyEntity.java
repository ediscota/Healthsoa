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
 * JPA entity representing a known allergic reaction for a patient.
 *
 * <p>Mapped to the {@code allergy} table. Linked to {@link PatientEntity} via a
 * many-to-one foreign key. The {@code substance} field is matched by name against
 * prescription drug names in the Care Coordinator's {@code RiskAnalyzer}.
 *
 * <p>Exposed via the SOAP {@code getAllergies} operation.
 */
@Entity
@Table(name = "allergy")
public class AllergyEntity {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Back-reference to the owning patient. Loaded lazily. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    /** Name of the allergenic substance (e.g., "Penicillina"). */
    @Column(nullable = false)
    private String substance;

    /** Clinical severity of the allergic reaction - see {@link ReactionSeverityEnum}. */
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_severity", nullable = false)
    private ReactionSeverityEnum reactionSeverity;

    /** Date the allergy was first detected or recorded. */
    @Column(name = "detected_date", nullable = false)
    private LocalDate detectedDate;

    public AllergyEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PatientEntity getPatient() { return patient; }
    public void setPatient(PatientEntity patient) { this.patient = patient; }

    public String getSubstance() { return substance; }
    public void setSubstance(String substance) { this.substance = substance; }

    public ReactionSeverityEnum getReactionSeverity() { return reactionSeverity; }
    public void setReactionSeverity(ReactionSeverityEnum reactionSeverity) { this.reactionSeverity = reactionSeverity; }

    public LocalDate getDetectedDate() { return detectedDate; }
    public void setDetectedDate(LocalDate detectedDate) { this.detectedDate = detectedDate; }

}
