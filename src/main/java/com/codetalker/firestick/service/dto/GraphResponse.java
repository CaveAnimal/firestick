package com.codetalker.firestick.service.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for dependency graph responses (simple nodes/edges + metadata).
 */
public class GraphResponse {
    public static class Node {
        public String id;
        public String label;
        public String type; // F, C, M

        public Node() {}
        public Node(String id, String label, String type) {
            this.id = id;
            this.label = label;
            this.type = type;
        }
    }

    public static class Edge {
        public String source;
        public String target;
        public String type; // e.g., contains, declares

        public Edge() {}
        public Edge(String source, String target, String type) {
            this.source = source;
            this.target = target;
            this.type = type;
        }
    }

    private List<Node> nodes;
    private List<Edge> edges;
    private Map<String, Object> metadata;

    public GraphResponse() {}

    public GraphResponse(List<Node> nodes, List<Edge> edges, Map<String, Object> metadata) {
        this.nodes = nodes;
        this.edges = edges;
        this.metadata = metadata;
    }

    public List<Node> getNodes() { return nodes; }
    public void setNodes(List<Node> nodes) { this.nodes = nodes; }
    public List<Edge> getEdges() { return edges; }
    public void setEdges(List<Edge> edges) { this.edges = edges; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
