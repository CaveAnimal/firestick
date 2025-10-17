package com.codetalker.firestick.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "symbols")
public class Symbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 32)
    private String type; // class, method, field

    @Column(length = 1024)
    private String signature;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "code_file_id", nullable = false)
    private CodeFile file;

    @Column(nullable = false)
    private int lineNumber;

    public Symbol() {}

    public Symbol(String name, String type, String signature, CodeFile file, int lineNumber) {
        this.name = name;
        this.type = type;
        this.signature = signature;
        this.file = file;
        this.lineNumber = lineNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public CodeFile getFile() { return file; }
    public void setFile(CodeFile file) { this.file = file; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
}
