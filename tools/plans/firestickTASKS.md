# Firestick - Development Tasks Document

**Version:** 1.0  
**Date:** October 14, 2025  
**Project:** Firestick - Legacy Code Analysis and Search Tool  
**Repository:** firestick (CaveAnimal/firestick)

---

## Task Summary

**Total Tasks:** 1,747 tasks (including main tasks and sub-tasks)  
**Completed/Tested:** 32 tasks  
**In Progress:** 1 tasks  
**Blocked:** 1 tasks  
**Percent Complete:** 1.83%  
**Last Updated:** October 14, 2025    4:30 PM Central Standard Time

## Task Management System

### Task Status Symbols
- `[ ]` Not Started
- `[-]` In Progress
- `[X]` Completed
- `[V]` Tested & Verified
- `[!]` Blocked
- `[>]` Deferred (include reason on next line)

### How to Use This Document
1. **Update Status Daily**: Change task status symbols as you work
2. **Add Notes**: Write brief notes under tasks about challenges or decisions
3. **Track Time**: Estimate time in parentheses, e.g., `(2h)` means 2 hours
4. **Break Down Tasks**: If a task takes more than 4 hours, break it into sub-tasks
5. **Ask for Help**: Use `[!]` when blocked and document what's blocking you
6. **Test Everything**: Move from `[X]` to `[V]` only after testing

---

## Phase 1: Foundation (Weeks 1-2)

**Status:** ~95% Complete  
**Goal:** Establish solid Spring Boot foundation with Chroma integration, local embeddings, and basic test pipeline  
**Team:** Backend Developer(s)  
**Duration:** Oct 6-20, 2025 (2 weeks)

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

**Verification Steps:**
```powershell
mvn clean install
mvn spring-boot:run
# Open browser to http://localhost:8080
```

---

#### Day 2: Health Check & Basic Controller ✅
**Goal:** Create first REST endpoint to verify web layer

- `[V]` Create `controller` package under `com.codetalkerl.firestick`
- `[V]` Create `HealthController.java` with `/health` endpoint
- `[V]` Test endpoint returns "OK" status
- `[V]` Add basic logging to controller
- `[V]` Create test class `HealthControllerTest.java`
- `[V]` Test: Run tests with `mvn test`

**Example Test:**
```java
@SpringBootTest
class HealthControllerTest {
    @Test
    void healthEndpointReturnsOk() {
        // Test code here
    }
}
```

---

#### Day 3: Add JavaParser Integration ✅
**Goal:** Set up code parsing capability

- `[V]` Add JavaParser dependencies to `pom.xml`
  - `javaparser-core` version 3.26.3
  - `javaparser-symbol-solver-core` version 3.26.3
- `[V]` Create `service` package under `com.codetalkerl.firestick`
- `[V]` Create `CodeParserService.java` class
- `[V]` Implement basic method to parse a Java file
  - `[ ]` Sub-task: Accept file path as parameter
  - `[ ]` Sub-task: Return CompilationUnit from JavaParser
  - `[ ]` Sub-task: Handle parsing errors with try-catch
- `[V]` Create `CodeParserServiceTest.java`
- `[V]` Test: Parse a sample Java file successfully

**Example Service Method:**
```java
public CompilationUnit parseJavaFile(String filePath) {
    // Implementation
}
```

---

#### Day 4: Add Lucene for Full-Text Search ✅
**Goal:** Set up text search capability

- `[V]` Add Apache Lucene dependencies to `pom.xml`
  - `lucene-core` version 9.12.0
  - `lucene-queryparser` version 9.12.0
  - `lucene-analysis-common` version 9.12.0
- `[V]` Create `CodeSearchService.java` class
- `[V]` Implement basic indexing method
  - `[ ]` Sub-task: Create in-memory Lucene index
  - `[ ]` Sub-task: Add sample document to index
  - `[ ]` Sub-task: Handle IOException properly
- `[V]` Implement basic search method
  - `[ ]` Sub-task: Accept query string parameter
  - `[ ]` Sub-task: Return list of matching documents
- `[V]` Create `CodeSearchServiceTest.java`
- `[V]` Test: Index and search a sample document

---

#### Day 5: Add JGraphT for Dependency Graphs ✅
**Goal:** Set up graph analysis capability

- `[V]` Add JGraphT dependencies to `pom.xml`
  - `jgrapht-core` version 1.5.2
  - `jgrapht-io` version 1.5.2
- `[V]` Create `DependencyGraphService.java` class
- `[V]` Implement method to create simple graph
  - `[ ]` Sub-task: Create DirectedGraph instance
  - `[ ]` Sub-task: Add vertices (nodes)
  - `[ ]` Sub-task: Add edges (connections)
- `[V]` Implement method to find dependencies
  - `[ ]` Sub-task: Get outgoing edges from a vertex
  - `[ ]` Sub-task: Return list of dependent classes
- `[V]` Create `DependencyGraphServiceTest.java`
- `[V]` Test: Create graph and query dependencies

---

### Week 2: Database, Embeddings & Chroma Integration

#### Day 6: H2 Database Setup
**Goal:** Set up embedded database for metadata storage

- `[ ]` Add H2 database dependency to `pom.xml` (already exists)
- `[ ]` Add Spring Data JPA dependency (already exists)
- `[ ]` Create `application.properties` configuration
  - `[ ]` Sub-task: Set H2 console enabled = true
  - `[ ]` Sub-task: Set datasource URL to file-based H2
  - `[ ]` Sub-task: Set JPA DDL auto to `update`
  - `[ ]` Sub-task: Add logging for SQL statements (optional)
- `[ ]` Test: Start application and access H2 console at `/h2-console`
- `[ ]` Verify database file is created

**Configuration Example:**
```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:file:./data/firestick
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

**Verification:**
- Navigate to `http://localhost:8080/h2-console`
- Connect to database
- Verify connection successful

---

#### Day 7: Create Database Entities
**Goal:** Define data models for code metadata

- `[ ]` Create `entity` package under `com.codetalkerl.firestick`
- `[ ]` Create `CodeFile.java` entity (30 min)
  - `[ ]` Sub-task: Add fields: id, filePath, lastModified, hash
  - `[ ]` Sub-task: Add JPA annotations (@Entity, @Id, @GeneratedValue)
  - `[ ]` Sub-task: Add constructors, getters, setters
- `[ ]` Create `CodeChunk.java` entity (30 min)
  - `[ ]` Sub-task: Add fields: id, fileId, content, startLine, endLine, type
  - `[ ]` Sub-task: Add @ManyToOne relationship to CodeFile
  - `[ ]` Sub-task: Add JPA annotations
- `[ ]` Create `Symbol.java` entity (30 min)
  - `[ ]` Sub-task: Add fields: id, name, type, signature, fileId, lineNumber
  - `[ ]` Sub-task: Add JPA annotations
- `[ ]` Create repository interfaces (1h)
  - `[ ]` Sub-task: `CodeFileRepository extends JpaRepository`
  - `[ ]` Sub-task: `CodeChunkRepository extends JpaRepository`
  - `[ ]` Sub-task: `SymbolRepository extends JpaRepository`
- `[ ]` Test: Run application and verify tables are created in H2

**Entity Example:**
```java
@Entity
public class CodeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String filePath;
    
    private LocalDateTime lastModified;
    private String hash;
    
    // Constructors, getters, setters
}
```

---

#### Day 8: ONNX Runtime Setup
**Goal:** Set up local embedding model infrastructure

- `[ ]` Verify ONNX Runtime dependency in `pom.xml` (already exists)
- `[ ]` Create `models` directory in project root (5 min)
- `[ ]` Download all-MiniLM-L6-v2 ONNX model (30 min)
  - `[ ]` Sub-task: Find model on Hugging Face (search "all-MiniLM-L6-v2 onnx")
  - `[ ]` Sub-task: Download `model.onnx` file
  - `[ ]` Sub-task: Download `tokenizer.json` file
  - `[ ]` Sub-task: Save both files to `models/` directory
  - Note: Model size is approximately 90MB
- `[ ]` Create `EmbeddingService.java` class (2h)
  - `[ ]` Sub-task: Add method to load ONNX model
  - `[ ]` Sub-task: Add method to load tokenizer
  - `[ ]` Sub-task: Add method `getEmbedding(String text)` that returns float[]
  - `[ ]` Sub-task: Handle model loading errors
- `[ ]` Create simple test to generate one embedding (1h)
- `[ ]` Test: Generate embedding for "Hello World" successfully

**Download Instructions:**
1. Go to https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2
2. Look for ONNX version or convert using `optimum` library
3. Alternative: Use pre-converted models from https://github.com/xenova/transformers.js

**Basic Service Structure:**
```java
@Service
public class EmbeddingService {
    private OrtEnvironment env;
    private OrtSession session;
    
    @PostConstruct
    public void init() throws OrtException {
        // Load ONNX model
    }
    
    public float[] getEmbedding(String text) throws OrtException {
        // Generate embedding
    }
}
```

---

#### Day 9: Chroma Integration - REST API Approach
**Goal:** Connect to local Chroma vector database

**Background:** The Chroma Java client is not stable, so we'll use HTTP REST API instead.

- `[ ]` Verify Chroma is running locally (15 min)
  - `[ ]` Sub-task: Open terminal/PowerShell
  - `[ ]` Sub-task: Navigate to Chroma directory
  - `[ ]` Sub-task: Run `chroma run --host localhost --port 8000`
  - `[ ]` Sub-task: Test with browser: `http://localhost:8000/api/v1/heartbeat`
- `[ ]` Add RestTemplate configuration (30 min)
  - `[ ]` Sub-task: Create `config` package
  - `[ ]` Sub-task: Create `RestTemplateConfig.java`
  - `[ ]` Sub-task: Create `@Bean` for RestTemplate
- `[ ]` Create `ChromaService.java` class (3h)
  - `[ ]` Sub-task: Add method `createCollection(String name)`
  - `[ ]` Sub-task: Add method `addEmbeddings(String collection, List<float[]> embeddings, List<String> documents)`
  - `[ ]` Sub-task: Add method `query(String collection, float[] queryEmbedding, int topK)`
  - `[ ]` Sub-task: Build HTTP requests manually using RestTemplate
  - `[ ]` Sub-task: Parse JSON responses
- `[ ]` Create `ChromaServiceTest.java`
- `[ ]` Test: Create collection and add one document

**Chroma REST API Reference:**
```
POST http://localhost:8000/api/v1/collections
GET  http://localhost:8000/api/v1/collections/{name}
POST http://localhost:8000/api/v1/collections/{name}/add
POST http://localhost:8000/api/v1/collections/{name}/query
```

**Service Example:**
```java
@Service
public class ChromaService {
    private final RestTemplate restTemplate;
    private final String chromaUrl = "http://localhost:8000/api/v1";
    
    public void createCollection(String name) {
        // POST to /collections
    }
    
    public void addEmbeddings(String collection, List<float[]> embeddings, List<String> documents) {
        // POST to /collections/{name}/add
    }
}
```

---

#### Day 10: End-to-End Pipeline Test
**Goal:** Test complete flow from code to embeddings to Chroma

- `[ ]` Create integration test class `CodeIndexingPipelineTest.java` (4h)
  - `[ ]` Sub-task: Parse a sample Java file with CodeParserService
  - `[ ]` Sub-task: Extract method text content
  - `[ ]` Sub-task: Generate embedding with EmbeddingService
  - `[ ]` Sub-task: Store in Chroma with ChromaService
  - `[ ]` Sub-task: Query Chroma with sample question
  - `[ ]` Sub-task: Verify results are returned
- `[ ]` Create sample test data (30 min)
  - `[ ]` Sub-task: Create `test-data/sample-code/` directory
  - `[ ]` Sub-task: Add 2-3 simple Java files for testing
- `[ ]` Document the pipeline flow (1h)
  - `[ ]` Sub-task: Create `PIPELINE.md` in `docs/` directory
  - `[ ]` Sub-task: Document each step with code examples
  - `[ ]` Sub-task: Add troubleshooting section
- `[ ]` Fix any bugs discovered during testing

**Pipeline Flow:**
```
Java File → Parse → Extract Methods → Generate Embeddings → Store in Chroma → Query → Retrieve
```

**Example Test:**
```java
@SpringBootTest
class CodeIndexingPipelineTest {
    @Autowired CodeParserService parser;
    @Autowired EmbeddingService embeddings;
    @Autowired ChromaService chroma;
    
    @Test
    void fullPipelineTest() {
        // 1. Parse code
        // 2. Get text
        // 3. Generate embedding
        // 4. Store in Chroma
        // 5. Query
        // 6. Verify results
    }
}
```

---

### Phase 1 Cleanup & Documentation

#### Additional Tasks (Can be done anytime)

- `[ ]` Fix package naming inconsistency (1h)
  - Current: Mixed `com.caveanimal` and `com.codetalkerl`
  - Target: Standardize to `com.codetalkerl.firestick`
  - `[ ]` Sub-task: Refactor all imports
  - `[ ]` Sub-task: Update pom.xml groupId
  - `[ ]` Sub-task: Update test packages
  - `[ ]` Sub-task: Run all tests to verify nothing broke

- `[ ]` Add logging framework (1h)
  - `[ ]` Sub-task: Add SLF4J + Logback dependencies (may already be included)
  - `[ ]` Sub-task: Create `logback.xml` in `src/main/resources/`
  - `[ ]` Sub-task: Add logger to each service class
  - `[ ]` Sub-task: Add meaningful log statements (INFO, DEBUG, ERROR)
  - `[ ]` Sub-task: Test logging at different levels

- `[ ]` Implement error handling (2h)
  - `[ ]` Sub-task: Create custom exceptions in `exception` package
    - `CodeParsingException`
    - `EmbeddingGenerationException`
    - `ChromaConnectionException`
  - `[ ]` Sub-task: Create `@ControllerAdvice` class for global exception handling
  - `[ ]` Sub-task: Return proper HTTP status codes (400, 500, etc.)
  - `[ ]` Sub-task: Add error handling to all service methods

- `[ ]` Improve test coverage (3h)
  - `[ ]` Sub-task: Add more test cases to existing test classes
  - `[ ]` Sub-task: Test error conditions and edge cases
  - `[ ]` Sub-task: Add integration tests for database operations
  - `[ ]` Sub-task: Run `mvn test` and verify >70% coverage
  - `[ ]` Sub-task: Fix any failing tests

- `[ ]` Create README documentation (1h)
  - `[ ]` Sub-task: Add project description
  - `[ ]` Sub-task: Document prerequisites (Java 21, Maven, Chroma)
  - `[ ]` Sub-task: Add setup instructions
  - `[ ]` Sub-task: Document how to run the application
  - `[ ]` Sub-task: Add API endpoint documentation

---

## Phase 1 Success Criteria

Before moving to Phase 2, verify ALL of the following:

### Functional Requirements
- ✅ Spring Boot application starts without errors
- ✅ Health endpoint returns 200 OK
- ✅ Can parse Java files with JavaParser
- ✅ Can create Lucene index and search
- ✅ Can create dependency graphs with JGraphT
- ⬜ H2 database is configured and accessible
- ⬜ Database entities are created and persisted
- ⬜ ONNX model generates embeddings successfully
- ⬜ Chroma connection works and can store/retrieve vectors
- ⬜ End-to-end pipeline test passes

### Code Quality Requirements
- ⬜ All unit tests pass (`mvn test`)
- ⬜ Integration tests pass
- ⬜ No critical errors in logs
- ⬜ Code follows consistent naming conventions
- ⬜ All service classes have proper error handling
- ⬜ Logging is implemented in all services

### Documentation Requirements
- ⬜ README.md exists with setup instructions
- ⬜ Code is commented where necessary
- ⬜ API endpoints are documented
- ⬜ Pipeline flow is documented

---

## Common Issues & Troubleshooting

### Issue: Chroma won't start
**Solution:**
```powershell
# Make sure Python is installed
python --version

# Install Chroma if needed
pip install chromadb

# Run Chroma server
chroma run --host localhost --port 8000
```

### Issue: ONNX model not found
**Solution:**
- Check that `models/model.onnx` file exists
- Verify file path in EmbeddingService
- Ensure file is not corrupted (re-download if needed)

### Issue: H2 console won't open
**Solution:**
- Verify `spring.h2.console.enabled=true` in application.properties
- Try URL: `http://localhost:8080/h2-console`
- Check JDBC URL matches configuration

### Issue: Tests are failing
**Solution:**
- Run `mvn clean install` to rebuild
- Check for package naming issues
- Verify all dependencies are downloaded
- Look at test output for specific errors
- Run tests individually to isolate problem

### Issue: Port 8080 already in use
**Solution:**
```properties
# In application.properties, change port:
server.port=8081
```

### Issue: Maven dependency download fails
**Solution:**
```powershell
# Clear Maven cache and retry
mvn dependency:purge-local-repository
mvn clean install
```

---

## Daily Standup Template

Use this template for daily updates:

**Yesterday:**
- List tasks you worked on
- Note status changes

**Today:**
- List tasks you plan to work on
- Estimate hours

**Blockers:**
- List anything preventing progress
- Tag with `[!]` in task list

**Questions:**
- Technical questions for team
- Clarifications needed

---

## Sprint Review Checklist (End of Week 2)

- [ ] Demo application running
- [ ] Show code parsing functionality
- [ ] Show Lucene search working
- [ ] Show dependency graph creation
- [ ] Show database with stored entities
- [ ] Show embedding generation
- [ ] Show Chroma integration
- [ ] Show end-to-end pipeline test
- [ ] Present any challenges encountered
- [ ] Document lessons learned

---

## Notes Section

Use this space for general notes, decisions, or important information:

---

---
---

# Phase 2: Code Indexing Engine (Weeks 3-4)

**Status:** Not Started  
**Goal:** Build complete indexing pipeline to parse, chunk, and embed code into Chroma  
**Team:** Backend Developer(s)  
**Duration:** Oct 21 - Nov 10, 2025 (3 weeks, Sprint 1-2)  
**Dependencies:** Phase 1 must be complete and verified

---

## Phase 2 Overview

### What We're Building
A complete code indexing system that can:
1. **Discover** all Java files in a project directory
2. **Parse** each file to extract classes, methods, and documentation
3. **Chunk** code into meaningful pieces for embedding
4. **Generate** embeddings for each chunk
5. **Store** everything in Chroma and H2 database
6. **Update** incrementally when files change

### Why This Matters
This is the foundation of Firestick. Without good indexing, search won't work well. We need to:
- Parse code correctly to understand structure
- Chunk intelligently so related code stays together
- Build dependency graphs to understand relationships
- Index efficiently to handle 1M+ lines of code

---

## Week 3: Indexing Foundation (Sprint 1, Part 1)

### Day 11: Project Cleanup & Planning
**Goal:** Clean up technical debt from Phase 1 and prepare for Phase 2

- `[ ]` **Code Review & Testing** (2h)
  - `[ ]` Sub-task: Run all Phase 1 tests and verify they pass
  - `[ ]` Sub-task: Fix any failing tests
  - `[ ]` Sub-task: Review code quality and refactor if needed
  - `[ ]` Sub-task: Ensure end-to-end pipeline test is working

- `[ ]` **Fix Package Naming** (2h)
  - `[ ]` Sub-task: Decide on final package name (`com.codetalkerl.firestick`)
  - `[ ]` Sub-task: Refactor all Java files to new package
  - `[ ]` Sub-task: Update `pom.xml` groupId
  - `[ ]` Sub-task: Update all imports in test files
  - `[ ]` Sub-task: Run `mvn clean install` to verify

- `[ ]` **Add Logging Framework** (2h)
  - `[ ]` Sub-task: Create `logback.xml` in `src/main/resources/`
  - `[ ]` Sub-task: Configure log levels (INFO for production, DEBUG for development)
  - `[ ]` Sub-task: Configure log file output to `logs/firestick.log`
  - `[ ]` Sub-task: Add logger fields to all service classes
  - `[ ]` Sub-task: Add log statements at key points (method entry, errors, important decisions)

- `[ ]` **Implement Global Exception Handling** (2h)
  - `[ ]` Sub-task: Create `exception` package
  - `[ ]` Sub-task: Create custom exceptions:
    - `FileDiscoveryException`
    - `CodeParsingException`
    - `IndexingException`
    - `EmbeddingException`
  - `[ ]` Sub-task: Create `GlobalExceptionHandler` with `@ControllerAdvice`
  - `[ ]` Sub-task: Handle exceptions and return proper HTTP status codes
  - `[ ]` Sub-task: Test exception handling with intentional errors

**Logback Configuration Example:**
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/firestick.log</file>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

---

### Day 12: File Discovery Service
**Goal:** Create service to scan directories and find all Java files

- `[ ]` **Create FileDiscoveryService Class** (4h)
  - `[ ]` Sub-task: Create `FileDiscoveryService.java` in service package
  - `[ ]` Sub-task: Add method `scanDirectory(String rootPath)` that returns List<Path>
  - `[ ]` Sub-task: Implement recursive directory traversal using `Files.walk()`
  - `[ ]` Sub-task: Filter for `.java` files only
  - `[ ]` Sub-task: Add exclusion patterns (ignore `target/`, `build/`, `.git/`, `test/`)
  - `[ ]` Sub-task: Handle IOException and SecurityException
  - `[ ]` Sub-task: Add logging for progress (every 100 files found)

- `[ ]` **Add File Filtering Configuration** (1h)
  - `[ ]` Sub-task: Create `IndexingConfig.java` in config package
  - `[ ]` Sub-task: Add properties for file extensions to include
  - `[ ]` Sub-task: Add properties for directories to exclude
  - `[ ]` Sub-task: Add method to check if file should be indexed
  - `[ ]` Sub-task: Make configuration externalized to `application.properties`

- `[ ]` **Create Unit Tests** (2h)
  - `[ ]` Sub-task: Create `FileDiscoveryServiceTest.java`
  - `[ ]` Sub-task: Create test directory structure with sample files
  - `[ ]` Sub-task: Test scanning finds all Java files
  - `[ ]` Sub-task: Test exclusion patterns work correctly
  - `[ ]` Sub-task: Test handling of empty directories
  - `[ ]` Sub-task: Test error handling for invalid paths

- `[ ]` **Create Integration Test** (1h)
  - `[ ]` Sub-task: Test with actual project (scan firestick source code)
  - `[ ]` Sub-task: Verify all expected files are found
  - `[ ]` Sub-task: Verify excluded directories are skipped
  - `[ ]` Sub-task: Log results for manual verification

**Service Example:**
```java
@Service
@Slf4j
public class FileDiscoveryService {
    private final IndexingConfig config;
    
    public List<Path> scanDirectory(String rootPath) throws FileDiscoveryException {
        log.info("Starting directory scan: {}", rootPath);
        List<Path> javaFiles = new ArrayList<>();
        
        try {
            Files.walk(Paths.get(rootPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .filter(this::shouldInclude)
                .forEach(path -> {
                    javaFiles.add(path);
                    if (javaFiles.size() % 100 == 0) {
                        log.debug("Found {} files so far", javaFiles.size());
                    }
                });
        } catch (IOException e) {
            throw new FileDiscoveryException("Failed to scan directory", e);
        }
        
        log.info("Scan complete. Found {} Java files", javaFiles.size());
        return javaFiles;
    }
    
    private boolean shouldInclude(Path path) {
        // Check against exclusion patterns
    }
}
```

**Configuration Example:**
```properties
# Indexing Configuration
indexing.file.extensions=.java
indexing.exclude.directories=target,build,.git,node_modules
indexing.exclude.patterns=**/test/**,**/*Test.java
```

---

### Day 13: Enhanced Code Parsing
**Goal:** Improve CodeParserService to extract comprehensive information

- `[ ]` **Enhance Method Extraction** (3h)
  - `[ ]` Sub-task: Update `CodeParserService` to extract method details
  - `[ ]` Sub-task: Extract method signature (name, parameters, return type)
  - `[ ]` Sub-task: Extract method body as string
  - `[ ]` Sub-task: Extract method modifiers (public, private, static, etc.)
  - `[ ]` Sub-task: Extract method annotations
  - `[ ]` Sub-task: Calculate method line range (start and end line)
  - `[ ]` Sub-task: Extract JavaDoc comment if present

- `[ ]` **Enhance Class Extraction** (2h)
  - `[ ]` Sub-task: Extract class name and fully qualified name
  - `[ ]` Sub-task: Extract class modifiers and annotations
  - `[ ]` Sub-task: Extract extends and implements clauses
  - `[ ]` Sub-task: Extract class-level JavaDoc
  - `[ ]` Sub-task: Extract inner classes recursively
  - `[ ]` Sub-task: Calculate class line range

- `[ ]` **Extract Import and Package Information** (1h)
  - `[ ]` Sub-task: Extract package declaration
  - `[ ]` Sub-task: Extract all import statements
  - `[ ]` Sub-task: Distinguish between static and regular imports
  - `[ ]` Sub-task: Store for dependency analysis

- `[ ]` **Create Data Transfer Objects (DTOs)** (2h)
  - `[ ]` Sub-task: Create `MethodInfo.java` class to hold method details
  - `[ ]` Sub-task: Create `ClassInfo.java` class to hold class details
  - `[ ]` Sub-task: Create `FileInfo.java` class to hold file-level details
  - `[ ]` Sub-task: Add builder pattern or constructors
  - `[ ]` Sub-task: Add toString() for debugging

**MethodInfo Example:**
```java
public class MethodInfo {
    private String name;
    private String signature;
    private String returnType;
    private List<String> parameters;
    private String body;
    private String javadoc;
    private int startLine;
    private int endLine;
    private List<String> modifiers;
    private List<String> annotations;
    private String className;  // Parent class
    private String filePath;
    
    // Constructors, getters, setters, builder
}
```

- `[ ]` **Update Tests** (1h)
  - `[ ]` Sub-task: Update `CodeParserServiceTest` with new functionality
  - `[ ]` Sub-task: Test method extraction with various method types
  - `[ ]` Sub-task: Test class extraction with inheritance
  - `[ ]` Sub-task: Test extraction of comments and JavaDoc
  - `[ ]` Sub-task: Verify line numbers are correct

---

### Day 14: Code Chunking Strategy
**Goal:** Create service to split code into meaningful chunks for embedding

- `[ ]` **Design Chunking Strategy** (1h)
  - `[ ]` Sub-task: Review research on code chunking best practices
  - `[ ]` Sub-task: Decide on chunk types:
    - Method-level chunks (method + class context)
    - Class-level chunks (class summary + key methods)
    - File-level chunks (package + imports + overview)
  - `[ ]` Sub-task: Document decisions in `docs/CHUNKING_STRATEGY.md`

- `[ ]` **Create CodeChunkingService** (4h)
  - `[ ]` Sub-task: Create `CodeChunkingService.java` in service package
  - `[ ]` Sub-task: Add method `chunkFile(FileInfo fileInfo)` returns List<CodeChunk>
  - `[ ]` Sub-task: Implement method-level chunking:
    - Include method signature
    - Include method body
    - Include class name as context
    - Include JavaDoc if present
  - `[ ]` Sub-task: Implement class-level chunking:
    - Include class declaration
    - Include key method signatures (public methods)
    - Include class JavaDoc
  - `[ ]` Sub-task: Add metadata to each chunk (type, file, lines, signature)

- `[ ]` **Create CodeChunk Entity/DTO** (1h)
  - `[ ]` Sub-task: Update existing `CodeChunk` entity or create new DTO
  - `[ ]` Sub-task: Add fields: id, content, metadata, chunkType, embedding
  - `[ ]` Sub-task: Add relationship to CodeFile entity
  - `[ ]` Sub-task: Add methods to format for display

- `[ ]` **Implement Context Inclusion** (2h)
  - `[ ]` Sub-task: For method chunks, add class context header
  - `[ ]` Sub-task: Format: `// Class: MyClass\n// Package: com.example\n<method code>`
  - `[ ]` Sub-task: Keep chunks under 512 tokens (approximately 2000 characters)
  - `[ ]` Sub-task: Split very large methods if needed
  - `[ ]` Sub-task: Add token counting utility method

**Chunking Example:**
```java
@Service
@Slf4j
public class CodeChunkingService {
    
    public List<CodeChunkDTO> chunkFile(FileInfo fileInfo) {
        List<CodeChunkDTO> chunks = new ArrayList<>();
        
        // Create method-level chunks
        for (MethodInfo method : fileInfo.getMethods()) {
            CodeChunkDTO chunk = createMethodChunk(method, fileInfo);
            chunks.add(chunk);
        }
        
        // Create class-level chunk
        CodeChunkDTO classChunk = createClassChunk(fileInfo);
        chunks.add(classChunk);
        
        log.debug("Created {} chunks from {}", chunks.size(), fileInfo.getFilePath());
        return chunks;
    }
    
    private CodeChunkDTO createMethodChunk(MethodInfo method, FileInfo fileInfo) {
        StringBuilder content = new StringBuilder();
        content.append("// Class: ").append(method.getClassName()).append("\n");
        content.append("// Package: ").append(fileInfo.getPackageName()).append("\n");
        content.append("// File: ").append(fileInfo.getFileName()).append("\n\n");
        
        if (method.getJavadoc() != null) {
            content.append(method.getJavadoc()).append("\n");
        }
        
        content.append(method.getSignature()).append(" {\n");
        content.append(method.getBody()).append("\n}");
        
        return CodeChunkDTO.builder()
            .content(content.toString())
            .chunkType("METHOD")
            .filePath(fileInfo.getFilePath())
            .startLine(method.getStartLine())
            .endLine(method.getEndLine())
            .signature(method.getSignature())
            .build();
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Create `CodeChunkingServiceTest.java`
  - `[ ]` Sub-task: Test chunking simple class with one method
  - `[ ]` Sub-task: Test chunking class with multiple methods
  - `[ ]` Sub-task: Test chunking with nested classes
  - `[ ]` Sub-task: Verify chunk sizes are reasonable
  - `[ ]` Sub-task: Verify metadata is complete

---

### Day 15: Dependency Graph Building - Part 1
**Goal:** Build comprehensive dependency graph from parsed code

- `[ ]` **Plan Graph Structure** (1h)
  - `[ ]` Sub-task: Review JGraphT documentation
  - `[ ]` Sub-task: Decide on graph node types (Class, Method, Package)
  - `[ ]` Sub-task: Decide on edge types (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
  - `[ ]` Sub-task: Document graph schema in `docs/GRAPH_SCHEMA.md`

- `[ ]` **Enhance DependencyGraphService** (4h)
  - `[ ]` Sub-task: Update `DependencyGraphService.java`
  - `[ ]` Sub-task: Create method `buildFromParsedFiles(List<FileInfo> files)`
  - `[ ]` Sub-task: Add all classes as vertices
  - `[ ]` Sub-task: Add all methods as vertices
  - `[ ]` Sub-task: Create edges for inheritance (extends)
  - `[ ]` Sub-task: Create edges for interface implementation (implements)
  - `[ ]` Sub-task: Create edges for imports
  - `[ ]` Sub-task: Store graph metadata

- `[ ]` **Create Graph Node Classes** (2h)
  - `[ ]` Sub-task: Create `GraphNode.java` interface or abstract class
  - `[ ]` Sub-task: Create `ClassNode.java` with class information
  - `[ ]` Sub-task: Create `MethodNode.java` with method information
  - `[ ]` Sub-task: Create `PackageNode.java` with package information
  - `[ ]` Sub-task: Add equals() and hashCode() for graph operations

- `[ ]` **Create Graph Edge Classes** (1h)
  - `[ ]` Sub-task: Create `DependencyEdge.java` class
  - `[ ]` Sub-task: Add edge types enum (EXTENDS, IMPLEMENTS, CALLS, IMPORTS)
  - `[ ]` Sub-task: Add weight or metadata if needed
  - `[ ]` Sub-task: Add toString() for debugging

**Graph Example:**
```java
@Service
@Slf4j
public class DependencyGraphService {
    private DirectedGraph<GraphNode, DependencyEdge> dependencyGraph;
    
