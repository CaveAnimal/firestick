package com.codetalker.firestick.service;

import java.util.Set;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Visits method declarations and collects called method names (simple version).
 */
public class MethodCallVisitor extends VoidVisitorAdapter<Set<String>> {
    @Override
    public void visit(MethodCallExpr mc, Set<String> calledMethods) {
        super.visit(mc, calledMethods);
        if (mc.getName() != null) {
            calledMethods.add(mc.getNameAsString());
        }
    }

    // Usage:
    // Set<String> called = new HashSet<>();
    // new MethodCallVisitor().visit(methodDecl, called);
}
