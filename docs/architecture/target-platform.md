# Target Platform: Progressive Architecture

This document describes the learning target in stages. The lab does not start by provisioning a large multi-cloud estate. Each component is introduced only after the previous path is understood, can be operated, and can be cleaned up.

## Stages

1. **Application baseline:** Java/Spring Boot REST API, health endpoint, request validation, and repeatable Maven build. Run locally or on a temporary EC2 lab instance.
2. **Persistence:** PostgreSQL in Docker for local practice; then evaluate a managed database only when the lab requires it. Store secrets outside source control, limit network access, and test restart/restore behavior.
3. **Container delivery:** multi-stage Dockerfile, non-root runtime, immutable tags, image scanning, and Docker Hub or a registry.
4. **CI/CD and IaC:** Git-based review, automated build/test/security checks, Terraform plan review, and environment-specific configuration.
5. **Cloud runtime:** private networking, least-privilege IAM, ingress/load balancing, TLS, health checks, controlled deployment, and cost monitoring.
6. **Kubernetes:** Deployments, Services, ConfigMaps/Secrets, probes, resource requests/limits, disruption handling, ingress, autoscaling, and rollback. Start with a local cluster; create managed clusters only for a planned lab.
7. **Async processing:** introduce a queue and worker, define retry/backoff, idempotency, dead-letter handling, visibility timeout, and backlog-based scaling.
8. **Observability and SRE:** metrics, structured logs, traces, dashboards, actionable alerts, SLIs/SLOs, error budgets, incident runbooks, and recovery tests.
9. **Resilience/cost:** load tests with bounds, dependency failures, multi-zone behavior, capacity policies, right-sizing, backup/restore, and teardown verification.

## Logical request path (later-stage target)

```text
Client
  -> DNS / TLS edge / load balancer
  -> ingress and API gateway
  -> Java service(s)
      -> PostgreSQL for durable records
      -> queue -> worker for asynchronous jobs
  -> telemetry pipeline
      -> metrics / logs / traces -> dashboards and alerting
```

Routing and TLS placement vary by deployment. In the lab, document the actual path and termination point rather than assuming every diagram applies.

## Reliability design questions

- What is the user-visible success condition and how is it measured?
- Which dependencies are on the synchronous critical path?
- What happens under a timeout, partial outage, duplicate request, or slow database?
- Are writes idempotent? How are retries bounded and jittered?
- How are database connections and worker concurrency bounded?
- What signal detects impact, and what alert has a clear operator action?
- What is the rollback, and how is recovery verified?
- What is the backup/restore objective and has restore actually been exercised?
- Which resources can accrue charges after the session, and how do we prove they are gone?

## Observability contract

Every service should expose a health signal appropriate to its role, structured logs with request correlation, and metrics for request rate, errors, latency, saturation, and dependency health. Traces should preserve trace context across service and async boundaries. Avoid high-cardinality labels and sensitive payloads. Dashboards should answer: Is the service healthy? Who is affected? Which dependency or operation is responsible? What changed?

## Learning boundary

The resume architecture is a curriculum map. We will build a smaller working analogue, record which parts are implemented versus conceptual, and never imply that the lab reproduces a production fleet or client environment.
