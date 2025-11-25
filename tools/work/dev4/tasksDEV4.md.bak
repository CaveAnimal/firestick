# Firestick - LLM Service Development Tasks

**Version:** 1.0
**Date:** November 19, 2025
**Project:** Firestick - LLM Service (Query Expansion & RAG)
**Repository:** firestick (CaveAnimal/firestick)
**Based on:** `firestickLlmPlanning.md`

---

## Task Summary
**Total Tasks:** 15 tasks
**Completed/Tested:** 15 tasks
**In Progress:** 0 tasks
**Blocked:** 0 tasks
**Percent Complete:** 100%
**Last Updated:** November 24, 2025

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

- `[X]` **Task 1.1:** Download candidate GGUF models to `models/` directory
	- `[X]` DeepSeek Coder V2 (Lite/7B)
	- `[X]` Mistral 7B (v0.3)
	- `[X]` Llama 3 (8B)
- `[X]` **Task 1.2:** Update `llm-service` configuration to allow easy switching between models via `.env` or config file
- `[X]` **Task 1.3:** Benchmark models against a set of 10 representative legacy code queries
	- [X] Evaluate for Speed (Tokens/sec)
	- [X] Evaluate for Instruction following
	- [X] Evaluate for Relevance of keywords

---

## Phase 2: Query Expansion Endpoint (Week 2)

**Status:** 0% Complete
**Goal:** Implement the pre-processing step in the Python `llm-service`.

- `[X]` **Task 2.1:** Create new API endpoint `POST /api/llm/expand-query`
	- [X] Input: `{"query": "user string"}`
	- [X] Output: `{"expanded_terms": ["term1", "term2", ...]}`
- `[X]` **Task 2.2:** Develop Prompt Engineering for expansion
	- [X] Draft Prompt: "You are an expert Java developer. Provide 5-10 technical keywords..."
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
	- [X] Input: `{"query": "...", "context_chunks": ["code snippet 1", "code snippet 2"]}`
	- [X] Output: `{"answer": "...", "citations": [...]}`
- `[X]` **Task 4.2:** Develop RAG Prompt
	- [X] Draft Prompt: "Using the following code snippets, answer the user's original question..."
- `[X]` **Task 4.3:** Handle context window limits (truncate chunks if necessary)

---

## Phase 5: High-Level Concept Indexing (Map-Reduce) (Week 4-5)

**Status:** 0% Complete
**Goal:** Implement Map-Reduce strategy to scale to 1M LOC and answer architectural questions.

- `[X]` **Task 5.1:** Database Schema Updates
	- `[X]` Add `summary` column to `CodeFile` table
	- `[X]` Create `FolderSummary` entity/table
- `[X]` **Task 5.2:** Implement "Map" Phase in Indexing Service
	- `[X]` Update `IndexingService` to call LLM `summarize` for each file
	- `[X]` Store summary in `CodeFile`
- `[X]` **Task 5.3:** Implement "Reduce" Service
	- `[X]` Create `SummaryAggregationService`
	- `[X]` Logic to group file summaries by folder
	- `[X]` Call LLM to generate folder summary
- `[X]` **Task 5.4:** Vectorize Summaries
	- `[X]` Generate embeddings for file and folder summaries
	- `[X]` Add to Vector Store (Chroma/Lucene)
- `[X]` **Task 5.5:** Update Search to include Summaries
	- `[X]` Include summary embeddings in search scope

## Phase 5b: Method-Level Summarization (Granular Insights) (Week 5)

**Status:** 0% Complete
**Goal:** Enhance precision by summarizing individual methods.

- `[X]` **Task 5b.1:** Database Schema Updates
	- `[X]` Add `summary` column to `CodeChunk` table
- `[X]` **Task 5b.2:** Update Indexing Service to summarize method chunks
	- `[X]` Iterate over chunks in `IndexingService`
	- `[X]` Call LLM `summarize` for method chunks (optionally filter by size)
	- `[X]` Store summary in `CodeChunk`
- `[X]` **Task 5b.3:** Update Search Service to index method summaries
	- `[X]` Index chunk summaries in Lucene

---

## Phase 6: UI Integration (Week 6)

**Status:** 0% Complete
**Goal:** Expose the new functionality to the user.

- `[X]` **Task 5.1:** Update Frontend Search Component
	- `[X]` Add "Thinking..." state while LLM is processing
	- `[X]` Display "Searching for related terms: [List]" to give user feedback
- `[X]` **Task 5.2:** Render the LLM-generated answer at the top of the search results
- `[X]` **Task 5.3:** Add "Regenerate" button to try with a different model or prompt (advanced)

---

## Phase 7: Indexing Console — Live, real-time progress & object-level reporting (New Goal)

**Status:** 0% Complete
**Goal:** Implement the Indexing Console end-to-end so the UI shows live progress counters, a `currentObject` card with duration or reason, and a small historical events feed. Tasks are broken into tiny, entry-level steps (open a file, add a method call, run a single test, commit) so beginners can follow them.

This section is strictly derived from the project's Goals, Planning and PRD. If anything is unclear at any step, add a `[!]` note in this file and request pairing help.

