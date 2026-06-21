package it.disim.univaq.sose.healthsoa.laboratorio.repository;

import it.disim.univaq.sose.healthsoa.laboratorio.model.OrderStatus;
import it.disim.univaq.sose.healthsoa.laboratorio.model.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TestOrder} entities.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD operations
 * ({@code findById}, {@code save}, {@code findAll}, etc.).
 *
 * <p>The custom query method is used by {@code LabService.buildMeasurements()} to
 * locate the most recent completed order for the same patient and panel, so that the
 * same measurement values are reused for subsequent orders on the same patient
 * (simulating realistic, reproducible lab data for demonstration purposes).
 */
public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {

    /**
     * Finds the most recent completed order for a given patient and exam panel.
     *
     * <p>Used to copy realistic measurement values from a previous order when a new
     * order is processed for the same patient and panel code. Returns empty if no
     * previous completed order exists (first order for this patient/panel combination).
     *
     * @param patientId numeric patient identifier
     * @param examCode  exam panel code (e.g., "PANEL_RENAL", "CBC")
     * @param status    must be {@link OrderStatus#COMPLETED}
     * @return the most recent matching order, or empty
     */
    Optional<TestOrder> findFirstByPatientIdAndExamCodeAndStatusOrderByCreatedAtDesc(
            String patientId, String examCode, OrderStatus status);
}
