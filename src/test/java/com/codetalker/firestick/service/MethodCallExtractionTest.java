package com.codetalker.firestick.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

class MethodCallExtractionTest {

    @Test
    void collectsQualifiedCalls_andConstructorCalls() {
        String src = "class A { void f(){ Util.utilMethod(); this.helper(); new B().inst(); new com.example.C(); } void helper(){} }";
        JavaParser jp = new JavaParser();
        ParseResult<CompilationUnit> pr = jp.parse(src);
        Optional<CompilationUnit> cuOpt = pr.getResult();
        assertThat(cuOpt).isPresent();
        CompilationUnit cu = cuOpt.get();
        MethodDeclaration m = cu.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("f")).get();

        // Qualified calls
        Set<String> qcalls = new HashSet<>();
        new QualifiedMethodCallVisitor().visit(m, qcalls);
        assertThat(qcalls).contains("Util.utilMethod");
        // normalized new expr qualifier
        assertThat(qcalls).contains("B().inst");

        // Constructors
        Set<String> ctors = new HashSet<>();
        new ConstructorCallVisitor().visit(m, ctors);
        assertThat(ctors).contains("B");
        assertThat(ctors).contains("com.example.C");
    }
}
