# Firestick - Product Requirements Document (PRD)

**Version:** 1.0  
**Date:** October 13, 2025  
**Product:** Firestick - Legacy Code Analysis and Search Tool  
**Repository:** firestick (CaveAnimal/firestick)

---

## 1. Executive Summary

### 1.1 Product Vision
Firestick is a standalone desktop Java web application designed to help developers understand, navigate, and analyze large legacy codebases (up to 1M+ lines of code) without requiring external AI services or paid subscriptions. It provides intelligent code search, dependency analysis, and structural insights through a combination of semantic search, graph analysis, and static code analysis.

### 1.2 Problem Statement
Developers working with large legacy applications face significant challenges:
- Difficult to understand code structure and dependencies
- Time-consuming to locate specific functionality across millions of lines
- Lack of documentation for legacy code
- Complex dependency chains are hard to visualize
- Need for code understanding tools that work offline without paid AI services

### 1.3 Product Positioning
Firestick is a **powerful code exploration and analysis tool** that combines:
- **Offline semantic search** using local embeddings
- **Full-text search** with Apache Lucene
- **Dependency graph analysis** for understanding code relationships
- **Static code analysis** for quality metrics
- **No external dependencies** on paid AI services

---

## 2. Product Goals & Success Metrics

### 2.1 Primary Goals
1. Enable developers to quickly find and understand code in large legacy codebases
2. Provide dependency visualization and impact analysis
3. Offer offline, standalone operation with no external API dependencies
4. Deliver fast search results (<2 seconds for most queries)
5. Support incremental indexing for efficient updates

### 2.2 Success Metrics
- **Performance:** Query response time < 2 seconds for 1M LOC
- **Accuracy:** Semantic search returns relevant results in top 10 results (>80% precision)
- **Coverage:** Successfully index and analyze 100% of Java source files
- **Usability:** Developers can find specific code within 3 queries or less
- **Adoption:** Reduce time spent searching for code by 70%

---

## 3. Target Users & Use Cases

### 3.1 Primary Users
- **Backend Developers** working on legacy Java applications
- **Technical Leads** conducting code reviews and architecture analysis
- **DevOps Engineers** analyzing dependencies and change impact
- **New Team Members** onboarding to large codebases

### 3.2 Key Use Cases

#### UC-1: Find Specific Functionality
**Actor:** Developer  
**Goal:** Locate where a specific business logic is implemented  
**Flow:**
1. Developer enters natural language query (e.g., "payment processing logic")
2. System performs hybrid search (semantic + keyword)
3. System returns ranked list of relevant code snippets with context
4. Developer navigates to source file to review implementation

#### UC-2: Analyze Code Dependencies
**Actor:** Technical Lead  
**Goal:** Understand dependencies for a specific class or module  
**Flow:**
1. User selects a class or method
2. System displays dependency graph showing callers and callees
3. User explores graph interactively to understand impact
4. System highlights circular dependencies or potential issues

#### UC-3: Identify Similar Code Patterns
**Actor:** Developer  
**Goal:** Find similar implementations across the codebase  
**Flow:**
1. Developer selects a code snippet
2. System uses vector similarity to find similar code
3. System returns ranked list of similar methods/classes
4. Developer reviews for refactoring opportunities or pattern understanding

#### UC-4: Index New or Updated Code
**Actor:** Developer  
**Goal:** Keep the search index up-to-date with latest code changes  
**Flow:**
1. Developer triggers incremental indexing
2. System detects changed files since last index
3. System re-parses, re-embeds, and updates indices
4. System provides indexing progress and completion status

#### UC-5: Dead Code Detection
**Actor:** Technical Lead  
**Goal:** Identify unused methods and classes for cleanup  
**Flow:**
1. User requests dead code analysis
2. System analyzes call graph to identify unreferenced code
3. System generates report of potentially unused code
4. User reviews and marks code for removal or preservation

---

## 4. Technical Architecture

### 4.1 Technology Stack

#### Backend Framework
- **Java 21** - Latest LTS version
- **Spring Boot 3.5.6** - Web framework with embedded Tomcat
- **Maven** - Build and dependency management

#### Code Analysis
- **JavaParser 3.26.3** - Java source parsing and AST analysis
- **JGraphT 1.5.2** - Graph library for dependency analysis
- **Apache Lucene 9.12.0** - Full-text search and BM25 ranking

#### Data & ML
- **ONNX Runtime 1.20.0** - Local ML inference for embeddings
- **DJL 0.31.1** - Deep Java Library for ML integration
- **Sentence-Transformers** - all-MiniLM-L6-v2 (ONNX format, offline)
- **H2 Database** - Embedded SQL for metadata storage
- **Chroma** - Vector database for semantic search (local instance)

