# Firestick LLM Goal: Pattern 3 - Query Expansion (Pre-processing)

This document outlines the strategy for implementing Query Expansion to improve search recall and accuracy within the Firestick application.

## Overview

Query Expansion is a pre-processing step where the user's initial natural language query is enriched with synonyms, related technical terms, and domain-specific concepts before being executed against the search index. This helps bridge the gap between how a user asks a question (e.g., "auth") and how the code is actually written (e.g., "LDAP", "SecurityContext").

## Workflow

### Step 1: User Query
**Input:** The user asks a natural language question via the web interface.
*   *Example:* "Where is auth handled?"

### Step 2: LLM Expansion
**Process:** The system sends the user's query to the local LLM (e.g., CodeLlama 7b, DeepSeek Coder) with a prompt designed to generate related technical terms.
**Prompt Strategy:** "You are an expert Java developer. Provide 5-10 technical keywords, class names, or concepts related to the following query for a legacy Java application. Do not explain, just list the terms."
**Output:** The LLM generates synonyms and related terms.
*   *Example:* "Login, Security, LDAP, RBAC, SessionManager, AuthenticationProvider, UserDetails, SecurityContextHolder"

### Step 3: Enhanced Search
**Process:** The system combines the original query with the generated terms to perform a comprehensive search.
*   **Vector Search:** Use the expanded terms to generate a richer embedding vector or multiple vectors.
*   **Lucene/Keyword Search:** Construct a boolean OR query with the new terms to catch exact matches in class names or comments.
*   *Action:* The search engine retrieves the most relevant code chunks based on this expanded context.

### Step 4: Summarization & Response
**Process:** The retrieved code chunks are fed back into the LLM.
**Prompt Strategy:** "Using the following code snippets, answer the user's original question: '[Original Question]'. Cite specific classes or methods."
**Output:** The LLM synthesizes the findings into a coherent answer.
*   *Example:* "Authentication is primarily handled in the `CustomAuthenticationProvider` class, which interfaces with LDAP. Session management is controlled by `SessionManager`..."

# Firestick LLM Goal: Pattern 4 - Map-Reduce Summarization (High-Level Concepts)

This pattern addresses the challenge of analyzing massive codebases (1M+ LOC) and answering high-level architectural questions that cannot be solved by searching for specific keywords or small code snippets.

## Overview

Map-Reduce Summarization is a multi-stage process that builds a hierarchical understanding of the codebase.
1.  **Map (File Level):** Each file is individually summarized by the LLM to extract its core responsibilities, dependencies, and patterns.
2.  **Reduce (Folder/Module Level):** File summaries are grouped by folder and "reduced" into a module-level summary.
3.  **Reduce (System Level):** Module summaries are aggregated to describe the entire system architecture.

## Target Capabilities

This strategy enables Firestick to answer high-level conceptual questions such as:
1.  **Architecture and System Boundaries:** "What are the major architectural layers or modules in this system, and how do they communicate with each other?"
2.  **Data Flow and Transformations:** "How does data move through the system from input to output, and where are the key transformation points?"
3.  **Business Logic Hotspots:** "Where are the core business rules implemented, and which components contain the most complex decision-making logic?"
4.  **Integration Points and Dependencies:** "What external systems, databases, or services does this application integrate with, and where are those integration boundaries?"

# Firestick LLM Goal: Pattern 5 - Method-Level Summarization (Granular Insights)

This pattern addresses the need for granular understanding of large "God Classes" or complex files where a single file-level summary is insufficient.

## Overview

Method-Level Summarization involves generating summaries for individual methods, particularly those that are large or complex.
1.  **Chunking:** The system already breaks files into method-level chunks.
2.  **Summarization:** The LLM generates a summary for each method chunk.
3.  **Indexing:** These summaries are indexed to allow precise search retrieval.

## Benefits

*   **Precision:** Search results can point to specific methods rather than just files.
*   **Context Handling:** Overcomes context window limits by summarizing parts of a file independently.
*   **Better Aggregation:** Folder-level summaries can be built from method summaries for higher accuracy.
5.  **Error Handling and Recovery Patterns:** "How does the system handle failures, what are the common error handling strategies, and where are critical recovery mechanisms?"
6.  **State Management:** "How is application state managed throughout the system, and where are the stateful components versus stateless ones?"
7.  **Security and Authorization Model:** "How does authentication and authorization work, and where are security-critical operations enforced?"
8.  **Performance Bottlenecks and Optimization Areas:** "Which parts of the codebase are performance-sensitive, and where might bottlenecks exist based on complexity or resource usage?"
9.  **Configuration and Customization Points:** "Where can the system's behavior be configured or customized, and what are the extension points for adding new functionality?"
10. **Technical Debt and Code Quality Patterns:** "What are the recurring anti-patterns, deprecated approaches, or areas of high coupling that might need refactoring?"

