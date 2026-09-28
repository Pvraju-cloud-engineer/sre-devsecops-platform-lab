# Project Day 1: Service Baseline, Access, and First-Response Habits

## Day 1 purpose

Start the platform from a small, observable vertical slice. Learn who can access the host, what runs there, how one request reaches durable data, and how to distinguish symptoms across the Linux, application, container, and database layers. This claims API is the first workload in a larger multi-service, multi-tenant SRE/DevSecOps lab; it is not the final platform.

Day 1 is complete only when the observed baseline is reproducible from this personal repository, the learner can explain it without notes, and the evidence and limits are documented. Previously observed actions are marked as completed below. Actions not yet proved remain open.

## Manager's Day 1 assignment

Ticket: Establish and document a safe lab service baseline for a claim-submission API backed by PostgreSQL.

User impact: A request must be accepted, stored, and retrievable. Operators need a reliable way to check service and dependency health.

Scope: One personal lab EC2 host; one Spring Boot process; one PostgreSQL container and named volume; loopback-only database port; synthetic data. No production account, customer data, public database listener, or extra always-on cloud service.

Acceptance criteria:

1. Identify the active Linux account, working directory, app process, listeners, and Docker access.
2. Confirm PostgreSQL readiness and the Spring health endpoint independently.
3. Create a record through HTTP, retrieve it through HTTP, and confirm the row in PostgreSQL.
4. Restart Spring Boot and prove the API can retrieve the same record.
5. Explain which layer each check exercises and what it does not prove.
6. Record symptoms, commands, evidence, diagnosis, fix, and verification for every issue observed.
7. Preserve the exact observed architecture and working steps in a sanitized, rebuildable repository. Keep credentials out of Git.
8. On the next session, reconstruct the flow from memory before consulting this file; use the note afterward to check gaps.

Definition of done: The system is healthy, the evidence is saved, the runbook is usable by another learner, and temporary resources have an explicit cost and cleanup owner.

## Learning before the assignment

Watch these before running the lab. Take notes in your own words; pause at the request/database path and health-check sections.

- Linux operating-system crash course, freeCodeCamp: https://www.youtube.com/watch?v=ROjZy1WbCIA. Focus on users, files, permissions, processes, and command-line diagnosis.
- Spring Boot REST API with PostgreSQL and Maven: https://www.youtube.com/watch?v=G6C0zGRNk7s. Focus on HTTP request, controller/service/repository, persistence, and status codes.
- Docker in 100 Seconds: https://www.youtube.com/watch?v=Gjnup-PuquQ. Intro only; then read Docker volumes: https://docs.docker.com/engine/storage/volumes/.
- SRE on-call principles: https://sre.google/sre-book/being-on-call/.
- Incident response and disciplined roles: https://sre.google/sre-book/managing-incidents/.
- Monitoring as a diagnostic tool: https://sre.google/workbook/monitoring/.

Learning questions to answer before the terminal work:

- What does SSH key authentication establish? Does it grant root automatically?
- What is the difference between a process being running, a port listening, and an HTTP health check succeeding?
- Why can PostgreSQL be healthy while the API returns connection refused?
- What survives container replacement: the image, writable layer, or named volume?
- What evidence would you gather before changing a service during an incident?

## Day 1: what an SRE is doing

In a service team, a first-day SRE normally gets access through approved onboarding, learns service ownership and escalation paths, reads the architecture and runbooks, inspects dashboards and recent changes, shadows an experienced on-call engineer, and learns change and incident procedures. The exact sequence varies by organization. A new engineer does not make unreviewed production changes before understanding ownership, impact, and rollback.

In this lab, we simulate that onboarding by establishing an inventory, mapping one request, observing health and persistence, and writing a first-response runbook. We do not claim the lab is production or that lab tests establish a production SLO.

## Starting state and architecture

Host: Amazon Linux 2023 EC2, accessed from Windows with PuTTY and a key pair. The interactive account used for the app was ec2-user. The app directory is /home/ec2-user/usaa-sre-lab/app.

Components:

- Spring Boot application launched with the Maven Wrapper from the app directory; HTTP port 8080.
- Spring Actuator health endpoint at /actuator/health.
- PostgreSQL 16 Alpine container named claims-db; database and user named claims.
- Docker named volume claims-db-data mounted at /var/lib/postgresql/data.
- PostgreSQL host port published on 127.0.0.1:5432, so it was not exposed as a public EC2 listener.
- Claims API supports creating and retrieving a claim. The observed record had a generated UUID, description, SUBMITTED status, and creation timestamp.

