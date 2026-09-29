# Project Day 2 — Containerize the API with Docker Compose

**Ticket:** LAB-002  
**Date:** 2026-09-29  
**Status:** Build and runtime checks completed on the disposable EC2 lab host. This report records the observed work; GitHub publication and EC2 cleanup are the closeout actions.

## 1. Assignment and outcome

Package the existing Spring Boot claims API as a Docker image, run it alongside PostgreSQL with Docker Compose, verify the service-to-database request path, and troubleshoot build and connectivity failures.

**Outcome:** The API and PostgreSQL ran as separate containers. The API used Compose DNS to connect to PostgreSQL; API health, claim creation/retrieval, and the persisted SQL row were verified. The image ran as a non-root user. Two troubleshooting drills were completed.

## 2. Day 2 roles and ownership

In an organization, this work is shared:

- **Application developer/service owner:** owns Java behavior, API contract, tests, and runtime requirements.
- **DevOps/platform engineer:** owns repeatable image builds, deployment configuration, networking conventions, and delivery automation.
- **SRE:** defines operational checks, verifies dependencies and recovery, diagnoses failures from evidence, controls blast radius, and records follow-up.

For this lab, the learner performs each perspective. A one-host lab does not represent a production high-availability platform or prove ownership of a real company system.

## 3. Architecture and connections

```text
PuTTY / SSH
   |
   v
Amazon Linux EC2 host
   | 127.0.0.1:8081 (host-only published API port)
   v
claims-api container: Java 21 / Spring Boot / UID 10001
   | Compose network; DNS name postgres:5432
   v
postgres container: PostgreSQL 16
   |
   v
named volume: claims-api_claims-db-data
```

HTTP reaches EC2 port 8081. Docker forwards it to the API container. Spring MVC handles /claims; Spring Data JPA/Hibernate persists the entity; JDBC sends SQL to PostgreSQL. PostgreSQL stores files in the named volume. The API and DB use Compose’s private network.

The API container uses postgres:5432. Inside a container, 127.0.0.1 points to that same container. The database mapping 127.0.0.1:5433:5432 is for processes on EC2; it is not the API container’s DB address. Compose DNS resolves the service name postgres to the DB container.

## 4. Step-by-step build

1. **Preserved the Day 1 app and database.** Kept Java source and Postgres; Day 2 adds image packaging and container-to-container networking.
2. **Added .dockerignore.** Excludes .git, .env, .env.*, target, and logs so secrets, repository metadata, and generated output are not sent in the Docker build context.
3. **Added a multi-stage Dockerfile.** Maven/JDK builds the JAR; a separate Java 21 JRE stage runs it. The runtime uses UID/GID 10001. EXPOSE 8081 documents the image port; it does not publish a host port by itself.
4. **Updated Compose.** Defined claims-api and postgres, injected configuration through environment variables, set DB_URL to jdbc:postgresql://postgres:5432/claims, kept the Postgres health check and named volume, mapped API to 127.0.0.1:8081:8081 and DB to 127.0.0.1:5433:5432, and made API startup wait for Postgres health.
5. **Validated code/config.** docker compose config --quiet succeeded. ./mvnw test completed with 2 tests, 0 failures, 0 errors. Tests were run separately; the image packaging command used -DskipTests after the test run.
6. **Built and inspected image.** Built claims-api:day2, inspected user/port metadata, and ran id in a disposable container; UID/GID 10001 was confirmed.
7. **Started stack.** After fixing Buildx below, docker compose up -d --build --wait --wait-timeout 120 succeeded. Both containers ran and Postgres was healthy.
8. **Verified live request path.** Used pg_isready, Actuator health, POST /claims, GET /claims/{id}, and a read-only SQL query.

## 5. Files added or changed

- services/claims-api/Dockerfile — multi-stage image build and non-root runtime.
- services/claims-api/.dockerignore — excludes local secrets and generated files from build context.
- services/claims-api/compose.yaml — API/DB services, networking, health dependency, loopback ports, persistent volume.
- scripts/bootstrap/install-buildx-plugin.sh — installs pinned Buildx and verifies SHA-256 before installation.
- Existing Java source, Maven configuration, .env.example, and secret initialization script were retained. The real .env is ignored and is not part of the image or repository.

## 6. Command reference: what, why, when

| Command | What it checks/does and why useful | When to use |
|---|---|---|
| docker compose config --quiet | Renders and validates Compose YAML and variables without starting services. | Before deployment and after config edits. |
| docker build -t claims-api:day2 . | Builds a named image from this directory’s Dockerfile and context. | To isolate image-build failures from Compose startup failures. |
| docker image inspect claims-api:day2 --format '{{.Config.User}} {{json .Config.ExposedPorts}}' | Reads configured user and exposed-port metadata. | To verify image settings without starting the app. |
| docker run --rm --entrypoint id claims-api:day2 | Runs id in a disposable container, then removes it. | To prove which user the image runs as. |
| docker compose up -d --build --wait --wait-timeout 120 | Builds and starts detached services, then waits on startup/readiness signals. | To deploy after changes. Up alone is not an application health check. |
| docker compose ps | Shows service state, age, and published ports. | First check when a service appears unavailable. |
| docker compose logs --tail=60 claims-api | Reads recent API logs. | To inspect startup exceptions or runtime errors. |
| docker compose exec postgres pg_isready -U claims -d claims | Checks DB readiness inside its container. | To distinguish database readiness from API availability. |
| curl -i http://127.0.0.1:8081/actuator/health | Calls the live API and prints HTTP status/headers. | To check health and verify recovery. |
| docker compose exec claims-api printenv DB_URL | Shows the non-secret JDBC URL used by the API. | To confirm active service hostname/port; never print passwords. |
| docker compose exec postgres psql ... -c 'SELECT ...' | Runs a read-only SQL query. | To verify an API write reached PostgreSQL. |

