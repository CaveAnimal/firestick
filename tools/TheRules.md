# Agent Development Rules (Firestick) — Concise

## Priority (do in this order)
1) Work Reports (percent complete required) 2) Core logic 3) UI 4) Data/models 5) Errors/validation 6) Unit tests 7) Docs 8) Perf 9) Integration tests 10) Cleanup/refactor 11) Build 12) CI/CD 13) Deploy
- Dev2 note: When in doubt or short on time, update your Work Report first; it supersedes all other tasks.

## Single-Task Workflow (read only what you need)
- Tasks: Each developer reads ONLY the tasks assigned to themselves in their personal file (`tools/work/dev1/tasksDEV1.md` for DEV1, `tools/work/dev2/tasksDEV2.md` for DEV2, `tools/work/dev3/tasksDEV3.md` for DEV3). Only refer to the master task list for status reporting or coordination.
- Examples: Each developer reads ONLY the example(s) directly relevant to their current task in `tools/plans/firestickEXAMPLES.md`.
- Read `tools/experiences/LESSONS_LEARNED.md` (Lessons Learned) for recent incidents and remediation before starting work.
- PRD: read only the FRs your task touches (Section 5) + key use cases.
- Planning: current sprint in `tools/plans/firestickPLANNING.md` for prerequisites/blockers.
- Stop reading when you can state: inputs, outputs, done criteria, and 2–3 tests.

## Per-Developer Task Lists & Scripts
- Status markers: `[ ]` Not Started, `[-]` In Progress, `[X]` Completed, `[V]` Tested, `[!]` Blocked, `[>]` Deferred.
- Scripts (one-step update):
  - DEV1: `tools/work/dev1/scripts/dev1-check.ps1`, `tools/work/dev1/scripts/dev1-update.ps1`
  - DEV2: `tools/work/dev2/scripts/dev2-check.ps1`, `tools/work/dev2/scripts/dev2-update.ps1`
  - DEV3: `tools/work/dev3/scripts/dev3-check.ps1`, `tools/work/dev3/scripts/dev3-update.ps1`
  - Note: `dev*-update.ps1` updates BOTH your personal summary and the master `## Task Summary` in `tools/plans/firestickTASKS.md`.
- After marking a task `[X]` or `[V]`, run your update script:
  - DEV1: `tools/work/dev1/scripts/dev1-update.ps1`
  - DEV2: `tools/work/dev2/scripts/dev2-update.ps1`
  - DEV3: `tools/work/dev3/scripts/dev3-update.ps1`
- Use `check` scripts for quick, read-only verification anytime.

## Captain’s Log (mandatory, 1 line per entry)
- Files: DEV1 `tools/work/dev1/dev1CAPTAINS_LOG.md`, DEV2 `tools/work/dev2/dev2CAPTAINS_LOG.md`, DEV3 `tools/work/dev3/dev3CAPTAINS_LOG.md`.
- Before coding (after required reading): `[YYYY-MM-DD HH:MM] TASK-ID — What: <plan> | Why: <reason>`
- After completion (before updating task lists): `[YYYY-MM-DD HH:MM] TASK-ID — Work done: <summary> | Result: <outcome>`

## Status Reporting
- Individual: personal task file. Overall: `tools/plans/firestickTASKS.md`.
- Report counts and % by status; reconcile any contradictions immediately.

## Work Reports (percent complete required)
- Every work report must end with a Percent Complete section using exactly this format (case and wording):
  - `Dev1 <number> percent complete`
  - `Dev2 <number> percent complete`
  - `Dev3 <number> percent complete`
  - `firestick <number> percent complete`
- Compute the numbers by running the update scripts before reporting so summaries are current:
  - DEV1: `tools/work/dev1/scripts/dev1-update.ps1`
  - DEV2: `tools/work/dev2/scripts/dev2-update.ps1`
  - DEV3: `tools/work/dev3/scripts/dev3-update.ps1`
  - Note: Each `dev*-update.ps1` also refreshes the master summary in `tools/plans/firestickTASKS.md` (used for the `firestick` percent).
 - DEV3 Scope: Mirror ONLY the master `## Current Phase — Focused Scope (Counted)` tasks. All Deferred/Backlog content is excluded via skip-count markers to keep DEV3 velocity focused on the active sprint.
 - Compliance: Missing or malformed percent-complete lines will block task progress and PR approval until corrected.

## Anti-Patterns (avoid)
- Hiding TODOs; infra before features; deploy before app works.
- Over-engineering schemas/APIs; premature scaling/caching; microservices for simple apps.

## Development Standards
- Tests: JUnit 5, at least happy path + 1 edge; don’t reduce coverage.
- Style: follow project formatting; ensure `mvn test` passes before commit.

## Build/Test Results Policy
- Do NOT analyze or parse Maven Surefire reports unless explicitly requested in the task/instruction.
- Treat the Maven output line `[INFO] BUILD SUCCESS` as sufficient confirmation that all tests passed.
- If detailed test failure analysis is required, the request will explicitly ask for Surefire report inspection or specific test case triage.

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

