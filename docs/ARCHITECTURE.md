# Architecture

This document captures the current high-level system architecture, core components, data flow, and key design decisions.

## Technology Stack

- Backend: Spring Boot 3.5.x (Java 21), H2, Lucene, JavaParser, JGraphT
- Embeddings/ML: ONNX runtime-based embeddings stored in Chroma
- UI: Vite + React 18 + TypeScript, Cytoscape.js, Recharts, Monaco
- Build/Test: Maven + JUnit 5, JaCoCo; UI uses Vite/Vitest/RTL

## System Overview

```mermaid
graph TB
	UI[React UI] --> API[REST API]
	API --> Search[Search Service]
	API --> Analysis[Analysis Service]
	API --> Index[Indexing Service]

	Search --> Lucene[Lucene Index]
	Search --> Chroma[Chroma Vector DB]
	Search --> Parser[JavaParser]

	Analysis --> Parser
	Analysis --> Graph[JGraphT]

	Index --> Parser
	Index --> H2[H2 Database]
	Index --> Embeddings[ONNX Embeddings]

	Embeddings --> Chroma
```

## Data Flow

1) Discover → 2) Parse → 3) Chunk → 4) Index → 5) Embed → 6) Search/Query → 7) Visualize (graph, dashboard)

```mermaid
sequenceDiagram
	participant Disc as Discovery
	participant Parser as Parser (JavaParser)
	participant Chunk as Chunker
	participant Index as Index (Lucene/H2)
	participant Embed as Embeddings (ONNX)
	participant Vec as Chroma
	participant API as REST API
	participant UI as UI

	Disc->>Parser: enumerate source files
	Parser->>Chunk: AST + symbols
	Chunk->>Index: code chunks + metadata
	Chunk->>Embed: text chunks
	Embed->>Vec: vector upsert
	UI->>API: search(query)
	API->>Index: keyword + metadata
	API->>Vec: vector similarity
	API->>UI: merged results
```

## Components and Relationships

- UI
	- Search page, Dashboard, Graph visualization, Code viewer
	- GraphView uses Cytoscape with type styling, layout presets, and keyboard help
- API
	- SearchController, Indexing endpoints, Analysis endpoints
- Services
	- SearchService (Lucene + Chroma), CodeParserService (JavaParser), GraphService (JGraphT)

```mermaid
graph LR
	subgraph UI
		A[Search] --> B[GraphView]
		A --> C[Dashboard]
		C --> B
		A --> D[Code Viewer]
	end
	subgraph Backend
		E[SearchController]
		F[SearchService]
		G[CodeParserService]
		H[GraphService]
	end
	A -. REST .-> E
	E --> F
	F --> G
	F --> H
	B -. enrich .-> E
```

## Design Decisions

- Keep search hybrid: keyword (Lucene) + semantic (Chroma)
- Prefer simple, explainable chunking and deterministic graph derivation (JGraphT)
- Store embeddings outside the relational DB for scalability (Chroma)
- Optimize UI bundle with vendor chunk splits for faster loads
- Persist user graph preferences locally to reduce friction

## Notes

- For API shapes and examples, see `docs/API.md` and `docs/api/mocks/`
- For test coverage goals, see `docs/coverage-goals.md`
