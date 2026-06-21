package it.disim.univaq.sose.healthsoa.diagnostic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Aggregated diagnostic data for a patient: lab results and imaging reports.
 *
 * <p>Returned by {@code GET /tracking/{trackingId}/result} (UC-3) and
 * {@code GET /patients/{patientId}/bundle} (UC-1 internal endpoint consumed by
 * the Care Coordinator). Combines:
 * <ul>
 *   <li>A single {@link TestResultDto} from the Laboratory Service (may be null if
 *       the lab order did not complete in time);</li>
 *   <li>Zero or more {@link ImagingReportDto} objects from the Imaging Service.</li>
 * </ul>
 *
 * <p>The Care Coordinator embeds this bundle in the {@code FitnessReport} and uses
 * the lab measurements and imaging critical flags for risk analysis (specification §6).
 */
@Schema(description = "Diagnostic bundle aggregating lab results and imaging reports for a patient")
public class DiagnosticBundle {

    /** Patient identifier shared by all items in this bundle. */
    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    /**
     * Lab test result from the Laboratory Service.
     * May be {@code null} if the order has not completed yet (used only in the
     * synchronous bundle endpoint when the polling loop times out).
     */
    @Schema(description = "Lab test result (null if the order did not complete)")
    private TestResultDto labResult;

    /**
     * List of radiology reports from the Imaging Service.
     * May be empty if no archived reports exist for the patient and panel.
     */
    @Schema(description = "List of radiology reports for the patient")
    private List<ImagingReportDto> imagingReports;

    public DiagnosticBundle() {}

    public DiagnosticBundle(String patientId, TestResultDto labResult, List<ImagingReportDto> imagingReports) {
        this.patientId = patientId;
        this.labResult = labResult;
        this.imagingReports = imagingReports;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public TestResultDto getLabResult() { return labResult; }
    public void setLabResult(TestResultDto labResult) { this.labResult = labResult; }
    public List<ImagingReportDto> getImagingReports() { return imagingReports; }
    public void setImagingReports(List<ImagingReportDto> imagingReports) { this.imagingReports = imagingReports; }
}
