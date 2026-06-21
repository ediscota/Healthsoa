package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for {@code POST /tests/orders/{id}/callback}.
 *
 * <p>Registers a webhook URL on an existing laboratory order. When the order
 * reaches COMPLETED status, the service will POST the full {@code TestResultDto}
 * to this URL. The URL may point to any HTTP endpoint reachable from the
 * laboratory service container, including the Diagnostic Aggregator's callback
 * receiver (used in the UC-3 callback flow described in the project specification §3.2).
 */
@Schema(description = "Request body for registering a webhook callback on a laboratory order")
public class CallbackRequest {

    /**
     * HTTP URL to which the service will POST the completed {@code TestResultDto}.
     * Must be reachable from within the Docker network ({@code healthsoa-net}).
     */
    @Schema(description = "Webhook URL to receive the completed result", example = "http://diagnostic-aggregator:9201/callback/lab")
    private String callbackUrl;

    public CallbackRequest() {
    }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

}
