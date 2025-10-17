# DEV1 Captain's Log

Template (copy and fill):
- Before coding (after required reading):
  - [YYYY-MM-DD HH:MM] TASK-ID — What: <plan> | Why: <reason>
- After completion (before updating task lists):
  - [YYYY-MM-DD HH:MM] TASK-ID — Work done: <summary> | Result: <outcome>

---

# Entries

[2025-10-15 10:05] DEV1-DAY6-H2 — What: Switch H2 to file-based db, enable console, verify JPA settings | Why: Meet Day 6 persistence requirements and allow inspection via H2 console
[2025-10-15 13:36] DEV1-DAY8-EMB — What: Add EmbeddingService (mock mode) + tests, add config stubs for ONNX | Why: Unblock pipeline w/o large model download; provide deterministic embeddings for tests
[2025-10-15 13:20] DEV1-DAY5-JGRAPH — Work done: Added addVertex/addEdge/findDependencies + tests | Result: All graph tests passing
[2025-10-15 13:32] DEV1-DAY6-H2 — Work done: Switched to file-based H2, enabled console, set ddl-auto=update and show-sql | Result: App boots, H2 console reachable, DB file created
[2025-10-15 13:35] DEV1-DAY7-ENTITIES — Work done: Added CodeFile/CodeChunk/Symbol entities + repositories; verified schema via tests | Result: Tables created, tests passing
[2025-10-15 13:49] DEV1-DAY8-EMB — Work done: Implemented EmbeddingService mock mode with deterministic 384-d vectors + tests; added properties | Result: Tests passing; ONNX loading deferred
[2025-10-15 13:55] DEV1-DAY9-CHROMA — Work done: Implemented ChromaService (create/add/query), parsed JSON, added MockRestServiceServer tests | Result: All Chroma API tests passing
[2025-10-15 14:10] DEV1-DAY10-E2E — What: Begin Day 10 pipeline integration test (parse, chunk, embed, store, query, verify) | Why: Validate full backend pipeline from code to vector search
