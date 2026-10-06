# SRE and DevOps Interview Question Bank

A growing, beginner-friendly interview wiki for the systems built in this repository. Stable question IDs let each day's project notes, tickets, and mock interviews point to the same question.

This is a curated learning resource, not a copy of the whole internet. Write original explanations, use hands-on evidence, and link authoritative sources for technical claims. Popularity cannot be guaranteed; accuracy, reproducibility, and helpful contributions are the goal.

## How to use this bank

1. Try the current day's questions without notes.
2. Follow the stable ID below for the answer outline and follow-ups.
3. Answer aloud in your own words, using only evidence you actually produced.
4. Mark each item: Can explain / Needs another attempt / Not practiced.
5. Revisit prior IDs before adding the next day's questions.

Each day note keeps that day's numbered question list and topic mapping. This bank is the cross-day index and expanded prep guide. IDs stay stable: D01-INT-001 means Day 1, Interview question 1.

## Day 1 — Linux host, Spring API, Docker Compose, PostgreSQL

**Day guide:** [Project Day 1](../docs/days/PROJECT_DAY_01.md#day-1-interview-question-map)

### D01-INT-001 — Trace a request from HTTP to a database row

**Day 1 topics:** HTTP methods/status codes; Spring MVC; validation; repository; JPA/Hibernate; JDBC; host/container ports; PostgreSQL; named volume.

**Question:** Walk me through what happens when a client submits a claim, from HTTP request until the row is stored.

**Answer outline:** In this lab, curl on EC2 sends POST /claims to Spring Boot on host port 8081. Embedded Tomcat accepts HTTP; Spring MVC routes to ClaimController; validation rejects a blank description. The controller creates a claim with UUID, status, and timestamp. ClaimRepository uses Spring Data JPA; Hibernate maps the entity to SQL; JDBC connects to PostgreSQL at host port 5433. Compose maps 5433 to container port 5432. PostgreSQL writes to storage mounted from a named volume. The API returns HTTP 201 and JSON. We compared that UUID in GET and SQL.

**Evidence:** Synthetic POST response, matching GET response, matching SQL row.

**Follow-ups:** What differs between host port 5433 and container port 5432? What does the repository do? What status does blank input receive? What does an unknown UUID receive?

**Source:** [Spring REST guide](https://spring.io/guides/tutorials/rest/).

### D01-INT-002 — Explain what a passing Maven test proves

**Day 1 topics:** pom.xml; dependencies; Maven Wrapper; test lifecycle; test context; MockMvc; tests versus live smoke checks.

**Question:** What does ./mvnw test prove, and what does it not prove?

**Answer outline:** It compiles this project and runs its automated tests in that run's environment. Our two Spring Boot tests cover create/read and rejection of blank input. A pass supports those assertions; it does not prove the separately launched API is listening, remotely reachable, continuously available, meeting an SLO, or recoverable from backup. We separately checked live health, POST, GET, and SQL.

**Evidence:** Test summary with two tests and no failures, plus separate live checks. Distinguish compile failures, assertion failures, and runtime outages.

**Follow-ups:** What is in pom.xml? Why use Maven Wrapper? What did the Jackson ObjectMapper compile error tell you? What belongs in CI?

**Sources:** [Maven POM guide](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html), [Maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle).

### D01-INT-003 — Troubleshoot an unreachable API when PostgreSQL is healthy

**Day 1 topics:** Incident triage; process; TCP listener; application logs; dependency health; safe mitigation; recovery proof.

**Question:** The API returns connection refused, but PostgreSQL is healthy. What do you check next?

**Answer outline:** Confirm URL and scope, then inspect the Java process (ps), TCP listener (ss), and Spring Boot console logs. Confirm expected port/configuration and check DB connectivity separately. In our controlled drill PostgreSQL was healthy, but there was no Java process and no listener on 8081. We restarted Spring Boot only, then checked Actuator health and a representative GET. We did not change the security group because curl ran locally on EC2.

**Evidence:** Curl failure; Compose/DB readiness; process/listener checks; post-recovery health and GET. Label it a controlled lab drill, not a production incident.

**Follow-ups:** How does connection refused differ from a timeout? What if Java exists but no listener? What if a listener exists but health is DOWN? What change would you avoid until evidence narrows the cause?

**Source:** [Google SRE incident management](https://sre.google/sre-book/managing-incidents/).

### D01-INT-004 — Explain the Compose health check and named volume

**Day 1 topics:** Image versus container; readiness; Compose service; port mapping; persistent storage; limits of local persistence.

**Question:** Why does the Compose database have a health check and a named volume?

**Answer outline:** The health check uses pg_isready to tell us PostgreSQL accepts connections, rather than just having a container process started. It does not prove the API can authenticate or save data. The named volume stores database files outside the container writable layer, so replacing a container can retain data while the same volume and host storage remain. A volume is not a backup; it can be deleted and remains on the same host.

**Evidence:** Compose says healthy; pg_isready accepts connections; the same synthetic UUID remains readable after restarting only the API. Do not claim a DB restart or restore was tested.

**Follow-ups:** What does docker compose down do? What extra risk does down -v have? How might production persistence differ, for example with RDS and backups?

**Sources:** [Docker Compose](https://docs.docker.com/compose/), [Docker volumes](https://docs.docker.com/engine/storage/volumes/).

### D01-INT-005 — Rebuild the service on a fresh EC2 host

**Day 1 topics:** Linux inventory; package installation; systemd; Git; dependencies; secrets; repeatable configuration; verification; cleanup.

**Question:** How would you rebuild Day 1 from a fresh Amazon Linux 2023 EC2?

**Answer outline:** Start with a disposable host, restricted access, and cleanup plan. SSH as ec2-user; inventory identity, OS, architecture, capacity, and tools. Install Git, Java 21, Docker, curl, and OpenSSL; enable Docker; add the lab user to the Docker group and reconnect. Clone only this repo and record branch/commit. Install the pinned Compose plugin; generate ignored local .env; validate Compose; start PostgreSQL and verify readiness; export local settings; run Maven tests; start Spring in a visible terminal. From a second session check health, POST, GET, and matching SQL. Run the controlled API-stop drill, record sanitized evidence, and clean up AWS resources.

**Evidence:** Commit hash; test count; DB readiness; API status codes; matching UUID in GET/SQL; outage diagnosis/recovery; cleanup record.

**Follow-ups:** Why check OS/architecture first? Why reconnect after usermod? Why not expose PostgreSQL publicly? What may remain billable after stopping EC2?

**Source:** [Amazon Linux 2023 user guide](https://docs.aws.amazon.com/linux/al2023/ug/what-is-amazon-linux.html).

### D01-INT-006 — Prove persistence after an API restart

**Day 1 topics:** Data path; SQL verification; API lifecycle; named volume; limits of evidence.

**Question:** How do you know the claim was stored and remained available after a restart?

**Answer outline:** POST returned a UUID; GET returned the same claim; SQL showed the matching row. We restarted Spring Boot while PostgreSQL and its volume remained running, then GET returned the row again. This proves the restarted API could read from the still-running DB. It does not prove DB restart, EC2 reboot, backup, restore, or disaster recovery.

**Follow-ups:** Persistence versus backup? What would you test for recovery? How would you protect credentials and data in a real environment?

**Source:** [Docker volumes](https://docs.docker.com/engine/storage/volumes/).

### D01-INT-007 — Write the ticket update and explain team ownership

**Day 1 topics:** Acceptance criteria; impact/scope; evidence; escalation; application/platform/database/SRE responsibilities; blameless learning.

**Question:** What belongs in a ticket update for this API-stop drill, and who owns the next actions?

**Answer outline:** Record time, symptom, impact/scope, sanitized evidence, supported diagnosis, action, recovery proof, remaining risk, follow-up owner, and next update. Typically the application team owns endpoint behavior; platform/DevOps owns shared build/runtime automation; a database/platform team owns DB platform concerns; SRE partners on reliability, incident response, runbooks, and toil reduction. Boundaries vary. Escalate with evidence and a specific request.

**Evidence:** PostgreSQL healthy; Java and port 8081 listener absent; Spring restarted; health returned 200/UP and GET succeeded. This was controlled practice.

**Follow-ups:** What do you communicate before mitigation? When do you escalate? How do you keep a postmortem blameless? What follow-up could prevent an unnoticed API stop?

**Sources:** [Google SRE incident management](https://sre.google/sre-book/managing-incidents/), [Google SRE on-call](https://sre.google/sre-book/being-on-call/).

## Reusable answer frameworks

- Technical explanation: **flow -> component responsibility -> evidence -> limits**.
- Troubleshooting: **impact -> scope -> checks -> hypothesis -> smallest safe action -> recovery proof -> follow-up**.
- Experience question: separate what you personally built and observed from what you have only studied.

## Contribution and source standards

- Use original, plain-language explanations. Do not paste books, courses, interview banks, or other repositories.
- Prefer official/primary sources for technical claims; videos are optional teaching aids, not proof.
- Use synthetic, sanitized examples. Never include client/employer confidential details, passwords, tokens, SSH keys, credentials, or production evidence.
- Built, tested, observed, simulated, and planned mean different things; label them accurately.
- Add the daily question list and topic mapping to the relevant day note, then update this bank in the same change.


## Day 2 — Docker image, runtime security, and Compose networking

**Day guide:** [Project Day 2](../docs/days/PROJECT_DAY_02.md) · [Day 2 prework and videos](../docs/days/DAY_02_PREWORK.md)

**Question-source method:** These are original questions derived from the Day 2 build and troubleshooting evidence. Public repositories are used as prompt-category inspiration, not copied answer text: [DevOps cloud interview scenarios](https://github.com/Techikrish/devops-cloud-interview-scenarios) and [DevOps interview handbook](https://github.com/hammadhaqqani/devops-interview-handbook). Validate technical answers with the official Docker references linked in each entry and with this lab's outputs.

### D02-INT-001 — Explain why the database address changed inside the API container

**Day 2 topics:** loopback; network namespaces; Compose network; service DNS; port publishing; host-to-container versus container-to-container traffic.

**Question:** When the API ran on the EC2 host, it reached PostgreSQL through 127.0.0.1:5433. Why does the containerized API use postgres:5432 instead? What breaks if you keep 127.0.0.1 in the container?

**Answer outline:** 127.0.0.1 always means the current network namespace. On the host it means the EC2 host, where Compose published host port 5433 to the database's container port 5432. Inside the API container it means the API container itself, where there is no PostgreSQL listener. Compose attaches both services to a project network and provides DNS for the postgres service name, so the API uses postgres:5432 directly. The API's published 127.0.0.1:8081:8081 mapping lets a client on the host reach the API while keeping that port off external interfaces.

**Evidence:** The container environment reported JDBC URL jdbc:postgresql://postgres:5432/claims; host curl to 127.0.0.1:8081 returned health and API responses; SQL matched the API-created UUID.

**Follow-ups:** What is a network namespace? Which address should a laptop outside EC2 use? Why bind to 127.0.0.1 in this lab? What changes in Kubernetes service discovery?

**Sources:** [Docker Compose networking](https://docs.docker.com/compose/how-tos/networking/) and the scenario-question categories in the [DevOps cloud scenarios repository](https://github.com/Techikrish/devops-cloud-interview-scenarios).

### D02-INT-002 — Defend the multi-stage Dockerfile and non-root runtime

**Day 2 topics:** build stage; runtime stage; JRE versus JDK; image size and attack surface; UID/GID; process privileges.

**Question:** Walk through this service's multi-stage Dockerfile. Why build with Maven and Java 21 in one stage, copy only the JAR into a Java 21 runtime stage, and run as UID 10001?

**Answer outline:** The build stage has Maven and the JDK needed to resolve dependencies, compile, and package the application. The runtime stage starts from a smaller JRE base and receives only the packaged JAR, so Maven and source files are absent at runtime. Running as a fixed non-root UID limits the process's privileges if the application is compromised. It is a security improvement, not a complete sandbox; image patching, read-only filesystems, resource limits, secrets handling, and vulnerability scanning still matter. The Dockerfile's EXPOSE is image metadata/documentation; Compose publishes the actual host port.

**Evidence:** Image inspect showed user 10001:10001 and exposed metadata for 8081; running id inside the image returned uid=10001 gid=10001; the container served health and API requests.

**Follow-ups:** Why might the build stage need a JDK while runtime needs only a JRE? What files can a non-root app write? How would you pin image digests and scan the image? Is EXPOSE equivalent to publishing a port?

**Sources:** [Docker multi-stage builds](https://docs.docker.com/build/building/multi-stage/) and [Docker build best practices](https://docs.docker.com/build/building/best-practices/).

### D02-INT-003 — Explain the build context and .dockerignore

**Day 2 topics:** Docker build context; COPY; cache layers; secret exclusion; generated outputs; reproducible image inputs.

**Question:** What does Docker send as a build context, why does .dockerignore matter, and what risks arise if .env, Git history, or target output is included?

**Answer outline:** The build context is the set of files available to the builder for instructions such as COPY. A narrow context reduces transfer and accidental inclusion. .dockerignore excludes irrelevant or sensitive local files such as .env, .git, target, and logs. Excluding secrets prevents those files from becoming available to build steps or accidentally entering an image layer; excluding target helps ensure the image is built from the intended source rather than a stale local artifact. The exact exclusions should match the Dockerfile and build process.

**Evidence:** The checked-in .dockerignore excludes .git, .env patterns, target, and logs; the image built from pom.xml and src in the multi-stage Dockerfile.

**Follow-ups:** Does .dockerignore protect secrets if a Dockerfile explicitly downloads them? Why copy pom.xml before source? What can make a cached layer stale? How would BuildKit secrets differ from ARG or ENV?

**Sources:** [Docker build context](https://docs.docker.com/build/concepts/context/) and [Docker cache optimization](https://docs.docker.com/build/cache/optimize/).

### D02-INT-004 — Explain Compose DNS, ports, and startup dependency health

**Day 2 topics:** Compose service discovery; internal versus published ports; health checks; depends_on; readiness versus process start.

**Question:** In this Compose stack, explain postgres:5432, 127.0.0.1:5433:5432, and depends_on with service_healthy. What does each prove or enable?

**Answer outline:** Within the Compose network, the service name postgres resolves to the database container, which listens on 5432. The host mapping publishes host loopback port 5433 to container port 5432 for host-side tools. The health check runs pg_isready; depends_on with service_healthy delays starting the API until Compose sees that database health check pass. It does not prove the API can authenticate, that schema operations succeed, or that end-to-end requests work. Those need separate checks.

**Evidence:** Compose showed PostgreSQL healthy; pg_isready accepted connections; API health and claim create/read succeeded; the matching row was queried from PostgreSQL.

**Follow-ups:** What if the database becomes unhealthy after the API starts? Does depends_on restart dependents? What check proves credentials and schema work? Why not publish PostgreSQL on all interfaces?

**Sources:** [Compose startup order](https://docs.docker.com/compose/how-tos/startup-order/) and [Compose networking](https://docs.docker.com/compose/how-tos/networking/).

### D02-INT-005 — Triage an unhealthy API container while the database is healthy

**Day 2 topics:** incident triage; container status; app logs; config; service DNS; dependency health; controlled recovery.

**Question:** Compose shows PostgreSQL healthy, but the API health check or client request fails. What is your evidence-first sequence before you edit configuration?

**Answer outline:** Confirm the exact symptom, time, expected result, and whether host-local or external access is failing. Inspect docker compose ps and health details; read API logs; verify the API process/listener in its container; inspect non-secret DB_URL and confirm the intended port/hostname; check that postgres resolves from the app's network and that DB readiness/authentication are healthy. Form one hypothesis and run the narrowest read-only check. Change one cause only, then repeat the same health/API request and verify persistence if the incident involved writes. In this lab, both services were healthy and an end-to-end POST/GET/SQL check passed; do not claim an API-down Compose drill unless it is separately performed and recorded.

**Evidence:** docker compose ps, bounded logs, container configuration with secrets omitted, health response, representative API result, and SQL row.

**Follow-ups:** How does connection refused differ from timeout? What if the container is healthy but a request returns 500? How do you avoid leaking DB_PASSWORD while inspecting config? When would you roll back instead of continuing diagnosis?

**Sources:** [Google SRE incident management](https://sre.google/sre-book/managing-incidents/) and the [SRE interview repository's troubleshooting/operations prompt categories](https://github.com/michaelkkehoe/sre-interview).

### D02-INT-006 — Diagnose the Compose buildx compatibility error

**Day 2 topics:** CLI plugins; Docker Compose build path; version compatibility; pinned artifacts; checksum verification; safe rebuild.

**Question:** Compose returned “compose build requires buildx 0.17.0 or later” while the host had Buildx 0.12.1. How did you investigate and resolve it, and what evidence would you report?

**Answer outline:** The error identifies a build-tool prerequisite, not an application compilation failure. We checked Docker Compose and Buildx versions and confirmed the installed Buildx was below the minimum in the error. The package repository had no buildx package, so the lab installed a pinned official Buildx release with architecture selection and SHA-256 verification, then reran the Compose image build. The final Compose build completed and both services became healthy. In production, prefer vendor-managed package updates and change review; a manually installed CLI plugin needs ownership and patching.

**Evidence:** Initial Buildx 0.12.1; verified plugin install reported v0.17.1; subsequent docker compose up -d --build completed and Compose showed API and database healthy.

**Follow-ups:** Why verify the checksum? How do CPU architecture and release asset names affect bootstrap scripts? What would you do if the checksum mismatched? How do you distinguish builder failure from Java compilation failure?

**Sources:** [Docker Buildx installation](https://docs.docker.com/build/install-buildx/) and the [Buildx releases](https://github.com/docker/buildx/releases).

### D02-INT-007 — Separate a mistyped Compose command from a service failure

**Day 2 topics:** command-line troubleshooting; Compose service names; observable state; avoiding unnecessary changes.

**Question:** A command using docker compose exec postgress fails with “service is not running,” but docker compose ps lists postgres as healthy. Is PostgreSQL down? What do you do?

**Answer outline:** Not based on that evidence. Compare the requested service name with the Compose service list and current state. The command used postgress, but the defined service is postgres. Retry the read-only readiness check using the exact service name. Do not recreate the database or modify the Compose file to fix a spelling mistake. Report it as an operator command typo and show the successful corrected check.

**Evidence:** docker compose ps listed postgres as healthy; the misspelled service command failed; docker compose exec postgres pg_isready returned accepting connections.

**Follow-ups:** How do you distinguish CLI syntax errors, missing services, exited containers, and a running but unready database? What output from docker compose config --services helps?

**Source:** Original question based on the observed lab command error; the [DevOps scenarios repository](https://github.com/Techikrish/devops-cloud-interview-scenarios) is used only for scenario-style practice inspiration.

## Day 2 practical and logical follow-up drills

These are interview tasks to perform during refresh, not claims already completed unless the Day 2 report says so:

1. From a blank file, write a two-stage Dockerfile with a non-root runtime; explain each instruction.
2. From a blank file, write the two-service Compose relationship, including DB environment, internal URL, ports, persistent volume, health check, and service dependency.
3. Predict and explain the result of changing DB_URL from postgres:5432 to 127.0.0.1:5433 inside the API container.
4. Diagnose these separately: image build cannot start; API container exits; database is healthy but credentials fail; host curl cannot reach the published port; API returns 500 after connecting.
5. Give a concise incident update: impact, evidence, current hypothesis, safe next step, recovery signal, and owner/follow-up.

Mark each drill Can explain, Needs another attempt, or Not practiced after attempting it. The recorded Buildx/tool-version issue and service-name typo are real lab observations, but they are not production incidents.
