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
