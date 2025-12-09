package com.codetalker.firestick.service;

import java.util.Set;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Collects qualified method calls in the form "Qualifier.method".
 * Examples:
 *  - Util.utilMethod()    -> "Util.utilMethod"
 *  - com.example.Util.m() -> "com.example.Util.m"
 *  - new Util().inst()    -> "Util().inst" (normalize object creation to ClassName())
 */
public class QualifiedMethodCallVisitor extends VoidVisitorAdapter<Set<String>> {
    @Override
    public void visit(MethodCallExpr mc, Set<String> out) {
        super.visit(mc, out);
        if (mc.getName() == null) return;
        if (mc.getScope().isEmpty()) return;
        Expression scope = mc.getScope().get();
        String qualifier;
        if (scope.isObjectCreationExpr()) {
            ObjectCreationExpr oce = scope.asObjectCreationExpr();
            String type = oce.getType().asString();
            qualifier = type + "()"; // normalize "new T()" -> "T()"
        } else {
            qualifier = scope.toString();
        }
        String method = mc.getNameAsString();
        out.add(qualifier + "." + method);
    }
}
