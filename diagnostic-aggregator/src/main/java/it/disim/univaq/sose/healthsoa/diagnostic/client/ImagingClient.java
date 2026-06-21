package it.disim.univaq.sose.healthsoa.diagnostic.client;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for the Imaging Service (Provider 4).
 *
 * <p>Resolved via Eureka discovery under the name {@code imaging-service}.
 * Used by {@code DiagnosticService} to retrieve archived radiology reports when
 * building a {@code DiagnosticBundle}. Load-balanced across all running instances
 * of {@code imaging-service}.
 *
 * <p>Method mapping mirrors {@code ImagingController.getReportsByPatient()}:
 * the optional {@code examType} query parameter filters results by panel code
 * (e.g., passing {@code "PANEL_RENAL"} returns only renal imaging reports).
 */
@FeignClient(name = "imaging-service")
public interface ImagingClient {

    /**
     * Retrieves archived radiology reports for a patient, optionally filtered
     * by exam type.
     *
     * @param patientId numeric patient identifier
     * @param examType  optional exam type filter (e.g., "PANEL_RENAL"); pass
     *                  {@code null} to retrieve all reports for the patient
     * @return list of matching imaging report DTOs (may be empty)
     */
    @GetMapping("/patients/{patientId}/reports")
    List<ImagingReportDto> getReports(@PathVariable("patientId") String patientId,
                                      @RequestParam(name = "examType", required = false) String examType);
}
