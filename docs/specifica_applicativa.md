# HealthSOA — Specifica Applicativa

**Piattaforma ospedaliera di supporto alla valutazione clinica pre-procedura**

---

## 1. Visione del sistema

HealthSOA è una piattaforma orientata ai servizi che supporta il personale clinico nella raccolta e nell'analisi integrata delle informazioni di un paziente prima di una procedura medica (intervento chirurgico, ricovero programmato, esame invasivo).

Dato un codice fiscale o un identificativo paziente, il sistema produce una **valutazione clinica completa**: anagrafica e storico patologico, terapie farmacologiche attive, allergie note, esiti degli esami di laboratorio più recenti e referti di diagnostica per immagini. I dati vengono incrociati per rilevare automaticamente situazioni di rischio — ad esempio un farmaco nefrotossico in corso di somministrazione a fronte di un valore di creatinina fuori range — e presentati al clinico sotto forma di un report strutturato con eventuali flag di attenzione.

### 1.1 Perché questo dominio

Il contesto sanitario giustifica in modo non artificiale tutte le scelte architetturali richieste dalla traccia.

**SOAP**: l'Anagrafe Sanitaria Regionale è il tipico sistema istituzionale legacy che, nel mondo reale, espone contratti tipizzati in stile HL7 via WSDL. L'uso di SOAP con approccio *contract-first* è imposto dall'esigenza di interoperabilità con sistemi già esistenti, non da una scelta estetica.

**Asincronia**: un pannello di laboratorio ordinato d'urgenza ha una latenza reale dovuta alla lavorazione del campione biologico. Una chiamata sincrona terrebbe occupati thread e connessioni mentre il clinico, nello stesso reparto, ordina altri esami. L'asincronia è imposta dalla fisica del problema.

**Parallelismo**: una valutazione di idoneità richiede contemporaneamente il bundle diagnostico (laboratorio + imaging) e il profilo storico-clinico (anagrafe + terapie + allergie). Le due raccolte sono logicamente indipendenti; eseguirle in sequenza raddoppierebbe inutilmente la latenza di una decisione time-critical.

**Scaling**: Anagrafe e Laboratorio si trovano sul percorso critico di ogni workflow e vengono colpiti da molti clinici in contemporanea durante le finestre di picco (giro visite mattutino, afflussi al pronto soccorso). Il profilo di carico è *bursty* e *read-heavy*, quindi la replica in più istanze è motivata dal comportamento specifico del sistema.

---

## 2. Attori

| Attore | Tipo | Descrizione |
|---|---|---|
| Medico | Utente primario | Avvia valutazioni, consulta report, ordina esami |
| Infermiere | Utente secondario | Consulta storie cliniche, verifica terapie attive |
| Anagrafe Sanitaria Regionale | Sistema esterno simulato | Fonte dei dati demografici e dello storico patologico |
| LIS (Laboratory Information System) | Sistema esterno simulato | Gestisce gli ordini e gli esiti degli esami di laboratorio |
| RIS/PACS (sistema di imaging) | Sistema esterno simulato | Archivia e restituisce referti di diagnostica per immagini |
| Sistema di Farmacia | Sistema interno | Gestisce le prescrizioni attive e le interazioni farmacologiche |

---

## 3. Inventario dei servizi

Il sistema è composto da quattro **service provider**, tre **service prosumer** e un'**applicazione client**. A questi si aggiungono i servizi infrastrutturali (API Gateway, Service Discovery, Load Balancer) che non sono servizi di business ma di supporto architetturale.

### 3.1 Service provider

#### Provider 1 — Anagrafe Pazienti (SOAP)

Espone i dati anagrafici, lo storico patologico e le allergie note di un paziente. Rappresenta il punto di integrazione con il sistema istituzionale legacy. Il contratto è definito tramite WSDL con approccio *contract-first*.

Operazioni principali:

- `getPatientById(patientId)` → dati anagrafici completi
- `getMedicalHistory(patientId)` → lista di diagnosi e ricoveri pregressi
- `getAllergies(patientId)` → allergie note con tipo di reazione

#### Provider 2 — Laboratorio Analisi (REST, asincrono)

Gestisce gli ordini di esami di laboratorio e la restituzione degli esiti. L'interazione è **asincrona**: l'ordine viene accettato immediatamente con un `202 Accepted`; l'esito è recuperabile tramite polling sullo stato o tramite un meccanismo di callback/webhook.

Operazioni principali:

