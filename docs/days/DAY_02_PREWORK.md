# Day 2 Prework: Containerize the Existing Spring Boot API

## Ticket and status

**LAB-002 — Package the existing API in a Docker image and run it with PostgreSQL through Docker Compose.**

**Status: Ready, not started.** Day 1 rebuild is recorded as complete in LAB-001. Tomorrow begins with revision and learning; implementation starts only after the learner can explain the prerequisite concepts.

## Source-of-truth rule

The application source and tests already live in `services/claims-api/` in this personal repository. Keep those files as the canonical application. Day 2 adds packaging and orchestration around the existing app; do not replace it with a second sample service or move its source. Make focused changes and preserve the Day 1 behavior and PostgreSQL volume.

## What Day 1 proved

Reported rebuild evidence showed: the repo was cloned on a fresh Amazon Linux 2023 EC2 instance; PostgreSQL ran in Compose and passed `pg_isready`; Spring Boot ran as a Java process on the EC2 host; Actuator returned HTTP 200 with `UP`; a synthetic claim was created, retrieved, and found in SQL; the claim remained retrievable after restarting only the API; and an API outage was diagnosed by checking Compose, the Java process, and port 8081 separately.

This is a single-host learning lab. It does not demonstrate production availability, an SLO, backup/restore, Kubernetes, or high availability.

## Day 1 no-notes revision — do this first

Before opening the Day 1 guide, spend 20–30 minutes explaining or writing the following from memory. Then use `docs/days/PROJECT_DAY_01.md` to check gaps.

1. Draw the request path: PuTTY/SSH key → EC2 as `ec2-user` → `curl` → Spring Boot on host port 8081 → controller and validation → repository/JPA/Hibernate → JDBC → PostgreSQL → SQL row → JSON response.
2. Explain placement: Spring Boot is currently a host Java process; Compose currently manages PostgreSQL. Therefore, `docker compose ps` does not list the Java API.
3. Explain ports: the host reaches the database at `127.0.0.1:5433`; Compose maps that host port to PostgreSQL container port 5432. The database port is bound to loopback, not exposed publicly.
4. Explain the data path and persistence: a named Docker volume stores PostgreSQL data separately from the container’s writable layer. An API restart does not restart or erase the database.
5. Point to the app, entity, controller, repository, tests, `pom.xml`, Maven Wrapper, `application.properties`, `compose.yaml`, `.env.example`, `.gitignore`, and bootstrap scripts. Say what each is for and which file must not be committed (the real `.env`).
6. Define in beginner language: process, listener/port, HTTP request, status code, JSON, image, container, volume, environment variable, health check, readiness, test, and build.
7. Retell the observed API outage using symptom → impact → evidence → diagnosis → recovery → verification. Explain why a healthy PostgreSQL container did not prove the API was up.
8. Give a 60-second interview walkthrough of one request and name what the evidence did and did not prove.

### Day 1 safe verification commands

Run only after connecting to the intended personal lab EC2. Explain each result; do not paste secrets or tokens into notes.

```bash
whoami
id
pwd
cat /etc/os-release
uname -m
free -h
df -h /
nproc
git status --short --branch

cd ~/sre-devsecops-platform-lab/services/claims-api
docker compose ps
docker compose exec postgres pg_isready -U claims -d claims
curl -i http://127.0.0.1:8081/actuator/health
ps -ef | grep '[j]ava'
ss -lntp | grep -E ':(8081|5433)\b'
```

Command meanings: `whoami` shows the effective username; `id` shows UID/GID and groups; `pwd` shows the current directory; `free`, `df`, and `nproc` show memory, filesystem capacity, and CPU count; `ps` shows processes; `ss` shows listening sockets; `curl` checks an HTTP path; Compose commands inspect the configured services; `pg_isready` checks PostgreSQL readiness. A single passing check is not proof that every layer works.

To repeat the data check, POST a synthetic claim, copy the returned ID, GET that ID, and query the same ID with `psql`. Use the existing API and database; do not use `docker compose down -v` because `-v` removes the named data volume.

## Day 2 prerequisite learning order

Watch/read these before changing the repository. Take notes in your own words and answer the check questions below.

