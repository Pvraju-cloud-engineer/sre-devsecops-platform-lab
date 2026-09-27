# Day 2 Prework: Understand and Rebuild the Spring Service

## Mission

Turn the Day 1 working baseline into something you can explain and recreate. Do not add Kubernetes, queues, or a large AWS footprint yet. First understand the source, configuration, build, persistence path, and observed failure modes.

## Before the session

1. Read docs/days/PROJECT_DAY_01.md without looking at the answers; explain the request path aloud.
2. Recall what each command proved: id, docker ps, pg_isready, curl health, POST, GET, and SQL query.
3. Watch or review one beginner Spring Boot REST/Maven lesson and one Docker volumes/networking lesson. Write the video title, link, timestamp, and two questions you still have before starting the build.
4. In PuTTY, connect as the unprivileged EC2 user and check whoami, pwd, id, free -h, df -h, java -version, and docker version. Do not start a new AWS resource until cost and current-instance state are checked.

## Assignment

Inspect and explain the existing Spring Boot project tree, then reproduce a clean build. The source files are not yet in this repository; retrieve them from the personal lab instance or regenerate them during the session and commit sanitized source here. Do not copy passwords, private IPs, account IDs, key material, or personal data.

## Acceptance criteria

- Explain the purpose of pom.xml, Maven Wrapper files, src/main/java, src/main/resources, src/test/java, and target/.
- Identify which controller, service, repository, entity/model, configuration, and migration files implement the HTTP-to-database flow. If layers are combined, record that accurately before refactoring.
- Run the Maven Wrapper build from the project root and record exact exit status and relevant summary.
- Run automated tests and describe what each verifies; add a test for persistence and validation only after understanding the current behavior.
- Locate DB URL/user/password configuration and remove any hard-coded secret from committed source.
- Start the API, check health, create/read a record, and query PostgreSQL to confirm the same record.
- Stop/start the application and verify persistence. Separately test database container restart only if the named volume is retained and the test is planned.
- Commit application source and configuration with secrets excluded; update Day 2 notes with actual results, not assumptions.

## Study prompts

- Maven lifecycle: validate, compile, test, package, verify; what runs at each phase?
- What does Spring Boot auto-configuration do, and where does explicit configuration override it?
- How does a request move through controller, service, repository, JDBC driver, and PostgreSQL?
- What is the difference between process health, liveness, and readiness?
- Where do runtime profiles and environment variables enter Spring configuration?
- Why can the app return connection refused while PostgreSQL is healthy?
- What logs and metrics would distinguish DNS/network failure, connection refusal, authentication failure, and SQL/schema failure?
- What is the difference between a container restart, app restart, host replacement, and volume deletion?

## Debugging drill

Before changing anything, write the exact symptom and expected behavior. Capture timestamp, HTTP status/body, process state, listener, app log lines, DB readiness, and a read-only DB query. Form one hypothesis, test it with the narrowest command, record evidence, then change one thing. Verify recovery with the same signal that originally failed.

## Handoff format

- Work item and acceptance criteria:
- Files inspected/changed:
- Commands and exit codes:
- Test/health/API/DB evidence:
- Failure reproduced and diagnosis:
- Security and cost notes:
- Rollback:
- Open questions:
- Interview explanation in 60 seconds:

## Day 2 interview drill

1. Walk through a request from HTTP controller to a durable PostgreSQL row.
2. What does the Maven Wrapper pin, and why use it in CI?
3. How do you distinguish a 500 caused by application logic from a database connection problem?
4. How do environment-specific settings avoid rebuilding the same artifact per environment?
5. How would you safely rotate a database password without exposing it in Git or logs?
6. What evidence would you use to claim data survived a restart?

Answer with the system behavior, the evidence you personally observed, and what remains to be implemented. Do not claim a test passed until it has been run and recorded.
