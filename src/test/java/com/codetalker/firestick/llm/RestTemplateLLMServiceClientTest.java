package com.codetalker.firestick.llm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@DisplayName("RestTemplateLLMServiceClient Tests")
class RestTemplateLLMServiceClientTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private RestTemplateBuilder builder;
    
    private RestTemplateLLMServiceClient client;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.connectTimeout(any())).thenReturn(builder);
        when(builder.readTimeout(any())).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        client = new RestTemplateLLMServiceClient(builder, "http://localhost:8001");
    }
    
    @Test
    @DisplayName("isHealthy returns true when service available")
    void testIsHealthyTrue() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));
        
        assertTrue(client.isHealthy());
    }
    
    @Test
    @DisplayName("isHealthy returns false when service unavailable")
    void testIsHealthyFalse() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));
        
        assertFalse(client.isHealthy());
    }
    
    @Test
    @DisplayName("explainCode returns null when service unhealthy")
    void testExplainCodeUnhealthy() throws LLMServiceException {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));
        
        client.isHealthy();
        assertNull(client.explainCode("test"));
    }
    
    @Test
    @DisplayName("detectPatterns returns empty list when unhealthy")
    void testDetectPatternsUnhealthy() throws LLMServiceException {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));
        
        client.isHealthy();
        assertTrue(client.detectPatterns("code").isEmpty());
    }
}
