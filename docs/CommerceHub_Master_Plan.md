# CommerceHub — Master Engineering Plan (Final)
### A mini Amazon-style engineering platform, built by a small team, to produce architect-level backend engineers

---

## 1. Vision

CommerceHub is not another "Spring Boot microservices" demo. It's a real distributed e-commerce platform where every service, event, and failure mode exists because the business needs it — and each one happens to be a classic senior/staff interview topic. Nothing gets added just to "cover a topic": if a pattern shows up, there's a concrete scenario in CommerceHub that breaks without it.

Most projects like this have three layers:

```
Business Features
     ↓
Architecture
     ↓
Deployment
```

Real companies run a fourth, invisible one underneath:

```
Engineering Practices
```

Testing discipline, migrations, versioning, contracts, audit trails, runbooks, rollout strategy — the stuff that doesn't show up in a demo but is most of what a staff engineer actually evaluates in a design review. CommerceHub treats that layer as first-class from day one, not bolted on later.

Target outcome: a working, deployable, observable, testable, documented microservices platform — built by a two-backend/one-frontend team — where every resume claim ("I designed for eventual consistency," "I've debugged a production memory leak," "I've run a canary rollout") is backed by code, an ADR, and a runbook you can pull up live in an interview.

---

## 2. The Feature Delivery Template

The standard unit of work. Every non-trivial feature — a service, an event flow, a cross-cutting capability — goes through these 13 checkpoints before it's "done":

1. **Business requirement** — why this exists, who needs it.
2. **High-Level Design (HLD)** — components, data flow, boundaries.
3. **Low-Level Design (LLD)** — classes, interfaces, state machines, sequence diagrams.
4. **Database design** — schema, indexes, ownership boundary.
5. **API contract** — OpenAPI spec, versioning stance.
6. **Failure scenarios** — what breaks, how it degrades, how it recovers.
7. **Implementation** — the actual code.
8. **Testing strategy** — which layers of the pyramid apply and why.
9. **Performance considerations** — expected load, hot paths, benchmarks.
10. **Security considerations** — authn/authz, data sensitivity, OWASP-relevant risks.
11. **Observability** — logs, metrics, traces specific to this feature.
12. **Deployment** — rollout strategy, flags, migration ordering.
13. **Interview discussion** — 3–5 questions and trade-offs, written down while the decision is fresh, with the author's name on it.

**Worked example — "Idempotent Payment Processing":** retries from clients/network must never double-charge → Payment Service sits behind an idempotency filter before the provider adapter → `IdempotencyKey` entity, unique constraint, request-hash comparison, `PENDING`/`COMPLETED`/`FAILED` states → `idempotency_keys` table, unique index on `(key, endpoint)`, TTL cleanup job → `POST /payments` requires an `Idempotency-Key` header, documented as required in OpenAPI → failure scenario: a crash between provider-charge and DB-write must not orphan a charge, this is the scenario that motivates the whole feature → service layer + DB constraint + provider reconciliation job → unit test for the race, integration test hammering the endpoint concurrently with TestContainers → unique-index lookup must stay O(1) under load, benchmarked in the performance phase → key scoped to the authenticated user so it can't be replayed across accounts → metric `payment.idempotent_hits`, trace tag on replayed requests → ships behind the CI contract test for the required header → interview discussion: "How do you guarantee exactly-once payment given retries at every layer?"

Every service gets a `docs/feature-specs/` folder holding this template, filled in, per feature, with an author field.

---

## 3. Team & Repository Strategy

Team: two backend engineers, one frontend engineer consuming the backend APIs.

**The problem to avoid:** a repo living at `github.com/<one-person>/commercehub` reads as that person's personal project to anyone skimming GitHub, regardless of what the contributor list actually says underneath.

**The setup:**

