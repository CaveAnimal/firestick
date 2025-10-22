This day focuses on preparing the codebase for Phase 2 development. See Phase 1 examples for logging and exception handling patterns.

### Example 11.1: Logback Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Console appender for development -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File appender for production -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/firestick.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- Daily rollover -->
            <fileNamePattern>logs/firestick.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- Keep 30 days worth of history -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Error file appender -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/firestick-error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/firestick-error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Set logging level for specific packages -->
    <logger name="com.codetalkerl.firestick" level="DEBUG"/>
    <logger name="org.springframework.web" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</configuration>
```

### Example 11.2: Custom Exceptions

```java
package com.codetalkerl.firestick.exception;

/**
 * Exception thrown when file discovery operations fail.
 */
public class FileDiscoveryException extends RuntimeException {
    public FileDiscoveryException(String message) {
        super(message);
    }
    
    public FileDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
package com.codetalkerl.firestick.exception;

/**
 * Exception thrown when code parsing operations fail.
 */
public class CodeParsingException extends RuntimeException {
    private final String filePath;
    
    public CodeParsingException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }
    
    public CodeParsingException(String message, String filePath, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }
    
    public String getFilePath() {
        return filePath;
    }
}
```

```java
package com.codetalkerl.firestick.exception;

/**
 * Exception thrown when indexing operations fail.
 */
public class IndexingException extends RuntimeException {
    public IndexingException(String message) {
        super(message);
    }
    
    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
package com.codetalkerl.firestick.exception;

/**
 * Exception thrown when embedding generation fails.
 */
public class EmbeddingException extends RuntimeException {
    public EmbeddingException(String message) {
        super(message);
    }
    
    public EmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Example 11.3: Global Exception Handler

```java
package com.codetalkerl.firestick.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileDiscoveryException.class)
    public ResponseEntity<Map<String, Object>> handleFileDiscoveryException(FileDiscoveryException ex) {
        logger.error("File discovery error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File discovery failed", ex.getMessage());
    }

    @ExceptionHandler(CodeParsingException.class)
    public ResponseEntity<Map<String, Object>> handleCodeParsingException(CodeParsingException ex) {
        logger.error("Code parsing error in file {}: {}", ex.getFilePath(), ex.getMessage(), ex);
        Map<String, Object> error = buildErrorMap(HttpStatus.UNPROCESSABLE_ENTITY, "Code parsing failed", ex.getMessage());
        error.put("filePath", ex.getFilePath());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(IndexingException.class)
    public ResponseEntity<Map<String, Object>> handleIndexingException(IndexingException ex) {
        logger.error("Indexing error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Indexing failed", ex.getMessage());
    }

    @ExceptionHandler(EmbeddingException.class)
    public ResponseEntity<Map<String, Object>> handleEmbeddingException(EmbeddingException ex) {
        logger.error("Embedding generation error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Embedding generation failed", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = buildErrorMap(status, error, message);
        return ResponseEntity.status(status).body(body);
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
```

---

## Day 12: File Discovery Service

### Example 12.1: FileDiscoveryService

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.config.IndexingConfig;
import com.codetalkerl.firestick.exception.FileDiscoveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for discovering Java files in a directory tree.
 */
@Service
public class FileDiscoveryService {
    private static final Logger logger = LoggerFactory.getLogger(FileDiscoveryService.class);
    
    private final IndexingConfig config;

    public FileDiscoveryService(IndexingConfig config) {
        this.config = config;
    }

    /**
     * Scan a directory recursively and find all Java files.
     *
     * @param rootPath Root directory to scan
     * @return List of paths to Java files
     * @throws FileDiscoveryException if scanning fails
     */
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        logger.info("Starting directory scan: {}", rootPath);
        
        Path root = Paths.get(rootPath);
        if (!Files.exists(root)) {
            throw new FileDiscoveryException("Directory does not exist: " + rootPath);
        }
        
        if (!Files.isDirectory(root)) {
            throw new FileDiscoveryException("Path is not a directory: " + rootPath);
        }
        
        List<Path> javaFiles = new ArrayList<>();
        
