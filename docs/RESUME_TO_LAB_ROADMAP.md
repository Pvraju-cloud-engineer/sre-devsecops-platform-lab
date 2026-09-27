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


## Mastery and interview practice standard

The goal is to build, operate, explain, and troubleshoot the resume-aligned systems from a clean start. We will study the real patterns in the resume and reproduce them at lab scale; every capability stays marked planned, in progress, practiced, or demonstrated until the code, commands, and evidence support the mark.

### Required daily practice

Every `docs/days/PROJECT_DAY_NN.md` must contain:

1. Resume skill and subtopics covered today, with a link to the reference videos or official documentation watched before the assignment.
2. Work ticket: user or business impact, scope, acceptance criteria, security and cost boundaries, rollback, and handoff.
3. Architecture and request/data flow; files changed and the reason for each.
4. Commands and expected meaning, followed by actual output or a clear note that a step was not run.
5. Verification: tests, health checks, dashboards, logs, traces, or cloud state that prove the result.
6. At least one safe failure drill, with symptoms, evidence collected, hypotheses, diagnostic steps, root cause, mitigation, recovery proof, and follow-up action. The number and difficulty of drills grow as the platform grows.
7. Interview practice: 5 recall questions from prior days, 3 fundamentals for today's topic, 2 troubleshooting/scenario questions, 1 design/trade-off question, and 1 ownership/communication question. Write answers in your own words and tie them to the lab evidence.
8. Cost/security review, cleanup/decommission evidence, status of each skill, and next-day handoff.

Use this answer pattern: clarify impact and scope; explain the system path; identify signals and commands; separate facts from hypotheses; state the safest mitigation; prove recovery; explain prevention and trade-offs. For experience questions, describe your role and decisions accurately and use only evidence produced in the lab or verified from your own work history.

### Full topic and subtopic checklist

#### Linux, operating systems, networking, and scripting

- Linux filesystem, permissions, users/groups, sudo, processes, signals, services/systemd, packages, environment, cron, logs/journald, file descriptors, sockets, CPU, memory, swap, disk, inode, and limits.
- Shell/Bash quoting, pipes, redirection, exit codes, loops, functions, traps, safe scripting, text tools, SSH, and command-line diagnostics.
- Python for APIs, JSON/YAML, files, subprocesses, retries/timeouts, logging, tests, and safe automation.
- DNS, TCP handshake, routing, subnet/CIDR, ports, firewalls/security groups, HTTP methods/status codes/headers, TLS certificates/handshake, proxies, load balancers, connection timeouts, and packet/request flow.
- Windows service/process/event-log/network diagnostics where relevant to the resume.

#### Java, Spring Boot, APIs, and data

- Java runtime/JVM memory model at an operator level, heap vs non-heap, garbage collection, thread pools, deadlocks, heap/thread dumps, startup flags, and container-aware resource sizing.
- Maven Wrapper, `pom.xml`, dependency scopes, lifecycle, plugins, profiles, reproducible builds, tests, packaging, and dependency troubleshooting.
- Spring Boot configuration precedence, profiles, dependency injection, controller/service/repository boundaries, REST/JSON, validation, exception handling, status codes, Actuator health/readiness/liveness, logging, and graceful shutdown.
- PostgreSQL schemas, tables, indexes, constraints, transactions, isolation, migrations, connection pools, locks, query plans, backups/restores, credentials, and failure diagnosis.
- Multi-tenancy: shared tables with tenant keys vs schema-per-tenant vs database-per-tenant; authenticated tenant context; authorization vs identity; isolation on every query and background job; PostgreSQL RLS; pooled-connection context reset; cross-tenant negative tests; migrations; tenant onboarding/offboarding; retention/export/deletion; audit/support access; quotas and noisy-neighbor controls.

#### Docker, supply chain, and DevSecOps

