# Project Day 1 — Beginner SRE and DevOps Service Baseline

> Single source of truth for Day 1: assignment, concepts, architecture, files, commands, observed work, troubleshooting, scenarios, interview practice, videos, recall, and cleanup.

## 1. Day 1 assignment and goal

Imagine a user submits a claim description to a web service. The service must accept it, store it, and return it when asked. If a request fails, an engineer must find which layer failed: Linux host, Java process, network listener, API, database, credentials/configuration, or stored data.

**Manager-assigned practice ticket:** establish a safe, repeatable baseline for a small claims API; submit synthetic data; retrieve it; prove it reached PostgreSQL; check API and DB separately; document evidence and troubleshooting.

Acceptance: rebuild from this repo on a fresh Amazon Linux 2023 EC2 host; identify account/process/listeners/Docker access; prove DB readiness and API health separately; POST a claim, GET it, check SQL; restart Spring Boot and fetch same row; stop the API intentionally, diagnose and recover; save sanitized code and learning notes in the personal GitHub repo. Never commit secrets.

This is the first small vertical slice of a larger SRE/DevOps learning platform. It is not production, does not prove an SLO, and does not prove high availability or disaster recovery.

## 2. What SRE and DevOps mean here

**DevOps** is collaboration between development and operations, supported by repeatable automation for building, testing, configuring, and releasing software. In this lab, Maven Wrapper, Compose YAML, bootstrap scripts, and Git are early examples.