        try (Stream<Path> pathStream = Files.walk(root)) {
            pathStream
                .filter(Files::isRegularFile)
                .filter(this::isJavaFile)
                .filter(this::shouldInclude)
                .forEach(path -> {
                    javaFiles.add(path);
                    
                    if (javaFiles.size() % 100 == 0) {
                        logger.debug("Found {} files so far...", javaFiles.size());
                    }
                });
### Example 34.3: Analysis Service Tests
            .map(node -> (ClassOrInterfaceDeclaration) node)
            .forEach(innerClassDecl -> {
                ClassInfo innerClassInfo = extractClassInfo(innerClassDecl, fileInfo);
                innerClasses.add(innerClassInfo);
import com.codetalkerl.firestick.dto.ComplexityMetrics;
        
        return classInfo;

// Get package
Optional<PackageDeclaration> pkg = cu.getPackageDeclaration();
import java.util.Collections;
// Get imports
List<ImportDeclaration> imports = cu.getImports();
```

class ComplexityAnalyzerTest {

    private ComplexityAnalyzer analyzer;
   - Ensure using correct JavaParser version
   - Check for exotic Java features
   - Try updating to newer parser version
        analyzer = new ComplexityAnalyzer();
2. **Missing information from AST**
   - Use JavaParser symbol solver for type resolution
   - Configure symbol solver with project classpath
    void analyzeMethod_CalculatesMetrics() {
        // Given: Method with known properties
        MethodInfo method = new MethodInfo();
        method.setName("calculateTotal");
        method.setSignature("public double calculateTotal(List<Item> items)");
        method.setReturnType("double");
        method.setParameters(List.of("List<Item> items"));
        method.setBody("{\n    double total = 0;\n    for (Item item : items) {\n        total += item.getPrice();\n    }\n    return total;\n}");
        method.setModifiers(List.of("public"));
        method.setStartLine(10);
        method.setEndLine(20);
   - Add timeout for parsing
        // When: Analyze method
        ComplexityMetrics metrics = analyzer.analyzeMethod(method, "Invoice");

        // Then: Metrics should be calculated correctly
        assertThat(metrics.getCyclomaticComplexity()).isEqualTo(2);
        assertThat(metrics.getLinesOfCode()).isEqualTo(11);
        assertThat(metrics.getParameterCount()).isEqualTo(1);
        assertThat(metrics.getMaxNestingDepth()).isEqualTo(1);
        assertThat(metrics.getComplexityLevel()).isEqualTo("LOW");
        assertThat(metrics.getRecommendations()).isEmpty();

```java
package com.codetalkerl.firestick.service;
    void detectSmells_LongMethod_AddsSmell() {
        // Given: Method with too many lines
        MethodInfo method = new MethodInfo();
        method.setName("longRunningProcess");
        method.setBody("""
                {
                    // Simulate long process
                    for (int i = 0; i < 100; i++) {
                        System.out.println("Processing " + i);
                    }
                }
                """);
        method.setStartLine(5);
        method.setEndLine(105);
import com.codetalkerl.firestick.dto.FileInfo;
        // When: Analyze method
        List<CodeSmell> smells = analyzer.detectMethodSmells(method, "Processor");
import org.slf4j.LoggerFactory;
        // Then: Should detect LONG_METHOD smell
        assertThat(smells).anyMatch(s -> s.getType().equals("LONG_METHOD"));
 * - Method-level chunks (method with class context)
 * - Class-level chunks (class summary with key methods)
 * - File-level chunks (package and imports overview)
    void detectSmells_TooManyParameters_AddsSmell() {
        // Given: Method with too many parameters
        MethodInfo method = new MethodInfo();
        method.setName("complexCalculation");
        method.setParameters(List.of("int a", "int b", "int c", "int d", "int e", "int f"));
        method.setStartLine(10);
        method.setEndLine(15);
    private static final Logger logger = LoggerFactory.getLogger(CodeChunkingService.class);
        // When: Analyze method
        List<CodeSmell> smells = analyzer.detectMethodSmells(method, "Calculator");
    private static final int MAX_CHUNK_SIZE = 2000;
        // Then: Should detect LONG_PARAMETER_LIST smell
        assertThat(smells).anyMatch(s -> s.getType().equals("LONG_PARAMETER_LIST"));
    public List<CodeChunkDTO> chunkFile(FileInfo fileInfo) {
        logger.debug("Chunking file: {}", fileInfo.getFileName());
        
    void detectSmells_DeepNesting_AddsSmell() {
        // Given: Method with deep nesting
        MethodInfo method = new MethodInfo();
        method.setName("nestedIfElse");
        method.setBody("""
                {
                    if (condition1) {
                        if (condition2) {
                            if (condition3) {
                                // Do something
                            }}}}
                """);
        method.setStartLine(10);
        method.setEndLine(20);
        // Create chunks for each class
        // When: Analyze method
        List<CodeSmell> smells = analyzer.detectMethodSmells(method, "DecisionTree");
        return chunks;
        // Then: Should detect DEEP_NESTING smell
        assertThat(smells).anyMatch(s -> s.getType().equals("DEEP_NESTING"));
        
        return chunks;
    }
    void detectSmells_MagicNumbers_AddsSmell() {
        // Given: Method with magic numbers
        MethodInfo method = new MethodInfo();
        method.setName("calculateArea");
        method.setBody("""
                {
                    return 3.14 * radius * radius;
                }
                """);
        method.setStartLine(10);
        method.setEndLine(15);
        if (!fileInfo.getImports().isEmpty()) {
        // When: Analyze method
        List<CodeSmell> smells = analyzer.detectMethodSmells(method, "Circle");
            }
        // Then: Should detect MAGIC_NUMBERS smell
        assertThat(smells).anyMatch(s -> s.getType().equals("MAGIC_NUMBERS"));
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            content.append("// - ").append(classInfo.getName());
            if (classInfo.isInterface()) {
    void detectSmells_EmptyCatchBlock_AddsSmell() {
        // Given: Method with empty catch block
        MethodInfo method = new MethodInfo();
        method.setName("handleRequest");
        method.setBody("""
                {
                    try {
                        // Risky operation
                    } catch (Exception e) {
                        // Ignore
                    }
                }
                """);
        method.setStartLine(10);
        method.setEndLine(15);
            logger.warn("Detected {} nodes involved in circular dependencies", cyclicNodes.size());
        // When: Analyze method
        List<CodeSmell> smells = analyzer.detectMethodSmells(method, "RequestHandler");
        return Collections.emptySet();
        // Then: Should detect EMPTY_CATCH_BLOCK smell
        assertThat(smells).anyMatch(s -> s.getType().equals("EMPTY_CATCH_BLOCK"));
        }
        
        DijkstraShortestPath<Object, DependencyEdge> pathFinder = 
    void detectPatterns_SingletonPattern_Detected() {
        // Given: Singleton class
        ClassInfo singletonClass = createSingletonClass();
        
        if (path == null) {
        List<DetectedPattern> patterns = patternDetector.detectPatterns(singletonClass);
        }
        // Then: Should detect Singleton pattern
        assertTrue(patterns.stream().anyMatch(p -> p.getPatternType().equals("SINGLETON")));

    /**
     * Get graph statistics.
    void detectPatterns_BuilderPattern_Detected() {
        // Given: Builder class
        ClassInfo builderClass = createBuilderClass();
        
        long classCount = dependencyGraph.vertexSet().stream()
        List<DetectedPattern> patterns = patternDetector.detectPatterns(builderClass);
            .count();
        // Then: Should detect Builder pattern
        assertTrue(patterns.stream().anyMatch(p -> p.getPatternType().equals("BUILDER")));
            .collect(Collectors.groupingBy(
                DependencyEdge::getType, 
                Collectors.counting()
    void detectPatterns_GodObject_AntiPatternDetected() {
        // Given: God Object class
        ClassInfo godObjectClass = createGodObjectClass();
        stats.put("classCount", classCount);
        stats.put("methodCount", methodCount);
        List<DetectedPattern> patterns = patternDetector.detectPatterns(godObjectClass);
        return List.of(interfaceFile, implFile);
        // Map to track JGraphT nodes to DB entities
        Map<Object, GraphNodeEntity> nodeMap = new HashMap<>();

        // Save all nodes
        for (Object vertex : graph.vertexSet()) {
            GraphNodeEntity entity = convertToEntity(vertex);
            entity = nodeRepository.save(entity);
            nodeMap.put(vertex, entity);
        }

        logger.debug("Saved {} nodes", nodeMap.size());

        // Save all edges
        List<GraphEdgeEntity> edgeEntities = new ArrayList<>();
        for (DependencyEdge edge : graph.edgeSet()) {
            Object source = graph.getEdgeSource(edge);
            Object target = graph.getEdgeTarget(edge);

            GraphNodeEntity fromEntity = nodeMap.get(source);
            GraphNodeEntity toEntity = nodeMap.get(target);

            if (fromEntity != null && toEntity != null) {
                GraphEdgeEntity edgeEntity = new GraphEdgeEntity();
                edgeEntity.setFromNode(fromEntity);
                edgeEntity.setToNode(toEntity);
                edgeEntity.setEdgeType(edge.getType().name());
                edgeEntity.setWeight(edge.getWeight());

                edgeEntities.add(edgeEntity);
            }
        }

        // Batch save edges
        edgeRepository.saveAll(edgeEntities);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Saved graph to database in {}ms ({} nodes, {} edges)",
                duration, nodeMap.size(), edgeEntities.size());
    }

    /**
     * Load the dependency graph from database.
     */
    @Transactional(readOnly = true)
    public Graph<Object, DependencyEdge> loadGraph() {
        logger.info("Loading graph from database...");
        long startTime = System.currentTimeMillis();

        Graph<Object, DependencyEdge> graph = new org.jgrapht.graph.DefaultDirectedGraph<>(DependencyEdge.class);
        Map<Long, Object> nodeMap = new HashMap<>();

        // Load all nodes
        List<GraphNodeEntity> nodeEntities = nodeRepository.findAll();
        for (GraphNodeEntity entity : nodeEntities) {
            Object node = convertFromEntity(entity);
            graph.addVertex(node);
            nodeMap.put(entity.getId(), node);
        }

        logger.debug("Loaded {} nodes", nodeMap.size());

        // Load all edges
        List<GraphEdgeEntity> edgeEntities = edgeRepository.findAll();
        for (GraphEdgeEntity entity : edgeEntities) {
            Object source = nodeMap.get(entity.getFromNode().getId());
            Object target = nodeMap.get(entity.getToNode().getId());

            if (source != null && target != null) {
                EdgeType type = EdgeType.valueOf(entity.getEdgeType());
                DependencyEdge edge = new DependencyEdge(type, entity.getWeight());
                graph.addEdge(source, target, edge);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Loaded graph from database in {}ms ({} nodes, {} edges)",
                duration, graph.vertexSet().size(), graph.edgeSet().size());

        return graph;
    }

    /**
     * Convert JGraphT node to database entity.
     */
    private GraphNodeEntity convertToEntity(Object node) {
        GraphNodeEntity entity = new GraphNodeEntity();

        if (node instanceof ClassNode) {
            ClassNode classNode = (ClassNode) node;
            entity.setNodeType("CLASS");
            entity.setName(classNode.getName());
            entity.setFullyQualifiedName(classNode.getFullyQualifiedName());
            entity.setPackageName(classNode.getPackageName());
            entity.setFilePath(classNode.getFilePath());
            entity.setIsInterface(classNode.isInterface());
        } else if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) node;
            entity.setNodeType("METHOD");
            entity.setName(methodNode.getName());
            entity.setFullyQualifiedName(methodNode.getFullyQualifiedName());
            entity.setStartLine(methodNode.getStartLine());
            entity.setEndLine(methodNode.getEndLine());
            entity.setMetadata(methodNode.getSignature());
        }

        return entity;
    }

    /**
     * Convert database entity to JGraphT node.
     */
    private Object convertFromEntity(GraphNodeEntity entity) {
        if ("CLASS".equals(entity.getNodeType())) {
            return new ClassNode(
                    entity.getName(),
                    entity.getFullyQualifiedName(),
                    entity.getPackageName(),
                    entity.getFilePath(),
                    entity.getIsInterface() != null && entity.getIsInterface()
            );
        } else if ("METHOD".equals(entity.getNodeType())) {
            return new MethodNode(
                    entity.getName(),
                    entity.getMetadata(), // signature
                    entity.getFullyQualifiedName(),
                    entity.getStartLine() != null ? entity.getStartLine() : 0,
                    entity.getEndLine() != null ? entity.getEndLine() : 0
            );
        }

        throw new IllegalArgumentException("Unknown node type: " + entity.getNodeType());
    }
}
```

---

## Day 19: Complete Indexing Pipeline

### Example 19.1: IndexingOrchestrationService

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.CodeParserService;
import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalkerl.firestick.exception.IndexingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates the complete indexing pipeline.
 * Coordinates file discovery, parsing, chunking, embedding, and storage.
 */
@Service
public class IndexingOrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(IndexingOrchestrationService.class);

    private final FileDiscoveryService fileDiscovery;
    private final CodeParserService parser;
    private final CodeChunkingService chunking;
    private final EmbeddingService embedding;
    private final ChromaService chroma;
    private final DependencyGraphService graphService;
    private final GraphPersistenceService graphPersistence;

    public IndexingOrchestrationService(
            FileDiscoveryService fileDiscovery,
            CodeParserService parser,
            CodeChunkingService chunking,
            EmbeddingService embedding,
            ChromaService chroma,
            DependencyGraphService graphService,
            GraphPersistenceService graphPersistence) {
        this.fileDiscovery = fileDiscovery;
        this.parser = parser;
        this.chunking = chunking;
        this.embedding = embedding;
        this.chroma = chroma;
        this.graphService = graphService;
        this.graphPersistence = graphPersistence;
    }

    /**
     * Index a directory with complete pipeline.
     *
     * @param rootPath Root directory to index
     * @return Indexing result with statistics
     */
    public IndexingResult indexDirectory(String rootPath) {
        logger.info("=== Starting indexing of directory: {} ===", rootPath);
        long overallStart = System.currentTimeMillis();

        IndexingProgress progress = new IndexingProgress();
        List<IndexingError> errors = new ArrayList<>();

        try {
            // Step 1: Discover files
            logger.info("Step 1/6: Discovering Java files...");
            long stepStart = System.currentTimeMillis();

            List<Path> files = fileDiscovery.scanDirectory(rootPath);
            progress.setTotalFiles(files.size());

            logger.info("  Found {} Java files in {}ms",
                    files.size(), System.currentTimeMillis() - stepStart);

            if (files.isEmpty()) {
                logger.warn("No Java files found in directory");
                return IndexingResult.success(progress, errors);
            }

            // Step 2: Parse all files
            logger.info("Step 2/6: Parsing files...");
            stepStart = System.currentTimeMillis();

            List<FileInfo> parsedFiles = parseFiles(files, progress, errors);

            logger.info("  Parsed {} files in {}ms ({} errors)",
                    parsedFiles.size(),
                    System.currentTimeMillis() - stepStart,
                    errors.size());

            // Step 3: Build dependency graph
            logger.info("Step 3/6: Building dependency graph...");
            stepStart = System.currentTimeMillis();

            graphService.buildFromParsedFiles(parsedFiles);
            graphPersistence.saveGraph(graphService.getGraph());

            logger.info("  Built and saved graph in {}ms",
                    System.currentTimeMillis() - stepStart);

            // Step 4: Chunk code
            logger.info("Step 4/6: Chunking code...");
            stepStart = System.currentTimeMillis();

            List<CodeChunkDTO> allChunks = chunkFiles(parsedFiles, progress);

            logger.info("  Created {} chunks in {}ms",
                    allChunks.size(),
                    System.currentTimeMillis() - stepStart);

            // Step 5: Generate embeddings
            logger.info("Step 5/6: Generating embeddings...");
            stepStart = System.currentTimeMillis();

            List<String> chunkTexts = allChunks.stream()
                    .map(CodeChunkDTO::getContent)
                    .collect(Collectors.toList());

            List<float[]> embeddings = embedding.generateBatchEmbeddings(chunkTexts);

            // Attach embeddings to chunks
            for (int i = 0; i < allChunks.size(); i++) {
                allChunks.get(i).setEmbedding(embeddings.get(i));
            }

            logger.info("  Generated {} embeddings in {}ms",
                    embeddings.size(),
                    System.currentTimeMillis() - stepStart);

            // Step 6: Store in Chroma
            logger.info("Step 6/6: Storing in Chroma...");
            stepStart = System.currentTimeMillis();

            storeInChroma(allChunks, progress);

            logger.info("  Stored {} chunks in {}ms",
                    allChunks.size(),
                    System.currentTimeMillis() - stepStart);

            // Summary
            long totalDuration = System.currentTimeMillis() - overallStart;
            logger.info("=== Indexing completed in {}ms ===", totalDuration);
            logger.info("  Files processed: {}/{}",
                    progress.getFilesParsed(), progress.getTotalFiles());
            logger.info("  Chunks created: {}", progress.getTotalChunks());
            logger.info("  Errors: {}", errors.size());

            progress.setComplete(true);
            return IndexingResult.success(progress, errors);

        } catch (Exception e) {
            logger.error("Indexing failed", e);
            return IndexingResult.failure(progress, errors, e);
        }
    }

    /**
     * Parse all files with error handling.
     */
    private List<FileInfo> parseFiles(List<Path> files, IndexingProgress progress,
                                      List<IndexingError> errors) {
        List<FileInfo> parsedFiles = new ArrayList<>();

        for (Path file : files) {
            try {
                FileInfo fileInfo = parser.parseFile(file);
                parsedFiles.add(fileInfo);
                progress.incrementFilesParsed();

                if (progress.getFilesParsed() % 50 == 0) {
                    logger.debug("  Parsed {}/{} files ({}%)",
                            progress.getFilesParsed(),
                            progress.getTotalFiles(),
                            progress.getPercentComplete());
                }
            } catch (Exception e) {
                logger.error("Failed to parse file: {}", file, e);
                errors.add(new IndexingError(
                        file.toString(),
                        "PARSE_ERROR",
                        e.getMessage()
                ));
            }
        }

        return parsedFiles;
    }

    /**
     * Chunk all files.
     */
    private List<CodeChunkDTO> chunkFiles(List<FileInfo> parsedFiles, IndexingProgress progress) {
        List<CodeChunkDTO> allChunks = new ArrayList<>();

        for (FileInfo fileInfo : parsedFiles) {
            List<CodeChunkDTO> chunks = chunking.chunkFile(fileInfo);
            allChunks.addAll(chunks);
        }

        progress.setTotalChunks(allChunks.size());
        return allChunks;
    }

    /**
     * Store chunks in Chroma with metadata.
     */
    private void storeInChroma(List<CodeChunkDTO> chunks, IndexingProgress progress) {
        String collectionName = "firestick_code";

        // Ensure collection exists
        if (!chroma.getCollection(collectionName).isPresent()) {
            chroma.createCollection(collectionName);
        }

        // Prepare data for Chroma
        List<String> ids = new ArrayList<>();
        List<float[]> embeddings = new ArrayList<>();
        List<String> documents = new ArrayList<>();
        List<Map<String, Object>> metadatas = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            CodeChunkDTO chunk = chunks.get(i);

            ids.add(UUID.randomUUID().toString());
            embeddings.add(chunk.getEmbedding());
            documents.add(chunk.getContent());
            metadatas.add(createMetadata(chunk));

            progress.incrementChunksStored();
        }

        // Store in Chroma
        chroma.addEmbeddings(collectionName, embeddings, documents, metadatas);
    }

    /**
     * Create metadata map for Chroma.
     */
    private Map<String, Object> createMetadata(CodeChunkDTO chunk) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("chunkType", chunk.getChunkType());
        metadata.put("filePath", chunk.getFilePath());
        metadata.put("fileName", chunk.getFileName());

        if (chunk.getPackageName() != null) {
            metadata.put("packageName", chunk.getPackageName());
        }

        if (chunk.getClassName() != null) {
            metadata.put("className", chunk.getClassName());
            metadata.put("fullyQualifiedClassName", chunk.getFullyQualifiedClassName());
        }

        if (chunk.getMethodName() != null) {
            metadata.put("methodName", chunk.getMethodName());
            metadata.put("methodSignature", chunk.getMethodSignature());
        }

        metadata.put("startLine", chunk.getStartLine());
        metadata.put("endLine", chunk.getEndLine());

        return metadata;
    }
}
```

### Example 19.2: IndexingProgress and IndexingResult

```java
package com.codetalkerl.firestick.dto;

/**
 * Tracks progress of indexing operation.
 */
public class IndexingProgress {
    private int totalFiles;
    private int filesParsed;
    private int totalChunks;
    private int chunksStored;
    private boolean complete;

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getFilesParsed() {
        return filesParsed;
    }

    public void incrementFilesParsed() {
        this.filesParsed++;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public int getChunksStored() {
        return chunksStored;
    }

    public void incrementChunksStored() {
        this.chunksStored++;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getPercentComplete() {
        if (totalFiles == 0) return 0;
        return (filesParsed * 100) / totalFiles;
    }
}
```

```java
package com.codetalkerl.firestick.dto;

import java.util.List;

/**
 * Result of indexing operation.
 */
public class IndexingResult {
    private boolean success;
    private IndexingProgress progress;
    private List<IndexingError> errors;
    private String errorMessage;

    public static IndexingResult success(IndexingProgress progress, List<IndexingError> errors) {
        IndexingResult result = new IndexingResult();
        result.success = true;
        result.progress = progress;
        result.errors = errors;
        return result;
    }

    public static IndexingResult failure(IndexingProgress progress, 
                                        List<IndexingError> errors, 
                                        Exception exception) {
        IndexingResult result = new IndexingResult();
        result.success = false;
        result.progress = progress;
        result.errors = errors;
        result.errorMessage = exception.getMessage();
        return result;
    }

    // Getters and setters
    
    public boolean isSuccess() {
        return success;
    }

    public IndexingProgress getProgress() {
        return progress;
    }

    public List<IndexingError> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
```

```java
package com.codetalkerl.firestick.dto;

/**
 * Represents an error during indexing.
 */
public class IndexingError {
    private String filePath;
    private String errorType;
    private String message;

    public IndexingError(String filePath, String errorType, String message) {
        this.filePath = filePath;
        this.errorType = errorType;
        this.message = message;
    }

    // Getters
    
    public String getFilePath() {
        return filePath;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return errorType + " in " + filePath + ": " + message;
    }
}
```

### Example 19.3: Enhanced ChromaService for Batch Storage

```java
/**
 * Add multiple embeddings to collection in batch.
 */
public void addEmbeddings(String collectionName, 
                         List<float[]> embeddings,
                         List<String> documents,
                         List<Map<String, Object>> metadatas) {
    logger.info("Adding {} embeddings to collection: {}", embeddings.size(), collectionName);
    
    String url = chromaUrl + "/api/v1/collections/" + collectionName + "/add";
    
    // Prepare request body
    Map<String, Object> requestBody = new HashMap<>();
    
    // Generate IDs
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < embeddings.size(); i++) {
        ids.add(UUID.randomUUID().toString());
    }
    
    requestBody.put("ids", ids);
    requestBody.put("embeddings", embeddings);
    requestBody.put("documents", documents);
    requestBody.put("metadatas", metadatas);
    
    try {
        ResponseEntity<String> response = restTemplate.postForEntity(
            url, requestBody, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Successfully added {} embeddings", embeddings.size());
        } else {
            logger.error("Failed to add embeddings: {}", response.getStatusCode());
        }
    } catch (Exception e) {
        logger.error("Error adding embeddings to Chroma", e);
        throw new RuntimeException("Failed to add embeddings", e);
    }
}
```

### Example 19.4: REST Controller for Indexing

```java
package com.codetalkerl.firestick.controller;

import com.codetalkerl.firestick.dto.IndexingResult;
import com.codetalkerl.firestick.service.IndexingOrchestrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for indexing operations.
 */
@RestController
@RequestMapping("/api/index")
public class IndexingController {
    
    private final IndexingOrchestrationService indexingService;

    public IndexingController(IndexingOrchestrationService indexingService) {
        this.indexingService = indexingService;
    }

    /**
     * Index a directory.
     */
    @PostMapping
    public ResponseEntity<IndexingResult> indexDirectory(@RequestBody IndexRequest request) {
        if (request.getDirectoryPath() == null || request.getDirectoryPath().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        IndexingResult result = indexingService.indexDirectory(request.getDirectoryPath());
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    public static class IndexRequest {
        private String directoryPath;

        public String getDirectoryPath() {
            return directoryPath;
        }

        public void setDirectoryPath(String directoryPath) {
            this.directoryPath = directoryPath;
        }
    }
}
```

### Advice for Days 17-19

**Indexing Pipeline Best Practices:**

1. **Error Handling**: Don't fail entire pipeline if one file fails
2. **Progress Reporting**: Log progress every N files/chunks
3. **Batch Processing**: Process embeddings and storage in batches
4. **Transaction Management**: Use transactions for database operations
5. **Cleanup**: Clear caches and resources after indexing

**Performance Optimization:**

```java
// Parallel file parsing (if order doesn't matter)
List<FileInfo> parsedFiles = files.parallelStream()
    .map(file -> {
        try {
            return parser.parseFile(file);
        } catch (Exception e) {
            return null;
        }
    })
    .filter(Objects::nonNull)
    .collect(Collectors.toList());

// Batch database operations
@Transactional
public void saveBatch(List<Entity> entities) {
    int batchSize = 100;
    for (int i = 0; i < entities.size(); i += batchSize) {
        int end = Math.min(i + batchSize, entities.size());
        repository.saveAll(entities.subList(i, end));
        repository.flush();
    }
}
```

**Chroma Metadata Best Practices:**

```java
// Good metadata structure
{
    "chunkType": "METHOD",
    "filePath": "/src/Calculator.java",
    "fileName": "Calculator.java",
    "packageName": "com.example",
    "className": "Calculator",
    "methodName": "add",
    "methodSignature": "public int add(int a, int b)",
    "startLine": 10,
    "endLine": 12
}

// Use consistent types (strings, numbers, booleans only)
// Avoid nested objects (Chroma doesn't support them well)
// Keep metadata small (< 1KB per chunk)
```

---

# Phase 3: Query Engine (Weeks 4-6, Days 24-33)

This phase builds the search and query engine that makes Firestick useful. We'll implement multiple search strategies (semantic, keyword, hybrid) and intelligent query routing.

---

## Day 24: Symbol Table for Fast Lookups

### Example 24.1: Complete Symbol Table Service

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.entity.SymbolEntity;
import com.codetalkerl.firestick.model.Symbol;
import com.codetalkerl.firestick.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory symbol table for fast exact-match lookups.
 * Indexes classes, methods, fields, and packages with multiple access patterns.
 */
@Service
public class SymbolTableService {
    private static final Logger logger = LoggerFactory.getLogger(SymbolTableService.class);
    
    private final SymbolRepository symbolRepository;
    
    // Multiple indexes for different access patterns
    private final Map<String, List<Symbol>> byName = new ConcurrentHashMap<>();
    private final Map<String, Symbol> byFullyQualifiedName = new ConcurrentHashMap<>();
    private final Map<String, List<Symbol>> bySignature = new ConcurrentHashMap<>();
    private final Map<String, List<Symbol>> byPackage = new ConcurrentHashMap<>();
    private final Map<String, List<Symbol>> byType = new ConcurrentHashMap<>();
    
    public SymbolTableService(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;
    }

    @PostConstruct
    public void buildSymbolTable() {
        logger.info("Building symbol table from database...");
        long startTime = System.currentTimeMillis();
        
        List<SymbolEntity> entities = symbolRepository.findAll();
        
        for (SymbolEntity entity : entities) {
            Symbol symbol = toSymbol(entity);
            indexSymbol(symbol);
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Symbol table built with {} symbols in {}ms", 
            byFullyQualifiedName.size(), duration);
        
        logStatistics();
    }

    /**
     * Index a symbol in all relevant indexes.
     */
    public void indexSymbol(Symbol symbol) {
        // Index by simple name
        byName.computeIfAbsent(symbol.getName(), k -> new ArrayList<>())
              .add(symbol);
        
        // Index by fully qualified name
        byFullyQualifiedName.put(symbol.getFullyQualifiedName(), symbol);
        
        // Index by signature (for methods)
        if (symbol.getSignature() != null && !symbol.getSignature().isEmpty()) {
            bySignature.computeIfAbsent(symbol.getSignature(), k -> new ArrayList<>())
                      .add(symbol);
        }
        
        // Index by package
        if (symbol.getPackageName() != null) {
            byPackage.computeIfAbsent(symbol.getPackageName(), k -> new ArrayList<>())
                    .add(symbol);
        }
        
        // Index by type
        byType.computeIfAbsent(symbol.getType(), k -> new ArrayList<>())
              .add(symbol);
    }

    /**
     * Find symbols by simple name.
     * Returns exact matches or fuzzy matches if no exact match found.
     */
    public List<Symbol> findByName(String name) {
        List<Symbol> exact = byName.get(name);
        
        if (exact != null && !exact.isEmpty()) {
            logger.debug("Found {} exact matches for name: {}", exact.size(), name);
            return new ArrayList<>(exact);
        }
        
        // Try fuzzy matching (max 2 character differences)
        logger.debug("No exact match for '{}', trying fuzzy search", name);
        return findSimilar(name, 2);
    }

    /**
     * Find symbol by fully qualified name.
     */
    public Optional<Symbol> findByFullyQualifiedName(String fqn) {
        Symbol symbol = byFullyQualifiedName.get(fqn);
        return Optional.ofNullable(symbol);
    }

    /**
     * Find symbols by method signature.
     */
    public List<Symbol> findBySignature(String signature) {
        List<Symbol> matches = bySignature.get(signature);
        return matches != null ? new ArrayList<>(matches) : Collections.emptyList();
    }

    /**
     * Find all symbols in a package.
     */
    public List<Symbol> findByPackage(String packageName) {
        List<Symbol> symbols = byPackage.get(packageName);
        return symbols != null ? new ArrayList<>(symbols) : Collections.emptyList();
    }

    /**
     * Find symbols by type (CLASS, METHOD, FIELD, INTERFACE).
     */
    public List<Symbol> findByType(String type) {
        List<Symbol> symbols = byType.get(type);
        return symbols != null ? new ArrayList<>(symbols) : Collections.emptyList();
    }

    /**
     * Fuzzy search using Levenshtein distance.
     * Returns symbols with names within maxDistance edits.
     */
    public List<Symbol> findSimilar(String query, int maxDistance) {
        return byName.entrySet().stream()
            .filter(entry -> levenshteinDistance(
                entry.getKey().toLowerCase(), 
                query.toLowerCase()) <= maxDistance)
            .flatMap(entry -> entry.getValue().stream())
            .sorted(Comparator.comparingInt(symbol -> 
                levenshteinDistance(symbol.getName().toLowerCase(), query.toLowerCase())))
            .collect(Collectors.toList());
    }

    /**
     * Prefix search - find symbols whose names start with prefix.
     */
    public List<Symbol> findByPrefix(String prefix, int limit) {
        String lowerPrefix = prefix.toLowerCase();
        
        return byName.entrySet().stream()
            .filter(entry -> entry.getKey().toLowerCase().startsWith(lowerPrefix))
            .flatMap(entry -> entry.getValue().stream())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Calculate Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1]
                    );
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }

    /**
     * Convert entity to domain model.
     */
    private Symbol toSymbol(SymbolEntity entity) {
        return Symbol.builder()
            .name(entity.getName())
            .fullyQualifiedName(entity.getFullyQualifiedName())
            .type(entity.getType())
            .signature(entity.getSignature())
            .packageName(entity.getPackageName())
            .filePath(entity.getFilePath())
            .startLine(entity.getStartLine())
            .endLine(entity.getEndLine())
            .modifiers(entity.getModifiers())
            .build();
    }

    /**
     * Rebuild symbol table (call after reindexing).
     */
    public void rebuild() {
        logger.info("Rebuilding symbol table...");
        clearAll();
        buildSymbolTable();
    }

    /**
     * Clear all indexes.
     */
    private void clearAll() {
        byName.clear();
        byFullyQualifiedName.clear();
        bySignature.clear();
        byPackage.clear();
        byType.clear();
    }

    /**
     * Get symbol table statistics.
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSymbols", byFullyQualifiedName.size());
        stats.put("uniqueNames", byName.size());
        stats.put("packages", byPackage.size());
        
        Map<String, Integer> byTypeCount = new HashMap<>();
        byType.forEach((type, symbols) -> byTypeCount.put(type, symbols.size()));
        stats.put("symbolsByType", byTypeCount);
        
        return stats;
    }

    /**
     * Log symbol table statistics.
     */
    private void logStatistics() {
        Map<String, Object> stats = getStatistics();
        logger.info("Symbol table statistics:");
        logger.info("  Total symbols: {}", stats.get("totalSymbols"));
        logger.info("  Unique names: {}", stats.get("uniqueNames"));
        logger.info("  Packages: {}", stats.get("packages"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> byTypeCount = (Map<String, Integer>) stats.get("symbolsByType");
        byTypeCount.forEach((type, count) -> 
            logger.info("  {}: {}", type, count));
    }
}
```

### Example 24.2: Symbol Domain Model

```java
package com.codetalkerl.firestick.model;

import java.util.List;

/**
 * Represents a code symbol (class, method, field, etc.)
 */
public class Symbol {
    private String name;
    private String fullyQualifiedName;
    private String type; // CLASS, METHOD, FIELD, INTERFACE, ENUM
    private String signature;
    private String packageName;
    private String filePath;
    private int startLine;
    private int endLine;
    private List<String> modifiers; // public, private, static, etc.

    // Builder pattern
    public static SymbolBuilder builder() {
        return new SymbolBuilder();
    }

    // Getters
    
    public String getName() {
        return name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getType() {
        return type;
    }

    public String getSignature() {
        return signature;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public boolean isPublic() {
        return modifiers != null && modifiers.contains("public");
    }

    public boolean isStatic() {
        return modifiers != null && modifiers.contains("static");
    }

    // Builder class
    public static class SymbolBuilder {
        private Symbol symbol = new Symbol();

        public SymbolBuilder name(String name) {
            symbol.name = name;
            return this;
        }

        public SymbolBuilder fullyQualifiedName(String fullyQualifiedName) {
            symbol.fullyQualifiedName = fullyQualifiedName;
            return this;
        }

        public SymbolBuilder type(String type) {
            symbol.type = type;
            return this;
        }

        public SymbolBuilder signature(String signature) {
            symbol.signature = signature;
            return this;
        }

        public SymbolBuilder packageName(String packageName) {
            symbol.packageName = packageName;
            return this;
        }

        public SymbolBuilder filePath(String filePath) {
            symbol.filePath = filePath;
            return this;
        }

        public SymbolBuilder startLine(int startLine) {
            symbol.startLine = startLine;
            return this;
        }

        public SymbolBuilder endLine(int endLine) {
            symbol.endLine = endLine;
            return this;
        }

        public SymbolBuilder modifiers(List<String> modifiers) {
            symbol.modifiers = modifiers;
            return this;
        }

        public Symbol build() {
            return symbol;
        }
    }
}
```

### Example 24.3: Symbol Repository

```java
package com.codetalkerl.firestick.repository;

import com.codetalkerl.firestick.entity.SymbolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymbolRepository extends JpaRepository<SymbolEntity, Long> {
    
    Optional<SymbolEntity> findByFullyQualifiedName(String fullyQualifiedName);
    
    List<SymbolEntity> findByName(String name);
    
    List<SymbolEntity> findByType(String type);
    
    List<SymbolEntity> findByPackageName(String packageName);
    
    @Query("SELECT s FROM SymbolEntity s WHERE s.type = 'CLASS' OR s.type = 'INTERFACE'")
    List<SymbolEntity> findAllClasses();
    
    @Query("SELECT s FROM SymbolEntity s WHERE s.type = 'METHOD'")
    List<SymbolEntity> findAllMethods();
    
    @Query("SELECT DISTINCT s.packageName FROM SymbolEntity s WHERE s.packageName IS NOT NULL")
    List<String> findAllPackages();
}
```

### Example 24.4: Tests for Symbol Table

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.model.Symbol;
import com.codetalkerl.firestick.repository.SymbolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SymbolTableServiceTest {
    
    @Autowired
    private SymbolTableService symbolTable;
    
    @Autowired
    private SymbolRepository symbolRepository;

    @BeforeEach
    void setUp() {
        // Symbol table is built automatically via @PostConstruct
    }

    @Test
    void findByName_ExactMatch_ReturnsSymbol() {
        List<Symbol> symbols = symbolTable.findByName("PaymentService");
        
        assertThat(symbols).isNotEmpty();
        assertThat(symbols.get(0).getName()).isEqualTo("PaymentService");
    }

    @Test
    void findByFullyQualifiedName_ReturnsSymbol() {
        Optional<Symbol> symbol = symbolTable.findByFullyQualifiedName(
            "com.codetalkerl.firestick.service.PaymentService");
        
        assertThat(symbol).isPresent();
        assertThat(symbol.get().getType()).isEqualTo("CLASS");
    }

    @Test
    void findSimilar_WithTypo_ReturnsSimilarSymbols() {
        // "PaymentServise" has 1 character difference from "PaymentService"
        List<Symbol> symbols = symbolTable.findSimilar("PaymentServise", 2);
        
        assertThat(symbols).isNotEmpty();
        assertThat(symbols.get(0).getName()).isEqualTo("PaymentService");
    }

    @Test
    void findByPrefix_ReturnsMatchingSymbols() {
        List<Symbol> symbols = symbolTable.findByPrefix("Payment", 10);
        
        assertThat(symbols).isNotEmpty();
        assertThat(symbols).allMatch(s -> s.getName().startsWith("Payment"));
    }

    @Test
    void findByPackage_ReturnsAllSymbolsInPackage() {
        List<Symbol> symbols = symbolTable.findByPackage("com.codetalkerl.firestick.service");
        
        assertThat(symbols).isNotEmpty();
        assertThat(symbols).allMatch(s -> 
            s.getPackageName().equals("com.codetalkerl.firestick.service"));
    }

    @Test
    void findByType_ReturnsSymbolsOfType() {
        List<Symbol> classes = symbolTable.findByType("CLASS");
        
        assertThat(classes).isNotEmpty();
        assertThat(classes).allMatch(s -> s.getType().equals("CLASS"));
    }

    @Test
    void getStatistics_ReturnsAccurateStats() {
        Map<String, Object> stats = symbolTable.getStatistics();
        
        assertThat(stats).containsKeys("totalSymbols", "uniqueNames", "packages");
        assertThat((Integer) stats.get("totalSymbols")).isGreaterThan(0);
    }

    @Test
    void indexSymbol_AddsToAllIndexes() {
        Symbol newSymbol = Symbol.builder()
            .name("TestClass")
            .fullyQualifiedName("com.test.TestClass")
            .type("CLASS")
            .packageName("com.test")
            .build();
        
        symbolTable.indexSymbol(newSymbol);
        
        List<Symbol> byName = symbolTable.findByName("TestClass");
        assertThat(byName).contains(newSymbol);
        
        Optional<Symbol> byFQN = symbolTable.findByFullyQualifiedName("com.test.TestClass");
        assertThat(byFQN).isPresent();
    }
}
```

---

## Day 25: Semantic Search with Chroma

### Example 25.1: Semantic Search Service

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.SearchResult;
import com.codetalkerl.firestick.exception.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Semantic search using vector embeddings and Chroma.
 */
@Service
public class SemanticSearchService {
    private static final Logger logger = LoggerFactory.getLogger(SemanticSearchService.class);
    
    private final ChromaService chroma;
    private final EmbeddingService embedding;
    
    @Value("${search.collection.name:firestick_code}")
    private String collectionName;

    public SemanticSearchService(ChromaService chroma, EmbeddingService embedding) {
        this.chroma = chroma;
        this.embedding = embedding;
    }

    /**
     * Semantic search for code using natural language query.
     *
     * @param query Natural language query
     * @param topK Number of results to return
     * @return List of search results ranked by semantic similarity
     */
    public List<SearchResult> search(String query, int topK) throws SearchException {
        logger.info("Semantic search: query='{}', topK={}", query, topK);
        long startTime = System.currentTimeMillis();
        
        try {
            // Preprocess query
            String processedQuery = preprocessQuery(query);
            logger.debug("Preprocessed query: '{}'", processedQuery);
            
            // Generate embedding for query
            float[] queryEmbedding = embedding.generateSingleEmbedding(processedQuery);
            logger.debug("Generated {} -dimensional embedding", queryEmbedding.length);
            
            // Query Chroma
            Map<String, Object> chromaResponse = chroma.query(
                collectionName,
                Collections.singletonList(queryEmbedding),
                topK,
                null // No metadata filter
            );
            
            // Parse results
            List<SearchResult> results = parseChromaResponse(chromaResponse);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Semantic search completed in {}ms, found {} results", 
                duration, results.size());
            
            return results;
            
        } catch (Exception e) {
            logger.error("Semantic search failed for query: {}", query, e);
            throw new SearchException("Failed to execute semantic search", e);
        }
    }

    /**
     * Search with metadata filters.
     */
    public List<SearchResult> searchWithFilters(String query, int topK, 
                                               Map<String, Object> filters) throws SearchException {
        logger.info("Semantic search with filters: query='{}', filters={}", query, filters);
        
        try {
            String processedQuery = preprocessQuery(query);
            float[] queryEmbedding = embedding.generateSingleEmbedding(processedQuery);
            
            // Query with filters
            Map<String, Object> chromaResponse = chroma.query(
                collectionName,
                Collections.singletonList(queryEmbedding),
                topK * 2, // Get more results for filtering
                filters
            );
            
            List<SearchResult> results = parseChromaResponse(chromaResponse);
            
            // Limit to topK after filtering
            return results.stream()
                .limit(topK)
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            logger.error("Filtered semantic search failed", e);
            throw new SearchException("Failed to execute filtered search", e);
        }
    }

    /**
     * Find similar code to a given code snippet.
     */
    public List<SearchResult> findSimilarCode(String codeSnippet, int topK) throws SearchException {
        logger.info("Finding similar code, topK={}", topK);
        
        try {
            // Generate embedding for the code snippet
            float[] codeEmbedding = embedding.generateSingleEmbedding(codeSnippet);
            
            // Search Chroma
            Map<String, Object> chromaResponse = chroma.query(
                collectionName,
                Collections.singletonList(codeEmbedding),
                topK,
                null
            );
            
            List<SearchResult> results = parseChromaResponse(chromaResponse);
            
            logger.info("Found {} similar code chunks", results.size());
            return results;
            
        } catch (Exception e) {
            logger.error("Similar code search failed", e);
            throw new SearchException("Failed to find similar code", e);
        }
    }

    /**
     * Preprocess query for better search results.
     */
    private String preprocessQuery(String query) {
        String processed = query.trim();
        
        // Expand common abbreviations
        processed = expandAbbreviations(processed);
        
        // Remove code artifacts if any
        processed = processed.replaceAll("[{}();]", " ");
        
        // Normalize whitespace
        processed = processed.replaceAll("\\s+", " ");
        
        return processed;
    }

    /**
     * Expand common abbreviations to full terms.
     */
    private String expandAbbreviations(String text) {
        Map<String, String> abbreviations = new HashMap<>();
        abbreviations.put(" db ", " database ");
        abbreviations.put(" auth ", " authentication ");
        abbreviations.put(" config ", " configuration ");
        abbreviations.put(" repo ", " repository ");
        abbreviations.put(" svc ", " service ");
        abbreviations.put(" util ", " utility ");
        abbreviations.put(" mgr ", " manager ");
        abbreviations.put(" impl ", " implementation ");
        
        String result = " " + text.toLowerCase() + " ";
        for (Map.Entry<String, String> entry : abbreviations.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        return result.trim();
    }

    /**
     * Parse Chroma response into SearchResult objects.
     */
    @SuppressWarnings("unchecked")
    private List<SearchResult> parseChromaResponse(Map<String, Object> response) {
        List<SearchResult> results = new ArrayList<>();
        
        // Chroma returns: {ids: [[...]], documents: [[...]], distances: [[...]], metadatas: [[...]]}
        List<List<String>> ids = (List<List<String>>) response.get("ids");
        List<List<String>> documents = (List<List<String>>) response.get("documents");
        List<List<Double>> distances = (List<List<Double>>) response.get("distances");
        List<List<Map<String, Object>>> metadatas = 
            (List<List<Map<String, Object>>>) response.get("metadatas");
        
        if (ids == null || ids.isEmpty() || ids.get(0) == null) {
            return results;
        }
        
        // Process first query results (we only send one query)
        List<String> resultIds = ids.get(0);
        List<String> resultDocs = documents != null ? documents.get(0) : Collections.emptyList();
        List<Double> resultDistances = distances != null ? distances.get(0) : Collections.emptyList();
        List<Map<String, Object>> resultMetadata = 
            metadatas != null ? metadatas.get(0) : Collections.emptyList();
        
        for (int i = 0; i < resultIds.size(); i++) {
            SearchResult result = new SearchResult();
            result.setId(resultIds.get(i));
            
            if (i < resultDocs.size()) {
                result.setContent(resultDocs.get(i));
            }
            
            if (i < resultDistances.size()) {
                // Convert distance to similarity score (0-1, where 1 is most similar)
                double distance = resultDistances.get(i);
                double score = 1.0 / (1.0 + distance);
                result.setScore(score);
            }
            
            if (i < resultMetadata.size()) {
                Map<String, Object> metadata = resultMetadata.get(i);
                populateMetadata(result, metadata);
            }
            
            result.setSearchType("SEMANTIC");
            results.add(result);
        }
        
        return results;
    }

    /**
     * Populate SearchResult from Chroma metadata.
     */
    private void populateMetadata(SearchResult result, Map<String, Object> metadata) {
        if (metadata.containsKey("filePath")) {
            result.setFilePath((String) metadata.get("filePath"));
        }
        if (metadata.containsKey("fileName")) {
            result.setFileName((String) metadata.get("fileName"));
        }
        if (metadata.containsKey("packageName")) {
            result.setPackageName((String) metadata.get("packageName"));
        }
        if (metadata.containsKey("className")) {
            result.setClassName((String) metadata.get("className"));
        }
        if (metadata.containsKey("methodName")) {
            result.setMethodName((String) metadata.get("methodName"));
        }
        if (metadata.containsKey("chunkType")) {
            result.setChunkType((String) metadata.get("chunkType"));
        }
        if (metadata.containsKey("startLine")) {
            result.setStartLine(((Number) metadata.get("startLine")).intValue());
        }
        if (metadata.containsKey("endLine")) {
            result.setEndLine(((Number) metadata.get("endLine")).intValue());
        }
    }
}
```

### Example 25.2: SearchResult DTO

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a search result from any search type.
 */
public class SearchResult implements Comparable<SearchResult> {
    private String id;
    private String content;
    private double score;
    private String searchType; // SEMANTIC, KEYWORD, SYMBOL
    
    // File information
    private String filePath;
    private String fileName;
    private int startLine;
    private int endLine;
    
    // Code structure
    private String packageName;
    private String className;
    private String fullyQualifiedClassName;
    private String methodName;
    private String methodSignature;
    private String chunkType; // FILE, CLASS, METHOD
    
    // Context information (added by ContextAssemblyService)
    private String fullContext;
    private int contextStartLine;
    private int contextEndLine;
    private List<String> callers;
    private List<String> callees;
    private String parentClass;
    private List<String> interfaces;
    
    // Highlighted content (for keyword search)
    private String highlightedContent;
    
    // Multiple search types (for hybrid search)
    private List<String> searchTypes = new ArrayList<>();

    // Getters and setters
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public String getChunkType() {
        return chunkType;
    }

    public void setChunkType(String chunkType) {
        this.chunkType = chunkType;
    }

    public String getFullContext() {
        return fullContext;
    }

    public void setFullContext(String fullContext) {
        this.fullContext = fullContext;
    }

    public int getContextStartLine() {
        return contextStartLine;
    }

    public void setContextStartLine(int contextStartLine) {
        this.contextStartLine = contextStartLine;
    }

    public int getContextEndLine() {
        return contextEndLine;
    }

    public void setContextEndLine(int contextEndLine) {
        this.contextEndLine = contextEndLine;
    }

    public List<String> getCallers() {
        return callers;
    }

    public void setCallers(List<String> callers) {
        this.callers = callers;
    }

    public List<String> getCallees() {
        return callees;
    }

    public void setCallees(List<String> callees) {
        this.callees = callees;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public String getHighlightedContent() {
        return highlightedContent;
    }

    public void setHighlightedContent(String highlightedContent) {
        this.highlightedContent = highlightedContent;
    }

    public List<String> getSearchTypes() {
        return searchTypes;
    }

    public void addSearchType(String type) {
        if (!searchTypes.contains(type)) {
            searchTypes.add(type);
        }
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score); // Descending order
    }

    /**
     * Get display snippet (first 200 characters).
     */
    public String getSnippet() {
        if (highlightedContent != null) {
            return highlightedContent;
        }
        if (content != null && content.length() > 200) {
            return content.substring(0, 200) + "...";
        }
        return content;
    }
}
```

---

## Day 26: Keyword Search with Lucene

### Example 26.1: Enhanced Keyword Search Service

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.CodeSearchService;
import com.codetalkerl.firestick.dto.SearchResult;
import com.codetalkerl.firestick.exception.SearchException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Keyword search using Apache Lucene with BM25 ranking.
 */
@Service
public class KeywordSearchService {
    private static final Logger logger = LoggerFactory.getLogger(KeywordSearchService.class);

    private final CodeSearchService codeSearchService; // Existing Lucene index
    private final StandardAnalyzer analyzer = new StandardAnalyzer();

    public KeywordSearchService(CodeSearchService codeSearchService) {
        this.codeSearchService = codeSearchService;
    }

    /**
     * Keyword search with support for Boolean operators and field-specific queries.
     *
     * @param queryString User query (supports Lucene syntax)
     * @param topK Number of results
     * @return List of search results ranked by BM25
     */
    public List<SearchResult> keywordSearch(String queryString, int topK) throws SearchException {
        logger.info("Keyword search: query='{}', topK={}", queryString, topK);
        long startTime = System.currentTimeMillis();

        try {
            IndexSearcher searcher = codeSearchService.getSearcher();

            // Parse query (supports AND, OR, NOT, field:value, wildcards, etc.)
            QueryParser parser = new QueryParser("content", analyzer);
            parser.setAllowLeadingWildcard(true);
            Query query = parser.parse(queryString);

            logger.debug("Parsed Lucene query: {}", query);

            // Execute search
            TopDocs topDocs = searcher.search(query, topK);

            logger.debug("Found {} hits", topDocs.scoreDocs.length);

            // Convert to SearchResult objects
            List<SearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);

                SearchResult result = documentToSearchResult(doc, scoreDoc.score);

                // Add highlighting
                String highlighted = highlightMatches(query, result.getContent());
                result.setHighlightedContent(highlighted);

                result.setSearchType("KEYWORD");
                results.add(result);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Keyword search completed in {}ms, found {} results",
                    duration, results.size());

            return results;

        } catch (Exception e) {
            logger.error("Keyword search failed for query: {}", queryString, e);
            throw new SearchException("Failed to execute keyword search", e);
        }
    }

    /**
     * Search specific field (e.g., className, methodName).
     */
    public List<SearchResult> searchField(String fieldName, String fieldValue, int topK)
            throws SearchException {
        String queryString = fieldName + ":\"" + fieldValue + "\"";
        return keywordSearch(queryString, topK);
    }

    /**
     * Boolean search with AND, OR, NOT operators.
     */
    public List<SearchResult> booleanSearch(List<String> mustTerms,
                                            List<String> shouldTerms,
                                            List<String> mustNotTerms,
                                            int topK) throws SearchException {
        StringBuilder queryBuilder = new StringBuilder();

        // Add MUST terms (AND)
        for (String term : mustTerms) {
            if (queryBuilder.length() > 0) queryBuilder.append(" AND ");
            queryBuilder.append("\"").append(term).append("\"");
        }

        // Add SHOULD terms (OR)
        if (!shouldTerms.isEmpty()) {
            if (queryBuilder.length() > 0) queryBuilder.append(" AND ");
            queryBuilder.append("(");
            for (int i = 0; i < shouldTerms.size(); i++) {
                if (i > 0) queryBuilder.append(" OR ");
                queryBuilder.append("\"").append(shouldTerms.get(i)).append("\"");
            }
            queryBuilder.append(")");
        }

        // Add MUST_NOT terms (NOT)
        for (String term : mustNotTerms) {
            queryBuilder.append(" NOT \"").append(term).append("\"");
        }

        return keywordSearch(queryBuilder.toString(), topK);
    }

    /**
     * Phrase search with proximity (e.g., "payment processing"~5).
     */
    public List<SearchResult> phraseSearch(String phrase, int slop, int topK)
            throws SearchException {
        String queryString = "\"" + phrase + "\"~" + slop;
        return keywordSearch(queryString, topK);
    }

    /**
     * Wildcard search (e.g., "pay*").
     */
    public List<SearchResult> wildcardSearch(String pattern, int topK) throws SearchException {
        return keywordSearch(pattern, topK);
    }

    /**
     * Fuzzy search for typo tolerance (e.g., "paymnt"~2).
     */
    public List<SearchResult> fuzzySearch(String term, int maxEdits, int topK)
            throws SearchException {
        String queryString = term + "~" + maxEdits;
        return keywordSearch(queryString, topK);
    }

    /**
     * Convert Lucene Document to SearchResult.
     */
    private SearchResult documentToSearchResult(Document doc, float score) {
        SearchResult result = new SearchResult();

        result.setId(doc.get("id"));
        result.setContent(doc.get("content"));
        result.setScore(score);
        result.setFilePath(doc.get("filePath"));
        result.setFileName(doc.get("fileName"));
        result.setPackageName(doc.get("packageName"));
        result.setClassName(doc.get("className"));
        result.setMethodName(doc.get("methodName"));
        result.setChunkType(doc.get("chunkType"));

        String startLine = doc.get("startLine");
        if (startLine != null) {
            result.setStartLine(Integer.parseInt(startLine));
        }

        String endLine = doc.get("endLine");
        if (endLine != null) {
            result.setEndLine(Integer.parseInt(endLine));
        }

        return result;
    }

    /**
     * Highlight matching terms in content.
     */
    private String highlightMatches(Query query, String content) {
        try {
            Formatter formatter = new SimpleHTMLFormatter("<mark>", "</mark>");
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleFragmenter(200);
            highlighter.setTextFragmenter(fragmenter);

            TokenStream tokenStream = analyzer.tokenStream("content",
                    new StringReader(content));

            String highlighted = highlighter.getBestFragment(tokenStream, content);

            if (highlighted != null) {
                return highlighted;
            }
        } catch (Exception e) {
            logger.warn("Failed to highlight content", e);
        }

        // Return truncated content if highlighting fails
        return content.length() > 200 ? content.substring(0, 200) + "..." : content;
    }
}
```

### Advice for Lucene Queries

**Query Syntax Examples:**

```java
// Exact phrase
"payment processing"

// Boolean operators
className:Payment AND methodName:process

// Wildcard
pay* AND process*

// Field-specific
className:PaymentService

// Fuzzy (typo tolerance)
paymnt~2

// Proximity search (words within 5 positions)
"payment processing"~5

// Range query
startLine:[1 TO 100]

// Negation
className:Payment NOT methodName:test

// Grouping
(payment OR billing) AND (process OR handle)

// Boost specific terms
payment^2 processing
```

**Best Practices:**

1. **Escape special characters**: `+ - && || ! ( ) { } [ ] ^ " ~ * ? : \ /`
2. **Use field-specific queries** for precise matches
3. **Combine with filters** to narrow results
4. **Set appropriate slop** for phrase queries (5-10 is usually good)
5. **Use fuzzy search sparingly** (can be slow on large indexes)

---

## Day 27: Hybrid Search Implementation

### Example 27.1: Complete Hybrid Search Service

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.SearchResult;
import com.codetalkerl.firestick.exception.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Hybrid search combining semantic and keyword search with intelligent re-ranking.
 */
@Service
public class HybridSearchService {
    private static final Logger logger = LoggerFactory.getLogger(HybridSearchService.class);
    
    private final SemanticSearchService semanticSearch;
    private final KeywordSearchService keywordSearch;
    private final ResultReRanker reRanker;
    
    @Value("${search.semantic.weight:0.6}")
    private double semanticWeight;
    
    @Value("${search.keyword.weight:0.4}")
    private double keywordWeight;
    
    @Value("${search.hybrid.diversity:0.3}")
    private double diversityWeight;

    public HybridSearchService(SemanticSearchService semanticSearch,
                              KeywordSearchService keywordSearch,
                              ResultReRanker reRanker) {
        this.semanticSearch = semanticSearch;
        this.keywordSearch = keywordSearch;
        this.reRanker = reRanker;
    }

    /**
     * Execute hybrid search combining semantic and keyword approaches.
     *
     * @param query Search query
     * @param topK Number of results to return
     * @return Merged and re-ranked search results
     */
    public List<SearchResult> search(String query, int topK) throws SearchException {
        logger.info("Hybrid search: query='{}', topK={}", query, topK);
        long startTime = System.currentTimeMillis();
        
        // Execute both searches in parallel
        CompletableFuture<List<SearchResult>> semanticFuture = 
            CompletableFuture.supplyAsync(() -> {
                try {
                    long start = System.currentTimeMillis();
                    List<SearchResult> results = semanticSearch.search(query, topK);
                    logger.debug("Semantic search took {}ms", 
                        System.currentTimeMillis() - start);
                    return results;
                } catch (Exception e) {
                    logger.error("Semantic search failed in hybrid search", e);
                    return Collections.emptyList();
                }
            });
        
        CompletableFuture<List<SearchResult>> keywordFuture = 
            CompletableFuture.supplyAsync(() -> {
                try {
                    long start = System.currentTimeMillis();
                    List<SearchResult> results = keywordSearch.keywordSearch(query, topK);
                    logger.debug("Keyword search took {}ms", 
                        System.currentTimeMillis() - start);
                    return results;
                } catch (Exception e) {
                    logger.error("Keyword search failed in hybrid search", e);
                    return Collections.emptyList();
                }
            });
        
        // Wait for both to complete
        List<SearchResult> semanticResults = semanticFuture.join();
        List<SearchResult> keywordResults = keywordFuture.join();
        
        logger.debug("Semantic found {}, keyword found {}", 
            semanticResults.size(), keywordResults.size());
        
        // Handle case where both searches failed
        if (semanticResults.isEmpty() && keywordResults.isEmpty()) {
            logger.warn("Both search methods returned no results");
            return Collections.emptyList();
        }
        
        // Normalize and weight scores
        normalizeScores(semanticResults, semanticWeight);
        normalizeScores(keywordResults, keywordWeight);
        
        // Merge results
        List<SearchResult> merged = mergeResults(semanticResults, keywordResults);
        logger.debug("Merged to {} unique results", merged.size());
        
        // Re-rank with domain-specific boosts
        List<SearchResult> reRanked = reRanker.reRank(merged, query);
        
        // Apply diversity to avoid too many results from same file
        List<SearchResult> diversified = applyDiversity(reRanked, diversityWeight);
        
        // Take top K
        List<SearchResult> topResults = diversified.stream()
            .limit(topK)
            .collect(Collectors.toList());
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Hybrid search completed in {}ms, returning {} results", 
            duration, topResults.size());
        
        return topResults;
    }

    /**
     * Normalize scores to 0-1 range and apply weight.
     */
    private void normalizeScores(List<SearchResult> results, double weight) {
        if (results.isEmpty()) return;
        
        double maxScore = results.stream()
            .mapToDouble(SearchResult::getScore)
            .max()
            .orElse(1.0);
        
        double minScore = results.stream()
            .mapToDouble(SearchResult::getScore)
            .min()
            .orElse(0.0);
        
        // Avoid division by zero
        double range = maxScore - minScore;
        if (range == 0) range = 1.0;
        
        results.forEach(result -> {
            double normalized = (result.getScore() - minScore) / range;
            double weighted = normalized * weight;
            result.setScore(weighted);
        });
        
        logger.debug("Normalized {} results with weight {}", results.size(), weight);
    }

    /**
     * Merge results from both search types, combining scores for duplicates.
     */
    private List<SearchResult> mergeResults(List<SearchResult> semantic, 
                                           List<SearchResult> keyword) {
        Map<String, SearchResult> merged = new HashMap<>();
        
        // Add all semantic results
        semantic.forEach(result -> {
            String key = generateKey(result);
            merged.put(key, result);
            result.addSearchType("SEMANTIC");
        });
        
        // Add keyword results, combining scores for duplicates
        keyword.forEach(result -> {
            String key = generateKey(result);
            
            if (merged.containsKey(key)) {
                // Duplicate found - combine scores
                SearchResult existing = merged.get(key);
                existing.setScore(existing.getScore() + result.getScore());
                existing.addSearchType("KEYWORD");
                
                // Keep highlighted content from keyword search if available
                if (result.getHighlightedContent() != null) {
                    existing.setHighlightedContent(result.getHighlightedContent());
                }
                
                logger.debug("Combined duplicate result: {} (new score: {})", 
                    key, existing.getScore());
            } else {
                result.addSearchType("KEYWORD");
                merged.put(key, result);
            }
        });
        
        return new ArrayList<>(merged.values());
    }

    /**
     * Generate unique key for deduplication.
     */
    private String generateKey(SearchResult result) {
        // Use file path and line numbers as unique identifier
        return result.getFilePath() + ":" + 
               result.getStartLine() + "-" + 
               result.getEndLine();
    }

    /**
     * Apply diversity to avoid too many results from same file.
     */
    private List<SearchResult> applyDiversity(List<SearchResult> results, double weight) {
        if (weight == 0 || results.isEmpty()) {
            return results;
        }
        
        Map<String, Integer> fileCount = new HashMap<>();
        List<SearchResult> diversified = new ArrayList<>();
        
        for (SearchResult result : results) {
            String filePath = result.getFilePath();
            int count = fileCount.getOrDefault(filePath, 0);
            
            // Apply penalty for multiple results from same file
            double penalty = count * weight;
            result.setScore(result.getScore() * (1.0 - penalty));
            
            diversified.add(result);
            fileCount.put(filePath, count + 1);
        }
        
        // Re-sort after applying diversity penalty
        diversified.sort(Collections.reverseOrder());
        
        logger.debug("Applied diversity penalty (weight: {})", weight);
        return diversified;
    }
}
```

### Example 27.2: Result Re-Ranker

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Re-ranks search results using domain-specific heuristics.
 */
@Component
public class ResultReRanker {
    private static final Logger logger = LoggerFactory.getLogger(ResultReRanker.class);

    /**
     * Re-rank results with domain-specific boosts.
     *
     * Boosts applied:
     * - Exact name match: +20%
     * - Public methods/classes: +10%
     * - Non-test files: +5%
     * - Main source (not generated): +5%
     */
    public List<SearchResult> reRank(List<SearchResult> results, String query) {
        if (results.isEmpty()) {
            return results;
        }
        
        logger.debug("Re-ranking {} results for query: '{}'", results.size(), query);
        
        List<SearchResult> reRanked = new ArrayList<>(results);
        String queryLower = query.toLowerCase();
        
        for (SearchResult result : reRanked) {
            double originalScore = result.getScore();
            double boost = 1.0;
            
            // Boost for exact name match
            if (hasExactNameMatch(result, queryLower)) {
                boost += 0.20;
                logger.trace("Exact name match boost for: {}", result.getClassName());
            }
            
            // Boost for public methods/classes
            if (isPublic(result)) {
                boost += 0.10;
            }
            
            // Boost for non-test files
            if (!isTestFile(result)) {
                boost += 0.05;
            }
            
            // Boost for main source code (not generated)
            if (!isGeneratedFile(result)) {
                boost += 0.05;
            }
            
            // Penalty for test files if query doesn't mention "test"
            if (isTestFile(result) && !queryLower.contains("test")) {
                boost -= 0.10;
            }
            
            // Apply boost
            result.setScore(originalScore * boost);
            
            if (boost != 1.0) {
                logger.trace("Re-ranked {}: {} -> {} (boost: {})", 
                    result.getId(), originalScore, result.getScore(), boost);
            }
        }
        
        // Sort by new scores
        reRanked.sort(Collections.reverseOrder());
        
        logger.debug("Re-ranking complete");
        return reRanked;
    }

    /**
     * Check if result has exact match with query.
     */
    private boolean hasExactNameMatch(SearchResult result, String queryLower) {
        if (result.getClassName() != null && 
            result.getClassName().toLowerCase().contains(queryLower)) {
            return true;
        }
        if (result.getMethodName() != null && 
            result.getMethodName().toLowerCase().contains(queryLower)) {
            return true;
        }
        return false;
    }

    /**
     * Check if result is a public method or class.
     * This would ideally check metadata, for now check naming conventions.
     */
    private boolean isPublic(SearchResult result) {
        // In a full implementation, this would check actual modifiers from metadata
        // For now, assume classes and methods are public unless in private packages
        String packageName = result.getPackageName();
        return packageName == null || !packageName.contains(".internal.");
    }

    /**
     * Check if result is from a test file.
     */
    private boolean isTestFile(SearchResult result) {
        String filePath = result.getFilePath();
        if (filePath == null) return false;
        
        return filePath.contains("/test/") || 
               filePath.contains("\\test\\") ||
               filePath.endsWith("Test.java") ||
               filePath.endsWith("Tests.java");
    }

    /**
     * Check if result is from a generated file.
     */
    private boolean isGeneratedFile(SearchResult result) {
        String filePath = result.getFilePath();
        if (filePath == null) return false;
        
        return filePath.contains("/generated/") || 
               filePath.contains("\\generated\\") ||
               filePath.contains("/target/") ||
               filePath.contains("\\target\\") ||
               filePath.contains("/build/") ||
               filePath.contains("\\build\\");
    }
}
```

---

## Day 28: Query Processing and Routing

### Example 28.1: Query Analyzer

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.QueryAnalysis;
import com.codetalkerl.firestick.dto.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes queries to determine type and best search strategy.
 */
@Component
public class QueryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(QueryAnalyzer.class);
    
    // Patterns for detecting query types
    private static final Pattern EXACT_SYMBOL_PATTERN = 
        Pattern.compile("^[A-Z][a-zA-Z0-9]*(?:\\.[a-z][a-zA-Z0-9]*)*$");
    
    private static final Pattern METHOD_SIGNATURE_PATTERN = 
        Pattern.compile("[A-Z][a-zA-Z0-9]*\\.[a-z][a-zA-Z0-9]*\\(");
    
    private static final Pattern DEPENDENCY_PATTERN = 
        Pattern.compile("(?i)(what|which|who|find).*(call|use|depend|reference)");
    
    private static final Pattern CODE_PATTERN = 
        Pattern.compile("(public|private|protected|class|interface|void|return|if|for|while)\\s+");

    /**
     * Analyze query to determine type and strategy.
     */
    public QueryAnalysis analyzeQuery(String query) {
        logger.debug("Analyzing query: '{}'", query);
        
        QueryAnalysis analysis = new QueryAnalysis();
        analysis.setQuery(query);
        
        // Determine query type
        QueryType type = determineQueryType(query);
        analysis.setType(type);
        
        // Extract entities (class names, method names, etc.)
        String entity = extractEntity(query, type);
        analysis.setExtractedEntity(entity);
        
        // Determine search strategy
        String strategy = determineStrategy(type);
        analysis.setStrategy(strategy);
        
        logger.debug("Query analysis: type={}, entity='{}', strategy={}", 
            type, entity, strategy);
        
        return analysis;
    }

    /**
     * Determine query type based on patterns.
     */
    private QueryType determineQueryType(String query) {
        String trimmed = query.trim();
        
        // Check for exact symbol (e.g., "ClassName.methodName")
        if (EXACT_SYMBOL_PATTERN.matcher(trimmed).matches()) {
            return QueryType.EXACT_SYMBOL;
        }
        
        // Check for method signature (e.g., "ClassName.method(")
        if (METHOD_SIGNATURE_PATTERN.matcher(trimmed).find()) {
            return QueryType.EXACT_SYMBOL;
        }
        
        // Check for dependency query
        if (DEPENDENCY_PATTERN.matcher(trimmed).find()) {
            return QueryType.DEPENDENCY;
        }
        
        // Check for code snippet
        if (CODE_PATTERN.matcher(trimmed).find()) {
            return QueryType.CODE_SNIPPET;
        }
        
        // Check for package query
        if (trimmed.matches("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)+$")) {
            return QueryType.PACKAGE;
        }
        
        // Default to natural language
        return QueryType.NATURAL_LANGUAGE;
    }

    /**
     * Extract entity from query based on type.
     */
    private String extractEntity(String query, QueryType type) {
        switch (type) {
            case EXACT_SYMBOL:
                return extractSymbolName(query);
                
            case DEPENDENCY:
                return extractDependencyTarget(query);
                
            case PACKAGE:
                return query.trim();
                
            default:
                return null;
        }
    }

    /**
     * Extract symbol name from query.
     */
    private String extractSymbolName(String query) {
        // Remove parentheses and anything after
        String cleaned = query.split("\\(")[0].trim();
        
        // Return the symbol name
        return cleaned;
    }

    /**
     * Extract dependency target from natural language query.
     */
    private String extractDependencyTarget(String query) {
        // Look for capitalized words (likely class names)
        Pattern pattern = Pattern.compile("[A-Z][a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(query);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }

    /**
     * Determine best search strategy for query type.
     */
    private String determineStrategy(QueryType type) {
        switch (type) {
            case EXACT_SYMBOL:
                return "SYMBOL_TABLE";
                
            case DEPENDENCY:
                return "GRAPH_TRAVERSAL";
                
            case CODE_SNIPPET:
                return "KEYWORD_SEARCH";
                
            case PACKAGE:
                return "SYMBOL_TABLE";
                
            case NATURAL_LANGUAGE:
            default:
                return "HYBRID_SEARCH";
        }
    }
}
```

### Example 28.2: Query Analysis DTOs

```java
package com.codetalkerl.firestick.dto;

/**
 * Result of query analysis.
 */
public class QueryAnalysis {
    private String query;
    private QueryType type;
    private String extractedEntity;
    private String strategy;

    // Getters and setters
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryType getType() {
        return type;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    public String getExtractedEntity() {
        return extractedEntity;
    }

    public void setExtractedEntity(String extractedEntity) {
        this.extractedEntity = extractedEntity;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
```

```java
package com.codetalkerl.firestick.dto;

/**
 * Types of queries Firestick can handle.
 */
public enum QueryType {
    EXACT_SYMBOL,       // "ClassName.methodName"
    DEPENDENCY,         // "what calls PaymentService"
    CODE_SNIPPET,       // "public void process("
    PACKAGE,            // "com.example.service"
    NATURAL_LANGUAGE    // "payment processing logic"
}
```

### Example 28.3: Query Processor

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalkerl.firestick.exception.SearchException;
import com.codetalkerl.firestick.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main query processor that routes queries to appropriate search strategies.
 */
@Service
public class QueryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class);

    private final QueryAnalyzer analyzer;
    private final SymbolTableService symbolTable;
    private final HybridSearchService hybridSearch;
    private final SemanticSearchService semanticSearch;
    private final KeywordSearchService keywordSearch;
    private final DependencyGraphService graphService;

    public QueryProcessor(QueryAnalyzer analyzer,
                          SymbolTableService symbolTable,
                          HybridSearchService hybridSearch,
                          SemanticSearchService semanticSearch,
                          KeywordSearchService keywordSearch,
                          DependencyGraphService graphService) {
        this.analyzer = analyzer;
        this.symbolTable = symbolTable;
        this.hybridSearch = hybridSearch;
        this.semanticSearch = semanticSearch;
        this.keywordSearch = keywordSearch;
        this.graphService = graphService;
    }

    /**
     * Process query and route to appropriate search strategy.
     */
    public SearchResponse process(String query, int topK) throws SearchException {
        logger.info("Processing query: '{}', topK={}", query, topK);
        long startTime = System.currentTimeMillis();

        try {
            // Analyze query
            QueryAnalysis analysis = analyzer.analyzeQuery(query);

            // Route to appropriate handler
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

                case PACKAGE:
                    response = processPackageQuery(analysis, topK);
                    break;

                case NATURAL_LANGUAGE:
                default:
                    response = processNaturalLanguageQuery(analysis, topK);
                    break;
            }

            // Add metadata
            long duration = System.currentTimeMillis() - startTime;
            response.setQuery(query);
            response.setQueryType(analysis.getType().toString());
            response.setStrategy(analysis.getStrategy());
            response.setProcessingTimeMs(duration);

            logger.info("Query processed in {}ms, found {} results",
                    duration, response.getTotalCount());

            return response;

        } catch (Exception e) {
            logger.error("Query processing failed", e);
            throw new SearchException("Failed to process query: " + query, e);
        }
    }

    /**
     * Process exact symbol query using symbol table.
     */
    private SearchResponse processSymbolQuery(QueryAnalysis analysis, int topK) {
        String symbolName = analysis.getExtractedEntity();
        logger.debug("Processing symbol query: {}", symbolName);

        List<Symbol> symbols;

        // Try exact match by FQN first
        Optional<Symbol> exactMatch = symbolTable.findByFullyQualifiedName(symbolName);
        if (exactMatch.isPresent()) {
            symbols = Collections.singletonList(exactMatch.get());
        } else {
            // Try by simple name
            symbols = symbolTable.findByName(symbolName);
        }

        // Convert to SearchResults
        List<SearchResult> results = symbols.stream()
                .limit(topK)
                .map(this::symbolToSearchResult)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .results(results)
                .totalCount(symbols.size())
                .build();
    }

    /**
     * Process dependency query using graph traversal.
     */
    private SearchResponse processDependencyQuery(QueryAnalysis analysis, int topK) {
        String target = analysis.getExtractedEntity();
        logger.debug("Processing dependency query for: {}", target);

        // Find target symbol
        List<Symbol> symbols = symbolTable.findByName(target);
        if (symbols.isEmpty()) {
            logger.warn("Target symbol not found: {}", target);
            return SearchResponse.empty("Symbol not found: " + target);
        }

        Symbol symbol = symbols.get(0);
        String query = analysis.getQuery().toLowerCase();

        // Determine direction (callers or callees)
        List<SearchResult> results;
        if (query.contains("call") || query.contains("use") || query.contains("depend")) {
            // Find what calls/uses this symbol
            results = getCallers(symbol, topK);
        } else {
            // Find what this symbol calls/uses
            results = getCallees(symbol, topK);
        }

        return SearchResponse.builder()
                .results(results)
                .totalCount(results.size())
                .build();
    }

    /**
     * Process code snippet query using keyword search.
     */
    private SearchResponse processCodeSnippetQuery(QueryAnalysis analysis, int topK)
            throws SearchException {
        logger.debug("Processing code snippet query");

        // Use keyword search for exact code matching
        List<SearchResult> results = keywordSearch.keywordSearch(
                analysis.getQuery(), topK);

        return SearchResponse.builder()
                .results(results)
                .totalCount(results.size())
                .build();
    }

    /**
     * Process package query.
     */
    private SearchResponse processPackageQuery(QueryAnalysis analysis, int topK) {
        String packageName = analysis.getExtractedEntity();
        logger.debug("Processing package query: {}", packageName);

        List<Symbol> symbols = symbolTable.findByPackage(packageName);

        List<SearchResult> results = symbols.stream()
                .limit(topK)
                .map(this::symbolToSearchResult)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .results(results)
                .totalCount(symbols.size())
                .build();
    }

    /**
     * Process natural language query using hybrid search.
     */
    private SearchResponse processNaturalLanguageQuery(QueryAnalysis analysis, int topK)
            throws SearchException {
        logger.debug("Processing natural language query");

        List<SearchResult> results = hybridSearch.search(analysis.getQuery(), topK);

        return SearchResponse.builder()
                .results(results)
                .totalCount(results.size())
                .build();
    }

    /**
     * Get callers of a symbol from dependency graph.
     */
    private List<SearchResult> getCallers(Symbol symbol, int limit) {
        // Use dependency graph to find callers
        List<String> callers = graphService.getClassDependents(
                symbol.getFullyQualifiedName());

        return callers.stream()
                .limit(limit)
                .map(caller -> {
                    SearchResult result = new SearchResult();
                    result.setClassName(caller);
                    result.setSearchType("DEPENDENCY");
                    result.setScore(1.0);
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get callees of a symbol from dependency graph.
     */
    private List<SearchResult> getCallees(Symbol symbol, int limit) {
        List<String> callees = graphService.getClassDependencies(
                symbol.getFullyQualifiedName());

        return callees.stream()
                .limit(limit)
                .map(callee -> {
                    SearchResult result = new SearchResult();
                    result.setClassName(callee);
                    result.setSearchType("DEPENDENCY");
                    result.setScore(1.0);
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert Symbol to SearchResult.
     */
    private SearchResult symbolToSearchResult(Symbol symbol) {
        SearchResult result = new SearchResult();
        result.setId(symbol.getFullyQualifiedName());
        result.setClassName(symbol.getName());
        result.setFullyQualifiedClassName(symbol.getFullyQualifiedName());
        result.setPackageName(symbol.getPackageName());
        result.setFilePath(symbol.getFilePath());
        result.setStartLine(symbol.getStartLine());
        result.setEndLine(symbol.getEndLine());
        result.setSearchType("SYMBOL");
        result.setScore(1.0);
        return result;
    }
}
```

### Example 28.4: SearchResponse DTO

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Response from search operations.
 */
public class SearchResponse {
    private String query;
    private String queryType;
    private String strategy;
    private List<SearchResult> results;
    private int totalCount;
    private long processingTimeMs;
    private List<String> suggestions;
    private String errorMessage;
    private boolean success;

    public static SearchResponse empty(String message) {
        SearchResponse response = new SearchResponse();
        response.results = Collections.emptyList();
        response.totalCount = 0;
        response.suggestions = Collections.emptyList();
        response.errorMessage = message;
        response.success = false;
        return response;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // Builder
    public static class Builder {
        private SearchResponse response = new SearchResponse();

        public Builder results(List<SearchResult> results) {
            response.results = results;
            response.success = true;
            return this;
        }

        public Builder totalCount(int totalCount) {
            response.totalCount = totalCount;
            return this;
        }

        public SearchResponse build() {
            if (response.results == null) {
                response.results = new ArrayList<>();
            }
            if (response.suggestions == null) {
                response.suggestions = new ArrayList<>();
            }
            return response;
        }
    }
}
```

---

## Day 29: Context Assembly

### Example 29.1: Context Assembly Service

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalkerl.firestick.dto.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds surrounding code context to search results.
 */
@Service
public class ContextAssemblyService {
    private static final Logger logger = LoggerFactory.getLogger(ContextAssemblyService.class);

    private final DependencyGraphService graphService;

    @Value("${search.context.lines:5}")
    private int contextLines;

    @Value("${search.context.max.size:100}")
    private int maxContextLines;

    public ContextAssemblyService(DependencyGraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * Add context to a search result.
     */
    public void addContext(SearchResult result) {
        if (result == null || result.getFilePath() == null) {
            return;
        }

        logger.debug("Adding context to result: {}", result.getId());

        try {
            // Add surrounding code lines
            addSurroundingLines(result);

            // Add method context (callers/callees)
            if (result.getMethodName() != null) {
                addMethodContext(result);
            }

            // Add class context (hierarchy)
            if (result.getClassName() != null) {
                addClassContext(result);
            }

        } catch (Exception e) {
            logger.warn("Failed to add context to result: {}", result.getId(), e);
            // Don't fail the search, just skip context
        }
    }

    /**
     * Add surrounding code lines from source file.
     */
    private void addSurroundingLines(SearchResult result) throws Exception {
        Path filePath = Paths.get(result.getFilePath());

        if (!Files.exists(filePath)) {
            logger.warn("File not found: {}", filePath);
            return;
        }

        List<String> allLines = Files.readAllLines(filePath);

        // Calculate context range
        int start = Math.max(0, result.getStartLine() - contextLines - 1);
        int end = Math.min(allLines.size(), result.getEndLine() + contextLines);

        // Limit total context size
        if (end - start > maxContextLines) {
            end = start + maxContextLines;
        }

        // Extract context lines
        List<String> contextLines = allLines.subList(start, end);
        String context = String.join("\n", contextLines);

        result.setFullContext(context);
        result.setContextStartLine(start + 1);
        result.setContextEndLine(end);

        logger.debug("Added {} lines of context ({}-{})",
                contextLines.size(), start + 1, end);
    }

    /**
     * Add method context (callers and callees).
     */
    private void addMethodContext(SearchResult result) {
        String methodFQN = result.getFullyQualifiedClassName() + "." + result.getMethodName();

        // Get callers (what calls this method)
        List<String> callers = graphService.getMethodCallers(methodFQN);
        if (callers != null && !callers.isEmpty()) {
            result.setCallers(callers.stream()
                    .limit(5)
                    .collect(Collectors.toList()));
        }

        // Get callees (what this method calls)
        List<String> callees = graphService.getMethodCallees(methodFQN);
        if (callees != null && !callees.isEmpty()) {
            result.setCallees(callees.stream()
                    .limit(5)
                    .collect(Collectors.toList()));
        }

        logger.debug("Added method context: {} callers, {} callees",
                result.getCallers() != null ? result.getCallers().size() : 0,
                result.getCallees() != null ? result.getCallees().size() : 0);
    }

    /**
     * Add class context (hierarchy and related classes).
     */
    private void addClassContext(SearchResult result) {
        String className = result.getFullyQualifiedClassName();

        // Get class dependencies
        List<String> dependencies = graphService.getClassDependencies(className);
        if (dependencies != null && !dependencies.isEmpty()) {
            // Store first few as "related classes"
            result.setInterfaces(dependencies.stream()
                    .limit(3)
                    .collect(Collectors.toList()));
        }

        logger.debug("Added class context for: {}", className);
    }

    /**
     * Add context to multiple results in batch.
     */
    public void addContextToAll(List<SearchResult> results) {
        logger.info("Adding context to {} results", results.size());

        results.parallelStream().forEach(this::addContext);

        logger.info("Context assembly complete");
    }
}
```

---

## Day 30: Search REST API

### Example 30.1: Complete Search Controller

```java
package com.codetalkerl.firestick.controller;

import com.codetalkerl.firestick.exception.SearchException;
import com.codetalkerl.firestick.service.ContextAssemblyService;
import com.codetalkerl.firestick.service.QueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * REST API for search operations.
 */
@RestController
@RequestMapping("/api/search")
@Validated
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final QueryProcessor queryProcessor;
    private final ContextAssemblyService contextAssembly;

    public SearchController(QueryProcessor queryProcessor,
                            ContextAssemblyService contextAssembly) {
        this.queryProcessor = queryProcessor;
        this.contextAssembly = contextAssembly;
    }

    /**
     * General search endpoint.
     */
    @PostMapping
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        logger.info("Search request: query='{}', topK={}",
                request.getQuery(), request.getTopK());

        try {
            // Process query
            SearchResponse response = queryProcessor.process(
                    request.getQuery(),
                    request.getTopK()
            );

            // Add context if requested
            if (request.isIncludeContext()) {
                contextAssembly.addContextToAll(response.getResults());
            }

            return ResponseEntity.ok(response);

        } catch (SearchException e) {
            logger.error("Search failed", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SearchResponse.empty(e.getMessage()));
        }
    }

    /**
     * Symbol lookup endpoint.
     */
    @GetMapping("/symbol/{name}")
    public ResponseEntity<SearchResponse> findSymbol(
            @PathVariable String name,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        logger.info("Symbol lookup: name='{}', limit={}", name, limit);

        SearchRequest request = new SearchRequest();
        request.setQuery(name);
        request.setTopK(limit);
        request.setIncludeContext(true);

        return search(request);
    }

    /**
     * Similar code search endpoint.
     */
    @PostMapping("/similar")
    public ResponseEntity<SearchResponse> findSimilarCode(
            @RequestBody CodeSnippet snippet,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {

        logger.info("Similar code search: limit={}", limit);

        try {
            SearchRequest request = new SearchRequest();
            request.setQuery(snippet.getCode());
            request.setTopK(limit);
            request.setIncludeContext(false);

            SearchResponse response = queryProcessor.process(
                    snippet.getCode(), limit);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Similar code search failed", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SearchResponse.empty(e.getMessage()));
        }
    }

    /**
     * Get symbol dependencies.
     */
    @GetMapping("/dependencies/{symbol}")
    public ResponseEntity<SearchResponse> getDependencies(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "callers") String type,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {

        logger.info("Dependency query: symbol='{}', type={}", symbol, type);

        try {
            String query;
            if ("callers".equals(type)) {
                query = "what calls " + symbol;
            } else if ("callees".equals(type)) {
                query = "what does " + symbol + " call";
            } else {
                return ResponseEntity.badRequest()
                        .body(SearchResponse.empty("Invalid type: " + type));
            }

            SearchResponse response = queryProcessor.process(query, limit);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Dependency query failed", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SearchResponse.empty(e.getMessage()));
        }
    }
}
```

### Example 30.2: Request DTOs

```java
package com.codetalkerl.firestick.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Search request DTO.
 */
public class SearchRequest {
    
    @NotBlank(message = "Query cannot be empty")
    private String query;
    
    @Min(1)
    @Max(100)
    private int topK = 10;
    
    private boolean includeContext = true;
    
    private SearchFilter filter;

    // Getters and setters
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public boolean isIncludeContext() {
        return includeContext;
    }

    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    public SearchFilter getFilter() {
        return filter;
    }

    public void setFilter(SearchFilter filter) {
        this.filter = filter;
    }
}
```

```java
package com.codetalkerl.firestick.dto;

/**
 * Code snippet for similarity search.
 */
public class CodeSnippet {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
```

```java
package com.codetalkerl.firestick.dto;

import java.util.List;

/**
 * Filter for search results.
 */
public class SearchFilter {
    private String filePathPattern;
    private String packageName;
    private List<String> codeTypes; // CLASS, METHOD, FIELD

    public String getFilePathPattern() {
        return filePathPattern;
    }

    public void setFilePathPattern(String filePathPattern) {
        this.filePathPattern = filePathPattern;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getCodeTypes() {
        return codeTypes;
    }

    public void setCodeTypes(List<String> codeTypes) {
        this.codeTypes = codeTypes;
    }

    /**
     * Check if result matches filter.
     */
    public boolean matches(SearchResult result) {
        if (filePathPattern != null && !result.getFilePath().matches(filePathPattern)) {
            return false;
        }
        
        if (packageName != null && !packageName.equals(result.getPackageName())) {
            return false;
        }
        
        if (codeTypes != null && !codeTypes.contains(result.getChunkType())) {
            return false;
        }
        
        return true;
    }
}
```

---

## Phase 3 Summary - Best Practices & Tips

### Query Optimization Tips

**1. Use Appropriate Search Type:**
- **Exact lookups**: Use symbol name directly
- **Concept search**: Use natural language
- **Code patterns**: Use code snippet with keywords
- **Dependencies**: Use "what calls X" format

**2. Improve Result Quality:**
```java
// Good queries
"payment processing logic"
"authenticate user with JWT"
"database connection pooling"

// Bad queries (too vague)
"code"
"method"
"process"
```

**3. Leverage Metadata Filters:**
```java
SearchFilter filter = new SearchFilter();
filter.setPackageName("com.example.service");
filter.setCodeTypes(Arrays.asList("METHOD"));
// Results will only include methods from specified package
```

**4. Balance Performance vs Accuracy:**
- Start with `topK=10`, increase if needed
- Use context sparingly (adds overhead)
- Cache frequently used queries
- Implement query timeout (5 seconds max)

### Testing Search Quality

```java
@Test
void testSearchQuality() {
    // Known code location
    String query = "payment processing";
    SearchResponse response = queryProcessor.process(query, 10);
    
    // Verify relevant result is in top 10
    boolean found = response.getResults().stream()
        .anyMatch(r -> r.getClassName().contains("Payment"));
    
    assertTrue(found, "Expected PaymentService in top 10 results");
    
    // Verify reasonable processing time
    assertTrue(response.getProcessingTimeMs() < 2000, 
        "Search should complete in <2 seconds");
}
```

### Common Patterns

**Pattern 1: Multi-strategy fallback**
```java
// Try exact match first, fall back to fuzzy
List<Symbol> results = symbolTable.findByFullyQualifiedName(query);
if (results.isEmpty()) {
    results = symbolTable.findByName(query);
}
if (results.isEmpty()) {
    results = symbolTable.findSimilar(query, 2);
}
```

**Pattern 2: Progressive enhancement**
```java
// Start fast with symbol table, add semantic if needed
SearchResponse response = symbolLookup(query);
if (response.getTotalCount() < 5) {
    // Augment with semantic search
    List<SearchResult> semantic = semanticSearch.search(query, 10);
    response.getResults().addAll(semantic);
}
```

**Pattern 3: Result deduplication**
```java
Set<String> seen = new HashSet<>();
List<SearchResult> unique = allResults.stream()
    .filter(r -> seen.add(r.getFilePath() + ":" + r.getStartLine()))
    .collect(Collectors.toList());
```

---

# Phase 4: Analysis Features (Weeks 7-8, Days 34-42)

This phase builds code analysis tools for quality metrics, complexity analysis, dead code detection, and pattern recognition to help developers improve code quality.

---

## Day 34: Complexity Analysis

### Example 34.1: Complete Complexity Analyzer

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.ComplexityMetrics;
import com.codetalkerl.firestick.dto.FileInfo;
import com.codetalkerl.firestick.dto.MethodInfo;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes code complexity using cyclomatic complexity and other metrics.
 */
@Service
public class ComplexityAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ComplexityAnalyzer.class);

    /**
     * Analyze complexity for all methods in a file.
     */
    public List<ComplexityMetrics> analyzeFile(FileInfo fileInfo) {
        logger.info("Analyzing complexity for file: {}", fileInfo.getFilePath());
        
        List<ComplexityMetrics> allMetrics = new ArrayList<>();
        
        fileInfo.getClasses().forEach(classInfo -> {
            classInfo.getMethods().forEach(methodInfo -> {
                ComplexityMetrics metrics = analyzeMethod(methodInfo, classInfo.getName());
                allMetrics.add(metrics);
            });
        });
        
        logger.info("Analyzed {} methods", allMetrics.size());
        return allMetrics;
    }

    /**
     * Analyze complexity for a single method.
     */
    public ComplexityMetrics analyzeMethod(MethodInfo methodInfo, String className) {
        logger.debug("Analyzing method: {}.{}", className, methodInfo.getName());
        
        ComplexityMetrics metrics = new ComplexityMetrics();
        metrics.setMethodName(methodInfo.getName());
        metrics.setClassName(className);
        metrics.setFilePath(methodInfo.getFilePath());
        metrics.setStartLine(methodInfo.getStartLine());
        metrics.setEndLine(methodInfo.getEndLine());
        
        // Calculate cyclomatic complexity
        int complexity = calculateCyclomaticComplexity(methodInfo);
        metrics.setCyclomaticComplexity(complexity);
        
        // Count lines of code
        int loc = methodInfo.getEndLine() - methodInfo.getStartLine() + 1;
        metrics.setLinesOfCode(loc);
        
        // Count parameters
        metrics.setParameterCount(methodInfo.getParameters().size());
        
        // Calculate nesting depth
        int depth = calculateNestingDepth(methodInfo);
        metrics.setMaxNestingDepth(depth);
        
        // Determine complexity level
        String level = determineComplexityLevel(complexity);
        metrics.setComplexityLevel(level);
        
        // Generate recommendations
        List<String> recommendations = generateRecommendations(metrics);
        metrics.setRecommendations(recommendations);
        
        logger.debug("Method {}: complexity={}, loc={}, level={}", 
            methodInfo.getName(), complexity, loc, level);
        
        return metrics;
    }

    /**
     * Calculate cyclomatic complexity using decision points.
     * Cyclomatic Complexity = Decision Points + 1
     */
    private int calculateCyclomaticComplexity(MethodInfo methodInfo) {
        // Parse method body to count decision points
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        
        // In a real implementation, you would parse the method body AST
        // For this example, we'll use a simplified approach
        String body = methodInfo.getBody();
        
        int complexity = 1; // Base complexity
        
        // Count decision points in method body
        complexity += countOccurrences(body, "if ");
        complexity += countOccurrences(body, "else if");
        complexity += countOccurrences(body, "for ");
        complexity += countOccurrences(body, "while ");
        complexity += countOccurrences(body, "case ");
        complexity += countOccurrences(body, "catch ");
        complexity += countOccurrences(body, "&&");
        complexity += countOccurrences(body, "||");
        complexity += countOccurrences(body, "?"); // Ternary operator
        
        return complexity;
    }

    /**
     * Calculate maximum nesting depth.
     */
    private int calculateNestingDepth(MethodInfo methodInfo) {
        String body = methodInfo.getBody();
        
        int maxDepth = 0;
        int currentDepth = 0;
        
        for (char c : body.toCharArray()) {
            if (c == '{') {
                currentDepth++;
                maxDepth = Math.max(maxDepth, currentDepth);
            } else if (c == '}') {
                currentDepth--;
            }
        }
        
        return maxDepth;
    }

    /**
     * Determine complexity level based on cyclomatic complexity.
     */
    private String determineComplexityLevel(int complexity) {
        if (complexity <= 5) return "LOW";
        if (complexity <= 10) return "MEDIUM";
        if (complexity <= 20) return "HIGH";
        return "VERY_HIGH";
    }

    /**
     * Generate recommendations based on metrics.
     */
    private List<String> generateRecommendations(ComplexityMetrics metrics) {
        List<String> recommendations = new ArrayList<>();
        
        if (metrics.getCyclomaticComplexity() > 10) {
            recommendations.add("Consider breaking this method into smaller methods");
            recommendations.add("Extract complex conditional logic into separate methods");
        }
        
        if (metrics.getLinesOfCode() > 50) {
            recommendations.add("Method is too long (>50 lines). Break into smaller methods");
        }
        
        if (metrics.getParameterCount() > 5) {
            recommendations.add("Too many parameters. Consider using a parameter object");
        }
        
        if (metrics.getMaxNestingDepth() > 4) {
            recommendations.add("Deep nesting detected. Consider extracting nested logic");
            recommendations.add("Use early returns to reduce nesting");
        }
        
        return recommendations;
    }

    /**
     * Helper to count string occurrences.
     */
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        
        return count;
    }

    /**
     * JavaParser visitor for accurate cyclomatic complexity calculation.
     */
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
        public void visit(ForEachStmt n, Void arg) {
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
}
```

### Example 34.2: ComplexityMetrics DTO

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Complexity metrics for a method.
 */
public class ComplexityMetrics {
    private String methodName;
    private String className;
    private String filePath;
    private int startLine;
    private int endLine;
    
    private int cyclomaticComplexity;
    private int linesOfCode;
    private int parameterCount;
    private int maxNestingDepth;
    private String complexityLevel; // LOW, MEDIUM, HIGH, VERY_HIGH
    
    private List<String> recommendations = new ArrayList<>();

    // Getters and setters
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    public void setMaxNestingDepth(int maxNestingDepth) {
        this.maxNestingDepth = maxNestingDepth;
    }

    public String getComplexityLevel() {
        return complexityLevel;
    }

    public void setComplexityLevel(String complexityLevel) {
        this.complexityLevel = complexityLevel;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Get color coding for UI visualization.
     */
    public String getColorCode() {
        switch (complexityLevel) {
            case "LOW": return "#28a745"; // Green
            case "MEDIUM": return "#ffc107"; // Yellow
            case "HIGH": return "#fd7e14"; // Orange
            case "VERY_HIGH": return "#dc3545"; // Red
            default: return "#6c757d"; // Gray
        }
    }

    /**
     * Check if method needs refactoring.
     */
    public boolean needsRefactoring() {
        return cyclomaticComplexity > 10 || 
               linesOfCode > 50 || 
               parameterCount > 5 ||
               maxNestingDepth > 4;
    }
}
```

---

## Day 35: Code Smell Detection

### Example 35.1: Complete Code Smell Detector

```java
package com.codetalkerl.firestick.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects code smells and quality issues.
 */
@Service
public class CodeSmellDetector {
    private static final Logger logger = LoggerFactory.getLogger(CodeSmellDetector.class);

    @Value("${analysis.max.method.lines:50}")
    private int maxMethodLines;

    @Value("${analysis.max.class.lines:500}")
    private int maxClassLines;

    @Value("${analysis.max.parameters:5}")
    private int maxParameters;

    @Value("${analysis.max.nesting:4}")
    private int maxNesting;

    /**
     * Detect all code smells in a file.
     */
    public List<CodeSmell> detectSmells(FileInfo fileInfo) {
        logger.info("Detecting code smells in: {}", fileInfo.getFilePath());

        List<CodeSmell> smells = new ArrayList<>();

        // Check each class
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            smells.addAll(detectClassSmells(classInfo));

            // Check each method
            for (MethodInfo method : classInfo.getMethods()) {
                smells.addAll(detectMethodSmells(method, classInfo));
            }
        }

        logger.info("Found {} code smells", smells.size());
        return smells;
    }

    /**
     * Detect class-level code smells.
     */
    private List<CodeSmell> detectClassSmells(ClassInfo classInfo) {
        List<CodeSmell> smells = new ArrayList<>();

        // Large class
        int classLines = classInfo.getEndLine() - classInfo.getStartLine();
        if (classLines > maxClassLines) {
            smells.add(CodeSmell.builder()
                    .type("LARGE_CLASS")
                    .severity("WARNING")
                    .className(classInfo.getName())
                    .description(String.format("Class has %d lines (max: %d)",
                            classLines, maxClassLines))
                    .recommendation("Consider splitting into smaller, focused classes")
                    .lineNumber(classInfo.getStartLine())
                    .build());
        }

        // Too many methods
        if (classInfo.getMethods().size() > 20) {
            smells.add(CodeSmell.builder()
                    .type("TOO_MANY_METHODS")
                    .severity("WARNING")
                    .className(classInfo.getName())
                    .description(String.format("Class has %d methods (max: 20)",
                            classInfo.getMethods().size()))
                    .recommendation("Consider extracting some methods to helper classes")
                    .lineNumber(classInfo.getStartLine())
                    .build());
        }

        // Naming convention violations
        if (!Character.isUpperCase(classInfo.getName().charAt(0))) {
            smells.add(CodeSmell.builder()
                    .type("NAMING_CONVENTION")
                    .severity("INFO")
                    .className(classInfo.getName())
                    .description("Class name should start with uppercase letter")
                    .recommendation("Rename class to follow PascalCase convention")
                    .lineNumber(classInfo.getStartLine())
                    .build());
        }

        return smells;
    }

    /**
     * Detect method-level code smells.
     */
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
                    .recommendation("Consider using a parameter object or builder pattern")
                    .lineNumber(method.getStartLine())
                    .build());
        }

        // Method naming convention
        if (!Character.isLowerCase(method.getName().charAt(0))) {
            smells.add(CodeSmell.builder()
                    .type("NAMING_CONVENTION")
                    .severity("INFO")
                    .className(classInfo.getName())
                    .methodName(method.getName())
                    .description("Method name should start with lowercase letter")
                    .recommendation("Rename method to follow camelCase convention")
                    .lineNumber(method.getStartLine())
                    .build());
        }

        // Magic numbers
        smells.addAll(detectMagicNumbers(method, classInfo));

        // Empty catch blocks
        if (hasEmptyCatchBlock(method)) {
            smells.add(CodeSmell.builder()
                    .type("EMPTY_CATCH_BLOCK")
                    .severity("ERROR")
                    .className(classInfo.getName())
                    .methodName(method.getName())
                    .description("Empty catch block swallows exceptions")
                    .recommendation("Add proper exception handling or logging")
                    .lineNumber(method.getStartLine())
                    .build());
        }

        return smells;
    }

    /**
     * Detect magic numbers in method body.
     */
    private List<CodeSmell> detectMagicNumbers(MethodInfo method, ClassInfo classInfo) {
        List<CodeSmell> smells = new ArrayList<>();

        String body = method.getBody();
        Pattern numberPattern = Pattern.compile("\\b\\d+\\.?\\d*\\b");
        Matcher matcher = numberPattern.matcher(body);

        Set<String> magicNumbers = new HashSet<>();
        while (matcher.find()) {
            String number = matcher.group();
            // Exclude common numbers (0, 1, -1, 100) and array indices
            if (!isCommonNumber(number)) {
                magicNumbers.add(number);
            }
        }

        if (!magicNumbers.isEmpty()) {
            smells.add(CodeSmell.builder()
                    .type("MAGIC_NUMBERS")
                    .severity("INFO")
                    .className(classInfo.getName())
                    .methodName(method.getName())
                    .description("Method contains magic numbers: " +
                            String.join(", ", magicNumbers))
                    .recommendation("Extract numbers to named constants with descriptive names")
                    .lineNumber(method.getStartLine())
                    .build());
        }

        return smells;
    }

    /**
     * Check if number is commonly used (doesn't need to be a constant).
     */
    private boolean isCommonNumber(String number) {
        return number.equals("0") ||
                number.equals("1") ||
                number.equals("-1") ||
                number.equals("2") ||
                number.equals("10") ||
                number.equals("100") ||
                number.equals("1000");
    }

    /**
     * Check for empty catch blocks.
     */
    private boolean hasEmptyCatchBlock(MethodInfo method) {
        String body = method.getBody();

        // Simple pattern matching for empty catch blocks
        Pattern pattern = Pattern.compile("catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}");
        return pattern.matcher(body).find();
    }
}
```

### Example 35.2: CodeSmell DTO

```java
package com.codetalkerl.firestick.dto;

/**
 * Represents a detected code smell.
 */
public class CodeSmell {
    private String type;
    private String severity; // INFO, WARNING, ERROR, CRITICAL
    private String className;
    private String methodName;
    private String description;
    private String recommendation;
    private int lineNumber;

    public static CodeSmellBuilder builder() {
        return new CodeSmellBuilder();
    }

    // Getters and setters
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    // Builder
    public static class CodeSmellBuilder {
        private CodeSmell smell = new CodeSmell();

        public CodeSmellBuilder type(String type) {
            smell.type = type;
            return this;
        }

        public CodeSmellBuilder severity(String severity) {
            smell.severity = severity;
            return this;
        }

        public CodeSmellBuilder className(String className) {
            smell.className = className;
            return this;
        }

        public CodeSmellBuilder methodName(String methodName) {
            smell.methodName = methodName;
            return this;
        }

        public CodeSmellBuilder description(String description) {
            smell.description = description;
            return this;
        }

        public CodeSmellBuilder recommendation(String recommendation) {
            smell.recommendation = recommendation;
            return this;
        }

        public CodeSmellBuilder lineNumber(int lineNumber) {
            smell.lineNumber = lineNumber;
            return this;
        }

        public CodeSmell build() {
            return smell;
        }
    }
}
```

---

## Days 36-37: Dead Code Detection

### Example 36.1: Complete Dead Code Detector

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalkerl.firestick.dto.DeadCodeReport;
import com.codetalkerl.firestick.model.Symbol;
import com.codetalkerl.firestick.repository.SymbolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Detects dead code (unused methods, classes, imports).
 */
@Service
public class DeadCodeDetector {
    private static final Logger logger = LoggerFactory.getLogger(DeadCodeDetector.class);

    private final DependencyGraphService graphService;
    private final SymbolRepository symbolRepository;

    // Framework annotations that indicate entry points
    private static final Set<String> ENTRY_POINT_ANNOTATIONS = Set.of(
            "@Test", "@BeforeEach", "@AfterEach", "@BeforeAll", "@AfterAll",
            "@PostConstruct", "@PreDestroy",
            "@RequestMapping", "@GetMapping", "@PostMapping", "@PutMapping", "@DeleteMapping",
            "@EventListener", "@Scheduled", "@Bean"
    );

    // Framework annotations that indicate used classes
    private static final Set<String> FRAMEWORK_ANNOTATIONS = Set.of(
            "@Service", "@Repository", "@Controller", "@RestController",
            "@Component", "@Configuration", "@Entity", "@SpringBootApplication"
    );

    public DeadCodeDetector(DependencyGraphService graphService,
                            SymbolRepository symbolRepository) {
        this.graphService = graphService;
        this.symbolRepository = symbolRepository;
    }

    /**
     * Find all dead code in the project.
     */
    public DeadCodeReport findDeadCode() {
        logger.info("Searching for dead code...");
        long startTime = System.currentTimeMillis();

        DeadCodeReport report = new DeadCodeReport();

        // Find unused methods
        List<Symbol> unusedMethods = findUnusedMethods();
        report.setUnusedMethods(unusedMethods);

        // Find unused classes
        List<Symbol> unusedClasses = findUnusedClasses();
        report.setUnusedClasses(unusedClasses);

        // Calculate statistics
        int totalDeadLines = calculateTotalLines(unusedMethods) +
                calculateTotalLines(unusedClasses);
        report.setTotalDeadLines(totalDeadLines);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Dead code analysis completed in {}ms: {} unused methods, {} unused classes, {} total lines",
                duration, unusedMethods.size(), unusedClasses.size(), totalDeadLines);

        return report;
    }

    /**
     * Find unused methods using dependency graph.
     */
    private List<Symbol> findUnusedMethods() {
        logger.debug("Finding unused methods...");

        List<Symbol> allMethods = symbolRepository.findByType("METHOD");
        List<Symbol> unusedMethods = new ArrayList<>();

        for (Symbol method : allMethods) {
            // Skip entry points
            if (isEntryPoint(method)) {
                continue;
            }

            // Skip overridden methods
            if (isOverriddenMethod(method)) {
                continue;
            }

            // Skip public methods (might be API)
            if (isPublic(method) && !isPrivate(method)) {
                continue;
            }

            // Check if method has any callers
            List<String> callers = graphService.getMethodCallers(
                    method.getFullyQualifiedName());

            if ((callers == null || callers.isEmpty()) && isPrivate(method)) {
                unusedMethods.add(method);
                logger.trace("Found unused private method: {}",
                        method.getFullyQualifiedName());
            }
        }

        logger.debug("Found {} unused methods", unusedMethods.size());
        return unusedMethods;
    }

    /**
     * Find unused classes.
     */
    private List<Symbol> findUnusedClasses() {
        logger.debug("Finding unused classes...");

        List<Symbol> allClasses = symbolRepository.findByType("CLASS");
        List<Symbol> unusedClasses = new ArrayList<>();

        for (Symbol clazz : allClasses) {
            // Skip classes with framework annotations
            if (hasFrameworkAnnotation(clazz)) {
                continue;
            }

            // Skip classes with main method
            if (hasMainMethod(clazz)) {
                continue;
            }

            // Skip test classes
            if (isTestClass(clazz)) {
                continue;
            }

            // Check if class is referenced
            List<String> dependencies = graphService.getClassDependents(
                    clazz.getFullyQualifiedName());

            if (dependencies == null || dependencies.isEmpty()) {
                unusedClasses.add(clazz);
                logger.trace("Found potentially unused class: {}",
                        clazz.getFullyQualifiedName());
            }
        }

        logger.debug("Found {} potentially unused classes", unusedClasses.size());
        return unusedClasses;
    }

    /**
     * Check if symbol is an entry point.
     */
    private boolean isEntryPoint(Symbol symbol) {
        // Check for main method
        if (symbol.getName().equals("main") &&
                symbol.getSignature() != null &&
                symbol.getSignature().contains("String[]")) {
            return true;
        }

        // Check for entry point annotations
        if (symbol.getModifiers() == null) {
            return false;
        }

        return symbol.getModifiers().stream()
                .anyMatch(ENTRY_POINT_ANNOTATIONS::contains);
    }

    /**
     * Check if method overrides a parent method.
     */
    private boolean isOverriddenMethod(Symbol method) {
        if (method.getModifiers() == null) {
            return false;
        }
        return method.getModifiers().contains("@Override");
    }

    /**
     * Check if symbol is public.
     */
    private boolean isPublic(Symbol symbol) {
        if (symbol.getModifiers() == null) {
            return false;
        }
        return symbol.getModifiers().contains("public");
    }

    /**
     * Check if symbol is private.
     */
    private boolean isPrivate(Symbol symbol) {
        if (symbol.getModifiers() == null) {
            return false;
        }
        return symbol.getModifiers().contains("private");
    }

    /**
     * Check if class has framework annotations.
     */
    private boolean hasFrameworkAnnotation(Symbol clazz) {
        if (clazz.getModifiers() == null) {
            return false;
        }

        return clazz.getModifiers().stream()
                .anyMatch(FRAMEWORK_ANNOTATIONS::contains);
    }

    /**
     * Check if class has a main method.
     */
    private boolean hasMainMethod(Symbol clazz) {
        // Query for main method in this class
        List<Symbol> methods = symbolRepository.findByName("main");

        return methods.stream()
                .anyMatch(m -> m.getFullyQualifiedName().startsWith(
                        clazz.getFullyQualifiedName()));
    }

    /**
     * Check if class is a test class.
     */
    private boolean isTestClass(Symbol clazz) {
        String name = clazz.getName();
        String path = clazz.getFilePath();

        return name.endsWith("Test") ||
                name.endsWith("Tests") ||
                (path != null && (path.contains("/test/") || path.contains("\\test\\")));
    }

    /**
     * Calculate total lines for symbols.
     */
    private int calculateTotalLines(List<Symbol> symbols) {
        return symbols.stream()
                .mapToInt(s -> s.getEndLine() - s.getStartLine() + 1)
                .sum();
    }
}
```

### Example 36.2: Unreachable Code Detector

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.CodeIssue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects unreachable code (code after return, throw, impossible conditions).
 */
@Component
public class UnreachableCodeDetector {

    /**
     * Find unreachable code in compilation unit.
     */
    public List<CodeIssue> findUnreachableCode(CompilationUnit cu) {
        List<CodeIssue> issues = new ArrayList<>();
        
        UnreachableCodeVisitor visitor = new UnreachableCodeVisitor();
        cu.accept(visitor, issues);
        
        return issues;
    }

    /**
     * Visitor to detect unreachable code.
     */
    private static class UnreachableCodeVisitor extends VoidVisitorAdapter<List<CodeIssue>> {
        
        @Override
        public void visit(MethodDeclaration method, List<CodeIssue> issues) {
            super.visit(method, issues);
            
            if (method.getBody().isPresent()) {
                BlockStmt body = method.getBody().get();
                checkForUnreachableStatements(body, issues, method.getNameAsString());
            }
        }

        /**
         * Check for unreachable statements in a block.
         */
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
                    
                    // Don't report multiple unreachable statements
                    break;
                }
                
                // Check if this statement terminates control flow
                if (stmt.isReturnStmt() || stmt.isThrowStmt()) {
                    foundTerminator = true;
                } else if (stmt.isIfStmt()) {
                    IfStmt ifStmt = stmt.asIfStmt();
                    checkForImpossibleConditions(ifStmt, issues, methodName);
                    checkNestedBlocks(ifStmt, issues, methodName);
                } else if (stmt.isBlockStmt()) {
                    checkForUnreachableStatements(stmt.asBlockStmt(), issues, methodName);
                } else if (stmt.isTryStmt()) {
                    TryStmt tryStmt = stmt.asTryStmt();
                    checkForEmptyCatchBlocks(tryStmt, issues, methodName);
                }
            }
        }

        /**
         * Check for impossible conditions (if (false)).
         */
        private void checkForImpossibleConditions(IfStmt ifStmt, 
                                                  List<CodeIssue> issues,
                                                  String methodName) {
            Expression condition = ifStmt.getCondition();
            
            // Check for if (false)
            if (condition instanceof BooleanLiteralExpr) {
                BooleanLiteralExpr boolExpr = (BooleanLiteralExpr) condition;
                if (!boolExpr.getValue()) {
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

        /**
         * Check nested blocks in if/else statements.
         */
        private void checkNestedBlocks(IfStmt ifStmt, List<CodeIssue> issues, String methodName) {
            Statement thenStmt = ifStmt.getThenStmt();
            if (thenStmt.isBlockStmt()) {
                checkForUnreachableStatements(thenStmt.asBlockStmt(), issues, methodName);
            }
            
            if (ifStmt.getElseStmt().isPresent()) {
                Statement elseStmt = ifStmt.getElseStmt().get();
                if (elseStmt.isBlockStmt()) {
                    checkForUnreachableStatements(elseStmt.asBlockStmt(), issues, methodName);
                }
            }
        }

        /**
         * Check for empty catch blocks.
         */
        private void checkForEmptyCatchBlocks(TryStmt tryStmt, 
                                             List<CodeIssue> issues,
                                             String methodName) {
            for (CatchClause catchClause : tryStmt.getCatchClauses()) {
                BlockStmt catchBody = catchClause.getBody();
                
                if (catchBody.getStatements().isEmpty()) {
                    issues.add(CodeIssue.builder()
                        .type("EMPTY_CATCH_BLOCK")
                        .severity("ERROR")
                        .methodName(methodName)
                        .lineNumber(catchClause.getBegin().get().line)
                        .description("Empty catch block swallows exceptions")
                        .recommendation("Add logging or proper exception handling")
                        .build());
                }
            }
        }
    }
}
```

### Example 36.3: DeadCodeReport DTO

```java
package com.codetalkerl.firestick.dto;

import com.codetalkerl.firestick.model.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Report of dead code findings.
 */
public class DeadCodeReport {
    private List<Symbol> unusedMethods = new ArrayList<>();
    private List<Symbol> unusedClasses = new ArrayList<>();
    private List<CodeIssue> unreachableCode = new ArrayList<>();
    private int totalDeadLines;

    // Getters and setters
    
    public List<Symbol> getUnusedMethods() {
        return unusedMethods;
    }

    public void setUnusedMethods(List<Symbol> unusedMethods) {
        this.unusedMethods = unusedMethods;
    }

    public List<Symbol> getUnusedClasses() {
        return unusedClasses;
    }

    public void setUnusedClasses(List<Symbol> unusedClasses) {
        this.unusedClasses = unusedClasses;
    }

    public List<CodeIssue> getUnreachableCode() {
        return unreachableCode;
    }

    public void setUnreachableCode(List<CodeIssue> unreachableCode) {
        this.unreachableCode = unreachableCode;
    }

    public int getTotalDeadLines() {
        return totalDeadLines;
    }

    public void setTotalDeadLines(int totalDeadLines) {
        this.totalDeadLines = totalDeadLines;
    }

    /**
     * Get total number of issues.
     */
    public int getTotalIssues() {
        return unusedMethods.size() + 
               unusedClasses.size() + 
               unreachableCode.size();
    }

    /**
     * Estimate cleanup effort (hours).
     */
    public double getEstimatedCleanupHours() {
        // Rough estimate: 5 minutes per unused method, 30 minutes per unused class
        double hours = (unusedMethods.size() * 5.0 + 
                       unusedClasses.size() * 30.0) / 60.0;
        return Math.round(hours * 10.0) / 10.0; // Round to 1 decimal
    }
}
```

---

## Day 38: Pattern Detection and Anti-Patterns

### Example 38.1: Complete Pattern Detector

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Detects design patterns and anti-patterns in code.
 */
@Service
public class PatternDetector {
    private static final Logger logger = LoggerFactory.getLogger(PatternDetector.class);

    private final DependencyGraphService graphService;

    public PatternDetector(DependencyGraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * Detect all patterns in a file.
     */
    public List<DetectedPattern> detectPatterns(FileInfo fileInfo) {
        logger.info("Detecting patterns in: {}", fileInfo.getFilePath());

        List<DetectedPattern> patterns = new ArrayList<>();

        for (ClassInfo classInfo : fileInfo.getClasses()) {
            // Check for design patterns
            if (isSingleton(classInfo)) {
                patterns.add(DetectedPattern.builder()
                        .patternType("SINGLETON")
                        .className(classInfo.getName())
                        .confidence(0.9)
                        .description("Class follows Singleton pattern")
                        .lineNumber(classInfo.getStartLine())
                        .severity("INFO")
                        .build());
            }

            if (isBuilder(classInfo)) {
                patterns.add(DetectedPattern.builder()
                        .patternType("BUILDER")
                        .className(classInfo.getName())
                        .confidence(0.85)
                        .description("Class implements Builder pattern")
                        .lineNumber(classInfo.getStartLine())
                        .severity("INFO")
                        .build());
            }

            if (isObserver(classInfo)) {
                patterns.add(DetectedPattern.builder()
                        .patternType("OBSERVER")
                        .className(classInfo.getName())
                        .confidence(0.8)
                        .description("Class implements Observer pattern")
                        .lineNumber(classInfo.getStartLine())
                        .severity("INFO")
                        .build());
            }

            // Check for Factory methods
            patterns.addAll(detectFactoryMethods(classInfo));

            // Check for anti-patterns
            if (isGodObject(classInfo)) {
                patterns.add(DetectedPattern.builder()
                        .patternType("GOD_OBJECT")
                        .className(classInfo.getName())
                        .confidence(0.95)
                        .description(String.format(
                                "Class has too many responsibilities (%d methods, %d fields)",
                                classInfo.getMethods().size(),
                                classInfo.getFields().size()))
                        .severity("WARNING")
                        .recommendation("Consider splitting into smaller, focused classes following Single Responsibility Principle")
                        .lineNumber(classInfo.getStartLine())
                        .build());
            }

            if (hasFeatureEnvy(classInfo)) {
                patterns.add(DetectedPattern.builder()
                        .patternType("FEATURE_ENVY")
                        .className(classInfo.getName())
                        .confidence(0.7)
                        .description("Class methods use other classes more than their own")
                        .severity("WARNING")
                        .recommendation("Move methods to the classes they primarily interact with")
                        .lineNumber(classInfo.getStartLine())
                        .build());
            }
        }

        // Check for circular dependencies (project-wide anti-pattern)
        patterns.addAll(detectCircularDependencies());

        logger.info("Detected {} patterns ({} anti-patterns)",
                patterns.size(),
                patterns.stream().filter(p -> p.getSeverity().equals("WARNING") ||
                        p.getSeverity().equals("ERROR")).count());

        return patterns;
    }

    /**
     * Check if class follows Singleton pattern.
     */
    private boolean isSingleton(ClassInfo classInfo) {
        // Singleton characteristics:
        // 1. Private constructor
        boolean hasPrivateConstructor = classInfo.getMethods().stream()
                .anyMatch(m -> m.getName().equals(classInfo.getName()) &&
                        m.getModifiers().contains("private"));

        // 2. Static instance field
        boolean hasStaticInstance = classInfo.getFields().stream()
                .anyMatch(f -> f.isStatic() &&
                        f.getType().equals(classInfo.getName()));

        // 3. Static getInstance() or similar method
        boolean hasGetInstance = classInfo.getMethods().stream()
                .anyMatch(m -> (m.getName().toLowerCase().contains("instance") ||
                        m.getName().toLowerCase().contains("singleton")) &&
                        m.isStatic() &&
                        m.getReturnType().equals(classInfo.getName()));

        return hasPrivateConstructor && hasStaticInstance && hasGetInstance;
    }

    /**
     * Check if class follows Builder pattern.
     */
    private boolean isBuilder(ClassInfo classInfo) {
        // Builder characteristics:
        // 1. Multiple fluent methods (methods returning 'this')
        long fluentMethods = classInfo.getMethods().stream()
                .filter(m -> m.getReturnType().equals(classInfo.getName()))
                .filter(m -> !m.isStatic())
                .filter(m -> !m.getName().equals("clone"))
                .count();

        // 2. Has a build() method
        boolean hasBuildMethod = classInfo.getMethods().stream()
                .anyMatch(m -> m.getName().equals("build") ||
                        m.getName().equals("create"));

        // Typically builders have at least 3 fluent methods
        return fluentMethods >= 3 && hasBuildMethod;
    }

    /**
     * Check if class follows Observer pattern.
     */
    private boolean isObserver(ClassInfo classInfo) {
        // Observer characteristics:
        // 1. Has add/remove listener methods
        boolean hasAddListener = classInfo.getMethods().stream()
                .anyMatch(m -> m.getName().toLowerCase().contains("addlistener") ||
                        m.getName().toLowerCase().contains("addobserver") ||
                        m.getName().toLowerCase().contains("subscribe"));

        boolean hasRemoveListener = classInfo.getMethods().stream()
                .anyMatch(m -> m.getName().toLowerCase().contains("removelistener") ||
                        m.getName().toLowerCase().contains("removeobserver") ||
                        m.getName().toLowerCase().contains("unsubscribe"));

        // 2. Has notify method
        boolean hasNotify = classInfo.getMethods().stream()
                .anyMatch(m -> m.getName().toLowerCase().contains("notify") ||
                        m.getName().toLowerCase().contains("fire") ||
                        m.getName().toLowerCase().contains("publish"));

        return hasAddListener && hasRemoveListener && hasNotify;
    }

    /**
     * Detect Factory methods.
     */
    private List<DetectedPattern> detectFactoryMethods(ClassInfo classInfo) {
        List<DetectedPattern> factories = new ArrayList<>();

        for (MethodInfo method : classInfo.getMethods()) {
            // Factory method characteristics:
            // - Static method
            // - Returns a type (often interface or base class)
            // - Name suggests creation (create, newInstance, build, etc.)
            if (method.isStatic() &&
                    !method.getReturnType().equals("void") &&
                    (method.getName().toLowerCase().contains("create") ||
                            method.getName().toLowerCase().contains("factory") ||
                            method.getName().toLowerCase().startsWith("new") ||
                            method.getName().toLowerCase().contains("instance") ||
                            method.getName().toLowerCase().equals("of") ||
                            method.getName().toLowerCase().equals("from"))) {

                factories.add(DetectedPattern.builder()
                        .patternType("FACTORY_METHOD")
                        .className(classInfo.getName())
                        .methodName(method.getName())
                        .confidence(0.75)
                        .description(String.format(
                                "Static method '%s' appears to be a factory method returning %s",
                                method.getName(),
                                method.getReturnType()))
                        .severity("INFO")
                        .lineNumber(method.getStartLine())
                        .build());
            }
        }

        return factories;
    }

    /**
     * Check if class is a God Object (anti-pattern).
     */
    private boolean isGodObject(ClassInfo classInfo) {
        // God Object indicators:
        // - Many methods (>30)
        // - Many fields (>15)
        // - Many dependencies (>10)
        // - Many lines (>1000)

        int methodCount = classInfo.getMethods().size();
        int fieldCount = classInfo.getFields().size();
        int lineCount = classInfo.getEndLine() - classInfo.getStartLine();

        // Check dependencies
        List<String> dependencies = graphService.getDependencies(
                classInfo.getFullyQualifiedName());
        int dependencyCount = dependencies != null ? dependencies.size() : 0;

        // Multiple indicators suggest God Object
        int indicators = 0;
        if (methodCount > 30) indicators++;
        if (fieldCount > 15) indicators++;
        if (lineCount > 1000) indicators++;
        if (dependencyCount > 10) indicators++;

        // Need at least 2 indicators to flag as God Object
        return indicators >= 2;
    }

    /**
     * Check if class exhibits Feature Envy (anti-pattern).
     */
    private boolean hasFeatureEnvy(ClassInfo classInfo) {
        // Feature Envy: methods use other classes more than their own

        int ownFieldAccess = 0;
        int externalAccess = 0;

        for (MethodInfo method : classInfo.getMethods()) {
            // Analyze method body for field/method calls
            String body = method.getBody();

            // Count access to own fields
            for (FieldInfo field : classInfo.getFields()) {
                if (body.contains(field.getName())) {
                    ownFieldAccess++;
                }
            }

            // Count external dependencies (simplified heuristic)
            List<String> dependencies = graphService.getMethodDependencies(
                    classInfo.getFullyQualifiedName() + "." + method.getName());

            if (dependencies != null) {
                for (String dep : dependencies) {
                    if (!dep.startsWith(classInfo.getFullyQualifiedName())) {
                        externalAccess++;
                    }
                }
            }
        }

        // If external access is significantly higher than own field access
        return externalAccess > 0 && externalAccess > (ownFieldAccess * 2);
    }

    /**
     * Detect circular dependencies (project-wide anti-pattern).
     */
    private List<DetectedPattern> detectCircularDependencies() {
        List<DetectedPattern> patterns = new ArrayList<>();

        List<List<String>> cycles = graphService.findCircularDependencies();

        for (List<String> cycle : cycles) {
            if (cycle.size() >= 2) {
                patterns.add(DetectedPattern.builder()
                        .patternType("CIRCULAR_DEPENDENCY")
                        .description(String.format(
                                "Circular dependency detected: %s",
                                String.join(" → ", cycle)))
                        .severity("ERROR")
                        .recommendation("Refactor to break circular dependency using dependency inversion or extracting shared interfaces")
                        .involvedClasses(cycle)
                        .confidence(1.0)
                        .build());
            }
        }

        return patterns;
    }

    /**
     * Generate a summary report of all patterns.
     */
    public PatternReport generatePatternReport(List<DetectedPattern> patterns) {
        logger.info("Generating pattern report...");

        PatternReport report = new PatternReport();
        report.setTotalPatterns(patterns.size());

        // Group by pattern type
        Map<String, Long> byType = patterns.stream()
                .collect(Collectors.groupingBy(
                        DetectedPattern::getPatternType,
                        Collectors.counting()));
        report.setPatternsByType(byType);

        // Separate design patterns from anti-patterns
        List<DetectedPattern> designPatterns = patterns.stream()
                .filter(p -> p.getSeverity().equals("INFO"))
                .collect(Collectors.toList());
        report.setDesignPatterns(designPatterns);

        List<DetectedPattern> antiPatterns = patterns.stream()
                .filter(p -> p.getSeverity().equals("WARNING") ||
                        p.getSeverity().equals("ERROR"))
                .collect(Collectors.toList());
        report.setAntiPatterns(antiPatterns);

        // Calculate statistics
        report.setDesignPatternCount(designPatterns.size());
        report.setAntiPatternCount(antiPatterns.size());

        // Get most common patterns
        List<Map.Entry<String, Long>> sortedPatterns = byType.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Long> topPatterns = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : sortedPatterns) {
            topPatterns.put(entry.getKey(), entry.getValue());
        }
        report.setTopPatterns(topPatterns);

        logger.info("Pattern report: {} design patterns, {} anti-patterns",
                designPatterns.size(), antiPatterns.size());

        return report;
    }
}
```

### Example 38.2: DetectedPattern DTO

```java
package com.codetalkerl.firestick.dto;

import java.util.List;

/**
 * Represents a detected design pattern or anti-pattern.
 */
public class DetectedPattern {
    private String patternType;
    private String className;
    private String methodName;
    private double confidence; // 0.0 to 1.0
    private String description;
    private String severity; // INFO, WARNING, ERROR
    private String recommendation;
    private int lineNumber;
    private List<String> involvedClasses; // For patterns spanning multiple classes

    public static DetectedPatternBuilder builder() {
        return new DetectedPatternBuilder();
    }

    // Getters and setters
    
    public String getPatternType() {
        return patternType;
    }

    public void setPatternType(String patternType) {
        this.patternType = patternType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<String> getInvolvedClasses() {
        return involvedClasses;
    }

    public void setInvolvedClasses(List<String> involvedClasses) {
        this.involvedClasses = involvedClasses;
    }

    /**
     * Check if this is an anti-pattern.
     */
    public boolean isAntiPattern() {
        return "WARNING".equals(severity) || "ERROR".equals(severity);
    }

    /**
     * Get confidence percentage.
     */
    public int getConfidencePercent() {
        return (int) (confidence * 100);
    }

    // Builder
    public static class DetectedPatternBuilder {
        private DetectedPattern pattern = new DetectedPattern();

        public DetectedPatternBuilder patternType(String patternType) {
            pattern.patternType = patternType;
            return this;
        }

        public DetectedPatternBuilder className(String className) {
            pattern.className = className;
            return this;
        }

        public DetectedPatternBuilder methodName(String methodName) {
            pattern.methodName = methodName;
            return this;
        }

        public DetectedPatternBuilder confidence(double confidence) {
            pattern.confidence = confidence;
            return this;
        }

        public DetectedPatternBuilder description(String description) {
            pattern.description = description;
            return this;
        }

        public DetectedPatternBuilder severity(String severity) {
            pattern.severity = severity;
            return this;
        }

        public DetectedPatternBuilder recommendation(String recommendation) {
            pattern.recommendation = recommendation;
            return this;
        }

        public DetectedPatternBuilder lineNumber(int lineNumber) {
            pattern.lineNumber = lineNumber;
            return this;
        }

        public DetectedPatternBuilder involvedClasses(List<String> involvedClasses) {
            pattern.involvedClasses = involvedClasses;
            return this;
        }

        public DetectedPattern build() {
            return pattern;
        }
    }
}
```

### Example 38.3: PatternReport DTO

```java
package com.codetalkerl.firestick.dto;

import java.util.List;
import java.util.Map;

/**
 * Summary report of detected patterns.
 */
public class PatternReport {
    private int totalPatterns;
    private int designPatternCount;
    private int antiPatternCount;
    private Map<String, Long> patternsByType;
    private Map<String, Long> topPatterns;
    private List<DetectedPattern> designPatterns;
    private List<DetectedPattern> antiPatterns;

    // Getters and setters
    
    public int getTotalPatterns() {
        return totalPatterns;
    }

    public void setTotalPatterns(int totalPatterns) {
        this.totalPatterns = totalPatterns;
    }

    public int getDesignPatternCount() {
        return designPatternCount;
    }

    public void setDesignPatternCount(int designPatternCount) {
        this.designPatternCount = designPatternCount;
    }

    public int getAntiPatternCount() {
        return antiPatternCount;
    }

    public void setAntiPatternCount(int antiPatternCount) {
        this.antiPatternCount = antiPatternCount;
    }

    public Map<String, Long> getPatternsByType() {
        return patternsByType;
    }

    public void setPatternsByType(Map<String, Long> patternsByType) {
        this.patternsByType = patternsByType;
    }

    public Map<String, Long> getTopPatterns() {
        return topPatterns;
    }

    public void setTopPatterns(Map<String, Long> topPatterns) {
        this.topPatterns = topPatterns;
    }

    public List<DetectedPattern> getDesignPatterns() {
        return designPatterns;
    }

    public void setDesignPatterns(List<DetectedPattern> designPatterns) {
        this.designPatterns = designPatterns;
    }

    public List<DetectedPattern> getAntiPatterns() {
        return antiPatterns;
    }

    public void setAntiPatterns(List<DetectedPattern> antiPatterns) {
        this.antiPatterns = antiPatterns;
    }

    /**
     * Get ratio of design patterns to anti-patterns.
     */
    public double getHealthScore() {
        if (totalPatterns == 0) return 1.0;
        return (double) designPatternCount / totalPatterns;
    }

    /**
     * Get health rating.
     */
    public String getHealthRating() {
        double score = getHealthScore();
        if (score >= 0.8) return "EXCELLENT";
        if (score >= 0.6) return "GOOD";
        if (score >= 0.4) return "FAIR";
        return "NEEDS_IMPROVEMENT";
    }
}
```

### Example 38.4: Pattern Detection Tests

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for PatternDetector service.
 */
@ExtendWith(MockitoExtension.class)
class PatternDetectorTest {

    @Mock
    private DependencyGraphService graphService;

    @InjectMocks
    private PatternDetector patternDetector;

    private FileInfo fileInfo;
    private ClassInfo singletonClass;
    private ClassInfo builderClass;
    private ClassInfo godObjectClass;

    @BeforeEach
    void setUp() {
        fileInfo = new FileInfo();
        fileInfo.setFilePath("/test/TestFile.java");

        // Create Singleton class
        singletonClass = createSingletonClass();

        // Create Builder class
        builderClass = createBuilderClass();

        // Create God Object class
        godObjectClass = createGodObjectClass();
    }

    @Test
    void testDetectSingletonPattern() {
        fileInfo.setClasses(List.of(singletonClass));

        List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

        assertTrue(patterns.stream()
                .anyMatch(p -> p.getPatternType().equals("SINGLETON")));

        DetectedPattern singleton = patterns.stream()
                .filter(p -> p.getPatternType().equals("SINGLETON"))
                .findFirst()
                .orElseThrow();

        assertEquals("DatabaseConnection", singleton.getClassName());
        assertTrue(singleton.getConfidence() >= 0.8);
        assertEquals("INFO", singleton.getSeverity());
    }

    @Test
    void testDetectBuilderPattern() {
        fileInfo.setClasses(List.of(builderClass));

        List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

        assertTrue(patterns.stream()
                .anyMatch(p -> p.getPatternType().equals("BUILDER")));

        DetectedPattern builder = patterns.stream()
                .filter(p -> p.getPatternType().equals("BUILDER"))
                .findFirst()
                .orElseThrow();

        assertEquals("PersonBuilder", builder.getClassName());
        assertFalse(builder.isAntiPattern());
    }

    @Test
    void testDetectGodObject() {
        when(graphService.getDependencies(anyString()))
                .thenReturn(createManyDependencies(15));

        fileInfo.setClasses(List.of(godObjectClass));

        List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

        assertTrue(patterns.stream()
                .anyMatch(p -> p.getPatternType().equals("GOD_OBJECT")));

        DetectedPattern godObject = patterns.stream()
                .filter(p -> p.getPatternType().equals("GOD_OBJECT"))
                .findFirst()
                .orElseThrow();

        assertEquals("WARNING", godObject.getSeverity());
        assertTrue(godObject.isAntiPattern());
        assertNotNull(godObject.getRecommendation());
    }

    @Test
    void testDetectFactoryMethod() {
        ClassInfo factoryClass = new ClassInfo();
        factoryClass.setName("PersonFactory");
        factoryClass.setFullyQualifiedName("com.example.PersonFactory");
        factoryClass.setStartLine(1);
        factoryClass.setEndLine(20);

        MethodInfo createMethod = new MethodInfo();
        createMethod.setName("createPerson");
        createMethod.setReturnType("Person");
        createMethod.setStatic(true);
        createMethod.setStartLine(5);

        factoryClass.setMethods(List.of(createMethod));
        fileInfo.setClasses(List.of(factoryClass));

        List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

        assertTrue(patterns.stream()
                .anyMatch(p -> p.getPatternType().equals("FACTORY_METHOD")));
    }

    @Test
    void testDetectCircularDependencies() {
        List<String> cycle = Arrays.asList("ClassA", "ClassB", "ClassC", "ClassA");
        when(graphService.findCircularDependencies())
                .thenReturn(List.of(cycle));

        fileInfo.setClasses(List.of());

        List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

        assertTrue(patterns.stream()
                .anyMatch(p -> p.getPatternType().equals("CIRCULAR_DEPENDENCY")));

        DetectedPattern circular = patterns.stream()
                .filter(p -> p.getPatternType().equals("CIRCULAR_DEPENDENCY"))
                .findFirst()
                .orElseThrow();

        assertEquals("ERROR", circular.getSeverity());
        assertNotNull(circular.getInvolvedClasses());
        assertEquals(4, circular.getInvolvedClasses().size());
    }

    @Test
    void testGeneratePatternReport() {
        List<DetectedPattern> patterns = Arrays.asList(
                createPattern("SINGLETON", "INFO"),
                createPattern("BUILDER", "INFO"),
                createPattern("GOD_OBJECT", "WARNING"),
                createPattern("CIRCULAR_DEPENDENCY", "ERROR")
        );

        PatternReport report = patternDetector.generatePatternReport(patterns);

        assertEquals(4, report.getTotalPatterns());
        assertEquals(2, report.getDesignPatternCount());
        assertEquals(2, report.getAntiPatternCount());
        assertEquals(0.5, report.getHealthScore(), 0.01);
        assertEquals("FAIR", report.getHealthRating());
    }

    // Helper methods

    private ClassInfo createSingletonClass() {
        ClassInfo clazz = new ClassInfo();
        clazz.setName("DatabaseConnection");
        clazz.setFullyQualifiedName("com.example.DatabaseConnection");
        clazz.setStartLine(1);
        clazz.setEndLine(50);

        // Private constructor
        MethodInfo constructor = new MethodInfo();
        constructor.setName("DatabaseConnection");
        constructor.setModifiers(List.of("private"));

        // Static getInstance method
        MethodInfo getInstance = new MethodInfo();
        getInstance.setName("getInstance");
        getInstance.setStatic(true);
        getInstance.setReturnType("DatabaseConnection");
        getInstance.setModifiers(List.of("public", "static"));

        clazz.setMethods(List.of(constructor, getInstance));

        // Static instance field
        FieldInfo instanceField = new FieldInfo();
        instanceField.setName("instance");
        instanceField.setType("DatabaseConnection");
        instanceField.setStatic(true);

        clazz.setFields(List.of(instanceField));

        return clazz;
    }

    private ClassInfo createBuilderClass() {
        ClassInfo clazz = new ClassInfo();
        clazz.setName("PersonBuilder");
        clazz.setFullyQualifiedName("com.example.PersonBuilder");
        clazz.setStartLine(1);
        clazz.setEndLine(80);

        // Fluent methods
        MethodInfo withName = createFluentMethod("withName", "PersonBuilder");
        MethodInfo withAge = createFluentMethod("withAge", "PersonBuilder");
        MethodInfo withEmail = createFluentMethod("withEmail", "PersonBuilder");

        // Build method
        MethodInfo build = new MethodInfo();
        build.setName("build");
        build.setReturnType("Person");

        clazz.setMethods(List.of(withName, withAge, withEmail, build));
        clazz.setFields(List.of());

        return clazz;
    }

    private ClassInfo createGodObjectClass() {
        ClassInfo clazz = new ClassInfo();
        clazz.setName("ApplicationManager");
        clazz.setFullyQualifiedName("com.example.ApplicationManager");
        clazz.setStartLine(1);
        clazz.setEndLine(1500); // Very long class

        // Create many methods (>30)
        List<MethodInfo> methods = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            MethodInfo method = new MethodInfo();
            method.setName("method" + i);
            methods.add(method);
        }
        clazz.setMethods(methods);

        // Create many fields (>15)
        List<FieldInfo> fields = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            FieldInfo field = new FieldInfo();
            field.setName("field" + i);
            fields.add(field);
        }
        clazz.setFields(fields);

        return clazz;
    }

    private MethodInfo createFluentMethod(String name, String returnType) {
        MethodInfo method = new MethodInfo();
        method.setName(name);
        method.setReturnType(returnType);
        method.setStatic(false);
        return method;
    }

    private List<String> createManyDependencies(int count) {
        List<String> deps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            deps.add("com.example.Dependency" + i);
        }
        return deps;
    }

    private DetectedPattern createPattern(String type, String severity) {
        return DetectedPattern.builder()
                .patternType(type)
                .severity(severity)
                .confidence(0.9)
                .build();
    }
}
```

### Best Practices for Pattern Detection

**1. Balance Heuristics with Accuracy**
```java
// Use multiple indicators to increase confidence
private boolean isSingleton(ClassInfo classInfo) {
    int indicators = 0;
    
    if (hasPrivateConstructor(classInfo)) indicators++;
    if (hasStaticInstanceField(classInfo)) indicators++;
    if (hasGetInstanceMethod(classInfo)) indicators++;
    
    // Require at least 2 of 3 indicators
    return indicators >= 2;
}
```

**2. Provide Confidence Scores**
```java
// Lower confidence for heuristic-based detection
DetectedPattern.builder()
    .patternType("FACTORY_METHOD")
    .confidence(0.75) // Not 100% certain
    .description("Method appears to follow factory pattern")
    .build();
```

**3. Give Actionable Recommendations**
```java
// Bad: Vague recommendation
"Class has problems"

// Good: Specific, actionable advice
"Consider splitting this class into 3 smaller classes:\n" +
"1. UserService (authentication logic)\n" +
"2. UserRepository (data access)\n" +
"3. UserValidator (validation rules)"
```

**4. Avoid False Positives**
```java
// Don't flag small classes as God Objects
if (lineCount > 1000 && methodCount > 30 && fieldCount > 15) {
    // Multiple strong indicators required
    return true;
}
```

**5. Consider Context**
```java
// Spring beans with @Service aren't necessarily God Objects
if (hasAnnotation(classInfo, "@Service") && methodCount > 20) {
    // Controllers/Services might legitimately have many methods
    // Only flag if VERY excessive (>50 methods)
    return methodCount > 50;
}
```

### Junior Developer Tips

**Understanding Design Patterns:**
- **Singleton**: One instance of a class for the entire application
- **Builder**: Fluent interface for constructing complex objects
- **Factory**: Methods that create objects without exposing creation logic
- **Observer**: Objects notify other objects of state changes

**Understanding Anti-Patterns:**
- **God Object**: Class doing too many things (violates Single Responsibility)
- **Feature Envy**: Method using another class's data more than its own
- **Circular Dependencies**: Classes depending on each other in a cycle
- **Tight Coupling**: Excessive dependencies making code hard to change

**How to Use Pattern Detection:**
```java
// 1. Run pattern detection
List<DetectedPattern> patterns = patternDetector.detectPatterns(fileInfo);

// 2. Separate concerns
List<DetectedPattern> goodPatterns = patterns.stream()
    .filter(p -> !p.isAntiPattern())
    .collect(Collectors.toList());

List<DetectedPattern> antiPatterns = patterns.stream()
    .filter(DetectedPattern::isAntiPattern)
    .collect(Collectors.toList());

// 3. Prioritize fixes by severity
antiPatterns.stream()
    .filter(p -> p.getSeverity().equals("ERROR"))
    .forEach(p -> System.out.println("URGENT: " + p.getDescription()));
```

---

## Day 39: Documentation Generation

### Example 39.1: DocumentationGenerator Service

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates documentation from code structure and JavaDoc comments.
 * Outputs Markdown format for easy viewing and publishing.
 */
@Service
public class DocumentationGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DocumentationGenerator.class);

    private final DependencyGraphService graphService;
    private final JavaParser javaParser;

    public DocumentationGenerator(DependencyGraphService graphService) {
        this.graphService = graphService;
        this.javaParser = new JavaParser();
    }

    /**
     * Generate documentation for a single class.
     *
     * What this does:
     * - Extracts class-level JavaDoc if present
     * - Lists all public methods with their signatures
     * - Shows class hierarchy (extends/implements)
     * - Lists dependencies
     *
     * Junior Developer Tip: This creates a simple Markdown "card" for each class
     * that developers can read to understand what the class does.
     */
    public String generateClassDocumentation(ClassInfo classInfo) {
        logger.debug("Generating documentation for: {}", classInfo.getName());

        StringBuilder doc = new StringBuilder();

        // Header with class name
        doc.append("# ").append(classInfo.getName()).append("\n\n");

        // Package location
        doc.append("**Package:** `").append(classInfo.getPackageName()).append("`\n\n");

        // JavaDoc description if available
        if (classInfo.getJavadoc() != null && !classInfo.getJavadoc().trim().isEmpty()) {
            doc.append("## Description\n\n");
            doc.append(cleanJavadoc(classInfo.getJavadoc())).append("\n\n");
        } else {
            // Infer purpose from class name if no JavaDoc
            doc.append("## Description\n\n");
            doc.append("*No documentation available. ");
            doc.append("Consider adding JavaDoc to describe this class.*\n\n");
        }

        // Class hierarchy
        if (classInfo.getParentClass() != null) {
            doc.append("**Extends:** `").append(classInfo.getParentClass()).append("`\n\n");
        }

        if (classInfo.getInterfaces() != null && !classInfo.getInterfaces().isEmpty()) {
            doc.append("**Implements:** ");
            doc.append(String.join(", ", classInfo.getInterfaces().stream()
                    .map(i -> "`" + i + "`")
                    .collect(Collectors.toList())));
            doc.append("\n\n");
        }

        // Public methods
        List<MethodInfo> publicMethods = classInfo.getMethods().stream()
                .filter(m -> m.getModifiers().contains("public"))
                .sorted(Comparator.comparing(MethodInfo::getName))
                .collect(Collectors.toList());

        if (!publicMethods.isEmpty()) {
            doc.append("## Public Methods\n\n");

            for (MethodInfo method : publicMethods) {
                doc.append("### ").append(method.getName()).append("\n\n");
                doc.append("```java\n");
                doc.append(method.getSignature()).append("\n");
                doc.append("```\n\n");

                if (method.getJavadoc() != null && !method.getJavadoc().trim().isEmpty()) {
                    doc.append(formatMethodJavadoc(method.getJavadoc())).append("\n\n");
                } else {
                    doc.append("*No documentation available*\n\n");
                }
            }
        }

        // Dependencies
        doc.append("## Dependencies\n\n");
        List<String> dependencies = graphService.getDependencies(classInfo.getFullyQualifiedName());
        if (dependencies == null || dependencies.isEmpty()) {
            doc.append("*No external dependencies*\n\n");
        } else {
            for (String dep : dependencies) {
                doc.append("- `").append(dep).append("`\n");
            }
            doc.append("\n");
        }

        return doc.toString();
    }

    /**
     * Generate documentation for a package.
     *
     * What this creates:
     * - Overview of all classes in the package
     * - Brief description of each class
     * - File locations
     *
     * Junior Developer Tip: Think of this as a "table of contents" for a package.
     */
    public String generatePackageDocumentation(String packageName, List<ClassInfo> classes) {
        logger.debug("Generating package documentation for: {}", packageName);

        StringBuilder doc = new StringBuilder();

        doc.append("# Package: ").append(packageName).append("\n\n");

        doc.append("## Overview\n\n");
        doc.append("This package contains ").append(classes.size()).append(" class(es).\n\n");

        doc.append("## Classes\n\n");

        for (ClassInfo classInfo : classes) {
            doc.append("### ").append(classInfo.getName()).append("\n\n");

            if (classInfo.getJavadoc() != null) {
                // Get first sentence as summary
                String summary = getFirstSentence(classInfo.getJavadoc());
                doc.append(summary).append("\n\n");
            } else {
                doc.append("*No description available*\n\n");
            }

            doc.append("**Location:** `").append(classInfo.getFilePath()).append("`\n\n");
        }

        return doc.toString();
    }

    /**
     * Generate project-wide documentation.
     *
     * What this does:
     * - Creates a docs folder
     * - Generates one markdown file per class
     * - Generates one markdown file per package
     * - Creates an index/README with overview
     *
     * Junior Developer Tip: Run this to create browsable documentation
     * for your entire codebase. Great for onboarding new team members!
     */
    public void generateProjectDocumentation(String projectPath, String outputPath) {
        logger.info("Generating project documentation...");
        logger.info("Project: {}", projectPath);
        logger.info("Output: {}", outputPath);

        try {
            // Create output directory
            Path docPath = Paths.get(outputPath);
            Files.createDirectories(docPath);

            // Group classes by package
            Map<String, List<ClassInfo>> classesByPackage = groupClassesByPackage(projectPath);

            logger.info("Found {} packages to document", classesByPackage.size());

            // Generate docs for each package
            for (Map.Entry<String, List<ClassInfo>> entry : classesByPackage.entrySet()) {
                String packageName = entry.getKey();
                List<ClassInfo> classes = entry.getValue();

                logger.debug("Documenting package: {} ({} classes)", packageName, classes.size());

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

            logger.info("Documentation generated successfully in: {}", outputPath);
            logger.info("Open {}/README.md to start browsing", outputPath);

        } catch (IOException e) {
            logger.error("Failed to generate documentation", e);
            throw new RuntimeException("Documentation generation failed", e);
        }
    }

    /**
     * Extract JavaDoc from source code using JavaParser.
     *
     * Why this is useful:
     * - Gets @param, @return, @throws tags
     * - Identifies missing documentation
     * - Can suggest what docs to add
     */
    public JavadocInfo extractJavadoc(String filePath, String className) {
        logger.debug("Extracting JavaDoc from: {}", filePath);

        try {
            CompilationUnit cu = javaParser.parse(Paths.get(filePath)).getResult()
                    .orElseThrow(() -> new RuntimeException("Failed to parse file"));

            JavadocInfo info = new JavadocInfo();

            // Find the class
            Optional<ClassOrInterfaceDeclaration> classDecl = cu.findFirst(
                    ClassOrInterfaceDeclaration.class,
                    c -> c.getNameAsString().equals(className)
            );

            if (classDecl.isPresent()) {
                ClassOrInterfaceDeclaration clazz = classDecl.get();

                // Extract class JavaDoc
                clazz.getJavadoc().ifPresent(javadoc -> {
                    info.setClassDescription(javadoc.getDescription().toText());
                });

                // Extract method JavaDocs
                for (MethodDeclaration method : clazz.getMethods()) {
                    MethodJavadocInfo methodInfo = new MethodJavadocInfo();
                    methodInfo.setMethodName(method.getNameAsString());

                    method.getJavadoc().ifPresentOrElse(
                            javadoc -> {
                                methodInfo.setDescription(javadoc.getDescription().toText());
                                methodInfo.setParamTags(extractParamTags(javadoc));
                                methodInfo.setReturnTag(extractReturnTag(javadoc));
                                methodInfo.setThrowsTags(extractThrowsTags(javadoc));
                                methodInfo.setHasDocumentation(true);
                            },
                            () -> {
                                methodInfo.setHasDocumentation(false);
                                methodInfo.setDescription("Missing documentation");
                            }
                    );

                    info.addMethodInfo(methodInfo);
                }
            }

            return info;

        } catch (IOException e) {
            logger.error("Failed to extract JavaDoc from: {}", filePath, e);
            return new JavadocInfo();
        }
    }

    // Helper methods

    private String cleanJavadoc(String javadoc) {
        // Remove JavaDoc formatting like /* * */
        return javadoc.replaceAll("/\\*\\*", "")
                .replaceAll("\\*/", "")
                .replaceAll("^\\s*\\*\\s?", "")
                .trim();
    }

    private String formatMethodJavadoc(String javadoc) {
        String cleaned = cleanJavadoc(javadoc);

        // Simple formatting: look for @param, @return, @throws
        StringBuilder formatted = new StringBuilder();
        String[] lines = cleaned.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("@param")) {
                formatted.append("- **Parameter**: ").append(line.substring(6).trim()).append("\n");
            } else if (line.startsWith("@return")) {
                formatted.append("- **Returns**: ").append(line.substring(7).trim()).append("\n");
            } else if (line.startsWith("@throws")) {
                formatted.append("- **Throws**: ").append(line.substring(7).trim()).append("\n");
            } else if (!line.isEmpty()) {
                formatted.append(line).append("\n");
            }
        }

        return formatted.toString().trim();
    }

    private String getFirstSentence(String text) {
        int periodIndex = text.indexOf('.');
        if (periodIndex > 0) {
            return text.substring(0, periodIndex + 1).trim();
        }
        return text.trim();
    }

    private Map<String, List<ClassInfo>> groupClassesByPackage(String projectPath) {
        // This would normally query your database or parse files
        // For this example, returning placeholder
        return new HashMap<>();
    }

    private String generateIndex(Map<String, List<ClassInfo>> classesByPackage) {
        StringBuilder index = new StringBuilder();

        index.append("# Project Documentation\n\n");
        index.append("## Packages\n\n");

        for (String packageName : classesByPackage.keySet()) {
            String fileName = packageName.replace(".", "_") + ".md";
            index.append("- [").append(packageName).append("](").append(fileName).append(")\n");
        }

        return index.toString();
    }

    private Map<String, String> extractParamTags(Javadoc javadoc) {
        Map<String, String> params = new HashMap<>();
        for (JavadocBlockTag tag : javadoc.getBlockTags()) {
            if (tag.getType() == JavadocBlockTag.Type.PARAM) {
                params.put(tag.getName().orElse(""), tag.getContent().toText());
            }
        }
        return params;
    }

    private String extractReturnTag(Javadoc javadoc) {
        return javadoc.getBlockTags().stream()
                .filter(tag -> tag.getType() == JavadocBlockTag.Type.RETURN)
                .map(tag -> tag.getContent().toText())
                .findFirst()
                .orElse(null);
    }

    private List<String> extractThrowsTags(Javadoc javadoc) {
        return javadoc.getBlockTags().stream()
                .filter(tag -> tag.getType() == JavadocBlockTag.Type.THROWS)
                .map(tag -> tag.getName().orElse("") + ": " + tag.getContent().toText())
                .collect(Collectors.toList());
    }
}
```

### Example 39.2: JavaDoc DTOs

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds extracted JavaDoc information for a class.
 */
public class JavadocInfo {
    private String classDescription;
    private List<MethodJavadocInfo> methods = new ArrayList<>();

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public List<MethodJavadocInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodJavadocInfo> methods) {
        this.methods = methods;
    }

    public void addMethodInfo(MethodJavadocInfo methodInfo) {
        this.methods.add(methodInfo);
    }

    /**
     * Count methods missing documentation.
     */
    public long getMissingDocumentationCount() {
        return methods.stream()
            .filter(m -> !m.isHasDocumentation())
            .count();
    }

    /**
     * Get documentation coverage percentage.
     */
    public double getDocumentationCoverage() {
        if (methods.isEmpty()) return 0.0;
        long documented = methods.stream()
            .filter(MethodJavadocInfo::isHasDocumentation)
            .count();
        return (double) documented / methods.size() * 100.0;
    }
}

/**
 * Holds JavaDoc info for a single method.
 */
class MethodJavadocInfo {
    private String methodName;
    private String description;
    private Map<String, String> paramTags;
    private String returnTag;
    private List<String> throwsTags;
    private boolean hasDocumentation;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getParamTags() {
        return paramTags;
    }

    public void setParamTags(Map<String, String> paramTags) {
        this.paramTags = paramTags;
    }

    public String getReturnTag() {
        return returnTag;
    }

    public void setReturnTag(String returnTag) {
        this.returnTag = returnTag;
    }

    public List<String> getThrowsTags() {
        return throwsTags;
    }

    public void setThrowsTags(List<String> throwsTags) {
        this.throwsTags = throwsTags;
    }

    public boolean isHasDocumentation() {
        return hasDocumentation;
    }

    public void setHasDocumentation(boolean hasDocumentation) {
        this.hasDocumentation = hasDocumentation;
    }
}
```

### Best Practices for Documentation Generation

**1. Keep Documentation Close to Code**
```java
// Good: JavaDoc right above the method
/**
 * Calculates user's age from birth date.
 * 
 * @param birthDate the user's birth date
 * @return age in years
 */
public int calculateAge(LocalDate birthDate) {
    return Period.between(birthDate, LocalDate.now()).getYears();
}
```

**2. Write for Your Audience**
```java
// For junior developers: Explain WHY, not just WHAT
/**
 * Validates email format.
 * 
 * Why we need this: Users often type invalid emails by mistake.
 * This catches common errors like missing @ or .com
 * 
 * @param email the email to check
 * @return true if valid format
 */
public boolean isValidEmail(String email) {
    // ...
}
```

**3. Document Public APIs Thoroughly**
```java
// Public methods need full docs - others might use them
/**
 * Searches code by semantic meaning.
 * 
 * How it works:
 * 1. Converts query to vector embedding
 * 2. Searches vector database
 * 3. Returns most similar code snippets
 * 
 * @param query what to search for (natural language)
 * @param limit max results to return
 * @return list of matching code snippets
 * @throws IllegalArgumentException if query is empty
 */
public List<CodeSnippet> semanticSearch(String query, int limit) {
    // ...
}
```

**4. Generate Examples from Tests**
```java
// Tests can provide usage examples for docs
/**
 * Example usage:
 * ```java
 * DocumentationGenerator gen = new DocumentationGenerator(graphService);
 * String markdown = gen.generateClassDocumentation(classInfo);
 * Files.writeString(Path.of("docs/MyClass.md"), markdown);
 * ```
 */
```

### Junior Developer Tips

**Understanding Documentation Generation:**

**What is auto-generated documentation?**
- Tool reads your code and creates human-readable docs
- Extracts JavaDoc comments you've already written
- Creates formatted Markdown files
- Generates navigation (index, table of contents)

**When to use it:**
- Onboarding new team members (they can read docs to understand codebase)
- Creating API documentation for library users
- Reviewing code coverage (what's missing docs?)
- Publishing docs to wiki or GitHub Pages

**Common workflow:**
```java
// 1. Write JavaDoc as you code
/**
 * Sends email notification.
 * @param recipient email address
 * @param subject email subject
 */
public void sendEmail(String recipient, String subject) { }

// 2. Run documentation generator
DocumentationGenerator gen = new DocumentationGenerator(graphService);
gen.generateProjectDocumentation("src/main/java", "docs");

// 3. Open docs/README.md in browser
// 4. Review and improve your JavaDoc where needed
```

**What makes good JavaDoc:**
- First sentence is a clear summary (appears in package docs)
- Explains WHY something exists, not just what it does
- Documents all parameters and return values
- Mentions exceptions that might be thrown
- Includes usage examples for complex methods

**Bad JavaDoc example:**
```java
/**
 * Gets the name.
 * @return the name
 */
public String getName() { return name; }
```

**Good JavaDoc example:**
```java
/**
 * Gets the user's display name for UI rendering.
 * If firstName and lastName are both present, returns "FirstName LastName".
 * Otherwise returns the username.
 * 
 * @return formatted display name, never null
 */
public String getDisplayName() {
    if (firstName != null && lastName != null) {
        return firstName + " " + lastName;
    }
    return username;
}
```

---

# Phase 5: Web UI (Weeks 9-10, Days 43-54)

This phase builds the React-based web interface that makes Firestick accessible and user-friendly.

---

## Day 43: React Project Setup

### Example 43.1: API Service Configuration

```javascript
// src/services/api.js
import axios from 'axios';

// Base configuration for all API calls
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - runs before every request
apiClient.interceptors.request.use(
  (config) => {
    // Helpful for debugging: log what API calls are being made
    console.log(`API Request: ${config.method.toUpperCase()} ${config.url}`);
    
    // Could add auth tokens here if needed:
    // const token = localStorage.getItem('auth_token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - runs after every response
apiClient.interceptors.response.use(
  (response) => {
    // Return just the data portion (cleaner to work with)
    return response.data;
  },
  (error) => {
    // Centralized error handling
    console.error('API Error:', error.response || error);
    
    // You could show toast notifications here
    // toast.error(`API Error: ${error.message}`);
    
    return Promise.reject(error);
  }
);

export default apiClient;
```

**Why this pattern helps:**
- Centralized configuration means changing the base URL in one place
- Interceptors avoid repeating code in every API call
- Error handling is consistent across the app
- Easy to add authentication later

### Example 43.2: Search Service Module

```javascript
// src/services/searchService.js
import apiClient from './api';

/**
 * Service for all search-related API calls.
 * Junior Developer Tip: Keeping API calls in service files
 * keeps your components clean and makes testing easier.
 */
export const searchService = {
  /**
   * Perform hybrid search (semantic + keyword).
   * 
   * @param {string} query - what to search for
   * @param {number} topK - how many results to return (default 10)
   * @param {object} filter - optional filters (file path, package, etc.)
   * @returns {Promise<Array>} search results
   */
  search: async (query, topK = 10, filter = null) => {
    const response = await apiClient.post('/search', {
      query,
      topK,
      filter,
      includeContext: true, // Get caller/callee info
    });
    return response;
  },

  /**
   * Find symbols by name (classes, methods, fields).
   * Good for "Go to Definition" features.
   */
  findSymbol: async (name, limit = 10) => {
    const response = await apiClient.get(`/search/symbol/${name}`, {
      params: { limit },
    });
    return response;
  },

  /**
   * Get autocomplete suggestions as user types.
   * Used in search box for better UX.
   */
  getSuggestions: async (prefix) => {
    const response = await apiClient.get('/search/suggestions', {
      params: { prefix, limit: 10 },
    });
    return response;
  },

  /**
   * Find code similar to a given snippet.
   * Useful for "find similar code" features.
   */
  findSimilar: async (code) => {
    const response = await apiClient.post('/search/similar', { code });
    return response;
  },
};
```

**Junior Developer Tip:** Notice how each function has a clear purpose and good documentation. When you use `searchService.search()` in a component, you don't need to know about axios or API details - just call the function.

### Example 43.3: Custom Hook for Debouncing

```javascript
// src/hooks/useDebounce.js
import { useState, useEffect } from 'react';

/**
 * Delays updating a value until user stops typing.
 * 
 * Why this is useful:
 * - User types "react" - without debouncing, you'd make 5 API calls (r, re, rea, reac, react)
 * - With debouncing, you wait until they stop typing, then make 1 API call
 * 
 * @param {any} value - the value to debounce
 * @param {number} delay - milliseconds to wait (typically 300-500ms)
 * @returns {any} debounced value
 */
export const useDebounce = (value, delay) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    // Set a timer to update the debounced value after delay
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    // Cleanup: if value changes again before timer fires, cancel the timer
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};
```

**How to use it:**
```javascript
function SearchBox() {
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, 300); // Wait 300ms after typing stops
  
  useEffect(() => {
    if (debouncedQuery) {
      // Only makes API call 300ms after user stops typing
      fetchSuggestions(debouncedQuery);
    }
  }, [debouncedQuery]);
  
  return <input value={query} onChange={e => setQuery(e.target.value)} />;
}
```

---

## Day 44: Search Interface - Part 1

### Example 44.1: SearchInput Component with Autocomplete

```javascript
// src/components/SearchInput.jsx
import React, { useState, useEffect } from 'react';
import { TextField, Autocomplete, CircularProgress } from '@mui/material';
import { searchService } from '../services/searchService';
import { useDebounce } from '../hooks/useDebounce';

/**
 * Search input with autocomplete suggestions.
 * 
 * Features:
 * - Shows suggestions as you type
 * - Debounces to avoid too many API calls
 * - Keyboard navigation (arrow keys, Enter)
 * - Loading indicator
 */
export const SearchInput = ({ onSearch, initialValue = '' }) => {
  const [query, setQuery] = useState(initialValue);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // Only fetch suggestions 300ms after user stops typing
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    // Only fetch suggestions if query is at least 3 characters
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
    // Submit search on Enter key (but not Shift+Enter for multiline)
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSearch();
    }
  };

  return (
    <Autocomplete
      freeSolo // Allow typing custom values, not just selecting from list
      options={suggestions}
      loading={loading}
      value={query}
      onInputChange={(event, newValue) => setQuery(newValue)}
      onChange={(event, newValue) => {
        // When user selects a suggestion from dropdown
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
          placeholder="Try: 'parse JSON' or 'UserService.login'"
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

**Junior Developer Tips:**
- `freeSolo` allows users to type anything, not just select from suggestions
- The loading spinner appears in the input while fetching suggestions
- `handleKeyPress` lets users hit Enter to search quickly
- Placeholder text gives examples of good search queries

---

## Day 45: Search Results Display

### Example 45.1: ResultCard Component

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
import { ContentCopy, OpenInNew } from '@mui/icons-material';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

/**
 * Displays a single search result with code preview.
 * 
 * What it shows:
 * - Method/class name
 * - File path and line numbers
 * - Match score (how relevant)
 * - Syntax-highlighted code preview
 * - Context info (what calls this, what it calls)
 */
export const ResultCard = ({ result, onViewFile }) => {
  const copyToClipboard = () => {
    navigator.clipboard.writeText(result.content);
    // Could show a toast: "Copied to clipboard!"
  };

  const handleViewFile = () => {
    onViewFile(result.filePath, result.startLine);
  };

  // Determine chip color based on score
  const getScoreColor = (score) => {
    if (score >= 0.8) return 'success';
    if (score >= 0.5) return 'warning';
    return 'default';
  };

  return (
    <Card sx={{ mb: 2, '&:hover': { boxShadow: 3 } }}>
      <CardContent>
        {/* Header with name and score */}
        <Box display="flex" justifyContent="space-between" alignItems="start" mb={1}>
          <Box>
            <Typography variant="h6" component="div">
              {result.className && `${result.className}.`}{result.methodName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {result.filePath} : lines {result.startLine}-{result.endLine}
            </Typography>
          </Box>
          <Box>
            <Chip 
              label={`Match: ${(result.score * 100).toFixed(0)}%`} 
              size="small" 
              color={getScoreColor(result.score)}
            />
            {result.searchType && (
              <Chip 
                label={result.searchType} 
                size="small" 
                sx={{ ml: 1 }}
                variant="outlined"
              />
            )}
          </Box>
        </Box>

        {/* Code Preview with Syntax Highlighting */}
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
              overflow: 'auto',
            }}
          >
            {result.content}
          </SyntaxHighlighter>
          
          {/* Copy button overlaid on code */}
          <IconButton
            size="small"
            onClick={copyToClipboard}
            sx={{ 
              position: 'absolute', 
              top: 8, 
              right: 8,
              bgcolor: 'background.paper',
              '&:hover': { bgcolor: 'action.hover' }
            }}
            title="Copy code"
          >
            <ContentCopy fontSize="small" />
          </IconButton>
        </Box>

        {/* Context: What calls this and what it calls */}
        {(result.callers?.length > 0 || result.callees?.length > 0) && (
          <Box mb={1} p={1} bgcolor="action.hover" borderRadius={1}>
            {result.callers?.length > 0 && (
              <Typography variant="body2" color="text.secondary">
                <strong>Called by:</strong> {result.callers.slice(0, 3).join(', ')}
                {result.callers.length > 3 && ` and ${result.callers.length - 3} more`}
              </Typography>
            )}
            {result.callees?.length > 0 && (
              <Typography variant="body2" color="text.secondary">
                <strong>Calls:</strong> {result.callees.slice(0, 3).join(', ')}
                {result.callees.length > 3 && ` and ${result.callees.length - 3} more`}
              </Typography>
            )}
          </Box>
        )}

        {/* Action buttons */}
        <Box display="flex" gap={1}>
          <IconButton 
            size="small" 
            onClick={handleViewFile} 
            title="Open full file"
          >
            <OpenInNew fontSize="small" />
          </IconButton>
        </Box>
      </CardContent>
    </Card>
  );
};
```

**What makes this component good:**
- Clear visual hierarchy (name → location → code → context)
- Syntax highlighting makes code easy to read
- Copy button is convenient for developers
- Score color coding gives quick relevance feedback
- Context info helps understand how code is used

---

## Day 46: Code Viewer with Monaco Editor

### Example 46.1: CodeViewer Component

```javascript
// src/components/CodeViewer.jsx
import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { Box, CircularProgress, Alert, Toolbar, IconButton } from '@mui/material';
import { ContentCopy, Download } from '@mui/icons-material';

/**
 * Full-featured code viewer using Monaco Editor (VS Code's editor).
 * 
 * Features:
 * - Syntax highlighting
 * - Line numbers
 * - Minimap (overview of file)
 * - Jump to specific line
 * - Read-only mode
 * - Copy and download buttons
 */
export const CodeViewer = ({ 
  filePath, 
  highlightLine = null,
  readOnly = true 
}) => {
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editorInstance, setEditorInstance] = useState(null);

  useEffect(() => {
    loadFile();
  }, [filePath]);

  const loadFile = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch(
        `http://localhost:8080/api/files/content?path=${encodeURIComponent(filePath)}`
      );
      
      if (!response.ok) {
        throw new Error(`Failed to load file: ${response.statusText}`);
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
    setEditorInstance(editor);
    
    if (highlightLine) {
      // Scroll to line and put cursor there
      editor.revealLineInCenter(highlightLine);
      
      // Highlight the line
      editor.deltaDecorations([], [
        {
          range: {
            startLineNumber: highlightLine,
            startColumn: 1,
            endLineNumber: highlightLine,
            endColumn: 999, // Highlight whole line
          },
          options: {
            isWholeLine: true,
            className: 'highlighted-line', // CSS class for styling
            glyphMarginClassName: 'highlighted-line-glyph',
          },
        },
      ]);
    }
  };

  const handleCopyAll = () => {
    navigator.clipboard.writeText(code);
    // Show toast: "Copied to clipboard!"
  };

  const handleDownload = () => {
    const blob = new Blob([code], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filePath.split('/').pop() || 'code.java';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
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
      {/* Toolbar with actions */}
      <Toolbar variant="dense" sx={{ bgcolor: 'background.paper', borderBottom: 1, borderColor: 'divider' }}>
        <Typography variant="subtitle2" sx={{ flexGrow: 1 }}>
          {filePath}
        </Typography>
        <IconButton size="small" onClick={handleCopyAll} title="Copy all code">
          <ContentCopy fontSize="small" />
        </IconButton>
        <IconButton size="small" onClick={handleDownload} title="Download file">
          <Download fontSize="small" />
        </IconButton>
      </Toolbar>

      {/* Monaco Editor */}
      <Editor
        height="600px"
        language="java"
        theme="vs-dark"
        value={code}
        onMount={handleEditorMount}
        options={{
          readOnly,
          minimap: { enabled: true }, // Shows overview of whole file on right
          scrollBeyondLastLine: false,
          fontSize: 14,
          lineNumbers: 'on',
          renderLineHighlight: 'all',
          automaticLayout: true, // Resize editor when container resizes
          scrollbar: {
            vertical: 'visible',
            horizontal: 'visible',
          },
        }}
      />
    </Box>
  );
};
```

**Why Monaco Editor:**
- Same editor as VS Code - developers are familiar with it
- Excellent syntax highlighting
- Built-in features like find/replace, minimap
- Good performance even with large files
- Easy to integrate into React

**Adding CSS for highlighted line:**
```css
/* src/index.css or similar */
.highlighted-line {
  background-color: rgba(255, 255, 0, 0.2) !important;
}

.highlighted-line-glyph {
  background-color: yellow;
}
```

---

## Day 47: Analysis Dashboard

### Example 47.1: ComplexityWidget Component

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

/**
 * Widget showing cyclomatic complexity analysis.
 * 
 * Displays:
 * - Average, max, total methods
 * - Bar chart of complexity distribution
 * - Table of most complex methods
 */
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

  if (loading) return <Box p={3}><CircularProgress /></Box>;
  if (!data) return <Alert severity="info">No complexity data available</Alert>;

  // Prepare data for bar chart
  const chartData = [
    { 
      level: 'Low\n(1-5)', 
      count: data.histogram.LOW || 0, 
      fill: '#4caf50' // Green for good
    },
    { 
      level: 'Medium\n(6-10)', 
      count: data.histogram.MEDIUM || 0, 
      fill: '#ff9800' // Orange for moderate
    },
    { 
      level: 'High\n(11-20)', 
      count: data.histogram.HIGH || 0, 
      fill: '#f44336' // Red for concerning
    },
    { 
      level: 'Very High\n(21+)', 
      count: data.histogram.VERY_HIGH || 0, 
      fill: '#9c27b0' // Purple for needs immediate attention
    },
  ];

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Cyclomatic Complexity Distribution
        </Typography>
        
        {/* Key Statistics */}
        <Box display="flex" gap={4} mb={3}>
          <Box>
            <Typography variant="caption" color="text.secondary">
              Average Complexity
            </Typography>
            <Typography variant="h4" color={data.average > 10 ? 'error' : 'primary'}>
              {data.average.toFixed(1)}
            </Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">
              Highest Complexity
            </Typography>
            <Typography variant="h4" color={data.max > 20 ? 'error' : 'warning.main'}>
              {data.max}
            </Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary">
              Total Methods Analyzed
            </Typography>
            <Typography variant="h4">
              {data.totalMethods}
            </Typography>
          </Box>
        </Box>

        {/* Bar Chart */}
        <ResponsiveContainer width="100%" height={250}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="level" style={{ fontSize: '12px' }} />
            <YAxis label={{ value: 'Number of Methods', angle: -90, position: 'insideLeft' }} />
            <Tooltip />
            <Legend />
            <Bar dataKey="count" name="Methods" />
          </BarChart>
        </ResponsiveContainer>

        {/* Top 5 Most Complex Methods */}
        {data.topComplex && data.topComplex.length > 0 && (
          <Box mt={3}>
            <Typography variant="subtitle2" gutterBottom>
              Methods Needing Refactoring (Complexity &gt; 10)
            </Typography>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Method Name</TableCell>
                  <TableCell align="center">Complexity</TableCell>
                  <TableCell>File Location</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.topComplex.slice(0, 5).map((method, idx) => (
                  <TableRow key={idx} hover>
                    <TableCell>
                      <Typography variant="body2" fontFamily="monospace">
                        {method.name}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <Chip
                        label={method.complexity}
                        size="small"
                        color={
                          method.complexity > 20 ? 'error' : 
                          method.complexity > 10 ? 'warning' : 'success'
                        }
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="caption" color="text.secondary">
                        {method.file}
                      </Typography>
                    </TableCell>
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

**What makes a good dashboard widget:**
- **Quick stats at top** - Users see key numbers immediately
- **Visual chart** - Bar chart shows distribution at a glance
- **Actionable table** - Shows what needs attention
- **Color coding** - Red/yellow/green helps prioritize
- **Loading states** - Shows spinner while fetching data
- **Error handling** - Shows helpful message if data fails to load

---

### Best Practices for React Development

**1. Keep Components Focused**
```javascript
// Bad: Component does too much
function SearchPage() {
  // Has search logic, filter logic, pagination, results display
  // 500+ lines of code
}

// Good: Break into smaller components
function SearchPage() {
  return (
    <div>
      <SearchInput onSearch={handleSearch} />
      <SearchFilters filters={filters} onChange={setFilters} />
      <SearchResults results={results} onPageChange={handlePageChange} />
    </div>
  );
}
```

**2. Use Services for API Calls**
```javascript
// Bad: API calls directly in components
function MyComponent() {
  const fetchData = async () => {
    const res = await axios.get('http://localhost:8080/api/data');
    setData(res.data);
  };
}

// Good: Use service layer
function MyComponent() {
  const fetchData = async () => {
    const data = await dataService.getData();
    setData(data);
  };
}
```

**3. Handle Loading and Error States**
```javascript
function DataDisplay() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Always show appropriate UI for each state
  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;
  if (!data) return <Alert severity="info">No data available</Alert>;
  
  return <div>{/* Display data */}</div>;
}
```

**4. Use Custom Hooks for Reusable Logic**
```javascript
// Create custom hook for common pattern
function useApiData(fetchFunction) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    setLoading(true);
    fetchFunction()
      .then(setData)
      .catch(setError)
      .finally(() => setLoading(false));
  }, []);
  
  return { data, loading, error };
}

// Use it in components
function MyComponent() {
  const { data, loading, error } = useApiData(searchService.search);
  // ...
}
```

---

### Junior Developer Tips for React + Spring Boot

**Understanding the Request Flow:**
```
1. User clicks "Search" button in React
2. React calls searchService.search(query)
3. Axios sends HTTP POST to http://localhost:8080/api/search
4. Spring Boot @RestController receives request
5. Service layer processes search
6. Returns JSON response
7. Axios receives response
8. React updates state and re-renders UI
```

**Common mistakes to avoid:**
- Forgetting to handle loading/error states
- Making API calls without debouncing (too many requests)
- Not using keys in lists (React warning)
- Putting too much logic in components (use services/hooks)
- Forgetting async/await (promises not resolving)

**Debugging tips:**
- Use browser DevTools Network tab to see API calls
- Check Console for errors and logs
- Use React DevTools to inspect component state
- Add console.log strategically (but remove before committing)

---

# Phase 6: Desktop Packaging (Weeks 10-11, Days 55-62)

This phase packages Firestick as a standalone desktop application that users can install without technical knowledge.

---

## Day 55: Prepare Application for Packaging

### Example 55.1: Maven Configuration for Uber JAR

```xml
<!-- pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.codetalkerl</groupId>
    <artifactId>firestick</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Firestick</name>
    <description>Legacy Code Analysis and Search Tool</description>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <main.class>com.codetalker.firestick.FirestickApplicationcom.codetalker.firestick.FirestickApplication</main.class>
        <spring.boot.version>3.5.6</spring.boot.version>
    </properties>

    <build>
        <finalName>firestick-${project.version}</finalName>
        <plugins>
            <!-- Spring Boot Maven Plugin creates executable JAR -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.version}</version>
                <configuration>
                    <mainClass>${main.class}</mainClass>
                    <layout>JAR</layout>
                    <!-- Include all dependencies in JAR -->
                    <executable>true</executable>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                </configuration>
            </plugin>

            <!-- Copy React build to resources (optional automation) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.3.0</version>
                <executions>
                    <execution>
                        <id>copy-react-build</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.outputDirectory}/static</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>${project.basedir}/../firestick-ui/dist</directory>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**What this does:**
- `spring-boot-maven-plugin` with `repackage` goal creates a "fat JAR" containing all dependencies
- Final JAR is executable: `java -jar firestick-1.0.0.jar`
- React build is automatically copied into JAR's static resources
- Single JAR contains everything needed to run the application

**To build:**
```bash
mvn clean package
# Creates target/firestick-1.0.0.jar (typically 50-100MB)
```

### Example 55.2: Application Properties for Packaged App

```properties
# src/main/resources/application.properties

# Application Metadata
spring.application.name=Firestick
app.version=1.0.0
app.vendor=CodeTalkerl

# Server Configuration
server.port=8080
server.address=localhost

# Database - Store in user's home directory
spring.datasource.url=jdbc:h2:file:${user.home}/.firestick/data/firestick;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# Chroma Vector Database
chroma.host=localhost
chroma.port=8000
chroma.data.path=${user.home}/.firestick/chroma

# ONNX Embedding Model
embedding.model.path=${user.home}/.firestick/models/all-MiniLM-L6-v2.onnx

# Indexing Defaults
indexing.default.exclude=target,build,.git,.svn,node_modules,dist

# Logging
logging.level.root=INFO
logging.level.com.codetalkerl.firestick=DEBUG
logging.file.name=${user.home}/.firestick/logs/firestick.log
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

**Junior Developer Tips:**
- `${user.home}` is a Java system property that points to:
  - Windows: `C:\Users\YourName`
  - macOS: `/Users/YourName`
  - Linux: `/home/yourname`
- Storing data in user home means each user has their own data
- `.firestick` folder is hidden (starts with dot) - keeps things tidy
- `AUTO_SERVER=TRUE` for H2 allows multiple processes to access database safely

---

## Day 56: jpackage Build Configuration

### Example 56.1: Windows Build Script

```batch
@echo off
REM packaging/build-windows.bat
REM Builds Windows installer (.msi) for Firestick

echo ========================================
echo Building Firestick for Windows
echo ========================================

REM Step 1: Build the application JAR
echo.
echo [1/2] Building application JAR...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed
    exit /b %errorlevel%
)

REM Step 2: Create Windows installer with jpackage
echo.
echo [2/2] Creating Windows installer (.msi)...
jpackage ^
  --input target ^
  --name Firestick ^
  --main-jar firestick-1.0.0.jar ^
  --main-class com.codetalker.firestick.FirestickApplication ^
  --type msi ^
  --app-version 1.0.0 ^
  --vendor "CodeTalkerl" ^
  --description "Legacy Code Analysis and Search Tool" ^
  --copyright "Copyright 2025 CodeTalkerl" ^
  --icon packaging/icons/firestick.ico ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group "Firestick" ^
  --java-options "-Xmx2048m" ^
  --java-options "-Xms512m" ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --dest packaging/output/windows

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo Installer: packaging\output\windows\Firestick-1.0.0.msi
echo.
pause
```

**What each jpackage option does:**
- `--input target` - Where to find the JAR file
- `--name Firestick` - Application name
- `--main-jar` - Which JAR to run
- `--main-class` - Entry point class with main() method
- `--type msi` - Create Windows MSI installer (could also use `exe`)
- `--win-dir-chooser` - Let user choose installation directory
- `--win-menu` - Add to Start Menu
- `--win-shortcut` - Create desktop shortcut
- `--java-options` - JVM settings (heap size, etc.)

**To run:**
```bash
cd packaging
build-windows.bat
```

### Example 56.2: macOS Build Script

```bash
#!/bin/bash
# packaging/build-macos.sh
# Builds macOS installer (.dmg) for Firestick

set -e  # Exit immediately if a command fails

echo "========================================"
echo "Building Firestick for macOS"
echo "========================================"

# Step 1: Build the application JAR
echo ""
echo "[1/2] Building application JAR..."
mvn clean package -DskipTests

# Step 2: Create macOS installer with jpackage
echo ""
echo "[2/2] Creating macOS installer (.dmg)..."
jpackage \
  --input target \
  --name Firestick \
  --main-jar firestick-1.0.0.jar \
  --main-class com.codetalker.firestick.FirestickApplication \
  --type dmg \
  --app-version 1.0.0 \
  --vendor "CodeTalkerl" \
  --description "Legacy Code Analysis and Search Tool" \
  --copyright "Copyright 2025 CodeTalkerl" \
  --icon packaging/icons/firestick.icns \
  --mac-package-identifier com.codetalkerl.firestick \
  --mac-package-name Firestick \
  --java-options "-Xmx2048m" \
  --java-options "-Xms512m" \
  --dest packaging/output/macos

# Optional: Code signing (requires Apple Developer account)
# echo ""
# echo "[3/3] Signing application..."
# codesign --force --deep --sign "Developer ID Application: Your Name" \
#   "packaging/output/macos/Firestick-1.0.0.dmg"

echo ""
echo "========================================"
echo "Build Complete!"
echo "========================================"
echo "Installer: packaging/output/macos/Firestick-1.0.0.dmg"
echo ""
```

**To run:**
```bash
chmod +x packaging/build-macos.sh  # Make executable (first time only)
./packaging/build-macos.sh
```

**macOS-specific notes:**
- `.icns` file is macOS icon format (can create with online converter)
- `--mac-package-identifier` should be reverse domain (com.yourcompany.appname)
- Code signing is optional but recommended for distribution
- Users may see "unidentified developer" warning without signing

### Example 56.3: Linux Build Script (.deb)

```bash
#!/bin/bash
# packaging/build-linux-deb.sh
# Builds Debian/Ubuntu package (.deb) for Firestick

set -e

echo "========================================"
echo "Building Firestick for Linux (Debian/Ubuntu)"
echo "========================================"

# Step 1: Build the application JAR
echo ""
echo "[1/2] Building application JAR..."
mvn clean package -DskipTests

# Step 2: Create .deb package with jpackage
echo ""
echo "[2/2] Creating .deb package..."
jpackage \
  --input target \
  --name firestick \
  --main-jar firestick-1.0.0.jar \
  --main-class com.codetalker.firestick.FirestickApplication \
  --type deb \
  --app-version 1.0.0 \
  --vendor "CodeTalkerl" \
  --description "Legacy Code Analysis and Search Tool" \
  --copyright "Copyright 2025 CodeTalkerl" \
  --icon packaging/icons/firestick.png \
  --linux-package-name firestick \
  --linux-deb-maintainer "support@codetalkerl.com" \
  --linux-menu-group "Development" \
  --linux-shortcut \
  --java-options "-Xmx2048m" \
  --java-options "-Xms512m" \
  --dest packaging/output/linux

echo ""
echo "========================================"
echo "Build Complete!"
echo "========================================"
echo "Package: packaging/output/linux/firestick_1.0.0-1_amd64.deb"
echo ""
echo "To install:"
echo "sudo dpkg -i packaging/output/linux/firestick_1.0.0-1_amd64.deb"
```

**To install the built package:**
```bash
sudo dpkg -i firestick_1.0.0-1_amd64.deb
# Or double-click in file manager on Ubuntu
```

**To uninstall:**
```bash
sudo apt remove firestick
```

---

## Day 60: Application Launcher Improvements

### Example 60.1: Auto-Launch Browser on Startup

```java
// src/main/java/com/codetalkerl/firestick/FirestickApplication.java
package com.codetalkerl.firestick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Desktop;
import java.net.URI;

/**
 * Main application class.
 * Auto-launches browser when application starts.
 */
@SpringBootApplication
public class FirestickApplication {
    private static final Logger logger = LoggerFactory.getLogger(FirestickApplication.class);
    private static final String APP_URL = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication.run(FirestickApplication.class, args);
    }

