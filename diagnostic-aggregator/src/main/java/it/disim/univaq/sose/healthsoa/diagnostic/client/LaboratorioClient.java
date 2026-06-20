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

@FeignClient(name = "laboratorio-service")
public interface LaboratorioClient {

    @PostMapping("/tests/orders")
    LabOrderResponse submitOrder(@RequestBody LabOrderRequest request);

    @GetMapping("/tests/orders/{id}/status")
    LabStatusDto getStatus(@PathVariable("id") Long orderId);

    @GetMapping("/tests/orders/{id}/result")
    TestResultDto getResult(@PathVariable("id") Long orderId);
}
