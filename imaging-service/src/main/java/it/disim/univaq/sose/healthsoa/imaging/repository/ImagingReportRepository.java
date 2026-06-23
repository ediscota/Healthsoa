package it.disim.univaq.sose.healthsoa.imaging.repository;

import it.disim.univaq.sose.healthsoa.imaging.model.ImagingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ImagingReport} entities.
 *
 * <p>Provides standard CRUD operations via {@link JpaRepository} and two
 * derived query methods used by the controller and Feign clients:
 * <ul>
 *   <li>{@link #findByPatientId} - used by the Diagnostic Aggregator to retrieve
 *       all archived reports for a patient when building the {@code DiagnosticBundle};</li>
 *   <li>{@link #findByPatientIdAndExamType} - filtered variant used when a specific
 *       exam panel is requested (e.g., all {@code PANEL_RENAL} imaging for patient 1).</li>
 * </ul>
 */
public interface ImagingReportRepository extends JpaRepository<ImagingReport, Long> {

    /**
     * Returns all imaging reports for a given patient, regardless of exam type or status.
     *
     * @param patientId numeric patient identifier
     * @return list of matching reports (may be empty)
     */
    List<ImagingReport> findByPatientId(String patientId);

    /**
     * Returns imaging reports for a given patient filtered by exam type.
     *
     * <p>Used by the Diagnostic Aggregator to retrieve only the reports relevant
     * to the diagnostic panel being assembled (e.g., {@code PANEL_RENAL} reports
     * when composing a renal diagnostic bundle).
     *
     * @param patientId numeric patient identifier
     * @param examType  imaging exam type code (e.g., "PANEL_RENAL", "RX_TORACE")
     * @return list of matching reports (may be empty)
     */
    List<ImagingReport> findByPatientIdAndExamType(String patientId, String examType);
}
