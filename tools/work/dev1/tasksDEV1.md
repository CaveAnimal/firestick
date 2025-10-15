# Firestick - Developer 1 Tasks (Backend Focus)

**Developer:** Developer 1 (Backend Lead)  
**Version:** 1.0  
**Date:** October 14, 2025  
**Project:** Firestick - Legacy Code Analysis and Search Tool  
**Repository:** firestick (CaveAnimal/firestick)  
**Assignment:** Backend Infrastructure, Indexing, Search, Analysis

---

## Task Summary (DEV1)

**Total Tasks:** 0  
**Completed/Tested:** 0  
**In Progress:** 0  
**Blocked:** 0  
**Percent Complete:** 0%  
**Last Updated:** (not yet)

---

## Developer 1 Task Summary

**Role:** Backend Development Lead  
**Primary Focus:** Spring Boot backend, indexing engine, search functionality, analysis features  
**Total Estimated Tasks:** ~874 tasks (50% of 1,747 total tasks)  
**Dependencies:** Coordinate with Developer 2 for API contracts and integration points  

**Your Phases:**
- ✅ Phase 1: Foundation - Backend Infrastructure (Weeks 1-2)
- 🔧 Phase 2: Code Indexing Engine (Weeks 3-4)
- 🔧 Phase 3: Search Engine (Weeks 5-6)
- 🔧 Phase 4: Analysis Features (Weeks 7-8)
- 🔧 Phase 7: Optimization & Polish - Backend Performance (Week 12)

**Developer 2's Phases (for reference):**
- Phase 1: Foundation - Testing & Documentation (Weeks 1-2)
- Phase 5: Web UI (Weeks 9-10)
- Phase 6: Desktop Packaging (Weeks 10-11)
- Phase 7: Optimization & Polish - Frontend Performance (Week 12)

---

## Coordination Points

### Week 2 (Day 9-10): API Contract Definition
**With Developer 2:**
- Define REST API endpoints
- Agree on request/response schemas
- Document error codes
- Create OpenAPI/Swagger specification

### Week 8 (Day 38-40): Integration Checkpoint
**With Developer 2:**
- Verify all backend APIs are complete
- Test API responses match frontend expectations
- Review error handling
- Performance testing of backend services

### Week 12 (Day 68-69): Final Integration
**With Developer 2:**
- End-to-end testing
- Performance optimization
- Bug fixes
- Final QA

---

## Task Status Symbols
- `[ ]` Not Started
- `[-]` In Progress
- `[X]` Completed
- `[V]` Tested & Verified
- `[!]` Blocked
- `[>]` Deferred (include reason on next line)

---

## Phase 1: Foundation - Backend Infrastructure (Weeks 1-2)

**Your Responsibility:** Backend setup, services, Chroma integration, embeddings  
**Duration:** Oct 6-20, 2025 (2 weeks)  
**Parallel Work:** Developer 2 handles testing framework and documentation

---

### Week 1: Spring Boot Setup & Core Services

#### Day 1: Project Initialization ✅
**Goal:** Get Spring Boot application running

- `[V]` Create Spring Boot 3.5.6 project with Maven
- `[V]` Configure Java 21 in `pom.xml`
- `[V]` Add Spring Boot Web starter dependency
- `[V]` Create main application class `FirestickApplication.java`
- `[V]` Test: Run `mvn spring-boot:run` successfully
- `[V]` Verify application starts on port 8080

---

#### Day 2: Health Check & Basic Controller ✅
**Goal:** Create first REST endpoint to verify web layer

- `[V]` Create `controller` package under `com.codetalker.firestick`
- `[V]` Create `HealthController.java` with `/health` endpoint
- `[V]` Test endpoint returns "OK" status
- `[V]` Add basic logging to controller
- `[V]` Create test class `HealthControllerTest.java`
- `[V]` Test: Run tests with `mvn test`

---

#### Day 3: Add JavaParser Integration ✅
**Goal:** Set up code parsing capability

