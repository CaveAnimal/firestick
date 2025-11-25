# Firestick - LLM Service Development Tasks

**Version:** 1.0
**Date:** November 19, 2025
**Project:** Firestick - LLM Service (Query Expansion & RAG)
**Repository:** firestick (CaveAnimal/firestick)
**Based on:** `firestickLlmPlanning.md`

---

## Task Summary

**Total Tasks:** 15 tasks
**Completed/Tested:** 0 tasks
**In Progress:** 0 tasks
**Blocked:** 0 tasks
**Percent Complete:** 0%
**Last Updated:** November 19, 2025

## Task Management System

### Task Status Symbols
- `[ ]` Not Started
- `[-]` In Progress
- `[X]` Completed
- `[V]` Tested & Verified
- `[!]` Blocked
- `[>]` Deferred (include reason on next line)

### How to Use This Document
1. **Update Status Daily**: Change task status symbols as you work
2. **Add Notes**: Write brief notes under tasks about challenges or decisions
3. **Track Time**: Estimate time in parentheses, e.g., `(2h)` means 2 hours
4. **Break Down Tasks**: If a task takes more than 4 hours, break it into sub-tasks
5. **Ask for Help**: Use `[!]` when blocked and document what's blocking you
6. **Test Everything**: Move from `[X]` to `[V]` only after testing

---

## Phase 1: Environment & Model Evaluation (Week 1)

**Status:** 0% Complete
**Goal:** Ensure the local environment supports the required models and select the best performing model for the task.

- `[ ]` **Task 1.1:** Download candidate GGUF models to `models/` directory
    - `[ ]` DeepSeek Coder V2 (Lite/7B)
    - `[ ]` Mistral 7B (v0.3)
    - `[ ]` Llama 3 (8B)
- `[X]` **Task 1.2:** Update `llm-service` configuration to allow easy switching between models via `.env` or config file
- `[X]` **Task 1.3:** Benchmark models against a set of 10 representative legacy code queries
    - [ ] Evaluate for Speed (Tokens/sec)
    - [ ] Evaluate for Instruction following
    - [ ] Evaluate for Relevance of keywords

---

## Phase 2: Query Expansion Endpoint (Week 2)

**Status:** 0% Complete
**Goal:** Implement the pre-processing step in the Python `llm-service`.

- `[X]` **Task 2.1:** Create new API endpoint `POST /api/llm/expand-query`
    - [ ] Input: `{"query": "user string"}`
    - [ ] Output: `{"expanded_terms": ["term1", "term2", ...]}`
- `[X]` **Task 2.2:** Develop Prompt Engineering for expansion
    - [ ] Draft Prompt: "You are an expert Java developer. Provide 5-10 technical keywords..."
- `[X]` **Task 2.3:** Implement parsing logic to extract clean list of terms from LLM response

---

## Phase 3: Search Integration (Week 2-3)

**Status:** 0% Complete
**Goal:** Connect the Java backend to the new expansion endpoint and utilize the results.

- `[X]` **Task 3.1:** Update Java `LlmClient` to call `/api/llm/expand-query`
- `[X]` **Task 3.2:** Modify the Search Service workflow
    - `[X]` Receive User Query
    - `[X]` Call Expansion Endpoint -> Get Terms
    - `[X]` **Lucene Search:** Construct Boolean Query: `(original_query) OR (term1 OR term2 ...)`
    - `[X]` **Vector Search:** (Optional) Generate embedding for expanded string or average of terms
- `[X]` **Task 3.3:** Aggregation logic to combine results from original query and expanded terms

---

## Phase 4: Summarization (RAG) Endpoint (Week 3)

**Status:** 0% Complete
**Goal:** Synthesize search results into a final answer.

- `[X]` **Task 4.1:** Create new API endpoint `POST /api/llm/answer-question`
    - [ ] Input: `{"query": "...", "context_chunks": ["code snippet 1", "code snippet 2"]}`
    - [ ] Output: `{"answer": "...", "citations": [...]}`
- `[X]` **Task 4.2:** Develop RAG Prompt
    - [ ] Draft Prompt: "Using the following code snippets, answer the user's original question..."
- `[X]` **Task 4.3:** Handle context window limits (truncate chunks if necessary)

---

## Phase 5: High-Level Concept Indexing (Map-Reduce) (Week 4-5)

**Status:** 0% Complete
**Goal:** Implement Map-Reduce strategy to scale to 1M LOC and answer architectural questions.

