package it.disim.univaq.sose.healthsoa.imaging.model;

/**
 * Lifecycle states of an imaging order / radiology report.
 *
 * <ul>
 *   <li>{@code PENDING} - order accepted but radiologist simulation has not started.</li>
 *   <li>{@code PROCESSING} - the radiologist's reading is being simulated (Thread.sleep).</li>
 *   <li>{@code COMPLETED} - findings and conclusion are available; {@code criticalFlag} is set.</li>
 *   <li>{@code ERROR} - processing failed due to an interrupted thread; report is unusable.</li>
 * </ul>
 */
public enum ImagingOrderStatus {
    PENDING, PROCESSING, COMPLETED, ERROR
}
