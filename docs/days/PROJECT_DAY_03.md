# Project Day 3 — Rebuild the service and add observability

**Ticket:** LAB-003 — Add service metrics and an operational dashboard  
**Status:** Ready / execution log not yet completed. This is a blank evidence record, not proof that the Day 3 lab has run.  
**Prework:** [DAY_03_PREWORK.md](DAY_03_PREWORK.md)  
**Previous build records:** [Day 1](PROJECT_DAY_01.md) · [Day 2](PROJECT_DAY_02.md)

## How to use this file

Read the prework and watch the assigned videos first. Then use this file during the lab, one stage at a time. The prior-day project files are the rebuild recipes; this file records what you personally rebuilt and changed today. For every command, note what it does, why it is next, expected evidence, actual result/exit code, and what the result proves or does not prove. Replace every bracketed prompt with observed facts. Never copy example output or claim an unrun test/drill passed.

## Assignment and team context

**Manager/ticket owner:** [priority, impact, acceptance criteria, reviewer]  
**Assigned engineer:** [learner]  
**Primary role practiced:** SRE — choose useful signals, distinguish application impact from telemetry failure, document operational response.  
**Supporting responsibilities:** application/service owner — application instrumentation and metric meaning; DevOps/platform — reproducible Compose configuration and versioning; security partner — endpoint exposure, secrets, and metric-label review. In a real organization, confirm owners and change approvals before touching production.

**User/service problem being practiced:** An on-call engineer needs evidence about whether the API is responding and whether telemetry is being collected, so they can distinguish customer-facing failure from monitoring-pipeline failure.

## 0. Session inventory and safety boundary

- Date/time and time zone: [fill]
- AWS region, instance type, disk, security group boundary: [fill; do not include credentials]
- Repository branch and commit before work: [fill]
- Confirm repository is the personal learning repo; working tree clean before edits: [command + result]
- Synthetic data only; no customer/employer information: [confirm]
- Cleanup plan and expected remaining AWS resources/cost: [fill]

## 1. Rebuild and verify Day 1 — LAB-001

Follow the exact procedure in PROJECT_DAY_01.md. Explain each command before running it. Record actual command/outcome; do not duplicate the old report’s evidence as today’s evidence.

| Checkpoint | What/why | Command or action | Actual result / exit code | What it proves / gap |
|---|---|---|---|---|
| Host identity and capacity | Confirm correct disposable Linux host, architecture, disk and resources | [fill] | [fill] | [fill] |
| Git checkout | Recreate baseline from committed personal repo | [fill] | [fill] | [fill] |
| PostgreSQL | Start and verify DB readiness | [fill] | [fill] | [fill] |
| Maven tests | Check defined application behavior before live traffic | [fill] | [fill] | [fill] |
| API health | Verify a live HTTP response | [fill] | [fill] | [fill] |
| POST → GET → SQL | Prove API behavior and row persistence with synthetic data | [fill] | [fill; redact generated IDs if desired] | [fill] |
| API-stop drill | Isolate API process/listener from DB health, recover, and verify | [fill] | [fill] | [fill] |

**Day 1 recall questions attempted before checking notes:** [answer briefly; mark corrections]

## 2. Rebuild and verify Day 2 — LAB-002

Follow PROJECT_DAY_02.md. First write a Dockerfile from memory, then compare with the committed one. Capture the build/Compose changes made today, including any actual failure and how evidence narrowed it.

| Checkpoint | What/why | Command or action | Actual result / exit code | What it proves / gap |
|---|---|---|---|---|
| Image build | Recreate image from Dockerfile and context | [fill] | [fill] | [fill] |
| Image/runtime review | Check runtime user, exposed port, and built image | [fill] | [fill] | [fill] |
| Compose config/startup | Validate interpolation then start dependencies and API | [fill] | [fill] | [fill] |
| Service DNS | Explain/check API uses postgres:5432 on Compose network | [fill] | [fill] | [fill] |
| API + persistence | Check health, POST/GET and SQL row | [fill] | [fill] | [fill] |
| Day 2 recall | Explain build/runtime stages, context, non-root user, ports, volume and readiness | [write answer/corrections] |  |  |

## 3. New Day 3 learning check — answer before editing

Write your own first attempt, including “not sure” where needed. After the build, correct it in a different column.

| Topic | First attempt | Corrected explanation after build |
|---|---|---|
| Metrics vs logs vs traces | [fill] | [fill] |
| Instrumentation and exporter endpoint | [fill] | [fill] |
| Prometheus pull/scrape, target, sample, labels | [fill] | [fill] |
| Grafana data source, query, time range, panel | [fill] | [fill] |
| Health endpoint vs successful metric scrape vs user request | [fill] | [fill] |
| Cardinality/privacy: why not label with claim IDs/user IDs/raw paths? | [fill] | [fill] |

## 4. LAB-003 implementation log — do not pre-fill results

Change one layer at a time and verify it before continuing. Explain why each config belongs in its file and how traffic crosses the container network.

