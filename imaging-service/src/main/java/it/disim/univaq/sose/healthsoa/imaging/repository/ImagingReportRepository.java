package it.disim.univaq.sose.healthsoa.imaging.repository;

import it.disim.univaq.sose.healthsoa.imaging.model.ImagingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagingReportRepository extends JpaRepository<ImagingReport, Long> {

    List<ImagingReport> findByPatientId(String patientId);

    List<ImagingReport> findByPatientIdAndExamType(String patientId, String examType);
}
