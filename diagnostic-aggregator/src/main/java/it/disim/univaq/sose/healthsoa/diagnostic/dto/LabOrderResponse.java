package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class LabOrderResponse {

    private Long orderId;
    private String status;
    private String message;

    public LabOrderResponse() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
