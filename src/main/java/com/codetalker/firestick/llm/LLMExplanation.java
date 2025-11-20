package com.codetalker.firestick.llm;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Entity for caching LLM explanations in H2 database
 */
@Entity
@Table(name = "llm_explanations", indexes = {
    @Index(name = "idx_code_hash", columnList = "code_hash"),
    @Index(name = "idx_type_created", columnList = "explanation_type,created_at")
})
public class LLMExplanation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 64)
    private String codeHash;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;
    
    @Column(nullable = false, length = 64)
    private String explanationType; // 'EXPLAIN', 'RELATIONSHIP', 'DOCS', 'PATTERNS'
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;
    
    @Column(nullable = false)
    private Double confidence;
    
    @Column(nullable = false)
    private Integer tokensUsed;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime lastAccessedAt;
    
    @Column(nullable = false)
    private Integer accessCount = 0;
    
    public LLMExplanation() {
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.accessCount = 1;
    }
    
    public LLMExplanation(String codeHash, String code, String explanationType, 
                         String explanation, Double confidence, Integer tokensUsed) {
        this();
        this.codeHash = codeHash;
        this.code = code;
        this.explanationType = explanationType;
        this.explanation = explanation;
        this.confidence = confidence;
        this.tokensUsed = tokensUsed;
    }
    
    public void recordAccess() {
        this.lastAccessedAt = LocalDateTime.now();
        this.accessCount++;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getExplanationType() { return explanationType; }
    public void setExplanationType(String explanationType) { this.explanationType = explanationType; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }
}
