package it.disim.univaq.sose.healthsoa.coordinator.client;

import it.disim.univaq.sose.healthsoa.coordinator.dto.ClinicalProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for the Clinical Aggregator (Prosumer 2).
 *
 * <p>Resolved via Eureka discovery under the name {@code clinical-aggregator}.
 * Called by {@code FitnessAssessmentService} inside a {@code CompletableFuture.supplyAsync()}
 * to retrieve the patient's clinical profile in parallel with the diagnostic bundle fetch.
 *
 * <p>Circuit breaker protection is applied at the call site in
 * {@code FitnessAssessmentService} via Resilience4j {@code @CircuitBreaker}.
 */
@FeignClient(name = "clinical-aggregator")
public interface ClinicalAggregatorClient {

    /**
     * Retrieves the complete clinical profile (demographics + medical history +
     * allergies + active prescriptions) for a given patient.
     *
     * @param patientId numeric patient identifier
     * @return aggregated clinical profile
     */
    @GetMapping("/patients/{patientId}/profile")
    ClinicalProfileDto getProfile(@PathVariable("patientId") String patientId);
}