- **Create a GitHub Organization**, not a personal repo: **`thelearnhub`**. This is the umbrella org for the whole curriculum — CommerceHub is project #1, with more projects added as siblings over time (see `TheLearnHub_Overview.md`). Owning this as an org from the start removes the ownership ambiguity — none of it belongs to any one person's namespace — and it scales cleanly as new projects get added, instead of having to migrate later.
- **Repositories under that org**: `commercehub` (backend monorepo, structure in Section 4), `commercehub-web` (frontend), and a shared `standards` repo (ADR template, feature-delivery template, runbook template, reusable CI workflows) that every current and future project under `thelearnhub` inherits from. Splitting frontend into its own repo mirrors how real companies do it, gives the frontend engineer full, unambiguous ownership of something, and decouples release cadence — the frontend only depends on the OpenAPI contracts the backend publishes, not on backend internals.
- **Explicit service ownership via `CODEOWNERS`**, mapped onto the 14-service catalog in Section 5. Example split (adjust to interest/strength): Engineer A primary-owns the saga/event-heavy core — Order, Payment, Inventory, Fraud, Analytics. Engineer B owns the CRUD- and search-heavy side — Auth, User, Product, Cart, Shipping, Notification, Review, Search, Recommendation — plus the platform services (Gateway, Eureka, Config). Write the split down; don't leave it to be inferred from git blame.
- **Branch protection on `main`**: every change ships as a PR requiring at least one approval from the other backend engineer. This produces a visible review trail — GitHub shows who reviewed what — which is itself evidence of collaboration beyond raw commit counts.
- **Never let squash-merges erase co-authorship.** If two people pair on something, use `Co-authored-by:` trailers so GitHub credits both authors on the squashed commit.
- **GitHub Issues/Projects**: break the phase roadmap (Section 12) into issues assigned per person. This shows the *planning* story — who scoped what, who picked up what — which a commit graph alone never shows, and it's exactly what "how did you divide work on a team" means in an interview.
- **README "Team & Ownership" section**: name each person, their role, and the services/features they primarily built and can speak to in depth. This is what a human reader — recruiter, hiring manager — actually reads.
- **Individual case-study writeups**: each engineer separately keeps a short personal doc ("I designed the Saga/Outbox flow across Order→Payment→Inventory, including the DLQ and idempotent-consumer strategy") linking back to the shared org repo. A shared codebase can still produce individual narrative credit if each person documents their own slice.
- **Commit hygiene**: everyone commits under their own GitHub-linked email so personal contribution graphs populate correctly. This already works technically in any repo — the org move is what fixes the *perception* problem, not the attribution mechanics.

---

## 4. Repository Structure

This repo is one project inside the `thelearnhub` org — see `TheLearnHub_Overview.md` for how it sits alongside future projects and the shared `standards` repo.

```
commercehub/                      (org: thelearnhub)
├── services/
│   ├── auth-service/ user-service/ product-service/ cart-service/
│   ├── order-service/ payment-service/ inventory-service/
│   ├── shipping-service/ notification-service/ review-service/
│   ├── search-service/ recommendation-service/
│   └── analytics-service/ fraud-service/
├── platform/
│   ├── api-gateway/ eureka-server/ config-server/ scheduler/
├── libs/
│   ├── common-dto/ common-exceptions/ common-tracing/
│   ├── common-kafka/ common-security/ common-testing/
├── infra/
│   ├── docker-compose/ k8s/ helm/ terraform/
│   └── db/migrations/           (Flyway sets, one per service schema)
├── contracts/                    (Pact files, OpenAPI specs per service)
├── docs/
│   ├── adr/ runbooks/ feature-specs/
└── .github/
    ├── workflows/
    └── CODEOWNERS

commercehub-web/                  (org: thelearnhub, separate repo)
└── frontend consuming published OpenAPI clients + REST/event contracts

standards/                        (org: thelearnhub, shared across all projects)
└── adr-template.md  feature-spec-template.md  runbook-template.md
    reusable-ci-workflows/  codeowners-convention.md
```

Local dev via Docker Compose: MySQL (one schema per service, never cross-schema joins), Redis, Kafka in KRaft mode, Schema Registry, Jaeger, Prometheus, Grafana, Loki.

---

## 5. Service Catalog (End State)

