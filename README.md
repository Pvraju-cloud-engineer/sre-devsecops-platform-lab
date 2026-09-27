# SRE + DevOps + DevSecOps Platform Lab

A hands-on learning repository for building and operating a small financial-services-style platform from the first service to a distributed, observable, resilient system. The project is generic and uses no client or employer names, production credentials, or private infrastructure details.

## Learning goals

- Build Java/Spring Boot services and understand their request, persistence, and failure paths.
- Package services as secure, repeatable Docker images and publish versioned images to Docker Hub.
- Automate build, test, security scanning, and deployment with CI/CD.
- Provision cloud infrastructure with Terraform and operate workloads on AWS.
- Progress from a single EC2-hosted service to containers, Kubernetes, managed data services, queues, and observability.
- Practice SRE operations: SLIs/SLOs, alerting, incident response, runbooks, capacity, cost, recovery, and blameless reviews.
- Keep a daily engineering record with commands, decisions, debugging evidence, and interview practice.

## Project approach

The platform grows in deliberate stages. Each stage starts with a small working baseline, then introduces one operational capability and a controlled failure exercise. Cloud resources are created only when needed, cost-checked, and decommissioned after the lab. Keep credentials in local environment variables or a secret manager; never commit secrets, account identifiers, private keys, customer data, or real incident records.

## Daily work loop

1. Review yesterday's architecture, commands, and lessons from memory.
2. Watch the day's selected reference material and record questions.
3. Read the work assignment and acceptance criteria.
4. Build or change one small slice; explain why each file and command exists.
5. Verify with a test or observable signal.
6. Inject a safe failure, diagnose from evidence, recover, and document the runbook.
7. Capture trade-offs, interview questions and answers, and next-day handoff.
8. Commit the notes and code; destroy temporary cloud resources and verify cleanup.

## Repository map

- `docs/REALTIME_PROJECT_WORK_MODEL.md` — simulated team roles, ticket flow, reviews, release and incident workflow.
- `docs/architecture/target-platform.md` — staged target architecture and data/telemetry paths.
- `docs/days/PROJECT_DAY_01.md` — Day 1 baseline, commands, troubleshooting, and interview review.
- `docs/days/DAY_02_PREWORK.md` — preparation checklist for the next build session.
- `services/README.md` — service boundaries and application conventions.
- `ops/postgres/README.md` — local database lab and safe persistence notes.
- `runbooks/README.md` — operational runbook index and incident template.

## Status

Day 1 established a Java/Spring Boot claims API baseline on an EC2 lab host and a PostgreSQL container with a named persistent volume. The API was started and queried locally; a claim was created and then confirmed in PostgreSQL. The subsequent days will rebuild the progression in this repository as reproducible files, scripts, and notes rather than relying on shell history.

## Working safely

- Use a dedicated personal AWS lab account and region.
- Create a budget and alerts before provisioning. Free tier does not guarantee zero charges.
- Restrict SSH to your current public IP; do not expose PostgreSQL publicly.
- Stop or terminate compute and remove unused storage, load balancers, snapshots, and NAT resources after exercises.
- Use synthetic data only.
- Never put passwords, tokens, SSH keys, `.env` files, Terraform state, or real customer/incident data in Git.

## How to start

Start with `docs/days/PROJECT_DAY_01.md`, then follow the daily work loop. Each day should end with a working increment, a verification result, a troubleshooting scenario, and an interview-ready explanation.
