package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalker.firestick.service.struct.CodeStructureService;

@WebMvcTest(GraphController.class)
@SuppressWarnings({"removal"})
class GraphControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CodeFileRepository fileRepository;
    @MockBean private CodeChunkRepository chunkRepository;
    @MockBean private CodeStructureService codeStructureService;
    @MockBean private DependencyGraphService dependencyGraphService;

    @Test
    void basic_withoutLimit_ShouldReturnEmptyGraph() throws Exception {
        mockMvc.perform(get("/api/graph/basic"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.edges").isArray());
    }
}
