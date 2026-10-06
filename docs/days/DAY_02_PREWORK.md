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


## Day 2 learning notes: containerize the existing API

### 1. Start with the Day 1 picture

On Day 1, a client sent an HTTP request to Spring Boot running as a Java process on the EC2 host. Spring Boot listened on host port 8081. It used Spring Data JPA to save or retrieve a claim in PostgreSQL. PostgreSQL ran as a Docker container. Compose published the database as host address 127.0.0.1, port 5433, while PostgreSQL itself listened on port 5432 inside its container. The named volume kept database files when the database container was replaced.

Day 2 changes where the API process runs: the API will run in its own container beside PostgreSQL. The API code, endpoint behavior, tests, and database stay the same. This is a packaging and runtime change, not a rewrite of the business application.

    Day 1: client -> EC2 port 8081 -> Java process on EC2 -> host port 5433 -> PostgreSQL container
    Day 2: client -> EC2 port 8081 -> API container -> Compose network -> PostgreSQL container port 5432

The difference matters during troubleshooting. In Day 2, the API-to-database path uses the Compose service name postgres and port 5432. Host-side tools still use 127.0.0.1:5433. From inside the API container, localhost means the API container itself; it does not mean EC2 or PostgreSQL.

### 2. Docker words in plain language

A Dockerfile is a recipe describing how to assemble an application image. A build reads that recipe and its build context to create an image. An image is a versioned, mostly read-only package containing the application and its runtime requirements. A container is a running process created from an image. Stopping and replacing a container does not have to delete database data if that data is stored in a separate volume.

Think of an image as a packaged recipe result and a container as one running copy. A container is not a full virtual machine: it shares the host kernel and uses isolation controls. This is why containers start quickly and why resource and security boundaries still need care.

A tag such as claims-api:day2 is a human-readable label for an image version. Tags can be moved, so production release systems often record the immutable image digest as well. For this lab, learn what was built and how to reproduce it before publishing it anywhere.

### 3. Why use a multi-stage Java build

A Java application needs a compiler and build tools to turn source code into a runnable JAR. The builder stage uses a JDK and Maven to compile the source, run tests, and package the JAR. The runtime stage starts from a smaller Java runtime image and copies only the JAR that it needs to launch.

This keeps Maven, source files, and compiler tools out of the final runtime image. That can reduce image size and the number of installed components that need patching. It does not make an image secure by itself: the base image still needs updates, the process should run with least privilege, secrets must stay outside the image, and the application must be scanned and configured well.

The Maven wrapper, mvnw, asks for the project-pinned Maven version, making local and CI builds more repeatable. Maven test runs configured tests; Maven package goes through earlier lifecycle phases and creates the packaged artifact. The image build must stop when a required test fails. A successful package proves the build completed; it does not prove the running service is healthy in production.

### 4. Build context and Docker ignore rules

The build context is the set of files Docker is allowed to send to the image builder. Dockerfile COPY instructions can use files inside that context, not arbitrary files elsewhere on EC2. Running docker build from the wrong directory can therefore cause missing-file errors or include unintended files.

A .dockerignore file removes unnecessary or sensitive local files from the build context, such as .git, target output, logs, local evidence, and .env. A .gitignore file instead tells Git which local files not to commit. They solve different problems: a file ignored by Git can still be sent to Docker unless .dockerignore excludes it. Never put a password or token in the Dockerfile, source, image layer, or build argument. Keep runtime secrets in local ignored configuration for this learning setup.

### 5. Compose networking and the two database addresses

Compose creates a private network for services in the Compose project. Containers on that network can find each other by service name through Docker's internal DNS. The API connects to PostgreSQL at hostname postgres, port 5432. The app should not connect to localhost:5433 from inside its container.

The Compose port mapping is written host-port:container-port. In this lab, 127.0.0.1:5433:5432 means a command running on EC2 can connect to 127.0.0.1:5433, and Docker forwards it to port 5432 in the database container. Binding to 127.0.0.1 limits that published port to the EC2 host; it is not an instruction to expose PostgreSQL publicly. The API container reaches the database over the Compose network and does not need the host-published port.

A common incident pattern is a wrong database URL. If the API logs say connection refused or unknown host, check whether the URL uses postgres:5432 from the API container, whether the Compose service is healthy, and whether the service names match the actual Compose file. Do not change security groups or open database ports to fix a container DNS mistake.

### 6. Environment configuration and secrets

Spring properties use environment-variable placeholders so the same application can run in different environments without editing Java code. For example, DB_URL can supply the JDBC address and DB_USERNAME and DB_PASSWORD can supply database credentials. Compose can read a project .env file to substitute values in the Compose YAML, but that alone does not automatically put every value inside every container. The API service must explicitly pass the required values through its environment or env_file configuration.

Configuration says how this copy of the application should connect; code says what the application does. Keep .env ignored by Git. Commit only .env.example with harmless placeholders. Confirm what is going into a commit before pushing, and never paste actual secrets into notes or logs.

### 7. Health, startup order, and least privilege

Different checks answer different questions. Compose ps shows container state. A PostgreSQL health check using pg_isready asks whether PostgreSQL is accepting connections. The Spring Actuator health endpoint asks whether the application reports healthy. A POST followed by GET checks the API behavior. A SQL query confirms the row reached the database. Passing one check does not prove all the others.

Compose can wait for a database health check before starting the API. That handles initial startup ordering, but it does not guarantee the database will never fail later. Applications need reasonable connection timeouts and recovery behavior; operators still inspect logs and dependencies during an incident.

