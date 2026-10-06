# Interview Round Readiness Matrix

This guide connects the daily build series to common SRE, production support, DevOps, and DevSecOps interview formats. Companies combine, rename, or skip rounds, so prepare for the skills being tested rather than memorizing one fixed interview sequence. This plan builds readiness; it cannot guarantee an offer.

## Our role target

**Primary target: mid-level SRE / production reliability.** The learner must reason about service behavior, user impact, telemetry, incidents, recovery, prevention, and cross-team ownership. **Supporting experience: DevOps and DevSecOps.** The learner must also explain repeatable builds, deployment automation, infrastructure, container security, and release controls. Be precise about what was built in this lab versus what is planned or only studied.

## Common interview formats and what to demonstrate

| Format | What the interviewer is checking | Practice evidence from this repository |
|---|---|---|
| Recruiter / profile screen | Clear experience summary, role fit, communication, location/shift/notice details | A concise introduction tied to actual resume work; explain the lab as practice without presenting it as employer production experience |
| T1 technical fundamentals | Linux, networking, HTTP, Java/Spring, SQL, Git, Docker, cloud basics, scripting | Explain each day’s architecture, commands, file changes, test output, and limits of the evidence |
| T2 deep technical | Root-cause reasoning, trade-offs, performance/reliability, safe operation, design choices | Walk from symptom to evidence to hypothesis to narrow test, fix, recovery verification, and prevention |
| Hands-on / live troubleshooting | Ability to operate tools under pressure without random changes | Rebuild the lab; diagnose controlled API, database, DNS, container, build, and telemetry failures; narrate evidence as you work |
| SRE / system design | SLIs, SLOs, error budgets, alert quality, scaling, capacity, resilience, DR, incident learning | Add these progressively to the service; distinguish measured evidence from assumptions and single-host limitations |
| DevOps / DevSecOps | CI/CD, IaC, release safety, image hygiene, scanning, secrets, access controls | Extend the project with pipeline, Terraform, security gates, deployment strategies, and rollback drills when those days are assigned |
| Manager / behavioral | Ownership, prioritization, ambiguity, collaboration, learning from failure | Use a truthful STAR story: situation, responsibility, actions, measurable result, learning; never invent incidents or metrics |
| Client / operations discussion | Calm impact communication, change control, handoffs, runbook discipline | Practice a short incident update: impact, current evidence, action owner, next update time, recovery proof, follow-up |

## A repeatable answer structure for technical scenarios

1. **Clarify:** Which service, users, region/environment, time window, and expected behavior?
2. **Scope impact:** What is failing, who is affected, and what still works? State uncertainty.
3. **Protect users:** Follow the approved runbook; mitigate safely and communicate ownership/updates.
4. **Collect evidence:** Check the failing signal, recent changes, logs, process/container, listener/network path, dependency health, and relevant metrics.
5. **Test one hypothesis:** Choose the narrowest reversible check. Avoid changing several variables at once.
6. **Recover and verify:** Use the original failing user-facing signal plus dependency/data checks. Confirm persistence where relevant.
7. **Prevent recurrence:** Record timeline, root cause, contributing factors, action owners, and measurable follow-up. Keep the review blameless.

For coding or command questions, explain the input, expected output, failure modes, safety boundaries, and how you would verify it. If you do not know a detail, say what you know, what you would check, and how you would test it safely.

## How each project day prepares you

| Stage | Build and learning | Interview practice |
|---|---|---|
| Day 1 — service baseline | Linux host, Java/Spring API, PostgreSQL, Compose-managed database, HTTP/SQL checks, process outage diagnosis | Explain request-to-row flow; distinguish process, port, API health, DB readiness, tests, and persistence |
| Day 2 — container delivery | Multi-stage Dockerfile, non-root runtime, build context, Compose DNS/ports, persistent volume, image and container operations | Write Dockerfile from memory; explain localhost versus service DNS; diagnose build/startup/DB connection issues |
| Day 3 — observability | Instrument API metrics, Prometheus scrape, Grafana dashboard, missing-target drill | Explain metric types, labels, scrape health, RED/USE signals, dashboard usefulness, and alert limitations |
| Later SRE stages | SLIs/SLOs, burn rate, alerting, incident response, postmortem, capacity, resilience, DR, toil reduction, cost | T2 scenarios, reliability/system design, incident simulation, manager/client handoff |
| Later DevOps/DevSecOps stages | CI/CD, Git branching/review, Terraform, AWS networking/IAM, Kubernetes, scanning, secrets, release strategies | Pipeline/IaC design, security gates, deployment/rollback practical, change governance |

The exact sequence may change as prerequisites and evidence dictate. Every stage must include a fresh rebuild, recall practice, at least one performed troubleshooting drill, interview questions with answer reasoning, and an honest evidence/limitations record.

## Daily readiness gate

Before marking a day Done, the report must show:

- What was assigned, who owns each layer, and why the change matters to reliability or delivery.
- A beginner explanation of every new term and a request/data/telemetry path connecting the components.
- Commands used with purpose, when to use them, expected evidence, observed result, and what the result does not prove.
- A clean rebuild or explicit explanation of what could not be rebuilt; tests and runtime checks are labeled separately.
- At least one actually performed failure drill, with symptom, impact, evidence, diagnosis, safe change, recovery proof, and prevention. Unperformed scenarios stay labeled **planned**.
- Interview questions at recall, explain, debug, trade-off, and design depth, with answers that use only observed evidence.
- What remains weak, what to repeat next day, and cleanup/cost status.

A green dashboard, successful build, or correct memorized answer alone is not mastery. Mastery means being able to recreate the relevant files and environment, predict likely failures, gather evidence, restore service safely, explain trade-offs, and communicate the result without notes.

## How to use this file

Use [QUESTION_BANK.md](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/interview/QUESTION_BANK.md) for the growing question set. Each day’s prework is for concepts and video learning; each `PROJECT_DAY_NN.md` is the single execution/evidence record for that day. Use the [ticket index](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/tickets/README.md) to check status. Day 1 and Day 2 are recorded Done; LAB-003 remains Ready until its build and evidence are actually completed.
