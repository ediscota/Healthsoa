package it.disim.univaq.sose.healthsoa.coordinator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO mirror of {@code DiagnosticBundle} from the Diagnostic Aggregator, used
 * internally by the Care Coordinator after deserialization from the Feign response.
 *
 * <p>Carries the lab test result and imaging reports for the patient.
 * Passed directly to {@code RiskAnalyzer} and embedded in the returned {@code FitnessReport}.
 */
@Schema(description = "Diagnostic bundle as received from the Diagnostic Aggregator")
public class DiagnosticBundleDto {

    @Schema(description = "Patient identifier", example = "1")
    private String patientId;

    @Schema(description = "Lab test result (null if the order did not complete)")
    private TestResultDto labResult;

    @Schema(description = "List of imaging reports for the patient")
    private List<ImagingReportDto> imagingReports;

    public DiagnosticBundleDto() {}

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public TestResultDto getLabResult() { return labResult; }
    public void setLabResult(TestResultDto labResult) { this.labResult = labResult; }
    public List<ImagingReportDto> getImagingReports() { return imagingReports; }
    public void setImagingReports(List<ImagingReportDto> imagingReports) { this.imagingReports = imagingReports; }
}
