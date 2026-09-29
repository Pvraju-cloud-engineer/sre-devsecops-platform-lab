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


## Preparation for similar mid-level SRE and DevOps job descriptions

Use each job description as a coverage checklist. Resume-aligned subjects remain the core curriculum; the additional subjects below extend that curriculum to comparable roles. For each requirement, prepare to describe the design, implement a lab-sized version where practical, operate it, troubleshoot a failure, and explain trade-offs.

### Core responsibilities and evidence

- **End-to-end delivery pipelines:** build and troubleshoot Jenkins Declarative and GitHub Actions pipelines for compile/test, quality and security gates, image publication, deployment approval, health verification, and rollback. Compare AWS CodePipeline and GitLab CI by mapping their stages, credentials, artifacts, permissions, and failure handling.
- **Multi-account AWS and Terraform:** build reusable modules and environment roots; understand account boundaries, role assumption, provider aliases, least privilege, remote state per environment, locking, state recovery, drift, module versioning, plan review, and teardown. If multiple personal accounts are unavailable, demonstrate the account-boundary design with isolated configurations and test plans; do not create accounts or incur charges just for the exercise.
- **AWS services:** include EC2, VPC, S3, RDS, IAM, Route 53, Lambda, ECR, ECS, EKS, CloudWatch, CloudTrail, SQS, and Elastic Disaster Recovery (DRS) concepts. For every service, explain its job, identity/network path, failure signals, security boundaries, cost drivers, and cleanup.
- **Containers and compute choices:** build Docker images; deploy the same application to local Kubernetes/EKS and ECS, including Fargate task/service concepts. Compare scheduling, scaling, networking, IAM, health checks, rollout, logs, costs, and operational responsibility. Use local labs first and only create cloud capacity for a bounded, cost-reviewed exercise.
- **Kubernetes service mesh:** learn Istio concepts—sidecars/data plane and control plane, traffic routing, retries/timeouts, circuit-breaking concepts, mTLS, identity/policy, telemetry, and mesh failure modes. Practice on a local cluster before considering managed cloud deployment; explain when a mesh adds enough value to justify its complexity.
- **Observability and governance:** build dashboards and actionable alerts using Prometheus/Grafana and CloudWatch; instrument with OpenTelemetry; practice Datadog and Splunk query/dashboard concepts where access exists. Add CloudTrail audit-event investigation, identity/change correlation, retention/access boundaries, and evidence handling. AppDynamics is a comparative APM vocabulary exercise if no lab access exists.
- **Reliability and recovery:** define SLIs/SLOs/error-budget policy, multi-AZ availability, autoscaling bounds, health checks, safe rollback, backup/restore, RTO/RPO, and game-day procedures. For AWS DRS, be able to explain source/staging/target roles, readiness checks, drill vs recovery, failover/failback, validation, ownership, and cost. Only execute a real recovery drill in a personal environment with an explicit cost plan; otherwise use a tabletop and local fault-injection simulation.
- **Automation and toil reduction:** keep a toil register; select repetitive, error-prone operational work; automate with Bash/Python/Ansible (and PowerShell where relevant); add input validation, idempotency, logging, least privilege, dry-run/check mode, tests, safe rollback, and measured before/after effort or risk.
- **Data and API plus-skills:** strengthen SQL query plans, indexes, transactions, connection pools, and query optimization using PostgreSQL. Learn PL/SQL syntax and Oracle-specific troubleshooting as a comparison if the role requires it. Compare Apigee/API gateway capabilities—authentication, quotas, routing, policies, analytics, and failure behavior—with the lab's API ingress/gateway; enterprise access is not assumed.
- **CloudFormation and IaC choices:** implement or read a small CloudFormation stack and compare it with the equivalent Terraform module: state model, drift, reuse, change preview, secrets, rollback, and team workflow. Ansible stays the configuration-management track for host baselines and repeatable recovery.
- **Advanced operations foundation:** deepen Linux process, memory, storage, systemd, networking, TLS, DNS, permissions, and performance diagnosis. Write tested Bash/Python utilities and practice PowerShell command/service/log diagnostics when the target role uses Windows.

