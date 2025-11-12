# Firestick — Code Chunking Tasks (Based on Open-Source Strategy)

Date: 2025-10-26
Owner: DEV1 (Backend)
Scope: Implement multi-level code chunking for Java (method/class/file/module), enrich with metadata, and prepare for embeddings + vector DB.

Status Symbols:
- [ ] Not Started  •  [-] In Progress  •  [X] Completed  •  [V] Tested & Verified  •  [!] Blocked  •  [>] Deferred

---

## 0) Prerequisites & Foundations
[V] Java AST parsing via JavaParser available (CodeParserService)
[V] DTOs available for parsed code (MethodInfo, ClassInfo, FileInfo)
[V] Dependency graph basics ready (helps provide relationships if needed)
[>] Python parsing (out of scope for this milestone)

Notes:
- Use hierarchical chunking: method (primary), class (secondary), file (tertiary), module (context). Target 200–1000 tokens per chunk with 10–20% overlap where applicable.

---

## 1) Design & Schema
[X] Define chunk types, sizes, and overlap rules in a design note
  [X] Document target token range per type (method/class/file/module)
  [X] Define 10–20% overlap for adjacent chunks where applicable
  [X] Decide complexity threshold for promoting private methods to chunks (e.g., >5)
[X] Adopt open-source strategy reference: `tools/plans/firestickOPEN_SOURCE_CHUNKING_STRATEGY.md`
[X] Create developer-facing summary doc `docs/CHUNKING_STRATEGY.md` (concise, actionable)

---

## 2) CodeChunkingService (Java)
[V] Create `CodeChunkingService.java` in `com.codetalker.firestick.service`
[V] API: `List<CodeChunk> chunkFile(FileInfo fileInfo)`
[V] API: `List<CodeChunk> chunkClass(FileInfo fileInfo, ClassInfo classInfo)`
[V] API: `List<CodeChunk> chunkMethod(FileInfo fileInfo, ClassInfo classInfo, MethodInfo methodInfo)`
[V] Implement method-level chunks (primary)
  [V] Include: file path, line range, class decl header, method signature, body, Javadoc, preceding comments
  [V] Promote complex private methods (cyclomatic > threshold)
[V] Implement class-level chunks (secondary)
  [V] Include: package, top N imports, class decl, fields, class docs, method signatures (no bodies), inner classes
  [V] Apply only when class < ~500 LOC (configurable)
[V] Implement file-level chunks with summary (tertiary)
  [V] Include: AI summary field (placeholder for now), package/module, imports, class/function signatures, file comments
[V] Implement module/package-level context chunks (context)
  [V] Include: module path, purpose/summary, key classes and roles, entry points, dependencies

---

## 3) DTOs & Metadata
[V] Create lightweight DTO `CodeChunk` (entity or in-memory DTO) with fields:
  [V] id, type(method|class|file|module), language, content, contentSummary, filePath, lineStart/End
  [V] hierarchy (package, class, method, parentChunkId)
  [V] code attributes (visibility, static/abstract/deprecated, complexity, loc)
  [V] relationships (calls, calledBy, imports, implements, extends)
  [V] documentation (hasDoc, summary)
  [V] quality metrics (hasTests, coverage?, author?, lastModified?)
[V] Add builder + toString for debugging
[V] Ensure consistency with strategy JSON schema in the reference doc

---

## 4) Utilities & Context Enrichment
[V] Token counting utility (approximate tokenizer for 200–1000 token targets)
[V] Split very large methods/classes into windows with overlap (10–20%)
[V] Prepend contextual header to method chunks:
  [V] `// File: <path>\n// Package: <pkg>\n// Class: <class>\n// Signature: <sig>`
[V] Extract preceding comment blocks and JavaDoc inclusion rules
[V] Compute cyclomatic complexity (or simple heuristic) for method promotion

---

## 5) Configuration
[V] Externalize settings in `application.properties` or a config class:
  [V] target token range, overlap %, class LOC threshold, complexity threshold
  [V] top N imports to include for class-level chunks

---

## 6) Integration with Embedding & Vector DB
[V] Prepare chunk text for embedding (header + code) per strategy
[V] Batch embedding hook (reuse `EmbeddingService` mock for now)
[V] Add method to push chunks + metadata to Chroma collection
[V] Minimal repository or service to persist chunk metadata (optional)

---

## 7) Testing
[V] Unit tests for method-level chunking (happy path + edge: no Javadoc, short method)
[V] Unit tests for class-level chunking (small class, inner classes)
[V] Unit tests for file-level summary chunk (placeholder summary)
[V] Unit tests for module-level context chunk
[V] Utility tests: token counting, overlap windows, complexity heuristic
[V] Integration test: parse → chunk → embed (mock) → push to Chroma (mock server)

---

## 8) Performance & Quality
[V] Micro-benchmarks for chunking throughput on medium project
[V] Log metrics: chunks/sec, avg tokens per chunk, distribution by type
[V] Memory profile on large files

---

## 9) Documentation
[V] `docs/CHUNKING_STRATEGY.md` with examples (method/class/file/module)
[V] `docs/CHUNKING_API.md` for `CodeChunkingService` usage
[V] Troubleshooting notes (oversized chunks, missing context, etc.)

---

## 10) Follow-ups / Deferred
[>] Python chunking parity (libcst/ast), out of scope this milestone
[>] AI-generated summaries for file/module chunks (needs local LLM)
[V] Optional: store chunk → graph node mappings using DependencyGraphService

---

## Acceptance Criteria
[V] Method-level chunking implemented and unit-tested
[V] Class-level and file-level chunking implemented (basic), with tests
[V] Metadata schema captured and validated in DTOs
[V] Configurable thresholds and overlap parameters available
[V] Integration test covers end-to-end flow (parse → chunk → embed mock → push mock)
