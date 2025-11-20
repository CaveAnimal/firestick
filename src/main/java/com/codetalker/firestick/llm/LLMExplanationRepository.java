package com.codetalker.firestick.llm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing cached LLM explanations
 */
@Repository
public interface LLMExplanationRepository extends JpaRepository<LLMExplanation, Long> {
    
    Optional<LLMExplanation> findByCodeHashAndExplanationType(String codeHash, String type);
    
    List<LLMExplanation> findByExplanationType(String type);
    
    @Query("SELECT e FROM LLMExplanation e ORDER BY e.accessCount DESC LIMIT 10")
    List<LLMExplanation> findMostUsedExplanations();
    
    long countByExplanationType(String type);
}
