package it.disim.univaq.sose.healthsoa.clinical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ClinicalAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalAggregatorApplication.class, args);
    }
}
