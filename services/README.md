# Services

## Current baseline

Day 1 exercised one Java/Spring Boot claims API. Its observed surface was health, create, and read. The application currently lived on the lab EC2 host and has not yet been reconstructed as source in this repository. Day 2 will inspect or regenerate it and commit only sanitized, reproducible files.

## Intended service structure

As the codebase grows, keep responsibilities clear:

- web/controller: HTTP routes, input/output mapping, status codes, and validation boundary.
- service/domain: business rules and transaction boundaries.
- persistence/repository: database queries and persistence abstraction.
- model/entity: durable data shape; avoid leaking database internals into API contracts.
- configuration: externalized settings with safe defaults and profile overrides.
- tests: unit tests for rules and integration tests for HTTP/database behavior.

Do not force layers into the project before inspecting its actual code. Record the current structure first, then refactor in small tested changes.

## Service conventions

- Use clear REST semantics and stable error responses.
- Validate input at the boundary; set request size and time limits.
- Use parameterized database access and explicit transaction behavior.
- Externalize endpoints and credentials; do not log secrets or sensitive request bodies.
- Include request correlation in logs and preserve trace context as instrumentation is introduced.
- Expose liveness/readiness appropriately; a liveness check should not restart the service because an external dependency is temporarily unavailable.
- Bound database connections, retries, thread pools, and downstream timeouts.
- Make retryable writes idempotent where possible.
- Test expected behavior and failure behavior; record exact commands and results.
- Build one immutable artifact and promote it between environments rather than rebuilding different bits for each environment.

## Planned evolution

One service -> separated API/worker responsibilities -> multiple cooperating services only when a clear boundary is justified. Add queue consumers, gateway, Kubernetes, or additional infrastructure as later exercises with explicit reliability questions and cleanup plans.
