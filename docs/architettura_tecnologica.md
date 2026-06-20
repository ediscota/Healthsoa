# HealthSOA — Architettura Tecnologica

Documento di specifica tecnologica e infrastrutturale del progetto **HealthSOA**, complementare al documento di specifica applicativa. Le scelte qui descritte adottano lo stack didattico presentato durante il corso (slide di Sviluppo Microservizi con Spring Boot, Dr. Filippone) e, dove la traccia richiede capacità non coperte dalle slide, optano per la soluzione più semplice e coerente con quello stack.

---

## 1. Principi guida

Le scelte tecnologiche di questo progetto seguono tre principi:

**Aderenza allo stack didattico.** La traccia chiede esplicitamente di *adottare le tecnologie spiegate in classe, nel modo in cui sono state insegnate*. Ogni componente che ha un equivalente diretto nelle slide del corso lo utilizza con la stessa configurazione e lo stesso pattern di integrazione.

**Semplicità prima di sofisticazione.** Per i punti che la traccia richiede ma che le slide non coprono (SOAP, asincronia, composizione parallela, OpenAPI), si sceglie l'implementazione più semplice e diretta, senza introdurre tecnologie aggiuntive non necessarie.

**Motivazione ancorata al dominio.** Ogni scelta infrastrutturale che la traccia chiede di motivare (scaling, asincronia) è giustificata con riferimento al profilo di carico e alla logica del sistema sanitario, non in modo generico.

---

## 2. Stack tecnologico complessivo

| Livello | Tecnologia | Versione | Fonte |
|---|---|---|---|
| Linguaggio | Java | 17 | Slide |
| Build | Apache Maven | 3.9.x | Slide |
| Framework base | Spring Boot | 3.x | Slide |
| API Gateway | Spring Cloud Gateway | Spring Cloud 2023.x | Slide |
| Service Registry | Netflix Eureka | Spring Cloud 2023.x | Slide |
| Configurazione centralizzata | Spring Cloud Config | Spring Cloud 2023.x | Slide |
| Client-side LB + chiamate REST | Spring Cloud OpenFeign | Spring Cloud 2023.x | Slide |
| Persistenza | Spring Data JPA + Hibernate | — | Slide |
| Database | MySQL | 8.x | Slide |
| SOAP | Apache CXF (Jakarta JAX-WS) | 4.x | Estensione (traccia) |
| Documentazione REST | springdoc-openapi | 2.x | Estensione (traccia) |
| Resilienza | Spring Retry + Resilience4j | — | Slide (Retry) + estensione |
| Containerization | Docker + docker-compose | — | Slide |

Tutti i servizi vengono inizializzati tramite **Spring Initializr / Spring Tools Suite** come progetti Maven indipendenti, esattamente come nella sessione di laboratorio.

---

## 3. Componenti infrastrutturali (servizi additional)

Questi tre componenti non sono servizi di business; sono l'infrastruttura di supporto che la traccia distingue dai service provider/prosumer.

### 3.1 Config Server — `healthsoa-config-server`

Porta 8888. Inizializzato con la dipendenza **Config Server**. Annotato con `@EnableConfigServer`. Legge da un repository git locale `healthsoa-properties-repository` (in dev) contenente un file `*-dev.properties` per ogni servizio:

```
healthsoa-properties-repository/
├── anagrafe-service-dev.properties
├── laboratorio-service-dev.properties
├── farmacia-service-dev.properties
├── imaging-service-dev.properties
├── diagnostic-aggregator-dev.properties
├── clinical-aggregator-dev.properties
├── care-coordinator-dev.properties
└── api-gateway-dev.yaml
```

Naming convention identica a quella della slide 25.

### 3.2 Discovery Server — `healthsoa-discovery-server`

