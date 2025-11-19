package com.codetalker.firestick.llm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for caching LLM explanations in H2 database
 */
@Service
public class LLMCachingService {
    
    private static final Logger logger = Logger.getLogger(LLMCachingService.class.getName());
    
    @Autowired
    private LLMExplanationRepository repository;
    
    public String generateCodeHash(String code) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            logger.log(java.util.logging.Level.WARNING, "Failed to generate code hash: {0}", e.getMessage());
            return String.valueOf(code.hashCode());
        }
    }
    
    public Optional<LLMExplanation> getCachedExplanation(String code, String type) {
        try {
            String codeHash = generateCodeHash(code);
            Optional<LLMExplanation> cached = repository.findByCodeHashAndExplanationType(codeHash, type);
            
            if (cached.isPresent()) {
                LLMExplanation explanation = cached.get();
                explanation.recordAccess();
                repository.save(explanation);
                logger.info(String.format("Cache hit for type=%s with %d accesses", type, explanation.getAccessCount()));
                return cached;
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, "Error retrieving cached explanation: {0}", e.getMessage());
            return Optional.empty();
        }
    }
    
    public void cacheExplanation(String code, String type, String explanation, 
                                 Double confidence, Integer tokensUsed) {
        try {
            String codeHash = generateCodeHash(code);
            
            // Check if exists
            Optional<LLMExplanation> existing = repository.findByCodeHashAndExplanationType(codeHash, type);
            if (existing.isPresent()) {
                logger.info(String.format("Explanation already cached for type=%s", type));
                return;
            }
            
            LLMExplanation cached = new LLMExplanation(codeHash, code, type, explanation, confidence, tokensUsed);
            repository.save(cached);
            logger.info(String.format("Cached explanation for type=%s", type));
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, "Error caching explanation: {0}", e.getMessage());
        }
    }
    
    public long getCacheSize(String type) {
        try {
            return repository.countByExplanationType(type);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, "Error getting cache size: {0}", e.getMessage());
            return 0;
        }
    }
    
    public void clearCache(String type) {
        try {
            repository.deleteAll(repository.findByExplanationType(type));
            logger.info(String.format("Cleared cache for type=%s", type));
        } catch (Exception e) {
            logger.log(java.util.logging.Level.WARNING, "Error clearing cache: {0}", e.getMessage());
        }
    }
}
