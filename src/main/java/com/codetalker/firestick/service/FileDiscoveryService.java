package com.codetalker.firestick.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.config.IndexingConfig;
import com.codetalker.firestick.exception.FileDiscoveryException;

/**
 * Service to recursively scan directories and find all Java files, with exclusion patterns.
 */
@Service
public class FileDiscoveryService {
    private static final Logger log = LoggerFactory.getLogger(FileDiscoveryService.class);
    private static final String JAVA_EXT = ".java";
    private static final List<String> EXCLUDE_DIRS = List.of("target", "build", ".git", "test");

    private final IndexingConfig indexingConfig;

    /**
     * No-arg constructor for tests or manual instantiation. Uses default IndexingConfig values.
     */
    public FileDiscoveryService() {
        this(new IndexingConfig());
    }

    public FileDiscoveryService(IndexingConfig indexingConfig) {
        this.indexingConfig = indexingConfig;
    }

    public List<Path> scanDirectory(String rootPath) {
        List<Path> javaFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of(rootPath))) {
            log.debug("Scanning directory for Java files: {}", rootPath);
            final List<String> effectiveExts = getEffectiveExtensions();
            final List<String> effectiveExcludeDirs = getEffectiveExcludeDirs();
            final List<java.nio.file.PathMatcher> configMatchers = buildConfigMatchers();
            javaFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> hasAllowedExtension(p, effectiveExts))
                .filter(p -> {
                    Path parent = p.getParent();
                    // For the default scan, stop checking when we reach the provided root path
                    // (preserve previous behavior to not consider ancestors above the root)
                    Path root = Path.of(rootPath).normalize();
                    while (parent != null && !parent.equals(root)) {
                        String dirName = parent.getFileName().toString();
                        if (effectiveExcludeDirs.contains(dirName)) {
                            return false;
                        }
                        parent = parent.getParent();
                    }
                    // Configured glob exclusions against full path
                    for (java.nio.file.PathMatcher matcher : configMatchers) {
                        if (matcher.matches(p.toAbsolutePath().normalize())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
            log.debug("Discovered {} Java files under {} (after exclusions)", javaFiles.size(), rootPath);
        } catch (IOException e) {
            log.error("Error scanning directory: {}", rootPath, e);
            throw new FileDiscoveryException("Error scanning directory: " + rootPath, e);
        }
        return javaFiles;
    }

    /**
     * Scan directory with additional exclusion controls.
     * - excludeDirNames: extra directory names to exclude (in addition to defaults)
     * - excludeGlobPatterns: glob patterns applied to full file path to exclude (for example, any path ending with 'Generated.java')
     */
    public List<Path> scanDirectory(String rootPath, List<String> excludeDirNames, List<String> excludeGlobPatterns) {
        List<String> allExcludeDirs = new ArrayList<>(getEffectiveExcludeDirs());
        if (excludeDirNames != null) allExcludeDirs.addAll(excludeDirNames);

        final List<java.nio.file.PathMatcher> pathMatchers = new ArrayList<>(buildConfigMatchers());
        if (excludeGlobPatterns != null) {
            for (String pattern : excludeGlobPatterns) {
                if (pattern != null && !pattern.isBlank()) {
                    pathMatchers.add(Path.of("").getFileSystem().getPathMatcher("glob:" + pattern));
                }
            }
        }

        List<Path> javaFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of(rootPath))) {
            log.debug("Scanning directory for Java files with excludes: root={}, dirs={}, globs={}", rootPath, allExcludeDirs, excludeGlobPatterns);
            javaFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> hasAllowedExtension(p, getEffectiveExtensions()))
                .filter(p -> {
                    // Directory name exclusion up the path
                    Path parent = p.getParent();
                    Path root = Path.of(rootPath).toAbsolutePath().normalize();
                    while (parent != null && !parent.equals(root)) {
                        String dirName = parent.getFileName().toString();
                        if (allExcludeDirs.contains(dirName)) {
                            return false;
                        }
                        parent = parent.getParent();
                    }
                    // Glob exclusion against full path
                    for (java.nio.file.PathMatcher matcher : pathMatchers) {
                        if (matcher.matches(p.toAbsolutePath().normalize())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
            log.debug("Discovered {} Java files under {} (after custom exclusions)", javaFiles.size(), rootPath);
        } catch (IOException e) {
            log.error("Error scanning directory (custom excludes): {}", rootPath, e);
            throw new FileDiscoveryException("Error scanning directory: " + rootPath, e);
        }
        return javaFiles;
    }

    /** Convenience overload for only directory-name exclusion. */
    public List<Path> scanDirectory(String rootPath, List<String> excludeDirNames) {
        return scanDirectory(rootPath, excludeDirNames, List.of());
    }

    private List<String> getEffectiveExtensions() {
        List<String> exts = (indexingConfig != null && indexingConfig.getFileExtensions() != null && !indexingConfig.getFileExtensions().isEmpty())
            ? indexingConfig.getFileExtensions()
            : List.of(JAVA_EXT);
        return exts;
    }

    private List<String> getEffectiveExcludeDirs() {
        List<String> dirs = (indexingConfig != null && indexingConfig.getExcludeDirectories() != null && !indexingConfig.getExcludeDirectories().isEmpty())
            ? indexingConfig.getExcludeDirectories()
            : EXCLUDE_DIRS;
        return dirs;
    }

    private List<java.nio.file.PathMatcher> buildConfigMatchers() {
        final List<java.nio.file.PathMatcher> matchers = new ArrayList<>();
        if (indexingConfig != null && indexingConfig.getExcludePatterns() != null) {
            for (String pattern : indexingConfig.getExcludePatterns()) {
                if (pattern != null && !pattern.isBlank()) {
                    matchers.add(Path.of("").getFileSystem().getPathMatcher("glob:" + pattern));
                }
            }
        }
        return matchers;
    }

    private boolean hasAllowedExtension(Path path, List<String> allowedExts) {
        String name = path.getFileName().toString();
        for (String ext : allowedExts) {
            if (name.endsWith(ext)) return true;
        }
        return false;
    }
}
