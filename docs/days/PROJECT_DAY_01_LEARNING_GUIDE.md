# Day 1 Learning Guide: Build and Operate the Claims API

## How to use this guide

Study one section at a time. Before rereading a section, draw or explain it from memory. For every command, say what it checks and what it cannot prove. The goal is understanding the service and its failure paths, not memorizing commands without context.

Day 1 builds a small Java web service backed by PostgreSQL on one EC2 lab host. It teaches Linux access, source control, build and test basics, containers, HTTP, persistence, health checks, and first-response troubleshooting. It does not yet build Terraform, CI/CD, EKS, dashboards, SLOs, multi-AZ, or disaster recovery.

## 1. Plain-language vocabulary

| Term | Beginner meaning | Day 1 example |
| --- | --- | --- |
| EC2 instance | Virtual computer rented from AWS | Amazon Linux host reached with PuTTY |
| Operating system | Software that manages the computer and runs programs | Amazon Linux 2023 |
| SSH | Encrypted remote terminal connection | PuTTY connects using a key pair |
| Shell | Program that reads terminal commands | Bash prompt for ec2-user |
| User and group | Linux identity and collections of permissions | ec2-user was added to docker group |
| Process | A program currently running | Spring Boot running as a Java process |
| Port | Numbered network entry point a process can listen on | API on 8081; PostgreSQL inside its container on 5432 |
| Client and server | Request sender and request handler | curl is the client; Spring Boot is the server |
| HTTP API | Agreed request and response format for software | POST /claims and GET /claims/{id} |
| Database | Program that stores and retrieves structured records | PostgreSQL stores claim rows |
| Image and container | Packaged template and running instance of the template | postgres:16-alpine image creates the DB container |
| Volume | Storage managed outside a container writable layer | claims-db-data stores PostgreSQL files |
| Environment variable | Runtime setting supplied outside source code | DB_URL and DB_PASSWORD |
| Health check | Check that reports a component current readiness or health | pg_isready for DB; Actuator for API |
| Git commit | Recorded snapshot of tracked file changes | Day 1 source and notes in repository history |

## 2. Architecture and two paths to remember

    Windows laptop -- PuTTY/SSH --> Amazon Linux EC2
                                         |
                                         +-- Git checkout of personal repo
                                         +-- Spring Boot Java process, host port 8081
                                         +-- Docker Compose
                                                +-- PostgreSQL container, port 5432
                                                +-- named data volume

    curl -> host 127.0.0.1:8081 -> Tomcat/Spring -> controller -> repository/JPA
         -> JDBC connection pool -> host 127.0.0.1:5433 -> Docker forwarding
         -> PostgreSQL container port 5432 -> claims table -> HTTP response

There were two stages in the learning history. The earlier one-off EC2 setup used the app on 8080 and PostgreSQL host port 5432 in a container named claims-db. The fresh repository rebuild uses the checked-in Compose project: app host port 8081, PostgreSQL host port 5433, and Compose service name postgres. Do not mix those commands or port numbers. When rebuilding from this repo, follow services/claims-api and use 8081/5433.

A create request travels through these layers:

1. curl sends HTTP to port 8081.
2. Embedded Tomcat in Spring Boot accepts the connection.
3. ClaimController receives the route and JSON body.
4. Validation rejects a blank description.
5. ClaimRepository asks Spring Data JPA to save the Java Claim.
6. Hibernate/JPA maps fields to database columns; JDBC sends database operations.
7. HikariCP supplies a database connection from its pool.
8. PostgreSQL stores the row and returns the result.
9. Spring Boot serializes the result as JSON and sends an HTTP response.

## 3. Repository map: what each file owns

    services/claims-api/
      pom.xml                         Maven project, dependencies, plugins, Java target
      mvnw / mvnw.cmd                 Maven wrapper scripts for Linux and Windows
      .mvn/wrapper/                   Maven version and wrapper configuration
      .env.example                    Safe list of local settings; placeholder only
      compose.yaml                    PostgreSQL container, ports, volume, health check
      src/main/java/com/example/claims/
        ClaimsApiApplication.java     Main method; starts Spring Boot
        ClaimController.java          HTTP routes, validation, response codes
        Claim.java                    Java entity mapped to database columns
        ClaimRepository.java          Spring Data JPA database access
      src/main/resources/
        application.properties        Port and externalized DB/Actuator settings
      src/test/java/com/example/claims/
        ClaimsApiApplicationTests.java Automated behavior tests

Root scripts/bootstrap contains setup scripts. docs/days/PROJECT_DAY_01.md is the daily record; this file is the deeper teaching guide. .gitignore excludes .env, keys, logs, build output, and local artifacts. The real .env password and database volume must never be committed. Git stores source and setup instructions, not the running machine, secret, or live database.

