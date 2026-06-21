package it.disim.univaq.sose.healthsoa.imaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for {@code POST /imaging/orders}.
 *
 * <p>Carries the patient identifier and the type of imaging exam requested.
 * The exam type determines which pre-defined findings template is used during
 * the asynchronous processing simulation.
 */
@Schema(description = "Request body for submitting a new imaging order")
public class ImagingOrderRequest {

    /** Numeric patient identifier (must exist in the anagrafe database). */
    @Schema(description = "Numeric patient identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String patientId;

    /**
     * Type of imaging examination (e.g., {@code RX_TORACE}, {@code TC_ADDOME},
     * {@code RM_CRANIO}, {@code ECO_ADDOME}). Determines the findings template
     * used by {@code ImagingService.EXAM_FINDINGS}.
     */
    @Schema(description = "Imaging exam type", example = "RX_TORACE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String examType;

    public ImagingOrderRequest() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
}
