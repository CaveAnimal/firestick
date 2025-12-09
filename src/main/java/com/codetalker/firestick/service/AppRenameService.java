package com.codetalker.firestick.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.IndexingJobRepository;

/**
 * Service for renaming applications and updating all related data atomically.
 * Handles H2 database updates (Chroma collection migration deferred to future version).
 */
@Service
public class AppRenameService {
    private static final Logger log = LoggerFactory.getLogger(AppRenameService.class);

    private final CodeFileRepository codeFileRepository;
    private final CodeChunkRepository codeChunkRepository;
    private final IndexingJobRepository indexingJobRepository;

    public AppRenameService(
            CodeFileRepository codeFileRepository,
            CodeChunkRepository codeChunkRepository,
            IndexingJobRepository indexingJobRepository) {
        this.codeFileRepository = codeFileRepository;
        this.codeChunkRepository = codeChunkRepository;
        this.indexingJobRepository = indexingJobRepository;
    }

    /**
     * Rename an application, updating all H2 records and Chroma collections atomically.
     * 
     * @param oldAppName the current application name
     * @param newAppName the new application name
     * @return AppRenameResult with affected record counts
     * @throws IllegalArgumentException if old app doesn't exist or new name conflicts
     */
    @Transactional
    public AppRenameResult rename(String oldAppName, String newAppName) {
        log.info("Renaming app '{}' to '{}'", oldAppName, newAppName);

        if (oldAppName == null || oldAppName.isBlank()) {
            throw new IllegalArgumentException("Old app name cannot be blank");
        }
        if (newAppName == null || newAppName.isBlank()) {
            throw new IllegalArgumentException("New app name cannot be blank");
        }
        if (oldAppName.equalsIgnoreCase(newAppName)) {
            throw new IllegalArgumentException("New app name must differ from old app name");
        }

        // Validate old app exists
        long oldAppFileCount = codeFileRepository.countByAppName(oldAppName);
        if (oldAppFileCount == 0) {
            throw new IllegalArgumentException("Application '" + oldAppName + "' not found");
        }

        // Validate new name doesn't already exist
        long newAppFileCount = codeFileRepository.countByAppName(newAppName);
        if (newAppFileCount > 0) {
            throw new IllegalArgumentException("Application '" + newAppName + "' already exists");
        }

        try {
            // 1. Update H2 records
            long filesUpdated = codeFileRepository.updateAppName(oldAppName, newAppName);
            long chunksUpdated = codeChunkRepository.updateAppName(oldAppName, newAppName);
            long jobsUpdated = indexingJobRepository.updateAppName(oldAppName, newAppName);

            log.info("App rename complete: {} files, {} chunks, {} jobs updated", 
                filesUpdated, chunksUpdated, jobsUpdated);

            return new AppRenameResult(filesUpdated, chunksUpdated, jobsUpdated, true);

        } catch (Exception e) {
            log.error("Error renaming app '{}' to '{}': {}", oldAppName, newAppName, e.getMessage(), e);
            // Transaction will rollback automatically
            throw new RuntimeException("Failed to rename application: " + e.getMessage(), e);
        }
    }

    /**
     * Result of an app rename operation.
     */
    public static class AppRenameResult {
        public final long filesUpdated;
        public final long chunksUpdated;
        public final long jobsUpdated;
        public final boolean success;

        public AppRenameResult(long filesUpdated, long chunksUpdated, long jobsUpdated, boolean success) {
            this.filesUpdated = filesUpdated;
            this.chunksUpdated = chunksUpdated;
            this.jobsUpdated = jobsUpdated;
            this.success = success;
        }
    }
}
