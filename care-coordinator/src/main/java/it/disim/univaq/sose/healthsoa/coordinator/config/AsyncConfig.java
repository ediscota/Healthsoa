package it.disim.univaq.sose.healthsoa.coordinator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    /**
     * Executor usato da CompletableFuture.supplyAsync() nel FitnessAssessmentService
     * per lanciare in parallelo le chiamate ai due aggregatori.
     * Core = 2: una per il DiagnosticAggregator e una per il ClinicalAggregator.
     * La chiamata al lab può richiedere 8-10s: i thread rimangono bloccati per tutta
     * la durata dell'elaborazione del campione, quindi il pool non deve essere troppo
     * piccolo se il coordinator riceve richieste concorrenti.
     */
    @Bean(name = "coordinatorExecutor")
    public Executor coordinatorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("coordinator-");
        executor.initialize();
        return executor;
    }
}