## Workflow

### Step 1: Map (Enrichment)
**Process:** During indexing, after parsing a file, send its content to the LLM.
**Prompt:** "Analyze this Java file. Provide a 2-3 sentence summary of its purpose, list its key responsibilities, identify any design patterns used, and list external dependencies."
**Storage:** Save this structured summary in the `CodeFile` entity (e.g., `summary` column).

### Step 2: Reduce (Aggregation)
**Process:** Periodically (or on demand), query the database for all file summaries within a specific folder/package.
**Prompt:** "Here are summaries of all files in the `com.codetalker.auth` package. Synthesize them into a description of this module's role in the system."
**Storage:** Save this in a new `FolderSummary` entity.

### Step 3: Concept Search
**Process:** Generate embeddings for these *summaries* (both file and folder level).
**Search:** When a user asks "Where is the security layer?", the vector search matches the *description* of the `auth` package, even if the user didn't use the word "auth".

---

# Goal: Indexing Console — Live, real-time progress and object-level reporting

Add a user-facing, developer-testable goal to make the Indexing Console (Indexing page) report live updates for every progress variable and also show the current object being indexed, with either a duration or a reason why that object wasn't indexed.

Purpose
- Give users an accurate real-time view of ongoing indexing activity so they can monitor progress, debug issues, and verify coverage.

Acceptance criteria
- The Indexing Console UI shows live updates for all progress-related fields during a running job: filesDiscovered, filesParsed, chunksProduced, documentsIndexed, embeddingsGenerated, filesSkipped, filesSummarized, foldersSummarized, methodsSummarized, and the computed percent/progress value.
- The same UI area additionally shows the "current object" being processed (file, folder or method), updated live by SSE or other streaming updates.
- For the current object, the UI displays either:
	- the elapsed duration (ms/seconds) from object-start to object-end, or
	- a short human-readable reason (e.g., "skipped: unsupported file type" or "error: parse failure") if the object was not indexed.
- The server publishes object-level events over the existing SSE endpoint (`/api/indexing/stream`) with event payloads that include: event-type (object-start, object-end, object-skipped), object type (file/folder/method), object name/path, timestamp(s), elapsedMs (if applicable), and reason (optional, for skipped/failed). Aggregated stats are published at sensible intervals (or after each object event) and are merged in the UI so all counters stay current.
- If SSE disconnects or is not available, the UI falls back to a short-polling refresh of the latest job so progress still updates (degraded mode).
- End-to-end tests validate the UI receives a small synthetic job and updates each progress field and current-object info as the server emits object-level events.

Implementation notes (developer facing)
- Backend
	- Ensure `IndexingService` emits object-level events via the existing SSE/ProgressBus (or equivalent) when a per-object lifecycle occurs: on object-start (include timestamp), on object-end (include timestamp + elapsedMs), and on object-skipped (include timestamp + reason).
	- Continue to emit aggregated job-level stats (filesParsed, etc.) at least after each object completion to keep UI counters in sync with DB persisted totals.
	- Make sure the event payload keys match what the UI expects (percent, filesParsed, filesDiscovered, totalFolders, totalMethods, currentFile or objectName, objectType, elapsedMs, reason).
	- Add or update integration tests to assert SSE stream emits object-start / object-end / object-skipped events and aggregated stat updates.

- Frontend
	- Indexing page should open SSE to `/api/indexing/stream?jobId=` and merge incoming messages into `job` state (all counters) and a `currentObject` / `objectEvents` area. The UI should show an explicit connection status (connected/connecting/disconnected).
	- Show the current object's name, type, start time, and either duration or reason (if skipped or failed). If object-end emits elapsedMs, compute a friendly duration string for display.
	- Add unit/integration tests (jest / react-testing-library / e2e) to verify UI updates when it receives simulated SSE messages (object-start/end/skip and aggregated stat updates).

- Backward compatibility
	- Keep backward-compatible payload parsing in the UI so older servers that don't publish per-object SSE still work (UI merges aggregated stats and falls back to polling when necessary).

This goal describes the user-visible feature, the acceptance criteria to validate it, and basic backend/frontend responsibilities so teams can implement it and write tests to prevent regressions.
