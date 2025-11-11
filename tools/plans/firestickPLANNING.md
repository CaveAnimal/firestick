# Firestick - Project Planning Document

**Version:** 1.0  
**Date:** October 13, 2025  
**Project:** Firestick - Legacy Code Analysis and Search Tool  
**Repository:** firestick (CaveAnimal/firestick)  
**Planning Horizon:** 13 Weeks (October 2025 - January 2026)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Project Timeline](#2-project-timeline)
3. [Team & Resources](#3-team--resources)
4. [Current Status Assessment](#4-current-status-assessment)
5. [Phase-by-Phase Planning](#5-phase-by-phase-planning)
6. [Risk Management](#6-risk-management)
7. [Quality Assurance Plan](#7-quality-assurance-plan)
8. [Communication Plan](#8-communication-plan)
9. [Success Metrics & KPIs](#9-success-metrics--kpis)
10. [Budget & Resources](#10-budget--resources)

---

## 1. Executive Summary

### 1.1 Project Overview
Firestick is a standalone desktop Java web application designed to help developers understand and navigate large legacy codebases (1M+ LOC) without requiring external AI services. The project aims to deliver intelligent code search, dependency analysis, and structural insights through offline semantic search and static analysis.

### 1.2 Key Objectives
- ✅ **Foundation Complete:** Spring Boot application with basic services (Phase 1)
- 🎯 **Next Milestone:** Complete indexing engine by Week 4
- 🎯 **Target Release:** Version 1.0 by Week 13 (Early January 2026)

### 1.3 Critical Success Factors
1. Successful offline embedding generation with acceptable performance
2. Effective integration with Chroma vector database
3. Hybrid search achieving <2s response time for 1M LOC
4. Cross-platform packaging working on Windows, macOS, and Linux
5. User-friendly interface requiring minimal training

---

## 2. Project Timeline

### 2.1 High-Level Milestones

| Milestone | Target Date | Status | Dependencies |
|-----------|-------------|--------|--------------|
| **M1: Foundation** | Week 2 (Oct 27) | ✅ **COMPLETE** | None |
| **M2: Indexing Engine** | Week 4 (Nov 10) | 📋 Planned | M1 |
| **M3: Search Engine** | Week 6 (Nov 24) | 📋 Planned | M2 |
| **M4: Analysis Features** | Week 8 (Dec 8) | 📋 Planned | M2, M3 |
| **M5: Web UI** | Week 10 (Dec 22) | 📋 Planned | M3, M4 |
| **M6: Desktop Packaging** | Week 12 (Jan 5) | 📋 Planned | M5 |
| **M7: Release 1.0** | Week 13 (Jan 12) | 📋 Planned | M6 |

### 2.2 Gantt Chart Overview

```
Week:        1    2    3    4    5    6    7    8    9   10   11   12   13
             Oct  Oct  Nov  Nov  Nov  Nov  Dec  Dec  Dec  Dec  Jan  Jan  Jan
Phase 1:    [====DONE====]
Phase 2:              [==========]
Phase 3:                        [==========]
Phase 4:                              [==========]
Phase 5:                                    [==========]
Phase 6:                                              [==========]
Phase 7:                                                        [====]

Testing:              [continuous throughout all phases]
Documentation:        [continuous throughout all phases]
```

### 2.3 Sprint Schedule (2-Week Sprints)

| Sprint | Dates | Focus Area | Key Deliverables |
|--------|-------|------------|------------------|
| **Sprint 0** | Oct 6-20 | Foundation | ✅ Spring Boot app, Basic services |
| **Sprint 1** | Oct 21-Nov 3 | Indexing Part 1 | Directory scanning, AST extraction, Chunking |
| **Sprint 2** | Nov 4-17 | Indexing Part 2 | Embeddings, Graph building, Incremental indexing |
| **Sprint 3** | Nov 18-Dec 1 | Search Engine | Hybrid search, Ranking, Symbol table |
| **Sprint 4** | Dec 2-15 | Analysis & UI Start | Analysis features, UI foundation |
| **Sprint 5** | Dec 16-29 | UI Development | Complete web interface |
| **Sprint 6** | Dec 30-Jan 12 | Packaging & Release | Desktop packaging, Documentation, Release |

---

## 3. Team & Resources

### 3.1 Team Structure

**Core Development Team:**
- **Technical Lead / Architect** - Overall design, code reviews, technical decisions
- **Backend Developer(s)** - Java services, indexing, search engine
- **Frontend Developer** - React UI, graph visualization
- **QA Engineer** - Testing, quality assurance, bug tracking

**Supporting Roles:**
- **DevOps Engineer** - CI/CD, build automation, packaging (part-time)
- **Technical Writer** - Documentation, tutorials (part-time)

### 3.2 Current Team Assignment
Based on project scope, this appears to be a small team project. Adjust assignments based on available resources.

### 3.3 Development Environment

**Required Tools:**
- **IDE:** IntelliJ IDEA or Eclipse with Java 21 support
- **Build:** Maven 3.6+
- **Version Control:** Git + GitHub
- **Database:** H2 (embedded), Chroma (local instance)
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **Frontend:** Node.js 18+, npm/yarn for React development

**Infrastructure:**
- Development machines with minimum 16GB RAM, 256GB SSD
- Test environments for Windows, macOS, Linux (VMs acceptable)

---

## 4. Current Status Assessment

### 4.1 Completed Work ✅

**Phase 1 Foundation (Week 1-2): ~95% Complete**

**Implemented Components:**
1. ✅ **Spring Boot Application Structure**
   - Spring Boot 3.5.6 with embedded Tomcat
   - Maven build configuration complete
   - Application runs successfully on port 8080

2. ✅ **Core Service Classes**
   - `CodeParserService` - JavaParser integration functional
   - `CodeSearchService` - Lucene indexing and search working
   - `DependencyGraphService` - JGraphT graph creation operational

3. ✅ **REST API Foundation**
   - `HealthController` - Basic health check endpoint
   - Controller layer structure established

4. ✅ **Testing Infrastructure**
   - Test package structure created
   - Basic unit tests for all services
   - Spring Boot test configuration

5. ✅ **Dependencies Configured**
   - JavaParser 3.26.3
   - JGraphT 1.5.2
   - Apache Lucene 9.12.0
   - ONNX Runtime 1.20.0
   - DJL 0.31.1
   - H2 Database

### 4.2 In-Progress Work 🚧

**Immediate Tasks:**
1. 🚧 **Package Naming Consistency**
   - Issue: Mixed usage of `com.caveanimal` and `com.codetalkerl`
   - Action: Standardize to one package name

2. 🚧 **Chroma Integration**
   - Status: Dependency commented out in pom.xml
   - Action: Evaluate Chroma Java client or REST API approach

3. 🚧 **ONNX Runtime Setup**
   - Status: Dependency added but not integrated
   - Action: Download model, create EmbeddingService

4. 🚧 **H2 Database Schema**
   - Status: H2 configured but schema not defined
   - Action: Design tables for metadata, symbols, graphs

### 4.3 Pending Work 📋

**Major Components Not Started:**
- Complete indexing pipeline
- Embedding generation service
- Hybrid search implementation
- Web UI (React application)
- Code analysis features
- Desktop packaging
- End-user documentation

### 4.4 Technical Debt & Issues

**Known Issues:**
1. **Package naming inconsistency** - Must be resolved before proceeding
2. **No error handling** - Services lack comprehensive error handling
3. **Limited test coverage** - Tests are basic, need expansion
4. **No logging framework** - Need structured logging (SLF4J + Logback)
5. **No configuration management** - Need externalized configuration
6. **Chroma integration unclear** - Need decision on approach

**Priority Fixes (Sprint 1):**
- [ ] Standardize package naming across project
- [ ] Add comprehensive logging with SLF4J
- [ ] Implement error handling and exception management
- [ ] Expand test coverage to >70%
- [ ] Add configuration properties management

### 4.5 Recent Progress (Nov 3, 2025)

- File discovery defaults have been externalized into `IndexingConfig` (Spring `@ConfigurationProperties` with prefix `indexing`).
  - Preserved prior scanning semantics; tests validate default behavior and exclusion handling.
  - Added commented examples in `src/main/resources/application.properties` to optionally override:
    - `indexing.file-extensions`
    - `indexing.exclude-directories`
    - `indexing.exclude-patterns`
- Minor docs update: README now includes a brief section on tuning `indexing.*` properties and the corrected H2 JDBC URL.


---

## 5. Phase-by-Phase Planning

### Phase 1: Foundation (COMPLETE ✅)

**Duration:** Week 1-2 (Oct 6-20, 2025)  
**Status:** ~95% Complete  
**Team:** Backend Developer

**Deliverables:**
- ✅ Spring Boot project structure
- ✅ Core service classes (Parser, Search, Graph)
- ✅ Basic REST API with health endpoint
- ✅ Maven build configuration
- ✅ Unit test framework

**Outstanding Phase 1 Tasks:**
- [ ] Fix package naming inconsistency
- [ ] Add logging framework
- [ ] Enhance error handling

---

### Phase 2: Code Indexing Engine

**Duration:** Week 3-4 (Oct 21 - Nov 10, 2025)  
**Status:** 📋 Not Started  
**Team:** Backend Developer(s)

#### Sprint 1 (Oct 21 - Nov 3): Indexing Foundation

**Week 3 Detailed Tasks:**

**Day 1-2: Setup & Technical Debt**
- [ ] Resolve package naming to `com.codetalkerl.firestick` (4h)
- [ ] Add SLF4J + Logback logging configuration (2h)
- [ ] Implement global exception handling with `@ControllerAdvice` (2h)
- [ ] Create configuration properties file structure (2h)
- [ ] Code review and merge (2h)

**Day 3-4: Directory Scanning**
- [ ] Create `FileDiscoveryService` (6h)
  - Recursive directory traversal
  - File extension filtering (.java)
  - Exclusion patterns (target/, build/, .git/)
  - Progress reporting
- [ ] Unit tests for `FileDiscoveryService` (4h)
- [ ] Integration test with sample project (2h)

**Day 5: AST Extraction Enhancement**
- [ ] Enhance `CodeParserService` (8h)
  - Extract method signatures with full details
  - Capture parameter types and return types
  - Extract JavaDoc and inline comments
  - Parse import statements
  - Handle parsing errors gracefully
- [ ] Unit tests for enhanced parsing (4h)

**Week 4 Detailed Tasks:**

**Day 1-2: Chunking Strategy**
- [ ] Create `CodeChunkingService` (8h)
  - Method-level chunks with class context
  - Class-level summaries
  - Package-level overviews
  - Maintain metadata (file, line numbers, signature)
- [ ] Create `CodeChunk` entity/model (2h)
- [ ] Unit tests for chunking (4h)

**Day 3: H2 Database Schema**
- [ ] Design database schema (3h)
  ```sql
  - code_files (id, path, hash, last_indexed)
  - code_chunks (id, file_id, type, content, start_line, end_line)
  - symbols (id, name, type, signature, file_id, line_number)
  - dependencies (from_id, to_id, type)
  ```
- [ ] Create JPA entities (3h)
- [ ] Create repositories (Spring Data JPA) (2h)
- [ ] Schema migration script (2h)

**Day 4-5: Embedding Service**
- [ ] Download all-MiniLM-L6-v2 ONNX model (1h)
- [ ] Create `EmbeddingService` with ONNX Runtime (8h)
  - Model loading and initialization
  - Tokenization
  - Batch embedding generation
  - Caching mechanism
- [ ] Performance testing (2h)
- [ ] Unit tests (3h)

#### Sprint 2 (Nov 4 - Nov 17): Integration & Advanced Features

**Week 5 Detailed Tasks:**

**Day 1-2: Chroma Integration**
- [ ] Research Chroma Java client options (2h)
- [ ] Implement Chroma integration (8h)
  - Option A: REST API client
  - Option B: Java client library
  - Collection creation and management
  - Vector storage and retrieval
- [ ] Test embedding storage and retrieval (4h)

**Day 3: Lucene Enhancement**
- [ ] Enhance `CodeSearchService` (6h)
  - Add field-specific indexing (method names, class names, comments)
  - Implement BM25 ranking
  - Add filtering capabilities
- [ ] Create `LuceneIndexer` component (4h)
- [ ] Unit tests (2h)

**Day 4-5: Dependency Graph Building**
- [ ] Enhance `DependencyGraphService` (8h)
  - Parse imports and build import graph
  - Detect method calls using JavaParser
  - Build class inheritance hierarchy
  - Detect interface implementations
- [ ] Persist graph to H2 database (4h)
- [ ] Graph query methods (callers, callees, paths) (4h)
- [ ] Unit tests (4h)

**Week 6 Detailed Tasks:**

**Day 1-3: Indexing Orchestration**
- [ ] Create `IndexingService` coordinator (12h)
  - Full indexing workflow
  - Parallel processing for files
  - Progress tracking and reporting
  - Transaction management
  - Error recovery
- [ ] Create `IndexingJob` entity for tracking (2h)
- [ ] Implement job status API (2h)

**Day 4-5: Incremental Indexing**
- [ ] Implement change detection (6h)
  - File timestamp comparison
  - Hash-based change detection
  - Identify added/modified/deleted files
- [ ] Incremental update logic (6h)
  - Update only changed chunks
  - Remove deleted entries
  - Update dependency graph
- [ ] Integration tests (4h)

**Sprint 2 Deliverables:**
- [ ] Complete indexing pipeline (scan → parse → chunk → embed → store)
- [ ] Incremental indexing capability
- [ ] Dependency graph construction
- [ ] Integration tests with 10K+ LOC project
- [ ] Documentation for indexing process

---

### Phase 3: Search & Query Engine

**Duration:** Week 7-8 (Nov 18 - Dec 1, 2025)  
**Status:** 📋 Not Started  
**Team:** Backend Developer(s)

#### Sprint 3 (Nov 18 - Dec 1): Search Implementation

**Week 7 Detailed Tasks:**

**Day 1-2: Query Processing**
- [ ] Create `QueryService` (8h)
  - Query parsing and analysis
  - Query classification (exact, semantic, graph)
  - Query expansion
- [ ] Create query DTOs and models (2h)
- [ ] Unit tests (4h)

**Day 3: Semantic Search**
- [ ] Implement semantic search in `SearchService` (6h)
  - Query embedding generation
  - Vector similarity search via Chroma
  - Result retrieval with metadata
- [ ] Unit tests (3h)
- [ ] Performance tests (3h)

**Day 4: Keyword Search**
- [ ] Enhance Lucene-based keyword search (6h)
  - Multi-field search
  - Fuzzy matching
  - Phrase queries
- [ ] Unit tests (3h)
- [ ] Performance tests (3h)

**Day 5: Hybrid Search**
- [ ] Create `HybridSearchService` (8h)
  - Parallel execution of semantic + keyword
  - Result merging algorithm
  - Scoring and ranking
  - Configurable weights
- [ ] Unit tests (4h)

**Week 8 Detailed Tasks:**

**Day 1: Symbol Table**
- [ ] Create `SymbolTableService` (8h)
  - In-memory symbol table from DB
  - Fast exact lookups
  - Autocomplete support
  - Symbol type classification
- [ ] Unit tests (4h)

**Day 2: Context Assembly**
- [ ] Create `ContextAssemblyService` (8h)
  - Retrieve complete method for method matches
  - Add class structure context
  - Include caller/callee information
  - Add file path and line numbers
- [ ] Unit tests (4h)

**Day 3: Similar Code Search**
- [ ] Implement similar code search (6h)
  - Accept code snippet input
  - Generate embedding
  - Vector similarity query
  - Return ranked results
- [ ] Unit tests (3h)
- [ ] Integration tests (3h)

**Day 4-5: REST API & Caching**
- [ ] Create `SearchController` REST endpoints (6h)
  ```
  POST /api/search
  GET /api/search/autocomplete
  POST /api/search/similar
  GET /api/search/symbol
  ```
- [ ] Implement Redis caching layer (6h)
  - Cache frequent queries
  - Cache embeddings
  - TTL configuration
- [ ] API documentation (Swagger/OpenAPI) (2h)
- [ ] Integration tests (4h)

**Sprint 3 Deliverables:**
- [ ] Hybrid search with <2s response time
- [ ] Symbol table with autocomplete
- [ ] Similar code search
- [ ] REST API for search operations
- [ ] Caching layer
- [ ] API documentation

---

### Phase 4: Analysis Features

**Duration:** Week 9-10 (Dec 2 - Dec 15, 2025)  
**Status:** 📋 Not Started  
**Team:** Backend Developer

#### Sprint 4 (Dec 2 - Dec 15): Analysis Implementation

**Week 9 Detailed Tasks:**

**Day 1-2: Dependency Visualization**
- [ ] Create `DependencyAnalysisService` (8h)
  - Graph traversal algorithms
  - Path finding between nodes
  - Circular dependency detection
  - Dependency depth calculation
- [ ] Create visualization data DTOs (2h)
- [ ] REST API endpoints (4h)
  ```
  GET /api/graph/dependencies?class={name}
  GET /api/graph/path?from={A}&to={B}
  GET /api/graph/circular
  ```
- [ ] Unit tests (4h)

**Day 3: Call Hierarchy**
- [ ] Create `CallHierarchyService` (8h)
  - Find all callers of a method
  - Find all callees of a method
  - Multi-level hierarchy traversal
  - Call frequency tracking
- [ ] REST API endpoints (2h)
  ```
  GET /api/graph/callhierarchy?method={signature}
  GET /api/graph/callers?method={signature}
  GET /api/graph/callees?method={signature}
  ```
- [ ] Unit tests (4h)

**Day 4: Dead Code Detection**
- [ ] Create `DeadCodeAnalyzer` (8h)
  - Analyze call graph for unreferenced methods
  - Detect unused private methods/fields
  - Identify unused imports
  - Generate report with locations
- [ ] REST API endpoint (2h)
  ```
  GET /api/analysis/deadcode
  POST /api/analysis/deadcode/ignore
  ```
- [ ] Unit tests (4h)

**Day 5: Code Metrics**
- [ ] Create `CodeMetricsService` (8h)
  - Cyclomatic complexity calculation
  - Lines of code metrics (LOC, SLOC)
  - Method length analysis
  - Class coupling metrics
- [ ] REST API endpoints (2h)
  ```
  GET /api/analysis/metrics?file={path}
  GET /api/analysis/complexity?threshold={n}
  ```
- [ ] Unit tests (4h)

**Week 10 Detailed Tasks:**

**Day 1-2: Pattern Detection**
- [ ] Create `PatternDetectionService` (10h)
  - Singleton pattern detection
  - Factory pattern detection
  - Builder pattern detection
  - God Class anti-pattern detection
  - Long Method anti-pattern detection
- [ ] Pattern matching using AST analysis (6h)
- [ ] Unit tests (4h)

**Day 3: Analysis Reports**
- [ ] Create `ReportGenerationService` (6h)
  - Generate HTML reports
  - Generate JSON reports
  - Export functionality
- [ ] REST API endpoints (2h)
  ```
  GET /api/analysis/report?format={html|json}
  GET /api/analysis/patterns
  ```
- [ ] Unit tests (4h)

**Day 4-5: Analysis Dashboard Data**
- [ ] Create `DashboardService` (8h)
  - Project statistics aggregation
  - Complexity distribution
  - Hotspot identification
  - Top dependencies
- [ ] REST API endpoint (2h)
  ```
  GET /api/dashboard/stats
  GET /api/dashboard/hotspots
  ```
- [ ] Performance optimization (4h)
- [ ] Integration tests (4h)

**Sprint 4 Deliverables:**
- [ ] Dependency visualization API
- [ ] Call hierarchy analysis
- [ ] Dead code detection
- [ ] Code metrics calculation
- [ ] Pattern detection
- [ ] Analysis reports
- [ ] Dashboard data API

---

### Phase 5: Web UI Development

**Duration:** Week 11-12 (Dec 16 - Dec 29, 2025)  
**Status:** 📋 Not Started  
**Team:** Frontend Developer, Backend Developer (support)

#### Sprint 5 (Dec 16 - Dec 29): Frontend Development

**Week 11 Detailed Tasks:**

**Day 1: UI Setup & Design**
- [ ] Create React application (2h)
  - Create React App or Vite setup
  - Configure proxy to Spring Boot backend
  - Set up routing (React Router)
- [ ] Design UI mockups/wireframes (4h)
- [ ] Set up component library (Material-UI or Bootstrap) (2h)
- [ ] Create base layout components (4h)
  - Header/Navigation
  - Sidebar
  - Main content area
  - Footer

**Day 2-3: Search Interface**
- [ ] Create search page components (12h)
  - Search bar with autocomplete
  - Search filters
  - Result list with pagination
  - Result item with code preview
  - Syntax highlighting integration
- [ ] Connect to search API (4h)
- [ ] Implement search state management (Redux/Context) (4h)

**Day 4: Code Viewer**
- [ ] Integrate Monaco Editor (6h)
  - Code display with syntax highlighting
  - Line numbers and highlighting
  - Read-only mode
  - Copy functionality
- [ ] Create code viewer modal/panel (4h)
- [ ] Connect to backend for full file content (2h)

**Day 5: Graph Visualization Foundation**
- [ ] Research and select graph library (2h)
  - D3.js vs Cytoscape.js evaluation
- [ ] Create graph visualization component (8h)
  - Basic graph rendering
  - Node and edge styling
  - Layout algorithms
- [ ] Create graph controls (zoom, pan, reset) (2h)

**Week 12 Detailed Tasks:**

**Day 1-2: Graph Features**
- [ ] Enhance graph visualization (10h)
  - Interactive node selection
  - Edge hover details
  - Filtering and focusing
  - Export as image (PNG/SVG)
- [ ] Connect to dependency API (4h)
- [ ] Create graph legend and controls (2h)
- [ ] Performance optimization for large graphs (4h)

**Day 3: Analysis Dashboard**
- [ ] Create dashboard page (8h)
  - Project statistics widgets
  - Complexity charts (Chart.js/Recharts)
  - Hotspot list
  - Quick action buttons
- [ ] Connect to dashboard API (2h)
- [ ] Responsive layout for dashboard (2h)

**Day 4: Indexing Console**
- [ ] Create indexing console page (8h)
  - Trigger indexing button
  - Progress bar with real-time updates
  - Indexing statistics display
  - Configuration options
  - Job history
- [ ] WebSocket integration for progress updates (4h)
- [ ] Connect to indexing API (2h)

**Day 5: Polish & Testing**
- [ ] Responsive design for mobile/tablet (6h)
- [ ] Error handling and user feedback (4h)
- [ ] Loading states and animations (2h)
- [ ] Cross-browser testing (4h)

**Sprint 5 Deliverables:**
- [ ] Complete React application
- [ ] Search interface with autocomplete
- [ ] Code viewer with Monaco Editor
- [ ] Interactive graph visualization
- [ ] Analysis dashboard
- [ ] Indexing console
- [ ] Responsive design

---

### Phase 6: Desktop Packaging

**Duration:** Week 13-14 (Dec 30 - Jan 12, 2026)  
**Status:** 📋 Not Started  
**Team:** DevOps Engineer, Backend Developer

#### Sprint 6 (Dec 30 - Jan 12): Packaging & Release

**Week 13 Detailed Tasks:**

**Day 1: Packaging Preparation**
- [ ] Create production build configuration (4h)
  - Spring Boot production profile
  - React production build
  - Static resources optimization
- [ ] Embed React build in Spring Boot (3h)
- [ ] Configure embedded Tomcat settings (2h)
- [ ] Test production build locally (3h)

**Day 2: Chroma Bundling**
- [ ] Package Chroma server (6h)
  - Bundle Chroma executable
  - Create startup script
  - Configure ports and paths
- [ ] Test Chroma auto-start (4h)
- [ ] Document Chroma configuration (2h)

**Day 3: ONNX Model Packaging**
- [ ] Download and bundle ONNX model (2h)
- [ ] Configure model path in application (2h)
- [ ] Test offline model loading (2h)
- [ ] Optimize model size if needed (4h)

**Day 4: jpackage Configuration**
- [ ] Install jpackage and dependencies (2h)
- [ ] Create jpackage configuration (4h)
  - Application icon
  - Launcher scripts
  - Resource files
- [ ] Download and bundle JRE 21 (2h)
- [ ] Test packaging process (4h)

**Day 5: Windows Packaging**
- [ ] Create Windows installer (.exe) (6h)
- [ ] Configure Windows-specific settings (2h)
  - Start menu integration
  - Desktop shortcut
  - File associations
- [ ] Test installation on Windows 10/11 (4h)
- [ ] Create Windows uninstaller (2h)

**Week 14 Detailed Tasks:**

**Day 1: macOS Packaging**
- [ ] Create macOS application bundle (.dmg) (6h)
- [ ] Code signing for macOS (if available) (2h)
- [ ] Test on macOS (Intel and Apple Silicon) (6h)

**Day 2: Linux Packaging**
- [ ] Create Debian package (.deb) (4h)
- [ ] Create RPM package (.rpm) (4h)
- [ ] Test on Ubuntu and Fedora (4h)

**Day 3: Launcher Development**
- [ ] Create application launcher (6h)
  - Auto-start Spring Boot
  - Auto-start Chroma
  - Open browser to localhost:8080
  - System tray icon
- [ ] Add stop/restart controls (4h)
- [ ] Test launcher on all platforms (2h)

**Day 4: CI/CD Pipeline**
- [ ] Create GitHub Actions workflow (6h)
  - Build automation
  - Test execution
  - Packaging for all platforms
  - Release artifact upload
- [ ] Configure release process (4h)
- [ ] Test CI/CD pipeline (2h)

**Day 5: Final Testing**
- [ ] Clean installation testing (4h)
  - Windows fresh install
  - macOS fresh install
  - Linux fresh install
- [ ] Performance testing with large codebases (4h)
- [ ] Security audit (4h)

**Sprint 6 Deliverables:**
- [ ] Native installers for Windows, macOS, Linux
- [ ] Self-contained packages with embedded JRE
- [ ] Application launcher with system tray
- [ ] Auto-start browser on launch
- [ ] CI/CD pipeline for automated builds

---

### Phase 7: Optimization & Release

**Duration:** Week 15 (Jan 13-19, 2026)  
**Status:** 📋 Not Started  
**Team:** All team members

**Week 15 Detailed Tasks:**

**Day 1: Performance Optimization**
- [ ] Profile application for bottlenecks (4h)
- [ ] Optimize embedding generation (4h)
  - Batch processing improvements
  - Thread pool tuning
- [ ] Optimize query performance (4h)
  - Database query optimization
  - Index tuning
  - Cache hit rate analysis

**Day 2: Memory Optimization**
- [ ] Analyze memory usage (4h)
- [ ] Fix memory leaks (if any) (4h)
- [ ] Optimize graph rendering (4h)
  - Lazy loading for large graphs
  - Progressive rendering

**Day 3: Documentation**
- [ ] Complete user documentation (8h)
  - Installation guide
  - Quick start guide
  - Feature documentation
  - Troubleshooting guide
- [ ] Create developer documentation (4h)
  - Architecture overview
  - Setup instructions
  - Contribution guidelines

**Day 4: Tutorial Content**
- [ ] Record demo video (4h)
- [ ] Create tutorial videos (4h)
  - Indexing a project
  - Searching code
  - Analyzing dependencies
- [ ] Create screenshots and GIFs (2h)
- [ ] Update README.md (2h)

**Day 5: Release Preparation**
- [ ] Final bug fixes (4h)
- [ ] Create release notes (2h)
- [ ] Prepare release announcement (2h)
- [ ] Tag release in Git (1h)
- [ ] Deploy release artifacts (1h)
- [ ] Publish release (2h)

**Phase 7 Deliverables:**
- [ ] Optimized application meeting all NFRs
- [ ] Complete user and developer documentation
- [ ] Tutorial videos and demos
- [ ] Release notes
- [ ] v1.0 Release

---

## 6. Risk Management

### 6.1 Risk Register

| Risk ID | Risk Description | Probability | Impact | Mitigation Strategy | Owner |
|---------|-----------------|-------------|--------|---------------------|-------|
| **R1** | Chroma Java client integration fails | Medium | High | Use REST API fallback, consider pgvector alternative | Backend Dev |
| **R2** | ONNX model performance too slow | Medium | High | Use quantized models, batch processing, consider lighter model | Backend Dev |
| **R3** | Memory usage exceeds 4GB for large codebases | Medium | Medium | Implement streaming, pagination, lazy loading | Backend Dev |
| **R4** | Graph visualization slow for large graphs | Low | Medium | Progressive rendering, limit node count, zoom-based loading | Frontend Dev |
| **R5** | Cross-platform packaging issues | Medium | High | Early testing on all platforms, use VMs for testing | DevOps |
| **R6** | Search accuracy below 80% | Low | High | Improve chunking strategy, adjust ranking algorithm | Backend Dev |
| **R7** | Team member availability issues | Medium | Medium | Cross-training, documentation, modular design | Tech Lead |
| **R8** | Scope creep pushing timeline | High | High | Strict scope management, defer features to v1.1 | Tech Lead |
| **R9** | Browser compatibility issues | Low | Low | Test early, use standard web technologies | Frontend Dev |
| **R10** | Dependency conflicts in embedded distribution | Medium | Medium | Test thoroughly, document dependencies | DevOps |

### 6.2 Risk Mitigation Plans

#### R1: Chroma Integration Risk
**Mitigation Steps:**
1. Week 5 Day 1: Evaluate both REST API and Java client approaches
2. Create proof of concept with both approaches
3. If both fail, implement fallback: PostgreSQL with pgvector extension
4. Alternative: Build simple in-memory vector store with FAISS

**Contingency:** Add 1 week buffer if major rework needed

#### R2: ONNX Performance Risk
**Mitigation Steps:**
1. Benchmark early in Week 4
2. If slow, try quantized INT8 model (trade accuracy for speed)
3. Implement aggressive batching (batch size 32-64)
4. Consider using all-MiniLM-L12-v2 (smaller, faster)
5. Add progress indicators to manage user expectations

**Contingency:** If still slow, limit semantic search to smaller scopes

#### R8: Scope Creep Risk
**Management Process:**
1. Maintain strict feature freeze after Week 10
2. All new feature requests go to v1.1 backlog
3. Weekly scope review in sprint planning
4. Tech lead has veto power on scope additions

---

## 7. Quality Assurance Plan

### 7.1 Testing Strategy

#### Unit Testing
- **Target Coverage:** >70% line coverage
- **Tool:** JUnit 5, Mockito
- **Frequency:** Continuous (every commit)
- **Responsibility:** Developer who writes the code

**Key Areas:**
- All service methods
- Utility functions
- Data transformations
- Error handling paths

#### Integration Testing
- **Target Coverage:** All API endpoints
- **Tool:** Spring Boot Test, RestAssured
- **Frequency:** Every sprint
- **Responsibility:** QA Engineer

**Key Areas:**
- REST API endpoints
- Database interactions
- Service integrations
- File I/O operations

#### End-to-End Testing
- **Scope:** Critical user workflows
- **Tool:** Selenium or Playwright
- **Frequency:** Before each release
- **Responsibility:** QA Engineer

**Test Scenarios:**
1. Index a project → Search → View results
2. Index → Analyze dependencies → View graph
3. Index → Dead code detection → Review report
4. Index → Similar code search → Compare results

#### Performance Testing
- **Tool:** JMeter, custom scripts
- **Frequency:** Sprint 3, 5, 6
- **Responsibility:** Backend Developer + QA

**Metrics to Test:**
- Query response time (<2s for 1M LOC)
- Indexing throughput (>10K LOC/min)
- Memory usage (<4GB)
- Concurrent user handling

#### Security Testing
- **Scope:** Local-only security
- **Tool:** OWASP ZAP, manual review
- **Frequency:** Before release
- **Responsibility:** Tech Lead

**Focus Areas:**
- No external data transmission
- Secure local web server configuration
- Input validation
- File system access controls

### 7.2 Code Quality Standards

**Code Review Process:**
1. All code must be reviewed before merge
2. Automated checks must pass (linting, tests)
3. At least one approval required
4. Tech Lead reviews critical components

**Code Standards:**
- Follow Google Java Style Guide
- Use meaningful variable/method names
- Maximum method length: 50 lines (guideline)
- Maximum class length: 500 lines (guideline)
- Cyclomatic complexity <10 per method

**Documentation Requirements:**
- All public APIs must have Javadoc
- Complex algorithms need explanatory comments
- README for each major module

### 7.3 Definition of Done

A feature is considered "Done" when:
- [ ] Code is written and follows style guidelines
- [ ] Unit tests written with >70% coverage
- [ ] Integration tests pass
- [ ] Code reviewed and approved
- [ ] Documentation updated
- [ ] No critical or high-severity bugs
- [ ] Performance meets NFR requirements
- [ ] Merged to main branch

---

## 8. Communication Plan

### 8.1 Meeting Schedule

**Daily Standup (15 minutes)**
- **Time:** 9:00 AM daily
- **Attendees:** All team members
- **Format:**
  - What did you do yesterday?
  - What will you do today?
  - Any blockers?

**Sprint Planning (2 hours)**
- **Frequency:** Beginning of each sprint (every 2 weeks)
- **Attendees:** All team members
- **Agenda:**
  - Review previous sprint
  - Plan next sprint tasks
  - Estimate effort
  - Assign tasks

**Sprint Review (1 hour)**
- **Frequency:** End of each sprint
- **Attendees:** All team members + stakeholders
- **Agenda:**
  - Demo completed features
  - Review metrics
  - Gather feedback

**Sprint Retrospective (1 hour)**
- **Frequency:** End of each sprint
- **Attendees:** Team members only
- **Agenda:**
  - What went well?
  - What could be improved?
  - Action items for next sprint

**Technical Sync (30 minutes)**
- **Frequency:** Weekly (Wednesdays)
- **Attendees:** Tech Lead, Senior Developers
- **Agenda:**
  - Technical decisions
  - Architecture discussions
  - Blocking issues

### 8.2 Communication Channels

**Primary Tools:**
- **Code:** GitHub (firestick repository)
- **Chat:** Slack/Discord/Teams (team channel)
- **Issues:** GitHub Issues
- **Documentation:** GitHub Wiki or Confluence
- **Video:** Zoom/Teams for remote meetings

**Communication Guidelines:**
- Urgent issues: Direct message or call
- Questions: Team chat channel
- Bugs: GitHub Issues with "bug" label
- Features: GitHub Issues with "enhancement" label
- Documentation: Wiki or docs/ folder

### 8.3 Status Reporting

**Weekly Status Report:**
- **When:** Every Friday EOD
- **Format:** Email or shared document
- **Content:**
  - Completed tasks
  - In-progress tasks
  - Blocked tasks
  - Upcoming tasks
  - Risks and issues

**Sprint Burndown:**
- Updated daily
- Visible to all team members
- Tracked in GitHub Projects or Jira

---

## 9. Success Metrics & KPIs

### 9.1 Development Metrics

| Metric | Target | Measurement Frequency |
|--------|--------|----------------------|
| **Sprint Velocity** | 40-60 story points/sprint | Every sprint |
| **Code Coverage** | >70% | Every commit (CI/CD) |
| **Build Success Rate** | >95% | Every build |
| **Bug Escape Rate** | <5 bugs/sprint to production | Every sprint |
| **Code Review Time** | <24 hours | Weekly |
| **Technical Debt Ratio** | <5% | Monthly |

### 9.2 Product Metrics

| Metric | Target | How to Measure |
|--------|--------|----------------|
| **Query Response Time** | <2s for 90% of queries | Performance tests |
| **Indexing Throughput** | >10K LOC/min | Performance tests |
| **Memory Usage** | <4GB for 1M LOC | Memory profiling |
| **Application Startup** | <10 seconds | Manual testing |
| **Test Coverage** | >70% | JaCoCo reports |
| **Search Accuracy** | >80% relevant in top 10 | Manual evaluation |

### 9.3 Release Metrics

**v1.0 Release Criteria:**
- [ ] All functional requirements implemented
- [ ] All NFRs met
- [ ] 0 critical bugs, <5 high-priority bugs
- [ ] Test coverage >70%
- [ ] Performance benchmarks passed
- [ ] Successfully tested on Windows, macOS, Linux
- [ ] User documentation complete
- [ ] Demo video created

---

## 10. Budget & Resources

### 10.1 Resource Allocation

**Personnel (13 weeks):**
- Technical Lead: 13 weeks × 40 hours = 520 hours
- Backend Developer(s): 13 weeks × 80 hours = 1,040 hours (assuming 2 devs)
- Frontend Developer: 6 weeks × 40 hours = 240 hours (Weeks 11-16)
- QA Engineer: 13 weeks × 20 hours = 260 hours (part-time)
- DevOps Engineer: 4 weeks × 20 hours = 80 hours (part-time, Weeks 13-16)
- Technical Writer: 2 weeks × 20 hours = 40 hours (part-time, Weeks 14-15)

**Total Effort:** ~2,180 person-hours

### 10.2 Infrastructure & Tools

**Development Tools (Free/Open Source):**
- IntelliJ IDEA Community Edition (Free)
- VS Code (Free)
- Git + GitHub (Free for public repos)
- Maven (Free)
- Node.js + npm (Free)

**Infrastructure:**
- Development machines (existing)
- Test VMs (AWS/Azure credits or local VirtualBox - Free/Low cost)
- CI/CD (GitHub Actions - Free for public repos)

**External Dependencies (Free):**
- All libraries are open source
- ONNX models available for free
- Chroma is open source

**Estimated Budget:** $0 - $500
- Potential costs: Domain name, SSL cert, cloud storage for releases (optional)

### 10.3 Contingency Buffer

**Time Buffer:** 2 weeks contingency built into 13-week schedule
- Can absorb 1-2 week delays without impacting v1.0 release

**Resource Buffer:** 
- Tech Lead can fill in for critical path items if needed
- Consider adding contractor if major delays occur

---

## 11. Appendices

### Appendix A: Development Environment Setup

**Required Software:**
```bash
# Java Development
- Java JDK 21
- Maven 3.6+
- IntelliJ IDEA or Eclipse

# Frontend Development (for Phase 5)
- Node.js 18+
- npm or yarn

# Database
- H2 (embedded, auto-configured)
- Chroma (local installation)

# Version Control
- Git
- GitHub account with repository access

# Optional Tools
- Docker (for containerized Chroma)
- Postman (API testing)
```

**Setup Steps:**
```bash
# Clone repository
git clone https://github.com/CaveAnimal/firestick.git
cd firestick

# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Access application
# http://localhost:8080/api/health
```

### Appendix B: Definition of Terms

- **AST:** Abstract Syntax Tree
- **BM25:** Best Matching 25 (ranking function)
- **Chunking:** Dividing code into searchable segments
- **Embedding:** Vector representation of code
- **LOC:** Lines of Code
- **NFR:** Non-Functional Requirement
- **ONNX:** Open Neural Network Exchange
- **RAG:** Retrieval Augmented Generation
- **Vector DB:** Database for storing and searching embeddings

### Appendix C: Key Decisions Log

| Date | Decision | Rationale | Decided By |
|------|----------|-----------|------------|
| Oct 13, 2025 | Use Spring Boot 3.5.6 | Latest stable, good ecosystem | Tech Lead |
| Oct 13, 2025 | Target Java 21 | Latest LTS, modern features | Tech Lead |
| Oct 13, 2025 | Use Chroma for vector DB | Open source, good performance | Tech Lead |
| Oct 13, 2025 | Use all-MiniLM-L6-v2 model | Lightweight, offline-capable | Tech Lead |
| Oct 13, 2025 | Desktop application (not web service) | Meets offline requirement | Product Owner |

### Appendix D: Contact Information

**Project Team:**
- **Technical Lead:** [Name] - [email]
- **Backend Developer:** [Name] - [email]
- **Frontend Developer:** [Name] - [email]
- **QA Engineer:** [Name] - [email]

**Stakeholders:**
- **Product Owner:** [Name] - [email]
- **Project Sponsor:** [Name] - [email]

### Appendix E: References

- [Firestick PRD](./firestickPRD.md)
- [Conversations Log](./conversations01.md)
- [GitHub Repository](https://github.com/CaveAnimal/firestick)
- [JavaParser Documentation](https://javaparser.org/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Chroma Documentation](https://docs.trychroma.com/)

---

## Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Oct 13, 2025 | GitHub Copilot | Initial planning document created |

---

**Next Steps:**
1. ✅ Review and approve this planning document
2. 📋 Fix package naming inconsistency (Sprint 1, Day 1)
3. 📋 Begin Sprint 1: Indexing Foundation (Oct 21, 2025)
4. 📋 Schedule sprint planning meeting
5. 📋 Set up project tracking board (GitHub Projects)

---

*End of Planning Document*
