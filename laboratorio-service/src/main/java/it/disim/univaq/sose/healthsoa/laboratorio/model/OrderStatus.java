package it.disim.univaq.sose.healthsoa.laboratorio.model;

/**
 * Lifecycle states of a laboratory test order.
 *
 * <ul>
 *   <li>{@code PENDING} — order has been submitted but processing has not started yet.</li>
 *   <li>{@code PROCESSING} — the biological sample is being analysed (simulated by Thread.sleep).</li>
 *   <li>{@code COMPLETED} — analysis is complete and measurements are available.</li>
 *   <li>{@code ERROR} — processing was interrupted (e.g., thread interrupted); order is unusable.</li>
 * </ul>
 */
public enum OrderStatus {
    PENDING, PROCESSING, COMPLETED, ERROR
}
