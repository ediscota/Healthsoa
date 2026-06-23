package it.disim.univaq.sose.healthsoa.coordinator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.disim.univaq.sose.healthsoa.coordinator.dto.FitnessReport;
import it.disim.univaq.sose.healthsoa.coordinator.service.FitnessAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Care Coordinator (Prosumer 3).
 *
 * <p>This is the top-level orchestration prosumer responsible for UC-1
 * (full clinical fitness assessment). It exposes a single endpoint that:
 * <ol>
 *   <li>Invokes the Diagnostic Aggregator and the Clinical Aggregator
 *       <strong>in parallel</strong> using {@code CompletableFuture.supplyAsync()}
 *       on a dedicated {@code coordinatorExecutor} thread pool;</li>
 *   <li>Waits at the synchronization barrier ({@code CompletableFuture.allOf().join()})
 *       until both futures complete;</li>
 *   <li>Runs the risk analysis logic ({@code RiskAnalyzer}) that cross-references
 *       lab values, prescriptions, and allergies according to the rules defined in
 *       the application specification §6;</li>
 *   <li>Returns a {@link FitnessReport} with outcome (IDONEO / CON_RISERVA / NON_IDONEO)
 *       and the full list of {@code RiskFlag} objects, each with severity and source.</li>
 * </ol>
 *
 * <p>Both downstream calls are protected by Resilience4j circuit breakers. If an
 * aggregator is unavailable, the circuit opens and a null bundle/profile is returned
 * (partial report). The risk analyzer handles this case gracefully by emitting a
 * WARNING flag.
 *
 * <p>Accessible through the API Gateway at {@code /api/coordinator/**}.
 */
@RestController
@Tag(name = "Care Coordinator", description = "Prosumer 3 - parallel orchestration of Diagnostic Aggregator and Clinical Aggregator, risk analysis, and fitness report generation (UC-1)")
public class CareCoordinatorController {

    private final FitnessAssessmentService assessmentService;

    public CareCoordinatorController(FitnessAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /**
     * Performs a full clinical fitness assessment for a patient (UC-1).
     *
     * <p>The coordinator launches two parallel calls:
     * <ul>
     *   <li>{@code GET /patients/{patientId}/bundle} on the Diagnostic Aggregator -
     *       submits a PANEL_RENAL lab order and waits for the result (8-10 s);</li>
     *   <li>{@code GET /patients/{patientId}/profile} on the Clinical Aggregator -
     *       retrieves anagrafe, medical history, allergies, and prescriptions.</li>
     * </ul>
     * Both calls run concurrently on the {@code coordinatorExecutor} pool; the response
     * to the client is only sent after both futures complete (synchronization barrier).
     *
     * <p>The resulting {@link FitnessReport} contains:
     * <ul>
     *   <li>{@code outcome}: IDONEO, CON_RISERVA, or NON_IDONEO;</li>
     *   <li>{@code riskFlags}: list of detected risks with type, severity, and source reference;</li>
     *   <li>the full {@code ClinicalProfileDto} and {@code DiagnosticBundleDto} embedded.</li>
     * </ul>
     *
     * @param patientId numeric patient identifier
     * @return 200 with a complete {@link FitnessReport}
     */
    @Operation(
        summary = "Full clinical fitness assessment (UC-1)",
        description = "Runs a parallel fitness assessment: invokes DiagnosticAggregator and ClinicalAggregator " +
                      "concurrently, waits for both (synchronization barrier), applies risk analysis rules (spec §6), " +
                      "and returns a FitnessReport with outcome and risk flags. " +
                      "Total response time is dominated by laboratory processing (~8-10 s)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "FitnessReport generated and returned successfully"),
        @ApiResponse(responseCode = "500", description = "Unexpected error during parallel aggregation or risk analysis")
    })
    @GetMapping("/patients/{patientId}/fitness-assessment")
    public ResponseEntity<FitnessReport> assess(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId) {
        return ResponseEntity.ok(assessmentService.assess(patientId));
    }
}
