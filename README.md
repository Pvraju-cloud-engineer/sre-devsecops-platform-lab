# SRE and DevSecOps Platform Lab

A client-neutral, hands-on learning project for building and operating a distributed service platform. Work proceeds in daily tickets: study, design, build, troubleshoot, recover, document, review, and clean up.

The first Spring API is one small runtime slice. The target is a multi-service platform that grows through CI/CD, cloud infrastructure, Kubernetes, security controls, observability, incident response, and cost management.

## Current checkpoint

Day 1: connected to an Amazon Linux host with PuTTY, ran a Spring Boot API with the Maven Wrapper, started PostgreSQL 16 in Docker with a named volume, checked database readiness and application health, submitted an API request, and verified the stored row in PostgreSQL. See [Day 1 notes](docs/days/PROJECT_DAY_01.md). The application source has not yet been added to this repository.

## Repository layout

- `docs/days/` — daily pre-study, work tickets, evidence, troubleshooting, interview Q&A, and reports.
- `docs/architecture/` — target architecture and decisions.
- `services/` — Java/Spring services, added after source review.
- `ops/postgres/` — local database setup and operations.
- `pipelines/` — Jenkins or GitHub Actions delivery automation.
- `platform/` — Terraform and Kubernetes platform definitions.
- `deploy/` — Helm and GitOps delivery.
- `observability/` — metrics, logs, traces, dashboards, and alerts.
- `runbooks/` — incident response, recovery, and routine operations.

## Daily work cycle

1. Study a focused video or document pack and answer its self-check questions.
2. Recall previous concepts without notes.
3. Commission only the resources needed for the ticket.
4. Implement a reviewable change and verify it with evidence.
5. Inject one controlled failure, isolate the cause, and prove recovery.
6. Record commands, decisions, resume-skill mapping, interview Q&A, and a report.
7. Review for secrets and identifying information before publishing.
8. Decommission disposable resources and protect persistent data.

## Public repository rules

Use generic project language. Never commit client names, customer data, account IDs, private IPs, credentials, private keys, `.env` files, Terraform state, or unredacted logs. Store secrets outside Git. Tag container images with immutable Git commit SHAs when image publishing is introduced.
