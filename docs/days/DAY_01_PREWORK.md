# Day 1 Prework — Learn the service and SRE workflow before building

**Ticket:** LAB-001 — Build and operate a small Java service with PostgreSQL  
**Status:** Historical prework for the completed Day 1 lab. The instructions remain a study path; actual work and evidence are in [PROJECT_DAY_01.md](PROJECT_DAY_01.md).

## Who this guide is for

Assume you are new to Linux operations, HTTP APIs, Java services, databases, Docker, and SRE. Learn one idea at a time. A video gives you a picture; this guide and the project file connect that picture to the actual files and commands. You do not need to memorize jargon before understanding the request path.

## Video learning order

Watch the listed sections in order. Pause after each section and explain what you saw without copying the speaker’s wording.

1. **How SRE work fits a team:** [SRE Fundamentals — Google Cloud](https://www.youtube.com/watch?v=eopc_ijIfLg). Watch 07:06–11:03 for SRE/DevOps and reliability work; 11:03 onward for error budgets; 23:57–49:55 for monitoring, change management, incident response, postmortems, and toil. Ask: what signal tells a team users are affected, who coordinates an incident, and how does a team prevent recurrence?
2. **How an HTTP request becomes application code:** [Build Your First Spring Boot REST API](https://www.youtube.com/watch?v=wfj-Z9OQpCA). Focus on project structure, dependencies, controller, and tests (about 2:19–16:07). Map the controller idea to ClaimController and the data path to ClaimRepository and Claim.
3. **How Java builds are repeatable:** [Introduction to Maven and its Lifecycle](https://www.youtube.com/watch?v=gzeIvdT3Dq4). Learn that compile, test, and package are different build stages. Map Maven concepts to pom.xml, the checked-in Maven Wrapper, and ./mvnw test.
4. **How relational data is stored and queried:** [PostgreSQL and SQL for Beginners](https://www.youtube.com/watch?v=qw--VYLpxG4). Focus on tables, rows, keys, SQL SELECT, and psql. For the lab, use synthetic data and the documented read-only query to verify persistence.
5. **How a local dependency runs in a container:** [Docker Compose beginner tutorial](https://www.youtube.com/watch?v=iOGEBj7Ozak). Focus on services, port publishing, health checks, named volumes, logs, and exec. Map these to the PostgreSQL service in services/claims-api/compose.yaml.

Videos are learning references, not evidence that you performed the lab. Confirm exact commands and configuration against the repository and official links in the project report.

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
