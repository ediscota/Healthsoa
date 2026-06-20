# HealthSOA

A microservices platform for supporting pre-procedure clinical assessment.

Project developed for the *Service-oriented Software Engineering* course — academic year 2025/2026, University of L'Aquila.

---

## Documentation

| Document | Contents |
|---|---|
| [`docs/specifica_applicativa.md`](docs/specifica_applicativa.md) | System vision, actors, services, use cases, data model |
| [`docs/architettura_tecnologica.md`](docs/architettura_tecnologica.md) | Stack, configuration, persistence, containerization |
| [`docs/diagrams/`](docs/diagrams/) | PlantUML diagrams (component diagram + sequence diagrams for UC-1…UC-4) |

---

## Prerequisites

Ensure the following tools are installed before starting the system:

| Tool | Minimum version | Check |
|---|---|---|
| Docker Desktop | 24.x | `docker --version` |
| Docker Compose | 2.x (bundled plugin) | `docker compose version` |
| Git | any | `git --version` |

Java and Maven are **not** required on the host machine: each service is compiled inside its own Docker container during the build step.

---

## Repository structure

```
healthsoa/                              ← this repository
├── anagrafe-service/
├── laboratorio-service/
├── farmacia-service/
├── imaging-service/
├── diagnostic-aggregator/
├── clinical-aggregator/
├── care-coordinator/
├── healthsoa-config-server/
├── healthsoa-discovery-server/
├── healthsoa-gateway/
├── healthsoa-client/
├── healthsoa-properties-repository/    ← git repository of externalized properties
├── docker-compose.yml
└── README.md
```

The Config Server reads its configuration from `healthsoa-properties-repository/`, which is mounted as a read-only volume by `docker-compose.yml`. This folder must exist as a sibling of (or be included inside) this repository before starting the system.

---

## Quick start

```bash
# 1. Clone the repository (if not already done)
git clone <repository-url> healthsoa
cd healthsoa

# 2. Build all images and start all containers
docker compose up --build
```

The first build takes several minutes: Maven downloads all dependencies and compiles each service inside the container. Subsequent starts (without `--build`) are significantly faster.

To run in the background:

```bash
docker compose up --build -d
```

---

## Startup order and dependencies

Docker Compose manages the startup order automatically via `depends_on` with `condition: service_healthy`. The effective order is:

```
MySQL  ──▶  Config Server  ──▶  Discovery Server (Eureka)
                                        │
               ┌────────────────────────┼─────────────────────────┐
               ▼                        ▼                          ▼
         API Gateway           Anagrafe Service           Farmacia Service
         Laboratorio Service   Imaging Service            Diagnostic Aggregator
         Clinical Aggregator   Care Coordinator           Web Client
```

Services that use a database add MySQL as an additional dependency.

---

## Host-exposed ports

| Service | Host port | Notes |
|---|---|---|
| Web Client | **8080** | Clinical web interface (open in browser) |
| API Gateway | **9000** | Single entry point for all client requests |
| Config Server | 8888 | Centralized configuration |
| Discovery Server (Eureka) | 8761 | Service registry — dashboard at `http://localhost:8761` |
| Farmacia Service | 9103 | Direct access for smoke tests |
| Clinical Aggregator | 9202 | Direct access for smoke tests |
| Care Coordinator | 9203 | Direct access for smoke tests |
| MySQL | 3306 | Database (direct access for debugging) |

Anagrafe, Laboratorio, Imaging and Diagnostic Aggregator use `expose` instead of `ports`: they are reachable only on the internal Docker network and are never exposed directly to the host — all external traffic goes through the Gateway.

---

## Verifying the system is up

Wait until all containers reach the `healthy` / `running` state, then run:

```bash
# 1. Config Server health
curl http://localhost:8888/actuator/health

# 2. Eureka — verify all services have registered
curl http://localhost:8761/eureka/apps

# 3. API Gateway — end-to-end smoke test
curl http://localhost:9000/api/coordinator/actuator/health
```

