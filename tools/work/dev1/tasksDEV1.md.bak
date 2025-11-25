## Task Summary (DEV1)

**Total Tasks:** 302 tasks (including main tasks and sub-tasks)  
**Completed/Tested:** 256 tasks  
**In Progress:** 1 tasks  
**Blocked:** 1 tasks  
**Percent Complete:** 84.77%  
**Last Updated:** November 6, 2025    2:31 PM Central Standard Time

## Recently Completed Follow-ups (DEV1)

- `[V]` Added MVC validation tests across controllers (Indexing, Jobs, Graph, Code Content, Progress) to assert 400 on invalid inputs; all tests green
- `[V]` Extended GlobalExceptionHandler to handle `MethodArgumentNotValidException` (body `@Valid`) as 400 with code `VALIDATION_ERROR` and field details
- `[V]` Updated OpenAPI (`docs/openapi/openapi.json`) with parameter constraints, added POST `/api/indexing/run` and `IndexingRequest` schema; aligned `docs/API.md` and `docs/ERRORS.md`
- `[V]` End-to-End pipeline test with mocked Chroma v2; added `PIPELINE.md` docs and sample `HelloWorld.java`
- `[V]` Broadened pipeline test data with `Calculator.java` and extended multi-document assertions
- `[V]` Implemented ONNX mode wiring in `EmbeddingService` (onnxruntime + WordPiece tokenizer); added guarded smoke test and updated docs
- `[V]` Centralized logging and GlobalExceptionHandler with domain exceptions; tests verifying handler behavior
- `[V]` Lucene modernization to StoredFields API (removed deprecated IndexSearcher.doc usage)
- `[V]` File discovery enhancements (exclude directories, glob patterns)
- `[V]` Externalized defaults via `IndexingConfig` (indexing.* properties)
- `[V]` Indexing orchestrator (`IndexingService`) and REST endpoints (`/api/indexing/run`)
- `[V]` Job tracking (`IndexingJob` entity/repo) and controller (`/api/indexing/jobs/...`)
- `[V]` Incremental indexing (skip unchanged files by lastModified)
- `[V]` Persistence of `CodeFile` and `CodeChunk` with transactional replacement of chunks
- `[V]` Exposed `jobId` in `IndexingReport` and API responses
- `[V]` Controller tests for job endpoints (latest/byId)
- `[V]` README updates covering incremental behavior and report fields
- `[V]` TheRules: policy to not analyze Surefire reports unless requested; treat `[INFO] BUILD SUCCESS` as sufficient
- `[V]` Enhanced parser and graph to handle static imports; normalized import labels (stripped "static ") and added import edges; tests updated and passing
- `[V]` Added minimal cross-class CALLS edges: qualified calls (OtherClass.method) and static-imported unqualified calls; covered by new unit test in `DependencyGraphServiceTest`
- `[V]` Added graph query helpers `getCallers`/`getCallees`; unit tests validate caller/callee sets
- `[V]` Implemented class cycle detection using simple cycles on class-only subgraph; added test for basic cycle
- `[V]` Captured call stats metadata (callsAttempted, callsResolved, callEdgesAdded) during graph build and stored in metadata
- `[V]` Instance and constructor call resolution: added heuristics for simple chained calls (qualifier ending with "()") and linking constructor invocations to constructor method nodes; added focused unit test
- `[V]` Extended MethodInfo to track calledConstructors; updated DependencyGraphService to resolve and add corresponding CALLS edges
- `[V]` Static import handling: resolve unqualified calls via multiple static import candidates; added wildcard support (`Class.*`) and tests
- `[V]` Observability: new graph build metrics `callsStaticWildcardResolved` and `callsQualifiedChainedResolved`; extended tests to assert presence and counts
- `[V]` Qualified call resolution for `this.method()` and `super.method()`; added superclass resolution helper and edges; unit test added and passing
- `[V]` Observability: added unresolved-call metrics buckets (`callsUnresolvedStaticNoClass`, `callsUnresolvedQualifiedNoClass`, `callsUnresolvedQualifiedNoMethod`, `callsUnresolvedCtorNoClass`, `callsUnresolvedCtorNoMethod`); validated by updated tests