- `[V]` Add JavaParser dependencies to `pom.xml`
	- `[ ]` Sub-task: `javaparser-core` version 3.26.3
	- `[ ]` Sub-task: `javaparser-symbol-solver-core` version 3.26.3
- `[V]` Create `service` package under `com.codetalker.firestick`
- `[V]` Create `CodeParserService.java` class
- `[V]` Implement basic method to parse a Java file
	- `[ ]` Sub-task: Accept file path as parameter
	- `[ ]` Sub-task: Return CompilationUnit from JavaParser
	- `[ ]` Sub-task: Handle parsing errors with try-catch
- `[V]` Create `CodeParserServiceTest.java`
- `[V]` Test: Parse a sample Java file successfully

---

#### Day 4: Add Lucene Integration ✅
**Goal:** Set up full-text search capability

- `[V]` Add Apache Lucene dependencies to `pom.xml`
	- `lucene-core` version 9.12.0
	- `lucene-queryparser` version 9.12.0
	- `lucene-analyzers-common` version 9.12.0
- `[V]` Create `CodeSearchService.java` class
- `[V]` Implement basic index creation method
	- `[ ]` Sub-task: Create in-memory Lucene index
	- `[ ]` Sub-task: Add sample document to index
	- `[ ]` Sub-task: Handle IOException properly
- `[V]` Implement basic search method
	- `[ ]` Sub-task: Accept query string parameter
	- `[ ]` Sub-task: Return list of matching documents
- `[V]` Create `CodeSearchServiceTest.java`
- `[V]` Test: Index and search a sample document

---

#### Day 5: Add JGraphT Integration ✅
**Goal:** Set up graph library for dependency analysis

- `[V]` Add JGraphT dependencies to `pom.xml`
	- `jgrapht-core` version 1.5.2
- `[V]` Create `DependencyGraphService.java` class
- `[V]` Implement basic graph creation
	- `[ ]` Sub-task: Create DirectedGraph instance
	- `[ ]` Sub-task: Add vertices (nodes)
	- `[ ]` Sub-task: Add edges (connections)
- `[V]` Implement method to find dependencies
	- `[ ]` Sub-task: Get outgoing edges from a vertex
	- `[ ]` Sub-task: Return list of dependent classes
- `[V]` Create `DependencyGraphServiceTest.java`
- `[V]` Test: Create graph and find dependencies

---

#### Day 6: Add H2 Database ✅
**Goal:** Set up persistent storage

- `[V]` Add H2 database dependencies to `pom.xml`
	- `h2` database
	- `spring-boot-starter-data-jpa`
- `[V]` Configure H2 in `application.properties`
	- `[ ]` Sub-task: Set H2 console enabled = true
	- `[ ]` Sub-task: Set datasource URL to file-based H2
	- `[ ]` Sub-task: Set JPA DDL auto to `update`
	- `[ ]` Sub-task: Add logging for SQL statements (optional)
- `[V]` Test: Run application and access H2 console at `/h2-console`
- `[V]` Verify database file is created

