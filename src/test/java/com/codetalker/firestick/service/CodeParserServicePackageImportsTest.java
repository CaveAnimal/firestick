package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.github.javaparser.ast.CompilationUnit;

class CodeParserServicePackageImportsTest {

    @Test
    void extractsPackageAndImportsFromSource() {
        String src = """
            package com.example.util;\n
            import java.util.List;\n
            import static java.lang.System.out;\n
            public class Foo {\n
                void bar() { out.println(List.of(1,2,3)); }\n
            }\n
        """;

        CodeParserService svc = new CodeParserService();
        CompilationUnit cu = svc.parseJavaCode(src).orElseThrow();

        String pkg = svc.extractPackage(cu);
        assertThat(pkg).isEqualTo("com.example.util");

        List<String> imports = svc.extractImports(cu);
        assertThat(imports).contains("java.util.List");

        List<String> staticImports = svc.extractStaticImports(cu);
        // JavaParser getNameAsString() includes the member for static imports
        assertThat(staticImports).contains("java.lang.System.out");
    }
}