Request path:

PuTTY shell or local process -> HTTP request to 127.0.0.1:8080 -> Spring Boot HTTP/controller layer -> application/service and persistence code -> PostgreSQL at 127.0.0.1:5432 -> PostgreSQL data files on claims-db-data -> JSON response.

Operational dependency path:

PuTTY SSH session -> Linux identity and file permissions -> Java process and TCP listener -> Docker daemon -> PostgreSQL container readiness -> database credentials/schema -> application request.

The application process and database container are separate failure domains. A healthy database does not mean the Java process is listening. A successful health response does not by itself prove claim creation, persistence, authorization, backup, restore, or end-user availability.

## End-to-end work performed and evidence

These items were reported from the user's terminal session. They are actual observed lab actions, not hypothetical production incidents.

1. Connected to EC2 through PuTTY using the key pair and logged in as ec2-user.
2. Inspected the app directory and confirmed Maven Wrapper, pom.xml, src/, and target/ existed.
3. Checked Docker Engine. Docker Compose was not available as a subcommand, so the first database launch used docker run.
4. Added ec2-user to the docker group, reconnected, and verified id showed docker membership and docker ps worked. Docker group membership is effectively root-level access; it is a lab convenience and must be treated as privileged access.
5. Started PostgreSQL 16 Alpine as claims-db with a named volume, restart policy, and host port bound only to loopback. The password used in the initial lab command is not a repository value; replace it with a local ignored env file or secret manager before making the stack reproducible.
6. Read container logs and ran pg_isready. The final state said PostgreSQL was accepting connections. Initialization logs included a temporary-server shutdown; readiness was determined from the final startup and readiness check, not one shutdown line.
7. Started the Spring Boot process with the Maven Wrapper in the app directory.
8. Requested /actuator/health on 127.0.0.1:8080 and received JSON health output.
9. Sent POST /claims with a synthetic description. The service returned HTTP 201 and a generated record with SUBMITTED status.
10. Sent GET /claims/{id} and received the same record.
11. Queried PostgreSQL with psql and confirmed the row and created_at value.
12. Restarted the Spring Boot application, queried the record again, and confirmed it was still available. This proves the app reconnected to the still-running database. It does not prove PostgreSQL container restart, host reboot, backup, or restore behavior.

## Commands and why they matter

Run one command at a time from the EC2 shell. Use a plain URL in curl, not Markdown link syntax.

    whoami
    pwd
    id
    ls -la ~/usaa-sre-lab/app

Identity and path checks explain home-directory and permission problems before changing ownership.

    docker --version
    docker compose version
    docker ps --filter name=claims-db
    docker logs --tail 30 claims-db
    docker exec claims-db pg_isready -U claims -d claims

These inspect the engine, optional Compose component, container state/logs, and PostgreSQL readiness. Container state alone is not a readiness check.

    ps -ef | grep '[j]ava'
    ss -lntp | grep -E ':8080|:5432'
    curl -i http://127.0.0.1:8080/actuator/health

These test the Java process, TCP listeners, and application health response. If a command returns no match, record that as evidence rather than repeatedly retrying.

    curl -i -X POST http://127.0.0.1:8080/claims -H 'Content-Type: application/json' -d '{"description":"synthetic lab claim"}'
    curl -i http://127.0.0.1:8080/claims/REPLACE_WITH_RETURNED_ID
    docker exec claims-db psql -U claims -d claims -c "SELECT id, description, status, created_at FROM claims;"

The POST response supplies the identifier for GET and SQL verification. Do not paste angle-bracket placeholders or Markdown links as literal URLs.

    docker inspect claims-db --format '{{json .Mounts}}'
    docker volume inspect claims-db-data

These verify that the named volume is attached. Inspecting a volume does not verify backups or successful restoration.

## Day 1 debugging record: actual observed issues

### App directory not found

Symptom: /home/ec2-user/usaa-sre-lab/app was absent when checked as ec2-user.

Evidence and cause: The app tree had initially been created under root's home. Tilde and relative paths resolve for the current user, so root and ec2-user have different homes.

