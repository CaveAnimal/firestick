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
