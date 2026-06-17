package it.disim.univaq.sose.healthsoa.farmacia.service;

import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.farmacia.model.Prescription;
import it.disim.univaq.sose.healthsoa.farmacia.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<PrescriptionDto> getActivePrescriptions(String patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private PrescriptionDto toDto(Prescription p) {
        return new PrescriptionDto(
                p.getDrugName(),
                p.getAtcCode(),
                p.getDosage(),
                p.getFrequency(),
                p.getStartDate(),
                p.getExpectedEndDate(),
                p.getPrescribingDoctor()
        );
    }

}
