package com.analyzer.graph;

public class DependencyEdge {
    private EdgeType type;
    private int weight;
    private String metadata; // Optional additional info

    public DependencyEdge(EdgeType type) {
        this.type = type;
        this.weight = 1;
    }

    public DependencyEdge(EdgeType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public EdgeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DependencyEdge{" +
                "type=" + type +
                ", weight=" + weight +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
