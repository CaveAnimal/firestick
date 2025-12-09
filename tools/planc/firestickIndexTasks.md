<!-- Beginner-friendly task list for implementing Indexing Console realtime progress -->
# Firestick — Indexing Console Tasks (Beginner-friendly)

This file breaks the Indexing Console feature into very small, beginner-friendly tasks and sub-tasks. Each
task has a checkbox you can check off as you complete it. The project tooling or CI can compute percent complete
by counting completed checkboxes vs total checkboxes.

How percent complete is calculated
- Total boxes = count of all [ ] / [x] items in this file
- Completed boxes = count of [x] items
- Percent complete = (Completed / Total) * 100

Example: if 4 of 20 boxes are checked, percent complete = 20%.

Tip: Use a small script or your editor's search to count "[x]" and "[ ]" to compute progress programmatically.

---

## 🧭 Overview (one-liner goal)
[ ] Provide real time live updates to the Indexing Console (Indexing Progress) showing the current object's path/name and current progress. Update every progress bar to show percent-complete.

## 1. Project setup and orientation (first day for a new dev)
[ ] Clone the repo and open it in VS Code
  - [ ] Verify `mvn -v` and Java 21 are available locally (or use the project's dev container / environment)
  - [ ] Run the project's unit tests or smoke tests to confirm the environment is healthy
  - [ ] Open `tools/planc/firestickIndexPRD.md`, `firestickIndexPlanning.md`, and `firestickIndexGoals.md` and read them

## 2. Backend: event shapes & SSE stream (basic)
[ ] Create Java DTOs (or JSON shapes) for SSE events
  - [ ] Add `ObjectStartEvent` shape with fields: event, jobId, objectId, objectType, path, ts
  - [ ] Add `ObjectProgressEvent` shape with objectWorkDone, objectTotalWork (optional), ts
  - [ ] Add `ObjectEndEvent` shape with elapsedMs (and ts)
  - [ ] Add `ObjectSkippedEvent` shape with reason
  - [ ] Add `ProgressEvent` shape for aggregated counters (percent, filesParsed, etc.)
[ ] Add simple unit tests to assert DTOs serialize to expected JSON shapes

## 3. Backend: emit events from the Indexing service (starter tasks)
[ ] Add `object-start` emission at the point where an object begins processing
  - [ ] Locate the IndexingService or equivalent class
  - [ ] Insert SSE emit for `object-start` with ts=System.currentTimeMillis()
  - [ ] Add a simple log line to show the event was sent
  - [ ] Add a unit test or integration assertion for `object-start` emission
[ ] Emit `object-end` when an object finishes
  - [ ] Compute elapsedMs = endTs - startTs
  - [ ] Emit `object-end` with elapsedMs
  - [ ] Verify aggregated job counters are recomputed after object-end
[ ] Add optional `object-progress` emits for long-running objects
  - [ ] Emit only at limited frequency (throttle to ~1/sec) to avoid SSE flooding
  - [ ] Ensure event includes objectWorkDone/objectTotalWork when available
[ ] Emit aggregated `progress` messages frequently (after each object-end or N seconds)

## 4. Database & persistence (safe, small steps)
[ ] Add minimal `indexing_objects` table (if not present) with columns: id, job_id, path, type, status, start_ts, end_ts, elapsed_ms, reason
  - [ ] Create a DB migration script (Flyway/Liquibase) in the repo
  - [ ] Run migrations in local dev and verify schema added
  - [ ] Add a simple persistence test that writes and reads an object row
[ ] Add fields to job metadata (e.g., totalFiles, totalFolders) if not present

## 5. API: SSE endpoint & snapshot (small increments)
[ ] Confirm or add GET /api/indexing/stream?jobId=<id> endpoint
  - [ ] Add basic docs for the event shapes in-code and in the API README
  - [ ] Add a small integration test that opens SSE and receives at least one `progress` event
[ ] Add snapshot endpoint GET /api/indexing/job/<jobId>/status returning latest aggregated counters and current object
  - [ ] Add unit test checking snapshot JSON shape and default values

## 6. Frontend: wire up SSE and show a minimal Current Object card (first UI PR)
[ ] Add EventSource/SSE consumer to the Indexing page
  - [ ] Connect to `/api/indexing/stream?jobId=` when page loads
  - [ ] Add state hooks to receive `object-start`, `object-progress`, `object-end`, `object-skipped`, and `progress`
  - [ ] Add a connection status indicator (Connected / Reconnecting / Disconnected)
[ ] Render a minimal Current Object card showing path/name and object type
  - [ ] While `in-progress` show spinner and startedAt timestamp
  - [ ] On `object-end` replace spinner with elapsedMs text
  - [ ] On `object-skipped` show reason text

## 7. Frontend: show percent-based progress bars for each counter (next PR)
[ ] For each metric (filesDiscovered, filesParsed, chunksProduced, documentsIndexed, embeddingsGenerated, filesSkipped, filesSummarized, foldersSummarized, methodsSummarized) add a progress bar
  - [ ] Decide which totals are required (e.g., totalFiles) and show indeterminate bars for unknown totals
  - [ ] Compute percent = Math.round(count / total * 100) and show tooltip like "42/100 — 42%"
  - [ ] Ensure accessible aria attributes on each progress bar

## 8. Frontend: per-object percent handling
[ ] If `object-progress` includes objectWorkDone/objectTotalWork compute object percent and show a per-object progress bar
  - [ ] If objectTotalWork unknown, render indeterminate spinner until `object-end`

## 9. Tests: unit, integration, and end-to-end (required)
[ ] Backend unit tests for events and serialization
  - [ ] object-start, object-progress, object-end, object-skipped, progress message shapes are tested
[ ] Backend integration test: synthetic indexing job emits the right sequence of SSE messages
[ ] Frontend unit tests: mocked EventSource to send sample messages and assert UI updates (Jest/RTL)
[ ] End-to-end Playwright test: run a small synthetic indexing job and assert that the UI shows counters, progress bars, current object updates and elapsed/skip reason

## 10. Observability & rollout (finalize)
[ ] Add metric collection for SSE publishes and connection errors
[ ] Wire up logs for missed events / throttling hits
[ ] Add feature-flag gating for SSE payloads to control rollout
[ ] Draft a quick runbook explaining how to troubleshoot SSE issues during rollout

## 11. QA checklist for handoff (what to verify before marking done)
[ ] Unit tests passing locally and in CI
[ ] Integration tests validating SSE event sequences
[ ] Frontend unit tests and accessibility checks passing
[ ] E2E tests passing in CI pipeline
[ ] DB migration applied successfully on test environment
[ ] Docs updated (API + PRD link) and a short demo recorded (optional)

## How to mark progress
- Edit this file and mark the checkbox for each sub-task you complete ([x]).
- Use a simple utility or search to compute percent complete across the file (count [x] vs total boxes).

Example quick PowerShell one-liner to compute percent in repo root:
```powershell
$text = Get-Content tools/planc/firestickIndexTasks.md -Raw; $total = ($text -split '\[\s\]' ).Length - 1 + ($text -split '\[x\]' ).Length - 1 ; $done = ($text -split '\[x\]' ).Length - 1; if($total -eq 0){0}else{[math]::Round($done/$total*100,1)}
```

---

If you want I can now open a branch and implement the minimal SSE emitter + small frontend stub (demo) and the corresponding tests as a follow-up task.
