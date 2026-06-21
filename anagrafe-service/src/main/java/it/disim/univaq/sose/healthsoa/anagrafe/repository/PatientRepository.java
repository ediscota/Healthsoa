package it.disim.univaq.sose.healthsoa.anagrafe.repository;

import it.disim.univaq.sose.healthsoa.anagrafe.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link PatientEntity}.
 *
 * <p>Provides standard CRUD via {@link JpaRepository} plus a derived query
 * method for fiscal-code lookup, used by the SOAP {@code getPatientByFiscalCode}
 * and {@code getPatientById} operations in {@code AnagrafeService}.
 */
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    /**
     * Finds a patient by their unique Italian fiscal code.
     *
     * @param fiscalCode 16-character alphanumeric fiscal code
     * @return an {@code Optional} containing the patient if found, or empty
     */
    Optional<PatientEntity> findByFiscalCode(String fiscalCode);

}