Alternatively, open the **Eureka dashboard** in a browser:

```
http://localhost:8761
```

All ten business microservices must appear with status `UP`.

---

## Web client

Open in a browser:

```
http://localhost:8080
```

The interface provides four panels, one per use case:

| Panel | Use case | Prosumer / Provider involved |
|---|---|---|
| Full Clinical Assessment | UC-1 | Care Coordinator → Diagnostic Aggregator + Clinical Aggregator (parallel) |
| Clinical Profile | UC-2 | Clinical Aggregator → Anagrafe (SOAP) + Farmacia (REST) |
| Lab Panel Order | UC-3 | Diagnostic Aggregator → Laboratorio (asynchronous) |
| New Prescription | UC-4 | Farmacia Service — direct client→provider interaction |

---

## Smoke tests via curl

### UC-1 — Full clinical assessment

```bash
curl -s http://localhost:9000/api/coordinator/patients/1/assessment | jq .
```

### UC-2 — Clinical history profile

```bash
curl -s http://localhost:9000/api/clinical/patients/1/profile | jq .
```

### UC-3 — Lab panel order (asynchronous)

```bash
# Submit order — returns 202 Accepted with a trackingId
curl -s -X POST http://localhost:9000/api/diagnostic/patients/1/panels/CBC | jq .

# Poll for status (replace <trackingId> with the value returned above)
curl -s http://localhost:9000/api/diagnostic/tracking/<trackingId>/status | jq .

# Retrieve result once status is COMPLETED
curl -s http://localhost:9000/api/diagnostic/tracking/<trackingId>/result | jq .
```

### UC-4 — New prescription (direct client→provider)

```bash
curl -s -X POST http://localhost:9000/api/pharmacy/patients/1/prescriptions \
  -H "Content-Type: application/json" \
  -d '{
    "drugName": "Amoxicillin",
    "atcCode": "J01CA04",
    "dosage": "500mg",
    "frequency": "every 8 hours",
    "startDate": "2025-06-20",
    "expectedEndDate": "2025-06-27",
    "prescribingDoctor": "Dr. Smith"
  }' | jq .
```

### OpenAPI / Swagger UI

Every REST service exposes interactive API documentation:

| Service | Swagger UI URL |
|---|---|
| Farmacia Service | `http://localhost:9103/swagger-ui.html` |
| Clinical Aggregator | `http://localhost:9202/swagger-ui.html` |
| Care Coordinator | `http://localhost:9203/swagger-ui.html` |

---

## Scaling services (multiple instances)

Anagrafe, Laboratorio and Imaging are designed to run as multiple replicas. To start with more than one instance:

```bash
# Example: 3 instances of laboratorio-service, 2 of imaging-service, 2 of anagrafe-service
docker compose up --build \
  --scale laboratorio-service=3 \
  --scale imaging-service=2 \
  --scale anagrafe-service=2 \
  -d
```

Replicated services use `expose` (not fixed `ports`) to avoid port conflicts on the host. The API Gateway reaches them via `lb://service-name`, which delegates load balancing to Eureka + Spring Cloud LoadBalancer.

---

## Stopping the system

```bash
# Stop all containers (MySQL data volume is preserved)
docker compose down

# Stop all containers and delete the MySQL volume (full reset)
docker compose down -v
```

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| A service does not appear in Eureka | Config Server was not yet `healthy` when the service started | `docker compose restart <service-name>` |
| `Connection refused` at the Gateway | Discovery Server has not yet propagated registrations | Wait 30–60 seconds and retry |
| `405 Method Not Allowed` on an endpoint | Container is running a stale image | `docker compose build <service-name> && docker compose up -d <service-name>` |
| MySQL fails to start | Port 3306 is already in use on the host | Stop the local MySQL process or change the port mapping in `docker-compose.yml` |
| Client receives no response | Gateway not yet registered with Eureka | Check `http://localhost:8761` and wait until all services show `UP` |
