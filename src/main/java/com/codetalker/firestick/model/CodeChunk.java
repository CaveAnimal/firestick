package com.codetalker.firestick.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "code_chunks")
public class CodeChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "code_file_id", nullable = false)
    private CodeFile file;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int startLine;

    @Column(nullable = false)
    private int endLine;

    @Column(nullable = false, length = 64)
    private String type; // e.g., class, method, block

    public CodeChunk() {}

    public CodeChunk(CodeFile file, String content, int startLine, int endLine, String type) {
        this.file = file;
        this.content = content;
        this.startLine = startLine;
        this.endLine = endLine;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CodeFile getFile() { return file; }
    public void setFile(CodeFile file) { this.file = file; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getStartLine() { return startLine; }
    public void setStartLine(int startLine) { this.startLine = startLine; }

    public int getEndLine() { return endLine; }
    public void setEndLine(int endLine) { this.endLine = endLine; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
