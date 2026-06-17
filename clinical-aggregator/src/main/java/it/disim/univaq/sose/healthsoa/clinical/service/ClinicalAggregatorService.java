package it.disim.univaq.sose.healthsoa.clinical.service;

import it.disim.univaq.sose.healthsoa.anagrafe.generated.AnagrafePortType;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Allergy;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Condition;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetAllergiesRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetMedicalHistoryRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetPatientByIdRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Patient;
import it.disim.univaq.sose.healthsoa.clinical.client.FarmaciaClient;
import it.disim.univaq.sose.healthsoa.clinical.dto.AllergyDto;
import it.disim.univaq.sose.healthsoa.clinical.dto.ClinicalProfile;
import it.disim.univaq.sose.healthsoa.clinical.dto.ConditionDto;
import it.disim.univaq.sose.healthsoa.clinical.dto.PatientDto;
import it.disim.univaq.sose.healthsoa.clinical.dto.PrescriptionDto;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@Service
public class ClinicalAggregatorService {

    private final AnagrafePortType anagrafePort;
    private final FarmaciaClient farmaciaClient;

    public ClinicalAggregatorService(AnagrafePortType anagrafePort, FarmaciaClient farmaciaClient) {
        this.anagrafePort = anagrafePort;
        this.farmaciaClient = farmaciaClient;
    }

    public ClinicalProfile buildProfile(String patientId) {
        // Chiamata SOAP 1: dati anagrafici
        GetPatientByIdRequest patientReq = new GetPatientByIdRequest();
        patientReq.setPatientId(patientId);
        Patient patient = anagrafePort.getPatientById(patientReq).getPatient();

        // Chiamata SOAP 2: storico patologico
        GetMedicalHistoryRequest histReq = new GetMedicalHistoryRequest();
        histReq.setPatientId(patientId);
        List<Condition> conditions = anagrafePort.getMedicalHistory(histReq)
                .getMedicalHistory().getCondition();

        // Chiamata SOAP 3: allergie
        GetAllergiesRequest allergyReq = new GetAllergiesRequest();
        allergyReq.setPatientId(patientId);
        List<Allergy> allergies = anagrafePort.getAllergies(allergyReq)
                .getAllergies().getAllergy();

        // Chiamata REST via Feign: prescrizioni attive
        List<PrescriptionDto> prescriptions = farmaciaClient.getPrescriptions(patientId);

        return new ClinicalProfile(
                toPatientDto(patient),
                conditions.stream().map(this::toConditionDto).toList(),
                allergies.stream().map(this::toAllergyDto).toList(),
                prescriptions
        );
    }

    private PatientDto toPatientDto(Patient p) {
        PatientDto dto = new PatientDto();
        dto.setId(p.getId());
        dto.setFiscalCode(p.getFiscalCode());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setDateOfBirth(calendarToString(p.getDateOfBirth()));
        dto.setGender(p.getGender() != null ? p.getGender().value() : null);
        dto.setPhone(p.getPhone());
        return dto;
    }

    private ConditionDto toConditionDto(Condition c) {
        ConditionDto dto = new ConditionDto();
        dto.setDescription(c.getDescription());
        dto.setIcdCode(c.getIcd10Code());
        dto.setDiagnosisDate(calendarToString(c.getOnsetDate()));
        dto.setStatus(c.getStatus() != null ? c.getStatus().value() : null);
        return dto;
    }

    private AllergyDto toAllergyDto(Allergy a) {
        AllergyDto dto = new AllergyDto();
        dto.setAllergen(a.getSubstance());
        dto.setSeverity(a.getReactionSeverity() != null ? a.getReactionSeverity().value() : null);
        return dto;
    }

    private String calendarToString(XMLGregorianCalendar cal) {
        return cal != null ? cal.toGregorianCalendar().toZonedDateTime().toLocalDate().toString() : null;
    }
}
