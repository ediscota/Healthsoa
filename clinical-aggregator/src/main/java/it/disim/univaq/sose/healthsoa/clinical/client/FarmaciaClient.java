package it.disim.univaq.sose.healthsoa.clinical.client;

import it.disim.univaq.sose.healthsoa.clinical.dto.PrescriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "farmacia-service")
public interface FarmaciaClient {

    @GetMapping("/patients/{patientId}/prescriptions")
    List<PrescriptionDto> getPrescriptions(@PathVariable("patientId") String patientId);
}
