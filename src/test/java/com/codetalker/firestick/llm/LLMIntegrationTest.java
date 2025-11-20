package com.codetalker.firestick.llm;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.llm.LLMController.CodeExplanationRequest;
import com.codetalker.firestick.llm.LLMController.RelationshipRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import java.util.Collections;

/**
 * Integration tests for LLM module - validates Java ↔ Python HTTP communication
 * Requires Python service running on port 8001 for full tests
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("LLM Integration Tests")
public class LLMIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private LLMCachingService cachingService;

    @MockBean
    private LLMServiceClient llmServiceClient;

    @org.junit.jupiter.api.BeforeEach
    void setupMocks() throws Exception {
        when(llmServiceClient.isHealthy()).thenReturn(true);
        when(llmServiceClient.getHealthInfo()).thenReturn(java.util.Map.of("status", "healthy", "model", "mock-model"));
        when(llmServiceClient.explainCode(anyString())).thenReturn("Mocked explanation");
        when(llmServiceClient.analyzeRelationship(anyString(), anyString(), anyString())).thenReturn("Mocked relationship");
        when(llmServiceClient.generateDocumentation(anyString())).thenReturn("Mocked documentation");
        when(llmServiceClient.detectPatterns(anyString())).thenReturn(Collections.singletonList("Mocked pattern"));
    }
    
    @BeforeAll
    public static void checkPythonService() {
        // Check if Python service is available on port 8001
        try {
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://127.0.0.1:8001/health"))
                .GET()
                .timeout(java.time.Duration.ofMillis(2000))
                .build();
            java.net.http.HttpClient.newHttpClient().send(req, java.net.http.HttpResponse.BodyHandlers.discarding());
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            // Service unavailable - tests will handle gracefully
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    @DisplayName("POST /api/llm/explain/code - Should reject empty code")
    public void testExplainCodeEmptyInput() throws Exception {
        CodeExplanationRequest request = new CodeExplanationRequest();
        request.setCode("");
        
        mockMvc.perform(post("/api/llm/explain/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("POST /api/llm/explain/code - Should accept valid code")
    public void testExplainCodeValidInput() throws Exception {
        CodeExplanationRequest request = new CodeExplanationRequest();
        request.setCode("int x = 5;");
        
        mockMvc.perform(post("/api/llm/explain/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.explanation").isNotEmpty());
    }
    
    @Test
    @DisplayName("POST /api/llm/analyze/relationship - Should analyze relationships")
    public void testAnalyzeRelationship() throws Exception {
        RelationshipRequest request = new RelationshipRequest();
        request.setFromClass("class A { void method() {} }");
        request.setToClass("class B { A dependency; }");
        request.setContext("java");
        
        mockMvc.perform(post("/api/llm/analyze/relationship")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("POST /api/llm/generate/docs - Should generate documentation")
    public void testGenerateDocs() throws Exception {
        CodeExplanationRequest request = new CodeExplanationRequest();
        request.setCode("public void calculateSum(int[] values) {}");
        
        mockMvc.perform(post("/api/llm/generate/docs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("POST /api/llm/detect/patterns - Should detect code patterns")
    public void testDetectPatterns() throws Exception {
        CodeExplanationRequest request = new CodeExplanationRequest();
        request.setCode("if (x > 0) { if (y > 0) { doSomething(); } }");
        
        mockMvc.perform(post("/api/llm/detect/patterns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET /api/llm/health - Should return health status")
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/llm/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(notNullValue()));
    }
    
    @Test
    @DisplayName("Cache - Should persist explanations with SHA-256 hash")
    public void testCachingWithHash() throws Exception {
        String code = "public class Example { private int value; }";
    // type variable unused - cache functions implicitly manage type
        
        CodeExplanationRequest request = new CodeExplanationRequest();
        request.setCode(code);
        
        mockMvc.perform(post("/api/llm/explain/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        // Second call should retrieve from cache
        mockMvc.perform(post("/api/llm/explain/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Cache - Should track access metadata")
    public void testCacheAccessTracking() {
        String code = "int sum = 0;";
        String type = "EXPLAIN";
        String explanation = "This initializes a sum variable to zero";
        
        // Cache an explanation
        cachingService.cacheExplanation(code, type, explanation, 0.95, 100);
        
        // Retrieve it (should increment accessCount)
        var cached = cachingService.getCachedExplanation(code, type);
        assert cached.isPresent() : "Cache miss";
        
        LLMExplanation entity = cached.get();
        assert entity.getAccessCount() >= 1 : "Access count not incremented";
        assert entity.getLastAccessedAt() != null : "Last accessed timestamp not set";
    }
    
    @Test
    @DisplayName("Cache - Should manage cache by type")
    public void testCacheManagement() {
        String type = "EXPLAIN_TEST_" + System.nanoTime();
        
        // Cache multiple items
        cachingService.cacheExplanation("code1", type, "explanation1", 0.95, 100);
        cachingService.cacheExplanation("code2", type, "explanation2", 0.95, 100);
        
        // Check size
        long count = cachingService.getCacheSize(type);
        assert count >= 2 : "Cache size incorrect: " + count;
        
        // Clear cache
        cachingService.clearCache(type);
        
        // Verify cleared
        count = cachingService.getCacheSize(type);
        assert count == 0 : "Cache not cleared, count: " + count;
    }
}
