# Zelevate SRE / DevOps Interview — 5-Day Crash Course

**Target:** Zelevate listing for a 2–5 year Site Reliability Engineer role. The details emphasize AWS, ECS Fargate, Terraform or CloudFormation, Jenkins, scripting, CloudWatch or Datadog, incident/RCA, application performance, WAF and GuardDuty. The listing metadata also labels it DevOps Engineer, so clarify the actual title and team scope.

**Goal:** Prepare to explain your real production work, reason through likely troubleshooting questions, and connect it to your personal lab. Five days can sharpen readiness; they cannot replace production experience or guarantee an offer. Keep past production examples distinct from lab practice.

**Daily rhythm (4–5 focused hours):** 30 min closed-book recall; 60–90 min selected video/docs; 90 min diagrams or hands-on practice; 60 min timed troubleshooting/interview drill; 30 min write answers and gaps.

**Cost and account rule:** Most exercises below are read-only, diagram-based, or local. Do not create AWS resources just to follow a video. ECS, NAT gateways, GuardDuty and other services may incur charges. Check current pricing and your account budget before provisioning. Use only your personal account; never use Cisco/work credentials.

## Day 1 — SRE incident response, Linux and Java service triage

### Learn
- Incident lifecycle: detect/acknowledge, scope user impact, assign incident roles, communicate, mitigate, verify, resolve, then track problem follow-ups. Incident response restores service; problem management prevents recurrence.
- SLI is a measured service indicator, such as successful requests divided by valid requests. SLO is the target over a window. Error budget is the unreliability allowed by that target.
- Golden signals: latency, traffic, errors, saturation. Host reachability or HTTP 200 from health does not prove every business flow or batch is healthy.
- Evidence order: timestamp/scope → process → listener → logs → resources → dependency/network → recent change. Know what each check can and cannot prove.

### Practice
Draw client → API → PostgreSQL for the personal lab. Rehearse the observed API-stopped symptom: curl fails while DB can be healthy; inspect process/listener/logs, restart only after diagnosis, and retest the failed signal. Tabletop a 5xx/latency alert and write a concise incident update.

Explain these commands: date -Is, whoami, id, hostname, cat /etc/os-release, uptime, free -h, df -h, ps -ef, ss -lntp, journalctl, tail, curl -i, docker compose ps, docker compose logs. Be able to state the purpose, expected evidence, and limits of each.

### Interview drills
1. Health URL refuses connections but PostgreSQL is healthy. What do you check, and why in that order?
2. HTTP 200 returns but users report failed transactions. What other signals and user journeys do you check?
3. Tell me about a real incident: impact, timeline, your actions, verification, and follow-up. Use a true work example. Label the lab drill as lab practice.

