# CodeTalker – Architecture Overview

Date: 2025-11-05
Owner: CodeTalker Team

Scope
- High-level overview of how CodeTalker works end-to-end: components, data flows, and where the Python toolchain fits.
- Based on the project’s PRD, Planning, Tasks, and Chunking Strategy documents under `tools/plans/`.

## System goals (recap)
- Explore large legacy Java codebases locally and offline.
- Provide hybrid search (semantic + keyword) with dependency/graph insights.
- Keep source and derived data on the user’s machine (no data egress).

## Logical components
- UI (Browser): Search, results, code viewer (Monaco), dependency graphs (D3/Cytoscape).
- Spring Boot Backend (Java):
  - Controllers: Search, Analysis, Indexing, Graph.
  - Services:
    - FileDiscoveryService – directory scanning with exclusions.
    - CodeParserService – AST parsing via JavaParser.
    - CodeChunkingService – method/class/file/module chunking with metadata.
    - EmbeddingService – ONNX-based embedding generation (local model).
    - CodeSearchService – Lucene indexing and BM25 queries.
    - DependencyGraphService – JGraphT graph construction and queries.
    - IndexingOrchestrationService – pipeline coordinator, progress reporting.
  - Persistence:
    - H2 (metadata, symbols, graph persistence),
    - Lucene (keyword index),
    - Chroma (vector DB for embeddings; accessed via REST when used).
- Local Models and Data:
  - ONNX model files and tokenizer artifacts (bundled under `models/`),
  - Vector stores on disk (Chroma persistence),
  - Lucene indexes, logs, and app configuration.
- Optional Python Tooling (local, for developers/packaging/testing):
  - Managing tokenizers and model assets (transformers),
  - Converting/validating ONNX models (onnx + optimum),
  - Running local Chroma server and one-off embedding checks,
  - Utility scripts for caching, parallelization, and CI smoke checks.

## High-level data flow

Indexing pipeline
1) Discover files → filter/exclude directories.
2) Parse Java files → extract classes/methods/imports, compute metrics.
3) Chunk into method/class/file/module units → enrich with metadata.
4) Generate embeddings (locally) for chunks using ONNX model.
5) Store:
   - Embeddings in Chroma (vector DB),
   - Text and fields into Lucene (keyword index),
   - Metadata/graph into H2.

Query pipeline
1) User query → classify (exact/semantic/graph).
2) Run in parallel:
   - Semantic search on Chroma (vector similarity),
   - Keyword search on Lucene (BM25).
3) Merge and re-rank results.
4) Assemble context (source lines, callers/callees from graph).
5) Return results to UI for navigation.

## Operational characteristics
- Local-only runtime by default (Chroma server, H2, Lucene, ONNX model file).
- No external AI services required; all inference and storage are local.
- Version pinning for both Java and Python components to ensure reproducibility.

## Where the requested Python packages fit
- transformers (tokenizer/config utilities; optional export to ONNX with Optimum).
- torch (backend required by transformers for certain conversion/validation flows).
- joblib (caching/parallelization for preprocessing and developer scripts).
- onnx (model graph tooling; used alongside onnxruntime for actual inference tests).

These packages do not introduce network calls in CodeTalker’s default operation; they support local developer tooling, validation, and packaging steps that help keep the app fully offline.
