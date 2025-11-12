package com.codetalker.firestick.service.dto;

import java.util.List;

/**
 * Request for IndexingService: root path and optional exclusion controls.
 */
public record IndexingRequest(
    @jakarta.validation.constraints.NotBlank String rootPath,
        List<String> excludeDirNames,
        List<String> excludeGlobPatterns,
        String appName
) {
    // Canonical compact constructor to normalize defaults
    public IndexingRequest {
        if (appName == null || appName.isBlank()) {
            appName = "default";
        }
    }
    // Backward-compatible factory: default appName
    public static IndexingRequest of(String rootPath) {
        return new IndexingRequest(rootPath, null, null, "default");
    }

    // New factory with explicit appName
    public static IndexingRequest of(String rootPath, String appName) {
        return new IndexingRequest(rootPath, null, null, appName);
    }

    // Backward-compatible 3-arg ctor (records can define custom constructors)
    public IndexingRequest(String rootPath, List<String> excludeDirNames, List<String> excludeGlobPatterns) {
        this(rootPath, excludeDirNames, excludeGlobPatterns, "default");
    }
}