    /**
     * Called when Spring Boot finishes startup.
     * Opens user's default browser to the application.
     * 
     * Junior Developer Tip: @EventListener listens for Spring events.
     * ApplicationReadyEvent fires when app is fully started and ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void launchBrowser() {
        // Give server a moment to be fully ready
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait 2 seconds
                
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(APP_URL));
                        logger.info("Browser launched at {}", APP_URL);
                    } else {
                        showManualInstructions();
                    }
                } else {
                    showManualInstructions();
                }
            } catch (Exception e) {
                logger.warn("Could not auto-launch browser: {}", e.getMessage());
                showManualInstructions();
            }
        }).start();
    }

    private void showManualInstructions() {
        logger.info("========================================");
        logger.info("Firestick is running!");
        logger.info("Open your browser and go to: {}", APP_URL);
        logger.info("========================================");
    }
}
```

**Why this is helpful:**
- Users don't need to know the URL or port
- Application feels more like a desktop app
- Reduces friction for non-technical users

### Example 60.2: System Tray Integration (Optional)

```java
// src/main/java/com/codetalkerl/firestick/config/SystemTrayConfig.java
package com.codetalkerl.firestick.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URI;

/**
 * Adds Firestick to system tray (Windows notification area, macOS menu bar).
 * 
 * Features:
 * - Shows tray icon
 * - Right-click menu: Open, Exit
 * - Click icon to open browser
 * - Shows notification on startup
 */
@Component
public class SystemTrayConfig {
    private static final Logger logger = LoggerFactory.getLogger(SystemTrayConfig.class);
    private static final String APP_URL = "http://localhost:8080";