- Image/layer/container model, build context, `.dockerignore`, multi-stage builds, non-root runtime, minimal base images, ports, health checks, signals, volumes, networks, resource limits, tags/digests, registry authentication, and image rollback.
- SonarQube quality gates, Trivy source/dependency/image scanning, CVE triage, SBOM, secret scanning, remediation, false positives, patch cadence, and artifact provenance.
- Secret handling, least privilege, IAM/RBAC, encryption in transit/at rest, dependency pinning, audit evidence, and secure defaults.

#### CI/CD, release, and GitOps

- Git fundamentals, branches, commits, pull requests, reviews, conflict resolution, tags, release notes, and rollback commits.
- Declarative Jenkinsfile: stages, agents, environment, credentials binding, artifacts, test reports, approvals, parallel work, failure handling, and workspace cleanup.
- GitHub Actions: workflow triggers, jobs/steps, permissions, secrets, artifacts, caching, environment protection, and pipeline debugging.
- Build-once/promote-same-artifact, versioning, deployment strategies, health gates, change records, separation of duties, rollback criteria, and release verification.
- Helm chart structure, values, templates, upgrades, rollback; Argo CD desired state, reconciliation, sync health, drift, self-heal, and safe promotion.

#### AWS, Terraform, and cloud foundations

- IAM users/roles/policies, trust policies, STS, least privilege, VPC, CIDR, public/private subnets, route tables, internet/NAT gateways, DNS, security groups/NACLs, endpoints, and load balancers.
- EC2, ECR, S3, RDS PostgreSQL, ECS, EKS, CloudWatch, SQS, KMS, secrets, logs/metrics, backups, and cost dimensions used by the lab.
- Terraform providers/resources/data/modules/variables/outputs, formatting/validation/plan/apply/destroy, state, remote S3 backend and locking, state sensitivity, imports, drift, dependency graphs, workspaces/environment separation, module versioning, and recovery from interrupted changes.
- AWS budgets/alerts, tags, region/account boundaries, shared responsibility, quota checks, free-tier limits, cost estimates, orphan detection, and teardown verification.

#### Kubernetes, containers, and platform operations

- Cluster/control plane/node/pod architecture; namespaces; labels/selectors; Deployments/ReplicaSets/StatefulSets/Jobs/CronJobs; Services/Ingress; DNS; ConfigMaps/Secrets; storage/PVC; RBAC; service accounts; network policy.
- Requests/limits, scheduling, probes, restart/backoff, rollout/history/rollback, node drain/disruption budgets, events, logs, exec, autoscaling, affinity/taints/tolerations, and capacity.
- Local cluster before managed EKS; EKS identity/network/node groups and operational boundaries; compare ECS. AKS and multi-cloud recovery are later comparative exercises when access and budget allow.
- Helm and Argo CD operations, GitOps drift, safe sync, deployment health, and progressive delivery concepts.

#### Asynchronous systems, scaling, and multi-tenant workload fairness

- SQS producer/consumer, visibility timeout, at-least-once delivery, idempotency, deduplication, retry/backoff, poison messages, DLQ/redrive, ordering, consistency, and trace/correlation propagation.
- Queue age/depth/throughput, consumer concurrency, database connection budget, backpressure, rate limits, load shedding, HPA metrics, KEDA triggers, scale-to-zero trade-offs, and lag recovery.
- Tenant queue authorization and context, per-tenant quotas/fairness, noisy-neighbor isolation, and ways to avoid cross-tenant message/data exposure.

#### Observability, dashboards, and SLOs

- Metrics/logs/traces and events; instrumentation vs collector vs backend; OpenTelemetry API/SDK/Collector; context propagation; sampling; semantic conventions; exporters; collector pipelines/backpressure.
- Prometheus scrape/labels/recording rules/PromQL; Grafana dashboard variables/panels/annotations/alerts; Loki/LogQL; Fluent Bit parsing, buffering, routing; Tempo trace search/correlation; CloudWatch; Splunk SPL; Datadog concepts.
- RED/USE signals, golden signals, request rate, errors, p50/p95/p99 latency, saturation, JVM and DB signals, queue lag, cardinality, retention, access controls, and privacy-safe tenant attribution.
- Define SLIs, SLO windows/targets, error budgets, burn-rate alerts, alert quality, dashboards from an empty folder, panel/query rationale, and operator actions.

