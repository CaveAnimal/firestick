package com.codetalker.firestick.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "indexing_jobs")
public class IndexingJob {

    public enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1024)
    private String rootPath;

    // Logical application/tenant name for multi-app isolation (Option B)
    @Column(nullable = true, length = 64)
    private String appName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant endedAt;

    private int filesDiscovered;
    private int filesParsed;
    private int chunksProduced;
    private int documentsIndexed;
    private int embeddingsGenerated;
    private int errorCount;

    @Lob
    private String errorSummary; // optional, newline-delimited

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }

    public int getFilesDiscovered() { return filesDiscovered; }
    public void setFilesDiscovered(int filesDiscovered) { this.filesDiscovered = filesDiscovered; }

    public int getFilesParsed() { return filesParsed; }
    public void setFilesParsed(int filesParsed) { this.filesParsed = filesParsed; }

    public int getChunksProduced() { return chunksProduced; }
    public void setChunksProduced(int chunksProduced) { this.chunksProduced = chunksProduced; }

    public int getDocumentsIndexed() { return documentsIndexed; }
    public void setDocumentsIndexed(int documentsIndexed) { this.documentsIndexed = documentsIndexed; }

    public int getEmbeddingsGenerated() { return embeddingsGenerated; }
    public void setEmbeddingsGenerated(int embeddingsGenerated) { this.embeddingsGenerated = embeddingsGenerated; }

    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }

    public String getErrorSummary() { return errorSummary; }
    public void setErrorSummary(String errorSummary) { this.errorSummary = errorSummary; }
}
