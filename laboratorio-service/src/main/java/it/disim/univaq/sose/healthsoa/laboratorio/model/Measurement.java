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

@Entity
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private TestOrder testOrder;

    /** Nome del parametro misurato (es. "Creatinina", "Glicemia"). */
    @Column(nullable = false)
    private String parameter;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String unit;

    /** Range di riferimento in forma testuale (es. "0.6-1.2"). */
    @Column(name = "reference_range", nullable = false)
    private String referenceRange;

    /** True se il valore è fuori dal range di riferimento. */
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
