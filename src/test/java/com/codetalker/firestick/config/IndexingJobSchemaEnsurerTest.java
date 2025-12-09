package com.codetalker.firestick.config;

import java.sql.Driver;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

class IndexingJobSchemaEnsurerTest {

    @Test
    void run_adds_missing_total_columns_when_table_exists_without_them() throws Exception {
        // Create an in-memory H2 DB and table that mimics older schema (no TOTAL_FOLDERS/TOTAL_METHODS)
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        @SuppressWarnings("unchecked")
        Class<? extends Driver> drv = (Class<? extends Driver>) Class.forName("org.h2.Driver");
        ds.setDriverClass(drv);
        ds.setUrl("jdbc:h2:mem:ensurer_test;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");

        JdbcTemplate jdbc = new JdbcTemplate(ds);

        // Create a very small INDEXING_JOBS table with only ID
        jdbc.execute("CREATE TABLE INDEXING_JOBS (ID BIGINT PRIMARY KEY)");

        // Sanity: columns should not exist yet
        Integer before = jdbc.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'INDEXING_JOBS' and column_name = 'TOTAL_FOLDERS'",
                Integer.class);
        assertThat(before).isNotNull();
        assertThat(before).isEqualTo(0);

        // Run the ensurer
        IndexingJobSchemaEnsurer ensurer = new IndexingJobSchemaEnsurer(jdbc);
        ensurer.run(null);

        // After running, we expect the columns exist
        Integer afterFolders = jdbc.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'INDEXING_JOBS' and column_name = 'TOTAL_FOLDERS'",
                Integer.class);
        Integer afterMethods = jdbc.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'INDEXING_JOBS' and column_name = 'TOTAL_METHODS'",
                Integer.class);

        assertThat(afterFolders).isNotNull().isGreaterThan(0);
        assertThat(afterMethods).isNotNull().isGreaterThan(0);
    }
}
