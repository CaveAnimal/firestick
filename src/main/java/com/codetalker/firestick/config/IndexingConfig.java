package com.codetalker.firestick.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for source code indexing and discovery.
 *
 * Properties are bound from prefix 'indexing'.
 * - indexing.file.extensions: List of file extensions to include (e.g., .java)
 * - indexing.exclude.directories: Directory names to exclude (e.g., target, build, .git, test)
 * - indexing.exclude.patterns: Glob patterns (full path) to exclude (e.g., patterns ending with Generated.java)
 */
@Configuration
@ConfigurationProperties(prefix = "indexing")
public class IndexingConfig {
    private List<String> fileExtensions = new ArrayList<>(List.of(".java"));
    private List<String> excludeDirectories = new ArrayList<>(List.of("target", "build", ".git", "test"));
    private List<String> excludePatterns = new ArrayList<>();

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
}