### What the Java files do

ClaimsApiApplication has the main method. Spring Boot discovers annotated components and starts the web server.

ClaimController maps /claims to create and /claims/{id} to retrieve. It accepts JSON. The @Valid and @NotBlank annotations reject a blank description. It returns 201 for a created claim, 200 for a found claim, 404 for an unknown ID, and validation returns 400.

Claim is a JPA entity. Its fields are ID, description, status, and createdAt. JPA annotations describe the table and column mapping. ClaimRepository extends JpaRepository, which supplies save and findById without handwritten SQL for those basic operations.

The controller separates HTTP handling from persistence access. Larger services commonly add explicit service/business-logic layers, DTOs, authorization, transactions, and database migrations. This small API is intentionally minimal.

## 4. Why these technologies, and alternatives

### Spring Boot

Spring Boot is a common Java framework for web services. It integrates HTTP routing, validation, database libraries, and Actuator health endpoints, and matches the Java/Spring technology we want to learn. Other teams may use Node.js, Python/FastAPI, .NET, or Quarkus. The decision depends on team skills, platform standards, ecosystem, performance, supportability, and requirements. There is no universally best framework.

### PostgreSQL

PostgreSQL is a relational database: data is organized in tables and accessed with SQL. It supports transactions and constraints useful for consistent business records. MySQL is also valid and can support this lab. Organizations choose based on existing standards, workload, required features, integrations, operational expertise, and cost. This lab teaches PostgreSQL; it does not prove PostgreSQL is always preferable.

### Docker and Compose

Docker runs PostgreSQL in an isolated container from a known image. Compose describes how to start it consistently. One local dependency on one host is a good place to learn Compose. Kubernetes/EKS adds scheduling, controllers, service discovery, health management, rollout behavior, and cluster operations; it is a later stage when those capabilities are needed.

### Maven

Maven downloads Java dependencies, compiles source, runs tests, and packages applications. pom.xml declares project configuration. The Maven Wrapper uses the version recorded in .mvn/wrapper so machines use a consistent Maven release without requiring manually installed Maven.

### Actuator and curl

Actuator exposes operational endpoints such as health. curl exercises HTTP without a browser. Both are useful first-response tools, not a complete monitoring platform.

## 5. PostgreSQL connection and persistence

Compose maps 127.0.0.1:5433 on EC2 to port 5432 inside the database container. The app runs on EC2 itself, so it connects through EC2 loopback using a JDBC URL like jdbc:postgresql://127.0.0.1:5433/claims. In the port mapping, the left side is the host port and the right side is the container port. Binding to 127.0.0.1 limits access to the host instead of publishing the database on all network interfaces.

Compose reads .env for substituting values into compose.yaml. The Java process also needs its settings in its own environment. We loaded them into the shell before launching Maven:

    set -a
    source .env
    set +a
    ./mvnw spring-boot:run

set -a exports variables defined after it. source reads .env into the current shell. set +a disables automatic exporting again. Do not run cat .env or paste its contents into chat. Production credentials belong in managed secret storage with access controls, not a local shell file.

The named Docker volume is mounted at PostgreSQL data directory. If a container is recreated while the volume is retained, data can remain. A volume is not a backup: it does not protect against host loss, accidental volume deletion, corruption, or bad recovery. Never use docker compose down -v casually; -v removes Compose volumes and can delete the lab database.

The lab sets spring.jpa.hibernate.ddl-auto=update. This lets Hibernate create or update the simple table while learning. Production schema changes normally use reviewed, versioned database migrations for controlled and auditable changes.

## 6. Build and verification sequence

Run commands from the correct directory. pwd prints the current directory; cd changes it. The app directory is services/claims-api inside the repo.

    git status --short --branch
    java --version
    docker --version
    docker compose version
    ./mvnw -version
    docker compose config --quiet
    ./scripts/bootstrap/init-claims-api-env.sh
    docker compose up -d --wait --wait-timeout 60
    docker compose ps
    docker compose exec postgres pg_isready -U claims -d claims

These commands identify branch and tools, validate Compose configuration, generate a local ignored environment file, start the database in detached mode, wait for its health check, list services, and check PostgreSQL readiness. pg_isready does not prove the API is healthy.

Run tests and start the service:

    set -a
    source .env
    set +a
    ./mvnw test
    ./mvnw spring-boot:run

