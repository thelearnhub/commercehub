# AI Agent Context & Repository State (`AGENTS.md`)

This document serves as the primary context file for AI coding assistants (Antigravity, Cursor, Claude Code, Copilot, etc.) working on the **CommerceHub** codebase.

---

## 1. Executive Summary

- **Project:** CommerceHub — A distributed mini-Amazon style e-commerce microservices platform.
- **Org:** [`thelearnhub`](https://github.com/thelearnhub)
- **Master Plan:** [`docs/CommerceHub_Master_Plan.md`](docs/CommerceHub_Master_Plan.md)
- **Phase Roadmap:** [`docs/phase_roadmap.md`](docs/phase_roadmap.md) (or brain artifact)
- **Current Branch:** `feature/auth`
- **Current Stage:** Transitioning from **Phase 0 (Foundations)** to **Phase 1 (Core Commerce)**.

---

## 2. Implemented Services & Modules

| Module | Location | Port | Status | Capabilities & Details |
|---|---|---|---|---|
| **Eureka Server** | `platform/eureka-server` | 8761 | ✅ Complete | Netflix Eureka Service Discovery server. Services register via `@EnableDiscoveryClient`. |
| **Auth Service** | `services/auth-service` | 8081 | ✅ Complete | Registration, email/password login, JWT access & refresh tokens, token refresh flow, Google OAuth2 Sign-In (tokeninfo verification), RBAC roles (`CUSTOMER`, `SELLER`, `ADMIN`), Flyway schema (V1/V2), OpenAPI, TestContainers integration tests. |
| **User Service** | `services/user-service` | 8082 | ✅ Complete | Profiles (`/users/profile`, `/users/me`, `/users/{id}`), Addresses (`/users/me/addresses` CRUD), default address exclusivity, Flyway schema (V1/V2), `ProfileMapper`/`AddressMapper` DTO mappers, parse-only JWT filter, OpenAPI, TestContainers integration tests. |

---

## 3. Pending & Scaffolded Modules (22 Total)

All of these directories exist with `.gitkeep` files and module declarations in `settings.gradle.kts`:

- **Platform (2 remaining):** `api-gateway` (prerequisite), `config-server` (prerequisite), `scheduler`.
- **Services (12 remaining):** `product-service`, `cart-service`, `order-service`, `payment-service`, `inventory-service`, `shipping-service`, `notification-service`, `review-service`, `search-service`, `recommendation-service`, `analytics-service`, `fraud-service`.
- **Shared Libraries (6 remaining):** `common-dto`, `common-exceptions`, `common-tracing`, `common-kafka`, `common-security`, `common-testing`.

---

## 4. Next Tasks for Future AI Agents

When taking on the next task, follow this recommended sequence:

1. **Finish Phase 0 Platform:**
   - Implement `platform/api-gateway` (Spring Cloud Gateway on port 8080) routing to `auth-service`, `user-service`, and Eureka.
   - Implement `platform/config-server` (Spring Cloud Config).
   - Complete `infra/docker-compose/docker-compose.yml` (add Redis, Kafka KRaft mode, Schema Registry, Jaeger, Prometheus, Grafana).
   - Add `.github/workflows/ci.yml` (Gradle build & TestContainers test run on PRs).

2. **Next Phase 1 Microservice — `services/product-service`:**
   - Owns product catalog, categories, pricing rules, and inventory lookup stubs.
   - Stack: MySQL + Redis (cache-aside pattern).
   - Pattern: Decorator pattern for price calculation pipelines (discounts, taxes, promos).
   - Schema: Flyway migrations `V1__init_product_schema.sql`.

3. **Extract Shared Libraries:**
   - Extract shared JWT validation filter and security config from `auth-service`/`user-service` into `libs/common-security`.

---

## 5. Non-Negotiable Engineering Rules & Conventions

For any AI agent modifying or expanding this repository:

1. **Database Migrations (Flyway):**
   - Hibernate DDL auto is strictly set to `validate` (`spring.jpa.hibernate.ddl-auto: validate`).
   - NEVER use `update`, `create`, or `create-drop`.
   - Every schema change MUST be driven by a Flyway SQL migration script under `src/main/resources/db/migration/V<N>__<name>.sql`.

2. **Database Isolation:**
   - Every microservice owns its own database schema (e.g., `commercehub_auth`, `commercehub_user`, `commercehub_product`).
   - NO cross-database foreign keys or cross-schema JOIN queries.
   - Cross-service identity correlation uses UUIDs/emails embedded in JWT tokens.

3. **Security & Authentication:**
   - Only **Auth Service** generates JWTs.
   - Downstream services (`user-service`, `product-service`, etc.) validate JWT signatures using a parse-only `JwtService` with the shared secret (`JWT_SECRET`).
   - User identity comes from JWT claims (`sub` = email, `role` = authority), accessible via `Authentication.getName()`.

4. **DTO Mapping:**
   - Use hand-written explicit static mapper classes (e.g., `ProfileMapper`, `AddressMapper`) rather than MapStruct or reflection mappers to keep patterns readable and testable.

5. **Testing Requirements:**
   - Every service MUST include integration tests using JUnit 5 + SpringBootTest + TestContainers (`MySQLContainer`).
   - Unit tests for mappers and domain logic.

6. **API Specifications:**
   - Expose OpenAPI 3.0 via `springdoc-openapi-starter-webmvc-ui` on `/swagger-ui.html` with Bearer SecurityScheme configured.

---

## 6. How to Run & Verify Locally

```bash
# Compile all modules
./gradlew compileJava compileTestJava

# Run Eureka Server (port 8761)
./gradlew :platform:eureka-server:bootRun

# Run Auth Service (port 8081)
./gradlew :services:auth-service:bootRun

# Run User Service (port 8082)
./gradlew :services:user-service:bootRun
```
