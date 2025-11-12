package com.codetalker.firestick.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileDiscoveryServiceExclusionsTest {
    private FileDiscoveryService service;

    @BeforeEach
    void setUp() {
        service = new FileDiscoveryService();
    }

    @Test
    void excludesByAdditionalDirNames() throws Exception {
        Path root = Path.of("src/test/resources/test-data/sample-code");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            Files.writeString(root.resolve("TestB.java"), "public class TestB {}\n");
        }
        // Exclude the directory itself to force zero results
        List<Path> found = service.scanDirectory("src/test/resources/test-data", List.of("sample-code"));
        assertThat(found).isEmpty();
    }

    @Test
    void excludesByGlobPatterns() throws Exception {
        Path root = Path.of("src/test/resources/test-data/sample-code");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            Files.writeString(root.resolve("CalcGenerated.java"), "public class CalcGenerated {}\n");
        }
        // Exclude any Java ending with Generated.java
        List<Path> found = service.scanDirectory(
            "src/test/resources/test-data/sample-code",
            List.of(),
            List.of("**/*Generated.java")
        );
        assertThat(found).noneMatch(p -> p.getFileName().toString().endsWith("Generated.java"));
    }
}