| Service | Responsibility | Own DB | Signature patterns/concepts |
|---|---|---|---|
| Auth | Login, JWT issuance, refresh tokens, RBAC, API keys | MySQL | JWT, OAuth2/OIDC, Chain of Responsibility, Rate Limiter |
| User | Profiles, addresses, preferences | MySQL | Repository, DTO mapping |
| Product (Catalog) | Product data, categories, pricing rules | MySQL + Redis (cache-aside) | Decorator (price pipeline), Caching |
| Cart | Session cart, TTL expiry | Redis (write-through) | Write-Through cache, TTL |
| Order | Order lifecycle, checkout facade | MySQL | State, Facade, Builder, Saga orchestrator, Outbox, CQRS, audit trail |
| Payment | Charge, refund, provider abstraction | MySQL | Factory, Adapter, Strategy, Idempotency, Circuit Breaker, Bulkhead, audit trail |
| Inventory | Stock levels, reservation | MySQL + Redis lock | Distributed Lock, optimistic/pessimistic locking, audit trail, event sourcing (stretch) |
| Shipping | Shipment creation & tracking | MySQL | Observer, State |
| Notification | Email/SMS/push dispatch | MySQL (log) | Template Method, Adapter |
| Review | Ratings & reviews | MySQL | Repository, CQRS read model |
| Search | Product search at scale | Elasticsearch | Autocomplete, facets, ranking, synonyms; synced via Kafka |
| Recommendation | "You may also like" | MySQL/Redis | Consumes clickstream + order events |
| Analytics | Dashboards, aggregates | OLAP-style store | Kafka Streams / batch aggregation |
| Fraud Detection | Rule-based transaction scoring | MySQL/Redis | Chain of Responsibility (rule chain), Observer |

Cross-cutting platform: **API Gateway**, **Eureka**, **Config Server**, **distributed scheduler**, **Kafka event bus**. Every service exposes `/actuator/health`, `/actuator/prometheus`, and a versioned OpenAPI doc, and owns Flyway migrations from day one (no `ddl-auto: update` past Phase 0). Order, Payment, and Inventory carry audit tables from Phase 1 — realistic "who cancelled this order / who issued this refund" questions.

---

## 6. Event Catalog (Kafka)

| Topic | Producer | Key Consumers | Notes |
|---|---|---|---|
| `order-created` | Order | Inventory, Notification, Analytics | Saga trigger |
| `inventory-reserved` | Inventory | Order | Saga step |
| `inventory-released` | Inventory | Order | Compensation event |
| `payment-success` | Payment | Order, Shipping, Notification | |
| `payment-failed` | Payment | Order | Compensation |
| `shipment-created` | Shipping | Order, Notification | |
| `shipment-delivered` | Shipping | Order, Notification, Review | Triggers review request |
| `refund-created` | Payment | Order, Notification | |
| `notification-requested` | Order/Payment/Shipping | Notification | Generic fan-in |
| `review-added` | Review | Analytics, Recommendation | |

All producers publish via **Outbox** (DB write + outbox row in one transaction; poller or Debezium CDC relays to Kafka) — never a direct post-commit publish. All consumers are **idempotent** (dedupe table keyed on event ID), with a **DLQ + retry topic + parking-lot queue** for poison messages. Partition key = `orderId` (event ordering); Schema Registry with **Avro**, backward-compatible evolution enforced in CI.

---

## 7. Engineering Practices Layer (cross-cutting, all phases)

Active from Phase 0, matures alongside the business features — not its own phase:

| Capability | Introduced | Matured |
|---|---|---|
| Unit tests (JUnit5, Mockito) | Phase 0 | Every service, ongoing |
| TestContainers (real MySQL/Kafka/Redis in tests) | Phase 1 | Ongoing |
| WireMock (stub payment gateway, external APIs) | Phase 1 | Phase 3 (failure injection) |
| Controller → Service → Repository → Integration → E2E pyramid | Phase 1 | Ongoing, enforced in CI |
| OpenAPI / Swagger per service | Phase 0 | Ongoing |
| API versioning & deprecation policy | Phase 1 | Phase 6 (rolling upgrade exercise) |
| Consumer-driven contract testing (Pact) — also covers the frontend↔backend contract | Phase 2 | Phase 6 |
| Flyway migrations, rollback, seed data | Phase 0 | Ongoing |
| Audit trail / soft delete / temporal tables | Phase 1 (Order, Payment, Inventory) | Phase 4 (Review, User) |
| Feature flags (rollout %) | Phase 3 (new Checkout facade) | Phase 6 (tied to canary deploys) |
| Multi-tenancy design exercise | Phase 4 | ADR comparing single-DB / schema-per-tenant / DB-per-tenant; prototype on one service |
| Performance testing (k6/Gatling) | Phase 5 | Ongoing regression gate in CI |
| Chaos testing (kill a pod/broker mid-flow) | Phase 3 | Phase 7 (game days) |
| Mutation testing (PIT) | Phase 5 | Applied to Payment/Inventory only |
| Runbooks | Phase 2 | Grows every phase |
| CI/CD pipeline | Phase 0 (build/test/lint) | Phase 6 (full multi-env pipeline, Section 10) |