Resolution: The directory was moved under /home/ec2-user/usaa-sre-lab and ownership was changed to ec2-user. Then the app path was listed successfully.

SRE lesson: Confirm whoami, pwd, id, and absolute paths before moving or chowning files. Use the least-privileged account for application work.

### su requested a password

Symptom: su ec2-user requested a password.

Evidence and cause: PuTTY public-key authentication and Linux su authentication are separate. su asks for the target account's password; SSH key login does not provide it.

Resolution: A fresh PuTTY login as ec2-user showed the expected identity and Docker group. Do not invent a password or weaken SSH access to work around this.

### Docker Compose command was missing

Symptom: docker --version worked; docker compose version said compose was not a Docker command.

Evidence and cause: The Docker Engine was installed, but the Compose plugin was not available in this environment.

Resolution: The initial PostgreSQL lab used docker run. Before a Compose-based rebuild, verify plugin installation from a trusted package source and document the installed version.

### Docker permission changed only after reconnect

Symptom: docker group membership was added, but the current login did not immediately have the new group.

Evidence and cause: Group membership is established at login/session creation.

Resolution: The user reconnected; id then showed docker membership and docker ps worked.

Security note: Membership in the docker group provides highly privileged host control. Keep it restricted to this personal lab account.

### First curl was connection refused

Symptom: curl to 127.0.0.1:8080 failed immediately; a later POST succeeded.

Evidence and cause: At the first attempt no process accepted connections on port 8080. PostgreSQL readiness only proved the database was ready; it did not prove Spring Boot was running.

Resolution: The app was running in another terminal for the successful request. A disciplined check is process -> listener -> application log -> health -> dependency -> user request.

### Markdown URL caused a shell syntax error

Symptom: shell reported a syntax error near an opening parenthesis.

Evidence and cause: Text copied in Markdown-link form included brackets and parentheses, which are not part of the URL and have shell meaning.

Resolution: Use only the plain URL in curl. Quote JSON and use Bash line continuation only when needed.

### PostgreSQL logs showed a shutdown during initialization

Symptom: logs contained a fast shutdown message during the first startup.

Evidence and cause: The official image initializes a fresh data directory using a temporary server, stops it, then starts the normal server.

Resolution: Check final logs and pg_isready. The final output said the database accepted connections.

## Practice scenarios: sourced, not observed on this lab day

These are next-step drills based on standard SRE incident-response and monitoring guidance. They did not happen in the recorded lab session. Reproduce them only in the disposable lab and record actual output before claiming a result.

- API process stopped while PostgreSQL remains healthy: predict the symptom, verify process/listener, restore the app, then prove health and GET recovery.
- Wrong application database password: identify the first failing layer from application logs, avoid printing secrets, correct local config, and prove a claim can be read.
- PostgreSQL unavailable: compare container state, final logs, pg_isready, and API symptoms. Recover without removing the named volume.
- Container replaced but named volume retained: verify the existing synthetic row after PostgreSQL restart/recreation. Do not run docker volume rm as a troubleshooting shortcut.
- Disk pressure or full filesystem: learn read-only inspection first with df -h and df -i; do not delete database files. Later perform a bounded test in a disposable environment.
- Bad release: identify the change, compare health and user-journey checks, roll back to a known version, and verify recovery before closing the event.

The response pattern is: establish impact and scope; record time and recent change; inspect metrics/logs/process/network/dependency evidence; state facts separately from hypotheses; choose the lowest-risk mitigation; verify service and data recovery; record follow-up work. Google's incident-management material describes why explicit roles, communication, and practiced procedures matter: https://sre.google/sre-book/managing-incidents/.

## What Day 1 teaches and what remains unproven

You should know how PuTTY SSH identity differs from sudo/su, how Linux user homes and groups affect access, how to inspect a process and listener, how HTTP status and health endpoints differ, how a container differs from its volume, how to check PostgreSQL readiness, and how to trace one API record into SQL.

Observed: API health response, HTTP 201 create, GET response, SQL row, and record read after application restart.

Not yet evidenced: clean clone/rebuild from GitHub, checked-in sanitized Java source and tests, Compose-based startup, PostgreSQL restart with the volume, host reboot recovery, backup/restore, external secret management, CI, dashboards, SLOs, multi-tenancy, or production availability. These are future assignments, not Day 1 results.

## Day 1 report: learner fills in after hands-on work

Date/session:

