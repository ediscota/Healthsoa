package it.disim.univaq.sose.healthsoa.farmacia.service;

import it.disim.univaq.sose.healthsoa.farmacia.dto.CreatePrescriptionRequest;
import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.farmacia.model.Prescription;
import it.disim.univaq.sose.healthsoa.farmacia.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Persiste una nuova prescrizione per il paziente indicato e restituisce
     * il DTO con l'identificativo assegnato dal database.
     */
    @Transactional
    public PrescriptionDto createPrescription(String patientId, CreatePrescriptionRequest request) {
        Prescription p = new Prescription();
        p.setPatientId(patientId);
        p.setDrugName(request.getDrugName());
        p.setAtcCode(request.getAtcCode());
        p.setDosage(request.getDosage());
        p.setFrequency(request.getFrequency());
        p.setStartDate(request.getStartDate());
        p.setExpectedEndDate(request.getExpectedEndDate());
        p.setPrescribingDoctor(request.getPrescribingDoctor());
        return toDto(prescriptionRepository.save(p));
    }

    private PrescriptionDto toDto(Prescription p) {
        return new PrescriptionDto(
                p.getId(),
                p.getPatientId(),
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