#### Frontend
- **React** or **HTML/JS/CSS** - Web UI
- **Monaco Editor** - Code viewer with syntax highlighting
- **D3.js or Cytoscape.js** - Graph visualization
- **Bootstrap** - UI framework

#### Packaging
- **jpackage** or **Launch4j** - Native desktop application packaging
- **Embedded JRE** - Self-contained distribution

### 4.2 System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Desktop Application                   │
│  ┌───────────────────────────────────────────────────┐  │
│  │              Web Browser (UI Layer)                │  │
│  │  - Search Interface                                │  │
│  │  - Code Viewer (Monaco)                            │  │
│  │  - Graph Visualization (D3.js)                     │  │
│  └────────────────────┬──────────────────────────────┘  │
│                       │ REST API                         │
│  ┌────────────────────▼──────────────────────────────┐  │
│  │        Spring Boot Application (Backend)           │  │
│  │                                                     │  │
│  │  Controllers:                                       │  │
│  │  - SearchController                                 │  │
│  │  - AnalysisController                               │  │
│  │  - IndexController                                  │  │
│  │  - GraphController                                  │  │
│  │                                                     │  │
│  │  Services:                                          │  │
│  │  - CodeParserService (JavaParser)                   │  │
│  │  - CodeSearchService (Lucene)                       │  │
│  │  - EmbeddingService (ONNX Runtime)                  │  │
│  │  - DependencyGraphService (JGraphT)                 │  │
│  │  - IndexingService                                  │  │
│  │  - AnalysisService                                  │  │
│  └─────────────────────┬───────────────────────────────┘ │
│                        │                                  │
│  ┌─────────────────────▼───────────────────────────────┐ │
│  │              Data Storage Layer                      │ │
│  │                                                       │ │
│  │  - Chroma (Vector DB for embeddings)                 │ │
│  │  - Lucene Index (Keyword search)                     │ │
│  │  - H2 Database (Metadata, graphs)                    │ │
│  │  - File System (Source code, ONNX models)            │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 4.3 Data Flow

#### Indexing Pipeline
```
Source Code → JavaParser → AST Extraction → Chunking Strategy
                                                  ↓
                                    ┌─────────────┴───────────────┐
                                    ↓                             ↓
                            Embedding Generation            Lucene Indexing
                            (ONNX Runtime)                  (Full-text)
                                    ↓                             ↓
                            Store in Chroma              Store in Lucene Index
                                    ↓                             ↓
                            Metadata to H2 ← ─ ─ ─ ─ ─ ─ ┘
                            (paths, signatures, metrics)
```

#### Query Pipeline
```
User Query → Query Analysis → Hybrid Search
                                   ↓
                    ┌──────────────┴────────────────┐
                    ↓                               ↓
            Semantic Search                  Keyword Search
            (Chroma Vector DB)               (Lucene BM25)
                    ↓                               ↓
                    └──────────────┬────────────────┘
                                   ↓
                            Result Merging & Ranking
                                   ↓
                            Context Assembly
                            (Add surrounding code)
                                   ↓
                            Return to User
```

---

## 5. Core Features & Requirements

### 5.1 Code Indexing Engine

#### FR-1.1: Directory Scanning
- **Description:** Recursively scan project directory to discover Java files
- **Acceptance Criteria:**
  - Support configurable root directory
  - Filter files by extension (.java)
  - Handle symbolic links appropriately
  - Report progress during scanning
  - Skip configured exclusion patterns (e.g., .git, target, build)

#### FR-1.2: Code Parsing & AST Extraction
- **Description:** Parse Java files and extract Abstract Syntax Tree
- **Acceptance Criteria:**
  - Parse valid Java 8+ syntax
  - Extract classes, methods, interfaces, enums
  - Capture method signatures, parameters, return types
  - Extract JavaDoc and inline comments
  - Identify import statements and package structure
  - Handle parsing errors gracefully

#### FR-1.3: Intelligent Chunking
- **Description:** Split code into semantically meaningful chunks for indexing
- **Acceptance Criteria:**
  - Create method-level chunks with class context
  - Create class-level summaries
  - Create package-level overviews
  - Maintain source location metadata (file, line numbers)
  - Preserve method caller/callee context

#### FR-1.4: Embedding Generation
- **Description:** Generate vector embeddings for code chunks using local model
- **Acceptance Criteria:**
  - Use ONNX Runtime with all-MiniLM-L6-v2 model
  - Generate embeddings offline (no API calls)
  - Batch process embeddings for efficiency
  - Cache embeddings to avoid recomputation
  - Store embeddings in Chroma vector database

#### FR-1.5: Full-Text Indexing
- **Description:** Index code using Apache Lucene for keyword search
- **Acceptance Criteria:**
  - Index method names, class names, variable names
  - Index code content for full-text search
  - Index comments and documentation
  - Support BM25 ranking algorithm
  - Maintain separate index for fast keyword lookup

