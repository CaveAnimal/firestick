package com.codetalker.firestick.llm;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

/**
 * Live integration tests for the LLM microservice. These tests run against a real
 * LLM instance listening on the URL configured by the system property `llm.service.url`
 * (defaults to http://127.0.0.1:8001). By default these tests are skipped unless
 * the LLM service is actually available.
 */
@DisplayName("LLM service - live integration tests (no mocks)")
class LLMServiceLiveTest {

    private static RestTemplateLLMServiceClient client;

    @BeforeAll
    static void init() {
        String url = System.getProperty("llm.service.url", "http://127.0.0.1:8001");
        RestTemplateBuilder builder = new RestTemplateBuilder();
        client = new RestTemplateLLMServiceClient(builder, url);
        // Skip all tests if live LLM is not running
        Assumptions.assumeTrue(client.isHealthy(), "LLM service not running on " + url);
    }

    @Test
    @DisplayName("explainCode returns a non-empty explanation for sample Java code")
    void testExplainCode() throws Exception {
        String code = "public class Hello { public void greet(){ System.out.println(\"hi\"); } }";
        String explanation = client.explainCode(code);
        assertThat(explanation).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("generateDocumentation returns non-empty Javadoc-like text")
    void testGenerateDocumentation() throws Exception {
        String code = "public class Calc { int add(int a, int b){ return a + b; } }";
        String docs = client.generateDocumentation(code);
        assertThat(docs).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("analyzeRelationship returns explanation string")
    void testAnalyzeRelationship() throws Exception {
        String result = client.analyzeRelationship("com.example.Foo", "com.example.Bar", "Calls on Bar in Foo");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("detectPatterns returns a list (may be empty if no patterns)")
    void testDetectPatterns() throws Exception {
        List<String> patterns = client.detectPatterns("public synchronized void foo() {} // suspicious lock");
        assertThat(patterns).isNotNull();
        // We allow empty results if the LLM doesn't detect patterns, but method must not throw
    }
}
