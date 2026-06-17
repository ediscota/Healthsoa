package it.disim.univaq.sose.healthsoa.coordinator.controller;

import it.disim.univaq.sose.healthsoa.coordinator.dto.FitnessReport;
import it.disim.univaq.sose.healthsoa.coordinator.service.FitnessAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CareCoordinatorController {

    private final FitnessAssessmentService assessmentService;

    public CareCoordinatorController(FitnessAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /**
     * UC-1: valutazione di idoneità completa per un paziente.
     * Invoca in parallelo DiagnosticAggregator e ClinicalAggregator,
     * attende la barriera di sincronizzazione, esegue la logica di rischio (§6)
     * e restituisce un FitnessReport con esito (IDONEO/CON_RISERVA/NON_IDONEO)
     * e lista dei RiskFlag motivati.
     */
    @GetMapping("/patients/{patientId}/fitness-assessment")
    public ResponseEntity<FitnessReport> assess(@PathVariable String patientId) {
        return ResponseEntity.ok(assessmentService.assess(patientId));
    }
}
