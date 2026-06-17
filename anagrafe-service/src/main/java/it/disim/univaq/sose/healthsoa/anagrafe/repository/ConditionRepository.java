package it.disim.univaq.sose.healthsoa.anagrafe.repository;

import it.disim.univaq.sose.healthsoa.anagrafe.model.ConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConditionRepository extends JpaRepository<ConditionEntity, Long> {

    List<ConditionEntity> findByPatientId(Long patientId);

}
