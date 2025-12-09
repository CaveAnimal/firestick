# DEV1 Captain's Log

Template (copy and fill):
- Before coding (after required reading):
  - [YYYY-MM-DD HH:MM] TASK-ID — What: <plan> | Why: <reason>
- After completion (before updating task lists):
  - [YYYY-MM-DD HH:MM] TASK-ID — Work done: <summary> | Result: <outcome>

---

# Entries

[2025-11-03 15:20] DEV1-DAY10-E2E — What: Finalize end-to-end pipeline test using MockRestServiceServer; add HelloWorld sample and verify parse→embed→store→query | Why: Validate full backend pipeline without external dependencies

[2025-11-03 16:05] DEV1-DAY10-E2E — Work done: Added `HelloWorld.java` sample; implemented `E2EPipelineTest` with MockRestServiceServer (v2 endpoints) to create collection, add embeddings, and query; fixed brittle assertion in `IndexingServiceTest` to account for multiple files; ran unit tests | Result: Test suite passing locally (no failures), BUILD SUCCESS

[2025-11-03 16:20] DEV1-DAY10-DOCS — Work done: Created `src/main/resources/docs/PIPELINE.md` documenting parse→chunk→embed→store→query flow, configuration, testing strategy, and troubleshooting; updated Day 10 tasks to [V]; refreshed Chroma endpoint docs to v2 | Result: Documentation added; task list updated

[2025-11-03 15:00] DEV1-DAY9-CHROMA — What: Install Chroma locally and verify it runs on port 8000; test heartbeat endpoint | Why: Day 9-10 task to set up vector database infrastructure for embedding storage/search
[2025-11-03 15:03] DEV1-DAY9-CHROMA — Work done: Verified chromadb 1.2.1 installed; started Chroma server on localhost:8000; tested heartbeat endpoint at /api/v2/heartbeat (v1 deprecated) | Result: Chroma running successfully, responding with heartbeat JSON

[2025-11-03 15:05] DEV1-DAY9-CHROMA-FIX — What: Update ChromaService.java to use v2 API endpoints instead of deprecated v1 | Why: ChromaDB 1.2.1 deprecated v1 API; must use /api/v2/* endpoints for all operations
[2025-11-03 15:08] DEV1-DAY9-CHROMA-FIX — Work done: Updated ChromaService to use /api/v2/ endpoints (collections, add, query); updated ChromaServiceTest expectations; ran mvn test -Dtest=ChromaServiceTest | Result: All tests passing (2/2), BUILD SUCCESS
[2025-11-03 15:10] DEV1-DAY9-CHROMA-FINAL — Work done: Ran full test suite (mvn test); added Chroma v2 API migration to LESSONS_LEARNED.md | Result: All 25 tests passing, BUILD SUCCESS; Day 9-10 Chroma installation task complete

[2025-11-03 14:28] DEV1-DAY16-HEALTH — What: Add a minimal HealthController exposing GET /api/health returning JSON {status:"OK"} and a MockMvc test | Why: Provide a simple uptime check endpoint and smoke test for web layer
[2025-11-03 14:31] DEV1-DAY16-HEALTH — Work done: Implemented HealthController and HealthControllerTest; ran tests and verified PASS via Surefire report (0 failures) | Result: /api/health returns HTTP 200 with {"status":"OK"}

[2025-10-26 00:15] DEV1-DAY15-GRAPH — What: Implement MethodCallVisitor, extend MethodInfo, and integrate method call analysis for CALLS edges in dependency graph | Why: Enable richer code relationship mapping for search/analysis
[2025-10-26 00:17] DEV1-DAY15-GRAPH — Work done: Completed method call analysis and CALLS edge implementation in DependencyGraphService; all related unit tests passing | Result: Backend graph enrichment phase validated

[2025-10-26 00:10] DEV1-DAY15-GRAPH — What: Begin method call analysis for dependency graph (Phase 2); implement JavaParser visitor to detect CALLS edges | Why: Enable richer code relationship mapping for search/analysis

[2025-10-26 00:05] DEV1-DAY14-GRAPH — What: Extend dependency graph builder to add EXTENDS/IMPLEMENTS edges for inheritance/interface relationships | Why: Complete Phase 1 schema and enable richer code analysis

[2025-10-15 10:05] DEV1-DAY6-H2 — What: Switch H2 to file-based db, enable console, verify JPA settings | Why: Meet Day 6 persistence requirements and allow inspection via H2 console
[2025-10-15 13:36] DEV1-DAY8-EMB — What: Add EmbeddingService (mock mode) + tests, add config stubs for ONNX | Why: Unblock pipeline w/o large model download; provide deterministic embeddings for tests
[2025-10-15 13:20] DEV1-DAY5-JGRAPH — Work done: Added addVertex/addEdge/findDependencies + tests | Result: All graph tests passing
[2025-10-15 13:32] DEV1-DAY6-H2 — Work done: Switched to file-based H2, enabled console, set ddl-auto=update and show-sql | Result: App boots, H2 console reachable, DB file created
[2025-10-15 13:35] DEV1-DAY7-ENTITIES — Work done: Added CodeFile/CodeChunk/Symbol entities + repositories; verified schema via tests | Result: Tables created, tests passing
[2025-10-15 13:49] DEV1-DAY8-EMB — Work done: Implemented EmbeddingService mock mode with deterministic 384-d vectors + tests; added properties | Result: Tests passing; ONNX loading deferred
[2025-10-15 13:55] DEV1-DAY9-CHROMA — Work done: Implemented ChromaService (create/add/query), parsed JSON, added MockRestServiceServer tests | Result: All Chroma API tests passing
[2025-10-15 14:10] DEV1-DAY10-E2E — What: Begin Day 10 pipeline integration test (parse, chunk, embed, store, query, verify) | Why: Validate full backend pipeline from code to vector search
[2025-10-25 09:00] DEV1-DAY11-FILEDISCOVERY — What: Implement and verify FileDiscoveryService tests (unit/integration, exclusion, error handling) | Why: Complete Day 11 backend requirements and ensure robust file scanning
[2025-10-25 23:55] DEV1-DAY14-GRAPH — What: Implement basic dependency graph buildFromParsedFiles (files/classes/methods/imports) + tests | Why: Start Phase 1 graph foundation per plan, unblock later edges
[2025-10-26 00:02] DEV1-DAY14-GRAPH — Work done: Added buildFromParsedFiles (file/class/method/import nodes + edges), unit test testBuildFromParsedFiles_basic passing | Result: Graph basics validated; extends/implements edges next

[2025-11-03 16:50] DEV1-OPT-ONNX — What: Implement ONNX embedding wiring and smoke test | Why: Optional task to enable real embeddings with local model
[2025-11-03 16:55] DEV1-OPT-ONNX — Work done: Added WordPieceTokenizer + OnnxEmbedder, wired EmbeddingService ONNX mode (lazy init), added guarded smoke test `EmbeddingServiceOnnxSmokeTest`, updated PIPELINE.md with ONNX setup and test instructions | Result: Default tests remain green (MOCK mode); ONNX test auto-skips without model files
