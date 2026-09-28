# Day 2 Prework: Containerize the Existing Spring Service

## Source-of-truth rule

The application source and tests already live in `services/claims-api/` in this personal repository. Keep them as the canonical project source. Day 2 builds on those files; do not recreate, replace, or move the app into a separate sample project. Make focused changes, review the diff, and preserve the working Day 1 behavior.

## Mission

**LAB-003 — Build a Docker image for the existing API and run it with PostgreSQL through Docker Compose.**

Learn how the code becomes a JAR, how a Dockerfile packages it, how Compose starts the API and database, and how the API finds PostgreSQL over the Compose network. Day 2 does not introduce Kubernetes, queues, Terraform, or a large AWS footprint.

## Before the session

1. Review `docs/days/PROJECT_DAY_01.md` from memory. Draw the Day 1 request path and explain what each health/API/SQL check proved.
2. Check the existing source tree, `pom.xml`, Maven Wrapper, `application.properties`, `.env.example`, `compose.yaml`, and `.gitignore` before changing anything.
3. Watch [Dockerize a Spring Boot app with a multi-stage build](https://www.youtube.com/watch?v=gV3_y-DaNr8). Focus on build stage versus runtime stage and why build tools should not be in the final runtime image.
4. Read Docker’s [Java guide](https://docs.docker.com/guides/java/), [multi-stage builds](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/), and [Compose networking](https://docs.docker.com/compose/how-tos/networking/). Be ready to explain why the API container connects to `postgres:5432`, while a host command can use `127.0.0.1:5433`.
5. Before starting or creating EC2 resources, inspect the AWS account, region, instance state, and current costs. Use only the personal learning repository and synthetic data.

## Day 2 learning topics

- Dockerfile instructions: `FROM`, `WORKDIR`, `COPY`, `RUN`, `USER`, `EXPOSE`, and `ENTRYPOINT`.
- Build context and `.dockerignore`; keep `.env`, `.git`, local build output, and credentials out of the image context.
- Multi-stage build: use Maven/JDK to test and package, then copy only the executable JAR into a Java runtime image.
- Image versus container; tag, layers, cache, logs, exit status, and restart behavior.
- Compose service DNS and port mapping: host `127.0.0.1:5433` to PostgreSQL container `5432`; API container uses `postgres:5432`.
- Runtime configuration and secret boundaries; never bake database credentials into a Dockerfile or image.
- Run the application as a non-root user, then inspect the container’s user and process.
- Health and dependency readiness: PostgreSQL health is separate from API health.

## Files to maintain or change

- Preserve all Java source, tests, Maven files, and current API behavior under `services/claims-api/`.
- Add a `services/claims-api/Dockerfile` with separate build and runtime stages.
- Add a `services/claims-api/.dockerignore` excluding `.env`, `.git`, `target`, logs, and local evidence.
- Update `services/claims-api/compose.yaml` to build and run the API alongside PostgreSQL.
- Update `.env.example` only if runtime settings need to reflect the API’s container network address. Keep real `.env` ignored and untracked.
- Record the assignment, commands, actual evidence, debugging, and interview answers in one file: `docs/days/PROJECT_DAY_02.md`.

## Acceptance criteria

- Existing source and tests remain in place; the diff shows only intentional changes.
- Maven tests pass and their result is recorded.
- Docker builds a tagged API image from this repository.
- The runtime image contains the JAR and Java runtime, not the Maven/JDK build toolchain or secrets.
- API and PostgreSQL start under Compose; API can resolve PostgreSQL by service name.
- Actuator health returns `UP`; synthetic `POST` and `GET` succeed; SQL shows the same record.
- `docker compose ps` and logs are used to explain container state; API and DB health are checked independently.
- A controlled API-container stop/restart is diagnosed and recovery is verified.
- The report records exact commands/results and clearly labels unperformed scenarios.
- Do not publish to Docker Hub on Day 2; first inspect and understand the local image.

## Day 2 failure drills

1. Stop the API container. Record the failed health check, Compose state, and logs. Restart it and verify recovery.
2. In a disposable lab change only the API DB hostname to `localhost`, rebuild/recreate, observe the connection failure, restore `postgres`, and prove recovery. Explain why `localhost` means different things on the EC2 host and inside a container.
3. Inspect a build failure or missing JAR path from the first error and the image build stage; do not guess at fixes.
4. Confirm the database volume remains present during ordinary container recreation. Never use `docker compose down -v` during this exercise.

## Manager handoff

- Ticket and acceptance criteria:
- Files inspected and changed:
- Commands, exit codes, and evidence:
- API and database health evidence:
- Failure reproduced, evidence, cause, and recovery:
- Secret, image, and AWS cost checks:
- Rollback steps:
- What I can explain from memory:
- Interview answer in 60 seconds:
- Open questions for Day 3:
