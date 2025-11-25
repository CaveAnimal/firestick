# Firestick — Indexing Console Product Requirements Document (PRD)

**Version:** 1.0
**Date:** 2025-11-24
**Component:** Indexing Console (Indexing Progress / Live Reporting)
**Parent project:** Firestick — Indexing subsystem

---

## 1. Executive Summary

The Indexing Console must provide a reliable, real-time view of in-flight indexing jobs so developers and operators
can monitor progress, understand what object the system is currently processing, and quickly triage failures or
skipped objects. This PRD defines the functional and non-functional requirements for live progress reporting at
both aggregated and per-object granularity, the streaming API contract (SSE), UI behavior (percent-based progress
bars and current object display), and tests to validate end-to-end correctness.

## 2. Product Goals

1. Provide accurate, low-latency live updates to the Indexing Console for all job-level counters.
2. Publish and surface per-object lifecycle events (object-start, object-progress, object-end, object-skipped) so
   the UI can show the currently-processing object path/name and per-object progress (percent) in real-time.
3. Ensure resilient operation: streaming via SSE is preferred; short-poll fallback must exist when streaming fails.
4. Add tests and monitoring to ensure the event contract is reliable and counters remain consistent with persisted totals.

## 3. Scope

In-scope:
- Job-level aggregated counters and percent value
- Per-object lifecycle events and per-object progress
- SSE-based streaming endpoint `/api/indexing/stream?jobId=<id>`
- Polling fallback that returns current job snapshot
- Frontend UI updates: percent-based progress bars, current-object display (path/name, type, duration or reason)
- Unit, integration, and end-to-end tests for streaming and fallback

Out-of-scope (for this PRD):
- Long-lived historical analytics beyond job-level persisted totals
- Indexing orchestration internals unrelated to reporting (e.g., chunking rules and LLM summarization)

## 4. Terminology

- Job: one indexing run with a unique jobId persisted in DB
- Object: unit being indexed — file, folder or method
- SSE: Server-Sent Events streaming channel carrying object lifecycle and aggregated progress events

## 5. Functional Requirements

### 5.1 Real-time aggregated progress

The system must publish job-level aggregated status over SSE and at a polling endpoint. Aggregated status includes:

- jobId, percent (050), filesDiscovered, filesParsed, chunksProduced, documentsIndexed, embeddingsGenerated,
  filesSkipped, filesSummarized, foldersSummarized, methodsSummarized, totalFiles (if known), totalFolders, totalMethods

Behavior:
- Aggregated progress events should be published frequently (e.g., after each object-end or every N seconds) so
  the UI counters and percent bars remain smooth and responsive.

### 5.2 Per-object lifecycle events and per-object progress

The Indexing Service must emit object lifecycle events with a compact event payload to allow the UI to show the
currently processed object and compute per-object percent where possible.

Required event types and payloads (JSON):

object-start
```json
{ "event": "object-start", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000000000 }
```

object-progress
```json
{ "event": "object-progress", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000000500, "objectWorkDone": 20, "objectTotalWork": 100 }
```
Notes: Progress messages can be optional; presence of objectWork* enables UI to compute an accurate per-object percent.

object-end
```json
{ "event": "object-end", "jobId": 123, "objectId": "o-1", "objectType": "file", "path": "src/foo/Bar.java", "ts": 1690000001000, "elapsedMs": 1000 }
```

object-skipped
```json
{ "event": "object-skipped", "jobId": 123, "objectId": "o-2", "objectType": "file", "path": "src/foo/Ignored.txt", "ts": 1690000002000, "reason": "unsupported extension" }
```

progress (aggregated)
```json
{ "event": "progress", "jobId": 123, "percent": 34, "filesDiscovered": 100, "filesParsed": 34, "chunksProduced": 50, "documentsIndexed": 48, "embeddingsGenerated": 48, "filesSummarized": 12, "foldersSummarized": 10, "methodsSummarized": 22, "filesSkipped": 4 }
```

Implementation notes:
- Event timestamps must use UTC epoch-ms. Optional objectId helps correlate events to DB rows.
- Event payloads must include default values for counters to simplify client parsing (0 or empty arrays).

### 5.3 UI behavior (frontend)

The Indexing Console (Indexing Progress view) must:

- Open an SSE connection to `/api/indexing/stream?jobId=<id>` and merge incoming events into the `job` state.
- Display an explicit SSE connection status indicator (connected / reconnecting / disconnected - polling fallback).
- Show aggregated counters and percent-based progress bars for each of the metrics where totals are known.
- Show a dedicated Current Object card with: object path/name (monospace), type badge (file|folder|method), in-flight timer
  while processing, and final elapsedMs when the object finishes — or a short reason if skipped.
- When object-progress messages are available, compute per-object percent using objectWorkDone / objectTotalWork and render a
  per-object progress bar; otherwise, show a spinner or indeterminate state while the object is processing.

Accessibility:
- All progress bars must have accessible labels and tooltips with raw counters and percent (e.g., "42/100 files parsed 	642%").