Mutation testing and full chaos tooling are the two items most likely to be cut if time runs short — everything else in this table is non-negotiable.

---

## 8. Distributed Systems Concepts

| Concept | Where it lives | Phase |
|---|---|---|
| Leader election | Distributed scheduler picks one node for order-expiry/payment-timeout jobs | 3 |
| Distributed scheduling (ShedLock over Quartz/Spring Scheduler) | Same scheduler, cluster-safe | 3 |
| Clock skew | Expiration/timeout windows computed server-side, never trusted from clients | 2/3 |
| Event ordering | Kafka partition key = `orderId`; discussion of out-of-order handling across partitions | 2 |
| Poison messages | Retry limits, DLQ, parking-lot queue for manual replay | 2 |
| Backpressure | Notification consumer lag under load; scale consumers vs. throttle producers | 3/5 |
| Event replay | Rebuild Search index and Analytics aggregates by replaying topics from offset 0 | 4 |
| Blue-green deployment | Compared against canary in an ADR | 6 |
| Canary deployment | Primary rollout strategy for the new Checkout facade, tied to feature flags | 6 |
| Shadow traffic | Mirror prod checkout requests to a new pricing engine version, stretch goal | 6 |
| Database sharding | Design exercise: shard Orders by `customerId % N`; prototype, not full production sharding | 5/6 |
| Read replicas | Order/Product read traffic split from writes | 5 |
| Connection pool exhaustion | Deliberately induced under load test, diagnosed via HikariCP metrics | 5 |

---

## 9. Pattern → Component Map

| Pattern | Where it lives | Interview trigger |
|---|---|---|
| Repository | Every service | "How do you abstract persistence?" |
| Factory | Payment provider selection | "How do you add a new payment method without touching callers?" |
| Strategy | Discount engine | "How do you swap algorithms at runtime?" |
| Decorator | Price calculation pipeline | "How do you stack optional behaviors?" |
| Builder | Order object construction | "Why not a telescoping constructor?" |
| Observer | Kafka consumers | "How do services react without polling?" |
| Adapter | Third-party payment gateway | "How do you normalize inconsistent external APIs?" |
| Facade | Checkout service | "How do you hide multi-service orchestration behind one API?" |
| Template Method | Notification templates | "How do you share structure but vary steps?" |
| Command | Cancel/retry/undo order | "How do you make actions queueable/undoable?" |
| Chain of Responsibility | Security filter chain, fraud rules | "How do you compose independent checks?" |
| State | Order lifecycle | "How do you prevent illegal state transitions?" |
| Saga | Checkout flow | "How do you keep data consistent across services without 2PC?" |

---

## 10. Deep-Dive Modules

**Performance (Phase 5):** deliberate N+1 and missing index, `EXPLAIN` plans, composite vs. covering indexes, offset vs. keyset pagination, connection pool tuning, batch inserts, read replicas, sharding design exercise. JVM: GC algorithms (G1 vs ZGC), a deliberately introduced memory leak diagnosed via heap dump + Eclipse MAT, thread dumps for a deadlock scenario, CPU profiling with JFR/JMC or VisualVM — closes with a written incident report (symptom → diagnosis → fix → prevention). Load testing with k6/Gatling, tracked as a CI regression gate.

**Security (Phase 3):** OAuth2 + OIDC (authorization code + PKCE), refresh token rotation. Service-to-service mTLS (implemented via Istio as a Phase 6 stretch goal). Secrets rotation, encryption at rest (RDS/KMS) and in transit. OWASP Top 10 applied directly to Gateway/Auth: deliberately introduce and fix one SQL injection, one XSS, one CSRF, one CORS misconfiguration — same break-it-first approach as performance. Gateway-level rate limiting, API keys, request size limits.

