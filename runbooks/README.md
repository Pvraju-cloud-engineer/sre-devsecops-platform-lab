# Runbooks

A runbook should help an engineer make a safe, evidence-led change under pressure. Keep steps specific to the deployed version, state preconditions and access needed, avoid secrets, and test the procedure periodically.

## Runbook index

- API unavailable / health check failing: identify scope, process, listener, dependencies, recent change, mitigation, and recovery proof.
- Elevated latency or errors: inspect request rate, latency percentiles, status codes, saturation, dependency timing, and deployment changes.
- PostgreSQL unavailable or connection pool exhausted: check container/service readiness, network path, credentials/config, connection counts, limits, and recovery behavior.
- Queue backlog growing: inspect producer rate, visible/in-flight messages, oldest message age, consumer errors/concurrency, retry/DLQ behavior, and scaling limits.
- Failed release: stop rollout, compare version/config, inspect health and user-facing errors, roll back to last known good, and verify.
- Lab cost cleanup: inventory tagged resources, stop/terminate planned compute, remove disposable supporting resources, and confirm no chargeable leftovers.

## Incident record template

### Summary

- Date/time range and timezone:
- Service and version:
- Severity and customer impact:
- Detection source:
- Incident lead / responders:

### Timeline (UTC)

| Time | Observation or action | Evidence / outcome |
| --- | --- | --- |
| | | |

### Diagnosis and recovery

- User-visible symptom:
- Scope and affected operations:
- Signals checked (metrics, logs, traces, health, dependency):
- Hypotheses considered and evidence for/against:
- Mitigation and why it was chosen:
- Recovery signal and duration observed:
- Data integrity check:

### Follow-up

| Action | Owner | Priority | Due date | Status |
| --- | --- | --- | --- | --- |
| | | | | |

Write blamelessly. Distinguish confirmed facts from hypotheses. Store only sanitized synthetic lab evidence in this public repository; do not add employer/customer incident details, private logs, identifiers, or credentials.

## Generic service-unavailable checklist

1. Confirm alert is current and whether users are affected; note when it started.
2. Check health endpoint, process state, listening port, recent deploy/config changes, and host resource pressure.
3. Check each dependency separately: DB readiness and connectivity, queue status, DNS, and upstream health.
4. Compare metrics for request rate, errors, latency, saturation, and dependency timing; inspect correlated logs and traces.
5. Choose the smallest reversible mitigation that reduces user impact. Record who approved or executed it.
6. Verify user path and data integrity; keep observing for recurrence.
7. Write the timeline, root cause only when evidence supports it, and tracked follow-up actions.
