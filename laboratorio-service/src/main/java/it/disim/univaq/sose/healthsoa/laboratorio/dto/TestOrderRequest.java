package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for {@code POST /tests/orders}.
 *
 * <p>Carries the patient identifier and the exam panel code that determine which
 * measurements will be produced when the order is processed asynchronously.
 */
@Schema(description = "Request body for submitting a new laboratory test order")
public class TestOrderRequest {

    /** Numeric patient identifier (matches the primary key in the anagrafe database). */
    @Schema(description = "Numeric patient identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String patientId;

    /**
     * Code of the exam panel to run (e.g., {@code PANEL_RENAL}, {@code CBC},
     * {@code PANEL_METABOLIC}). The panel code determines which parameters are
     * measured and stored as {@code Measurement} entities.
     */
    @Schema(description = "Exam panel code", example = "PANEL_RENAL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String examCode;

    public TestOrderRequest() {
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

}
