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
        assertThat(chunks.stream().anyMatch(c -> c.getType().equals("method"))).isTrue();
        assertThat(chunks.stream().anyMatch(c -> c.getType().equals("class"))).isTrue();
    }
}