### Watch / read
- [Google SRE Book: Being On-Call](https://sre.google/sre-book/being-on-call/)
- [Google SRE Book: Monitoring Distributed Systems](https://sre.google/sre-book/monitoring-distributed-systems/)
- Review docs/days/PROJECT_DAY_01.md for the lab request path and outage drill.

**Deliverable:** One-page incident timeline and separate 60-second real-production and lab explanations.

## Day 2 — AWS network path and ECS Fargate operations

### Learn
- VPC/CIDR → public/private subnets → route tables → internet gateway/NAT → security groups and network ACLs → DNS/Route 53 → load balancer → task.
- ECS concepts: cluster, task definition (container blueprint), task, service (desired count/replacement), deployment, task execution role vs task role, ECR image, health check, CloudWatch logs.
- Fargate reduces EC2 host management, not networking, IAM, image-pull, health-check or application troubleshooting. Fargate tasks use awsvpc networking and receive ENIs; check subnet routes, security groups, DNS and egress when tasks cannot start or reach dependencies.
- Compare ECS Fargate with EKS: ECS uses AWS task/service primitives; EKS exposes Kubernetes APIs and ecosystem. State accurately which you operated and which you are studying.

### Practice
Draw inbound and outbound paths for a public API with private Fargate tasks. Tabletop: ECR image pull denied; task runs but target health fails; app cannot reach PostgreSQL; DNS resolves an old address; private task lacks egress. Say what evidence you gather before changing configuration.

### Interview drills
1. Fargate task cannot pull from ECR: check execution role, subnet egress/endpoints, DNS, ECS events and logs.
2. Explain security group vs NACL with a failure example.
3. Container health passes but load balancer health fails. What configuration and evidence do you compare?
4. When does localhost work between containers in one task, and why not usually between separate tasks?

### Watch / read
- [AWS Networking Foundations: VPC to Hybrid](https://aws.amazon.com/video/watch/93425b7df63/)
- [AWS re:Invent: ECS on Fargate deep dive with Affirm](https://www.youtube.com/watch?v=Hq5cUQ1rLMM)
- [ECS Fargate task networking](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/fargate-task-networking.html)
- [ECS Fargate getting started](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/getting-started-fargate.html)

**Deliverable:** Annotated request-path diagram and evidence checklist for the five failures. No cloud deployment required.

## Day 3 — Terraform / CloudFormation, Jenkins and scripting

### Learn
- Terraform: providers, resources, variables, outputs, modules, state, plan/apply/destroy, drift and locking. Plan previews intended changes; it does not prove apply will succeed. State can contain sensitive values and requires access control. Review plans before apply.
- CloudFormation: templates, parameters, resources, outputs, change sets, stacks and drift detection. Compare AWS-native lifecycle with Terraform provider/module/state workflows.
- Jenkins: controller/agent, SCM trigger, Jenkinsfile, stages, credentials binding, artifacts, approvals and deployment. Keep secrets in credentials storage, never source or console output.
- Safe Bash/Python operations automation: validate inputs, timeouts, idempotency, exit codes, structured logs, least privilege, dry-run, tests and rollback.

### Practice
Review on paper or a local sample a Terraform change to a security group or task count. Identify blast radius, replacement/deletion, state/backend concern and rollback. Sketch Jenkins stages: checkout → test → scan → build/tag → publish → approval → deploy → smoke check/rollback. No AWS credential in source.

### Interview drills
1. Terraform says no changes but console differs. What could explain it and how do you investigate safely?
2. Terraform partially fails. What do you inspect and why avoid hand-editing state first?
3. Jenkins is green but the service is down. Which gates or post-deploy checks are missing?
4. What makes an incident remediation script safe to run?

### Watch / read
- [HashiCorp Terraform tutorials](https://developer.hashicorp.com/terraform/tutorials)
- [Jenkins Pipeline documentation](https://www.jenkins.io/doc/book/pipeline/)
- [Jenkins tutorials](https://www.jenkins.io/doc/tutorials/)
- [HashiCorp: Terraform in automation](https://developer.hashicorp.com/terraform/tutorials/automation/automate-terraform)

**Deliverable:** Reviewed sample plan and verbally explained Jenkins pipeline, including risks and rollback.

## Day 4 — CloudWatch / Datadog, performance, WAF and GuardDuty

### Learn
- CloudWatch metrics, dimensions, logs, dashboards, alarms, actions and retention. Some EC2 memory/filesystem metrics require an agent; do not assume they are always collected automatically.
- Datadog concepts: integrations/agents, metrics, logs, traces, tags, monitors and service dashboards. Translate concepts, but do not claim tools are identical.
- Performance triage: define operation and time window; compare latency percentiles, throughput, errors, CPU/memory, connection pool/DB waits, dependency latency and recent changes. Locate the bottleneck before tuning.
- WAF applies web ACL rules to requests; consider false positives, rule order/scope, logging and safe count-mode testing. GuardDuty produces security findings from AWS signals; triage severity, identity/resource, timeline, preserve evidence and engage security. Neither tool alone proves an application incident is resolved.

### Practice
Design an API dashboard: rate, error rate, p50/p95/p99 latency, saturation, DB/dependency health, deployment marker, alarm state. Tabletop high latency and suspicious role activity. Identify what evidence to preserve, who owns containment, and how to check customer impact. Do not enable paid cloud features as an interview exercise.

### Interview drills
1. Alert on user impact without paging for every CPU spike?
2. p99 is high while average latency is normal. What may that indicate and what next?
3. WAF blocks valid requests after a release. How do you limit impact and safely adjust a rule?
4. GuardDuty reports suspicious access to a workload role. What do you do first and who owns containment?

### Watch / read
- [AWS CloudWatch best-practice alarms video](https://www.youtube.com/watch?v=E2gAm12Ldq0)
- [CloudWatch alarm documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch_Alarms.html)
- [AWS WAF getting started](https://docs.aws.amazon.com/waf/latest/developerguide/getting-started.html)
- [GuardDuty getting started and sample findings](https://docs.aws.amazon.com/guardduty/latest/ug/guardduty_settingup.html)

**Deliverable:** Dashboard sketch, three symptom-focused alert definitions, and short WAF/GuardDuty triage notes.

## Day 5 — Mock interview and closeout

### Round A: Resume and behavioral, 30 min
Prepare truthful stories for incident ownership, on-call handoff, automation, cross-team work, release/change, and a mistake/lesson. Structure: Situation → impact → your actions → observable result → follow-up. Do not invent metrics or imply lab was production.

### Round B: Live troubleshooting, 30 min
Scenario: after deployment, intermittent timeouts; some ECS tasks restart; CPU is moderate; DB connections rise. Talk aloud: scope/time window, version/change, service/task events and logs, target health, latency/errors, ENI/subnet/SG path, pool/DB waits. Choose safe mitigation (rollback, scale within dependency capacity, or route traffic); communicate and verify recovery.

### Round C: System design, 30 min
Whiteboard: Route 53 → WAF/ALB → ECS Fargate across AZs in private subnets → data dependency. Show IAM roles, routes/NAT or required endpoints, telemetry/alarms, deploy rollback and backup/recovery boundary. Name questions for app, security, database and network teams.

### Rapid-fire
- ECS task vs service vs task definition?
- Fargate vs EKS/EC2 containers?
- Route table vs security group vs NACL?
- Task execution role vs task role?
- What can HTTP 200 health fail to prove?
- What does Terraform plan tell you and not tell you?
- Alarm fires but user impact is unclear: what next?
- What evidence closes an incident?
- When should GuardDuty findings enter the security incident process?
- How would you reduce toil without creating an unsafe one-click production tool?

### Ask the interviewer
What is the first 90-day reliability priority? Is production ECS/Fargate or EKS? What is the on-call model and incident authority? Which IaC and observability tools are used? How are releases and rollback approved? What does success look like?

**Readiness gate:** Explain request path without notes; troubleshoot one Linux/app and one AWS network/container case; review a sample IaC change; sketch useful alerts; tell a real production story and separate lab story; name gaps clearly.

## Resume-to-JD coverage tracker

| JD requirement | Day | Lab connection | Current status |
|---|---|---|---|
| Incident/RCA/prevention | 1, 5 | Day 1 outage drill; Day 2 container checks | Lab evidence exists; recall/mock remains |
| Linux, scripting, toil | 1, 3 | EC2 baseline and bootstrap scripts | Partial hands-on; Python support tool remains future practice |
| EC2/VPC/Route 53/S3 | 2 | EC2 lab; diagram/network review | JD-specific review required |
| ECS Fargate/Docker | 2 | Day 2 Docker + Compose, not ECS production | Fargate tabletop/comparison until separately practiced |
| Terraform/CloudFormation | 3 | Later roadmap | Not implemented in this interview sprint |
| Jenkins/GitHub Actions | 3 | Later roadmap | Study/mock only until built |
| CloudWatch/Datadog/performance | 4 | Future observability stage | Tool-specific practice required |
| WAF/GuardDuty/security tooling | 4 | DevSecOps roadmap | JD-specific triage remains to practice |

This is an interview plan, not a claim these skills have been built or used in production. Each day, record date, recall score, drill evidence, weak areas and next action on this branch.
