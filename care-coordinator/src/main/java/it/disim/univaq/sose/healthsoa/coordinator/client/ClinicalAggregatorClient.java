package it.disim.univaq.sose.healthsoa.coordinator.client;

import it.disim.univaq.sose.healthsoa.coordinator.dto.ClinicalProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clinical-aggregator")
public interface ClinicalAggregatorClient {

    @GetMapping("/patients/{patientId}/profile")
    ClinicalProfileDto getProfile(@PathVariable("patientId") String patientId);
}
