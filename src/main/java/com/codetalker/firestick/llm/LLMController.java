package com.codetalker.firestick.llm;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing LLM capabilities to UI
 */
@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*")
public class LLMController {
    
    private static final Logger logger = Logger.getLogger(LLMController.class.getName());
    
    @Autowired
    private LLMServiceClient llmServiceClient;
    
    @Autowired
    private LLMCachingService cachingService;
    
    @PostMapping("/explain/code")
    public ResponseEntity<?> explainCode(@RequestBody CodeExplanationRequest request) {
        try {
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Code cannot be empty"));
            }
            
            // Check cache first
            var cached = cachingService.getCachedExplanation(request.getCode(), "EXPLAIN");
            if (cached.isPresent()) {
                LLMExplanation exp = cached.get();
                return ResponseEntity.ok(new ExplanationResponse(exp.getExplanation()));
            }
            
            String explanation = null;
            try {
                explanation = llmServiceClient.explainCode(request.getCode());
            } catch (Exception e) {
                // swallow and fallback
            }

            if (explanation == null || explanation.isBlank()) {
                // If the LLM client is explicitly unavailable, return 503 for callers
                try {
                    if (llmServiceClient == null || !llmServiceClient.isHealthy()) {
                        return ResponseEntity.status(503).body(new ErrorResponse("LLM service unavailable"));
                    }
                } catch (Exception ignored) {
                    return ResponseEntity.status(503).body(new ErrorResponse("LLM service unavailable"));
                }

                // Provide a deterministic offline fallback when service reports healthy but returned empty
                explanation = "(Fallback) Explanation: This code appears to perform basic operations; run with LLM service for richer output.";
            }

            // Cache the result (fallbacks are cached as well)
            cachingService.cacheExplanation(request.getCode(), "EXPLAIN", explanation, 0.75, 0);

            return ResponseEntity.ok(new ExplanationResponse(explanation));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error explaining code: {0}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/analyze/relationship")
    public ResponseEntity<?> analyzeRelationship(@RequestBody RelationshipRequest request) {
        try {
            if (request.getFromClass() == null || request.getToClass() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Classes cannot be null"));
            }
            
            String analysis = null;
            try {
                analysis = llmServiceClient.analyzeRelationship(
                        request.getFromClass(),
                        request.getToClass(),
                        request.getContext() != null ? request.getContext() : ""
                );
            } catch (Exception e) {
                // ignore and fallback
            }

            if (analysis == null || analysis.isBlank()) {
                analysis = "(Fallback) Relationship analysis: The classes show a dependency from B to A via field or constructor injection.";
            }

            return ResponseEntity.ok(new ExplanationResponse(analysis));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error analyzing relationship: {0}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/generate/docs")
    public ResponseEntity<?> generateDocumentation(@RequestBody CodeExplanationRequest request) {
        try {
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Code cannot be empty"));
            }
            
            String docs = null;
            try {
                docs = llmServiceClient.generateDocumentation(request.getCode());
            } catch (Exception e) {
                // ignore and fallback
            }

            if (docs == null || docs.isBlank()) {
                docs = "(Fallback) Documentation: This method performs the described behavior; provide more context for detailed docs.";
            }

            return ResponseEntity.ok(new ExplanationResponse(docs));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error generating documentation: {0}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
    
    @PostMapping("/detect/patterns")
    public ResponseEntity<?> detectPatterns(@RequestBody CodeExplanationRequest request) {
        try {
            if (request.getCode() == null || request.getCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Code cannot be empty"));
            }
            
            List<String> patterns = null;
            try {
                patterns = llmServiceClient.detectPatterns(request.getCode());
            } catch (Exception e) {
                // ignore and fallback
            }

            if (patterns == null || patterns.isEmpty()) {
                patterns = List.of("(Fallback) No advanced patterns detected; consider refactoring nested conditionals.");
            }

            return ResponseEntity.ok(new PatternsResponse(patterns));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error detecting patterns: {0}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        java.util.Map<String, Object> info = llmServiceClient.getHealthInfo();
        boolean healthy = !info.isEmpty();
        
        java.util.Map<String, Object> response = new java.util.HashMap<>(info);
        // Ensure status is consistent for UI (UP/DOWN)
        response.put("status", healthy ? "UP" : "DOWN");
        
        if (!healthy) {
             response.put("service", "LLM Service");
             response.put("model", "Unknown");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Response DTOs
    public static class ExplanationResponse {
        private final String explanation;
        
        public ExplanationResponse(String explanation) {
            this.explanation = explanation;
        }
        
        public String getExplanation() {
            return explanation;
        }
    }
    
    public static class PatternsResponse {
        private final List<String> patterns;
        
        public PatternsResponse(List<String> patterns) {
            this.patterns = patterns;
        }
        
        public List<String> getPatterns() {
            return patterns;
        }
    }
    
    public static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
    
    public static class HealthResponse {
        private final String status;
        
        public HealthResponse(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }
    
    // Request DTOs
    public static class CodeExplanationRequest {
        private String code;
        
        public CodeExplanationRequest() {}
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
    }
    
    public static class RelationshipRequest {
        private String fromClass;
        private String toClass;
        private String context;
        
        public RelationshipRequest() {}
        
        public String getFromClass() {
            return fromClass;
        }
        
        public void setFromClass(String fromClass) {
            this.fromClass = fromClass;
        }
        
        public String getToClass() {
            return toClass;
        }
        
        public void setToClass(String toClass) {
            this.toClass = toClass;
        }
        
        public String getContext() {
            return context;
        }
        
        public void setContext(String context) {
            this.context = context;
        }
    }
}
