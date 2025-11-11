package com.codetalker.firestick.controller;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DashboardControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void summaryReturnsOkAndHasStats() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> resp = rest.getForEntity("http://localhost:" + port + "/api/dashboard/summary", Map.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        @SuppressWarnings("unchecked")
    Map<String, Object> body = resp.getBody();
    assertThat(body).as("dashboard body").isNotNull();
    assertThat(body).as("dashboard keys").containsKeys("stats", "chart", "hotspots", "recentJobs");
    @SuppressWarnings("unchecked")
    Map<String, Object> stats = (Map<String, Object>) body.getOrDefault("stats", java.util.Collections.emptyMap());
        assertThat(stats).containsKeys("totalFiles", "totalClasses", "totalMethods", "hotspotCount");
    }
}
