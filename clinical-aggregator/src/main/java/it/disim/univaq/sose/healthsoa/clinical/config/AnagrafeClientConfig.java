package it.disim.univaq.sose.healthsoa.clinical.config;

import it.disim.univaq.sose.healthsoa.anagrafe.generated.AnagrafePortType;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.AnagrafeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class AnagrafeClientConfig {

    /**
     * URL del servizio SOAP Anagrafe, letta dal Config Server.
     * Analogo a microservice.user.find.uri di slide 28.
     * Aggiornabile a runtime via POST /actuator/refresh grazie a @RefreshScope.
     */
    @Value("${anagrafe.soap.endpoint:http://localhost:9101/soap/anagrafe}")
    private String anagrafeEndpointUrl;

    /**
     * Bean del port JAX-WS annotato @RefreshScope: se anagrafe.soap.endpoint
     * cambia nel config server e si chiama /actuator/refresh, il bean viene
     * ricreato con il nuovo URL senza riavviare il servizio.
     */
    @Bean
    @RefreshScope
    public AnagrafePortType anagrafePortType() throws MalformedURLException {
        URL wsdlUrl = getClass().getClassLoader().getResource("wsdl/anagrafe.wsdl");
        QName serviceName = new QName(
                "http://anagrafe.healthsoa.sose.univaq.disim.it/", "AnagrafeService");
        AnagrafeService service = new AnagrafeService(wsdlUrl, serviceName);
        AnagrafePortType port = service.getAnagrafePort();

        // Sovrascrive l'endpoint hardcodato nel WSDL con quello da Config Server
        ((jakarta.xml.ws.BindingProvider) port)
                .getRequestContext()
                .put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, anagrafeEndpointUrl);

        return port;
    }
}
