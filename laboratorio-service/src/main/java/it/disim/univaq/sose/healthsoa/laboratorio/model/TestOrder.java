package it.disim.univaq.sose.healthsoa.laboratorio.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a laboratory test order.
 *
 * <p>Maps to the {@code test_order} table in the {@code laboratorio} MySQL schema.
 * Each order goes through the lifecycle defined by {@link OrderStatus}:
 * PENDING → PROCESSING → COMPLETED (or ERROR).
 *
 * <p>An order belongs to one patient and one exam panel. When the processing
 * completes, a list of {@link Measurement} entities is attached (one per parameter
 * in the panel). If a callback URL was registered, the service POSTs the result
 * to that URL immediately after transitioning to COMPLETED.
 *
 * <p>The {@code callbackUrl} is optional and may be set either at creation time or
 * later via {@code POST /tests/orders/{id}/callback}.
 */
@Entity
@Table(name = "test_order")
public class TestOrder {

    /** Auto-generated primary key; returned to the client as {@code orderId}. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifier of the patient for whom the order was placed. */
    @Column(name = "patient_id", nullable = false)
    private String patientId;

    /**
     * Code identifying the exam panel (e.g., {@code PANEL_RENAL}, {@code CBC}).
     * Determines which measurements are generated during processing.
     */
    @Column(name = "exam_code", nullable = false)
    private String examCode;

    /** Current processing state of the order. Persisted as a string. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Optional webhook URL. When set, the service POSTs the {@code TestResultDto}
     * here upon completion. Delivery is best-effort; failures are silently ignored.
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /** Timestamp when the order was first created. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last status transition. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Measurements produced by the analysis. Loaded lazily; only populated after
     * the order reaches COMPLETED status.
     */
    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Measurement> measurements = new ArrayList<>();

    public TestOrder() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Measurement> getMeasurements() { return measurements; }
    public void setMeasurements(List<Measurement> measurements) { this.measurements = measurements; }

}
