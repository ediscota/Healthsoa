package it.disim.univaq.sose.healthsoa.farmacia.service;

import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionService {

    public List<PrescriptionDto> getActivePrescriptions(String patientId) {
        return List.of(
                new PrescriptionDto("Amoxicillina", "J01CA04", "1g", "ogni 8 ore",
                        LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 10), "Dr. Rossi"),
                new PrescriptionDto("Ramipril", "C09AA05", "5mg", "una volta al giorno",
                        LocalDate.of(2026, 1, 15), LocalDate.of(2026, 12, 31), "Dr. Bianchi")
        );
    }

}
