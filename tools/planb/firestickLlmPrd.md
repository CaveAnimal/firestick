# Firestick LLM Service - Product Requirements Document (PRD)

**Version:** 1.0
**Date:** November 19, 2025
**Component:** LLM Service & Query Expansion
**Parent Project:** Firestick

---

## 1. Executive Summary

### 1.1 Vision
The Firestick LLM Service aims to enhance the code discovery process by bridging the semantic gap between natural language user queries and the technical terminology used in the codebase. By implementing **Query Expansion (Pattern 3)**, the system will intelligently broaden search terms to improve recall and provide more accurate, context-aware answers.

### 1.2 Problem Statement
Users often search using high-level concepts (e.g., "auth", "saving") which may not match the specific implementation terms (e.g., "LDAP", "PersistenceManager") in the legacy codebase. This leads to poor search results and missed relevant code sections.

---

## 2. Product Goals

1.  **Improve Search Recall:** Increase the number of relevant code chunks retrieved by expanding user queries with technical synonyms.
2.  **Enhance Answer Quality:** Provide synthesized, context-aware answers citing specific classes and methods.
3.  **Maintain Offline Privacy:** All LLM processing must occur locally without external API calls.
4.  **Model Agnostic:** Support swapping underlying LLM models (CodeLlama, DeepSeek, etc.) via GGUF format.

---

## 3. Functional Requirements

### 3.1 Query Expansion (Pre-processing)
The system must intercept the user's raw query and process it before searching the index.

*   **Input:** User natural language query (e.g., "Where is auth handled?").
*   **Processing:**
    *   Prompt the local LLM to act as an expert Java developer.
    *   Generate 5-10 related technical keywords, class names, or concepts.
    *   *Example Output:* "Login, Security, LDAP, RBAC, SessionManager, AuthenticationProvider".
*   **Output:** A list of expanded search terms.

### 3.2 Enhanced Search Execution
The system must utilize the expanded terms to query the underlying search engines.

*   **Vector Search:** Generate embeddings for the expanded terms to find semantically related code.
*   **Keyword Search (Lucene):** Construct Boolean OR queries using the expanded terms to find exact text matches in code and comments.
*   **Result Aggregation:** Combine and rank results from both search methods.

### 3.3 Result Summarization
The system must synthesize the retrieved code chunks into a final answer.

### 3.4 Method-Level Summarization
The system must generate and store summaries for individual methods to support granular search and analysis.

*   **Input:** Code chunks of type "method".
*   **Processing:**
    *   Identify method chunks (optionally filter by length, e.g., > 50 lines).
    *   Send chunk content to LLM for summarization.
    *   Store summary in `CodeChunk` entity.
*   **Output:** Persisted method summaries available for search and aggregation.

### 3.4 Map-Reduce Summarization (High-Level Concepts)
The system must support hierarchical summarization to handle 1M+ LOC and answer architectural questions.

*   **Map Phase (File Enrichment):**
    *   The Indexing Service must invoke the LLM for every parsed file.
    *   Generate a structured summary (Purpose, Responsibilities, Patterns, Dependencies).
    *   Store summary in `CodeFile` entity.
*   **Reduce Phase (Module Aggregation):**
    *   The system must be able to aggregate file summaries by folder/package.
    *   Generate a module-level summary using the LLM.
    *   Store in a new `FolderSummary` entity.
*   **Scalability:**
    *   The architecture must support processing 1M+ LOC.
    *   Summarization should be asynchronous or batched to avoid blocking the main indexing flow.
*   **Concept Search:**
    *   Summaries must be embedded and indexed in the Vector Database.
    *   Search must include these summary embeddings to match high-level concepts (e.g., "Security Layer", "Business Logic").

---

## 4. Data Requirements

### 4.1 CodeFile Updates
*   New column: `summary` (TEXT/CLOB) - Stores the LLM-generated file summary.
*   New column: `patterns` (JSON/TEXT) - Stores detected design patterns.

### 4.2 New Entity: FolderSummary
*   `id`: Unique Identifier
*   `path`: Folder path (e.g., `src/main/java/com/codetalker/auth`)
*   `summary`: LLM-generated module description.
*   `embedding`: Vector representation of the summary.

---

## 5. Non-Functional Requirements
*   **Performance:** Map phase should not increase indexing time by more than 3x.
*   **Reliability:** Failure to summarize a single file should not fail the entire indexing job.
*   **Storage:** Database schema must accommodate text summaries for potentially 100k+ files.

*   **Input:** The original user query and the top-ranked code chunks.
*   **Processing:** Prompt the LLM to answer the question using *only* the provided context.
*   **Output:** A natural language response citing specific classes, methods, and files.

---

## 4. Technical Specifications

### 4.1 LLM Integration
*   **Model Format:** GGUF (via `llama.cpp` python bindings).
*   **Recommended Models:** CodeLlama 7B, DeepSeek Coder V2, or Mistral 7B.
*   **Context Window:** Must support at least 4096 tokens to handle multiple code chunks.

### 4.2 Performance Targets
*   **Expansion Latency:** < 2 seconds for generating synonyms.
*   **Summarization Latency:** < 10 seconds for final answer generation.
*   **Total Turnaround:** < 15 seconds per query.

---

## 5. User Interaction Flow

