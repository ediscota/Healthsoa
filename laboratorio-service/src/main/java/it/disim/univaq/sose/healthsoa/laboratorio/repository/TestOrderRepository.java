package it.disim.univaq.sose.healthsoa.laboratorio.repository;

import it.disim.univaq.sose.healthsoa.laboratorio.model.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
}
