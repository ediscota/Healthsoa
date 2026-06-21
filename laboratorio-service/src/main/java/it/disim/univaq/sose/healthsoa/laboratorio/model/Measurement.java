package it.disim.univaq.sose.healthsoa.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a single measurement within a completed laboratory test order.
 *
 * <p>Maps to the {@code measurement} table in the {@code laboratorio} MySQL schema.
 * Each {@link TestOrder} produces one or more measurements, one per parameter in the
 * exam panel (e.g., Creatinine, Haemoglobin, Glucose).
 *
 * <p>The {@code anomalyFlag} is set to {@code true} when the measured value falls
 * outside the {@code referenceRange}. This flag is consumed by the Care Coordinator's
 * {@code RiskAnalyzer} to detect critical lab values (specification §6, rule 3).
 */
@Entity
@Table(name = "measurement")
public class Measurement {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The test order to which this measurement belongs. Loaded lazily. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TestOrder testOrder;

    /** Human-readable name of the measured parameter (e.g., "Creatinina", "Glicemia"). */
    @Column(nullable = false)
    private String parameter;

    /** Numeric value of the measurement. */
    @Column(nullable = false)
    private Double value;

    /** Unit of measure (e.g., "mg/dL", "g/dL", "mEq/L"). */
    @Column(nullable = false)
    private String unit;

    /**
     * Normal reference range in textual format (e.g., "0.6-1.2").
     * Parsed by {@code RiskAnalyzer.parseRange()} to compute severity thresholds.
     */
    @Column(name = "reference_range", nullable = false)
    private String referenceRange;

    /**
     * {@code true} if {@code value} is outside {@code referenceRange}.
     * Consumed by the Care Coordinator to trigger risk flag generation.
     */
    @Column(name = "anomaly_flag", nullable = false)
    private boolean anomalyFlag;

    public Measurement() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TestOrder getTestOrder() { return testOrder; }
    public void setTestOrder(TestOrder testOrder) { this.testOrder = testOrder; }

    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }

    public boolean isAnomalyFlag() { return anomalyFlag; }
    public void setAnomalyFlag(boolean anomalyFlag) { this.anomalyFlag = anomalyFlag; }

}