    @EventListener(ApplicationReadyEvent.class)
    public void initSystemTray() {
        // Check if system tray is supported (not available on all Linux desktops)
        if (!SystemTray.isSupported()) {
            logger.info("System tray not supported on this platform");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            
            // Load icon image
            Image icon = loadTrayIcon();
            
            // Create popup menu
            PopupMenu popup = new PopupMenu();
            
            // "Open Firestick" menu item
            MenuItem openItem = new MenuItem("Open Firestick");
            openItem.addActionListener(e -> openInBrowser());
            popup.add(openItem);
            
            popup.addSeparator();
            
            // "Exit" menu item
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                logger.info("Exiting Firestick...");
                System.exit(0);
            });
            popup.add(exitItem);
            
            // Create and add tray icon
            TrayIcon trayIcon = new TrayIcon(icon, "Firestick - Code Analysis Tool", popup);
            trayIcon.setImageAutoSize(true);
            
            // Double-click icon to open browser
            trayIcon.addActionListener(e -> openInBrowser());
            
            tray.add(trayIcon);
            
            // Show notification that app started
            trayIcon.displayMessage(
                "Firestick Started",
                "Application is running. Click to open.",
                TrayIcon.MessageType.INFO
            );
            
            logger.info("System tray icon added");
            
        } catch (Exception e) {
            logger.error("Failed to initialize system tray", e);
        }
    }

