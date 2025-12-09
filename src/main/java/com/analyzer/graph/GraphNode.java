package com.analyzer.graph;

public interface GraphNode {
    String getId();           // Unique identifier
    String getType();         // "CLASS", "METHOD", "PACKAGE"
    String getDisplayName();  // Human-readable name
}
