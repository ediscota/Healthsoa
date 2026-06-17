package it.disim.univaq.sose.healthsoa.farmacia.repository;

import it.disim.univaq.sose.healthsoa.farmacia.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientId(String patientId);

}
