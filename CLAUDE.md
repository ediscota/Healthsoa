# CLAUDE.md — Regole operative per HealthSOA

Questo file è il contesto operativo del progetto. Va letto ad ogni inizio di sessione, **prima** di qualunque modifica al codice.

---

## Contesto del progetto

HealthSOA è un'applicazione a microservizi del dominio sanitario, sviluppata come progetto d'esame del corso *Service-oriented Software Engineering* (a.a. 2025/2026, Università dell'Aquila).

Lo scopo del sistema, gli use case, il modello dei dati e le scelte architetturali sono interamente specificati nei documenti in `docs/`. Non sono ripetuti qui. Non improvvisare requisiti che non siano in quei documenti.

## Documenti di specifica (lettura obbligatoria a inizio sessione)

- `docs/specifica_applicativa.md` — visione, attori, servizi, use case, modello dati, logica di rischio.
- `docs/architettura_tecnologica.md` — stack, configurazione, persistenza, containerization, mapping vincoli→tecnologie.

Prima di iniziare qualunque task, leggi la sezione pertinente di entrambi i documenti. Se quello che ti viene chiesto non è in specifica, **fermati e chiedi**, non inventare.

---

## Stack pinnato (non negoziabile)

| Componente | Versione esatta |
|---|---|
| Java | 17 |
| Apache Maven | 3.9.x |
| Spring Boot | 3.2.x |
| Spring Cloud | 2023.0.x |
| Apache CXF (solo Anagrafe e Clinical Aggregator) | 4.0.x |
| springdoc-openapi (solo servizi REST) | 2.3.x |
| MySQL connector | quello allineato a MySQL 8 |
| Resilience4j (Spring Boot starter) | 2.2.x |

**Non aggiornare a versioni più recenti** anche se sembrano disponibili. Lo stack è stato fissato per coerenza con il materiale didattico del corso. Se una dipendenza dichiarata in uno dei `pom.xml` esistenti del progetto ha già una versione, **usa quella**, non sostituirla.

---

## Convenzioni di naming (critiche per il funzionamento)

Per ogni microservizio devono coincidere esattamente:

1. Il nome della **cartella** del progetto (es. `laboratorio-service`)
2. Il valore di `spring.application.name` nelle properties
3. Il nome usato dal gateway in `uri: lb://...`
4. Il nome usato in `@FeignClient(name="...")` dai consumer

Se questi quattro nomi non sono identici, il discovery e il load balancing non funzionano. È la causa #1 di guasti silenziosi.

### Nomi ufficiali

```
healthsoa-config-server
healthsoa-discovery-server
healthsoa-gateway
anagrafe-service          (SOAP)
laboratorio-service       (REST async)
farmacia-service          (REST)
imaging-service           (REST async)
diagnostic-aggregator
clinical-aggregator
care-coordinator
```

### Porte standard

| Servizio | Porta |
|---|---|
| Config Server | 8888 |
| Discovery Server | 8761 |
| Gateway | 9000 |
| Anagrafe | 9101 |
| Laboratorio | 9102 |
| Farmacia | 9103 |
| Imaging | 9104 |
| Diagnostic Aggregator | 9201 |
| Clinical Aggregator | 9202 |
| Care Coordinator | 9203 |
| MySQL | 3306 |

---

## Struttura standard di un microservizio

Ogni microservizio è un **progetto Maven indipendente** (niente parent POM, niente multi-module — scelta esplicita allineata al materiale del corso). La struttura tipica è:

```
nome-servizio/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/it/disim/univaq/sose/healthsoa/<service>/
    │   ├── ServiceApplication.java       (con @EnableDiscoveryClient e altre annotazioni)
    │   ├── controller/                    (solo per servizi REST)
    │   ├── endpoint/                      (solo per servizi SOAP)
    │   ├── service/
    │   ├── repository/                    (solo se ha persistenza)
    │   ├── model/
    │   ├── dto/
    │   └── client/                        (solo per prosumer: Feign client, JAX-WS client)
    └── resources/
        ├── application.properties         (minimale: solo name, profile, config import)
        └── data.sql                        (se ha persistenza)
```

Il `application.properties` locale contiene **solo** queste tre proprietà:

```properties
spring.application.name=nome-servizio
spring.profiles.active=dev
spring.config.import=optional:configserver:http://${CONFIG_SERVER_HOST:localhost}:${CONFIG_SERVER_PORT:8888}
```

**Tutto il resto** (porta, datasource, eureka, valori di business) sta nel file `nome-servizio-dev.properties` del repository di configurazione (`../healthsoa-properties-repository`), letto dal Config Server.

---

## Pattern obbligatori

Riferimenti puntuali al materiale del corso (slide di Filippone, *Develop microservices with Spring Boot*).

- **Inter-service REST**: Feign con discovery, `@FeignClient(name="...")` senza URL. Pattern di slide 47-48.
- **Gateway routing**: `lb://nome-servizio` nelle route, mai URL hardcodate. Pattern di slide 43.
- **Config refresh a runtime**: bean con `@RefreshScope`, esposizione di `/actuator/refresh`. Pattern di slide 31.
- **Boot resiliente**: `spring-retry` + `spring-boot-starter-aop` su ogni client del config server, per gestire l'ordine di startup in docker-compose. Slide 58.
- **Dockerfile**: single-stage, base `maven:3.9.2-eclipse-temurin-17-alpine`, `RUN mvn clean install` + `CMD mvn spring-boot:run`. Slide 51 e 55. Non usare multi-stage.
- **OpenAPI**: tutti i servizi REST espongono `/swagger-ui.html` via `springdoc-openapi-starter-webmvc-ui`.
- **SOAP**: contract-first, WSDL in `src/main/resources/wsdl/`, generazione classi via `cxf-codegen-plugin` al `mvn generate-sources`.
- **Composizione parallela** (solo Care Coordinator): `CompletableFuture.supplyAsync()` su un `ThreadPoolTaskExecutor` Spring Bean + `CompletableFuture.allOf().join()` come barriera di sincronizzazione. Implementazione documentata nella specifica tecnologica §5.3.
- **Asincronia** (solo Laboratorio e Imaging): `@Async` con `@EnableAsync`, polling endpoint + webhook callback. Implementazione documentata nella specifica tecnologica §4.2.

---

## Cosa NON fare mai

Queste regole sono assolute. Se hai dubbi, **non farlo**.

- **Non aggiungere feature non in specifica.** Se la specifica non menziona autenticazione, non aggiungerla. Se non menziona caching, non aggiungerlo. Niente "miglioramenti spontanei".
- **Non scrivere test JUnit, Mockito, integration test** a meno che non venga esplicitamente richiesto in quella sessione. La validazione del progetto avviene con test manuali via `curl` e via il client web.
- **Non aggiungere Lombok**, anche se è comodo. Codice Java esplicito con getter/setter generati dall'IDE.
- **Non usare Spring WebFlux / reactive**. Lo stack è imperativo (`spring-boot-starter-web` + Feign sincrono). Il parallelismo nel Care Coordinator è a livello applicativo con `CompletableFuture`, non reattivo.
- **Non aggiungere Spring Security.** Non è in scope.
- **Non aggiungere Spring Cloud Sleuth, Zipkin, Micrometer Tracing** o altri tool di osservabilità avanzata. Solo `actuator` con gli endpoint base (`health`, `info`, `refresh`).
- **Non creare un parent POM Maven** o struttura multi-module. Ogni servizio è un progetto Maven indipendente.
- **Non hardcodare indirizzi** di config server, Eureka, MySQL, Anagrafe SOAP. Tutto via variabili d'ambiente nel docker-compose o via properties del Config Server.
- **Non sostituire MySQL con H2** "per semplicità" dopo che è già stato configurato MySQL. H2 si rompe con multi-istanza (slide 49).
- **Non usare Redis** o database esterni per lo stato del Diagnostic Aggregator. `ConcurrentHashMap` in-memory, prosumer in singola istanza. Scelta documentata nella specifica tecnologica §5.1 e §12.
- **Non inventare endpoint** che non siano nella specifica applicativa.
- **Non modificare i due file in `docs/`** durante una sessione di coding. Se la specifica va aggiornata, è un task a sé.

---

## Workflow di una sessione

1. **Leggi** la sezione pertinente di `docs/specifica_applicativa.md` e `docs/architettura_tecnologica.md`.
2. **Conferma** in 3-4 righe cosa stai per implementare, quali file creerai/modificherai, quale è il criterio di "fatto".
3. **Implementa solo quello.** Niente refactor di codice esistente non chiesto, niente abbellimenti.
4. **Verifica** che il servizio compili (`mvn clean install`) e parta. Se ha un endpoint, fornisci all'utente il comando `curl` di smoke test.
5. **Fermati** e segnala all'utente cosa va testato manualmente. Non procedere oltre senza conferma.

Una sessione = un servizio (o un'aggiunta delimitata). Non implementare due microservizi nella stessa sessione.

---

## Git

- Branch principale: `main`.
- Branch di lavoro per persona/feature: nominati con prefisso `feat/` (es. `feat/anagrafe-service`).
- Commit messaggi in inglese, imperativi, brevi (es. `add diagnostic aggregator skeleton`).
- **Non eseguire `git push`** automaticamente. Lascia che l'utente lo faccia esplicitamente.
- Mai forzare un push, mai riscrivere la history di branch condivisi.

---

## Repository delle properties

È un repository git **separato** dal repository del codice, posizionato come cartella sorella:

```
~/progetti/
├── healthsoa/                              ← questo repo
└── healthsoa-properties-repository/        ← repo git separato delle properties
```

Il Config Server in dev legge da `file://${user.home}/progetti/healthsoa-properties-repository` (o equivalente). I nomi dei file devono seguire lo schema `<nome-servizio>-dev.properties` (es. `laboratorio-service-dev.properties`).

Non spostare le properties dentro questo repository.