**H2 Console Access:**
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/firestick
Username: sa
Password: (leave empty)
```

---

#### Day 7: Create Database Entities ✅
**Goal:** Define data model for code storage

- `[V]` Create `model` package under `com.codetalker.firestick`
- `[V]` Create `CodeFile` entity
	- `[ ]` Sub-task: Add fields: id, filePath, lastModified, hash
	- `[ ]` Sub-task: Add JPA annotations (@Entity, @Id, @GeneratedValue)
	- `[ ]` Sub-task: Add constructors, getters, setters
- `[V]` Create `CodeChunk` entity
	- `[ ]` Sub-task: Add fields: id, fileId, content, startLine, endLine, type
	- `[ ]` Sub-task: Add @ManyToOne relationship to CodeFile
	- `[ ]` Sub-task: Add JPA annotations
- `[V]` Create `Symbol` entity (class, method, field)
	- `[ ]` Sub-task: Add fields: id, name, type, signature, fileId, lineNumber
	- `[ ]` Sub-task: Add JPA annotations
- `[V]` Create repository interfaces
	- `[ ]` Sub-task: `CodeFileRepository extends JpaRepository`
	- `[ ]` Sub-task: `CodeChunkRepository extends JpaRepository`
	- `[ ]` Sub-task: `SymbolRepository extends JpaRepository`
- `[V]` Test: Run application and verify tables are created in H2

---

### Week 2: Chroma Integration & Embedding Pipeline

#### Day 8: Download & Set Up Embedding Model
**Goal:** Get local ONNX embedding model working

- `[V]` Research ONNX Runtime for Java
- `[V]` Add ONNX Runtime dependency to `pom.xml`
	- `onnxruntime` version 1.16.0
- `[V]` Download all-MiniLM-L6-v2 model in ONNX format
	- `[ ]` Sub-task: Find model on Hugging Face (search "all-MiniLM-L6-v2 onnx")
	- `[ ]` Sub-task: Download `model.onnx` file
	- `[ ]` Sub-task: Download `tokenizer.json` file
	- `[ ]` Sub-task: Save both files to `models/` directory
- `[V]` Create `EmbeddingService.java` class
	- `[ ]` Sub-task: Add method to load ONNX model
	- `[ ]` Sub-task: Add method to load tokenizer
	- `[ ]` Sub-task: Add method `getEmbedding(String text)` that returns float[]
	- `[ ]` Sub-task: Handle model loading errors
- `[V]` Create `EmbeddingServiceTest.java`
- `[V]` Test: Generate embedding for sample text
- `[V]` Verify embedding is 384-dimensional vector (for all-MiniLM-L6-v2)

**Model Information:**
```
Model: sentence-transformers/all-MiniLM-L6-v2
Format: ONNX
Embedding Dimension: 384
Use Case: Semantic search, text similarity
```

---

#### Day 9-10: Chroma Vector Database Integration
**Goal:** Connect to Chroma for vector storage and search

**⚠️ COORDINATION POINT:** Meet with Developer 2 to define API contracts

- `[ ]` Install Chroma locally
	- `[ ]` Sub-task: Open terminal/PowerShell
	- `[ ]` Sub-task: Navigate to Chroma directory
	- `[ ]` Sub-task: Run `chroma run --host localhost --port 8000`
	- `[ ]` Sub-task: Test with browser: `http://localhost:8000/api/v1/heartbeat`
- `[ ]` Create `ChromaService.java` to interact with Chroma HTTP API
	- `[ ]` Sub-task: Create `config` package
	- `[ ]` Sub-task: Create `RestTemplateConfig.java`
	- `[ ]` Sub-task: Create `@Bean` for RestTemplate
	- `[ ]` Sub-task: Add method `createCollection(String name)`
	- `[ ]` Sub-task: Add method `addEmbeddings(String collection, List<float[]> embeddings, List<String> documents)`
	- `[ ]` Sub-task: Add method `query(String collection, float[] queryEmbedding, int topK)`
	- `[ ]` Sub-task: Build HTTP requests manually using RestTemplate
	- `[ ]` Sub-task: Parse JSON responses
- `[ ]` Create `ChromaServiceTest.java`
- `[ ]` Test: Create collection, add embedding, query by similarity

**Chroma API Endpoints:**
```
POST   /api/v1/collections
POST   /api/v1/collections/{name}/add
POST   /api/v1/collections/{name}/query
GET    /api/v1/collections/{name}
DELETE /api/v1/collections/{name}
```

---

#### Day 10: End-to-End Pipeline Test
**Goal:** Test complete flow from code to embedding to search