#### FR-1.6: Dependency Graph Construction
- **Description:** Build graph of code dependencies
- **Acceptance Criteria:**
  - Parse import statements
  - Identify method call relationships
  - Detect class inheritance and interface implementation
  - Store graph in JGraphT structure
  - Persist graph data to H2 database
  - Support graph queries (callers, callees, paths)

#### FR-1.7: Incremental Indexing
- **Description:** Update index for changed files only
- **Acceptance Criteria:**
  - Detect files modified since last index (timestamp-based)
  - Re-process only changed files
  - Update vector database, Lucene index, and graph
  - Maintain index consistency
  - Report indexing progress and statistics

### 5.2 Search & Query Engine

#### FR-2.1: Hybrid Search
- **Description:** Combine semantic and keyword search for best results
- **Acceptance Criteria:**
  - Execute semantic search via Chroma (vector similarity)
  - Execute keyword search via Lucene (BM25)
  - Merge results using configurable ranking algorithm
  - Return top-k results (configurable, default 20)
  - Include relevance scores

#### FR-2.2: Query Classification
- **Description:** Analyze query intent to optimize search strategy
- **Acceptance Criteria:**
  - Detect exact match queries (class/method names)
  - Identify semantic queries (natural language)
  - Recognize graph queries (dependency-related)
  - Route query to appropriate search strategy
  - Support query syntax for advanced searches

#### FR-2.3: Context Assembly
- **Description:** Provide surrounding context for search results
- **Acceptance Criteria:**
  - Include complete method for method-level matches
  - Show class structure for class-level matches
  - Display caller/callee information from graph
  - Include file path and line numbers
  - Provide "jump to definition" links

#### FR-2.4: Symbol Table Lookup
- **Description:** Fast exact lookups for classes, methods, variables
- **Acceptance Criteria:**
  - Maintain in-memory symbol table for all identifiers
  - Support autocomplete suggestions
  - Return instant results for exact matches
  - Include all locations where symbol is defined
  - Show symbol type (class, method, field, etc.)

#### FR-2.5: Similar Code Search
- **Description:** Find code similar to a given snippet
- **Acceptance Criteria:**
  - Accept code snippet as input
  - Generate embedding for input
  - Query vector database for similar vectors
  - Return ranked list of similar code
  - Highlight structural similarities

### 5.3 Code Analysis Features

#### FR-3.1: Dependency Visualization
- **Description:** Visual representation of code dependencies
- **Acceptance Criteria:**
  - Display interactive dependency graph
  - Support zoom, pan, and navigation
  - Highlight selected node and its connections
  - Show dependency paths between two nodes
  - Detect and highlight circular dependencies
  - Export graph as image (PNG, SVG)

#### FR-3.2: Call Hierarchy
- **Description:** Show methods that call or are called by a given method
- **Acceptance Criteria:**
  - Display call tree (callers and callees)
  - Support multi-level hierarchy exploration
  - Show call count/frequency if available
  - Provide navigation to each call site
  - Identify external library calls

#### FR-3.3: Dead Code Detection
- **Description:** Identify potentially unused code
- **Acceptance Criteria:**
  - Analyze call graph to find unreferenced methods
  - Identify unused private methods/fields
  - Detect unused imports
  - Generate report with file locations
  - Allow manual marking of intentionally unused code

#### FR-3.4: Complexity Metrics
- **Description:** Calculate code quality metrics
- **Acceptance Criteria:**
  - Calculate cyclomatic complexity for methods
  - Compute lines of code (LOC) metrics
  - Identify overly complex methods (threshold-based)
  - Generate metrics report
  - Support filtering by metric ranges

#### FR-3.5: Pattern Detection
- **Description:** Identify design patterns and anti-patterns
- **Acceptance Criteria:**
  - Detect common design patterns (Singleton, Factory, etc.)
  - Identify anti-patterns (God Class, Long Method, etc.)
  - Use AST-based pattern matching
  - Generate pattern usage report
  - Provide examples for each detected pattern

### 5.4 Web User Interface

#### FR-4.1: Search Interface
- **Description:** Main search page for querying code
- **Acceptance Criteria:**
  - Prominent search bar with autocomplete
  - Display search results in ranked order
  - Show code snippets with syntax highlighting
  - Include file path and line numbers
  - Support result pagination
  - Provide filters (by file, package, type)

#### FR-4.2: Code Viewer
- **Description:** Display source code with rich features
- **Acceptance Criteria:**
  - Use Monaco Editor for code display
  - Syntax highlighting for Java
  - Line numbers and highlighting
  - "Go to definition" support
  - Code folding
  - Copy to clipboard functionality

