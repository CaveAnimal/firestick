package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DependencyGraphServiceTest {

    @Autowired
    private DependencyGraphService dependencyGraphService;

    @Test
    void testCreateDependencyGraph() {
        Graph<String, DefaultEdge> graph = dependencyGraphService.createDependencyGraph();

        assertThat(graph.vertexSet()).hasSize(3);
        assertThat(graph.edgeSet()).hasSize(2);
        assertThat(graph.containsVertex("Module A")).isTrue();
        assertThat(graph.containsVertex("Module B")).isTrue();
        assertThat(graph.containsVertex("Module C")).isTrue();
    }

    @Test
    void testAddVertexAndEdge() {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        dependencyGraphService.addVertex(graph, "Module X");
        dependencyGraphService.addVertex(graph, "Module Y");
        dependencyGraphService.addEdge(graph, "Module X", "Module Y");

        assertThat(graph.vertexSet()).contains("Module X", "Module Y");
        assertThat(graph.edgeSet()).hasSize(1);
    }

    @Test
    void testFindDependencies() {
        Graph<String, DefaultEdge> graph = dependencyGraphService.createDependencyGraph();

        List<String> dependencies = dependencyGraphService.findDependencies(graph, "Module A");

        assertThat(dependencies).containsExactly("Module B");
    }
}