- `[X]` **Task 5.1:** Database Schema Updates
    - `[X]` Add `summary` column to `CodeFile` table
    - `[X]` Create `FolderSummary` entity/table
- `[X]` **Task 5.2:** Implement "Map" Phase in Indexing Service
    - `[ ]` Update `IndexingService` to call LLM `summarize` for each file
    - `[ ]` Store summary in `CodeFile`
- `[X]` **Task 5.3:** Implement "Reduce" Service
    - `[ ]` Create `SummaryAggregationService`
    - `[ ]` Logic to group file summaries by folder
    - `[ ]` Call LLM to generate folder summary
- `[X]` **Task 5.4:** Vectorize Summaries
    - `[ ]` Generate embeddings for file and folder summaries
    - `[ ]` Add to Vector Store (Chroma/Lucene)
- `[X]` **Task 5.5:** Update Search to include Summaries
    - `[ ]` Include summary embeddings in search scope

## Phase 5b: Method-Level Summarization (Granular Insights) (Week 5)

**Status:** 0% Complete
**Goal:** Enhance precision by summarizing individual methods.

- `[X]` **Task 5b.1:** Database Schema Updates
    - `[ ]` Add `summary` column to `CodeChunk` table
- `[X]` **Task 5b.2:** Update Indexing Service to summarize method chunks
    - `[ ]` Iterate over chunks in `IndexingService`
    - `[ ]` Call LLM `summarize` for method chunks (optionally filter by size)
    - `[ ]` Store summary in `CodeChunk`
- `[X]` **Task 5b.3:** Update Search Service to index method summaries
    - `[ ]` Index chunk summaries in Lucene

---

## Phase 6: UI Integration (Week 6)

**Status:** 0% Complete
**Goal:** Expose the new functionality to the user.

- `[X]` **Task 5.1:** Update Frontend Search Component
    - `[ ]` Add "Thinking..." state while LLM is processing
    - `[ ]` Display "Searching for related terms: [List]" to give user feedback
- `[X]` **Task 5.2:** Render the LLM-generated answer at the top of the search results
- `[ ]` **Task 5.3:** Add "Regenerate" button to try with a different model or prompt (advanced)
 - `[X]` **Task 5.3:** Add "Regenerate" button to try with a different model or prompt (advanced)

---

## Phase 7: Indexing Console — Live, real-time progress & object-level reporting (New Goal)

**Status:** 0% Complete
**Goal:** Implement the Indexing Console end-to-end so the UI shows live progress counters, a `currentObject` card with duration or reason, and a small historical events feed. Tasks are broken into tiny, entry-level steps (open a file, add a method call, run a single test, commit) so beginners can follow them.

This section is strictly derived from the project's Goals, Planning and PRD. If anything is unclear at any step, add a `[!]` note in this file and request pairing help.

### Quick local dev checklist (one-liners you will run frequently)
-- [ ] Start backend:
```powershell
mvn spring-boot:run
```
-- [ ] Start frontend:
```powershell
cd ui
npm install
npm run dev
```
-- [ ] Run a small backend test only (fast):
```powershell
mvn -Dtest=IndexingJobControllerTest test
```
-- [ ] Run a single frontend unit test (jest):
```powershell
cd ui
npm test -- -t "Indexing"
```

---

### Phase 7 — Task status tracker
Use the checklist below to report progress for Phase 7. Update checkboxes as you complete steps; the top `Status` line above can be manually updated to reflect the percent-complete.

- **Phase 7 - Totals:**
    - Total items (top-level): 18
    - Completed: 0
    - In Progress: 0
    - Percent Complete: 0%

- Top-level task checklist (mark `[X]` when a top-level area is complete):
    - [ ] B1 — SSE contract in docs
    - [ ] B2 — Add DB migration
    - [ ] B3 — IndexingObject entity & repo
    - [ ] B4 — Persist rows in IndexingService
    - [ ] B5 — Emit SSE events at lifecycle points
    - [ ] B6 — Add controller endpoint GET /api/indexing/jobs/{id}/objects
    - [ ] F1 — Add currentObject state & ObjectCard UI
    - [ ] F2 — Wire SSE to UI
    - [ ] F3 — Add polling fallback
    - [ ] F4 — UI unit tests for SSE
    - [ ] F5 — E2E Playwright test
    - [ ] T1 — Unit tests for emission & persistence
    - [ ] T2 — Integration streaming test (raw HTTP)
    - [ ] T3 — Frontend unit tests
    - [ ] T4 — E2E in CI
    - [ ] D1 — Verify / update API docs
    - [ ] D2 — Add tools/planb README snippet for migrations/tests
    - [ ] D3 — Rollout plan and monitoring items

