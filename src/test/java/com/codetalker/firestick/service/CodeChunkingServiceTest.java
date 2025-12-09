package com.codetalker.firestick.service;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

class CodeChunkingServiceTest {
    @Test
    void testExtractChunks() {
        String code = """
            public class HelloWorld {
                public int add(int a, int b) { return a + b; }
            }
            """;
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(code).getResult().orElseThrow();
        CodeFile file = new CodeFile("HelloWorld.java", Instant.now(), "hash");
        CodeChunkingService chunker = new CodeChunkingService();
        List<CodeChunk> chunks = chunker.extractChunks(file, cu);
        assertThat(chunks).isNotEmpty();
    // Prefer finding a class chunk first, then derive the file chunk from its parent to tolerate flat vs hierarchical returns
    CodeChunk classChunk = chunks.stream().filter(c -> "class".equals(c.getType())).findFirst().orElse(null);
    assertThat(classChunk).isNotNull();
    CodeChunk fileChunk = classChunk.getParent();
    assertThat(fileChunk).isNotNull();
    // Class-level chunk (from file's children)
    classChunk = fileChunk.getChildren().stream().filter(c -> c.getType().equals("class")).findFirst().orElse(null);
        assertThat(classChunk).isNotNull();
        assertThat(classChunk.getParent()).isEqualTo(fileChunk);
        // Method-level chunk
        CodeChunk methodChunk = classChunk.getChildren().stream().filter(c -> c.getType().equals("method")).findFirst().orElse(null);
        assertThat(methodChunk).isNotNull();
        assertThat(methodChunk.getParent()).isEqualTo(classChunk);
        // Metadata checks
        assertThat(fileChunk.getMetadata()).contains("file");
        assertThat(classChunk.getMetadata()).contains("class");
        assertThat(methodChunk.getMetadata()).contains("method");
    }
}
