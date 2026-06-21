package it.disim.univaq.sose.healthsoa.anagrafe;

import it.disim.univaq.sose.healthsoa.anagrafe.endpoint.AnagrafeEndpoint;
import it.disim.univaq.sose.healthsoa.anagrafe.service.AnagrafeService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * Entry point for the Anagrafe Pazienti service (Provider 1 — SOAP).
 *
 * <p>Spring Boot application that exposes a contract-first SOAP endpoint at
 * {@code /soap/anagrafe} using Apache CXF. The WSDL is defined at
 * {@code src/main/resources/wsdl/anagrafe.wsdl}; Java binding classes are generated
 * at build time by the {@code cxf-codegen-plugin} Maven plugin.
 *
 * <p>The SOAP endpoint is published via a {@link org.apache.cxf.jaxws.EndpointImpl}
 * bean created in {@link #anagrafeEndpoint}, which registers {@code AnagrafeEndpoint}
 * on the CXF {@code Bus} without double-registering it as a Spring {@code @Component}.
 *
 * <p>{@code @EnableDiscoveryClient} registers this service with Eureka under the name
 * {@code anagrafe-service} so the Clinical Aggregator can resolve the SOAP endpoint
 * URL via the Config Server property rather than a hardcoded address.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AnagrafeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnagrafeServiceApplication.class, args);
    }

    /**
     * Publishes {@code AnagrafeEndpoint} at {@code /soap/anagrafe} via the CXF Bus.
     * The endpoint is instantiated here (not as a {@code @Component}) to prevent
     * double-registration in the Spring application context.
     */
    @Bean
    public EndpointImpl anagrafeEndpoint(Bus bus, AnagrafeService anagrafeService) {
        AnagrafeEndpoint endpoint = new AnagrafeEndpoint(anagrafeService);
        EndpointImpl ep = new EndpointImpl(bus, endpoint);
        ep.publish("/anagrafe");
        return ep;
    }

}
