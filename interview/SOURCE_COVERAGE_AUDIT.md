# Interview Source Audit and Coverage Plan

**Reviewed:** 2026-10-06  
**Scope:** Public interview repositories already named in this lab's resource map. This file records what was inspected, how we use it, and the gaps to close by day. It is a coverage tracker, not a promise that every interviewer question on the internet has been found.

## Why this audit exists

Interview practice follows the system we build and operate. Each lab day gets stable question IDs, beginner explanations, command interpretation, practical file-writing, troubleshooting, logical follow-ups, team ownership, and evidence-based answer outlines. We revisit prior days before adding new questions.

We use outside repositories to find recurring question patterns. We write original questions and explanations here, then verify technical claims against primary/vendor documentation and actual lab evidence. We do not copy whole question banks or claim a prompt was hands-on unless its drill was run and recorded.

## Source inventory and reviewed scope

| Source | Categories reviewed | How it feeds preparation | Review depth |
|---|---|---|---|
| [michaelkkehoe/sre-interview](https://github.com/michaelkkehoe/sre-interview) | Culture/soft skills, incident response, Linux/systems administration, networking, observability, security, system design, questions for interviewers. | SRE fundamentals, incident reasoning, Linux/network checks, observability, security, design, and manager/behavioral discussion. | README taxonomy reviewed; selected category files considered for Day 1–3 mapping. Not every answer audited. |
| [balajisa09/sre-interview-preparation](https://github.com/balajisa09/sre-interview-preparation) | Common rounds: coding/scripting, Linux, system design, troubleshooting, tools; log parsing, networking, performance, monitoring design. | Add Bash/Python/log parsing, Linux performance, incident stories, monitoring/logging design, and tool internals to later stages. | README and topic outline reviewed; linked resources are not all independently verified. |
| [Techikrish/devops-cloud-interview-scenarios](https://github.com/Techikrish/devops-cloud-interview-scenarios) | README lists 11 domains and 906 scenarios in its domain table: Docker, Linux/SRE, observability, networking, security, AWS, Kubernetes, Terraform, CI/CD, Git, general DevOps. Docker file topics reviewed include lifecycle, networking, multi-stage builds, non-root, health, BuildKit, logs, resource limits, scanning, and failure diagnosis. | Main scenario-prompt source. Use foundational and mid-level prompts now; schedule advanced topics with their build days. | README/domain inventory and a focused Docker sample reviewed. The full 906-scenario corpus was not read question by question. README headline/count wording may vary; the count is not a completeness guarantee. |
| [hammadhaqqani/devops-interview-handbook](https://github.com/hammadhaqqani/devops-interview-handbook) | README indexes AWS, Terraform, Kubernetes, CI/CD, networking, Linux and security Q&A; deployment/incident scenarios; HA app, pipeline and multi-region DR architecture exercises. | Mid-level trade-offs, cloud, IaC, security, architecture, incidents, and cross-team operations. | README/navigation and topic categories reviewed. Individual topic answers and all scenario files have not been audited. |
| [Google SRE book](https://sre.google/sre-book/table-of-contents/) and [Google SRE Workbook](https://sre.google/workbook/table-of-contents/) | Primary SRE principles and operational practices. | Validate reliability, SLI/SLO, incident response, postmortems, toil, monitoring, and rollout reasoning. | Consult the primary reference relevant to each question; it does not replace lab evidence. |
| Official vendor docs linked from each day | Product behavior for Docker, Spring, Maven, AWS, Prometheus, Grafana, and other tools. | Resolve outdated or context-dependent community answers. | Check the page relevant to the question and record the exact link. |
| User-shared PDFs/ZIP, command handbook, resume, JDs, and creator material | Beginner explanations, commands, tool topics, role expectations, and examples. | Daily prework, resume/JD mapping, and commands used in our lab. | Their intended use is mapped in the repo. Every page of every shared file has not been extracted and audited. |

## Coverage by day and question ID

| Day | Interview scope and evidence | Current questions | Source categories checked | Remaining gap / next action |
|---|---|---|---|---|
| Day 1 — Linux/host baseline, Java/Spring REST API, Maven, PostgreSQL, Compose database, incident basics | Request-to-row flow; HTTP codes/validation; tests versus live checks; process/listener/dependency triage; health-check/volume limits; rebuild; persistence proof; ticket update/ownership. Evidence is in the Day 1 report and observed controlled API-stop drill. | [D01-INT-001–007](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/interview/QUESTION_BANK.md#day-1--linux-host-spring-api-docker-compose-postgresql) | SRE Linux, networking, incident response, culture/communication; troubleshooting and tools; DevOps Linux/networking/incident scenarios. | Refresh answers and rebuild from clone. Later add log parsing, Linux performance, DNS/TCP depth and scripting when built/practiced. |
| Day 2 — Dockerfile, multi-stage build, non-root runtime, image context, Buildx, Compose DNS/ports/health | Host loopback versus container loopback; Compose DNS; EXPOSE versus publishing; dockerignore; image identity; build-prerequisite triage; service-name typo. Buildx mismatch and typo are observed lab events; other failure cases remain practice prompts. | [D02-INT-001–007](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/interview/QUESTION_BANK.md#day-2--docker-image-runtime-security-and-compose-networking) | Docker scenarios on lifecycle, network, multi-stage, non-root, health, build and troubleshooting; DevOps Docker/network/security/scenario categories. | Add refresh prompts for process/health distinction, OOM/exit interpretation, resource limits, graceful stop/signals, safe disk cleanup, and image/logging security. Mark not practiced until executed. |
| Day 3 — application metrics, Prometheus, Grafana | Counters/gauges/histograms, labels/cardinality, scrape targets, PromQL, dashboards, alert design, service health versus user-facing SLI. | Add D03 IDs after the build. | SRE observability/monitoring; observability scenarios listed by the DevOps scenario source; monitoring-design prompts in the SRE prep source. | Inspect selected prompts, build signals/dashboard, run safe drills, and add original Q/A with evidence and Prometheus/Grafana documentation. |
| Later — logs/traces, SLO/error budgets/on-call, scripting, CI/CD, Terraform/AWS, Kubernetes, DevSecOps, DR and FinOps | Match to resume and target JDs; include technical, practical, scenario, client/operations, and manager/behavioral formats. Interview stages vary by employer. | Add stable Dxx-INT-nnn IDs as each stage is built. | Linked SRE and DevOps sources cover these categories; select relevant prompts when reaching that stage. | Keep gaps for coding, architecture, production debugging, communication, change/rollback, security, cost, and ownership; assess via mock rounds after related project work. |

## Day 2 source-driven refresh prompts

These original prompts are inspired by Docker/SRE source categories. They are **not evidence of failures already seen in our lab**:

1. **Container says Up but API fails:** what does container state prove? Inspect logs, configured health signal, PID/process and listener. What is missing when the service has no explicit application health check?
2. **Exit code 137 / suspected OOM:** what does 137 tell you and not tell you? Check inspect state, memory limits, host/kernel evidence and recent logs before concluding OOM.
3. **CPU/memory pressure:** establish usage and limits, determine whether process or host is constrained, and choose a safe temporary mitigation versus reviewed configuration change.
4. **Graceful stop:** explain SIGTERM, timeout, SIGKILL, PID 1 and exec; what would logs and restart behavior show?
5. **Disk pressure from image builds:** inspect usage and identify safe cleanup candidates first. Do not reflexively prune broadly on a shared/production host; volumes can contain data.
6. **Host port/security boundary:** why is the API bound to host loopback in this lab? If an external client cannot reach it, confirm intended design before changing bind address/security groups; production exposure belongs behind intended ingress/load-balancer controls.
7. **Container logs/image security:** explain stdout/stderr collection, bounded logs, secret exposure through inspect/build layers, and where image scanning/gates fit in CI.

When incorporated as D02 entries, each needs an answer outline, command/expected evidence, follow-up, source link, and status: **observed**, **practiced**, **planned**, or **not practiced**. Do not turn a hypothetical into a claimed incident.

## Daily source-coverage procedure

1. Review that day's files and evidence; list changed tools and behaviors.
2. Search mapped source categories for foundational, mid-level, and scenario prompts on those topics.
3. Deduplicate into original stable-ID questions; do not import giant copied answer lists.
4. Cover knowledge, command interpretation, practical file-writing, troubleshooting, trade-offs, design, ownership, and communication where relevant.
5. Link each question to the daily report and a primary/vendor reference.
6. Track readiness per question; revisit missed answers and rebuild the previous day before advancing.
7. At milestones, run mock technical, troubleshooting, practical, and behavioral/manager rounds. This trains common assessment modes; it cannot predict every employer's loop.

## Source links

- [SRE interview categories](https://github.com/michaelkkehoe/sre-interview)
- [SRE interview preparation and round categories](https://github.com/balajisa09/sre-interview-preparation)
- [DevOps/cloud scenario index](https://github.com/Techikrish/devops-cloud-interview-scenarios)
- [Docker scenario file](https://github.com/Techikrish/devops-cloud-interview-scenarios/blob/main/docker/scenarios.md)
- [Linux/SRE scenario file](https://github.com/Techikrish/devops-cloud-interview-scenarios/blob/main/linux-sre/scenarios.md)
- [Observability scenario file](https://github.com/Techikrish/devops-cloud-interview-scenarios/blob/main/observability/scenarios.md)
- [DevOps interview handbook](https://github.com/hammadhaqqani/devops-interview-handbook)
- [Google SRE book](https://sre.google/sre-book/table-of-contents/)
- [Google SRE Workbook](https://sre.google/workbook/table-of-contents/)
