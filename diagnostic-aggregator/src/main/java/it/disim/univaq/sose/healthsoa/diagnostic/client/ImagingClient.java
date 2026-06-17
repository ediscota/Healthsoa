package it.disim.univaq.sose.healthsoa.diagnostic.client;

import it.disim.univaq.sose.healthsoa.diagnostic.dto.ImagingReportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "imaging-service")
public interface ImagingClient {

    @GetMapping("/patients/{patientId}/reports")
    List<ImagingReportDto> getReports(@PathVariable("patientId") String patientId);
}
