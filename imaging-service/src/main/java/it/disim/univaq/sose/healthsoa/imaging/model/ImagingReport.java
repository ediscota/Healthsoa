package it.disim.univaq.sose.healthsoa.imaging.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Referto di diagnostica per immagini archiviato nel RIS/PACS simulato.
 * Può essere pre-esistente (data.sql) oppure il risultato di una richiesta asincrona.
 */
@Entity
@Table(name = "imaging_report")
public class ImagingReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientId;

    /** Tipo di esame: RX_TORACE, TC_ADDOME, RM_CRANIO, ECO_ADDOME, ... */
    @Column(nullable = false)
    private String examType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImagingOrderStatus status;

    /** Descrizione testuale del radiologo, disponibile solo a COMPLETED. */
    @Column(columnDefinition = "TEXT")
    private String findings;

    /** Conclusione sintetica del radiologo. */
    private String conclusion;

    /** Eventuale flag: true se il radiologo segnala un rilievo critico. */
    private boolean criticalFlag;

    private LocalDate reportDate;

    /** URL di callback registrata dal richiedente per la notifica al completamento. */
    private String callbackUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public ImagingOrderStatus getStatus() { return status; }
    public void setStatus(ImagingOrderStatus status) { this.status = status; }

    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public boolean isCriticalFlag() { return criticalFlag; }
    public void setCriticalFlag(boolean criticalFlag) { this.criticalFlag = criticalFlag; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}
