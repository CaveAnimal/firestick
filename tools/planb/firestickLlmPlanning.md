## 7. Indexing Console — Live, real-time progress & object-level reporting (new goal)

This section describes an implementation plan, tests, CI and rollout guidance for the new "Indexing Console — Live, real-time progress and object-level reporting" goal added to `firestickLlmGoal.md`.
Summary
- Make the Indexing Console show live real-time updates for all progress counters and the currently-processed object (file/folder/method). The UI must display elapsed duration for successfully processed objects or an explicit reason for skipped/failed objects. The stream of truth is the server's SSE stream with persisted DB records used as a fallback and for historical views.

Why this matters
- Observability: Developers and maintainers need live feedback to debug indexing, confirm coverage, and detect failures early.
- UX: Real-time updates make indexing feel responsive and trustworthy.

Assumptions
- Backend already persists per-object telemetry in `indexing_objects` table and emits object-level events to SSE (if not, this work is required — see Backend tasks).
- UI runs on modern browsers with EventSource support. If EventSource is unavailable, the UI will gracefully fall back to polling the latest job.

High-level scope & phases
1. Design and API contract (small PR) — define SSE payload contract, example event schema, and any new endpoints or query parameters needed.
2. Backend changes (medium PRs) — ensure IndexingService emits object-start / object-end / object-skipped events and aggregated stats; add unit and integration tests for emission and persistence; ensure DB migration exists for production (Flyway/Liquibase) for indexing_objects and new totals.
3. Frontend changes (medium PR) — add `currentObject` & `objectEvents` UI state, ensure `startSse` merges payloads to `job` state and sets current-object display, show connection status, and fall back to polling when SSE is broken.
4. Testing & CI (small->medium PR) — unit tests, integration SSE tests (server+client), and an e2e playbook to exercise a tiny synthetic job that emits object-level events.
5. Monitoring & rollout (small) — add server-side metrics for SSE connections and event rate; add docs and a migration plan for production databases.

Acceptance criteria (developer & QA)
- When an indexing job runs, the Indexing page shows live updates for these fields: filesDiscovered, filesParsed, chunksProduced, documentsIndexed, embeddingsGenerated, filesSkipped, filesSummarized, foldersSummarized, methodsSummarized, and overall percent. These must update while the job is active.
- The Indexing page shows the "current object" (name/path, objectType) and either a friendly duration (e.g., "took 42ms / 1.2s") after `object-end` or a reason string after `object-skipped` (e.g., "skipped: unsupported file type").
- The SSE payload contract is respected by both backend and frontend. Example payloads are documented in the API contract below.
- If SSE disconnects or fails, the UI updates via polling (every 2s) as a fallback, with an obvious visual indicator that it is polling rather than streaming.

Backend tasks
- Task B1 — Define SSE payload contract, and add schema examples to API docs:
    - object-start: { event: "object-start", jobId, objectId?, type: "file|folder|method", name, ts }
    - object-end: { event: "object-end", jobId, objectId?, type, name, ts, elapsedMs }
    - object-skipped: { event: "object-skipped", jobId, objectId?, type, name, ts, reason }
    - aggregate: { event: "progress", jobId, percent, filesParsed, filesDiscovered, chunksProduced, documentsIndexed, embeddingsGenerated, filesSummarized, foldersSummarized, methodsSummarized, skippedFiles: [...] }

- Task B2 — IndexingService changes:
    - Emit the above events at the right lifecycle points (start/end/skip). Prefer to emit `progress` after object completion to keep counters consistent.
    - Persist indexing object records to `indexing_objects` with timestamps and elapsedMs / reason.
    - Ensure all data used by the UI are non-null or have sensible defaults (0 or empty arrays) to avoid NPEs.
- Task B3 — Controller tests & integration tests:
    - Add unit tests for IndexingService that verify events are enqueued/emitted on lifecycle changes.
    - Add an integration test that opens `/api/indexing/stream?jobId=...` and asserts receipt of at least one object-start and object-end and a final progress >= 100.
    - Existing schema-ensurer (H2) already updated to create TOTAL_* columns; add a Flyway/Liquibase migration to create `indexing_objects` and TOTAL columns for production DBs.

Frontend tasks
- Task F1 — UI state & components:
    - In `ui/src/pages/Indexing.tsx`, add `currentObject` state and wire it to SSE messages.
    - Add a connection-status indicator (Connected/Connecting/Disconnected) somewhere near the top of the progress card. `sseConnected` already exists — make it visible.
    - Create an `ObjectCard` small component to show current object name, type, startedAt, elapsed/duration or reason.

- Task F2 — SSE handling & fallback behavior:
    - Ensure `startSse(jobId)` merges incoming `progress` payloads into `job.stats` and applies totalFolders/totalMethods to job meta fields.
    - On object-start, set `currentObject` to the payload; on object-end/skipped update `currentObject` to show elapsed reason and optionally push a summary into `objectEvents` history.
    - If SSE reconnect attempts exceed a threshold (e.g., 5 attempts), switch to polling (every 2s) but continue to attempt reconnection in backoff increments.

