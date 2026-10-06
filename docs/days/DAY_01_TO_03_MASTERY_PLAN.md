# Day 1–3 Topic Mastery Plan

Use this study contract with each day's files:

- Day 1: [Prework](DAY_01_PREWORK.md) · [Build, rebuild, and evidence](PROJECT_DAY_01.md)
- Day 2: [Prework](DAY_02_PREWORK.md) · [Build, rebuild, and evidence](PROJECT_DAY_02.md)
- Day 3: [Prework](DAY_03_PREWORK.md) · [Build, rebuild, and evidence](PROJECT_DAY_03.md)

## How we pace each day

Do not rush through a fixed clock. Each numbered topic is a small lesson. Complete its **understand → connect → do → explain** loop before moving to the next topic. A video introduces the picture; the notes and lab teach the exact implementation. Reading a file or seeing a command once does not count as practice.

For every topic, the learner should be able to:

1. Define its terms in plain language and explain the problem it solves.
2. Explain why this design/tool is used here, a reasonable alternative, and the trade-off.
3. Point to where it appears in our architecture and repo files; trace inputs, outputs, dependencies, and boundaries.
4. Perform the build or operational task, then explain each command and meaningful line of configuration.
5. Predict a plausible failure, collect evidence before changing anything, make the smallest safe change, and verify recovery.
6. Explain the SRE-first responsibility, partner roles, and handoff or escalation.
7. Answer a source-mapped interview question from our lab evidence; label unrun scenarios Not practiced.

Keep a short record in that day's project report: **topic · my explanation · file/command · evidence · failure check · question ID · what to revisit**. In production, use approved runbooks and documentation; mastery means sound reasoning and safe execution, not memorizing every flag.

## Day 1 — Host, API, database, and first SRE investigation

**Goal:** Understand and rebuild the service path, then independently diagnose the observed case where the API process is stopped while PostgreSQL is healthy.

### Topics to master

1. **Linux host and shell.** Know EC2 as the remote Linux machine, SSH as access, ec2-user versus root, sudo, home/current directory, architecture, CPU, memory, disk, process, and listening port. Practice whoami, id, hostname, pwd, cat /etc/os-release, uname -m, free -h, df -h /, nproc, ps, and ss. Explain what each output tells you and which assumption it checks.
2. **HTTP, REST, JSON, and request path.** Understand client/server, method, path, headers, JSON body, status, and response. Trace POST /claims and GET /claims/{id}; explain expected 201/200, validation 400, and missing-resource 404. Compare HTTP response with direct database evidence.
3. **Java/Spring Boot structure.** Understand startup, dependency injection, controller, request validation, entity, repository, and test. Point to ClaimsApiApplication, ClaimController, Claim, ClaimRepository. Trace request → controller → repository → database → response. Explain component ownership and why responsibilities are separated.
4. **Build recipe and Maven.** Read pom.xml: project coordinates, Spring dependencies, Java level, build configuration. Explain Maven Wrapper, dependency resolution, compile, test, package. Run ./mvnw test; trace a compiler error to its first useful error, source file, or missing dependency. State clearly: a passing test checks tested behavior; it does not prove a live port is reachable.
5. **Relational database and SQL.** Define database/schema/table/row/column/primary key, JDBC URL, ORM/JPA, SQL. Trace entity fields to the claims table. Use pg_isready for readiness and a read-only SELECT to verify synthetic data. Explain why pg_isready alone does not prove the API wrote the intended row. PostgreSQL is the lab choice; production selection depends on workload and organizational standards.
6. **Docker and Compose dependency basics.** Define image, container, named volume, service, published port, health check, environment variable. Read services/claims-api/compose.yaml. Explain host 127.0.0.1:5433 mapping to database container port 5432; the named volume survives container replacement but is removed by down -v. Know docker compose config, up, ps, logs, exec, down; explain what each proves and does not prove. Keep real .env out of Git.
7. **SRE incident method and evidence.** Practice symptom/impact → baseline → one hypothesis → narrow check → mitigation → same-signal recovery verification → ticket/runbook follow-up. For the observed API stop, independently check Compose DB status, Java process, ss listener, logs, and HTTP health. Explain why healthy DB does not imply healthy API. Separate observed evidence from hypothetical prompts.