### Job-description scenario drills

- Pipeline: deployment is green but the service is unhealthy; find the missing gate, use the prior artifact, roll back, and show recovery.
- Terraform: a shared module change affects several environments; review the plan, isolate state, protect production data, and recover safely from a lock or partial apply.
- Multi-account AWS: a workload cannot read its bucket or assume its deployment role; trace caller identity, trust policy, permissions, region, endpoint, and audit event.
- EKS/ECS: pods or tasks fail readiness, cannot pull an image, lack permissions, or scale beyond DB capacity; diagnose the relevant scheduler, network, identity, and service signals.
- Istio: mTLS policy or retry behavior causes elevated latency/5xx; isolate control-plane/data-plane configuration, prevent retry amplification, and roll back safely.
- Observability/governance: a change preceded an outage; correlate deployment, CloudTrail, application metrics, traces, logs, and user impact while protecting sensitive data.
- SLO/DR: availability is degrading or a region is unavailable; declare impact, use the recovery objective, choose rollback/failover, validate data and service health, and communicate status.
- Toil: a recurring manual task causes delays or configuration drift; quantify the problem, automate with guardrails, test failure paths, and measure the improvement.
- Database/API: latency rises after traffic growth; use query plans, pool metrics, traces, and API gateway signals to find and verify the bottleneck.

### Interview simulation for this role profile

We will rotate through resume deep-dive, Linux/scripting, AWS networking/IAM, Terraform module/state design, CI/CD release, Docker/EKS/ECS/Istio, observability/SLO, incident/DR, system design, and behavioral/ownership rounds. Some sessions will be a timed live-debugging exercise; others will require a whiteboard explanation, command-writing, or a design review. Follow-ups will test alternatives, failure handling, security, cost, and what evidence proves the result.

A topic is interview-ready when you can explain it from memory, build or inspect the lab implementation, diagnose a new failure from evidence, state a safe recovery and trade-offs, and answer follow-up questions. For tools that are optional, proprietary, or expensive, be clear about what you implemented hands-on and what you learned comparatively.


## Daily resume-to-learning traceability

**This is the governing rule for every day:** each new topic, command, file, build, drill, and interview question must connect to a responsibility or skill named in the resume. SRE is the primary career track; DevOps and DevSecOps are included as complementary work and as the earlier phase of the resume.

Each daily report must include a resume mapping table with these columns:

| Resume responsibility/skill | What this day teaches | Repo artifact or command | Evidence actually observed | Status / next practice |
|---|---|---|---|---|

Use status words precisely: **planned** means not started; **built** means files were created; **verified** means the stated check passed; **practiced** means the failure drill was actually run and its evidence recorded. A topic is not interview-ready just because a file mentions it.

### Current day-by-day map

| Day | Resume responsibility/skill being practiced | Learning/build connection | Evidence and boundary |
|---|---|---|---|
| Day 1 — LAB-001 | SRE foundation: understand a service, check availability, investigate a stopped process, verify persistence; supports production support and incident triage. DevOps foundation: repeatable Java/Maven build and local dependency setup. | Linux/EC2 baseline → HTTP request → Spring Boot controller/repository → PostgreSQL; tests, health check, API/SQL verification, API-stop troubleshooting. | The Day 1 report records the fresh rebuild, test/API/SQL checks, and observed stop/recovery drill. This is a single-host practice service; it teaches request-path and triage fundamentals. |
| Day 2 — LAB-002 | SRE: separate service and dependency health, diagnose a DNS/build failure by evidence, verify recovery. DevOps/DevSecOps: container build, Compose networking, non-root runtime, local secret exclusion. | Dockerfile multi-stage build → API and database containers → Compose DNS/health/volume → HTTP and SQL verification; Buildx and wrong-host drills. | The Day 2 report records successful image/runtime checks, API and DB evidence, and the two completed drills. The Docker practices build toward containerized microservice operations and hardened image basics. |
| Day 3 — LAB-003 | SRE primary: observability, useful dashboards, operational signals, safe monitoring triage and runbook thinking. | Spring metrics → Prometheus scrape target → queries → Grafana data source/dashboard → missing-target drill. | **Planned only** until implemented and verified. Tomorrow's prework defines the learning and acceptance checks; the Day 3 report must replace planned status with actual evidence. |