The test command compiles code and tests, then runs tests. The observed run had 2 tests with no failures or errors. A blank-description validation warning was expected by its test. Keep Spring Boot running in one PuTTY window and use another to test HTTP.

    curl -i http://127.0.0.1:8081/actuator/health
    curl -i -X POST http://127.0.0.1:8081/claims -H 'Content-Type: application/json' -d '{"description":"synthetic lab claim"}'
    curl -i http://127.0.0.1:8081/claims/PASTE_RETURNED_ID
    docker compose exec postgres psql -U claims -d claims -c "SELECT id, description, status, created_at FROM claims;"

The POST response supplies the ID for GET. HTTP 201 means created; 200 means successful retrieval; 400 means request validation failed; 404 means the ID was not found. SQL independently confirms the stored row. Use plain URLs in shell; Markdown brackets and parentheses are not part of the URL and can cause shell errors.

## 7. What checks prove and do not prove

- Maven tests prove tested code paths behaved as expected in the test environment. They do not prove AWS networking, load performance, or production availability.
- PostgreSQL health check proves the database accepted its local readiness check. It does not prove the Java application is running.
- Actuator health returning 200/UP proves the endpoint reported that status at that time. It does not by itself prove external-user access, every business flow, an SLO, or monitoring coverage.
- POST followed by GET proves the API returned the claim. A matching SQL row proves persistence in PostgreSQL.
- Reading the same record after restarting Spring Boot proves the API reconnected to the still-running database and its retained volume. It does not prove a database restart, host reboot, backup, or restore.
- docker compose ps shows Compose-managed containers. It does not list a Java process started directly on EC2.

Evidence is stronger when multiple independent checks agree: process, listener, application response, dependency readiness, logs, and SQL row.

## 8. Command reference and troubleshooting method

| Command | Meaning and use |
| --- | --- |
| whoami | Current Linux username |
| hostname | Host system name |
| pwd | Current working directory |
| ls -la | Files including hidden entries, owners, and permissions |
| cat /etc/os-release | Linux distribution; leading slash means absolute path |
| uname -m | CPU architecture, such as x86_64 |
| id | User ID and groups; useful for permissions |
| free -h | Memory summary in readable units |
| df -h / | Disk free space for root filesystem |
| nproc | CPU processing units visible to Linux |
| git status --short --branch | Branch and modified/untracked files |
| git log -1 --oneline | Latest commit in compact form |
| find services -type f | Find files under services; maxdepth can hide nested files |
| bash -n script.sh | Check Bash syntax without running a script |
| ps -ef | List running processes |
| ss -lntp | Inspect listening TCP ports and process owners |
| docker ps | Running containers |
| docker compose ps | Containers in this Compose project |
| docker logs --tail 30 NAME | Last 30 lines of container logs |
| docker compose exec postgres psql ... | Run psql inside PostgreSQL container |
| git diff --check | Find whitespace mistakes before commit |

Troubleshoot in an order that narrows the layer: record symptom and time; check impact and recent changes; verify command and working directory; inspect process; inspect listener; inspect application logs and health; check dependency readiness; test the user/API path; verify database state. Separate observed facts from hypotheses. Change one thing at a time, choose a low-risk mitigation, and verify recovery before saying the issue is resolved.

## 9. Actual issues observed

### API was not initially listening

A first curl to port 8081 returned connection refused. Later requests succeeded once Spring Boot was running. In the controlled outage drill, we stopped Spring Boot; curl failed, ps showed no Java process, and ss showed no listener on 8081 while PostgreSQL stayed healthy. Restarting Spring Boot restored HTTP 200/UP. This API-stopped scenario was practiced.

### Docker group permission required a new login

After usermod added ec2-user to docker group, the existing SSH session did not yet have that group. id did not show docker and docker ps returned permission denied. Reconnecting created a session with new group membership. id then showed docker and docker ps worked. Docker group grants powerful host access; restrict it to trusted lab operators.

### Docker Compose plugin was missing

Docker Engine existed but docker compose did not. A pinned plugin installer downloaded a release and checked its SHA-256 before installing it in the user's Docker CLI plugin directory. bash -n checked script syntax; running it printed checksum OK and the Compose version. Checksums help detect unexpected binary changes; use trusted sources and reviewed hashes.

### Wrong path, option, or service name

cat etc/os-release failed because it looked relative to the working directory; cat /etc/os-release succeeded. git log --online failed due to a typo; git log --oneline is correct. A misspelled Compose service such as postgress does not match service postgres. Check directory, spelling, and service names before changing the system.

### Maven test compilation issue

A previous test compile complained Jackson ObjectMapper was unavailable. That was a compile-time dependency/import problem, not a running API outage. Read the first compiler error and inspect pom.xml and dependency configuration. A later test run completed with two tests passing. Keep build logs free of secrets.

### GitHub authentication issue

