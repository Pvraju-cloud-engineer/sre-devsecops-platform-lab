# Ticket Index and Troubleshooting Catalog

Use this folder as the first stop when you meet a failure. Find the matching ticket below, open its linked section, and follow the evidence-based triage and recovery notes.

## Current tickets

| Ticket | Status | Work item | Detailed record |
|---|---|---|---|
| LAB-001 | Done — baseline rebuilt and verified; recall practice remains part of learning | Rebuild and operate the existing Spring Boot API with PostgreSQL | [Day 1 guide and completion evidence](../days/PROJECT_DAY_01.md#13-day-1-completion-and-boundaries) · [Troubleshooting method](../days/PROJECT_DAY_01.md#8-command-reference-and-troubleshooting-method) · [Observed issues](../days/PROJECT_DAY_01.md#9-actual-issues-observed) · [API process stopped drill](../days/PROJECT_DAY_01.md#api-was-not-initially-listening) |
| LAB-002 | Done — container build, runtime checks, and troubleshooting evidence recorded | Containerize the existing API and run it with PostgreSQL using Compose | [Day 2 assignment](../days/DAY_02_PREWORK.md#mission) · [Day 2 build, evidence, and troubleshooting](../days/PROJECT_DAY_02.md#project-day-2--containerize-the-api-with-docker-compose) |
| LAB-003 | Ready — SRE observability build not started | Add API metrics, Prometheus scraping, Grafana data source/dashboard, and a safe missing-target drill | [Day 3 preparation and acceptance criteria](../days/DAY_03_PREWORK.md#mission) |

## How we track a ticket

- **Ready:** assignment and acceptance criteria are clear; work has not started.
- **In progress:** implementation or investigation is underway.
- **Blocked:** progress requires a named external dependency or decision; record what is needed.
- **Done:** acceptance criteria have been checked and the report contains observed evidence.

Each daily report is the single detailed record for that day's work. This index links to the assignment, troubleshooting method, and exact incident/scenario section. Do not create a second copy of the same notes.

## What a resolved troubleshooting record must include

1. Ticket, date/time, symptom, expected behavior, and user/service impact.
2. Baseline and evidence: command, relevant output/exit code, logs, process/listener/dependency state.
3. Hypothesis and the check that confirmed or disproved it.
4. Root cause, mitigation/fix, and why the action was safe.
5. Recovery verification using the signal that originally failed.
6. Rollback or cleanup, follow-up prevention, and what remains untested.
7. SRE/DevOps role connection and a concise interview explanation.

Use synthetic data only. Never add credentials, tokens, private keys, account identifiers, customer information, or employer incident details. Mark a scenario **practiced** only after running it and recording the evidence.