Ticket and impact:

Architecture and request path drawn from memory:

Commands run and important output:

Observed symptom:

Facts collected:

Hypothesis and test:

Root cause supported by evidence:

Mitigation and recovery proof:

What remains unknown:

Cost/security review and cleanup status:

One runbook improvement:

## Interview practice and memory drill

Answer aloud without reading first. Then compare with the note and improve your own wording.

Recall from the terminal work:

1. Why did the first curl fail, and what evidence would distinguish app-down from database-down?
2. Why did su ask for a password after PuTTY key authentication?
3. What changed after reconnecting for docker group membership?
4. What does pg_isready prove? What does it not prove?
5. What did the application restart check demonstrate, and what recovery test remains?

Fundamentals:

6. Explain HTTP 201 versus a health response.
7. Explain container writable layer versus a named volume.
8. Why bind PostgreSQL to loopback in this single-host exercise?

Troubleshooting:

9. The API returns connection refused but PostgreSQL is ready. Walk through diagnosis and recovery.
10. The container is Up but API calls time out. What checks would you run and in what order?

Design and ownership:

11. What evidence would you require before calling this baseline production-ready?
12. How would you hand off this service to the next on-call engineer?

Use this answer structure: impact -> request/data path -> facts -> hypothesis/test -> safest mitigation -> recovery evidence -> follow-up. Avoid claiming an unperformed test.

## No-notes rebuild at the next session

Before opening this guide, draw the two flow paths from memory, list the ports and components, and write the diagnostic order for a failed request. Then start from a clean clone of this personal repository and rebuild the app/database using only the files checked in here. Record every point where instructions or files are missing. After the attempt, consult this note, fix the gaps, and repeat until you can complete the flow without it.

Current blocker to that rebuild: the app source and automated tests were not yet in this repository when this note was expanded. The Day 1 closeout must add sanitized source/tests and reproducible local configuration before the clean-clone criterion can pass.

## Cleanup and cost ownership

Record the EC2 instance state, region, instance type, attached storage, public-IP/security-group exposure, running processes, containers, and Docker volume before ending the session. Stop or terminate only the lab resources you intentionally own and verify the AWS console state afterward. Stopping an instance does not necessarily stop storage or other billable resources. Never delete claims-db-data unless intentionally resetting disposable synthetic lab data and confirming it is no longer needed.

## Day 1 status and Day 2 handoff

Completed from reported evidence: EC2 login as ec2-user; Linux identity and Docker access; PostgreSQL ready; Spring Actuator health returned JSON; synthetic claim created and fetched; database row verified; record read after restarting Spring Boot; actual command and account issues documented.

Open Day 1 closeout: put sanitized app code and tests in the personal repo; add reproducible database/app setup without committed secrets; perform a clean-clone rebuild; separately restart PostgreSQL with the same volume and verify the test row; record teardown evidence.

Day 2 begins with a 10-minute no-notes recall and rebuild attempt. Then study pom.xml, Maven lifecycle, src/main, src/test, Spring configuration, and controller/service/repository boundaries. Improve the build and automated tests based on the gaps Day 1 exposed. Do not advance the platform until the baseline rebuild is repeatable.


## Repository source of truth and daily rebuild workflow

The personal GitHub repository is the canonical, versioned source for the lab. As the platform grows, keep the files needed to recreate each layer here: Spring Boot application source and tests, Maven configuration, Dockerfiles, Compose files, environment examples, shell scripts, Terraform modules, Kubernetes/Helm manifests, CI/CD workflows, dashboards, alerts, runbooks, and the daily learning and incident records. A lab step is not reproducible if its only instructions or code live on an EC2 home directory, in a terminal scrollback, or in an untracked local file.

### Start every work session