- `[ ]` Create integration test class `E2EPipelineTest.java`
- `[ ]` Implement full pipeline test:
	- `[ ]` Sub-task: Parse a sample Java file with CodeParserService
	- `[ ]` Sub-task: Extract method text content
	- `[ ]` Sub-task: Generate embedding with EmbeddingService
	- `[ ]` Sub-task: Store in Chroma with ChromaService
	- `[ ]` Sub-task: Query Chroma with sample question
	- `[ ]` Sub-task: Verify results are returned
- `[ ]` Create test data files
	- `[ ]` Sub-task: Create `test-data/sample-code/` directory
	- `[ ]` Sub-task: Add 2-3 simple Java files for testing
- `[ ]` Document the pipeline
	- `[ ]` Sub-task: Create `PIPELINE.md` in `docs/` directory
	- `[ ]` Sub-task: Document each step with code examples
	- `[ ]` Sub-task: Add troubleshooting section
- `[ ]` Run test and verify all steps work together

**Pipeline Flow:**
```
Java File → JavaParser → Extract Methods → 
Generate Embeddings → Store in Chroma → 
Query by Similarity → Return Results
```

---

## Phase 2: Code Indexing Engine (Weeks 3-4)

**Your Responsibility:** File discovery, parsing, chunking, dependency graph building  
**Duration:** Oct 21 - Nov 3, 2025 (2 weeks)  
**Parallel Work:** Developer 2 works on test infrastructure and documentation

---

### Week 3: File Discovery & Parsing

#### Day 11: File Discovery Service
**Goal:** Scan project directories and find all Java files

- `[ ]` Create `FileDiscoveryService.java`
	- `[ ]` Sub-task: Create `FileDiscoveryService.java` in service package
	- `[ ]` Sub-task: Add method `scanDirectory(String rootPath)` that returns List<Path>
	- `[ ]` Sub-task: Implement recursive directory traversal using `Files.walk()`
	- `[ ]` Sub-task: Filter for `.java` files only
	- `[ ]` Sub-task: Add exclusion patterns (ignore `target/`, `build/`, `.git/`, `test/`)
	- `[ ]` Sub-task: Handle IOException and SecurityException
	- `[ ]` Sub-task: Add logging for progress (every 100 files found)

- `[ ]` Create configuration class for indexing
	- `[ ]` Sub-task: Create `IndexingConfig.java` in config package
	- `[ ]` Sub-task: Add properties for file extensions to include
	- `[ ]` Sub-task: Add properties for directories to exclude
	- `[ ]` Sub-task: Add method to check if file should be indexed
	- `[ ]` Sub-task: Make configuration externalized to `application.properties`

- `[ ]` Create tests
	- `[ ]` Sub-task: Create `FileDiscoveryServiceTest.java`
	- `[ ]` Sub-task: Create test directory structure with sample files
	- `[ ]` Sub-task: Test scanning finds all Java files
	- `[ ]` Sub-task: Test exclusion patterns work correctly
	- `[ ]` Sub-task: Test handling of empty directories
	- `[ ]` Sub-task: Test error handling for invalid paths

- `[ ]` Integration test
	- `[ ]` Sub-task: Test with actual project (scan firestick source code)
	- `[ ]` Sub-task: Verify all expected files are found
	- `[ ]` Sub-task: Verify excluded directories are skipped
	- `[ ]` Sub-task: Log results for manual verification

---

#### Day 12: Enhanced Code Parsing
**Goal:** Extract detailed information from Java files

- `[ ]` Enhance `CodeParserService` to extract method details
	- `[ ]` Sub-task: Update `CodeParserService` to extract method details
	- `[ ]` Sub-task: Extract method signature (name, parameters, return type)
	- `[ ]` Sub-task: Extract method body as string
	- `[ ]` Sub-task: Extract method modifiers (public, private, static, etc.)
	- `[ ]` Sub-task: Extract method annotations
	- `[ ]` Sub-task: Calculate method line range (start and end line)
	- `[ ]` Sub-task: Extract JavaDoc comment if present