## Windows Shell Syntax (CRITICAL - read environment info)
- **ALWAYS check environment info** for user's OS and shell before generating terminal commands.
- **This project uses PowerShell** (even if environment reports "cmd.exe" as default shell).
- **`&&` does NOT work in Windows PowerShell or cmd.exe** - this is a Unix/bash operator only.
- **`&` does NOT work in PowerShell** - PowerShell reserves `&` for the call operator and will error with "AmpersandNotAllowed".

### Correct syntax by shell:
- For **PowerShell** (this project): Use semicolon `;` to chain commands:
  ```powershell
  cd some-directory ; mvn test
  ```
- For **cmd.exe** (legacy/batch files only): Use single ampersand `&` to chain commands:
  ```cmd
  cd some-directory & mvn test
  ```

### Best practice
- **Avoid unnecessary command chaining**. If already in the project directory, just run the command:
  ```powershell
  mvn test -Dtest=SomeTest
  ```
- When in doubt, use semicolon `;` for PowerShell (the default for this project).
- See `tools/experiences/LESSONS_LEARNED.md` (2025-11-12 entries) for detailed incident reports.

## Bookmarks (resume quickly)
- Purpose: Jump back to the most relevant spot in your task file without scrolling.
- Dev2 bookmarks:
  - Config: `tools/work/dev2/tasksDEV2_bookmark.json` (holds `source`, `markers[]`, and `resume.preferredStartLine`).
  - Resolver: `tools/work/dev2/scripts/dev2-resume.ps1`.
  - Behavior: Resolves the best line number using patterns in `markers`; prints `path:line`; with `-OpenInVSCode`, opens the file at that line (requires `code` on PATH).
- Example (PowerShell):

```powershell
# Print the location to resume editing
e:\MyProjects\MyGitHubCopilot\firestick\fstk-001\tools\work\dev2\scripts\dev2-resume.ps1

# Or open directly in VS Code at the resolved line
e:\MyProjects\MyGitHubCopilot\firestick\fstk-001\tools\work\dev2\scripts\dev2-resume.ps1 -OpenInVSCode
```

- Notes:
  - Keep lightweight HTML anchors in the task file to improve resilience, e.g., `<!-- @anchor:dev2-recommendations-backlog -->`.
  - Update `tasksDEV2_bookmark.json` if section headings or key phrases change significantly.

## Working Agreement — Time Awareness & Long-Running Operations

### Purpose
Prevent silent stalls (“spinning”) and ensure continuous visibility & actionable next steps during development and AI-assisted workflows.

### Core Time Boxes
1. Heartbeat Interval: Provide a status update at least every 60s during any operation that hasn’t completed.
2. Soft Timeout (Diagnose): 2 minutes without meaningful forward progress (no new log phase, no new file, no test delta) triggers automatic diagnostics.
3. Hard Timeout (Decision): 5 minutes without progress requires an explicit branch: Retry | Alternate Strategy | Abort.

### Heartbeat Content (succinct)
`[HH:MM:SS+elapsed] Phase=<name> WaitingOn=<resource/command> LastOutput=<t-ago> NextAction=<planned probe or step>`

### Automatic Diagnostics (Soft Timeout Trigger)
- Capture last 20–40 lines of relevant log (build/test/server) if available.
- Port / process check (e.g., is target port bound; list java/python processes if relevant).
- Resource probe: rough CPU/disk usage if accessible.
- For test hang: list currently running test (if surefire/failsafe output) or dump thread count when feasible.

### Hard Timeout Decision Tree
| Condition | Default Branch |
|-----------|----------------|
| External dependency unreachable (network / registry) | Switch to offline/mock strategy |
| Build/test deadlock suspicion | Abort run, re-run smallest reproducible subset |
| Local port conflict | Auto-increment port (e.g., 8000→8001→8002) |
| Persisting lock file / DB busy | Back up & remove stale lock, restart component |

### Fallback Strategies (Examples)
- Chroma server start failure: alternate invocation (`python -m chromadb` → `chroma run` → Docker container) and auto-port shift.
- ONNX model load slow: fall back to MOCK embeddings with explicit warning & TODO to restore.
- Full test suite slow: run impacted module tests only, then resume full run in background.

### User Control Signals
- "Stop here" → immediate cancellation suggestion; summarize partial results.
- "Fast path only" → prefer mocks/in-memory, skip heavy downloads.
- "Verbose mode" → increase heartbeat frequency to every 20–30s with added diagnostics.
- "Background" → continue asynchronously; queue summary at next milestone.

### Logging & Traceability
- Each timeout escalation (soft/hard) produces a one-line structured marker: `TIMEBOX <phase> <soft|hard> <action>`.
- Any remediation that alters environment (kill process, clear lock, change port) is logged with BEFORE/AFTER state.

### Exit Criteria for Long Operation
Must emit: Success outcome OR explicit categorized failure (Transient / Config / Defect) plus next-step recommendation.

### Scope & Exceptions
- Applies to build, test, server start, embedding/model load, vector store ops.
- Exemption only if a predicted duration & ETA was declared upfront and still within that window.

### Change Management
- Revisions to this agreement require adding a dated subsection under this heading; prior rules remain until explicitly superseded.

---
Last updated: 2025-11-10