A commit can succeed locally when push fails. The local branch showed ahead by one commit after GitHub rejected authentication. A username prompt must receive the exact GitHub username; a fine-grained token goes at the password prompt. Never paste or screenshot tokens in chat. Verify push by checking git status and the remote commit ID.

## 10. Scenario status

**Practiced: API process stopped.** Health endpoint was refused. PostgreSQL stayed healthy, no Java process existed, and port 8081 had no listener. Diagnosis was stopped Spring Boot. Restart restored HTTP 200/UP.

**Automated test practiced: blank description.** Validation rejected blank input; test expected HTTP 400 and passed.

**Implemented, not separately drilled: unknown claim ID.** Request a nonexistent UUID and verify HTTP 404.

**Not yet practiced: PostgreSQL unavailable.** In disposable lab, stop only service postgres using docker compose stop postgres. Record Compose state, API health, and logs; attempt a harmless API request. Recover using docker compose start postgres; wait healthy; check pg_isready, API health, and known synthetic row. Do not run down -v, docker volume rm, or delete database files. Observe the actual API error instead of assuming.

**Not yet practiced: wrong local DB password.** In disposable lab only, temporarily use an incorrect password, restart API, inspect logs without printing secret, identify authentication failure, restore local setting, restart, and verify a known request. Never commit the password.

**Not yet practiced: PostgreSQL restart with volume.** Record synthetic claim, restart container without removing volume, wait healthy, then verify readiness and query the claim. This differs from the completed API-process restart test.

## 11. SRE/DevOps role and organizational flow

A first-day engineer normally completes approved onboarding, learns service ownership and escalation channels, reads architecture and runbooks, shadows experienced staff, learns change controls, and uses non-production systems before risky production changes. Exact duties vary by company. A manager sets priority and expected outcome; the engineer clarifies acceptance criteria, investigates or implements, tests, reviews changes, communicates status, records evidence, and updates documentation.

Application engineers commonly own service code and business behavior. Platform/DevOps teams build paved paths, build/deploy automation, and infrastructure tooling. SREs work with service teams on reliability goals, observability, incident response, capacity, toil, and recovery. Database specialists may own performance, backup, access, and recovery. Security engineers set controls and review risks. During incidents, a designated lead coordinates; responders diagnose and mitigate; a scribe records the timeline and evidence. Teams share ownership according to their operating model.

Our lab ticket was to establish a safe baseline where a synthetic claim can be accepted, persisted, and retrieved, and where an operator can distinguish API health from DB readiness. The outage drill was controlled practice, not a production incident or proof of real on-call experience.

## 12. Interview answers and memory practice

**How did you diagnose the API failure?**

> The health request to port 8081 was refused. Compose showed PostgreSQL healthy. The Java process and listening socket were absent, so evidence pointed to Spring Boot being stopped. I restarted it and verified HTTP 200/UP.

**How did you verify persistence?**

> POST returned an ID, GET returned the claim, and SQL showed the matching row. After restarting the API process, GET still returned it. That confirms persistence across API restart while PostgreSQL and its volume remained running; it does not prove backup/restore or database-host recovery.

**Why can the DB be healthy while API is unavailable?**

> They are separate processes and failure domains. DB readiness says PostgreSQL accepts connections. The API can still be stopped or not listening, so I check application health and process/listener state too.

**Is this production-ready or highly available?**

> No. It is a single-host learning lab. It lacks multi-AZ, load balancing, managed secrets, backup/restore proof, production monitoring, SLOs, and tested failover. It establishes the API/database and troubleshooting foundation for later stages.

Before the next day, draw the architecture without looking and answer: What runs on EC2? What is a container? Why are ports 8081, 5433, and 5432 different? What path writes a row? What does each health check prove? What did the outage drill prove? What remains untested?

## 13. Day 1 completion and boundaries

Observed and completed: fresh repo clone on Amazon Linux 2023; Java/Git/Docker installed; Docker Compose plugin checksum verified; local ignored .env generated; PostgreSQL container healthy with volume; Maven tests passed; API health returned UP; claim POST/GET worked; SQL showed row; API restart retained read access; controlled API-stopped diagnosis and recovery recorded.

Documentation closeout still needs the guide and updated daily notes committed on the personal repo branch, then a recall exercise without notes. PostgreSQL outage and container-restart persistence are optional Day 1 drills and must not be labeled practiced unless actually performed.

Day 1 is a foundation, not the whole resume platform. Later stages add Docker image builds, CI/CD, Terraform, AWS networking/IAM, EKS/Kubernetes, observability dashboards/logs/traces, SLIs/SLOs, alerting, scaling, multi-tenancy, security controls, cost review, and disaster recovery one increment at a time.
