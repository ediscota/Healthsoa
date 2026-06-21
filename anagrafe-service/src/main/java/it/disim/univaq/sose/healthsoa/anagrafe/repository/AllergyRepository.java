package it.disim.univaq.sose.healthsoa.anagrafe.repository;

import it.disim.univaq.sose.healthsoa.anagrafe.model.AllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link AllergyEntity}.
 *
 * <p>Used by {@code AnagrafeService.getAllergies()} to load all known allergic
 * reactions for a given patient. Results are consumed by the Clinical Aggregator
 * and ultimately by the Care Coordinator's {@code RiskAnalyzer}.
 */
public interface AllergyRepository extends JpaRepository<AllergyEntity, Long> {

    /**
     * Returns all known allergies for a given patient.
     *
     * @param patientId database primary key of the patient
     * @return list of allergy entities (may be empty)
     */
    List<AllergyEntity> findByPatientId(Long patientId);

}
