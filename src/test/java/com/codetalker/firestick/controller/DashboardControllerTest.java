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
 * Tests for DashboardController endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/dashboard/summary returns dashboard data")
    void summaryReturnsOkAndHasStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.stats").exists())
                .andExpect(jsonPath("$.stats.totalFiles").exists())
                .andExpect(jsonPath("$.stats.totalClasses").exists())
                .andExpect(jsonPath("$.stats.totalMethods").exists())
                .andExpect(jsonPath("$.stats.hotspotCount").exists())
                .andExpect(jsonPath("$.chart").exists())
                .andExpect(jsonPath("$.hotspots").exists())
                .andExpect(jsonPath("$.recentJobs").exists());
    }
}
