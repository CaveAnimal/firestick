package com.analyzer.graph;

import java.util.List;
import java.util.Objects;

public class ClassNode implements GraphNode {
    private String fullyQualifiedName;
    private String packageName;
    private String superClassName;
    private List<String> interfaces;

    public ClassNode(String fullyQualifiedName, String packageName, String superClassName, List<String> interfaces) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.packageName = packageName;
        this.superClassName = superClassName;
        this.interfaces = interfaces;
    }

    @Override
    public String getId() {
        return fullyQualifiedName;
    }

    @Override
    public String getType() {
        return "CLASS";
    }

    @Override
    public String getDisplayName() {
        return fullyQualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassNode classNode = (ClassNode) o;
        return Objects.equals(fullyQualifiedName, classNode.fullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedName);
    }

    // Getters and setters can be added as needed
}
