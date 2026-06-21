package it.disim.univaq.sose.healthsoa.anagrafe.repository;

import it.disim.univaq.sose.healthsoa.anagrafe.model.ConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ConditionEntity}.
 *
 * <p>Used by {@code AnagrafeService.getMedicalHistory()} to load all ICD-10 coded
 * diagnoses and hospital admissions associated with a given patient.
 */
public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {

    /**
     * Returns all clinical conditions for a given patient.
     *
     * @param patientId database primary key of the patient
     * @return list of conditions (may be empty)
     */
    List<ConditionEntity> findByPatientId(Long patientId);

}