### Quick local dev checklist (one-liners you will run frequently)
- [X] Start backend:
```powershell
mvn spring-boot:run
```
- [X] Start frontend:
```powershell
cd ui
npm install
npm run dev
```
- [X] Run a small backend test only (fast):
```powershell
mvn -Dtest=IndexingJobControllerTest test
```
- [X] Run a single frontend unit test (jest):
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
	- [X] B1 — SSE contract in docs
	- [X] B2 — Add DB migration
	- [X] B3 — IndexingObject entity & repo
	- [X] B4 — Persist rows in IndexingService
	- [X] B5 — Emit SSE events at lifecycle points
	- [X] B6 — Add controller endpoint GET /api/indexing/jobs/{id}/objects
	- [X] F1 — Add currentObject state & ObjectCard UI
	- [X] F2 — Wire SSE to UI
	- [X] F3 — Add polling fallback
	- [X] F4 — UI unit tests for SSE
	- [X] F5 — E2E Playwright test
	- [X] T1 — Unit tests for emission & persistence
	- [X] T2 — Integration streaming test (raw HTTP)
	- [X] T3 — Frontend unit tests
	- [X] T4 — E2E in CI
	- [X] D1 — Verify / update API docs
	- [X] D2 — Add tools/planb README snippet for migrations/tests
	- [X] D3 — Rollout plan and monitoring items

Keep this checklist updated so a simple percent can be calculated. If you prefer I can add a small script to compute counts automatically from the markdown, let me know.

### Overall Phase 7 plan (very small steps)
For each major area (backend, frontend, tests, docs, CI) tasks are written as small numbered steps — each step should be one git commit and include a test change when relevant.

---

### Backend — step-by-step (B1 -> B6)

Goal: Emit per-object SSE events, persist indexing_objects rows, and add required controller endpoints and migrations.

- [X] B1 — SSE contract in docs (very small, 1–2 commits)
- [X] 1) Open `docs/API.md` and add the SSE event examples (copy from `tools/planb/firestickLlmPrd.md`) including: `object-start`, `object-end`, `object-skipped`, `progress`.
- [X] 2) Add a tiny paragraph describing timestamp format (epoch ms) and default values (0 or []).
- [X] 3) Commit with message: "docs(api): add indexing SSE contract examples".

Why: This keeps frontend and tests aligned immediately.

- [X] B2 — Add DB migration (Flyway recommended, small)
- [X] 1) Create `src/main/resources/db/migration/V2__create_indexing_objects.sql` with the minimal schema for `indexing_objects` and the `ALTER TABLE` statements for `total_folders` and `total_methods`.
- [X] 2) Add only the SQL above and save.
- [X] 3) Run a single test that loads the migration (optional local verification; do not run the whole test suite):
```powershell
mvn -q -DskipTests=false -Dtest=IndexingJobControllerTest test
```
- [X] 4) Commit as: "chore(db): add flyway migration V2__create_indexing_objects.sql".

Notes: If the project uses Liquibase instead, put a parallel changelog in `src/main/resources/db/changelog` and update README.

- [X] B3 — IndexingObject JPA entity + repository (tiny & testable)
- [X] 1) Add new Java entity `IndexingObject` in `src/main/java/com/codetalker/firestick/model/IndexingObject.java` with fields:
	- [X] id (Long @Id @GeneratedValue)
	- [X] jobId (Long)
	- [X] objectType (String)
	- [X] objectName (String)
	- [X] startedAt (Instant / Timestamp)
	- [X] endedAt (Instant / Timestamp)
	- [X] elapsedMs (Long)
	- [X] reasonSkipped (String)
- [X] 2) Add `IndexingObjectRepository` in `src/main/java/com/codetalker/firestick/repository/IndexingObjectRepository.java` extending `JpaRepository<IndexingObject, Long>` plus `List<IndexingObject> findByJobIdOrderByStartedAt(Long jobId, Pageable p)`.
- [X] 3) Add a unit test for repository basic save/read in `src/test/java/.../IndexingObjectRepositoryTest.java`.
- [X] 4) Commit: "feat(indexing): add IndexingObject entity + repository".

- [X] B4 — Persist lifecycle rows during indexing (safe incremental changes)
- [X] 1) Locate `src/main/java/com/codetalker/firestick/service/IndexingService.java`.
- [X] 2) Find the place where the service starts processing an object (file/method/folder). Insert a call to create and save a new `IndexingObject` with startedAt timestamp. Keep code guarded with a null-check or feature flag if desired.
- [X] 3) When object completes, update the previously-saved `IndexingObject` row with endedAt and elapsedMs; if skipped set `reasonSkipped`.
- [X] 4) Add small unit tests verifying that IndexingService calls repository.save twice (you can mock repository in a unit test, or run an integration that asserts row exists). Suggested unit test class: `IndexingServiceObjectPersistenceTest` in `src/test/java/...`.
- [X] 5) Commit: "feat(indexing): persist per-object rows during indexing".

- [X] B5 — Emit SSE events at lifecycle points (small commits, test-driven)
- [X] 1) Find existing ProgressBus (likely `src/main/java/com/codetalker/firestick/service/ProgressBus.java` or similar). Add or confirm a `publish(long jobId, Map<String,Object> payload)` method exists.
- [X] 2) On the same code paths from B4 where you save indexing rows, publish the SSE events using ProgressBus.publish(jobId, Map.of(...)) with payload matching the documented shapes.
- [X] 3) Add unit tests that mock ProgressBus to assert payloads were sent on object-start / object-end / object-skipped.
- [X] 4) Commit: "feat(sse): emit object-level start/end/skip events".

- [X] B6 — Controller endpoint for objects list (small)
- [X] 1) Add `GET /api/indexing/jobs/{id}/objects` in `IndexingJobController.java` that returns paged `IndexingObject` rows for a job.
 - [X] 2) Accept query params: page, size, sort and optional `type=file|method|folder|all` to filter.
 - [X] 3) Add tests: `IndexingJobControllerObjectsEndpointTest.java` that creates sample objects and asserts the endpoint returns the correct paging and filters.
 - [X] 4) Commit: "feat(api): add GET /api/indexing/jobs/{id}/objects endpoint".

... (file continues with the rest of canonical content) ...

