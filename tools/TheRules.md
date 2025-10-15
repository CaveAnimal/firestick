# Agent Development Rules (Firestick) — Concise

## Priority (do in this order)
1) Core logic 2) UI 3) Data/models 4) Errors/validation 5) Unit tests 6) Docs 7) Perf 8) Integration tests 9) Cleanup/refactor 10) Build 11) CI/CD 12) Deploy

## Single-Task Workflow (read only what you need)
- Tasks: personal first (`tools/work/dev1/tasksDEV1.md` or `tools/work/dev2/tasksDEV2.md`), then master (`tools/plans/firestickTASKS.md`).
- Examples: open only relevant sections in `tools/plans/firestickEXAMPLES.md`.
- PRD: read only the FRs your task touches (Section 5) + key use cases.
- Planning: current sprint in `tools/plans/firestickPLANNING.md` for prerequisites/blockers.
- Stop reading when you can state: inputs, outputs, done criteria, and 2–3 tests.

## Per-Developer Task Lists & Scripts
- Status markers: `[ ]` Not Started, `[-]` In Progress, `[X]` Completed, `[V]` Tested, `[!]` Blocked, `[>]` Deferred.
- Scripts (one-step update):
  - DEV1: `tools/work/dev1/scripts/dev1-check.ps1`, `tools/work/dev1/scripts/dev1-update.ps1`
  - DEV2: `tools/work/dev2/scripts/dev2-check.ps1`, `tools/work/dev2/scripts/dev2-update.ps1`
  - Note: `dev*-update.ps1` updates BOTH your personal summary and the master `## Task Summary` in `tools/plans/firestickTASKS.md`.
- After marking a task `[X]` or `[V]`, run your update script:
  - DEV1: `tools/work/dev1/scripts/dev1-update.ps1`
  - DEV2: `tools/work/dev2/scripts/dev2-update.ps1`
- Use `check` scripts for quick, read-only verification anytime.

## Captain’s Log (mandatory, 1 line per entry)
- Files: DEV1 `tools/work/dev1/dev1CAPTAINS_LOG.md`, DEV2 `tools/work/dev2/dev2CAPTAINS_LOG.md`.
- Before coding (after required reading): `[YYYY-MM-DD HH:MM] TASK-ID — What: <plan> | Why: <reason>`
- After completion (before updating task lists): `[YYYY-MM-DD HH:MM] TASK-ID — Work done: <summary> | Result: <outcome>`

## Status Reporting
- Individual: personal task file. Overall: `tools/plans/firestickTASKS.md`.
- Report counts and % by status; reconcile any contradictions immediately.

## Anti-Patterns (avoid)
- Hiding TODOs; infra before features; deploy before app works.
- Over-engineering schemas/APIs; premature scaling/caching; microservices for simple apps.

## Development Standards
- Tests: JUnit 5, at least happy path + 1 edge; don’t reduce coverage.
- Style: follow project formatting; ensure `mvn test` passes before commit.

## Micro-Tasking (example)
- Break “Implement auth” into: User class, validatePassword(), test valid/invalid, repository interface.

## When Blocked
- Check examples for the task, read errors carefully, split work smaller.
- Ask with a specific question + TASK-ID if still stuck.

## Lessons Learned (on repeat failures)
- If any command fails and you need to run it again, add a brief (≤5 lines) incident to `tools/work/devLESSONS_LEARNED.md`.
- Include: timestamp, task/command, What (plan), Why (reason), Work done (fix), Result/Prevent.

## Security
- Never commit secrets/large binaries. Prefer env vars; rotate any leaked secret immediately.

## Acceptance & Self-Review
- For each task define: inputs, outputs, done criteria, tests, error handling.
- Checklist before complete: works as expected; errors handled; tests passing; style OK; docs updated; no debug code; acceptable performance.

## Environment & Tools (brief)
- Build: Java 21, Maven 3.9+, Spring Boot 3.5.x.
- PowerShell: test complex commands interactively; use the `check`/`update` scripts to manage task summaries.