Keep this checklist updated so a simple percent can be calculated. If you prefer I can add a small script to compute counts automatically from the markdown, let me know.

### Overall Phase 7 plan (very small steps)
For each major area (backend, frontend, tests, docs, CI) tasks are written as small numbered steps — each step should be one git commit and include a test change when relevant.

---

### Backend — step-by-step (B1 -> B6)

Goal: Emit per-object SSE events, persist indexing_objects rows, and add required controller endpoints and migrations.

- [ ] B1 — SSE contract in docs (very small, 1–2 commits)
- [ ] 1) Open `docs/API.md` and add the SSE event examples (copy from `tools/planb/firestickLlmPrd.md`) including: `object-start`, `object-end`, `object-skipped`, `progress`.
- [ ] 2) Add a tiny paragraph describing timestamp format (epoch ms) and default values (0 or []).
- [ ] 3) Commit with message: "docs(api): add indexing SSE contract examples".

Why: This keeps frontend and tests aligned immediately.

- [ ] B2 — Add DB migration (Flyway recommended, small)
- [ ] 1) Create `src/main/resources/db/migration/V2__create_indexing_objects.sql` with the minimal schema for `indexing_objects` and the `ALTER TABLE` statements for `total_folders` and `total_methods`.
- [ ] 2) Add only the SQL above and save.
- [ ] 3) Run a single test that loads the migration (optional local verification; do not run the whole test suite):
```powershell
mvn -q -DskipTests=false -Dtest=IndexingJobControllerTest test
```
- [ ] 4) Commit as: "chore(db): add flyway migration V2__create_indexing_objects.sql".

Notes: If the project uses Liquibase instead, put a parallel changelog in `src/main/resources/db/changelog` and update README.

- [ ] B3 — IndexingObject JPA entity + repository (tiny & testable)
- [ ] 1) Add new Java entity `IndexingObject` in `src/main/java/com/codetalker/firestick/model/IndexingObject.java` with fields:
    - [ ] id (Long @Id @GeneratedValue)
    - [ ] jobId (Long)
    - [ ] objectType (String)
    - [ ] objectName (String)
    - [ ] startedAt (Instant / Timestamp)
    - [ ] endedAt (Instant / Timestamp)
    - [ ] elapsedMs (Long)
    - [ ] reasonSkipped (String)
- [ ] 2) Add `IndexingObjectRepository` in `src/main/java/com/codetalker/firestick/repository/IndexingObjectRepository.java` extending `JpaRepository<IndexingObject, Long>` plus `List<IndexingObject> findByJobIdOrderByStartedAt(Long jobId, Pageable p)`.
- [ ] 3) Add a unit test for repository basic save/read in `src/test/java/.../IndexingObjectRepositoryTest.java`.
- [ ] 4) Commit: "feat(indexing): add IndexingObject entity + repository".

- [ ] B4 — Persist lifecycle rows during indexing (safe incremental changes)
- [ ] 1) Locate `src/main/java/com/codetalker/firestick/service/IndexingService.java`.
- [ ] 2) Find the place where the service starts processing an object (file/method/folder). Insert a call to create and save a new `IndexingObject` with startedAt timestamp. Keep code guarded with a null-check or feature flag if desired.
- [ ] 3) When object completes, update the previously-saved `IndexingObject` row with endedAt and elapsedMs; if skipped set `reasonSkipped`.
- [ ] 4) Add small unit tests verifying that IndexingService calls repository.save twice (you can mock repository in a unit test, or run an integration that asserts row exists). Suggested unit test class: `IndexingServiceObjectPersistenceTest` in `src/test/java/...`.
- [ ] 5) Commit: "feat(indexing): persist per-object rows during indexing".

- [ ] B5 — Emit SSE events at lifecycle points (small commits, test-driven)
- [ ] 1) Find existing ProgressBus (likely `src/main/java/com/codetalker/firestick/service/ProgressBus.java` or similar). Add or confirm a `publish(long jobId, Map<String,Object> payload)` method exists.
- [ ] 2) On the same code paths from B4 where you save indexing rows, publish the SSE events using ProgressBus.publish(jobId, Map.of(...)) with payload matching the documented shapes.
- [ ] 3) Add unit tests that mock ProgressBus to assert payloads were sent on object-start / object-end / object-skipped.
- [ ] 4) Commit: "feat(sse): emit object-level start/end/skip events".

