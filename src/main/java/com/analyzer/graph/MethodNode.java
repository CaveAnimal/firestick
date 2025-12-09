package com.analyzer.graph;

import java.util.Objects;

public class MethodNode implements GraphNode {
    private String className;
    private String methodName;
    private String signature;
    private String returnType;

    public MethodNode(String className, String methodName, String signature, String returnType) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.returnType = returnType;
    }

    @Override
    public String getId() {
        return className + "." + methodName + signature;
    }

    @Override
    public String getType() {
        return "METHOD";
    }

    @Override
    public String getDisplayName() {
        return methodName + signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodNode that = (MethodNode) o;
        return Objects.equals(className, that.className) &&
               Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, signature);
    }

    // Getters and setters can be added as needed
}
