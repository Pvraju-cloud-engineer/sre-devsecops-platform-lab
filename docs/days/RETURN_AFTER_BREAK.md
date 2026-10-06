# Return After a Break — Resume the Learning Loop

## Current checkpoint

- **Day 1:** API plus PostgreSQL baseline was built, rebuilt from the personal repo, tested, and used for a recorded API-stopped drill. Revisit it; do not assume one successful run means permanent recall.
- **Day 2:** The API was containerized with a multi-stage Dockerfile and Compose networking. Live health, POST/GET, SQL persistence, non-root user, and two troubleshooting issues were recorded. Rebuild and explain the files again before moving ahead.
- **Day 3:** Prometheus/Grafana observability is the next build. The prework and execution template are not proof that Day 3 is built.

## Before opening a terminal

1. Open the [resource-to-lab map](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/interview/RESOURCE_TO_LAB_MAP.md). It shows shared resources, interview repositories, day links, and honest completion status.
2. Read the [Day 1 build and learning record](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_01.md). Trace one request from HTTP through Spring MVC, JPA, JDBC, and PostgreSQL. Note any part you cannot explain.
3. Read the [Day 2 build record](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_02.md). Redraw the container network: host client to port 8081, API container to the Compose service name postgres on port 5432, and PostgreSQL data in its named volume.
4. Read the [Day 1 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_01_PREWORK.md), [Day 2 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_02_PREWORK.md), and their video references. Watch the parts you cannot yet explain; pause to teach the idea back in your own words. Use the [Day 3 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_03_PREWORK.md) only after Day 1 and 2 recall is solid.
5. Review the [interview question bank](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/interview/QUESTION_BANK.md) and the question section in the Day 2 record. Day 1 has seven central-bank questions; Day 2's questions are still in its day record and are not consolidated in the bank yet.

## Recall checkpoint — answer without notes

Write short answers first. If unsure, mark it as a gap and return to the linked lesson or note; do not guess.

### Day 1

1. What is the full request path when a client creates a claim? Name each layer and what it owns.
2. Why can a Java process be down while PostgreSQL is healthy? Which command checks each separately?
3. What does a passing Maven test establish? What live behaviors still need smoke checks?
4. Why is PostgreSQL's host port bound to loopback, and what does the named volume preserve?
5. What evidence proved the claim existed after an API restart?

### Day 2

1. Why does an API running on the EC2 host use 127.0.0.1:5433, while the API container uses postgres:5432?
2. Explain build stage versus runtime stage, why Maven is absent from the runtime image, and why the process runs as UID 10001.
3. What is a build context? Which files should .dockerignore exclude and why?
4. How does Compose DNS resolve postgres? What do the two port numbers in host mapping mean?
5. If the API health check fails but the database is healthy, what evidence do you collect before changing anything?
6. What did the Buildx version error mean in this environment, and how was the compatibility issue resolved?
7. Why did the misspelled Compose service name fail, and how did you confirm the correct service?

Check the exact question wording and answer details in the day records. The questions here are a retrieval-practice checkpoint, not a replacement question bank.

## Rebuild session — order of work

Use a disposable Linux lab host and the repository's documented bootstrap instructions. Never use the Cisco work repository or its credentials. Keep secrets out of Git and terminal transcripts.

1. Record the current Git branch and clean/dirty status before work. Sync only the personal project branch you intend to use; do not force-reset away uncommitted work.
2. Recreate the Day 1 host baseline and dependencies by following the Day 1 record. Explain what each command checks or changes before running it.
3. Start PostgreSQL and verify readiness. Run the Maven tests. Start the API with visible logs. Check actuator health, create a claim, fetch it, and query the matching row.
4. Perform the documented Day 1 stopped-API drill: verify process, listener and database separately; restore the API; repeat the health and GET check.
5. Stop the host API and rebuild/start the Day 2 Compose stack from its checked-in Dockerfile and Compose file. Do not rely on an old image as the build result; record the build command and image tag.
6. Verify both containers, Compose health, API health, POST/GET, database row, configured API DB URL, and runtime user. Explain why the API container reaches postgres:5432 while a host client reaches loopback:8081 and the published database port is 5433.
7. Re-run the two recorded Day 2 troubleshooting cases only as controlled lab drills. Capture initial symptom, evidence, hypothesis, safe correction, and the same successful check afterward.
8. Add actual commands, exit codes, timestamps, output summaries, problems and fixes to the day's execution record. Never record an unrun planned drill as practiced.

The detailed Day 1 and Day 2 records contain the exact repo paths and prior observed outputs. Follow them rather than relying on this checklist for command spelling. If a file differs from the report, pause and inspect Git status/history before editing.

## Move-on gate for Day 3

Start Day 3 only after you can, without copying answers:

- redraw and explain both Day 1 host-to-database and Day 2 container-to-database paths;
- write the essential Dockerfile and Compose service relationship from a blank file, then explain each line;
- bring the stack up and prove API health, persistence, and process identity with evidence;
- diagnose one API-down and one Compose/network/build failure without random edits;
- explain what was proven, what was not tested, and who owns application, database, container host, and deployment concerns in a real team.

Then work through [Day 3 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_03_PREWORK.md), including its videos, before implementing the [Day 3 execution plan](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_03.md). Day 3's record should capture the real files, dashboards, metric queries, failure drills, results, and interview practice from the session.

## End-of-session closeout

- Update the correct daily report with observed results and unresolved gaps.
- Update the ticket index only when acceptance criteria are evidenced; keep statuses auditable.
- Review staged file names and diff; confirm no .env, tokens, keys, private data, or generated build output is included.
- Commit and push only to the personal learning repository after checking the intended branch and remote.
- Stop services and clean up the disposable cloud resources according to the day's cleanup notes; stopping an instance alone may leave billable storage behind.
