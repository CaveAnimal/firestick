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

@SpringBootTest(properties = {
        "chroma.base-url=http://localhost:8000"
})
class ChromaServiceTest {

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
    void createCollection_sendsPost() throws Exception {
        server.expect(once(), requestTo("http://localhost:8000/api/v1/collections"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("{\"id\":\"col-1\"}", MediaType.APPLICATION_JSON));

        String resp = chromaService.createCollection("test-col");
        assertThat(resp).contains("col-1");
    }

    @Test
    void query_parsesDocuments() throws Exception {
        String responseJson = "{\n" +
                "  \"ids\": [[\"id1\", \"id2\"]],\n" +
                "  \"documents\": [[\"doc one\", \"doc two\"]],\n" +
                "  \"distances\": [[0.1, 0.2]]\n" +
                "}";

        server.expect(once(), requestTo("http://localhost:8000/api/v1/collections/test-col/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        List<String> docs = chromaService.query("test-col", new float[]{0.1f, 0.2f}, 2);
        assertThat(docs).containsExactly("doc one", "doc two");
    }
}