#### FR-4.3: Graph Visualization Panel
- **Description:** Interactive dependency graph viewer
- **Acceptance Criteria:**
  - Render graphs using D3.js or Cytoscape.js
  - Support zoom, pan, drag
  - Node selection and highlighting
  - Edge hover to show relationship details
  - Layout algorithms (hierarchical, force-directed)
  - Export graph as image

#### FR-4.4: Analysis Dashboard
- **Description:** Overview of code metrics and insights
- **Acceptance Criteria:**
  - Display project statistics (LOC, classes, methods)
  - Show complexity distribution
  - List hotspots (most complex code)
  - Display top dependencies
  - Provide quick links to analysis reports

#### FR-4.5: Indexing Console
- **Description:** Interface for managing code indexing
- **Acceptance Criteria:**
  - Trigger full or incremental indexing
  - Display indexing progress (real-time)
  - Show indexing statistics (files, chunks, time)
  - Configure indexing options (exclusions, batch size)
  - Cancel ongoing indexing operation

### 5.5 REST API

#### FR-5.1: Search Endpoints
```
POST /api/search
  - Query: { "query": string, "filters": object, "limit": number }
  - Response: { "results": [...], "total": number, "time": number }

GET /api/search/autocomplete?q={query}
  - Response: { "suggestions": [string] }
```

#### FR-5.2: Analysis Endpoints
```
GET /api/analysis/complexity?file={path}
  - Response: { "metrics": [...] }

GET /api/analysis/deadcode
  - Response: { "unused": [...] }

GET /api/analysis/patterns
  - Response: { "patterns": [...] }
```

#### FR-5.3: Graph Endpoints
```
GET /api/graph/dependencies?class={name}
  - Response: { "nodes": [...], "edges": [...] }

GET /api/graph/callhierarchy?method={signature}
  - Response: { "callers": [...], "callees": [...] }

GET /api/graph/path?from={A}&to={B}
  - Response: { "paths": [[...]] }
```

#### FR-5.4: Index Endpoints
```
POST /api/index/trigger
  - Body: { "mode": "full"|"incremental", "path": string }
  - Response: { "status": "started", "jobId": string }

GET /api/index/status?jobId={id}
  - Response: { "progress": number, "status": string, "stats": {...} }

DELETE /api/index/cancel?jobId={id}
  - Response: { "status": "cancelled" }
```

---

## 6. Non-Functional Requirements

### 6.1 Performance
- **NFR-1.1:** Query response time < 2 seconds for 90% of queries on 1M LOC
- **NFR-1.2:** Indexing throughput > 10,000 LOC per minute
- **NFR-1.3:** Memory usage < 4GB for 1M LOC indexed
- **NFR-1.4:** Application startup time < 10 seconds
- **NFR-1.5:** Graph rendering < 1 second for graphs with < 100 nodes

### 6.2 Scalability
- **NFR-2.1:** Support codebases up to 5M LOC
- **NFR-2.2:** Handle projects with 10,000+ Java files
- **NFR-2.3:** Maintain index for multiple projects simultaneously
- **NFR-2.4:** Support incremental updates without full re-index

### 6.3 Reliability
- **NFR-3.1:** Gracefully handle parsing errors without crashing
- **NFR-3.2:** Recover from incomplete indexing operations
- **NFR-3.3:** Maintain index consistency across updates
- **NFR-3.4:** Provide detailed error messages for troubleshooting

### 6.4 Usability
- **NFR-4.1:** Application must work offline (no internet required)
- **NFR-4.2:** Single-click installation process
- **NFR-4.3:** No manual configuration required for basic usage
- **NFR-4.4:** Intuitive UI accessible to developers without training
- **NFR-4.5:** Comprehensive user documentation

### 6.5 Portability
- **NFR-5.1:** Support Windows, macOS, and Linux
- **NFR-5.2:** Include embedded JRE (no Java installation required)
- **NFR-5.3:** Self-contained package (all dependencies bundled)
- **NFR-5.4:** Consistent behavior across platforms

### 6.6 Security
- **NFR-6.1:** No data leaves the local machine
- **NFR-6.2:** No external API calls or telemetry
- **NFR-6.3:** Secure local web server (localhost only)
- **NFR-6.4:** No persistence of sensitive data

---

## 7. Implementation Phases

### Phase 1: Foundation (Weeks 1-2)
**Goal:** Establish core infrastructure and basic indexing

**Tasks:**
- Set up Spring Boot project structure ✅ (COMPLETE)
- Integrate JavaParser for code parsing ✅ (COMPLETE)
- Integrate Lucene for full-text search ✅ (COMPLETE)
- Integrate JGraphT for dependency graphs ✅ (COMPLETE)
- Set up H2 database schema
- Integrate ONNX Runtime and download all-MiniLM-L6-v2 model
- Integrate Chroma vector database (local instance)
- Create basic REST API structure ✅ (COMPLETE - HealthController)
- Test end-to-end: Code → Parse → Embed → Store → Retrieve

