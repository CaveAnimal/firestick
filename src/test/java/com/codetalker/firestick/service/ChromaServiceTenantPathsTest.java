package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import static org.springframework.test.web.client.ExpectedCount.once;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.web.client.RestTemplate;

/**
 * Verifies ChromaService uses tenant/database namespaced routes when configured.
 */
@SpringBootTest(properties = {
    "chroma.base-url=http://localhost:8000",
    "chroma.tenant=foo",
    "chroma.database=bar",
    "embedding.mode=mock",
    "embedding.dimension=384"
})
class ChromaServiceTenantPathsTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ChromaService chromaService;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void usesNamespacedRoutes_whenTenantAndDatabaseProvided() {
        String base = "http://localhost:8000/api/v2/tenants/foo/databases/bar/collections";

        // create collection
        server.expect(once(), requestTo(base))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess("{\"id\":\"ns-col-1\"}", MediaType.APPLICATION_JSON));

        // add embeddings
        server.expect(once(), requestTo(base + "/mycol/add"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // query
        String responseJson = "{\n" +
            "  \"ids\": [[\"id1\"]],\n" +
            "  \"documents\": [[\"hello doc\"]],\n" +
            "  \"distances\": [[0.1]]\n" +
            "}";
        server.expect(once(), requestTo(base + "/mycol/query"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        String create = chromaService.createCollection("mycol");
        assertThat(create).contains("ns-col-1");

        chromaService.addEmbeddings("mycol", List.of(new float[]{0.1f, 0.2f}), List.of("hello doc"));
        List<String> docs = chromaService.query("mycol", new float[]{0.3f, 0.4f}, 1);
        assertThat(docs).containsExactly("hello doc");
    }
}
