package it.disim.univaq.sose.healthsoa.farmacia.repository;

import it.disim.univaq.sose.healthsoa.farmacia.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Prescription} entities.
 *
 * <p>Provides standard CRUD via {@link JpaRepository} and a derived query
 * for patient-scoped lookup. The {@code findByPatientId} method is the primary
 * read path: it is called by {@code PrescriptionService.getActivePrescriptions()}
 * which in turn is invoked by the Clinical Aggregator's {@code FarmaciaClient}.
 */
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Returns all prescriptions for a given patient.
     *
     * @param patientId numeric patient identifier (stored as string to match the
     *                  identifier format used by the other services)
     * @return list of prescriptions (may be empty if the patient has none)
     */
    List<Prescription> findByPatientId(String patientId);

}
