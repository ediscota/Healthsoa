package it.disim.univaq.sose.healthsoa.anagrafe.model;

/**
 * Clinical severity of an allergic reaction.
 *
 * <ul>
 *   <li>{@code MILD} — minor symptoms (e.g., skin rash, mild itching);</li>
 *   <li>{@code SEVERE} — significant systemic reaction requiring medical attention;</li>
 *   <li>{@code ANAPHYLACTIC} — life-threatening anaphylactic shock; triggers a
 *       CRITICAL risk flag in {@code RiskAnalyzer} when a conflicting prescription
 *       is detected.</li>
 * </ul>
 *
 * <p>Exposed as a string via the SOAP {@code getAllergies} operation.
 */
public enum ReactionSeverityEnum {
    MILD, SEVERE, ANAPHYLACTIC
}
