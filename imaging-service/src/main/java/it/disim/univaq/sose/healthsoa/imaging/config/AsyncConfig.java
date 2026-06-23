package it.disim.univaq.sose.healthsoa.imaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async thread pool configuration for the Imaging Service.
 *
 * <p>Defines a dedicated {@link java.util.concurrent.Executor} used by
 * {@code @Async("imagingExecutor")} in {@code ImagingService.processOrderAsync()}.
 * The pool is separate from Tomcat's HTTP thread pool to prevent the radiologist
 * simulation delay ({@code Thread.sleep}) from blocking incoming HTTP requests.
 *
 * <p>Pool parameters:
 * <ul>
 *   <li>Core threads: 4 - baseline capacity for concurrent imaging orders;</li>
 *   <li>Max threads: 10 - burst capacity;</li>
 *   <li>Queue capacity: 50 - orders buffered before rejection;</li>
 *   <li>Thread name prefix: {@code imaging-async-} - visible in thread dumps.</li>
 * </ul>
 */
@Configuration
public class AsyncConfig {

    /**
     * Creates and initialises the imaging async executor bean.
     *
     * <p>Named {@code "imagingExecutor"} so that {@code @Async("imagingExecutor")}
     * in {@code ImagingService} selects this pool rather than the Spring default.
     *
     * @return configured {@link ThreadPoolTaskExecutor} ready for use
     */
    @Bean(name = "imagingExecutor")
    public Executor imagingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("imaging-async-");
        executor.initialize();
        return executor;
    }
}
