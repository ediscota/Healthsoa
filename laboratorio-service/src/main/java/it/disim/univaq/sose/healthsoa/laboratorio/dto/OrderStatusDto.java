package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import java.time.LocalDateTime;

public class OrderStatusDto {

    private Long orderId;
    private String patientId;
    private String examCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderStatusDto(Long orderId, String patientId, String examCode,
                          String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.patientId = patientId;
        this.examCode = examCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getOrderId() { return orderId; }
    public String getPatientId() { return patientId; }
    public String getExamCode() { return examCode; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}
