package com.codetalker.firestick.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalker.firestick.service.struct.CodeStructureService;

@WebMvcTest(GraphController.class)
class GraphControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CodeFileRepository fileRepository;
    @MockBean private CodeChunkRepository chunkRepository;
    @MockBean private CodeStructureService codeStructureService;
    @MockBean private DependencyGraphService dependencyGraphService;

    @Test
    void basic_withData_shouldBuildFileClassMethodGraph() throws Exception {
        // Arrange: one file with a class and method chunk (metadata binds method -> class)
        CodeFile f = new CodeFile("src/Foo.java", Instant.now(), "hash");
        f.setId(1L);

        CodeChunk classChunk = new CodeChunk(f, "class Foo {}", 1, 1, "class",
            "Foo", "com.example.Foo", null, null, null);

        CodeChunk methodChunk = new CodeChunk(f, "void bar(int x){}", 2, 3, "method",
            "bar", "bar(int)", null, null, null);
        methodChunk.setMetadata("class=Foo");

        when(fileRepository.findAll()).thenReturn(List.of(f));
        when(chunkRepository.findByFile(f)).thenReturn(List.of(classChunk, methodChunk));

        // Act & Assert
        mockMvc.perform(get("/api/graph/basic").param("limit", "10"))
            .andExpect(status().isOk())
            // nodes and edges present
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.edges").isArray())
            // metadata counts > 0
            .andExpect(jsonPath("$.metadata.files").value(org.hamcrest.Matchers.greaterThan(0)))
            .andExpect(jsonPath("$.metadata.classes").value(org.hamcrest.Matchers.greaterThan(0)))
            .andExpect(jsonPath("$.metadata.methods").value(org.hamcrest.Matchers.greaterThan(0)))
            .andExpect(jsonPath("$.metadata.edges").value(org.hamcrest.Matchers.greaterThan(1)));
    }

    @Test
    void enriched_withFilters_shouldRespectIncludeAndNodeTypes() throws Exception {
        // Arrange a small graph with F, C, M, I vertices and typical edges
        var g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        String fNode = "F:src/Foo.java";
        String cNode = "C:com.example.Foo";
        String mNode = "M:com.example.Foo#bar(int)";
        String iNode = "I:java.util.List";
        g.addVertex(fNode);
        g.addVertex(cNode);
        g.addVertex(mNode);
        g.addVertex(iNode);
        g.addEdge(fNode, cNode); // declares
        g.addEdge(cNode, mNode); // contains
        g.addEdge(fNode, iNode); // imports

        when(fileRepository.findAll()).thenReturn(List.of());
        when(chunkRepository.findByFile(any())).thenReturn(List.of());
        when(dependencyGraphService.buildFromParsedFiles(any(), any())).thenReturn(g);
        when(dependencyGraphService.getMetadata()).thenReturn(java.util.Map.of("source", "mock"));

        // include both containment and imports; allow all node types
        mockMvc.perform(get("/api/graph/enriched")
                .param("include", "containment,imports")
                .param("nodeTypes", "F,C,M,I"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.edges").isArray())
            .andExpect(jsonPath("$.metadata.nodes").value(4))
            .andExpect(jsonPath("$.metadata.edges").value(3))
            .andExpect(jsonPath("$.metadata.includeImports").value(true))
            .andExpect(jsonPath("$.metadata.includeContainment").value(true));

        // Only methods should be included when nodeTypes=M
        mockMvc.perform(get("/api/graph/enriched").param("nodeTypes", "M"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.nodes[0].type").value("M"));

        // Include containment only should filter out imports edge
        mockMvc.perform(get("/api/graph/enriched").param("include", "containment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.edges").isArray())
            .andExpect(jsonPath("$.metadata.includeImports").value(false))
            .andExpect(jsonPath("$.metadata.includeContainment").value(true));
    }
}
