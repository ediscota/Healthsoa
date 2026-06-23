package it.disim.univaq.sose.healthsoa.coordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Care Coordinator (Prosumer 3 - Orchestrator).
 *
 * <p>Spring Boot application that orchestrates the full UC-1 fitness assessment.
 * On each request it fires two parallel {@code CompletableFuture} calls - one to
 * the Clinical Aggregator for the patient's clinical profile, one to the Diagnostic
 * Aggregator for the diagnostic bundle - joins them at a synchronisation barrier,
 * runs the {@code RiskAnalyzer}, and returns the {@code FitnessReport}.
 *
 * <p>Both remote calls are protected by Resilience4j {@code @CircuitBreaker};
 * a fallback method returns an empty DTO on failure so the partial data still
 * produces a usable (degraded) report.
 *
 * <p>Key annotations:
 * <ul>
 *   <li>{@code @EnableDiscoveryClient} - registers with Eureka as {@code care-coordinator}
 *       so the Gateway can route UC-1 and UC-4 requests to it;</li>
 *   <li>{@code @EnableFeignClients} - activates proxies for {@code ClinicalAggregatorClient}
 *       and {@code DiagnosticAggregatorClient}.</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CareCoordinatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareCoordinatorApplication.class, args);
    }
}
