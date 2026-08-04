# The Learn Hub

### An umbrella org for engineering projects built to teach — not to demo

---

## 1. What this is

**The Learn Hub** (`thelearnhub` on GitHub) is a home for a series of production-grade engineering projects, each one built end-to-end around a real domain instead of scattered tutorials. **CommerceHub** — a distributed e-commerce platform — is project #1. Future projects live here as siblings, each with its own repo(s), but all sharing the same engineering discipline: HLD/LLD before code, testing as a first-class citizen, ADRs for every non-trivial decision, and production-incident simulations instead of just feature checklists.

The org exists so that: (1) the work reads as a team effort, not one person's personal repo, and (2) the engineering rigor built for CommerceHub — the Feature Delivery Template, ADR format, runbook template, CI conventions — is reusable on the next project instead of being reinvented.

---

## 2. Org structure

```
github.com/thelearnhub/
├── .github/                  → org profile README (this doc, trimmed, shown on the org homepage)
├── standards/                → shared across every project in the org
│   ├── adr-template.md
│   ├── feature-spec-template.md   (the 13-point Feature Delivery Template)
│   ├── runbook-template.md
│   ├── codeowners-convention.md
│   └── reusable-ci-workflows/     (GitHub Actions workflows other repos call into)
│
├── commercehub/               → Project 1: backend monorepo (14 services + platform)
├── commercehub-web/           → Project 1: frontend consuming CommerceHub's APIs
│
└── (future projects, same pattern)
    ├── <project-2>/
    ├── <project-2>-web/       (if it needs one)
    └── ...
```

Each project gets its own repo(s) rather than living in one giant monorepo across projects — different domains, different lifecycles, different teams over time. The `standards` repo is what keeps them consistent without forcing them into the same codebase.

---

## 3. Current projects

| Project | Status | Domain | What it teaches |
|---|---|---|---|
| **CommerceHub** | In progress — Phase 0 | Distributed e-commerce platform | Microservices, Kafka/Saga/Outbox, resilience patterns, distributed tracing, CQRS, Kubernetes, AWS. Full plan: `CommerceHub_Master_Plan.md`. |

---

## 4. Future project ideas (placeholder — fill in as you go)

Not committed yet, but the kind of thing that fits the same model — each teaches a domain CommerceHub doesn't fully cover:

- A **real-time streaming/analytics platform** (Kafka Streams / Flink depth, windowing, exactly-once at a different layer than CommerceHub's).
- A **distributed job scheduler** (Quartz/ShedLock taken further — leader election, work partitioning, retries at scale — CommerceHub only touches this lightly).
- An **observability platform** of your own (mini Prometheus/Grafana-style metrics pipeline, since CommerceHub only *consumes* observability tooling rather than building it).
- A **multi-tenant SaaS backend** (CommerceHub touches multi-tenancy as one ADR; a dedicated project could go much deeper on isolation models, billing, tenant-aware rate limiting).

Add real project pages here once one starts — same pattern as CommerceHub: a `<Project>_Master_Plan.md` living in that project's repo, linked from this table.

---

## 5. Team & how work is organized

Two backend engineers, one frontend engineer, currently on CommerceHub. Per-project ownership (who owns which services) is defined in each project's own plan doc and its repo's `CODEOWNERS` file — not here, since it'll differ project to project. What's constant across all projects:

- Every repo requires PR + review before merge to `main`.
- Every feature goes through the Feature Delivery Template in `standards/feature-spec-template.md`, with an author on record.
- Every non-trivial decision gets an ADR using `standards/adr-template.md`.
- Each engineer keeps a personal case-study writeup per project they contribute to, linking back to the org — this is what turns a shared codebase into individually defensible interview material.

---

## 6. Setting this up on GitHub (step by step)

I don't have a GitHub connector authorized in this session, so these are the exact clicks to run yourself. Once a GitHub connector is available in a future session, I can do steps 3 onward directly instead.

### Step 1 — Create the organization
1. Go to `github.com/account/organizations/new` (or: click your profile picture, top right → **Settings** → **Organizations** (left sidebar, under "Access") → **New organization**).
2. Pick the **Free** plan (fine for this — private repos with collaborators are included).
3. **Organization account name**: `thelearnhub`.
4. **Contact email**: any email you check.
5. **This organization belongs to**: "My personal account."
6. Complete the verification step, then **Create organization**.

### Step 2 — Invite your teammates
1. Inside the new org, go to the **People** tab → **Invite member**.
2. Invite your co-backend-engineer and your frontend engineer by GitHub username or email.
3. Default role: **Member** (not Owner) — keep Owner limited to whoever administers billing/settings; you can add a second Owner later if you want shared admin control.

### Step 3 — Create a team per discipline (optional but useful)
1. **People** tab → **Teams** → **New team**.
2. Create `backend` (both backend engineers) and `frontend` (frontend engineer). Teams let you grant repo access in one place instead of per-person, and map cleanly onto `CODEOWNERS` later (`@thelearnhub/backend`).

### Step 4 — Create the repos
Create three repositories inside the org (**New repository**, owner = `thelearnhub`):
1. `standards` — private or public, your call. Add `adr-template.md`, `feature-spec-template.md`, `runbook-template.md`, `codeowners-convention.md`, and a `reusable-ci-workflows/` folder.
2. `commercehub` — the backend monorepo. Push `CommerceHub_Master_Plan.md` into `docs/` as the first commit.
3. `commercehub-web` — the frontend.

### Step 5 — Protect `main` on both code repos
1. Repo → **Settings** → **Branches** → **Add branch protection rule** (or **Rulesets** on newer GitHub UIs) → pattern `main`.
2. Enable: **Require a pull request before merging**, **Require approvals** (set to 1), **Require status checks to pass before merging** (once CI exists).

### Step 6 — Add `CODEOWNERS`
Add `.github/CODEOWNERS` to `commercehub` reflecting the service split from `CommerceHub_Master_Plan.md` Section 3, e.g.:
```
/services/order-service/      @thelearnhub/engineer-a
/services/payment-service/    @thelearnhub/engineer-a
/services/inventory-service/  @thelearnhub/engineer-a
/services/product-service/    @thelearnhub/engineer-b
/services/cart-service/       @thelearnhub/engineer-b
/services/auth-service/       @thelearnhub/engineer-b
/platform/                    @thelearnhub/engineer-b
```

### Step 7 — Org profile README
1. Create a repo literally named `.github` inside the org (owner = `thelearnhub`, repo name = `.github`).
2. Add `profile/README.md` with a trimmed version of Sections 1–3 of this document — GitHub renders it automatically as the org homepage.

---

## Next Step

Create the org and the `standards` repo first — everything else, including CommerceHub, builds on it. Once a GitHub connector is available in a session, I can create repos, branch protection rules, and CODEOWNERS files directly instead of this being a manual checklist.
