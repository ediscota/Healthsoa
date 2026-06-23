package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Top-level response of the Care Coordinator's fitness assessment.
 *
 * <p>Returned by {@code GET /patients/{patientId}/assess} after the
 * two parallel {@code CompletableFuture} calls to the Clinical Aggregator
 * and Diagnostic Aggregator have been joined and the {@code RiskAnalyzer}
 * has evaluated the collected data.
 *
 * <p>The {@code outcome} field is the headline result:
 * <ul>
 *   <li>{@code IDONEO} - no risk flags, patient is medically fit;</li>
 *   <li>{@code CON_RISERVA} - at least one WARNING flag, fitness is conditional;</li>
 *   <li>{@code NON_IDONEO} - at least one CRITICAL flag, patient is not fit.</li>
 * </ul>
 *
 * <p>The full {@code clinicalProfile} and {@code diagnosticBundle} are embedded
 * so the web client can display the supporting evidence without additional API calls.
 */
@Schema(description = "Fitness assessment report produced by the Care Coordinator")
public class FitnessReport {

    /**
     * Summary outcome determined by the {@code RiskAnalyzer}.
     * One of: {@code IDONEO}, {@code CON_RISERVA}, {@code NON_IDONEO}.
     */
    @Schema(description = "Overall fitness outcome",
            allowableValues = {"IDONEO", "CON_RISERVA", "NON_IDONEO"}, example = "CON_RISERVA")
    private String outcome;  // IDONEO, CON_RISERVA, NON_IDONEO
    /** Patient for whom the assessment was generated. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /** Timestamp when this report was assembled by the Care Coordinator. */
    @Schema(description = "Report generation timestamp (ISO-8601)")
    private LocalDateTime generatedAt;

    /** Full clinical profile assembled by the Clinical Aggregator. */
    @Schema(description = "Clinical profile sourced from the Clinical Aggregator")
    private ClinicalProfileDto clinicalProfile;

    /** Diagnostic bundle sourced from the Diagnostic Aggregator. */
    @Schema(description = "Diagnostic bundle sourced from the Diagnostic Aggregator")
    private DiagnosticBundleDto diagnosticBundle;

    /**
     * List of risk flags detected by the {@code RiskAnalyzer}.
     * An empty list corresponds to outcome {@code IDONEO}.
     * WARNING flags → {@code CON_RISERVA}; CRITICAL flags → {@code NON_IDONEO}.
     */
    @Schema(description = "List of detected risk flags (empty if outcome is IDONEO)")
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