- `POST /tests/orders` → crea un ordine; restituisce `orderId` e `202 Accepted`
- `GET /tests/orders/{orderId}/status` → stato corrente dell'ordine (PENDING, PROCESSING, COMPLETED, ERROR)
- `GET /tests/orders/{orderId}/result` → esito completo (disponibile solo in stato COMPLETED)
- `POST /tests/orders/{orderId}/callback` → registra una URL di callback per la notifica automatica al completamento

Il servizio è replicabile in più istanze per assorbire i picchi di richieste.

#### Provider 3 — Farmacia / Prescrizioni (REST)

Espone le terapie farmacologiche attive di un paziente e offre un endpoint per la verifica di potenziali interazioni tra farmaci.

Operazioni principali:

- `GET /patients/{patientId}/prescriptions` → lista delle prescrizioni attive con dosaggio e durata
- `POST /interactions/check` → riceve una lista di farmaci e restituisce eventuali interazioni rilevate

#### Provider 4 — Diagnostica per Immagini (REST, asincrono)

Gestisce le richieste di referti radiologici e di imaging. Il modello di interazione è analogo a quello del Laboratorio: richiesta con risposta asincrona, polling o callback.

Operazioni principali:

- `GET /patients/{patientId}/reports` → lista dei referti archiviati per un paziente
- `GET /reports/{reportId}` → referto completo con descrizione del radiologo

Il servizio è replicabile in più istanze.

---

### 3.2 Service prosumer

I prosumer consumano uno o più provider e compongono i risultati per offrire al livello superiore (client o coordinator) un'interfaccia di granularità più alta.

#### Prosumer 1 — Aggregatore Diagnostico

Consuma il **Laboratorio** (P2) e la **Diagnostica per Immagini** (P4). Si occupa di coordinare le richieste diagnostiche, gestire il ciclo di vita degli ordini asincroni e restituire un bundle diagnostico unificato.

Responsabilità:

- Ordinare esami di laboratorio e monitorarne l'esito (polling o ricezione callback)
- Recuperare i referti di imaging disponibili per il paziente
- Aggregare i due flussi in un unico oggetto `DiagnosticBundle`

#### Prosumer 2 — Aggregatore Storico-Clinico

Consuma l'**Anagrafe Pazienti** (P1, SOAP) e la **Farmacia** (P3, REST). Funge da bridge tra il protocollo SOAP del sistema legacy e il resto dell'architettura REST. Compone il profilo completo del paziente dal punto di vista clinico-anamnestico.

Responsabilità:

- Tradurre la risposta SOAP dell'Anagrafe in un formato REST uniforme
- Integrare i dati anagrafici e lo storico con le prescrizioni attive
- Restituire un oggetto `ClinicalProfile` contenente anagrafica, storia, allergie e terapie

#### Prosumer 3 — Care Coordinator

È il prosumer di orchestrazione. Consuma **Aggregatore Diagnostico** (PR1) e **Aggregatore Storico-Clinico** (PR2), invocandoli **in parallelo**, attendendo il completamento di entrambi (barriera di sincronizzazione) e incrociando i due risultati per produrre il report finale di idoneità.

Responsabilità:

- Lanciare in concorrenza le due chiamate verso PR1 e PR2
- Attendere entrambe le risposte (barriera sincrona)
- Eseguire la logica di analisi del rischio (es. controindicazioni farmaco/laboratorio)
- Restituire al client un oggetto `FitnessReport` con esito sintetico e flag di rischio motivati

Il parallelismo è giustificato dalla logica: le due raccolte dati sono indipendenti e il passo di incrocio richiede *entrambi* i risultati prima di poter procedere. La barriera di sincronizzazione è quindi intrinseca al dominio.

---

### 3.3 Client

#### Applicazione web clinica

Interfaccia web fruita da medici e infermieri. Tutta la comunicazione con i servizi di back-end transita obbligatoriamente attraverso l'**API Gateway**, che rappresenta il punto unico di ingresso per il client.

---

## 4. Use case e scenari di interazione

La traccia richiede almeno tre interazioni distinte client→prosumer e almeno una interazione diretta client→provider.

### UC-1 — Valutazione clinica completa

**Attore**: Medico  
**Prosumer coinvolto**: Care Coordinator (PR3)

Il medico richiede la valutazione di idoneità completa di un paziente. Il Care Coordinator riceve la richiesta e:

1. Invoca in parallelo l'Aggregatore Diagnostico (PR1) e l'Aggregatore Storico-Clinico (PR2).
2. Attende il completamento di entrambi (barriera di sincronizzazione).
3. Incrocia i due bundle: confronta i valori di laboratorio con le terapie attive e le allergie note.
4. Produce e restituisce un `FitnessReport` con esito globale (IDONEO / CON RISERVA / NON IDONEO) e lista dei flag di rischio.

