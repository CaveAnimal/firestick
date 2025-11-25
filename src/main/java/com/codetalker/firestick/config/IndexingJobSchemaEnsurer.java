package com.codetalker.firestick.config;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Small startup helper to make the local development DB resilient to missing columns added in
 * recent index job work (files_summarized, folders_summarized, methods_summarized, skipped_files).
 *
 * This is intentionally conservative and only applies automatic DDL for H2 databases.
 */
@Component
public class IndexingJobSchemaEnsurer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(IndexingJobSchemaEnsurer.class);

    private final JdbcTemplate jdbc;

    public IndexingJobSchemaEnsurer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            DatabaseMetaData meta = jdbc.getDataSource().getConnection().getMetaData();
            String product = meta.getDatabaseProductName();
            if (product == null || !product.toLowerCase().contains("h2")) {
                log.debug("IndexingJobSchemaEnsurer skipping automatic schema adjustments for DB: {}", product);
                return;
            }

            ensureColumnExists("INDEXING_JOBS", "FILES_SUMMARIZED", "INT DEFAULT 0");
            ensureColumnExists("INDEXING_JOBS", "FOLDERS_SUMMARIZED", "INT DEFAULT 0");
            ensureColumnExists("INDEXING_JOBS", "METHODS_SUMMARIZED", "INT DEFAULT 0");
            // New totals added in recent work — ensure they exist and default to 0 so existing DB files
            // (local dev H2) won't break when application expects non-null int fields.
            ensureColumnExists("INDEXING_JOBS", "TOTAL_FOLDERS", "INT DEFAULT 0");
            ensureColumnExists("INDEXING_JOBS", "TOTAL_METHODS", "INT DEFAULT 0");
            ensureColumnExists("INDEXING_JOBS", "SKIPPED_FILES", "CLOB");

        } catch (Exception e) {
            log.debug("IndexingJobSchemaEnsurer could not check/modify DB schema: {}", e.getMessage());
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String ddlType) {
        try (ResultSet rs = jdbc.getDataSource().getConnection().getMetaData().getColumns(null, null, tableName, columnName)) {
            if (rs != null && rs.next()) {
                // Column exists
                log.debug("Column {}.{} already exists", tableName, columnName);
                return;
            }
        } catch (Exception e) {
            // If metadata lookup fails continue and attempt a safe check using INFORMATION_SCHEMA
        }

        try {
            Integer count = jdbc.queryForObject(
                    "select count(*) from information_schema.columns where table_name = ? and column_name = ?",
                    Integer.class, tableName.toUpperCase(), columnName.toUpperCase());
            if (count != null && count > 0) {
                log.debug("Column {}.{} found via INFORMATION_SCHEMA", tableName, columnName);
                return;
            }
        } catch (Exception ignored) {
            // ignored; we'll attempt ALTER TABLE below
        }

        try {
            String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, ddlType);
            jdbc.execute(sql);
            log.info("Added missing column {}.{} ({})", tableName, columnName, ddlType);
        } catch (Exception e) {
            log.warn("Failed to add {}.{} — please run a migration manually. Cause: {}", tableName, columnName, e.getMessage());
        }
    }
}
