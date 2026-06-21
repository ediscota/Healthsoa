package it.disim.univaq.sose.healthsoa.diagnostic.client;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderRequest;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabOrderResponse;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.LabStatusDto;
import it.disim.univaq.sose.healthsoa.diagnostic.dto.TestResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for the Laboratory Service (Provider 2).
 *
 * <p>Resolved via Eureka discovery under the name {@code laboratorio-service}.
 * Used by {@code DiagnosticService} to submit lab orders, poll their status,
 * and retrieve completed results. The {@code lb://} load balancer prefix in the
 * resolved URL means calls are automatically distributed across all running
 * instances of {@code laboratorio-service}.
 *
 * <p>Method mapping mirrors the endpoints defined in {@code LabController}:
 * <ul>
 *   <li>{@link #submitOrder} → {@code POST /tests/orders} (202 Accepted);</li>
 *   <li>{@link #getStatus} → {@code GET /tests/orders/{id}/status};</li>
 *   <li>{@link #getResult} → {@code GET /tests/orders/{id}/result}.</li>
 * </ul>
 */
@FeignClient(name = "laboratorio-service")
public interface LaboratorioClient {

    /**
     * Submits a new lab order. Returns immediately with 202 Accepted and the
     * assigned {@code orderId}.
     *
     * @param request patient and exam panel identifiers
     * @return response with {@code orderId} for subsequent polling
     */
    @PostMapping("/tests/orders")
    LabOrderResponse submitOrder(@RequestBody LabOrderRequest request);

    /**
     * Polls the current status of a lab order.
     *
     * @param orderId database-assigned order identifier
     * @return status DTO with current lifecycle state
     */
    @GetMapping("/tests/orders/{id}/status")
    LabStatusDto getStatus(@PathVariable("id") Long orderId);

    /**
     * Retrieves the full result of a COMPLETED lab order.
     * Throws a Feign exception if the order is not yet completed (409 Conflict).
     *
     * @param orderId database-assigned order identifier
     * @return full result with list of measurements
     */
    @GetMapping("/tests/orders/{id}/result")
    TestResultDto getResult(@PathVariable("id") Long orderId);
}
