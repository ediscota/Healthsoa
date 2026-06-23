package it.disim.univaq.sose.healthsoa.imaging.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.disim.univaq.sose.healthsoa.imaging.dto.CallbackRequest;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingOrderRequest;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingOrderResponse;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingReportDto;
import it.disim.univaq.sose.healthsoa.imaging.dto.ImagingStatusDto;
import it.disim.univaq.sose.healthsoa.imaging.model.ImagingReport;
import it.disim.univaq.sose.healthsoa.imaging.service.ImagingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for the Diagnostic Imaging Service (Provider 4).
 *
 * <p>Simulates a RIS/PACS (Radiology Information System / Picture Archiving and
 * Communication System) that stores and serves radiology reports. Like the
 * Laboratory Service, it follows the <em>Asynchronous Request-Reply</em> pattern:
 * a new imaging request returns 202 Accepted immediately, and the report is
 * generated asynchronously on the {@code imagingExecutor} thread pool.
 *
 * <p>Pre-existing archived reports (loaded from {@code data.sql}) are also
 * accessible via the GET endpoints and are returned synchronously.
 *
 * <p>This service can be scaled to multiple instances; all endpoints are
 * stateless with respect to in-flight orders (state lives in MySQL).
 */
@RestController
@Tag(name = "Imaging Reports", description = "Retrieval of radiology reports and management of asynchronous imaging orders (RIS/PACS simulation)")
public class ImagingController {

    private final ImagingService imagingService;

    public ImagingController(ImagingService imagingService) {
        this.imagingService = imagingService;
    }

