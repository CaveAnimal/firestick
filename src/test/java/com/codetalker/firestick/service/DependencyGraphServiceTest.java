package com.codetalker.firestick.service;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
}