### Resume topics to connect in later increments

| Resume area | Planned lab progression | SRE / DevOps connection |
|---|---|---|
| Production support, incidents, on-call, batch monitoring/recovery | Repeatable incident tickets, batch-job failure/replay drills, runbooks, handoffs, post-incident actions | SRE owns impact assessment, mitigation, recovery evidence, and learning; service/platform teams coordinate fixes. |
| Grafana, Prometheus, Splunk/APM, centralized telemetry | Metrics/dashboard first; then structured logs, correlation fields, trace context, queries, alerting | SRE turns signals into triage and response; DevOps makes agents/configuration repeatable. |
| SLI, SLO, error budget, burn rate, MTTR | Define user-facing indicators from observed service behavior; practice alert/noise and incident decisions | SRE uses reliability objectives to prioritize availability work and changes; metrics must have clear definitions and windows. |
| AWS, Terraform, networking, IAM, multi-account infrastructure | Build small isolated infrastructure, reusable modules, state/plan/review, identity and network troubleshooting | DevOps/platform automates infrastructure; SRE verifies service reliability, access boundaries, recovery and operational readiness. |
| Jenkins, GitHub Actions, release governance, blue/green/canary, rollback | Add tested pipeline stages, image tagging/scanning, deployment gates, controlled rollout and rollback | DevOps owns delivery automation; SRE defines health signals, release safety and rollback evidence; security owns policy partnership. |
| Docker, Kubernetes/EKS/AKS, autoscaling, Karpenter, multi-tenancy | Progress from Compose to Kubernetes workloads, probes, requests/limits, scheduling, RBAC/network policy, scaling and tenant isolation | Platform/DevOps builds the paved road; SRE troubleshoots scheduling, saturation, dependency limits and tenant impact. |
| SQS, KEDA, asynchronous/batch processing | Add queue/worker lab, backlog and age signals, retry/idempotency, safe replay and scaling limits | SRE protects latency/error objectives and databases; application/platform owners define processing semantics and capacity. |
| DR, multi-region, cost optimization/Kubecost | Recovery objectives, restore/failover drills, capacity and cost baselines, rightsizing trade-offs | SRE owns recovery readiness and service objectives; DevOps automates repeatable infrastructure; cost changes must be measured. |
| DevSecOps: Jenkinsfiles, Trivy, SonarQube, Terraform modules, Docker hardening | Add secure build stages, dependency/image scanning, quality gates, secret handling, infrastructure review | Security policy is shared with security partners; DevOps integrates gates; SRE considers release risk and operational impact. |

These are roadmap topics, not completed work. We add them only when the previous increments are understood and the repo has a bounded ticket, build steps, evidence, troubleshooting, interview practice, and cleanup instructions.

### Daily report review questions

At closeout, answer all of these in that day's report:

1. Which exact resume responsibility does this ticket practice, and why is it relevant to an SRE-first role?
2. What did I personally learn, build, run, and verify today? Link the files and commands.
3. What symptom or failure did I investigate, what evidence identified the fault boundary, and how did I verify recovery?
4. Which parts were only discussed or planned and therefore still need hands-on practice?
5. How would I explain this work in an interview without overstating scope or production ownership?
6. What is the next resume topic, and what prerequisite from this day does it use?
