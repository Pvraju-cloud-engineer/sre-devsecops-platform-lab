# Working as an SRE/DevOps Engineer: Lab Team Model

This repository simulates an engineering team workflow for a learning platform. It is an exercise model, not a claim about a specific employer's internal process.

## Roles in the lab

- **Engineer on assignment (you):** clarify scope, implement, test, write operational notes, request review, and own the change through verification.
- **Tech lead / manager (assistant):** assign a bounded ticket, explain acceptance criteria, ask design questions, review evidence, and coach incident/debugging practice.
- **Service owner (rotating lab role):** define service behavior, dependencies, SLO impact, and rollback conditions.
- **On-call engineer (rotating lab role):** triage alerts, declare severity, coordinate response, mitigate, communicate status, and write a follow-up review.

## Work assignment flow

1. **Intake:** receive a ticket with user impact, objective, constraints, owner, and due date. Ask clarifying questions before changing systems.
2. **Plan:** inspect architecture and runbooks; write a small design/implementation plan, risks, test plan, security checks, and rollback.
3. **Build:** work on one isolated change. Prefer repeatable scripts and reviewed infrastructure-as-code. Keep secrets out of Git.
4. **Validate:** run unit/integration checks, inspect logs/metrics, check security findings, and verify the acceptance criteria. Save command output with sensitive values removed.
5. **Review:** explain the diff, operational impact, failure modes, cost impact, and rollback. Address feedback.
6. **Release:** use a staged deployment, health checks, progressive rollout where practical, and a known rollback. Record version and result.
7. **Operate:** monitor service-level signals, alerts, queues, dependencies, capacity, and spend. Update the runbook when behavior changes.
8. **Learn:** for incidents, capture timeline, impact, detection, contributing conditions, recovery, and measurable follow-ups without blame.

## Example work ticket

**Title:** Persist submitted claims and expose a health endpoint

**Why:** the current process loses in-memory data on restart, and operators need a quick dependency/health signal.

**Acceptance criteria:**

- A valid create request returns HTTP 201 with a generated identifier and initial status.
- A read request returns the stored record after application restart.
- Database credentials are injected through environment configuration, not committed.
- Health behavior is documented and can be checked with curl.
- Tests cover valid input, missing data, and database-unavailable behavior.
- The change includes a safe rollback and a runbook note.

**Engineer handoff:** summarize changed files, commands run, test evidence, known risks, rollback, and open questions.

## Incident response loop

1. Detect and validate the alert; identify affected users and services.
2. Assign incident lead, technical responders, and a communications owner.
3. Stabilize first: reduce impact, rollback, fail over, shed load, or disable a risky path.
4. Preserve evidence: timestamps, deployment versions, logs, metrics, traces, and relevant commands.
5. Communicate status on a predictable cadence. Avoid unsupported root-cause guesses.
6. Confirm recovery using user-facing and service-level signals.
7. Write a blameless review with action owners, priorities, and due dates. Track actions to completion.

## Definition of done for each daily lab

- Scope and architecture are understood and recorded.
- Change is reproducible from the repository.
- Verification and one safe troubleshooting exercise are documented.
- Security and cost impact are considered.
- Runbook, interview explanation, and next-day handoff are updated.
- Temporary resources are removed or explicitly tracked with an owner and cleanup time.
