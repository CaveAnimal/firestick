package com.analyzer.graph;

import java.util.Objects;

public class PackageNode implements GraphNode {
    private String packageName;

    public PackageNode(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getId() {
        return packageName;
    }

    @Override
    public String getType() {
        return "PACKAGE";
    }

    @Override
    public String getDisplayName() {
        return packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageNode that = (PackageNode) o;
        return Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName);
    }

    // Getters and setters can be added as needed
}
