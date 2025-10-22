package com.codetalker.firestick.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

/**
 * Service to recursively scan directories and find all Java files, with exclusion patterns.
 */
@Service
public class FileDiscoveryService {
    private static final String JAVA_EXT = ".java";
    private static final List<String> EXCLUDE_DIRS = List.of("target", "build", ".git", "test");

    public List<Path> scanDirectory(String rootPath) {
        List<Path> javaFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of(rootPath))) {
            javaFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(JAVA_EXT))
                .filter(p -> EXCLUDE_DIRS.stream().noneMatch(ex -> p.toString().contains(ex)))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error scanning directory: " + rootPath, e);
        }
        return javaFiles;
    }
}
