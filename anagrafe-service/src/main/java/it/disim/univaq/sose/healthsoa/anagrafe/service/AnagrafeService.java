package it.disim.univaq.sose.healthsoa.anagrafe.service;

import it.disim.univaq.sose.healthsoa.anagrafe.generated.Allergy;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.AllergyList;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Condition;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.ConditionList;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.ConditionStatus;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Gender;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Patient;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.ReactionSeverity;
import it.disim.univaq.sose.healthsoa.anagrafe.model.AllergyEntity;
import it.disim.univaq.sose.healthsoa.anagrafe.model.ConditionEntity;
import it.disim.univaq.sose.healthsoa.anagrafe.model.PatientEntity;
import it.disim.univaq.sose.healthsoa.anagrafe.repository.AllergyRepository;
import it.disim.univaq.sose.healthsoa.anagrafe.repository.ConditionRepository;
import it.disim.univaq.sose.healthsoa.anagrafe.repository.PatientRepository;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Service
public class AnagrafeService {

    private final PatientRepository patientRepository;
    private final ConditionRepository conditionRepository;
    private final AllergyRepository allergyRepository;

    public AnagrafeService(PatientRepository patientRepository,
                           ConditionRepository conditionRepository,
                           AllergyRepository allergyRepository) {
        this.patientRepository = patientRepository;
        this.conditionRepository = conditionRepository;
        this.allergyRepository = allergyRepository;
    }

    public Patient getPatientById(String patientId) {
        PatientEntity entity = patientRepository.findById(Long.parseLong(patientId))
                .orElseThrow(() -> new RuntimeException("Paziente non trovato: " + patientId));
        return toPatientWs(entity);
    }

    public ConditionList getMedicalHistory(String patientId) {
        PatientEntity patient = patientRepository.findById(Long.parseLong(patientId))
                .orElseThrow(() -> new RuntimeException("Paziente non trovato: " + patientId));
        List<ConditionEntity> entities = conditionRepository.findByPatientId(patient.getId());
        ConditionList list = new ConditionList();
        entities.stream().map(this::toConditionWs).forEach(list.getCondition()::add);
        return list;
    }

    public AllergyList getAllergies(String patientId) {
        PatientEntity patient = patientRepository.findById(Long.parseLong(patientId))
                .orElseThrow(() -> new RuntimeException("Paziente non trovato: " + patientId));
        List<AllergyEntity> entities = allergyRepository.findByPatientId(patient.getId());
        AllergyList list = new AllergyList();
        entities.stream().map(this::toAllergyWs).forEach(list.getAllergy()::add);
        return list;
    }

    private Patient toPatientWs(PatientEntity e) {
        Patient p = new Patient();
        p.setId(e.getId());
        p.setFiscalCode(e.getFiscalCode());
        p.setFirstName(e.getFirstName());
        p.setLastName(e.getLastName());
        p.setDateOfBirth(toXmlDate(e.getDateOfBirth()));
        p.setGender(Gender.fromValue(e.getGender().name()));
        p.setPhone(e.getPhone());
        return p;
    }

    private Condition toConditionWs(ConditionEntity e) {
        Condition c = new Condition();
        c.setId(e.getId());
        c.setIcd10Code(e.getIcd10Code());
        c.setDescription(e.getDescription());
        c.setOnsetDate(toXmlDate(e.getOnsetDate()));
        c.setStatus(ConditionStatus.fromValue(e.getStatus().name()));
        return c;
    }

    private Allergy toAllergyWs(AllergyEntity e) {
        Allergy a = new Allergy();
        a.setId(e.getId());
        a.setSubstance(e.getSubstance());
        a.setReactionSeverity(ReactionSeverity.fromValue(e.getReactionSeverity().name()));
        a.setDetectedDate(toXmlDate(e.getDetectedDate()));
        return a;
    }

    private XMLGregorianCalendar toXmlDate(java.time.LocalDate date) {
        try {
            GregorianCalendar gc = GregorianCalendar.from(
                    date.atStartOfDay(ZoneId.of("UTC")));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (Exception ex) {
            throw new RuntimeException("Errore conversione data", ex);
        }
    }

}
