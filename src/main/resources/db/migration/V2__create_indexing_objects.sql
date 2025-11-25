-- Flyway migration: create indexing_objects table for per-object telemetry
CREATE TABLE IF NOT EXISTS indexing_objects (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    job_id BIGINT NOT NULL,
    object_type VARCHAR(16) NOT NULL,
    object_name VARCHAR(2048) NOT NULL,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    elapsed_ms BIGINT,
    reason_skipped VARCHAR(1024)
);

CREATE INDEX IF NOT EXISTS idx_indexing_objects_job_id ON indexing_objects(job_id);
