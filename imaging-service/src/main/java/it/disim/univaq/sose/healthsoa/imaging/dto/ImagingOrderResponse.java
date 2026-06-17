package it.disim.univaq.sose.healthsoa.imaging.dto;

public class ImagingOrderResponse {

    private Long reportId;
    private String status;
    private String message;

    public ImagingOrderResponse(Long reportId, String status, String message) {
        this.reportId = reportId;
        this.status = status;
        this.message = message;
    }

    public Long getReportId() { return reportId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
