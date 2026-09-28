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

## Resume-to-lab roadmap

The lab progresses from fundamentals to a multi-service, multi-tenant platform. Each stage includes a build, verification, a controlled troubleshooting exercise, operator documentation, and interview practice. Multi-tenancy is a core requirement: we will implement tenant-aware authorization and data isolation, then carry tenant context through queues, logs, metrics, traces, dashboards, and incident response.

See [the complete resume-to-lab roadmap](docs/RESUME_TO_LAB_ROADMAP.md) for the staged plan, resume topic checklist, daily working loop, and completion evidence.

## Daily work loop

1. Review yesterday's architecture, commands, and lessons from memory.
2. Watch the day's selected reference material and record questions.
3. Read the work assignment and acceptance criteria.
4. Build or change one small slice; explain why each file and command exists.
5. Verify with a test or observable signal.
6. Inject a safe failure, diagnose from evidence, recover, and document the runbook.
7. Capture trade-offs, interview questions and answers, and next-day handoff.
8. Commit the notes and code; destroy temporary cloud resources and verify cleanup.

## Daily working model

Each day starts with recall and reference study, then one assigned work ticket. We build and explain a small increment, verify it from observed evidence, reproduce a safe failure, diagnose and recover, document the result in `docs/days/PROJECT_DAY_NN.md`, and clean up temporary cloud resources. Topics are marked planned, in progress, practiced, or demonstrated based on actual work.

## Repository map

- `docs/tickets/README.md` — clickable ticket status and troubleshooting index; each entry links to the canonical daily evidence.
- `docs/RESUME_TO_LAB_ROADMAP.md` — staged plan mapping resume skills to build evidence, including multi-tenancy.
- `docs/REALTIME_PROJECT_WORK_MODEL.md` — simulated team roles, ticket flow, reviews, release and incident workflow.
- `docs/architecture/target-platform.md` — staged target architecture and data/telemetry paths.
- `docs/days/PROJECT_DAY_01.md` — Day 1 baseline, commands, troubleshooting, and interview review.
- `docs/days/DAY_02_PREWORK.md` — preparation checklist for the next build session.
- `services/README.md` — service boundaries and application conventions.
