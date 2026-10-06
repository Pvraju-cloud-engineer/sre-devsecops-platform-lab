# Day 3 — SRE observability: metrics, Prometheus, and Grafana

## Returning after a break: restart the learning path from Day 1

Do not jump straight into LAB-003. The Day 3 prework is ready, but LAB-003 is not complete. Because this is a learning rebuild, resume in order and pass each checkpoint before moving forward. Use the committed recipe on the personal repository's main branch; cloud machines and database volumes are disposable.

1. **Day 1 refresh — docs/days/PROJECT_DAY_01.md (LAB-001):** begin with the fresh-host and repo setup steps. Explain each command before running it. Recreate PostgreSQL and the Spring API, run Maven tests, check health, create a synthetic record, GET it, confirm the database row, then practice the API-stopped drill. Write down what each signal proves and what it does not.
2. **Day 1 recall gate:** close the notes and narrate the request path, Maven test limits, Compose health check/volume, API-versus-database checks, and recovery evidence. Reopen Day 1 notes and record corrections. Move on when you can rebuild and explain the path without copying commands blindly.
3. **Day 2 refresh — docs/days/PROJECT_DAY_02.md (LAB-002):** rebuild Day 1 once from GitHub, then recreate the containerized version. Before looking at the checked-in Dockerfile, write a first draft from memory; compare it with the repo, explain every instruction, build the multi-stage image, run the API and PostgreSQL in Compose, and verify health, POST/GET, and the matching SQL row. Repeat the Docker build-tool and wrong-service-DNS troubleshooting drills; record what actually happens.
4. **Day 2 recall gate:** explain build stage versus runtime stage, build context/.dockerignore, non-root user, container DNS (postgres:5432) versus host port (127.0.0.1:5433), dependency health, persistent volume, and Buildx evidence. Correct the Day 2 notes before proceeding.
5. **Resume Day 3 — docs/days/DAY_03_PREWORK.md (LAB-003):** only after Day 1 and Day 2 checkpoints pass, rebuild the Day 2 baseline again, revise both days, then add metrics, Prometheus, Grafana, and the controlled scrape-target failure drill. Record the implementation in docs/days/PROJECT_DAY_03.md and mark the ticket complete only with observed evidence.

**Daily rule:** keep one active day/ticket. Do not mark a rebuild, test, incident drill, or interview answer complete because the instructions were read; record a command/output or a short explanation you produced yourself. If a step fails, stop at that boundary, capture safe evidence, make one change, and verify the same signal again.


**Ticket:** LAB-003 — Add service metrics and an operational dashboard  
**Status:** Ready; mark Done only after tomorrow's checks and evidence.  
**Role emphasis:** SRE first. DevOps/platform work makes the setup repeatable.

## 1. The daily learning contract

This series follows the same work cycle: learn concepts before changing files; rebuild the previous baseline from the repo; revise earlier interview topics; take one ticket; build; troubleshoot with evidence; verify; write the report; save it to the personal repo; then clean up the disposable cloud resources.

- **Day 1:** learn host/Linux basics, HTTP, Spring Boot, Maven, PostgreSQL, Compose basics, and the incident method; build and verify the API/database; practice the observed API-stop drill; revise Day 1 interview questions.
- **Day 2:** start from a clean EC2 and rebuild Day 1; revise Day 1 questions; learn Docker image/container concepts, multi-stage builds, Compose networking, and non-root runtime; containerize the same API; practice build-tool and DNS troubleshooting; record Day 2 evidence and interview answers.
- **Day 3 (this ticket):** rebuild Day 2 from GitHub; revise Day 1 and Day 2 questions; learn metrics, scrape targets, labels, dashboards, and service-level signals; add Prometheus/Grafana observability; practice a controlled monitoring failure; document evidence and Day 3 interview answers.

Every later day repeats this pattern: rebuild the previous day's committed baseline, revise all earlier recall/interview topics, study the new prerequisites, complete one ticket, build/debug safely, verify, write that day's report, and clean up EC2 after confirming the GitHub copy. The repo stores the recipe and notes; EC2 state and database volumes are temporary lab state.

## 2. Mission and why this is SRE work

