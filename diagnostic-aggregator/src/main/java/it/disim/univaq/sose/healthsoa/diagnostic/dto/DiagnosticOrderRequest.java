package it.disim.univaq.sose.healthsoa.diagnostic.dto;

public class DiagnosticOrderRequest {

    private String panelCode;

    public DiagnosticOrderRequest() {}

    public String getPanelCode() { return panelCode; }
    public void setPanelCode(String panelCode) { this.panelCode = panelCode; }
}