- `[V]` Qualified call resolution metrics: added `callsQualifiedFqnResolved` (FQN-qualified calls) and `callsQualifiedUniqueSimpleResolved` (unique simple-name fallback)
- `[V]` Fallback heuristics: implemented unique simple-name fallback (`resolveByUniqueSimpleName`) to resolve qualified class names when exactly one known class has the qualifier’s simple name
- `[V]` Tests: new unit tests covering FQN-qualified resolution and unique simple-name fallback with metric assertions; full suite passing

## Node Types
- **ClassNode**: fullyQualifiedName, packageName, modifiers, superClassName, interfaces[]
- **MethodNode**: className, methodName, signature, returnType, parameters[], modifiers
- **PackageNode**: packageName, classCount

## Edge Types
- **EXTENDS**: source=subclass, target=superclass
- **IMPLEMENTS**: source=class, target=interface
- **CALLS**: source=caller method, target=callee method
- **IMPORTS**: source=class, target=imported class/package


## Graph Type
DirectedGraph<GraphNode, DependencyEdge> using JGraphT
Decision Tasks (Complete These First)
Task 0: Make Design Decisions

[X] Decide on graph node types: Review the schema above and confirm we will use ClassNode, MethodNode, and PackageNode as our three node types
[X] Decide on edge types: Review the schema above and confirm we will use EXTENDS, IMPLEMENTS, CALLS, and IMPORTS as our four edge types
[X] Decide on JGraphT graph type: Confirm we will use DirectedGraph<GraphNode, DependencyEdge> from JGraphT library
[X] Document decisions: Record these decisions in docs/GRAPH_SCHEMA.md (Task 1)

Instructions for Dev1:
"Review the proposed schema in Task 1. If you agree with the node types (ClassNode, MethodNode, PackageNode) and edge types (EXTENDS, IMPLEMENTS, CALLS, IMPORTS), check off these decision boxes and proceed to implementation. If you have concerns, document them before proceeding."

[X] Verify file is committed to version control

BLOCKING ISSUE #2: Node Classes Don't Exist
Task 2: Create Graph Node Classes

[X] Create interface src/main/java/com/analyzer/graph/GraphNode.java:

java  public interface GraphNode {
String getId();           // Unique identifier
String getType();         // "CLASS", "METHOD", "PACKAGE"
String getDisplayName();  // Human-readable name
}

[X] Create class src/main/java/com/analyzer/graph/ClassNode.java:

java  public class ClassNode implements GraphNode {
private String fullyQualifiedName;
private String packageName;
private String superClassName;
private List<String> interfaces;

	// Constructor with all fields
	// Implement getId(), getType(), getDisplayName()
	// Override equals() and hashCode() using fullyQualifiedName
}

[X] Create class src/main/java/com/analyzer/graph/MethodNode.java:

java  public class MethodNode implements GraphNode {
private String className;
private String methodName;
private String signature;
private String returnType;

	// Constructor with all fields
	// Implement getId(), getType(), getDisplayName()
	// Override equals() and hashCode() using className+signature
}

[X] Create class src/main/java/com/analyzer/graph/PackageNode.java:

java  public class PackageNode implements GraphNode {
private String packageName;

	// Constructor
	// Implement getId(), getType(), getDisplayName()
	// Override equals() and hashCode() using packageName
}

[X] Compile all node classes and verify no errors

BLOCKING ISSUE #3: Edge Class Doesn't Exist
Task 3: Create Edge Class and Enum

[X] Create enum src/main/java/com/analyzer/graph/EdgeType.java:

java  public enum EdgeType {
EXTENDS,
IMPLEMENTS,
CALLS,
IMPORTS
}

[X] Create class src/main/java/com/analyzer/graph/DependencyEdge.java:

