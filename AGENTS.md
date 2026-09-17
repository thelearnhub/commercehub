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
| **API Gateway** | `platform/api-gateway` | 8080 | ✅ Complete | Spring Cloud Gateway entry point routing `/auth/**`, `/users/**`, `/products/**` via Eureka load balancing (`lb://`). Global CORS configured. |
| **Config Server** | `platform/config-server` | 8888 | ✅ Complete | Centralized Spring Cloud Config Server with native profile search locations for microservice configurations. |
| **Common Security** | `libs/common-security` | — | ✅ Complete | Shared library module providing reusable `JwtService` parse-only validator and `JwtAuthenticationFilter` across downstream microservices. |
| **Auth Service** | `services/auth-service` | 8081 | ✅ Complete | Registration, email/password login, JWT access & refresh tokens, token refresh flow, Google OAuth2 Sign-In (tokeninfo verification), RBAC roles (`CUSTOMER`, `SELLER`, `ADMIN`), Flyway schema (V1/V2), OpenAPI, TestContainers integration tests. |
| **User Service** | `services/user-service` | 8082 | ✅ Complete | Profiles (`/users/profile`, `/users/me`, `/users/{id}`), Addresses (`/users/me/addresses` CRUD), default address exclusivity, Flyway schema (V1/V2), `ProfileMapper`/`AddressMapper` DTO mappers, shared `libs/common-security` filter, OpenAPI, TestContainers integration tests. |
| **Product Service** | `services/product-service` | 8083 | ✅ Complete | Product catalog & categories (`/products`), SKU uniqueness, Redis Cache-Aside (`@Cacheable`, `@CacheEvict`), **Decorator Pattern** pricing pipeline (`/products/{id}/price`), Flyway schema (V1), OpenAPI, unit & TestContainers integration tests. |
| **Cart Service** | `services/cart-service` | 8084 | ✅ Complete | Active shopping cart sessions (`/cart`), Write-Through Redis session caching (`RedisCartRepository`), automatic 7-day TTL expiry, guest $\rightarrow$ user cart merging, OpenAPI, unit tests. |
| **Inventory Service** | `services/inventory-service` | 8085 | ✅ Complete | Stock management & reservations (`/inventory`), **Redis Distributed Lock Pattern** (`RedisDistributedLock`), Audit Trail history logging (`InventoryAuditLog`), Flyway schema (V1), OpenAPI, unit tests. |

---

## 3. Pending & Scaffolded Modules (16 Total)

All of these directories exist with `.gitkeep` files and module declarations in `settings.gradle.kts`:

- **Platform (1 remaining):** `scheduler`.
- **Services (9 remaining):** `order-service`, `payment-service`, `shipping-service`, `notification-service`, `review-service`, `search-service`, `recommendation-service`, `analytics-service`, `fraud-service`.
- **Shared Libraries (6 remaining):** `common-dto`, `common-exceptions`, `common-tracing`, `common-kafka`, `common-testing`.

---

## 4. Next Tasks for Future AI Agents

When taking on the next task, follow this recommended sequence:

1. **Continue Phase 1 Core Services:**
   - **`services/cart-service` (Port 8084):** Shopping cart sessions, item management, Redis Write-Through cache + TTL expiry.
   - **`services/inventory-service` (Port 8085):** Stock reservations, Redis Distributed Lock pattern, audit trails.
   - **`services/payment-service` (Port 8086):** Charge/refund flows, Factory + Adapter payment providers, Idempotency-Key filter.
   - **`services/order-service` (Port 8087):** State Machine order lifecycle, Checkout Facade orchestrating Cart/Inventory/Payment/Shipping.

2. **Deferred Infrastructure (To be set up later):**
   - **Observability Stack (Jaeger, Prometheus, Grafana, Loki):** Deferred until the frontend application (`commercehub-web`) is ready and consuming backend APIs.
   - **Kafka (KRaft mode) + Schema Registry:** Deferred to Phase 2 (Event-Driven Backbone).

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