The API container should run as a non-root user when the image supports it. Least privilege means giving a process only the permissions it needs. It reduces the damage a compromised process can cause. File ownership and permissions must still allow the runtime user to read the JAR and write only to intended locations.

### 8. Who does this work in a real team

The application team owns the service code, API contract, tests, and application settings. A platform or DevOps engineer commonly builds reusable Docker and Compose or deployment patterns, CI checks, and release automation with application-team input. An SRE focuses on reliability: health signals, failure behavior, operational procedures, incident evidence, recovery, and reducing repeated manual work. A database owner advises on database operations, access, backups, schema safety, and performance. In a small team, one person may cover several roles; ownership and review still need to be explicit.

For LAB-002, the assigned work is to package the already-existing service, make its configuration work inside Compose, verify behavior and data persistence, and document evidence. The ticket is not complete just because a Dockerfile exists. It is complete when tests, image build, health, API behavior, database persistence, and documented recovery checks pass.

### 9. Day 2 troubleshooting approach

Use a symptom, evidence, diagnosis, action, and verification sequence. Change one thing at a time and keep the database volume safe.

| Symptom | First evidence to collect | Likely area to inspect |
| --- | --- | --- |
| Image build fails | Build output and failing Dockerfile instruction | Build context, COPY path, Maven dependency, compiler or test failure |
| API container exits | docker compose ps -a and docker compose logs --tail 100 for the API service | Startup error, missing config, wrong command or unreadable JAR |
| API cannot reach database | API logs, Compose service names, DB_URL, database health | Wrong hostname or port, database not ready, credentials mismatch |
| Health works but POST fails | HTTP status and response, application logs, validation input | Request JSON, validation, route or database write failure |
| Host curl cannot reach API | Container state, published port mapping, host listener and security group | API not running, port not published, wrong host port or network rule |
| Old data disappeared | Compose volume listing and the exact stop/remove command used | Volume was removed or a different Compose project/volume was started |

Useful commands and what they tell you:

    docker compose ps -a                 # running and stopped services
    docker compose logs --tail 100 api    # recent API container output; replace api with the service name in the file
    docker compose exec postgres pg_isready -U claims -d claims  # database accepts local connections
    docker compose config --quiet         # Compose file parses and required substitutions exist
    docker image ls                       # locally available images
    docker inspect <container-name>       # container configuration and state
    curl -i http://127.0.0.1:8081/actuator/health  # application-level HTTP health check

Read command output before choosing a fix. Use the actual service name from compose.yaml; a typo such as postgress will produce a Compose service-not-found error. Do not use docker compose down -v in the persistence drill: -v removes named volumes and their data.

### 10. Day 2 recall check before building

Before implementing, explain these in your own words without reading the answers:

1. What changes between Day 1 and Day 2, and what remains the same?
2. What is the difference between a Dockerfile, image, container, and volume?
3. Why does a multi-stage build have a builder stage and a runtime stage?
4. Why does the API container use postgres:5432 while EC2 uses 127.0.0.1:5433?
5. What does localhost mean from inside the API container?
6. How are .gitignore and .dockerignore different?
7. Why does a Compose .env file not automatically configure every container?
8. What does each of pg_isready, Actuator health, an HTTP request, and a SQL query prove?
9. What evidence would you gather if the API container exits or cannot connect to PostgreSQL?
10. Why is removing the named volume different from recreating a container?

If you cannot explain one yet, reread that subsection, draw the request path, and say it aloud using the real port numbers. Watching a video or seeing a command once is exposure, not mastery. Build only after you can explain the data path and predict which check you would run for a failure.

### Day 2 learning versus Day 2 implementation

This section teaches the concepts before the change. The ticket remains Ready until the learner completes the recall check and starts the implementation. During the build, record the actual commands, outputs, failures, diagnosis, fix, verification, and cleanup in the Day 2 work report. Do not claim an implementation or test passed merely because it is described here.


## Video-first prerequisite path

Watch these before Day 2 implementation. Then close the video and explain the concept using this repo’s files. The video is a visual aid; use the official reference already linked in the repo for current behavior.

1. [Docker Compose beginner tutorial](https://www.youtube.com/watch?v=iOGEBj7Ozak) — services, port publishing, volumes, health checks, logs, and exec. Map each item to services/claims-api/compose.yaml.
2. [Docker multi-stage builds and BuildKit](https://www.youtube.com/watch?v=JofsaZ3H1qM) — build and runtime stages. It is an older walkthrough, so verify syntax with the [official multi-stage build guide](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/).
3. [Dockerfile tutorial: image layers and container basics](https://www.youtube.com/watch?v=pg19Z8LL06w) — use it to visualize image, container, layer, and build context; map those terms to Dockerfile and .dockerignore.

### Watch → connect → practice

- Before build, draw host, API container, PostgreSQL container, Compose network, named volume, and published host ports. Label which address is for host-to-container and which is for container-to-container.
- Explain the Dockerfile instructions in your own words before opening the checked-in file. Then compare your draft line by line with services/claims-api/Dockerfile.
- For each Compose command in the build session, record: what it does, when you use it, expected evidence, actual output/exit code, and what that evidence does not prove.
- Use PROJECT_DAY_02.md as the execution record. Prework prepares the concepts; the project-day file records the actual rebuild, build, tests, failure evidence, fixes, and interview answers.