    public void buildFromParsedFiles(List<FileInfo> files) {
        dependencyGraph = new DefaultDirectedGraph<>(DependencyEdge.class);
        
        // First pass: Add all classes and methods as vertices
        for (FileInfo file : files) {
            for (ClassInfo classInfo : file.getClasses()) {
                ClassNode classNode = new ClassNode(classInfo);
                dependencyGraph.addVertex(classNode);
                
                for (MethodInfo method : classInfo.getMethods()) {
                    MethodNode methodNode = new MethodNode(method);
                    dependencyGraph.addVertex(methodNode);
                    // Add edge: class contains method
                    dependencyGraph.addEdge(classNode, methodNode, 
                        new DependencyEdge(EdgeType.CONTAINS));
                }
            }
        }
        
        // Second pass: Add dependency edges
        for (FileInfo file : files) {
            addInheritanceEdges(file);
            addImportEdges(file);
        }
        
        log.info("Built graph with {} vertices and {} edges", 
            dependencyGraph.vertexSet().size(), 
            dependencyGraph.edgeSet().size());
    }
}
```

---

## Week 4: Indexing Integration (Sprint 1, Part 2)

### Day 16: Dependency Graph Building - Part 2
**Goal:** Complete dependency graph with method call analysis

- `[ ]` **Implement Method Call Detection** (4h)
  - `[ ]` Sub-task: Research JavaParser method call visitor pattern
  - `[ ]` Sub-task: Create `MethodCallVisitor` class extends VoidVisitorAdapter
  - `[ ]` Sub-task: Override visit(MethodCallExpr) to detect method calls
  - `[ ]` Sub-task: Store caller → callee relationships
  - `[ ]` Sub-task: Handle method calls within same class
  - `[ ]` Sub-task: Handle method calls to other classes

- `[ ]` **Add Method Call Edges to Graph** (2h)
  - `[ ]` Sub-task: Update `DependencyGraphService` to include method calls
  - `[ ]` Sub-task: Create edges with CALLS type
  - `[ ]` Sub-task: Handle unresolved method calls gracefully
  - `[ ]` Sub-task: Add statistics logging (total calls, unique calls, etc.)

- `[ ]` **Implement Graph Query Methods** (2h)
  - `[ ]` Sub-task: Add method `getCallers(MethodNode method)` returns List<MethodNode>
  - `[ ]` Sub-task: Add method `getCallees(MethodNode method)` returns List<MethodNode>
  - `[ ]` Sub-task: Add method `getDependencies(ClassNode clazz)` returns List<ClassNode>
  - `[ ]` Sub-task: Add method `findCircularDependencies()` returns List<List<ClassNode>>
  - `[ ]` Sub-task: Add method `getCallChain(MethodNode from, MethodNode to)` returns Path

**Method Call Visitor Example:**
```java
public class MethodCallVisitor extends VoidVisitorAdapter<Map<String, List<String>>> {
    @Override
    public void visit(MethodCallExpr methodCall, Map<String, List<String>> collector) {
        super.visit(methodCall, collector);
        
        String methodName = methodCall.getNameAsString();
        String callerContext = getCurrentMethodContext(); // Track where we are
        
        collector.computeIfAbsent(callerContext, k -> new ArrayList<>())
                 .add(methodName);
        
        log.debug("Found method call: {} calls {}", callerContext, methodName);
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test graph building with sample classes
  - `[ ]` Sub-task: Test method call detection
  - `[ ]` Sub-task: Test query methods return correct results
  - `[ ]` Sub-task: Test circular dependency detection

---

### Day 17: Batch Embedding Generation
**Goal:** Optimize embedding generation for large batches

- `[ ]` **Enhance EmbeddingService for Batching** (3h)
  - `[ ]` Sub-task: Add method `generateBatchEmbeddings(List<String> texts)` returns List<float[]>
  - `[ ]` Sub-task: Process multiple texts in single ONNX inference call
  - `[ ]` Sub-task: Add batch size configuration (default 32)
  - `[ ]` Sub-task: Handle texts of different lengths in batch
  - `[ ]` Sub-task: Add padding/truncation logic for tokenization
  - `[ ]` Sub-task: Add progress logging for large batches

- `[ ]` **Implement Caching Mechanism** (2h)
  - `[ ]` Sub-task: Create `EmbeddingCache` class with LRU cache
  - `[ ]` Sub-task: Hash text content to use as cache key
  - `[ ]` Sub-task: Store embeddings in memory cache (limit to 10,000 entries)
  - `[ ]` Sub-task: Add cache hit/miss logging
  - `[ ]` Sub-task: Add method to clear cache when needed

- `[ ]` **Add Performance Monitoring** (1h)
  - `[ ]` Sub-task: Track time per embedding
  - `[ ]` Sub-task: Track batch processing time
  - `[ ]` Sub-task: Calculate embeddings per second
  - `[ ]` Sub-task: Log performance metrics
  - `[ ]` Sub-task: Add warning if performance is too slow

- `[ ]` **Optimize ONNX Session** (1h)
  - `[ ]` Sub-task: Configure ONNX session options for performance
  - `[ ]` Sub-task: Set thread pool size
  - `[ ]` Sub-task: Enable GPU acceleration if available
  - `[ ]` Sub-task: Test performance improvements

- `[ ]` **Create Performance Tests** (1h)
  - `[ ]` Sub-task: Test embedding generation for 1 text
  - `[ ]` Sub-task: Test batch of 10 texts
  - `[ ]` Sub-task: Test batch of 100 texts
  - `[ ]` Sub-task: Measure and log timing results
  - `[ ]` Sub-task: Verify embeddings are consistent

**Batch Processing Example:**
```java
@Service
@Slf4j
public class EmbeddingService {
    private final int BATCH_SIZE = 32;
    private final Map<String, float[]> cache = new ConcurrentHashMap<>();
    
    public List<float[]> generateBatchEmbeddings(List<String> texts) throws EmbeddingException {
        log.info("Generating embeddings for {} texts", texts.size());
        long startTime = System.currentTimeMillis();
        
        List<float[]> embeddings = new ArrayList<>();
        
        // Process in batches
        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);
            
            List<float[]> batchEmbeddings = processBatch(batch);
            embeddings.addAll(batchEmbeddings);
            
            if (i % 100 == 0) {
                log.debug("Processed {}/{} texts", i, texts.size());
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = texts.size() / (duration / 1000.0);
        log.info("Generated {} embeddings in {}ms ({} embeddings/sec)", 
            texts.size(), duration, String.format("%.2f", rate));
        
        return embeddings;
    }
    
    private List<float[]> processBatch(List<String> batch) throws EmbeddingException {
        // Check cache first
        List<float[]> results = new ArrayList<>();
        List<String> uncached = new ArrayList<>();
        
        for (String text : batch) {
            String hash = hashText(text);
            if (cache.containsKey(hash)) {
                results.add(cache.get(hash));
            } else {
                uncached.add(text);
            }
        }
        
        // Process uncached texts
        if (!uncached.isEmpty()) {
            List<float[]> newEmbeddings = generateWithONNX(uncached);
            for (int i = 0; i < uncached.size(); i++) {
                String hash = hashText(uncached.get(i));
                cache.put(hash, newEmbeddings.get(i));
                results.add(newEmbeddings.get(i));
            }
        }
        
        return results;
    }
}
```

---

### Day 18: Persist Graph to Database
**Goal:** Store dependency graph in H2 for persistence and querying

- `[ ]` **Design Database Schema for Graph** (1h)
  - `[ ]` Sub-task: Create table for graph nodes (id, type, name, metadata)
  - `[ ]` Sub-task: Create table for graph edges (from_id, to_id, edge_type, weight)
  - `[ ]` Sub-task: Add indexes for performance
  - `[ ]` Sub-task: Document schema in `docs/DATABASE_SCHEMA.md`

- `[ ]` **Create JPA Entities** (2h)
  - `[ ]` Sub-task: Create `GraphNodeEntity.java`
  - `[ ]` Sub-task: Create `GraphEdgeEntity.java`
  - `[ ]` Sub-task: Add proper relationships and annotations
  - `[ ]` Sub-task: Add cascade options
  - `[ ]` Sub-task: Create repositories

- `[ ]` **Implement Graph Persistence** (3h)
  - `[ ]` Sub-task: Add method `saveGraph()` to DependencyGraphService
  - `[ ]` Sub-task: Convert JGraphT nodes to entities
  - `[ ]` Sub-task: Convert JGraphT edges to entities
  - `[ ]` Sub-task: Batch save for performance
  - `[ ]` Sub-task: Add transaction management
  - `[ ]` Sub-task: Add error handling

- `[ ]` **Implement Graph Loading** (2h)
  - `[ ]` Sub-task: Add method `loadGraph()` to DependencyGraphService
  - `[ ]` Sub-task: Query all nodes and edges from database
  - `[ ]` Sub-task: Rebuild JGraphT graph from entities
  - `[ ]` Sub-task: Verify graph structure is correct
  - `[ ]` Sub-task: Add caching to avoid repeated loads

**Entity Example:**
```java
@Entity
@Table(name = "graph_nodes")
public class GraphNodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nodeType; // CLASS, METHOD, PACKAGE
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String fullyQualifiedName;
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string with additional data
    
    // Getters, setters
}

@Entity
@Table(name = "graph_edges")
public class GraphEdgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "from_node_id", nullable = false)
    private GraphNodeEntity fromNode;
    
    @ManyToOne
    @JoinColumn(name = "to_node_id", nullable = false)
    private GraphNodeEntity toNode;
    
    @Column(nullable = false)
    private String edgeType; // EXTENDS, IMPLEMENTS, CALLS, IMPORTS
    
    private Integer weight;
    
    // Getters, setters
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test saving simple graph
  - `[ ]` Sub-task: Test loading saved graph
  - `[ ]` Sub-task: Verify nodes and edges match
  - `[ ]` Sub-task: Test with large graph (1000+ nodes)

---

### Day 19: Complete Indexing Pipeline
**Goal:** Integrate all components into single indexing pipeline

- `[ ]` **Create IndexingOrchestrationService** (4h)
  - `[ ]` Sub-task: Create `IndexingOrchestrationService.java`
  - `[ ]` Sub-task: Inject all required services (FileDiscovery, Parser, Chunking, Embedding, Chroma)
  - `[ ]` Sub-task: Add method `indexDirectory(String path)` that orchestrates:
    1. Discover all Java files
    2. Parse each file
    3. Build dependency graph
    4. Chunk parsed code
    5. Generate embeddings for chunks
    6. Store in Chroma
    7. Save metadata to H2
  - `[ ]` Sub-task: Add progress reporting (console and log)
  - `[ ]` Sub-task: Add error recovery (continue on error, collect errors)
  - `[ ]` Sub-task: Add summary report at end

- `[ ]` **Implement Progress Tracking** (2h)
  - `[ ]` Sub-task: Create `IndexingProgress` class to track state
  - `[ ]` Sub-task: Track: files found, files parsed, chunks created, embeddings generated
  - `[ ]` Sub-task: Calculate percentage complete
  - `[ ]` Sub-task: Estimate time remaining
  - `[ ]` Sub-task: Log progress every 10%
  - `[ ]` Sub-task: Provide method to get current status

- `[ ]` **Add Error Handling and Recovery** (2h)
  - `[ ]` Sub-task: Wrap each file processing in try-catch
  - `[ ]` Sub-task: Log errors but continue processing other files
  - `[ ]` Sub-task: Collect all errors in list
  - `[ ]` Sub-task: Report errors in summary
  - `[ ]` Sub-task: Add retry logic for transient failures
  - `[ ]` Sub-task: Add circuit breaker for Chroma failures

**Pipeline Example:**
```java
@Service
@Slf4j
public class IndexingOrchestrationService {
    private final FileDiscoveryService fileDiscovery;
    private final CodeParserService parser;
    private final CodeChunkingService chunking;
    private final EmbeddingService embedding;
    private final ChromaService chroma;
    private final DependencyGraphService graphService;
    
    public IndexingResult indexDirectory(String rootPath) {
        log.info("Starting indexing of directory: {}", rootPath);
        IndexingProgress progress = new IndexingProgress();
        List<IndexingError> errors = new ArrayList<>();
        
        try {
            // Step 1: Discover files
            log.info("Step 1/6: Discovering Java files...");
            List<Path> files = fileDiscovery.scanDirectory(rootPath);
            progress.setTotalFiles(files.size());
            log.info("Found {} Java files", files.size());
            
            // Step 2: Parse all files
            log.info("Step 2/6: Parsing files...");
            List<FileInfo> parsedFiles = new ArrayList<>();
            for (Path file : files) {
                try {
                    FileInfo fileInfo = parser.parseFile(file);
                    parsedFiles.add(fileInfo);
                    progress.incrementFilesParsed();
                    
                    if (progress.getFilesParsed() % 50 == 0) {
                        log.info("Parsed {}/{} files ({}%)", 
                            progress.getFilesParsed(), 
                            progress.getTotalFiles(),
                            progress.getPercentComplete());
                    }
                } catch (Exception e) {
                    log.error("Failed to parse file: {}", file, e);
                    errors.add(new IndexingError(file.toString(), "PARSE_ERROR", e.getMessage()));
                }
            }
            
            // Step 3: Build dependency graph
            log.info("Step 3/6: Building dependency graph...");
            graphService.buildFromParsedFiles(parsedFiles);
            graphService.saveGraph();
            
            // Step 4: Chunk code
            log.info("Step 4/6: Chunking code...");
            List<CodeChunkDTO> allChunks = new ArrayList<>();
            for (FileInfo fileInfo : parsedFiles) {
                List<CodeChunkDTO> chunks = chunking.chunkFile(fileInfo);
                allChunks.addAll(chunks);
            }
            progress.setTotalChunks(allChunks.size());
            log.info("Created {} chunks", allChunks.size());
            
            // Step 5: Generate embeddings
            log.info("Step 5/6: Generating embeddings...");
            List<String> texts = allChunks.stream()
                .map(CodeChunkDTO::getContent)
                .collect(Collectors.toList());
            List<float[]> embeddings = embedding.generateBatchEmbeddings(texts);
            
            // Step 6: Store in Chroma
            log.info("Step 6/6: Storing in Chroma...");
            chroma.addEmbeddings("firestick_code", embeddings, texts, 
                allChunks.stream().map(this::createMetadata).collect(Collectors.toList()));
            
            log.info("Indexing complete!");
            return IndexingResult.success(progress, errors);
            
        } catch (Exception e) {
            log.error("Indexing failed", e);
            return IndexingResult.failure(progress, errors, e);
        }
    }
}
```

- `[ ]` **Create REST API Endpoint** (1h)
  - `[ ]` Sub-task: Create `IndexingController.java`
  - `[ ]` Sub-task: Add POST `/api/index` endpoint
  - `[ ]` Sub-task: Accept directory path as parameter
  - `[ ]` Sub-task: Return progress updates (consider WebSocket or SSE)
  - `[ ]` Sub-task: Add validation for directory path

---

### Day 20: Incremental Indexing
**Goal:** Support updating index when files change

- `[ ]` **Implement File Change Detection** (3h)
  - `[ ]` Sub-task: Add method to calculate file hash (MD5 or SHA-256)
  - `[ ]` Sub-task: Store file hash in CodeFile entity
  - `[ ]` Sub-task: Compare current hash with stored hash
  - `[ ]` Sub-task: Identify new files (not in database)
  - `[ ]` Sub-task: Identify modified files (hash changed)
  - `[ ]` Sub-task: Identify deleted files (in database but not on disk)
  - `[ ]` Sub-task: Return lists of changes

- `[ ]` **Implement Incremental Update Logic** (3h)
  - `[ ]` Sub-task: Add method `incrementalIndex(String rootPath)`
  - `[ ]` Sub-task: For new files: index normally
  - `[ ]` Sub-task: For modified files:
    - Delete old chunks from Chroma
    - Delete old entries from H2
    - Re-index file
  - `[ ]` Sub-task: For deleted files:
    - Remove from Chroma
    - Remove from H2
    - Update dependency graph
  - `[ ]` Sub-task: Rebuild affected parts of dependency graph only

- `[ ]` **Optimize Performance** (2h)
  - `[ ]` Sub-task: Process only changed files
  - `[ ]` Sub-task: Skip unchanged files entirely
  - `[ ]` Sub-task: Add parallel processing for independent files
  - `[ ]` Sub-task: Add benchmark comparing full vs incremental index
  - `[ ]` Sub-task: Log time savings

**Incremental Indexing Example:**
```java
public IncrementalIndexingResult incrementalIndex(String rootPath) {
    log.info("Starting incremental indexing...");
    
    // Detect changes
    FileChangeDetector detector = new FileChangeDetector();
    FileChanges changes = detector.detectChanges(rootPath);
    
    log.info("Changes detected: {} new, {} modified, {} deleted", 
        changes.getNewFiles().size(),
        changes.getModifiedFiles().size(),
        changes.getDeletedFiles().size());
    
    if (changes.hasNoChanges()) {
        log.info("No changes detected, skipping indexing");
        return IncrementalIndexingResult.noChanges();
    }
    
    // Process deletions first
    for (Path deleted : changes.getDeletedFiles()) {
        removeFromIndex(deleted);
    }
    
    // Process new and modified files
    List<Path> filesToIndex = new ArrayList<>();
    filesToIndex.addAll(changes.getNewFiles());
    filesToIndex.addAll(changes.getModifiedFiles());
    
    for (Path file : filesToIndex) {
        indexFile(file);
    }
    
    // Update dependency graph
    graphService.incrementalUpdate(filesToIndex);
    
    return IncrementalIndexingResult.success(changes);
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test detecting new files
  - `[ ]` Sub-task: Test detecting modified files
  - `[ ]` Sub-task: Test detecting deleted files
  - `[ ]` Sub-task: Test incremental indexing updates correctly
  - `[ ]` Sub-task: Verify old data is removed

---

## Week 5: Optimization & Testing (Sprint 2, Part 1)

### Day 21: Performance Testing & Optimization
**Goal:** Ensure indexing can handle large codebases efficiently

- `[ ]` **Create Performance Test Suite** (3h)
  - `[ ]` Sub-task: Create `PerformanceTest.java` class
  - `[ ]` Sub-task: Test indexing 100 files
  - `[ ]` Sub-task: Test indexing 1,000 files
  - `[ ]` Sub-task: Test indexing 10,000 files (simulated if needed)
  - `[ ]` Sub-task: Measure time, memory usage, throughput
  - `[ ]` Sub-task: Create performance report

- `[ ]` **Identify Bottlenecks** (2h)
  - `[ ]` Sub-task: Profile application with VisualVM or JProfiler
  - `[ ]` Sub-task: Identify slowest operations
  - `[ ]` Sub-task: Identify memory-intensive operations
  - `[ ]` Sub-task: Document findings

- `[ ]` **Optimize Critical Paths** (3h)
  - `[ ]` Sub-task: Add parallel processing where safe
  - `[ ]` Sub-task: Optimize database queries (add indexes)
  - `[ ]` Sub-task: Optimize embedding batch sizes
  - `[ ]` Sub-task: Reduce object allocations
  - `[ ]` Sub-task: Measure improvements
  - `[ ]` Sub-task: Re-run performance tests

**Performance Targets:**
- Parse 100 files/second
- Generate 50 embeddings/second
- Index 1,000 files in under 5 minutes
- Memory usage < 2GB for 10,000 files

---

### Day 22: Integration Testing
**Goal:** Test all components working together

- `[ ]` **Create Comprehensive Integration Tests** (4h)
  - `[ ]` Sub-task: Test full indexing pipeline end-to-end
  - `[ ]` Sub-task: Test with real Firestick source code
  - `[ ]` Sub-task: Test with sample legacy project
  - `[ ]` Sub-task: Verify all data stored correctly in H2
  - `[ ]` Sub-task: Verify all embeddings stored in Chroma
  - `[ ]` Sub-task: Verify dependency graph is complete

- `[ ]` **Test Error Scenarios** (2h)
  - `[ ]` Sub-task: Test with malformed Java files
  - `[ ]` Sub-task: Test with very large files
  - `[ ]` Sub-task: Test with Chroma unavailable
  - `[ ]` Sub-task: Test with database errors
  - `[ ]` Sub-task: Verify graceful degradation

- `[ ]` **Test Incremental Indexing** (2h)
  - `[ ]` Sub-task: Index project
  - `[ ]` Sub-task: Modify files
  - `[ ]` Sub-task: Run incremental index
  - `[ ]` Sub-task: Verify only changed files processed
  - `[ ]` Sub-task: Verify results are correct

---

### Day 23: Documentation & Code Review
**Goal:** Document work and prepare for next phase

- `[ ]` **Write Documentation** (3h)
  - `[ ]` Sub-task: Update README with indexing instructions
  - `[ ]` Sub-task: Document API endpoints
  - `[ ]` Sub-task: Create architecture diagram of indexing pipeline
  - `[ ]` Sub-task: Document configuration options
  - `[ ]` Sub-task: Write troubleshooting guide

- `[ ]` **Code Review & Refactoring** (3h)
  - `[ ]` Sub-task: Review all code for quality
  - `[ ]` Sub-task: Add missing JavaDoc comments
  - `[ ]` Sub-task: Refactor any messy code
  - `[ ]` Sub-task: Ensure consistent naming
  - `[ ]` Sub-task: Remove dead code and TODOs

- `[ ]` **Prepare Demo** (2h)
  - `[ ]` Sub-task: Create demo script
  - `[ ]` Sub-task: Prepare sample project for demo
  - `[ ]` Sub-task: Practice demo walkthrough
  - `[ ]` Sub-task: Create slides if needed

---

## Phase 2 Success Criteria

Before moving to Phase 3, verify ALL of the following:

### Functional Requirements
- ⬜ Can discover all Java files in a directory recursively
- ⬜ Can parse Java files and extract classes, methods, comments
- ⬜ Can chunk code into meaningful pieces
- ⬜ Can build complete dependency graph
- ⬜ Can generate embeddings for all chunks
- ⬜ Can store all data in Chroma and H2
- ⬜ Can perform full indexing of a project
- ⬜ Can perform incremental indexing
- ⬜ Can query indexed code (basic verification)

### Performance Requirements
- ⬜ Index 1,000 files in under 5 minutes
- ⬜ Parse 100+ files per second
- ⬜ Generate 50+ embeddings per second
- ⬜ Incremental index is 10x faster than full index for small changes
- ⬜ Memory usage reasonable (< 2GB for moderate projects)

### Code Quality Requirements
- ⬜ All unit tests pass
- ⬜ All integration tests pass
- ⬜ Test coverage > 70%
- ⬜ No critical bugs or errors
- ⬜ Code is well-documented
- ⬜ Logging is comprehensive

### Documentation Requirements
- ⬜ Architecture documented
- ⬜ API endpoints documented
- ⬜ Configuration options documented
- ⬜ Setup instructions are clear
- ⬜ Troubleshooting guide exists

---

## Common Issues & Troubleshooting

### Issue: Parsing very large files fails
**Solution:**
- Add memory configuration to JavaParser
- Consider splitting very large files
- Add timeout for parsing
- Skip problematic files and log warning

### Issue: Embedding generation is too slow
**Solution:**
- Increase batch size (try 64 or 128)
- Enable GPU acceleration in ONNX
- Cache embeddings for unchanged code
- Process embeddings in parallel threads

### Issue: Dependency graph is incomplete
**Solution:**
- Verify JavaParser symbol solver is configured
- Check that all import statements are parsed
- Review method call detection logic
- Add debug logging to see what's missing

### Issue: Incremental indexing not detecting changes
**Solution:**
- Verify file hash calculation is consistent
- Check file modification time comparison
- Ensure database is storing hashes correctly
- Add logging to change detection

### Issue: Chroma connection fails during indexing
**Solution:**
- Verify Chroma server is running
- Add connection retry logic
- Implement circuit breaker pattern
- Add health check before indexing
- Consider local queue for offline operation

### Issue: Out of memory during indexing
**Solution:**
- Increase JVM heap size: `-Xmx4g`
- Process files in smaller batches
- Clear caches periodically
- Use streaming where possible
- Profile to find memory leaks

### Issue: Graph build is slow
**Solution:**
- Build graph incrementally
- Use parallel streams for independent operations
- Add indexes to graph queries
- Cache graph queries
- Consider graph database for very large projects

---

## Phase 2 Sprint Review Checklist

**Demo Items:**
- [ ] Show file discovery scanning project
- [ ] Show parsed code with extracted methods
- [ ] Show code chunks with context
- [ ] Show dependency graph visualization (if available)
- [ ] Show embeddings generated
- [ ] Show data in H2 database
- [ ] Show data in Chroma collection
- [ ] Show full indexing pipeline running
- [ ] Show incremental indexing working
- [ ] Show performance metrics

**Retrospective Questions:**
- What went well in Phase 2?
- What challenges did we face?
- What would we do differently?
- What did we learn?
- What risks should we monitor in Phase 3?

---
---

# Phase 3: Search Engine (Weeks 5-6)

**Status:** Not Started  
**Goal:** Build hybrid search system combining semantic and keyword search with intelligent ranking  
**Team:** Backend Developer(s)  
**Duration:** Nov 11 - Nov 24, 2025 (2 weeks, Sprint 2-3)  
**Dependencies:** Phase 2 indexing pipeline must be complete

---

## Phase 3 Overview

### What We're Building
A powerful search engine that can:
1. **Understand** natural language queries about code
2. **Search** using both semantic similarity and exact keyword matching
3. **Rank** results by relevance and context
4. **Provide** code snippets with surrounding context
5. **Respond** in under 2 seconds for most queries

### Search Types We'll Support
- **Natural Language:** "payment processing logic"
- **Class/Method Names:** "PaymentService.processPayment"
- **Exact Code:** "String calculateTotal("
- **Concept Search:** "database connection pooling"
- **Dependency Queries:** "what calls this method?"

### Why This Matters
The search engine is what makes Firestick useful. Good search means:
- Developers find code faster
- Results are accurate and relevant
- Context helps understand code quickly
- No need to grep through thousands of files

---

## Week 5: Hybrid Search Implementation (Sprint 2, Part 2)

### Day 24: Symbol Table for Fast Lookups
**Goal:** Create fast exact-match lookup system for classes and methods

- `[ ]` **Design Symbol Table Structure** (1h)
  - `[ ]` Sub-task: Review what symbols we need to index (classes, methods, fields, packages)
  - `[ ]` Sub-task: Design in-memory data structure (HashMap with multiple indexes)
  - `[ ]` Sub-task: Plan for quick lookups by name, fully qualified name, signature
  - `[ ]` Sub-task: Document symbol table schema

- `[ ]` **Create SymbolTableService** (3h)
  - `[ ]` Sub-task: Create `SymbolTableService.java` in service package
  - `[ ]` Sub-task: Create `Symbol` class to represent indexed symbols
  - `[ ]` Sub-task: Add fields: name, type, fullyQualifiedName, signature, filePath, lineNumber
  - `[ ]` Sub-task: Create multiple indexes:
    - By simple name (e.g., "PaymentService")
    - By fully qualified name (e.g., "com.example.PaymentService")
    - By signature (e.g., "processPayment(Order, User)")
  - `[ ]` Sub-task: Add method `indexSymbol(Symbol symbol)`
  - `[ ]` Sub-task: Add method `findByName(String name)` returns List<Symbol>
  - `[ ]` Sub-task: Add method `findByFQN(String fqn)` returns Symbol
  - `[ ]` Sub-task: Add method `findBySignature(String signature)` returns List<Symbol>

- `[ ]` **Build Symbol Table from Database** (2h)
  - `[ ]` Sub-task: Query all symbols from H2 database
  - `[ ]` Sub-task: Load into memory on application startup
  - `[ ]` Sub-task: Add `@PostConstruct` method to build table
  - `[ ]` Sub-task: Add rebuild method for when index updates
  - `[ ]` Sub-task: Add statistics logging (total symbols, by type)

- `[ ]` **Implement Fuzzy Matching** (2h)
  - `[ ]` Sub-task: Add Levenshtein distance algorithm for typo tolerance
  - `[ ]` Sub-task: Add method `findSimilar(String name, int maxDistance)` 
  - `[ ]` Sub-task: Support partial matches (e.g., "PayServ" matches "PaymentService")
  - `[ ]` Sub-task: Rank fuzzy results by similarity score

**Symbol Table Example:**
```java
@Service
@Slf4j
public class SymbolTableService {
    // Multiple indexes for fast lookups
    private Map<String, List<Symbol>> byName = new ConcurrentHashMap<>();
    private Map<String, Symbol> byFQN = new ConcurrentHashMap<>();
    private Map<String, List<Symbol>> bySignature = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void buildSymbolTable() {
        log.info("Building symbol table...");
        long start = System.currentTimeMillis();
        
        List<SymbolEntity> symbols = symbolRepository.findAll();
        
        for (SymbolEntity entity : symbols) {
            Symbol symbol = toSymbol(entity);
            indexSymbol(symbol);
        }
        
        long duration = System.currentTimeMillis() - start;
        log.info("Symbol table built with {} symbols in {}ms", 
            byFQN.size(), duration);
    }
    
    public void indexSymbol(Symbol symbol) {
        // Index by simple name
        byName.computeIfAbsent(symbol.getName(), k -> new ArrayList<>())
              .add(symbol);
        
        // Index by fully qualified name
        byFQN.put(symbol.getFullyQualifiedName(), symbol);
        
        // Index by signature
        if (symbol.getSignature() != null) {
            bySignature.computeIfAbsent(symbol.getSignature(), k -> new ArrayList<>())
                      .add(symbol);
        }
    }
    
    public List<Symbol> findByName(String name) {
        List<Symbol> exact = byName.get(name);
        if (exact != null && !exact.isEmpty()) {
            return exact;
        }
        
        // Try fuzzy matching
        return findSimilar(name, 2); // Allow 2 character differences
    }
    
    public List<Symbol> findSimilar(String query, int maxDistance) {
        return byName.entrySet().stream()
            .filter(e -> levenshteinDistance(e.getKey(), query) <= maxDistance)
            .flatMap(e -> e.getValue().stream())
            .sorted(Comparator.comparingInt(s -> 
                levenshteinDistance(s.getName(), query)))
            .collect(Collectors.toList());
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test indexing symbols
  - `[ ]` Sub-task: Test exact name lookup
  - `[ ]` Sub-task: Test fully qualified name lookup
  - `[ ]` Sub-task: Test fuzzy matching
  - `[ ]` Sub-task: Test performance with 10,000+ symbols

---

### Day 25: Semantic Search with Chroma
**Goal:** Implement vector-based semantic search

- `[ ]` **Create SemanticSearchService** (3h)
  - `[ ]` Sub-task: Create `SemanticSearchService.java` in service package
  - `[ ]` Sub-task: Inject ChromaService and EmbeddingService
  - `[ ]` Sub-task: Add method `search(String query, int topK)` returns List<SearchResult>
  - `[ ]` Sub-task: Generate embedding for query text
  - `[ ]` Sub-task: Query Chroma with query embedding
  - `[ ]` Sub-task: Parse Chroma results into SearchResult objects
  - `[ ]` Sub-task: Add error handling for Chroma failures

- `[ ]` **Create SearchResult DTO** (1h)
  - `[ ]` Sub-task: Create `SearchResult.java` class
  - `[ ]` Sub-task: Add fields: id, content, score, metadata, filePath, lineNumbers
  - `[ ]` Sub-task: Add method to extract code snippet
  - `[ ]` Sub-task: Add method to format for display
  - `[ ]` Sub-task: Add Comparable interface for sorting by score

- `[ ]` **Implement Query Preprocessing** (2h)
  - `[ ]` Sub-task: Add method to clean and normalize queries
  - `[ ]` Sub-task: Remove special characters if needed
  - `[ ]` Sub-task: Expand common abbreviations (e.g., "DB" → "database")
  - `[ ]` Sub-task: Handle code snippets in queries
  - `[ ]` Sub-task: Add logging for query transformations

- `[ ]` **Add Metadata Filtering** (2h)
  - `[ ]` Sub-task: Support filtering by file path pattern
  - `[ ]` Sub-task: Support filtering by code type (class, method, etc.)
  - `[ ]` Sub-task: Support filtering by package
  - `[ ]` Sub-task: Add filter parameters to search method
  - `[ ]` Sub-task: Apply filters to Chroma query

**Semantic Search Example:**
```java
@Service
@Slf4j
public class SemanticSearchService {
    private final ChromaService chroma;
    private final EmbeddingService embedding;
    
    public List<SearchResult> search(String query, int topK) throws SearchException {
        log.info("Semantic search: '{}' (top {})", query, topK);
        
        try {
            // Preprocess query
            String processedQuery = preprocessQuery(query);
            
            // Generate embedding for query
            float[] queryEmbedding = embedding.getEmbedding(processedQuery);
            
            // Search in Chroma
            ChromaQueryResult chromaResult = chroma.query(
                "firestick_code", 
                queryEmbedding, 
                topK
            );
            
            // Convert to SearchResult objects
            List<SearchResult> results = parseChromaResults(chromaResult);
            
            log.info("Found {} results", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Semantic search failed", e);
            throw new SearchException("Failed to execute semantic search", e);
        }
    }
    
    private String preprocessQuery(String query) {
        // Clean and normalize query
        String processed = query.trim().toLowerCase();
        
        // Expand abbreviations
        processed = processed.replace(" db ", " database ");
        processed = processed.replace(" auth ", " authentication ");
        
        log.debug("Query preprocessed: '{}' -> '{}'", query, processed);
        return processed;
    }
    
    public List<SearchResult> searchWithFilters(String query, int topK, 
                                                SearchFilter filter) {
        List<SearchResult> results = search(query, topK * 2); // Get more for filtering
        
        // Apply filters
        return results.stream()
            .filter(r -> filter.matches(r))
            .limit(topK)
            .collect(Collectors.toList());
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test basic semantic search
  - `[ ]` Sub-task: Test with different query types
  - `[ ]` Sub-task: Test query preprocessing
  - `[ ]` Sub-task: Test filtering
  - `[ ]` Sub-task: Verify results are ranked by relevance

---

### Day 26: Keyword Search with Lucene
**Goal:** Implement fast exact-match and BM25 keyword search

- `[ ]` **Enhance CodeSearchService** (3h)
  - `[ ]` Sub-task: Update existing `CodeSearchService` with new methods
  - `[ ]` Sub-task: Add method `keywordSearch(String query, int topK)` returns List<SearchResult>
  - `[ ]` Sub-task: Use QueryParser to support complex queries
  - `[ ]` Sub-task: Support Boolean operators (AND, OR, NOT)
  - `[ ]` Sub-task: Support field-specific search (e.g., "className:Payment")
  - `[ ]` Sub-task: Support wildcard and fuzzy queries
  - `[ ]` Sub-task: Return results with BM25 scores

- `[ ]` **Index Additional Fields** (2h)
  - `[ ]` Sub-task: Add method to index code chunks with multiple fields
  - `[ ]` Sub-task: Index fields: content, className, methodName, packageName, fileName
  - `[ ]` Sub-task: Set appropriate analyzers for each field
  - `[ ]` Sub-task: Store full chunk data for retrieval
  - `[ ]` Sub-task: Re-index sample data with new schema

- `[ ]` **Implement Query Parsing** (2h)
  - `[ ]` Sub-task: Create `QueryParser` for user-friendly queries
  - `[ ]` Sub-task: Support exact phrases with quotes ("payment processing")
  - `[ ]` Sub-task: Support prefix matching (pay*)
  - `[ ]` Sub-task: Support proximity search ("payment processing"~5)
  - `[ ]` Sub-task: Handle special characters properly
  - `[ ]` Sub-task: Add error handling for malformed queries

- `[ ]` **Add Highlighting** (1h)
  - `[ ]` Sub-task: Use Lucene Highlighter to show matching terms
  - `[ ]` Sub-task: Highlight matches in code snippets
  - `[ ]` Sub-task: Add HTML tags for highlighting (or markers)
  - `[ ]` Sub-task: Limit highlighted fragment size
  - `[ ]` Sub-task: Return highlighted snippets in SearchResult

**Keyword Search Example:**
```java
@Service
@Slf4j
public class KeywordSearchService {
    private Directory indexDirectory;
    private IndexWriter indexWriter;
    private IndexSearcher indexSearcher;
    
    public List<SearchResult> keywordSearch(String query, int topK) 
            throws SearchException {
        log.info("Keyword search: '{}'", query);
        
        try {
            // Parse query
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query luceneQuery = parser.parse(query);
            
            // Search
            TopDocs topDocs = indexSearcher.search(luceneQuery, topK);
            
            // Convert to SearchResult
            List<SearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                
                SearchResult result = SearchResult.builder()
                    .content(doc.get("content"))
                    .score(scoreDoc.score)
                    .filePath(doc.get("filePath"))
                    .className(doc.get("className"))
                    .methodName(doc.get("methodName"))
                    .startLine(Integer.parseInt(doc.get("startLine")))
                    .endLine(Integer.parseInt(doc.get("endLine")))
                    .build();
                
                // Add highlighting
                String highlighted = highlightMatches(luceneQuery, result.getContent());
                result.setHighlightedContent(highlighted);
                
                results.add(result);
            }
            
            log.info("Found {} keyword matches", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Keyword search failed", e);
            throw new SearchException("Failed to execute keyword search", e);
        }
    }
    
    private String highlightMatches(Query query, String content) throws Exception {
        Highlighter highlighter = new Highlighter(
            new SimpleHTMLFormatter("<mark>", "</mark>"),
            new QueryScorer(query)
        );
        
        TokenStream tokenStream = new StandardAnalyzer()
            .tokenStream("content", new StringReader(content));
        
        String highlighted = highlighter.getBestFragment(tokenStream, content);
        return highlighted != null ? highlighted : content.substring(0, 
            Math.min(200, content.length()));
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test simple keyword search
  - `[ ]` Sub-task: Test Boolean queries
  - `[ ]` Sub-task: Test field-specific search
  - `[ ]` Sub-task: Test wildcard and fuzzy search
  - `[ ]` Sub-task: Test highlighting

---

### Day 27: Hybrid Search Implementation
**Goal:** Combine semantic and keyword search with intelligent ranking

- `[ ]` **Create HybridSearchService** (4h)
  - `[ ]` Sub-task: Create `HybridSearchService.java` in service package
  - `[ ]` Sub-task: Inject SemanticSearchService and KeywordSearchService
  - `[ ]` Sub-task: Add method `search(String query, int topK)` that:
    - Executes both semantic and keyword search in parallel
    - Merges results from both sources
    - Re-ranks combined results
    - Returns top K overall
  - `[ ]` Sub-task: Add configurable weights for semantic vs keyword scores
  - `[ ]` Sub-task: Handle duplicate results (same code chunk from both searches)
  - `[ ]` Sub-task: Add timing metrics for each search type

- `[ ]` **Implement Result Merging** (2h)
  - `[ ]` Sub-task: Create method to merge two result lists
  - `[ ]` Sub-task: Detect duplicate results by chunk ID
  - `[ ]` Sub-task: Combine scores for duplicates (weighted average)
  - `[ ]` Sub-task: Preserve best score and metadata
  - `[ ]` Sub-task: Add diversity to prevent too many results from same file

- `[ ]` **Implement Re-Ranking Algorithm** (2h)
  - `[ ]` Sub-task: Create `ResultReRanker` class
  - `[ ]` Sub-task: Implement scoring function:
    - Base score from search (semantic or keyword)
    - Boost for exact name matches (+20%)
    - Boost for public methods/classes (+10%)
    - Boost for recent files (+5%)
    - Penalty for test files (-10%)
  - `[ ]` Sub-task: Add method to calculate final score
  - `[ ]` Sub-task: Sort results by final score
  - `[ ]` Sub-task: Add logging for score adjustments

**Hybrid Search Example:**
```java
@Service
@Slf4j
public class HybridSearchService {
    private final SemanticSearchService semanticSearch;
    private final KeywordSearchService keywordSearch;
    private final ResultReRanker reRanker;
    
    @Value("${search.semantic.weight:0.6}")
    private double semanticWeight;
    
    @Value("${search.keyword.weight:0.4}")
    private double keywordWeight;
    
    public List<SearchResult> search(String query, int topK) throws SearchException {
        log.info("Hybrid search: '{}' (top {})", query, topK);
        long startTime = System.currentTimeMillis();
        
        // Execute both searches in parallel
        CompletableFuture<List<SearchResult>> semanticFuture = 
            CompletableFuture.supplyAsync(() -> {
                try {
                    return semanticSearch.search(query, topK);
                } catch (Exception e) {
                    log.error("Semantic search failed", e);
                    return Collections.emptyList();
                }
            });
        
        CompletableFuture<List<SearchResult>> keywordFuture = 
            CompletableFuture.supplyAsync(() -> {
                try {
                    return keywordSearch.keywordSearch(query, topK);
                } catch (Exception e) {
                    log.error("Keyword search failed", e);
                    return Collections.emptyList();
                }
            });
        
        // Wait for both to complete
        List<SearchResult> semanticResults = semanticFuture.join();
        List<SearchResult> keywordResults = keywordFuture.join();
        
        log.debug("Semantic found {}, keyword found {}", 
            semanticResults.size(), keywordResults.size());
        
        // Normalize and weight scores
        normalizeScores(semanticResults, semanticWeight);
        normalizeScores(keywordResults, keywordWeight);
        
        // Merge results
        List<SearchResult> merged = mergeResults(semanticResults, keywordResults);
        
        // Re-rank
        List<SearchResult> reRanked = reRanker.reRank(merged, query);
        
        // Take top K
        List<SearchResult> topResults = reRanked.stream()
            .limit(topK)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Hybrid search completed in {}ms, returning {} results", 
            duration, topResults.size());
        
        return topResults;
    }
    
    private void normalizeScores(List<SearchResult> results, double weight) {
        if (results.isEmpty()) return;
        
        double maxScore = results.stream()
            .mapToDouble(SearchResult::getScore)
            .max()
            .orElse(1.0);
        
        results.forEach(r -> {
            double normalized = (r.getScore() / maxScore) * weight;
            r.setScore(normalized);
        });
    }
    
    private List<SearchResult> mergeResults(List<SearchResult> semantic, 
                                           List<SearchResult> keyword) {
        Map<String, SearchResult> merged = new HashMap<>();
        
        // Add all semantic results
        semantic.forEach(r -> merged.put(r.getId(), r));
        
        // Add keyword results, combining scores for duplicates
        keyword.forEach(r -> {
            if (merged.containsKey(r.getId())) {
                SearchResult existing = merged.get(r.getId());
                existing.setScore(existing.getScore() + r.getScore());
                existing.addSearchType("KEYWORD");
            } else {
                r.addSearchType("KEYWORD");
                merged.put(r.getId(), r);
            }
        });
        
        semantic.forEach(r -> r.addSearchType("SEMANTIC"));
        
        return new ArrayList<>(merged.values());
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test hybrid search with various queries
  - `[ ]` Sub-task: Test result merging
  - `[ ]` Sub-task: Test score normalization
  - `[ ]` Sub-task: Test re-ranking boosts work correctly
  - `[ ]` Sub-task: Verify performance (<2 seconds)

---

### Day 28: Query Processing and Routing
**Goal:** Intelligently route queries to appropriate search strategies

- `[ ]` **Create QueryAnalyzer** (3h)
  - `[ ]` Sub-task: Create `QueryAnalyzer.java` class
  - `[ ]` Sub-task: Add method `analyzeQuery(String query)` returns QueryType
  - `[ ]` Sub-task: Detect query types:
    - EXACT_SYMBOL: "ClassName.methodName"
    - CODE_SNIPPET: Contains code syntax like "public void"
    - NATURAL_LANGUAGE: Plain text description
    - DEPENDENCY: "what calls X", "dependencies of Y"
    - PACKAGE: Package-related queries
  - `[ ]` Sub-task: Extract query entities (class names, method names)
  - `[ ]` Sub-task: Determine best search strategy for each type
  - `[ ]` Sub-task: Return QueryAnalysis object with type and strategy

- `[ ]` **Create QueryProcessor** (3h)
  - `[ ]` Sub-task: Create `QueryProcessor.java` class
  - `[ ]` Sub-task: Inject all search services and QueryAnalyzer
  - `[ ]` Sub-task: Add method `process(String query, int topK)` that:
    - Analyzes query type
    - Routes to appropriate search service
    - Adds additional context if needed
    - Returns unified SearchResponse
  - `[ ]` Sub-task: Add special handling for dependency queries
  - `[ ]` Sub-task: Add special handling for symbol lookups
  - `[ ]` Sub-task: Add fallback to hybrid search if specific strategy fails

- `[ ]` **Implement Query Expansion** (2h)
  - `[ ]` Sub-task: Add synonym expansion (e.g., "auth" includes "authentication")
  - `[ ]` Sub-task: Add related term expansion from context
  - `[ ]` Sub-task: Expand acronyms based on codebase
  - `[ ]` Sub-task: Add method to get related queries
  - `[ ]` Sub-task: Log expansions for debugging

**Query Processing Example:**
```java
@Service
@Slf4j
public class QueryProcessor {
    private final QueryAnalyzer analyzer;
    private final SymbolTableService symbolTable;
    private final HybridSearchService hybridSearch;
    private final DependencyGraphService graphService;
    
    public SearchResponse process(String query, int topK) throws SearchException {
        log.info("Processing query: '{}'", query);
        
        // Analyze query
        QueryAnalysis analysis = analyzer.analyzeQuery(query);
        log.debug("Query type: {}, strategy: {}", 
            analysis.getType(), analysis.getStrategy());
        
        SearchResponse response;
        
        switch (analysis.getType()) {
            case EXACT_SYMBOL:
                response = processSymbolQuery(analysis, topK);
                break;
                
            case DEPENDENCY:
                response = processDependencyQuery(analysis, topK);
                break;
                
            case CODE_SNIPPET:
                response = processCodeSnippetQuery(analysis, topK);
                break;
                
            case NATURAL_LANGUAGE:
            default:
                response = processNaturalLanguageQuery(analysis, topK);
                break;
        }
        
        // Add suggestions
        response.setSuggestions(generateSuggestions(query, analysis));
        
        // Add query insights
        response.setQueryType(analysis.getType().toString());
        response.setProcessingTime(response.getEndTime() - response.getStartTime());
        
        log.info("Query processed in {}ms, found {} results", 
            response.getProcessingTime(), response.getResults().size());
        
        return response;
    }
    
    private SearchResponse processSymbolQuery(QueryAnalysis analysis, int topK) {
        String symbolName = analysis.getExtractedEntity();
        List<Symbol> symbols = symbolTable.findByName(symbolName);
        
        if (symbols.isEmpty()) {
            // Try fuzzy match
            symbols = symbolTable.findSimilar(symbolName, 2);
        }
        
        List<SearchResult> results = symbols.stream()
            .limit(topK)
            .map(this::symbolToSearchResult)
            .collect(Collectors.toList());
        
        return SearchResponse.builder()
            .results(results)
            .totalCount(symbols.size())
            .searchType("SYMBOL_LOOKUP")
            .build();
    }
    
    private SearchResponse processDependencyQuery(QueryAnalysis analysis, int topK) {
        // Extract target from query (e.g., "PaymentService" from "what calls PaymentService")
        String target = analysis.getExtractedEntity();
        
        // Find symbol
        List<Symbol> symbols = symbolTable.findByName(target);
        if (symbols.isEmpty()) {
            return SearchResponse.empty("Symbol not found: " + target);
        }
        
        Symbol symbol = symbols.get(0);
        
        // Get dependencies from graph
        List<GraphNode> dependencies;
        if (analysis.getQuery().contains("calls")) {
            dependencies = graphService.getCallers(symbol);
        } else {
            dependencies = graphService.getCallees(symbol);
        }
        
        List<SearchResult> results = dependencies.stream()
            .limit(topK)
            .map(this::nodeToSearchResult)
            .collect(Collectors.toList());
        
        return SearchResponse.builder()
            .results(results)
            .totalCount(dependencies.size())
            .searchType("DEPENDENCY_ANALYSIS")
            .build();
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test query type detection
  - `[ ]` Sub-task: Test routing to correct search service
  - `[ ]` Sub-task: Test each query type end-to-end
  - `[ ]` Sub-task: Test fallback behavior
  - `[ ]` Sub-task: Test query expansion

---

## Week 6: Context Assembly & Search API (Sprint 3, Part 1)

### Day 29: Context Assembly
**Goal:** Add surrounding code context to search results

- `[ ]` **Create ContextAssemblyService** (4h)
  - `[ ]` Sub-task: Create `ContextAssemblyService.java` in service package
  - `[ ]` Sub-task: Add method `addContext(SearchResult result)` that enriches result
  - `[ ]` Sub-task: Read source file for the result
  - `[ ]` Sub-task: Extract surrounding lines (configurable window, default ±5 lines)
  - `[ ]` Sub-task: Add caller/callee information from graph
  - `[ ]` Sub-task: Add class hierarchy information
  - `[ ]` Sub-task: Add related methods in same class
  - `[ ]` Sub-task: Format context for display

- `[ ]` **Implement Smart Context Selection** (2h)
  - `[ ]` Sub-task: Include full method if result is partial
  - `[ ]` Sub-task: Include class header for methods
  - `[ ]` Sub-task: Include JavaDoc if available
  - `[ ]` Sub-task: Include imports if relevant to understanding
  - `[ ]` Sub-task: Limit total context size (max 100 lines)

- `[ ]` **Add Cross-References** (2h)
  - `[ ]` Sub-task: Link to caller methods
  - `[ ]` Sub-task: Link to callee methods
  - `[ ]` Sub-task: Link to parent class/interface
  - `[ ]` Sub-task: Link to overridden methods
  - `[ ]` Sub-task: Create reference objects with links

**Context Assembly Example:**
```java
@Service
@Slf4j
public class ContextAssemblyService {
    private final DependencyGraphService graphService;
    private final CodeFileRepository fileRepository;
    
    @Value("${search.context.lines:5}")
    private int contextLines;
    
    public void addContext(SearchResult result) {
        log.debug("Adding context to result: {}", result.getId());
        
        try {
            // Read source file
            Path filePath = Paths.get(result.getFilePath());
            List<String> allLines = Files.readAllLines(filePath);
            
            // Get surrounding lines
            int start = Math.max(0, result.getStartLine() - contextLines - 1);
            int end = Math.min(allLines.size(), result.getEndLine() + contextLines);
            
            List<String> contextLines = allLines.subList(start, end);
            String context = String.join("\n", contextLines);
            
            result.setFullContext(context);
            result.setContextStartLine(start + 1);
            result.setContextEndLine(end);
            
            // Add caller/callee information
            if (result.getMethodName() != null) {
                addMethodContext(result);
            }
            
            // Add class context
            if (result.getClassName() != null) {
                addClassContext(result);
            }
            
        } catch (Exception e) {
            log.error("Failed to add context to result", e);
            // Continue without context
        }
    }
    
    private void addMethodContext(SearchResult result) {
        String methodFQN = result.getClassName() + "." + result.getMethodName();
        
        // Get callers
        List<GraphNode> callers = graphService.getCallers(methodFQN);
        result.setCallers(callers.stream()
            .map(GraphNode::getName)
            .limit(5)
            .collect(Collectors.toList()));
        
        // Get callees
        List<GraphNode> callees = graphService.getCallees(methodFQN);
        result.setCallees(callees.stream()
            .map(GraphNode::getName)
            .limit(5)
            .collect(Collectors.toList()));
        
        log.debug("Added method context: {} callers, {} callees", 
            result.getCallers().size(), result.getCallees().size());
    }
    
    private void addClassContext(SearchResult result) {
        // Get class hierarchy
        ClassInfo classInfo = graphService.getClassInfo(result.getClassName());
        
        if (classInfo != null) {
            result.setParentClass(classInfo.getParentClass());
            result.setInterfaces(classInfo.getInterfaces());
            result.setRelatedClasses(graphService.getRelatedClasses(
                result.getClassName(), 3));
        }
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test context extraction
  - `[ ]` Sub-task: Test caller/callee information
  - `[ ]` Sub-task: Test class hierarchy information
  - `[ ]` Sub-task: Test with edge cases (start/end of file)

---

### Day 30: Search REST API
**Goal:** Create comprehensive REST API for all search functionality

- `[ ]` **Create SearchController** (3h)
  - `[ ]` Sub-task: Create `SearchController.java` with `@RestController`
  - `[ ]` Sub-task: Add `POST /api/search` endpoint for general search
  - `[ ]` Sub-task: Add `GET /api/search/symbol/{name}` for symbol lookup
  - `[ ]` Sub-task: Add `GET /api/search/dependencies/{symbol}` for dependency queries
  - `[ ]` Sub-task: Add `GET /api/search/similar/{fileId}/{lineStart}` for similar code
  - `[ ]` Sub-task: Add proper request/response DTOs
  - `[ ]` Sub-task: Add validation for parameters

- `[ ]` **Create Request/Response DTOs** (2h)
  - `[ ]` Sub-task: Create `SearchRequest.java` with fields: query, topK, filters
  - `[ ]` Sub-task: Create `SearchResponse.java` with fields: results, totalCount, timing, suggestions
  - `[ ]` Sub-task: Create `SearchFilter.java` for filtering options
  - `[ ]` Sub-task: Add validation annotations (@NotNull, @Min, @Max)
  - `[ ]` Sub-task: Add Jackson annotations for JSON serialization

- `[ ]` **Add Error Handling** (1h)
  - `[ ]` Sub-task: Handle invalid queries gracefully
  - `[ ]` Sub-task: Handle empty results
  - `[ ]` Sub-task: Handle service failures
  - `[ ]` Sub-task: Return appropriate HTTP status codes
  - `[ ]` Sub-task: Return user-friendly error messages

- `[ ]` **Add API Documentation** (2h)
  - `[ ]` Sub-task: Add OpenAPI/Swagger annotations
  - `[ ]` Sub-task: Document all endpoints
  - `[ ]` Sub-task: Add request/response examples
  - `[ ]` Sub-task: Document query syntax
  - `[ ]` Sub-task: Add usage guide in README

**Search API Example:**
```java
@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {
    private final QueryProcessor queryProcessor;
    private final ContextAssemblyService contextAssembly;
    
    @PostMapping
    public ResponseEntity<SearchResponse> search(
            @Valid @RequestBody SearchRequest request) {
        
        log.info("Search request: query='{}', topK={}", 
            request.getQuery(), request.getTopK());
        
        try {
            // Process query
            SearchResponse response = queryProcessor.process(
                request.getQuery(), 
                request.getTopK()
            );
            
            // Add context to results
            response.getResults().forEach(contextAssembly::addContext);
            
            // Apply filters if provided
            if (request.getFilter() != null) {
                response.setResults(applyFilters(
                    response.getResults(), 
                    request.getFilter()
                ));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (SearchException e) {
            log.error("Search failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SearchResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/symbol/{name}")
    public ResponseEntity<SearchResponse> findSymbol(
            @PathVariable String name,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Symbol lookup: name='{}', limit={}", name, limit);
        
        SearchRequest request = SearchRequest.builder()
            .query(name)
            .topK(limit)
            .build();
        
        return search(request);
    }
    
    @GetMapping("/dependencies/{symbol}")
    public ResponseEntity<DependencyResponse> getDependencies(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "callers") String type) {
        
        log.info("Dependency query: symbol='{}', type={}", symbol, type);
        
        try {
            List<GraphNode> dependencies;
            
            if ("callers".equals(type)) {
                dependencies = graphService.getCallers(symbol);
            } else if ("callees".equals(type)) {
                dependencies = graphService.getCallees(symbol);
            } else {
                return ResponseEntity.badRequest()
                    .body(DependencyResponse.error("Invalid type: " + type));
            }
            
            DependencyResponse response = DependencyResponse.builder()
                .symbol(symbol)
                .type(type)
                .dependencies(dependencies)
                .count(dependencies.size())
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Dependency query failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DependencyResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/similar")
    public ResponseEntity<SearchResponse> findSimilar(
            @RequestBody CodeSnippet snippet,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Similar code search: limit={}", limit);
        
        try {
            // Generate embedding for snippet
            float[] embedding = embeddingService.getEmbedding(snippet.getCode());
            
            // Search for similar code
            List<SearchResult> results = semanticSearch.searchByEmbedding(
                embedding, 
                limit
            );
            
            // Add context
            results.forEach(contextAssembly::addContext);
            
            SearchResponse response = SearchResponse.builder()
                .results(results)
                .totalCount(results.size())
                .searchType("SIMILARITY")
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Similar code search failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SearchResponse.error(e.getMessage()));
        }
    }
}
```

**SearchRequest DTO:**
```java
@Data
@Builder
public class SearchRequest {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    @Min(1)
    @Max(100)
    private int topK = 10;
    
    private SearchFilter filter;
    
    private boolean includeContext = true;
}

@Data
public class SearchFilter {
    private String filePathPattern;
    private String packageName;
    private List<String> codeTypes; // CLASS, METHOD, FIELD
    private LocalDateTime modifiedAfter;
    private LocalDateTime modifiedBefore;
}
```

- `[ ]` **Create API Tests** (1h)
  - `[ ]` Sub-task: Test all endpoints with REST Assured or MockMvc
  - `[ ]` Sub-task: Test request validation
  - `[ ]` Sub-task: Test error handling
  - `[ ]` Sub-task: Test response format
  - `[ ]` Sub-task: Test with various query types

---

### Day 31: Query Optimization
**Goal:** Optimize search performance to meet <2 second target

- `[ ]` **Add Caching Layer** (3h)
  - `[ ]` Sub-task: Add Spring Cache dependencies
  - `[ ]` Sub-task: Configure cache manager (Caffeine or EhCache)
  - `[ ]` Sub-task: Cache frequent queries (LRU cache, max 1000 entries)
  - `[ ]` Sub-task: Add @Cacheable annotations to search methods
  - `[ ]` Sub-task: Add cache key based on query and parameters
  - `[ ]` Sub-task: Add cache eviction when index updates
  - `[ ]` Sub-task: Add cache statistics logging

- `[ ]` **Optimize Database Queries** (2h)
  - `[ ]` Sub-task: Add database indexes for common queries
  - `[ ]` Sub-task: Optimize symbol lookup queries
  - `[ ]` Sub-task: Use query projections to load only needed fields
  - `[ ]` Sub-task: Add batch fetching for related entities
  - `[ ]` Sub-task: Profile queries with EXPLAIN
  - `[ ]` Sub-task: Optimize slow queries

- `[ ]` **Implement Query Timeout** (1h)
  - `[ ]` Sub-task: Add timeout configuration for searches
  - `[ ]` Sub-task: Set timeout to 5 seconds
  - `[ ]` Sub-task: Cancel long-running queries
  - `[ ]` Sub-task: Return partial results if timeout occurs
  - `[ ]` Sub-task: Log timeout occurrences

- `[ ]` **Add Performance Monitoring** (2h)
  - `[ ]` Sub-task: Add timing metrics for each search component
  - `[ ]` Sub-task: Track average query time
  - `[ ]` Sub-task: Track slow queries (>2 seconds)
  - `[ ]` Sub-task: Add performance dashboard endpoint
  - `[ ]` Sub-task: Add alerts for performance degradation
  - `[ ]` Sub-task: Create performance report

**Caching Example:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "search-results", 
            "symbol-lookup",
            "embeddings"
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        
        return cacheManager;
    }
}

@Service
public class CachedSearchService {
    
    @Cacheable(value = "search-results", key = "#query + '-' + #topK")
    public SearchResponse search(String query, int topK) {
        // Actual search implementation
    }
    
    @CacheEvict(value = "search-results", allEntries = true)
    public void clearCache() {
        log.info("Search cache cleared");
    }
}
```

- `[ ]` **Performance Testing** (1h)
  - `[ ]` Sub-task: Test query performance with cache cold/warm
  - `[ ]` Sub-task: Measure improvement from caching
  - `[ ]` Sub-task: Test concurrent queries
  - `[ ]` Sub-task: Identify remaining bottlenecks
  - `[ ]` Sub-task: Document performance improvements

---

### Day 32: Search Suggestions & Auto-Complete
**Goal:** Add intelligent search suggestions and auto-complete

- `[ ]` **Create Suggestion Engine** (3h)
  - `[ ]` Sub-task: Create `SuggestionService.java`
  - `[ ]` Sub-task: Build prefix tree (Trie) for symbol names
  - `[ ]` Sub-task: Add method `getSuggestions(String prefix, int limit)`
  - `[ ]` Sub-task: Support fuzzy suggestions
  - `[ ]` Sub-task: Rank suggestions by usage frequency
  - `[ ]` Sub-task: Cache suggestion results

- `[ ]` **Implement Query Completion** (2h)
  - `[ ]` Sub-task: Add auto-complete for class names
  - `[ ]` Sub-task: Add auto-complete for method names
  - `[ ]` Sub-task: Add auto-complete for package names
  - `[ ]` Sub-task: Return suggestions as user types (after 3 characters)
  - `[ ]` Sub-task: Limit to top 10 suggestions

- `[ ]` **Add "Did You Mean?"** (2h)
  - `[ ]` Sub-task: Detect misspelled queries
  - `[ ]` Sub-task: Use Levenshtein distance for corrections
  - `[ ]` Sub-task: Suggest corrections when no results found
  - `[ ]` Sub-task: Support automatic correction with user confirmation
  - `[ ]` Sub-task: Learn from user selections

- `[ ]` **Create Suggestion API** (1h)
  - `[ ]` Sub-task: Add `GET /api/search/suggestions` endpoint
  - `[ ]` Sub-task: Accept prefix parameter
  - `[ ]` Sub-task: Return ranked suggestions
  - `[ ]` Sub-task: Add filtering options
  - `[ ]` Sub-task: Test endpoint

**Suggestion Service Example:**
```java
@Service
@Slf4j
public class SuggestionService {
    private final Trie symbolTrie = new Trie();
    private final Map<String, Integer> usageCount = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void buildTrie() {
        log.info("Building suggestion trie...");
        
        List<Symbol> symbols = symbolRepository.findAll();
        symbols.forEach(symbol -> {
            symbolTrie.insert(symbol.getName());
            symbolTrie.insert(symbol.getFullyQualifiedName());
        });
        
        log.info("Trie built with {} symbols", symbols.size());
    }
    
    @Cacheable("suggestions")
    public List<String> getSuggestions(String prefix, int limit) {
        if (prefix.length() < 3) {
            return Collections.emptyList();
        }
        
        // Get all matching symbols
        List<String> matches = symbolTrie.findWordsWithPrefix(prefix);
        
        // Rank by usage and similarity
        return matches.stream()
            .sorted((a, b) -> {
                int usageCompare = usageCount.getOrDefault(b, 0)
                    .compareTo(usageCount.getOrDefault(a, 0));
                if (usageCompare != 0) return usageCompare;
                
                // Prefer exact prefix matches
                boolean aStarts = a.toLowerCase().startsWith(prefix.toLowerCase());
                boolean bStarts = b.toLowerCase().startsWith(prefix.toLowerCase());
                if (aStarts != bStarts) return aStarts ? -1 : 1;
                
                // Prefer shorter names
                return Integer.compare(a.length(), b.length());
            })
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public void recordUsage(String symbol) {
        usageCount.merge(symbol, 1, Integer::sum);
    }
    
    public List<String> getDidYouMean(String query) {
        List<String> allSymbols = symbolRepository.findAllNames();
        
        return allSymbols.stream()
            .map(symbol -> new Pair<>(symbol, 
                levenshteinDistance(symbol.toLowerCase(), query.toLowerCase())))
            .filter(pair -> pair.getValue() <= 3) // Max 3 character difference
            .sorted(Comparator.comparingInt(Pair::getValue))
            .limit(5)
            .map(Pair::getKey)
            .collect(Collectors.toList());
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test suggestion generation
  - `[ ]` Sub-task: Test auto-complete
  - `[ ]` Sub-task: Test "did you mean" suggestions
  - `[ ]` Sub-task: Test ranking algorithm
  - `[ ]` Sub-task: Test API endpoint

---

### Day 33: Integration Testing & Documentation
**Goal:** Comprehensive testing and documentation of search system

- `[ ]` **Create End-to-End Search Tests** (3h)
  - `[ ]` Sub-task: Test complete search flow from API to results
  - `[ ]` Sub-task: Test all query types with real data
  - `[ ]` Sub-task: Test search with Firestick's own codebase
  - `[ ]` Sub-task: Test performance meets <2 second requirement
  - `[ ]` Sub-task: Test error handling and edge cases
  - `[ ]` Sub-task: Test caching behavior

- `[ ]` **Performance Benchmarking** (2h)
  - `[ ]` Sub-task: Create benchmark suite
  - `[ ]` Sub-task: Test with different index sizes (100, 1K, 10K files)
  - `[ ]` Sub-task: Test concurrent query handling
  - `[ ]` Sub-task: Measure memory usage
  - `[ ]` Sub-task: Document benchmark results
  - `[ ]` Sub-task: Compare against Phase 3 goals

- `[ ]` **Write Search Documentation** (3h)
  - `[ ]` Sub-task: Document query syntax and examples
  - `[ ]` Sub-task: Document all search features
  - `[ ]` Sub-task: Create user guide for search
  - `[ ]` Sub-task: Document API endpoints with examples
  - `[ ]` Sub-task: Add troubleshooting section
  - `[ ]` Sub-task: Create video demo or screenshots

**Performance Benchmarks to Document:**
- Average query time by type
- Cache hit rate
- Concurrent query capacity
- Memory usage
- Index size vs query time

---

## Phase 3 Success Criteria

Before moving to Phase 4, verify ALL of the following:

### Functional Requirements
- ⬜ Symbol table provides instant exact-match lookups
- ⬜ Semantic search returns relevant results for natural language queries
- ⬜ Keyword search supports Boolean and field-specific queries
- ⬜ Hybrid search combines both approaches effectively
- ⬜ Query processor correctly identifies and routes query types
- ⬜ Context assembly adds useful surrounding code
- ⬜ Search API is fully functional and documented
- ⬜ Suggestions and auto-complete work correctly
- ⬜ Can find code using various query styles

### Performance Requirements
- ⬜ 95% of queries complete in under 2 seconds
- ⬜ Symbol lookups complete in under 100ms
- ⬜ Cache improves performance by 50%+ for repeated queries
- ⬜ System handles 10+ concurrent queries
- ⬜ Memory usage is reasonable (< 3GB)

### Search Quality Requirements
- ⬜ Semantic search returns relevant results in top 10 (>80% precision)
- ⬜ Keyword search finds exact matches reliably
- ⬜ Hybrid search improves results over either alone
- ⬜ Re-ranking improves result ordering
- ⬜ Context provides enough information to understand code

### Code Quality Requirements
- ⬜ All unit tests pass
- ⬜ All integration tests pass
- ⬜ Test coverage > 70%
- ⬜ No critical bugs
- ⬜ Code is well-documented
- ⬜ API is documented with examples

### Documentation Requirements
- ⬜ Query syntax is documented
- ⬜ API endpoints are fully documented
- ⬜ User guide exists
- ⬜ Performance benchmarks are documented
- ⬜ Troubleshooting guide is complete

---

## Common Issues & Troubleshooting

### Issue: Semantic search returns irrelevant results
**Solution:**
- Check embedding model is loaded correctly
- Verify chunk quality (proper context included)
- Adjust query preprocessing
- Try increasing topK and re-ranking
- Check if query needs expansion

### Issue: Keyword search misses results
**Solution:**
- Verify Lucene indexing is complete
- Check analyzer configuration
- Try different query syntax
- Add missing fields to index
- Check for tokenization issues

### Issue: Search is too slow (>2 seconds)
**Solution:**
- Check cache is enabled and warming up
- Profile to find bottleneck
- Optimize database queries
- Reduce topK for initial search
- Enable parallel search execution
- Add more specific query routing

### Issue: Hybrid search gives worse results
**Solution:**
- Adjust semantic/keyword weights
- Check score normalization
- Review re-ranking algorithm
- Test each search type independently
- Add more domain-specific boosts

### Issue: No results for valid queries
**Solution:**
- Verify index is built and populated
- Check Chroma connection
- Verify query preprocessing isn't too aggressive
- Try simplified query
- Check logs for errors

### Issue: Symbol lookup is slow
**Solution:**
- Verify symbol table is built in memory
- Check index structure
- Add more specific indexes
- Profile symbol table lookups
- Consider better data structure

### Issue: Context assembly fails
**Solution:**
- Verify file paths are correct
- Check file permissions
- Handle deleted/moved files
- Add error handling for missing files
- Cache file contents

---

## Phase 3 Sprint Review Checklist

**Demo Items:**
- [ ] Show natural language search
- [ ] Show exact symbol lookup
- [ ] Show keyword search with Boolean operators
- [ ] Show hybrid search combining both
- [ ] Show search results with context
- [ ] Show dependency queries
- [ ] Show similar code search
- [ ] Show search suggestions and auto-complete
- [ ] Show performance metrics (<2 seconds)
- [ ] Show API in action (Postman/Swagger)

**Retrospective Questions:**
- What search features work best?
- What query types are most challenging?
- How can we improve result relevance?
- What performance optimizations were most effective?
- What should we focus on in Phase 4?

---
---

# Phase 4: Analysis Features (Weeks 7-8)

**Status:** Not Started  
**Goal:** Build code analysis tools for quality metrics, pattern detection, and dead code identification  
**Team:** Backend Developer(s)  
**Duration:** Nov 25 - Dec 8, 2025 (2 weeks, Sprint 3-4)  
**Dependencies:** Phase 2 (indexing) and Phase 3 (search) must be complete

---

## Phase 4 Overview

### What We're Building
Advanced code analysis features that help developers understand code quality and structure:
1. **Static Analysis** - Complexity metrics, code smells, quality scores
2. **Dead Code Detection** - Find unused classes, methods, and imports
3. **Pattern Recognition** - Identify design patterns and anti-patterns
4. **Documentation Generation** - Auto-generate docs from code structure
5. **Visualization** - Create visual representations of metrics and patterns

### Why This Matters
Analysis features make Firestick more than just a search tool. They help developers:
- Identify code that needs refactoring
- Find and remove dead code
- Understand architectural patterns
- Improve code quality
- Make informed refactoring decisions

### Types of Analysis We'll Support
- **Complexity Analysis:** Cyclomatic complexity, cognitive complexity
- **Code Smells:** Long methods, large classes, duplicate code
- **Dead Code:** Unused methods, unreachable code
- **Patterns:** Design patterns, common idioms
- **Dependencies:** Circular dependencies, tight coupling
- **Documentation:** Missing JavaDoc, outdated comments

---

## Week 7: Static Analysis & Metrics (Sprint 3, Part 2)

### Day 34: Complexity Analysis
**Goal:** Calculate code complexity metrics for methods and classes

- `[ ]` **Research Complexity Metrics** (1h)
  - `[ ]` Sub-task: Review cyclomatic complexity definition
  - `[ ]` Sub-task: Review cognitive complexity definition
  - `[ ]` Sub-task: Understand Halstead complexity
  - `[ ]` Sub-task: Decide which metrics to implement
  - `[ ]` Sub-task: Document metric definitions

- `[ ]` **Create ComplexityAnalyzer** (4h)
  - `[ ]` Sub-task: Create `ComplexityAnalyzer.java` in new `analysis` package
  - `[ ]` Sub-task: Implement cyclomatic complexity calculation
    - Count decision points (if, for, while, case, catch, &&, ||)
    - Calculate per method
    - Return complexity score (1-10+ scale)
  - `[ ]` Sub-task: Implement method line count
  - `[ ]` Sub-task: Implement parameter count
  - `[ ]` Sub-task: Calculate nesting depth
  - `[ ]` Sub-task: Create `ComplexityMetrics` DTO to hold results

- `[ ]` **Integrate with JavaParser** (2h)
  - `[ ]` Sub-task: Create visitor to traverse AST
  - `[ ]` Sub-task: Count control flow statements
  - `[ ]` Sub-task: Handle nested structures correctly
  - `[ ]` Sub-task: Calculate metrics for all methods in a file
  - `[ ]` Sub-task: Store metrics in database

- `[ ]` **Add Complexity Thresholds** (1h)
  - `[ ]` Sub-task: Define complexity levels:
    - Low: 1-5 (simple)
    - Medium: 6-10 (moderate)
    - High: 11-20 (complex)
    - Very High: 21+ (very complex)
  - `[ ]` Sub-task: Color-code or flag based on thresholds
  - `[ ]` Sub-task: Make thresholds configurable

**Complexity Analyzer Example:**
```java
@Service
@Slf4j
public class ComplexityAnalyzer {
    
    public ComplexityMetrics analyzeMethod(MethodDeclaration method) {
        log.debug("Analyzing complexity for method: {}", method.getNameAsString());
        
        ComplexityMetrics metrics = new ComplexityMetrics();
        metrics.setMethodName(method.getNameAsString());
        
        // Calculate cyclomatic complexity
        int complexity = calculateCyclomaticComplexity(method);
        metrics.setCyclomaticComplexity(complexity);
        
        // Count lines
        int lines = method.getEnd().get().line - method.getBegin().get().line + 1;
        metrics.setLineCount(lines);
        
        // Count parameters
        metrics.setParameterCount(method.getParameters().size());
        
        // Calculate nesting depth
        int depth = calculateNestingDepth(method);
        metrics.setMaxNestingDepth(depth);
        
        // Determine complexity level
        metrics.setComplexityLevel(determineLevel(complexity));
        
        return metrics;
    }
    
    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.accept(visitor, null);
        return visitor.getComplexity();
    }
    
    private static class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {
        private int complexity = 1; // Base complexity
        
        @Override
        public void visit(IfStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ForStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(WhileStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(DoStmt n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(SwitchEntry n, Void arg) {
            if (!n.getLabels().isEmpty()) {
                complexity++;
            }
            super.visit(n, arg);
        }
        
        @Override
        public void visit(CatchClause n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        @Override
        public void visit(ConditionalExpr n, Void arg) {
            complexity++;
            super.visit(n, arg);
        }
        
        public int getComplexity() {
            return complexity;
        }
    }
    
    private String determineLevel(int complexity) {
        if (complexity <= 5) return "LOW";
        if (complexity <= 10) return "MEDIUM";
        if (complexity <= 20) return "HIGH";
        return "VERY_HIGH";
    }
}
```

**ComplexityMetrics DTO:**
```java
@Data
@Builder
public class ComplexityMetrics {
    private String methodName;
    private String className;
    private String filePath;
    private int cyclomaticComplexity;
    private int lineCount;
    private int parameterCount;
    private int maxNestingDepth;
    private String complexityLevel; // LOW, MEDIUM, HIGH, VERY_HIGH
    private List<String> recommendations;
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test complexity calculation for simple method
  - `[ ]` Sub-task: Test with nested conditions
  - `[ ]` Sub-task: Test with loops and switches
  - `[ ]` Sub-task: Verify thresholds work correctly
  - `[ ]` Sub-task: Test with real code samples

---

### Day 35: Code Smell Detection
**Goal:** Detect common code smells and quality issues

- `[ ]` **Create CodeSmellDetector** (4h)
  - `[ ]` Sub-task: Create `CodeSmellDetector.java` in analysis package
  - `[ ]` Sub-task: Detect long methods (>50 lines threshold)
  - `[ ]` Sub-task: Detect large classes (>500 lines threshold)
  - `[ ]` Sub-task: Detect too many parameters (>5 parameters)
  - `[ ]` Sub-task: Detect deep nesting (>4 levels)
  - `[ ]` Sub-task: Detect long parameter lists
  - `[ ]` Sub-task: Create `CodeSmell` DTO to represent findings

- `[ ]` **Implement Naming Convention Checks** (2h)
  - `[ ]` Sub-task: Check class names are PascalCase
  - `[ ]` Sub-task: Check method names are camelCase
  - `[ ]` Sub-task: Check constants are UPPER_CASE
  - `[ ]` Sub-task: Check for single-letter variable names (except loop counters)
  - `[ ]` Sub-task: Flag violations with severity levels

- `[ ]` **Detect Magic Numbers and Strings** (1h)
  - `[ ]` Sub-task: Find numeric literals (except 0, 1, -1)
  - `[ ]` Sub-task: Find string literals used multiple times
  - `[ ]` Sub-task: Suggest extracting to constants
  - `[ ]` Sub-task: Exclude common cases (empty strings, etc.)

- `[ ]` **Create Smell Severity Levels** (1h)
  - `[ ]` Sub-task: Define severity: INFO, WARNING, ERROR, CRITICAL
  - `[ ]` Sub-task: Assign severity to each smell type
  - `[ ]` Sub-task: Create prioritized list of smells to fix
  - `[ ]` Sub-task: Add recommendations for each smell

**Code Smell Detector Example:**
```java
@Service
@Slf4j
public class CodeSmellDetector {
    
    @Value("${analysis.max.method.lines:50}")
    private int maxMethodLines;
    
    @Value("${analysis.max.parameters:5}")
    private int maxParameters;
    
    @Value("${analysis.max.nesting:4}")
    private int maxNesting;
    
    public List<CodeSmell> detectSmells(FileInfo fileInfo) {
        log.info("Detecting code smells in: {}", fileInfo.getFilePath());
        
        List<CodeSmell> smells = new ArrayList<>();
        
        // Check each class
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            smells.addAll(detectClassSmells(classInfo));
            
            // Check each method
            for (MethodInfo method : classInfo.getMethods()) {
                smells.addAll(detectMethodSmells(method, classInfo));
            }
        }
        
        log.info("Found {} code smells", smells.size());
        return smells;
    }
    
    private List<CodeSmell> detectClassSmells(ClassInfo classInfo) {
        List<CodeSmell> smells = new ArrayList<>();
        
        // Large class
        int classLines = classInfo.getEndLine() - classInfo.getStartLine();
        if (classLines > 500) {
            smells.add(CodeSmell.builder()
                .type("LARGE_CLASS")
                .severity("WARNING")
                .className(classInfo.getName())
                .description(String.format("Class has %d lines (max: 500)", classLines))
                .recommendation("Consider splitting into smaller classes")
                .lineNumber(classInfo.getStartLine())
                .build());
        }
        
        // Too many methods
        if (classInfo.getMethods().size() > 20) {
            smells.add(CodeSmell.builder()
                .type("TOO_MANY_METHODS")
                .severity("WARNING")
                .className(classInfo.getName())
                .description(String.format("Class has %d methods", classInfo.getMethods().size()))
                .recommendation("Consider extracting some methods to helper classes")
                .build());
        }
        
        return smells;
    }
    
    private List<CodeSmell> detectMethodSmells(MethodInfo method, ClassInfo classInfo) {
        List<CodeSmell> smells = new ArrayList<>();
        
        // Long method
        int methodLines = method.getEndLine() - method.getStartLine();
        if (methodLines > maxMethodLines) {
            smells.add(CodeSmell.builder()
                .type("LONG_METHOD")
                .severity("WARNING")
                .className(classInfo.getName())
                .methodName(method.getName())
                .description(String.format("Method has %d lines (max: %d)", 
                    methodLines, maxMethodLines))
                .recommendation("Break method into smaller, focused methods")
                .lineNumber(method.getStartLine())
                .build());
        }
        
        // Too many parameters
        if (method.getParameters().size() > maxParameters) {
            smells.add(CodeSmell.builder()
                .type("LONG_PARAMETER_LIST")
                .severity("WARNING")
                .className(classInfo.getName())
                .methodName(method.getName())
                .description(String.format("Method has %d parameters (max: %d)", 
                    method.getParameters().size(), maxParameters))
                .recommendation("Consider using a parameter object")
                .lineNumber(method.getStartLine())
                .build());
        }
        
        // Check for magic numbers in method body
        smells.addAll(detectMagicNumbers(method, classInfo));
        
        return smells;
    }
    
    private List<CodeSmell> detectMagicNumbers(MethodInfo method, ClassInfo classInfo) {
        List<CodeSmell> smells = new ArrayList<>();
        
        // Parse method body and find numeric literals
        String body = method.getBody();
        Pattern numberPattern = Pattern.compile("\\b\\d+\\.?\\d*\\b");
        Matcher matcher = numberPattern.matcher(body);
        
        Set<String> magicNumbers = new HashSet<>();
        while (matcher.find()) {
            String number = matcher.group();
            // Exclude common numbers
            if (!number.equals("0") && !number.equals("1") && !number.equals("-1")) {
                magicNumbers.add(number);
            }
        }
        
        if (!magicNumbers.isEmpty()) {
            smells.add(CodeSmell.builder()
                .type("MAGIC_NUMBERS")
                .severity("INFO")
                .className(classInfo.getName())
                .methodName(method.getName())
                .description("Method contains magic numbers: " + magicNumbers)
                .recommendation("Extract numbers to named constants")
                .lineNumber(method.getStartLine())
                .build());
        }
        
        return smells;
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test detection of long methods
  - `[ ]` Sub-task: Test detection of too many parameters
  - `[ ]` Sub-task: Test naming convention checks
  - `[ ]` Sub-task: Test magic number detection
  - `[ ]` Sub-task: Verify severity levels

---

### Day 36: Dead Code Detection - Part 1
**Goal:** Identify unused methods and classes

- `[ ]` **Create DeadCodeDetector** (3h)
  - `[ ]` Sub-task: Create `DeadCodeDetector.java` in analysis package
  - `[ ]` Sub-task: Use dependency graph to find unused methods
  - `[ ]` Sub-task: Identify methods with no incoming edges (not called)
  - `[ ]` Sub-task: Identify private methods that are never called
  - `[ ]` Sub-task: Exclude entry points (main, @Test, etc.)
  - `[ ]` Sub-task: Exclude overridden methods
  - `[ ]` Sub-task: Create `DeadCodeReport` DTO

- `[ ]` **Implement Unused Class Detection** (2h)
  - `[ ]` Sub-task: Find classes with no incoming dependencies
  - `[ ]` Sub-task: Exclude classes with annotations (@Service, @Controller, etc.)
  - `[ ]` Sub-task: Exclude classes with main() method
  - `[ ]` Sub-task: Check if class is referenced in config files
  - `[ ]` Sub-task: Flag potentially unused classes

- `[ ]` **Detect Unused Imports** (2h)
  - `[ ]` Sub-task: Parse all import statements
  - `[ ]` Sub-task: Check if imported class is used in code
  - `[ ]` Sub-task: Use JavaParser to verify usage
  - `[ ]` Sub-task: Generate list of unused imports
  - `[ ]` Sub-task: Provide auto-fix suggestions

- `[ ]` **Handle Edge Cases** (1h)
  - `[ ]` Sub-task: Handle reflection usage (mark as potentially used)
  - `[ ]` Sub-task: Handle @SuppressWarnings annotations
  - `[ ]` Sub-task: Handle factory patterns
  - `[ ]` Sub-task: Add configuration for exclusions
  - `[ ]` Sub-task: Document limitations

**Dead Code Detector Example:**
```java
@Service
@Slf4j
public class DeadCodeDetector {
    private final DependencyGraphService graphService;
    private final SymbolRepository symbolRepository;
    
    public DeadCodeReport findDeadCode(String projectPath) {
        log.info("Searching for dead code in: {}", projectPath);
        
        DeadCodeReport report = new DeadCodeReport();
        
        // Find unused methods
        List<Symbol> unusedMethods = findUnusedMethods();
        report.setUnusedMethods(unusedMethods);
        
        // Find unused classes
        List<Symbol> unusedClasses = findUnusedClasses();
        report.setUnusedClasses(unusedClasses);
        
        // Find unused imports
        Map<String, List<String>> unusedImports = findUnusedImports();
        report.setUnusedImports(unusedImports);
        
        log.info("Dead code report: {} unused methods, {} unused classes, {} files with unused imports",
            unusedMethods.size(), unusedClasses.size(), unusedImports.size());
        
        return report;
    }
    
    private List<Symbol> findUnusedMethods() {
        List<Symbol> allMethods = symbolRepository.findByType("METHOD");
        List<Symbol> unusedMethods = new ArrayList<>();
        
        for (Symbol method : allMethods) {
            // Skip if it's an entry point
            if (isEntryPoint(method)) {
                continue;
            }
            
            // Skip if it's an overridden method
            if (isOverriddenMethod(method)) {
                continue;
            }
            
            // Check if method has any callers
            List<GraphNode> callers = graphService.getCallers(method.getFullyQualifiedName());
            
            if (callers.isEmpty() && isPrivate(method)) {
                unusedMethods.add(method);
                log.debug("Found unused private method: {}", method.getFullyQualifiedName());
            }
        }
        
        return unusedMethods;
    }
    
    private boolean isEntryPoint(Symbol method) {
        // Check for main method
        if (method.getName().equals("main") && 
            method.getSignature().contains("String[]")) {
            return true;
        }
        
        // Check for common annotations
        List<String> entryPointAnnotations = Arrays.asList(
            "@Test", "@BeforeEach", "@AfterEach", "@BeforeAll", "@AfterAll",
            "@PostConstruct", "@PreDestroy",
            "@RequestMapping", "@GetMapping", "@PostMapping", "@PutMapping", "@DeleteMapping",
            "@EventListener", "@Scheduled"
        );
        
        for (String annotation : entryPointAnnotations) {
            if (method.getAnnotations().contains(annotation)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isOverriddenMethod(Symbol method) {
        // Check if method overrides a parent method
        // This is a simplified check - more sophisticated logic needed
        return method.getAnnotations().contains("@Override");
    }
    
    private boolean isPrivate(Symbol method) {
        return method.getModifiers().contains("private");
    }
    
    private List<Symbol> findUnusedClasses() {
        List<Symbol> allClasses = symbolRepository.findByType("CLASS");
        List<Symbol> unusedClasses = new ArrayList<>();
        
        for (Symbol clazz : allClasses) {
            // Skip classes with special annotations
            if (hasFrameworkAnnotation(clazz)) {
                continue;
            }
            
            // Skip classes with main method
            if (hasMainMethod(clazz)) {
                continue;
            }
            
            // Check if class is referenced
            List<GraphNode> references = graphService.getIncomingDependencies(
                clazz.getFullyQualifiedName());
            
            if (references.isEmpty()) {
                unusedClasses.add(clazz);
            }
        }
        
        return unusedClasses;
    }
    
    private boolean hasFrameworkAnnotation(Symbol clazz) {
        List<String> frameworkAnnotations = Arrays.asList(
            "@Service", "@Repository", "@Controller", "@RestController",
            "@Component", "@Configuration", "@Entity"
        );
        
        return clazz.getAnnotations().stream()
            .anyMatch(frameworkAnnotations::contains);
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test unused method detection
  - `[ ]` Sub-task: Test with entry points (should not flag)
  - `[ ]` Sub-task: Test unused class detection
  - `[ ]` Sub-task: Test unused import detection
  - `[ ]` Sub-task: Verify exclusions work

---

### Day 37: Dead Code Detection - Part 2
**Goal:** Detect unreachable code and other dead code patterns

- `[ ]` **Implement Unreachable Code Detection** (3h)
  - `[ ]` Sub-task: Detect code after return statements
  - `[ ]` Sub-task: Detect code in impossible conditions (if (false))
  - `[ ]` Sub-task: Detect code after throw statements
  - `[ ]` Sub-task: Detect empty catch blocks
  - `[ ]` Sub-task: Use control flow analysis

- `[ ]` **Detect Unused Variables and Fields** (2h)
  - `[ ]` Sub-task: Find local variables that are assigned but never read
  - `[ ]` Sub-task: Find private fields that are never used
  - `[ ]` Sub-task: Exclude fields that might be used via reflection
  - `[ ]` Sub-task: Use JavaParser to track variable usage
  - `[ ]` Sub-task: Generate report with line numbers

- `[ ]` **Create Dead Code Summary Report** (2h)
  - `[ ]` Sub-task: Aggregate all dead code findings
  - `[ ]` Sub-task: Group by file and type
  - `[ ]` Sub-task: Calculate total lines of dead code
  - `[ ]` Sub-task: Estimate potential cleanup impact
  - `[ ]` Sub-task: Prioritize by severity and impact
  - `[ ]` Sub-task: Generate HTML/Markdown report

- `[ ]` **Add Confidence Scores** (1h)
  - `[ ]` Sub-task: Assign confidence level to each finding
  - `[ ]` Sub-task: High confidence: Private unused methods
  - `[ ]` Sub-task: Medium confidence: Classes with no references
  - `[ ]` Sub-task: Low confidence: Possible reflection usage
  - `[ ]` Sub-task: Allow filtering by confidence level

**Unreachable Code Detection Example:**
```java
public class UnreachableCodeDetector {
    
    public List<CodeIssue> findUnreachableCode(CompilationUnit cu) {
        List<CodeIssue> issues = new ArrayList<>();
        
        UnreachableCodeVisitor visitor = new UnreachableCodeVisitor();
        cu.accept(visitor, issues);
        
        return issues;
    }
    
    private static class UnreachableCodeVisitor extends VoidVisitorAdapter<List<CodeIssue>> {
        
        @Override
        public void visit(MethodDeclaration method, List<CodeIssue> issues) {
            super.visit(method, issues);
            
            if (method.getBody().isPresent()) {
                BlockStmt body = method.getBody().get();
                checkForUnreachableStatements(body, issues, method.getNameAsString());
            }
        }
        
        private void checkForUnreachableStatements(BlockStmt block, 
                                                   List<CodeIssue> issues,
                                                   String methodName) {
            List<Statement> statements = block.getStatements();
            boolean foundTerminator = false;
            
            for (int i = 0; i < statements.size(); i++) {
                Statement stmt = statements.get(i);
                
                if (foundTerminator) {
                    // This statement is unreachable
                    issues.add(CodeIssue.builder()
                        .type("UNREACHABLE_CODE")
                        .severity("WARNING")
                        .methodName(methodName)
                        .lineNumber(stmt.getBegin().get().line)
                        .description("Code after return/throw is unreachable")
                        .recommendation("Remove unreachable code")
                        .build());
                }
                
                // Check if this statement terminates the flow
                if (stmt.isReturnStmt() || stmt.isThrowStmt()) {
                    foundTerminator = true;
                } else if (stmt.isIfStmt()) {
                    IfStmt ifStmt = stmt.asIfStmt();
                    checkForImpossibleConditions(ifStmt, issues, methodName);
                }
            }
        }
        
        private void checkForImpossibleConditions(IfStmt ifStmt, 
                                                  List<CodeIssue> issues,
                                                  String methodName) {
            Expression condition = ifStmt.getCondition();
            
            // Check for if (false)
            if (condition.isBooleanLiteralExpr() && 
                !condition.asBooleanLiteralExpr().getValue()) {
                issues.add(CodeIssue.builder()
                    .type("IMPOSSIBLE_CONDITION")
                    .severity("WARNING")
                    .methodName(methodName)
                    .lineNumber(ifStmt.getBegin().get().line)
                    .description("Condition is always false")
                    .recommendation("Remove dead branch or fix condition")
                    .build());
            }
        }
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test unreachable code after return
  - `[ ]` Sub-task: Test impossible conditions
  - `[ ]` Sub-task: Test unused variable detection
  - `[ ]` Sub-task: Test report generation
  - `[ ]` Sub-task: Test confidence scoring

---

### Day 38: Pattern Recognition
**Goal:** Identify design patterns and anti-patterns in code

- `[ ]` **Research Design Patterns** (1h)
  - `[ ]` Sub-task: List common patterns to detect (Singleton, Factory, Observer, etc.)
  - `[ ]` Sub-task: Define pattern signatures/characteristics
  - `[ ]` Sub-task: Research anti-patterns (God Object, Spaghetti Code, etc.)
  - `[ ]` Sub-task: Document detection strategies

- `[ ]` **Create PatternDetector** (4h)
  - `[ ]` Sub-task: Create `PatternDetector.java` in analysis package
  - `[ ]` Sub-task: Detect Singleton pattern:
    - Private constructor
    - Static instance field
    - Static getInstance() method
  - `[ ]` Sub-task: Detect Factory pattern:
    - Method returning interface/abstract type
    - Creates different concrete instances
  - `[ ]` Sub-task: Detect Builder pattern:
    - Fluent interface (methods returning this)
    - Build() method at end
  - `[ ]` Sub-task: Create `Pattern` DTO with type, confidence, location

- `[ ]` **Implement Anti-Pattern Detection** (2h)
  - `[ ]` Sub-task: Detect God Object (class with too many responsibilities)
  - `[ ]` Sub-task: Detect Feature Envy (method using more of another class than its own)
  - `[ ]` Sub-task: Detect Circular Dependencies between classes
  - `[ ]` Sub-task: Detect Tight Coupling (excessive dependencies)
  - `[ ]` Sub-task: Assign severity levels

- `[ ]` **Create Pattern Report** (1h)
  - `[ ]` Sub-task: Generate summary of detected patterns
  - `[ ]` Sub-task: Show pattern distribution across codebase
  - `[ ]` Sub-task: Highlight anti-patterns for attention
  - `[ ]` Sub-task: Provide refactoring suggestions
  - `[ ]` Sub-task: Create visualization-ready data

**Pattern Detector Example:**
```java
@Service
@Slf4j
public class PatternDetector {
    
    public List<DetectedPattern> detectPatterns(FileInfo fileInfo) {
        log.info("Detecting patterns in: {}", fileInfo.getFilePath());
        
        List<DetectedPattern> patterns = new ArrayList<>();
        
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            // Check for Singleton
            if (isSingleton(classInfo)) {
                patterns.add(DetectedPattern.builder()
                    .patternType("SINGLETON")
                    .className(classInfo.getName())
                    .confidence(0.9)
                    .description("Class follows Singleton pattern")
                    .lineNumber(classInfo.getStartLine())
                    .build());
            }
            
            // Check for Builder
            if (isBuilder(classInfo)) {
                patterns.add(DetectedPattern.builder()
                    .patternType("BUILDER")
                    .className(classInfo.getName())
                    .confidence(0.85)
                    .description("Class implements Builder pattern")
                    .lineNumber(classInfo.getStartLine())
                    .build());
            }
            
            // Check for Factory
            patterns.addAll(detectFactoryMethods(classInfo));
            
            // Check for anti-patterns
            if (isGodObject(classInfo)) {
                patterns.add(DetectedPattern.builder()
                    .patternType("GOD_OBJECT")
                    .className(classInfo.getName())
                    .confidence(0.95)
                    .description("Class has too many responsibilities")
                    .severity("WARNING")
                    .recommendation("Consider splitting into smaller classes")
                    .lineNumber(classInfo.getStartLine())
                    .build());
            }
        }
        
        // Check for circular dependencies
        patterns.addAll(detectCircularDependencies());
        
        log.info("Detected {} patterns", patterns.size());
        return patterns;
    }
    
    private boolean isSingleton(ClassInfo classInfo) {
        boolean hasPrivateConstructor = classInfo.getMethods().stream()
            .anyMatch(m -> m.getName().equals(classInfo.getName()) && 
                          m.getModifiers().contains("private"));
        
        boolean hasStaticInstance = classInfo.getFields().stream()
            .anyMatch(f -> f.isStatic() && 
                          f.getType().equals(classInfo.getName()));
        
        boolean hasGetInstance = classInfo.getMethods().stream()
            .anyMatch(m -> m.getName().toLowerCase().contains("instance") &&
                          m.isStatic() &&
                          m.getReturnType().equals(classInfo.getName()));
        
        return hasPrivateConstructor && hasStaticInstance && hasGetInstance;
    }
    
    private boolean isBuilder(ClassInfo classInfo) {
        // Check for fluent interface
        long fluentMethods = classInfo.getMethods().stream()
            .filter(m -> m.getReturnType().equals(classInfo.getName()))
            .filter(m -> !m.isStatic())
            .count();
        
        // Check for build() method
        boolean hasBuildMethod = classInfo.getMethods().stream()
            .anyMatch(m -> m.getName().equals("build"));
        
        return fluentMethods >= 3 && hasBuildMethod;
    }
    
    private List<DetectedPattern> detectFactoryMethods(ClassInfo classInfo) {
        List<DetectedPattern> factories = new ArrayList<>();
        
        for (MethodInfo method : classInfo.getMethods()) {
            // Factory method characteristics:
            // - Static method
            // - Returns interface or abstract class
            // - Name contains "create" or "newInstance"
            if (method.isStatic() && 
                (method.getName().toLowerCase().contains("create") ||
                 method.getName().toLowerCase().contains("factory") ||
                 method.getName().toLowerCase().startsWith("new"))) {
                
                factories.add(DetectedPattern.builder()
                    .patternType("FACTORY_METHOD")
                    .className(classInfo.getName())
                    .methodName(method.getName())
                    .confidence(0.75)
                    .description("Method appears to be a factory method")
                    .lineNumber(method.getStartLine())
                    .build());
            }
        }
        
        return factories;
    }
    
    private boolean isGodObject(ClassInfo classInfo) {
        // God Object indicators:
        // - Many methods (>30)
        // - Many fields (>15)
        // - Many dependencies (>10)
        // - Many lines (>1000)
        
        int methodCount = classInfo.getMethods().size();
        int fieldCount = classInfo.getFields().size();
        int lineCount = classInfo.getEndLine() - classInfo.getStartLine();
        
        return methodCount > 30 || fieldCount > 15 || lineCount > 1000;
    }
    
    private List<DetectedPattern> detectCircularDependencies() {
        List<DetectedPattern> patterns = new ArrayList<>();
        
        List<List<String>> cycles = graphService.findCircularDependencies();
        
        for (List<String> cycle : cycles) {
            patterns.add(DetectedPattern.builder()
                .patternType("CIRCULAR_DEPENDENCY")
                .description("Circular dependency detected: " + String.join(" -> ", cycle))
                .severity("ERROR")
                .recommendation("Refactor to break circular dependency")
                .involvedClasses(cycle)
                .build());
        }
        
        return patterns;
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test Singleton detection
  - `[ ]` Sub-task: Test Builder detection
  - `[ ]` Sub-task: Test Factory detection
  - `[ ]` Sub-task: Test God Object detection
  - `[ ]` Sub-task: Test circular dependency detection

---

### Day 39: Documentation Generation
**Goal:** Auto-generate documentation from code structure

- `[ ]` **Create DocumentationGenerator** (4h)
  - `[ ]` Sub-task: Create `DocumentationGenerator.java` in analysis package
  - `[ ]` Sub-task: Generate class documentation:
    - Class purpose (from JavaDoc or infer from name)
    - Public methods with signatures
    - Dependencies and relationships
    - Usage examples (from tests if available)
  - `[ ]` Sub-task: Generate package documentation:
    - Package purpose
    - List of classes
    - Package dependencies
  - `[ ]` Sub-task: Output as Markdown format

- `[ ]` **Extract and Enhance JavaDoc** (2h)
  - `[ ]` Sub-task: Parse existing JavaDoc comments
  - `[ ]` Sub-task: Extract @param, @return, @throws tags
  - `[ ]` Sub-task: Identify missing JavaDoc
  - `[ ]` Sub-task: Generate placeholder docs for undocumented code
  - `[ ]` Sub-task: Format for readability

- `[ ]` **Generate API Documentation** (2h)
  - `[ ]` Sub-task: Document all public methods
  - `[ ]` Sub-task: Include method signatures and parameters
  - `[ ]` Sub-task: Include return types
  - `[ ]` Sub-task: Include exceptions thrown
  - `[ ]` Sub-task: Group by class and package
  - `[ ]` Sub-task: Generate table of contents

**Documentation Generator Example:**
```java
@Service
@Slf4j
public class DocumentationGenerator {
    
    public String generateClassDocumentation(ClassInfo classInfo) {
        StringBuilder doc = new StringBuilder();
        
        // Class header
        doc.append("# ").append(classInfo.getName()).append("\n\n");
        
        // Package
        doc.append("**Package:** `").append(classInfo.getPackageName()).append("`\n\n");
        
        // JavaDoc if available
        if (classInfo.getJavadoc() != null) {
            doc.append("## Description\n\n");
            doc.append(classInfo.getJavadoc()).append("\n\n");
        }
        
        // Class hierarchy
        if (classInfo.getParentClass() != null) {
            doc.append("**Extends:** `").append(classInfo.getParentClass()).append("`\n\n");
        }
        
        if (!classInfo.getInterfaces().isEmpty()) {
            doc.append("**Implements:** ");
            doc.append(String.join(", ", classInfo.getInterfaces().stream()
                .map(i -> "`" + i + "`")
                .collect(Collectors.toList())));
            doc.append("\n\n");
        }
        
        // Public methods
        doc.append("## Public Methods\n\n");
        
        List<MethodInfo> publicMethods = classInfo.getMethods().stream()
            .filter(m -> m.getModifiers().contains("public"))
            .sorted(Comparator.comparing(MethodInfo::getName))
            .collect(Collectors.toList());
        
        for (MethodInfo method : publicMethods) {
            doc.append("### ").append(method.getName()).append("\n\n");
            doc.append("```java\n");
            doc.append(method.getSignature()).append("\n");
            doc.append("```\n\n");
            
            if (method.getJavadoc() != null) {
                doc.append(method.getJavadoc()).append("\n\n");
            } else {
                doc.append("*No documentation available*\n\n");
            }
            
            // Parameters
            if (!method.getParameters().isEmpty()) {
                doc.append("**Parameters:**\n");
                for (String param : method.getParameters()) {
                    doc.append("- `").append(param).append("`\n");
                }
                doc.append("\n");
            }
            
            // Return type
            if (!method.getReturnType().equals("void")) {
                doc.append("**Returns:** `").append(method.getReturnType()).append("`\n\n");
            }
        }
        
        // Dependencies
        doc.append("## Dependencies\n\n");
        List<String> dependencies = graphService.getDependencies(classInfo.getFullyQualifiedName());
        if (dependencies.isEmpty()) {
            doc.append("*No dependencies*\n\n");
        } else {
            for (String dep : dependencies) {
                doc.append("- `").append(dep).append("`\n");
            }
            doc.append("\n");
        }
        
        return doc.toString();
    }
    
    public String generatePackageDocumentation(String packageName, 
                                               List<ClassInfo> classes) {
        StringBuilder doc = new StringBuilder();
        
        doc.append("# Package: ").append(packageName).append("\n\n");
        
        doc.append("## Classes\n\n");
        
        for (ClassInfo classInfo : classes) {
            doc.append("### ").append(classInfo.getName()).append("\n\n");
            
            if (classInfo.getJavadoc() != null) {
                // Get first sentence of JavaDoc
                String summary = classInfo.getJavadoc().split("\\.")[0] + ".";
                doc.append(summary).append("\n\n");
            }
            
            doc.append("**Location:** `").append(classInfo.getFilePath()).append("`\n\n");
        }
        
        return doc.toString();
    }
    
    public void generateProjectDocumentation(String projectPath, String outputPath) {
        log.info("Generating project documentation...");
        
        try {
            // Create output directory
            Path docPath = Paths.get(outputPath);
            Files.createDirectories(docPath);
            
            // Generate docs for each package
            Map<String, List<ClassInfo>> classesByPackage = groupClassesByPackage();
            
            for (Map.Entry<String, List<ClassInfo>> entry : classesByPackage.entrySet()) {
                String packageName = entry.getKey();
                List<ClassInfo> classes = entry.getValue();
                
                // Generate package doc
                String packageDoc = generatePackageDocumentation(packageName, classes);
                String fileName = packageName.replace(".", "_") + ".md";
                Files.writeString(docPath.resolve(fileName), packageDoc);
                
                // Generate doc for each class
                for (ClassInfo classInfo : classes) {
                    String classDoc = generateClassDocumentation(classInfo);
                    String classFileName = classInfo.getName() + ".md";
                    Files.writeString(docPath.resolve(classFileName), classDoc);
                }
            }
            
            // Generate index
            String index = generateIndex(classesByPackage);
            Files.writeString(docPath.resolve("README.md"), index);
            
            log.info("Documentation generated in: {}", outputPath);
            
        } catch (IOException e) {
            log.error("Failed to generate documentation", e);
            throw new RuntimeException("Documentation generation failed", e);
        }
    }
}
```

- `[ ]` **Create Tests** (1h)
  - `[ ]` Sub-task: Test class documentation generation
  - `[ ]` Sub-task: Test package documentation generation
  - `[ ]` Sub-task: Test with missing JavaDoc
  - `[ ]` Sub-task: Test output format
  - `[ ]` Sub-task: Verify markdown validity

---

### Day 40: Analysis API & Visualization
**Goal:** Create API endpoints for analysis features and prepare data for visualization

- `[ ]` **Create AnalysisController** (3h)
  - `[ ]` Sub-task: Create `AnalysisController.java` with @RestController
  - `[ ]` Sub-task: Add `GET /api/analysis/complexity/{filePath}` endpoint
  - `[ ]` Sub-task: Add `GET /api/analysis/smells` endpoint
  - `[ ]` Sub-task: Add `GET /api/analysis/dead-code` endpoint
  - `[ ]` Sub-task: Add `GET /api/analysis/patterns` endpoint
  - `[ ]` Sub-task: Add `POST /api/analysis/full` for complete analysis
  - `[ ]` Sub-task: Add proper error handling and validation

- `[ ]` **Create Analysis DTOs** (1h)
  - `[ ]` Sub-task: Create `AnalysisRequest.java`
  - `[ ]` Sub-task: Create `AnalysisResponse.java`
  - `[ ]` Sub-task: Create `ComplexityReport.java`
  - `[ ]` Sub-task: Create `SmellReport.java`
  - `[ ]` Sub-task: Add JSON serialization annotations

- `[ ]` **Prepare Visualization Data** (3h)
  - `[ ]` Sub-task: Create data structures for charts:
    - Complexity distribution histogram
    - Code smells by category
    - Dead code by file
    - Pattern distribution
  - `[ ]` Sub-task: Calculate statistics (averages, totals, percentages)
  - `[ ]` Sub-task: Format for charting libraries (Chart.js, D3.js)
  - `[ ]` Sub-task: Add color coding for severity levels
  - `[ ]` Sub-task: Create visualization endpoint

- `[ ]` **Add Report Export** (1h)
  - `[ ]` Sub-task: Support JSON export
  - `[ ]` Sub-task: Support CSV export
  - `[ ]` Sub-task: Support HTML export
  - `[ ]` Sub-task: Support PDF export (optional)
  - `[ ]` Sub-task: Add download endpoints

**Analysis Controller Example:**
```java
@RestController
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisController {
    private final ComplexityAnalyzer complexityAnalyzer;
    private final CodeSmellDetector smellDetector;
    private final DeadCodeDetector deadCodeDetector;
    private final PatternDetector patternDetector;
    
    @GetMapping("/complexity/{fileId}")
    public ResponseEntity<ComplexityReport> analyzeComplexity(
            @PathVariable Long fileId) {
        
        log.info("Analyzing complexity for file: {}", fileId);
        
        try {
            FileInfo fileInfo = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));
            
            List<ComplexityMetrics> metrics = new ArrayList<>();
            
            for (ClassInfo classInfo : fileInfo.getClasses()) {
                for (MethodInfo method : classInfo.getMethods()) {
                    ComplexityMetrics methodMetrics = 
                        complexityAnalyzer.analyzeMethod(method);
                    metrics.add(methodMetrics);
                }
            }
            
            ComplexityReport report = ComplexityReport.builder()
                .filePath(fileInfo.getFilePath())
                .metrics(metrics)
                .averageComplexity(calculateAverage(metrics))
                .highComplexityCount(countHighComplexity(metrics))
                .totalMethods(metrics.size())
                .build();
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Complexity analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/smells")
    public ResponseEntity<SmellReport> analyzeCodeSmells(
            @RequestParam(required = false) String filePath,
            @RequestParam(defaultValue = "WARNING") String minSeverity) {
        
        log.info("Analyzing code smells (minSeverity: {})", minSeverity);
        
        try {
            List<FileInfo> files;
            if (filePath != null) {
                files = List.of(fileRepository.findByPath(filePath));
            } else {
                files = fileRepository.findAll();
            }
            
            List<CodeSmell> allSmells = new ArrayList<>();
            
            for (FileInfo file : files) {
                List<CodeSmell> fileSmells = smellDetector.detectSmells(file);
                allSmells.addAll(fileSmells);
            }
            
            // Filter by severity
            List<CodeSmell> filtered = filterBySeverity(allSmells, minSeverity);
            
            SmellReport report = SmellReport.builder()
                .smells(filtered)
                .totalCount(filtered.size())
                .byType(groupByType(filtered))
                .bySeverity(groupBySeverity(filtered))
                .topFiles(getTopSmellFiles(filtered, 10))
                .build();
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Code smell analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/dead-code")
    public ResponseEntity<DeadCodeReport> analyzeDeadCode(
            @RequestParam(defaultValue = "0.7") double minConfidence) {
        
        log.info("Analyzing dead code (minConfidence: {})", minConfidence);
        
        try {
            DeadCodeReport report = deadCodeDetector.findDeadCode(".");
            
            // Filter by confidence
            report.setUnusedMethods(filterByConfidence(
                report.getUnusedMethods(), minConfidence));
            report.setUnusedClasses(filterByConfidence(
                report.getUnusedClasses(), minConfidence));
            
            // Calculate statistics
            report.setTotalDeadLines(calculateDeadLines(report));
            report.setEstimatedCleanupTime(estimateCleanupTime(report));
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Dead code analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/patterns")
    public ResponseEntity<PatternReport> analyzePatterns() {
        log.info("Analyzing design patterns");
        
        try {
            List<FileInfo> allFiles = fileRepository.findAll();
            List<DetectedPattern> allPatterns = new ArrayList<>();
            
            for (FileInfo file : allFiles) {
                List<DetectedPattern> patterns = patternDetector.detectPatterns(file);
                allPatterns.addAll(patterns);
            }
            
            PatternReport report = PatternReport.builder()
                .patterns(allPatterns)
                .totalCount(allPatterns.size())
                .byType(groupPatternsByType(allPatterns))
                .goodPatterns(filterGoodPatterns(allPatterns))
                .antiPatterns(filterAntiPatterns(allPatterns))
                .build();
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Pattern analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/full")
    public ResponseEntity<FullAnalysisReport> runFullAnalysis(
            @RequestBody AnalysisRequest request) {
        
        log.info("Running full analysis on: {}", request.getProjectPath());
        
        try {
            FullAnalysisReport report = new FullAnalysisReport();
            
            // Run all analyses
            report.setComplexity(analyzeAllComplexity());
            report.setSmells(analyzeAllSmells());
            report.setDeadCode(deadCodeDetector.findDeadCode(request.getProjectPath()));
            report.setPatterns(analyzeAllPatterns());
            
            // Generate summary
            report.setSummary(generateSummary(report));
            
            // Calculate quality score
            report.setQualityScore(calculateQualityScore(report));
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Full analysis failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/visualization/complexity")
    public ResponseEntity<Map<String, Object>> getComplexityVisualization() {
        // Return data formatted for charts
        Map<String, Object> vizData = new HashMap<>();
        
        // Histogram data
        List<ComplexityMetrics> allMetrics = getAllComplexityMetrics();
        Map<String, Long> histogram = allMetrics.stream()
            .collect(Collectors.groupingBy(
                ComplexityMetrics::getComplexityLevel,
                Collectors.counting()));
        
        vizData.put("histogram", histogram);
        vizData.put("average", calculateAverageComplexity(allMetrics));
        vizData.put("max", findMaxComplexity(allMetrics));
        
        return ResponseEntity.ok(vizData);
    }
}
```

- `[ ]` **Create API Tests** (1h)
  - `[ ]` Sub-task: Test all analysis endpoints
  - `[ ]` Sub-task: Test filtering and parameters
  - `[ ]` Sub-task: Test error handling
  - `[ ]` Sub-task: Test visualization data format
  - `[ ]` Sub-task: Test export functionality

---

## Week 8: Integration & Polish (Sprint 4, Part 1)

### Day 41: Analysis Integration Testing
**Goal:** Test all analysis features working together

- `[ ]` **Create Integration Test Suite** (3h)
  - `[ ]` Sub-task: Test full analysis pipeline
  - `[ ]` Sub-task: Test with Firestick's own codebase
  - `[ ]` Sub-task: Test with sample legacy project
  - `[ ]` Sub-task: Verify all metrics are calculated correctly
  - `[ ]` Sub-task: Verify reports are generated properly

- `[ ]` **Performance Testing** (2h)
  - `[ ]` Sub-task: Test analysis performance with large codebase
  - `[ ]` Sub-task: Measure time for each analysis type
  - `[ ]` Sub-task: Optimize slow operations
  - `[ ]` Sub-task: Add progress reporting for long analyses
  - `[ ]` Sub-task: Test concurrent analysis requests

- `[ ]` **Cross-Feature Testing** (2h)
  - `[ ]` Sub-task: Test analysis results used in search
  - `[ ]` Sub-task: Test dead code detection with dependency graph
  - `[ ]` Sub-task: Test pattern detection with complexity metrics
  - `[ ]` Sub-task: Verify data consistency across features

- `[ ]` **Error Handling** (1h)
  - `[ ]` Sub-task: Test with malformed code
  - `[ ]` Sub-task: Test with missing files
  - `[ ]` Sub-task: Test with very large files
  - `[ ]` Sub-task: Verify graceful degradation
  - `[ ]` Sub-task: Test recovery from errors

---

### Day 42: Documentation & Code Review
**Goal:** Document analysis features and review code quality

- `[ ]` **Write Analysis Documentation** (3h)
  - `[ ]` Sub-task: Document all analysis metrics and their meanings
  - `[ ]` Sub-task: Document thresholds and how they're determined
  - `[ ]` Sub-task: Create user guide for analysis features
  - `[ ]` Sub-task: Document API endpoints with examples
  - `[ ]` Sub-task: Add troubleshooting guide
  - `[ ]` Sub-task: Create analysis best practices guide

- `[ ]` **Code Review & Refactoring** (3h)
  - `[ ]` Sub-task: Review all analysis code for quality
  - `[ ]` Sub-task: Add missing JavaDoc comments
  - `[ ]` Sub-task: Refactor complex methods
  - `[ ]` Sub-task: Ensure consistent coding style
  - `[ ]` Sub-task: Remove any dead code or TODOs

- `[ ]` **Create Analysis Examples** (2h)
  - `[ ]` Sub-task: Create sample analysis reports
  - `[ ]` Sub-task: Document how to interpret results
  - `[ ]` Sub-task: Add screenshots or visualizations
  - `[ ]` Sub-task: Create video walkthrough (optional)
  - `[ ]` Sub-task: Add FAQ section

---

## Phase 4 Success Criteria

Before moving to Phase 5, verify ALL of the following:

### Functional Requirements
- ⬜ Complexity analysis calculates metrics accurately
- ⬜ Code smell detection identifies common issues
- ⬜ Dead code detector finds unused code reliably
- ⬜ Pattern detector identifies design patterns correctly
- ⬜ Documentation generator creates useful docs
- ⬜ All analysis APIs work correctly
- ⬜ Reports are accurate and helpful
- ⬜ Visualization data is properly formatted

### Analysis Quality Requirements
- ⬜ Complexity thresholds are reasonable
- ⬜ Code smells are actionable
- ⬜ Dead code confidence scores are accurate
- ⬜ Pattern detection has <20% false positives
- ⬜ Generated documentation is readable

### Performance Requirements
- ⬜ Complexity analysis completes in <10 seconds for 1000 methods
- ⬜ Dead code detection completes in <30 seconds for 10,000 files
- ⬜ Pattern detection completes in <20 seconds
- ⬜ Full analysis completes in <2 minutes for medium project
- ⬜ Memory usage is reasonable

### Code Quality Requirements
- ⬜ All unit tests pass
- ⬜ All integration tests pass
- ⬜ Test coverage > 70%
- ⬜ No critical bugs
- ⬜ Code is well-documented
- ⬜ APIs are documented

### Documentation Requirements
- ⬜ All metrics are documented
- ⬜ User guide exists
- ⬜ API documentation is complete
- ⬜ Examples are provided
- ⬜ Troubleshooting guide exists

---

## Common Issues & Troubleshooting

### Issue: Complexity calculation seems wrong
**Solution:**
- Review cyclomatic complexity algorithm
- Check that all control structures are counted
- Verify nesting depth calculation
- Compare with known complexity tools
- Add debug logging to see what's being counted

### Issue: Too many false positives in dead code detection
**Solution:**
- Review exclusion rules (annotations, entry points)
- Check for reflection usage
- Add configuration for project-specific exclusions
- Lower confidence threshold
- Review graph completeness

### Issue: Pattern detection misses obvious patterns
**Solution:**
- Review pattern signatures
- Add more pattern variants
- Check JavaParser AST extraction
- Add logging to see why patterns are missed
- Adjust confidence thresholds

### Issue: Generated documentation is unhelpful
**Solution:**
- Improve JavaDoc extraction
- Add more context to generated docs
- Include usage examples from tests
- Better formatting for readability
- Add more inferencing for missing docs

### Issue: Analysis is too slow
**Solution:**
- Add caching for analyzed files
- Process files in parallel
- Skip unchanged files (incremental analysis)
- Optimize AST traversal
- Profile to find bottlenecks

### Issue: Out of memory during analysis
**Solution:**
- Process files in batches
- Clear caches periodically
- Increase JVM heap size
- Optimize data structures
- Use streaming where possible

---

## Phase 4 Sprint Review Checklist

**Demo Items:**
- [ ] Show complexity analysis on real code
- [ ] Show code smell detection results
- [ ] Show dead code detection report
- [ ] Show design pattern detection
- [ ] Show anti-pattern detection
- [ ] Show generated documentation
- [ ] Show analysis APIs in action
- [ ] Show visualization data
- [ ] Show full analysis report
- [ ] Show before/after metrics

**Retrospective Questions:**
- What analysis features are most valuable?
- What analysis results are most surprising?
- How accurate are the detections?
- What additional analyses would be helpful?
- How can we improve result presentation?

---
---

# Phase 5: Web UI (Weeks 9-10)

**Status:** Not Started  
**Goal:** Build modern web interface for search, analysis, and code exploration  
**Team:** Frontend Developer + Backend Developer  
**Duration:** Dec 9 - Dec 22, 2025 (2 weeks, Sprint 4-5)  
**Dependencies:** Phase 3 (Search API) and Phase 4 (Analysis API) must be complete

---

## Phase 5 Overview

### What We're Building
A modern, responsive web interface that provides:
1. **Search Interface** - Intuitive search with auto-complete and filters
2. **Code Viewer** - Syntax-highlighted code display with navigation
3. **Analysis Dashboard** - Visual displays of metrics and reports
4. **Dependency Graphs** - Interactive graph visualization
5. **Documentation Viewer** - Browse generated documentation
6. **Settings & Configuration** - User preferences and indexing controls

### Why This Matters
The UI is the face of Firestick. A good interface means:
- Developers can work faster
- Results are easy to understand
- Complex data becomes accessible
- Features are discoverable
- Application is pleasant to use

### Technology Stack
- **Frontend Framework:** React 18+ with hooks
- **Build Tool:** Vite for fast development
- **Styling:** Tailwind CSS or Material-UI
- **Code Highlighting:** Monaco Editor (VS Code editor)
- **Charts:** Chart.js or Recharts
- **Graph Visualization:** Cytoscape.js or React Flow
- **HTTP Client:** Axios
- **Routing:** React Router

---

## Week 9: UI Foundation & Core Components (Sprint 4, Part 2)

### Day 43: React Project Setup
**Goal:** Set up React development environment and project structure

- `[ ]` **Initialize React Project** (2h)
  - `[ ]` Sub-task: Install Node.js 18+ if not present
  - `[ ]` Sub-task: Create React app with Vite: `npm create vite@latest firestick-ui -- --template react`
  - `[ ]` Sub-task: Navigate to project: `cd firestick-ui`
  - `[ ]` Sub-task: Install dependencies: `npm install`
  - `[ ]` Sub-task: Test dev server: `npm run dev`
  - `[ ]` Sub-task: Verify app runs on http://localhost:5173

- `[ ]` **Install Required Dependencies** (1h)
  - `[ ]` Sub-task: Install React Router: `npm install react-router-dom`
  - `[ ]` Sub-task: Install Axios: `npm install axios`
  - `[ ]` Sub-task: Install UI library: `npm install @mui/material @emotion/react @emotion/styled` (Material-UI)
  - `[ ]` Sub-task: OR install Tailwind: `npm install -D tailwindcss postcss autoprefixer`
  - `[ ]` Sub-task: Install icons: `npm install @mui/icons-material` or `npm install react-icons`
  - `[ ]` Sub-task: Install Monaco Editor: `npm install @monaco-editor/react`
  - `[ ]` Sub-task: Install chart library: `npm install recharts`

- `[ ]` **Set Up Project Structure** (2h)
  - `[ ]` Sub-task: Create folder structure:
    ```
    src/
      components/     # Reusable UI components
      pages/         # Page components
      services/      # API service layer
      hooks/         # Custom React hooks
      utils/         # Utility functions
      styles/        # Global styles
      contexts/      # React contexts
    ```
  - `[ ]` Sub-task: Create basic component files (empty for now)
  - `[ ]` Sub-task: Set up routing skeleton
  - `[ ]` Sub-task: Create API service configuration

- `[ ]` **Configure API Integration** (2h)
  - `[ ]` Sub-task: Create `src/services/api.js` with Axios configuration
  - `[ ]` Sub-task: Set base URL to Spring Boot backend (http://localhost:8080)
  - `[ ]` Sub-task: Add request/response interceptors
  - `[ ]` Sub-task: Create service modules for each API area:
    - `searchService.js`
    - `analysisService.js`
    - `indexingService.js`
  - `[ ]` Sub-task: Add error handling wrapper
  - `[ ]` Sub-task: Test API connection

**API Service Example:**
```javascript
// src/services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error.response || error);
    return Promise.reject(error);
  }
);

export default apiClient;
```

```javascript
// src/services/searchService.js
import apiClient from './api';

export const searchService = {
  search: async (query, topK = 10, filter = null) => {
    const response = await apiClient.post('/search', {
      query,
      topK,
      filter,
      includeContext: true,
    });
    return response;
  },

  findSymbol: async (name, limit = 10) => {
    const response = await apiClient.get(`/search/symbol/${name}`, {
      params: { limit },
    });
    return response;
  },

  getSuggestions: async (prefix) => {
    const response = await apiClient.get('/search/suggestions', {
      params: { prefix, limit: 10 },
    });
    return response;
  },

  findSimilar: async (code) => {
    const response = await apiClient.post('/search/similar', {
      code,
    });
    return response;
  },
};
```

- `[ ]` **Create Basic Layout** (1h)
  - `[ ]` Sub-task: Create `Layout.jsx` component with header, sidebar, main content
  - `[ ]` Sub-task: Add navigation menu
  - `[ ]` Sub-task: Add responsive design
  - `[ ]` Sub-task: Test layout on different screen sizes

---

### Day 44: Search Interface - Part 1
**Goal:** Create search input and results display

- `[ ]` **Create Search Page Component** (2h)
  - `[ ]` Sub-task: Create `src/pages/SearchPage.jsx`
  - `[ ]` Sub-task: Add search input with submit button
  - `[ ]` Sub-task: Add state management for query and results
  - `[ ]` Sub-task: Wire up to search API
  - `[ ]` Sub-task: Add loading state
  - `[ ]` Sub-task: Add error handling
  - `[ ]` Sub-task: Test basic search functionality

- `[ ]` **Implement Auto-Complete** (3h)
  - `[ ]` Sub-task: Create `SearchInput.jsx` component
  - `[ ]` Sub-task: Add debounced input handler (300ms delay)
  - `[ ]` Sub-task: Call suggestions API as user types
  - `[ ]` Sub-task: Display dropdown with suggestions
  - `[ ]` Sub-task: Allow keyboard navigation (up/down arrows)
  - `[ ]` Sub-task: Select suggestion on click or Enter
  - `[ ]` Sub-task: Style suggestions dropdown

**SearchInput Component Example:**
```javascript
// src/components/SearchInput.jsx
import React, { useState, useEffect } from 'react';
import { TextField, Autocomplete, CircularProgress } from '@mui/material';
import { searchService } from '../services/searchService';
import { useDebounce } from '../hooks/useDebounce';

export const SearchInput = ({ onSearch, initialValue = '' }) => {
  const [query, setQuery] = useState(initialValue);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    if (debouncedQuery && debouncedQuery.length >= 3) {
      fetchSuggestions(debouncedQuery);
    } else {
      setSuggestions([]);
    }
  }, [debouncedQuery]);

  const fetchSuggestions = async (prefix) => {
    setLoading(true);
    try {
      const results = await searchService.getSuggestions(prefix);
      setSuggestions(results);
    } catch (error) {
      console.error('Failed to fetch suggestions:', error);
      setSuggestions([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (query.trim()) {
      onSearch(query);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSearch();
    }
  };

  return (
    <Autocomplete
      freeSolo
      options={suggestions}
      loading={loading}
      value={query}
      onInputChange={(event, newValue) => setQuery(newValue)}
      onChange={(event, newValue) => {
        if (newValue) {
          setQuery(newValue);
          onSearch(newValue);
        }
      }}
      renderInput={(params) => (
        <TextField
          {...params}
          label="Search code..."
          variant="outlined"
          fullWidth
          onKeyPress={handleKeyPress}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <>
                {loading ? <CircularProgress size={20} /> : null}
                {params.InputProps.endAdornment}
              </>
            ),
          }}
        />
      )}
    />
  );
};
```

```javascript
// src/hooks/useDebounce.js
import { useState, useEffect } from 'react';

export const useDebounce = (value, delay) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};
```

- `[ ]` **Create Search Filters** (2h)
  - `[ ]` Sub-task: Create `SearchFilters.jsx` component
  - `[ ]` Sub-task: Add filter for file path pattern
  - `[ ]` Sub-task: Add filter for code type (class, method, field)
  - `[ ]` Sub-task: Add filter for package
  - `[ ]` Sub-task: Add "Advanced" toggle to show/hide filters
  - `[ ]` Sub-task: Apply filters to search

- `[ ]` **Add Search History** (1h)
  - `[ ]` Sub-task: Store recent searches in localStorage
  - `[ ]` Sub-task: Display recent searches below input
  - `[ ]` Sub-task: Allow clicking recent search to re-run
  - `[ ]` Sub-task: Add clear history button
  - `[ ]` Sub-task: Limit to 10 most recent

---

### Day 45: Search Interface - Part 2
**Goal:** Display search results with highlighting and context

- `[ ]` **Create SearchResults Component** (3h)
  - `[ ]` Sub-task: Create `SearchResults.jsx` component
  - `[ ]` Sub-task: Display list of results with key information:
    - Code snippet with highlighting
    - File path and line numbers
    - Match score/relevance
    - Code type (class, method, etc.)
  - `[ ]` Sub-task: Add "No results" state with helpful message
  - `[ ]` Sub-task: Add loading skeleton while searching
  - `[ ]` Sub-task: Make results clickable to view details

- `[ ]` **Implement Result Card** (2h)
  - `[ ]` Sub-task: Create `ResultCard.jsx` component
  - `[ ]` Sub-task: Display code snippet with syntax highlighting
  - `[ ]` Sub-task: Show file path as clickable breadcrumb
  - `[ ]` Sub-task: Display context information (callers, callees)
  - `[ ]` Sub-task: Add "View Full File" button
  - `[ ]` Sub-task: Add "Copy" button for code
  - `[ ]` Sub-task: Style card with hover effects

**ResultCard Component Example:**
```javascript
// src/components/ResultCard.jsx
import React from 'react';
import { 
  Card, 
  CardContent, 
  Typography, 
  Chip, 
  IconButton,
  Box 
} from '@mui/material';
import { 
  ContentCopy, 
  OpenInNew,
  Code 
} from '@mui/icons-material';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

export const ResultCard = ({ result, onViewFile }) => {
  const copyToClipboard = () => {
    navigator.clipboard.writeText(result.content);
  };

  const handleViewFile = () => {
    onViewFile(result.filePath, result.startLine);
  };

  return (
    <Card sx={{ mb: 2, '&:hover': { boxShadow: 3 } }}>
      <CardContent>
        {/* Header */}
        <Box display="flex" justifyContent="space-between" alignItems="start" mb={1}>
          <Box>
            <Typography variant="h6" component="div">
              {result.className && `${result.className}.`}{result.methodName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {result.filePath} : {result.startLine}-{result.endLine}
            </Typography>
          </Box>
          <Box>
            <Chip 
              label={`Score: ${result.score.toFixed(2)}`} 
              size="small" 
              color="primary" 
            />
            {result.searchType && (
              <Chip 
                label={result.searchType} 
                size="small" 
                sx={{ ml: 1 }}
              />
            )}
          </Box>
        </Box>

        {/* Code Preview */}
        <Box sx={{ mb: 2, position: 'relative' }}>
          <SyntaxHighlighter 
            language="java" 
            style={vscDarkPlus}
            showLineNumbers
            startingLineNumber={result.startLine}
            customStyle={{ 
              borderRadius: '4px',
              fontSize: '0.875rem',
              maxHeight: '300px',
            }}
          >
            {result.highlightedContent || result.content}
          </SyntaxHighlighter>
          
          {/* Copy button */}
          <IconButton
            size="small"
            onClick={copyToClipboard}
            sx={{ position: 'absolute', top: 8, right: 8 }}
          >
            <ContentCopy fontSize="small" />
          </IconButton>
        </Box>

        {/* Context Information */}
        {(result.callers?.length > 0 || result.callees?.length > 0) && (
          <Box mb={1}>
            {result.callers?.length > 0 && (
              <Typography variant="body2" color="text.secondary">
                Called by: {result.callers.slice(0, 3).join(', ')}
                {result.callers.length > 3 && ` +${result.callers.length - 3} more`}
              </Typography>
            )}
            {result.callees?.length > 0 && (
              <Typography variant="body2" color="text.secondary">
                Calls: {result.callees.slice(0, 3).join(', ')}
                {result.callees.length > 3 && ` +${result.callees.length - 3} more`}
              </Typography>
            )}
          </Box>
        )}

        {/* Actions */}
        <Box display="flex" gap={1}>
          <IconButton size="small" onClick={handleViewFile} title="View full file">
            <OpenInNew fontSize="small" />
          </IconButton>
        </Box>
      </CardContent>
    </Card>
  );
};
```

- `[ ]` **Add Pagination** (2h)
  - `[ ]` Sub-task: Implement "Load More" button
  - `[ ]` Sub-task: OR implement infinite scroll
  - `[ ]` Sub-task: Show results count (e.g., "Showing 10 of 45 results")
  - `[ ]` Sub-task: Add "Back to Top" button
  - `[ ]` Sub-task: Preserve scroll position on navigation

- `[ ]` **Add Result Sorting** (1h)
  - `[ ]` Sub-task: Add sort dropdown (Relevance, Name, File Path)
  - `[ ]` Sub-task: Implement client-side sorting
  - `[ ]` Sub-task: Remember sort preference
  - `[ ]` Sub-task: Update URL params with sort option

---

### Day 46: Code Viewer Component
**Goal:** Create full-featured code viewer with Monaco Editor

- `[ ]` **Integrate Monaco Editor** (3h)
  - `[ ]` Sub-task: Create `CodeViewer.jsx` component
  - `[ ]` Sub-task: Import and configure Monaco Editor
  - `[ ]` Sub-task: Set Java language mode
  - `[ ]` Sub-task: Set VS Code Dark+ theme
  - `[ ]` Sub-task: Make editor read-only
  - `[ ]` Sub-task: Configure line numbers and minimap
  - `[ ]` Sub-task: Set appropriate height/width

- `[ ]` **Add File Loading** (2h)
  - `[ ]` Sub-task: Create API endpoint to fetch full file content
  - `[ ]` Sub-task: Load file content when viewer opens
  - `[ ]` Sub-task: Show loading state
  - `[ ]` Sub-task: Handle file not found errors
  - `[ ]` Sub-task: Cache loaded files in memory

- `[ ]` **Implement Code Navigation** (2h)
  - `[ ]` Sub-task: Jump to specific line number
  - `[ ]` Sub-task: Highlight search match line
  - `[ ]` Sub-task: Add breadcrumb navigation (package > class > method)
  - `[ ]` Sub-task: Add "Go to Definition" on symbol click (if available)
  - `[ ]` Sub-task: Add method outline/list in sidebar

- `[ ]` **Add Code Actions** (1h)
  - `[ ]` Sub-task: Add copy button
  - `[ ]` Sub-task: Add download button
  - `[ ]` Sub-task: Add open in external editor button
  - `[ ]` Sub-task: Add syntax theme selector
  - `[ ]` Sub-task: Add font size controls

**CodeViewer Component Example:**
```javascript
// src/components/CodeViewer.jsx
import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { Box, CircularProgress, Alert } from '@mui/material';

export const CodeViewer = ({ 
  filePath, 
  highlightLine = null,
  readOnly = true 
}) => {
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadFile();
  }, [filePath]);

  const loadFile = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // API call to get file content
      const response = await fetch(`http://localhost:8080/api/files/content?path=${encodeURIComponent(filePath)}`);
      
      if (!response.ok) {
        throw new Error('Failed to load file');
      }
      
      const content = await response.text();
      setCode(content);
    } catch (err) {
      setError(err.message);
      console.error('Error loading file:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEditorMount = (editor) => {
    if (highlightLine) {
      // Scroll to and highlight line
      editor.revealLineInCenter(highlightLine);
      editor.setSelection({
        startLineNumber: highlightLine,
        startColumn: 1,
        endLineNumber: highlightLine,
        endColumn: 1,
      });
      
      // Add decoration (highlight)
      editor.deltaDecorations([], [
        {
          range: {
            startLineNumber: highlightLine,
            startColumn: 1,
            endLineNumber: highlightLine,
            endColumn: 1,
          },
          options: {
            isWholeLine: true,
            className: 'highlighted-line',
            glyphMarginClassName: 'highlighted-line-glyph',
          },
        },
      ]);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height={600}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error">
        Failed to load file: {error}
      </Alert>
    );
  }

  return (
    <Box>
      <Editor
        height="600px"
        language="java"
        theme="vs-dark"
        value={code}
        onMount={handleEditorMount}
        options={{
          readOnly,
          minimap: { enabled: true },
          scrollBeyondLastLine: false,
          fontSize: 14,
          lineNumbers: 'on',
          renderLineHighlight: 'all',
          automaticLayout: true,
        }}
      />
    </Box>
  );
};
```

---

### Day 47: Analysis Dashboard - Part 1
**Goal:** Create dashboard to display analysis results

- `[ ]` **Create Dashboard Page** (2h)
  - `[ ]` Sub-task: Create `AnalysisDashboard.jsx` page component
  - `[ ]` Sub-task: Add grid layout for different widgets
  - `[ ]` Sub-task: Create dashboard header with refresh button
  - `[ ]` Sub-task: Add date/time of last analysis
  - `[ ]` Sub-task: Make layout responsive

- `[ ]` **Create Complexity Metrics Widget** (3h)
  - `[ ]` Sub-task: Create `ComplexityWidget.jsx` component
  - `[ ]` Sub-task: Fetch complexity data from API
  - `[ ]` Sub-task: Display summary statistics (avg, max, total methods)
  - `[ ]` Sub-task: Create bar chart showing complexity distribution
  - `[ ]` Sub-task: Show top 10 most complex methods in table
  - `[ ]` Sub-task: Make chart interactive (click to see details)

- `[ ]` **Create Code Smells Widget** (2h)
  - `[ ]` Sub-task: Create `CodeSmellsWidget.jsx` component
  - `[ ]` Sub-task: Fetch code smell data from API
  - `[ ]` Sub-task: Display total smells by severity
  - `[ ]` Sub-task: Create pie chart showing smell types
  - `[ ]` Sub-task: Show top 5 files with most smells
  - `[ ]` Sub-task: Add severity color coding

- `[ ]` **Add Quality Score Card** (1h)
  - `[ ]` Sub-task: Create `QualityScoreCard.jsx` component
  - `[ ]` Sub-task: Calculate overall quality score (0-100)
  - `[ ]` Sub-task: Display score with color (green/yellow/red)
  - `[ ]` Sub-task: Show score breakdown by category
  - `[ ]` Sub-task: Add trend indicator (improving/declining)

**Dashboard Widget Example:**
```javascript
// src/components/ComplexityWidget.jsx
import React, { useState, useEffect } from 'react';
import { 
  Card, 
  CardContent, 
  Typography, 
  Box,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow 
} from '@mui/material';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend,
  ResponsiveContainer 
} from 'recharts';
import { analysisService } from '../services/analysisService';

export const ComplexityWidget = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchComplexityData();
  }, []);

  const fetchComplexityData = async () => {
    try {
      const response = await analysisService.getComplexityVisualization();
      setData(response);
    } catch (error) {
      console.error('Failed to fetch complexity data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (!data) return <div>No data available</div>;

  // Prepare chart data
  const chartData = [
    { level: 'Low (1-5)', count: data.histogram.LOW || 0, fill: '#4caf50' },
    { level: 'Medium (6-10)', count: data.histogram.MEDIUM || 0, fill: '#ff9800' },
    { level: 'High (11-20)', count: data.histogram.HIGH || 0, fill: '#f44336' },
    { level: 'Very High (21+)', count: data.histogram.VERY_HIGH || 0, fill: '#9c27b0' },
  ];

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Cyclomatic Complexity
        </Typography>
        
        {/* Summary Stats */}
        <Box display="flex" gap={3} mb={3}>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Average
            </Typography>
            <Typography variant="h4">
              {data.average.toFixed(1)}
            </Typography>
          </Box>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Maximum
            </Typography>
            <Typography variant="h4">
              {data.max}
            </Typography>
          </Box>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Total Methods
            </Typography>
            <Typography variant="h4">
              {data.totalMethods}
            </Typography>
          </Box>
        </Box>

        {/* Distribution Chart */}
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="level" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="count" fill="#8884d8" />
          </BarChart>
        </ResponsiveContainer>

        {/* Top Complex Methods */}
        {data.topComplex && data.topComplex.length > 0 && (
          <Box mt={3}>
            <Typography variant="subtitle2" gutterBottom>
              Most Complex Methods
            </Typography>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Method</TableCell>
                  <TableCell align="right">Complexity</TableCell>
                  <TableCell>File</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.topComplex.slice(0, 5).map((method, idx) => (
                  <TableRow key={idx}>
                    <TableCell>{method.name}</TableCell>
                    <TableCell align="right">
                      <Typography 
                        color={method.complexity > 20 ? 'error' : 
                               method.complexity > 10 ? 'warning' : 'success'}
                      >
                        {method.complexity}
                      </Typography>
                    </TableCell>
                    <TableCell>{method.file}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};
```

---

### Day 48: Analysis Dashboard - Part 2
**Goal:** Add more analysis visualizations and dead code report

- `[ ]` **Create Dead Code Widget** (3h)
  - `[ ]` Sub-task: Create `DeadCodeWidget.jsx` component
  - `[ ]` Sub-task: Fetch dead code report from API
  - `[ ]` Sub-task: Display total unused methods and classes
  - `[ ]` Sub-task: Show list of dead code with confidence scores
  - `[ ]` Sub-task: Group by file or package
  - `[ ]` Sub-task: Add filter by confidence level
  - `[ ]` Sub-task: Add "Fix" or "Review" action buttons

- `[ ]` **Create Pattern Detection Widget** (2h)
  - `[ ]` Sub-task: Create `PatternsWidget.jsx` component
  - `[ ]` Sub-task: Fetch pattern data from API
  - `[ ]` Sub-task: Display good patterns vs anti-patterns
  - `[ ]` Sub-task: Show pattern distribution chart
  - `[ ]` Sub-task: List detected patterns with locations
  - `[ ]` Sub-task: Color code by pattern type

- `[ ]` **Create Trends Chart** (2h)
  - `[ ]` Sub-task: Create `TrendsWidget.jsx` component
  - `[ ]` Sub-task: Show metrics over time (if historical data available)
  - `[ ]` Sub-task: Display line chart with multiple metrics
  - `[ ]` Sub-task: Add date range selector
  - `[ ]` Sub-task: Show improvement/regression indicators
  - `[ ]` Sub-task: Add export chart button

- `[ ]` **Add Export Functionality** (1h)
  - `[ ]` Sub-task: Add "Export Report" button to dashboard
  - `[ ]` Sub-task: Generate PDF report (use jsPDF or similar)
  - `[ ]` Sub-task: Generate CSV export for raw data
  - `[ ]` Sub-task: Generate JSON export
  - `[ ]` Sub-task: Add print stylesheet for dashboard

---

## Week 10: Advanced Features & Polish (Sprint 5)

### Day 49: Dependency Graph Visualization
**Goal:** Create interactive dependency graph visualization

- `[ ]` **Set Up Graph Library** (1h)
  - `[ ]` Sub-task: Install React Flow: `npm install reactflow`
  - `[ ]` Sub-task: OR install Cytoscape: `npm install cytoscape react-cytoscape`
  - `[ ]` Sub-task: Review documentation and examples
  - `[ ]` Sub-task: Choose best library for use case

- `[ ]` **Create DependencyGraph Component** (4h)
  - `[ ]` Sub-task: Create `DependencyGraph.jsx` component
  - `[ ]` Sub-task: Fetch graph data from API
  - `[ ]` Sub-task: Convert backend graph format to library format
  - `[ ]` Sub-task: Display nodes (classes/methods)
  - `[ ]` Sub-task: Display edges (dependencies, calls)
  - `[ ]` Sub-task: Apply layout algorithm (hierarchical or force-directed)
  - `[ ]` Sub-task: Add zoom and pan controls

- `[ ]` **Add Interactivity** (2h)
  - `[ ]` Sub-task: Make nodes clickable to see details
  - `[ ]` Sub-task: Highlight node on hover
  - `[ ]` Sub-task: Show dependency path on node selection
  - `[ ]` Sub-task: Add filter to show only certain node types
  - `[ ]` Sub-task: Add search to find and highlight specific node
  - `[ ]` Sub-task: Add "Expand/Collapse" for node neighborhoods

- `[ ]` **Add Graph Controls** (1h)
  - `[ ]` Sub-task: Add layout selector (hierarchical, circular, force)
  - `[ ]` Sub-task: Add zoom controls (+/- buttons)
  - `[ ]` Sub-task: Add "Fit to Screen" button
  - `[ ]` Sub-task: Add legend for node/edge types
  - `[ ]` Sub-task: Add export as image button

**DependencyGraph Component Example (React Flow):**
```javascript
// src/components/DependencyGraph.jsx
import React, { useState, useEffect, useCallback } from 'react';
import ReactFlow, { 
  MiniMap, 
  Controls, 
  Background,
  useNodesState,
  useEdgesState,
  Panel
} from 'reactflow';
import 'reactflow/dist/style.css';
import { Button, Box } from '@mui/material';

export const DependencyGraph = ({ className }) => {
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadGraphData();
  }, [className]);

  const loadGraphData = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/graph/dependencies/${className}`
      );
      const data = await response.json();
      
      // Convert backend format to React Flow format
      const flowNodes = data.nodes.map(node => ({
        id: node.id,
        data: { 
          label: node.name,
          type: node.type 
        },
        position: { x: node.x || 0, y: node.y || 0 },
        style: getNodeStyle(node.type),
      }));

      const flowEdges = data.edges.map(edge => ({
        id: `${edge.from}-${edge.to}`,
        source: edge.from,
        target: edge.to,
        label: edge.type,
        animated: edge.type === 'CALLS',
        style: getEdgeStyle(edge.type),
      }));

      setNodes(flowNodes);
      setEdges(flowEdges);
    } catch (error) {
      console.error('Failed to load graph:', error);
    } finally {
      setLoading(false);
    }
  };

  const getNodeStyle = (type) => {
    const baseStyle = {
      padding: 10,
      borderRadius: 5,
      fontSize: 12,
      border: '2px solid',
    };

    switch (type) {
      case 'CLASS':
        return { ...baseStyle, background: '#4caf50', borderColor: '#2e7d32' };
      case 'METHOD':
        return { ...baseStyle, background: '#2196f3', borderColor: '#1565c0' };
      case 'INTERFACE':
        return { ...baseStyle, background: '#ff9800', borderColor: '#e65100' };
      default:
        return { ...baseStyle, background: '#9e9e9e', borderColor: '#616161' };
    }
  };

  const getEdgeStyle = (type) => {
    switch (type) {
      case 'CALLS':
        return { stroke: '#2196f3', strokeWidth: 2 };
      case 'EXTENDS':
        return { stroke: '#4caf50', strokeWidth: 2, strokeDasharray: '5,5' };
      case 'IMPLEMENTS':
        return { stroke: '#ff9800', strokeWidth: 2, strokeDasharray: '5,5' };
      default:
        return { stroke: '#9e9e9e', strokeWidth: 1 };
    }
  };

  const onNodeClick = useCallback((event, node) => {
    console.log('Node clicked:', node);
    // Could open details panel or navigate to code
  }, []);

  if (loading) return <div>Loading graph...</div>;

  return (
    <Box height={600}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={onNodeClick}
        fitView
      >
        <Controls />
        <MiniMap />
        <Background variant="dots" gap={12} size={1} />
        <Panel position="top-right">
          <Box display="flex" gap={1}>
            <Button size="small" variant="contained">
              Export
            </Button>
            <Button size="small" variant="outlined">
              Reset View
            </Button>
          </Box>
        </Panel>
      </ReactFlow>
    </Box>
  );
};
```

---

### Day 50: Settings & Configuration UI
**Goal:** Create settings page for user preferences and indexing control

- `[ ]` **Create Settings Page** (2h)
  - `[ ]` Sub-task: Create `SettingsPage.jsx` component
  - `[ ]` Sub-task: Add tabbed interface for different settings categories
  - `[ ]` Sub-task: Tabs: General, Indexing, Search, Analysis, Appearance
  - `[ ]` Sub-task: Save settings to localStorage
  - `[ ]` Sub-task: Add reset to defaults button

- `[ ]` **Indexing Configuration UI** (3h)
  - `[ ]` Sub-task: Create form to configure indexing options
  - `[ ]` Sub-task: Add directory path selector
  - `[ ]` Sub-task: Add exclusion pattern editor
  - `[ ]` Sub-task: Add "Start Indexing" button
  - `[ ]` Sub-task: Show indexing progress with progress bar
  - `[ ]` Sub-task: Display indexing status and statistics
  - `[ ]` Sub-task: Add "Stop Indexing" button
  - `[ ]` Sub-task: Show last indexed date/time

- `[ ]` **Search Preferences** (1h)
  - `[ ]` Sub-task: Add toggle for hybrid vs semantic vs keyword search
  - `[ ]` Sub-task: Add slider for semantic/keyword weight
  - `[ ]` Sub-task: Add default results per page setting
  - `[ ]` Sub-task: Add "Include tests" toggle
  - `[ ]` Sub-task: Save and apply preferences

- `[ ]` **Appearance Settings** (2h)
  - `[ ]` Sub-task: Add theme selector (Light/Dark/Auto)
  - `[ ]` Sub-task: Add code editor theme selector
  - `[ ]` Sub-task: Add font size selector
  - `[ ]` Sub-task: Apply theme changes immediately
  - `[ ]` Sub-task: Persist theme choice

**Settings Page Example:**
```javascript
// src/pages/SettingsPage.jsx
import React, { useState } from 'react';
import {
  Box,
  Tabs,
  Tab,
  Paper,
  TextField,
  Button,
  Switch,
  FormControlLabel,
  Typography,
  Slider,
  Select,
  MenuItem,
} from '@mui/material';

export const SettingsPage = () => {
  const [currentTab, setCurrentTab] = useState(0);
  const [settings, setSettings] = useState({
    indexPath: '',
    excludePatterns: 'target,build,.git',
    searchMode: 'hybrid',
    semanticWeight: 0.6,
    resultsPerPage: 10,
    includeTests: false,
    theme: 'dark',
    editorTheme: 'vs-dark',
    fontSize: 14,
  });

  const handleSave = () => {
    localStorage.setItem('firestick-settings', JSON.stringify(settings));
    alert('Settings saved!');
  };

  const handleReset = () => {
    // Reset to defaults
    setSettings({
      indexPath: '',
      excludePatterns: 'target,build,.git',
      searchMode: 'hybrid',
      semanticWeight: 0.6,
      resultsPerPage: 10,
      includeTests: false,
      theme: 'dark',
      editorTheme: 'vs-dark',
      fontSize: 14,
    });
  };

  return (
    <Box p={3}>
      <Typography variant="h4" gutterBottom>
        Settings
      </Typography>

      <Paper>
        <Tabs value={currentTab} onChange={(e, v) => setCurrentTab(v)}>
          <Tab label="General" />
          <Tab label="Indexing" />
          <Tab label="Search" />
          <Tab label="Appearance" />
        </Tabs>

        <Box p={3}>
          {/* General Tab */}
          {currentTab === 0 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                General Settings
              </Typography>
              {/* General settings fields */}
            </Box>
          )}

          {/* Indexing Tab */}
          {currentTab === 1 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Indexing Configuration
              </Typography>
              
              <TextField
                fullWidth
                label="Project Directory"
                value={settings.indexPath}
                onChange={(e) => setSettings({ ...settings, indexPath: e.target.value })}
                margin="normal"
                helperText="Root directory of the project to index"
              />

              <TextField
                fullWidth
                label="Exclude Patterns"
                value={settings.excludePatterns}
                onChange={(e) => setSettings({ ...settings, excludePatterns: e.target.value })}
                margin="normal"
                helperText="Comma-separated patterns to exclude (e.g., target,build,.git)"
              />

              <Box mt={2}>
                <Button variant="contained" color="primary">
                  Start Indexing
                </Button>
                <Button variant="outlined" sx={{ ml: 1 }}>
                  View Index Status
                </Button>
              </Box>
            </Box>
          )}

          {/* Search Tab */}
          {currentTab === 2 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Search Preferences
              </Typography>

              <FormControlLabel
                control={
                  <Select
                    value={settings.searchMode}
                    onChange={(e) => setSettings({ ...settings, searchMode: e.target.value })}
                  >
                    <MenuItem value="hybrid">Hybrid (Semantic + Keyword)</MenuItem>
                    <MenuItem value="semantic">Semantic Only</MenuItem>
                    <MenuItem value="keyword">Keyword Only</MenuItem>
                  </Select>
                }
                label="Search Mode"
                labelPlacement="start"
                sx={{ width: '100%', justifyContent: 'space-between', ml: 0 }}
              />

              {settings.searchMode === 'hybrid' && (
                <Box mt={2}>
                  <Typography gutterBottom>
                    Semantic Weight: {settings.semanticWeight.toFixed(1)}
                  </Typography>
                  <Slider
                    value={settings.semanticWeight}
                    onChange={(e, v) => setSettings({ ...settings, semanticWeight: v })}
                    min={0}
                    max={1}
                    step={0.1}
                    marks
                    valueLabelDisplay="auto"
                  />
                </Box>
              )}

              <FormControlLabel
                control={
                  <Switch
                    checked={settings.includeTests}
                    onChange={(e) => setSettings({ ...settings, includeTests: e.target.checked })}
                  />
                }
                label="Include test files in search"
                sx={{ mt: 2 }}
              />
            </Box>
          )}

          {/* Appearance Tab */}
          {currentTab === 3 && (
            <Box>
              <Typography variant="h6" gutterBottom>
                Appearance
              </Typography>

              <FormControlLabel
                control={
                  <Select
                    value={settings.theme}
                    onChange={(e) => setSettings({ ...settings, theme: e.target.value })}
                  >
                    <MenuItem value="light">Light</MenuItem>
                    <MenuItem value="dark">Dark</MenuItem>
                    <MenuItem value="auto">Auto</MenuItem>
                  </Select>
                }
                label="Theme"
                labelPlacement="start"
                sx={{ width: '100%', justifyContent: 'space-between', ml: 0 }}
              />

              <Box mt={2}>
                <Typography gutterBottom>
                  Font Size: {settings.fontSize}px
                </Typography>
                <Slider
                  value={settings.fontSize}
                  onChange={(e, v) => setSettings({ ...settings, fontSize: v })}
                  min={10}
                  max={20}
                  step={1}
                  marks
                  valueLabelDisplay="auto"
                />
              </Box>
            </Box>
          )}
        </Box>

        <Box p={2} display="flex" justifyContent="flex-end" gap={1}>
          <Button onClick={handleReset}>
            Reset to Defaults
          </Button>
          <Button variant="contained" color="primary" onClick={handleSave}>
            Save Settings
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};
```

---

### Day 51: Documentation Viewer & Help
**Goal:** Create documentation viewer and help system

- `[ ]` **Create Documentation Viewer** (3h)
  - `[ ]` Sub-task: Create `DocumentationPage.jsx` component
  - `[ ]` Sub-task: Fetch generated documentation from API
  - `[ ]` Sub-task: Display markdown content with proper formatting
  - `[ ]` Sub-task: Create navigation tree for packages/classes
  - `[ ]` Sub-task: Add search within documentation
  - `[ ]` Sub-task: Add breadcrumb navigation
  - `[ ]` Sub-task: Support linking between docs

- `[ ]` **Create Help System** (2h)
  - `[ ]` Sub-task: Create `HelpPage.jsx` component
  - `[ ]` Sub-task: Add user guide content
  - `[ ]` Sub-task: Add FAQ section
  - `[ ]` Sub-task: Add keyboard shortcuts reference
  - `[ ]` Sub-task: Add video tutorials (links)
  - `[ ]` Sub-task: Add search in help content

- `[ ]` **Add Onboarding Tour** (2h)
  - `[ ]` Sub-task: Install tour library: `npm install react-joyride`
  - `[ ]` Sub-task: Create guided tour for first-time users
  - `[ ]` Sub-task: Highlight key features (search, analysis, graph)
  - `[ ]` Sub-task: Add "Skip" and "Next" buttons
  - `[ ]` Sub-task: Save tour completion status
  - `[ ]` Sub-task: Add "Restart Tour" button in help menu

- `[ ]` **Add Tooltips and Help Icons** (1h)
  - `[ ]` Sub-task: Add tooltips to all major UI elements
  - `[ ]` Sub-task: Add help icons with explanations
  - `[ ]` Sub-task: Add contextual help in settings
  - `[ ]` Sub-task: Add "What's this?" links

---

### Day 52: UI Polish & Responsive Design
**Goal:** Polish UI and ensure responsive design

- `[ ]` **Improve Responsive Design** (3h)
  - `[ ]` Sub-task: Test all pages on mobile (375px width)
  - `[ ]` Sub-task: Test on tablet (768px width)
  - `[ ]` Sub-task: Test on desktop (1920px width)
  - `[ ]` Sub-task: Adjust layouts for different screen sizes
  - `[ ]` Sub-task: Hide/show elements based on screen size
  - `[ ]` Sub-task: Make tables scrollable on mobile
  - `[ ]` Sub-task: Adjust font sizes for readability

- `[ ]` **Add Loading States** (2h)
  - `[ ]` Sub-task: Replace all "Loading..." text with proper skeletons
  - `[ ]` Sub-task: Add skeleton screens for search results
  - `[ ]` Sub-task: Add skeleton screens for dashboard widgets
  - `[ ]` Sub-task: Add progress indicators for long operations
  - `[ ]` Sub-task: Add optimistic UI updates where appropriate

- `[ ]` **Improve Error Handling** (2h)
  - `[ ]` Sub-task: Create consistent error message components
  - `[ ]` Sub-task: Add retry buttons for failed requests
  - `[ ]` Sub-task: Add fallback UI for errors
  - `[ ]` Sub-task: Add error boundary for React errors
  - `[ ]` Sub-task: Log errors to console with details
  - `[ ]` Sub-task: Add user-friendly error messages

- `[ ]` **Add Animations and Transitions** (1h)
  - `[ ]` Sub-task: Add smooth page transitions
  - `[ ]` Sub-task: Add hover animations on interactive elements
  - `[ ]` Sub-task: Add fade-in for loaded content
  - `[ ]` Sub-task: Add slide-in for modals/drawers
  - `[ ]` Sub-task: Keep animations subtle and purposeful

---

### Day 53: Performance Optimization & Testing
**Goal:** Optimize performance and test all features

- `[ ]` **Performance Optimization** (3h)
  - `[ ]` Sub-task: Implement code splitting for routes
  - `[ ]` Sub-task: Lazy load heavy components (Monaco Editor, Charts)
  - `[ ]` Sub-task: Memoize expensive computations
  - `[ ]` Sub-task: Optimize re-renders with React.memo
  - `[ ]` Sub-task: Implement virtual scrolling for long lists
  - `[ ]` Sub-task: Compress and optimize images
  - `[ ]` Sub-task: Add service worker for caching (optional)

- `[ ]` **Cross-Browser Testing** (2h)
  - `[ ]` Sub-task: Test in Chrome
  - `[ ]` Sub-task: Test in Firefox
  - `[ ]` Sub-task: Test in Safari
  - `[ ]` Sub-task: Test in Edge
  - `[ ]` Sub-task: Fix browser-specific issues
  - `[ ]` Sub-task: Add polyfills if needed

- `[ ]` **Accessibility Testing** (2h)
  - `[ ]` Sub-task: Test keyboard navigation
  - `[ ]` Sub-task: Add ARIA labels where needed
  - `[ ]` Sub-task: Ensure proper heading hierarchy
  - `[ ]` Sub-task: Test with screen reader (optional)
  - `[ ]` Sub-task: Ensure color contrast meets WCAG standards
  - `[ ]` Sub-task: Add focus indicators

- `[ ]` **Integration Testing** (1h)
  - `[ ]` Sub-task: Test search flow end-to-end
  - `[ ]` Sub-task: Test analysis dashboard updates
  - `[ ]` Sub-task: Test code viewer navigation
  - `[ ]` Sub-task: Test settings persistence
  - `[ ]` Sub-task: Test error scenarios
  - `[ ]` Sub-task: Document any known issues

---

### Day 54: Deployment & Documentation
**Goal:** Prepare for deployment and create UI documentation

- `[ ]` **Build for Production** (2h)
  - `[ ]` Sub-task: Run production build: `npm run build`
  - `[ ]` Sub-task: Test production build locally
  - `[ ]` Sub-task: Optimize bundle size
  - `[ ]` Sub-task: Configure environment variables
  - `[ ]` Sub-task: Set up proxy for API calls
  - `[ ]` Sub-task: Add build to Spring Boot static resources

- `[ ]` **Configure Spring Boot Integration** (2h)
  - `[ ]` Sub-task: Copy React build to `src/main/resources/static`
  - `[ ]` Sub-task: Configure Spring Boot to serve React app
  - `[ ]` Sub-task: Set up API proxy or CORS
  - `[ ]` Sub-task: Test integrated application
  - `[ ]` Sub-task: Add Maven/Gradle build task for UI

- `[ ]` **Write UI Documentation** (3h)
  - `[ ]` Sub-task: Document UI architecture and structure
  - `[ ]` Sub-task: Document component hierarchy
  - `[ ]` Sub-task: Document state management approach
  - `[ ]` Sub-task: Create developer guide for UI
  - `[ ]` Sub-task: Document API integration
  - `[ ]` Sub-task: Add screenshots to documentation
  - `[ ]` Sub-task: Create troubleshooting guide

- `[ ]` **Create Demo Video/Screenshots** (1h)
  - `[ ]` Sub-task: Take screenshots of all major pages
  - `[ ]` Sub-task: Create short demo video (optional)
  - `[ ]` Sub-task: Add to README
  - `[ ]` Sub-task: Create user guide with visuals

---

## Phase 5 Success Criteria

Before moving to Phase 6, verify ALL of the following:

### Functional Requirements
- ⬜ Search interface works with auto-complete
- ⬜ Search results display correctly with syntax highlighting
- ⬜ Code viewer loads and displays files
- ⬜ Analysis dashboard shows all metrics
- ⬜ Dependency graph renders and is interactive
- ⬜ Settings can be saved and applied
- ⬜ Documentation viewer displays content
- ⬜ All API integrations work correctly
- ⬜ Error handling works properly

### UI/UX Requirements
- ⬜ Interface is intuitive and easy to navigate
- ⬜ Design is consistent across all pages
- ⬜ Responsive on mobile, tablet, and desktop
- ⬜ Loading states are clear
- ⬜ Error messages are helpful
- ⬜ Animations are smooth
- ⬜ Accessibility standards are met

### Performance Requirements
- ⬜ Initial page load < 3 seconds
- ⬜ Navigation between pages is instant
- ⬜ Search results appear < 2 seconds
- ⬜ Code viewer loads files < 1 second
- ⬜ Dashboard loads < 3 seconds
- ⬜ No layout shifts or jank
- ⬜ Bundle size is optimized (<500KB initial)

### Code Quality Requirements
- ⬜ Code follows React best practices
- ⬜ Components are properly structured
- ⬜ No console errors or warnings
- ⬜ Code is well-commented
- ⬜ Proper error boundaries in place

### Documentation Requirements
- ⬜ UI architecture is documented
- ⬜ Component API is documented
- ⬜ Setup instructions are clear
- ⬜ User guide exists with screenshots
- ⬜ Known issues are documented

---

## Common Issues & Troubleshooting

### Issue: CORS errors when calling API
**Solution:**
```java
// Add CORS configuration in Spring Boot
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### Issue: Monaco Editor not loading
**Solution:**
- Check import statement is correct
- Verify @monaco-editor/react is installed
- Check for console errors
- Try clearing node_modules and reinstalling
- Ensure proper height is set on container

### Issue: Charts not rendering
**Solution:**
- Verify data format matches library requirements
- Check that parent container has defined dimensions
- Ensure ResponsiveContainer is used
- Check for JavaScript errors in console
- Verify chart library is correctly imported

### Issue: Build fails or is too large
**Solution:**
- Run `npm run build -- --debug` to see what's included
- Use lazy loading for large components
- Check for duplicate dependencies
- Remove unused dependencies
- Use dynamic imports for routes

### Issue: State not persisting
**Solution:**
- Check localStorage is available (not disabled)
- Verify JSON serialization/deserialization
- Check for quota exceeded errors
- Consider using sessionStorage for temporary data
- Add error handling for storage operations

### Issue: UI is slow or laggy
**Solution:**
- Use React DevTools Profiler to find slow components
- Implement React.memo for expensive components
- Use useCallback and useMemo appropriately
- Implement virtual scrolling for long lists
- Reduce re-renders with proper state management
- Optimize images and assets

---

## Phase 5 Sprint Review Checklist

**Demo Items:**
- [ ] Show search with auto-complete
- [ ] Show search results with highlighting
- [ ] Show code viewer with navigation
- [ ] Show complexity dashboard with charts
- [ ] Show code smells visualization
- [ ] Show dead code report
- [ ] Show dependency graph
- [ ] Show settings and configuration
- [ ] Show documentation viewer
- [ ] Show responsive design on different devices
- [ ] Show dark/light theme switching

**Retrospective Questions:**
- What UI features are most useful?
- What needs improvement in UX?
- Are there any confusing elements?
- How is the performance?
- What additional features would enhance the UI?

---
---

# Phase 6: Desktop Packaging (Weeks 10-11)

**Status:** Not Started  
**Goal:** Package Firestick as a standalone desktop application with native installers  
**Team:** DevOps Engineer + Backend Developer  
**Duration:** Dec 22, 2025 - Jan 4, 2026 (2 weeks, Sprint 5-6)  
**Dependencies:** Phase 5 (Web UI) must be complete and tested

---

## Phase 6 Overview

### What We're Building
A professional desktop application that:
1. **Runs Standalone** - No manual Java/Maven/npm installation required
2. **Easy Installation** - Native installers for Windows, macOS, and Linux
3. **Self-Contained** - Bundles JRE, application, and all dependencies
4. **Professional** - Application icon, splash screen, proper branding
5. **User-Friendly** - Simple launcher, system tray integration
6. **Distributable** - Ready to share with end users

### Why This Matters
Desktop packaging transforms Firestick from a developer tool into a user-friendly application:
- **No technical knowledge required** - Users just download and install
- **Professional appearance** - Looks like a real application
- **Easy distribution** - Single installer file per platform
- **Isolated environment** - Doesn't interfere with user's Java setup
- **Better UX** - Native look and feel on each platform

### Technology Stack
- **Build Tool:** jpackage (built into JDK 21)
- **Alternative:** jlink for custom JRE
- **Launcher:** Native executable (created by jpackage)
- **Installer Formats:**
  - Windows: `.msi` or `.exe`
  - macOS: `.dmg` or `.pkg`
  - Linux: `.deb` and `.rpm`
- **Build Automation:** Maven or shell scripts
- **Testing:** Manual testing on each platform

---

## Week 10-11: Packaging & Distribution (Sprint 5, Part 2 & Sprint 6)

### Day 55: Prepare Application for Packaging
**Goal:** Prepare Spring Boot application for standalone packaging

- `[ ]` **Create Uber JAR** (2h)
  - `[ ]` Sub-task: Configure Maven to build fat JAR with all dependencies
  - `[ ]` Sub-task: Add Spring Boot Maven plugin configuration
  - `[ ]` Sub-task: Update `pom.xml` with proper main class
  - `[ ]` Sub-task: Build JAR: `mvn clean package`
  - `[ ]` Sub-task: Test JAR runs: `java -jar target/firestick-1.0.0.jar`
  - `[ ]` Sub-task: Verify embedded React UI is included
  - `[ ]` Sub-task: Check JAR size (should be ~50-100MB)

**Maven Configuration Example:**

```xml
<!-- pom.xml -->
<project>
    <groupId>com.codetalker</groupId>
    <artifactId>firestick</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <main.class>com.codetalker.firestick.FirestickApplicationcom.codetalker.firestick.FirestickApplication</main.class>
    </properties>

    <build>
        <finalName>firestick-${project.version}</finalName>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>3.5.6</version>
                <configuration>
                    <mainClass>${main.class}</mainClass>
                    <layout>JAR</layout>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- `[ ]` **Embed React Build** (2h)
  - `[ ]` Sub-task: Build React app: `cd firestick-ui && npm run build`
  - `[ ]` Sub-task: Copy `dist/` contents to `src/main/resources/static/`
  - `[ ]` Sub-task: Configure Spring Boot to serve static files from root
  - `[ ]` Sub-task: Test that http://localhost:8080/ shows React app
  - `[ ]` Sub-task: Automate build process in Maven

**Spring Boot Static Content Configuration:**
```java
// src/main/java/com/codetalker/firestick/config/WebConfig.java
package com.codetalker.firestick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from /static
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all non-API routes to React (for client-side routing)
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:\\w+}")
                .setViewName("forward:/index.html");
    }
}
```

- `[ ]` **Create Application Metadata** (2h)
  - `[ ]` Sub-task: Create application icon (256x256 PNG)
  - `[ ]` Sub-task: Create icons for different platforms:
    - Windows: `.ico` file (multiple sizes)
    - macOS: `.icns` file
    - Linux: `.png` file (256x256)
  - `[ ]` Sub-task: Create splash screen image (optional)
  - `[ ]` Sub-task: Prepare application description and copyright text
  - `[ ]` Sub-task: Choose app name and vendor name

- `[ ]` **Configure Application Properties** (2h)
  - `[ ]` Sub-task: Set default server port (8080)
  - `[ ]` Sub-task: Configure H2 database for embedded mode
  - `[ ]` Sub-task: Set default data directory to user home
  - `[ ]` Sub-task: Configure Chroma to use local directory
  - `[ ]` Sub-task: Add application version to properties
  - `[ ]` Sub-task: Test configuration

**Application Properties Example:**
```properties
# src/main/resources/application.properties

# Application Info
spring.application.name=Firestick
app.version=1.0.0
app.vendor=CodeTalker

# Server Configuration
server.port=8080
server.address=localhost

# H2 Database (Embedded)
spring.datasource.url=jdbc:h2:file:${user.home}/.firestick/data/firestick;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# Chroma Configuration
chroma.host=localhost
chroma.port=8000
chroma.data.path=${user.home}/.firestick/chroma

# Embedding Model
embedding.model.path=${user.home}/.firestick/models/all-MiniLM-L6-v2.onnx

# Indexing
indexing.default.exclude=target,build,.git,.svn,node_modules

# Logging
logging.level.root=INFO
logging.level.com.codetalker.firestick=DEBUG
logging.file.name=${user.home}/.firestick/logs/firestick.log
```

---

### Day 56: Set Up jpackage Build
**Goal:** Configure jpackage to create native installers

- `[ ]` **Install jpackage Prerequisites** (1h)
  - `[ ]` Sub-task: Verify JDK 21 is installed
  - `[ ]` Sub-task: Verify `jpackage` command is available
  - `[ ]` Sub-task: Install platform-specific tools:
    - **Windows:** WiX Toolset 3.11+ (for .msi) or Inno Setup (for .exe)
    - **macOS:** Xcode command line tools
    - **Linux:** `fakeroot` and `dpkg` (for .deb), `rpmbuild` (for .rpm)
  - `[ ]` Sub-task: Test jpackage: `jpackage --version`

- `[ ]` **Create jpackage Configuration** (3h)
  - `[ ]` Sub-task: Create `packaging/` directory in project root
  - `[ ]` Sub-task: Create `jpackage-config.properties` file
  - `[ ]` Sub-task: Define application metadata
  - `[ ]` Sub-task: Define file associations (optional)
  - `[ ]` Sub-task: Define launcher configuration
  - `[ ]` Sub-task: Document all jpackage options

**jpackage Configuration Example:**

```properties
# packaging/jpackage-config.properties
# Application Info
app.name=Firestick
app.version=1.0.0
app.vendor=CodeTalkerl
app.description=Legacy Code Analysis and Search Tool
app.copyright=Copyright © 2025 CodeTalkerl
# Main JAR and Class
app.jar=firestick-1.0.0.jar
app.main-class=com.codetalker.firestick.FirestickApplication
# Icons (platform-specific)
app.icon.windows=packaging/icons/firestick.ico
app.icon.macos=packaging/icons/firestick.icns
app.icon.linux=packaging/icons/firestick.png
# Installation Directories
app.install-dir.windows=Firestick
app.install-dir.macos=/Applications
app.install-dir.linux=/opt/firestick
# Launcher Options
launcher.name=Firestick
launcher.java-options=-Xmx2048m -Xms512m
# File Associations (optional)
file.association.extension=.fstk
file.association.description=Firestick Project
```

- `[ ]` **Create Build Script for Windows** (2h)
  - `[ ]` Sub-task: Create `packaging/build-windows.bat` script
  - `[ ]` Sub-task: Add JAR build step
  - `[ ]` Sub-task: Add jpackage command with Windows options
  - `[ ]` Sub-task: Configure .msi installer creation
  - `[ ]` Sub-task: Set installation directory and shortcuts
  - `[ ]` Sub-task: Test script on Windows machine

**Windows Build Script Example:**
```batch
@echo off
REM packaging/build-windows.bat

echo Building Firestick for Windows...

REM Step 1: Build JAR
echo.
echo [1/3] Building application JAR...
call mvn clean package -DskipTests
if %errorlevel% neq 0 exit /b %errorlevel%

REM Step 2: Create runtime image with jlink (optional, for smaller size)
REM echo.
REM echo [2/3] Creating custom JRE...
REM jlink --add-modules java.base,java.sql,java.naming,java.desktop,java.management,jdk.unsupported ^
REM       --output packaging/runtime ^
REM       --strip-debug ^
REM       --no-header-files ^
REM       --no-man-pages ^
REM       --compress=2

REM Step 3: Create installer with jpackage
echo.
echo [2/3] Creating Windows installer...
jpackage ^
  --input target ^
  --name Firestick ^
  --main-jar firestick-1.0.0.jar ^
  --main-class com.codetalker.firestick.FirestickApplication ^
  --type msi ^
  --app-version 1.0.0 ^
  --vendor "CodeTalkerl" ^
  --description "Legacy Code Analysis and Search Tool" ^
  --icon packaging/icons/firestick.ico ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group "Firestick" ^
  --java-options "-Xmx2048m" ^
  --java-options "-Xms512m" ^
  --dest packaging/output/windows

echo.
echo [3/3] Build complete!
echo Installer location: packaging\output\windows\Firestick-1.0.0.msi
pause
```

- `[ ]` **Test Windows Build** (2h)
  - `[ ]` Sub-task: Run build script
  - `[ ]` Sub-task: Verify .msi is created
  - `[ ]` Sub-task: Install on clean Windows machine
  - `[ ]` Sub-task: Test application launches
  - `[ ]` Sub-task: Test uninstaller
  - `[ ]` Sub-task: Document any issues

---

### Day 57: Create macOS Package
**Goal:** Build macOS installer with .dmg or .pkg format

- `[ ]` **Create Build Script for macOS** (3h)
  - `[ ]` Sub-task: Create `packaging/build-macos.sh` script
  - `[ ]` Sub-task: Make script executable: `chmod +x packaging/build-macos.sh`
  - `[ ]` Sub-task: Add JAR build step
  - `[ ]` Sub-task: Add jpackage command with macOS options
  - `[ ]` Sub-task: Configure .dmg or .pkg creation
  - `[ ]` Sub-task: Set application bundle identifier

**macOS Build Script Example:**
```bash
#!/bin/bash
# packaging/build-macos.sh

set -e  # Exit on error

echo "Building Firestick for macOS..."

# Step 1: Build JAR
echo ""
echo "[1/3] Building application JAR..."
mvn clean package -DskipTests

# Step 2: Create installer with jpackage
echo ""
echo "[2/3] Creating macOS installer..."
jpackage \
  --input target \
  --name Firestick \
  --main-jar firestick-1.0.0.jar \
  --main-class com.codetalker.firestick.FirestickApplication \
  --type dmg \
  --app-version 1.0.0 \
  --vendor "CodeTalkerl" \
  --description "Legacy Code Analysis and Search Tool" \
  --icon packaging/icons/firestick.icns \
  --mac-package-identifier com.codetalkerl.firestick \
  --mac-package-name Firestick \
  --java-options "-Xmx2048m" \
  --java-options "-Xms512m" \
  --dest packaging/output/macos

# Step 3: (Optional) Sign the application
# echo ""
# echo "[3/3] Signing application..."
# codesign --force --deep --sign "Developer ID Application: Your Name" \
#   packaging/output/macos/Firestick-1.0.0.dmg

echo ""
echo "[3/3] Build complete!"
echo "Installer location: packaging/output/macos/Firestick-1.0.0.dmg"
```

- `[ ]` **Configure macOS App Bundle** (2h)
  - `[ ]` Sub-task: Set bundle identifier (com.codetalkerl.firestick)
  - `[ ]` Sub-task: Create Info.plist template
  - `[ ]` Sub-task: Configure app category
  - `[ ]` Sub-task: Set minimum macOS version
  - `[ ]` Sub-task: Configure code signing (if available)
  - `[ ]` Sub-task: Test on macOS machine

- `[ ]` **Create macOS Launcher** (2h)
  - `[ ]` Sub-task: Configure launch options
  - `[ ]` Sub-task: Set heap size for JVM
  - `[ ]` Sub-task: Add system properties
  - `[ ]` Sub-task: Configure splash screen
  - `[ ]` Sub-task: Test launcher

- `[ ]` **Test macOS Build** (1h)
  - `[ ]` Sub-task: Run build script
  - `[ ]` Sub-task: Verify .dmg is created
  - `[ ]` Sub-task: Mount .dmg and test drag-to-Applications
  - `[ ]` Sub-task: Test application launches
  - `[ ]` Sub-task: Test uninstaller (drag to trash)
  - `[ ]` Sub-task: Document any issues

---

### Day 58: Create Linux Packages
**Goal:** Build Linux installers for .deb and .rpm formats

- `[ ]` **Create Build Script for Linux (Debian/Ubuntu)** (3h)
  - `[ ]` Sub-task: Create `packaging/build-linux-deb.sh` script
  - `[ ]` Sub-task: Make script executable
  - `[ ]` Sub-task: Add JAR build step
  - `[ ]` Sub-task: Add jpackage command for .deb
  - `[ ]` Sub-task: Configure package metadata
  - `[ ]` Sub-task: Set dependencies

**Linux (Debian) Build Script Example:**
```bash
#!/bin/bash
# packaging/build-linux-deb.sh

set -e  # Exit on error

echo "Building Firestick for Linux (Debian/Ubuntu)..."

# Step 1: Build JAR
echo ""
echo "[1/3] Building application JAR..."
mvn clean package -DskipTests

# Step 2: Create .deb installer with jpackage
echo ""
echo "[2/3] Creating .deb package..."
jpackage \
  --input target \
  --name firestick \
  --main-jar firestick-1.0.0.jar \
  --main-class com.codetalker.firestick.FirestickApplication \
  --type deb \
  --app-version 1.0.0 \
  --vendor "CodeTalkerl" \
  --description "Legacy Code Analysis and Search Tool" \
  --icon packaging/icons/firestick.png \
  --linux-package-name firestick \
  --linux-deb-maintainer "support@codetalkerl.com" \
  --linux-menu-group "Development" \
  --linux-shortcut \
  --java-options "-Xmx2048m" \
  --java-options "-Xms512m" \
  --dest packaging/output/linux

echo ""
echo "[3/3] Build complete!"
echo "Package location: packaging/output/linux/firestick_1.0.0-1_amd64.deb"
```

- `[ ]` **Create Build Script for Linux (RPM)** (2h)
  - `[ ]` Sub-task: Create `packaging/build-linux-rpm.sh` script
  - `[ ]` Sub-task: Add jpackage command for .rpm
  - `[ ]` Sub-task: Configure RPM-specific metadata
  - `[ ]` Sub-task: Set package group
  - `[ ]` Sub-task: Test on Fedora/RHEL/CentOS

**Linux (RPM) Build Script Example:**
```bash
#!/bin/bash
# packaging/build-linux-rpm.sh

set -e

echo "Building Firestick for Linux (RPM)..."

echo ""
echo "[1/3] Building application JAR..."
mvn clean package -DskipTests

echo ""
echo "[2/3] Creating .rpm package..."
jpackage \
  --input target \
  --name firestick \
  --main-jar firestick-1.0.0.jar \
  --main-class com.codetalker.firestick.FirestickApplication \
  --type rpm \
  --app-version 1.0.0 \
  --vendor "CodeTalkerl" \
  --description "Legacy Code Analysis and Search Tool" \
  --icon packaging/icons/firestick.png \
  --linux-package-name firestick \
  --linux-rpm-license-type "MIT" \
  --linux-menu-group "Development" \
  --linux-shortcut \
  --java-options "-Xmx2048m" \
  --java-options "-Xms512m" \
  --dest packaging/output/linux

echo ""
echo "[3/3] Build complete!"
echo "Package location: packaging/output/linux/firestick-1.0.0-1.x86_64.rpm"
```

- `[ ]` **Configure Desktop Entry** (2h)
  - `[ ]` Sub-task: Create `.desktop` file for Linux
  - `[ ]` Sub-task: Set application name and icon
  - `[ ]` Sub-task: Configure categories
  - `[ ]` Sub-task: Set terminal mode (false)
  - `[ ]` Sub-task: Test desktop entry

**Desktop Entry Example:**
```desktop
[Desktop Entry]
Type=Application
Name=Firestick
GenericName=Code Analysis Tool
Comment=Legacy Code Analysis and Search Tool
Icon=firestick
Exec=/opt/firestick/bin/Firestick
Terminal=false
Categories=Development;
```

- `[ ]` **Test Linux Builds** (1h)
  - `[ ]` Sub-task: Test .deb on Ubuntu
  - `[ ]` Sub-task: Test .rpm on Fedora
  - `[ ]` Sub-task: Verify installation with package manager
  - `[ ]` Sub-task: Test application launches
  - `[ ]` Sub-task: Test uninstallation
  - `[ ]` Sub-task: Document any issues

---

### Day 59: Cross-Platform Testing
**Goal:** Test installers on all target platforms

- `[ ]` **Windows Testing** (2h)
  - `[ ]` Sub-task: Test on Windows 10
  - `[ ]` Sub-task: Test on Windows 11
  - `[ ]` Sub-task: Test installation process
  - `[ ]` Sub-task: Test application launches correctly
  - `[ ]` Sub-task: Test all features work (search, analysis, UI)
  - `[ ]` Sub-task: Test data persistence across restarts
  - `[ ]` Sub-task: Test uninstallation is clean
  - `[ ]` Sub-task: Check Start Menu shortcuts
  - `[ ]` Sub-task: Verify no registry pollution after uninstall

- `[ ]` **macOS Testing** (2h)
  - `[ ]` Sub-task: Test on macOS 12 (Monterey)
  - `[ ]` Sub-task: Test on macOS 13 (Ventura)
  - `[ ]` Sub-task: Test on macOS 14 (Sonoma)
  - `[ ]` Sub-task: Test DMG mounting and installation
  - `[ ]` Sub-task: Test application launches (check Gatekeeper warnings)
  - `[ ]` Sub-task: Test all features work
  - `[ ]` Sub-task: Test data persistence
  - `[ ]` Sub-task: Test uninstallation (drag to trash)
  - `[ ]` Sub-task: Verify no leftover files

- `[ ]` **Linux Testing** (2h)
  - `[ ]` Sub-task: Test .deb on Ubuntu 22.04 LTS
  - `[ ]` Sub-task: Test .deb on Debian 12
  - `[ ]` Sub-task: Test .rpm on Fedora 38
  - `[ ]` Sub-task: Test installation with package manager
  - `[ ]` Sub-task: Test application launches
  - `[ ]` Sub-task: Test all features work
  - `[ ]` Sub-task: Test data persistence
  - `[ ]` Sub-task: Test uninstallation
  - `[ ]` Sub-task: Check application menu integration

- `[ ]` **Document Test Results** (2h)
  - `[ ]` Sub-task: Create test results spreadsheet
  - `[ ]` Sub-task: Document issues found on each platform
  - `[ ]` Sub-task: Prioritize issues (critical, major, minor)
  - `[ ]` Sub-task: Create GitHub issues for bugs
  - `[ ]` Sub-task: Document known limitations
  - `[ ]` Sub-task: Update README with platform requirements

---

### Day 60: Application Launcher Improvements
**Goal:** Improve application startup and user experience

- `[ ]` **Create Launcher Wrapper** (3h)
  - `[ ]` Sub-task: Create custom launcher script (optional)
  - `[ ]` Sub-task: Add environment variable checks
  - `[ ]` Sub-task: Add port availability check
  - `[ ]` Sub-task: Add browser auto-launch on startup
  - `[ ]` Sub-task: Add error handling and logging
  - `[ ]` Sub-task: Test launcher on all platforms

**Launcher Script Example (Shell/Batch):**
```java
// Add to FirestickApplication.java
package com.codetalkerl.firestick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class FirestickApplication {
    private static final Logger logger = LoggerFactory.getLogger(FirestickApplication.class);
    private static final String APP_URL = "http://localhost:8080";

    public static void main(String[] args) {
        // Start Spring Boot
        SpringApplication.run(FirestickApplication.class, args);
        
        // Auto-launch browser after startup
        launchBrowser();
    }

    private static void launchBrowser() {
        try {
            // Wait for server to be ready
            Thread.sleep(3000);
            
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(APP_URL));
                    logger.info("Launched browser at {}", APP_URL);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not launch browser automatically: {}", e.getMessage());
            logger.info("Please open your browser and navigate to {}", APP_URL);
        }
    }
}
```

- `[ ]` **Add Splash Screen** (2h)
  - `[ ]` Sub-task: Create splash screen image (400x300)
  - `[ ]` Sub-task: Configure jpackage to show splash on startup
  - `[ ]` Sub-task: Add progress indicator (optional)
  - `[ ]` Sub-task: Set splash display duration
  - `[ ]` Sub-task: Test splash screen appearance

- `[ ]` **Add System Tray Integration** (Optional) (2h)
  - `[ ]` Sub-task: Add system tray icon
  - `[ ]` Sub-task: Add tray menu (Open, Exit)
  - `[ ]` Sub-task: Show notification on startup
  - `[ ]` Sub-task: Allow minimize to tray
  - `[ ]` Sub-task: Test on all platforms

**System Tray Example:**
```java
// src/main/java/com/codetalkerl/firestick/SystemTrayManager.java
package com.codetalkerl.firestick;

import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
public class SystemTrayManager {
    
    @PostConstruct
    public void init() {
        if (!SystemTray.isSupported()) {
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            
            // Load icon
            Image icon = Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("/icons/tray-icon.png"));
            
            // Create popup menu
            PopupMenu popup = new PopupMenu();
            
            MenuItem openItem = new MenuItem("Open Firestick");
            openItem.addActionListener(e -> openBrowser());
            popup.add(openItem);
            
            popup.addSeparator();
            
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(exitItem);
            
            // Create tray icon
            TrayIcon trayIcon = new TrayIcon(icon, "Firestick", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> openBrowser());
            
            tray.add(trayIcon);
            
            // Show notification
            trayIcon.displayMessage(
                "Firestick",
                "Application is running on http://localhost:8080",
                TrayIcon.MessageType.INFO
            );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- `[ ]` **Improve Error Messages** (1h)
  - `[ ]` Sub-task: Add friendly error dialogs
  - `[ ]` Sub-task: Show port conflict errors
  - `[ ]` Sub-task: Show missing dependency errors
  - `[ ]` Sub-task: Add troubleshooting links
  - `[ ]` Sub-task: Test error scenarios

---

### Day 61: Documentation & User Guide
**Goal:** Create comprehensive user documentation for packaged application

- `[ ]` **Create Installation Guide** (3h)
  - `[ ]` Sub-task: Write Windows installation instructions
  - `[ ]` Sub-task: Write macOS installation instructions
  - `[ ]` Sub-task: Write Linux installation instructions
  - `[ ]` Sub-task: Add screenshots for each step
  - `[ ]` Sub-task: Document system requirements
  - `[ ]` Sub-task: Document troubleshooting steps
  - `[ ]` Sub-task: Create FAQ section

**Installation Guide Template:**
```markdown
# Firestick Installation Guide

## System Requirements

### All Platforms
- RAM: 2GB minimum, 4GB recommended
- Disk Space: 500MB for application + space for indexed projects
- Internet: Required for initial download only

### Windows
- Windows 10 or later (64-bit)
- No additional software required

### macOS
- macOS 12 (Monterey) or later
- No additional software required

### Linux
- Ubuntu 20.04+ / Debian 11+ / Fedora 36+
- No additional software required

## Installation

### Windows

1. Download `Firestick-1.0.0.msi` from the releases page
2. Double-click the installer file
3. Follow the installation wizard
4. Click "Install" (may require administrator privileges)
5. Wait for installation to complete
6. Click "Finish"
7. Launch Firestick from the Start Menu

**Troubleshooting:**
- If you see "Windows protected your PC", click "More info" then "Run anyway"
- If installation fails, ensure you have administrator privileges

### macOS

1. Download `Firestick-1.0.0.dmg` from the releases page
2. Double-click the DMG file to mount it
3. Drag the Firestick icon to the Applications folder
4. Eject the DMG
5. Open Applications and double-click Firestick
6. If you see "Firestick cannot be opened", right-click and select "Open"

**Troubleshooting:**
- If you see "cannot be opened because it is from an unidentified developer":
  - Right-click the app and select "Open"
  - Click "Open" in the dialog
- Grant permissions if macOS asks for Accessibility or Files access

### Linux (Debian/Ubuntu)

1. Download `firestick_1.0.0-1_amd64.deb`
2. Install using package manager:
   ```bash
   sudo dpkg -i firestick_1.0.0-1_amd64.deb
   sudo apt-get install -f  # Install dependencies if needed
   ```
3. Launch from Applications menu or run `firestick` in terminal

### Linux (Fedora/RHEL)

1. Download `firestick-1.0.0-1.x86_64.rpm`
2. Install using package manager:
   ```bash
   sudo rpm -i firestick-1.0.0-1.x86_64.rpm
   ```
3. Launch from Applications menu or run `firestick` in terminal

## First Launch

1. Firestick will start and automatically open in your browser
2. If browser doesn't open, navigate to http://localhost:8080
3. Click "Settings" to configure your first project
4. Enter the path to your project directory
5. Click "Start Indexing"
6. Wait for indexing to complete
7. Start searching!

## Uninstallation

### Windows
1. Go to Settings > Apps > Installed apps
2. Find "Firestick"
3. Click the three dots and select "Uninstall"

### macOS
1. Open Applications folder
2. Drag Firestick to Trash
3. Empty Trash

### Linux (Debian/Ubuntu)
```bash
sudo apt-get remove firestick
```

### Linux (Fedora/RHEL)
```bash
sudo rpm -e firestick
```

## Data Location

Firestick stores data in:
- **Windows:** `C:\Users\<YourName>\.firestick\`
- **macOS:** `/Users/<YourName>/.firestick/`
- **Linux:** `/home/<YourName>/.firestick/`

This data is NOT removed when you uninstall. Delete manually if needed.
```

- `[ ]` **Create User Guide** (3h)
  - `[ ]` Sub-task: Write "Getting Started" section
  - `[ ]` Sub-task: Document all features with screenshots
  - `[ ]` Sub-task: Create search tips and tricks
  - `[ ]` Sub-task: Document analysis features
  - `[ ]` Sub-task: Add keyboard shortcuts reference
  - `[ ]` Sub-task: Create video tutorials (optional)

- `[ ]` **Create Developer Documentation** (2h)
  - `[ ]` Sub-task: Document build process
  - `[ ]` Sub-task: Document packaging configuration
  - `[ ]` Sub-task: Document troubleshooting build issues
  - `[ ]` Sub-task: Document how to modify installers
  - `[ ]` Sub-task: Document release process
  - `[ ]` Sub-task: Add contribution guidelines

---

### Day 62: Release Preparation
**Goal:** Prepare for official release

- `[ ]` **Create Release Notes** (2h)
  - `[ ]` Sub-task: List all features in v1.0.0
  - `[ ]` Sub-task: List known issues
  - `[ ]` Sub-task: List system requirements
  - `[ ]` Sub-task: Add upgrade instructions (if applicable)
  - `[ ]` Sub-task: Add credits and acknowledgments
  - `[ ]` Sub-task: Format release notes in markdown

**Release Notes Template:**
```markdown
# Firestick v1.0.0 Release Notes

## 🎉 What's New

Firestick is a standalone desktop application for analyzing and searching legacy codebases. This is the initial release!

### Features

#### Search
- **Semantic Search** - Find code by meaning, not just keywords
- **Keyword Search** - Traditional text-based search with filters
- **Hybrid Search** - Best of both worlds
- **Auto-complete** - Smart suggestions as you type
- **Symbol Search** - Find classes, methods, and fields by name

#### Analysis
- **Cyclomatic Complexity** - Identify complex methods
- **Code Smells** - Detect anti-patterns and bad practices
- **Dead Code Detection** - Find unused methods and classes
- **Pattern Recognition** - Identify design patterns
- **Dependency Graphs** - Visualize code dependencies

#### User Interface
- **Modern Web UI** - Clean, responsive interface
- **Code Viewer** - Syntax-highlighted code display
- **Interactive Graphs** - Explore dependencies visually
- **Analysis Dashboard** - Overview of code quality metrics
- **Dark/Light Theme** - Choose your preferred appearance

### Supported Platforms

- Windows 10/11 (64-bit)
- macOS 12+ (Monterey, Ventura, Sonoma)
- Linux (Ubuntu 20.04+, Debian 11+, Fedora 36+)

### System Requirements

- RAM: 2GB minimum, 4GB recommended
- Disk: 500MB + space for indexed projects
- No Java installation required!

## 🐛 Known Issues

- Large projects (>100K files) may take a while to index
- First search may be slow while embedding model loads
- Graph visualization may be slow for very large dependency graphs

## 📥 Download

Choose the installer for your platform:

- **Windows:** [Firestick-1.0.0.msi](link)
- **macOS:** [Firestick-1.0.0.dmg](link)
- **Linux (deb):** [firestick_1.0.0-1_amd64.deb](link)
- **Linux (rpm):** [firestick-1.0.0-1.x86_64.rpm](link)

## 📖 Documentation

- [Installation Guide](link)
- [User Guide](link)
- [FAQ](link)

## 🙏 Credits

Built with:
- Spring Boot 3.5.6
- React 18
- JavaParser 3.26.3
- Apache Lucene 9.12.0
- Chroma Vector Database
- Monaco Editor

## 📄 License

MIT License - see LICENSE file for details
```

- `[ ]` **Prepare GitHub Release** (2h)
  - `[ ]` Sub-task: Tag version in git: `git tag v1.0.0`
  - `[ ]` Sub-task: Push tag: `git push origin v1.0.0`
  - `[ ]` Sub-task: Create GitHub release page
  - `[ ]` Sub-task: Upload all installers (.msi, .dmg, .deb, .rpm)
  - `[ ]` Sub-task: Add release notes
  - `[ ]` Sub-task: Mark as "Latest Release"

- `[ ]` **Create Checksums** (1h)
  - `[ ]` Sub-task: Generate SHA-256 checksums for all installers
  - `[ ]` Sub-task: Create `checksums.txt` file
  - `[ ]` Sub-task: Add checksums to release page
  - `[ ]` Sub-task: Document how to verify checksums

**Checksum Generation:**
```bash
# Linux/macOS
sha256sum firestick-1.0.0.msi > checksums.txt
sha256sum Firestick-1.0.0.dmg >> checksums.txt
sha256sum firestick_1.0.0-1_amd64.deb >> checksums.txt
sha256sum firestick-1.0.0-1.x86_64.rpm >> checksums.txt

# Windows (PowerShell)
Get-FileHash -Algorithm SHA256 Firestick-1.0.0.msi | Format-List > checksums.txt
```

- `[ ]` **Update README** (2h)
  - `[ ]` Sub-task: Add download links
  - `[ ]` Sub-task: Add screenshots/GIFs
  - `[ ]` Sub-task: Update installation instructions
  - `[ ]` Sub-task: Add badge for latest release
  - `[ ]` Sub-task: Update feature list
  - `[ ]` Sub-task: Add link to documentation

- `[ ]` **Final Quality Check** (1h)
  - `[ ]` Sub-task: Download each installer from GitHub
  - `[ ]` Sub-task: Verify checksums
  - `[ ]` Sub-task: Test each installer on clean machine
  - `[ ]` Sub-task: Verify all links in documentation work
  - `[ ]` Sub-task: Check for typos
  - `[ ]` Sub-task: Get final approval

---

## Phase 6 Success Criteria

Before announcing the release, verify ALL of the following:

### Packaging Requirements
- ⬜ Windows .msi installer builds successfully
- ⬜ macOS .dmg installer builds successfully
- ⬜ Linux .deb package builds successfully
- ⬜ Linux .rpm package builds successfully
- ⬜ All installers include embedded JRE
- ⬜ All installers are under 150MB

### Installation Requirements
- ⬜ Windows installer works on clean Windows 10/11
- ⬜ macOS installer works on clean macOS 12+
- ⬜ Linux installers work on Ubuntu/Debian/Fedora
- ⬜ No manual Java installation required
- ⬜ Application appears in system menus
- ⬜ Desktop shortcuts created (where applicable)
- ⬜ Uninstaller works cleanly

### Application Requirements
- ⬜ Application launches successfully on all platforms
- ⬜ Browser opens automatically on startup
- ⬜ All features work as expected
- ⬜ Data persists in user directory
- ⬜ No console window shown (Windows)
- ⬜ Application icon displays correctly
- ⬜ Application name shows correctly in task manager

### User Experience Requirements
- ⬜ Installation is simple (< 5 clicks)
- ⬜ First launch is intuitive
- ⬜ No errors on fresh install
- ⬜ Error messages are helpful
- ⬜ Application starts in < 10 seconds

### Documentation Requirements
- ⬜ Installation guide is complete and accurate
- ⬜ User guide covers all features
- ⬜ Screenshots are clear and helpful
- ⬜ Troubleshooting section is comprehensive
- ⬜ System requirements are documented
- ⬜ Known issues are listed

### Release Requirements
- ⬜ Version number is correct (1.0.0)
- ⬜ Release notes are complete
- ⬜ All installers uploaded to GitHub
- ⬜ Checksums are generated and verified
- ⬜ README is updated with download links
- ⬜ LICENSE file is included
- ⬜ Git tag is created and pushed

---

## Common Issues & Troubleshooting

### Issue: jpackage command not found
**Solution:**
- Ensure JDK 21 is installed (not just JRE)
- Check that JDK bin directory is in PATH
- Run `jpackage --version` to verify
- On Windows, may need to restart terminal after JDK install

### Issue: WiX Toolset error on Windows
**Solution:**
- Install WiX Toolset 3.11 or later
- Add WiX to PATH: `C:\Program Files (x86)\WiX Toolset v3.11\bin`
- Restart terminal
- Alternative: Use `--type exe` instead of `--type msi`

### Issue: Icon not showing in installer
**Solution:**
- Verify icon file exists at specified path
- Windows: Use .ico file (256x256 or multiple sizes)
- macOS: Use .icns file
- Linux: Use .png file (256x256)
- Use absolute path or path relative to project root

### Issue: Application won't launch after install
**Solution:**
- Check main class is correct in jpackage command
- Verify JAR is executable: `java -jar target/firestick-1.0.0.jar`
- Check logs in user directory: `~/.firestick/logs/`
- Ensure all dependencies are in JAR
- Check Java options aren't too restrictive (heap size)

### Issue: Large installer size
**Solution:**
- Use jlink to create custom JRE (removes unused modules)
- Exclude debug symbols: `--strip-debug`
- Compress JRE: `--compress=2`
- Remove unnecessary dependencies from JAR
- Expected size: 80-120MB is normal with embedded JRE

### Issue: macOS Gatekeeper blocks app
**Solution:**
- Sign the application with Apple Developer ID
- Or: Right-click app → Open → Open anyway
- Add instructions to user guide
- Consider notarization for public distribution

### Issue: Port 8080 already in use
**Solution:**
- Add port conflict detection in launcher
- Show error dialog with instructions
- Option to change port in settings
- Or auto-select available port

### Issue: Data not persisting
**Solution:**
- Check user has write permissions to ~/.firestick
- Verify H2 database path is correct
- Check Spring Boot configuration
- Look for errors in application logs

---

## Phase 6 Sprint Review Checklist

**Demo Items:**
- [ ] Show Windows installation process
- [ ] Show macOS installation process
- [ ] Show Linux installation process
- [ ] Show application launching automatically
- [ ] Show browser opening to UI
- [ ] Show all features working in packaged version
- [ ] Show uninstallation process
- [ ] Show installation guide documentation
- [ ] Show GitHub release page

**Retrospective Questions:**
- Was the packaging process smooth?
- Are installers easy to use?
- What issues did testers encounter?
- Is the documentation clear enough?
- What improvements are needed for v1.1?

---
---

# Phase 7: Optimization & Polish (Week 12)

**Status:** Not Started  
**Goal:** Optimize performance, enhance user experience, and finalize documentation  
**Team:** Full Team (Backend, Frontend, QA)  
**Duration:** Jan 5 - Jan 11, 2026 (1 week, Sprint 6)  
**Dependencies:** Phase 6 (Desktop Packaging) must be complete

---

## Phase 7 Overview

### What We're Optimizing
The final week before release focuses on polish and performance:
1. **Performance Tuning** - Make everything faster and more efficient
2. **User Experience** - Smooth out rough edges and improve usability
3. **Documentation** - Ensure everything is well-documented
4. **Bug Fixes** - Address remaining issues
5. **Final Testing** - Comprehensive QA across all features
6. **Release Readiness** - Ensure we're ready to ship

### Why This Matters
This is the difference between a "working" application and a "great" application:
- Users notice performance - fast apps feel professional
- Small UX improvements have big impact on satisfaction
- Good documentation reduces support burden
- Catching bugs now prevents user frustration
- Polish makes the application feel finished

### Success Metrics
- Search response time < 1s for 90% of queries
- UI feels responsive (no lag)
- Zero critical bugs
- Documentation covers all features
- All team members can demo the app confidently

---

## Week 12: Final Polish (Sprint 6)

### Day 63: Performance Profiling & Database Optimization
**Goal:** Identify and fix performance bottlenecks in backend

- `[ ]` **Profile Backend Performance** (2h)
  - `[ ]` Sub-task: Add Spring Boot Actuator metrics
  - `[ ]` Sub-task: Enable performance logging
  - `[ ]` Sub-task: Profile search endpoint with large dataset
  - `[ ]` Sub-task: Profile analysis endpoints
  - `[ ]` Sub-task: Identify slow queries and operations
  - `[ ]` Sub-task: Document baseline performance metrics

**Add Spring Boot Actuator:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.enable.jvm=true
management.metrics.enable.process=true
management.metrics.enable.system=true
```

**Performance Logging:**
```java
// Add to application.properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.com.codetalkerl.firestick=DEBUG

# Log slow queries
spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=100
```

- `[ ]` **Optimize Database Queries** (3h)
  - `[ ]` Sub-task: Add database indexes on frequently queried columns
  - `[ ]` Sub-task: Optimize JPA queries (use fetch joins where needed)
  - `[ ]` Sub-task: Add query result caching
  - `[ ]` Sub-task: Batch database operations
  - `[ ]` Sub-task: Use pagination for large result sets
  - `[ ]` Sub-task: Measure query performance improvements

**Database Optimization Example:**
```java
// Add indexes to entities
@Entity
@Table(name = "code_chunks",
       indexes = {
           @Index(name = "idx_file_path", columnList = "filePath"),
           @Index(name = "idx_chunk_type", columnList = "chunkType"),
           @Index(name = "idx_class_name", columnList = "className")
       })
public class CodeChunk {
    // ... existing code
}

// Optimize queries with fetch joins
@Query("SELECT c FROM CodeChunk c " +
       "LEFT JOIN FETCH c.metadata " +
       "WHERE c.filePath LIKE :pattern")
List<CodeChunk> findByFilePathPattern(@Param("pattern") String pattern);

// Add caching
@Cacheable(value = "codeChunks", key = "#id")
public CodeChunk findById(Long id) {
    return repository.findById(id).orElse(null);
}
```

- `[ ]` **Optimize Embedding Generation** (2h)
  - `[ ]` Sub-task: Implement batch embedding generation (process multiple at once)
  - `[ ]` Sub-task: Cache embeddings to avoid recomputation
  - `[ ]` Sub-task: Optimize ONNX model loading (load once, reuse)
  - `[ ]` Sub-task: Use parallel processing for large batches
  - `[ ]` Sub-task: Measure embedding performance improvements

**Batch Embedding Optimization:**
```java
// EmbeddingService.java
@Service
public class EmbeddingService {
    private static final int BATCH_SIZE = 32;
    private OrtSession session; // Reuse session
    
    @PostConstruct
    public void init() {
        // Load model once at startup
        this.session = loadModel();
    }
    
    public List<float[]> generateEmbeddingsBatch(List<String> texts) {
        List<float[]> embeddings = new ArrayList<>();
        
        // Process in batches
        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);
            
            // Generate embeddings for batch in parallel
            List<float[]> batchEmbeddings = batch.parallelStream()
                .map(this::generateEmbedding)
                .collect(Collectors.toList());
            
            embeddings.addAll(batchEmbeddings);
        }
        
        return embeddings;
    }
    
    @Cacheable(value = "embeddings", key = "#text.hashCode()")
    public float[] generateEmbedding(String text) {
        // Use cached session
        return runInference(session, text);
    }
}
```

- `[ ]` **Optimize Chroma Vector Search** (1h)
  - `[ ]` Sub-task: Tune Chroma collection settings
  - `[ ]` Sub-task: Optimize vector search parameters
  - `[ ]` Sub-task: Add connection pooling
  - `[ ]` Sub-task: Cache frequent queries
  - `[ ]` Sub-task: Measure search latency improvements

---

### Day 64: Frontend Performance Optimization
**Goal:** Make the UI fast and responsive

- `[ ]` **Optimize React Bundle Size** (2h)
  - `[ ]` Sub-task: Analyze bundle with `npm run build -- --analyze`
  - `[ ]` Sub-task: Implement code splitting for routes
  - `[ ]` Sub-task: Lazy load heavy components (Monaco, Charts)
  - `[ ]` Sub-task: Remove unused dependencies
  - `[ ]` Sub-task: Tree-shake imports
  - `[ ]` Sub-task: Measure bundle size reduction

**Code Splitting Example:**
```javascript
// src/App.jsx - Lazy load route components
import React, { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { CircularProgress } from '@mui/material';

// Lazy load pages
const SearchPage = lazy(() => import('./pages/SearchPage'));
const AnalysisDashboard = lazy(() => import('./pages/AnalysisDashboard'));
const CodeViewer = lazy(() => import('./pages/CodeViewer'));
const DependencyGraph = lazy(() => import('./pages/DependencyGraph'));
const SettingsPage = lazy(() => import('./pages/SettingsPage'));

// Loading fallback
const LoadingFallback = () => (
  <div style={{ display: 'flex', justifyContent: 'center', padding: '50px' }}>
    <CircularProgress />
  </div>
);

function App() {
  return (
    <BrowserRouter>
      <Suspense fallback={<LoadingFallback />}>
        <Routes>
          <Route path="/" element={<SearchPage />} />
          <Route path="/analysis" element={<AnalysisDashboard />} />
          <Route path="/code" element={<CodeViewer />} />
          <Route path="/graph" element={<DependencyGraph />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}

export default App;
```

- `[ ]` **Optimize React Rendering** (3h)
  - `[ ]` Sub-task: Use React.memo for expensive components
  - `[ ]` Sub-task: Use useMemo and useCallback appropriately
  - `[ ]` Sub-task: Implement virtual scrolling for long lists
  - `[ ]` Sub-task: Debounce expensive operations
  - `[ ]` Sub-task: Profile with React DevTools
  - `[ ]` Sub-task: Eliminate unnecessary re-renders

**React Performance Optimization Example:**
```javascript
// Memoize expensive components
const ResultCard = React.memo(({ result, onViewFile }) => {
  // Component implementation
}, (prevProps, nextProps) => {
  // Only re-render if result changed
  return prevProps.result.id === nextProps.result.id;
});

// Use useMemo for expensive computations
const ComplexityWidget = () => {
  const [data, setData] = useState([]);
  
  const chartData = useMemo(() => {
    // Expensive transformation
    return data.map(item => ({
      level: calculateLevel(item),
      count: calculateCount(item),
    }));
  }, [data]); // Only recalculate when data changes
  
  return <BarChart data={chartData} />;
};

// Virtual scrolling for long lists
import { FixedSizeList } from 'react-window';

const ResultsList = ({ results }) => {
  const Row = ({ index, style }) => (
    <div style={style}>
      <ResultCard result={results[index]} />
    </div>
  );
  
  return (
    <FixedSizeList
      height={600}
      itemCount={results.length}
      itemSize={200}
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
};
```

- `[ ]` **Optimize API Calls** (2h)
  - `[ ]` Sub-task: Implement request caching
  - `[ ]` Sub-task: Implement request deduplication
  - `[ ]` Sub-task: Add retry logic with exponential backoff
  - `[ ]` Sub-task: Cancel pending requests on component unmount
  - `[ ]` Sub-task: Use AbortController for cancellable requests

**API Optimization Example:**
```javascript
// src/hooks/useSearchResults.js
import { useState, useEffect, useRef } from 'react';
import { searchService } from '../services/searchService';

export const useSearchResults = (query) => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const abortControllerRef = useRef(null);
  
  useEffect(() => {
    if (!query) {
      setResults([]);
      return;
    }
    
    // Cancel previous request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    
    // Create new abort controller
    abortControllerRef.current = new AbortController();
    
    const fetchResults = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const data = await searchService.search(query, {
          signal: abortControllerRef.current.signal
        });
        setResults(data);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message);
        }
      } finally {
        setLoading(false);
      }
    };
    
    fetchResults();
    
    // Cleanup on unmount
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [query]);
  
  return { results, loading, error };
};
```

- `[ ]` **Add Loading Optimizations** (1h)
  - `[ ]` Sub-task: Add service worker for caching (optional)
  - `[ ]` Sub-task: Preload critical resources
  - `[ ]` Sub-task: Optimize images (compression, lazy loading)
  - `[ ]` Sub-task: Add skeleton screens for better perceived performance
  - `[ ]` Sub-task: Measure load time improvements

---

### Day 65: User Experience Improvements
**Goal:** Polish the UI and improve usability

- `[ ]` **Improve Error Handling** (2h)
  - `[ ]` Sub-task: Create consistent error message components
  - `[ ]` Sub-task: Add user-friendly error messages (no stack traces!)
  - `[ ]` Sub-task: Add "retry" buttons for failed operations
  - `[ ]` Sub-task: Add error boundaries for graceful degradation
  - `[ ]` Sub-task: Log errors for debugging
  - `[ ]` Sub-task: Test error scenarios

**Error Handling Example:**
```javascript
// src/components/ErrorBoundary.jsx
import React from 'react';
import { Alert, Button, Box } from '@mui/material';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <Box p={3}>
          <Alert severity="error">
            <h3>Oops! Something went wrong</h3>
            <p>We encountered an unexpected error. Please try refreshing the page.</p>
            {process.env.NODE_ENV === 'development' && (
              <pre>{this.state.error?.toString()}</pre>
            )}
            <Box mt={2}>
              <Button variant="contained" onClick={this.handleReset}>
                Try Again
              </Button>
              <Button 
                variant="outlined" 
                onClick={() => window.location.reload()}
                sx={{ ml: 1 }}
              >
                Refresh Page
              </Button>
            </Box>
          </Alert>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

- `[ ]` **Add Keyboard Shortcuts** (2h)
  - `[ ]` Sub-task: Implement common shortcuts (Ctrl+K for search, Esc to close)
  - `[ ]` Sub-task: Add shortcuts for navigation
  - `[ ]` Sub-task: Add shortcuts help dialog (? key)
  - `[ ]` Sub-task: Make shortcuts customizable (optional)
  - `[ ]` Sub-task: Test shortcuts on all platforms

**Keyboard Shortcuts Example:**
```javascript
// src/hooks/useKeyboardShortcuts.js
import { useEffect } from 'react';

export const useKeyboardShortcuts = () => {
  useEffect(() => {
    const handleKeyPress = (e) => {
      // Ctrl/Cmd + K - Focus search
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.getElementById('search-input')?.focus();
      }
      
      // Escape - Close modals/clear search
      if (e.key === 'Escape') {
        // Implement close logic
      }
      
      // ? - Show help
      if (e.key === '?') {
        // Show keyboard shortcuts help
      }
    };
    
    window.addEventListener('keydown', handleKeyPress);
    return () => window.removeEventListener('keydown', handleKeyPress);
  }, []);
};
```

- `[ ]` **Improve Loading States** (2h)
  - `[ ]` Sub-task: Replace all spinners with skeleton screens
  - `[ ]` Sub-task: Add progress indicators for long operations
  - `[ ]` Sub-task: Add optimistic UI updates
  - `[ ]` Sub-task: Show meaningful loading messages
  - `[ ]` Sub-task: Ensure loading states are consistent

- `[ ]` **Add User Feedback** (2h)
  - `[ ]` Sub-task: Add success messages for actions
  - `[ ]` Sub-task: Add confirmation dialogs for destructive actions
  - `[ ]` Sub-task: Add toast notifications for background operations
  - `[ ]` Sub-task: Add progress feedback for indexing
  - `[ ]` Sub-task: Make feedback consistent across app

**Toast Notifications Example:**
```javascript
// src/contexts/NotificationContext.jsx
import React, { createContext, useContext, useState } from 'react';
import { Snackbar, Alert } from '@mui/material';

const NotificationContext = createContext();

export const useNotification = () => useContext(NotificationContext);

export const NotificationProvider = ({ children }) => {
  const [notification, setNotification] = useState(null);

  const showNotification = (message, severity = 'info') => {
    setNotification({ message, severity });
  };

  const hideNotification = () => {
    setNotification(null);
  };

  return (
    <NotificationContext.Provider value={{ showNotification }}>
      {children}
      <Snackbar
        open={!!notification}
        autoHideDuration={6000}
        onClose={hideNotification}
        anchorOrigin={{ vertical: 'bottom', right: 'right' }}
      >
        {notification && (
          <Alert onClose={hideNotification} severity={notification.severity}>
            {notification.message}
          </Alert>
        )}
      </Snackbar>
    </NotificationContext.Provider>
  );
};

// Usage in components
const SomeComponent = () => {
  const { showNotification } = useNotification();
  
  const handleSave = async () => {
    try {
      await saveData();
      showNotification('Settings saved successfully!', 'success');
    } catch (error) {
      showNotification('Failed to save settings', 'error');
    }
  };
};
```

---

### Day 66: Accessibility & Browser Compatibility
**Goal:** Ensure app is accessible and works across browsers

- `[ ]` **Accessibility Audit** (3h)
  - `[ ]` Sub-task: Run accessibility audit with browser tools
  - `[ ]` Sub-task: Test keyboard navigation on all pages
  - `[ ]` Sub-task: Add ARIA labels to interactive elements
  - `[ ]` Sub-task: Ensure proper heading hierarchy (h1 → h2 → h3)
  - `[ ]` Sub-task: Test with screen reader (NVDA or VoiceOver)
  - `[ ]` Sub-task: Ensure color contrast meets WCAG AA standards
  - `[ ]` Sub-task: Add focus indicators to all interactive elements

**Accessibility Improvements:**
```javascript
// Add ARIA labels
<button 
  aria-label="Search code"
  onClick={handleSearch}
>
  <SearchIcon />
</button>

// Proper heading hierarchy
<h1>Firestick - Code Analysis</h1>
  <h2>Search Results</h2>
    <h3>Result 1</h3>
    <h3>Result 2</h3>
  <h2>Filters</h2>

// Keyboard navigation
<div
  role="button"
  tabIndex={0}
  onClick={handleClick}
  onKeyPress={(e) => e.key === 'Enter' && handleClick()}
>
  Click me
</div>

// Focus visible
.focusable:focus-visible {
  outline: 2px solid #2196f3;
  outline-offset: 2px;
}
```

- `[ ]` **Cross-Browser Testing** (2h)
  - `[ ]` Sub-task: Test in Chrome (latest)
  - `[ ]` Sub-task: Test in Firefox (latest)
  - `[ ]` Sub-task: Test in Safari (latest)
  - `[ ]` Sub-task: Test in Edge (latest)
  - `[ ]` Sub-task: Document browser-specific issues
  - `[ ]` Sub-task: Add polyfills if needed
  - `[ ]` Sub-task: Test on older browser versions (optional)

- `[ ]` **Mobile Responsiveness** (2h)
  - `[ ]` Sub-task: Test on mobile devices (iOS, Android)
  - `[ ]` Sub-task: Ensure touch targets are large enough (44x44px minimum)
  - `[ ]` Sub-task: Test gestures work correctly
  - `[ ]` Sub-task: Ensure text is readable without zooming
  - `[ ]` Sub-task: Test on various screen sizes
  - `[ ]` Sub-task: Fix any mobile-specific issues

- `[ ]` **Add Print Styles** (1h)
  - `[ ]` Sub-task: Create print-friendly CSS
  - `[ ]` Sub-task: Hide unnecessary elements when printing
  - `[ ]` Sub-task: Ensure code is readable when printed
  - `[ ]` Sub-task: Test printing from all major pages

---

### Day 67: Documentation Polish
**Goal:** Ensure all documentation is complete and accurate

- `[ ]` **Update API Documentation** (2h)
  - `[ ]` Sub-task: Document all REST endpoints with examples
  - `[ ]` Sub-task: Add request/response schemas
  - `[ ]` Sub-task: Document error codes and messages
  - `[ ]` Sub-task: Add API usage examples
  - `[ ]` Sub-task: Consider adding Swagger/OpenAPI spec
  - `[ ]` Sub-task: Test all API examples work

**Swagger Configuration:**
```xml
<!-- pom.xml - Add Springdoc OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

```java
// Configure Swagger
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI firestickOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Firestick API")
                .description("Legacy Code Analysis and Search API")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("CodeTalkerl")
                    .email("support@codetalkerl.com"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")));
    }
}

// Add to controllers
@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Code search operations")
public class SearchController {
    
    @Operation(summary = "Search code by query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search successful"),
        @ApiResponse(responseCode = "400", description = "Invalid query"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping
    public SearchResponse search(
        @Parameter(description = "Search query") @RequestBody SearchRequest request
    ) {
        // Implementation
    }
}
```

- `[ ]` **Create Developer Guide** (3h)
  - `[ ]` Sub-task: Document architecture and design decisions
  - `[ ]` Sub-task: Document database schema
  - `[ ]` Sub-task: Document code organization
  - `[ ]` Sub-task: Add contribution guidelines
  - `[ ]` Sub-task: Document how to add new features
  - `[ ]` Sub-task: Document troubleshooting steps
  - `[ ]` Sub-task: Add diagrams (architecture, flow, etc.)

**Developer Guide Structure:**
```markdown
# Firestick Developer Guide

## Architecture Overview
- High-level architecture diagram
- Component relationships
- Data flow diagrams

## Technology Stack
- Backend: Spring Boot, H2, Lucene, JavaParser
- Frontend: React, Material-UI, Monaco Editor
- Vector DB: Chroma with ONNX embeddings

## Project Structure
```
firestick/
  src/main/java/           # Backend code
  src/main/resources/      # Configuration and static files
  firestick-ui/           # React frontend
  packaging/              # Installer scripts
```

## Database Schema
- Tables and relationships
- Indexes and constraints
- Sample queries

## API Design
- RESTful conventions
- Error handling strategy
- Authentication (if applicable)

## Adding New Features
1. Backend changes
2. Frontend changes
3. Testing requirements
4. Documentation updates

## Testing
- Unit tests
- Integration tests
- E2E tests

## Troubleshooting
- Common issues and solutions
```

- `[ ]` **Update User Guide** (2h)
  - `[ ]` Sub-task: Add screenshots for all features
  - `[ ]` Sub-task: Create step-by-step tutorials
  - `[ ]` Sub-task: Add FAQ section
  - `[ ]` Sub-task: Add tips and tricks
  - `[ ]` Sub-task: Document keyboard shortcuts
  - `[ ]` Sub-task: Add video tutorial links (if available)

- `[ ]` **Code Documentation** (1h)
  - `[ ]` Sub-task: Review all public methods have Javadoc
  - `[ ]` Sub-task: Add inline comments for complex logic
  - `[ ]` Sub-task: Document configuration properties
  - `[ ]` Sub-task: Add README files to key directories
  - `[ ]` Sub-task: Ensure code is self-documenting

---

### Day 68: Comprehensive Testing
**Goal:** Thoroughly test all features and fix remaining bugs

- `[ ]` **End-to-End Testing** (3h)
  - `[ ]` Sub-task: Test complete indexing workflow
  - `[ ]` Sub-task: Test all search variations (semantic, keyword, hybrid)
  - `[ ]` Sub-task: Test all analysis features
  - `[ ]` Sub-task: Test code viewer functionality
  - `[ ]` Sub-task: Test dependency graph
  - `[ ]` Sub-task: Test settings and configuration
  - `[ ]` Sub-task: Document test results

**E2E Test Checklist:**
```markdown
## Search Flow
- [ ] Can search by text query
- [ ] Auto-complete shows suggestions
- [ ] Filters work correctly
- [ ] Results display with highlighting
- [ ] Can navigate to code from results
- [ ] Search history works
- [ ] Can clear search

## Analysis Flow
- [ ] Can run complexity analysis
- [ ] Dashboard shows correct metrics
- [ ] Can view code smells
- [ ] Dead code report is accurate
- [ ] Patterns are detected
- [ ] Can export reports

## Code Viewer
- [ ] Files load correctly
- [ ] Syntax highlighting works
- [ ] Can jump to line numbers
- [ ] Can navigate between files
- [ ] Copy/download functions work

## Dependency Graph
- [ ] Graph renders correctly
- [ ] Can zoom and pan
- [ ] Nodes are clickable
- [ ] Layout algorithms work
- [ ] Can export graph

## Settings
- [ ] Can change indexing path
- [ ] Exclusions work
- [ ] Search preferences apply
- [ ] Theme changes work
- [ ] Settings persist
```

- `[ ]` **Edge Case Testing** (2h)
  - `[ ]` Sub-task: Test with empty project
  - `[ ]` Sub-task: Test with very large project (100K+ files)
  - `[ ]` Sub-task: Test with no results found
  - `[ ]` Sub-task: Test with special characters in queries
  - `[ ]` Sub-task: Test with network errors
  - `[ ]` Sub-task: Test with corrupted data
  - `[ ]` Sub-task: Document edge cases handled

- `[ ]` **Performance Testing** (2h)
  - `[ ]` Sub-task: Test with 1000+ search results
  - `[ ]` Sub-task: Test indexing performance on large codebase
  - `[ ]` Sub-task: Test concurrent users (if applicable)
  - `[ ]` Sub-task: Measure memory usage under load
  - `[ ]` Sub-task: Test startup time
  - `[ ]` Sub-task: Document performance benchmarks

- `[ ]` **Security Review** (1h)
  - `[ ]` Sub-task: Review for SQL injection vulnerabilities
  - `[ ]` Sub-task: Check for XSS vulnerabilities
  - `[ ]` Sub-task: Review file access controls
  - `[ ]` Sub-task: Check for sensitive data in logs
  - `[ ]` Sub-task: Review dependency vulnerabilities (npm audit, OWASP)
  - `[ ]` Sub-task: Document security considerations

---

### Day 69: Bug Fixes & Final Polish
**Goal:** Fix all remaining bugs and add final polish

- `[ ]` **Bug Triage** (2h)
  - `[ ]` Sub-task: Review all open issues
  - `[ ]` Sub-task: Prioritize bugs (critical, major, minor)
  - `[ ]` Sub-task: Assign bugs to team members
  - `[ ]` Sub-task: Set deadline for critical/major bugs
  - `[ ]` Sub-task: Defer minor bugs to v1.1 if needed

- `[ ]` **Fix Critical Bugs** (4h)
  - `[ ]` Sub-task: Fix any crashes or data loss issues
  - `[ ]` Sub-task: Fix broken core functionality
  - `[ ]` Sub-task: Fix performance issues
  - `[ ]` Sub-task: Fix security vulnerabilities
  - `[ ]` Sub-task: Test fixes thoroughly
  - `[ ]` Sub-task: Update issue tracker

- `[ ]` **UI Polish** (2h)
  - `[ ]` Sub-task: Fix alignment and spacing issues
  - `[ ]` Sub-task: Ensure consistent styling
  - `[ ]` Sub-task: Fix any visual glitches
  - `[ ]` Sub-task: Improve animations and transitions
  - `[ ]` Sub-task: Add finishing touches
  - `[ ]` Sub-task: Get design review

---

## Phase 7 Success Criteria

Before releasing v1.0.0, verify ALL of the following:

### Performance Requirements
- ⬜ Search returns results in < 1s for 90% of queries
- ⬜ UI is responsive with no visible lag
- ⬜ Application starts in < 10 seconds
- ⬜ Indexing processes files at > 100 files/second
- ⬜ Memory usage stays under 2GB for typical projects
- ⬜ Bundle size is optimized (< 500KB initial JS)

### User Experience Requirements
- ⬜ All features are intuitive to use
- ⬜ Error messages are helpful and user-friendly
- ⬜ Loading states are clear and informative
- ⬜ Keyboard shortcuts work on all platforms
- ⬜ Mobile responsiveness works well
- ⬜ No broken links or dead-end flows

### Accessibility Requirements
- ⬜ Keyboard navigation works on all pages
- ⬜ ARIA labels are present where needed
- ⬜ Color contrast meets WCAG AA standards
- ⬜ Focus indicators are visible
- ⬜ Screen reader compatibility (basic)

### Browser Compatibility
- ⬜ Works in Chrome (latest)
- ⬜ Works in Firefox (latest)
- ⬜ Works in Safari (latest)
- ⬜ Works in Edge (latest)
- ⬜ No console errors in any browser

### Documentation Requirements
- ⬜ API documentation is complete
- ⬜ Developer guide is comprehensive
- ⬜ User guide covers all features
- ⬜ Installation guide is accurate
- ⬜ FAQ answers common questions
- ⬜ Code is well-commented

### Quality Requirements
- ⬜ Zero critical bugs
- ⬜ Zero major bugs
- ⬜ Minor bugs documented for v1.1
- ⬜ All tests pass
- ⬜ No security vulnerabilities
- ⬜ Code follows style guidelines

### Release Readiness
- ⬜ Version number is correct (1.0.0)
- ⬜ Release notes are finalized
- ⬜ All installers tested and working
- ⬜ Checksums generated
- ⬜ GitHub release prepared
- ⬜ Team is confident in release

---

## Common Issues & Troubleshooting

### Issue: Slow search performance
**Solution:**
- Check database indexes are created
- Verify Chroma is running and accessible
- Check embedding cache is working
- Profile queries to find bottleneck
- Consider increasing heap size
- Optimize batch sizes

### Issue: UI feels sluggish
**Solution:**
- Use React DevTools Profiler to find slow components
- Implement React.memo and useMemo
- Use virtual scrolling for long lists
- Reduce bundle size with code splitting
- Check for unnecessary re-renders
- Optimize images and assets

### Issue: High memory usage
**Solution:**
- Check for memory leaks (event listeners not cleaned up)
- Limit cache sizes
- Use pagination for large result sets
- Clear old data from memory
- Profile with JVM profiler
- Adjust heap size appropriately

### Issue: Application won't start
**Solution:**
- Check port 8080 is available
- Verify H2 database can be created
- Check file permissions for ~/.firestick
- Review application logs
- Check Java version compatibility
- Verify all dependencies are present

### Issue: Indexing is very slow
**Solution:**
- Check disk I/O performance
- Verify exclusion patterns are working
- Increase batch sizes
- Use parallel processing
- Check for unnecessary file reads
- Profile indexing code

### Issue: Tests are failing
**Solution:**
- Run tests individually to isolate issue
- Check test data is correct
- Verify database is in correct state
- Check for timing issues (add waits if needed)
- Review recent code changes
- Check test environment configuration

---

## Phase 7 Sprint Review Checklist

**Demo Items:**
- [ ] Show performance improvements (before/after metrics)
- [ ] Show optimized search speed
- [ ] Show smooth UI interactions
- [ ] Show accessibility features
- [ ] Show error handling improvements
- [ ] Show complete documentation
- [ ] Show final polished application
- [ ] Celebrate the team's work! 🎉

**Retrospective Questions:**
- What performance improvements had the biggest impact?
- What UX improvements do users appreciate most?
- What was the most challenging optimization?
- What would we do differently in the next project?
- What are we most proud of?
- What should we prioritize for v1.1?

---

## Release Checklist

**Final checks before v1.0.0 release:**

### Technical
- [ ] All tests pass
- [ ] No critical or major bugs
- [ ] Performance metrics meet targets
- [ ] Security review complete
- [ ] Code review complete
- [ ] All dependencies up to date
- [ ] No TODO/FIXME comments in critical code

### Documentation
- [ ] README is complete and accurate
- [ ] User guide is comprehensive
- [ ] Developer guide is up to date
- [ ] API documentation is complete
- [ ] Installation guides are tested
- [ ] Changelog is up to date
- [ ] License file is present

### Distribution
- [ ] All installers built and tested
- [ ] Checksums generated
- [ ] GitHub release created
- [ ] Release notes published
- [ ] Download links work
- [ ] Website updated (if applicable)

### Communication
- [ ] Team is aligned on release
- [ ] Support team is briefed
- [ ] Known issues documented
- [ ] Feedback channels ready
- [ ] Announcement prepared

### Post-Release
- [ ] Monitor for critical issues
- [ ] Respond to user feedback
- [ ] Plan for v1.1 based on feedback
- [ ] Celebrate success! 🎊

---

## Project Completion

**Congratulations! You've built Firestick from scratch!**

### What You've Accomplished

Over 12 weeks, you've built a complete desktop application including:

1. **Backend Services** - Spring Boot application with search, analysis, and indexing
2. **Vector Search** - Semantic search with embeddings and Chroma
3. **Code Analysis** - Complexity metrics, code smells, dead code detection
4. **Web Interface** - Modern React UI with rich visualizations
5. **Desktop Packaging** - Native installers for Windows, macOS, and Linux
6. **Professional Polish** - Performance optimization, documentation, and UX improvements

### Key Metrics

- **Lines of Code:** ~15,000+ (backend + frontend)
- **Features:** 25+ major features
- **Platforms:** 3 (Windows, macOS, Linux)
- **Team Skills:** Full-stack development, DevOps, UX design
- **Time:** 12 weeks from start to release

### What's Next?

**Version 1.1 Ideas:**
- Auto-update functionality
- Multi-project workspace
- Additional language support (Python, JavaScript)
- CI/CD integration
- Plugin system
- Performance improvements for very large codebases
- Team collaboration features
- Cloud sync

**Ongoing Activities:**
- Monitor user feedback
- Fix bugs as reported
- Improve documentation based on user questions
- Optimize performance based on real-world usage
- Plan new features for v2.0

**Community Building:**
- Respond to GitHub issues
- Accept pull requests
- Build user community
- Create tutorials and content
- Speak at conferences (optional)

### Lessons Learned

Document what you learned:
- Technical skills gained
- Project management insights
- Team collaboration best practices
- What worked well
- What could be improved
- Advice for next project

---

**Project Status:** ✅ COMPLETE  
**Version:** 1.0.0  
**Release Date:** January 11, 2026  
**Team:** Amazing! 👏

**Document Last Updated:** October 14, 2025  
**Final Review:** January 11, 2026

