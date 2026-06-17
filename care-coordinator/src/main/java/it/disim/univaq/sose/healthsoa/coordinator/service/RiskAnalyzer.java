package it.disim.univaq.sose.healthsoa.coordinator.service;

import it.disim.univaq.sose.healthsoa.coordinator.dto.AllergyDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.ClinicalProfileDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.DiagnosticBundleDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.MeasurementDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.RiskFlag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RiskAnalyzer {

    /**
     * Farmaci nefrotossici noti: il loro uso in presenza di creatinina elevata
     * genera un RiskFlag CRITICAL (regola §6 controindicazione farmaco/funzionalità renale).
     */
    private static final Set<String> NEPHROTOXIC_DRUGS = Set.of(
            "amoxicillina", "gentamicina", "vancomicina", "ibuprofene",
            "naprossene", "diclofenac", "metformina", "ciclosporina"
    );

    /**
     * Cross-reattività allergica: sostanza allergizzante → farmaci cross-reattivi.
     * Usato per la regola §6 controindicazione farmaco/allergia.
     */
    private static final Map<String, Set<String>> CROSS_REACTIONS = Map.of(
            "penicillina", Set.of("amoxicillina", "ampicillina", "piperacillina"),
            "cefalosporine", Set.of("cefazolina", "ceftriaxone"),
            "aspirina", Set.of("acido acetilsalicilico", "asa", "ketoprofene")
    );

    /**
     * Applica le quattro regole di rilevamento del rischio (specifica §6):
     * 1. Controindicazione farmaco/funzionalità renale (creatinina fuori range + farmaco nefrotossico)
     * 2. Controindicazione farmaco/allergia (farmaco prescritto = sostanza allergizzante)
     * 3. Valore di laboratorio critico (anomalyFlag + distanza dal range)
     * 4. Nessun rischio → lista vuota
     */
    public List<RiskFlag> analyze(ClinicalProfileDto clinical, DiagnosticBundleDto diagnostic) {
        List<RiskFlag> flags = new ArrayList<>();

        if (clinical == null || diagnostic == null) {
            flags.add(new RiskFlag("ALTRO", "Dati parziali: uno o entrambi gli aggregatori non hanno risposto.",
                    "WARNING", "care-coordinator/circuit-breaker"));
            return flags;
        }

        List<PrescriptionDto> prescriptions = orEmpty(clinical.getActivePrescriptions());
        List<AllergyDto> allergies = orEmpty(clinical.getAllergies());
        List<MeasurementDto> measurements = diagnostic.getLabResult() != null
                ? orEmpty(diagnostic.getLabResult().getMeasurements()) : List.of();

        // Regola 1: controindicazione farmaco/funzionalità renale
        boolean creatininaElevata = measurements.stream()
                .anyMatch(m -> m.getParameter() != null
                        && m.getParameter().toLowerCase().contains("creatinina")
                        && m.isAnomalyFlag());

        if (creatininaElevata) {
            for (PrescriptionDto rx : prescriptions) {
                if (rx.getDrugName() != null
                        && NEPHROTOXIC_DRUGS.contains(rx.getDrugName().toLowerCase())) {
                    flags.add(new RiskFlag(
                            "CONTROINDICAZIONE_FARMACO",
                            "Farmaco nefrotossico '" + rx.getDrugName()
                                    + "' in corso con creatinina sierica elevata.",
                            "CRITICAL",
                            "Prescrizione: " + rx.getDrugName() + " | Lab: Creatinina anomala"
                    ));
                }
            }
        }

        // Regola 2: controindicazione farmaco/allergia
        for (PrescriptionDto rx : prescriptions) {
            if (rx.getDrugName() == null) continue;
            String drugLower = rx.getDrugName().toLowerCase();

            for (AllergyDto allergy : allergies) {
                if (allergy.getAllergen() == null) continue;
                String allergenLower = allergy.getAllergen().toLowerCase();

                boolean directMatch = drugLower.contains(allergenLower) || allergenLower.contains(drugLower);
                boolean crossMatch = CROSS_REACTIONS.getOrDefault(allergenLower, Set.of()).contains(drugLower);

                if (directMatch || crossMatch) {
                    flags.add(new RiskFlag(
                            "ALLERGIA_RILEVANTE",
                            "Il farmaco '" + rx.getDrugName()
                                    + "' è controindicato: paziente allergico a '"
                                    + allergy.getAllergen() + "' (severità: " + allergy.getSeverity() + ").",
                            "CRITICAL",
                            "Prescrizione: " + rx.getDrugName() + " | Allergia: " + allergy.getAllergen()
                    ));
                }
            }
        }

        // Regola 3: valore di laboratorio critico
        for (MeasurementDto m : measurements) {
            if (!m.isAnomalyFlag() || m.getValue() == null || m.getReferenceRange() == null) continue;

            double[] bounds = parseRange(m.getReferenceRange());
            if (bounds == null) {
                // Range non parsabile: segnala come WARNING generico
                flags.add(new RiskFlag(
                        "VALORE_CRITICO",
                        "Parametro '" + m.getParameter() + "' fuori range: "
                                + m.getValue() + " " + m.getUnit() + " [ref: " + m.getReferenceRange() + "].",
                        "WARNING",
                        "Lab: " + m.getParameter()
                ));
                continue;
            }

            double low = bounds[0];
            double high = bounds[1];
            double val = m.getValue();

            // Oltre il doppio del range superiore o sotto la metà del range inferiore → CRITICAL
            boolean critical = (val > high && val > 2 * high) || (val < low && val < low / 2.0);
            String severity = critical ? "CRITICAL" : "WARNING";

            flags.add(new RiskFlag(
                    "VALORE_CRITICO",
                    "Parametro '" + m.getParameter() + "' fuori range: "
                            + m.getValue() + " " + m.getUnit() + " [ref: " + m.getReferenceRange() + "].",
                    severity,
                    "Lab: " + m.getParameter()
            ));
        }

        return flags;
    }

    /** Ricava l'esito sintetico dalla lista di flag. */
    public String computeOutcome(List<RiskFlag> flags) {
        if (flags.isEmpty()) return "IDONEO";
        boolean hasCritical = flags.stream().anyMatch(f -> "CRITICAL".equals(f.getSeverity()));
        return hasCritical ? "NON_IDONEO" : "CON_RISERVA";
    }

    /** Parsa un range "low-high" in double[]{low, high}. Ritorna null se non parsabile. */
    private double[] parseRange(String range) {
        try {
            String[] parts = range.split("-");
            if (parts.length == 2) {
                return new double[]{Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim())};
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    private <T> List<T> orEmpty(List<T> list) {
        return list != null ? list : List.of();
    }
}
