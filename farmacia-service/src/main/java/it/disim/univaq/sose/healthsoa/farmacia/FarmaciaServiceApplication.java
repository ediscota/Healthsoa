package it.disim.univaq.sose.healthsoa.farmacia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the Farmacia Service (Provider 3 — REST).
 *
 * <p>Spring Boot application that manages pharmaceutical prescriptions.
 * Exposes a REST API at port {@code 9103} for two consumers:
 * <ul>
 *   <li>The Clinical Aggregator (Prosumer 2) — reads active prescriptions via Feign
 *       to build the {@code ClinicalProfile} for UC-1 and UC-2;</li>
 *   <li>The clinical workstation web client — creates new prescriptions via the Gateway
 *       (UC-4).</li>
 * </ul>
 *
 * <p>{@code @EnableDiscoveryClient} registers this service with Eureka as
 * {@code farmacia-service} so the Gateway and Clinical Aggregator can resolve
 * it dynamically.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FarmaciaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmaciaServiceApplication.class, args);
    }

}