java  public class DependencyEdge {
private EdgeType type;
private int weight;
private String metadata;  // Optional additional info

	// Constructor with type
	// Constructor with type and weight
	// Getters and setters
	// Override toString() for debugging
}

[X] Compile and verify no errors

BLOCKING ISSUE #4: Inheritance Edges Not Implemented
Task 4: Implement EXTENDS and IMPLEMENTS Edges

[X] In DependencyGraphService.java, add method:

java  private void addInheritanceEdges(List<FileInfo> files) {
for (FileInfo file : files) {
ClassNode classNode = findOrCreateClassNode(file.getClassName());

	    // Add EXTENDS edge
	    if (file.getSuperClass() != null) {
		  ClassNode superNode = findOrCreateClassNode(file.getSuperClass());
		  graph.addEdge(classNode, superNode, 
			new DependencyEdge(EdgeType.EXTENDS));
	    }
          
	    // Add IMPLEMENTS edges
	    for (String interfaceName : file.getInterfaces()) {
		  ClassNode interfaceNode = findOrCreateClassNode(interfaceName);
		  graph.addEdge(classNode, interfaceNode, 
			new DependencyEdge(EdgeType.IMPLEMENTS));
	    }
	}
}

[X] Call addInheritanceEdges(files) from buildFromParsedFiles() method
[X] Test with a sample class that extends another class
[X] Test with a sample class that implements an interface

BLOCKING ISSUE #5: Graph Metadata Not Stored
Task 5: Add Graph Metadata Storage

[X] In DependencyGraphService.java, add fields:

java  private Map<String, Object> graphMetadata = new HashMap<>();

[X] Add method to store metadata:

java  public void addMetadata(String key, Object value) {
graphMetadata.put(key, value);
}

[X] At end of buildFromParsedFiles(), store basic metadata:

java  addMetadata("nodeCount", graph.vertexSet().size());
addMetadata("edgeCount", graph.edgeSet().size());
addMetadata("buildTimestamp", System.currentTimeMillis());

[X] Add getter method:

java  public Map<String, Object> getMetadata() {
return new HashMap<>(graphMetadata);
}
Verification Tasks
Task 6: Integration and Testing


[X] Create test class DependencyGraphServiceTest.java
[X] Write test that creates a simple graph with 2 classes (one extends the other)
[X] Verify EXTENDS edge is created
[X] Verify IMPLEMENTS edge is created for interface
[X] Verify metadata is stored correctly
[X] Run all tests and ensure they pass

Task 7: Update Build Configuration


[X] In buildFromParsedFiles(), ensure this order:
	- Add all class nodes
	- Add all method nodes
	- Call addInheritanceEdges(files)
	- Add import edges (already done)
	- Store metadata

[X] Add logging statements for each phase

[X] Run full build and verify no exceptions


Summary of What to Do First
Priority Order:

