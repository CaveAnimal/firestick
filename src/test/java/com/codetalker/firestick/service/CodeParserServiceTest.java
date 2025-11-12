package com.codetalker.firestick.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.javaparser.ast.CompilationUnit;

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
    @Test
    void testExtractMethodAndClassDetails() {
        String javaCode = """
            /**
             * Example class JavaDoc
             */
            public class Example {
                /**
                 * Example method JavaDoc
                 */
                @Deprecated
                public void foo(int x) {}
            }
            """;

        Optional<CompilationUnit> result = codeParserService.parseJavaCode(javaCode);
        assertThat(result).isPresent();
        com.codetalker.firestick.model.CodeFile codeFile = new com.codetalker.firestick.model.CodeFile("Example.java", java.time.Instant.now(), "hash");
        CodeChunkingService chunker = new CodeChunkingService();
        java.util.List<com.codetalker.firestick.model.CodeChunk> chunks = chunker.extractChunks(codeFile, result.get());

        // Find class chunk
        com.codetalker.firestick.model.CodeChunk classChunk = chunks.stream().filter(c -> "class".equals(c.getType())).findFirst().orElse(null);
        assertThat(classChunk).isNotNull();
        assertThat(classChunk.getName()).isEqualTo("Example");
        assertThat(classChunk.getJavaDoc()).contains("Example class JavaDoc");

        // Find method chunk
        com.codetalker.firestick.model.CodeChunk methodChunk = chunks.stream().filter(c -> "method".equals(c.getType())).findFirst().orElse(null);
        assertThat(methodChunk).isNotNull();
        assertThat(methodChunk.getName()).isEqualTo("foo");
    assertThat(methodChunk.getSignature()).contains("void foo(int)");
        assertThat(methodChunk.getModifiers()).contains("public");
        assertThat(methodChunk.getAnnotations()).contains("Deprecated");
        assertThat(methodChunk.getJavaDoc()).contains("Example method JavaDoc");
    }
        @Test
        void testExtractPackageAndImports() {
            String javaCode = """
                package com.example.test;
                import java.util.List;
                import static java.lang.Math.*;
                public class TestClass {}
                """;
            Optional<CompilationUnit> result = codeParserService.parseJavaCode(javaCode);
            assertThat(result).isPresent();
            String pkg = codeParserService.extractPackage(result.get());
            assertThat(pkg).isEqualTo("com.example.test");
            java.util.List<String> imports = codeParserService.extractImports(result.get());
            assertThat(imports).anyMatch(s -> s.contains("java.util.List"));
            // Static imports are now returned via extractStaticImports (normalized, no 'static ' or trailing '.*')
            java.util.List<String> staticImports = codeParserService.extractStaticImports(result.get());
            assertThat(staticImports).anyMatch(s -> s.contains("java.lang.Math"));
        }
}