## 7. Observed verification evidence

- ./mvnw test: **2 tests passed; 0 failures; 0 errors**.
- Compose configuration parsed and listed postgres and claims-api.
- Final Compose build succeeded with Buildx v0.17.1; both services started and Postgres was healthy.
- Image metadata showed user 10001:10001; the disposable id check reported uid=10001 gid=10001.
- pg_isready reported PostgreSQL accepting connections.
- GET /actuator/health returned HTTP 200 and status UP.
- POST /claims returned HTTP 201 for synthetic description “day2 containerized smoke test”; ID 37d079a8-858c-4afd-88f2-a4f69b1a523a.
- GET /claims/37d079a8-858c-4afd-88f2-a4f69b1a523a returned HTTP 200 and the same claim.
- A SQL query returned the matching PostgreSQL row.
- The API environment showed jdbc:postgresql://postgres:5432/claims.
- After rebuilding/recreating the API container, health and retrieval still worked, and the database row remained. This verifies the DB volume retained data across API replacement; it does not prove DB host failure recovery.

The API has no Compose health check in this increment. Compose Up shows a running container; the separate HTTP health request is the application-level check.

## 8. Troubleshooting drills

### Drill A — Compose required a newer Buildx

**Symptom:** docker compose up -d --build failed with “compose build requires buildx 0.17.0 or later”.

**Triage:** Checked versions: Compose v5.5.1, Buildx v0.12.1. The error pointed to local builder/plugin compatibility, not Java source.

**Mitigation:** Started the already-built image with --no-build to keep the lab running while fixing the builder. dnf search buildx found no package. Added a bootstrap script for pinned Buildx v0.17.1; it downloads the architecture-specific release, verifies SHA-256, installs under the user’s Docker CLI plugin directory, and checks the version.

**Recovery evidence:** Checksum passed, Buildx reported v0.17.1, and the normal Compose build then succeeded.

**Lesson:** Identify the failing layer, check versions, avoid changing app code for a tooling issue, and verify the intended build after repair.

### Drill B — API could not resolve database hostname

**Scenario:** Ran a disposable API container with deliberately misspelled JDBC hostname postgress.

**Evidence/diagnosis:** Startup logs reported UnknownHostException for postgress. Compose service is named postgres; the wrong DNS name cannot resolve. Hibernate’s later inability to infer a DB dialect was downstream of the failed DB connection.

**Recovery/impact:** The wrong host was confined to the disposable container. The normal Compose stack remained available; HTTP health and claim retrieval succeeded afterward. Correct internal URL: jdbc:postgresql://postgres:5432/claims.

**Lesson:** Check service DNS/network name before changing credentials or DB data. Distinguish name resolution, TCP connection, authentication, and SQL/schema errors by finding the earliest causal error.

## 9. SRE/DevOps connection and boundaries

- **Repeatability:** Dockerfile and Compose turn manual setup into a reproducible build/run recipe.
- **Release engineering:** versioned image builds and Maven tests are foundations for CI; no CI pipeline was built today.
- **Security:** build/runtime separation, .dockerignore, loopback-only host ports, secret exclusion, and non-root runtime reduce exposure; they are not a complete production security program.
- **Reliability:** dependency readiness, app health, data verification, and bounded fault drills exercise operational validation.
- **Incident method:** symptom → evidence → hypothesis → narrow check → mitigation → recovery verification → follow-up.

This is a single-EC2 learning lab, not an HA production deployment. Kubernetes/EKS, TLS ingress, CI/CD, managed RDS, centralized observability, SLO/error budgets, and DR are future increments.

## 10. Interview questions for LAB-002

1. **Why does the containerized API use postgres:5432 rather than 127.0.0.1:5433?** Containers have separate loopback interfaces. Compose DNS resolves the service name; 5432 is the internal DB port. Host port 5433 is for EC2 processes.
2. **What does a multi-stage Dockerfile achieve?** Build tools stay in the build stage; the runtime uses a JRE and the packaged JAR. The process uses UID 10001. I verified the user; I did not measure a size reduction.
3. **What did the Postgres health check prove?** pg_isready showed Postgres accepted connections at that time. It did not prove API routes worked, so I separately checked health, POST, GET, and SQL.
4. **How did you diagnose the build failure?** The error named a minimum Buildx version. I inspected the installed version, installed a checksum-verified compatible version, and rebuilt.
5. **How did you diagnose the hostname failure?** The earliest causal error was UnknownHostException for a misspelled host. I compared it with the Compose service name and used postgres.
6. **Does a passing test prove production is healthy?** No. It proves tested behavior passed at that time. Live health, reachability, dependencies, metrics, and SLOs require their own checks.
7. **What does the named volume protect, and what removes it?** It preserves DB files across container replacement. docker compose down -v removes the volume and lab data.

## 11. Security, rollback, cleanup

The real .env contains a local database password and must remain untracked; do not print or upload it. Host mappings bind to EC2 loopback. Keep the DB volume until intentionally cleaning the lab; do not use docker compose down -v unless deleting data is intended.

If rolling back to the Day 1 host-run API, stop the Compose API first to free port 8081, keep Postgres running, then start the host process with the local environment loaded. Verify health and a claim read afterward. This rollback was documented but not exercised today.

**EC2 cleanup:** Pending GitHub publication verification. After code and this report are visible in the personal repo, stop or terminate EC2 in AWS and check for remaining billable resources. Terminating compute does not necessarily remove every separately billed resource.

## 12. Completion record

LAB-002 implementation and technical acceptance checks are complete based on the evidence above. GitHub publication and EC2 cleanup are operational closeout steps. This records observed lab work, not an employer production deployment or incident.