- Task F3 — Tests:
    - Add unit-level tests (React / jest) that simulate SSE messages via mocked EventSource and assert `Indexing.tsx` updates: stats, percent, currentObject, objectEvents history, and connection indicator.
    - Add an E2E test using Playwright (existing Playwright config is in the repo) or Cypress to simulate an indexing run with the backend test harness that produces SSE events and assert the UI updates in real time.

QA and CI tasks
- Task C1 — CI Integration Tests:
    - Run the SSE integration test as part of CI test suite (it should be stable — we already changed server tests to use raw HTTP streaming where needed).
    - Add a lightweight synthetic indexing job generator in test infra to ensure predictable events for E2E tests.

- Task C2 — Monitoring & Observability:
    - Add Prometheus metrics (or a StatsD metric) for SSE connections, events emitted per job, and event processing errors.
    - Add alerts for SSE publish failures or when an indexing job emits too many skipped events.

Rollout & backward compatibility notes
- UI should handle legacy servers that don't emit object-level events by continuing to poll `GET /api/indexing/jobs/latest` and showing aggregated stats only.
- Migrations: Add Flyway or Liquibase migrations for production DBs to include `indexing_objects` plus `total_folders` & `total_methods` fields (already done for H2). Provide a short `README` in `tools/planb` with commands to run migrations locally.

Estimated timeline
- Design & API contract: 0.5 day
- Backend dev & unit tests: 1–2 days
- Backend integration tests + migration: 0.5–1 day
- Frontend dev & unit tests: 1–2 days
- E2E tests + CI: 1 day

Deliverables / PR list (suggested)
1.  docs(api): SSE contract + openapi update
2.  backend: IndexingService SSE emission + indexing_objects persistence + migrations + unit/integration tests
3.  frontend: Indexing UI changes (currentObject, ObjectCard, SSE indicator) + unit tests
4.  e2e: Playwright tests exercising the SSE->UI update path
5.  monitoring: metrics & alerts

Acceptance tests (manual / automated)
- Manual: Start a test indexing run locally and observe the Indexing page shows live updates and the current object with duration/reason in real-time.
- Automated: E2E scenario that creates a synthetic job, streams a few object-start / object-end events, and asserts UI updates.

Notes
- Keep the UI parsing tolerant (null-safe) because older servers may have different payload shapes. Use defensive getters and default values in the frontend.

# Firestick LLM Service - Implementation Planning

**Version:** 1.0
**Date:** November 19, 2025
**Component:** LLM Service (Query Expansion & RAG)
**Based on:** `firestickLlmGoal.md`, `firestickLlmPrd.md`

---

## 1. Executive Summary

This planning document details the implementation steps for the "Query Expansion" and "Summarization" features (Pattern 3) for the Firestick project. The goal is to enhance the search experience by using a local LLM to bridge the gap between user queries and codebase terminology, and then synthesizing the results into a coherent answer.

## 2. Phased Implementation Plan

### Phase 1: Environment & Model Evaluation (Week 1)
**Goal:** Ensure the local environment supports the required models and select the best performing model for the task.

*   **Task 1.1:** Download candidate GGUF models to `models/` directory.
    *   DeepSeek Coder V2 (Lite/7B)
    *   Mistral 7B (v0.3)
    *   Llama 3 (8B)
*   **Task 1.2:** Update `llm-service` configuration to allow easy switching between models via `.env` or config file.
*   **Task 1.3:** Benchmark models against a set of 10 representative legacy code queries (e.g., "Where is auth?", "How are reports generated?"). Evaluate for:
    *   Speed (Tokens/sec)
    *   Instruction following (Did it give a list of keywords or a paragraph?)
    *   Relevance of keywords.

### Phase 2: Query Expansion Endpoint (Week 2)
**Goal:** Implement the pre-processing step in the Python `llm-service`.

*   **Task 2.1:** Create new API endpoint `POST /api/llm/expand-query`.
    *   Input: `{"query": "user string"}`
    *   Output: `{"expanded_terms": ["term1", "term2", ...]}`
*   **Task 2.2:** Develop Prompt Engineering for expansion.
    *   *Draft Prompt:* "You are an expert Java developer. Provide 5-10 technical keywords, class names, or concepts related to the following query for a legacy Java application. Do not explain, just list the terms."
*   **Task 2.3:** Implement parsing logic to extract clean list of terms from LLM response.

### Phase 3: Search Integration (Week 2-3)
**Goal:** Connect the Java backend to the new expansion endpoint and utilize the results.