- `[ ]` Add class extraction
	- `[ ]` Sub-task: Extract class name and fully qualified name
	- `[ ]` Sub-task: Extract class modifiers and annotations
	- `[ ]` Sub-task: Extract extends and implements clauses
	- `[ ]` Sub-task: Extract class-level JavaDoc
	- `[ ]` Sub-task: Extract inner classes recursively
	- `[ ]` Sub-task: Calculate class line range

- `[ ]` Add package and import extraction
	- `[ ]` Sub-task: Extract package declaration
	- `[ ]` Sub-task: Extract all import statements
	- `[ ]` Sub-task: Distinguish between static and regular imports
	- `[ ]` Sub-task: Store for dependency analysis

- `[ ]` Create data classes for parsed information
	- `[ ]` Sub-task: Create `MethodInfo.java` class to hold method details
	- `[ ]` Sub-task: Create `ClassInfo.java` class to hold class details
	- `[ ]` Sub-task: Create `FileInfo.java` class to hold file-level details
	- `[ ]` Sub-task: Add builder pattern or constructors
	- `[ ]` Sub-task: Add toString() for debugging

---

#### Day 13: Code Chunking Strategy
**Goal:** Define and implement code chunking for embeddings

- `[ ]` Research and design chunking strategy
	- `[ ]` Sub-task: Review research on code chunking best practices
	- `[ ]` Sub-task: Decide on chunk types:
		- Method-level chunks (each method = one chunk)
		- Class-level chunks (entire class = one chunk)
		- File-level chunks (for small files)
	- `[ ]` Sub-task: Document decisions in `docs/CHUNKING_STRATEGY.md`

- `[ ]` Create `CodeChunkingService.java`
	- `[ ]` Sub-task: Create `CodeChunkingService.java` in service package
	- `[ ]` Sub-task: Add method `chunkFile(FileInfo fileInfo)` returns List<CodeChunk>
	- `[ ]` Sub-task: Implement method-level chunking:
		- Extract each method as separate chunk
		- Include method signature
		- Include method body
		- Include class context (which class this method belongs to)
	- `[ ]` Sub-task: Implement class-level chunking:
		- For classes with no methods (interfaces, enums)
		- For small classes (< 50 lines)
		- Include all class content
	- `[ ]` Sub-task: Add metadata to each chunk (type, file, lines, signature)

- `[ ]` Enhance `CodeChunk` entity/model
	- `[ ]` Sub-task: Update existing `CodeChunk` entity or create new DTO
	- `[ ]` Sub-task: Add fields: id, content, metadata, chunkType, embedding
	- `[ ]` Sub-task: Add relationship to CodeFile entity
	- `[ ]` Sub-task: Add methods to format for display

- `[ ]` Implement context enrichment
	- `[ ]` Sub-task: For method chunks, add class context header
	- `[ ]` Sub-task: Format: `// Class: MyClass\n// Package: com.example\n<method code>`
	- `[ ]` Sub-task: Keep chunks under 512 tokens (approximately 2000 characters)
	- `[ ]` Sub-task: Split very large methods if needed
	- `[ ]` Sub-task: Add token counting utility method

---

#### Day 14: Dependency Graph Building - Phase 1
**Goal:** Build basic dependency graph structure