Porta 8761. Inizializzato con la dipendenza **Eureka Server**. Annotato con `@EnableEurekaServer`. Configurazione *standalone* (un'unica istanza), come da slide 39:

```yaml
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

### 3.3 API Gateway — `healthsoa-gateway`

Porta 9000. Inizializzato con le dipendenze **Spring Cloud Gateway** + **Eureka Discovery Client** + **Config Client**. Annotato con `@EnableDiscoveryClient`. Le route sono definite in `api-gateway-dev.yaml` nel repository delle properties, usando lo schema `lb://nome-servizio` per il client-side load balancing tramite Eureka (slide 43).

Route esposte:

| Path esterno | Servizio interno | Note |
|---|---|---|
| `/api/coordinator/**` | `lb://care-coordinator` | UC-1 |
| `/api/clinical/**` | `lb://clinical-aggregator` | UC-2 |
| `/api/diagnostic/**` | `lb://diagnostic-aggregator` | UC-3 |
| `/api/pharmacy/**` | `lb://farmacia-service` | UC-4 (interazione diretta col provider) |
| `/soap/anagrafe/**` | URL diretto dell'Anagrafe | Routing SOAP, no rewrite del SOAPAction header |

Tutti i client esterni (compresa l'app web clinica) accedono al sistema esclusivamente tramite il gateway, soddisfacendo il vincolo 2 della traccia.

---

## 4. Service provider — scelte tecnologiche

Tutti i provider sono progetti Maven Spring Boot indipendenti, ognuno con il proprio `Dockerfile`, registrati su Eureka, configurati dal Config Server.

### 4.1 Anagrafe Service (SOAP)

**Stack**: Spring Boot + Apache CXF (`cxf-spring-boot-starter-jaxws`) + Spring Data JPA + MySQL connector + Eureka client + Config client.

**Approccio**: contract-first. Il WSDL e gli XSD sono mantenuti sotto `src/main/resources/wsdl/`. Le classi Java sono generate al `mvn generate-sources` tramite `cxf-codegen-plugin` configurato nel `pom.xml`.

**Endpoint**: pubblicato come `@WebService` Spring Bean. Esposto su `/soap/anagrafe`.

**Discovery**: il servizio si registra su Eureka come qualunque servizio Spring Boot tramite `@EnableDiscoveryClient`, ma la chiamata SOAP dall'esterno (dal Clinical Aggregator) non sfrutta il `lb://` di Feign — Feign è REST-only. Si veda la sezione 5.2.

**Documentazione del contratto**: il WSDL stesso è documentazione (vincolo 8 della traccia: "code must be documented with clear and verbose comments within source code, WSDL files, etc.").

### 4.2 Laboratorio Service (REST, asincrono)

**Stack**: Spring Boot + Spring Web + Spring Data JPA + MySQL connector + Eureka client + Config client + springdoc-openapi-starter-webmvc-ui.

**Esposizione asincrona**: nel `@RestController`:

- `POST /tests/orders` → crea l'entità `TestOrder` con stato `PENDING`, ritorna `202 Accepted` con `Location: /tests/orders/{id}` e l'`orderId` nel body. Il metodo del service è annotato `@Async` (con `@EnableAsync` sull'`Application` class) e simula la lavorazione del campione con un `Thread.sleep` configurabile + transizione di stato a `COMPLETED`.
- `GET /tests/orders/{id}/status` → restituisce lo stato corrente (pattern *polling*).
- `GET /tests/orders/{id}/result` → restituisce il risultato; ritorna `409 Conflict` se lo stato non è ancora `COMPLETED`.
- `POST /tests/orders/{id}/callback` → registra una URL di callback nell'entità ordine. Al completamento, il service esegue una `POST` su quella URL con il risultato (pattern *webhook*).

Entrambi i pattern (polling e callback) sono esposti per dimostrare la flessibilità; il consumer (Diagnostic Aggregator) sceglie quale usare.

**Documentazione REST**: `springdoc-openapi-starter-webmvc-ui` aggiunge automaticamente `/swagger-ui.html` e `/v3/api-docs` (vincolo 8).

**Scaling**: replicabile in più istanze via `docker compose up --scale laboratorio-service=3`. Il provider è sul percorso critico di ogni workflow diagnostico.

### 4.3 Farmacia Service (REST)

**Stack**: Spring Boot + Spring Web + Spring Data JPA + MySQL connector + Eureka client + Config client + springdoc-openapi.

**Endpoint**: sincroni, REST canonici per prescrizioni attive e controllo interazioni. Documentazione OpenAPI automatica.

### 4.4 Imaging Service (REST, asincrono)

**Stack**: identico al Laboratorio. Stesso pattern di asincronia (polling + callback) per i referti di imaging.

**Scaling**: replicabile come il Laboratorio.

---

## 5. Service prosumer — scelte tecnologiche

I prosumer sono progetti Spring Boot REST. **Non hanno persistenza di dominio**: sono componenti di orchestrazione. Si registrano su Eureka per essere raggiungibili dal gateway tramite `lb://`.

### 5.1 Diagnostic Aggregator — `diagnostic-aggregator`

**Stack**: Spring Boot + Spring Web + Spring Cloud OpenFeign + Eureka client + Config client + Resilience4j + springdoc-openapi.

**Chiamate ai provider**:

- Verso Laboratorio e Imaging: due `@FeignClient(name="laboratorio-service")` e `@FeignClient(name="imaging-service")`, esattamente come Filippone fa per `User`→`Job` (slide 47-48). Nessuna URL hardcodata: la risoluzione è via Eureka, il load balancing è client-side e gratuito.

**Gestione dello stato asincrono — design stateless**:

Il `trackingId` restituito al client dopo un ordine diagnostico è un token **Base64URL** che codifica le informazioni necessarie a ricostruire la risposta senza stato condiviso:

```
trackingId = Base64URL("{patientId}:{panelCode}:{labOrderId}")
```

Qualsiasi istanza dell'aggregatore che riceve una richiesta di polling (`GET /tracking/{trackingId}/status` o `GET /tracking/{trackingId}/result`) decodifica il token, interroga direttamente il laboratorio per lo stato e il risultato, e recupera i referti di imaging. Non esiste nessuna mappa in-memory, nessuna callback registrata, nessuna dipendenza dall'istanza che ha creato l'ordine.

**Scaling**: replicabile in più istanze via `docker compose up --scale diagnostic-aggregator=2`. Si trova sul percorso critico di ogni workflow UC-3 e può scalare insieme ai provider che orchestra.

### 5.2 Clinical Aggregator — `clinical-aggregator`

**Stack**: Spring Boot + Spring Web + Spring Cloud OpenFeign + Apache CXF client (per il SOAP) + Eureka client + Config client + Resilience4j + springdoc-openapi.

**Chiamata verso Farmacia (REST)**: tramite `@FeignClient(name="farmacia-service")`, come per il Diagnostic.

**Chiamata verso Anagrafe (SOAP)**: il client JAX-WS è generato dal WSDL tramite `cxf-codegen-plugin` nel `pom.xml` dell'aggregatore. L'endpoint URL del client è **iniettato come property** (`anagrafe.soap.endpoint`) letta dal Config Server, sullo stesso modello con cui Filippone gestisce `microservice.user.find.uri` nel `job-microservice-dev.properties` (slide 28). Il bean del client SOAP è annotato `@RefreshScope`, così un cambio dell'URL via `actuator/refresh` viene preso senza riavvio.

Questa scelta — URL statico via config server invece di lookup dinamico su Eureka per il SOAP — è la soluzione più semplice e replica il pattern delle slide. Il prezzo è che SOAP non beneficia del client-side load balancing di Eureka; si scala l'Anagrafe se necessario mettendo più istanze dietro un load balancer (gateway o un round-robin DNS).

### 5.3 Care Coordinator — `care-coordinator`

**Stack**: Spring Boot + Spring Web + Spring Cloud OpenFeign + Eureka client + Config client + Resilience4j + springdoc-openapi.

**Chiamate ai due aggregatori**: `@FeignClient(name="diagnostic-aggregator")` e `@FeignClient(name="clinical-aggregator")`.

**Composizione parallela con sincronizzazione (UC-1)**: implementata con `CompletableFuture` su un `ThreadPoolTaskExecutor` Spring Bean:

```java
@Service
public class FitnessAssessmentService {

    private final DiagnosticAggregatorClient diagnosticClient;  // Feign
    private final ClinicalAggregatorClient   clinicalClient;    // Feign
    private final RiskAnalyzer               riskAnalyzer;
    private final Executor                   executor;

    public FitnessReport assess(String patientId) {
        CompletableFuture<DiagnosticBundle> diag =
            CompletableFuture.supplyAsync(
                () -> diagnosticClient.getBundle(patientId), executor);

        CompletableFuture<ClinicalProfile> clin =
            CompletableFuture.supplyAsync(
                () -> clinicalClient.getProfile(patientId), executor);

        // Barriera di sincronizzazione: blocca finché entrambi non completano
        CompletableFuture.allOf(diag, clin).join();

        return riskAnalyzer.analyze(diag.join(), clin.join());
    }
}
```

**Resilienza**: i metodi del Feign client sono annotati `@CircuitBreaker(name="diagnostic", fallbackMethod="fallbackDiagnostic")`. Se un aggregatore non risponde entro il timeout configurato, il coordinator restituisce un `FitnessReport` parziale con un `RiskFlag` informativo che segnala la mancanza del dato.

---

## 6. Configurazione e proprietà

### 6.1 Pattern di configurazione

Ogni microservizio ha un solo `application.properties` minimale, contenente esclusivamente:

```properties
spring.application.name=nome-servizio
spring.profiles.active=dev
spring.config.import=optional:configserver:http://${CONFIG_SERVER_HOST}:${CONFIG_SERVER_PORT}
```

Tutto il resto è esternalizzato nel repository git delle properties e servito dal Config Server. Indirizzi (Eureka, MySQL, Anagrafe SOAP) **non sono mai hardcodati**: sono variabili d'ambiente (`${EUREKA_SERVER}`, `${MYSQL_HOST}`, ecc.) il cui valore è iniettato da docker-compose. Pattern replicato dalla slide 54.

### 6.2 Refresh a runtime

I bean che leggono proprietà via `@Value` e che possono cambiare a runtime (URL Anagrafe SOAP, soglie di rischio, timeout) sono annotati `@RefreshScope`. L'endpoint `POST /actuator/refresh` viene esposto via:

```properties
management.endpoints.web.exposure.include=health,info,refresh
```

### 6.3 Resilienza al boot

Tutti i microservizi includono le dipendenze `spring-retry` + `spring-boot-starter-aop` per evitare crash al boot se il Config Server o Eureka non sono ancora pronti quando il container parte (slide 58). La proprietà `spring.cloud.config.fail-fast=true` combinata con `spring.cloud.config.retry.*` regola il comportamento di retry.

---

## 7. Persistenza

### 7.1 Strategia generale

**Un database fisico (istanza MySQL), schema separato per provider.** Questa è la stessa strategia di Filippone (slide 11 e 49), estesa ai nostri quattro provider:

- Schema `anagrafe` → Anagrafe Service
- Schema `laboratorio` → Laboratorio Service
- Schema `farmacia` → Farmacia Service
- Schema `imaging` → Imaging Service

I prosumer non hanno persistenza.

### 7.2 Configurazione

Ogni provider ha nel proprio `*-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}/${MYSQL_DB}?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create
spring.sql.init.mode=always
```

Le variabili sono iniettate da docker-compose. Pattern dalla slide 54.

### 7.3 Dati iniziali

Ogni provider ha un `data.sql` in `src/main/resources/` con pazienti, esami, farmaci di esempio per i test di sistema. Slide 49: usare backtick (`` ` ``) per gli identificatori, non virgolette doppie.

---

## 8. Documentazione delle API

| Servizio | Tecnologia di contratto | Documentazione esposta |
|---|---|---|
| Anagrafe (SOAP) | WSDL contract-first | `GET /soap/anagrafe?wsdl` |
| Laboratorio (REST) | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |
| Farmacia (REST) | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |
| Imaging (REST) | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |
| Diagnostic Aggregator | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |
| Clinical Aggregator | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |
| Care Coordinator | springdoc-openapi | `/swagger-ui.html`, `/v3/api-docs` |

Dipendenza Maven da aggiungere a ogni servizio REST:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.x.x</version>
</dependency>
```

---

## 9. Containerization

### 9.1 Dockerfile per servizio

Ogni progetto Maven ha alla radice il proprio `Dockerfile`, identico nella struttura a quello della slide 51:

```dockerfile
FROM maven:3.9.2-eclipse-temurin-17-alpine

WORKDIR /service-name
COPY . .

RUN mvn clean install
CMD mvn spring-boot:run
```

### 9.2 docker-compose

Un unico `docker-compose.yml` alla radice del repository orchestrastraverso tutto. Estratto strutturale:

```yaml
services:

  healthsoa-mysql:
    image: mysql:8
    volumes:
      - mysql-data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
    networks:
      - healthsoa-network

  config-server:
    build: ./healthsoa-config-server
    ports:
      - "8888:8888"
    networks:
      - healthsoa-network

  discovery-server:
    build: ./healthsoa-discovery-server
    ports:
      - "8761:8761"
    depends_on:
      - config-server
    networks:
      - healthsoa-network

  gateway:
    build: ./healthsoa-gateway
    ports:
      - "9000:9000"
    environment:
      CONFIG_SERVER_HOST: config-server
      CONFIG_SERVER_PORT: 8888
      EUREKA_SERVER: http://discovery-server:8761/eureka/
    depends_on:
      - config-server
      - discovery-server
    networks:
      - healthsoa-network

  anagrafe-service:
    build: ./anagrafe-service
    expose:
      - "9101"
    environment:
      CONFIG_SERVER_HOST: config-server
      CONFIG_SERVER_PORT: 8888
      EUREKA_SERVER: http://discovery-server:8761/eureka/
      MYSQL_HOST: healthsoa-mysql:3306
      MYSQL_DB: anagrafe
      MYSQL_USER: root
      MYSQL_PASSWORD: root
    depends_on:
      - config-server
      - discovery-server
      - healthsoa-mysql
    networks:
      - healthsoa-network

  laboratorio-service:
    build: ./laboratorio-service
    expose:
      - "9102"
    environment:
      CONFIG_SERVER_HOST: config-server
      CONFIG_SERVER_PORT: 8888
      EUREKA_SERVER: http://discovery-server:8761/eureka/
      MYSQL_HOST: healthsoa-mysql:3306
      MYSQL_DB: laboratorio
      MYSQL_USER: root
      MYSQL_PASSWORD: root
    depends_on:
      - config-server
      - discovery-server
      - healthsoa-mysql
    networks:
      - healthsoa-network

  # ... farmacia-service, imaging-service analoghi
  # ... diagnostic-aggregator, clinical-aggregator, care-coordinator senza MySQL

networks:
  healthsoa-network:

volumes:
  mysql-data:
```

L'uso di `expose` invece di `ports` sui servizi replicabili (Anagrafe, Laboratorio, Imaging) permette il comando di scaling:

```bash
docker compose -p healthsoa up -d --scale laboratorio-service=3 --scale imaging-service=3 --scale anagrafe-service=2 --scale diagnostic-aggregator=2
```

Pattern dalla slide 58. Il gateway, raggiungendo questi servizi via `lb://`, distribuisce automaticamente le richieste tra le repliche.

### 9.3 Avvio sequenziale

`depends_on` garantisce l'ordine di partenza dei container, ma non la *readiness*. Spring Retry (sezione 6.3) compensa, consentendo ai client del config server di ritentare la connessione fino a quando questo è effettivamente disponibile.

---

## 10. Mapping vincoli della traccia → tecnologie

| Vincolo traccia | Tecnologia adottata | Riferimento slide |
|---|---|---|
| 1 — REST + SOAP + microservizi (CXF, Jakarta, Spring) | Spring Boot per tutti + Apache CXF per l'Anagrafe | Estensione |
| 2 — Gateway, prosumer, provider | Spring Cloud Gateway + microservizi Spring Boot | Slide 17-20 |
| 3 — Client→prosumer→provider | Feign con `@FeignClient(name=...)` | Slide 46-48 |
| 4 — Asincronia + parallelismo con sincronizzazione | `@Async` + callback nel Laboratorio; `CompletableFuture.allOf().join()` nel Coordinator | Estensione |
| 5 — 3 interazioni prosumer + 1 diretta | Route gateway distinte | Slide 20 |
| 6 — Diagramma + sequence | (Documentazione esterna) | — |
| 7 — Descrizione testuale | (Specifica applicativa + questo documento) | — |
| 8 — Commenti, WSDL, OpenAPI | WSDL contract-first + springdoc-openapi | Estensione |
| 9 — README di setup | (Documentazione esterna) | — |
| 10 — Spring Boot + Docker | Spring Boot + Dockerfile per servizio + docker-compose | Slide 51-57 |
| 11 — Maven | Progetti Maven indipendenti (uno per servizio) | Slide 10 |
| 12 — Archetype (opzionale) | **Non realizzato** (scelta di semplicità) | — |
| 13 — Multi-istanza + LB + discovery, motivati | Eureka + Spring Cloud LoadBalancer; scaling solo dei provider read-heavy (motivazione nel doc applicativo §1.1) | Slide 36-43 |

---

## 11. Convenzioni di porte e naming

Per coerenza e per facilitare il debug in ambiente di sviluppo locale.

| Servizio | Porta interna | Porta host (se mappata) |
|---|---|---|
| Config Server | 8888 | 8888 |
| Discovery Server | 8761 | 8761 |
| Gateway | 9000 | 9000 |
| Anagrafe Service (SOAP) | 9101 | range 9101-9110 quando scalato |
| Laboratorio Service | 9102 | range 9111-9120 quando scalato |
| Farmacia Service | 9103 | 9103 |
| Imaging Service | 9104 | range 9121-9130 quando scalato |
| Diagnostic Aggregator | 9201 | range 9131-9140 quando scalato |
| Clinical Aggregator | 9202 | 9202 |
| Care Coordinator | 9203 | 9203 |
| MySQL | 3306 | 3306 |

I nomi degli `spring.application.name` corrispondono ai nomi delle directory dei progetti Maven (`anagrafe-service`, `laboratorio-service`, ecc.). Questa coerenza è critica per il funzionamento di Eureka + Feign + `lb://`.

---

## 12. Cosa NON usiamo (e perché)

Per chiarezza in sede di difesa, elenco esplicito delle tecnologie scartate.

- **Gradle**: la traccia (vincolo 11) impone Maven esclusivamente.
- **Maven parent POM / multi-module**: scelta di semplicità, allineata al pattern delle slide che inizializzano ogni servizio come progetto indipendente.
- **Maven archetype**: opzionale per la traccia (vincolo 12), non realizzato per semplicità.
- **Lookup Eureka per il client SOAP**: il client JAX-WS dell'Anagrafe usa URL statica via Config Server. È la soluzione più semplice e replica il pattern di slide 28 (`microservice.user.find.uri`).
- **Redis o store esterno per lo stato asincrono**: il Diagnostic Aggregator usa un `trackingId` stateless (token Base64URL contenente `patientId:panelCode:labOrderId`) invece di una mappa in-memory. Questo elimina qualsiasi stato condiviso tra istanze senza richiedere un database aggiuntivo. Il Clinical Aggregator e il Care Coordinator sono invece singola istanza per design: il Clinical dipende da un endpoint SOAP con URL statica (non load-balanceable via Eureka), e il Care Coordinator mantiene la logica di composizione parallela stateless ma non è un collo di bottiglia (è chiamato una volta per sessione clinica).
- **Multi-stage Dockerfile**: i `Dockerfile` sono single-stage `mvn clean install` + `mvn spring-boot:run`, esattamente come nelle slide 51 e 55.
- **Spring WebFlux / reactive stack**: stack imperativo standard (Spring Web MVC + Feign sincrono) come nelle slide. Il parallelismo del Care Coordinator si ottiene a livello di applicazione con `CompletableFuture` su un executor, senza introdurre un modello reattivo end-to-end.
- **Kubernetes**: la scalabilità richiesta dalla traccia è dimostrata con `docker compose --scale`, sufficiente per gli obiettivi didattici.
