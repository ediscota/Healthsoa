package it.disim.univaq.sose.healthsoa.laboratorio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body for {@code POST /tests/orders} (202 Accepted).
 *
 * <p>Returned immediately after an order is accepted. The {@code orderId} must be
 * stored by the client to poll status ({@code GET /tests/orders/{orderId}/status})
 * or register a callback webhook ({@code POST /tests/orders/{orderId}/callback}).
 */
@Schema(description = "Response returned when a laboratory order is accepted (202 Accepted)")
public class TestOrderResponse {

    /** Database-assigned identifier for the newly created order. */
    @Schema(description = "Unique identifier of the created order", example = "42")
    private Long orderId;

    /** Initial status of the order, always PENDING at submission time. */
    @Schema(description = "Initial processing status", example = "PENDING")
    private String status;

    /** Human-readable instructions for the client on how to track the order. */
    @Schema(description = "Informational message with polling instructions")
    private String message;

    public TestOrderResponse(Long orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public Long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }

}
