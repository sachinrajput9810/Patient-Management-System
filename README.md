# Patient Management System – Full Project Overview

This document provides a **complete, end-to-end description** of the Patient-Management-System microservices repository: what each service does, how they communicate, and how to build & run the stack locally or in the cloud.

---

## 1. High-Level Architecture

```
┌──────────────┐        gRPC         ┌──────────────┐
│  Patient     │────────────────────▶│  Billing     │
│  Service     │                    │  Service     │
└──────────────┘                    └──────────────┘
      │   ▲                                │
      │   │ Kafka (patient-events)         │
      ▼   │                                ▼
┌──────────────┐       gRPC         ┌──────────────┐
│  Analytics   │◀──────────────────▶│ API Gateway  │
│  Service     │                    └──────────────┘
└──────────────┘                           ▲
                                           │ REST/JSON
                                 ┌─────────┴─────────┐
                                 │  Front-end /      │
                                 │  External Clients │
                                 └────────────────────┘
```

* **Spring Boot 3** powers every microservice.
* **API Gateway** (Spring Cloud Gateway) fronts the system, exposes REST endpoints, and does routing, rate-limit & JWT auth.
* **gRPC** handles low-latency, request/response communication between services (Patient ⇆ Billing, Gateway ⇆ internal services).
* **Apache Kafka** provides an **event-driven backbone** – Patient events are published to `patient-events` and consumed by Analytics for real-time dashboards.
* **Docker / Docker Compose** containerize every microservice and run them on a shared internal bridge network, enabling zero-config service discovery by container name.

---

## 2. Service Breakdown

| Service | Port | Responsibilities | Key Tech |
|---------|------|------------------|----------|
| **patient-service** | 8081 (REST via Gateway) / 9091 (gRPC) | CRUD for patients, emits domain events to Kafka, invokes Billing via gRPC | Spring WebFlux, Spring Data JPA, gRPC, Kafka Producer |
| **billing-service** | 8082 / 9092 | Generates invoices when notified by Patient service, exposes gRPC for queries | Spring Boot, gRPC, Hibernate |
| **analytics-service** | 8083 / 9093 | Consumes `patient-events`, aggregates KPIs, exposes REST dashboards | Spring Boot, Kafka Consumer, Spring Data Mongo |
| **auth-service** | 8084 | Issues & validates JWT, used by Gateway for security | Spring Security, OAuth2 Resource Server |
| **api-gateway** | 8080 | Single entry-point; routes `/patients/**`, `/billing/**`, etc. to internal services, handles JWT auth, rate-limiting & circuit-breaking | Spring Cloud Gateway, Resilience4j |

---

## 3. Communication Patterns

### 3.1 gRPC (Synchronous)
* **Proto files** live in `grpc-requests/` and are shared across modules via Maven dependency.
* Generated stubs are used by Patient ⇆ Billing and Gateway ⇆ services to minimize latency & payload size.

### 3.2 Kafka (Asynchronous)
* Topic: `patient-events` (partitions = 3, replication = 1)
* **Producer**: Patient service publishes events (`PATIENT_CREATED`, `PATIENT_UPDATED`, …).
* **Consumer**: Analytics service persists stream to MongoDB and calculates real-time metrics.

---

## 4. Local Development & Running the Stack

### 4.0 Clone the Repository
```bash
git clone <your-fork-or-source-url>
cd Patient-Management-System-main
```

### 4.1 Prerequisites
* JDK 17+
* Maven 3.8+
* Docker & Docker Compose

### 4.2 One-Command Startup

```bash
# From repository root
mvn clean package -DskipTests

docker compose -f docker-compose.yml up --build
```
* Compose file starts Zookeeper, Kafka, PostgreSQL, MongoDB, and every microservice.
* Docker Compose also creates an internal network so services can reach each other via DNS (e.g., `patient-service:9091`).
* Access Swagger UI via `http://localhost:8080/swagger-ui.html` (through Gateway).

### 4.3 Running a Single Service Locally
```bash
cd patient-service
mvn spring-boot:run
```

Export `SPRING_PROFILES_ACTIVE=local` to use the embedded H2 database.

---

## 5. Directory Structure (important paths)

```
├── analytics-service/      # Real-time analytics & dashboards
├── api-gateway/            # Spring Cloud Gateway
├── auth-service/           # JWT / OAuth2 provider
├── billing-service/        # Invoice management
├── patient-service/        # Core patient CRUD & events
├── grpc-requests/          # .proto contracts, generated Java stubs
├── docker-compose.yml      # One-click stack launcher
└── integration-test/       # Testcontainers-based E2E tests
```

---

## 6. Contributing
1. Create feature branch → `git checkout -b feature/my-change`  
2. Ensure `mvn verify` passes  
3. Open PR against `main` with description & screenshot/GIF if UI change.

---

## 7. References
* Spring Cloud Gateway Docs  
* Apache Kafka Documentation  
* grpc.io – Java Quickstart 
