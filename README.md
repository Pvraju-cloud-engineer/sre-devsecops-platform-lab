# SRE + DevOps + DevSecOps Platform Lab

A hands-on learning repository for building and operating a small financial-services-style platform from the first service to a distributed, observable, resilient system. The project is generic and uses no client or employer names, production credentials, or private infrastructure details.

## Learning goals

- Build Java/Spring Boot services and understand their request, persistence, and failure paths.
- Package services as secure, repeatable Docker images and publish versioned images to Docker Hub.
- Automate build, test, security scanning, and deployment with CI/CD.
- Provision cloud infrastructure with Terraform and operate workloads on AWS.
- Progress from a single EC2-hosted service to containers, Kubernetes, managed data services, queues, and observability.
- Practice SRE operations: SLIs/SLOs, alerting, incident response, runbooks, capacity, cost, recovery, and blameless reviews.

## Resume-to-lab roadmap

The lab progresses from fundamentals to a multi-service, multi-tenant platform. Each stage includes a build, verification, a controlled troubleshooting exercise, operator documentation, and interview practice. Multi-tenancy is a core requirement: we will implement tenant-aware authorization and data isolation, then carry tenant context through queues, logs, metrics, traces, dashboards, and incident response.

See [the complete resume-to-lab roadmap](docs/RESUME_TO_LAB_ROADMAP.md) for the staged plan, resume topic checklist, daily working loop, and completion evidence.

## Daily working model

Each day starts with recall and reference study, then one assigned work ticket. We build and explain a small increment, verify it from observed evidence, reproduce a safe failure, diagnose and recover, document the result in `docs/days/PROJECT_DAY_NN.md`, and clean up temporary cloud resources. Topics are marked planned, in progress, practiced, or demonstrated based on actual work.
