package it.disim.univaq.sose.healthsoa.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.disim.univaq.sose.healthsoa.farmacia.dto.CreatePrescriptionRequest;
import it.disim.univaq.sose.healthsoa.farmacia.dto.PrescriptionDto;
import it.disim.univaq.sose.healthsoa.farmacia.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Prescrizioni", description = "Gestione delle prescrizioni farmacologiche del paziente")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Prescrizioni attive del paziente",
               description = "Restituisce l'elenco delle prescrizioni farmacologiche attive per il paziente indicato.")
    @ApiResponse(responseCode = "200", description = "Elenco prescrizioni restituito correttamente")
    @GetMapping("/patients/{patientId}/prescriptions")
    public List<PrescriptionDto> getPrescriptions(
            @Parameter(description = "Identificativo paziente") @PathVariable String patientId) {
        return prescriptionService.getActivePrescriptions(patientId);
    }

    @Operation(summary = "Crea una nuova prescrizione",
               description = "Registra una nuova prescrizione farmacologica per il paziente. " +
                             "Chiamata diretta client→provider (UC-4): nessun prosumer è coinvolto. " +
                             "Ritorna 201 Created con la prescrizione salvata nel body, incluso l'ID assegnato.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prescrizione creata correttamente"),
        @ApiResponse(responseCode = "400", description = "Dati obbligatori mancanti o malformati")
    })
    @PostMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @Parameter(description = "Identificativo paziente") @PathVariable String patientId,
            @Valid @RequestBody CreatePrescriptionRequest request) {
        PrescriptionDto created = prescriptionService.createPrescription(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Traduce i fallimenti di validazione in una mappa campo→messaggio con status 400. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }
}