    private Image loadTrayIcon() throws Exception {
        // Load from resources
        InputStream is = getClass().getResourceAsStream("/icons/tray-icon.png");
        if (is != null) {
            return ImageIO.read(is);
        }
        
        // Fallback: create simple icon
        return Toolkit.getDefaultToolkit().createImage(new byte[0]);
    }

    private void openInBrowser() {
        try {
            Desktop.getDesktop().browse(new URI(APP_URL));
        } catch (Exception e) {
            logger.error("Failed to open browser", e);
        }
    }
}
```

**Using the system tray:**
- **Windows**: Icon appears in notification area (bottom-right)
- **macOS**: Icon appears in menu bar (top-right)
- **Linux**: Depends on desktop environment (not always supported)

**Tray icon tips:**
- Icon should be small (16x16 or 22x22 pixels)
- Use simple, recognizable design
- PNG with transparency works best

---

### Best Practices for Desktop Packaging

**1. Keep Installation Simple**
```text
Good:
- Double-click installer
- Click "Next" a few times
- Application runs

Bad:
- Install Java manually
- Set JAVA_HOME
- Run from command line
- Configure properties files
```

**2. Store Data in User Directory**
```java
// Good: User-specific data location
String dataPath = System.getProperty("user.home") + "/.firestick";

