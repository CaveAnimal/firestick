package com.codetalker.firestick.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "code_files")
public class CodeFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1024)
    private String filePath;

    @Column(nullable = false)
    private Instant lastModified;

    @Column(nullable = false, length = 128)
    private String hash;

    // Non-persistent field for extracted code chunks
    @jakarta.persistence.Transient
    private java.util.List<com.codetalker.firestick.model.CodeChunk> chunks = new java.util.ArrayList<>();

    public CodeFile() {}

    public CodeFile(String filePath, Instant lastModified, String hash) {
        this.filePath = filePath;
        this.lastModified = lastModified;
        this.hash = hash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Instant getLastModified() { return lastModified; }
    public void setLastModified(Instant lastModified) { this.lastModified = lastModified; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public java.util.List<com.codetalker.firestick.model.CodeChunk> getChunks() { return chunks; }
    public void setChunks(java.util.List<com.codetalker.firestick.model.CodeChunk> chunks) { this.chunks = chunks; }
}