- [ ] B6 — Controller endpoint for objects list (small)
- [ ] 1) Add `GET /api/indexing/jobs/{id}/objects` in `IndexingJobController.java` that returns paged `IndexingObject` rows for a job.
 - [ ] 2) Accept query params: page, size, sort and optional `type=file|method|folder|all` to filter.
 - [ ] 3) Add tests: `IndexingJobControllerObjectsEndpointTest.java` that creates sample objects and asserts the endpoint returns the correct paging and filters.
 - [ ] 4) Commit: "feat(api): add GET /api/indexing/jobs/{id}/objects endpoint".

---

### Frontend — step-by-step (F1 -> F5)

Goal: Show live counters, connection indicator, currentObject card, recent events feed, and fallback polling.

- [ ] F1 — Add `currentObject` and `objectEvents` UI state (very small)
 - [ ] 1) Open `ui/src/pages/Indexing.tsx`.
 - [ ] 2) Add state variables near existing job/progress state:
```ts
const [currentObject, setCurrentObject] = useState<IndexedObject | null>(null)
const [objectEvents, setObjectEvents] = useState<Array<IndexedObjectEvent>>([])
```
 - [ ] 3) Create a very small `ObjectCard` component inline or `ui/src/components/ObjectCard.tsx` that renders name, type, startedAt, elapsed or reason.
 - [ ] 4) Commit: "ui(indexing): add currentObject state + ObjectCard".

- [ ] F2 — Wire SSE to UI (small incremental)
 - [ ] 1) Find `startSse(jobId)` in `ui/src/pages/Indexing.tsx` (EventSource logic). Extend the message handling to support `object-start`, `object-end`, `object-skipped`, and `progress`.
 - [ ] 2) On `object-start` set `currentObject` to event payload and start a client-side timer (Date.now()) if required.
 - [ ] 3) On `object-end` update `currentObject` with elapsedMs, push a summary into `objectEvents` and keep the most recent 20 entries.
 - [ ] 4) On `progress` merge counters into current job state.
 - [ ] 5) Add a visible connection indicator (green/yellow/red) near the top of the progress card (use existing `sseConnected` state or add it).
 - [ ] 6) Commit: "ui(indexing): wire SSE events to currentObject & counters".

- [ ] F3 — Polling fallback (small)
 - [ ] 1) Implement a fallback path in the SSE client: upon N consecutive connection failures (e.g., 3), begin polling `GET /api/indexing/jobs/latest` every 2s.
 - [ ] 2) Make the UI show a small text label near the connection indicator: "Polling".
 - [ ] 3) Commit: "ui(indexing): add polling fallback and indicator".

- [ ] F4 — UI unit tests (jest) (small)
 - [ ] 1) Create `ui/src/tests/Indexing.sse.test.tsx`.
 - [ ] 2) Mock `EventSource` to simulate the three event types and a `progress` message.
 - [ ] 3) Assert that counters update, `currentObject` reflects the last object, and `objectEvents` includes finished objects.
 - [ ] 4) Commit: "test(ui): add Indexing SSE unit tests".

- [ ] F5 — E2E Playwright test (single controlled scenario)
 - [ ] 1) Add test `ui/tests/indexing.sse.e2e.ts` that uses the existing Playwright harness. Test should:
   a) Start a small synthetic indexing job via backend test helper (if present),
   b) Navigate to Indexing page,
   c) Assert that `ObjectCard` updates on `object-start` and `object-end` and counters increment.
 - [ ] 2) Keep it small and deterministic — upload a 2-file synthetic job to the test DB.
 - [ ] 3) Commit: "e2e(indexing): add SSE end-to-end test (small)".

---

### Tests & CI — step-by-step (T1 -> T4)

Goal: Add unit and integration tests for emission, persistence and streaming; run a single targeted test locally during development.

- [ ] T1 — Unit tests for IndexingService emission & persistence (tiny TDD loop)
 - [ ] 1) Add `IndexingServiceObjectPersistenceTest` verifying repository.save called.
 - [ ] 2) Add `IndexingServiceObjectEventsTest` verifying ProgressBus.publish calls on object lifecycle.
 - [ ] 3) Run those tests locally by name: `mvn -Dtest=IndexingServiceObjectPersistenceTest test`.
 - [ ] 4) Commit: "test(indexing): add unit tests for object persistence & events".

