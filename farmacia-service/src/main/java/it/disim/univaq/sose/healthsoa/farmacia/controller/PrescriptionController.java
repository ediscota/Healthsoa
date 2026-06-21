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
@Tag(name = "Prescriptions", description = "Management of pharmaceutical prescriptions for patients")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Operation(summary = "Get active prescriptions for a patient",
               description = "Returns the list of active pharmaceutical prescriptions for the specified patient.")
    @ApiResponse(responseCode = "200", description = "Prescription list returned successfully")
    @GetMapping("/patients/{patientId}/prescriptions")
    public List<PrescriptionDto> getPrescriptions(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId) {
        return prescriptionService.getActivePrescriptions(patientId);
    }

    @Operation(summary = "Create a new prescription",
               description = "Registers a new pharmaceutical prescription for the patient. " +
                             "Direct client→provider call (UC-4): no prosumer is involved. " +
                             "Returns 201 Created with the saved prescription in the body, including the assigned ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prescription created successfully"),
        @ApiResponse(responseCode = "400", description = "Required fields missing or malformed")
    })
    @PostMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId,
            @Valid @RequestBody CreatePrescriptionRequest request) {
        PrescriptionDto created = prescriptionService.createPrescription(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Translates validation failures into a field→message map with HTTP 400. */
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