// Bad: Shared location (permissions issues, conflicts)
String dataPath = "C:/ProgramData/firestick";
```

**3. Provide Reasonable Defaults**
```properties
# Good: Works out of the box
server.port=8080
database.location=${user.home}/.firestick/data

# Bad: User must configure
server.port=CONFIGURE_ME
database.location=CONFIGURE_ME
```

**4. Handle Errors Gracefully**
```java
// Good: User-friendly error message
if (portInUse(8080)) {
    showDialog("Port 8080 is already in use. Please close other applications.");
}

// Bad: Cryptic error
// BindException: Address already in use
```

**5. Test on Clean Systems**
- Test on VM without Java installed
- Test with non-admin user
- Test with antivirus enabled
- Test on different OS versions

---

### Junior Developer Tips for Desktop Packaging

**Understanding jpackage:**
- jpackage is a tool (built into JDK 14+) that creates native installers
- Takes your JAR + JRE → Creates platform-specific installer
- Output is .msi (Windows), .dmg (macOS), or .deb/.rpm (Linux)
- Bundled JRE means users don't need Java installed

**Why bundle a JRE:**
- User doesn't need to install Java
- You control which Java version is used
- Avoids "works on my machine" problems
- Smaller download than full JDK (JRE is runtime-only)

**Common issues:**

**Issue: "jpackage: command not found"**
```bash
# Solution: Use JDK 14+ and ensure it's in PATH
java -version  # Should show JDK 14+
jpackage --version  # Should work
```

**Issue: Large installer size (>200MB)**
```text
Solution: This is normal for bundled JRE
- JRE alone: ~50MB
- Your app: ~100MB
- Total: ~150-200MB
- Users only download once
```

**Issue: Application won't start after install**
```text
Solution: Check logs
- Windows: %USERPROFILE%\.firestick\logs
- macOS: ~/.firestick/logs
- Linux: ~/.firestick/logs

