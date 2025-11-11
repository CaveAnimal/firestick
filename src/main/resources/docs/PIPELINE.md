# End-to-End Pipeline (Code → Embedding → Vector DB)

This document explains the backend pipeline that takes source code, parses it into chunks, generates embeddings, stores them in Chroma, and serves similarity queries.

## Overview

1) File discovery: scan a root folder for code files to index.
2) Parse + chunk: use JavaParser to extract classes/methods and produce chunk texts.
3) Embeddings: generate a fixed-size vector per chunk via EmbeddingService (MOCK by default).
4) Index + store: index chunk text in Lucene and persist chunks in H2; push embeddings into Chroma.
5) Query: embed the query text and retrieve nearest documents from Chroma.

```text
Java files → JavaParser → CodeChunking → EmbeddingService → Lucene + Chroma → Search results
```

## Key Components

- FileDiscoveryService: scans directories, supports excludes/globs.
- CodeParserService: wraps JavaParser; parses file to CompilationUnit.
- CodeChunkingService: converts parsed data into chunk texts with metadata.
- EmbeddingService: returns float[] vectors. Modes:
  - MOCK (default): deterministic, dependency-free.
  - ONNX: uses onnxruntime with a local sentence-transformers model (e.g., all-MiniLM-L6-v2) and a minimal WordPiece tokenizer.
- ChromaService: HTTP client (RestTemplate) for Chroma v2 API.
- IndexingService: orchestrates discovery → parse/chunk → index/store → embed/push.

## Minimal Example

```java
// Parse & chunk a file
CodeFile cf = codeParserService.parseFile("src/test/resources/test-data/sample-code/HelloWorld.java");
List<CodeChunk> chunks = cf.getChunks();

// Generate embedding
float[] emb = embeddingService.getEmbedding(chunks.get(0).getContent());

// Store + query via Chroma
String collection = "my-collection";
chromaService.createCollection(collection);
chromaService.addEmbeddings(collection, List.of(emb), List.of(chunks.get(0).getContent()));
List<String> results = chromaService.query(collection, emb, 3);
```

## Configuration

application.properties (defaults/keys):

- embedding.mode=mock (set to onnx to enable ONNX mode)
- embedding.dimension=384 (expected hidden size)
- embedding.max-length=128 (max tokens per sequence)
- embedding.model-path=models/model_onnx/onnx/model.onnx
- embedding.tokenizer-path=models/model_onnx (directory containing vocab.txt)
- chroma.base-url=http://localhost:8000 (Chroma v2 server)

## Testing Strategy

- Unit tests mock Chroma HTTP with MockRestServiceServer.
- E2EPipelineTest covers parse → embed → store → query without real Chroma.
- IndexingServiceTest runs the indexing orchestrator against sample files and validates persistence.

## Troubleshooting

- Chroma v2: endpoints are under /api/v2. v1 is deprecated.
- If ONNX mode is selected but files are missing, EmbeddingService will throw a clear IllegalStateException (model-path/tokenizer-path required).
- ONNX input names can vary; the embedder supports common names (input_ids, attention_mask, optional token_type_ids).

## ONNX Smoke Test

- A guarded smoke test `EmbeddingServiceOnnxSmokeTest` runs if the model and vocab exist at:
  - models/model_onnx/onnx/model.onnx
  - models/model_onnx/vocab.txt
- It is skipped automatically on environments without these files.
- To run only the smoke test:

  mvn -q -Dtest=EmbeddingServiceOnnxSmokeTest test
- For brittle assertions that depend on dataset size, prefer per-file expectations (see IndexingServiceTest change).

## Future Enhancements

- Implement ONNX runtime path and tokenizer loading in EmbeddingService (Mode.ONNX).
- Expose pipeline endpoints (run indexing job, query) via REST controllers.
- Add health checks for external services (Chroma).