### Day 1 mastery gate

From a clean checkout, explain the architecture, identify the files, start services using documented steps, create/retrieve synthetic data, verify its SQL row, and diagnose the API-stopped condition from evidence. Explain what each check proves and its limits; give a concise incident handoff.

**Work mapping:** Service/application owner owns API behavior and tests; database owner advises on data/access/recovery; platform/DevOps enables repeatable host/dependency setup; SRE leads health reasoning, safe mitigation, runbook, and follow-up. The learner performs the assigned work and records evidence for review.

## Day 2 — Dockerfile, image construction, and container networking

**Goal:** Package the exact Day 1 API without changing behavior, run API and PostgreSQL as Compose services, and understand/debug each image and network boundary.

### Topics to master

1. **Container model and image lifecycle.** Distinguish source, build context, Dockerfile, image/layers/tag/digest, container/process, registry, and volume. Draw source → builder → artifact → runtime image → running container. Explain container versus VM and why database data is separate from an application image.
2. **Write a Dockerfile from a blank file.** Before opening the checked-in file, draft this structure, then write and explain every instruction used in our Dockerfile:

        FROM maven:3.9.16-eclipse-temurin-21-noble AS build
        WORKDIR /workspace
        COPY pom.xml .
        RUN mvn -B -ntp dependency:go-offline
        COPY src ./src
        RUN mvn -B -ntp -DskipTests package
        FROM eclipse-temurin:21-jre-noble AS runtime
        WORKDIR /app
        COPY --from=build --chown=10001:10001 /workspace/target/*.jar /app/app.jar
        USER 10001:10001
        EXPOSE 8081
        ENTRYPOINT ["java", "-jar", "/app/app.jar"]

   Explain FROM (base/stage), AS (stage name), WORKDIR (default directory), COPY (context input), RUN (build-time command), COPY --from (artifact between stages), --chown (runtime file access), USER (least privilege), EXPOSE (documentation only; it does not publish a port), and exec-form ENTRYPOINT (starts Java as main process and supports signal handling). For each, state inputs, outputs, build/runtime effect, and likely failure. Verify syntax against current Docker docs when versions change.
3. **Multi-stage build, Java artifact, and cache.** Explain why Maven/JDK belong in the builder while JRE is enough to run the JAR. Explain why copying pom.xml before source can reuse dependency layers. In this lab, ./mvnw test is the explicit test step; Docker packaging uses -DskipTests after tests are run, so that command alone is not test evidence. Compare size/toolchain/patch trade-offs; a multi-stage image is not automatically secure.
4. **Build context and .dockerignore.** Explain which directory Docker sends to BuildKit and why COPY paths are relative to it. Explain how .dockerignore keeps .env, .git, target, logs, and local evidence out. Contrast .dockerignore (image build input) with .gitignore (Git commit selection). Diagnose wrong directory, missing COPY path/JAR, dependency failure, and Buildx incompatibility from the first meaningful error.
5. **Runtime identity and configuration.** Understand UID/GID, permissions, least privilege, environment variables, image layers, and why passwords must not be baked in or printed. Inspect configured runtime user without dumping secrets. Explain Compose .env interpolation versus values explicitly passed into the API container.
6. **Compose networking, DNS, and ports.** Draw host, API container, PostgreSQL container, and Compose network. API container uses postgres:5432 (service DNS/internal port); EC2 host uses published 127.0.0.1:5433. Container localhost means that same container. Explain host-published 8081:8081. Diagnose wrong hostname/port/service typo/refused connection without opening database access publicly.
7. **Health, lifecycle, persistent data.** Separate Compose process state, pg_isready, Actuator health, HTTP behavior, SQL persistence. Explain depends_on: service_healthy gates initial startup, not all future database failures. Explain stop/recreate versus named-volume deletion. Inspect image/container state/logs/user/ports/config safely.
8. **Buildx as a build dependency.** Know Docker Engine, Compose plugin, Buildx plugin, and BuildKit roles. Read versions before changing application code. Explain the observed Buildx mismatch and checksum-verified installer as a toolchain fix, not an application-code fix.

### Day 2 mastery gate

Write the Dockerfile from a blank editor, explain every instruction, build the tagged image, prove runtime is non-root, and run the unchanged API and PostgreSQL through Compose. Verify DB readiness, API health, POST/GET, matching SQL row, and data survival after container recreation. Diagnose API stop, wrong DB hostname, and image build failure one at a time; label unrun scenarios honestly.

**Work mapping:** Application owner confirms unchanged behavior/runtime needs; platform/DevOps owns repeatable image/build/Compose patterns; SRE validates operability, health, safe recovery, and runbook; security partner reviews non-root, secrets/build context, and endpoint exposure.

## Day 3 — Instrumentation, Prometheus, Grafana, and operational signals

**Goal:** Add observability to the Day 2 service and create a dashboard that helps a responder distinguish service failure from monitoring failure.

### Topics to master

1. **Observability signals.** Define metrics, logs, traces, profiles; what each answers and its limits. Metrics summarize numeric behavior over time; logs give event detail; traces connect spans across a request. Explain why the lab starts with metrics and does not claim full APM.
2. **Instrumentation and metric types.** Understand instrumentation, Micrometer/Actuator, registry/export endpoint, counter, gauge, histogram, labels, buckets, and samples. Identify metrics this app actually exposes. Use appropriate rates/latency distributions; JVM health does not alone prove user requests succeed.
3. **Prometheus scrape model.** Explain pull/scrape, target, job, interval, time series, sample, scrape path, target status. Configure Prometheus to reach API by Compose DNS and internal port. Verify target UP, scrape errors, and actual query results; running Prometheus alone proves nothing about collection.
4. **PromQL foundations.** Read selectors, label filters, range vectors, rate/increase, aggregations, and up. Explain query window and labels; predict what it returns. Empty results may mean wrong name/labels/time range/no samples, not necessarily service outage.
5. **Labels, cardinality, privacy.** Explain each unique label combination creates series. Use bounded labels such as method/status; reject claim IDs, user IDs, raw paths, or sensitive/unbounded values. Understand memory, cost, query, and privacy impact.
6. **Grafana data source, query, dashboard.** Grafana queries Prometheus; it does not create metrics. Configure data source, panel query, time range, units, labels, legend, refresh. Build panels from real metrics and state what a responder should do. Distinguish blank dashboard from API outage.
7. **Health versus SLI/SLO.** Explain health endpoint versus collection versus user-facing SLI. Define a candidate request success/latency SLI, required instrumentation and denominator/window; do not claim an SLO until expectations and measurement are agreed. Learn latency, traffic, errors, saturation.
8. **SRE dashboard operations and failure drill.** Triage API health → metrics endpoint → Prometheus target/error → samples/query → Grafana source/panel/time range. Only after baseline is healthy, run controlled target-down drill; restore and prove target/samples recover. Document alert/runbook usefulness, noise, owners, follow-up.

### Day 3 mastery gate

From healthy Day 2 baseline, explain the metric path, expose/scrape actual samples, query Prometheus, show Grafana panels, and diagnose a controlled missing-target or blank-panel symptom at the first failing boundary. Explain what telemetry proves and does not prove about a user request. Record actual evidence; mark unrun drills Not practiced.

**Work mapping:** Service owner validates metric meaning and app changes; SRE selects operational signals, interprets reliability, performs triage, writes alert/runbook guidance; platform/DevOps maintains versioned monitoring configuration; security reviews endpoints, labels, and sensitive data; ticket owner reviews acceptance evidence and risk.

## End-of-day readiness record

For each numbered topic, record: **Not started**, **Studied**, **Practiced with evidence**, **Can explain/rebuild**, or **Needs revisit**. Close a day only when its mastery gate has actual evidence and your own explanation. A passed interview question is not evidence that an unrun drill occurred; a green dashboard/test alone is not proof of end-to-end user success.
