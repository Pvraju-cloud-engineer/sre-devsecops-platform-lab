# Day 1 Prework — Learn the service and SRE workflow before building

**Ticket:** LAB-001 — Build and operate a small Java service with PostgreSQL  
**Status:** Historical prework for the completed Day 1 lab. The instructions remain a study path; actual work and evidence are in [PROJECT_DAY_01.md](PROJECT_DAY_01.md).

## Who this guide is for

Assume you are new to Linux operations, HTTP APIs, Java services, databases, Docker, and SRE. Learn one idea at a time. A video gives you a picture; this guide and the project file connect that picture to the actual files and commands. You do not need to memorize jargon before understanding the request path.

## Video learning order — exact intervals

Watch only the assigned ranges, in this order. Times are video timestamps (hh:mm:ss when needed). After each range, pause and explain the idea using the named file or command below. You do not need to watch every minute of a long course.

1. **Linux shell and host checks (about 59 min total):** [The 50 Most Popular Linux & Terminal Commands — freeCodeCamp](https://www.youtube.com/watch?v=ZtqBQ68cfJc): 00:29:43–00:49:21 (whoami, man, pwd, ls); 00:49:21–01:02:03 (cd basics); 02:32:10–02:44:04 (find, grep, du, df); 02:47:32–03:01:37 (ps, top, kill). Connect to the EC2 baseline, locating the repo, checking disk, and checking whether Java is running. Skip commands you are not ready to run; never copy destructive commands from a video into the lab.
2. **SRE responsibilities and incident flow (about 34 min):** [SRE Fundamentals — Google Cloud](https://www.youtube.com/watch?v=eopc_ijIfLg): 00:07:06–00:11:03 (SRE, DevOps, team responsibilities); 00:11:03–00:23:57 (error budgets); 00:23:57–00:26:57 (monitoring); 00:30:55–00:34:00 (change management); 00:41:50–00:49:55 (incident response and postmortems); 00:49:55–00:52:55 (toil). Connect incident checks to the API-stopped drill and role table in PROJECT_DAY_01.md.
3. **Spring REST API request path (about 32 min):** [Spring Boot Tutorial for Beginners | Full Course 2025 — Amigoscode](https://www.youtube.com/watch?v=Cw0J6jYJtzw): 00:03:20–00:14:21 (project, dependencies, API endpoint, test); 00:15:35–00:22:55 (model, controller, JSON response); 00:28:41–00:33:43 (PostgreSQL container and Compose); 00:33:43–00:42:33 (Spring Data JPA, database configuration, troubleshooting). Map to ClaimController → ClaimRepository → Claim → PostgreSQL.
4. **Maven build lifecycle (about 5 min):** [Introduction to Maven and its Lifecycle](https://www.youtube.com/watch?v=gzeIvdT3Dq4): 00:00–00:04:51 (watch the full clip). Connect compile/test/package to pom.xml, the Maven Wrapper, and ./mvnw test. A passing test proves only the behaviors exercised by that test.
5. **PostgreSQL and SQL (about 39 min):** [PostgreSQL and SQL for Beginners](https://www.youtube.com/watch?v=qw--VYLpxG4): 00:03:16–00:10:53 (database, relational model, SQL); 00:17:38–00:21:39 (terminal/psql); 00:41:37–00:55:55 (tables, constraints, inserts); 01:12:28–01:25:29 (SELECT, ORDER BY, WHERE). Connect to the claims table and use only the documented read-only SELECT with synthetic data.
6. **Docker Compose and service dependencies (about 13 min):** [Docker Compose Tutorial — KodeKloud](https://www.youtube.com/watch?v=iOGEBj7Ozak): 00:00–00:01:30 (why Compose); 00:05:53–00:10:13 (Compose file and services); 00:21:00–00:24:45 (depends_on and networks); 00:28:30–00:31:30 (troubleshooting and recap). Connect to the PostgreSQL service in services/claims-api/compose.yaml. This video does not explain every health-check/volume detail; use the file and the linked official Docker docs for those.

**After each video:** close it, explain the idea in your own words, point to the repo file/command, and answer: “What evidence would show this part is healthy, and what could still be broken?” Videos are learning aids, not evidence that you performed the lab.

## Beginner concepts to understand before opening the terminal

- **Host / EC2:** the Linux virtual machine where the lab runs. SSH opens a remote shell; ec2-user is a normal account and sudo is used for authorized system administration.
- **Process:** a running program. In Day 1, the Java Spring application is a host process; PostgreSQL is a Docker container.
- **Port / listener:** a numbered network endpoint where a process accepts traffic. Curl to 127.0.0.1:8081 checks the API from the same EC2 host.
- **HTTP request and response:** a client asks a server to do something. POST creates a claim and returns 201; GET reads a claim and normally returns 200. A 400 can mean the submitted description failed validation; 404 means the requested claim ID was not found.
- **JSON:** structured text used by this API for request and response bodies.
- **Spring Boot:** the Java framework that starts the web server and connects application components. The controller handles HTTP, the repository handles persistence operations, and the entity describes stored claim data.
- **Maven / pom.xml:** the build system and its project recipe. Dependencies provide libraries. The Maven Wrapper runs the pinned Maven version for repeatable builds. Tests check defined behavior but do not prove that a live service is reachable.
- **PostgreSQL:** the relational database storing rows. The JDBC URL tells Java where the database is. SQL SELECT lets an operator verify a row independently of the API response.
- **Image, container, volume:** an image is a packaged template; a container is a running instance of an image; a named volume stores database files outside the replaceable container layer.
- **Compose:** YAML configuration and commands for running related containers together. A health check tests database readiness; it does not prove the Spring API works.
- **Environment variable and secret:** configuration passed to a process. The real .env file contains a local password and must stay ignored by Git. .env.example documents names with a placeholder, not a usable secret.
- **SRE incident method:** state the symptom and impact, collect evidence, form a hypothesis, run the narrowest safe check, mitigate, verify the original signal, and record follow-up. Do not guess and change several things at once.

## Draw the dots before the lab

Laptop / SSH → EC2 Linux host → curl HTTP request to host port 8081 → Spring Boot process → ClaimController validates request → ClaimRepository / JPA / JDBC → PostgreSQL container on port 5432 → SQL row stored on named volume → JSON response to curl.

The host-side Compose mapping publishes PostgreSQL on 127.0.0.1:5433 for the host application. It maps to database port 5432 inside the container. The named volume preserves database files across container recreation. The API runs directly on the host in Day 1, so docker compose ps lists PostgreSQL but not Java.

## Who does what in a real organization?

| Role | Typical responsibility in this lab-shaped work |
|---|---|
| Engineering manager / ticket owner | Explains priority, impact, acceptance criteria, access, and review expectations. |
| Application engineer / service owner | Owns API behavior, validation, persistence model, and application tests. |
| SRE (main role practiced here) | Checks reliability signals, diagnoses service impact, restores service safely, writes runbooks, and follows up to reduce repeat toil. |
| DevOps / platform engineer | Makes build and runtime setup repeatable, maintains deployment tooling, and helps teams operate dependencies. |
| Database owner | Defines database access, schema/data protections, backup expectations, and recovery practices. |
| Learner / assigned engineer | Rebuilds, checks evidence, troubleshoots, records results, and asks for review. |

On a small team, one person may perform several roles. In a real production system, follow its access controls, change process, on-call escalation, and customer-data rules. This lab is a synthetic single-host exercise.

## Check your understanding before the build

Try without notes. It is fine to say “I’m not sure”; bring that point to the teaching/build session.

1. What is the difference between the EC2 host, a running process, a Docker container, and an image?
2. Follow POST /claims from curl to the database row and back to the JSON response.
3. What does HTTP 201 mean here? What evidence would prove the row was saved?
4. Why is the database reachable from the host at 127.0.0.1:5433 while PostgreSQL listens inside its container on 5432?
5. If curl cannot reach port 8081 but PostgreSQL is healthy, what would you check before restarting anything?
6. What does ./mvnw test tell you? What does it not tell you about the live API?
7. What survives a PostgreSQL container restart, and where is that data stored?
8. What information must never go into the public repository?

## Handoff into the project-day file

After this prework, open [PROJECT_DAY_01.md](PROJECT_DAY_01.md). It is the separate build/rebuild and evidence record: environment setup, file map, command explanations, observed outputs, debugging, failure drills, interview questions and answers, cleanup, and what remains unproven. Use that file to recreate the lab step by step. Do not treat reading the prework as completing the build or a troubleshooting drill.


## Topic-by-topic video map and mastery checks

Use this as the required Day 1 video path. Work through one block at a time: watch the named section, pause, explain it in your own words, point to the matching repo file or command, then do the small practice task. The video introduces the idea; the repo and observed evidence define what we actually built.

### 1. Linux host, shell, and first checks

**Watch:** [The 50 Most Popular Linux & Terminal Commands — freeCodeCamp](https://www.youtube.com/watch?v=ZtqBQ68cfJc): 00:29:43–00:49:21 for whoami/man/pwd/ls and 00:49:21–01:02:03 for cd basics. Use 02:32:10–02:44:04 for find/grep/disk checks and 02:47:32–03:01:37 for ps/top/kill.

**Learn:** the EC2 host is the remote Linux machine; SSH opens a shell as **ec2-user**; **sudo** is elevated permission; **pwd** shows location; **id** and **whoami** establish identity; **uname -m** shows CPU architecture; **free -h**, **df -h /**, and **nproc** show memory, disk, and CPU count. **ps** shows processes; **ss -lntp** shows listening TCP ports.

**Connect:** these are the opening checks before installing or debugging the app. They answer “am I on the right machine, as the right user, with enough resources, and is the expected process/port present?”

**Practice / mastery:** explain the output of each command from your own fresh-EC2 transcript. Given “curl cannot connect to 8081,” identify **ps** and **ss** as process/listener checks, then use curl as the application-level check.

### 2. SRE, DevOps, and incident handling

**Watch:** [SRE Fundamentals — Google Cloud](https://www.youtube.com/watch?v=eopc_ijIfLg). Watch 00:07:06–00:11:03 for SRE/DevOps, 00:11:03–00:23:57 for error budgets, 00:23:57–00:26:57 for monitoring, 00:30:55–00:34:00 for change management, 00:41:50–00:49:55 for incident response/postmortems, and 00:49:55–00:52:55 for toil.

**Learn:** DevOps improves delivery and shared ownership; SRE applies engineering and measurable reliability practices to operations. An incident response starts with user/service impact, establishes evidence, assigns communication and technical roles, mitigates safely, verifies recovery, and records prevention work.

**Connect:** the API-stopped drill in **PROJECT_DAY_01.md** is a controlled lab fault. SRE owns clear impact/health reasoning and follow-up; the application owner helps with Java behavior; the platform/DevOps owner supports host and repeatable setup; the database owner helps distinguish database health from application health.

**Practice / mastery:** say the sequence symptom/impact → baseline → hypothesis → narrow check → mitigation → verify the original signal → ticket/runbook follow-up. State that our practice lab is not a production incident and do not invent customer impact.

### 3. HTTP, REST, and the Spring request path

**Watch:** [Spring Boot Tutorial for Beginners | Full Course 2025 — Amigoscode](https://www.youtube.com/watch?v=Cw0J6jYJtzw): 00:03:20–00:14:21 for project/dependencies/API/test; 00:15:35–00:22:55 for model/controller/JSON; 00:33:43–00:42:33 for JPA, database configuration, and troubleshooting.

**Learn:** an HTTP request has method, path, headers, and sometimes a body. JSON is the request/response data format. **POST /claims** creates and normally returns 201; **GET /claims/{id}** reads and returns 200; invalid input can return 400; an unknown ID returns 404. Spring Boot starts the service; the controller maps HTTP to application behavior, validation checks input, the entity models stored data, and the repository performs persistence operations.

**Connect:** trace **ClaimController → ClaimRepository → Claim → PostgreSQL**. Compare the API response with the read-only SQL query.

**Practice / mastery:** draw this request path from memory; explain why the controller should not itself contain SQL. Predict the response for blank description and unknown UUID, then point to the validation/test and lookup behavior that supports your answer.

### 4. Maven build, tests, and repeatability

**Watch:** [Introduction to Maven and its Lifecycle](https://www.youtube.com/watch?v=gzeIvdT3Dq4), 00:00–00:04:51 (full clip). Connect phases to pom.xml and ./mvnw test.

**Learn:** **pom.xml** describes the project, dependencies, Java/build configuration, and plugins. The Maven Wrapper (**./mvnw**) invokes the pinned Maven version. Compile checks source; test runs automated behavior checks; package creates the artifact. A passing test proves only the behavior those tests exercise, not that a currently running server is reachable.

**Connect:** inspect **services/claims-api/pom.xml** and the wrapper properties; run **./mvnw test** from the service directory. In the observed debugging history, a missing Jackson class was a compile-time dependency issue, separate from runtime connectivity.

**Practice / mastery:** explain the first meaningful compiler error, name the file/dependency implicated, and tell the difference between test evidence and a live curl smoke test.

### 5. PostgreSQL, relational data, and independent verification

**Watch:** [PostgreSQL and SQL for Beginners](https://www.youtube.com/watch?v=qw--VYLpxG4): 00:03:16–00:10:53 database/SQL basics; 00:17:38–00:21:39 psql; 00:41:37–00:55:55 tables/constraints; 01:12:28–01:25:29 SELECT/WHERE.

**Learn:** PostgreSQL is a relational database: a table stores rows; columns define fields; a primary key identifies a row. JDBC is Java’s database connectivity interface; the JDBC URL identifies host, port, and database. JPA/Hibernate maps Java entity fields to relational storage. **pg_isready** tests whether PostgreSQL is accepting connections; **SELECT** reads data. Neither check alone proves the whole user request succeeded.

**Connect:** read **Claim.java** beside the **claims** table. The API runs **POST**, then the operator verifies the same synthetic UUID with a read-only SQL query.

**Practice / mastery:** explain why an HTTP 201 should be checked against the database when proving persistence. Describe one reason an organization might choose PostgreSQL or another database; selection depends on workload, skills, existing platform standards, licensing, operations, and support—not a universal “best” database.

### 6. Docker Compose dependency, ports, health, and persistence

**Watch:** [Docker Compose Tutorial — KodeKloud](https://www.youtube.com/watch?v=iOGEBj7Ozak): 00:05:53–00:10:13 services; 00:21:00–00:24:45 dependencies/networks; 00:28:30–00:31:30 troubleshooting. Read the repo Compose file and official Docker docs for health-check and volume details.

**Learn:** an image is a template, a container is a running instance, and a named volume keeps database files beyond a container replacement. In **127.0.0.1:5433:5432**, host port 5433 forwards to the container’s PostgreSQL port 5432 and binds only to host loopback. Compose health is a database readiness signal, not proof the API is healthy. **docker compose ps** shows container state; **logs** shows service output; **exec** runs a command inside a running service; **down -v** removes named volumes and their data.

**Connect:** read **services/claims-api/compose.yaml** and **.env.example**. The real **.env** is local configuration and must stay ignored.

**Practice / mastery:** explain what **docker compose up -d --wait** and **pg_isready** each verify. State why healthy PostgreSQL does not prove that Spring Boot is listening on 8081, and why deleting a volume is a data-destructive action.

### 7. Day 1 integrated recall

Close prework when, without looking at the notes, you can draw the host → HTTP → Spring controller → repository/entity → JDBC → PostgreSQL path; explain which service is a host process and which is a container; name the exact repo files; explain the checks and their limits; and describe how you would diagnose API-down/DB-healthy. Then compare your explanation with **DAY_01_TO_03_MASTERY_PLAN.md** and record weak topics before the rebuild.