As the assigned SRE, make the containerized service easier to operate: expose useful runtime measurements, collect them, and provide a dashboard that helps answer: is it up, slow, or failing? The job is not simply to install tools. The SRE selects useful signals, checks their meaning, distinguishes app health from telemetry health, and writes a runbook another on-call engineer can use.

### Acceptance criteria for tomorrow

1. Recreate the Day 2 service on fresh EC2 using only the repo and its documented steps.
2. Attempt Day 1 and Day 2 recall questions before looking at their answers; record corrections.
3. Spring Boot exposes Prometheus-format metrics only on the lab's intended local/network boundary.
4. Prometheus scrapes the API successfully; verify target state and real metric samples.
5. Grafana uses Prometheus as its data source and has a small dashboard with panels backed by observed metrics.
6. Run a safe failure drill for a missing scrape target; keep the normal API/database recoverable.
7. Record commands, explanations, evidence, troubleshooting, cleanup, role mapping, and interview answers in docs/days/PROJECT_DAY_03.md. Update the ticket index only after observed checks.

If a criterion is not met, record it as incomplete or blocked with evidence. Never invent successful output or mark an unrun drill practiced.

## 3. Roles and ownership in an organization

| Role | Owns in this ticket | Coordination |
|---|---|---|
| Engineering manager / ticket owner | Impact, priority, acceptance criteria, time/cost boundary, reviewer | Assigns and clarifies priorities/access; reviews outcome. |
| Application/service owner | Actuator setup, metric meaning, API behavior, tests, runtime requirements | Explains service semantics; coordinates behavior changes. |
| SRE — primary role practiced | Golden signal selection, dashboard/runbook usefulness, reliability interpretation, safe drill, evidence and follow-up | Works with service/platform owners; does not treat a green dashboard as proof of end-to-end success. |
| DevOps/platform engineer | Compose config, repeatable startup, service discovery, ports, versioned deployment setup | Enables a consistent stack and future promotion to CI/orchestration. |
| Security/DevSecOps partner | Endpoint exposure, secret handling, least privilege, sensitive metric data | Reviews that metrics do not expose secrets or customer/request identifiers. |
| Assigned engineer / learner | Rebuilds, implements agreed change, tests, troubleshoots, writes report/runbook, requests review | Explains evidence and unresolved risks. This lab rehearses roles; it is not a claim of production ownership. |

In a real team, the ticket owner and service owner agree on signals. SRE owns operational usefulness and response context. Platform/DevOps supports safe repeatability. Security reviews exposure and data handling. Changes go through code review and the team's release process.

## 4. Prerequisites — understand these before building

1. **Metric:** numeric measurement recorded over time, such as request count, latency, or memory. Metrics show trends; logs describe events; traces follow a request through components.
2. **Prometheus:** monitoring system that periodically pulls metrics from configured targets. A running Prometheus process does not prove an API is being scraped; check target status and samples.
3. **Scrape target:** reachable host, port, and path from Prometheus's network. Inside a Prometheus container, localhost means that same container. Use Compose service DNS and the API container's internal port.
4. **Grafana:** visualization/query UI. It does not create telemetry. A panel is useful only when its data source, query, time range, units, and labels are right.
5. **Golden signals:** latency, traffic, errors, saturation. They are a starting framework, not a full SLO. JVM/process metrics help explain pressure but do not replace user-facing request metrics.
6. **Health versus metrics:** Actuator health is a point-in-time result. Metrics are measurements across time. Neither alone proves the customer's journey works.
7. **Labels/cardinality:** labels group measurements (for example method/status). Never label by claim ID, user ID, raw URL, or other sensitive/unbounded values: that can leak data and create too many time series.
8. **SRE response:** observe symptom, check signal/source, form a hypothesis, make the smallest safe check, mitigate, verify the same signal, record follow-up. A dashboard supports this process but does not replace it.

### Learning references

