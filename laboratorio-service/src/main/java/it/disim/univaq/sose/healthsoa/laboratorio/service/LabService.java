package it.disim.univaq.sose.healthsoa.laboratorio.service;

import it.disim.univaq.sose.healthsoa.laboratorio.dto.MeasurementDto;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.OrderStatusDto;
import it.disim.univaq.sose.healthsoa.laboratorio.dto.TestResultDto;
import it.disim.univaq.sose.healthsoa.laboratorio.model.Measurement;
import it.disim.univaq.sose.healthsoa.laboratorio.model.OrderStatus;
import it.disim.univaq.sose.healthsoa.laboratorio.model.TestOrder;
import it.disim.univaq.sose.healthsoa.laboratorio.repository.TestOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LabService {

    private final TestOrderRepository orderRepository;
    private final RestTemplate restTemplate;

    /** Durata in ms della simulazione di lavorazione del campione (configurabile). */
    @Value("${lab.processing.delay-ms:8000}")
    private long processingDelayMs;

    /** Risultati simulati per codice esame. Realistici per la logica di rischio del Care Coordinator. */
    private static final Map<String, List<Measurement>> EXAM_RESULTS = Map.of(
        "PANEL_RENAL", List.of(
            meas("Creatinina",   1.8, "mg/dL", "0.6-1.2", true),
            meas("Azotemia",     55.0, "mg/dL", "10-50",  true),
            meas("Sodio",        140.0, "mEq/L", "136-145", false)
        ),
        "PANEL_METABOLICO", List.of(
            meas("Glicemia",     98.0, "mg/dL", "70-100",  false),
            meas("Colesterolo",  210.0, "mg/dL", "0-200",   true),
            meas("Trigliceridi", 145.0, "mg/dL", "0-150",   false)
        ),
        "PANEL_EMOCROMO", List.of(
            meas("Emoglobina",   12.1, "g/dL",  "13.5-17.5", true),
            meas("Leucociti",    7500.0, "cel/uL","4000-10000", false),
            meas("Piastrine",    220000.0, "cel/uL","150000-400000", false)
        )
    );

    public LabService(TestOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public TestOrder createOrder(String patientId, String examCode) {
        TestOrder order = new TestOrder();
        order.setPatientId(patientId);
        order.setExamCode(examCode);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public void registerCallback(Long orderId, String callbackUrl) {
        TestOrder order = findOrder(orderId);
        order.setCallbackUrl(callbackUrl);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getStatus(Long orderId) {
        TestOrder o = findOrder(orderId);
        return new OrderStatusDto(o.getId(), o.getPatientId(), o.getExamCode(),
                o.getStatus().name(), o.getCreatedAt(), o.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public TestResultDto getResult(Long orderId) {
        TestOrder o = findOrder(orderId);
        if (o.getStatus() != OrderStatus.COMPLETED) {
            throw new OrderNotCompletedException(orderId, o.getStatus().name());
        }
        return toResultDto(o);
    }

    /**
     * Lavorazione asincrona: transita PROCESSING, dorme processingDelayMs,
     * persiste i Measurement, transita COMPLETED, chiama la callback se registrata.
     * Annotato @Async("labExecutor") — viene eseguito su un thread del pool dedicato
     * senza bloccare il thread HTTP che ha risposto 202 al client.
     */
    @Async("labExecutor")
    @Transactional
    public void processOrderAsync(Long orderId) {
        TestOrder order = findOrder(orderId);
        try {
            order.setStatus(OrderStatus.PROCESSING);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            Thread.sleep(processingDelayMs);

            List<Measurement> results = buildMeasurements(order);
            results.forEach(m -> m.setTestOrder(order));
            order.getMeasurements().addAll(results);
            order.setStatus(OrderStatus.COMPLETED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            if (order.getCallbackUrl() != null && !order.getCallbackUrl().isBlank()) {
                fireCallback(order);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            order.setStatus(OrderStatus.ERROR);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }

    private void fireCallback(TestOrder order) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            TestResultDto payload = toResultDto(order);
            HttpEntity<TestResultDto> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(order.getCallbackUrl(), request, Void.class);
        } catch (Exception e) {
            // La callback è best-effort: un fallimento non deve far fallire la lavorazione.
        }
    }

    private List<Measurement> buildMeasurements(TestOrder order) {
        List<Measurement> template = EXAM_RESULTS.get(order.getExamCode());
        if (template != null) {
            return template.stream().map(m -> {
                Measurement copy = new Measurement();
                copy.setParameter(m.getParameter());
                copy.setValue(m.getValue());
                copy.setUnit(m.getUnit());
                copy.setReferenceRange(m.getReferenceRange());
                copy.setAnomalyFlag(m.isAnomalyFlag());
                return copy;
            }).toList();
        }
        // Esame non in catalogo: genera un measurement generico senza anomalia.
        Measurement generic = new Measurement();
        generic.setParameter("Risultato");
        generic.setValue(1.0);
        generic.setUnit("u.a.");
        generic.setReferenceRange("0-2");
        generic.setAnomalyFlag(false);
        return List.of(generic);
    }

    private TestResultDto toResultDto(TestOrder o) {
        List<MeasurementDto> mDtos = o.getMeasurements().stream()
                .map(m -> new MeasurementDto(m.getParameter(), m.getValue(),
                        m.getUnit(), m.getReferenceRange(), m.isAnomalyFlag()))
                .toList();
        return new TestResultDto(o.getId(), o.getPatientId(), o.getExamCode(), mDtos);
    }

    private TestOrder findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private static Measurement meas(String param, double val, String unit, String ref, boolean flag) {
        Measurement m = new Measurement();
        m.setParameter(param);
        m.setValue(val);
        m.setUnit(unit);
        m.setReferenceRange(ref);
        m.setAnomalyFlag(flag);
        return m;
    }

    // Eccezioni locali al service per mappatura HTTP nel controller
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(Long id) { super("Ordine non trovato: " + id); }
    }

    public static class OrderNotCompletedException extends RuntimeException {
        private final String currentStatus;
        public OrderNotCompletedException(Long id, String status) {
            super("Ordine " + id + " non ancora completato. Stato: " + status);
            this.currentStatus = status;
        }
        public String getCurrentStatus() { return currentStatus; }
    }

}
