# Day 1–3 Mastery Plan: One Repeating Build and Rebuild Flow

This is the single day-by-day operating flow for LAB-001 to LAB-003. Keep using this personal repository and its existing service. Do not create a second sample application. For each day, learn first, rebuild the previous checkpoint, add one agreed change, troubleshoot it, prove recovery, document the evidence, and clean up the disposable environment.

Use the detailed daily prework for beginner explanations and the topic-by-topic video maps. Use the PROJECT_DAY file for actual commands, results, interview answers, and what happened. This plan tells you the order and the commands you will run. Never mark work complete because you watched a video or read an example.

## The same daily work cycle

1. **Get the assignment and boundaries.** Read the ticket, acceptance criteria, previous project-day report, and this plan. Confirm personal AWS account/region, instance, SSH source IP, time/cost limit, synthetic data only, and cleanup plan. In an organization, the manager/ticket owner sets priority and acceptance criteria; the service owner clarifies application behavior; SRE owns reliability checks and response quality; platform/DevOps supports repeatability; security and database partners advise in their areas.
2. **Start from a clean, disposable Linux host.** Create Amazon Linux 2023 EC2 with SSH restricted to your current IP, then connect as ec2-user. Record region, instance type, disk and commit. Do not use a Cisco work repository or machine.
3. **Inventory before changing the host.** Say what each command checks before running it:

   ```bash
   whoami
   id
   hostname
   pwd
   cat /etc/os-release
   uname -m
   free -h
   df -h /
   nproc
   ```

   These check identity/groups, host name, working directory, OS, architecture, memory, disk, and CPU count. They let an operator test assumptions before installing/building.
4. **Install only the lab prerequisites and start Docker.** On Amazon Linux 2023:

   ```bash
   sudo dnf install -y git java-21-amazon-corretto docker curl openssl
   sudo systemctl enable --now docker
   sudo usermod -aG docker ec2-user
   ```

   The package manager installs tools, systemd starts Docker and enables it after reboot, and group membership allows the lab account to use the Docker socket. Docker group membership is highly privileged; use only on the disposable learning host. Disconnect and reconnect after usermod, then check id and docker ps. If Docker is not installed or running, diagnose that before application work.
5. **Clone only this repo and inspect what you got.**

   ```bash
   cd /home/ec2-user
   git clone --branch main --single-branch https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab.git
   cd ~/sre-devsecops-platform-lab
   git status --short --branch
   git log -1 --oneline
   find docs services scripts -maxdepth 4 -type f -print | sort
   ```

   The status and commit identify the exact source baseline; the file list helps find the runbook, app, Compose config and scripts. If the repo already exists, use git pull --ff-only only after checking status; do not overwrite unexplained local changes.
6. **Create an evidence-led ticket note.** Before changing anything, record the symptom or requested change, expected behavior, impact in this lab, current status, timestamp, commit, hypothesis, and acceptance checks. For each topic use: plain-language explanation → repo file → command → output/evidence → likely failure → safe check → interview question / correction.
7. **Learn the new day’s topics from the linked prework videos.** Watch the assigned chapter, pause, draw the components and arrows from memory, explain each new term in your words, then point to the matching repo file. Use current official docs linked in prework to check current syntax. If you cannot explain a concept, revisit it before changing files.
8. **Build one small increment.** Keep a clean baseline, make one change at a time, review git diff, and run tests/config validation before startup. Explain each command first.
9. **Troubleshoot as the on-call SRE.** State symptom and impact, collect baseline, form one hypothesis, run the narrowest safe check, change one thing, and recheck the original failing signal. Check the application, dependency and monitoring layer separately. Preserve database volumes and secrets.
10. **Prove end-to-end behavior.** A process-up signal, database-ready signal, HTTP health response, successful request and matching SQL row prove different things. Capture more than one layer when acceptance requires it. Record exact sanitized output and exit status; never paste passwords/tokens.
11. **Report, review, save, and decommission.** Complete that day’s PROJECT_DAY report and ticket with observed evidence, interview answers, unrun drills marked Not practiced, risks, rollback and cleanup. Review git status/diff and secret exposure. Commit/push only the personal repo through its intended workflow. Confirm the commit is visible on GitHub before cleanup. Stop/remove lab containers without deleting volumes unless data deletion is intentional; inspect AWS for EC2, EBS and any other billable resources before ending the day.

## Day 1 — Build the first service baseline (LAB-001)

**Goal:** From a new EC2 and this repo, run the existing Spring Boot API on the Linux host with PostgreSQL in Docker Compose. Follow a request to its database row, then investigate the observed case where the API is stopped while PostgreSQL remains healthy.