**Deliverables:**
- Working Spring Boot application with embedded Tomcat ✅
- Basic services for parsing, search, and graph analysis ✅
- Ability to index small codebase and perform simple searches
- Unit tests for core services ✅

### Phase 2: Code Indexing Engine (Weeks 3-4)
**Goal:** Complete indexing pipeline with all features

**Tasks:**
- Implement directory scanner with exclusion patterns
- Build AST extraction logic (classes, methods, imports)
- Implement intelligent chunking strategy
- Create embedding service using ONNX Runtime
- Build full-text indexing with Lucene
- Implement dependency graph construction
- Add progress reporting for indexing
- Implement incremental indexing
- Create indexing service integration tests

**Deliverables:**
- Complete indexing pipeline for Java projects
- Support for full and incremental indexing
- Ability to index 100K+ LOC projects
- Comprehensive test coverage

### Phase 3: Search & Query Engine (Weeks 5-6)
**Goal:** Implement hybrid search with ranking

**Tasks:**
- Build hybrid search (semantic + keyword)
- Implement query classification logic
- Create result ranking and merging algorithm
- Build symbol table for fast lookups
- Implement context assembly for results
- Add autocomplete functionality
- Create similar code search feature
- Performance optimization (caching, batching)
- Load testing with large codebases

**Deliverables:**
- Functional hybrid search with <2s response time
- Autocomplete and symbol lookup
- Context-rich search results
- Similar code detection

### Phase 4: Analysis Features (Weeks 7-8)
**Goal:** Implement code analysis capabilities

**Tasks:**
- Build dependency visualization logic
- Implement call hierarchy analysis
- Create dead code detection algorithm
- Add complexity metrics calculation
- Implement pattern detection (design patterns)
- Build analysis report generation
- Create graph traversal algorithms
- Add circular dependency detection

**Deliverables:**
- Dependency graph analysis
- Code quality metrics
- Dead code detection
- Pattern recognition
- Analysis reports

### Phase 5: Web UI Development (Weeks 9-10)
**Goal:** Build user interface

**Tasks:**
- Design UI mockups and user flows
- Implement search interface with React
- Integrate Monaco Editor for code viewing
- Build graph visualization with D3.js/Cytoscape.js
- Create analysis dashboard
- Implement indexing console
- Add responsive design for different screen sizes
- Implement keyboard shortcuts and navigation
- User testing and feedback incorporation

**Deliverables:**
- Complete web UI with all features
- Interactive code viewer
- Graph visualization
- Responsive design
- User-friendly navigation

### Phase 6: Desktop Packaging (Weeks 11-12)
**Goal:** Create standalone desktop application

**Tasks:**
- Configure Spring Boot for embedded deployment
- Bundle embedded JRE (Java 21)
- Package Chroma database
- Include ONNX models in distribution
- Use jpackage to create native installers (.exe, .dmg, .deb)
- Create application launcher
- Add system tray integration
- Test installation on Windows, macOS, Linux
- Create uninstaller
- Build CI/CD pipeline for releases

**Deliverables:**
- Native installers for Windows, macOS, Linux
- Self-contained distribution with embedded JRE
- Auto-launch browser on startup
- System tray controls

### Phase 7: Optimization & Polish (Week 13)
**Goal:** Performance tuning and final touches

**Tasks:**
- Profile application for bottlenecks
- Optimize embedding generation (batch processing)
- Implement query result caching
- Optimize graph rendering for large graphs
- Memory usage optimization
- Add telemetry/logging for debugging
- Create comprehensive user documentation
- Record demo videos and tutorials
- Final bug fixes and edge case handling

**Deliverables:**
- Optimized application meeting NFR targets
- User documentation
- Tutorial videos
- Release notes
- 1.0 release candidate

---

## 8. Current Status

### 8.1 Completed Components ✅
- **Spring Boot Application:** Basic structure with embedded Tomcat
- **CodeParserService:** JavaParser integration for AST parsing
- **CodeSearchService:** Apache Lucene integration for full-text search
- **DependencyGraphService:** JGraphT integration for graph analysis
- **HealthController:** Basic REST endpoint for health checks
- **Build System:** Maven configuration with all dependencies
- **Test Structure:** Unit test framework with basic tests

### 8.2 In Progress 🚧
- ONNX Runtime integration for embeddings
- Chroma vector database integration
- H2 database schema design

### 8.3 Not Started 📋
- Complete indexing pipeline
- Hybrid search implementation
- Web UI development
- Code analysis features
- Desktop packaging
- Documentation

