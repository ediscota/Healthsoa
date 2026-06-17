package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class LabCallbackRequest {

    private String callbackUrl;

    public LabCallbackRequest() {}

    public LabCallbackRequest(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}
