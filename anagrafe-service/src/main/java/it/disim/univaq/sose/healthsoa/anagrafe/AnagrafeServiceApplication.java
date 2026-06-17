package it.disim.univaq.sose.healthsoa.anagrafe;

import it.disim.univaq.sose.healthsoa.anagrafe.endpoint.AnagrafeEndpoint;
import it.disim.univaq.sose.healthsoa.anagrafe.service.AnagrafeService;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class AnagrafeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnagrafeServiceApplication.class, args);
    }

    /**
     * Pubblica AnagrafeEndpoint su /soap/anagrafe tramite il Bus CXF.
     * L'endpoint viene istanziato qui (non come @Component) per evitare
     * la doppia registrazione del bean nello Spring context.
     */
    @Bean
    public EndpointImpl anagrafeEndpoint(Bus bus, AnagrafeService anagrafeService) {
        AnagrafeEndpoint endpoint = new AnagrafeEndpoint(anagrafeService);
        EndpointImpl ep = new EndpointImpl(bus, endpoint);
        ep.publish("/anagrafe");
        return ep;
    }

}
