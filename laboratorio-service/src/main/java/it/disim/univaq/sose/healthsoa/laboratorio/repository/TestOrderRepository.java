package it.disim.univaq.sose.healthsoa.laboratorio.repository;

import it.disim.univaq.sose.healthsoa.laboratorio.model.OrderStatus;
import it.disim.univaq.sose.healthsoa.laboratorio.model.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {

    Optional<TestOrder> findFirstByPatientIdAndExamCodeAndStatusOrderByCreatedAtDesc(
            String patientId, String examCode, OrderStatus status);
}
