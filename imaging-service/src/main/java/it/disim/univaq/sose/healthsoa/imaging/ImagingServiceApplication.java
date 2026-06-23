package it.disim.univaq.sose.healthsoa.imaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Diagnostic Imaging Service (Provider 4).
 *
 * <p>Spring Boot application that simulates a RIS/PACS (Radiology Information
 * System / Picture Archiving and Communication System). Pre-existing reports are
 * seeded from {@code data.sql} at startup. New reports are created asynchronously
 * via {@code POST /imaging/orders} and processed on the {@code imagingExecutor} pool.
 *
 * <p>Key annotations:
 * <ul>
 *   <li>{@code @EnableDiscoveryClient} - registers with Eureka under the name
 *       {@code imaging-service} so that Feign clients in the Diagnostic Aggregator
 *       can resolve it dynamically;</li>
 *   <li>{@code @EnableAsync} - activates Spring's async infrastructure, enabling
 *       {@code @Async("imagingExecutor")} in {@code ImagingService}.</li>
 * </ul>
 *
 * <p>Configuration (port, datasource, Eureka URL, processing delay) is read from
 * {@code imaging-service-dev.properties} in the Config Server repository.
 * This service can be scaled via {@code docker compose up --scale imaging-service=N}.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class ImagingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImagingServiceApplication.class, args);
    }
}
