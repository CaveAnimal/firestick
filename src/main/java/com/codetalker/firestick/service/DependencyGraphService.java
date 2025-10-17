package com.codetalker.firestick.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

/**
 * Service for analyzing code dependencies using JGraphT.
 */
@Service
public class DependencyGraphService {

    /**
     * Create a simple dependency graph.
     *
     * @return A directed graph representing dependencies
     */
    public Graph<String, DefaultEdge> createDependencyGraph() {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Example: Add vertices
        graph.addVertex("Module A");
        graph.addVertex("Module B");
        graph.addVertex("Module C");

        // Example: Add edges (dependencies)
        graph.addEdge("Module A", "Module B");
        graph.addEdge("Module B", "Module C");

        return graph;
    }

    /**
     * Add a vertex to the graph.
     *
     * @param graph  The graph instance
     * @param vertex The vertex to add
     */
    public void addVertex(Graph<String, DefaultEdge> graph, String vertex) {
        graph.addVertex(vertex);
    }

    /**
     * Add an edge to the graph.
     *
     * @param graph  The graph instance
     * @param source The source vertex
     * @param target The target vertex
     */
    public void addEdge(Graph<String, DefaultEdge> graph, String source, String target) {
        graph.addEdge(source, target);
    }

    /**
     * Find dependencies for a given vertex.
     *
     * @param graph  The graph instance
     * @param vertex The vertex to analyze
     * @return A list of dependent vertices
     */
    public List<String> findDependencies(Graph<String, DefaultEdge> graph, String vertex) {
        return graph.outgoingEdgesOf(vertex).stream()
                .map(graph::getEdgeTarget)
                .collect(Collectors.toList());
    }
}