**SRE / team ownership:** the service owner explains the API/data contract; database owner advises on data and recovery; platform/DevOps helps make host and dependency setup repeatable; SRE verifies service availability, separates failure domains, mitigates safely, and writes the handoff. The learner performs the ticket and records evidence for review.

**Prework and videos:** use [Day 1 Prework](DAY_01_PREWORK.md). Its video map covers Linux host/shell, SRE and incident flow, HTTP/REST and Spring request path, Maven, SQL/PostgreSQL, and Compose. Core visual references: [SRE Fundamentals — Google Cloud](https://www.youtube.com/watch?v=eopc_ijIfLg), [Spring Boot REST API](https://www.youtube.com/watch?v=wfj-Z9OQpCA), [Maven lifecycle](https://www.youtube.com/watch?v=NwrdhG4nTgg), [PostgreSQL and SQL](https://www.youtube.com/watch?v=qw--VYLpxG4), [Docker Compose basics](https://www.youtube.com/watch?v=iOGEBj7Ozak). Watch the sections and exercises listed in prework, not just the whole video passively.

**Build and verify, in order:**

1. Read services/claims-api/README or its current service documentation if present, pom.xml, application.properties, compose.yaml, .env.example, .gitignore, and the Java files. Explain which service is a host process (Java) and which is a container (PostgreSQL). Do not assume docker compose ps lists the Java process.
2. Install the pinned Compose CLI plugin from this repo and create a local ignored environment file:

   ```bash
   cd ~/sre-devsecops-platform-lab
   bash scripts/bootstrap/install-compose-plugin.sh
   bash scripts/bootstrap/init-claims-api-env.sh
   git check-ignore -v services/claims-api/.env
   ```

   The installer verifies a pinned download checksum; the env initializer generates local configuration without printing the password. check-ignore proves Git will ignore that path. Never cat or commit the real .env.
3. Start only PostgreSQL for the Day 1 topology. The current Compose file also defines the Day 2 API container, so explicitly name the database service:

   ```bash
   cd ~/sre-devsecops-platform-lab/services/claims-api
   docker compose config --quiet
   docker compose up -d --wait --wait-timeout 60 postgres
   docker compose ps
   docker compose exec postgres pg_isready -U claims -d claims
   ```

   config checks YAML and required interpolation; up starts PostgreSQL and waits for its health check; ps shows Compose-managed containers; pg_isready checks whether PostgreSQL accepts connections. None proves the API is listening or that a claim was saved. Port mapping 127.0.0.1:5433:5432 means EC2 host port 5433 forwards to PostgreSQL container port 5432 and binds only to host loopback. The named volume holds data outside the replaceable container.
4. Load the ignored env values into the current shell, test the app, then start it in the foreground so logs remain visible:

   ```bash
   cd ~/sre-devsecops-platform-lab/services/claims-api
   set -a
   source .env
   set +a
   ./mvnw test
   ./mvnw spring-boot:run
   ```

   Run these in one terminal; leave spring-boot:run in the foreground. Maven Wrapper selects the project’s Maven version. Tests check only coded test behavior. Use a second SSH terminal for live HTTP checks. Ctrl+C stops the foreground API and is later used for the outage drill.
5. Verify the real request path from that second terminal:

   ```bash
   curl -i http://127.0.0.1:8081/actuator/health
   curl -i -X POST http://127.0.0.1:8081/claims -H 'Content-Type: application/json' -d '{"description":"day1 synthetic test"}'
   curl -i http://127.0.0.1:8081/claims/PASTE_RETURNED_UUID_HERE
   ```

   Copy the returned synthetic UUID locally. Use it in a read-only database query:

   ```bash
   docker compose exec postgres psql -U claims -d claims -c "SELECT id, description, status, created_at FROM claims WHERE id = 'PASTE_RETURNED_UUID_HERE';"
   ```

   Explain the chain: curl/HTTP → Spring/Tomcat → ClaimController validation → ClaimRepository/JPA/Hibernate/JDBC → PostgreSQL claims row → JSON response. HTTP 201 shows the API reported create success; GET checks retrieval; SQL independently verifies persistence.
6. Practice the observed API-stopped incident. With the API process stopped, capture the failing health check and inspect layers independently:

   ```bash
   curl -i http://127.0.0.1:8081/actuator/health
   docker compose ps
   docker compose exec postgres pg_isready -U claims -d claims
   ps -ef | grep '[j]ava'
   ss -lntp | grep ':8081'
   ```

   Expected diagnostic logic: curl fails, PostgreSQL still accepts connections, no Java process and no listener on 8081. This points to a stopped host API, not a PostgreSQL outage. Restart with env loaded and ./mvnw spring-boot:run, then verify the original health/request check and existing UUID again. Record symptom → impact → evidence → diagnosis → mitigation → recovery proof → follow-up. Do not call this a production incident; it is a reproduced lab drill.
7. **Day 1 exit gate:** rebuild from repo instructions; explain every command and file; narrate the request and port path; show health, POST, GET and SQL evidence; diagnose API-down/DB-healthy without guessing; explain what each check does not prove. Record results in [PROJECT_DAY_01.md](PROJECT_DAY_01.md). Do not advance just because commands were copied.

## Day 2 — Rebuild Day 1, then containerize the same API (LAB-002)

**Goal:** Start on a fresh EC2, reconstruct the Day 1 baseline from this repo, revise its interview/troubleshooting concepts, then move the unchanged API from a host Java process into a non-root application container beside PostgreSQL.

**SRE / team ownership:** the application owner confirms behavior remains unchanged; platform/DevOps builds the image and Compose deployment recipe; SRE verifies operability, dependency separation, incident recovery, and runbook quality; security partner reviews build context, secrets, exposed ports and runtime identity.

**Prework and videos:** use [Day 2 Prework](DAY_02_PREWORK.md). Follow its video map for image/container/layer fundamentals, Dockerfile authoring, multi-stage build/cache, .dockerignore, non-root runtime, Compose DNS/ports, volumes/health, and Buildx. Core visual references: [Docker beginner course](https://www.youtube.com/watch?v=3c-iBn73dDE), [Dockerfile best practices](https://www.youtube.com/watch?v=JofsaZ3H1qM), [Compose networking](https://www.youtube.com/watch?v=rFQqiuFIjms), [Docker volumes/persistence](https://www.youtube.com/watch?v=bRyuhBJtJ6M). For current command behavior use the official docs linked from prework.

**Part A — rebuild yesterday before new work:** repeat the Day 1 clean-host inventory, install/reconnect, clone/status/commit checks, Compose/Buildx setup and ignored env creation. Then start only PostgreSQL with docker compose up -d --wait --wait-timeout 60 postgres, load .env, run ./mvnw test, and start the API on the host. Repeat health, POST, GET, SQL, and the API-stopped diagnosis. Explain the command before running it and write your no-notes recall answers/corrections in PROJECT_DAY_02.md. This is the prior-day rebuild checkpoint; the same repo and same app are used.

**Part B — build Day 2 only after Part A passes:**

1. Stop the host Java process with Ctrl+C after capturing its healthy baseline. Draw the new path: client → EC2 published API port → claims-api container → Compose private network/DNS → PostgreSQL container; the database named volume stays separate.
2. Before viewing the checked-in Dockerfile, write its instruction skeleton from memory. Then inspect services/claims-api/Dockerfile and .dockerignore. Explain FROM/AS, WORKDIR, COPY, RUN, COPY --from, --chown, USER, EXPOSE and exec-form ENTRYPOINT; state what each does during build or runtime and one way it can fail.
3. Explain why Maven/JDK are in the build stage and only Java runtime/JAR are in runtime stage; why pom.xml is copied before source for dependency-layer caching; why .dockerignore and .gitignore solve different problems; why the process runs as UID/GID 10001; why EXPOSE alone does not publish a host port. Do not claim a multi-stage build makes an image secure by itself.
4. Validate and build/run the service stack:

   ```bash
   cd ~/sre-devsecops-platform-lab/services/claims-api
   docker compose config --quiet
   docker compose config --services
   docker compose up -d --build --wait --wait-timeout 120
   docker compose ps
   docker compose logs --tail=60 claims-api
   docker compose exec postgres pg_isready -U claims -d claims
   ```

   config validates Compose interpolation; config --services proves the service names; up --build invokes Compose/Buildx/BuildKit to make the image and waits for configured health; ps and logs show runtime state; pg_isready checks the database. An Up container is not by itself proof of successful HTTP/API behavior.
5. Prove the network addresses and the application behavior:

   - From EC2, API is at 127.0.0.1:8081 and the optional host-side PostgreSQL mapping is 127.0.0.1:5433.
   - From the API container, PostgreSQL is postgres:5432. localhost inside the API means the API container itself; published host ports are not used for service-to-service Compose traffic.
   - Run curl health, POST one synthetic claim, GET its returned UUID, query the same row with psql, and inspect the non-secret URL with docker compose exec claims-api printenv DB_URL. Never print DB_PASSWORD.
   - Prove non-root configuration with docker image inspect claims-api:day2 --format 'Configured user={{.Config.User}}' or docker run --rm --entrypoint id claims-api:day2. Inspect only needed metadata; do not dump environment secrets.
6. **Troubleshoot one issue at a time:**

   - Build fails with “requires buildx 0.17.0 or later”: classify as builder toolchain, compare docker compose version / docker buildx version / docker version, use the checksum-verified repo installer, retry, record first failing error. Do not edit Java code for a Buildx prerequisite.
   - API cannot reach database: inspect docker compose ps, docker compose logs --tail=100 claims-api, docker compose exec claims-api printenv DB_URL, compose service name, internal port, and DB readiness. A wrong hostname/port is fixed in Compose config; do not open public PostgreSQL ingress.
   - API container stopped: inspect docker compose ps -a and logs; restart with docker compose up -d claims-api; verify curl health and GET.
   - Persistence: recreate containers without -v, then GET/query the old UUID. Never use docker compose down -v for this drill; -v deletes the named volume and its data.
7. **Day 2 exit gate:** write Dockerfile from memory, trace build context/image/runtime, explain host vs container localhost/DNS/ports, prove non-root user, DB readiness, API request, SQL row and data survival, and explain at least one real diagnosis from actual output. Record actual work and unrun drills in [PROJECT_DAY_02.md](PROJECT_DAY_02.md). Do not copy illustrative output as evidence.

## Day 3 — Rebuild Day 2, then add SRE observability (LAB-003)

**Goal:** On fresh EC2, rebuild the Day 2 containerized service from the same repo, revise Days 1–2, then instrument the API, scrape metrics with Prometheus, build an operational Grafana dashboard, and distinguish an application failure from a monitoring failure.

**SRE / team ownership:** application/service owner confirms the meaning of request metrics and API behavior; SRE selects operationally useful signals, interprets user impact, tests alert/runbook usefulness, and runs the safe failure drill; platform/DevOps maintains reproducible monitoring config; security reviews endpoint exposure and label/privacy risk; ticket owner reviews evidence against acceptance criteria.

**Prework and videos:** use [Day 3 Prework](DAY_03_PREWORK.md), topic by topic: metrics/logs/traces; Actuator/Micrometer; counters/gauges/histograms; Prometheus target/scrape; PromQL; cardinality/privacy; Grafana datasource/panels; health vs SLI/SLO; blank-dashboard triage. Core visual references: [Spring, Micrometer, Prometheus, Grafana walkthrough](https://www.youtube.com/watch?v=_WdIlz33FKE), [Prometheus intro — PromLabs](https://www.youtube.com/watch?v=STVMGrYIlfg), [PromQL tutorial](https://www.youtube.com/watch?v=hvACEDjHQZE), [Prometheus histograms](https://www.youtube.com/watch?v=yYbXak-1hew), [Grafana course](https://www.youtube.com/watch?v=CjABEnRg9NI), and [SRE Fundamentals](https://www.youtube.com/watch?v=eopc_ijIfLg) for SLI/SLO/error-budget concepts. Prework gives topic timestamps and current official references. Older UI videos teach concepts; use the official docs for current configuration syntax.

**Part A — rebuild Day 2 first:** repeat fresh EC2 inventory and repo clone; run the checked-in Compose/Buildx installers; initialize ignored env; validate Compose; execute docker compose up -d --build --wait --wait-timeout 120; run Maven tests with the documented env loaded; inspect ps/logs and pg_isready; verify API health, synthetic POST/GET and matching SQL. Write Day 1 and Day 2 recall answers before opening notes; record corrections in PROJECT_DAY_03.md. If this baseline fails, troubleshoot it before touching telemetry.

**Part B — add observability after baseline passes:**

1. Inspect pom.xml, application.properties, Dockerfile, compose.yaml and actual Day 2 health/request signals. Record baseline commit and tests. Add only the agreed metrics changes; preserve API behavior and database volume.
2. Add Spring’s Prometheus/Micrometer registry and intentionally expose the metrics endpoint within the lab network boundary. Explain Actuator (operational endpoints), Micrometer (instrumentation/API), registry (Prometheus format), counter/gauge/histogram, and identify metric names/labels from the running endpoint rather than assuming names from videos.
3. Add a versioned Prometheus scrape config. From Prometheus’s network namespace, target the API Compose service name and internal port/path; localhost would mean Prometheus itself. Validate config before restarting, then check Prometheus Targets and query up plus actual exported metrics. “Prometheus container is running” is not proof the app was scraped; up is scrape status, not end-to-end customer success.
4. Add Grafana with a Prometheus datasource and dashboard config. Use Compose DNS from Grafana to Prometheus; bind any host UI ports only to 127.0.0.1 for this lab. Build panels only from verified series and state what on-call action each panel informs. Do not treat Actuator UP or a pretty graph as an agreed SLO.
5. Validate the rendered config and inspect all layers:

   ```bash
   cd ~/sre-devsecops-platform-lab/services/claims-api
   docker compose config --quiet
   docker compose up -d --build --wait --wait-timeout 120
   docker compose ps
   docker compose logs --tail=100 claims-api
   docker compose logs --tail=100 prometheus
   curl -i http://127.0.0.1:8081/actuator/health
   ```

   Then inspect Prometheus Targets, query up and real application metrics, and verify Grafana’s datasource/query/time range/panels. Record exact query, time range, and observed result. No secrets, IDs or raw user paths as metric labels: unbounded cardinality costs memory/storage/query time and may expose sensitive data.
6. Run a controlled monitoring-only failure after a healthy baseline: change one scrape target to an invalid service name/port in the disposable lab; observe target DOWN and the scrape error; independently check API health to show the service can remain up while monitoring is broken; restore the target; prove target UP, samples and panels return. Record response as symptom → impact → evidence → hypothesis → narrow check → restore → same-signal verification → prevention/runbook action. Mark it practiced only if actually run.
7. **Day 3 exit gate:** narrate the request path and separate telemetry path; prove API, target, real samples and Grafana panels individually; explain a blank panel using evidence and first failing boundary; explain labels/cardinality and why health is not an SLI/SLO; record evidence and interview Q&A in [PROJECT_DAY_03.md](PROJECT_DAY_03.md). LAB-003 stays in progress until the agreed checks pass.

## The commands we reuse and how to choose them

- git status --short --branch / git log -1 --oneline: check the checkout, changes and exact baseline before work or commit.
- docker compose config --quiet: catch YAML/interpolation errors before creating containers.
- docker compose up -d --wait …: start services and wait for configured health checks; still verify the app separately.
- docker compose ps / ps -a: inspect running, exited and health state; not a substitute for API requests.
- docker compose logs --tail=100 SERVICE: inspect recent service errors; use the service name from compose.yaml.
- docker compose exec postgres pg_isready …: database connection readiness; does not verify an app write.
- ps -ef / ss -lntp: host process and listening socket; use during a host-mode API investigation.
- curl -i URL: live HTTP response/status from this machine; does not independently prove durable DB persistence.
- docker compose exec postgres psql … SELECT …: read-only independent data check for a synthetic UUID.
- docker image inspect / docker inspect: inspect image/container metadata; avoid displaying env values that may contain secrets.
- docker compose stop SERVICE / start SERVICE: controlled stop and restart of one service, normally preserving named volumes.
- docker compose down: remove project containers/network while retaining named volumes by default. docker compose down -v removes the named volumes and data; use only when data deletion is intended.

For every command, be ready to answer: what question am I asking? what output would support or refute my hypothesis? what can this result not prove? what do I check next?

## Ticket, interview, and completion rules

For each day, track one LAB ticket and one project-day report. Ticket status is Ready before work; In progress once investigation/build begins; Blocked only with a named external dependency; Done only after acceptance evidence is recorded. Include relevant interview questions from the shared repo/resources in the same day’s report: question ID/source, your first answer, correction, file/command/evidence that supports the answer. Do not claim an interview scenario or production incident was executed if it was only discussed.

A complete daily report contains: assignment/acceptance criteria; architecture/request path; role and ownership; concepts/videos studied; file-by-file changes; commands with purpose and actual result/exit code; tests and layered health/API/DB evidence; troubleshooting timeline and safe recovery; scenarios practiced vs Not practiced; security/cost/rollback; interview answers; cleanup evidence; next-day rebuild steps. Each next day starts by re-cloning or pulling this same repo, rebuilding the last committed state, recalling earlier topics, and then adding only that day’s increment.

## What “mastered” means for this series

You can start from the repository on a clean host, explain each important file and command, redraw the architecture, predict the next useful check from a symptom, safely reproduce and recover a failure, prove behavior at the correct layer, document what evidence does and does not prove, and give a concise interview explanation grounded in your actual lab work. If a step needs copying, mark it Needs revisit and repeat it next session.
