package com.codetalker.firestick.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileDiscoveryServiceTest {
    private FileDiscoveryService service;

    @BeforeEach
    void setUp() {
        service = new FileDiscoveryService();
    }

    @Test
    void findsJavaFilesAndExcludesDirs() throws Exception {
        Path root = Path.of("src/test/resources/test-data/sample-code");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            Files.writeString(root.resolve("TestA.java"), "public class TestA {}\n");
            Files.createDirectories(root.resolve("target"));
            Files.writeString(root.resolve("target/ShouldNotFind.java"), "public class ShouldNotFind {}\n");
        }
        List<Path> found = service.scanDirectory(root.toString());
        assertThat(found).anyMatch(p -> p.getFileName().toString().equals("TestA.java"));
        assertThat(found).noneMatch(p -> p.getFileName().toString().equals("ShouldNotFind.java"));
    }

    @Test
    void handlesEmptyDirectory() throws Exception {
        Path emptyDir = Path.of("src/test/resources/test-data/empty");
        if (!Files.exists(emptyDir)) Files.createDirectories(emptyDir);
        List<Path> found = service.scanDirectory(emptyDir.toString());
        assertThat(found).isEmpty();
    }
}
