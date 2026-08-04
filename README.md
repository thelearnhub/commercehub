# CommerceHub

A distributed e-commerce platform built as a hands-on senior/staff engineering curriculum — microservices, event-driven architecture (Kafka/Saga/Outbox), resilience patterns, CQRS, Kubernetes, and AWS, all built around one real system rather than isolated demos.

Part of the [`thelearnhub`](https://github.com/thelearnhub) org. Full plan: [`docs/CommerceHub_Master_Plan.md`](docs/CommerceHub_Master_Plan.md). Org-level context: [`docs/TheLearnHub_Overview.md`](docs/TheLearnHub_Overview.md).

## Status

**Phase 0 — Foundations.** Repo scaffolding in progress. See the roadmap in the master plan for what's next.

## Team & Ownership

| Engineer | GitHub | Owns |
|---|---|---|
| (add name) | @arifkhanesm | TBD — see `.github/CODEOWNERS` once services exist |
| (add name) | | |
| (frontend, separate repo: `commercehub-web`) | | |

## Repository Structure

```
commercehub/
├── services/       14 microservices (auth, user, product, cart, order, payment,
│                   inventory, shipping, notification, review, search,
│                   recommendation, analytics, fraud)
├── platform/       api-gateway, eureka-server, config-server, scheduler
├── libs/           shared modules: common-dto, common-exceptions, common-tracing,
│                   common-kafka, common-security, common-testing
├── infra/          docker-compose, k8s, helm, terraform
├── contracts/      OpenAPI specs, Pact contract files
└── docs/           adr/, runbooks/, feature-specs/, the two plan docs above
```

## Local Development

Prerequisites: Docker + Docker Compose, Java 21, Gradle (or Maven).

```bash
docker compose -f infra/docker-compose/docker-compose.yml up -d
```

Brings up MySQL, Redis, Kafka (KRaft mode), Schema Registry, Jaeger, Prometheus, Grafana, Loki. (Compose file to be added in Phase 0.)

## Contributing

Every change ships as a PR against `main` requiring at least one approval. Every non-trivial feature follows the Feature Delivery Template in `docs/feature-specs/` (13 checkpoints: business requirement → HLD → LLD → DB design → API contract → failure scenarios → implementation → testing → performance → security → observability → deployment → interview discussion). Every non-trivial decision gets an ADR in `docs/adr/`.
