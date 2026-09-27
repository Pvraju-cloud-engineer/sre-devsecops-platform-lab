# Resume-to-Lab Roadmap

## Purpose

Use the resume as the topic map for a hands-on SRE, DevOps, and DevSecOps learning program. We will build a working analogue from a small service to a multi-service, multi-tenant, observable, automated platform. Every stage includes study, implementation, verification, a controlled failure, a runbook, and interview practice.

This public repository contains generic lab material only. Do not copy employer/client names, resume contact details, private logs, production diagrams, real incident records, account IDs, keys, or credentials into it. The lab demonstrates the same engineering patterns at a learning scale; resource count and traffic scale are not the target.

## How we will work each day

1. Review the previous day from memory, then correct gaps.
2. Watch the selected references before the assignment and note questions.
3. Receive one bounded work ticket with impact, acceptance criteria, security/cost limits, verification, and rollback.
4. Build the smallest useful increment from repository files and explain each command.
5. Run tests and operational checks; record actual outputs and exit codes, never assumed results.
6. Reproduce one safe failure, diagnose from evidence, mitigate, and prove recovery.
7. Update the day note, runbook, architecture, and interview answers.
8. Commit to this personal repository. Decommission temporary resources and verify cleanup.

## Progressive build stages

### Stage 0 — Foundations and safe lab practice

Linux and Windows administration, shell/Bash, basic Python, Git, networking, DNS, HTTP/TLS, process/memory/disk tools, Java, Maven lifecycle, and incident evidence. Set AWS budget alerts, tagging, least privilege, SSH source restrictions, and a cleanup checklist before cloud builds.

**Exit evidence:** explain a process/listener/request path; make a reviewed Git change; capture a sanitized diagnostic bundle; identify every lab resource and its owner/cleanup plan.

### Stage 1 — Spring Boot service and durable data

Rebuild the Java/Spring Boot REST baseline with Maven Wrapper, health endpoints, validation, tests, PostgreSQL, schema management, environment-based configuration, and synthetic records. Explain pom.xml, application configuration, source/test directories, controller/service/persistence boundaries, HTTP status codes, SQL and connection pooling.

**Scenarios:** app process down, wrong port, bad configuration, DB unavailable, invalid request, schema mismatch, connection exhaustion, restart and data checks.

### Stage 2 — Multi-tenant application and data isolation

Multi-tenancy is a first-class design requirement, not a late dashboard label.

- Define tenant identity from authenticated context; never trust an arbitrary caller-supplied tenant ID as authorization.
- Compare shared tables with tenant key, schema-per-tenant, and database-per-tenant: cost, isolation, migrations, onboarding, noisy-neighbor behavior, and operations.
- Start with an explicit tenant-aware API and persistence path. Enforce tenant scope in every read, write, update, and background job.
- Add negative tests proving Tenant A cannot read or mutate Tenant B records; test missing/invalid tenant context and privileged support access.
- Evaluate PostgreSQL row-level security as defense in depth; test transaction/session context resets with connection pooling.
- Carry tenant attribution through logs, metrics, traces, queue messages, audit events, dashboards, and incident investigation without putting sensitive payloads in telemetry.
- Prevent unbounded metric cardinality: use approved tenant tiers/cohorts or filtered views, and limit raw tenant IDs in metric labels.
- Add tenant quotas, rate limits, fair queue handling, data retention, deletion/export workflow, and onboarding/offboarding checks.
- Document the threat model, operator access, audit trail, key/secret boundaries, backup/restore implications, and noisy-neighbor controls.

**Exit evidence:** cross-tenant negative integration tests pass; reviewers can trace how tenant context is authenticated, propagated, enforced, observed, and audited.

### Stage 3 — Docker and software supply chain

Build a multi-stage Dockerfile, minimal non-root runtime, immutable tags, health checks, resource limits, and reproducible image. Publish a sanitized image to the personal Docker Hub namespace or AWS ECR after the account is configured. Scan source/dependencies and images with SonarQube and Trivy; triage severity, false positives, and remediation. Learn SBOM, image provenance, secret scanning, and patch workflow.

