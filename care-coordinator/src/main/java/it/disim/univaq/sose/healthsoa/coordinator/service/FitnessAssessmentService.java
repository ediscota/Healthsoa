package it.disim.univaq.sose.healthsoa.coordinator.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.disim.univaq.sose.healthsoa.coordinator.client.ClinicalAggregatorClient;
import it.disim.univaq.sose.healthsoa.coordinator.client.DiagnosticAggregatorClient;
import it.disim.univaq.sose.healthsoa.coordinator.dto.ClinicalProfileDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.DiagnosticBundleDto;
import it.disim.univaq.sose.healthsoa.coordinator.dto.FitnessReport;
import it.disim.univaq.sose.healthsoa.coordinator.dto.RiskFlag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class FitnessAssessmentService {

    private final DiagnosticAggregatorClient diagnosticClient;
    private final ClinicalAggregatorClient clinicalClient;
    private final RiskAnalyzer riskAnalyzer;
    private final Executor executor;

    public FitnessAssessmentService(DiagnosticAggregatorClient diagnosticClient,
                                    ClinicalAggregatorClient clinicalClient,
                                    RiskAnalyzer riskAnalyzer,
                                    @Qualifier("coordinatorExecutor") Executor executor) {
        this.diagnosticClient = diagnosticClient;
        this.clinicalClient = clinicalClient;
        this.riskAnalyzer = riskAnalyzer;
        this.executor = executor;
    }

    /**
     * UC-1: valutazione di idoneità completa per un paziente.
     *
     * Le due chiamate agli aggregatori partono in parallelo su thread separati
     * dell'executor "coordinatorExecutor". CompletableFuture.allOf().join() è la
     * barriera di sincronizzazione: il thread corrente si blocca qui finché entrambe
     * le future non completano (o vanno in fallback per circuit breaker aperto).
     * Solo dopo la barriera viene eseguita la logica di analisi del rischio (§6).
     */
    public FitnessReport assess(String patientId) {
        CompletableFuture<DiagnosticBundleDto> diagFuture =
                CompletableFuture.supplyAsync(() -> getDiagnosticBundle(patientId), executor);

        CompletableFuture<ClinicalProfileDto> clinFuture =
                CompletableFuture.supplyAsync(() -> getClinicalProfile(patientId), executor);

        // Barriera di sincronizzazione: attende entrambe le future prima di procedere.
        CompletableFuture.allOf(diagFuture, clinFuture).join();

        DiagnosticBundleDto bundle = diagFuture.join();
        ClinicalProfileDto profile = clinFuture.join();

        List<RiskFlag> flags = riskAnalyzer.analyze(profile, bundle);
        String outcome = riskAnalyzer.computeOutcome(flags);

        return new FitnessReport(outcome, patientId, LocalDateTime.now(), profile, bundle, flags);
    }

    /**
     * Chiama il DiagnosticAggregator. Protetto da circuit breaker: se l'aggregatore
     * non risponde entro il timeout configurato o supera la soglia di fallimenti,
     * il circuito si apre e viene restituito un bundle null (report parziale).
     */
    @CircuitBreaker(name = "diagnosticAggregator", fallbackMethod = "fallbackDiagnosticBundle")
    public DiagnosticBundleDto getDiagnosticBundle(String patientId) {
        return diagnosticClient.getBundle(patientId);
    }

    /**
     * Chiama il ClinicalAggregator. Stesso pattern di circuit breaker.
     */
    @CircuitBreaker(name = "clinicalAggregator", fallbackMethod = "fallbackClinicalProfile")
    public ClinicalProfileDto getClinicalProfile(String patientId) {
        return clinicalClient.getProfile(patientId);
    }

    public DiagnosticBundleDto fallbackDiagnosticBundle(String patientId, Throwable ex) {
        return null;
    }

    public ClinicalProfileDto fallbackClinicalProfile(String patientId, Throwable ex) {
        return null;
    }
}
