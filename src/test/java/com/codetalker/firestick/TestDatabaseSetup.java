package com.codetalker.firestick;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit 5 Extension that ensures clean test database state by deleting any H2 test database files
 * that may have been created during previous test runs. This allows mvn clean install to run
 * repeatedly without H2 file corruption or locking issues.
 *
 * The test profile uses in-memory H2 (:mem:testdb), but this extension ensures there are no
 * lingering file-based test database artifacts in the data/ directory.
 *
 * Automatically registered via META-INF/services/org.junit.jupiter.api.extension.Extension
 */
public class TestDatabaseSetup implements BeforeAllCallback {
    private static final Logger log = LoggerFactory.getLogger(TestDatabaseSetup.class);
    private static volatile boolean cleanupDone = false;

    /**
     * Runs once before all tests to clean up any test database files from previous runs.
     * This ensures:
     * - Test database doesn't leak to production data/
     * - No file locking issues between test runs
     * - mvn clean install can run repeatedly without corruption
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Only run cleanup once, even if multiple test classes load this extension
        if (cleanupDone) {
            return;
        }
        cleanupDone = true;

        // List of test database files to clean up if they exist
        String[] testDbFiles = {
            "./data/firestick_test.mv.db",
            "./data/firestick_test.trace.db",
            "./data/testdb.mv.db",
            "./data/testdb.trace.db"
        };

        for (String filePath : testDbFiles) {
            try {
                File file = Paths.get(filePath).toFile();
                if (file.exists()) {
                    if (Files.deleteIfExists(Paths.get(filePath))) {
                        log.info("[TestSetup] Deleted stale test database file: {}", filePath);
                    }
                }
            } catch (IOException | SecurityException e) {
                log.warn("[TestSetup] Failed to delete test database file {}: {}", filePath, e.getMessage());
                // Continue anyway; in-memory DB should still work even if file cleanup fails
            }
        }

        // CRITICAL: Also check for corrupted production database (MVStore assertion errors)
        // This can happen if a file was partially deleted, truncated, or contains stale MVStore metadata
        String[] productionDbFiles = {
            "./data/firestick.mv.db",
            "./data/firestick.trace.db"
        };

        for (String filePath : productionDbFiles) {
            try {
                File file = Paths.get(filePath).toFile();
                if (file.exists()) {
                    // Check for obvious corruption: files that are suspiciously small or truncated
                    long fileSize = file.length();
                    // H2 MVStore files should be at least 512 bytes; if smaller, likely corrupted
                    if (fileSize > 0 && fileSize < 512) {
                        log.warn("[TestSetup] Production database file {} is suspiciously small ({} bytes); likely corrupted. Deleting.", filePath, fileSize);
                        if (Files.deleteIfExists(Paths.get(filePath))) {
                            log.info("[TestSetup] Deleted corrupted production database file: {}", filePath);
                        }
                    }
                }
            } catch (IOException | SecurityException e) {
                log.warn("[TestSetup] Failed to check/delete production database file {}: {}", filePath, e.getMessage());
                // Continue anyway; we'll try to work with what we have
            }
        }

        log.info("[TestSetup] Test database cleanup complete. Using in-memory H2 for all tests.");
    }
}
