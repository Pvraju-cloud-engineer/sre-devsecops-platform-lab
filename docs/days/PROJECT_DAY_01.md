# Project Day 1: API and Database Baseline

## Assignment

Establish a small Java/Spring Boot service that accepts a claim request, persists it, returns the record, and exposes a health endpoint. Learn the Linux account boundary, Maven project, Docker daemon, database container, and end-to-end request path.

Acceptance evidence observed: health returned JSON; POST returned HTTP 201 with an ID and SUBMITTED status; GET returned the record; PostgreSQL showed the row.

## What was built and observed

- Amazon Linux 2023 EC2 lab host accessed from Windows using PuTTY and a key pair.
- Spring Boot application under the ec2-user home directory, with Maven Wrapper (mvnw), pom.xml, and src/ tree.
- Actuator health endpoint on loopback port 8080.
- PostgreSQL 16 Alpine container named claims-db, with database/user claims.
- Docker named volume claims-db-data mounted at the PostgreSQL data directory.
- Host port 5432 was bound to 127.0.0.1; PostgreSQL was not published as a public listener.
- Claims endpoint supported create and read. The saved row was found after an application restart check.

This is an observed baseline, not yet a reproducible repository build: app source, Docker configuration, automated tests, and deployment scripts still need to be checked in and rebuilt from a clean host.

## Request and persistence flow

PuTTY terminal or local curl -> 127.0.0.1:8080 Spring HTTP endpoint -> controller -> service -> repository/JDBC -> PostgreSQL container -> named volume. The response returned JSON with ID, description, status, and timestamp.

Actuator gives an operational health signal. Docker runs PostgreSQL isolated from host packages. The named volume keeps database files outside the container writable layer, so replacing the container does not automatically remove the data. A volume is persistence, not a backup.

## Commands observed or used

Run from the EC2 shell. Use plain URLs; do not paste Markdown link syntax into the terminal.

    id
    ls -la ~/usaa-sre-lab/app
    docker --version
    docker compose version
    docker ps --filter name=claims-db
    docker logs --tail 20 claims-db
    docker exec claims-db pg_isready -U claims -d claims
    curl http://127.0.0.1:8080/actuator/health
    curl -i -X POST http://127.0.0.1:8080/claims -H 'Content-Type: application/json' -d '{"description":"rear bumper repair"}'
    curl http://127.0.0.1:8080/claims/<claim-id>
    docker exec claims-db psql -U claims -d claims -c "SELECT id, description, status, created_at FROM claims;"

The database container was run with Docker options equivalent to: detached mode; name claims-db; restart unless-stopped; POSTGRES_DB and POSTGRES_USER set to claims; POSTGRES_PASSWORD provided locally; port 127.0.0.1:5432:5432; volume claims-db-data mounted to /var/lib/postgresql/data; image postgres:16-alpine. Treat any password as a local secret, never commit it. This initial command was a practice shortcut; later replace it with a compose file plus excluded environment file or a secret manager.

## Debugging: symptom, evidence, action, learning

### Project directory not found

The project was initially under root's home, while the shell was ec2-user. The tree was moved to /home/ec2-user/usaa-sre-lab and ownership corrected. Tilde resolves to the current account home: root's home differs from ec2-user's. Check pwd, whoami, id, and ls -la. Work as an unprivileged app user where possible.

### su asked for a password

SSH key auth and Linux su auth are separate. su ec2-user requested the target user's password. Reconnect in PuTTY as ec2-user or use an authorized admin method; do not invent a password to work around it.

### First curl returned connection refused

TCP reached the host but nothing accepted connections on port 8080 at that moment. Check process and listener with ps -ef | grep '[j]ava' and ss -lntp | grep ':8080', then inspect the application terminal and health endpoint. PostgreSQL readiness does not imply Spring Boot is running. A later POST succeeded when the app was running in another terminal.

### Docker Compose was unavailable

Docker Engine worked, but docker compose was not a Docker subcommand. Docker Engine and the Compose plugin are separate components here. Direct docker run was used for the first DB lab; verify/install the plugin before relying on compose.yaml.

### Docker group permission

After usermod -aG docker ec2-user, a fresh PuTTY session showed the docker group and docker ps worked. Group membership is attached to a login session. Docker group access is effectively root-level; grant only to trusted lab users.

### Markdown URL caused shell syntax error

Text shaped like [label](URL) is Markdown, not a shell URL. Copy only the plain URL. Quote JSON and use valid shell line continuations if splitting commands.

### PostgreSQL logs showed shutdown messages during initialization

The final log said the database was ready and pg_isready succeeded. The official image may start a temporary server to initialize a new data directory, stop it, and start the real server. Decide readiness from the final state and health probe, not one isolated shutdown line.

## Restart test distinction

- Restarting Spring Boot and reading the record verifies the app reconnects to stored data.
- Restarting PostgreSQL with the same named volume checks that data survives container restart/replacement.
- Removing the named volume deletes the lab data. Do not remove it unless intentionally resetting the lab.

## Interview questions and concise answers

**Why use a named Docker volume?** It keeps PostgreSQL data outside a disposable container layer and can be reattached. It is not a backup, so restore must be tested separately.

**Why bind PostgreSQL to 127.0.0.1?** Only host-local clients reach the published port. This reduces network exposure. Multi-host access should use private networking and narrow security rules.

**What does HTTP 201 mean?** The create request succeeded and a resource was created. The generated ID is used to read the record.

**What does health prove?** The process can answer that health request and may report dependencies, depending on configuration. It does not prove every user journey works.

**How would you triage a failed API request?** Establish scope/time; inspect response; verify process and listener; inspect application logs; check health; check DB readiness, network, and credentials; query the record; correlate evidence and recover.

**Why did first curl fail and later succeed?** No process was accepting port 8080 at the first attempt. Once the app was running in its own terminal, the endpoint returned success. Verify with listener/process/log evidence rather than blindly retry.

**What remains undone?** Clean-checkout rebuild, source/test review, secret externalization, reproducible Compose, CI, image hardening, IaC, dashboards, SLOs, and tested backup/restore.

## Day 1 closeout

- [x] Confirmed Linux identity and Docker access.
- [x] PostgreSQL became ready.
- [x] Spring health endpoint responded.
- [x] Created and fetched a claim; verified the DB row.
- [x] Observed the record after an application restart.
- [ ] Recreate baseline from this repository on a clean environment.
- [ ] Add sanitized reproducible app source and tests.
- [ ] Replace one-off docker run with documented configuration.

## Next assignment

Day 2: explain pom.xml, src/main, src/test, Maven Wrapper, configuration, controller/service/repository/entity; verify build and tests from a clean checkout; and move runtime settings to environment configuration. Do not add larger AWS services until the baseline is reproducible.