Common causes:
- Port 8080 already in use
- Missing write permissions
- Antivirus blocking
```

**Cross-platform file paths:**
```java
// Good: Works everywhere
Path dataDir = Paths.get(System.getProperty("user.home"), ".firestick");
Files.createDirectories(dataDir);

// Bad: Windows-only
File dataDir = new File("C:\\Users\\...\\firestick");
```

**Testing checklist:**
- ✅ Installer runs on fresh OS install
- ✅ Application launches after install
- ✅ All features work (search, analysis, UI)
- ✅ Data persists after restart
- ✅ Uninstaller removes everything cleanly
- ✅ No leftover files after uninstall
- ✅ Works with non-admin user
- ✅ Works with antivirus enabled

---

# Phase 7: Optimization & Polish (Week 12, Days 63-69)

This final phase transforms Firestick from "working" to "professional-grade" through performance optimization, UX polish, and thorough testing.

---

## Day 63: Performance Profiling & Database Optimization

### Example 63.1: Spring Boot Actuator Metrics

```xml
<!-- pom.xml - Add performance monitoring -->
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
# src/main/resources/application.properties
# Expose performance metrics endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.enable.jvm=true
management.metrics.enable.process=true
management.metrics.enable.system=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

**Access metrics:**
```bash
# Check application health
curl http://localhost:8080/actuator/health

# View all available metrics
curl http://localhost:8080/actuator/metrics

# Specific metric (e.g., JVM memory)
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

**What this gives you:**
- JVM memory usage (heap, non-heap)
- CPU usage
- Thread counts
- HTTP request metrics (count, duration, percentiles)
- Database connection pool stats
- Custom application metrics

### Example 63.2: Database Index Optimization

```java
// src/main/java/com/codetalkerl/firestick/model/CodeChunk.java
package com.codetalkerl.firestick.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Before optimization: Full table scans on every query (SLOW!)
 * After optimization: Index seeks on commonly queried columns (FAST!)
 */
