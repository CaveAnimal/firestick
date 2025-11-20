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
- `[ ]` **Task 1.2:** Update `llm-service` configuration to allow easy switching between models via `.env` or config file
- `[ ]` **Task 1.3:** Benchmark models against a set of 10 representative legacy code queries
    - Evaluate for Speed (Tokens/sec)
    - Evaluate for Instruction following
    - Evaluate for Relevance of keywords

---

## Phase 2: Query Expansion Endpoint (Week 2)

**Status:** 0% Complete
**Goal:** Implement the pre-processing step in the Python `llm-service`.

- `[ ]` **Task 2.1:** Create new API endpoint `POST /api/llm/expand-query`
    - Input: `{"query": "user string"}`
    - Output: `{"expanded_terms": ["term1", "term2", ...]}`
- `[ ]` **Task 2.2:** Develop Prompt Engineering for expansion
    - Draft Prompt: "You are an expert Java developer. Provide 5-10 technical keywords..."
- `[ ]` **Task 2.3:** Implement parsing logic to extract clean list of terms from LLM response

---

## Phase 3: Search Integration (Week 2-3)

**Status:** 0% Complete
**Goal:** Connect the Java backend to the new expansion endpoint and utilize the results.

- `[ ]` **Task 3.1:** Update Java `LlmClient` to call `/api/llm/expand-query`
- `[ ]` **Task 3.2:** Modify the Search Service workflow
    - `[ ]` Receive User Query
    - `[ ]` Call Expansion Endpoint -> Get Terms
    - `[ ]` **Lucene Search:** Construct Boolean Query: `(original_query) OR (term1 OR term2 ...)`
    - `[ ]` **Vector Search:** (Optional) Generate embedding for expanded string or average of terms
- `[ ]` **Task 3.3:** Aggregation logic to combine results from original query and expanded terms

---

## Phase 4: Summarization (RAG) Endpoint (Week 3)

**Status:** 0% Complete
**Goal:** Synthesize search results into a final answer.

- `[ ]` **Task 4.1:** Create new API endpoint `POST /api/llm/answer-question`
    - Input: `{"query": "...", "context_chunks": ["code snippet 1", "code snippet 2"]}`
    - Output: `{"answer": "...", "citations": [...]}`
- `[ ]` **Task 4.2:** Develop RAG Prompt
    - Draft Prompt: "Using the following code snippets, answer the user's original question..."
- `[ ]` **Task 4.3:** Handle context window limits (truncate chunks if necessary)

---

## Phase 5: High-Level Concept Indexing (Map-Reduce) (Week 4-5)

**Status:** 0% Complete
**Goal:** Implement Map-Reduce strategy to scale to 1M LOC and answer architectural questions.

- `[ ]` **Task 5.1:** Database Schema Updates
    - `[ ]` Add `summary` column to `CodeFile` table
    - `[ ]` Create `FolderSummary` entity/table
- `[ ]` **Task 5.2:** Implement "Map" Phase in Indexing Service
    - `[ ]` Update `IndexingService` to call LLM `summarize` for each file
    - `[ ]` Store summary in `CodeFile`
- `[ ]` **Task 5.3:** Implement "Reduce" Service
    - `[ ]` Create `SummaryAggregationService`
    - `[ ]` Logic to group file summaries by folder
    - `[ ]` Call LLM to generate folder summary
- `[ ]` **Task 5.4:** Vectorize Summaries
    - `[ ]` Generate embeddings for file and folder summaries
    - `[ ]` Add to Vector Store (Chroma/Lucene)
- `[ ]` **Task 5.5:** Update Search to include Summaries
    - `[ ]` Include summary embeddings in search scope

## Phase 5b: Method-Level Summarization (Granular Insights) (Week 5)

**Status:** 0% Complete
**Goal:** Enhance precision by summarizing individual methods.

- `[ ]` **Task 5b.1:** Database Schema Updates
    - `[ ]` Add `summary` column to `CodeChunk` table
- `[ ]` **Task 5b.2:** Update Indexing Service to summarize method chunks
    - `[ ]` Iterate over chunks in `IndexingService`
    - `[ ]` Call LLM `summarize` for method chunks (optionally filter by size)
    - `[ ]` Store summary in `CodeChunk`
- `[ ]` **Task 5b.3:** Update Search Service to index method summaries
    - `[ ]` Index chunk summaries in Lucene

---

## Phase 6: UI Integration (Week 6)

**Status:** 0% Complete
**Goal:** Expose the new functionality to the user.

- `[ ]` **Task 5.1:** Update Frontend Search Component
    - `[ ]` Add "Thinking..." state while LLM is processing
    - `[ ]` Display "Searching for related terms: [List]" to give user feedback
- `[ ]` **Task 5.2:** Render the LLM-generated answer at the top of the search results
- `[ ]` **Task 5.3:** Add "Regenerate" button to try with a different model or prompt (advanced)
