package com.codetalker.firestick.controller;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IndexingBrowseControllerTest {

    @LocalServerPort
    private int port;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void browse_root_returns_system_roots_or_home_listing() throws Exception {
        URL url = new URL("http://localhost:" + port + "/api/indexing/browse");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.connect();

        assertThat(conn.getResponseCode()).isEqualTo(200);

        try (var in = conn.getInputStream()) {
            JsonNode root = om.readTree(in);
            // expecting a JSON { currentPath: string, entries: [ {name, path, isDirectory} ] }
            assertThat(root.has("currentPath")).isTrue();
            assertThat(root.has("entries")).isTrue();
            var entries = root.get("entries");
            assertThat(entries.isArray()).isTrue();
            assertThat(entries.size()).isGreaterThan(0);
            // At least one entry should be a directory
            boolean anyDir = false;
            for (var e : entries) {
                if (e.has("isDirectory") && e.get("isDirectory").asBoolean(false)) {
                    anyDir = true; break;
                }
            }
            assertThat(anyDir).isTrue();
        }
    }
}