@Entity
@Table(name = "code_chunks",
       indexes = {
           // Index on file path (used in "find all chunks in file X")
           @Index(name = "idx_file_path", columnList = "filePath"),
           
           // Index on chunk type (used in "find all methods" or "find all classes")
           @Index(name = "idx_chunk_type", columnList = "chunkType"),
           
           // Index on class name (used in "find chunks in class X")
           @Index(name = "idx_class_name", columnList = "className"),
           
           // Composite index for common queries (file + type)
           @Index(name = "idx_file_type", columnList = "filePath,chunkType"),
           
           // Index on created date (used for "recently indexed")
           @Index(name = "idx_created", columnList = "createdAt")
       })
public class CodeChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String filePath;
    
    @Column(nullable = false, length = 50)
    private String chunkType; // METHOD, CLASS, INTERFACE, etc.
    
    @Column(length = 255)
    private String className;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // ... getters/setters
}
```

**Why indexes help:**
```sql
-- Without index: Scans ALL rows
SELECT * FROM code_chunks WHERE file_path = '/path/to/File.java';
-- Full table scan: 100,000 rows → 500ms

-- With index: Uses B-tree index
SELECT * FROM code_chunks WHERE file_path = '/path/to/File.java';
-- Index seek: Jump directly to matching rows → 5ms
```

### Example 63.3: Batch Embedding Generation

```java
// src/main/java/com/codetalkerl/firestick/service/EmbeddingService.java
package com.codetalkerl.firestick.service;

import ai.onnxruntime.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Optimized embedding generation with batching and caching.
 * 
 * Performance improvements:
 * - Before: Process one at a time → 100ms per embedding
 * - After: Batch process + parallel → 10ms per embedding
 */
@Service
public class EmbeddingService {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
    private static final int BATCH_SIZE = 32;
    private static final int THREAD_POOL_SIZE = 4;
    
    private OrtEnvironment env;
    private OrtSession session;
    private ExecutorService executorService;

    @PostConstruct
    public void init() throws Exception {
        // Load ONNX model ONCE at startup (not every time we need embeddings)
        logger.info("Loading ONNX embedding model...");
        env = OrtEnvironment.getEnvironment();
        String modelPath = System.getProperty("user.home") + "/.firestick/models/all-MiniLM-L6-v2.onnx";
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
        
        // Thread pool for parallel processing
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        logger.info("Embedding service initialized");
    }

    /**
     * Generate embeddings for multiple texts in batches.
     * Much faster than calling generateEmbedding() multiple times.
     */
    public List<float[]> generateEmbeddingsBatch(List<String> texts) {
        if (texts.isEmpty()) {
            return Collections.emptyList();
        }
        
        logger.debug("Generating embeddings for {} texts", texts.size());
        List<float[]> allEmbeddings = new ArrayList<>();
        
        // Process in batches of 32
        for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, texts.size());
            List<String> batch = texts.subList(i, end);
            
            // Process batch in parallel
            List<Future<float[]>> futures = batch.stream()
                .map(text -> executorService.submit(() -> generateEmbedding(text)))
                .collect(Collectors.toList());
            
            // Collect results
            for (Future<float[]> future : futures) {
                try {
                    allEmbeddings.add(future.get());
                } catch (Exception e) {
                    logger.error("Error generating embedding", e);
                    allEmbeddings.add(new float[384]); // Default empty embedding
                }
            }
        }
        
        logger.debug("Generated {} embeddings", allEmbeddings.size());
        return allEmbeddings;
    }

    /**
     * Generate embedding for single text.
     * Cached to avoid recomputing for same text.
     */
    @Cacheable(value = "embeddings", key = "#text.hashCode()")
    public float[] generateEmbedding(String text) {
        try {
            // Tokenize text (simplified - real implementation uses tokenizer)
            long[] inputIds = tokenize(text);
            
            // Create input tensor
            OnnxTensor inputTensor = OnnxTensor.createTensor(
                env, 
                new long[][]{inputIds}
            );
            
            // Run inference
            Map<String, OnnxTensor> inputs = Map.of("input_ids", inputTensor);
            OrtSession.Result result = session.run(inputs);
            
            // Extract embedding
            float[][] output = (float[][]) result.get(0).getValue();
            return output[0]; // Return first (and only) embedding
            
        } catch (Exception e) {
            logger.error("Error generating embedding for text: {}", text, e);
            return new float[384]; // Default embedding
        }
    }

    private long[] tokenize(String text) {
        // Simplified tokenization (real implementation uses SentencePiece)
        // Returns token IDs for input text
        return new long[128]; // Placeholder
    }
}
```

**Performance comparison:**
```text
Generating embeddings for 1000 code chunks:

❌ Before optimization (sequential):
- 1000 chunks × 100ms = 100 seconds

✅ After optimization (batched + parallel + cached):
- 1000 chunks / 32 batch size = 32 batches
- 32 batches × 10ms = 320ms (first time)
- Subsequent runs: ~50ms (cached)

Result: 300x faster!
```

### Example 63.4: Caching Configuration

```java
// src/main/java/com/codetalkerl/firestick/config/CacheConfig.java
package com.codetalkerl.firestick.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * Caching configuration for performance optimization.
 * 
 * What gets cached:
 * - Embeddings (expensive to compute)
 * - Search results (frequently repeated queries)
 * - Code chunks (reduce database hits)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "embeddings",      // Cache embeddings for 1 hour
            "searchResults",   // Cache search results for 10 minutes
            "codeChunks"       // Cache code chunks for 30 minutes
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10_000)           // Max 10,000 entries
            .expireAfterWrite(1, TimeUnit.HOURS)  // Expire after 1 hour
            .recordStats());               // Track cache hit/miss stats
        
        return cacheManager;
    }
}
```

**Using the cache:**
```java
// Cache search results
@Cacheable(value = "searchResults", key = "#query + #limit")
public List<SearchResult> search(String query, int limit) {
    // Expensive search operation
    return performSearch(query, limit);
}

// Cache code chunks by ID
@Cacheable(value = "codeChunks", key = "#id")
public CodeChunk findById(Long id) {
    return repository.findById(id).orElse(null);
}

// Evict cache when data changes
@CacheEvict(value = "codeChunks", allEntries = true)
public void reindexProject(String path) {
    // Re-indexing invalidates cached chunks
}
```

**Junior Developer Tips on Caching:**
- Cache expensive operations (database queries, API calls, computations)
- Don't cache everything (memory is limited)
- Set appropriate expiration times (too short = useless, too long = stale data)
- Invalidate cache when underlying data changes
- Monitor cache hit rate (should be > 70% to be worthwhile)

---

## Day 64: Frontend Performance Optimization

### Example 64.1: React Code Splitting

```javascript
// src/App.jsx - Lazy load route components
import React, { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { CircularProgress, Box } from '@mui/material';

/**
 * Code splitting reduces initial bundle size.
 * 
 * Before: One big bundle (2MB) - takes 10s to load
 * After: Small initial bundle (200KB) + lazy chunks - takes 1s to load
 */

// Load immediately (small, always needed)
import Navigation from './components/Navigation';

// Load on demand (large, only needed on specific pages)
const SearchPage = lazy(() => import('./pages/SearchPage'));
const AnalysisDashboard = lazy(() => import('./pages/AnalysisDashboard'));
const CodeViewer = lazy(() => import('./pages/CodeViewer'));
const DependencyGraph = lazy(() => import('./pages/DependencyGraph'));
const SettingsPage = lazy(() => import('./pages/SettingsPage'));

// Loading spinner shown while lazy components load
function LoadingFallback() {
  return (
    <Box 
      display="flex" 
      justifyContent="center" 
      alignItems="center" 
      minHeight="400px"
    >
      <CircularProgress />
    </Box>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Navigation />
      
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

**Bundle size comparison:**
```text
Before code splitting:
  main.js: 2.1 MB (includes everything)
  Total download: 2.1 MB

After code splitting:
  main.js: 180 KB (core app)
  SearchPage.chunk.js: 120 KB (loaded on demand)
  AnalysisDashboard.chunk.js: 350 KB (loaded on demand)
  CodeViewer.chunk.js: 800 KB (loaded on demand - Monaco Editor)
  DependencyGraph.chunk.js: 600 KB (loaded on demand - D3.js)
  
  Initial download: 180 KB (90% smaller!)
  Other chunks load only when user visits those pages
```

### Example 64.2: React Performance Optimization

```javascript
// src/components/ResultCard.jsx
import React, { memo, useCallback } from 'react';
import { Card, CardContent, Typography, Button } from '@mui/material';

/**
 * Optimized result card component.
 * 
 * Performance techniques:
 * 1. React.memo - Skip re-render if props haven't changed
 * 2. useCallback - Prevent function recreation on every render
 * 3. Shallow comparison - Only re-render if result ID changed
 */
const ResultCard = memo(({ result, onViewFile, onViewAnalysis }) => {
  // useCallback prevents these functions from being recreated every render
  const handleViewFile = useCallback(() => {
    onViewFile(result.id);
  }, [result.id, onViewFile]);

  const handleViewAnalysis = useCallback(() => {
    onViewAnalysis(result.id);
  }, [result.id, onViewAnalysis]);

  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        <Typography variant="h6">{result.className}</Typography>
        <Typography variant="body2" color="text.secondary">
          {result.filePath}
        </Typography>
        <Typography variant="body1" sx={{ mt: 1 }}>
          {result.content}
        </Typography>
        
        <Button onClick={handleViewFile} sx={{ mt: 1 }}>
          View File
        </Button>
        <Button onClick={handleViewAnalysis} sx={{ mt: 1, ml: 1 }}>
          Analyze
        </Button>
      </CardContent>
    </Card>
  );
}, (prevProps, nextProps) => {
  // Custom comparison: Only re-render if result ID changed
  return prevProps.result.id === nextProps.result.id;
});

export default ResultCard;
```

```javascript
// src/components/ResultsList.jsx - Virtual scrolling for long lists
import React from 'react';
import { FixedSizeList } from 'react-window';
import ResultCard from './ResultCard';

/**
 * Virtual scrolling for large result sets.
 * 
 * Problem: Rendering 1000 ResultCards = slow, janky scrolling
 * Solution: Only render visible cards (maybe 10 at a time)
 * 
 * Before: 1000 DOM nodes, 2s to render, choppy scrolling
 * After: 10-15 DOM nodes, instant render, smooth scrolling
 */
function ResultsList({ results, onViewFile, onViewAnalysis }) {
  // Each row in the virtual list
  const Row = ({ index, style }) => (
    <div style={style}>
      <ResultCard 
        result={results[index]}
        onViewFile={onViewFile}
        onViewAnalysis={onViewAnalysis}
      />
    </div>
  );

  return (
    <FixedSizeList
      height={800}              // Visible area height
      itemCount={results.length} // Total items
      itemSize={200}            // Each item height
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
}

export default ResultsList;
```

### Example 64.3: API Request Optimization

```javascript
// src/hooks/useSearchResults.js
import { useState, useEffect, useRef } from 'react';
import { searchService } from '../services/searchService';

/**
 * Optimized search hook with cancellation and deduplication.
 * 
 * Features:
 * - Cancels previous request when new search starts
 * - Prevents duplicate requests for same query
 * - Debounces rapid searches
 */
export function useSearchResults(query, debounceMs = 300) {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const abortControllerRef = useRef(null);
  const debounceTimerRef = useRef(null);
  const lastQueryRef = useRef('');

  useEffect(() => {
    // Clear previous debounce timer
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }

    // Empty query = clear results
    if (!query) {
      setResults([]);
      setLoading(false);
      return;
    }

    // Same query = skip (already have results)
    if (query === lastQueryRef.current) {
      return;
    }

    // Debounce: Wait for user to stop typing
    debounceTimerRef.current = setTimeout(() => {
      performSearch(query);
    }, debounceMs);

    // Cleanup on unmount or query change
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
      if (debounceTimerRef.current) {
        clearTimeout(debounceTimerRef.current);
      }
    };
  }, [query, debounceMs]);

  const performSearch = async (searchQuery) => {
    // Cancel previous request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // Create new abort controller for this request
    abortControllerRef.current = new AbortController();
    
    setLoading(true);
    setError(null);
    lastQueryRef.current = searchQuery;

    try {
      const data = await searchService.search(searchQuery, {
        signal: abortControllerRef.current.signal
      });
      
      setResults(data);
    } catch (err) {
      // Ignore abort errors (expected when canceling)
      if (err.name !== 'AbortError') {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  return { results, loading, error };
}
```

**What this optimization achieves:**
```text
User types: "s" → "se" → "sea" → "sear" → "searc" → "search"

❌ Without optimization:
- 6 API requests (one per keystroke)
- Last request might finish first (race condition!)
- Wasted bandwidth and server resources

✅ With optimization:
- Debounce: Wait 300ms after last keystroke
- Only 1 API request for "search"
- Previous requests canceled
- Much faster and more efficient
```

---

## Day 65: User Experience Improvements

### Example 65.1: Error Boundary

```javascript
// src/components/ErrorBoundary.jsx
import React from 'react';
import { Alert, Button, Box, Typography } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

/**
 * Catches React errors and shows user-friendly message.
 * Prevents white screen of death!
 */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { 
      hasError: false, 
      error: null,
      errorInfo: null 
    };
  }

  static getDerivedStateFromError(error) {
    // Update state so next render shows fallback UI
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // Log error details for debugging
    console.error('Error caught by boundary:', error, errorInfo);
    
    this.setState({
      error,
      errorInfo
    });
    
    // Could send to error tracking service (Sentry, etc.)
    // trackError(error, errorInfo);
  }

  handleReset = () => {
    this.setState({ 
      hasError: false, 
      error: null,
      errorInfo: null 
    });
  };

  render() {
    if (this.state.hasError) {
      return (
        <Box 
          display="flex" 
          flexDirection="column" 
          alignItems="center" 
          justifyContent="center"
          minHeight="400px"
          p={3}
        >
          <ErrorOutlineIcon 
            sx={{ fontSize: 80, color: 'error.main', mb: 2 }} 
          />
          
          <Typography variant="h4" gutterBottom>
            Oops! Something went wrong
          </Typography>
          
          <Typography variant="body1" color="text.secondary" paragraph>
            We encountered an unexpected error. Don't worry, your data is safe.
          </Typography>

          {/* Show error details in development only */}
          {process.env.NODE_ENV === 'development' && this.state.error && (
            <Alert severity="error" sx={{ mt: 2, maxWidth: 600, textAlign: 'left' }}>
              <Typography variant="subtitle2">Error Details:</Typography>
              <pre style={{ fontSize: '12px', overflow: 'auto' }}>
                {this.state.error.toString()}
                {this.state.errorInfo?.componentStack}
              </pre>
            </Alert>
          )}

          <Box mt={3}>
            <Button 
              variant="contained" 
              onClick={this.handleReset}
              sx={{ mr: 1 }}
            >
              Try Again
            </Button>
            <Button 
              variant="outlined" 
              onClick={() => window.location.reload()}
            >
              Refresh Page
            </Button>
          </Box>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

**Usage in App:**
```javascript
// src/App.jsx
import ErrorBoundary from './components/ErrorBoundary';

function App() {
  return (
    <ErrorBoundary>
      {/* Entire app wrapped in error boundary */}
      <BrowserRouter>
        <Routes>
          {/* ... routes */}
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  );
}
```

### Example 65.2: Toast Notifications

```javascript
// src/contexts/NotificationContext.jsx
import React, { createContext, useContext, useState, useCallback } from 'react';
import { Snackbar, Alert } from '@mui/material';

const NotificationContext = createContext();

export function useNotification() {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
}

export function NotificationProvider({ children }) {
  const [notification, setNotification] = useState(null);

  const showNotification = useCallback((message, severity = 'info') => {
    setNotification({ message, severity });
  }, []);

  const hideNotification = useCallback(() => {
    setNotification(null);
  }, []);

  return (
    <NotificationContext.Provider value={{ showNotification }}>
      {children}
      
      <Snackbar
        open={!!notification}
        autoHideDuration={6000}
        onClose={hideNotification}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        {notification && (
          <Alert 
            onClose={hideNotification} 
            severity={notification.severity}
            variant="filled"
            sx={{ width: '100%' }}
          >
            {notification.message}
          </Alert>
        )}
      </Snackbar>
    </NotificationContext.Provider>
  );
}
```

**Usage in components:**
```javascript
import { useNotification } from '../contexts/NotificationContext';

function SettingsPage() {
  const { showNotification } = useNotification();

  const handleSave = async () => {
    try {
      await saveSettings();
      showNotification('Settings saved successfully!', 'success');
    } catch (error) {
      showNotification('Failed to save settings', 'error');
    }
  };

  const handleIndexingComplete = () => {
    showNotification('Indexing completed! Found 1,234 code chunks.', 'info');
  };

  return (
    <div>
      {/* Component UI */}
    </div>
  );
}
```

### Example 65.3: Skeleton Screens

```javascript
// src/components/ResultSkeleton.jsx
import React from 'react';
import { Card, CardContent, Skeleton, Box } from '@mui/material';

/**
 * Skeleton screen for loading states.
 * Shows placeholder UI while data loads - better UX than spinner!
 */
function ResultSkeleton() {
  return (
    <Card sx={{ mb: 2 }}>
      <CardContent>
        {/* Skeleton for title */}
        <Skeleton variant="text" width="60%" height={32} />
        
        {/* Skeleton for file path */}
        <Skeleton variant="text" width="80%" height={20} />
        
        {/* Skeleton for content */}
        <Box mt={1}>
          <Skeleton variant="rectangular" width="100%" height={60} />
        </Box>
        
        {/* Skeleton for buttons */}
        <Box mt={1} display="flex" gap={1}>
          <Skeleton variant="rectangular" width={100} height={36} />
          <Skeleton variant="rectangular" width={100} height={36} />
        </Box>
      </CardContent>
    </Card>
  );
}

export default ResultSkeleton;
```

**Using skeletons:**
```javascript
// src/components/ResultsList.jsx
function ResultsList({ results, loading }) {
  if (loading) {
    // Show 5 skeleton cards while loading
    return (
      <Box>
        {[...Array(5)].map((_, i) => (
          <ResultSkeleton key={i} />
        ))}
      </Box>
    );
  }

  return (
    <Box>
      {results.map(result => (
        <ResultCard key={result.id} result={result} />
      ))}
    </Box>
  );
}
```

**Why skeletons are better than spinners:**
```text
Spinner:
- Shows nothing except spinning circle
- User doesn't know what to expect
- Feels slower (no visual progress)

Skeleton:
- Shows layout of upcoming content
- User knows what's coming
- Feels faster (visual feedback)
- More professional appearance
```

---

## Day 67: Documentation Templates

### Example 67.1: API Documentation with Swagger

```java
// src/main/java/com/codetalkerl/firestick/config/OpenApiConfig.java
package com.codetalkerl.firestick.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for auto-generated API documentation.
 * Access at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI firestickOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Firestick API")
                .description("Legacy Code Analysis and Search Tool - REST API Documentation")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("CodeTalkerl Support")
                    .email("support@codetalkerl.com")
                    .url("https://github.com/codetalkerl/firestick"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server")
            ));
    }
}
```

```java
// src/main/java/com/codetalkerl/firestick/controller/SearchController.java
package com.codetalkerl.firestick.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Search API endpoints with full Swagger documentation.
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Code search and retrieval operations")
public class SearchController {

    @Operation(
        summary = "Search code by query",
        description = "Performs semantic search across indexed code using natural language or keywords"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SearchResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search query",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @PostMapping
    public SearchResponse search(
        @Parameter(
            description = "Search request containing query and filters",
            required = true
        )
        @RequestBody SearchRequest request
    ) {
        // Implementation
        return searchService.search(request);
    }

    @Operation(
        summary = "Get autocomplete suggestions",
        description = "Returns search suggestions based on partial query"
    )
    @GetMapping("/suggestions")
    public List<String> getSuggestions(
        @Parameter(description = "Partial search query", example = "parse")
        @RequestParam String query,
        
        @Parameter(description = "Maximum number of suggestions", example = "10")
        @RequestParam(defaultValue = "10") int limit
    ) {
        return searchService.getSuggestions(query, limit);
    }
}
```

**Access documentation:**
```text
Start application and visit:
http://localhost:8080/swagger-ui.html

Features:
- Interactive API explorer
- Try out API calls directly
- See request/response schemas
- Download OpenAPI spec (JSON/YAML)
```

### Example 67.2: README Template

```markdown
# Firestick - Legacy Code Analysis and Search Tool

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Version](https://img.shields.io/badge/version-1.0.0-blue)]()
[![License](https://img.shields.io/badge/license-MIT-green)]()

> Intelligent code search and analysis tool for understanding large legacy codebases

## ✨ Features

- **🔍 Semantic Search** - Find code by meaning, not just keywords
- **📊 Code Analysis** - Complexity metrics, code smells, dead code detection
- **🎯 Pattern Detection** - Identify design patterns and anti-patterns
- **📈 Dependency Graphs** - Visualize code relationships
- **🖥️ Modern UI** - Beautiful, responsive web interface
- **📦 Desktop App** - Native installers for Windows, macOS, Linux

## 🚀 Quick Start

### Installation

**Windows:**
```bash
# Download firestick-1.0.0.msi
# Double-click to install
# Launch from Start Menu
```

**macOS:**
```bash
# Download firestick-1.0.0.dmg
# Drag to Applications folder
# Launch from Applications
```

**Linux (Debian/Ubuntu):**
```bash
sudo dpkg -i firestick-1.0.0.deb
firestick
```

### First Use

1. **Launch** Firestick (opens browser automatically)
2. **Index** your project: Settings → Index Directory → Select folder
3. **Wait** for indexing to complete (progress shown)
4. **Search** for code using natural language queries

## 📖 Documentation

- [User Guide](docs/USER_GUIDE.md) - How to use Firestick
- [Developer Guide](docs/DEVELOPER_GUIDE.md) - How to contribute
- [API Documentation](http://localhost:8080/swagger-ui.html) - REST API reference
- [FAQ](docs/FAQ.md) - Common questions

## 🛠️ Technology Stack

- **Backend:** Spring Boot 3.5, Java 21, H2 Database
- **Search:** Apache Lucene, Chroma Vector DB
- **Analysis:** JavaParser, ONNX Runtime
- **Frontend:** React 18, Material-UI, Monaco Editor
- **Packaging:** jpackage, Maven

## 🤝 Contributing

Contributions welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file.

## 🙏 Acknowledgments

- [JavaParser](https://javaparser.org/) - Java code parsing
- [Chroma](https://www.trychroma.com/) - Vector database
- [ONNX Runtime](https://onnxruntime.ai/) - ML inference
- [Material-UI](https://mui.com/) - React components

## 📧 Support

- **Issues:** [GitHub Issues](https://github.com/codetalkerl/firestick/issues)
- **Email:** support@codetalkerl.com
- **Discussions:** [GitHub Discussions](https://github.com/codetalkerl/firestick/discussions)

---

Made with ❤️ by CodeTalkerl
```

---

### Best Practices for Optimization

**1. Measure Before Optimizing**
```text
❌ Bad: "I think this is slow, let me optimize it"
✅ Good: "Profiler shows this function takes 80% of time, let's optimize it"

Always:
1. Measure current performance (baseline)
2. Identify bottleneck (profiling)
3. Optimize the bottleneck
4. Measure improvement
5. Repeat if needed
```

**2. Focus on User-Perceived Performance**
```text
User doesn't care about:
- Total bundle size (if lazy loaded)
- Backend latency (if UI feels responsive)
- Memory usage (if app doesn't crash)

User cares about:
- How fast UI responds to clicks
- How quickly results appear
- Whether app feels smooth
- If loading is clear and predictable
```

**3. Optimize in Order of Impact**
```text
High Impact (do these first):
1. Database indexes on slow queries
2. Caching expensive operations
3. Code splitting for large bundles
4. Virtual scrolling for long lists

Medium Impact:
1. React.memo for expensive components
2. Image optimization
3. API request batching

Low Impact (nice to have):
1. Micro-optimizations
2. Variable renaming
3. Reducing function calls by 1-2
```

**4. Don't Over-Optimize**
```java
// Bad: Premature optimization
int sum = 0;
for (int i = 0; i < items.length; ++i) { // Using ++i instead of i++ "for performance"
    sum += items[i];
}

// Good: Clear, readable code
int sum = Arrays.stream(items).sum();

// The JVM compiler will optimize both to the same bytecode!
// Clear code > micro-optimizations
```

---

### Junior Developer Tips for Performance

**Understanding caching:**
```text
Cache = Fast temporary storage

Example: Looking up a word in dictionary
- Without cache: Search entire dictionary every time (slow)
- With cache: Remember last 10 lookups (fast if repeated)

When to cache:
✅ Expensive to compute (complex calculations)
✅ Frequently accessed (same query many times)
✅ Rarely changes (static data)

When NOT to cache:
❌ Cheap to compute (simple arithmetic)
❌ Rarely accessed (unique queries)
❌ Frequently changes (real-time data)
```

**Understanding indexes:**
```text
Index = Table of contents for database

Without index:
- Database scans every row to find matches
- Like reading entire book to find one word
- Slow for large tables

With index:
- Database jumps directly to matches
- Like using book index to find page number
- Fast regardless of table size

Rule: Add index on columns you frequently search/filter by
```

**Common performance mistakes:**
```javascript
// ❌ Mistake 1: Creating functions in render
function MyComponent({ items }) {
  return items.map(item => (
    <div onClick={() => handleClick(item.id)}>  // New function every render!
      {item.name}
    </div>
  ));
}

// ✅ Fix: Use useCallback or move outside
const handleClick = useCallback((id) => {
  // Handle click
}, []);

// ❌ Mistake 2: Not cleaning up listeners
useEffect(() => {
  window.addEventListener('resize', handleResize);
  // Missing cleanup! Memory leak!
}, []);

// ✅ Fix: Always clean up
useEffect(() => {
  window.addEventListener('resize', handleResize);
  return () => window.removeEventListener('resize', handleResize);
}, []);

// ❌ Mistake 3: Fetching in loop
for (const id of ids) {
  await fetchItem(id);  // 100 IDs = 100 requests!
}

// ✅ Fix: Batch requests
await fetchItems(ids);  // 1 request for all items
```

**Debugging slow code:**
```text
1. Use browser DevTools Performance tab
   - Record interaction
   - See timeline of what's slow
   - Click on slow items for details

2. Use React DevTools Profiler
   - Record component renders
   - See which components are slow
   - See why components re-render

3. Use Chrome Lighthouse
   - Run audit on page
   - Get performance score
   - Get specific recommendations

4. Add console.time for custom timing
   console.time('search');
   performSearch();
   console.timeEnd('search');  // "search: 234ms"
```

---

**🎉 Congratulations!** You've completed all 7 phases of Firestick development! The application is now optimized, polished, and ready for users.

