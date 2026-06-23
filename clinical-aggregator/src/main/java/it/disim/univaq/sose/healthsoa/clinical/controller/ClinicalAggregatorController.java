package it.disim.univaq.sose.healthsoa.clinical.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.disim.univaq.sose.healthsoa.clinical.dto.ClinicalProfile;
import it.disim.univaq.sose.healthsoa.clinical.service.ClinicalAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Clinical Aggregator (Prosumer 2).
 *
 * <p>This prosumer bridges the legacy SOAP protocol of the Anagrafe Pazienti
 * service (Provider 1) and the REST architecture of the rest of the system. It
 * exposes a single REST endpoint that:
 * <ol>
 *   <li>Calls {@code anagrafe-service} via three SOAP operations
 *       ({@code getPatientById}, {@code getMedicalHistory}, {@code getAllergies});</li>
 *   <li>Calls {@code farmacia-service} via Feign REST to retrieve active prescriptions;</li>
 *   <li>Composes all four data sets into a single {@link ClinicalProfile} and returns it.</li>
 * </ol>
 *
 * <p>This endpoint is used by:
 * <ul>
 *   <li><strong>UC-2</strong>: direct client → Clinical Aggregator call for history consultation;</li>
 *   <li><strong>UC-1</strong>: Care Coordinator calls this endpoint in parallel with the
 *       Diagnostic Aggregator inside a {@code CompletableFuture.supplyAsync()}.</li>
 * </ul>
 *
 * <p>All requests are routed through the API Gateway at {@code /api/clinical/**}.
 */
@RestController
@Tag(name = "Clinical Aggregator", description = "Prosumer 2 - aggregates SOAP anagrafe data and REST prescriptions into a ClinicalProfile (UC-2 and UC-1)")
public class ClinicalAggregatorController {

    private final ClinicalAggregatorService service;

    public ClinicalAggregatorController(ClinicalAggregatorService service) {
        this.service = service;
    }

    /**
     * Returns the complete clinical profile of a patient (UC-2 entry point).
     *
     * <p>Internally makes four remote calls:
     * <ol>
     *   <li>SOAP {@code getPatientById} → demographic data;</li>
     *   <li>SOAP {@code getMedicalHistory} → list of ICD-10 conditions;</li>
     *   <li>SOAP {@code getAllergies} → list of known allergies;</li>
     *   <li>REST {@code GET /patients/{id}/prescriptions} via Feign → active prescriptions.</li>
     * </ol>
     * The four results are assembled into a single {@link ClinicalProfile} object, which
     * is then returned to the caller. All SOAP calls share a single JAX-WS port instance
     * configured via {@code AnagrafeClientConfig}.
     *
     * @param patientId numeric patient identifier (must exist in the anagrafe database)
     * @return 200 with the assembled {@link ClinicalProfile}
     */
    @Operation(
        summary = "Get clinical profile for a patient (UC-2)",
        description = "Aggregates data from the SOAP Anagrafe service (patient demographics, " +
                      "medical history, allergies) and the REST Farmacia service (active prescriptions) " +
                      "into a single ClinicalProfile response. Used directly in UC-2 and internally " +
                      "by the Care Coordinator in UC-1."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "ClinicalProfile assembled and returned successfully"),
        @ApiResponse(responseCode = "500", description = "Error communicating with the Anagrafe SOAP service or the Farmacia REST service")
    })
    @GetMapping("/patients/{patientId}/profile")
    public ResponseEntity<ClinicalProfile> getClinicalProfile(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId) {
        return ResponseEntity.ok(service.buildProfile(patientId));
    }
}
