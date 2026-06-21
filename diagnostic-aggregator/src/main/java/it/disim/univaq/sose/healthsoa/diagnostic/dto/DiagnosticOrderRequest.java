package it.disim.univaq.sose.healthsoa.diagnostic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for {@code POST /patients/{patientId}/diagnostics}.
 *
 * <p>Carries the exam panel code that the Diagnostic Aggregator will forward to
 * the Laboratory Service. The patient identifier is taken from the path variable.
 */
@Schema(description = "Request body for ordering a diagnostic panel via the Diagnostic Aggregator")
public class DiagnosticOrderRequest {

    /**
     * Code identifying the diagnostic panel (e.g., {@code CBC}, {@code PANEL_RENAL},
     * {@code PANEL_METABOLIC}). This code is passed verbatim to the Laboratory
     * Service as the {@code examCode} field.
     */
    @Schema(description = "Exam panel code to submit to the laboratory", example = "PANEL_RENAL",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String panelCode;

    public DiagnosticOrderRequest() {}

    public String getPanelCode() { return panelCode; }
    public void setPanelCode(String panelCode) { this.panelCode = panelCode; }
}
