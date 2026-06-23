package it.disim.univaq.sose.healthsoa.laboratorio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async thread pool configuration for the Laboratory Service.
 *
 * <p>Defines a dedicated {@link java.util.concurrent.Executor} used by
 * {@code @Async("labExecutor")} in {@code LabService.processOrderAsync()}.
 * The pool is intentionally separate from Tomcat's HTTP thread pool to prevent
 * the long-running sample analysis simulation ({@code Thread.sleep}) from
 * exhausting the threads available for handling incoming HTTP requests.
 *
 * <p>Pool parameters:
 * <ul>
 *   <li>Core threads: 4 - always available for concurrent orders;</li>
 *   <li>Max threads: 10 - burst capacity during peak order intake;</li>
 *   <li>Queue capacity: 50 - orders waiting for a free thread before rejection;</li>
 *   <li>Thread name prefix: {@code lab-async-} - visible in thread dumps and logs.</li>
 * </ul>
 */
@Configuration
public class AsyncConfig {

    /**
     * Creates and initialises the lab async executor bean.
     *
     * <p>Named {@code "labExecutor"} so that {@code @Async("labExecutor")} in
     * {@code LabService} selects this specific pool rather than the Spring default.
     *
     * @return configured {@link ThreadPoolTaskExecutor} ready for use
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
