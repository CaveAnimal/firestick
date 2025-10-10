package com.caveanimal.firestick.service;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CodeParserServiceTest {

    @Autowired
    private CodeParserService codeParserService;

    @Test
    void testParseJavaCode() {
        String javaCode = """
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello, World!");
                }
            }
            """;

        Optional<CompilationUnit> result = codeParserService.parseJavaCode(javaCode);

        assertThat(result).isPresent();
        assertThat(result.get().toString()).contains("HelloWorld");
    }
}
