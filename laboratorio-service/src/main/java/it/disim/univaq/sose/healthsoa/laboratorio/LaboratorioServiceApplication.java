package it.disim.univaq.sose.healthsoa.laboratorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Laboratory Analysis Service (Provider 2).
 *
 * <p>Spring Boot application that simulates a LIS (Laboratory Information System).
 * Implements the <em>Asynchronous Request-Reply</em> pattern: orders are accepted
 * immediately with 202 Accepted and processed in background via {@code @Async}.
 *
 * <p>Key annotations:
 * <ul>
 *   <li>{@code @EnableDiscoveryClient} - registers with Eureka so that Feign clients
 *       in the Diagnostic Aggregator can resolve {@code laboratorio-service} by name;</li>
 *   <li>{@code @EnableAsync} - activates Spring's async processing infrastructure,
 *       enabling {@code @Async("labExecutor")} in {@code LabService}.</li>
 * </ul>
 *
 * <p>Configuration (port, datasource, Eureka URL, processing delay) is read from
 * {@code laboratorio-service-dev.properties} in the Config Server repository.
 * This service can be scaled to multiple instances via
 * {@code docker compose up --scale laboratorio-service=N}.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class LaboratorioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaboratorioServiceApplication.class, args);
    }

}
