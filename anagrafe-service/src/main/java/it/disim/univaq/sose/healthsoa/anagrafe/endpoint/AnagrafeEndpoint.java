package it.disim.univaq.sose.healthsoa.anagrafe.endpoint;

import it.disim.univaq.sose.healthsoa.anagrafe.generated.AllergyList;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.AnagrafePortType;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.ConditionList;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetAllergiesRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetAllergiesResponse;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetMedicalHistoryRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetMedicalHistoryResponse;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetPatientByIdRequest;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.GetPatientByIdResponse;
import it.disim.univaq.sose.healthsoa.anagrafe.generated.Patient;
import it.disim.univaq.sose.healthsoa.anagrafe.service.AnagrafeService;
import jakarta.jws.WebService;

@WebService(
        serviceName   = "AnagrafeService",
        portName      = "AnagrafePort",
        targetNamespace = "http://anagrafe.healthsoa.sose.univaq.disim.it/",
        endpointInterface = "it.disim.univaq.sose.healthsoa.anagrafe.generated.AnagrafePortType",
        wsdlLocation  = "classpath:wsdl/anagrafe.wsdl"
)
public class AnagrafeEndpoint implements AnagrafePortType {

    private final AnagrafeService anagrafeService;

    public AnagrafeEndpoint(AnagrafeService anagrafeService) {
        this.anagrafeService = anagrafeService;
    }

    @Override
    public GetPatientByIdResponse getPatientById(GetPatientByIdRequest parameters) {
        Patient patient = anagrafeService.getPatientById(parameters.getPatientId());
        GetPatientByIdResponse response = new GetPatientByIdResponse();
        response.setPatient(patient);
        return response;
    }

    @Override
    public GetMedicalHistoryResponse getMedicalHistory(GetMedicalHistoryRequest parameters) {
        ConditionList history = anagrafeService.getMedicalHistory(parameters.getPatientId());
        GetMedicalHistoryResponse response = new GetMedicalHistoryResponse();
        response.setMedicalHistory(history);
        return response;
    }

    @Override
    public GetAllergiesResponse getAllergies(GetAllergiesRequest parameters) {
        AllergyList allergies = anagrafeService.getAllergies(parameters.getPatientId());
        GetAllergiesResponse response = new GetAllergiesResponse();
        response.setAllergies(allergies);
        return response;
    }

}