- [ ] T2 — Integration streaming test (raw HTTP streaming, single scenario)
 - [ ] 1) Add server-side integration test `SseIntegrationTest` that
    - [ ] Starts a small in-memory indexing job
    - [ ] Connects to `/api/indexing/stream?jobId=<id>` using raw HTTP streaming
    - [ ] Asserts at least one `object-start` and `object-end` event are received and final `progress.percent >= 100`.
 - [ ] 2) Keep test short by using a tiny synthetic dataset.
 - [ ] 3) Add the test to CI, but mark it isolated/focused so it doesn't slow full suite.
 - [ ] 4) Commit: "test(integration): add SSE streaming integration test".

- [ ] T3 — Frontend unit tests (jest) for SSE handling
 - [ ] 1) Ensure `Indexing.sse.test.tsx` passes locally (see F4). Use mocked EventSource. Keep tests tiny.
 - [ ] 2) Commit: "test(ui): indexing SSE mocked tests".

- [ ] T4 — E2E in CI (Playwright)
 - [ ] 1) Add the Playwright test (F5) to CI in a temporary 'indexing-sse' gated job.
 - [ ] 2) Run the job multiple times until flakiness is resolved. Use small timeouts & retry logic if necessary.
 - [ ] 3) Commit: "ci(e2e): add indexing SSE E2E test".

---

### Documentation & rollout (D1 -> D3)

- [ ] D1 — API docs (done earlier but verify)
 - [ ] 1) Confirm `docs/API.md` includes the SSE examples and format. If not, update it.
 - [ ] 2) Commit: "docs(api): verify SSE examples"

- [ ] D2 — Dev README and migration snippet
 - [ ] 1) Add `tools/planb/README.md` snippet with quick commands to run the migration(s) locally and create a synthetic indexing job for testing.
 - [ ] 2) Include the sample `curl` for SSE debugging: `curl -N http://localhost:8001/api/indexing/stream?jobId=<id>`.
 - [ ] 3) Commit: "docs(planb): add Phase7 migration & debug guide".

- [ ] D3 — Rollout plan and monitoring (small)
 - [ ] 1) Add a migration checklist to the README: feature flags, canary rollout, DB migration plan.
 - [ ] 2) Add Server counters and logs for SSE publish success/fail.
 - [ ] 3) Commit: "chore(deploy): add rollout checklist for indexing SSE".

---

### Example SSE payloads to copy into tests and UI (small convenient snippet)
Use these exact samples in tests & docs so frontend and backend share a single source of truth.

object-start
```json
{ "event": "object-start", "jobId": 123, "objectId": 456, "type": "file", "name": "src/main/java/.../MyClass.java", "ts": 1690000000000 }
```

object-end
```json
{ "event": "object-end", "jobId": 123, "objectId": 456, "type": "file", "name": "src/.../MyClass.java", "ts": 1690000001000, "elapsedMs": 1000 }
```

object-skipped
```json
{ "event": "object-skipped", "jobId": 123, "objectId": 457, "type": "file", "name": "src/.../Ignored.js", "ts": 1690000001005, "reason": "unsupported extension" }
```

progress
```json
{ "event": "progress", "jobId": 123, "percent": 34, "filesDiscovered": 100, "filesParsed": 34, "chunksProduced": 50, "documentsIndexed": 48, "embeddingsGenerated": 48, "filesSummarized": 12, "foldersSummarized": 10, "methodsSummarized": 22, "skippedFiles": [] }
```

---

### Bite-sized acceptance checks (PUT A CHECK next to each once done)
- [ ] Backend emits `object-start`, `object-end`, `object-skipped` events and persists rows
- [ ] `GET /api/indexing/jobs/{id}/objects` returns paged per-object rows
- [ ] Frontend Indexing page shows live counters, `currentObject` with elapsed/ reason and a recent events feed
- [ ] Frontend shows streaming connection status and falls back to polling after failures
- [ ] Unit & integration tests cover emission + persistence + SSE streaming
- [ ] Small Playwright E2E proves the end-to-end flow in CI

### Suggested day-by-day micro-plan for one entry-level dev
Day 1: B1 docs + B3 small unit test wiring + F1 placeholder component
Day 2: B3 emit events + unit tests + B4 small persistence wiring
Day 3: B5 ProgressBus publish checks + B6 controller endpoint + backend tests
Day 4: F2 SSE wiring + F3 polling fallback + F4 unit tests
Day 5: T2 integration test for SSE + F5 E2E Playwright + CI wiring

If you'd like, I can now apply this change to `tools/planb/firestickLlmTasks.md` (done) and then create a matching, synchronized copy in `tools/work/dev4/tasksDEV4.md`.

