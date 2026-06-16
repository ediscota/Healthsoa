package it.disim.univaq.sose.healthsoa.farmacia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FarmaciaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmaciaServiceApplication.class, args);
    }

}