Questo scenario soddisfa il vincolo 4 della traccia: due prosumer eseguono in parallelo e si sincronizzano prima che il coordinator risponda al client.

### UC-2 — Consultazione storico-clinico

**Attore**: Medico o infermiere  
**Prosumer coinvolto**: Aggregatore Storico-Clinico (PR2)

Il clinico consulta il profilo anamnestico di un paziente senza richiedere una valutazione completa. L'Aggregatore Storico-Clinico recupera i dati dall'Anagrafe (SOAP) e dalla Farmacia (REST), li compone e restituisce un `ClinicalProfile`. Questo scenario mette in mostra l'integrazione SOAP↔REST all'interno di un prosumer.

### UC-3 — Richiesta esami e monitoraggio

**Attore**: Medico  
**Prosumer coinvolto**: Aggregatore Diagnostico (PR1)

Il medico ordina uno o più esami di laboratorio per un paziente. L'Aggregatore Diagnostico inoltro l'ordine al Laboratorio, che risponde con `202 Accepted`. Il prosumer monitora l'esito tramite:

- **Polling**: interroga periodicamente lo stato dell'ordine fino al completamento, oppure
- **Callback**: registra una webhook sul Laboratorio; quando l'esito è pronto il Laboratorio notifica il prosumer, che aggiorna il proprio stato interno e rende disponibile il risultato.

Il client può interrogare il prosumer per conoscere lo stato della richiesta e, a completamento, ottenere l'esito. Questo scenario illustra il pattern *Asynchronous Request-Reply* end-to-end.

### UC-4 — Verifica interazioni farmacologiche (interazione diretta)

**Attore**: Medico  
**Provider coinvolto**: Farmacia (P3) — accesso diretto tramite API Gateway

Il medico verifica rapidamente se una nuova prescrizione è compatibile con le terapie già in corso. La richiesta parte dal client, transita dal Gateway e arriva direttamente al provider Farmacia, senza passare per un prosumer. Soddisfa il vincolo 5 della traccia ("il client può anche interagire direttamente con i provider, se necessario").

---

## 5. Modello dei dati principali

Le strutture seguenti definiscono il contratto semantico tra i servizi; la loro serializzazione (XML per SOAP, JSON per REST) è dettagliata nella specifica tecnica.

**Patient** — identificativo, codice fiscale, nome, data di nascita, sesso, recapito.

**MedicalHistory** — lista di `Condition` (codice ICD-10, descrizione, data di insorgenza, stato attivo/risolto).

**Allergy** — sostanza allergizzante, tipo di reazione (lieve / grave / anafilattica), data di rilevamento.

**Prescription** — farmaco (nome generico, codice ATC), dosaggio, frequenza, data inizio, data fine presunta, medico prescrittore.

**TestOrder** — identificativo ordine, codice esame, stato (PENDING / PROCESSING / COMPLETED / ERROR), timestamp creazione e aggiornamento.

**TestResult** — riferimento all'ordine, lista di `Measurement` (parametro, valore, unità di misura, range di riferimento, flag di anomalia).

**ImagingReport** — identificativo referto, data, tipo di esame, corpo referto in testo libero, medico refertante.

**DiagnosticBundle** — composizione di una lista di `TestResult` e una lista di `ImagingReport`.

**ClinicalProfile** — composizione di `Patient`, lista di `Condition`, lista di `Allergy`, lista di `Prescription`.

**RiskFlag** — tipo di rischio (CONTROINDICAZIONE_FARMACO / VALORE_CRITICO / ALLERGIA_RILEVANTE / ALTRO), descrizione testuale, livello di severità (INFO / WARNING / CRITICAL), riferimento ai dati sorgente che hanno generato il flag.

**FitnessReport** — esito sintetico (IDONEO / CON_RISERVA / NON_IDONEO), paziente di riferimento, timestamp di generazione, `ClinicalProfile`, `DiagnosticBundle`, lista di `RiskFlag`.

---

## 6. Logica di rilevamento del rischio

Il Care Coordinator applica le seguenti regole dopo aver ricevuto entrambi i bundle:

