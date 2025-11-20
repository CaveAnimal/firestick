package com.codetalker.firestick.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("LLMController Tests")
class LLMControllerTest {
    
    @Mock
    private LLMServiceClient llmServiceClient;
    @Mock
    private LLMCachingService cachingService;
    
    private LLMController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new LLMController();
        ReflectionTestUtils.setField(controller, "llmServiceClient", llmServiceClient);
    ReflectionTestUtils.setField(controller, "cachingService", cachingService);
    }
    
    @Test
    @DisplayName("explainCode returns 400 for empty code")
    void testExplainCodeEmptyInput() {
        LLMController.CodeExplanationRequest request = new LLMController.CodeExplanationRequest();
        request.setCode("");
        
        ResponseEntity<?> response = controller.explainCode(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    @DisplayName("explainCode returns 503 when service unavailable")
    void testExplainCodeServiceUnavailable() throws LLMServiceException {
        LLMController.CodeExplanationRequest request = new LLMController.CodeExplanationRequest();
        request.setCode("code");
        
        when(llmServiceClient.explainCode(anyString())).thenReturn(null);
        ResponseEntity<?> response = controller.explainCode(request);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
    
    @Test
    @DisplayName("analyzeRelationship returns 400 for null class")
    void testAnalyzeRelationshipNullInput() {
        LLMController.RelationshipRequest request = new LLMController.RelationshipRequest();
        request.setFromClass(null);
        request.setToClass("B");
        
        ResponseEntity<?> response = controller.analyzeRelationship(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    @DisplayName("generateDocumentation returns 400 for empty code")
    void testGenerateDocumentationEmptyInput() {
        LLMController.CodeExplanationRequest request = new LLMController.CodeExplanationRequest();
        request.setCode("");
        
        ResponseEntity<?> response = controller.generateDocumentation(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    @DisplayName("detectPatterns returns 400 for empty code")
    void testDetectPatternsEmptyInput() {
        LLMController.CodeExplanationRequest request = new LLMController.CodeExplanationRequest();
        request.setCode("");
        
        ResponseEntity<?> response = controller.detectPatterns(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    @DisplayName("healthCheck returns OK")
    void testHealthCheck() {
        when(llmServiceClient.isHealthy()).thenReturn(true);
        ResponseEntity<?> response = controller.healthCheck();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
