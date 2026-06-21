package it.disim.univaq.sose.healthsoa.diagnostic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Diagnostic Aggregator (Prosumer 1).
 *
 * <p>Spring Boot application that acts as both service consumer (calls the
 * Laboratory Service and the Imaging Service via Feign) and service provider
 * (exposes a REST API for UC-3 tracking and the UC-1 synchronous bundle).
 *
 * <p>Key design decisions:
 * <ul>
 *   <li>Stateless tracking: the {@code trackingId} token encodes all information
 *       needed to poll the Laboratory Service directly, so this aggregator can be
 *       scaled horizontally without shared in-memory state or an external cache.</li>
 *   <li>Single-instance for in-memory state: if future requirements introduce
 *       in-memory order tracking, scaling would require sticky sessions or an
 *       external store (specification §5.1 and §12).</li>
 * </ul>
 *
 * <p>Key annotations:
 * <ul>
 *   <li>{@code @EnableDiscoveryClient} — registers with Eureka as {@code diagnostic-aggregator};</li>
 *   <li>{@code @EnableFeignClients} — activates Feign client proxies for
 *       {@code LaboratorioClient} and {@code ImagingClient}.</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class DiagnosticAggregatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiagnosticAggregatorApplication.class, args);
    }
}