**Scenarios:** image build failure, vulnerable base layer, wrong architecture/tag, container exits, non-root permission issue, secret accidentally staged (practice only with a dummy value), rollback to a prior image.

### Stage 4 — CI/CD and release governance

Create a declarative Jenkins pipeline for Maven compile/test/package, quality gates, image build/scan, artifact publication, and deployment approval. Add or compare GitHub Actions. Use branch/review rules and keep credentials in a credentials store. Promote the same versioned artifact across environments. Capture release evidence and rollback.

**Scenarios:** flaky test, failed quality gate, registry auth denied, partial deploy, bad config, rollback and verification.

### Stage 5 — AWS foundations and Terraform

Provision only the needed lab resources with modular Terraform: VPC/subnets/routing, security groups, IAM roles/policies, S3, ECR, and later ECS/EKS/RDS as budget and learning objectives require. Review plan before apply, separate environments, tag resources, use remote state with locking, protect state access, and test drift/destroy behavior. Learn CloudWatch and AWS networking/IAM fundamentals.

Use a dedicated personal account/region. No claim of free-tier coverage: estimate before apply and check billing afterward. Avoid expensive always-on networking or clusters unless the session explicitly needs them.

**Scenarios:** invalid plan, state lock, drift, least-privilege denial, route/security-group issue, failed apply recovery, orphaned resources, state backup/restore.

### Stage 6 — Kubernetes, Helm, GitOps, and cluster operations

Start with a local Kubernetes cluster to learn Pods, Deployments, Services, ConfigMaps/Secrets, RBAC, namespaces, probes, requests/limits, HPA, disruption budgets, network policy, ingress, rollout, and rollback. Package the service with Helm and deploy by GitOps using Argo CD, reconciliation, drift detection, sync policy, and safe self-healing. Move to EKS for planned cloud exercises; compare ECS. Treat AKS as a separate later exercise if a personal Azure subscription and budget are available.

**Scenarios:** CrashLoopBackOff, ImagePullBackOff, readiness failure, OOMKilled, pending pod, bad rollout, HPA not scaling, DNS/service discovery failure, node drain, secret/config mismatch, GitOps drift.

### Stage 7 — Asynchronous processing and workload scaling

Add a producer, Amazon SQS queue, and worker. Define idempotency, retry/backoff, visibility timeout, dead-letter queue, poison-message handling, ordering needs, and data consistency. Use HPA for resource signals and evaluate KEDA queue-depth scaling where supported. Keep concurrency bounded so scale-out does not overwhelm PostgreSQL. Propagate trace/correlation context through messages and preserve tenant boundaries in queue authorization and payload handling.

**Scenarios:** growing backlog, consumer crash, duplicate delivery, poison message, DLQ growth, queue permission failure, scale-to-zero cold start, DB saturation from too many workers.

### Stage 8 — Multi-tenant observability and SLOs

Instrument Java services with OpenTelemetry. Use an OTel Collector for routing; Prometheus/PromQL metrics, Grafana dashboards/alerts, Loki/LogQL logs, Fluent Bit collection, and Tempo traces. Learn AWS CloudWatch and compare Datadog where access permits. Practice Splunk SPL dashboards/searches for incident triage; use a local/mock workflow if enterprise Splunk is unavailable. Correlate traces, logs, and metrics with request/tenant context while controlling PII, secrets, access, retention, and cardinality.

Define SLIs for request success, p95/p99 latency, and service/dependency availability; set an explicit SLO and error-budget policy. Alert on user impact and actionable symptoms, not every resource fluctuation. Build dashboards from an empty folder and explain every panel, query, label, threshold, and operator action.

**Scenarios:** elevated 5xx, latency regression, JVM memory/GC, missing trace context, noisy/high-cardinality tenant labels, dropped logs, collector backpressure, false-positive alert, SLO burn alert.

### Stage 9 — On-call, incident response, and recovery

Practice a full PagerDuty/ServiceNow/Jira-style workflow using lab tickets or simulated notifications: detect, assess severity and impact, assign incident lead/responders/comms, establish timeline, mitigate safely, update stakeholders, verify user recovery and data integrity, and complete a blameless review with owned follow-ups. No real employer incident or personal/customer data goes into the public repo.

