package it.disim.univaq.sose.healthsoa.farmacia.controller;

import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.farmacia.service.PrescriptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public List<PrescriptionDto> getPrescriptions(@PathVariable String patientId) {
        return prescriptionService.getActivePrescriptions(patientId);
    }

}
