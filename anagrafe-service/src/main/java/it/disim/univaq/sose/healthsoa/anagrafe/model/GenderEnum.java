package it.disim.univaq.sose.healthsoa.anagrafe.model;

/**
 * Biological sex of a patient, stored as a string column in the {@code patient} table.
 *
 * <ul>
 *   <li>{@code M} — male;</li>
 *   <li>{@code F} — female;</li>
 *   <li>{@code OTHER} — non-binary or unspecified.</li>
 * </ul>
 */
public enum GenderEnum {
    M, F, OTHER
}
