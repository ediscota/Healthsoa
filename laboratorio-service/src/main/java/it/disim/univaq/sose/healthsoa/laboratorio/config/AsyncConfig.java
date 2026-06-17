package it.disim.univaq.sose.healthsoa.laboratorio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    /**
     * Executor dedicato alla lavorazione asincrona degli ordini di laboratorio.
     * Pool separato per non saturare il thread pool HTTP di Tomcat durante
     * le simulazioni di elaborazione prolungata (Thread.sleep).
     */
    @Bean(name = "labExecutor")
    public Executor labExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("lab-async-");
        executor.initialize();
        return executor;
    }

}
