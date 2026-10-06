# Shared Resources Mapped to the Daily Labs

## Why this file exists

This map connects the learning material you shared, selected public interview repositories, our daily build, and the interview practice. It is a study route, not a claim that every external file or every question in every repository has been audited. We use repositories as prompts and compare technical answers with official documentation and lab evidence. We write original explanations and examples; we do not copy other people's answer banks.

The learning loop for each topic is:

1. Learn the words and request/system flow in beginner language.
2. Watch the linked lesson to visualize it; pause and explain the idea back without notes.
3. Check the linked official documentation for exact behavior and limits.
4. Build or recreate the files from a clean clone; explain each line and command.
5. Operate it and save sanitized evidence: expected result, actual result, logs, status, and exit code.
6. Run a controlled failure drill, form a hypothesis, test one layer at a time, recover, and verify.
7. Answer recall, theory, command, practical debugging, trade-off, and design questions.
8. Rebuild next session from the repo and revisit missed answers before adding new scope.

A topic is marked practiced only after the commands or drill have been performed and recorded in that day's report. A prompt or planned exercise is not evidence of hands-on completion.

## Resources you shared and how we use them

| Shared resource | Useful topics | Where it fits |
|---|---|---|
| Cracking-SRE.pdf | Reliability concepts, operational thinking, incidents and interview framing | SRE foundation, incident response, SLI/SLO, postmortems; use alongside the Google SRE references below |
| DevOps-Zero-to-Hero-Guide ZIP and its Linux, Git, Docker, Kubernetes, Jenkins, AWS, Terraform, monitoring and DevSecOps PDFs | Beginner explanations, command familiarity, tool interview prompts | Follow the sequence in the roadmap; select only the chapters needed for the current build |
| The Ultimate DevOps Command Handbook | Linux and operations command recall | Use commands in the lab, explain flags and expected output, then add only useful commands to the daily command notes |
| Your resume and the JDs you shared | Role expectations and skills to defend | Map each day's hands-on work to the relevant resume/JD skill, and clearly separate lab evidence from prior work claims |
| Your shared SRE/DevOps repositories and codewithanush material | Additional examples and question styles | Inspiration and practice prompts; verify implementation advice against official docs and our observed behavior |

The shared PDFs and ZIP are on your computer; this GitHub map records their learning purpose. It does not imply that every page has been individually extracted and checked.

## Public question repositories and primary references

Community question repositories provide prompts and topic breadth. They can contain incomplete, dated, or context-dependent answers, so treat them as question sources rather than authorities.

- [SRE interview question repository](https://github.com/michaelkkehoe/sre-interview) — incident response, Linux, networking, monitoring, reliability and system design prompts.
- [SRE interview preparation repository](https://github.com/balajisa09/sre-interview-preparation) — coding, Linux, troubleshooting, tools and system design prompts.
- [DevOps cloud interview scenarios](https://github.com/Techikrish/devops-cloud-interview-scenarios) — scenario prompts across Linux, Docker, cloud, CI/CD, Terraform and Kubernetes.
- [DevOps interview handbook](https://github.com/hammadhaqqani/devops-interview-handbook) — categorized questions and practical scenario ideas.
- [Google SRE Workbook](https://sre.google/workbook/table-of-contents/) — primary SRE reference for monitoring, SLOs, incident response, postmortems, capacity and canarying.
- [Google incident management guide](https://static.googleusercontent.com/media/sre.google/en//static/pdf/IncidentManagementGuide.pdf) — incident roles, coordination, communication and resolution workflow.

When we select a prompt, the daily record should include our own question ID, answer in plain language, evidence from our build or a clearly labeled hypothetical, follow-up questions, and a source link. Do not paste copyrighted question/answer collections into this repo.

## Map from topics to daily work

| Day | Learn and visualize | Build/rebuild evidence | Troubleshooting and interview connection | Current status |
|---|---|---|---|---|
| Day 1 — Linux/host baseline, Java/Spring API, HTTP, PostgreSQL, Maven, Compose DB | SRE/DevOps ownership, request path, process/port/database basics, health checks, test versus live verification | [Day 1 learning and build record](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_01.md); [Day 1 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_01_PREWORK.md); [central question bank](QUESTION_BANK.md) | API stopped while DB stayed healthy; identify process/listener/dependency separately. Explain evidence and recovery. | Rebuilt and verified as recorded. Day 1 recall/rebuild remains recurring practice. |
| Day 2 — Docker image, multi-stage build, non-root runtime, Compose networking | Image versus container, build context, layers, service DNS, host port versus container port, secret/config injection | [Day 2 work record](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_02.md); [Day 2 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_02_PREWORK.md) | Buildx version mismatch and service-name typo were observed and recovered. The Day 2 record contains its interview questions; central question-bank integration is a follow-up. | Implementation and live checks are recorded. Rebuild practice is still required. |
| Day 3 — application metrics and Prometheus/Grafana | Metric, label, counter, gauge, histogram, scrape, dashboard, alert, and SLI/SLO foundations | [Day 3 prework](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/DAY_03_PREWORK.md); [Day 3 execution record](https://github.com/Pvraju-cloud-engineer/sre-devsecops-platform-lab/blob/main/docs/days/PROJECT_DAY_03.md) | Planned drills and questions become completed only after the stack is built, signals are inspected, failures are induced safely, and results are logged. | Assigned/prework stage; do not describe the planned build or drills as completed. |
| Later stages — incidents, logs/traces, SLO/error budgets, Linux/Python/Bash automation, CI/CD, Terraform/AWS, Kubernetes, security and FinOps | Add topics in dependency order, tied to your resume and the JDs | Each stage gets prework, a build/rebuild record, code/config, and a ticket with evidence | Include production-style scenarios, practical file-writing, rollback, collaboration, and interviewer follow-ups | Not yet completed; schedule after foundations and each preceding acceptance check. |

## Interview practice coverage

For each day, questions should cover all of these modes, not only definitions:

- **Recall:** define a term and identify where it appears in this lab.
- **Theory:** explain why the component exists, how it works, and its limits.
- **Commands:** choose a command, explain each relevant option, interpret output, and decide the next check.
- **Practical:** write or modify a file from an empty starting point and explain the change.
- **Troubleshooting:** use symptom, impact, expected behavior, evidence, hypothesis, narrow check, mitigation, verification and follow-up.
- **Logical/trade-off:** predict what changes if a dependency, port, identity, config value or network boundary changes; compare alternatives.
- **Design/operations:** discuss ownership, change risk, rollback, escalation, communication, toil and what would be needed before production use.
- **Interview rounds:** short direct answer, deeper technical follow-up, hands-on task, incident scenario, and concise ownership/story answer.

The [central interview question bank](QUESTION_BANK.md) holds reusable questions. A daily work record holds that day's implementation-specific evidence and additional follow-ups. Keep question IDs stable and cross-link duplicates instead of maintaining conflicting answers.

## Audit checkpoint — 2026-10-06

- Day 1 has a central bank section with seven detailed questions and a recorded rebuild/outage drill.
- Day 2 has implementation evidence, two observed troubleshooting issues, and interview prompts in its daily record. Its questions have not yet been consolidated into the central bank.
- Day 3 has prework, an execution template, planned scenarios and prompts. It is not a completed observability build yet.
- The shared materials and selected public repositories are mapped by topic here, but have not all been audited page by page or question by question.

This checkpoint is deliberately specific so a learner can tell what exists, what has been performed, and what remains practice or future work.
