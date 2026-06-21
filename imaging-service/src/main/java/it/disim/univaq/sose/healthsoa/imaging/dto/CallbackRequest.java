package it.disim.univaq.sose.healthsoa.imaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for {@code POST /imaging/orders/{id}/callback}.
 *
 * <p>Registers a webhook URL on an existing imaging order. When the report reaches
 * COMPLETED status, the service will POST the full {@link ImagingReportDto} to the
 * given URL. Callback delivery is best-effort; failures are silently ignored and do
 * not affect the report's stored state.
 */
@Schema(description = "Request body for registering a webhook callback on an imaging order")
public class CallbackRequest {

    /**
     * HTTP URL that will receive the completed {@link ImagingReportDto} via POST.
     * Must be reachable from within the Docker network ({@code healthsoa-net}).
     */
    @Schema(description = "Webhook URL to receive the completed report", example = "http://diagnostic-aggregator:9201/callback/imaging")
    private String callbackUrl;

    public CallbackRequest() {}

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}
