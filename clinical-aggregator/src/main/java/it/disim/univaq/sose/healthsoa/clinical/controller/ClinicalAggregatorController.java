package it.disim.univaq.sose.healthsoa.clinical.controller;

import it.disim.univaq.sose.healthsoa.clinical.dto.ClinicalProfile;
import it.disim.univaq.sose.healthsoa.clinical.service.ClinicalAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClinicalAggregatorController {

    private final ClinicalAggregatorService service;

    public ClinicalAggregatorController(ClinicalAggregatorService service) {
        this.service = service;
    }

    @GetMapping("/patients/{patientId}/profile")
    public ResponseEntity<ClinicalProfile> getClinicalProfile(@PathVariable String patientId) {
        return ResponseEntity.ok(service.buildProfile(patientId));
    }
}
