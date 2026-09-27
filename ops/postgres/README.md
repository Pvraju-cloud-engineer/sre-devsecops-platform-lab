# PostgreSQL Lab Operations

## Day 1 configuration observed

- Image: postgres:16-alpine
- Container name: claims-db
- Database and user: claims
- Published host port: 127.0.0.1:5432 mapped to container port 5432
- Named volume: claims-db-data mounted at /var/lib/postgresql/data
- Restart policy: unless-stopped

The database stayed host-local; do not open 5432 to the public internet. The Day 1 password was a lab-only value passed at container creation. Do not repeat it in Git. The current container initialization is not a production secrets pattern.

## Read-only checks

    docker ps --filter name=claims-db
    docker logs --tail 50 claims-db
    docker exec claims-db pg_isready -U claims -d claims
    docker exec claims-db psql -U claims -d claims -c "SELECT id, description, status, created_at FROM claims;"
    docker inspect claims-db
    docker volume inspect claims-db-data

The last two commands can reveal configuration details; redact before sharing. Never paste passwords, environment dumps, or account/host identifiers into public issues.

## Concepts to know

- Image is the template; container is a running instance.
- A published port maps host networking to a container port. Binding to loopback limits remote access through that mapping.
- A named volume is managed Docker storage that persists independently of the container lifecycle.
- docker exec runs a command inside an existing container; it does not make PostgreSQL public.
- pg_isready checks whether PostgreSQL accepts connections; it does not verify application schema or user journeys.
- A successful SELECT proves the queried row exists; it does not prove backup recoverability.

## Safe next exercises

1. Record docker ps, readiness, and a test-row query.
2. Restart only the app and verify the row remains readable.
3. In a planned maintenance exercise, restart the DB container while retaining claims-db-data; verify readiness and the test row.
4. Separately design backup and restore. Restore to a disposable instance and compare a known synthetic record.
5. Practice DB-unavailable handling only in a controlled window: identify impact, capture logs, restore the dependency, and verify application recovery.

## Destructive reset warning

Removing the container and removing the named volume are different operations. Removing claims-db-data deletes the stored database files. Before any reset, verify the target volume name, ensure data is disposable or backed up, and state the expected data loss. Avoid broad prune commands because they can remove unrelated local Docker resources.

## Future production-like hardening

Use strong generated credentials from a secret manager, TLS where traffic crosses a trust boundary, least-privilege DB roles, private networking, resource limits, connection pooling, monitoring, tested backups, restore objectives, and a documented patch/upgrade plan. The lab must demonstrate these one at a time.
