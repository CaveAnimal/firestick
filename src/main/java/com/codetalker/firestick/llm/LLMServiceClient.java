package com.codetalker.firestick.llm;

public interface LLMServiceClient {
    /**
     * Generate a brief explanation of code
     */
    String explainCode(String code) throws LLMServiceException;
    
    /**
     * Analyze relationship between two classes
     */
    String analyzeRelationship(String fromClass, String toClass, String context) throws LLMServiceException;
    
    /**
     * Answer a natural language question given supporting context.
     */
    String answerQuestion(String question, String context) throws LLMServiceException;

    /**
     * Generate documentation for code
     */
    String generateDocumentation(String code) throws LLMServiceException;
    
    /**
     * Detect patterns in code
     */
    java.util.List<String> detectPatterns(String code) throws LLMServiceException;
    
    /**
     * Expand a user query with related technical terms
     */
    java.util.List<String> expandQuery(String query) throws LLMServiceException;

    /**
     * Generate a high-level summary of the file content.
     */
    String summarize(String content) throws LLMServiceException;

    /**
     * Check if service is healthy and responsive
     */
    boolean isHealthy();

    /**
     * Get detailed health info including model name
     */
    java.util.Map<String, Object> getHealthInfo();
}
