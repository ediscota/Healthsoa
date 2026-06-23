package it.disim.univaq.sose.healthsoa.anagrafe.model;

/**
 * Lifecycle status of a clinical condition in a patient's medical history.
 *
 * <ul>
 *   <li>{@code ACTIVE} - the condition is ongoing and currently affecting the patient;</li>
 *   <li>{@code RESOLVED} - the condition has been treated and is no longer active.</li>
 * </ul>
 *
 * <p>Exposed as a string via the SOAP {@code getMedicalHistory} operation.
 */
public enum ConditionStatusEnum {
    ACTIVE, RESOLVED
}