- **Controindicazione farmaco/funzionalità renale**: se una prescrizione attiva contiene un farmaco contrassegnato come nefrotossico (flag nel catalogo farmaci) e il valore di creatinina sierica è superiore alla soglia di riferimento, viene emesso un `RiskFlag` di livello CRITICAL con riferimento ai dati sorgente.
- **Controindicazione farmaco/allergia**: se una prescrizione attiva contiene una sostanza presente nell'elenco delle allergie del paziente, viene emesso un `RiskFlag` di livello CRITICAL.
- **Valore di laboratorio critico**: qualsiasi `Measurement` con flag di anomalia attivo e valore al di fuori del doppio del range di riferimento genera un `RiskFlag` di livello WARNING o CRITICAL in base alla distanza dalla soglia.
- **Nessun rischio rilevato**: il report viene emesso con esito IDONEO e lista vuota di flag.

La logica è intenzionalmente separata dai singoli provider per preservare il principio di separazione delle responsabilità: ogni provider conosce solo il proprio dominio (laboratorio, farmacia, anagrafe); il ragionamento clinico integrato appartiene al livello di orchestrazione.

---

## 7. Scenari di interazione — sequence diagram (descrizione)

I diagrammi di sequenza formali (PlantUML) sono prodotti come documento separato. Di seguito la descrizione narrativa dei flussi principali.

### UC-1 Sequence

```
Client → Gateway → Care Coordinator
Care Coordinator →‖ Aggregatore Diagnostico  (parallelo)
Care Coordinator →‖ Aggregatore Storico-Clinico  (parallelo)
  Aggregatore Diagnostico → Laboratorio (async: POST order)
  Aggregatore Diagnostico → Laboratorio: GET /tests/orders/{orderId}/status (polling)
  Aggregatore Diagnostico → Imaging (GET reports)
  Aggregatore Storico-Clinico → Anagrafe SOAP (getPatientById, getMedicalHistory, getAllergies)
  Aggregatore Storico-Clinico → Farmacia REST (getPrescriptions)
‖ (barriera: entrambi completati)
Care Coordinator → logica di analisi del rischio
Care Coordinator → Client: FitnessReport
```

### UC-2 Sequence

```
Client → Gateway → Aggregatore Storico-Clinico
  Aggregatore Storico-Clinico → Anagrafe SOAP: getPatientById
  Aggregatore Storico-Clinico → Anagrafe SOAP: getMedicalHistory
  Aggregatore Storico-Clinico → Anagrafe SOAP: getAllergies
  Aggregatore Storico-Clinico → Farmacia REST: getPrescriptions
Aggregatore Storico-Clinico → Client: ClinicalProfile
```

### UC-3 Sequence

```
Client → Gateway → Aggregatore Diagnostico: orderTests(panelCode)
  Aggregatore Diagnostico → Laboratorio: POST /tests/orders (202 Accepted)
  Aggregatore Diagnostico → Client: 202 Accepted + trackingId
--- (intervallo di elaborazione) ---
[polling]
  Client → Aggregatore Diagnostico: GET /tracking/{trackingId}/status
  Aggregatore Diagnostico decodifica trackingId → recupera orderId
  Aggregatore Diagnostico → Laboratorio: GET /tests/orders/{orderId}/status
  Aggregatore Diagnostico → Client: stato corrente
--- (quando stato = COMPLETED) ---
  Client → Aggregatore Diagnostico: GET /tracking/{trackingId}/result
  Aggregatore Diagnostico → Laboratorio: GET /tests/orders/{orderId}/result
  Aggregatore Diagnostico → Client: DiagnosticBundle
```

### UC-4 Sequence

```
Client → Gateway → Farmacia REST: POST /interactions/check
Farmacia REST → Client: lista interazioni rilevate
```

---

## 8. Vincoli architetturali e pattern applicati

Questa sezione mappa esplicitamente i vincoli della traccia alle scelte progettuali.