#### SRE operations, incidents, capacity, cost, and resilience

- Incident severity, on-call handoff, roles, communication cadence, timeline, customer impact, escalation, mitigation vs root cause, evidence preservation, blameless review, action owners, and runbook quality.
- Alert triage, P1/P2 workflow, service dependencies, change correlation, rollback, feature disablement, traffic shaping, recovery criteria, and post-incident follow-up.
- OOMKilled/heap/GC/thread deadlock/CPU throttling/connection pool exhaustion/DNS/TLS/5xx/latency/failed batch/SQS backlog/telemetry loss/permission denial/node or zone loss.
- Capacity planning, load testing, utilization, HPA/KEDA behavior, Kubecost allocation, right-sizing with SLO guardrails, backup/restore, RTO/RPO, DR/failover/return, and dependency timeouts.
- Ansible inventory/playbooks/roles/handlers/variables/idempotency/check mode, Linux/Windows baseline configuration, patching, drift detection, and rollback.
- PagerDuty/ServiceNow/Jira-style ticket, notification, escalation, change, incident, and problem workflows using simulated lab records.

### Scenario progression

- Foundation: command returns unexpected output, wrong working directory, permission denied, service not listening, DNS resolution failure, wrong port, disk full, process exit, and malformed HTTP request.
- Application/data: bad profile, missing environment variable, failed health probe, exception/5xx, slow query, lock wait, schema mismatch, DB down, pool exhaustion, and restart with persistence check.
- Tenant/security: missing or forged tenant context, cross-tenant read/write attempt, stale pooled connection context, queue message with wrong tenant, leaked sensitive field in logs, excessive metric cardinality, and overbroad operator permission.
- Container/release/cloud: build context mistake, non-root permission failure, image pull/auth error, vulnerable dependency, failed quality gate, Terraform drift/state lock/partial apply, IAM denial, bad route/security group, failed rollout, and rollback.
- Kubernetes/async/telemetry: Pending pod, CrashLoopBackOff, OOMKilled, readiness failure, service DNS issue, HPA/KEDA not scaling, growing SQS age, duplicate/poison message, DLQ growth, collector backpressure, missing trace, dropped logs, and false alert.
- Production-style exercise: combine a bad release with a latency/SLO burn, tenant impact, queue growth, or DB saturation; coordinate roles, communicate impact, mitigate, verify recovery, and record actions.

Each scenario must be reproducible and safe. Start from observed evidence, change one variable at a time where possible, protect data, avoid uncontrolled load, and clean up injected faults. Complexity grows from one host to containers, local Kubernetes, and carefully scoped cloud exercises.

### Interview-round coverage

- Resume/deep dive: explain each resume bullet with a clear system flow, your responsibility, tools, decisions, operational evidence, outcome, and limits of what you personally did.
- Fundamentals: Linux, networking, Git, Java/Maven, Docker, AWS, Terraform, Kubernetes, SQL, security, and telemetry.
- Live troubleshooting: narrate impact, inspect signals, form/test hypotheses, mitigate safely, and verify recovery while sharing concise updates.
- System design: service boundaries, multi-tenant isolation, availability, scaling, failure domains, data consistency, observability, security, cost, and DR trade-offs.
- Automation/coding: write/read Bash or Python helpers, explain edge cases, test behavior, handle errors, and protect secrets.
- CI/CD and DevSecOps: pipeline stages, quality/security gates, artifact integrity, release approvals, deployment safety, and rollback.
- SRE/on-call: SLI/SLO/error budgets, alerting, incident command, postmortems, capacity, toil reduction, and reliability trade-offs.
- Behavioral/ownership: preparation, coordination, disagreement, escalation, learning from an incident, prioritization, and measurable evidence; use truthful examples from verified experience and the lab.

No finite video list guarantees passing every interview. References prepare each day's work; the mastery evidence is that you can rebuild it, debug it under a scenario, explain alternatives, and answer follow-ups without relying on memorized scripts.
