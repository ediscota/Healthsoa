package it.disim.univaq.sose.healthsoa.coordinator.client;

import it.disim.univaq.sose.healthsoa.coordinator.dto.DiagnosticBundleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "diagnostic-aggregator")
public interface DiagnosticAggregatorClient {

    /**
     * Endpoint sincrono del DiagnosticAggregator: ordina PANEL_RENAL, attende il
     * completamento del laboratorio tramite polling interno e restituisce il bundle.
     * La chiamata può durare 8-10s (elaborazione campione in lab).
     */
    @GetMapping("/patients/{patientId}/bundle")
    DiagnosticBundleDto getBundle(@PathVariable("patientId") String patientId);
}