✅ Complete Task 2 (Node Classes) - MOST CRITICAL
✅ Complete Task 3 (Edge Class) - SECOND MOST CRITICAL
✅ Complete Task 1 (Documentation) - Can be done in parallel
✅ Complete Task 4 (Inheritance Edges
✅ Complete Task 5 (Metadata)
✅ Complete Tasks 6-7 (Testing & Verification)

Key Message for Dev1
"Start with Task 2. Create all three node classes (ClassNode, MethodNode, PackageNode) with proper equals() and hashCode() methods. Then move to Task 3 to create the edge classes. Do not proceed to other tasks until these foundation classes are complete and compiling."RetryClaude does not have the ability to run the code it generates yet.
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
	- `[X]` Sub-task: `javaparser-core` version 3.26.3
	- `[X]` Sub-task: `javaparser-symbol-solver-core` version 3.26.3
- `[V]` Create `service` package under `com.codetalker.firestick`
- `[V]` Create `CodeParserService.java` class
- `[V]` Implement basic method to parse a Java file
	- `[X]` Sub-task: Accept file path as parameter
	- `[X]` Sub-task: Return CompilationUnit from JavaParser
	- `[X]` Sub-task: Handle parsing errors with try-catch
- `[V]` Create `CodeParserServiceTest.java`
- `[V]` Test: Parse a sample Java file successfully

---


#### Day 4: Add Lucene Integration ✅
**Goal:** Set up full-text search capability

- `[V]` Add Apache Lucene dependencies to `pom.xml`
	- `[X]` Sub-task: lucene-core version 9.12.0
	- `[X]` Sub-task: lucene-queryparser version 9.12.0
	- `[X]` Sub-task: lucene-analyzers-common version 9.12.0
- `[V]` Create `CodeSearchService.java` class
- `[V]` Implement basic index creation method
	- `[X]` Sub-task: Create in-memory Lucene index
	- `[X]` Sub-task: Add sample document to index
	- `[X]` Sub-task: Handle IOException properly
- `[V]` Implement basic search method
	- `[X]` Sub-task: Accept query string parameter
	- `[X]` Sub-task: Return list of matching documents
- `[V]` Create `CodeSearchServiceTest.java`
- `[V]` Test: Index and search a sample document

---

#### Day 5: Add JGraphT Integration ✅
**Goal:** Set up graph library for dependency analysis

- `[V]` Add JGraphT dependencies to `pom.xml`
	- `jgrapht-core` version 1.5.2
- `[V]` Create `DependencyGraphService.java` class
- `[V]` Implement basic graph creation
	- `[V]` Sub-task: Create DirectedGraph instance
	- `[V]` Sub-task: Add vertices (nodes)
	- `[V]` Sub-task: Add edges (connections)
- `[V]` Implement method to find dependencies
	- `[V]` Sub-task: Get outgoing edges from a vertex
	- `[V]` Sub-task: Return list of dependent classes
- `[V]` Create `DependencyGraphServiceTest.java`
- `[V]` Test: Create graph and find dependencies

---

#### Day 6: Add H2 Database ✅
**Goal:** Set up persistent storage

- `[V]` Add H2 database dependencies to `pom.xml`
	- `h2` database
	- `spring-boot-starter-data-jpa`
- `[V]` Configure H2 in `application.properties`
	- `[V]` Sub-task: Set H2 console enabled = true
	- `[V]` Sub-task: Set datasource URL to file-based H2
	- `[V]` Sub-task: Set JPA DDL auto to `update`
	- `[V]` Sub-task: Add logging for SQL statements (optional)
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
	- `[V]` Sub-task: Add fields: id, filePath, lastModified, hash
	- `[V]` Sub-task: Add JPA annotations (@Entity, @Id, @GeneratedValue)
	- `[V]` Sub-task: Add constructors, getters, setters
- `[V]` Create `CodeChunk` entity
	- `[V]` Sub-task: Add fields: id, fileId, content, startLine, endLine, type
	- `[V]` Sub-task: Add @ManyToOne relationship to CodeFile
	- `[V]` Sub-task: Add JPA annotations
- `[V]` Create `Symbol` entity (class, method, field)
	- `[V]` Sub-task: Add fields: id, name, type, signature, fileId, lineNumber
	- `[V]` Sub-task: Add JPA annotations
- `[V]` Create repository interfaces
	- `[V]` Sub-task: `CodeFileRepository extends JpaRepository`
	- `[V]` Sub-task: `CodeChunkRepository extends JpaRepository`
	- `[V]` Sub-task: `SymbolRepository extends JpaRepository`
- `[V]` Test: Run application and verify tables are created in H2

---

### Week 2: Chroma Integration & Embedding Pipeline

#### Day 8: Download & Set Up Embedding Model
**Goal:** Get local ONNX embedding model working

- `[V]` Research ONNX Runtime for Java
- `[V]` Add ONNX Runtime dependency to `pom.xml`
	- `onnxruntime` version 1.16.0
- `[V]` Download all-MiniLM-L6-v2 model in ONNX format
	- `[V]` Sub-task: Find model on Hugging Face (search "all-MiniLM-L6-v2 onnx") — Completed (repository includes `models/model_onnx/onnx/model.onnx`)
	- `[V]` Sub-task: Download `model.onnx` file — Present at `models/model_onnx/onnx/model.onnx`
	- `[V]` Sub-task: Download `tokenizer.json` file — Present at `models/model_onnx/tokenizer.json`
	- `[V]` Sub-task: Save both files to `models/` directory — Model + tokenizer artifacts committed under `models/model_onnx`
- `[V]` Create `EmbeddingService.java` class
	- `[V]` Sub-task: Add method to load ONNX model — Implemented via `ensureOnnx()` and `OnnxEmbedder` construction in `EmbeddingService`
	- `[>]` Sub-task: Add method to load tokenizer
	- `[V]` Sub-task: Add method `getEmbedding(String text)` that returns float[] (mock mode)
	- `[V]` Sub-task: Handle model loading errors (ONNX deferred)
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

- `[X]` Install Chroma locally
	- `[X]` Sub-task: Open terminal/PowerShell
	- `[X]` Sub-task: Navigate to Chroma directory
	- `[X]` Sub-task: Run `chroma run --host localhost --port 8000`
	- `[X]` Sub-task: Test with browser: `http://localhost:8000/api/v2/heartbeat` (v2 API, v1 deprecated)
- `[V]` Create `ChromaService.java` to interact with Chroma HTTP API
	- `[V]` Sub-task: Create `config` package
	- `[V]` Sub-task: Create `RestTemplateConfig.java`
	- `[X]` Sub-task: Create `@Bean` for RestTemplate
	- `[V]` Sub-task: Add method `createCollection(String name)`
	- `[V]` Sub-task: Add method `addEmbeddings(String collection, List<float[]> embeddings, List<String> documents)`
	- `[V]` Sub-task: Add method `query(String collection, float[] queryEmbedding, int topK)`
	- `[V]` Sub-task: Build HTTP requests manually using RestTemplate
	- `[V]` Sub-task: Parse JSON responses
`[V]` Create `ChromaServiceTest.java`
`[V]` Test: Create collection, add embedding, query by similarity

**Chroma API Endpoints (v2):**
```
POST   /api/v2/collections
POST   /api/v2/collections/{name}/add
POST   /api/v2/collections/{name}/query
GET    /api/v2/collections/{name}
DELETE /api/v2/collections/{name}
```

---

#### Day 10: End-to-End Pipeline Test
**Goal:** Test complete flow from code to embedding to search

- `[V]` Create integration test class `E2EPipelineTest.java`
- `[V]` Implement full pipeline test:
	- `[V]` Sub-task: Parse a sample Java file with CodeParserService
	- `[V]` Sub-task: Extract method text content
	- `[V]` Sub-task: Generate embedding with EmbeddingService
	- `[V]` Sub-task: Store in Chroma with ChromaService
	- `[V]` Sub-task: Query Chroma with sample question
	- `[V]` Sub-task: Verify results are returned
- `[V]` Create test data files
	- `[V]` Sub-task: Create `test-data/sample-code/` directory
	- `[V]` Sub-task: Add 2-3 simple Java files for testing (added `HelloWorld.java`)
- `[V]` Document the pipeline
	- `[V]` Sub-task: Create `PIPELINE.md` in `docs/` directory
	- `[V]` Sub-task: Document each step with code examples
	- `[V]` Sub-task: Add troubleshooting section
- `[V]` Run test and verify all steps work together

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

- `[X]` Create `FileDiscoveryService.java`
	- `[X]` Sub-task: Create `FileDiscoveryService.java` in service package
	- `[X]` Sub-task: Add method `scanDirectory(String rootPath)` that returns List<Path>
	- `[X]` Sub-task: Implement recursive directory traversal using `Files.walk()`
	- `[X]` Sub-task: Filter for `.java` files only
	- `[X]` Sub-task: Add exclusion patterns (ignore `target/`, `build/`, `.git/`, `test/`)
	- `[X]` Sub-task: Handle IOException and SecurityException
	- `[X]` Sub-task: Add logging for progress (every 100 files found)

- `[X]` Create configuration class for indexing
	- `[X]` Sub-task: Create `IndexingConfig.java` in config package
	- `[X]` Sub-task: Add properties for file extensions to include
	- `[X]` Sub-task: Add properties for directories to exclude
	- `[X]` Sub-task: Add method to check if file should be indexed
	- `[X]` Sub-task: Make configuration externalized to `application.properties`

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

### Optional

- `[V]` Broaden pipeline test data (Origin: Day 10 — End-to-End Pipeline Test)
	- Add 1–2 more sample Java files under `src/test/resources/test-data/sample-code/`
	- Extend E2E assertions to validate multiple documents returned and content variety

- `[V]` Implement ONNX mode wiring (Origin: Day 8 — EmbeddingService mock/ONNX)
	- Implement EmbeddingService ONNX mode using onnxruntime + tokenizer
	- Add a smoke test guarded by model presence (skips when files missing)
	- Update PIPELINE.md with ONNX setup instructions

#### Day 12: Enhanced Code Parsing
**Goal:** Extract detailed information from Java files

- `[X]` Enhance `CodeParserService` to extract method details
	- `[X]` Sub-task: Update `CodeParserService` to extract method details
	- `[X]` Sub-task: Extract method signature (name, parameters, return type)
	- `[X]` Sub-task: Extract method body as string
	- `[X]` Sub-task: Extract method modifiers (public, private, static, etc.)
	- `[X]` Sub-task: Extract method annotations
	- `[X]` Sub-task: Calculate method line range (start and end line)
	- `[X]` Sub-task: Extract JavaDoc comment if present

- `[X]` Add class extraction
	- `[X]` Sub-task: Extract class name and fully qualified name
	- `[X]` Sub-task: Extract class modifiers and annotations
	- `[X]` Sub-task: Extract extends and implements clauses
	- `[X]` Sub-task: Extract class-level JavaDoc
	- `[X]` Sub-task: Extract inner classes recursively
	- `[V]` Sub-task: Calculate class line range

- `[V]` Add package and import extraction
	- `[V]` Sub-task: Extract package declaration
	- `[V]` Sub-task: Extract all import statements
	- `[V]` Sub-task: Distinguish between static and regular imports
	- `[V]` Sub-task: Store for dependency analysis

- `[V]` Create data classes for parsed information
	- `[V]` Sub-task: Create `MethodInfo.java` class to hold method details
	- `[V]` Sub-task: Create `ClassInfo.java` class to hold class details
	- `[V]` Sub-task: Create `FileInfo.java` class to hold file-level details
	- `[V]` Sub-task: Add builder pattern or constructors
	- `[V]` Sub-task: Add toString() for debugging

---



#### Day 13: Code Chunking Strategy
**Goal:** Define and implement code chunking for embeddings
// ...existing code...

- `[V]` Research and design chunking strategy
	- `[V]` Sub-task: Review research on code chunking best practices
	- `[V]` Sub-task: Decide on chunk types:
		- Method-level chunks (each method = one chunk)
		- Class-level chunks (entire class = one chunk)
		- File-level chunks (for small files)
	- `[V]` Sub-task: Document decisions in `docs/CHUNKING_STRATEGY.md`

- `[V]` Create `CodeChunkingService.java`
	- `[V]` Sub-task: Create `CodeChunkingService.java` in service package
	- `[V]` Sub-task: Add method `chunkFile(FileInfo fileInfo)` returns List<CodeChunk>
	- `[V]` Sub-task: Implement method-level chunking:
		- Extract each method as separate chunk
		- Include method signature
		- Include method body
		- Include class context (which class this method belongs to)
	- `[V]` Sub-task: Implement class-level chunking:
		- For classes with no methods (interfaces, enums)
		- For small classes (< 50 lines)
		- Include all class content
	- `[V]` Sub-task: Add metadata to each chunk (type, file, lines, signature)

- `[V]` Enhance `CodeChunk` entity/model
	- `[V]` Sub-task: Update existing `CodeChunk` entity or create new DTO
	- `[V]` Sub-task: Add fields: id, content, metadata, chunkType, embedding
	- `[V]` Sub-task: Add relationship to CodeFile entity
	- `[V]` Sub-task: Add methods to format for display

- `[V]` Implement context enrichment
	- `[V]` Sub-task: For method chunks, add class context header
	- `[V]` Sub-task: Format: `// Class: MyClass\n// Package: com.example\n<method code>`
	- `[V]` Sub-task: Keep chunks under 512 tokens (approximately 2000 characters)
	- `[V]` Sub-task: Split very large methods if needed
	- `[V]` Sub-task: Add token counting utility method

---

#### Day 14: Dependency Graph Building - Phase 1
**Goal:** Build basic dependency graph structure

- `[V]` Design graph schema
	- `[V]` Sub-task: Review JGraphT documentation
	- `[V]` Sub-task: Decide on graph node types (Class, Method, Package)
	- `[V]` Sub-task: Decide on edge types (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
	- `[V]` Sub-task: Document graph schema in `docs/GRAPH_SCHEMA.md`

- `[V]` Implement basic graph construction
	- `[V]` Sub-task: Update `DependencyGraphService.java`
	- `[V]` Sub-task: Create method `buildFromParsedFiles(List<FileInfo> files)`
	- `[V]` Sub-task: Add all classes as vertices
	- `[V]` Sub-task: Add all methods as vertices
	- `[V]` Sub-task: Create edges for inheritance (extends)
	- `[V]` Sub-task: Create edges for interface implementation (implements)
	- `[V]` Sub-task: Create edges for imports
	- `[V]` Sub-task: Store graph metadata

- `[V]` Create graph data structures
	- `[V]` Sub-task: Create `GraphNode.java` interface or abstract class
	- `[V]` Sub-task: Create `ClassNode.java` with class information
	- `[V]` Sub-task: Create `MethodNode.java` with method information
	- `[V]` Sub-task: Create `PackageNode.java` with package information
	- `[V]` Sub-task: Add equals() and hashCode() for graph operations

- `[V]` Create edge types
	- `[V]` Sub-task: Create `DependencyEdge.java` class
	- `[V]` Sub-task: Add edge types enum (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
	- `[V]` Sub-task: Add weight or metadata if needed
	- `[V]` Sub-task: Add toString() for debugging

---

### Week 4: Advanced Parsing & Graph Completion

#### Day 15: Dependency Graph Building - Phase 2
**Goal:** Add method call detection to dependency graph


- `[V]` Implement method call detection
	- `[V]` Sub-task: Research JavaParser method call visitor pattern
	- `[V]` Sub-task: Create `MethodCallVisitor` class extends VoidVisitorAdapter
	- `[V]` Sub-task: Override visit(MethodCallExpr) to detect method calls
	- `[V]` Sub-task: Store caller → callee relationships
	- `[V]` Sub-task: Handle method calls within same class
	- `[V]` Sub-task: Handle method calls to other classes (qualified name + static import heuristic)

- `[V]` Enhance dependency graph with method calls
	- `[V]` Sub-task: Update `DependencyGraphService` to include method calls
	- `[V]` Sub-task: Add CALLS edges between methods
	- `[V]` Sub-task: Handle static method calls (via static import mapping)
	- `[ ]` Sub-task: Handle instance method calls
	- `[ ]` Sub-task: Handle constructor calls

- `[V]` Test method call detection
	- `[V]` Sub-task: Create test with simple method calls
	- `[ ]` Sub-task: Test with chained method calls
	- `[V]` Sub-task: Test with static imports
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

---

Environment sync (Python deps) — Oct 30, 2025

- Aligned to other machine per `requirements_status.txt` (while keeping resolver-compatible versions):
	- numpy: 2.3.4 -> 2.1.2
	- pydantic: 2.12.3 -> 2.9.2
	- pydantic_core: 2.41.4 -> 2.23.4
	- rpds-py: 0.27.1 -> 0.28.0
	- tokenizers: 0.22.1 -> 0.21.4 (to satisfy transformers<0.22)
	- typing_extensions: 4.15.0 -> 4.12.2
- Backup saved as `requirements.prev.txt`. Dependencies installed successfully.

---

## Optional

- `[V]` Chroma tenant/database-aware routes (Origin: Day 9 — Chroma Integration)
	- Add optional configuration keys `chroma.tenant` and `chroma.database`
	- When set, use namespaced v2 routes: `/api/v2/tenants/{tenant}/databases/{database}/collections/...`
	- Fallback to non-namespaced `/api/v2/collections/...` when unset

- `[V]` Test: Namespaced Chroma URLs (Origin: Day 9 — Chroma Integration)
	- Add unit test validating requests target `/api/v2/tenants/{tenant}/databases/{database}/collections/...`
	- Cover create, add, and query endpoints

- `[V]` Docs: Document Chroma namespacing (Origin: Day 9 — Chroma Integration)
	- README: example properties and explanation of when to use namespacing
	- application.properties: commented example keys

- `[ ]` ONNX batch embedding API (Origin: Day 8 — EmbeddingService ONNX)
	- Add `getEmbeddings(List<String>)` to process multiple texts in one session run
	- Keep memory footprint bounded and maintain output order

---

## Reconciliations

- `[ ]` Master: Mark Day 10 outcome "End-to-end pipeline test passes" as `[V]`
	- Evidence: `E2EPipelineTest.java` green; multi-document assertions; mocked Chroma v2

- `[ ]` Master: Mark "Show end-to-end pipeline test" as `[V]`
	- Evidence: `src/main/resources/docs/PIPELINE.md` documents pipeline with examples and troubleshooting

- `[ ]` Master: Mark "Ensure end-to-end pipeline test is working" as `[V]`
	- Evidence: Test suite runs green locally; guarded ONNX smoke; default MOCK mode

- `[ ]` Master: Mark H2 console verification as `[V]`
	- Evidence: `application.properties` contains `spring.h2.console.enabled=true` and `/h2-console` path

- `[ ]` Master: Fix package naming references
	- Current plan references `com.codetalkerl.firestick` and `com.caveanimal` in places
	- Target should be `com.codetalker.firestick` (matches codebase)
	- Update plan and README examples accordingly; then mark this resolved

- `[ ]` Master: Logging framework — add a quick log-level smoke test then mark "Test logging at different levels"
	- Create a lightweight test that toggles logger level for a class and verifies output via an in-memory appender

- `[ ]` Master: Error handling — review remaining services and elevate to domain exceptions where appropriate
	- After review/adjustments, mark "Add error handling to all service methods"

- `[ ]` Master: Lucene search result handling — reconcile sub-bullets
	- Confirm search returns top N (10) via TopDocs and mark related checklist as `[V]`
	- Note StoredFields API usage replacing deprecated `IndexSearcher#doc`

- `[ ]` Master: JGraphT advanced items — reconcile implemented features
	- Mark inheritance edges (EXTENDS/IMPLEMENTS) added in `DependencyGraphService` as `[V]`
	- Mark graph metadata storage (nodeCount, edgeCount, buildTimestamp) as `[V]`

- `[ ]` Master: Exception name alignment — adjust plan or code for parity
	- Plan lists `EmbeddingGenerationException`/`ChromaConnectionException`; code has `EmbeddingException`
	- Either rename plan items to match code or introduce thin wrappers; then mark as resolved

- `[ ]` Master: Day 11 tests reconciliation
	- Mark FileDiscovery tests as `[V]` (present: `FileDiscoveryServiceTest`, `FileDiscoveryServiceExclusionsTest`)

- `[ ]` Master: Data classes reconciliation
	- Mark `FileInfo`, `ClassInfo`, `MethodInfo` as implemented `[V]` and referenced by services/tests