- `[ ]` Design graph schema
	- `[ ]` Sub-task: Review JGraphT documentation
	- `[ ]` Sub-task: Decide on graph node types (Class, Method, Package)
	- `[ ]` Sub-task: Decide on edge types (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
	- `[ ]` Sub-task: Document graph schema in `docs/GRAPH_SCHEMA.md`

- `[ ]` Implement basic graph construction
	- `[ ]` Sub-task: Update `DependencyGraphService.java`
	- `[ ]` Sub-task: Create method `buildFromParsedFiles(List<FileInfo> files)`
	- `[ ]` Sub-task: Add all classes as vertices
	- `[ ]` Sub-task: Add all methods as vertices
	- `[ ]` Sub-task: Create edges for inheritance (extends)
	- `[ ]` Sub-task: Create edges for interface implementation (implements)
	- `[ ]` Sub-task: Create edges for imports
	- `[ ]` Sub-task: Store graph metadata

- `[ ]` Create graph data structures
	- `[ ]` Sub-task: Create `GraphNode.java` interface or abstract class
	- `[ ]` Sub-task: Create `ClassNode.java` with class information
	- `[ ]` Sub-task: Create `MethodNode.java` with method information
	- `[ ]` Sub-task: Create `PackageNode.java` with package information
	- `[ ]` Sub-task: Add equals() and hashCode() for graph operations

- `[ ]` Create edge types
	- `[ ]` Sub-task: Create `DependencyEdge.java` class
	- `[ ]` Sub-task: Add edge types enum (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
	- `[ ]` Sub-task: Add weight or metadata if needed
	- `[ ]` Sub-task: Add toString() for debugging

---

### Week 4: Advanced Parsing & Graph Completion

#### Day 15: Dependency Graph Building - Phase 2
**Goal:** Add method call detection to dependency graph

- `[ ]` Implement method call detection
	- `[ ]` Sub-task: Research JavaParser method call visitor pattern
	- `[ ]` Sub-task: Create `MethodCallVisitor` class extends VoidVisitorAdapter
	- `[ ]` Sub-task: Override visit(MethodCallExpr) to detect method calls
	- `[ ]` Sub-task: Store caller → callee relationships
	- `[ ]` Sub-task: Handle method calls within same class
	- `[ ]` Sub-task: Handle method calls to other classes

- `[ ]` Enhance dependency graph with method calls
	- `[ ]` Sub-task: Update `DependencyGraphService` to include method calls
	- `[ ]` Sub-task: Add CALLS edges between methods
	- `[ ]` Sub-task: Handle static method calls
	- `[ ]` Sub-task: Handle instance method calls
	- `[ ]` Sub-task: Handle constructor calls

- `[ ]` Test method call detection
	- `[ ]` Sub-task: Create test with simple method calls
	- `[ ]` Sub-task: Test with chained method calls
	- `[ ]` Sub-task: Test with static imports
	- `[ ]` Sub-task: Verify all relationships are captured

---

(Continue with remaining days 16-18 following the same detailed pattern...)

---

## Phase 3: Search Engine (Weeks 5-6)

**Your Responsibility:** Lucene indexing, Chroma queries, semantic search, hybrid search  
**Duration:** Nov 4-17, 2025 (2 weeks)

(Include all search-related tasks from the original document...)

---

## Phase 4: Analysis Features (Weeks 7-8)

**Your Responsibility:** Complexity metrics, code smells, dead code detection, pattern analysis  
**Duration:** Nov 18 - Dec 1, 2025 (2 weeks)

(Include all analysis-related tasks from the original document...)

---

## Phase 7: Optimization & Polish - Backend (Week 12)

**Your Responsibility:** Backend performance, database optimization, API optimization  
**Duration:** Jan 5-11, 2026 (1 week)

### Day 63: Performance Profiling & Database Optimization
- `[ ]` Profile Backend Performance (2h)
- `[ ]` Optimize Database Queries (3h)
- `[ ]` Optimize Embedding Generation (2h)
- `[ ]` Optimize Chroma Vector Search (1h)

### Day 68: Backend Testing
- `[ ]` End-to-End Backend Testing (3h)
- `[ ]` API Performance Testing (2h)
- `[ ]` Security Review (1h)

---

## Daily Standup Template

**Today's Focus:**
- [ ] Task 1
- [ ] Task 2
- [ ] Task 3

**Coordination with Developer 2:**
- [ ] API contract discussions
- [ ] Integration points
- [ ] Blockers

**Blockers:**
- None / [List blockers]

**Notes:**
- [Any important notes or decisions]

---

**Last Updated:** October 14, 2025  
**Next Review:** [Date]  
**Questions:** Contact Developer 2 for frontend/UI questions