**Kubernetes (Phase 6):** Pods, ReplicaSets, Deployments, Services, Ingress, HPA, StatefulSet (local Kafka/MySQL dev only — production uses managed RDS/MSK), DaemonSet (log shipping), ConfigMap, Secrets, Volumes/PVCs, node affinity, Pod Disruption Budgets, rolling-update tuning tied to the canary/blue-green discussion.

**AWS (Phase 6):** VPC (public/private subnets, NAT gateway), ALB vs NLB, Route53, CloudFront + S3 for product images, Lambda (thumbnail generation on upload), SNS/SQS (fan-out compared against Kafka topics, written as an ADR), MSK vs. self-hosted Kafka, RDS/Aurora, EKS, ECR, IAM least-privilege roles, KMS, Secrets Manager, CloudWatch, X-Ray compared against the existing Jaeger/OTel tracing.

---

## 11. Full CI/CD Pipeline

Built incrementally:

```
Commit → SonarQube → Unit Tests → Integration Tests (TestContainers)
   → Mutation Tests (PIT, Payment/Inventory only) → Build → Docker Image
   → Image Scan (Trivy) → Push (ECR) → Deploy Dev → Smoke Tests
   → Deploy QA → Manual Approval Gate → Deploy Production (canary)
```

Phase 0: commit → unit tests → build. Phase 1–2: integration tests, OpenAPI contract validation, migration checks. Phase 3: SonarQube, image scanning. Phase 5: mutation tests, performance regression gates. Phase 6: full multi-environment promotion with approval gates and canary deploy.

---

## 12. Monitoring, Dashboards & Runbooks

**Business dashboard** (fed by Analytics): orders/min, revenue/min, failed-payment rate, top products, average checkout time.
**Technical dashboard** (Prometheus/Grafana): CPU, heap, GC pause time, Kafka consumer lag, thread pool saturation, DB connection pool usage, API latency (p50/p95/p99), Redis hit rate.

**Runbook template:** Symptom → Diagnosis steps → Immediate mitigation → Root cause investigation → Long-term fix → Escalation path.
**Initial runbook set** (grows every phase): Kafka broker down, payment gateway slow/timing out, database disk full, DLQ growing unbounded, consumer lag spiking, suspected memory leak, connection pool exhausted.

---

## 13. Phased Roadmap

| Phase | Focus | Details |
|---|---|---|
| **0 — Foundations** (1–2 wks) | Repo, Docker Compose, Eureka/Gateway/Config skeletons | Flyway from day one, OpenAPI baseline, CI skeleton with SonarQube hook. *Unlocks:* Spring Boot auto-configuration, bean lifecycle, externalized config. |
| **1 — Core Commerce** (4–6 wks) | Auth, User, Product, Cart, Order, Payment, Inventory, Shipping, Notification over sync REST/Feign | State machine (order lifecycle), Facade (checkout), Factory+Strategy+Adapter (payment providers), Decorator (pricing), Builder (order), Chain of Responsibility (security filters); audit trails; full test pyramid; API versioning policy. *Unlocks:* DDD boundaries, SOLID, patterns, JWT/RBAC, Gateway, Eureka. *Debrief:* graceful degradation across downstream calls. |
| **2 — Event-Driven Backbone** (4–5 wks) | Kafka introduced, checkout becomes a Saga | Outbox for Order/Payment/Inventory, idempotent consumers, DLQ + parking-lot queue, Kafka transactions/exactly-once, Schema Registry + Avro, event ordering, contract testing, first runbooks. *Unlocks:* Saga, Outbox, exactly-once, duplicate handling, schema evolution. *Debrief:* payment succeeds but Kafka publish fails; duplicate events; saga fails halfway. |
| **3 — Production Hardening** (3 wks) | Resilience4j, tracing, metrics, security | Circuit Breaker/Retry/Bulkhead/Rate Limiter; cache-aside/write-through/distributed lock; OpenTelemetry tracing end to end; Micrometer/Prometheus/Grafana; leader election + distributed scheduler; clock skew handling; OAuth2/OIDC/mTLS; OWASP fix-it exercises; feature flags; chaos testing starts. *Debrief:* gateway timeout that still charges; inventory down during checkout; broker unavailable. |
| **4 — Advanced Architecture** (4–5 wks) | CQRS, Search, Recommendation, Analytics, Fraud, Batch | Event sourcing (stretch, Inventory); multi-tenancy ADR; pricing engine as its own module; file storage/S3/CDN; event replay; business dashboard; Spring Batch reconciliation jobs. *Debrief:* read traffic 100x writes; catalog grows to millions of products. |
| **5 — Performance Engineering** (2 wks) | DB + JVM performance module | Full profiling toolchain, mutation testing, sharding/read-replica design, connection pool exhaustion drill. *Debrief:* missing index causes a production incident. |
| **6 — Cloud Deployment** (3–4 wks) | K8s, Helm, AWS, CI/CD | Deep K8s/AWS modules; blue-green/canary/shadow traffic; full CI/CD pipeline; service mesh mTLS (stretch). *Debrief:* rolling upgrade with mixed API versions across services. |
| **7 — Interview Mode** (ongoing) | Incident drills, HLD/LLD sessions | Expanded question bank below; game days using chaos tooling; mock interviews grounded in real ADRs/runbooks. |