- [Prometheus: Getting started](https://prometheus.io/docs/prometheus/latest/getting_started/) — targets, scrape config, first queries.
- [Prometheus: Metric types](https://prometheus.io/docs/concepts/metric_types/) — counters, gauges, histograms, summaries.
- [Grafana: Prometheus data source](https://grafana.com/docs/grafana/latest/datasources/prometheus/) — connect Grafana and query.
- [Spring Boot Actuator metrics](https://docs.spring.io/spring-boot/reference/actuator/metrics.html) — instrumentation/export endpoint.
- Optional visual introduction: [Prometheus and Grafana beginner videos](https://www.youtube.com/results?search_query=Prometheus+getting+started+metrics+Grafana). Use official docs above to verify terminology.

Study these first. Then close references and explain: who pulls metrics, where localhost points inside a container, and how a Grafana panel gets values.

## 5. Start-of-day recall — attempt before opening answers

Write answers in the Day 3 report, then check the detailed Day 1/Day 2 reports and record corrections.

1. Day 1: trace POST /claims from HTTP to the PostgreSQL row. What proves persistence?
2. Day 1: what does ./mvnw test prove, and what does it not prove about a live service?
3. Day 1: API port is unreachable while PostgreSQL is healthy. What evidence distinguishes stopped API from DB fault?
4. Day 2: why does containerized API use postgres:5432 rather than 127.0.0.1:5433?
5. Day 2: explain Dockerfile build/runtime stages, .dockerignore, Compose health check, port mapping, and named volume.
6. Day 2: what was the earliest cause of UnknownHostException in the wrong-host drill? What showed the Buildx version mismatch?
7. Day 3: how will you prove Prometheus scraped the API and Grafana is querying those samples?

The detailed explanations remain in the existing daily reports; this prework assigns recall and correction, it does not duplicate their notes.

## 6. Tomorrow's rebuild — Day 2 baseline first

**Branch choice:** Use main. It contains the Day 2 report, Dockerfile, Compose API service, and Buildx installer. Ignore old PR #7/day2-closeout directions. After this break, follow the Day 1 → Day 2 → Day 3 restart sequence at the end of this document.


1. Create disposable Amazon Linux 2023 EC2 with SSH restricted to your current IP, intended key, recorded region/type/storage/security group, and cleanup plan. SRE reason: know the environment and blast radius before operational changes.
2. SSH as ec2-user. Run whoami, hostname, cat /etc/os-release, uname -m, free -h, df -h /, and nproc. These show identity, OS, architecture, memory, disk, CPU; these affect package/build behavior and troubleshooting.
3. Install only documented prerequisites (Git, Java 21, Docker, curl, OpenSSL); start Docker with systemd. If adding ec2-user to docker group for this disposable lab, reconnect before testing because login groups are refreshed at login. Docker access is highly privileged; keep host disposable.
4. Clone only this personal repo and correct branch. Check git status --short --branch and git log -1 --oneline. This proves which committed recipe you rebuilt. Never use Cisco work repository.
5. Install/verify Compose and Buildx with bash scripts/bootstrap/install-compose-plugin.sh and bash scripts/bootstrap/install-buildx-plugin.sh. These pin versions and verify checksums rather than guessing binaries.
6. Run bash scripts/bootstrap/init-claims-api-env.sh. Confirm .env is ignored with git check-ignore -v services/claims-api/.env. Never print or commit the real .env.
7. Enter services/claims-api and run docker compose config --quiet. This catches YAML/interpolation errors before containers are created.
8. Run docker compose up -d --build --wait --wait-timeout 120, then docker compose ps and docker compose exec postgres pg_isready -U claims -d claims. This builds/starts services and checks DB readiness; Up alone is not HTTP proof.
9. Call curl -i http://127.0.0.1:8081/actuator/health. POST one synthetic claim, copy its ID, GET it, and use read-only SQL to find the same row. This confirms host-to-API routing, API-to-DB networking, and persistence before monitoring work.
10. Record commit hash, sanitized outcomes, timestamps, and actual failures in PROJECT_DAY_03.md. Do not claim rebuild success unless each check passed.

## 7. LAB-003 build sequence — add observability after baseline passes

1. **Inspect:** read pom.xml, application.properties, Dockerfile, compose.yaml; note current working behavior. Do not replace Day 1/2 functionality.
2. **Expose metrics:** add Spring Boot Prometheus/Micrometer registry dependency and configure Actuator exposure intentionally. Keep management endpoints inside intended lab boundary. Never put sensitive values in metrics.
3. **Configure Prometheus:** add checked-in scrape config targeting the claims-api Compose DNS name, internal port, and metrics path. Prometheus cannot use host 127.0.0.1 to reach another container.
4. **Add Grafana:** provision Prometheus data source and a small readable dashboard from versioned config where practical. Use stable service names, pinned image versions, and local-only host port bindings.
5. **Validate:** run Compose config validation; inspect rendered services/ports without showing secrets. Review git diff to ensure .env/token/generated DB data are absent.
6. **Verify each layer:** inspect Compose status/logs; check API health; verify Prometheus target is UP and query actual samples; verify Grafana data source and panels show those samples. Record exact query/time range/result.
7. **Safe failure drill:** in a disposable configuration, point Prometheus at invalid service name or port; observe target DOWN, inspect target error/logs, restore correct target, prove UP and samples return. Do not disrupt a production system. Mark practiced only after execution.
8. **Write response:** symptom → impact → evidence → hypothesis → narrow check → mitigation → recovery check → prevention/runbook. Distinguish API failure from telemetry collection failure.
9. **Review/close:** inspect tests, diff, secrets, ports, health, metrics, target, dashboard, and criteria. Save changes to the personal repo. Update ticket only with evidence. Clean EC2 and separately billable resources after confirming GitHub copy.

## 8. Day 3 troubleshooting command guide

| Check/command | What it tells you | Use it when |
|---|---|---|
| git status --short --branch | Branch and changed/untracked files | Before work, commit, cleanup |
| docker compose config --quiet | Compose syntax/interpolation validity | Before startup and after config edits |
| docker compose ps | Container lifecycle/health/ports | First container view when endpoint fails |
| docker compose logs --tail=100 prometheus | Prometheus startup/scrape/config errors | Target DOWN or Prometheus fails |
| docker compose logs --tail=100 claims-api | API startup/runtime errors | Exporter/app errors |
| Prometheus Targets page | Target UP/DOWN and last scrape error | Dashboard empty; check collection before query |
| Prometheus query for up | 1 indicates scrape succeeded for target; 0 indicates scrape failure | Confirm collection and target labels |
| curl -i http://127.0.0.1:8081/actuator/health | Live API HTTP health from host | Distinguish service health from telemetry |
| docker compose exec claims-api printenv DB_URL | Non-secret active JDBC URL | Verify DNS/port; never print passwords |

**Triage order:** API healthy? Metrics endpoint reachable from Prometheus container? Target listed and UP? Samples present? Grafana pointing to correct Prometheus service? Panel query/time range correct? Follow the first failed boundary; do not start by rewriting the dashboard.

## 9. Realistic scenarios and interview drill

These are lab simulations of common failure classes, not claims of employer production incidents.

1. **Dashboard empty, API healthy:** check Prometheus target and scrape error; then endpoint/path/DNS/network; only after samples exist inspect Grafana source/query/time range. Explain how this separates telemetry failure from customer impact.
2. **Prometheus target DOWN after Compose edit:** compare hostname/port to Compose service/internal port; inspect logs; restore config; verify up equals 1 and samples return.
3. **Target UP but panel empty:** query metric directly in Prometheus; verify metric name, labels, time range, panel query/units. Do not infer outage from blank graph alone.
4. **Dashboard has claim IDs or huge series count:** remove sensitive/unbounded label, review instrumentation, explain privacy/cardinality impact.
5. **API health DOWN and metrics disappear:** check container status/logs, app, DB readiness, then health/request/data path. Missing metrics may be consequence rather than root cause.

### Interview questions to answer aloud

1. Difference between metrics, logs, traces; give a lab example of each.
2. What does Prometheus up measure and not prove about user requests?
3. Why Compose DNS rather than localhost for a scrape target?
4. Define latency, traffic, errors, saturation. Which does this lab actually observe?
5. How can high-cardinality labels harm Prometheus? Which labels should be rejected?
6. Dashboard blank but curl health returns 200: explain evidence-based triage.
7. How would a signal become an on-call runbook or alert? What is needed before defining an SLO?
8. Which part is SRE-owned, service-owner-owned, and platform/DevOps-supported?

For every answer: state observation, evidence, likely fault boundary, next safe check, recovery verification, then map it to a repo file/command. Record your first answer and correction in the Day 3 report.

## 10. Resume connection

This increment practices SRE observability, dashboards, production triage, runbook context, and reliability signals. Compose provisioning/versioned config practices DevOps collaboration that makes the SRE setup repeatable. Later increments can map these same operating concepts to CloudWatch, Splunk, Datadog, Kubernetes/EKS, SLOs/error budgets, alerting, incident and postmortem practices from the target role profile.

A strong interview answer says exactly what you built and verified, the actual failure evidence, what remains untested, and how the signal guides a responder. This local lab demonstrates hands-on concepts; it is not a claim of production ownership.

## 11. Day 3 closeout template

- Date, EC2 region/type, repo branch and commit:
- Day 2 rebuild checks/results:
- Day 1/Day 2 recall answers and corrections:
- LAB-003 files changed and why:
- Tests/config validation:
- API, Prometheus target/query, Grafana dashboard evidence:
- Failure drill actually run, evidence, diagnosis, recovery:
- Security review: ports, labels, secrets, image versions:
- Acceptance criteria not met/open issues:
- Cleanup and remaining resources checked:
- 60-second interview explanation:
- Reviewer/next action:


## Returning after a break: restart the learning path from Day 1

Do not jump straight into LAB-003. The Day 3 prework is ready, but LAB-003 is not complete. Because this is a learning rebuild, resume in order and pass each checkpoint before moving forward. Use the committed recipe on the personal repository's main branch; cloud machines and database volumes are disposable.

1. **Day 1 refresh — docs/days/PROJECT_DAY_01.md (LAB-001):** begin with the fresh-host and repo setup steps. Explain each command before running it. Recreate PostgreSQL and the Spring API, run Maven tests, check health, create a synthetic record, GET it, confirm the database row, then practice the API-stopped drill. Write down what each signal proves and what it does not.
2. **Day 1 recall gate:** close the notes and narrate the request path, Maven test limits, Compose health check/volume, API-versus-database checks, and recovery evidence. Reopen Day 1 notes and record corrections. Move on when you can rebuild and explain the path without copying commands blindly.
3. **Day 2 refresh — docs/days/PROJECT_DAY_02.md (LAB-002):** rebuild Day 1 once from GitHub, then recreate the containerized version. Before looking at the checked-in Dockerfile, write a first draft from memory; compare it with the repo, explain every instruction, build the multi-stage image, run the API and PostgreSQL in Compose, and verify health, POST/GET, and the matching SQL row. Repeat the Docker build-tool and wrong-service-DNS troubleshooting drills; record what actually happens.
4. **Day 2 recall gate:** explain build stage versus runtime stage, build context/.dockerignore, non-root user, container DNS (postgres:5432) versus host port (127.0.0.1:5433), dependency health, persistent volume, and Buildx evidence. Correct the Day 2 notes before proceeding.
5. **Resume Day 3 — docs/days/DAY_03_PREWORK.md (LAB-003):** only after Day 1 and Day 2 checkpoints pass, rebuild the Day 2 baseline again, revise both days, then add metrics, Prometheus, Grafana, and the controlled scrape-target failure drill. Record the implementation in docs/days/PROJECT_DAY_03.md and mark the ticket complete only with observed evidence.

**Daily rule:** keep one active day/ticket. Do not mark a rebuild, test, incident drill, or interview answer complete because the instructions were read; record a command/output or a short explanation you produced yourself. If a step fails, stop at that boundary, capture safe evidence, make one change, and verify the same signal again.


## Video-first learning path — Days 1–3

Use these videos to see the systems and workflows, then use the repo notes to connect them to the project. Watch the assigned sections before the lab; pause and explain each idea in your own words. Before running a command, say what it checks and what result would change your diagnosis.

### Day 1 — Linux host, HTTP, Spring Boot, Maven, PostgreSQL, and incident basics

1. **SRE mindset and team work:** [SRE Fundamentals — Google Cloud](https://www.youtube.com/watch?v=eopc_ijIfLg). Watch 07:06–11:03 for SRE/DevOps and reliability responsibilities; 11:03 onward for error budgets; 23:57–49:55 for monitoring, change management, incident response, postmortems, and toil. Connect this to the role table and API-stopped drill in [PROJECT_DAY_01.md](PROJECT_DAY_01.md).
2. **Spring request flow:** [Build Your First Spring Boot REST API](https://www.youtube.com/watch?v=wfj-Z9OQpCA). Focus on project structure, dependencies, controller, and testing (about 2:19–16:07). Trace its request path into this lab: ClaimController → ClaimRepository → Claim → PostgreSQL row.
3. **Build and tests:** [Introduction to Maven and its Lifecycle](https://www.youtube.com/watch?v=gzeIvdT3Dq4). Learn compile/test/package as different lifecycle stages; map them to pom.xml, ./mvnw test, and the build error we diagnosed.
4. **Database basics:** [PostgreSQL and SQL for Beginners](https://www.youtube.com/watch?v=qw--VYLpxG4). Watch the database/relational concepts and psql, table, insert, and select portions; practice the lab’s read-only SELECT when verifying synthetic data.
5. **Containers and local dependencies:** [Docker Compose beginner tutorial](https://www.youtube.com/watch?v=iOGEBj7Ozak). Focus on services, ports, health checks, volumes, logs, and exec. Map each term to services/claims-api/compose.yaml and the Day 1 PostgreSQL service.

### Day 2 — image construction, safe runtime, and container networking

1. **Dockerfile and multi-stage image:** [Docker multi-stage builds and BuildKit](https://www.youtube.com/watch?v=JofsaZ3H1qM). This is an older visual walkthrough, so verify current syntax and security guidance in the [official multi-stage build guide](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/). Explain every instruction in the lab Dockerfile.
2. **Compose networking and operations:** Rewatch the [Docker Compose beginner tutorial](https://www.youtube.com/watch?v=iOGEBj7Ozak). Draw why a container reaches PostgreSQL as postgres:5432, while a host process uses its published host address and port. Localhost means the current network namespace.
3. **Rebuild practice:** use [PROJECT_DAY_02.md](PROJECT_DAY_02.md) after writing a Dockerfile draft from memory. Record differences and explain .dockerignore, non-root execution, health dependencies, and named storage.

### Day 3 — instrumentation, Prometheus scraping, and Grafana dashboards

1. **Application metrics path:** [Spring Boot, Micrometer, Prometheus, and Grafana walkthrough](https://www.youtube.com/watch?v=_WdIlz33FKE). Focus on Actuator (~0:59), Micrometer (~17:51), Prometheus (~21:52), and integration (~26:09). Map it to Spring exposing a metrics endpoint, then Prometheus scraping it.
2. **Prometheus fundamentals:** [Prometheus getting started — PromLabs/Julius Volz](https://www.youtube.com/watch?v=OxZmn4svOyA). Focus on targets, scrape status, and first PromQL queries. A running Prometheus server does not prove the target is up; verify the target and actual time series.
3. **Grafana panels:** [Grafana with Prometheus dashboard walkthrough](https://www.youtube.com/watch?v=Fpw4Rwpb160). Watch how a data source and query feed panels. Check time range, units, labels, and query when a panel is blank.
4. Use the official references above to confirm endpoint names, metric semantics, and current configuration. Videos show the idea; the repo’s pinned files and official docs define what we run.

### The connection to remember

Day 1: HTTP request → Spring controller → repository/JPA → PostgreSQL. Day 2 packages the same application and database into separate containers, so container DNS replaces host loopback for service-to-service traffic. Day 3 adds a second path: Spring instrumentation → metrics endpoint → Prometheus scrape and time series → Grafana query and panel. Monitoring observes the application path; it does not replace user-facing health or persistence checks.

### How to study before each build

- Watch the assigned section once, then close it and draw the components and arrows from memory.
- For every new word, write a plain-language definition, a repo example, the command/file that shows it, and one failure symptom.
- Attempt recall questions without notes. Mark “not sure” honestly and bring those gaps to the build session before changing files.
- During the lab, explain each command before running it and record actual output, exit status, and what it proves. Never copy sample output as evidence.
- After the lab, rebuild the change from committed files. Repeat until you can predict the next check and explain why; memorize the troubleshooting reasoning, not a blind sequence.

Actual execution evidence and interview answers belong in [PROJECT_DAY_03.md](PROJECT_DAY_03.md) after the work is performed. This prework is for learning before the build; it does not mark LAB-003 complete.
