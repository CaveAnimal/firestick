package com.codetalker.firestick.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for EmbeddingInfoController endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EmbeddingInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("GET /api/embedding/info returns current embedding configuration")
    void testEmbeddingInfoEndpoint() throws Exception {
        // Act & Assert - test with whatever mode is configured (likely MOCK in default profile)
        mockMvc.perform(get("/api/embedding/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.mode").exists())
                .andExpect(jsonPath("$.dimension").exists())
                .andExpect(jsonPath("$.modelPath").exists())
                .andExpect(jsonPath("$.tokenizerPath").exists())
                .andExpect(jsonPath("$.maxSeqLen").exists())
                .andExpect(jsonPath("$.activeProfiles").exists())
                .andExpect(jsonPath("$.activeProfiles").isArray());
    }
}