1.  **User** types "Where is auth handled?" into the search bar.
2.  **UI** shows a "Thinking..." or "Expanding query..." indicator.
3.  **System** internally expands query to "Login, Security, LDAP...".
4.  **System** searches and retrieves relevant code (e.g., `CustomAuthenticationProvider.java`).
5.  **System** generates a summary.
6.  **UI** displays:
    *   **Direct Answer:** "Authentication is primarily handled in `CustomAuthenticationProvider`..."
    *   **Source Links:** Clickable links to the referenced files.
    *   **Expanded Terms:** (Optional) "Searched for: Auth, Login, LDAP, RBAC..."

    ---

    ## 6. Product Requirement: Indexing Console — Live, real-time progress & object-level reporting

    ### 6.1 Summary
    The Indexing Console must provide a real-time, live view of in-flight indexing jobs. It should display up-to-date counters (files discovered, parsed, chunks produced, documents indexed, embeddings generated, files summarized, folders summarized, methods summarized, skipped files), a computed percent/progress value, and the currently-processing object — showing either a duration (elapsed time for an object that completed) or a short human-readable reason for skipped/failed objects.

    This feature will be driven primarily by SSE (Server-Sent Events) from the backend; polling is a fallback when streaming is unavailable. The server must persist per-object telemetry (if not already), and the frontend must merge object-level events and aggregated progress updates for a smooth and resilient UI.

    ### 6.2 User stories
    1.  As a developer, I want to see live counters while indexing runs so I can verify progress and detect anomalies immediately.
    2.  As an operator, I want to see the currently-processed object and its elapsed duration or a reason when skipped, so I can quickly triage indexing failures.
    3.  As a QA engineer, I want the UI to fall back to polling if streaming fails so the user always sees an up-to-date snapshot.

    ### 6.3 Success metrics
    - Real-time update frequency: UI reflects server updates with <1 second latency for object-level events in local envs.
    - Accuracy: UI counters match backend persisted totals within ±1 per update (eventual consistency allowed for in-flight stats).
    - Reliability: SSE connections stay open and deliver events for 95%+ of local test runs; CI tests validate event delivery.
    - UX: Manual test confirms current object view shows a duration or skip reason in 100% of observed object lifecycle events.

    ### 6.4 API / Event contract (summary)
    Design SSE message shapes that backend emits over `/api/indexing/stream?jobId=<id>`.

    object-start
    ```
    { "event": "object-start", "jobId": 123, "objectId": 456, "type": "file", "name": "src/main/java/.../MyClass.java", "ts": 1690000000000 }
    ```

    object-end
    ```
    { "event": "object-end", "jobId": 123, "objectId": 456, "type": "file", "name": "src/.../MyClass.java", "ts": 1690000001000, "elapsedMs": 1000 }
    ```

    object-skipped
    ```
    { "event": "object-skipped", "jobId": 123, "objectId": 457, "type": "file", "name": "src/.../Ignored.js", "ts": 1690000001005, "reason": "unsupported extension" }
    ```

    progress (aggregated counters)
    ```
    { "event": "progress", "jobId": 123, "percent": 34, "filesDiscovered": 100, "filesParsed": 34, "chunksProduced": 50, "documentsIndexed": 48, "embeddingsGenerated": 48, "filesSummarized": 12, "foldersSummarized": 10, "methodsSummarized": 22, "skippedFiles": [] }
    ```

    Notes:
    - Fields must be present or safely defaulted (0, empty-array) so UI deserialization is robust.
    - Timestamps are epoch ms UTC. Object IDs are optional but useful for correlating DB rows.

    ### 6.5 UX considerations
    - Connection indicator (green dot / yellow / red) near the progress header to reflect streaming status (Connected / Reconnecting / Polling).
    - Current Object card: name (monospace), type badge (file|folder|method), startedAt timestamp, live elapsed timer during processing, final duration on completion, or a short reason text if skipped.
    - Recent events feed: a compact list of recent object-start, object-end, object-skipped events, with timestamps and short summaries.

    ### 6.6 Acceptance criteria
    1.  Indexing page displays live updates for all counters and computed percent while job is active.
    2.  Current Object shows a live timer while in-flight and final duration (or reason if skipped) when completed.
    3.  SSE-based streaming connected state is visible and reconnect/backoff behavior is implemented (with polling fallback after several failures).
    4.  UI unit tests simulate SSE messages and assert proper state updates for counters, percent, currentObject, and objectEvents.
    5.  Integration tests (server & test client) validate SSE stream sends object lifecycle events and aggregated progress to connected clients.

    ### 6.7 Edge cases & constraints
    - If job has already completed (progress >= 100) UI should not attach SSE but can show final persisted stats.
    - Older servers may not publish object-level events; UI must gracefully handle aggregated-only payloads and use polling.
    - Avoid sending extremely verbose messages (no huge payloads in SSE). Object payloads should include necessary metadata only.

    ### 6.8 Rollout & migration plan
    1.  Add DB migration (Flyway/Liquibase) to ensure `indexing_objects` table exists and `total_folders` / `total_methods` columns are present in production DBs.
    2.  Release backend changes with feature flags (if desired) so we can enable SSE payloads gradually.
    3.  Release frontend changes that handle both streaming and polling modes, with e2e tests enabled in CI.
    4.  Monitor SSE connections and error rates for 48–72 hours post-deploy, rollback if metrics indicate issues in production.

    ### 6.9 Testing checklist (QA/CI)
    - Unit tests (backend): verify events emitted at lifecycle points and DB records persisted.
    - Integration test (server-client streaming): open stream for a test job and assert object-start / object-end / progress events seen.
    - Frontend unit tests: mocked EventSource feeds and assertions for UI updates (counters, percent, currentObject, objectEvents history, connection indicator).
    - E2E: Playwright scenario that kicks off a synthetic indexing job and asserts real-time UI updates for a small job.

    ### 6.10 Success & metrics to capture post-launch
    - Percent of indexing runs where SSE clients see at least one object event (target >= 95%).
    - Median SSE event latency (server -> rendered UI) under local conditions (goal < 500ms).
    - Rate of jobs with large numbers of skipped objects; use sliding window alerts if rate exceeds threshold.

