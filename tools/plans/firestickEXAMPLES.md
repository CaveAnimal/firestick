# Firestick Examples & Advice

**Version:** 1.0  
**Date:** October 14, 2025  
**Project:** Firestick - Legacy Code Analysis and Search Tool  
**Companion Document:** firestickTASKS.md

---

## How to Use This Document

- **Find the task number** from firestickTASKS.md (e.g., "Day 1: Project Initialization")
- **Locate the corresponding example** below
- **Copy and adapt the code** for your implementation
- **Ask questions** if anything is unclear
- **Test thoroughly** after implementing examples

---

## Table of Contents

- [Phase 1: Foundation (Weeks 1-2)](#phase-1-foundation-weeks-1-2)
  - [Day 1: Project Initialization](#day-1-project-initialization)
  - [Day 2: Health Check & Basic Controller](#day-2-health-check--basic-controller)
  - [Day 3: JavaParser Integration](#day-3-javaparser-integration)
  - [Day 4: Lucene Integration](#day-4-lucene-integration)
  - [Day 5: JGraphT Integration](#day-5-jgrapht-integration)
  - [Day 6: H2 Database Setup](#day-6-h2-database-setup)
  - [Day 7: Database Entities](#day-7-database-entities)
  - [Day 8: ONNX Runtime Setup](#day-8-onnx-runtime-setup)
  - [Day 9: Chroma Integration](#day-9-chroma-integration)
  - [Day 10: End-to-End Pipeline Test](#day-10-end-to-end-pipeline-test)

---
---

# Phase 1: Foundation (Weeks 1-2)

## Day 1: Project Initialization

### Example 1.1: Complete pom.xml Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Spring Boot Parent -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <!-- Project Coordinates -->
    <groupId>com.codetalkerl</groupId>
    <artifactId>firestick</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>firestick</name>
    <description>Legacy Code Analysis and Search Tool</description>
    
    <!-- Java Version -->
    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        
        <!-- Dependency Versions -->
        <javaparser.version>3.26.3</javaparser.version>
        <lucene.version>9.12.0</lucene.version>
        <jgrapht.version>1.5.2</jgrapht.version>
        <onnxruntime.version>1.16.1</onnxruntime.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- H2 Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- JavaParser for Code Parsing -->
        <dependency>
            <groupId>com.github.javaparser</groupId>
            <artifactId>javaparser-core</artifactId>
            <version>${javaparser.version}</version>
        </dependency>
        
        <dependency>
            <groupId>com.github.javaparser</groupId>
            <artifactId>javaparser-symbol-solver-core</artifactId>
            <version>${javaparser.version}</version>
        </dependency>
        
        <!-- Apache Lucene for Full-Text Search -->
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>${lucene.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-queryparser</artifactId>
            <version>${lucene.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-analysis-common</artifactId>
            <version>${lucene.version}</version>
        </dependency>
        
        <!-- JGraphT for Dependency Graphs -->
        <dependency>
            <groupId>org.jgrapht</groupId>
            <artifactId>jgrapht-core</artifactId>
            <version>${jgrapht.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.jgrapht</groupId>
            <artifactId>jgrapht-io</artifactId>
            <version>${jgrapht.version}</version>
        </dependency>
        
        <!-- ONNX Runtime for Embeddings -->
        <dependency>
            <groupId>com.microsoft.onnxruntime</groupId>
            <artifactId>onnxruntime</artifactId>
            <version>${onnxruntime.version}</version>
        </dependency>
        
        <!-- Lombok (optional, for reducing boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Example 1.2: Main Application Class

```java
package com.codetalkerl.firestick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Firestick.
 * 
 * Firestick is a legacy code analysis and search tool that uses:
 * - JavaParser for code parsing
 * - Apache Lucene for keyword search
 * - ONNX Runtime for semantic embeddings
 * - Chroma vector database for semantic search
 * - JGraphT for dependency analysis
 */
@SpringBootApplication
public class FirestickApplication {
    private static final Logger logger = LoggerFactory.getLogger(FirestickApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Firestick Application...");
        SpringApplication.run(FirestickApplication.class, args);
        logger.info("Firestick Application started successfully!");
    }
}
```

### Example 1.3: Basic application.properties

```properties
# Application Info
spring.application.name=Firestick
server.port=8080

# Logging
logging.level.root=INFO
logging.level.com.codetalkerl.firestick=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Spring Boot Banner
spring.banner.location=classpath:banner.txt
```

### Example 1.4: Custom Banner (Optional)

Create `src/main/resources/banner.txt`:

```
  _____ _              _   _      _    
 |  ___(_)_ __ ___ ___| |_(_) ___| | __
 | |_  | | '__/ _ / __| __| |/ __| |/ /
 |  _| | | | |  __\__ | |_| | (__|   < 
 |_|   |_|_|  \___|___/\__|_|\___|_|\_\
 
 :: Legacy Code Analysis Tool :: (v1.0.0)
```

### Example 1.5: Project Directory Structure

```
firestick/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── codetalkerl/
│   │   │           └── firestick/
│   │   │               ├── FirestickApplication.java
│   │   │               ├── config/          # Configuration classes
│   │   │               ├── controller/      # REST controllers
│   │   │               ├── service/         # Business logic
│   │   │               ├── entity/          # JPA entities
│   │   │               ├── repository/      # Data repositories
│   │   │               ├── exception/       # Custom exceptions
│   │   │               └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── banner.txt
│   │       └── logback.xml (to be added later)
│   └── test/
│       └── java/
│           └── com/
│               └── codetalkerl/
│                   └── firestick/
│                       ├── FirestickApplicationTests.java
│                       ├── controller/
│                       ├── service/
│                       └── integration/
├── models/                  # ONNX models (to be added)
├── data/                    # H2 database files
├── logs/                    # Application logs
├── pom.xml
└── README.md
```

### Advice for Day 1

**Common Pitfalls:**
1. **Wrong Java Version**: Ensure you have JDK 21 installed, not just JRE
2. **Maven Not Found**: Add Maven to your PATH or use Maven Wrapper (mvnw)
3. **Port Already in Use**: If port 8080 is taken, change `server.port` in application.properties

**Verification Steps:**
```powershell
# Check Java version
java -version
# Should show: openjdk version "21.x.x"

# Check Maven version
mvn -version
# Should show: Apache Maven 3.8.x or later

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Test in browser
# Navigate to: http://localhost:8080
# You should see a "Whitelabel Error Page" (this is OK - means server is running)
```

**Troubleshooting:**

1. **"mvn command not found"**
   - Install Maven: https://maven.apache.org/download.cgi
   - Or use Maven Wrapper: `./mvnw` (Linux/Mac) or `mvnw.cmd` (Windows)

2. **"Spring Boot application failed to start"**
   - Check the console for error messages
   - Ensure no other app is using port 8080
   - Verify pom.xml has no syntax errors

3. **Dependencies won't download**
   - Check internet connection
   - Try: `mvn clean install -U` (force update)
   - Clear Maven cache: Delete `~/.m2/repository` and retry

---

## Day 2: Health Check & Basic Controller

### Example 2.1: HealthController

```java
package com.codetalkerl.firestick.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    /**
     * Basic health check endpoint.
     * 
     * @return OK status with timestamp
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        logger.debug("Health check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Firestick");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0-SNAPSHOT");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Detailed status endpoint with component checks.
     * 
     * @return Detailed status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        logger.debug("Status check requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Firestick");
        response.put("status", "RUNNING");
        response.put("uptime", getUptime());
        
        // Component status (to be implemented)
        Map<String, String> components = new HashMap<>();
        components.put("database", "OK");
        components.put("parser", "OK");
        components.put("search", "OK");
        response.put("components", components);
        
        return ResponseEntity.ok(response);
    }
    
    private String getUptime() {
        // Simplified uptime - in production, track actual start time
        return "Running";
    }
}
```

### Example 2.2: HealthController Test

```java
package com.codetalkerl.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for HealthController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointReturnsOk() {
        // Given
        String url = "http://localhost:" + port + "/api/health";
        
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("OK");
        assertThat(response.getBody().get("service")).isEqualTo("Firestick");
    }
    
    @Test
    void statusEndpointReturnsDetailedInfo() {
        // Given
        String url = "http://localhost:" + port + "/api/status";
        
        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("application")).isEqualTo("Firestick");
        assertThat(response.getBody().get("status")).isEqualTo("RUNNING");
        assertThat(response.getBody()).containsKey("components");
    }
}
```

### Example 2.3: Alternative Test Using MockMvc

```java
package com.codetalkerl.firestick.controller;

import com.codetalker.firestick.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HealthController using MockMvc.
 */
@WebMvcTest(HealthController.class)
class HealthControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsOkWithJson() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.service").value("Firestick"))
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void statusEndpointReturnsComponents() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("Firestick"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.database").value("OK"));
    }
}
```

### Advice for Day 2

**Testing Best Practices:**
1. **Use Descriptive Test Names**: Name tests based on what they verify
2. **Follow AAA Pattern**: Arrange, Act, Assert
3. **Test Both Success and Failure**: Don't just test the happy path
4. **Use AssertJ**: More readable assertions than JUnit's basic assertions

**Common Issues:**

1. **"Port already in use" in tests**
   - Solution: Use `@SpringBootTest(webEnvironment = RANDOM_PORT)`
   - This assigns a random available port for each test

2. **Tests pass individually but fail together**
   - Solution: Ensure tests don't share state
   - Use `@DirtiesContext` if needed

3. **JSON parsing errors**
   - Solution: Check your response structure matches what you're asserting
   - Use actual JSON response in error messages for debugging

**Testing Commands:**
```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=HealthControllerTest

# Run specific test method
mvn test -Dtest=HealthControllerTest#healthEndpointReturnsOk

# Run tests with detailed output
mvn test -X

# Skip tests (not recommended, but useful for quick builds)
mvn install -DskipTests
```

**Manual Testing:**
```powershell
# Using curl (if installed)
curl http://localhost:8080/api/health

# Using PowerShell
Invoke-RestMethod -Uri http://localhost:8080/api/health

# Using browser
# Navigate to: http://localhost:8080/api/health
```

---

## Day 3: JavaParser Integration

### Example 3.1: CodeParserService

```java
package com.codetalkerl.firestick.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for parsing Java source code using JavaParser.
 * Extracts classes, methods, and other structural information.
 */
@Service
public class CodeParserService {
    private static final Logger logger = LoggerFactory.getLogger(CodeParserService.class);
    private final JavaParser javaParser;

    public CodeParserService() {
        this.javaParser = new JavaParser();
    }

    /**
     * Parse a Java file and return the CompilationUnit.
     *
     * @param filePath Path to the Java file
     * @return CompilationUnit representing the parsed file
     * @throws IOException if file cannot be read
     */
    public CompilationUnit parseJavaFile(String filePath) throws IOException {
        logger.debug("Parsing Java file: {}", filePath);
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        ParseResult<CompilationUnit> parseResult = javaParser.parse(file);
        
        if (!parseResult.isSuccessful()) {
            logger.error("Failed to parse file: {}", filePath);
            parseResult.getProblems().forEach(problem -> 
                logger.error("Parse error: {}", problem.getMessage())
            );
            throw new IOException("Failed to parse Java file: " + filePath);
        }

        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new IOException("No compilation unit found"));
        
        logger.debug("Successfully parsed file: {}", filePath);
        return cu;
    }

    /**
     * Parse Java source code from a string.
     *
     * @param sourceCode Java source code as string
     * @return CompilationUnit representing the parsed code
     */
    public CompilationUnit parseJavaCode(String sourceCode) {
        logger.debug("Parsing Java code from string");
        
        ParseResult<CompilationUnit> parseResult = javaParser.parse(sourceCode);
        
        if (!parseResult.isSuccessful()) {
            logger.error("Failed to parse source code");
            throw new RuntimeException("Failed to parse Java code");
        }

        return parseResult.getResult()
            .orElseThrow(() -> new RuntimeException("No compilation unit found"));
    }

    /**
     * Extract all class names from a compilation unit.
     *
     * @param cu CompilationUnit to extract from
     * @return List of class names
     */
    public List<String> extractClassNames(CompilationUnit cu) {
        List<String> classNames = new ArrayList<>();
        
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            String className = classDecl.getNameAsString();
            classNames.add(className);
            logger.debug("Found class: {}", className);
        });
        
        return classNames;
    }

    /**
     * Extract all methods from a compilation unit.
     *
     * @param cu CompilationUnit to extract from
     * @return List of MethodDeclaration objects
     */
    public List<MethodDeclaration> extractMethods(CompilationUnit cu) {
        List<MethodDeclaration> methods = new ArrayList<>();
        
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            methods.add(method);
            logger.debug("Found method: {}", method.getNameAsString());
        });
        
        return methods;
    }

    /**
     * Get method source code as string.
     *
     * @param method MethodDeclaration
     * @return Method source code
     */
    public String getMethodSource(MethodDeclaration method) {
        return method.toString();
    }

    /**
     * Extract JavaDoc comment from a method.
     *
     * @param method MethodDeclaration
     * @return JavaDoc comment or empty string
     */
    public String getMethodJavaDoc(MethodDeclaration method) {
        Optional<JavadocComment> javadoc = method.getJavadocComment();
        return javadoc.map(JavadocComment::getContent).orElse("");
    }

    /**
     * Get method signature (name and parameters).
     *
     * @param method MethodDeclaration
     * @return Method signature
     */
    public String getMethodSignature(MethodDeclaration method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getNameAsString());
        signature.append("(");
        
        method.getParameters().forEach(param -> {
            signature.append(param.getType()).append(" ").append(param.getName()).append(", ");
        });
        
        if (method.getParameters().isNonEmpty()) {
            signature.setLength(signature.length() - 2); // Remove trailing ", "
        }
        
        signature.append(")");
        return signature.toString();
    }
}
```

### Example 3.2: CodeParserService Test

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.CodeParserService;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for CodeParserService.
 */
class CodeParserServiceTest {

    private CodeParserService parserService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parserService = new CodeParserService();
    }

    @Test
    void parseJavaFile_ValidFile_ReturnsCompilationUnit() throws IOException {
        // Given: Create a test Java file
        String javaCode = """
                package com.example;
                
                public class TestClass {
                    private String name;
                
                    public String getName() {
                        return name;
                    }
                
                    public void setName(String name) {
                        this.name = name;
                    }
                }
                """;

        Path javaFile = tempDir.resolve("TestClass.java");
        Files.writeString(javaFile, javaCode);

        // When: Parse the file
        CompilationUnit cu = parserService.parseJavaFile(javaFile.toString());

        // Then: CompilationUnit should be created
        assertThat(cu).isNotNull();
        assertThat(cu.getPackageDeclaration()).isPresent();
        assertThat(cu.getPackageDeclaration().get().getNameAsString()).isEqualTo("com.example");
    }

    @Test
    void parseJavaFile_FileNotFound_ThrowsException() {
        // Given: Non-existent file path
        String nonExistentPath = "nonexistent/file/path.java";

        // When/Then: Should throw IOException
        assertThatThrownBy(() -> parserService.parseJavaFile(nonExistentPath))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File not found");
    }

    @Test
    void parseJavaCode_ValidCode_ReturnsCompilationUnit() {
        // Given: Valid Java code
        String javaCode = """
                public class SimpleClass {
                    public void doSomething() {
                        System.out.println("Hello");
                    }
                }
                """;

        // When: Parse the code
        CompilationUnit cu = parserService.parseJavaCode(javaCode);

        // Then: Should successfully parse
        assertThat(cu).isNotNull();
    }

    @Test
    void extractClassNames_SingleClass_ReturnsClassName() {
        // Given: Code with one class
        String javaCode = """
                public class MyClass {
                    public void myMethod() {}
                }
                """;
        CompilationUnit cu = parserService.parseJavaCode(javaCode);

        // When: Extract class names
        List<String> classNames = parserService.extractClassNames(cu);

        // Then: Should find the class
        assertThat(classNames).containsExactly("MyClass");
    }

    @Test
    void extractMethods_MultipleMethods_ReturnsAllMethods() {
        // Given: Code with multiple methods
        String javaCode = """
                public class Calculator {
                    public int add(int a, int b) {
                        return a + b;
                    }
                
                    public int subtract(int a, int b) {
                        return a - b;
                    }
                
                    public int multiply(int a, int b) {
                        return a * b;
                    }
                }
                """;
        CompilationUnit cu = parserService.parseJavaCode(javaCode);

        // When: Extract methods
        List<MethodDeclaration> methods = parserService.extractMethods(cu);

        // Then: Should find all three methods
        assertThat(methods).hasSize(3);
        assertThat(methods)
                .extracting(MethodDeclaration::getNameAsString)
                .containsExactlyInAnyOrder("add", "subtract", "multiply");
    }

    @Test
    void getMethodSignature_ReturnsCorrectSignature() {
        // Given: Code with a method
        String javaCode = """
                public class Example {
                    public String greet(String name, int age) {
                        return "Hello " + name;
                    }
                }
                """;
        CompilationUnit cu = parserService.parseJavaCode(javaCode);
        MethodDeclaration method = parserService.extractMethods(cu).get(0);

        // When: Get signature
        String signature = parserService.getMethodSignature(method);

        // Then: Should return correct signature
        assertThat(signature).isEqualTo("greet(String name, int age)");
    }

    @Test
    void getMethodJavaDoc_WithJavaDoc_ReturnsComment() {
        // Given: Code with JavaDoc
        String javaCode = """
                public class Example {
                    /**
                     * This method greets a person.
                     * @param name The person's name
                     * @return A greeting message
                     */
                    public String greet(String name) {
                        return "Hello " + name;
                    }
                }
                """;
        CompilationUnit cu = parserService.parseJavaCode(javaCode);
        MethodDeclaration method = parserService.extractMethods(cu).get(0);

        // When: Get JavaDoc
        String javadoc = parserService.getMethodJavaDoc(method);

        // Then: Should return JavaDoc content
        assertThat(javadoc).contains("This method greets a person");
        assertThat(javadoc).contains("@param name");
    }
}
```

### Example 3.3: Sample Java File for Testing

Create `src/test/resources/sample-code/SampleClass.java`:

```java
package com.example.sample;

/**
 * A sample class for testing the parser.
 */
public class SampleClass {
    private String message;
    
    /**
     * Constructor for SampleClass.
     * @param message The initial message
     */
    public SampleClass(String message) {
        this.message = message;
    }
    
    /**
     * Gets the current message.
     * @return The message string
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets a new message.
     * @param message The new message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Processes the message by converting to uppercase.
     * @return The processed message
     */
    public String processMessage() {
        if (message == null) {
            return "";
        }
        return message.toUpperCase();
    }
}
```

### Advice for Day 3

**JavaParser Best Practices:**

1. **Always Handle Parse Errors**: Check `parseResult.isSuccessful()` before accessing results
2. **Use Visitors for Complex Analysis**: For more complex code analysis, use the Visitor pattern
3. **Cache Parsed Results**: Parsing is expensive - cache CompilationUnit objects when possible
4. **Handle Edge Cases**: Empty files, syntax errors, missing imports

**Common Issues:**

1. **"Cannot resolve symbol" errors**
   - JavaParser doesn't need the code to compile
   - It works on syntax alone
   - Symbol resolution is optional (requires additional setup)

2. **Memory issues with large files**
   - Consider streaming or chunking for very large codebases
   - Clear CompilationUnit objects after processing

3. **Different Java versions**
   - JavaParser 3.26.3 supports Java 21 syntax
   - For older Java versions, use appropriate JavaParser version

**Advanced Usage Example - Visitor Pattern:**

```java
// Custom visitor to count method calls
class MethodCallCounter extends VoidVisitorAdapter<Void> {
    private int count = 0;
    
    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
        super.visit(methodCall, arg);
        count++;
    }
    
    public int getCount() {
        return count;
    }
}

// Usage
MethodCallCounter counter = new MethodCallCounter();
counter.visit(compilationUnit, null);
System.out.println("Method calls: " + counter.getCount());
```

---

## Day 9: Chroma Integration

### Example 9.1: RestTemplate Configuration

```java
package com.codetalkerl.firestick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate to communicate with external APIs.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        
        return new RestTemplate(factory);
    }
}
```

### Example 9.2: ChromaService - Complete Implementation

```java
package com.codetalkerl.firestick.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for interacting with Chroma vector database via REST API.
 * 
 * Chroma must be running locally: chroma run --host localhost --port 8000
 */
@Service
public class ChromaService {
    private static final Logger logger = LoggerFactory.getLogger(ChromaService.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${chroma.url:http://localhost:8000}")
    private String chromaBaseUrl;
    
    private static final String API_VERSION = "/api/v1";

    public ChromaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Check if Chroma server is running.
     *
     * @return true if server is accessible
     */
    public boolean isChromaRunning() {
        try {
            String url = chromaBaseUrl + API_VERSION + "/heartbeat";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            logger.info("Chroma heartbeat: {}", response.getBody());
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Chroma is not running: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create a new collection in Chroma.
     *
     * @param collectionName Name of the collection
     * @return true if created successfully
     */
    public boolean createCollection(String collectionName) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", collectionName);
            requestBody.put("metadata", Map.of("description", "Code embeddings"));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Collection created: {}", collectionName);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Failed to create collection: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get collection information.
     *
     * @param collectionName Name of the collection
     * @return Collection info or null if not found
     */
    public Map<String, Object> getCollection(String collectionName) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections/" + collectionName;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to get collection: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Delete a collection.
     *
     * @param collectionName Name of the collection to delete
     * @return true if deleted successfully
     */
    public boolean deleteCollection(String collectionName) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections/" + collectionName;
            restTemplate.delete(url);
            logger.info("Collection deleted: {}", collectionName);
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete collection: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Add embeddings to a collection.
     *
     * @param collectionName Name of the collection
     * @param embeddings List of embedding vectors
     * @param documents List of document texts
     * @param ids List of unique IDs for each embedding
     * @param metadatas Optional metadata for each document
     * @return true if added successfully
     */
    public boolean addEmbeddings(String collectionName, 
                                  List<float[]> embeddings, 
                                  List<String> documents,
                                  List<String> ids,
                                  List<Map<String, Object>> metadatas) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections/" + collectionName + "/add";
            
            // Convert float[] to List<Float> for JSON serialization
            List<List<Float>> embeddingsList = new ArrayList<>();
            for (float[] embedding : embeddings) {
                List<Float> floatList = new ArrayList<>();
                for (float value : embedding) {
                    floatList.add(value);
                }
                embeddingsList.add(floatList);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("embeddings", embeddingsList);
            requestBody.put("documents", documents);
            requestBody.put("ids", ids);
            if (metadatas != null && !metadatas.isEmpty()) {
                requestBody.put("metadatas", metadatas);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Added {} embeddings to collection {}", embeddings.size(), collectionName);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Failed to add embeddings: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Query a collection with an embedding vector.
     *
     * @param collectionName Name of the collection
     * @param queryEmbedding Query embedding vector
     * @param topK Number of results to return
     * @return Query results
     */
    public Map<String, Object> query(String collectionName, 
                                      float[] queryEmbedding, 
                                      int topK) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections/" + collectionName + "/query";
            
            // Convert float[] to List<Float>
            List<Float> embeddingList = new ArrayList<>();
            for (float value : queryEmbedding) {
                embeddingList.add(value);
            }
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query_embeddings", List.of(embeddingList));
            requestBody.put("n_results", topK);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Query executed on collection: {}", collectionName);
                return response.getBody();
            }
            
            return Collections.emptyMap();
        } catch (Exception e) {
            logger.error("Failed to query collection: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get count of items in a collection.
     *
     * @param collectionName Name of the collection
     * @return Number of items
     */
    public int getCollectionCount(String collectionName) {
        try {
            String url = chromaBaseUrl + API_VERSION + "/collections/" + collectionName + "/count";
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
            return response.getBody() != null ? response.getBody() : 0;
        } catch (Exception e) {
            logger.error("Failed to get collection count: {}", e.getMessage());
            return 0;
        }
    }
}
```

### Example 9.3: ChromaService Test

```java
package com.codetalkerl.firestick.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests for ChromaService.
 * These tests require Chroma to be running locally on port 8000.
 */
@SpringBootTest
class ChromaServiceTest {

    @Autowired
    private ChromaService chromaService;

    private static final String TEST_COLLECTION = "test_collection";

    @BeforeEach
    void setUp() {
        // Only run tests if Chroma is running
        assumeTrue(chromaService.isChromaRunning(), "Chroma must be running for these tests");
        
        // Clean up: delete test collection if it exists
        chromaService.deleteCollection(TEST_COLLECTION);
    }

    @Test
    void isChromaRunning_ChromaIsUp_ReturnsTrue() {
        // When/Then
        assertThat(chromaService.isChromaRunning()).isTrue();
    }

    @Test
    void createCollection_ValidName_CreatesSuccessfully() {
        // When
        boolean created = chromaService.createCollection(TEST_COLLECTION);

        // Then
        assertThat(created).isTrue();
        
        // Verify collection exists
        Map<String, Object> collection = chromaService.getCollection(TEST_COLLECTION);
        assertThat(collection).isNotNull();
        assertThat(collection.get("name")).isEqualTo(TEST_COLLECTION);
    }

    @Test
    void addEmbeddings_ValidData_AddsSuccessfully() {
        // Given
        chromaService.createCollection(TEST_COLLECTION);
        
        List<float[]> embeddings = Arrays.asList(
            new float[]{0.1f, 0.2f, 0.3f},
            new float[]{0.4f, 0.5f, 0.6f}
        );
        
        List<String> documents = Arrays.asList(
            "First document",
            "Second document"
        );
        
        List<String> ids = Arrays.asList("id1", "id2");
        
        List<Map<String, Object>> metadatas = Arrays.asList(
            Map.of("source", "test1"),
            Map.of("source", "test2")
        );

        // When
        boolean added = chromaService.addEmbeddings(
            TEST_COLLECTION, 
            embeddings, 
            documents, 
            ids, 
            metadatas
        );

        // Then
        assertThat(added).isTrue();
        assertThat(chromaService.getCollectionCount(TEST_COLLECTION)).isEqualTo(2);
    }

    @Test
    void query_WithValidEmbedding_ReturnsResults() {
        // Given: Add some test data
        chromaService.createCollection(TEST_COLLECTION);
        
        List<float[]> embeddings = Arrays.asList(
            new float[]{0.1f, 0.2f, 0.3f},
            new float[]{0.4f, 0.5f, 0.6f}
        );
        
        List<String> documents = Arrays.asList(
            "Document about Java programming",
            "Document about Python programming"
        );
        
        List<String> ids = Arrays.asList("java-doc", "python-doc");
        
        chromaService.addEmbeddings(TEST_COLLECTION, embeddings, documents, ids, null);

        // When: Query with similar embedding
        float[] queryEmbedding = new float[]{0.11f, 0.21f, 0.31f};
        Map<String, Object> results = chromaService.query(TEST_COLLECTION, queryEmbedding, 2);

        // Then
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
        // Chroma returns results in a specific structure - adjust based on actual response
    }

    @Test
    void deleteCollection_ExistingCollection_DeletesSuccessfully() {
        // Given
        chromaService.createCollection(TEST_COLLECTION);
        assertThat(chromaService.getCollection(TEST_COLLECTION)).isNotNull();

        // When
        boolean deleted = chromaService.deleteCollection(TEST_COLLECTION);

        // Then
        assertThat(deleted).isTrue();
    }
}
```

### Example 9.4: Application Properties for Chroma

```properties
# Chroma Configuration
chroma.url=http://localhost:8000
chroma.default.collection=firestick_code
```

### Example 9.5: Starting Chroma Server

**Option 1: Using Docker (Recommended)**
```powershell
# Pull Chroma Docker image
docker pull chromadb/chroma

# Run Chroma in a container
docker run -p 8000:8000 chromadb/chroma

# Or with persistent storage
docker run -p 8000:8000 -v ./chroma-data:/chroma/chroma chromadb/chroma
```

**Option 2: Using Python**
```powershell
# Install Chroma
pip install chromadb

# Run Chroma server
chroma run --host localhost --port 8000

# Or specify data directory
chroma run --path ./chroma-data --host localhost --port 8000
```

**Option 3: Using Python Script**

Create `start-chroma.py`:
```python
import chromadb
from chromadb.config import Settings

# Create Chroma client with persistent storage
client = chromadb.Client(Settings(
    chroma_db_impl="duckdb+parquet",
    persist_directory="./chroma-data"
))

# Start HTTP server
from chromadb.server.fastapi import FastAPI
import uvicorn

app = FastAPI(client=client)
uvicorn.run(app, host="localhost", port=8000)
```

Run it:
```powershell
python start-chroma.py
```

### Advice for Day 9

**Chroma Best Practices:**

1. **Always Check Connection**: Use `isChromaRunning()` before operations
2. **Handle Errors Gracefully**: Chroma might not always be available
3. **Use Unique IDs**: Ensure document IDs are unique within a collection
4. **Batch Operations**: Add embeddings in batches for better performance
5. **Monitor Collection Size**: Large collections may impact query performance

**Common Issues:**

1. **"Connection refused" error**
   - Ensure Chroma is running: `chroma run --host localhost --port 8000`
   - Check port 8000 is not blocked by firewall
   - Try accessing http://localhost:8000/api/v1/heartbeat in browser

2. **"Collection already exists" error**
   - Delete existing collection first
   - Or use a different collection name
   - Collections persist across Chroma restarts

3. **Embedding dimension mismatch**
   - Ensure all embeddings have the same dimension
   - Check your embedding model output size
   - All-MiniLM-L6-v2 produces 384-dimensional vectors

4. **Slow queries**
   - Consider reducing collection size
   - Use more specific queries
   - Limit topK parameter

**Testing Tips:**

1. **Use JUnit Assumptions**: Skip tests if Chroma isn't running
   ```java
   assumeTrue(chromaService.isChromaRunning(), "Chroma must be running");
   ```

2. **Clean Up After Tests**: Always delete test collections
   ```java
   @AfterEach
   void cleanup() {
       chromaService.deleteCollection(TEST_COLLECTION);
   }
   ```

3. **Use Test Profiles**: Different Chroma URLs for dev/test/prod
   ```properties
   # application-test.properties
   chroma.url=http://localhost:8001
   ```

**Debugging Chroma Requests:**

```java
// Enable RestTemplate logging
logging.level.org.springframework.web.client.RestTemplate=DEBUG

// Or use interceptor for detailed logging
@Bean
public RestTemplate restTemplate() {
    RestTemplate template = new RestTemplate();
    template.setInterceptors(List.of((request, body, execution) -> {
        logger.debug("Request: {} {}", request.getMethod(), request.getURI());
        logger.debug("Body: {}", new String(body));
        ClientHttpResponse response = execution.execute(request, body);
        logger.debug("Response: {}", response.getStatusCode());
        return response;
    }));
    return template;
}
```

---

## Day 4: Lucene Integration

### Example 4.1: CodeSearchService with Lucene

```java
package com.codetalkerl.firestick.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for full-text search using Apache Lucene.
 * Provides keyword-based search capabilities for code.
 */
@Service
public class CodeSearchService {
    private static final Logger logger = LoggerFactory.getLogger(CodeSearchService.class);
    
    private Directory indexDirectory;
    private StandardAnalyzer analyzer;
    private IndexWriter indexWriter;
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    @PostConstruct
    public void init() throws IOException {
        logger.info("Initializing Lucene CodeSearchService...");
        
        // Create in-memory index
        indexDirectory = new ByteBuffersDirectory();
        analyzer = new StandardAnalyzer();
        
        // Configure index writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        
        indexWriter = new IndexWriter(indexDirectory, config);
        
        logger.info("Lucene CodeSearchService initialized");
    }

    /**
     * Index a code document.
     *
     * @param id Unique identifier for the document
     * @param content Code content to index
     * @param metadata Additional metadata (file path, class name, etc.)
     * @throws IOException if indexing fails
     */
    public void indexDocument(String id, String content, Map<String, String> metadata) throws IOException {
        logger.debug("Indexing document: {}", id);
        
        Document doc = new Document();
        
        // ID field (stored, not indexed for search)
        doc.add(new StringField("id", id, Field.Store.YES));
        
        // Content field (indexed and stored)
        doc.add(new TextField("content", content, Field.Store.YES));
        
        // Add metadata fields
        if (metadata != null) {
            metadata.forEach((key, value) -> {
                doc.add(new StringField(key, value, Field.Store.YES));
            });
        }
        
        indexWriter.addDocument(doc);
        indexWriter.commit();
        
        // Refresh reader and searcher
        refreshSearcher();
        
        logger.debug("Document indexed successfully: {}", id);
    }

    /**
     * Index multiple documents in batch.
     *
     * @param documents List of documents to index
     * @throws IOException if indexing fails
     */
    public void indexDocuments(List<Map<String, Object>> documents) throws IOException {
        logger.info("Batch indexing {} documents", documents.size());
        
        for (Map<String, Object> docData : documents) {
            String id = (String) docData.get("id");
            String content = (String) docData.get("content");
            @SuppressWarnings("unchecked")
            Map<String, String> metadata = (Map<String, String>) docData.get("metadata");
            
            Document doc = new Document();
            doc.add(new StringField("id", id, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            
            if (metadata != null) {
                metadata.forEach((key, value) -> {
                    doc.add(new StringField(key, value, Field.Store.YES));
                });
            }
            
            indexWriter.addDocument(doc);
        }
        
        indexWriter.commit();
        refreshSearcher();
        
        logger.info("Batch indexing completed");
    }

    /**
     * Search for documents matching a query.
     *
     * @param queryString Search query
     * @param maxResults Maximum number of results to return
     * @return List of matching documents with scores
     * @throws Exception if search fails
     */
    public List<Map<String, Object>> search(String queryString, int maxResults) throws Exception {
        logger.debug("Searching for: {}", queryString);
        
        if (indexSearcher == null) {
            refreshSearcher();
        }
        
        // Parse query
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(queryString);
        
        // Execute search
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        
        // Convert results to list
        List<Map<String, Object>> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", doc.get("id"));
            result.put("content", doc.get("content"));
            result.put("score", scoreDoc.score);
            
            // Add all stored fields
            doc.getFields().forEach(field -> {
                if (!field.name().equals("id") && !field.name().equals("content")) {
                    result.put(field.name(), field.stringValue());
                }
            });
            
            results.add(result);
        }
        
        logger.debug("Found {} results for query: {}", results.size(), queryString);
        return results;
    }

    /**
     * Search with field-specific queries.
     *
     * @param fieldName Field to search in
     * @param queryString Query string
     * @param maxResults Maximum results
     * @return List of matching documents
     * @throws Exception if search fails
     */
    public List<Map<String, Object>> searchField(String fieldName, String queryString, int maxResults) throws Exception {
        logger.debug("Searching field '{}' for: {}", fieldName, queryString);
        
        if (indexSearcher == null) {
            refreshSearcher();
        }
        
        QueryParser parser = new QueryParser(fieldName, analyzer);
        Query query = parser.parse(queryString);
        
        TopDocs topDocs = indexSearcher.search(query, maxResults);
        
        List<Map<String, Object>> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            
            Map<String, Object> result = new HashMap<>();
            doc.getFields().forEach(field -> {
                result.put(field.name(), field.stringValue());
            });
            result.put("score", scoreDoc.score);
            
            results.add(result);
        }
        
        return results;
    }

    /**
     * Delete a document from the index.
     *
     * @param id Document ID to delete
     * @throws IOException if deletion fails
     */
    public void deleteDocument(String id) throws IOException {
        logger.debug("Deleting document: {}", id);
        
        indexWriter.deleteDocuments(new Term("id", id));
        indexWriter.commit();
        refreshSearcher();
        
        logger.debug("Document deleted: {}", id);
    }

    /**
     * Get the total number of indexed documents.
     *
     * @return Number of documents
     */
    public int getDocumentCount() {
        try {
            if (indexReader == null) {
                refreshSearcher();
            }
            return indexReader.numDocs();
        } catch (IOException e) {
            logger.error("Failed to get document count", e);
            return 0;
        }
    }

    /**
     * Clear the entire index.
     *
     * @throws IOException if clearing fails
     */
    public void clearIndex() throws IOException {
        logger.info("Clearing Lucene index");
        indexWriter.deleteAll();
        indexWriter.commit();
        refreshSearcher();
    }

    /**
     * Refresh the index reader and searcher.
     */
    private void refreshSearcher() throws IOException {
        if (indexReader != null) {
            indexReader.close();
        }
        
        indexReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(indexReader);
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up Lucene resources");
        
        try {
            if (indexWriter != null) {
                indexWriter.close();
            }
            if (indexReader != null) {
                indexReader.close();
            }
            if (indexDirectory != null) {
                indexDirectory.close();
            }
            if (analyzer != null) {
                analyzer.close();
            }
        } catch (IOException e) {
            logger.error("Error cleaning up Lucene resources", e);
        }
    }
}
```

### Example 4.2: CodeSearchService Test

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.CodeSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CodeSearchServiceTest {

    @Autowired
    private CodeSearchService searchService;

    @BeforeEach
    void setUp() throws Exception {
        // Clear index before each test
        searchService.clearIndex();
    }

    @Test
    void indexDocument_ValidDocument_IndexesSuccessfully() throws Exception {
        // Given
        String id = "doc1";
        String content = "public class HelloWorld { public static void main(String[] args) {} }";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("filePath", "/src/HelloWorld.java");
        metadata.put("className", "HelloWorld");

        // When
        searchService.indexDocument(id, content, metadata);

        // Then
        assertThat(searchService.getDocumentCount()).isEqualTo(1);
    }

    @Test
    void search_MatchingQuery_ReturnsResults() throws Exception {
        // Given: Index some documents
        searchService.indexDocument("doc1",
                "public class Calculator { public int add(int a, int b) { return a + b; } }",
                Map.of("className", "Calculator"));

        searchService.indexDocument("doc2",
                "public class StringUtils { public String concatenate(String a, String b) { return a + b; } }",
                Map.of("className", "StringUtils"));

        // When: Search for documents containing "Calculator"
        List<Map<String, Object>> results = searchService.search("Calculator", 10);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).get("className")).isEqualTo("Calculator");
    }

    @Test
    void search_NoMatches_ReturnsEmptyList() throws Exception {
        // Given
        searchService.indexDocument("doc1",
                "public class Example {}",
                Map.of("className", "Example"));

        // When
        List<Map<String, Object>> results = searchService.search("NonExistent", 10);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void searchField_SpecificField_ReturnsMatchingDocuments() throws Exception {
        // Given
        searchService.indexDocument("doc1",
                "Some content",
                Map.of("className", "MyClass", "packageName", "com.example"));

        searchService.indexDocument("doc2",
                "Other content",
                Map.of("className", "OtherClass", "packageName", "com.test"));

        // When: Search in className field
        List<Map<String, Object>> results = searchService.searchField("className", "MyClass", 10);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).get("className")).isEqualTo("MyClass");
    }

    @Test
    void deleteDocument_ExistingDocument_RemovesFromIndex() throws Exception {
        // Given
        searchService.indexDocument("doc1", "Content to delete", null);
        assertThat(searchService.getDocumentCount()).isEqualTo(1);

        // When
        searchService.deleteDocument("doc1");

        // Then
        assertThat(searchService.getDocumentCount()).isEqualTo(0);
    }

    @Test
    void getDocumentCount_MultipleDocuments_ReturnsCorrectCount() throws Exception {
        // Given
        searchService.indexDocument("doc1", "First document", null);
        searchService.indexDocument("doc2", "Second document", null);
        searchService.indexDocument("doc3", "Third document", null);

        // When/Then
        assertThat(searchService.getDocumentCount()).isEqualTo(3);
    }
}
```

### Advice for Day 4

**Lucene Best Practices:**

1. **Commit After Batch Operations**: Don't commit after every single document
2. **Use Appropriate Field Types**: 
   - `TextField` for searchable content
   - `StringField` for exact matching (IDs, tags)
   - `StoredField` for data you want to retrieve but not search
3. **Refresh Searcher After Updates**: Always refresh after index changes
4. **Close Resources Properly**: Use `@PreDestroy` to clean up

**Common Issues:**

1. **"No results found" but document was indexed**
   - Solution: Call `refreshSearcher()` after indexing
   - Or use `commit()` to persist changes

2. **Query syntax errors**
   - Lucene query syntax: `field:value`, `AND`, `OR`, `NOT`
   - Escape special characters: `+ - && || ! ( ) { } [ ] ^ " ~ * ? : \`
   - Use `QueryParser.escape()` for user input

3. **OutOfMemoryError with large indexes**
   - Use `FSDirectory` instead of `ByteBuffersDirectory` for persistence
   - Increase JVM heap size
   - Implement pagination for large result sets

**Advanced Query Examples:**

```java
// Boolean queries
BooleanQuery.Builder builder = new BooleanQuery.Builder();
builder.add(new TermQuery(new Term("className", "Calculator")), BooleanClause.Occur.MUST);
builder.add(new TermQuery(new Term("packageName", "com.example")), BooleanClause.Occur.MUST);
Query query = builder.build();

// Phrase queries
PhraseQuery phraseQuery = new PhraseQuery("content", "public", "static", "void");

// Wildcard queries
WildcardQuery wildcardQuery = new WildcardQuery(new Term("className", "Test*"));

// Fuzzy queries (for typos)
FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("content", "methd"), 2);
```

---

## Day 5: JGraphT Integration

### Example 5.1: DependencyGraphService

```java
package com.codetalkerl.firestick.service;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing code dependency graphs using JGraphT.
 * Tracks relationships between classes, methods, and packages.
 */
@Service
public class DependencyGraphService {
    private static final Logger logger = LoggerFactory.getLogger(DependencyGraphService.class);
    
    // Graph where vertices are class/method names and edges are dependencies
    private final Graph<String, DefaultEdge> dependencyGraph;

    public DependencyGraphService() {
        this.dependencyGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        logger.info("DependencyGraphService initialized");
    }

    /**
     * Add a vertex (class or method) to the graph.
     *
     * @param vertexName Name of the vertex
     */
    public void addVertex(String vertexName) {
        if (!dependencyGraph.containsVertex(vertexName)) {
            dependencyGraph.addVertex(vertexName);
            logger.debug("Added vertex: {}", vertexName);
        }
    }

    /**
     * Add a dependency edge between two vertices.
     *
     * @param source Source vertex (dependent)
     * @param target Target vertex (dependency)
     */
    public void addDependency(String source, String target) {
        // Ensure both vertices exist
        addVertex(source);
        addVertex(target);
        
        // Add edge if it doesn't exist
        if (!dependencyGraph.containsEdge(source, target)) {
            dependencyGraph.addEdge(source, target);
            logger.debug("Added dependency: {} -> {}", source, target);
        }
    }

    /**
     * Get all direct dependencies of a vertex.
     *
     * @param vertexName Name of the vertex
     * @return Set of direct dependencies
     */
    public Set<String> getDirectDependencies(String vertexName) {
        if (!dependencyGraph.containsVertex(vertexName)) {
            logger.warn("Vertex not found: {}", vertexName);
            return Collections.emptySet();
        }
        
        return dependencyGraph.outgoingEdgesOf(vertexName).stream()
            .map(dependencyGraph::getEdgeTarget)
            .collect(Collectors.toSet());
    }

    /**
     * Get all direct dependents (reverse dependencies) of a vertex.
     *
     * @param vertexName Name of the vertex
     * @return Set of direct dependents
     */
    public Set<String> getDirectDependents(String vertexName) {
        if (!dependencyGraph.containsVertex(vertexName)) {
            logger.warn("Vertex not found: {}", vertexName);
            return Collections.emptySet();
        }
        
        return dependencyGraph.incomingEdgesOf(vertexName).stream()
            .map(dependencyGraph::getEdgeSource)
            .collect(Collectors.toSet());
    }

    /**
     * Get all transitive dependencies using BFS.
     *
     * @param vertexName Name of the vertex
     * @return Set of all transitive dependencies
     */
    public Set<String> getAllDependencies(String vertexName) {
        if (!dependencyGraph.containsVertex(vertexName)) {
            return Collections.emptySet();
        }
        
        Set<String> dependencies = new HashSet<>();
        BreadthFirstIterator<String, DefaultEdge> iterator = 
            new BreadthFirstIterator<>(dependencyGraph, vertexName);
        
        // Skip the starting vertex itself
        if (iterator.hasNext()) {
            iterator.next();
        }
        
        // Collect all reachable vertices
        iterator.forEachRemaining(dependencies::add);
        
        return dependencies;
    }

    /**
     * Find shortest path between two vertices.
     *
     * @param source Source vertex
     * @param target Target vertex
     * @return List of vertices in the path, or empty if no path exists
     */
    public List<String> findShortestPath(String source, String target) {
        if (!dependencyGraph.containsVertex(source) || !dependencyGraph.containsVertex(target)) {
            return Collections.emptyList();
        }
        
        DijkstraShortestPath<String, DefaultEdge> dijkstra = 
            new DijkstraShortestPath<>(dependencyGraph);
        
        var path = dijkstra.getPath(source, target);
        
        if (path == null) {
            return Collections.emptyList();
        }
        
        return path.getVertexList();
    }

    /**
     * Check if there's a path from source to target.
     *
     * @param source Source vertex
     * @param target Target vertex
     * @return true if path exists
     */
    public boolean hasPath(String source, String target) {
        return !findShortestPath(source, target).isEmpty();
    }

    /**
     * Get all vertices in the graph.
     *
     * @return Set of all vertex names
     */
    public Set<String> getAllVertices() {
        return new HashSet<>(dependencyGraph.vertexSet());
    }

    /**
     * Get the number of vertices in the graph.
     *
     * @return Vertex count
     */
    public int getVertexCount() {
        return dependencyGraph.vertexSet().size();
    }

    /**
     * Get the number of edges (dependencies) in the graph.
     *
     * @return Edge count
     */
    public int getEdgeCount() {
        return dependencyGraph.edgeSet().size();
    }

    /**
     * Remove a vertex and all its edges.
     *
     * @param vertexName Name of the vertex to remove
     */
    public void removeVertex(String vertexName) {
        if (dependencyGraph.containsVertex(vertexName)) {
            dependencyGraph.removeVertex(vertexName);
            logger.debug("Removed vertex: {}", vertexName);
        }
    }

    /**
     * Clear the entire graph.
     */
    public void clearGraph() {
        dependencyGraph.removeAllVertices(new HashSet<>(dependencyGraph.vertexSet()));
        logger.info("Graph cleared");
    }

    /**
     * Get graph statistics.
     *
     * @return Map of statistics
     */
    public Map<String, Object> getGraphStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("vertexCount", getVertexCount());
        stats.put("edgeCount", getEdgeCount());
        stats.put("density", calculateDensity());
        
        return stats;
    }

    /**
     * Calculate graph density (ratio of edges to possible edges).
     *
     * @return Density value between 0 and 1
     */
    private double calculateDensity() {
        int n = getVertexCount();
        if (n <= 1) {
            return 0.0;
        }
        
        int maxEdges = n * (n - 1); // Directed graph
        return (double) getEdgeCount() / maxEdges;
    }

    /**
     * Export graph as adjacency list.
     *
     * @return Map where key is vertex and value is list of dependencies
     */
    public Map<String, List<String>> exportAsAdjacencyList() {
        Map<String, List<String>> adjacencyList = new HashMap<>();
        
        for (String vertex : dependencyGraph.vertexSet()) {
            List<String> dependencies = new ArrayList<>(getDirectDependencies(vertex));
            adjacencyList.put(vertex, dependencies);
        }
        
        return adjacencyList;
    }
}
```

### Example 5.2: DependencyGraphService Test

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyGraphServiceTest {

    private DependencyGraphService graphService;

    @BeforeEach
    void setUp() {
        graphService = new DependencyGraphService();
    }

    @Test
    void addVertex_NewVertex_AddsSuccessfully() {
        // When
        graphService.addVertex("ClassA");

        // Then
        assertThat(graphService.getVertexCount()).isEqualTo(1);
        assertThat(graphService.getAllVertices()).contains("ClassA");
    }

    @Test
    void addDependency_ValidVertices_CreatesDependency() {
        // When
        graphService.addDependency("ClassA", "ClassB");

        // Then
        assertThat(graphService.getVertexCount()).isEqualTo(2);
        assertThat(graphService.getEdgeCount()).isEqualTo(1);
        assertThat(graphService.getDirectDependencies("ClassA")).contains("ClassB");
    }

    @Test
    void getDirectDependencies_MultipleEdges_ReturnsAllDependencies() {
        // Given: ClassA depends on ClassB and ClassC
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassA", "ClassC");

        // When
        Set<String> dependencies = graphService.getDirectDependencies("ClassA");

        // Then
        assertThat(dependencies).containsExactlyInAnyOrder("ClassB", "ClassC");
    }

    @Test
    void getDirectDependents_ReverseDependency_ReturnsDependents() {
        // Given: ClassB is used by ClassA
        graphService.addDependency("ClassA", "ClassB");

        // When
        Set<String> dependents = graphService.getDirectDependents("ClassB");

        // Then
        assertThat(dependents).contains("ClassA");
    }

    @Test
    void getAllDependencies_TransitiveDependencies_ReturnsAll() {
        // Given: A -> B -> C -> D (chain of dependencies)
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");
        graphService.addDependency("ClassC", "ClassD");

        // When
        Set<String> allDeps = graphService.getAllDependencies("ClassA");

        // Then: ClassA transitively depends on B, C, and D
        assertThat(allDeps).containsExactlyInAnyOrder("ClassB", "ClassC", "ClassD");
    }

    @Test
    void findShortestPath_PathExists_ReturnsPath() {
        // Given: A -> B -> C
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");

        // When
        List<String> path = graphService.findShortestPath("ClassA", "ClassC");

        // Then
        assertThat(path).containsExactly("ClassA", "ClassB", "ClassC");
    }

    @Test
    void findShortestPath_NoPath_ReturnsEmptyList() {
        // Given: Disconnected vertices
        graphService.addVertex("ClassA");
        graphService.addVertex("ClassB");

        // When
        List<String> path = graphService.findShortestPath("ClassA", "ClassB");

        // Then
        assertThat(path).isEmpty();
    }

    @Test
    void hasPath_PathExists_ReturnsTrue() {
        // Given
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");

        // When/Then
        assertThat(graphService.hasPath("ClassA", "ClassC")).isTrue();
        assertThat(graphService.hasPath("ClassC", "ClassA")).isFalse();
    }

    @Test
    void removeVertex_ExistingVertex_RemovesVertexAndEdges() {
        // Given
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");

        // When
        graphService.removeVertex("ClassB");

        // Then
        assertThat(graphService.getVertexCount()).isEqualTo(2);
        assertThat(graphService.getAllVertices()).containsExactlyInAnyOrder("ClassA", "ClassC");
        assertThat(graphService.getEdgeCount()).isEqualTo(0);
    }

    @Test
    void clearGraph_WithData_RemovesEverything() {
        // Given
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");

        // When
        graphService.clearGraph();

        // Then
        assertThat(graphService.getVertexCount()).isEqualTo(0);
        assertThat(graphService.getEdgeCount()).isEqualTo(0);
    }

    @Test
    void getGraphStatistics_ReturnsCorrectStats() {
        // Given
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassB", "ClassC");

        // When
        Map<String, Object> stats = graphService.getGraphStatistics();

        // Then
        assertThat(stats.get("vertexCount")).isEqualTo(3);
        assertThat(stats.get("edgeCount")).isEqualTo(2);
        assertThat(stats).containsKey("density");
    }

    @Test
    void exportAsAdjacencyList_ReturnsCorrectStructure() {
        // Given
        graphService.addDependency("ClassA", "ClassB");
        graphService.addDependency("ClassA", "ClassC");
        graphService.addDependency("ClassB", "ClassC");

        // When
        Map<String, List<String>> adjacencyList = graphService.exportAsAdjacencyList();

        // Then
        assertThat(adjacencyList.get("ClassA")).containsExactlyInAnyOrder("ClassB", "ClassC");
        assertThat(adjacencyList.get("ClassB")).containsExactly("ClassC");
        assertThat(adjacencyList.get("ClassC")).isEmpty();
    }
}
```

### Advice for Day 5

**JGraphT Best Practices:**

1. **Choose Right Graph Type**: 
   - `DirectedGraph` for dependencies (A depends on B)
   - `UndirectedGraph` for associations
2. **Use Appropriate Edge Types**: `DefaultEdge` is usually sufficient
3. **Check Vertex Existence**: Before adding edges or querying
4. **Consider Memory**: Large graphs can consume significant memory

**Common Graph Operations:**

```java
// Detect cycles
CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
boolean hasCycles = cycleDetector.detectCycles();
Set<String> cyclicVertices = cycleDetector.findCycles();

// Find strongly connected components
StrongConnectivityAlgorithm<String, DefaultEdge> scAlg = 
    new KosarajuStrongConnectivityInspector<>(graph);
List<Set<String>> stronglyConnectedSets = scAlg.stronglyConnectedSets();

// Topological sort (for DAGs)
TopologicalOrderIterator<String, DefaultEdge> topoIterator = 
    new TopologicalOrderIterator<>(graph);
List<String> topologicalOrder = new ArrayList<>();
topoIterator.forEachRemaining(topologicalOrder::add);
```

---

## Day 8: ONNX Runtime Setup

### Example 8.1: EmbeddingService with ONNX Runtime

```java
package com.codetalkerl.firestick.service;

import ai.onnxruntime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Service for generating text embeddings using ONNX Runtime.
 * Uses the all-MiniLM-L6-v2 model for generating 384-dimensional embeddings.
 */
@Service
public class EmbeddingService {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
    
    @Value("${embedding.model.path:models/model.onnx}")
    private String modelPath;
    
    @Value("${embedding.vocab.path:models/vocab.txt}")
    private String vocabPath;
    
    private OrtEnvironment env;
    private OrtSession session;
    private SimpleTokenizer tokenizer;
    
    // Model constants for all-MiniLM-L6-v2
    private static final int MAX_SEQUENCE_LENGTH = 128;
    private static final int EMBEDDING_DIMENSION = 384;

    @PostConstruct
    public void init() throws OrtException, IOException {
        logger.info("Initializing ONNX Embedding Service...");
        
        // Create ONNX environment
        env = OrtEnvironment.getEnvironment();
        
        // Load ONNX model
        Path modelFilePath = Paths.get(modelPath);
        if (!Files.exists(modelFilePath)) {
            throw new IOException("ONNX model not found at: " + modelPath);
        }
        
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        session = env.createSession(modelFilePath.toString(), options);
        
        logger.info("ONNX model loaded from: {}", modelPath);
        logger.info("Model inputs: {}", session.getInputNames());
        logger.info("Model outputs: {}", session.getOutputNames());
        
        // Load tokenizer
        tokenizer = new SimpleTokenizer(vocabPath);
        
        logger.info("Embedding Service initialized successfully");
    }

    /**
     * Generate embedding vector for text.
     *
     * @param text Input text
     * @return 384-dimensional embedding vector
     * @throws OrtException if generation fails
     */
    public float[] generateEmbedding(String text) throws OrtException {
        logger.debug("Generating embedding for text: {}", 
            text.length() > 50 ? text.substring(0, 50) + "..." : text);
        
        // Tokenize text
        long[] inputIds = tokenizer.tokenize(text, MAX_SEQUENCE_LENGTH);
        long[] attentionMask = createAttentionMask(inputIds);
        long[] tokenTypeIds = new long[inputIds.length]; // All zeros
        
        // Create input tensors
        long[] shape = {1, inputIds.length};
        
        OnnxTensor inputIdsTensor = OnnxTensor.createTensor(env, 
            new long[][]{inputIds});
        OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(env, 
            new long[][]{attentionMask});
        OnnxTensor tokenTypeIdsTensor = OnnxTensor.createTensor(env, 
            new long[][]{tokenTypeIds});
        
        // Prepare inputs
        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_ids", inputIdsTensor);
        inputs.put("attention_mask", attentionMaskTensor);
        inputs.put("token_type_ids", tokenTypeIdsTensor);
        
        // Run inference
        try (OrtSession.Result result = session.run(inputs)) {
            // Get output tensor (last_hidden_state or pooler_output)
            OnnxValue output = result.get(0); // First output
            
            float[][][] outputArray = (float[][][]) output.getValue();
            
            // Apply mean pooling to get sentence embedding
            float[] embedding = meanPooling(outputArray[0], attentionMask);
            
            // Normalize the embedding
            normalize(embedding);
            
            logger.debug("Generated embedding with dimension: {}", embedding.length);
            return embedding;
            
        } finally {
            // Clean up tensors
            inputIdsTensor.close();
            attentionMaskTensor.close();
            tokenTypeIdsTensor.close();
        }
    }

    /**
     * Generate embeddings for multiple texts in batch.
     *
     * @param texts List of texts
     * @return List of embedding vectors
     */
    public List<float[]> generateEmbeddingsBatch(List<String> texts) throws OrtException {
        logger.info("Generating embeddings for {} texts", texts.size());
        
        List<float[]> embeddings = new ArrayList<>();
        for (String text : texts) {
            embeddings.add(generateEmbedding(text));
        }
        
        return embeddings;
    }

    /**
     * Calculate cosine similarity between two embeddings.
     *
     * @param embedding1 First embedding
     * @param embedding2 Second embedding
     * @return Similarity score between -1 and 1
     */
    public float cosineSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embeddings must have same dimension");
        }
        
        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }
        
        return dotProduct / (float)(Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Create attention mask (1 for real tokens, 0 for padding).
     */
    private long[] createAttentionMask(long[] inputIds) {
        long[] mask = new long[inputIds.length];
        for (int i = 0; i < inputIds.length; i++) {
            mask[i] = inputIds[i] != 0 ? 1 : 0; // 0 is padding token
        }
        return mask;
    }

    /**
     * Apply mean pooling over token embeddings.
     */
    private float[] meanPooling(float[][] tokenEmbeddings, long[] attentionMask) {
        int seqLength = tokenEmbeddings.length;
        int embeddingDim = tokenEmbeddings[0].length;
        
        float[] pooled = new float[embeddingDim];
        int validTokens = 0;
        
        for (int i = 0; i < seqLength; i++) {
            if (attentionMask[i] == 1) {
                for (int j = 0; j < embeddingDim; j++) {
                    pooled[j] += tokenEmbeddings[i][j];
                }
                validTokens++;
            }
        }
        
        // Average
        for (int j = 0; j < embeddingDim; j++) {
            pooled[j] /= validTokens;
        }
        
        return pooled;
    }

    /**
     * Normalize embedding vector to unit length.
     */
    private void normalize(float[] embedding) {
        float norm = 0.0f;
        for (float value : embedding) {
            norm += value * value;
        }
        norm = (float) Math.sqrt(norm);
        
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] /= norm;
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up ONNX resources");
        
        try {
            if (session != null) {
                session.close();
            }
            if (env != null) {
                env.close();
            }
        } catch (Exception e) {
            logger.error("Error cleaning up ONNX resources", e);
        }
    }

    /**
     * Simple tokenizer for demonstration.
     * In production, use a proper tokenizer library.
     */
    private static class SimpleTokenizer {
        private final Map<String, Integer> vocab;
        private static final int PAD_TOKEN_ID = 0;
        private static final int CLS_TOKEN_ID = 101;
        private static final int SEP_TOKEN_ID = 102;
        private static final int UNK_TOKEN_ID = 100;

        public SimpleTokenizer(String vocabPath) throws IOException {
            vocab = new HashMap<>();
            
            // Check if vocab file exists
            Path path = Paths.get(vocabPath);
            if (Files.exists(path)) {
                // Load vocab from file
                List<String> lines = Files.readAllLines(path);
                for (int i = 0; i < lines.size(); i++) {
                    vocab.put(lines.get(i).trim(), i);
                }
                logger.info("Loaded vocabulary with {} tokens", vocab.size());
            } else {
                // Use minimal vocab for demo
                logger.warn("Vocab file not found, using minimal vocabulary");
                buildMinimalVocab();
            }
        }

        private void buildMinimalVocab() {
            // Minimal vocabulary for testing
            vocab.put("[PAD]", PAD_TOKEN_ID);
            vocab.put("[CLS]", CLS_TOKEN_ID);
            vocab.put("[SEP]", SEP_TOKEN_ID);
            vocab.put("[UNK]", UNK_TOKEN_ID);
        }

        public long[] tokenize(String text, int maxLength) {
            // Simple whitespace tokenization
            String[] tokens = text.toLowerCase().split("\\s+");
            
            long[] inputIds = new long[maxLength];
            
            // [CLS] token
            inputIds[0] = CLS_TOKEN_ID;
            
            int pos = 1;
            for (String token : tokens) {
                if (pos >= maxLength - 1) break;
                
                inputIds[pos++] = vocab.getOrDefault(token, UNK_TOKEN_ID);
            }
            
            // [SEP] token
            if (pos < maxLength) {
                inputIds[pos++] = SEP_TOKEN_ID;
            }
            
            // Rest is padding (already 0)
            
            return inputIds;
        }
    }
}
```

### Example 8.2: Embedding Service Test

```java
package com.codetalkerl.firestick.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void generateEmbedding_ValidText_ReturnsEmbedding() throws Exception {
        // Given
        String text = "public class Calculator { public int add(int a, int b) { return a + b; } }";

        // When
        float[] embedding = embeddingService.generateEmbedding(text);

        // Then
        assertThat(embedding).isNotNull();
        assertThat(embedding).hasSize(384); // all-MiniLM-L6-v2 dimension
        
        // Check that embedding is normalized
        float norm = 0.0f;
        for (float value : embedding) {
            norm += value * value;
        }
        norm = (float) Math.sqrt(norm);
        assertThat(norm).isCloseTo(1.0f, within(0.01f));
    }

    @Test
    void generateEmbedding_DifferentTexts_ProducesDifferentEmbeddings() throws Exception {
        // Given
        String text1 = "Calculate the sum of two numbers";
        String text2 = "Process a string value";

        // When
        float[] embedding1 = embeddingService.generateEmbedding(text1);
        float[] embedding2 = embeddingService.generateEmbedding(text2);

        // Then
        assertThat(embedding1).isNotEqualTo(embedding2);
    }

    @Test
    void cosineSimilarity_SimilarTexts_HighSimilarity() throws Exception {
        // Given
        String text1 = "This is a test";
        String text2 = "This is a test";

        // When
        float[] embedding1 = embeddingService.generateEmbedding(text1);
        float[] embedding2 = embeddingService.generateEmbedding(text2);
        float similarity = embeddingService.cosineSimilarity(embedding1, embedding2);

        // Then
        assertThat(similarity).isCloseTo(1.0f, within(0.001f));
    }

    @Test
    void cosineSimilarity_DifferentTexts_LowerSimilarity() throws Exception {
        // Given
        String text1 = "Java programming language";
        String text2 = "Python web framework";

        // When
        float[] embedding1 = embeddingService.generateEmbedding(text1);
        float[] embedding2 = embeddingService.generateEmbedding(text2);
        float similarity = embeddingService.cosineSimilarity(embedding1, embedding2);

        // Then: Should be less similar than identical texts
        assertThat(similarity).isLessThan(1.0f);
    }

    @Test
    void generateEmbeddingsBatch_MultipleTexts_ReturnsAllEmbeddings() throws Exception {
        // Given
        List<String> texts = Arrays.asList(
            "First text",
            "Second text",
            "Third text"
        );

        // When
        List<float[]> embeddings = embeddingService.generateEmbeddingsBatch(texts);

        // Then
        assertThat(embeddings).hasSize(3);
        embeddings.forEach(embedding -> {
            assertThat(embedding).hasSize(384);
        });
    }
}
```

### Example 8.3: Downloading ONNX Model

**Option 1: From Hugging Face (Recommended)**

```powershell
# Create models directory
mkdir models
cd models

# Download using Python
pip install optimum[onnxruntime]

# Convert model to ONNX
python -c "
from optimum.onnxruntime import ORTModelForFeatureExtraction
from transformers import AutoTokenizer

model_id = 'sentence-transformers/all-MiniLM-L6-v2'

# Load and export model
model = ORTModelForFeatureExtraction.from_pretrained(model_id, export=True)
tokenizer = AutoTokenizer.from_pretrained(model_id)

# Save to current directory
model.save_pretrained('.')
tokenizer.save_pretrained('.')
"
```

**Option 2: Pre-converted Model**

```powershell
# Download from Xenova's pre-converted models
cd models

# Download model.onnx
Invoke-WebRequest -Uri "https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx" -OutFile "model.onnx"

# Download tokenizer files
Invoke-WebRequest -Uri "https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/main/tokenizer.json" -OutFile "tokenizer.json"
Invoke-WebRequest -Uri "https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/main/vocab.txt" -OutFile "vocab.txt"
```

### Example 8.4: Application Properties for Embeddings

```properties
# Embedding Model Configuration
embedding.model.path=models/model.onnx
embedding.vocab.path=models/vocab.txt
embedding.dimension=384
embedding.max.sequence.length=128
```

### Advice for Day 8

**ONNX Runtime Best Practices:**

1. **Load Model Once**: Create session in @PostConstruct, reuse for all requests
2. **Close Resources**: Always close tensors and sessions
3. **Handle Large Batches**: Process in smaller batches to avoid memory issues
4. **Cache Embeddings**: Don't regenerate for same text

**Common Issues:**

1. **"Model file not found"**
   - Check modelPath is correct
   - Ensure model.onnx is in the models/ directory
   - Use absolute path if relative path doesn't work

2. **OutOfMemoryError**
   - Increase JVM heap: `-Xmx4g`
   - Process smaller batches
   - Don't keep all embeddings in memory

3. **Slow performance**
   - ONNX Runtime uses CPU by default
   - For GPU acceleration, use `onnxruntime-gpu` dependency
   - Consider caching frequently used embeddings

4. **Wrong embedding dimension**
   - all-MiniLM-L6-v2 produces 384D vectors
   - Other models have different dimensions
   - Verify output shape matches expectations

**Testing Tips:**

```java
// Test with simple text first
String simpleText = "hello world";
float[] embedding = embeddingService.generateEmbedding(simpleText);

// Verify embedding properties
assertThat(embedding).hasSize(384);

// Check normalization (vector length should be ~1.0)
float norm = 0;
for (float v : embedding) norm += v * v;
assertThat(Math.sqrt(norm)).isCloseTo(1.0, within(0.01));

// Test similarity
float similarity = embeddingService.cosineSimilarity(embedding, embedding);
assertThat(similarity).isCloseTo(1.0f, within(0.001f));
```

---

---

# Phase 2: Code Indexing Engine (Weeks 3-4)

## Day 11: Project Cleanup & Planning

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
    public List<Path> scanDirectory(String rootPath) throws FileDiscoveryException {
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
        } catch (IOException e) {
            throw new FileDiscoveryException("Failed to scan directory: " + rootPath, e);
        } catch (SecurityException e) {
            throw new FileDiscoveryException("Access denied to directory: " + rootPath, e);
        }
        
        logger.info("Scan complete. Found {} Java files in {}", javaFiles.size(), rootPath);
        return javaFiles;
    }

    /**
     * Check if file has .java extension.
     */
    private boolean isJavaFile(Path path) {
        return path.toString().endsWith(".java");
    }

    /**
     * Check if file should be included based on exclusion patterns.
     */
    private boolean shouldInclude(Path path) {
        String pathStr = path.toString();
        
        // Check excluded directories
        for (String excludeDir : config.getExcludeDirectories()) {
            if (pathStr.contains(excludeDir)) {
                logger.trace("Excluding file in directory {}: {}", excludeDir, path);
                return false;
            }
        }
        
        // Check excluded patterns
        for (String pattern : config.getExcludePatterns()) {
            if (matchesPattern(pathStr, pattern)) {
                logger.trace("Excluding file matching pattern {}: {}", pattern, path);
                return false;
            }
        }
        
        return true;
    }

    /**
     * Simple pattern matching (supports * wildcard).
     */
    private boolean matchesPattern(String path, String pattern) {
        // Convert glob pattern to regex
        String regex = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .replace("**", ".*");
        
        return path.matches(regex);
    }

    /**
     * Get file count in directory without scanning.
     */
    public long estimateFileCount(String rootPath) throws FileDiscoveryException {
        Path root = Paths.get(rootPath);
        
        try (Stream<Path> pathStream = Files.walk(root)) {
            return pathStream
                .filter(Files::isRegularFile)
                .filter(this::isJavaFile)
                .count();
        } catch (IOException e) {
            throw new FileDiscoveryException("Failed to estimate file count", e);
        }
    }
}
```

### Example 12.2: IndexingConfig

```java
package com.codetalkerl.firestick.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration for indexing operations.
 */
@Configuration
@ConfigurationProperties(prefix = "indexing")
public class IndexingConfig {
    
    private List<String> fileExtensions = Arrays.asList(".java");
    private List<String> excludeDirectories = Arrays.asList(
        "target", "build", ".git", "node_modules", ".idea", "out"
    );
    private List<String> excludePatterns = new ArrayList<>();
    
    private int batchSize = 50;
    private int maxFileSize = 1024 * 1024; // 1MB
    private boolean skipTests = false;

    // Getters and setters
    
    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public List<String> getExcludeDirectories() {
        return excludeDirectories;
    }

    public void setExcludeDirectories(List<String> excludeDirectories) {
        this.excludeDirectories = excludeDirectories;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
    }
}
```

### Example 12.3: Application Properties for Indexing

```properties
# Indexing Configuration
indexing.file.extensions=.java
indexing.exclude.directories=target,build,.git,node_modules,.idea,out,bin
indexing.exclude.patterns=**/test/**,**/*Test.java,**/*Tests.java
indexing.batch-size=50
indexing.max-file-size=1048576
indexing.skip-tests=false
```

### Example 12.4: FileDiscoveryService Tests

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.config.IndexingConfig;
import com.codetalkerl.firestick.exception.FileDiscoveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDiscoveryServiceTest {

    @TempDir
    Path tempDir;

    private FileDiscoveryService fileDiscoveryService;
    private IndexingConfig config;

    @BeforeEach
    void setUp() {
        config = new IndexingConfig();
        fileDiscoveryService = new FileDiscoveryService(config);
    }

    @Test
    void scanDirectory_FindsJavaFiles() throws IOException {
        // Given: Create test directory structure
        createFile(tempDir.resolve("Test1.java"), "public class Test1 {}");
        createFile(tempDir.resolve("Test2.java"), "public class Test2 {}");
        createFile(tempDir.resolve("README.md"), "# README");

        // When
        List<Path> javaFiles = fileDiscoveryService.scanDirectory(tempDir.toString());

        // Then
        assertThat(javaFiles).hasSize(2);
        assertThat(javaFiles).allMatch(path -> path.toString().endsWith(".java"));
    }

    @Test
    void scanDirectory_RecursiveSearch_FindsNestedFiles() throws IOException {
        // Given: Create nested structure
        Path subDir = tempDir.resolve("com/example");
        Files.createDirectories(subDir);
        
        createFile(tempDir.resolve("Root.java"), "public class Root {}");
        createFile(subDir.resolve("Nested.java"), "public class Nested {}");

        // When
        List<Path> javaFiles = fileDiscoveryService.scanDirectory(tempDir.toString());

        // Then
        assertThat(javaFiles).hasSize(2);
    }

    @Test
    void scanDirectory_ExcludesTargetDirectory() throws IOException {
        // Given: Create files in target directory
        Path targetDir = tempDir.resolve("target/classes");
        Files.createDirectories(targetDir);
        
        createFile(tempDir.resolve("Source.java"), "public class Source {}");
        createFile(targetDir.resolve("Compiled.java"), "public class Compiled {}");

        // When
        List<Path> javaFiles = fileDiscoveryService.scanDirectory(tempDir.toString());

        // Then: Should only find Source.java, not files in target/
        assertThat(javaFiles).hasSize(1);
        assertThat(javaFiles.get(0).getFileName().toString()).isEqualTo("Source.java");
    }

    @Test
    void scanDirectory_InvalidPath_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> fileDiscoveryService.scanDirectory("/nonexistent/path"))
            .isInstanceOf(FileDiscoveryException.class)
            .hasMessageContaining("does not exist");
    }

    @Test
    void scanDirectory_FileInsteadOfDirectory_ThrowsException() throws IOException {
        // Given: Create a file
        Path file = tempDir.resolve("file.txt");
        Files.createFile(file);

        // When/Then
        assertThatThrownBy(() -> fileDiscoveryService.scanDirectory(file.toString()))
            .isInstanceOf(FileDiscoveryException.class)
            .hasMessageContaining("not a directory");
    }

    @Test
    void scanDirectory_EmptyDirectory_ReturnsEmptyList() {
        // When
        List<Path> javaFiles = fileDiscoveryService.scanDirectory(tempDir.toString());

        // Then
        assertThat(javaFiles).isEmpty();
    }

    @Test
    void estimateFileCount_ReturnsCorrectCount() throws IOException {
        // Given
        createFile(tempDir.resolve("Test1.java"), "public class Test1 {}");
        createFile(tempDir.resolve("Test2.java"), "public class Test2 {}");
        createFile(tempDir.resolve("Test3.java"), "public class Test3 {}");

        // When
        long count = fileDiscoveryService.estimateFileCount(tempDir.toString());

        // Then
        assertThat(count).isEqualTo(3);
    }

    private void createFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
}
```

---

## Day 13: Enhanced Code Parsing with JavaParser

### Example 13.1: Enhanced CodeParserService

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.ClassInfo;
import com.codetalkerl.firestick.dto.FileInfo;
import com.codetalkerl.firestick.dto.MethodInfo;
import com.codetalkerl.firestick.exception.CodeParsingException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enhanced service for parsing Java code and extracting comprehensive information.
 */
@Service
public class CodeParserService {
    private static final Logger logger = LoggerFactory.getLogger(CodeParserService.class);
    
    private final JavaParser javaParser;

    public CodeParserService() {
        this.javaParser = new JavaParser();
    }

    /**
     * Parse a Java file and extract comprehensive information.
     *
     * @param filePath Path to the Java file
     * @return FileInfo containing all extracted information
     * @throws CodeParsingException if parsing fails
     */
    public FileInfo parseFile(Path filePath) throws CodeParsingException {
        logger.debug("Parsing file: {}", filePath);
        
        if (!Files.exists(filePath)) {
            throw new CodeParsingException("File not found", filePath.toString());
        }
        
        try {
            String content = Files.readString(filePath);
            ParseResult<CompilationUnit> parseResult = javaParser.parse(content);
            
            if (!parseResult.isSuccessful()) {
                String errors = parseResult.getProblems().toString();
                throw new CodeParsingException("Failed to parse file: " + errors, filePath.toString());
            }
            
            CompilationUnit cu = parseResult.getResult()
                .orElseThrow(() -> new CodeParsingException("No compilation unit found", filePath.toString()));
            
            return extractFileInfo(cu, filePath);
            
        } catch (IOException e) {
            throw new CodeParsingException("Failed to read file", filePath.toString(), e);
        }
    }

    /**
     * Extract comprehensive file information from compilation unit.
     */
    private FileInfo extractFileInfo(CompilationUnit cu, Path filePath) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath(filePath.toString());
        fileInfo.setFileName(filePath.getFileName().toString());
        
        // Extract package
        cu.getPackageDeclaration().ifPresent(pkg -> 
            fileInfo.setPackageName(pkg.getNameAsString())
        );
        
        // Extract imports
        List<String> imports = cu.getImports().stream()
            .map(ImportDeclaration::getNameAsString)
            .collect(Collectors.toList());
        fileInfo.setImports(imports);
        
        // Extract static imports
        List<String> staticImports = cu.getImports().stream()
            .filter(ImportDeclaration::isStatic)
            .map(ImportDeclaration::getNameAsString)
            .collect(Collectors.toList());
        fileInfo.setStaticImports(staticImports);
        
        // Extract classes
        List<ClassInfo> classes = new ArrayList<>();
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            ClassInfo classInfo = extractClassInfo(classDecl, fileInfo);
            classes.add(classInfo);
        });
        fileInfo.setClasses(classes);
        
        logger.debug("Parsed file {} with {} classes", filePath.getFileName(), classes.size());
        return fileInfo;
    }

    /**
     * Extract comprehensive class information.
     */
    private ClassInfo extractClassInfo(ClassOrInterfaceDeclaration classDecl, FileInfo fileInfo) {
        ClassInfo classInfo = new ClassInfo();
        
        // Basic info
        classInfo.setName(classDecl.getNameAsString());
        classInfo.setFullyQualifiedName(
            fileInfo.getPackageName() != null 
                ? fileInfo.getPackageName() + "." + classDecl.getNameAsString()
                : classDecl.getNameAsString()
        );
        classInfo.setInterface(classDecl.isInterface());
        classInfo.setAbstract(classDecl.isAbstract());
        
        // Modifiers
        List<String> modifiers = classDecl.getModifiers().stream()
            .map(mod -> mod.getKeyword().asString())
            .collect(Collectors.toList());
        classInfo.setModifiers(modifiers);
        
        // Annotations
        List<String> annotations = classDecl.getAnnotations().stream()
            .map(anno -> anno.getNameAsString())
            .collect(Collectors.toList());
        classInfo.setAnnotations(annotations);
        
        // Inheritance
        classDecl.getExtendedTypes().forEach(ext -> 
            classInfo.setExtendsClass(ext.getNameAsString())
        );
        
        List<String> implementsList = classDecl.getImplementedTypes().stream()
            .map(impl -> impl.getNameAsString())
            .collect(Collectors.toList());
        classInfo.setImplementsInterfaces(implementsList);
        
        // JavaDoc
        classDecl.getJavadocComment().ifPresent(javadoc -> 
            classInfo.setJavadoc(javadoc.getContent())
        );
        
        // Line range
        classDecl.getRange().ifPresent(range -> {
            classInfo.setStartLine(range.begin.line);
            classInfo.setEndLine(range.end.line);
        });
        
        // Extract methods
        List<MethodInfo> methods = new ArrayList<>();
        classDecl.getMethods().forEach(methodDecl -> {
            MethodInfo methodInfo = extractMethodInfo(methodDecl, classInfo);
            methods.add(methodInfo);
        });
        classInfo.setMethods(methods);
        
        // Extract inner classes recursively
        List<ClassInfo> innerClasses = new ArrayList<>();
        classDecl.getChildNodes().stream()
            .filter(node -> node instanceof ClassOrInterfaceDeclaration)
            .map(node -> (ClassOrInterfaceDeclaration) node)
            .forEach(innerClassDecl -> {
                ClassInfo innerClassInfo = extractClassInfo(innerClassDecl, fileInfo);
                innerClasses.add(innerClassInfo);
            });
        classInfo.setInnerClasses(innerClasses);
        
        return classInfo;
    }

    /**
     * Extract comprehensive method information.
     */
    private MethodInfo extractMethodInfo(MethodDeclaration methodDecl, ClassInfo classInfo) {
        MethodInfo methodInfo = new MethodInfo();
        
        // Basic info
        methodInfo.setName(methodDecl.getNameAsString());
        methodInfo.setClassName(classInfo.getName());
        methodInfo.setFullyQualifiedClassName(classInfo.getFullyQualifiedName());
        
        // Return type
        methodInfo.setReturnType(methodDecl.getTypeAsString());
        
        // Parameters
        List<String> parameters = methodDecl.getParameters().stream()
            .map(param -> param.getTypeAsString() + " " + param.getNameAsString())
            .collect(Collectors.toList());
        methodInfo.setParameters(parameters);
        
        // Signature
        String signature = buildMethodSignature(methodDecl);
        methodInfo.setSignature(signature);
        
        // Modifiers
        List<String> modifiers = methodDecl.getModifiers().stream()
            .map(mod -> mod.getKeyword().asString())
            .collect(Collectors.toList());
        methodInfo.setModifiers(modifiers);
        
        // Annotations
        List<String> annotations = methodDecl.getAnnotations().stream()
            .map(anno -> anno.getNameAsString())
            .collect(Collectors.toList());
        methodInfo.setAnnotations(annotations);
        
        // JavaDoc
        methodDecl.getJavadocComment().ifPresent(javadoc -> 
            methodInfo.setJavadoc(javadoc.getContent())
        );
        
        // Body
        methodDecl.getBody().ifPresent(body -> 
            methodInfo.setBody(body.toString())
        );
        
        // Line range
        methodDecl.getRange().ifPresent(range -> {
            methodInfo.setStartLine(range.begin.line);
            methodInfo.setEndLine(range.end.line);
        });
        
        return methodInfo;
    }

    /**
     * Build method signature string.
     */
    private String buildMethodSignature(MethodDeclaration methodDecl) {
        StringBuilder signature = new StringBuilder();
        
        // Modifiers
        methodDecl.getModifiers().forEach(mod -> 
            signature.append(mod.getKeyword().asString()).append(" ")
        );
        
        // Return type
        signature.append(methodDecl.getTypeAsString()).append(" ");
        
        // Method name
        signature.append(methodDecl.getNameAsString());
        
        // Parameters
        signature.append("(");
        String params = methodDecl.getParameters().stream()
            .map(param -> param.getTypeAsString() + " " + param.getNameAsString())
            .collect(Collectors.joining(", "));
        signature.append(params);
        signature.append(")");
        
        return signature.toString();
    }

    /**
     * Parse Java code string (not from file).
     */
    public FileInfo parseCode(String code) throws CodeParsingException {
        ParseResult<CompilationUnit> parseResult = javaParser.parse(code);
        
        if (!parseResult.isSuccessful()) {
            String errors = parseResult.getProblems().toString();
            throw new CodeParsingException("Failed to parse code: " + errors, "<string>");
        }
        
        CompilationUnit cu = parseResult.getResult()
            .orElseThrow(() -> new CodeParsingException("No compilation unit found", "<string>"));
        
        return extractFileInfo(cu, Path.of("<string>"));
    }
}
```

### Example 13.2: Data Transfer Objects (DTOs)

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a parsed Java file.
 */
public class FileInfo {
    private String filePath;
    private String fileName;
    private String packageName;
    private List<String> imports = new ArrayList<>();
    private List<String> staticImports = new ArrayList<>();
    private List<ClassInfo> classes = new ArrayList<>();

    // Getters and setters
    
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<String> getStaticImports() {
        return staticImports;
    }

    public void setStaticImports(List<String> staticImports) {
        this.staticImports = staticImports;
    }

    public List<ClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassInfo> classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", classes=" + classes.size() +
                '}';
    }
}
```

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a parsed Java class.
 */
public class ClassInfo {
    private String name;
    private String fullyQualifiedName;
    private boolean isInterface;
    private boolean isAbstract;
    private List<String> modifiers = new ArrayList<>();
    private List<String> annotations = new ArrayList<>();
    private String extendsClass;
    private List<String> implementsInterfaces = new ArrayList<>();
    private String javadoc;
    private int startLine;
    private int endLine;
    private List<MethodInfo> methods = new ArrayList<>();
    private List<ClassInfo> innerClasses = new ArrayList<>();

    // Getters and setters
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public String getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(String extendsClass) {
        this.extendsClass = extendsClass;
    }

    public List<String> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public void setImplementsInterfaces(List<String> implementsInterfaces) {
        this.implementsInterfaces = implementsInterfaces;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
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

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

    public List<ClassInfo> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<ClassInfo> innerClasses) {
        this.innerClasses = innerClasses;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "name='" + name + '\'' +
                ", methods=" + methods.size() +
                ", innerClasses=" + innerClasses.size() +
                '}';
    }
}
```

```java
package com.codetalkerl.firestick.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a parsed Java method.
 */
public class MethodInfo {
    private String name;
    private String className;
    private String fullyQualifiedClassName;
    private String signature;
    private String returnType;
    private List<String> parameters = new ArrayList<>();
    private String body;
    private String javadoc;
    private int startLine;
    private int endLine;
    private List<String> modifiers = new ArrayList<>();
    private List<String> annotations = new ArrayList<>();

    // Getters and setters
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
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

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
```

### Example 13.3: AST Visitor Pattern for Custom Extraction

```java
package com.codetalkerl.firestick.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visitor to extract method call information from AST.
 * Tracks which methods are called from each method.
 */
public class MethodCallVisitor extends VoidVisitorAdapter<Map<String, List<String>>> {
    
    private String currentMethod = null;

    @Override
    public void visit(MethodDeclaration methodDecl, Map<String, List<String>> collector) {
        // Track current method context
        String previousMethod = currentMethod;
        currentMethod = methodDecl.getNameAsString();
        
        // Initialize list for this method if needed
        collector.putIfAbsent(currentMethod, new ArrayList<>());
        
        // Visit method body to find calls
        super.visit(methodDecl, collector);
        
        // Restore previous context
        currentMethod = previousMethod;
    }

    @Override
    public void visit(MethodCallExpr methodCall, Map<String, List<String>>> collector) {
        super.visit(methodCall, collector);
        
        if (currentMethod != null) {
            String calledMethod = methodCall.getNameAsString();
            
            // Add to collector
            collector.get(currentMethod).add(calledMethod);
        }
    }
}
```

**Usage Example:**

```java
// Parse file
CompilationUnit cu = javaParser.parse(code).getResult().get();

// Collect method calls
Map<String, List<String>> methodCalls = new HashMap<>();
MethodCallVisitor visitor = new MethodCallVisitor();
visitor.visit(cu, methodCalls);

// Results: methodCalls contains mapping of "methodName" -> ["calledMethod1", "calledMethod2", ...]
methodCalls.forEach((caller, callees) -> {
    System.out.println(caller + " calls: " + callees);
});
```

### Advice for Days 12-13

**JavaParser Best Practices:**

1. **Reuse JavaParser Instance**: Create once, use many times
2. **Check Parse Results**: Always verify `parseResult.isSuccessful()`
3. **Use Optionals Carefully**: Many JavaParser methods return `Optional`
4. **Handle Parse Errors Gracefully**: Don't fail entire indexing if one file fails

**Common AST Patterns:**

```java
// Find all method declarations
List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

// Find all class declarations
List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

// Find all method calls
List<MethodCallExpr> calls = cu.findAll(MethodCallExpr.class);

// Find all field declarations
List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);

// Get package
Optional<PackageDeclaration> pkg = cu.getPackageDeclaration();

// Get imports
List<ImportDeclaration> imports = cu.getImports();
```

**Troubleshooting:**

1. **Parse errors on valid Java code**
   - Ensure using correct JavaParser version
   - Check for exotic Java features
   - Try updating to newer parser version

2. **Missing information from AST**
   - Use JavaParser symbol solver for type resolution
   - Configure symbol solver with project classpath
   - Fall back gracefully if resolution fails

3. **Performance issues with large files**
   - Add timeout for parsing
   - Skip very large files (> 1MB)
   - Consider parsing in parallel

---

## Day 14: Code Chunking Strategy

### Example 14.1: CodeChunkingService

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.ClassInfo;
import com.codetalkerl.firestick.dto.CodeChunkDTO;
import com.codetalkerl.firestick.dto.FileInfo;
import com.codetalkerl.firestick.dto.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for chunking code into meaningful pieces for embedding and search.
 * Implements multiple chunking strategies:
 * - Method-level chunks (method with class context)
 * - Class-level chunks (class summary with key methods)
 * - File-level chunks (package and imports overview)
 */
@Service
public class CodeChunkingService {
    private static final Logger logger = LoggerFactory.getLogger(CodeChunkingService.class);
    
    // Chunk size limits (in characters, approximately 512 tokens)
    private static final int MAX_CHUNK_SIZE = 2000;
    private static final int IDEAL_CHUNK_SIZE = 1500;
    
    /**
     * Chunk a parsed file into searchable pieces.
     *
     * @param fileInfo Parsed file information
     * @return List of code chunks
     */
    public List<CodeChunkDTO> chunkFile(FileInfo fileInfo) {
        logger.debug("Chunking file: {}", fileInfo.getFileName());
        
        List<CodeChunkDTO> chunks = new ArrayList<>();
        
        // Create file-level chunk (package and imports overview)
        CodeChunkDTO fileChunk = createFileOverviewChunk(fileInfo);
        chunks.add(fileChunk);
        
        // Create chunks for each class
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            chunks.addAll(chunkClass(classInfo, fileInfo));
        }
        
        logger.debug("Created {} chunks from {}", chunks.size(), fileInfo.getFileName());
        return chunks;
    }

    /**
     * Create chunks for a class (class-level and method-level).
     */
    private List<CodeChunkDTO> chunkClass(ClassInfo classInfo, FileInfo fileInfo) {
        List<CodeChunkDTO> chunks = new ArrayList<>();
        
        // Create class-level chunk
        CodeChunkDTO classChunk = createClassChunk(classInfo, fileInfo);
        chunks.add(classChunk);
        
        // Create method-level chunks
        for (MethodInfo methodInfo : classInfo.getMethods()) {
            CodeChunkDTO methodChunk = createMethodChunk(methodInfo, classInfo, fileInfo);
            chunks.add(methodChunk);
        }
        
        // Process inner classes recursively
        for (ClassInfo innerClass : classInfo.getInnerClasses()) {
            chunks.addAll(chunkClass(innerClass, fileInfo));
        }
        
        return chunks;
    }

    /**
     * Create file overview chunk with package and imports.
     */
    private CodeChunkDTO createFileOverviewChunk(FileInfo fileInfo) {
        StringBuilder content = new StringBuilder();
        
        content.append("// File: ").append(fileInfo.getFileName()).append("\n");
        
        if (fileInfo.getPackageName() != null) {
            content.append("package ").append(fileInfo.getPackageName()).append(";\n\n");
        }
        
        // Add imports (limit to first 20 to avoid huge chunks)
        if (!fileInfo.getImports().isEmpty()) {
            content.append("// Imports:\n");
            int importCount = Math.min(fileInfo.getImports().size(), 20);
            for (int i = 0; i < importCount; i++) {
                content.append("import ").append(fileInfo.getImports().get(i)).append(";\n");
            }
            if (fileInfo.getImports().size() > 20) {
                content.append("// ... and ").append(fileInfo.getImports().size() - 20).append(" more imports\n");
            }
        }
        
        // Add class names
        content.append("\n// Classes in this file:\n");
        for (ClassInfo classInfo : fileInfo.getClasses()) {
            content.append("// - ").append(classInfo.getName());
            if (classInfo.isInterface()) {
                content.append(" (interface)");
            }
            content.append("\n");
        }
        
        return CodeChunkDTO.builder()
            .content(content.toString())
            .chunkType("FILE_OVERVIEW")
            .filePath(fileInfo.getFilePath())
            .fileName(fileInfo.getFileName())
            .packageName(fileInfo.getPackageName())
            .startLine(1)
            .endLine(1)
            .build();
    }

    /**
     * Create class-level chunk with class declaration and key methods.
     */
    private CodeChunkDTO createClassChunk(ClassInfo classInfo, FileInfo fileInfo) {
        StringBuilder content = new StringBuilder();
        
        // Add context header
        content.append("// File: ").append(fileInfo.getFileName()).append("\n");
        if (fileInfo.getPackageName() != null) {
            content.append("// Package: ").append(fileInfo.getPackageName()).append("\n");
        }
        content.append("\n");
        
        // Add JavaDoc if present
        if (classInfo.getJavadoc() != null && !classInfo.getJavadoc().isEmpty()) {
            content.append("/**\n");
            content.append(classInfo.getJavadoc());
            content.append("\n */\n");
        }
        
        // Add annotations
        for (String annotation : classInfo.getAnnotations()) {
            content.append("@").append(annotation).append("\n");
        }
        
        // Add class declaration
        for (String modifier : classInfo.getModifiers()) {
            content.append(modifier).append(" ");
        }
        
        if (classInfo.isInterface()) {
            content.append("interface ");
        } else {
            content.append("class ");
        }
        
        content.append(classInfo.getName());
        
        // Add extends/implements
        if (classInfo.getExtendsClass() != null) {
            content.append(" extends ").append(classInfo.getExtendsClass());
        }
        
        if (!classInfo.getImplementsInterfaces().isEmpty()) {
            content.append(" implements ");
            content.append(String.join(", ", classInfo.getImplementsInterfaces()));
        }
        
        content.append(" {\n");
        
        // Add public method signatures (not full implementations)
        content.append("\n    // Public methods:\n");
        for (MethodInfo method : classInfo.getMethods()) {
            if (method.getModifiers().contains("public")) {
                content.append("    ").append(method.getSignature()).append(";\n");
            }
        }
        
        content.append("}\n");
        
        return CodeChunkDTO.builder()
            .content(content.toString())
            .chunkType("CLASS")
            .filePath(fileInfo.getFilePath())
            .fileName(fileInfo.getFileName())
            .packageName(fileInfo.getPackageName())
            .className(classInfo.getName())
            .fullyQualifiedClassName(classInfo.getFullyQualifiedName())
            .startLine(classInfo.getStartLine())
            .endLine(classInfo.getEndLine())
            .build();
    }

    /**
     * Create method-level chunk with full implementation and context.
     */
    private CodeChunkDTO createMethodChunk(MethodInfo methodInfo, ClassInfo classInfo, FileInfo fileInfo) {
        StringBuilder content = new StringBuilder();
        
        // Add context header
        content.append("// File: ").append(fileInfo.getFileName()).append("\n");
        if (fileInfo.getPackageName() != null) {
            content.append("// Package: ").append(fileInfo.getPackageName()).append("\n");
        }
        content.append("// Class: ").append(classInfo.getName()).append("\n");
        
        // Add class inheritance context if relevant
        if (classInfo.getExtendsClass() != null) {
            content.append("// Extends: ").append(classInfo.getExtendsClass()).append("\n");
        }
        if (!classInfo.getImplementsInterfaces().isEmpty()) {
            content.append("// Implements: ").append(String.join(", ", classInfo.getImplementsInterfaces())).append("\n");
        }
        
        content.append("\n");
        
        // Add JavaDoc if present
        if (methodInfo.getJavadoc() != null && !methodInfo.getJavadoc().isEmpty()) {
            content.append("/**\n");
            content.append(methodInfo.getJavadoc());
            content.append("\n */\n");
        }
        
        // Add annotations
        for (String annotation : methodInfo.getAnnotations()) {
            content.append("@").append(annotation).append("\n");
        }
        
        // Add method signature and body
        content.append(methodInfo.getSignature());
        
        if (methodInfo.getBody() != null) {
            content.append(" ");
            content.append(methodInfo.getBody());
        } else {
            content.append(";"); // Abstract or interface method
        }
        
        // Check if chunk is too large
        String chunkContent = content.toString();
        if (chunkContent.length() > MAX_CHUNK_SIZE) {
            chunkContent = truncateChunk(chunkContent, methodInfo);
        }
        
        return CodeChunkDTO.builder()
            .content(chunkContent)
            .chunkType("METHOD")
            .filePath(fileInfo.getFilePath())
            .fileName(fileInfo.getFileName())
            .packageName(fileInfo.getPackageName())
            .className(classInfo.getName())
            .fullyQualifiedClassName(classInfo.getFullyQualifiedName())
            .methodName(methodInfo.getName())
            .methodSignature(methodInfo.getSignature())
            .startLine(methodInfo.getStartLine())
            .endLine(methodInfo.getEndLine())
            .build();
    }

    /**
     * Truncate very large chunks while preserving important information.
     */
    private String truncateChunk(String content, MethodInfo methodInfo) {
        logger.warn("Method {} is too large ({}), truncating", 
            methodInfo.getSignature(), content.length());
        
        // Keep header and signature, truncate body
        int bodyStart = content.indexOf("{");
        if (bodyStart == -1) {
            // No body, just truncate
            return content.substring(0, MAX_CHUNK_SIZE) + "\n// ... (truncated)";
        }
        
        String header = content.substring(0, bodyStart + 1);
        String truncatedBody = content.substring(bodyStart + 1, 
            Math.min(content.length(), IDEAL_CHUNK_SIZE - header.length()));
        
        return header + "\n    // ... (method body truncated due to size)\n}";
    }

    /**
     * Count approximate tokens in text (rough estimation).
     * Real tokenization would use the actual tokenizer.
     */
    private int estimateTokens(String text) {
        // Rough approximation: 1 token ≈ 4 characters
        return text.length() / 4;
    }

    /**
     * Split a very large chunk into multiple smaller chunks.
     * Used for extremely large methods.
     */
    private List<CodeChunkDTO> splitLargeChunk(CodeChunkDTO largeChunk) {
        List<CodeChunkDTO> chunks = new ArrayList<>();
        
        String content = largeChunk.getContent();
        int chunkIndex = 0;
        
        while (content.length() > MAX_CHUNK_SIZE) {
            String part = content.substring(0, MAX_CHUNK_SIZE);
            
            CodeChunkDTO chunk = CodeChunkDTO.builder()
                .content(part + "\n// ... (continued)")
                .chunkType(largeChunk.getChunkType() + "_PART_" + chunkIndex)
                .filePath(largeChunk.getFilePath())
                .fileName(largeChunk.getFileName())
                .packageName(largeChunk.getPackageName())
                .className(largeChunk.getClassName())
                .methodName(largeChunk.getMethodName())
                .startLine(largeChunk.getStartLine())
                .endLine(largeChunk.getEndLine())
                .build();
            
            chunks.add(chunk);
            content = content.substring(MAX_CHUNK_SIZE);
            chunkIndex++;
        }
        
        // Add final part
        if (!content.isEmpty()) {
            CodeChunkDTO finalChunk = CodeChunkDTO.builder()
                .content("// ... (continuation)\n" + content)
                .chunkType(largeChunk.getChunkType() + "_PART_" + chunkIndex)
                .filePath(largeChunk.getFilePath())
                .fileName(largeChunk.getFileName())
                .packageName(largeChunk.getPackageName())
                .className(largeChunk.getClassName())
                .methodName(largeChunk.getMethodName())
                .startLine(largeChunk.getStartLine())
                .endLine(largeChunk.getEndLine())
                .build();
            
            chunks.add(finalChunk);
        }
        
        return chunks;
    }
}
```

### Example 14.2: CodeChunkDTO

```java
package com.codetalkerl.firestick.dto;

/**
 * Data Transfer Object for code chunks.
 * Represents a piece of code ready for embedding and indexing.
 */
public class CodeChunkDTO {
    private String content;
    private String chunkType; // FILE_OVERVIEW, CLASS, METHOD
    private String filePath;
    private String fileName;
    private String packageName;
    private String className;
    private String fullyQualifiedClassName;
    private String methodName;
    private String methodSignature;
    private int startLine;
    private int endLine;
    private float[] embedding; // Set after embedding generation

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChunkType() {
        return chunkType;
    }

    public void setChunkType(String chunkType) {
        this.chunkType = chunkType;
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

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "CodeChunkDTO{" +
                "chunkType='" + chunkType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", lines=" + startLine + "-" + endLine +
                '}';
    }

    // Builder class
    public static class Builder {
        private final CodeChunkDTO chunk = new CodeChunkDTO();

        public Builder content(String content) {
            chunk.content = content;
            return this;
        }

        public Builder chunkType(String chunkType) {
            chunk.chunkType = chunkType;
            return this;
        }

        public Builder filePath(String filePath) {
            chunk.filePath = filePath;
            return this;
        }

        public Builder fileName(String fileName) {
            chunk.fileName = fileName;
            return this;
        }

        public Builder packageName(String packageName) {
            chunk.packageName = packageName;
            return this;
        }

        public Builder className(String className) {
            chunk.className = className;
            return this;
        }

        public Builder fullyQualifiedClassName(String fullyQualifiedClassName) {
            chunk.fullyQualifiedClassName = fullyQualifiedClassName;
            return this;
        }

        public Builder methodName(String methodName) {
            chunk.methodName = methodName;
            return this;
        }

        public Builder methodSignature(String methodSignature) {
            chunk.methodSignature = methodSignature;
            return this;
        }

        public Builder startLine(int startLine) {
            chunk.startLine = startLine;
            return this;
        }

        public Builder endLine(int endLine) {
            chunk.endLine = endLine;
            return this;
        }

        public Builder embedding(float[] embedding) {
            chunk.embedding = embedding;
            return this;
        }

        public CodeChunkDTO build() {
            return chunk;
        }
    }
}
```

### Example 14.3: CodeChunkingService Tests

```java
package com.codetalkerl.firestick.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeChunkingServiceTest {

    private CodeChunkingService chunkingService;

    @BeforeEach
    void setUp() {
        chunkingService = new CodeChunkingService();
    }

    @Test
    void chunkFile_SimpleClass_CreatesMultipleChunks() {
        // Given: FileInfo with one class and one method
        FileInfo fileInfo = createSimpleFileInfo();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Should have file overview, class chunk, and method chunk
        assertThat(chunks).hasSizeGreaterThanOrEqualTo(3);
        assertThat(chunks).anyMatch(c -> c.getChunkType().equals("FILE_OVERVIEW"));
        assertThat(chunks).anyMatch(c -> c.getChunkType().equals("CLASS"));
        assertThat(chunks).anyMatch(c -> c.getChunkType().equals("METHOD"));
    }

    @Test
    void chunkFile_FileOverviewChunk_ContainsPackageAndImports() {
        // Given
        FileInfo fileInfo = createSimpleFileInfo();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: File overview should contain package and imports
        CodeChunkDTO fileChunk = chunks.stream()
                .filter(c -> c.getChunkType().equals("FILE_OVERVIEW"))
                .findFirst()
                .orElseThrow();

        assertThat(fileChunk.getContent()).contains("package com.example");
        assertThat(fileChunk.getContent()).contains("import");
    }

    @Test
    void chunkFile_ClassChunk_ContainsClassDeclaration() {
        // Given
        FileInfo fileInfo = createSimpleFileInfo();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Class chunk should have class declaration
        CodeChunkDTO classChunk = chunks.stream()
                .filter(c -> c.getChunkType().equals("CLASS"))
                .findFirst()
                .orElseThrow();

        assertThat(classChunk.getContent()).contains("public class Calculator");
        assertThat(classChunk.getClassName()).isEqualTo("Calculator");
    }

    @Test
    void chunkFile_MethodChunk_ContainsFullMethodWithContext() {
        // Given
        FileInfo fileInfo = createSimpleFileInfo();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Method chunk should have context and full method
        CodeChunkDTO methodChunk = chunks.stream()
                .filter(c -> c.getChunkType().equals("METHOD"))
                .findFirst()
                .orElseThrow();

        assertThat(methodChunk.getContent()).contains("// Class: Calculator");
        assertThat(methodChunk.getContent()).contains("public int add");
        assertThat(methodChunk.getContent()).contains("return a + b");
        assertThat(methodChunk.getMethodName()).isEqualTo("add");
    }

    @Test
    void chunkFile_MethodWithJavaDoc_IncludesJavaDoc() {
        // Given
        FileInfo fileInfo = createFileInfoWithJavaDoc();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Method chunk should include JavaDoc
        CodeChunkDTO methodChunk = chunks.stream()
                .filter(c -> c.getChunkType().equals("METHOD"))
                .findFirst()
                .orElseThrow();

        assertThat(methodChunk.getContent()).contains("/**");
        assertThat(methodChunk.getContent()).contains("Adds two numbers");
    }

    @Test
    void chunkFile_MultipleClasses_CreatesChunksForEach() {
        // Given: File with two classes
        FileInfo fileInfo = createFileWithMultipleClasses();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Should have chunks for both classes
        long classChunks = chunks.stream()
                .filter(c -> c.getChunkType().equals("CLASS"))
                .count();

        assertThat(classChunks).isEqualTo(2);
    }

    @Test
    void chunkFile_ClassWithInnerClass_ProcessesRecursively() {
        // Given: Class with inner class
        FileInfo fileInfo = createFileWithInnerClass();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: Should have chunks for outer and inner classes
        assertThat(chunks.stream()
                .filter(c -> c.getChunkType().equals("CLASS"))
                .count()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void chunkFile_ChunkMetadata_IsComplete() {
        // Given
        FileInfo fileInfo = createSimpleFileInfo();

        // When
        List<CodeChunkDTO> chunks = chunkingService.chunkFile(fileInfo);

        // Then: All chunks should have complete metadata
        chunks.forEach(chunk -> {
            assertThat(chunk.getFilePath()).isNotNull();
            assertThat(chunk.getFileName()).isNotNull();
            assertThat(chunk.getChunkType()).isNotNull();
            assertThat(chunk.getStartLine()).isGreaterThan(0);
        });
    }

    // Helper methods to create test data

    private FileInfo createSimpleFileInfo() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilePath("/src/Calculator.java");
        fileInfo.setFileName("Calculator.java");
        fileInfo.setPackageName("com.example");
        fileInfo.setImports(Arrays.asList("java.util.List", "java.util.ArrayList"));

        ClassInfo classInfo = new ClassInfo();
        classInfo.setName("Calculator");
        classInfo.setFullyQualifiedName("com.example.Calculator");
        classInfo.setModifiers(Arrays.asList("public"));
        classInfo.setStartLine(5);
        classInfo.setEndLine(15);

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName("add");
        methodInfo.setSignature("public int add(int a, int b)");
        methodInfo.setReturnType("int");
        methodInfo.setParameters(Arrays.asList("int a", "int b"));
        methodInfo.setBody("{\n    return a + b;\n}");
        methodInfo.setModifiers(Arrays.asList("public"));
        methodInfo.setStartLine(10);
        methodInfo.setEndLine(12);
        methodInfo.setClassName("Calculator");

        classInfo.setMethods(Arrays.asList(methodInfo));
        fileInfo.setClasses(Arrays.asList(classInfo));

        return fileInfo;
    }

    private FileInfo createFileInfoWithJavaDoc() {
        FileInfo fileInfo = createSimpleFileInfo();

        MethodInfo method = fileInfo.getClasses().get(0).getMethods().get(0);
        method.setJavadoc(" * Adds two numbers together.\n * @param a first number\n * @param b second number\n * @return sum");

        return fileInfo;
    }

    private FileInfo createFileWithMultipleClasses() {
        FileInfo fileInfo = createSimpleFileInfo();

        ClassInfo secondClass = new ClassInfo();
        secondClass.setName("Multiplier");
        secondClass.setFullyQualifiedName("com.example.Multiplier");
        secondClass.setModifiers(Arrays.asList("public"));
        secondClass.setStartLine(20);
        secondClass.setEndLine(30);

        fileInfo.getClasses().add(secondClass);

        return fileInfo;
    }

    private FileInfo createFileWithInnerClass() {
        FileInfo fileInfo = createSimpleFileInfo();

        ClassInfo innerClass = new ClassInfo();
        innerClass.setName("InnerHelper");
        innerClass.setFullyQualifiedName("com.example.Calculator.InnerHelper");
        innerClass.setModifiers(Arrays.asList("private", "static"));
        innerClass.setStartLine(16);
        innerClass.setEndLine(20);

        fileInfo.getClasses().get(0).setInnerClasses(Arrays.asList(innerClass));

        return fileInfo;
    }
}
```

### Example 14.4: Chunking Strategy Examples

**Example: Method Chunk Output**

```java
// File: Calculator.java
// Package: com.example.math
// Class: Calculator

/**
 * Adds two integers and returns the sum.
 * @param a first number
 * @param b second number
 * @return the sum of a and b
 */
@Override
public int add(int a, int b) {
    return a + b;
}
```

**Example: Class Chunk Output**

```java
// File: Calculator.java
// Package: com.example.math

/**
 * A simple calculator for basic arithmetic operations.
 */
@Service
public class Calculator {

    // Public methods:
    public int add(int a, int b);
    public int subtract(int a, int b);
    public int multiply(int a, int b);
    public double divide(int a, int b);
}
```

**Example: File Overview Chunk Output**

```java
// File: Calculator.java
package com.example.math;

// Imports:
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

// Classes in this file:
// - Calculator
```

### Example 14.5: Advanced Chunking Strategies

```java
/**
 * Alternative chunking strategy: Semantic blocks.
 * Groups related methods together.
 */
public class SemanticChunkingService {
    
    /**
     * Chunk methods by semantic similarity.
     * Groups CRUD operations, helpers, validators, etc.
     */
    public List<CodeChunkDTO> chunkBySemantics(ClassInfo classInfo, FileInfo fileInfo) {
        List<CodeChunkDTO> chunks = new ArrayList<>();
        
        // Group methods by prefix/pattern
        Map<String, List<MethodInfo>> methodGroups = new HashMap<>();
        methodGroups.put("get", new ArrayList<>());
        methodGroups.put("set", new ArrayList<>());
        methodGroups.put("is", new ArrayList<>());
        methodGroups.put("validate", new ArrayList<>());
        methodGroups.put("calculate", new ArrayList<>());
        methodGroups.put("other", new ArrayList<>());
        
        for (MethodInfo method : classInfo.getMethods()) {
            String name = method.getName().toLowerCase();
            boolean categorized = false;
            
            for (String prefix : methodGroups.keySet()) {
                if (name.startsWith(prefix)) {
                    methodGroups.get(prefix).add(method);
                    categorized = true;
                    break;
                }
            }
            
            if (!categorized) {
                methodGroups.get("other").add(method);
            }
        }
        
        // Create chunks for each group
        for (Map.Entry<String, List<MethodInfo>> entry : methodGroups.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                CodeChunkDTO chunk = createSemanticGroupChunk(
                    entry.getKey(), entry.getValue(), classInfo, fileInfo);
                chunks.add(chunk);
            }
        }
        
        return chunks;
    }
    
    private CodeChunkDTO createSemanticGroupChunk(
            String groupName, 
            List<MethodInfo> methods, 
            ClassInfo classInfo, 
            FileInfo fileInfo) {
        
        StringBuilder content = new StringBuilder();
        content.append("// Semantic group: ").append(groupName).append(" methods\n");
        content.append("// Class: ").append(classInfo.getName()).append("\n\n");
        
        for (MethodInfo method : methods) {
            content.append(method.getSignature()).append(" { ... }\n");
        }
        
        return CodeChunkDTO.builder()
            .content(content.toString())
            .chunkType("SEMANTIC_GROUP_" + groupName.toUpperCase())
            .className(classInfo.getName())
            .build();
    }
}
```

### Advice for Day 14

**Chunking Best Practices:**

1. **Include Context**: Always add file, package, and class context to method chunks
2. **Preserve JavaDoc**: Documentation is crucial for semantic search
3. **Size Limits**: Keep chunks under 2000 characters (~512 tokens)
4. **Metadata**: Store rich metadata for filtering and display
5. **Multiple Strategies**: Different chunk types serve different search purposes

**Chunking Trade-offs:**

| Strategy | Pros | Cons |
|----------|------|------|
| Method-level | Fine-grained search, precise results | More chunks to process |
| Class-level | Good overview, fewer chunks | Less detail, might miss specific code |
| File-level | Shows structure | Not useful for code search |
| Semantic groups | Related code together | Complex grouping logic |

**Common Chunking Patterns:**

```java
// Pattern 1: Method with minimal context
public int add(int a, int b) {
    return a + b;
}

// Pattern 2: Method with class context (RECOMMENDED)
// Class: Calculator
// Package: com.example
public int add(int a, int b) {
    return a + b;
}

// Pattern 3: Method with full context including inheritance
// Class: ScientificCalculator extends Calculator
// Package: com.example.advanced
// Implements: AdvancedOperations
public int add(int a, int b) {
    return a + b;
}
```

**Handling Edge Cases:**

```java
// Very long methods
if (methodBody.length() > MAX_CHUNK_SIZE) {
    // Option 1: Truncate with note
    String truncated = methodBody.substring(0, MAX_CHUNK_SIZE);
    chunk.setContent(truncated + "\n// ... (truncated)");
    
    // Option 2: Split into multiple chunks
    List<CodeChunkDTO> parts = splitIntoMultipleChunks(method);
}

// Abstract methods (no body)
if (method.getBody() == null) {
    chunk.setContent(method.getSignature() + ";");
}

// Constructor methods
if (method.getName().equals(classInfo.getName())) {
    chunk.setChunkType("CONSTRUCTOR");
}

// Inner classes
if (classInfo.getInnerClasses() != null) {
    for (ClassInfo inner : classInfo.getInnerClasses()) {
        chunks.addAll(chunkClass(inner, fileInfo));
    }
}
```

**Token Estimation:**

```java
// Simple estimation (1 token ≈ 4 characters)
int estimatedTokens = text.length() / 4;

// More accurate: use actual tokenizer
// (would require integrating the same tokenizer as embedding model)
```

**Optimization Tips:**

1. **Batch Processing**: Process multiple files before chunking
2. **Lazy Chunking**: Only chunk when needed, not during parse
3. **Cache Chunks**: Store chunks to avoid re-chunking unchanged files
4. **Parallel Chunking**: Chunk multiple classes in parallel

---

## Day 15-16: Dependency Graph Building with JGraphT

### Example 15.1: Enhanced DependencyGraphService

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.dto.ClassInfo;
import com.codetalkerl.firestick.dto.FileInfo;
import com.codetalkerl.firestick.dto.MethodInfo;
import com.codetalkerl.firestick.model.ClassNode;
import com.codetalkerl.firestick.model.DependencyEdge;
import com.codetalkerl.firestick.model.EdgeType;
import com.codetalkerl.firestick.model.MethodNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced service for building and managing code dependency graphs.
 * Tracks relationships between classes and methods including:
 * - Inheritance (extends)
 * - Interface implementation (implements)
 * - Import dependencies
 * - Method calls
 */
@Service
public class DependencyGraphService {
    private static final Logger logger = LoggerFactory.getLogger(DependencyGraphService.class);
    
    private Graph<Object, DependencyEdge> dependencyGraph;
    
    // Indexes for fast lookup
    private Map<String, ClassNode> classIndex;
    private Map<String, MethodNode> methodIndex;

    public DependencyGraphService() {
        this.dependencyGraph = new DefaultDirectedGraph<>(DependencyEdge.class);
        this.classIndex = new HashMap<>();
        this.methodIndex = new HashMap<>();
        logger.info("DependencyGraphService initialized");
    }

    /**
     * Build dependency graph from parsed files.
     *
     * @param files List of parsed file information
     */
    public void buildFromParsedFiles(List<FileInfo> files) {
        logger.info("Building dependency graph from {} files", files.size());
        
        // Clear existing graph
        clearGraph();
        
        // First pass: Add all classes and methods as vertices
        for (FileInfo file : files) {
            addVerticesFromFile(file);
        }
        
        logger.debug("Added {} vertices to graph", dependencyGraph.vertexSet().size());
        
        // Second pass: Add dependency edges
        for (FileInfo file : files) {
            addDependencyEdges(file);
        }
        
        logger.info("Built graph with {} vertices and {} edges", 
            dependencyGraph.vertexSet().size(), 
            dependencyGraph.edgeSet().size());
        
        // Log statistics
        logGraphStatistics();
    }

    /**
     * Add all classes and methods from a file as vertices.
     */
    private void addVerticesFromFile(FileInfo file) {
        for (ClassInfo classInfo : file.getClasses()) {
            addClassVertex(classInfo, file);
        }
    }

    /**
     * Add a class and its methods as vertices.
     */
    private void addClassVertex(ClassInfo classInfo, FileInfo file) {
        // Create class node
        ClassNode classNode = new ClassNode(
            classInfo.getName(),
            classInfo.getFullyQualifiedName(),
            file.getPackageName(),
            file.getFilePath(),
            classInfo.isInterface()
        );
        
        // Add to graph and index
        dependencyGraph.addVertex(classNode);
        classIndex.put(classInfo.getFullyQualifiedName(), classNode);
        
        logger.trace("Added class vertex: {}", classInfo.getFullyQualifiedName());
        
        // Add all methods as vertices
        for (MethodInfo methodInfo : classInfo.getMethods()) {
            addMethodVertex(methodInfo, classNode);
        }
        
        // Process inner classes recursively
        for (ClassInfo innerClass : classInfo.getInnerClasses()) {
            addClassVertex(innerClass, file);
        }
    }

    /**
     * Add a method as a vertex.
     */
    private void addMethodVertex(MethodInfo methodInfo, ClassNode parentClass) {
        MethodNode methodNode = new MethodNode(
            methodInfo.getName(),
            methodInfo.getSignature(),
            parentClass.getFullyQualifiedName(),
            methodInfo.getStartLine(),
            methodInfo.getEndLine()
        );
        
        // Add to graph and index
        dependencyGraph.addVertex(methodNode);
        String methodKey = parentClass.getFullyQualifiedName() + "." + methodInfo.getSignature();
        methodIndex.put(methodKey, methodNode);
        
        // Add edge: class contains method
        DependencyEdge containsEdge = new DependencyEdge(EdgeType.CONTAINS);
        dependencyGraph.addEdge(parentClass, methodNode, containsEdge);
        
        logger.trace("Added method vertex: {}", methodInfo.getSignature());
    }

    /**
     * Add all dependency edges from a file.
     */
    private void addDependencyEdges(FileInfo file) {
        for (ClassInfo classInfo : file.getClasses()) {
            addInheritanceEdges(classInfo);
            addImplementsEdges(classInfo);
            addImportEdges(classInfo, file);
            
            // Process inner classes
            for (ClassInfo innerClass : classInfo.getInnerClasses()) {
                addInheritanceEdges(innerClass);
                addImplementsEdges(innerClass);
                addImportEdges(innerClass, file);
            }
        }
    }

    /**
     * Add edges for class inheritance (extends).
     */
    private void addInheritanceEdges(ClassInfo classInfo) {
        if (classInfo.getExtendsClass() == null) {
            return;
        }
        
        ClassNode sourceClass = classIndex.get(classInfo.getFullyQualifiedName());
        if (sourceClass == null) {
            logger.warn("Source class not found in index: {}", classInfo.getFullyQualifiedName());
            return;
        }
        
        // Try to find parent class in index
        ClassNode parentClass = findClassByName(classInfo.getExtendsClass());
        
        if (parentClass != null) {
            DependencyEdge edge = new DependencyEdge(EdgeType.EXTENDS);
            dependencyGraph.addEdge(sourceClass, parentClass, edge);
            logger.trace("Added EXTENDS edge: {} -> {}", 
                sourceClass.getName(), parentClass.getName());
        } else {
            logger.debug("Parent class not in graph: {}", classInfo.getExtendsClass());
        }
    }

    /**
     * Add edges for interface implementation (implements).
     */
    private void addImplementsEdges(ClassInfo classInfo) {
        if (classInfo.getImplementsInterfaces().isEmpty()) {
            return;
        }
        
        ClassNode sourceClass = classIndex.get(classInfo.getFullyQualifiedName());
        if (sourceClass == null) {
            return;
        }
        
        for (String interfaceName : classInfo.getImplementsInterfaces()) {
            ClassNode interfaceClass = findClassByName(interfaceName);
            
            if (interfaceClass != null) {
                DependencyEdge edge = new DependencyEdge(EdgeType.IMPLEMENTS);
                dependencyGraph.addEdge(sourceClass, interfaceClass, edge);
                logger.trace("Added IMPLEMENTS edge: {} -> {}", 
                    sourceClass.getName(), interfaceClass.getName());
            }
        }
    }

    /**
     * Add edges for import dependencies.
     */
    private void addImportEdges(ClassInfo classInfo, FileInfo file) {
        ClassNode sourceClass = classIndex.get(classInfo.getFullyQualifiedName());
        if (sourceClass == null) {
            return;
        }
        
        for (String importName : file.getImports()) {
            // Check if import is a class in our graph
            ClassNode importedClass = classIndex.get(importName);
            
            if (importedClass != null) {
                DependencyEdge edge = new DependencyEdge(EdgeType.IMPORTS);
                
                // Only add if edge doesn't exist
                if (!dependencyGraph.containsEdge(sourceClass, importedClass)) {
                    dependencyGraph.addEdge(sourceClass, importedClass, edge);
                    logger.trace("Added IMPORTS edge: {} -> {}", 
                        sourceClass.getName(), importedClass.getName());
                }
            }
        }
    }

    /**
     * Add method call edges (requires method call analysis).
     */
    public void addMethodCallEdges(Map<String, List<String>> methodCalls, ClassInfo classInfo) {
        for (Map.Entry<String, List<String>> entry : methodCalls.entrySet()) {
            String callerMethod = entry.getKey();
            List<String> calledMethods = entry.getValue();
            
            // Find caller method node
            String callerKey = classInfo.getFullyQualifiedName() + "." + callerMethod;
            MethodNode callerNode = methodIndex.get(callerKey);
            
            if (callerNode == null) {
                logger.debug("Caller method not found: {}", callerKey);
                continue;
            }
            
            // Add edges to called methods
            for (String calledMethod : calledMethods) {
                // Try to find called method in same class first
                String calleeKey = classInfo.getFullyQualifiedName() + "." + calledMethod;
                MethodNode calleeNode = methodIndex.get(calleeKey);
                
                if (calleeNode != null) {
                    DependencyEdge edge = new DependencyEdge(EdgeType.CALLS);
                    
                    if (!dependencyGraph.containsEdge(callerNode, calleeNode)) {
                        dependencyGraph.addEdge(callerNode, calleeNode, edge);
                        logger.trace("Added CALLS edge: {} -> {}", callerMethod, calledMethod);
                    }
                }
            }
        }
    }

    /**
     * Find a class by simple name or fully qualified name.
     */
    private ClassNode findClassByName(String className) {
        // Try exact match first (fully qualified)
        ClassNode node = classIndex.get(className);
        if (node != null) {
            return node;
        }
        
        // Try partial match (simple name)
        for (Map.Entry<String, ClassNode> entry : classIndex.entrySet()) {
            if (entry.getKey().endsWith("." + className) || 
                entry.getValue().getName().equals(className)) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    /**
     * Get all direct dependencies of a class.
     */
    public Set<ClassNode> getClassDependencies(String fullyQualifiedName) {
        ClassNode classNode = classIndex.get(fullyQualifiedName);
        if (classNode == null) {
            return Collections.emptySet();
        }
        
        return dependencyGraph.outgoingEdgesOf(classNode).stream()
            .map(dependencyGraph::getEdgeTarget)
            .filter(node -> node instanceof ClassNode)
            .map(node -> (ClassNode) node)
            .collect(Collectors.toSet());
    }

    /**
     * Get all classes that depend on a given class.
     */
    public Set<ClassNode> getClassDependents(String fullyQualifiedName) {
        ClassNode classNode = classIndex.get(fullyQualifiedName);
        if (classNode == null) {
            return Collections.emptySet();
        }
        
        return dependencyGraph.incomingEdgesOf(classNode).stream()
            .map(dependencyGraph::getEdgeSource)
            .filter(node -> node instanceof ClassNode)
            .map(node -> (ClassNode) node)
            .collect(Collectors.toSet());
    }

    /**
     * Get methods that a method calls.
     */
    public Set<MethodNode> getMethodCallees(MethodNode method) {
        return dependencyGraph.outgoingEdgesOf(method).stream()
            .filter(edge -> edge.getType() == EdgeType.CALLS)
            .map(dependencyGraph::getEdgeTarget)
            .filter(node -> node instanceof MethodNode)
            .map(node -> (MethodNode) node)
            .collect(Collectors.toSet());
    }

    /**
     * Get methods that call a given method.
     */
    public Set<MethodNode> getMethodCallers(MethodNode method) {
        return dependencyGraph.incomingEdgesOf(method).stream()
            .filter(edge -> edge.getType() == EdgeType.CALLS)
            .map(dependencyGraph::getEdgeSource)
            .filter(node -> node instanceof MethodNode)
            .map(node -> (MethodNode) node)
            .collect(Collectors.toSet());
    }

    /**
     * Detect circular dependencies in the graph.
     */
    public Set<Object> detectCircularDependencies() {
        CycleDetector<Object, DependencyEdge> cycleDetector = 
            new CycleDetector<>(dependencyGraph);
        
        if (cycleDetector.detectCycles()) {
            Set<Object> cyclicNodes = cycleDetector.findCycles();
            logger.warn("Detected {} nodes involved in circular dependencies", cyclicNodes.size());
            return cyclicNodes;
        }
        
        return Collections.emptySet();
    }

    /**
     * Find shortest dependency path between two classes.
     */
    public List<Object> findDependencyPath(String fromClass, String toClass) {
        ClassNode source = classIndex.get(fromClass);
        ClassNode target = classIndex.get(toClass);
        
        if (source == null || target == null) {
            return Collections.emptyList();
        }
        
        DijkstraShortestPath<Object, DependencyEdge> pathFinder = 
            new DijkstraShortestPath<>(dependencyGraph);
        
        var path = pathFinder.getPath(source, target);
        
        if (path == null) {
            return Collections.emptyList();
        }
        
        return path.getVertexList();
    }

    /**
     * Get graph statistics.
     */
    public Map<String, Object> getGraphStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long classCount = dependencyGraph.vertexSet().stream()
            .filter(v -> v instanceof ClassNode)
            .count();
        
        long methodCount = dependencyGraph.vertexSet().stream()
            .filter(v -> v instanceof MethodNode)
            .count();
        
        Map<EdgeType, Long> edgesByType = dependencyGraph.edgeSet().stream()
            .collect(Collectors.groupingBy(
                DependencyEdge::getType, 
                Collectors.counting()
            ));
        
        stats.put("totalVertices", dependencyGraph.vertexSet().size());
        stats.put("classCount", classCount);
        stats.put("methodCount", methodCount);
        stats.put("totalEdges", dependencyGraph.edgeSet().size());
        stats.put("edgesByType", edgesByType);
        stats.put("hasCycles", new CycleDetector<>(dependencyGraph).detectCycles());
        
        return stats;
    }

    /**
     * Clear the graph.
     */
    public void clearGraph() {
        dependencyGraph = new DefaultDirectedGraph<>(DependencyEdge.class);
        classIndex.clear();
        methodIndex.clear();
        logger.debug("Graph cleared");
    }

    /**
     * Log graph statistics.
     */
    private void logGraphStatistics() {
        Map<String, Object> stats = getGraphStatistics();
        logger.info("Graph Statistics:");
        logger.info("  Classes: {}", stats.get("classCount"));
        logger.info("  Methods: {}", stats.get("methodCount"));
        logger.info("  Total Edges: {}", stats.get("totalEdges"));
        logger.info("  Has Cycles: {}", stats.get("hasCycles"));
        
        @SuppressWarnings("unchecked")
        Map<EdgeType, Long> edgesByType = (Map<EdgeType, Long>) stats.get("edgesByType");
        edgesByType.forEach((type, count) -> 
            logger.info("  {} edges: {}", type, count)
        );
    }

    // Getters for testing
    public Graph<Object, DependencyEdge> getGraph() {
        return dependencyGraph;
    }

    public Map<String, ClassNode> getClassIndex() {
        return classIndex;
    }
}
```

### Example 15.2: Graph Node Classes

```java
package com.codetalkerl.firestick.model;

import java.util.Objects;

/**
 * Represents a class in the dependency graph.
 */
public class ClassNode {
    private final String name;
    private final String fullyQualifiedName;
    private final String packageName;
    private final String filePath;
    private final boolean isInterface;

    public ClassNode(String name, String fullyQualifiedName, String packageName, 
                     String filePath, boolean isInterface) {
        this.name = name;
        this.fullyQualifiedName = fullyQualifiedName;
        this.packageName = packageName;
        this.filePath = filePath;
        this.isInterface = isInterface;
    }

    public String getName() {
        return name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassNode classNode = (ClassNode) o;
        return Objects.equals(fullyQualifiedName, classNode.fullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedName);
    }

    @Override
    public String toString() {
        return "ClassNode{" + fullyQualifiedName + "}";
    }
}
```

```java
package com.codetalkerl.firestick.model;

import java.util.Objects;

/**
 * Represents a method in the dependency graph.
 */
public class MethodNode {
    private final String name;
    private final String signature;
    private final String className;
    private final int startLine;
    private final int endLine;

    public MethodNode(String name, String signature, String className, 
                      int startLine, int endLine) {
        this.name = name;
        this.signature = signature;
        this.className = className;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getClassName() {
        return className;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getFullyQualifiedName() {
        return className + "." + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodNode that = (MethodNode) o;
        return Objects.equals(signature, that.signature) &&
               Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, className);
    }

    @Override
    public String toString() {
        return "MethodNode{" + className + "." + signature + "}";
    }
}
```

```java
package com.codetalkerl.firestick.model;

/**
 * Types of edges in the dependency graph.
 */
public enum EdgeType {
    CONTAINS,    // Class contains method
    EXTENDS,     // Class extends another class
    IMPLEMENTS,  // Class implements interface
    IMPORTS,     // Class imports another class
    CALLS        // Method calls another method
}
```

```java
package com.codetalkerl.firestick.model;

import org.jgrapht.graph.DefaultEdge;

/**
 * Edge representing a dependency relationship in the graph.
 */
public class DependencyEdge extends DefaultEdge {
    private final EdgeType type;
    private int weight;

    public DependencyEdge(EdgeType type) {
        this.type = type;
        this.weight = 1;
    }

    public DependencyEdge(EdgeType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public EdgeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
```

### Example 15.3: Method Call Analysis with JavaParser

```java
package com.codetalkerl.firestick.visitor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * JavaParser visitor to extract method call relationships.
 * Tracks which methods are called from each method.
 */
public class MethodCallVisitor extends VoidVisitorAdapter<Map<String, List<String>>> {
    private static final Logger logger = LoggerFactory.getLogger(MethodCallVisitor.class);
    
    private String currentMethod = null;
    private final Stack<String> methodStack = new Stack<>();

    @Override
    public void visit(MethodDeclaration methodDecl, Map<String, List<String>> collector) {
        // Build method signature
        String methodSignature = buildMethodSignature(methodDecl);
        
        // Track current method context
        String previousMethod = currentMethod;
        currentMethod = methodSignature;
        methodStack.push(methodSignature);
        
        // Initialize list for this method if needed
        collector.putIfAbsent(methodSignature, new ArrayList<>());
        
        logger.trace("Visiting method: {}", methodSignature);
        
        // Visit method body to find calls
        super.visit(methodDecl, collector);
        
        // Restore previous context
        methodStack.pop();
        currentMethod = previousMethod;
    }

    @Override
    public void visit(MethodCallExpr methodCall, Map<String, List<String>> collector) {
        super.visit(methodCall, collector);
        
        if (currentMethod != null) {
            String calledMethod = methodCall.getNameAsString();
            
            // Try to get scope (object or class being called)
            String scope = null;
            if (methodCall.getScope().isPresent()) {
                scope = methodCall.getScope().get().toString();
            }
            
            // Build full method call reference
            String fullCall = scope != null ? scope + "." + calledMethod : calledMethod;
            
            // Add to collector
            List<String> calls = collector.get(currentMethod);
            if (!calls.contains(fullCall)) {
                calls.add(fullCall);
                logger.trace("  {} calls {}", currentMethod, fullCall);
            }
        }
    }

    /**
     * Build method signature string.
     */
    private String buildMethodSignature(MethodDeclaration methodDecl) {
        StringBuilder signature = new StringBuilder();
        
        // Modifiers
        methodDecl.getModifiers().forEach(mod -> 
            signature.append(mod.getKeyword().asString()).append(" ")
        );
        
        // Return type
        signature.append(methodDecl.getTypeAsString()).append(" ");
        
        // Method name
        signature.append(methodDecl.getNameAsString());
        
        // Parameters
        signature.append("(");
        String params = methodDecl.getParameters().stream()
            .map(param -> param.getTypeAsString() + " " + param.getNameAsString())
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        signature.append(params);
        signature.append(")");
        
        return signature.toString();
    }
}
```

### Example 15.4: Enhanced CodeParserService with Method Calls

```java
/**
 * Parse file and extract method call information.
 */
public MethodCallInfo parseFileWithMethodCalls(Path filePath) throws CodeParsingException {
    logger.debug("Parsing file with method calls: {}", filePath);
    
    // First, parse normally to get file info
    FileInfo fileInfo = parseFile(filePath);
    
    // Then, analyze method calls
    try {
        String content = Files.readString(filePath);
        ParseResult<CompilationUnit> parseResult = javaParser.parse(content);
        
        if (!parseResult.isSuccessful()) {
            throw new CodeParsingException("Failed to parse file", filePath.toString());
        }
        
        CompilationUnit cu = parseResult.getResult().orElseThrow();
        
        // Extract method calls
        Map<String, List<String>> methodCalls = new HashMap<>();
        MethodCallVisitor visitor = new MethodCallVisitor();
        visitor.visit(cu, methodCalls);
        
        return new MethodCallInfo(fileInfo, methodCalls);
        
    } catch (IOException e) {
        throw new CodeParsingException("Failed to read file", filePath.toString(), e);
    }
}

/**
 * Container for file info and method call info.
 */
public static class MethodCallInfo {
    private final FileInfo fileInfo;
    private final Map<String, List<String>> methodCalls;
    
    public MethodCallInfo(FileInfo fileInfo, Map<String, List<String>> methodCalls) {
        this.fileInfo = fileInfo;
        this.methodCalls = methodCalls;
    }
    
    public FileInfo getFileInfo() {
        return fileInfo;
    }
    
    public Map<String, List<String>> getMethodCalls() {
        return methodCalls;
    }
}
```

### Example 15.5: Integration Example

```java
/**
 * Example of building complete dependency graph.
 */
public class GraphBuildingExample {
    
    public void buildCompleteGraph(List<Path> javaFiles) {
        CodeParserService parser = new CodeParserService();
        DependencyGraphService graphService = new DependencyGraphService();
        
        // Step 1: Parse all files
        List<FileInfo> parsedFiles = new ArrayList<>();
        Map<FileInfo, Map<String, List<String>>> allMethodCalls = new HashMap<>();
        
        for (Path file : javaFiles) {
            try {
                // Parse with method call analysis
                var methodCallInfo = parser.parseFileWithMethodCalls(file);
                parsedFiles.add(methodCallInfo.getFileInfo());
                allMethodCalls.put(methodCallInfo.getFileInfo(), methodCallInfo.getMethodCalls());
            } catch (CodeParsingException e) {
                logger.error("Failed to parse file: {}", file, e);
            }
        }
        
        // Step 2: Build graph structure (classes, methods, inheritance, imports)
        graphService.buildFromParsedFiles(parsedFiles);
        
        // Step 3: Add method call edges
        for (Map.Entry<FileInfo, Map<String, List<String>>> entry : allMethodCalls.entrySet()) {
            FileInfo fileInfo = entry.getKey();
            Map<String, List<String>> methodCalls = entry.getValue();
            
            for (ClassInfo classInfo : fileInfo.getClasses()) {
                graphService.addMethodCallEdges(methodCalls, classInfo);
            }
        }
        
        // Step 4: Analyze graph
        Map<String, Object> stats = graphService.getGraphStatistics();
        logger.info("Graph built successfully:");
        logger.info("  Classes: {}", stats.get("classCount"));
        logger.info("  Methods: {}", stats.get("methodCount"));
        logger.info("  Edges: {}", stats.get("totalEdges"));
        
        // Check for circular dependencies
        Set<Object> cycles = graphService.detectCircularDependencies();
        if (!cycles.isEmpty()) {
            logger.warn("Found circular dependencies involving {} nodes", cycles.size());
        }
    }
}
```

### Example 15.6: Graph Query Examples

```java
/**
 * Example queries on the dependency graph.
 */
public class GraphQueryExamples {
    
    private final DependencyGraphService graphService;
    
    public GraphQueryExamples(DependencyGraphService graphService) {
        this.graphService = graphService;
    }
    
    /**
     * Find all classes that a class depends on.
     */
    public void findClassDependencies(String className) {
        Set<ClassNode> dependencies = graphService.getClassDependencies(className);
        
        System.out.println(className + " depends on:");
        dependencies.forEach(dep -> 
            System.out.println("  - " + dep.getFullyQualifiedName())
        );
    }
    
    /**
     * Find all classes that depend on a given class (reverse dependencies).
     */
    public void findClassDependents(String className) {
        Set<ClassNode> dependents = graphService.getClassDependents(className);
        
        System.out.println("Classes that depend on " + className + ":");
        dependents.forEach(dep -> 
            System.out.println("  - " + dep.getFullyQualifiedName())
        );
    }
    
    /**
     * Find dependency path between two classes.
     */
    public void findDependencyPath(String fromClass, String toClass) {
        List<Object> path = graphService.findDependencyPath(fromClass, toClass);
        
        if (path.isEmpty()) {
            System.out.println("No dependency path found");
            return;
        }
        
        System.out.println("Dependency path:");
        for (int i = 0; i < path.size(); i++) {
            System.out.println("  " + i + ". " + path.get(i));
        }
    }
    
    /**
     * Analyze circular dependencies.
     */
    public void analyzeCircularDependencies() {
        Set<Object> cycles = graphService.detectCircularDependencies();
        
        if (cycles.isEmpty()) {
            System.out.println("No circular dependencies found!");
            return;
        }
        
        System.out.println("Warning: Found circular dependencies");
        System.out.println("Nodes involved in cycles:");
        cycles.forEach(node -> {
            if (node instanceof ClassNode) {
                ClassNode classNode = (ClassNode) node;
                System.out.println("  - Class: " + classNode.getFullyQualifiedName());
            }
        });
    }
}
```

### Example 15.7: DependencyGraphService Tests

```java
package com.codetalkerl.firestick.service;

import com.codetalker.firestick.service.DependencyGraphService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyGraphServiceTest {

    private DependencyGraphService graphService;

    @BeforeEach
    void setUp() {
        graphService = new DependencyGraphService();
    }

    @Test
    void buildFromParsedFiles_CreatesVertices() {
        // Given
        List<FileInfo> files = createSampleFiles();

        // When
        graphService.buildFromParsedFiles(files);

        // Then
        assertThat(graphService.getGraph().vertexSet()).isNotEmpty();
        assertThat(graphService.getClassIndex()).hasSize(2);
    }

    @Test
    void buildFromParsedFiles_CreatesInheritanceEdges() {
        // Given: Child class extends Parent class
        List<FileInfo> files = createFilesWithInheritance();

        // When
        graphService.buildFromParsedFiles(files);

        // Then: Should have EXTENDS edge
        long extendsEdges = graphService.getGraph().edgeSet().stream()
                .filter(e -> e.getType() == EdgeType.EXTENDS)
                .count();

        assertThat(extendsEdges).isGreaterThan(0);
    }

    @Test
    void buildFromParsedFiles_CreatesImplementsEdges() {
        // Given: Class implements interface
        List<FileInfo> files = createFilesWithInterfaces();

        // When
        graphService.buildFromParsedFiles(files);

        // Then: Should have IMPLEMENTS edge
        long implementsEdges = graphService.getGraph().edgeSet().stream()
                .filter(e -> e.getType() == EdgeType.IMPLEMENTS)
                .count();

        assertThat(implementsEdges).isGreaterThan(0);
    }

    @Test
    void getClassDependencies_ReturnsDependentClasses() {
        // Given
        List<FileInfo> files = createFilesWithInheritance();
        graphService.buildFromParsedFiles(files);

        // When
        Set<ClassNode> dependencies = graphService.getClassDependencies("com.example.Child");

        // Then
        assertThat(dependencies).isNotEmpty();
        assertThat(dependencies).anyMatch(node ->
                node.getFullyQualifiedName().equals("com.example.Parent")
        );
    }

    @Test
    void getClassDependents_ReturnsReverseDependencies() {
        // Given
        List<FileInfo> files = createFilesWithInheritance();
        graphService.buildFromParsedFiles(files);

        // When
        Set<ClassNode> dependents = graphService.getClassDependents("com.example.Parent");

        // Then
        assertThat(dependents).isNotEmpty();
        assertThat(dependents).anyMatch(node ->
                node.getFullyQualifiedName().equals("com.example.Child")
        );
    }

    @Test
    void detectCircularDependencies_NoCircles_ReturnsEmpty() {
        // Given: Simple hierarchy with no cycles
        List<FileInfo> files = createSampleFiles();
        graphService.buildFromParsedFiles(files);

        // When
        Set<Object> cycles = graphService.detectCircularDependencies();

        // Then
        assertThat(cycles).isEmpty();
    }

    @Test
    void findDependencyPath_PathExists_ReturnsPath() {
        // Given
        List<FileInfo> files = createFilesWithInheritance();
        graphService.buildFromParsedFiles(files);

        // When
        List<Object> path = graphService.findDependencyPath("com.example.Child", "com.example.Parent");

        // Then
        assertThat(path).isNotEmpty();
        assertThat(path).hasSize(2);
    }

    @Test
    void getGraphStatistics_ReturnsCorrectCounts() {
        // Given
        List<FileInfo> files = createSampleFiles();
        graphService.buildFromParsedFiles(files);

        // When
        Map<String, Object> stats = graphService.getGraphStatistics();

        // Then
        assertThat(stats).containsKeys("classCount", "methodCount", "totalEdges");
        assertThat((Long) stats.get("classCount")).isGreaterThan(0);
    }

    // Helper methods

    private List<FileInfo> createSampleFiles() {
        FileInfo file1 = new FileInfo();
        file1.setFileName("ClassA.java");
        file1.setPackageName("com.example");

        ClassInfo classA = new ClassInfo();
        classA.setName("ClassA");
        classA.setFullyQualifiedName("com.example.ClassA");

        MethodInfo method = new MethodInfo();
        method.setName("doSomething");
        method.setSignature("public void doSomething()");
        classA.setMethods(List.of(method));

        file1.setClasses(List.of(classA));

        FileInfo file2 = new FileInfo();
        file2.setFileName("ClassB.java");
        file2.setPackageName("com.example");

        ClassInfo classB = new ClassInfo();
        classB.setName("ClassB");
        classB.setFullyQualifiedName("com.example.ClassB");
        file2.setClasses(List.of(classB));

        return List.of(file1, file2);
    }

    private List<FileInfo> createFilesWithInheritance() {
        FileInfo parentFile = new FileInfo();
        parentFile.setFileName("Parent.java");
        parentFile.setPackageName("com.example");

        ClassInfo parent = new ClassInfo();
        parent.setName("Parent");
        parent.setFullyQualifiedName("com.example.Parent");
        parentFile.setClasses(List.of(parent));

        FileInfo childFile = new FileInfo();
        childFile.setFileName("Child.java");
        childFile.setPackageName("com.example");

        ClassInfo child = new ClassInfo();
        child.setName("Child");
        child.setFullyQualifiedName("com.example.Child");
        child.setExtendsClass("com.example.Parent");
        childFile.setClasses(List.of(child));

        return List.of(parentFile, childFile);
    }

    private List<FileInfo> createFilesWithInterfaces() {
        FileInfo interfaceFile = new FileInfo();
        interfaceFile.setFileName("MyInterface.java");
        interfaceFile.setPackageName("com.example");

        ClassInfo myInterface = new ClassInfo();
        myInterface.setName("MyInterface");
        myInterface.setFullyQualifiedName("com.example.MyInterface");
        myInterface.setInterface(true);
        interfaceFile.setClasses(List.of(myInterface));

        FileInfo implFile = new FileInfo();
        implFile.setFileName("MyImpl.java");
        implFile.setPackageName("com.example");

        ClassInfo impl = new ClassInfo();
        impl.setName("MyImpl");
        impl.setFullyQualifiedName("com.example.MyImpl");
        impl.setImplementsInterfaces(List.of("com.example.MyInterface"));
        implFile.setClasses(List.of(impl));

        return List.of(interfaceFile, implFile);
    }
}
```

### Advice for Days 15-16

**JGraphT Best Practices:**

1. **Use Directed Graphs**: Dependencies are directional (A depends on B ≠ B depends on A)
2. **Create Indexes**: Maintain maps for fast node lookup by name
3. **Edge Types**: Use custom edge class to distinguish relationship types
4. **Immutable Nodes**: Make node classes immutable with proper equals/hashCode
5. **Performance**: For large graphs (10,000+ nodes), consider:
   - Graph databases (Neo4j)
   - Lazy loading
   - Caching query results

**Dependency Analysis Patterns:**

```java
// Find all classes a class depends on (direct + transitive)
BreadthFirstIterator<Object, DependencyEdge> iterator = 
    new BreadthFirstIterator<>(graph, startNode);
Set<ClassNode> allDependencies = new HashSet<>();
iterator.forEachRemaining(node -> {
    if (node instanceof ClassNode) {
        allDependencies.add((ClassNode) node);
    }
});

// Find strongly connected components (circular dependency groups)
StrongConnectivityAlgorithm<Object, DependencyEdge> scAlg = 
    new KosarajuStrongConnectivityInspector<>(graph);
List<Set<Object>> components = scAlg.stronglyConnectedSets();

// Topological sort (for build order)
TopologicalOrderIterator<Object, DependencyEdge> topoIterator = 
    new TopologicalOrderIterator<>(graph);
List<Object> buildOrder = new ArrayList<>();
topoIterator.forEachRemaining(buildOrder::add);
```

**Method Call Analysis Tips:**

1. **Scope Resolution**: Track which class a method belongs to
2. **Qualified Calls**: Handle `object.method()` vs `method()`
3. **Static Imports**: Can make calls ambiguous
4. **Overloaded Methods**: Same name, different signatures
5. **Lambda Expressions**: May contain hidden method calls

**Troubleshooting:**

1. **Missing edges**: Check that both classes are in the graph
2. **Duplicate nodes**: Ensure proper equals/hashCode implementation
3. **Performance issues**: Add indexes, limit graph traversal depth
4. **Circular dependencies**: Use CycleDetector to identify and log

---

## Day 17: Batch Embedding Generation

### Example 17.1: Enhanced EmbeddingService with Batching

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.exception.EmbeddingException;
import ai.onnxruntime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * Enhanced embedding service with batch processing and caching.
 */
@Service
public class EmbeddingService {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
    
    @Value("${embedding.batch.size:32}")
    private int batchSize;
    
    @Value("${embedding.cache.size:10000}")
    private int cacheSize;
    
    private OrtEnvironment env;
    private OrtSession session;
    
    // LRU cache for embeddings
    private final Map<String, float[]> embeddingCache = 
        Collections.synchronizedMap(new LinkedHashMap<String, float[]>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, float[]> eldest) {
                return size() > cacheSize;
            }
        });
    
    private int cacheHits = 0;
    private int cacheMisses = 0;

    @PostConstruct
    public void init() throws OrtException {
        // Initialize ONNX (see Day 8 example)
        logger.info("Embedding service initialized with batch size: {}", batchSize);
    }

    /**
     * Generate embeddings for multiple texts in batches.
     *
     * @param texts List of texts to embed
     * @return List of embedding vectors
     */
    public List<float[]> generateBatchEmbeddings(List<String> texts) throws EmbeddingException {
        if (texts.isEmpty()) {
            return Collections.emptyList();
        }
        
        logger.info("Generating embeddings for {} texts", texts.size());
        long startTime = System.currentTimeMillis();
        
        List<float[]> embeddings = new ArrayList<>(texts.size());
        
        // Process in batches
        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);
            
            List<float[]> batchEmbeddings = processBatch(batch);
            embeddings.addAll(batchEmbeddings);
            
            if ((i / batchSize) % 10 == 0) {
                logger.debug("Processed {}/{} texts ({} batches)", 
                    end, texts.size(), (i / batchSize) + 1);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        double rate = texts.size() / (duration / 1000.0);
        
        logger.info("Generated {} embeddings in {}ms ({:.2f} embeddings/sec)", 
            texts.size(), duration, rate);
        logger.debug("Cache stats - Hits: {}, Misses: {}, Hit rate: {:.2f}%",
            cacheHits, cacheMisses, 
            cacheHits * 100.0 / (cacheHits + cacheMisses));
        
        return embeddings;
    }

    /**
     * Process a single batch of texts.
     */
    private List<float[]> processBatch(List<String> batch) throws EmbeddingException {
        List<float[]> results = new ArrayList<>();
        List<String> uncachedTexts = new ArrayList<>();
        List<Integer> uncachedIndexes = new ArrayList<>();
        
        // Check cache first
        for (int i = 0; i < batch.size(); i++) {
            String text = batch.get(i);
            String hash = hashText(text);
            
            if (embeddingCache.containsKey(hash)) {
                results.add(embeddingCache.get(hash));
                cacheHits++;
            } else {
                results.add(null); // Placeholder
                uncachedTexts.add(text);
                uncachedIndexes.add(i);
                cacheMisses++;
            }
        }
        
        // Generate embeddings for uncached texts
        if (!uncachedTexts.isEmpty()) {
            try {
                List<float[]> newEmbeddings = generateWithONNX(uncachedTexts);
                
                // Store in cache and results
                for (int i = 0; i < uncachedTexts.size(); i++) {
                    String text = uncachedTexts.get(i);
                    float[] embedding = newEmbeddings.get(i);
                    int resultIndex = uncachedIndexes.get(i);
                    
                    String hash = hashText(text);
                    embeddingCache.put(hash, embedding);
                    results.set(resultIndex, embedding);
                }
            } catch (OrtException e) {
                throw new EmbeddingException("Failed to generate embeddings", e);
            }
        }
        
        return results;
    }

    /**
     * Generate embeddings using ONNX Runtime.
     */
    private List<float[]> generateWithONNX(List<String> texts) throws OrtException {
        // Implementation from Day 8
        // For each text: tokenize, create tensors, run inference, extract embeddings
        List<float[]> embeddings = new ArrayList<>();
        
        for (String text : texts) {
            float[] embedding = generateSingleEmbedding(text);
            embeddings.add(embedding);
        }
        
        return embeddings;
    }

    /**
     * Generate single embedding (from Day 8 implementation).
     */
    private float[] generateSingleEmbedding(String text) throws OrtException {
        // See Day 8 example for full implementation
        // Returns 384-dimensional vector
        return new float[384];
    }

    /**
     * Hash text for cache key.
     */
    private String hashText(String text) {
        return String.valueOf(text.hashCode());
    }

    /**
     * Clear the cache.
     */
    public void clearCache() {
        embeddingCache.clear();
        cacheHits = 0;
        cacheMisses = 0;
        logger.info("Embedding cache cleared");
    }

    /**
     * Get cache statistics.
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", embeddingCache.size());
        stats.put("maxCacheSize", cacheSize);
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        stats.put("hitRate", cacheHits * 100.0 / Math.max(1, cacheHits + cacheMisses));
        return stats;
    }
}
```

---

## Day 18: Persist Graph to Database

### Example 18.1: Graph JPA Entities

```java
package com.codetalkerl.firestick.entity;

import jakarta.persistence.*;

/**
 * Entity for storing graph nodes in database.
 */
@Entity
@Table(name = "graph_nodes", indexes = {
    @Index(name = "idx_node_fqn", columnList = "fullyQualifiedName"),
    @Index(name = "idx_node_type", columnList = "nodeType")
})
public class GraphNodeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String nodeType; // CLASS, METHOD, PACKAGE
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, unique = true, length = 512)
    private String fullyQualifiedName;
    
    @Column(length = 255)
    private String packageName;
    
    @Column(length = 512)
    private String filePath;
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string with additional data
    
    @Column
    private Boolean isInterface;
    
    @Column
    private Integer startLine;
    
    @Column
    private Integer endLine;

    // Getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsInterface() {
        return isInterface;
    }

    public void setIsInterface(Boolean isInterface) {
        this.isInterface = isInterface;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }
}
```

```java
package com.codetalkerl.firestick.entity;

import jakarta.persistence.*;

/**
 * Entity for storing graph edges in database.
 */
@Entity
@Table(name = "graph_edges", indexes = {
    @Index(name = "idx_edge_from", columnList = "fromNodeId"),
    @Index(name = "idx_edge_to", columnList = "toNodeId"),
    @Index(name = "idx_edge_type", columnList = "edgeType")
})
public class GraphEdgeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromNodeId", nullable = false)
    private GraphNodeEntity fromNode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toNodeId", nullable = false)
    private GraphNodeEntity toNode;
    
    @Column(nullable = false, length = 50)
    private String edgeType; // EXTENDS, IMPLEMENTS, CALLS, IMPORTS, CONTAINS
    
    @Column
    private Integer weight;

    // Getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GraphNodeEntity getFromNode() {
        return fromNode;
    }

    public void setFromNode(GraphNodeEntity fromNode) {
        this.fromNode = fromNode;
    }

    public GraphNodeEntity getToNode() {
        return toNode;
    }

    public void setToNode(GraphNodeEntity toNode) {
        this.toNode = toNode;
    }

    public String getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
```

### Example 18.2: Graph Repositories

```java
package com.codetalkerl.firestick.repository;

import com.codetalkerl.firestick.entity.GraphNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GraphNodeRepository extends JpaRepository<GraphNodeEntity, Long> {
    
    Optional<GraphNodeEntity> findByFullyQualifiedName(String fullyQualifiedName);
    
    List<GraphNodeEntity> findByNodeType(String nodeType);
    
    List<GraphNodeEntity> findByPackageName(String packageName);
    
    @Query("SELECT n FROM GraphNodeEntity n WHERE n.nodeType = 'CLASS'")
    List<GraphNodeEntity> findAllClasses();
    
    @Query("SELECT n FROM GraphNodeEntity n WHERE n.nodeType = 'METHOD'")
    List<GraphNodeEntity> findAllMethods();
}
```

```java
package com.codetalkerl.firestick.repository;

import com.codetalkerl.firestick.entity.GraphEdgeEntity;
import com.codetalkerl.firestick.entity.GraphNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphEdgeRepository extends JpaRepository<GraphEdgeEntity, Long> {
    
    List<GraphEdgeEntity> findByFromNode(GraphNodeEntity fromNode);
    
    List<GraphEdgeEntity> findByToNode(GraphNodeEntity toNode);
    
    List<GraphEdgeEntity> findByEdgeType(String edgeType);
    
    List<GraphEdgeEntity> findByFromNodeAndEdgeType(GraphNodeEntity fromNode, String edgeType);
}
```

### Example 18.3: Graph Persistence Service

```java
package com.codetalkerl.firestick.service;

import com.codetalkerl.firestick.entity.GraphEdgeEntity;
import com.codetalkerl.firestick.entity.GraphNodeEntity;
import com.codetalkerl.firestick.repository.GraphEdgeRepository;
import com.codetalkerl.firestick.repository.GraphNodeRepository;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for persisting dependency graph to database.
 */
@Service
public class GraphPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(GraphPersistenceService.class);

    private final GraphNodeRepository nodeRepository;
    private final GraphEdgeRepository edgeRepository;

    public GraphPersistenceService(GraphNodeRepository nodeRepository,
                                   GraphEdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    /**
     * Save the dependency graph to database.
     */
    @Transactional
    public void saveGraph(Graph<Object, DependencyEdge> graph) {
        logger.info("Saving graph to database...");
        long startTime = System.currentTimeMillis();

        // Clear existing data
        edgeRepository.deleteAll();
        nodeRepository.deleteAll();

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

