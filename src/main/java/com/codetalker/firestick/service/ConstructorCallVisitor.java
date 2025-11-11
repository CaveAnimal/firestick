package com.codetalker.firestick.service;

import java.util.Set;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Collects constructor invocations as class names (simple or fully qualified), e.g.,
 *  - new Util() -> "Util"
 *  - new com.example.Util() -> "com.example.Util"
 */
public class ConstructorCallVisitor extends VoidVisitorAdapter<Set<String>> {
    @Override
    public void visit(ObjectCreationExpr oce, Set<String> out) {
        super.visit(oce, out);
        String type = oce.getType().asString();
        out.add(type);
    }
}
