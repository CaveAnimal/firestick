package com.codetalker.firestick.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents an edge in the dependency graph (dependency between nodes).
 * Stored in H2 database for persistence and querying.
 */
@Entity
@Table(name = "code_edges", indexes = {
    // Use unique index name to avoid collisions across tables (H2 index names are schema-global)
    @Index(name = "idx_edge_app_name", columnList = "app_name"),
    @Index(name = "idx_source_id", columnList = "source_id"),
    @Index(name = "idx_target_id", columnList = "target_id"),
    @Index(name = "idx_app_source", columnList = "app_name,source_id"),
    @Index(name = "idx_app_target", columnList = "app_name,target_id"),
    @Index(name = "idx_app_source_target", columnList = "app_name,source_id,target_id")
})
public class CodeEdge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "app_name", nullable = false, length = 255)
    private String appName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private CodeNode source;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private CodeNode target;
    
    @Column(name = "edge_type", nullable = false, length = 50)
    private String edgeType;  // "calls", "extends", "implements", "uses"
    
    @Column(name = "weight")
    private Integer weight = 1;  // Frequency of calls
    
    @Column(name = "description", length = 1000)
    private String description;
    
    // Constructors
    public CodeEdge() {}
    
    public CodeEdge(CodeNode source, CodeNode target, String edgeType) {
        this.source = source;
        this.target = target;
        this.edgeType = edgeType;
        this.appName = source.getAppName();
    }
    
    public CodeEdge(CodeNode source, CodeNode target, String edgeType, Integer weight) {
        this.source = source;
        this.target = target;
        this.edgeType = edgeType;
        this.weight = weight;
        this.appName = source.getAppName();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public CodeNode getSource() {
        return source;
    }
    
    public void setSource(CodeNode source) {
        this.source = source;
    }
    
    public CodeNode getTarget() {
        return target;
    }
    
    public void setTarget(CodeNode target) {
        this.target = target;
    }
    
    public String getEdgeType() {
        return edgeType;
    }
    
    public void setEdgeType(String edgeType) {
        this.edgeType = edgeType;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeEdge)) return false;
        CodeEdge codeEdge = (CodeEdge) o;
        return Objects.equals(source, codeEdge.source) &&
               Objects.equals(target, codeEdge.target) &&
               Objects.equals(edgeType, codeEdge.edgeType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(source, target, edgeType);
    }
    
    @Override
    public String toString() {
        return "CodeEdge{" +
                "id=" + id +
                ", source=" + (source != null ? source.getFullName() : "null") +
                ", target=" + (target != null ? target.getFullName() : "null") +
                ", edgeType='" + edgeType + '\'' +
                ", weight=" + weight +
                '}';
    }
}
