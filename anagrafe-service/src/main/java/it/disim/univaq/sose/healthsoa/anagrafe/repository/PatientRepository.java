package it.disim.univaq.sose.healthsoa.anagrafe.repository;

import it.disim.univaq.sose.healthsoa.anagrafe.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    Optional<PatientEntity> findByFiscalCode(String fiscalCode);

}
