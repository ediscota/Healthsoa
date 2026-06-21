package it.disim.univaq.sose.healthsoa.clinical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Clinical Aggregator (Prosumer 2).
 *
 * <p>Spring Boot application that bridges the SOAP Anagrafe Pazienti service
 * (Provider 1) and the REST Farmacia Service (Provider 3), exposing a unified
 * REST endpoint ({@code GET /patients/{patientId}/profile}) that returns a complete
 * {@code ClinicalProfile} (demographics + medical history + allergies + prescriptions).
 *
 * <p>Key annotations:
 * <ul>
 *   <li>{@code @EnableDiscoveryClient} — registers with Eureka as
 *       {@code clinical-aggregator} so the Gateway and Care Coordinator can
 *       resolve it via service discovery;</li>
 *   <li>{@code @EnableFeignClients} — activates the {@code FarmaciaClient}
 *       Feign proxy for calling the Farmacia REST service.</li>
 * </ul>
 *
 * <p>The Anagrafe SOAP client is a JAX-WS generated port, not a Feign client.
 * It is wired via {@code AnagrafeClientConfig} and refreshed at runtime via
 * {@code @RefreshScope}.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ClinicalAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalAggregatorApplication.class, args);
    }
}
