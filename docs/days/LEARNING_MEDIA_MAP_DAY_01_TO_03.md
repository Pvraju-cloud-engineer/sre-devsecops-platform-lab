# Video-First Learning Map — Days 1–3

This guide supports the main SRE/DevOps build series. Watch the listed videos before each day's rebuild, then close the video and explain the flow in your own words. Documentation is a lookup reference; video + rebuild + failure drill are the learning loop.

The Zelevate interview sprint is on its own branch. This map is for the ongoing day-by-day project. Keep real production examples separate from this practice lab.

## How to study each video

1. Watch the assigned segment once for the story, without copying commands.
2. Pause and draw the system path from memory.
3. Rewatch only parts you could not explain. Write down the question, not a transcript.
4. Rebuild the day's work from the repository on a clean or disposable environment.
5. Run the day's failure drill, gather evidence before changing anything, recover, and verify the original symptom is gone.
6. Answer the interview prompts aloud. Add your concise answer and evidence to that day's ticket/report.

A video is not complete learning until you can explain what each component does, why it exists, what can fail, which team owns the next action, and what evidence proves recovery.

---

## Day 1 — Linux, SRE operations, Spring request path, PostgreSQL

### Watch first

- [Google Cloud: SRE Fundamentals](https://www.youtube.com/watch?v=eopc_ijIfLg) — 07:06–11:03 for SRE/DevOps and error budgets; 23:57–30:55 for monitoring, capacity, performance and change; 39:55–49:55 for incident response, postmortems and toil.
- [Java Brains: Spring Boot Quick Start — the REST API we'll build](https://www.youtube.com/watch?v=KcFPBkczcFs) — introductory REST API concepts. Use it to understand resource paths, HTTP verbs and response shape; our app code and tests are the authority for what we implemented.

### Connect the video to our build

curl is the client. ClaimController accepts HTTP and validates JSON. Claim represents the record. ClaimRepository uses Spring Data JPA to store or retrieve it. Hibernate/JPA translates the operation into SQL. PostgreSQL persists the row in its named Docker volume. Actuator exposes a health signal. The API ran on the EC2 host on port 8081; PostgreSQL ran in a container with host port 5433 bound to loopback.

SRE connection: establish impact, check service/process/listener/logs/dependencies in layers, mitigate safely, verify using the failed request, record follow-up. DevOps connection: versioned build/config, repeatable Maven wrapper, automated tests, containerized dependency, documented startup/cleanup.

### Recall and interview drill

- Health curl cannot connect; PostgreSQL is healthy. What do ps, ss, app logs and a repeat curl each prove?
- What does ./mvnw test prove, and what does it not prove about a running service?
- Explain POST → controller → validation → repository → database row; then explain GET by ID.
- Why is localhost/127.0.0.1 correct when the API runs on the EC2 host? How does that change after the API joins a container network?
- What evidence distinguishes connection refused, HTTP 400, HTTP 404, HTTP 500 and a slow response?
- Give a true production incident story separately from the lab's API-stopped drill.

### Completion check

Without notes, draw the request path; explain five commands you used; reproduce the API-stopped diagnosis; state the user impact, mitigation, recovery evidence and follow-up. See [PROJECT_DAY_01.md](PROJECT_DAY_01.md).

---

## Day 2 — Write the Dockerfile yourself; connect API and database with Compose

Day 2 is not mastered just because a Dockerfile runs. You must be able to start with a blank file, build the image, explain every instruction, identify common build/runtime failures, and verify the running service. Our earlier Dockerfile was entered from supplied text; this deliberate rebuild closes that learning gap.

### Watch first

- [Docker Tutorial for Beginners](https://www.youtube.com/watch?v=b0HMimUb4f0) — focus on image vs container, build context, Dockerfile/layers, ports, volumes, Compose and multi-stage builds. Use it as the visual introduction, then apply the Java-specific practice below.
- [Spring: Getting Started with Spring Boot and Docker](https://spring.io/guides/gs/spring-boot-docker/) — short practical companion showing a Spring Boot JAR packaged in an image. This is a written guide, used after the video to compare the Java-specific build.
- [Docker: Multi-stage builds](https://docs.docker.com/get-started/docker-concepts/building-images/multi-stage-builds/) — reference after attempting the exercise; compare how the build stage differs from runtime.
- Resume bridge only: [AWS re:Invent: ECS on Fargate deep dive with Affirm](https://www.youtube.com/watch?v=Hq5cUQ1rLMM). Watch after local Docker/Compose makes sense. ECS/Fargate is a later AWS orchestration target; this local Compose lab does not mean we have deployed ECS in production.

### Build from a blank Dockerfile

Work in services/claims-api. First inspect pom.xml, src/, .dockerignore and the existing Dockerfile; write down the JAR output location and the port the app actually listens on. Then reconstruct the Dockerfile from memory in this order:

1. Choose a Maven + Java 21 builder image and name the build stage.
2. Set a predictable build working directory.
3. Copy the Maven descriptor first and resolve dependencies; explain why this makes dependency layers cacheable.
4. Copy source and run Maven package. Decide deliberately whether tests run in image build or in a prior CI stage; never confuse -DskipTests with passing tests.
5. Start a second, smaller Java 21 runtime stage.
6. Copy only the built JAR from the builder stage; do not ship Maven or source unnecessarily.
7. Run as a non-root UID/GID. Verify the actual configured user.
8. Document the intended application port and set an exec-form Java entrypoint.
9. Review .dockerignore: ensure .env, .git, target, logs and local artifacts cannot leak into the build context; ensure needed source and pom.xml are not excluded.

Before looking at the saved Dockerfile, explain FROM, stage alias, WORKDIR, COPY, RUN, COPY --from, --chown, USER, EXPOSE and ENTRYPOINT. Then compare your version with the committed one and explain each difference. Do not publish an image until its contents, tag, account and secret handling have been reviewed.

### Build and prove it

Run the workflow from the service directory and explain the purpose of every command:

- docker build -t claims-api:day2 . — build from the current directory's context.
- docker image inspect claims-api:day2 --format '{{.Config.User}} {{json .Config.ExposedPorts}}' — inspect image metadata, not application health.
- docker run --rm --entrypoint id claims-api:day2 — verify the configured non-root identity without starting the app.
- docker compose config --quiet — validate/render Compose configuration; it does not start containers.
- docker compose up -d --build --wait — build and start API and database; readiness depends on configured health checks.
- docker compose ps, docker compose logs --tail=80 claims-api, curl -i http://127.0.0.1:8081/actuator/health — inspect service state, app logs and API response separately.
- POST a synthetic claim, GET it, then query that exact ID with psql. This verifies the HTTP-to-database path.
- docker compose down stops/removes Compose containers/network; it retains the named database volume. docker compose down -v deletes the lab database data, so use it only when deliberately resetting disposable data.

Compose creates a private network. The API connects to database service DNS postgres:5432; localhost inside the API container means the API container itself. The host-published database port 127.0.0.1:5433 is for host tools, not the container-to-container path. The API must listen on 0.0.0.0 inside its container; host publishing maps 127.0.0.1:8081 to container port 8081. EXPOSE documents a port; it does not publish it.

### Troubleshooting reps — induce one at a time

Use a disposable lab and keep the last known-good file/image. Capture failed command/output first. Change one thing at a time.

- Buildx too old / Compose cannot build: check docker buildx version, plugin path/version, then rebuild. Do not mistake a running old image for a successful new build.
- Maven cannot find POM or source: check build context (.), Dockerfile location, COPY paths and .dockerignore; use --no-cache only after explaining what cache bypass changes.
- No JAR / wildcard COPY fails: run Maven package; inspect target/; compare JAR path and stage.
- Container exits / connection refused: inspect docker compose ps -a and docker compose logs; compare app port, SERVER_PORT, entrypoint and Compose mapping.
- API cannot connect to PostgreSQL: inspect DB_URL without printing passwords, service DNS, DB health, credentials' source and port. From the API container, expected hostname is postgres, not 127.0.0.1.
- Host curl cannot connect but API container is healthy: check host-to-container publication and loopback binding; check listener/container status.
- Permission denied: inspect USER, copied file ownership and runtime write paths. Do not reflexively solve it with root.
- API starts before DB is ready: compare health check and depends_on: condition: service_healthy; distinguish container started from dependency ready.

### Day 2 interview questions

1. Walk through your Dockerfile line by line. Why two stages? Which artifacts exist in runtime image?
2. What is a build context? What does the final dot in docker build -t claims-api:day2 . mean?
3. RUN vs CMD vs ENTRYPOINT? Why use exec form for Java?
4. What does .dockerignore protect, and what breaks if it excludes pom.xml or src?
5. EXPOSE vs Compose ports? Explain host port, container port and loopback binding.
6. Why does jdbc:postgresql://postgres:5432/claims work between Compose services while localhost fails?
7. What is multi-stage build's effect on image size, build tools and attack surface? What does it not guarantee?
8. How do you verify a container runs non-root? What do docker image inspect and docker run --entrypoint id tell you?
9. Compose says app is healthy, but users see 500s. What logs, dependencies, endpoints and SQL evidence do you gather?
10. Difference between docker compose down and docker compose down -v? What data is at risk?
11. Compose build fails because buildx is old, but an existing image starts. How do you prove which image/version is running?
12. Compare Compose with ECS Fargate. Which responsibilities move to ECS/AWS, and which app/network/IAM responsibilities remain yours?

Answer using symptom → impact → evidence → hypothesis → safe fix → verification → prevention. Do not call a local Docker/Compose deployment ECS or production experience.

---

## Day 3 — Terraform, Jenkins pipeline and operational scripting

### Watch first

- [HashiCorp: Terraform Basics](https://www.youtube.com/watch?v=_45W3Z8XWL4) — 00:00–15:30; focus on configuration, init, plan, apply, update/destroy and state.
- [HashiCorp: Introduction to Terraform](https://www.youtube.com/watch?v=ZFLWA1kQ3ls) — conceptual overview of Terraform and infrastructure lifecycle; use if the workflow video felt too command-focused.
- [Your First Jenkins Declarative Pipeline Script from scratch](https://www.youtube.com/watch?v=ndruoIiJXkk) — watch for Jenkinsfile structure and stages, then map those stages to our Maven test and container build.
- Secondary reference: [Jenkins Pipeline as Code](https://www.jenkins.io/doc/book/pipeline/pipeline-as-code/). Treat credentials, approvals and deployment checks as first-class pipeline design, not optional decorations.

### Connect it to our lab and resume

Terraform is the future way to describe repeatable infrastructure; today Dockerfile and Compose describe repeatable application packaging and local runtime dependencies. Jenkins would orchestrate repository checkout, tests, image build/scan, publication, controlled deployment and post-deploy smoke checks. A green build alone does not prove users can complete a business transaction.

### Practice and interview drill

- Read a sample Terraform plan and identify create/update/replace/destroy, blast radius, state/backend/locking risks and approval point. Do not apply cloud resources for this recall exercise.
- Draw a Jenkins pipeline for this app: checkout → ./mvnw test → Docker build → image/security scan → publish immutable tag → approval → deploy → health + POST/GET smoke test → rollback on failure.
- Explain where credentials live, how logs avoid secret disclosure, how the artifact is promoted without rebuilding, and who approves production changes.
- Scenario: Jenkins is green but API returns 500 after deploy. Check deployed image digest/tag, rollout events, app logs, health, dependency connectivity and synthetic transaction; identify rollback trigger and recovery signal.
- Scenario: Terraform plan unexpectedly replaces a network resource. Stop before apply; inspect plan, references, state, provider/module change and dependent workloads; consult infrastructure owner and prepare a safe change/rollback.

### Day 3 interview questions

1. What do terraform init, plan, apply and destroy do? Which previews changes?
2. What is Terraform state, why protect it, and what is drift?
3. Difference between Terraform and CloudFormation for this team? What determines the choice?
4. Why store a Jenkinsfile with the application? What are stage, agent, step and credential binding?
5. How structure CI vs CD, immutable artifacts, approvals, smoke checks and rollback?
6. Jenkins passes but users fail after deploy. What signal is missing, and how prove the version actually deployed?
7. How make an operational script idempotent, observable and safe to rerun?
8. Which teams do you involve for a network/IAM issue, and what evidence do you bring instead of forwarding a vague alert?

Do not claim Terraform/Jenkins hands-on from this study map until those stages are actually built and run in a later lab ticket.

---

## Readiness tracker

| Day | Must demonstrate before moving on |
|---|---|
| 1 | Recall the host/container request path, explain Linux checks, diagnose API-stopped drill, verify DB persistence, explain impact and recovery. |
| 2 | Write Dockerfile from blank, explain each instruction, build/inspect/run image, connect Compose services via service DNS, complete at least two failure drills and answer Day 2 questions aloud. |
| 3 | Read Terraform plan safely, describe Jenkins pipeline/artifact path, reason through deployment failure and rollback, explain current hands-on boundaries accurately. |

Record weak answers, exact evidence and one next practice task in the matching day's report/ticket. A watched video without recall and hands-on explanation is not a completed prerequisite.