---

## 14. Production Incident Simulation Backlog

1. Payment succeeds but Kafka publish fails — Phase 2
2. Kafka publishes but DB transaction rolls back — Phase 2
3. Duplicate `order-created` events arrive — Phase 2
4. Inventory service is down during checkout — Phase 3
5. Payment gateway times out but eventually charges — Phase 3
6. Two users buy the last item simultaneously — Phase 3
7. Kafka broker becomes unavailable — Phase 3
8. New event version introduced without breaking consumers — Phase 2/4
9. Order service calls multiple downstream services with graceful degradation — Phase 1/3
10. Read traffic 100x write traffic — Phase 4
11. Product catalog grows to millions — Phase 4
12. Notification service falls behind under load — Phase 4/5
13. Missing DB index causes a performance incident — Phase 5
14. Rolling upgrade with mixed API versions across services — Phase 6
15. Long-running saga fails halfway, needs compensation — Phase 2

---

## 15. Expanded Interview Question Bank

"Design Amazon Checkout." "Design a payment system." "How do you prevent double payments?" "How do you handle eventual consistency?" "How would you migrate a monolith to microservices?" "How would you scale to 100 million users?" "What happens if Kafka is unavailable?" "How do you debug a memory leak in production?" "How do you perform a zero-downtime deployment?" "How do you evolve an event schema safely?" "How do you observe and troubleshoot a request across 10 services?"

Each should be answerable by pointing at a specific ADR, runbook, or piece of code — that's the test of whether a phase actually landed.

---

## 16. Definition of Done

A phase isn't done when the code runs — it's done when the owning engineer can answer, on camera, without notes: what problem this solved, what the alternatives were, what the trade-off was, and what breaks it in production. Each phase closes with ADRs in `docs/adr/`, feature specs in `docs/feature-specs/` (with author names), and runbook entries for anything operational.

---

## 17. Scope Guardrails

**Core track (non-negotiable):** all 9 Phase 1 services, Kafka/Saga/Outbox, full Resilience4j suite, tracing/metrics/logging, the complete test pyramid, Flyway, OpenAPI, basic CI/CD, at least one performance incident and one security incident fixed end-to-end, K8s deployment of the whole system, and the full runbook set.

**Extended track (build in this order, if time allows):** CQRS/event sourcing, search/recommendation/analytics/fraud services, multi-tenancy prototype, mutation testing, chaos engineering tooling, service mesh/mTLS, shadow traffic, full sharding implementation.

If a phase overruns, cut from the extended track first. The core track alone already carries a senior/staff-level system design conversation; the extended track is what pushes it toward architect-level.

---

## Next Step

Set up the `thelearnhub` GitHub organization and its three repos (Section 3), agree the `CODEOWNERS` split, then start **Phase 0**: repo skeleton, Docker Compose, Eureka + Gateway + Config + Auth service, with Flyway/OpenAPI/CI baked in from the first commit. See `TheLearnHub_Overview.md` for the org-level setup steps.