**Resume-aligned technical drills:** OOMKilled container, JVM heap exhaustion, thread deadlock, database connection pool starvation, failed batch/worker, SQS backlog, bad release, expired/invalid certificate, and telemetry gap. Learn Linux and Windows service diagnostics, Bash/Python automation, log collection, and evidence preservation.

**Exit evidence:** sanitized incident timeline, diagnostic commands, hypothesis/evidence table, mitigation rationale, recovery signal, customer-impact statement, and tracked corrective actions.

### Stage 10 — Capacity, cost, configuration management, and resilience

Use Kubecost or a local cost model to inspect requests/limits, utilization, idle capacity, and namespace/tenant allocation. Right-size with before/after evidence and no SLO regression. Use Ansible for repeatable Linux/Windows baseline configuration, patching, access settings, and drift remediation; document idempotency and rollback. Practice backup/restore, regional recovery design, dependency timeouts, rate limits, load shedding, and controlled failover. Multi-cloud DR between AWS and Azure is a later architecture exercise, not a free-tier assumption.

**Scenarios:** noisy neighbor, node/zone loss, capacity saturation, cost spike, configuration drift, failed patch, backup restore failure, and DR cutover/return.

### Stage 11 — Integrated operating exercise

Operate the complete lab as a small service platform: tenant-aware Java APIs, PostgreSQL, queue/worker, Docker images, Jenkins or GitHub Actions, security gates, Terraform, Helm, Argo CD, Kubernetes, dashboards, alerts, runbooks, SLOs, and cost controls. Add services only when a clear boundary exists. Run a release, introduce an incident, coordinate response, recover, review, improve, and tear down temporary cloud capacity.

## Resume topic coverage checklist

| Resume area | Lab evidence we will build |
| --- | --- |
| 24/7 production support and on-call | simulated rota, incident tickets, severity, timeline, recovery, postmortem |
| Multi-tenant telemetry and SLI/SLO | tenant isolation model, safe attribution, dashboards, PromQL/LogQL/SPL, SLO/error budget |
| AWS EKS/ECS/VPC/IAM/S3/RDS/ECR/CloudWatch | staged Terraform and runtime labs with cost/cleanup evidence |
| Azure AKS and multi-cloud | later comparative cluster/DR exercise when account and budget are ready |
| Java/Spring Boot, Maven | reproducible app, tests, health, config, database and JVM diagnostics |
| SQS queue-depth scaling, HPA/KEDA | queue worker, backlog metrics, bounded scaling and failure drills |
| OpenTelemetry, Tempo, Prometheus, Grafana, Loki, Fluent Bit | end-to-end telemetry, dashboards and trace/log/metric triage |
| Splunk, Datadog, CloudWatch | query/dashboard exercises where access is available; otherwise documented equivalent practice |
| Jenkins Declarative, GitHub Actions, Argo CD, Helm | CI quality/security pipeline, versioned deploy and GitOps reconciliation |
| Docker multi-stage/non-root, SonarQube, Trivy, ECR | hardened image, quality/security gate, remediation and promotion |
| Terraform modules and remote state | reviewed plan/apply, remote state/locking, drift and teardown |
| Ansible, Bash, Python, Linux/Windows administration | idempotent configuration and diagnostic automation |
| PagerDuty, ServiceNow, Jira, SOPs/runbooks | simulated workflow, escalation record, operational docs and handoff |
| Kubecost and compute optimization | cost baseline, right-sizing change, after-checks and SLO guardrail |

## Daily report format

Each docs/days/PROJECT_DAY_NN.md should include assignment, video/reference links, prerequisites, architecture/data flow, files changed and why, commands with purpose, actual test/health evidence, troubleshooting scenario, root cause supported by evidence, rollback/recovery, security/cost impact, interview Q&A, memory-recall quiz, and next assignment.

## Completion standard

A topic is learned when you can explain it without notes, build or change it from a clean checkout, verify expected behavior, diagnose a controlled failure from evidence, recover safely, write/update an operator runbook, discuss trade-offs, and answer follow-up interview questions. We will mark each resume topic as planned, in progress, practiced, or demonstrated; the roadmap itself is not evidence that a capability has been implemented.