### 8.4 Known Issues
- Package naming inconsistency (`com.caveanimal` in pom.xml vs `com.codetalkerl` in code)
- Chroma Java client dependency commented out in pom.xml
- No embedding service implementation yet
- No frontend UI implemented

---

## 9. Technical Challenges & Mitigation

### 9.1 Challenge: Offline Embedding Generation
**Issue:** Running ML models locally without GPU acceleration may be slow  
**Mitigation:**
- Use lightweight model (all-MiniLM-L6-v2)
- Batch processing for embeddings
- Cache embeddings to avoid recomputation
- Consider quantized ONNX models for faster inference

### 9.2 Challenge: Vector Database Integration
**Issue:** Chroma Java client is not officially supported  
**Mitigation:**
- Use REST API to communicate with Chroma
- Bundle Chroma server with application
- Consider alternative: build simple vector store with PostgreSQL + pgvector
- Fallback: In-memory FAISS via JNI bindings

### 9.3 Challenge: Large Codebase Performance
**Issue:** 1M+ LOC may strain memory and processing time  
**Mitigation:**
- Streaming file processing (don't load all in memory)
- Incremental indexing to limit reprocessing
- Database pagination for large result sets
- Lazy loading for UI components

### 9.4 Challenge: Cross-Platform Packaging
**Issue:** Creating native installers for multiple OS platforms  
**Mitigation:**
- Use jpackage (built into JDK 14+)
- Test on virtual machines for each platform
- Automate builds with GitHub Actions
- Provide fallback: executable JAR with instructions

### 9.5 Challenge: Dependency Graph Complexity
**Issue:** Large codebases may have thousands of dependencies  
**Mitigation:**
- Implement graph filtering and focusing
- Limit visualization to N-levels deep
- Use progressive rendering for large graphs
- Provide zoom/pan navigation

---

## 10. Dependencies & Assumptions

### 10.1 External Dependencies
- **Java 21:** Required for application runtime
- **Chroma:** Vector database (must be installed/bundled)
- **ONNX Model:** all-MiniLM-L6-v2 (must be downloaded)
- **Maven:** Required for building from source

### 10.2 Assumptions
- Target codebase is Java (Java 8 or newer)
- Source code is available locally (not remote repositories)
- User has sufficient disk space for indices (estimate: 20% of source size)
- User machine has minimum 4GB RAM available
- Modern web browser available (Chrome, Firefox, Edge)

---

## 11. Out of Scope (v1.0)

The following features are explicitly **not included** in version 1.0:

- **Multi-language support:** Only Java is supported initially
- **Cloud deployment:** Desktop application only, no server deployment
- **Real-time collaboration:** Single-user application
- **Code editing:** Read-only code viewing (no IDE features)
- **Version control integration:** No Git integration
- ~~**LLM-based Q&A:** No natural language generation (answers are structural, not conversational)~~ **[REVISED - See Section 11.1 below]**
- **Remote indexing:** Must index local codebases only
- **Plugin system:** No extensibility via plugins
- **Custom analyzers:** Limited to built-in analysis features

These may be considered for future versions based on user feedback.

---

## 11.1 LLM Integration (Added: Phase 2 Enhancement)

### Rationale for LLM Addition

After architecture review, incorporating a local LLM provides significant value:

1. **Code Explanation & Documentation** – Generate brief human-readable explanations of complex methods and dependencies, leveraging already-indexed code
2. **Dead Code Analysis** – Assist in identifying unused code by understanding intent and usage patterns
3. **Documentation Generation** – Auto-generate Javadoc-style comments from code analysis
4. **Dependency Explanation** – Provide natural language summaries of why classes/methods are related

### Implementation Approach

- **Model:** CodeLlama 7B (quantized, CPU-only, ~3-5GB RAM)
- **Deployment:** Python microservice (FastAPI) running alongside Java backend
- **Integration:** REST API calls from Spring Boot to Python service for enrichment
- **Scope:** Optional enhancement to search results and analysis features, not core functionality

### Key Features Enabled

- Code summarization in search results
- Automated explanation generation for dependency graphs
- Pattern detection and anti-pattern alerts
- Code quality insights

### Technical Stack Addition

- **Python 3.12** microservice runtime
- **CodeLlama 7B** model (open-source, offline)
- **FastAPI** for Python service endpoints
- **ONNX Runtime optimization** for inference
- **Inter-process communication:** REST/gRPC between Java and Python services

---

## 12. Release Criteria

Version 1.0 will be released when:

1. ✅ All Phase 1-7 deliverables are complete
2. ✅ All functional requirements (FR) are implemented
3. ✅ All non-functional requirements (NFR) are met
4. ✅ Unit test coverage > 70%
5. ✅ Integration tests passing for core workflows
6. ✅ Performance benchmarks met (query <2s, indexing >10K LOC/min)
7. ✅ Successfully tested on Windows, macOS, Linux
8. ✅ User documentation complete
9. ✅ No critical or high-severity bugs
10. ✅ Demo video and tutorials created

---

## 13. Glossary

- **AST:** Abstract Syntax Tree - tree representation of source code structure
- **BM25:** Best Matching 25 - ranking function for full-text search
- **Chunking:** Dividing code into semantically meaningful pieces for indexing
- **Embedding:** Vector representation of code in high-dimensional space
- **ONNX:** Open Neural Network Exchange - format for ML models
- **RAG:** Retrieval Augmented Generation - technique combining search with LLM
- **Semantic Search:** Search based on meaning/context rather than keywords
- **Symbol Table:** Data structure mapping identifiers to their definitions
- **Vector Database:** Database optimized for storing and querying embeddings

---

## 14. References

- [JavaParser Documentation](https://javaparser.org/)
- [Apache Lucene Guide](https://lucene.apache.org/core/)
- [JGraphT User Guide](https://jgrapht.org/)
- [ONNX Runtime Java API](https://onnxruntime.ai/docs/api/java/)
- [Chroma Documentation](https://docs.trychroma.com/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Sentence-Transformers Models](https://www.sbert.net/)

---

**Document Version History:**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-10-13 | GitHub Copilot | Initial PRD based on conversations01.md and project state |

---

**Approvals:**

---

## 15. Auto-Derived App Names & Multi-Application Support (November 13, 2025)

### 15.1 Problem Statement
To enable Firestick to work with multiple legacy applications simultaneously, each with complete data isolation, the system needs a reliable way to distinguish one application from another. Currently, app names must be provided manually during indexing, which is error-prone and cumbersome.

### 15.2 Solution: Auto-Derived Application Names
The application name should be automatically derived from the folder name containing the source code. For example:
- Folder: `E:\MyProjects\MyApp` → App Name: `myapp`
- Folder: `/home/dev/legacy-system` → App Name: `legacy_system`
- Folder: `C:\workspace\App-2025` → App Name: `app_2025`

This provides:
1. **Zero-configuration defaults** - App names auto-populate from folder structure
2. **Human-readable identifiers** - Derived from actual project folder names
3. **Override capability** - Users can edit names if folder names are cryptic
4. **Complete data isolation** - Each app has separate data across all backends

### 15.3 Implementation Plan

#### Phase 1: Auto-Derivation & UI Editing
**Objective:** Enable automatic app name derivation and allow users to override before indexing

**Tasks:**
1. ✅ Implement `deriveAppNameFromPath()` method in IndexingService
   - Extract folder name from path (Windows & Unix paths)
   - Sanitize name: lowercase, replace non-alphanumeric with underscore, collapse repeats
   - Return sanitized name or "default" if derivation fails

2. Modify `IndexingRequest` to support optional appName override
   - Keep existing appName field
   - Support null/blank/"default" to trigger auto-derivation
   - Send auto-derived name to frontend

3. Add app name input field in Indexing.tsx UI
   - Display auto-derived app name in read-only field
   - Add editable text input below with "Edit" button
   - Allow user to override auto-derived name before starting indexing
   - Pass appName to startIndexing() API call

**Database Impacts:** None - no changes to existing data

**API Changes:**
```
POST /api/indexing/start
Request body now includes optional appName override
Response includes auto-derived appName for UI feedback
```

#### Phase 2: App Management & Renaming
**Objective:** Allow users to rename applications and update all related data

**Tasks:**
1. Create `AppRenameRequest` DTO
   ```
   {
     "oldAppName": "myapp",
     "newAppName": "my_legacy_app"
   }
   ```

2. Implement rename endpoint in IndexingController
   ```
   POST /api/indexing/apps/{oldName}/rename
   ```
   
3. Implement app rename service with batch updates:
   - **H2 Database:** Update app_name column in code_file, code_chunk, indexing_job tables
   - **Chroma:** Create new collection, copy embeddings, delete old collection
   - **Transaction:** All-or-nothing atomicity for data consistency
   - **Validation:** Check if old app exists, new name doesn't exist
   - **Response:** Return affected record counts

4. Add rename UI in Indexing.tsx
   - Show list of indexed applications
   - Add "Rename" button next to each app
   - Modal dialog with old/new name inputs
   - Confirmation dialog before renaming
   - Toast notification on success/failure

**Database Impacts:**
- H2: UPDATE statements for code_file, code_chunk, indexing_job (app_name column)
- Chroma: New collection creation and data migration

#### Phase 3: App Selection & Filtering
**Objective:** Enable users to select and filter results by application

**Tasks:**
1. Create app list endpoint in IndexingController
   ```
   GET /api/indexing/apps
   Response: { "apps": ["myapp", "legacy_system", "..."] }
   ```

2. Modify search service to accept app filter
   - Update SearchService to filter by app_name in both H2 and Chroma queries
   - If app filter provided, only search within that app's data
   - If no filter, search across all apps (current behavior)

3. Add app selection dropdown to Search.tsx
   - Fetch available apps on page load
   - Add dropdown for app selection
   - Pass selected app to search API
   - Display selected app in search context

4. Update search endpoints to accept app parameter
   ```
   POST /api/search
   Body: { "query": "...", "appName": "myapp", ... }
   ```

**Database Impacts:** Query filtering only - no schema changes

### 15.4 Data Consistency Strategy

**Multi-Tenant Isolation:**
- Each app has isolated data: H2 records with app_name column, Chroma collections with app-specific naming
- TenantContext ensures queries respect app boundaries
- TenantFilter extracts app parameter from requests

**Rename Operation Safety:**
1. Validate: Check old app exists, new name doesn't conflict
2. Begin transaction
3. Update H2: CodeFile, CodeChunk, IndexingJob records
4. Update Chroma: Create new collection, copy embeddings
5. Delete old Chroma collection
6. Commit transaction
7. If any step fails, rollback all changes

**App Deletion (Future):**
- Would require cleanup of all related records in H2 and Chroma
- Implement in Phase 4 if needed

### 15.5 Configuration & Defaults

**Sanitization Rules (consistent with ChromaUtil):**
```
1. Convert to lowercase: MyApp → myapp
2. Replace non-alphanumeric with underscore: MyApp-2025 → myapp_2025
3. Collapse multiple underscores: my__app → my_app
4. Trim leading/trailing underscores: _myapp_ → myapp
5. If result is empty, return "default"
```

**UI Default Behavior:**
- Auto-derived name shown on load
- User can edit or confirm without changes
- Selected app persists during session
- App selection remembered in browser localStorage

### 15.6 Testing Strategy

**Unit Tests:**
- Test deriveAppNameFromPath() with various path formats
- Test sanitization with edge cases (special chars, unicode, etc.)
- Test rename validation (conflicts, non-existent apps)

**Integration Tests:**
- Index with auto-derived name, verify app_name in database
- Rename app, verify all records updated in H2 and Chroma
- Search with app filter, verify results isolated to selected app
- Verify cross-app data isolation (app A can't access app B data)

**Manual Testing:**
- Index sample project, verify auto-derived name
- Edit app name, verify change reflected in UI
- Index second project with different name
- Verify both projects appear in app dropdown
- Search within specific app, verify results isolated
- Rename app and verify data integrity

### 15.7 User Workflow

**Scenario 1: Index New Application (Auto-Derived Name)**
```
1. User clicks "Browse" and selects folder: /projects/customers_db
2. Frontend auto-derives app name: "customers_db"
3. UI displays: "App Name: customers_db (editable)"
4. User clicks "Start Indexing"
5. Backend stores data with app_name="customers_db"
6. Indexing complete, app available in dropdown
```

**Scenario 2: Index with Custom Name**
```
1. User selects folder: /projects/legacy-auth-v2.3
2. Frontend auto-derives: "legacy_auth_v2_3"
3. User clicks "Edit" and changes to: "legacy_auth"
4. User clicks "Start Indexing"
5. Backend stores data with app_name="legacy_auth"
```

**Scenario 3: Rename Application**
```
1. User sees list of apps: ["myapp", "legacy_system"]
2. Clicks "Rename" next to "legacy_system"
3. Modal shows: Old: "legacy_system", New: (empty)
4. User types: "ecommerce_platform"
5. Clicks "Confirm" with warning: "This will update X files and Y chunks"
6. Rename completes, app dropdown updates
```

**Scenario 4: Search Specific Application**
```
1. User opens Search page
2. App dropdown shows: ["myapp", "legacy_system", "ecommerce_platform"]
3. User selects: "ecommerce_platform"
4. User searches: "payment processing"
5. Results filtered to only ecommerce_platform data
6. Results show: file paths, code snippets, all from selected app
```

### 15.8 Success Criteria

✅ **Completion Checklist:**
- [ ] Auto-derived name calculation working correctly
- [ ] App name editing UI implemented and functional
- [ ] App name persisted correctly in H2 and Chroma
- [ ] App rename endpoint created and tested
- [ ] Rename operation updates H2 and Chroma atomically
- [ ] App list endpoint returns all indexed applications
- [ ] Search filtering by app working end-to-end
- [ ] Cross-app data isolation verified (manual test)
- [ ] UI properly displays selected app context
- [ ] Edge cases handled (special chars, unicode, conflicts)

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Product Owner | | | |
| Technical Lead | | | |
| Project Manager | | | |

---

*End of Product Requirements Document*
