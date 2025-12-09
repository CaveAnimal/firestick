package com.codetalker.firestick.graph.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Represents a node in the dependency graph (method, class, or field).
 * Stored in H2 database for persistence and querying.
 */
@Entity
@Table(name = "code_nodes", indexes = {
    @Index(name = "idx_app_name", columnList = "app_name"),
    @Index(name = "idx_full_name", columnList = "full_name"),
    @Index(name = "idx_app_full_name", columnList = "app_name,full_name", unique = true),
    @Index(name = "idx_type", columnList = "type")
})
public class CodeNode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "app_name", nullable = false, length = 255)
    private String appName;
    
    @Column(name = "full_name", nullable = false, length = 500)
    private String fullName;  // e.g., "PaymentProcessor.process"
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;  // "method", "class", "field"
    
    @Column(name = "signature", length = 1000)
    private String signature;  // Method signature with parameters
    
    @Column(name = "line_number")
    private Integer lineNumber;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Column(name = "class_name", length = 255)
    private String className;  // e.g., "PaymentProcessor"
    
    @Column(name = "method_name", length = 255)
    private String methodName;  // e.g., "process"
    
    @Column(name = "return_type", length = 255)
    private String returnType;  // e.g., "void", "String"
    
    @Column(name = "is_public")
    private Boolean isPublic = true;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    // Constructors
    public CodeNode() {}
    
    public CodeNode(String appName, String fullName, String type) {
        this.appName = appName;
        this.fullName = fullName;
        this.type = type;
    }
    
    public CodeNode(String appName, String className, String methodName, String type) {
        this.appName = appName;
        this.className = className;
        this.methodName = methodName;
        this.fullName = className + "." + methodName;
        this.type = type;
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
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public Integer getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public Boolean getPublic() {
        return isPublic;
    }
    
    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // equals and hashCode (required for JGraphT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeNode)) return false;
        CodeNode codeNode = (CodeNode) o;
        return Objects.equals(appName, codeNode.appName) &&
               Objects.equals(fullName, codeNode.fullName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(appName, fullName);
    }
    
    @Override
    public String toString() {
        return "CodeNode{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type='" + type + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
