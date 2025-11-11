package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.DependencyGraphService;
import com.codetalker.firestick.service.struct.CodeStructureService;

@WebMvcTest(GraphController.class)
@SuppressWarnings({"removal", "deprecation", "unused"})
class GraphControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CodeFileRepository fileRepository;
    @MockBean private CodeChunkRepository chunkRepository;
    @MockBean private CodeStructureService codeStructureService;
    @MockBean private DependencyGraphService dependencyGraphService;

    @Test
    void basic_withLimitZero_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/graph/basic").param("limit", "0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void enriched_withLimitZero_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/graph/enriched").param("limit", "0"))
            .andExpect(status().isBadRequest());
    }
}
