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
import jakarta.persistence.Transient;

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

    // Logical application/tenant name for multi-app isolation (Option B)
    @Column(name = "app_name", nullable = true, length = 64)
    private String appName;

    public CodeChunk() {}

    private String name;
    private String signature;
    private String modifiers;
    private String annotations;
    private String javaDoc;
    @Column(length = 128)
    private String chunkType;
    // Metadata as JSON string for chunk type, parent/child relationships, boundaries, etc.
    @Column(length = 1024)
    private String metadata; // e.g. {"chunkType":"method","parentId":123,"children":[456,789],"startLine":10,"endLine":20}
    @Lob
    private float[] embedding;

    public CodeChunk(CodeFile file, String content, int startLine, int endLine, String type) {
        this.file = file;
        this.content = content;
        this.startLine = startLine;
        this.endLine = endLine;
        this.type = type;
    }

    // Backward-compatible constructor (without chunkType/metadata/embedding)
    public CodeChunk(CodeFile file, String content, int startLine, int endLine, String type,
                      String name, String signature, String modifiers, String annotations, String javaDoc) {
        this.file = file;
        this.content = content;
        this.startLine = startLine;
        this.endLine = endLine;
        this.type = type;
        this.name = name;
        this.signature = signature;
        this.modifiers = modifiers;
        this.annotations = annotations;
        this.javaDoc = javaDoc;
        this.chunkType = null;
        this.metadata = null;
        this.embedding = null;
    }

    public CodeChunk(CodeFile file, String content, int startLine, int endLine, String type,
                      String name, String signature, String modifiers, String annotations, String javaDoc,
                      String chunkType, String metadata, float[] embedding) {
        this.file = file;
        this.content = content;
        this.startLine = startLine;
        this.endLine = endLine;
        this.type = type;
        this.name = name;
        this.signature = signature;
        this.modifiers = modifiers;
        this.annotations = annotations;
        this.javaDoc = javaDoc;
        this.chunkType = chunkType;
        this.metadata = metadata;
        this.embedding = embedding;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public String getModifiers() { return modifiers; }
    public void setModifiers(String modifiers) { this.modifiers = modifiers; }
    public String getAnnotations() { return annotations; }
    public void setAnnotations(String annotations) { this.annotations = annotations; }
    public String getJavaDoc() { return javaDoc; }
    public void setJavaDoc(String javaDoc) { this.javaDoc = javaDoc; }
    public String getChunkType() { return chunkType; }
    public void setChunkType(String chunkType) { this.chunkType = chunkType; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }

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

    public String getType() { return type == null ? null : type.trim(); }
    public void setType(String type) { this.type = type; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    // Hierarchical relationships (not persisted)
    @Transient
    private CodeChunk parent;
    @Transient
    private java.util.List<CodeChunk> children;

        public CodeChunk getParent() { return parent; }
        public void setParent(CodeChunk parent) { this.parent = parent; }
        public java.util.List<CodeChunk> getChildren() { return children; }
        public void setChildren(java.util.List<CodeChunk> children) { this.children = children; }

        // Builder pattern
        public static class Builder {
            private CodeFile file;
            private String content;
            private int startLine;
            private int endLine;
            private String type;
            private String name;
            private String signature;
            private String modifiers;
            private String annotations;
            private String javaDoc;
            private String chunkType;
            private String metadata;
            private float[] embedding;
            private CodeChunk parent;
            private java.util.List<CodeChunk> children;

            public Builder file(CodeFile file) { this.file = file; return this; }
            public Builder content(String content) { this.content = content; return this; }
            public Builder startLine(int startLine) { this.startLine = startLine; return this; }
            public Builder endLine(int endLine) { this.endLine = endLine; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder signature(String signature) { this.signature = signature; return this; }
            public Builder modifiers(String modifiers) { this.modifiers = modifiers; return this; }
            public Builder annotations(String annotations) { this.annotations = annotations; return this; }
            public Builder javaDoc(String javaDoc) { this.javaDoc = javaDoc; return this; }
            public Builder chunkType(String chunkType) { this.chunkType = chunkType; return this; }
            public Builder metadata(String metadata) { this.metadata = metadata; return this; }
            public Builder embedding(float[] embedding) { this.embedding = embedding; return this; }
            public Builder parent(CodeChunk parent) { this.parent = parent; return this; }
            public Builder children(java.util.List<CodeChunk> children) { this.children = children; return this; }
            public CodeChunk build() {
                CodeChunk chunk = new CodeChunk();
                chunk.file = this.file;
                chunk.content = this.content;
                chunk.startLine = this.startLine;
                chunk.endLine = this.endLine;
                chunk.type = this.type;
                chunk.name = this.name;
                chunk.signature = this.signature;
                chunk.modifiers = this.modifiers;
                chunk.annotations = this.annotations;
                chunk.javaDoc = this.javaDoc;
                chunk.chunkType = this.chunkType;
                chunk.metadata = this.metadata;
                chunk.embedding = this.embedding;
                chunk.parent = this.parent;
                chunk.children = this.children;
                return chunk;
            }
        }

        @Override
        public String toString() {
            return "CodeChunk{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", startLine=" + startLine +
                    ", endLine=" + endLine +
                    ", parent=" + (parent != null ? parent.getName() : null) +
                    ", childrenCount=" + (children != null ? children.size() : 0) +
                    '}';
        }
}
