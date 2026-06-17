package it.disim.univaq.sose.healthsoa.coordinator.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FitnessReport {

    private String outcome;  // IDONEO, CON_RISERVA, NON_IDONEO
    private String patientId;
    private LocalDateTime generatedAt;
    private ClinicalProfileDto clinicalProfile;
    private DiagnosticBundleDto diagnosticBundle;
    private List<RiskFlag> riskFlags;

    public FitnessReport() {}

    public FitnessReport(String outcome, String patientId, LocalDateTime generatedAt,
                         ClinicalProfileDto clinicalProfile, DiagnosticBundleDto diagnosticBundle,
                         List<RiskFlag> riskFlags) {
        this.outcome = outcome;
        this.patientId = patientId;
        this.generatedAt = generatedAt;
        this.clinicalProfile = clinicalProfile;
        this.diagnosticBundle = diagnosticBundle;
        this.riskFlags = riskFlags;
    }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public ClinicalProfileDto getClinicalProfile() { return clinicalProfile; }
    public void setClinicalProfile(ClinicalProfileDto clinicalProfile) { this.clinicalProfile = clinicalProfile; }
    public DiagnosticBundleDto getDiagnosticBundle() { return diagnosticBundle; }
    public void setDiagnosticBundle(DiagnosticBundleDto diagnosticBundle) { this.diagnosticBundle = diagnosticBundle; }
    public List<RiskFlag> getRiskFlags() { return riskFlags; }
    public void setRiskFlags(List<RiskFlag> riskFlags) { this.riskFlags = riskFlags; }
}