| Vincolo | Requisito | Soluzione adottata |
|---|---|---|
| 1 | REST + SOAP + microservizi | P1 espone SOAP; P2, P3, P4 e i prosumer espongono REST |
| 2 | ≥3 provider, ≥2 prosumer, ≥1 client, gateway | 4 provider, 3 prosumer, 1 client; API Gateway come punto unico d'ingresso |
| 3 | Client→prosumer; prosumer→≥2 provider | PR1 consuma P2+P4; PR2 consuma P1+P3; PR3 consuma PR1+PR2 |
| 4 | Servizio asincrono + ≥2 prosumer paralleli con sincronizzazione | P2 è asincrono (ordine/polling/callback); UC-1: PR3 invoca PR1 e PR2 in parallelo e sincronizza |
| 5 | ≥3 interazioni client→prosumer + interazione diretta | UC-1 (PR3), UC-2 (PR2), UC-3 (PR1), UC-4 (P3 diretto) |
| 6 | Diagramma architetturale + sequence per scenario | Diagramma a componenti (sezione 3) + sequence narrativi (sezione 7) |
| 7 | Documentazione testuale | Questo documento |
| 8 | Commenti codice, WSDL, OpenAPI/Swagger | WSDL per P1; OpenAPI per P2, P3, P4 e i prosumer |
| 9 | README di setup | README con istruzioni di avvio via docker-compose |
| 10 | Spring Boot + Docker | Tutti i microservizi containerizzati con Docker |
| 11 | Maven | Progetto multi-modulo Maven con parent POM |
| 12 | Archetype Maven (opzionale) | Archetype per la struttura standard dei moduli di servizio |
| 13 | Multi-istanza + LB + discovery, motivati | P2 e P4 replicati per i picchi di ordini; P1 replicato per il profilo read-heavy; scaling motivato dal profilo di carico specifico |

### Pattern di integrazione applicati

**API Gateway**: punto unico di ingresso per il client; gestisce routing, autenticazione e, per i servizi replicati, load balancing.

**Service Registry / Discovery**: i servizi si registrano all'avvio; il gateway e i prosumer risolvono gli indirizzi dinamicamente.

**API Composition / Aggregator**: PR1 e PR2 implementano questo pattern componendo i dati di due provider distinti in un'unica risposta di granularità maggiore.

**Asynchronous Request-Reply**: il Laboratorio accetta un ordine con `202 Accepted` e restituisce il risultato in un momento successivo; il consumer sceglie tra polling e callback/webhook.

**Circuit Breaker**: il Care Coordinator applica un circuit breaker sulle chiamate verso PR1 e PR2; se un aggregatore non risponde entro il timeout configurato, il coordinator può restituire un report parziale con indicazione dei dati mancanti invece di bloccarsi indefinitamente.

**Pipes and Filters**: la catena Client → Gateway → Prosumer → Provider può essere vista come una pipeline in cui ogni componente trasforma o arricchisce i dati prima di passarli al successivo.

---

## 9. Responsabilità dei componenti — riepilogo

| Componente | Responsabilità esclusiva | Confini |
|---|---|---|
| P1 Anagrafe | Dati demografici, storia, allergie | Non conosce farmaci, lab, imaging |
| P2 Laboratorio | Gestione ordini ed esiti di laboratorio | Non conosce paziente, farmaci, imaging |
| P3 Farmacia | Prescrizioni attive, controllo interazioni | Non conosce storia, lab, imaging |
| P4 Imaging | Referti radiologici e di imaging | Non conosce paziente, farmaci, lab |
| PR1 Diagnostico | Composizione Lab + Imaging | Non conosce storia clinica e farmaci |
| PR2 Storico-clinico | Composizione Anagrafe + Farmacia | Non conosce lab e imaging |
| PR3 Care Coordinator | Orchestrazione parallela, logica di rischio | Non accede direttamente ai provider |
| Gateway | Routing, entry point unico, LB | Nessuna logica di business |

La separazione delle responsabilità garantisce che la logica di analisi del rischio risieda esclusivamente nel Care Coordinator, l'unico componente che detiene la visione completa del paziente. Ogni provider e ogni aggregatore conosce solo il proprio dominio.

---

## 10. Glossario

| Termine | Definizione |
|---|---|
| LIS | Laboratory Information System: sistema informatico per la gestione degli ordini e degli esiti di laboratorio |
| RIS/PACS | Radiology Information System / Picture Archiving and Communication System: sistemi per la gestione dei referti e delle immagini diagnostiche |
| WSDL | Web Services Description Language: linguaggio XML per la descrizione di un servizio SOAP |
| ICD-10 | International Classification of Diseases, 10a revisione: sistema di codifica delle diagnosi |
| ATC | Anatomical Therapeutic Chemical: codice di classificazione dei farmaci |
| Prosumer | Componente che è al tempo stesso service consumer (consuma servizi di altri) e service provider (espone un'interfaccia a componenti superiori) |
| Bundle | Oggetto aggregato che raccoglie in un'unica struttura dati provenienti da fonti diverse |
| Flag di rischio | Segnalazione automatica generata dall'incrocio di informazioni cliniche, con livello di severità e riferimento ai dati sorgente |
| Barriera di sincronizzazione | Punto nel flusso di esecuzione in cui un componente attende il completamento di tutte le operazioni parallele avviate prima di procedere |