    /**
     * Returns all archived radiology reports for a given patient.
     *
     * <p>An optional {@code examType} query parameter filters by exam type
     * (e.g., {@code RX_TORACE}, {@code TC_ADDOME}). This filter is used by the
     * Diagnostic Aggregator when composing the diagnostic bundle for a specific
     * panel (UC-1, UC-3).
     *
     * @param patientId numeric patient identifier
     * @param examType  optional exam type filter; omit to return all reports
     * @return 200 with list of matching reports (may be empty)
     */
    @Operation(
        summary = "List radiology reports for a patient",
        description = "Returns all archived imaging reports for the given patient. " +
                      "Use the optional examType parameter to filter by exam category " +
                      "(e.g. RX_TORACE, TC_ADDOME, RM_CRANIO, ECO_ADDOME)."
    )
    @ApiResponse(responseCode = "200", description = "Report list returned (may be empty)")
    @GetMapping("/patients/{patientId}/reports")
    public ResponseEntity<List<ImagingReportDto>> getReportsByPatient(
            @Parameter(description = "Numeric patient identifier") @PathVariable String patientId,
            @Parameter(description = "Optional exam type filter") @RequestParam(name = "examType", required = false) String examType) {
        List<ImagingReportDto> reports = (examType != null && !examType.isBlank())
                ? imagingService.getReportsByPatientAndExamType(patientId, examType)
                : imagingService.getReportsByPatient(patientId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Returns a single radiology report by its identifier.
     *
     * <p>The report may be in any status (PENDING, PROCESSING, COMPLETED). If the
     * report is still being processed, the {@code findings} and {@code conclusion}
     * fields will be null.
     *
     * @param reportId the report identifier
     * @return 200 with report data, or 404 if not found
     */
    @Operation(
        summary = "Get a radiology report by ID",
        description = "Returns the full radiology report. If the report is still being processed, " +
                      "findings and conclusion will be null and status will be PENDING or PROCESSING."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Report returned successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ImagingReportDto> getReport(
            @Parameter(description = "Report identifier") @PathVariable Long reportId) {
        try {
            return ResponseEntity.ok(imagingService.getReport(reportId));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Submits a new asynchronous imaging order and returns 202 Accepted immediately.
     *
     * <p>Processing runs on the {@code imagingExecutor} thread pool, simulating the
     * radiologist's reading time.
     *
     * @param request JSON body with {@code patientId} and {@code examType}
     * @return 202 Accepted with the assigned reportId and polling instructions
     */
    @Operation(
        summary = "Submit an imaging order",
        description = "Accepts the imaging request immediately (202 Accepted) and starts asynchronous processing. " +
                      "Use the returned reportId to poll for status or register a callback webhook."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Imaging order accepted; processing started in background"),
        @ApiResponse(responseCode = "400", description = "Malformed request body")
    })
    @PostMapping("/imaging/orders")
    public ResponseEntity<ImagingOrderResponse> submitOrder(@RequestBody ImagingOrderRequest request) {
        ImagingReport report = imagingService.createOrder(request.getPatientId(), request.getExamType());
        imagingService.processOrderAsync(report.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ImagingOrderResponse(report.getId(), report.getStatus().name(),
                        "Request accepted. Use GET /imaging/orders/" + report.getId() + "/status for polling."));
    }

    /**
     * Returns the current processing status of an imaging order.
     *
     * <p>Possible statuses: {@code PENDING}, {@code PROCESSING}, {@code COMPLETED}, {@code ERROR}.
     *
     * @param id the report identifier returned by the submit endpoint
     * @return 200 with status details, or 404 if not found
     */
    @Operation(
        summary = "Get imaging order status",
        description = "Returns the current processing status of an imaging order. " +
                      "Possible values: PENDING, PROCESSING, COMPLETED, ERROR."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status returned successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/imaging/orders/{id}/status")
    public ResponseEntity<ImagingStatusDto> getStatus(
            @Parameter(description = "Report identifier") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(imagingService.getStatus(id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Returns the full completed radiology report.
     *
     * <p>Returns 409 Conflict if the report has not yet reached COMPLETED status.
     * The client should poll {@code GET /imaging/orders/{id}/status} until COMPLETED
     * before calling this endpoint.
     *
     * @param id the report identifier
     * @return 200 with complete report, 404 if not found, 409 if not yet completed
     */
    @Operation(
        summary = "Get imaging order result",
        description = "Returns the completed radiology report including radiologist findings and conclusion. " +
                      "Returns 409 Conflict if the order is not yet in COMPLETED status."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Completed report returned successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "409", description = "Order not yet completed - retry after polling status")
    })
    @GetMapping("/imaging/orders/{id}/result")
    public ResponseEntity<?> getResult(
            @Parameter(description = "Report identifier") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(imagingService.getResult(id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ImagingService.ReportNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "currentStatus", e.getCurrentStatus()));
        }
    }
 /*
    /**
     * Registers or updates a webhook callback URL for an imaging order.
     *
     * <p>When the imaging report is ready, the service will POST the completed
     * {@link ImagingReportDto} to the registered URL. Callback delivery is
     * best-effort; a delivery failure does not affect the order's final state.
     *
     * @param id   the report identifier
     * @param body JSON body containing the {@code callbackUrl}
     * @return 200 on successful registration, 404 if not found
     */

    // @Operation(
    //     summary = "Register a callback webhook",
    //     description = "Registers (or replaces) a callback URL for an imaging order. " +
    //                   "The service will POST the completed ImagingReportDto to the given URL when processing finishes."
    // )
    // @ApiResponses({
    //     @ApiResponse(responseCode = "200", description = "Callback URL registered successfully"),
    //     @ApiResponse(responseCode = "404", description = "Order not found")
    // })
    @PostMapping("/imaging/orders/{id}/callback")
    public ResponseEntity<?> registerCallback(
            @Parameter(description = "Report identifier") @PathVariable Long id,
            @RequestBody CallbackRequest body) {
        try {
            imagingService.registerCallback(id, body.getCallbackUrl());
            return ResponseEntity.ok(Map.of("message", "Callback registered for report " + id));
        } catch (ImagingService.ReportNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}
