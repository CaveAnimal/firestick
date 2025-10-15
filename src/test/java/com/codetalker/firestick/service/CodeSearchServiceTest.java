package com.codetalker.firestick.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CodeSearchServiceTest {

    @Autowired
    private CodeSearchService codeSearchService;

    @Test
    void testIndexAndSearchCode() throws Exception {
        // Index some code
        codeSearchService.indexCode("1", "public class Calculator { int add(int a, int b) { return a + b; } }");
        codeSearchService.indexCode("2", "public class StringUtils { String concat(String a, String b) { return a + b; } }");

        // Search for "Calculator"
        List<String> results = codeSearchService.searchCode("Calculator");

        assertThat(results).contains("1");
        assertThat(results).doesNotContain("2");
    }
}
