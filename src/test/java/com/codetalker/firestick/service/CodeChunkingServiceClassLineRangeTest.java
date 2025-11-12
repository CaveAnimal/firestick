package com.codetalker.firestick.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;

class CodeChunkingServiceClassLineRangeTest {

    @Test
    void extractsClassStartAndEndLines() throws Exception {
        String src = String.join("\n",
            "package com.example;",
            "",
            "public class LineRangeSample {",
            "    public void a() {",
            "        // do a",
            "    }",
            "",
            "    private int b(int x) {",
            "        return x + 1;",
            "    }",
            "}");

        Path tempDir = Files.createTempDirectory("lrtest");
        Path file = tempDir.resolve("LineRangeSample.java");
        Files.writeString(file, src);

        CodeParserService parser = new CodeParserService();
        CodeFile cf = parser.parseFile(file.toString());
        List<CodeChunk> chunks = cf.getChunks();
        assertThat(chunks).isNotEmpty();

        CodeChunk classChunk = chunks.stream()
            .filter(c -> "class".equals(c.getType()))
            .findFirst()
            .orElseThrow();

        // Expect class begins at line 3 (public class ...) and ends at line 11 ('}')
        assertThat(classChunk.getStartLine()).isEqualTo(3);
        assertThat(classChunk.getEndLine()).isEqualTo(11);
    }
}
