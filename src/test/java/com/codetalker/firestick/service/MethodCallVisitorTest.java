package com.codetalker.firestick.service;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

class MethodCallVisitorTest {

    @Test
    void collectsCalledMethodNamesWithinClass() {
        String code = """
            package com.example;
            public class Sample {
                void a() { b(); c(42); }
                void b() { }
                void c(int x) { this.b(); d().toString(); }
                String d() { return \"ok\"; }
            }
        """;

        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        ClassOrInterfaceDeclaration cls = cu.getClassByName("Sample").orElseThrow();
        MethodDeclaration methodA = cls.getMethodsByName("a").get(0);
        MethodDeclaration methodC = cls.getMethodsByName("c").get(0);

        Set<String> calledInA = new HashSet<>();
        new MethodCallVisitor().visit(methodA, calledInA);
        assertThat(calledInA).contains("b", "c");

        Set<String> calledInC = new HashSet<>();
        new MethodCallVisitor().visit(methodC, calledInC);
        // this.b() -> "b", d().toString() -> method names "d" and "toString"
        assertThat(calledInC).contains("b", "d", "toString");
    }
}
