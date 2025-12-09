package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CodeSearchServiceTest {

    @Autowired
    private CodeSearchService codeSearchService;

    @Test
    void testIndexAndSearchCode() throws Exception {
        String appName = "test-app-search";
        // Index some code
        codeSearchService.indexCode("1", appName, "public class Calculator { int add(int a, int b) { return a + b; } }");
        codeSearchService.indexCode("2", appName, "public class StringUtils { String concat(String a, String b) { return a + b; } }");

        // Search for "Calculator"
        List<String> results = codeSearchService.searchCode("Calculator", appName);

        assertThat(results).contains("1");
        assertThat(results).doesNotContain("2");
    }

    @Test
    void testGetAvailableApps() throws Exception {
        // Index code for a specific app
        codeSearchService.indexCode("app1-doc1", "app1", "content");
        
        List<String> apps = codeSearchService.getAvailableApps();
        assertThat(apps).contains("app1");
    }
}
