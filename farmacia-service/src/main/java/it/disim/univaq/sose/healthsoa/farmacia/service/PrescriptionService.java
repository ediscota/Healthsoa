package it.disim.univaq.sose.healthsoa.farmacia.service;

import it.disim.univaq.sose.healthsoa.farmacia.dto.CreatePrescriptionRequest;
import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.farmacia.model.Prescription;
import it.disim.univaq.sose.healthsoa.farmacia.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic layer for the Farmacia Service (Provider 3).
 *
 * <p>Implements two operations:
 * <ul>
 *   <li>{@link #getActivePrescriptions} — lists all prescriptions for a patient,
 *       consumed by the Clinical Aggregator via Feign;</li>
 *   <li>{@link #createPrescription} — persists a new prescription issued via UC-4
 *       (the clinical workstation calling the Gateway directly).</li>
 * </ul>
 *
 * <p>The private {@code toDto} method converts JPA entities to DTOs, keeping
 * the persistence model isolated from the REST API contract.
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Returns all prescriptions for a patient as DTOs.
     *
     * @param patientId numeric patient identifier
     * @return list of prescription DTOs (may be empty)
     */
    public List<PrescriptionDto> getActivePrescriptions(String patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Persists a new prescription for the given patient and returns the saved DTO
     * with the database-assigned identifier.
     *
     * @param patientId numeric patient identifier from the URL path variable
     * @param request   validated request body with drug and dosage details
     * @return DTO of the newly created prescription
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