| Step | File/component | Change and why | Command/check | Actual evidence |
|---|---|---|---|---|
| 1 | pom.xml / Spring metrics dependency | [add compatible registry dependency; explain metric bridge] | [test/package] | [fill] |
| 2 | application.properties | [expose only required metrics endpoint within lab boundary] | [inspect config safely] | [fill] |
| 3 | Prometheus scrape configuration | [target API by Compose service DNS, internal port, correct path] | [config validation/logs] | [fill] |
| 4 | compose.yaml | [add Prometheus/Grafana, pinned image tags, network, health/ports] | [docker compose config --quiet; up] | [fill] |
| 5 | Grafana provisioning/dashboard | [connect data source and version dashboard/query config] | [open UI/query panel] | [fill] |
| 6 | Review | [inspect diff, secrets, labels, ports, versions, health] | [git diff --check/status; safe checks] | [fill] |

### Architecture built today

Application request path: [draw the actual path and port numbers].  
Metrics path: [draw actual instrumentation → endpoint → Prometheus target/scrape → stored time series → Grafana datasource/query/panel].  
Explain which address is for host access and which name/port is used container-to-container: [write].

## 5. Verification evidence

Record exact timestamp and output summary (do not include secrets). A green container status alone is not enough.

- Compose configuration validation: [command, exit code]
- API health response: [status/body]
- POST and GET result: [status and synthetic description; omit or sanitize IDs]
- SQL row check: [query result summary]
- Prometheus target: [target name, state, last scrape error if any]
- Prometheus query: [exact query, time range, observed sample/result]
- Grafana datasource: [connected result]
- Dashboard panels: [panel title, query, unit, time range, observed value]
- What these signals do not prove: [state limits, e.g. no production SLO/HA claim]

## 6. Troubleshooting and scenario tickets

For each drill, mark **Not run**, **Practiced**, or **Blocked**. Do not break the normal stack until baseline checks pass. Use a reversible bad scrape target/config in this disposable lab; restore it immediately after collecting evidence.

### Drill A — Prometheus target deliberately unavailable

- Status: [Not run / Practiced / Blocked]
- Symptom and impact: [fill; telemetry gap is not automatically API outage]
- Baseline evidence: API health, Compose state, target state and scrape error [fill]
- Hypothesis and narrow check: [fill]
- Safe change / rollback: [fill]
- Recovery proof: target returns UP, samples return, API health/data path still works [actual evidence]
- Prevention/runbook follow-up: [fill]

### Drill B — API healthy, Grafana panel blank

- Status: [Not run / Practiced / Blocked]
- Check in order: target and scrape → samples in Prometheus → Grafana datasource → query/labels/time range/units [actual evidence]
- Diagnosis, safe correction, same-signal recovery proof: [fill]

### Drill C — API unhealthy and metrics disappear

- Status: [Not run / Practiced / Blocked]
- Separate API process/container, DB dependency, and scrape evidence; record the first failed boundary: [fill]

### Evidence-based incident response template

**Symptom → user impact → timeline → evidence → hypothesis → narrow test → diagnosis → safe mitigation → recovery proof → follow-up/owner/due date.** Record actual facts, not a guessed root cause. Preserve sanitized logs and exact commands only; never put credentials or customer data in Git.

## 7. Interview practice linked to this work

Attempt out loud before reading your notes. Use: context → action → evidence → result → limitation → next improvement. Update the answer with what actually happened today.

1. **LAB-001 / application path:** Walk through POST /claims to the committed database row. Which files and protocol boundaries are involved?
2. **LAB-001 / troubleshooting:** API connection refused while PostgreSQL is healthy. What do process, listener, Compose, and health checks each establish?
3. **LAB-002 / Docker:** Explain each Dockerfile instruction and why builder and runtime images are separated. What does a non-root runtime reduce?
4. **LAB-002 / networking:** Why is postgres:5432 correct inside Compose while 127.0.0.1:5433 was correct from the Day 1 host process?
5. **LAB-003 / observability:** Explain the metric collection path. What evidence proves a target was scraped and Grafana queried real samples?
6. **LAB-003 / scenario:** API health is 200 but a dashboard is blank. Walk the evidence-based triage order and explain how you avoid declaring a customer incident too early.
7. **LAB-003 / SRE judgment:** What would you alert on, what would you put in a dashboard, and what additional traffic/SLI evidence is needed before defining a meaningful SLO?
8. **Cross-team ownership:** Which part is service-owner work, SRE work, platform/DevOps work, and security review? How would you coordinate and document a production change?

First response / correction / confidence (1–5): [fill]

## 8. Closeout, Git, and cleanup

- Acceptance criteria from DAY_03_PREWORK.md: [pass/fail with links to evidence]
- Files changed and reviewer-ready diff: [list]
- Tests/config validation actually run: [commands and results]
- Secrets scan/manual review: confirm .env, tokens, keys, generated database data and customer data are absent.
- Commit hash and personal branch: [fill]
- Push verified on personal GitHub: [fill]
- Cleanup: API/Compose state, EC2 status/termination, volumes, disks, addresses and other billable resources checked separately: [fill]
- Open defects, follow-up owner and next ticket: [fill]

**Close LAB-003 only when the build, recovery drill, verification, documentation, and GitHub copy all have actual evidence.** Otherwise leave the ticket Ready/In progress/Blocked and state why.