1. **Dockerfile and image basics.** Watch Docker’s [Dockerfile Best Practices talk](https://www.youtube.com/watch?v=JofsaZ3H1qM). It is an older talk, so use it for concepts: build stages, image layers/cache, lean runtime images, and reducing unnecessary tools. Use current Docker docs for actual syntax.
2. **Multi-stage build.** Read Docker’s [multi-stage build guide](https://docs.docker.com/build/building/multi-stage/) and [Java container guide](https://docs.docker.com/guides/java/). Learn why a JDK/Maven builder can produce a JAR that is copied into a smaller Java runtime image. The runtime image should not contain the Maven toolchain or source tree unless explicitly needed.
3. **Build context and exclusions.** Learn what files Docker sends to the builder and how `.dockerignore` prevents `.env`, `.git`, `target/`, logs, and local evidence from entering the build context. Read [Docker build best practices](https://docs.docker.com/build/building/best-practices/).
4. **Compose networking.** Read [Networking in Compose](https://docs.docker.com/compose/how-tos/networking/). Services on the Compose network resolve by service name. Once the API is a container, it should connect to PostgreSQL at `postgres:5432`; `localhost` from inside the API container refers to the API container itself. Host-side tools can still use `127.0.0.1:5433` through the published host mapping.
5. **Runtime configuration.** Read Docker’s [Compose environment-variable guide](https://docs.docker.com/compose/how-tos/environment-variables/). Understand the difference between Compose interpolation and variables passed into a container. Never bake a password into a Dockerfile or image. Keep the real `.env` ignored and use the example file for safe placeholders.
6. **Runtime security and health.** Learn why the service should run as a non-root user, why database health and API health are separate, and why “container is running” alone does not prove that HTTP requests succeed.

### Learning check before building

Be able to answer without copying text:

- What does a Dockerfile describe? How is an image different from a container?
- Why use one build stage and a separate runtime stage for this Java service?
- What is the Docker build context, and why must `.env` be excluded?
- Why will `jdbc:postgresql://localhost:5433/claims` fail from an API container?
- Which address does an API container use for PostgreSQL, and which address does an EC2-host command use?
- What does a host-to-container port mapping do? Which port is used for container-to-container traffic?
- Why run as non-root? Why does PostgreSQL readiness not prove API health?
- Which checks would distinguish a failed build, an exited API container, a DB DNS/configuration problem, and an unhealthy database?

If any answer is unclear, revisit that topic before implementation. Videos support learning; they do not replace explaining and verifying the behavior yourself.

## Day 2 mission and planned repository changes

Preserve the current Java source, tests, API paths, validation, and database data. Planned changes are:

- Add `services/claims-api/Dockerfile` with a separate Maven/JDK build stage and Java runtime stage.
- Add `services/claims-api/.dockerignore` to exclude secrets and generated/local files.
- Update `services/claims-api/compose.yaml` to build and run the API alongside PostgreSQL.
- Update only necessary non-secret example settings so the API container uses the Compose DNS name `postgres` and database port `5432`.
- Record actual commands, results, troubleshooting, interview answers, rollback, and cleanup in one report: `docs/days/PROJECT_DAY_02.md`.

Do not publish to Docker Hub on Day 2. First inspect and understand the local image. Do not introduce Kubernetes, Terraform, a second database, or additional always-on AWS services in this assignment.

## Acceptance criteria

LAB-002 can be marked Done only after evidence shows:

1. Existing code/tests remain in place and the Git diff contains only intentional changes.
2. Maven tests pass, with the result recorded.
3. Docker builds a tagged image from this repository using the multi-stage Dockerfile.
4. The runtime image contains the app JAR and Java runtime, not the Maven/JDK build toolchain or secrets.
5. API and PostgreSQL start under Compose and the API reaches PostgreSQL by service name.
6. Actuator health returns UP; synthetic POST and GET work; SQL shows the matching row.
7. Container state and logs are inspected; API health and DB readiness are explained separately.
8. A controlled API-container stop/restart is diagnosed and recovery is verified.
9. The report distinguishes performed tests from scenarios not yet performed.
10. The PostgreSQL named volume is preserved; real secrets are absent from Git and image.

## Day 2 failure drills

Run each only in the disposable learning lab and record the evidence before changing anything:

1. Stop the API container. Capture Compose status and logs, diagnose the API health failure, restart it, and verify recovery with health plus a request.
2. Change only the API DB hostname to `localhost`, rebuild/recreate, observe the connection error, restore `postgres`, and prove recovery. Explain host localhost versus container localhost.
3. If the image build fails or the JAR cannot be found, start at the first build error, identify the failing stage/path, fix the cause, and rebuild.
4. Recreate containers without removing the database volume; confirm a previously written synthetic record remains. Never run `docker compose down -v` for this drill.

## Rollback, reporting, and handoff

Before editing, record the clean Git status and current service health. If the new API image fails, use Compose logs/status, restore the last known-good configuration, and verify PostgreSQL data still exists. Review the diff, keep credentials out of the report, and commit only to the personal learning repository.

At handoff, fill in:

- Ticket and acceptance criteria:
- Files inspected and changed:
- Commands, exit codes, and evidence:
- API and database health evidence:
- Failure reproduced, evidence, cause, recovery, and verification:
- Secret/image/AWS-cost checks:
- Rollback performed or available:
- What I can explain from memory:
- 60-second interview answer:
- Open questions for Day 3:

Until the build and evidence exist, keep LAB-002 marked Ready / Not started.
