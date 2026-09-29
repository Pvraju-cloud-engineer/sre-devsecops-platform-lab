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