*   **Task 3.1:** Update Java `LlmClient` to call `/api/llm/expand-query`.
*   **Task 3.2:** Modify the Search Service workflow:
    1.  Receive User Query.
    2.  Call Expansion Endpoint -> Get Terms.
    3.  **Lucene Search:** Construct Boolean Query: `(original_query) OR (term1 OR term2 ...)`
    4.  **Vector Search:** (Optional) Generate embedding for expanded string or average of terms.
*   **Task 3.3:** Aggregation logic to combine results from original query and expanded terms.

### Phase 5: High-Level Concept Indexing (Map-Reduce) (Week 4-5)
**Goal:** Implement Map-Reduce strategy to scale to 1M LOC and answer architectural questions.

*   **Task 5.1:** Database Schema Updates (Add `summary` to `CodeFile`, create `FolderSummary`).
*   **Task 5.2:** Implement "Map" Phase in Indexing Service (File Summarization).
*   **Task 5.3:** Implement "Reduce" Service (Folder Aggregation).
*   **Task 5.4:** Vectorize Summaries.
*   **Task 5.5:** Update Search to include Summaries.

### Phase 5b: Method-Level Summarization (Granular Insights) (Week 5)
**Goal:** Enhance precision by summarizing individual methods.

*   **Task 5b.1:** Database Schema Updates (Add `summary` to `CodeChunk`).
*   **Task 5b.2:** Update Indexing Service to summarize method chunks.
*   **Task 5b.3:** Update Search Service to index method summaries.

### Phase 6: UI Integration (Week 6)
**Goal:** Synthesize search results into a final answer.

*   **Task 4.1:** Create new API endpoint `POST /api/llm/answer-question`.
*   **Task 4.2:** Develop RAG Prompt.
*   **Task 4.3:** Handle context window limits.

### Phase 5: High-Level Concept Indexing (Map-Reduce) (Week 4-5)
**Goal:** Implement Map-Reduce strategy to scale to 1M LOC and answer architectural questions.

*   **Task 5.1:** Database Schema Updates.
    *   Add `summary` column to `CodeFile`.
    *   Create `FolderSummary` entity.
*   **Task 5.2:** Implement "Map" Phase in Indexing Service.
    *   Call LLM `summarize` endpoint during file parsing.
    *   Store result in `CodeFile`.
*   **Task 5.3:** Implement "Reduce" Service.
    *   Service to group file summaries by folder.
    *   Call LLM to generate folder summary.
*   **Task 5.4:** Vectorize Summaries.
    *   Generate embeddings for file and folder summaries.
    *   Add to Vector Store.
*   **Task 5.5:** Update Search to include Summaries.
    *   Include summary embeddings in search scope.
**Goal:** Synthesize search results into a final answer.

*   **Task 4.1:** Create new API endpoint `POST /api/llm/answer-question`.
    *   Input: `{"query": "...", "context_chunks": ["code snippet 1", "code snippet 2"]}`
    *   Output: `{"answer": "...", "citations": [...]}`
*   **Task 4.2:** Develop RAG Prompt.
    *   *Draft Prompt:* "Using the following code snippets, answer the user's original question: '{query}'. Cite specific classes or methods. If the answer is not in the context, state that."
*   **Task 4.3:** Handle context window limits (truncate chunks if necessary).

### Phase 5: UI Integration (Week 4)
**Goal:** Expose the new functionality to the user.

*   **Task 5.1:** Update Frontend Search Component.
    *   Add "Thinking..." state while LLM is processing.
    *   Display "Searching for related terms: [List]" to give user feedback.
*   **Task 5.2:** Render the LLM-generated answer at the top of the search results.
*   **Task 5.3:** Add "Regenerate" button to try with a different model or prompt (advanced).

---

## 3. Technical Architecture & Data Flow

1.  **User** -> **Web UI** -> **Java Backend** (SearchController)
2.  **Java Backend** -> **Python LLM Service** (`/expand-query`)
3.  **Python LLM Service** -> **Local LLM** -> Returns Synonyms
4.  **Java Backend** -> **Lucene/Vector Index** -> Returns Code Chunks
5.  **Java Backend** -> **Python LLM Service** (`/answer-question` with chunks)
6.  **Python LLM Service** -> **Local LLM** -> Returns Answer
7.  **Java Backend** -> **Web UI** (Answer + Source Links)

---

## 4. Risks & Mitigation

*   **Risk:** Latency is too high (>15s).
    *   *Mitigation:* Use smaller quantized models (Q4_K_M). Cache expansion results for common terms. Stream the final answer to the UI token-by-token (requires WebSocket/SSE).
*   **Risk:** LLM Hallucination (inventing classes).
    *   *Mitigation:* Strict prompting ("Only use provided context"). In Phase 3, verify expanded terms exist in the index before searching (optional optimization).
*   **Risk:** Context Window Overflow.
    *   *Mitigation:* Limit number of chunks sent to summarization (e.g., top 5). Use models with larger context windows (16k+).