1. Connect to the personal lab repository and clone it on a fresh machine, or pull the latest approved changes into the existing personal lab checkout. Never run these Git commands from the Cisco work repository.
2. Check `git status`, the current branch, latest commit, and repository tree. Confirm there are no unexpected local changes before rebuilding.
3. Read yesterday's `docs/days/PROJECT_DAY_NN.md`, but first attempt the recall task without looking: draw the architecture, name the request path, list the key commands, explain what each check proves, and describe one failure and its diagnostic evidence. Then compare with the notes and correct gaps.
4. Follow only version-controlled scripts and configuration to recreate the previous day's baseline. Use clearly named sample configuration such as `.env.example`; supply actual credentials locally through ignored environment files or a secrets manager.
5. Run the baseline smoke checks and capture relevant, sanitized outputs in today's report. Record the commit SHA and environment details so the result can be tied to the code that produced it.
6. Work today's manager-assigned ticket as a small increment. Diagnose failures layer by layer, preserve the evidence, write the fix and the reason for it, then update the runbook, architecture, interview questions, and next-day handoff.
7. Review the change with `git diff`, commit it on the personal project branch, and push to this same personal GitHub repository. Do not put credentials, tokens, private customer information, or raw sensitive logs in a public repo.
8. Decommission lab resources when the assignment is finished; record what was stopped or deleted, what persistent data was intentionally kept, and any expected cost.

### What belongs in Git, and what does not

Commit reproducible definitions and sanitized evidence. Do not commit real secrets, SSH private keys, `.env` files, cloud credentials, Terraform state, database volume contents, generated binaries, temporary logs, or real customer data. Use `.gitignore`, least-privilege IAM, and a future secure Terraform state backend for those items. Keep synthetic lab data disposable and recreate it from a documented seed or migration when the assignment requires data. Git stores the recipe and code; it does not preserve the running EC2 machine, a Docker volume, or a live database.

A clean clone is the reproducibility test: it should be possible to reconstruct the prior day's intended environment from committed code and documented prerequisites, then continue with the new increment. If the clone cannot do this, record the missing dependency as a defect in the lab rather than relying on memory or undocumented manual steps.

### Day 1 source-code closeout ticket

The EC2 host currently has the working Spring Boot application and PostgreSQL container, but the application source has not yet been committed to this repository. Therefore the service is not yet reproducible from a clean clone. The next coding increment is to add the sanitized Spring Boot source and tests, `pom.xml` and Maven wrapper, database configuration, Compose definition, and `.env.example` here; then prove the same service can be built and started from a fresh clone. Keep credentials out of the files. Until that work is committed and rebuilt, describe the running EC2 service as an observed lab state, not as a repo-recreated deployment.

For each future day, create a new report such as `docs/days/PROJECT_DAY_02.md`, while retaining prior reports. The repository should let the learner start at the beginning, rebuild Day 1, then apply Day 2 and later increments in sequence.


## Fresh repository rebuild and API outage drill — 2026-09-28

This update supersedes the earlier statements in this report that the source was not yet committed, that a clean-clone rebuild remained blocked, and that an API-stopped drill was only planned. Those statements described the state before the fresh repository rebuild.

The sanitized Spring Boot source, tests, Maven Wrapper, configuration, PostgreSQL Compose definition, environment example, and bootstrap scripts are in this repository. On a fresh Amazon Linux 2023 EC2 host, the personal branch was cloned and the lab rebuilt from the checked-in files. The generated .env stayed local and ignored. The current repo rebuild runs Spring Boot on host port 8081 and Compose PostgreSQL as service postgres, with host port 5433 forwarded to container port 5432. The original one-off setup described earlier used different ports and a different container name; use the current repo files for a rebuild.

Observed rebuild evidence: Compose reported PostgreSQL healthy; pg_isready accepted connections; Maven tests completed with 2 tests and no failures or errors; the API health endpoint returned HTTP 200 with status UP; a synthetic claim was created with HTTP 201, retrieved with HTTP 200, and confirmed in a PostgreSQL query. After restarting the API process while leaving PostgreSQL running, the same claim remained retrievable. This does not prove database restart, EC2 reboot, backup/restore, or high availability.

Observed controlled outage drill: Spring Boot was stopped. The health curl failed to connect; docker compose ps still showed PostgreSQL healthy; ps showed no Java process; ss showed no listener on port 8081. The evidence identified a stopped API process. Spring Boot was restarted and the health endpoint returned HTTP 200/UP. This was a lab drill, not a production incident.

Still not demonstrated: PostgreSQL container restart while retaining the volume, host reboot recovery, backup/restore, external secret management, CI/CD, dashboards, SLOs, multi-tenancy, production availability, and cloud disaster recovery. Do not mark those topics complete until tested and documented.

See docs/days/PROJECT_DAY_01_LEARNING_GUIDE.md for beginner definitions, file ownership, tool choices, command explanations, request flow, troubleshooting approach, interview practice, and the recall checklist.