### 5.4 Polling fallback

If SSE is unavailable or disconnected for a configurable threshold (e.g., 3 failed reconnect attempts / 5 seconds), the UI must
start a short-poll loop (e.g., every 12 seconds) hitting a snapshot endpoint (e.g., GET /api/indexing/job/<jobId>/status) that
returns the latest aggregated counters and the current object summary.

### 5.5 Backward compatibility and migration

- The UI must be able to operate when older servers only publish aggregated progress (no per-object events). In this mode the UI
  should merge aggregated stats and present a best-effort Current Object using persisted job-state.
- Backend changes should be feature-gated where possible and shipped with tests to avoid regressions.

## 6. Non-Functional Requirements

- Real-time latency: object and progress events should be emitted and reflected in the UI within 500ms in local test environments.
- Reliability: SSE connections should maintain >95% up-time for local/CI runs. Implement exponential backoff for reconnect attempts.
- Scalability: Event publishing must support indexing jobs that process thousands of objects without overwhelming clients 	6 consider batching or rate-limiting progress messages.
- Observability: Backend should emit metrics for events published, SSE connections, reconnects, and missed events.

## 7. Data & API Requirements

### 7.1 DB & persistence

- Persist minimal per-object telemetry (start_ts, end_ts, object_type, path, status, reason, elapsed_ms) to enable clients to
  fetch final persisted info if the SSE client joined late or missed events.
- Add any necessary columns (e.g., total_files, total_folders, total_methods) to job metadata to enable percent calculations.

### 7.2 API Endpoints

- SSE streaming: GET /api/indexing/stream?jobId=<id>
- Polling snapshot: GET /api/indexing/job/<jobId>/status — returns the latest aggregated counters and current object summary

Response schema for snapshot (JSON):

```json
{
  "jobId": 123,
  "percent": 42,
  "filesDiscovered": 100,
  "filesParsed": 42,
  "chunksProduced": 60,
  "documentsIndexed": 58,
  "embeddingsGenerated": 58,
  "filesSummarized": 20,
  "foldersSummarized": 8,
  "methodsSummarized": 22,
  "currentObject": { "objectId": "o-12", "objectType": "file", "path": "src/.../My.java", "status": "in-progress", "objectWorkDone": 50, "objectTotalWork": 100, "startedAt": 1690000000000 }
}
```

Fields must be optional-friendly for older servers.

## 8. UX / Visual Design

- Placement: The Indexing Console header shows job name + percent + connection status dot. Below it: a grid of metric counters each with a progress bar beneath the counter.
- Current Object Card: left-hand card that displays the path/name (monospace), object type badge, a per-object progress bar or spinner, and a small line with the elapsed duration OR a short reason if skipped.
- Events feed: compact list with most recent object-start/object-end/object-skipped rows (timestamped). Clicking a row expands details.

## 9. Acceptance Criteria

1. UI shows live updates for all aggregated counters and percent for an active indexing job.
2. UI displays the currently-processing object (path/name) and shows a live per-object progress indicator (percent where available) and final elapsedMs or skip reason on completion.
3. SSE event shapes are emitted as specified and integration tests confirm the stream delivers object-start / object-progress / object-end / object-skipped / progress messages.
4. If SSE is disconnected, the UI falls back to the snapshot polling endpoint and seamlessly updates counters and current object state.
5. Unit, integration and E2E tests exist that validate the end-to-end flow with a synthetic job.

## 10. Testing Plan

- Backend unit tests: events emitted at lifecycle boundaries, correct payload shapes and fields.
- Integration tests: test client subscribes to SSE for a synthetic job and asserts receipt and order of object lifecycle events and progress messages.
- Frontend unit tests: mocked EventSource producing object-start/progress/end/skip/progress messages 	6 assert correct UI state updates and progress bar rendering.
- E2E tests: Playwright scenario that starts a small synthetic indexing job, verifies live counters update, current object is shown and shows elapsed/skip reason.

## 11. Monitoring & Success Metrics

- Percent of indexing runs where SSE clients receive at least one object event (target >= 95%).
- Median event latency from server publish to UI render (target < 500ms in local envs).
- Rate of missed or out-of-order events (target as low as possible 	6 track for alerts).

## 12. Rollout & Migration Plan

1. Add DB migration to include minimal per-object telemetry fields when not present.
2. Add backend event-emission behind a feature flag and create integration tests.
3. Deploy backend changes and enable feature flag for a small set of internal environments in a canary rollout.
4. Release frontend to read new SSE shapes but keep polling fallback enabled.
5. Monitor telemetry for 4872 hours and progressively increase rollout.

## 13. Security & Privacy

- SSE stream must be authenticated and scoped to the job ID; Event payload data must not leak secrets or credentials.
- Limit event size and sanitize path names as needed for safety.

---

This PRD is modeled after existing PRD style found in /tools/plans and /tools/planb and focuses specifically on the Indexing Console product requirement for live progress, per-object reporting, percent-based progress bars, SSE streaming, and polling fallback.
