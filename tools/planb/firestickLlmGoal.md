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