**SRE** applies software engineering and operational methods to keep services reliable. SRE considers user impact, measures service behavior, investigates incidents using evidence, restores safely, records learning, and reduces repeated manual toil. Day 1’s stopped-API drill is controlled practice, not a production incident. See [Google Cloud SRE](https://cloud.google.com/sre) and the SRE book’s [on-call chapter](https://sre.google/sre-book/being-on-call/).

The manager/service owner defines ticket scope and acceptance criteria. Developers usually own application behavior/tests; platform or DevOps engineers commonly own delivery automation and shared runtime foundations; database engineers may own database operations; SRE partners with service teams on reliability, on-call, observability, incident response, and automation. Actual ownership varies by employer. A new engineer learns the escalation and change paths before changing production.

**SRE’s questions:** What is the symptom? Who is affected? What evidence separates the layers? What changed? What is the lowest-risk mitigation? How will recovery be verified? What follow-up reduces recurrence?

## 3. Architecture and request flow

PuTTY is the SSH terminal on Windows. It uses a key pair to log in to the EC2 host as ec2-user. curl sends HTTP to Spring Boot running on the EC2 host. The API connects to PostgreSQL running in Docker Compose. A named Docker volume keeps database files separate from the container writable layer.

    PuTTY -> SSH key -> Amazon Linux 2023 EC2 (ec2-user)
      -> curl HTTP to 127.0.0.1:8081
      -> Spring Boot / embedded Tomcat / ClaimController
      -> validation -> ClaimRepository / Spring Data JPA / Hibernate
      -> JDBC to host 127.0.0.1:5433
      -> Compose port mapping -> PostgreSQL container port 5432
      -> claims table -> claims-db-data named volume
      -> JSON response to curl

**Repo rebuild ports:** API host port 8081; database Compose service postgres; host mapping 127.0.0.1:5433 to container port 5432. Spring runs on host, so docker compose ps lists PostgreSQL but not Java. The initial manual setup used ports 8080/5432 and a standalone claims-db container; it is a different setup. Follow current repo files for rebuild.

One POST path: curl sends POST /claims with JSON; embedded Tomcat accepts HTTP; Spring routes to ClaimController; validation rejects blank description; controller creates a Claim with UUID, description, status SUBMITTED, timestamp; ClaimRepository asks Spring Data JPA to save; Hibernate maps entity to SQL; JDBC sends it to PostgreSQL; PostgreSQL writes a row on its mounted volume; Spring returns HTTP 201 plus JSON and ID. GET /claims/{id} reads the row and returns 200, or 404 if absent.

**Definitions:** HTTP is the request/response protocol; a controller handles web requests; an entity/model represents data; a repository accesses data; JPA/Hibernate maps Java objects to SQL; JDBC connects Java to the database; PostgreSQL stores relational rows; an image is a container template; a container is a running isolated process; a volume stores data separately from container lifecycle; a health endpoint is a point-in-time signal, not a complete SLO/monitoring system.

Successful end-to-end work requires Java running, API listener open, correct DB URL/port, PostgreSQL ready, valid credentials, schema, and correct code. A healthy DB does not prove API availability. API health does not prove all user journeys or backups.

## 4. Why these tools? What alternatives exist?

- **EC2 / Amazon Linux 2023:** a Linux host to learn identity, packages, files, processes, ports, and service operation. “Free tier” does not make all storage, IP, or services free; verify billing and cleanup.
- **Java / Spring Boot:** chosen because the project and resume use Java/Spring. Boot starts the configured app and integrates HTTP, validation, database access, health endpoint. Python/FastAPI, Node, Go, or other Java frameworks could implement a similar API; we follow this codebase, not a claim of universal superiority.
- **PostgreSQL:** fits the existing config and relational claim data. MySQL is also relational and could support the use case with different driver/config. Neither is always better; team standards and workload decide.
- **Docker Engine:** runs PostgreSQL in an isolated container. **Compose:** describes the service, ports, health check, restart policy, and volume in YAML and manages its lifecycle. In production a team may use RDS, ECS, or Kubernetes; Compose is our learning-scale tool. [Docker Compose docs](https://docs.docker.com/compose/).
- **Maven:** reads pom.xml, dependencies and plugins; compiles, tests, packages. Maven Wrapper uses project-selected Maven version, avoiding dependence on globally installed Maven. test builds through tests; package also makes a distributable artifact such as a JAR. Official [POM guide](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) and [lifecycle guide](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle).
- **Git/GitHub:** stores versioned recipe/code, not EC2 itself or a running DB volume. A clean clone tests reproducibility.
- **Secrets:** local .env is ignored; .env.example is a placeholder template. Never commit real passwords, private SSH keys, tokens, AWS credentials, customer data, Terraform state, or sensitive logs.

## 5. Repository files: what each does

    docs/days/PROJECT_DAY_01.md       # this single Day 1 source of truth
    docs/days/DAY_02_PREWORK.md
    docs/architecture/target-platform.md
    scripts/bootstrap/install-compose-plugin.sh
    scripts/bootstrap/init-claims-api-env.sh
    services/claims-api/.env.example
    services/claims-api/compose.yaml
    services/claims-api/pom.xml
    services/claims-api/mvnw and .mvn/wrapper/
    services/claims-api/src/main/java/com/example/claims/
    services/claims-api/src/main/resources/application.properties
    services/claims-api/src/test/java/com/example/claims/ClaimsApiApplicationTests.java
    .gitignore and README.md

- ClaimsApiApplication.java starts Spring Boot; @SpringBootApplication enables configuration/component discovery.
- ClaimController.java defines HTTP endpoints, validation, status codes.
- Claim.java defines entity fields mapped to claims table.
- ClaimRepository.java provides Spring Data persistence operations.
- application.properties stores app/database configuration; environment variables can override.
- pom.xml defines project identity, dependencies, Java/build plugins.
- ClaimsApiApplicationTests.java tests create/read and invalid blank input.
- compose.yaml declares PostgreSQL image, service name, required environment, loopback port, named volume, readiness check.
- .env.example safely names variables; local .env contains secret and stays ignored.
- init-claims-api-env.sh creates ignored config using a random password without printing it.
- install-compose-plugin.sh installs pinned Compose only after SHA-256 verification.
- .gitignore excludes secrets, generated target, keys, local state/logs.

Application developers normally change code/tests; DevOps/platform engineers often change build/release/config automation; SRE and service owners document operational behavior. In this lab you learn to navigate all layers.

## 6. Rebuild flow from a fresh EC2

1. Create a disposable Amazon Linux 2023 EC2 with intended SSH key, restricted access, recorded region/type/storage/security group, and cleanup plan. SSH key auth as ec2-user does not make you root; use sudo only for system administration.
2. Inventory user, OS, CPU architecture, disk, memory, tools. This catches wrong identity, environment, or resource assumptions early.
3. Install required Git, Java 21, Docker, curl, OpenSSL, utilities using OS package manager. Start/enable Docker using systemd. Add ec2-user to docker group only for lab convenience; Docker group is highly privileged. Reconnect to refresh group membership.
4. Clone only the personal repo/branch. Inspect status, commit, files. Never run commands from Cisco work repo.
5. Run checked-in Compose plugin installer; verify docker compose version.
6. Run env initializer. It generates local ignored .env; do not print it. Confirm .gitignore matches it.
7. Validate with docker compose config --quiet. Start DB with docker compose up -d --wait --wait-timeout 60. Check docker compose ps and pg_isready; Up alone is not readiness.
8. Load local environment in shell and run ./mvnw test. Tests prove coded behaviors at test time, not a live server’s availability.
9. Start Spring Boot in a visible terminal so logs are available. The repo API port is 8081.
10. Check health, POST, GET, SQL. Record sanitized evidence and exact commit/environment.
11. Stop API for controlled drill; inspect app process/listener and DB independently; restart; verify health + GET.
12. Record cleanup; stopping EC2 can leave storage and other billable resources. Deleting a named volume deletes its data. Verify the AWS console state.

## 7. Build, rebuild, ownership, and command guide

This is a working runbook. Build means creating the service/configuration the first time. Rebuild means reproducing it on a fresh EC2 using only the checked-in repo plus documented prerequisites. Each command answers a question about one layer; success at one layer does not prove the whole service works.

### 7.1 Who owns each part in a typical organization?

| Work | Typical owner | Day 1 responsibility/evidence |
|---|---|---|
| API behavior | Application developer/service team | Defines HTTP contract, validation, persistence behavior, tests, and useful logs. Files: ClaimController, Claim, ClaimRepository, Spring tests. |
| Host/runtime and automation | DevOps/platform engineer | Provides supported host setup, Java/Docker, configuration, repeatable build/run/release automation. Files: bootstrap scripts, Maven Wrapper, Compose YAML. |
| Database platform | DBA or database/platform team | Sets database standards, access, backup, migration, performance, recovery. Here PostgreSQL is a local practice dependency with disposable synthetic data. |
| Service reliability | SRE with service owners | Defines health signals, runbooks, incident response, recovery evidence, reliability targets, and toil reduction. Here: separate API/DB checks and the controlled outage drill. |
| Ticket execution/review | Assigned engineer; reviewer/manager | Engineer follows acceptance criteria, records evidence and risks, escalates with findings, and hands off status. In this lab you are the engineer; I assign/review practice tickets. |

Titles vary; teams may combine duties. SRE does not automatically own every app or database change. Service owners remain accountable and partner with SRE, platform, and DBA teams.

### 7.2 First build: what is created and why

1. Define a simple contract: POST /claims accepts JSON, validates description, stores a generated ID/status/time, returns HTTP 201. GET /claims/{id} returns the record or 404. The application/service owner defines this behavior.
2. Create the Java service: Spring Boot starts it; Spring MVC maps HTTP routes; Bean Validation rejects blank input; Spring Data JPA maps Java objects to SQL; PostgreSQL stores rows. This gives us a real request path to operate and troubleshoot.
3. Make setup repeatable: pom.xml declares dependencies/plugins; Maven Wrapper pins Maven; compose.yaml defines PostgreSQL, local port, named data volume and health check; .env.example documents settings; generated .env holds a random local password and is ignored by Git.
4. Prove behavior: Maven tests check create/read and invalid input. Then start PostgreSQL, run the API, check health, send HTTP requests, and verify the same ID in SQL.
5. Practice operations: stop only the API, use process/port/container evidence to locate the fault, restore it, verify health/data, and write a ticket update. This is a controlled drill, not a production incident.

### 7.3 Rebuild from a fresh EC2: repeatable sequence

Use a disposable Amazon Linux 2023 host, intended SSH key, restricted inbound access, and cleanup plan. Record region/type/disk/security group. SSH as ec2-user; use sudo only for host administration. Do not use a work/Cisco checkout.

1. Confirm identity and capacity: whoami, hostname, pwd, id, cat /etc/os-release, uname -m, free -h, df -h /, nproc.
2. Install host prerequisites and start Docker: sudo dnf install -y git java-21-amazon-corretto docker curl openssl; sudo systemctl enable --now docker; sudo usermod -aG docker ec2-user. Log out/reconnect to refresh group membership.
3. Clone only the personal repo main branch: git clone --branch main --single-branch https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab.git. Enter it, then inspect git status --short --branch and git log -1 --oneline.
4. Check/install Compose: bash -n scripts/bootstrap/install-compose-plugin.sh; run ./scripts/bootstrap/install-compose-plugin.sh; verify docker compose version.
5. Create local config/secret: run ./scripts/bootstrap/init-claims-api-env.sh; verify with git check-ignore -v services/claims-api/.env. Never print or commit .env.
6. Start database: cd services/claims-api; docker compose config --quiet; docker compose up -d --wait --wait-timeout 60; docker compose ps; docker compose exec postgres pg_isready -U claims -d claims.
7. Test and run API: set -a; source .env; set +a; ./mvnw -version; ./mvnw test; ./mvnw spring-boot:run. Keep this terminal open for logs.
8. In a second PuTTY session, run health, POST, GET, SQL checks in 7.4. Save the returned UUID. Perform the controlled API stop/recovery drill in 7.5.

### 7.4 Command guide: what it does, when to use it, and project value

#### Host and identity: use before install/debug

| Command | What/when | Mapping to this lab |
|---|---|---|
| whoami | Prints current login; check when permissions or paths surprise you. | Expect ec2-user, not an accidental root session. |
| hostname | Prints host name; use to distinguish SSH sessions. | Confirms which EC2 instance PuTTY reached. |
| pwd | Prints current directory; use before relative paths or Git edits. | Should be inside /home/ec2-user/sre-devsecops-platform-lab, so you do not edit another checkout. |
| id | Shows UID and groups; use after Docker group changes. | Reconnect until docker appears in groups; that explains docker socket permission errors. |
| cat /etc/os-release | Displays Linux release; use before choosing package manager/package names. | Amazon Linux 2023 means dnf. cat etc/os-release failed because it omitted the leading slash; /etc/... is an absolute path. |
| uname -m | Shows CPU architecture; use before architecture-specific binary install. | x86_64 selects matching Compose plugin. |
| free -h; df -h /; nproc | Show memory, root disk capacity, CPU count in readable units. Use for OOM/disk-full/slow-build investigations. | Records host baseline; it does not prove application health. |

#### Git/bootstrap: use before starting services

| Command | What/when | Mapping to this lab |
|---|---|---|
| git clone --branch main --single-branch URL | Downloads one branch into a new directory on a fresh host. | Reproduces tracked files. Never put a token in the URL; use only the personal repo. |
| git status --short --branch | Shows branch and local tracked/untracked changes; use before/after a ticket. | Confirms source state; .env should not appear because it is ignored. |
| git log -1 --oneline | Shows latest commit hash/subject; use to record exact revision. | Reviewer can tell which repo version was rebuilt. |
| bash -n script.sh | Parses Bash syntax without executing; use before changed scripts. | Catches syntax errors but does not prove downloads/install succeed. |
| ./scripts/bootstrap/install-compose-plugin.sh | Installs pinned Compose CLI plugin and verifies SHA-256 before install. | Needed because this host's package repo lacked the Compose plugin. Version output confirms CLI availability; checksum mismatch must stop install. |
| ./scripts/bootstrap/init-claims-api-env.sh | Generates random local DB password and .env from example without printing secret; run once per clean clone. | Gives Compose/Spring matching configuration; refuses to overwrite an existing secret. |
| git check-ignore -v services/claims-api/.env | Shows which ignore rule excludes a file; run before staging. | Must show the .gitignore rule. Never cat, stage, or share .env. |

#### Docker Compose/PostgreSQL: database layer

| Command | What/when | Mapping to this lab |
|---|---|---|
| docker compose config --quiet | Validates YAML and resolves variables without starting containers; use after config edits. | Exit code 0/empty output means Compose model parsed and required values resolved. |
| docker compose up -d --wait --wait-timeout 60 | Creates/starts services detached and waits for health check up to 60 sec. | Starts PostgreSQL; wait is more meaningful than merely asking it to start. Still inspect health/readiness. |
| docker compose ps | Lists containers in this Compose project and their state/ports. | Shows PostgreSQL only; Spring runs as Java on EC2, not in Compose. |
| docker compose logs --tail 50 postgres | Reads recent DB logs; use when health/startup fails. | Look for initialization/auth/config errors or ready-to-accept-connections. Do not expose secrets in shared evidence. |
| docker compose exec postgres pg_isready -U claims -d claims | Runs PostgreSQL readiness probe inside container; -U selects DB user, -d database. | “accepting connections” proves DB listener readiness for this DB/user; not app health. postgres is exact Compose service name (not postgress/postgresdy). |
| docker compose exec postgres psql -U claims -d claims -c "SELECT ..." | Runs SQL in container; -c executes one statement. Use for independent persistence proof. | Compare SQL UUID/description with POST response to verify stored row. |
| docker compose down | Stops/removes Compose containers/network but retains named-volume data. | Can stop DB normally; next up can reuse data. docker compose down -v deletes the volume/rows; only use for intentional reset of disposable data. |

#### Maven, Spring, HTTP: application layer

| Command | What/when | Mapping to this lab |
|---|---|---|
| set -a; source .env; set +a | Exports variables from local config into current shell for child process. Use before Spring launch. | Supplies DB_URL/DB_USERNAME/DB_PASSWORD. Do not echo values; this is lab convenience, not a production secrets manager. |
| ./mvnw -version | Runs checked-in Maven Wrapper and prints Maven/Java versions. | Confirms pinned Maven and Java prerequisite; avoids relying on system Maven. |
| ./mvnw test | Resolves dependencies, compiles, runs tests; use after code changes and before packaging. | Checks create/read and blank-input behavior. Earlier ObjectMapper compile error meant test dependency missing from pom.xml. Passing tests do not prove a live app is available. |
| ./mvnw package | Runs Maven lifecycle and creates JAR under target; use when making an artifact for Docker/CI. | Later image/CI work will consume it; target is generated and Git-ignored. |
| ./mvnw spring-boot:run | Starts API in foreground with logs; use after DB ready and env loaded. | Default lab port is 8081. Ctrl+C stops this Java process. |
| ps -ef | Lists processes; combine with grep '[j]ava' to find Java without matching grep itself. Use after curl failure. | No Java process suggests app stopped/failed startup; inspect the visible startup logs. |
| ss -lntp | Shows TCP listeners/process when permitted; filter for :8081 or :5433. Use to check binding/port conflict. | No 8081 listener explains connection refusal; Java listener supports app-start diagnosis. |
| curl -i http://127.0.0.1:8081/actuator/health | Sends HTTP GET; -i includes status/headers, 127.0.0.1 means this EC2 host. | 200 and UP proves health endpoint answered, not a complete monitoring/SLO system. |
| curl -i -X POST http://127.0.0.1:8081/claims -H 'Content-Type: application/json' -d '{"description":"practice claim"}' | Sends JSON POST; -X method, -H header, -d body. | Expect 201 plus generated UUID/status/time. Use synthetic content only. |
| curl -i http://127.0.0.1:8081/claims/UUID | Sends GET; replace UUID with ID returned by POST. | Expect 200 and same record. Unknown ID gives 404; blank description POST gives 400. |

Command-reading basics: spaces separate arguments; a pipe sends output to another command; grep filters text. Run one command at a time while learning so the failing step is visible. Quotes protect JSON/space characters from the shell. sudo means administrator privilege; do not use it by habit. Flags mean different things in different commands; use that command's --help/manual before reusing them.

### 7.5 Troubleshooting: scenarios and evidence

Think in this order: symptom → user impact → scope → evidence → hypothesis → one safe change → recovery proof → ticket update. Check layers separately; do not restart everything before isolating the fault.

| Symptom | Checks | Interpretation/action | Recovery proof |
|---|---|---|---|
| docker permission denied | id; sudo systemctl status docker; reconnect; docker ps | Daemon may be stopped or login lacks refreshed docker group. Group membership applies on new login. Docker group grants effectively root-level access; only on disposable lab host. | id includes docker and docker ps succeeds. |
| docker compose unknown | docker --version; docker compose version; run pinned installer, inspect checksum result. | Engine exists but CLI plugin is missing. | Compose version prints. |
| DB unhealthy / API cannot connect to 5433 | docker compose ps; docker compose logs --tail 50 postgres; pg_isready; docker compose config --quiet. | Identify startup/config/readiness issue. Host 5433 maps to container 5432; Spring JDBC URL must use 127.0.0.1:5433. | Container healthy; readiness says accepting connections. |
| Maven says Jackson ObjectMapper missing | Read first compiler error; inspect pom dependencies and test imports. | Imported test library absent from test classpath. Declare correct dependency, then rerun tests; do not hide error by random code edits. | mvnw test reports success/count. |
| API curl connection refused | docker compose ps; ps -ef; grep '[j]ava'; ss -lntp; grep ':8081'; inspect Spring terminal logs. | DB healthy + no Java + no listener means API stopped/failed startup. Restore API; do not open security group for localhost test. | Health returns 200/UP; POST and GET succeed. |
| POST returns 400 | Inspect response, JSON, content type, nonblank description. | Validation rejected input as designed. | Valid POST gives 201; invalid remains 400. |
| GET returns 404 | Check copied UUID and query DB. | ID typo/missing row; not automatically a DB outage. | Correct UUID gets 200 and same SQL row. |
| Data missing after restart | Check volume, DB readiness/SQL, DB URL; ask whether down -v ran. | Named volume survives container recreation; deleting volume resets data. | Same UUID available through GET and SQL after API restart. |

Observed API outage drill: health curl failed; Compose showed PostgreSQL healthy; process and listener checks found no Java process and no 8081 listener. Diagnosis: Spring Boot had stopped, while DB remained available. Restarted app; health returned 200/UP. This is controlled lab evidence, not a production incident.

### 7.6 Ticket handoff and cleanup

A useful update states symptom/user impact, scope, checks/evidence, diagnosis, action, recovery check, remaining risk/follow-up, owner, and next update. Application owner handles API behavior; platform/DevOps owns shared runtime/build automation; DBA owns database platform concerns; SRE/service on-call coordinates reliability and incident response. Escalate with evidence and a specific request.

At end, stop Spring with Ctrl+C. Decide whether to stop or terminate EC2. Stop can leave EBS, public IPv4, snapshots, or other billable resources; verify AWS resources/costs. docker compose down retains named-volume rows; down -v deletes them. Process stop, container stop, EC2 stop, and EC2 termination are different actions. Keep this disposable host free of sensitive data and remove resources/credentials when no longer needed.
## 8. Work performed and observed evidence

On a fresh Amazon Linux 2023 host, the repo was cloned. Compose plugin installed with checksum verification; env bootstrap created ignored .env; Compose config validated; PostgreSQL healthy and pg_isready accepted connections. Maven reported **2 tests, zero failures, zero errors**. Actuator returned HTTP 200 and UP. Synthetic POST returned 201 with UUID; GET returned 200 and same claim; SQL showed the row. Spring Boot was restarted with PostgreSQL still running; GET still returned the record. This proves the app reconnected/read the existing row from the still-running DB. It does not prove DB restart, host reboot, backup, or restore.

**Observed API-stop drill:** health curl failed to connect; docker compose ps showed PostgreSQL healthy; ps found no Java process; ss showed no port 8081 listener. Diagnosis: Spring Boot had stopped; DB remained up. After restart, health returned HTTP 200/UP. This was a controlled lab drill, not a production incident.

## 9. Troubleshooting records and SRE method

| Symptom | Evidence/cause | Response/lesson |
|---|---|---|
| App folder absent under ec2-user | Files initially under root home; ~ means current user home. | Check whoami, pwd, absolute path, ownership before moving/chowning; use least privilege. |
| su ec2-user requested password | PuTTY key authentication differs from Linux su password auth. | Fresh PuTTY login as ec2-user; do not guess password. |
| Docker permission denied after usermod | Existing SSH session had old supplementary group list. | Reconnect; id should show docker. Restrict docker group because it is privileged. |
| docker compose unavailable | Docker Engine installed but Compose plugin absent. | Check/install/verify plugin independently. |
| DB healthy but curl refused | DB readiness does not prove Java listener exists. | Process -> listener -> app logs/health -> DB -> request. |
| API stopped drill | no Java process/listener; PostgreSQL healthy. | Restart app; verify health and GET; record evidence. |
| cat etc/os-release failed | missing leading slash made it a relative path. | Use /etc/os-release. |
| curl syntax error | Markdown link brackets/parentheses copied into shell. | Use plain URL and quote JSON. |
| Compose service not found | typo; actual service is postgres. | Read services in compose.yaml and use exact name. |
| Maven test compilation error on ObjectMapper | Class unavailable on attempted test classpath; it failed before tests. Later run passed two tests. | Read first compiler errors; inspect dependencies/imports; distinguish compile/test/runtime. |
| Postgres init logs showed shutdown | initial image setup may stop a temporary server before final one starts. | Check final logs and pg_isready, not one line in isolation. |
| git --online failed | invalid option typo. | Use git log -1 --oneline. |

**Incident routine:** assess impact/scope; note time/recent changes; collect read-only evidence (process, port, logs, health, DB, disk/memory); separate facts from hypotheses; choose bounded low-risk mitigation; verify health and representative action/data; record timeline, supported cause, mitigation, recovery proof, follow-up. Communicate/escalate by team procedure. Blameless follow-up improves guardrails rather than blaming a person. See [Google incident management](https://sre.google/sre-book/managing-incidents/).

## 10. Scenario practice — planned unless you record it as done

For each, predict symptom, collect evidence, state hypothesis, test, mitigate safely, verify. Never delete a volume just to clear an error.

- API stopped / DB healthy: compare curl, ps, ss, app logs, Compose; restart app; verify health + GET.
- Wrong DB password: app may log authentication failure while pg_isready still works. Fix local config without printing secret; retry read/write.
- PostgreSQL stopped: compare Compose state/logs, pg_isready, API symptoms; recover without removing volume; verify synthetic row.
- Container replaced with same volume: inspect mount and row. Volume persistence is not backup/restore.
- API port conflict: inspect ss and config; identify owner; never kill unknown process blindly.
- Disk pressure: inspect df -h /, df -i /, Docker usage/logs; never delete DB files.
- Blank request: expect 400 from validation.
- Unknown UUID: expect 404; distinguish missing record from API outage.
- Bad release (future drill): connect change to evidence, rollback to known-good, verify key user path.

Only the stopped-API drill is an observed controlled outage in this record. Others remain practice scenarios until actually performed.

## 11. Health vs monitoring and SLOs

A **health check** is a point-in-time signal. A **metric** is a value over time (request count, errors, latency, CPU, memory, queue). A **log** is timestamped event/message. A **trace** follows work across services. Day 1 manually used command output, app logs, HTTP, SQL. It did not build Prometheus, Grafana, Splunk, tracing, alerts, SLI/SLO, or error budget.

An **SLI** measures behavior, such as valid requests succeeding or a latency percentile. An **SLO** is a target for an SLI over a time window. Error budget expresses allowed unreliability implied by the target. One health endpoint and a few curl requests cannot define/prove an SLO. See [Google SRE Workbook monitoring](https://sre.google/workbook/monitoring/); implementation comes later.

## 12. Interview answers to rehearse

**Walk through the path:** “PuTTY connects to Amazon Linux as ec2-user. curl sends HTTP to Spring Boot on host port 8081. The controller validates JSON and uses Spring Data JPA/Hibernate via the repository; JDBC reaches PostgreSQL on host 5433 mapped to container 5432. PostgreSQL stores the row on a named volume. I verified POST, GET, and SQL.”

**API connection refused?** “I assess scope, then check process, listener, logs, health, and PostgreSQL separately. In the observed drill DB was healthy, but Java and port 8081 listener were absent. I restarted Spring Boot and verified 200/UP and GET. I would not infer DB failure from refusal alone.”

**What does pg_isready prove?** “That PostgreSQL accepts readiness checks at that endpoint. It does not prove app credentials, schema, API behavior, data persistence, or backup restore.”

**How did you prove persistence?** “POST returned ID, GET returned the record, SQL showed it. After restarting only the API, GET still found it while DB stayed up. DB restart, host reboot, backup/restore remain untested.”

**Why Compose doesn’t show Java?** “Spring runs directly on EC2; Compose manages PostgreSQL only. ps/ss check host Java, Compose checks database container.”

**How did you handle test error?** “First run failed during test compilation because ObjectMapper was not available on that classpath. Later run reported two passing tests. I separate compile, assertion, startup, and runtime failures.”

**Container vs volume?** “Container is runtime instance; writable layer has container lifecycle. Volume is separate mounted storage that can survive container replacement. It is not an off-host backup and does not prove restore.”

Use: **impact -> request path -> evidence -> hypothesis/test -> safest mitigation -> recovery proof -> remaining risk/follow-up**. Do not claim tests you did not perform.

## 13. Videos and reading order

Read this lesson first; then watch focused material, return to terminal, and teach the flow aloud. Pause to draw; video completion alone is not mastery.

1. **SRE/team/incident:** [SRE Fundamentals — Google Cloud Community](https://www.youtube.com/watch?v=eopc_ijIfLg). Focus what SRE solves, team structures, principles, responsibility areas. Read [on-call](https://sre.google/sre-book/being-on-call/) and [incident response](https://sre.google/sre-book/managing-incidents/).
2. **Linux:** [Linux Operating System Crash Course — freeCodeCamp](https://www.youtube.com/watch?v=ROjZy1WbCIA). Focus terminal, directories/files, system info, networking, package manager; skip desktop sections if short on time. Learn whoami, pwd, id, paths, groups, processes, ports—not every command.
3. **Spring/PostgreSQL/REST:** [CRUD in Spring Boot with PostgreSQL/JPA/REST](https://www.youtube.com/watch?v=6Evwt6nsRWs). Focus model, repository, controller, request and persistence; our API only creates/reads. Compare with [official Spring REST guide](https://spring.io/guides/tutorials/rest/).
4. **Maven:** [Maven tutorial for beginners](https://www.youtube.com/watch?v=b93NNK2J3GM). Focus POM, dependency, wrapper, test/package; reference [POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) and [lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle).
5. **Docker/Compose:** [Docker in 100 Seconds](https://www.youtube.com/watch?v=Gjnup-PuquQ) is quick orientation only; study [Compose docs](https://docs.docker.com/compose/) and [volumes](https://docs.docker.com/engine/storage/volumes/) for service, port, health, volume, ps/logs/exec.
6. **SQL:** [PostgreSQL tutorial](https://www.postgresql.org/docs/17/tutorial.html); focus table, row, SELECT, connection. Query tuning comes later.

**Day 1 target:** explain one request; locate each file; state ports and placement; run core checks; distinguish DB readiness from API health; diagnose actual API-stopped drill; say what evidence proves and does not prove. Advanced Java, SQL tuning, Kubernetes, Terraform, dashboards, and production database operations are later topics.

## 14. No-notes recall before Day 2

Spend 10 minutes before opening this file:

- Draw PuTTY/EC2 -> curl -> controller -> repository/JPA -> JDBC -> PostgreSQL container -> named volume -> HTTP response.
- Write ports 8081, 5433, 5432 and service postgres.
- Define user, group, directory, process, listener, status code, image, container, volume, readiness, env variable, POM, test.
- List checks for host, Git, Compose/DB, process/port, health, HTTP, SQL.
- Explain how DB can be healthy while API is refused.
- Retell the actual outage drill: symptom, evidence, cause, action, recovery proof.
- Mark scenarios not run unless you have output. Confirm .env ignored; save only sanitized evidence.

Then clone the personal repo on a fresh disposable host and rebuild from checked-in scripts/config. Record missing instructions, compare this file, fix the recipe, and repeat. Start Day 2 when you can recreate/explain Day 1, not merely after videos.

## 15. Cleanup and boundaries

Record instance ID/region/state/type, storage, public IP/security group, Java process, Compose services, and volume. Stop/terminate only resources you intend to remove and verify AWS state. EC2 stop can leave billable storage/resources. Deleting named volume deletes lab DB data; do this only for intentional reset after confirming data disposable. Never put credentials or real tokens in repo.

**Demonstrated:** fresh repo rebuild, two tests passing, DB readiness, API health, synthetic HTTP create/read, SQL row, data readable after API restart, controlled API-stop diagnosis/recovery.

**Not demonstrated yet:** PostgreSQL restart, EC2 reboot, backup/restore, managed secrets, CI/CD, production dashboards/alerts, measured SLI/SLO/error budget, multi-tenancy, HA, Kubernetes/EKS, Terraform provisioning, canary/blue-green, disaster recovery. These are future assignments; build and record evidence before claiming them.
