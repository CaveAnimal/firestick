# Firestick — Indexing Console Implementation Plan

**Component:** Indexing Console / Indexing Progress UI + SSE reporting
**Source PRD:** tools/planc/firestickIndexPRD.md
**Goal:** Provide real-time live updates to the Indexing Console showing the current object path/name and percent-complete progress for every progress metric.

---

## Summary

This planning document translates the Indexing Console PRD into a practical implementation plan. It breaks work into backend, frontend, and test deliverables with milestones, required schema/API changes, rollout guidance, and rough effort estimates. The intent is to provide a concrete checklist teams can use to implement streaming, per-object progress reporting, UI updates (percent-based progress bars), and end-to-end tests.

## Milestones

1. Backend event emission & DTOs (SSE): object-start / object-progress / object-end / object-skipped + aggregated `progress` messages.
2. Persist per-object telemetry & job totals (DB migrations) so late-join clients can recover state.
3. Frontend SSE consumer + UI component: Indexing Console shows counters, percent bars, connection state and Current Object card.
4. Fallback polling API: snapshot endpoint and frontend polling logic for degraded mode.
5. Tests: backend unit tests, integration server-client SSE tests, frontend unit tests, and E2E (Playwright) synthetic job tests.
6. Rollout: feature flags, canary release, monitoring and metrics.

## Implementation work items

### Backend (Indexing service / SSE)

- Add a small event model (Java DTOs / JSON shapes) for SSE messages: object-start, object-progress, object-end, object-skipped, progress.
- Emit per-object lifecycle events from the IndexingService or ProgressBus at these points:
  - When indexing begins processing an object → object-start
  - Periodically during long-running object processing or when objectWorkDone changes → object-progress
  - On completion → object-end (include elapsedMs)
  - When skipped → object-skipped (include reason)
- Ensure after each object-end the aggregated job counters are recomputed and an aggregated `progress` message is emitted.
- Add configurable throttling for `object-progress` messages (e.g., no more than once/second per object) to avoid SSE flooding.
- Feature-flag SSE payloads during initial rollout.

DB / Persistence changes
- Add/modify job metadata columns: totalFiles, totalFolders, totalMethods (optional but useful) and minimal `indexing_objects` table columns: objectId, path, type, status, start_ts, end_ts, elapsed_ms, reason.
- Add migration (Flyway/Liquibase) and update index write paths to populate these fields.

API
- SSE endpoint: GET /api/indexing/stream?jobId=<id>
- Snapshot endpoint: GET /api/indexing/job/<jobId>/status

Tests (backend)
- Unit tests for event creation, fields, and defaulting behavior.
- Integration test that runs a synthetic indexing job and verifies the SSE stream publishes object-start/object-progress/object-end and progress messages in order.

### Frontend (Indexing Console)

UX Components
- Connection indicator showing SSE status (Connected / Reconnecting / Disconnected → Polling).
- Aggregated counters grid with percent-based progress bars per metric (filesDiscovered, filesParsed, chunksProduced, documentsIndexed, embeddingsGenerated, filesSkipped, filesSummarized, foldersSummarized, methodsSummarized).
- Current Object card showing object path/name (monospace), type badge, per-object progress (percent or indeterminate spinner), live elapsed timer while active, final elapsedMs or skip reason when done.
- Recent events feed (compact) showing latest lifecycle events.

Client behavior
- Open SSE to /api/indexing/stream?jobId=<id> and merge messages into job state.
- Compute percent for each progress bar when totals are available (eg percent = counter / total * 100). When totals are unknown show an indeterminate bar.
- When object-progress includes objectWorkDone/objectTotalWork compute object percent; otherwise show spinner.
- If SSE disconnects and reconnection attempts exceed threshold, start polling `/api/indexing/job/<jobId>/status` every X seconds (configurable) until SSE reconnects.

Accessibility & styling
- All progress bars must include accessible aria-labels and tooltip showing raw counters and percent (e.g., "42/100 — 42% files parsed").

Tests (frontend)
- Unit tests (Jest/React Testing Library): mocked EventSource streams to simulate object-start/object-progress/object-end/object-skipped/progress messages and assert UI updates.
- Visual tests / snapshot tests for components and accessibility checks.

### End-to-end tests

- E2E (Playwright): a synthetic indexing job (small fixture) should cause server to publish SSE messages and the UI to update counters, progress bars, current object, and events feed.

## Rollout plan

1. Implement DB migrations and backend event emission behind a feature flag (internal envs only).
2. Deploy backend changes and enable SSE for a small number of internal test jobs; verify metrics and logs.
3. Deploy frontend changes to read SSE shapes (keep polling fallback enabled), run E2E tests in CI.
4. Remove feature flag once stable and monitor SSE delivery metrics and client uptimes for 48–72 hours.

## Monitoring, observability & success metrics

- SSE publish rate and error count (monitor for repeats & throttling).
- SSE client connect/reconnect rate and median event latency (target < 500ms in local envs).
- Percent of indexing runs where at least one object event is delivered to clients (target >= 95%).
- UI acceptance tests pass consistently in CI for streaming and polling fallbacks.

## Risks & mitigations

- Event flooding: throttle `object-progress` messages; allow clients to opt-in / set max frequency.
- Clients that join late: persist telemetry and provide snapshot endpoint.
- Schemas mismatch: strict versioning and feature flags; fallback compatibility in UI.

## Rough estimates & owners (T-shirt sizing)

- Backend core events + DB migration: 3-5 engineering days (backend engineer)
- SSE integration tests & feature-flag rollout: 2-3 engineering days (backend/QA)
- Frontend SSE consumer + UI components + unit tests: 3-5 engineering days (frontend engineer)
- E2E tests (playwright) and polish: 2-3 engineering days (QA/front-end)

---

This plan is designed to give an actionable path to implementing the Indexing Console realtime progress feature described in the PRD and goals. If you want, I can now open a branch, implement a minimal SSE emitter + frontend stub (demo) and the corresponding tests as a follow-up task.
