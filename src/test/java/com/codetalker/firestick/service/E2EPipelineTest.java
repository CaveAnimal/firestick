package com.codetalker.firestick.service;

import java.nio.file.Files;
import java.nio.file.Path;
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

import com.codetalker.firestick.model.CodeFile;

@SpringBootTest(properties = {
	"chroma.base-url=http://localhost:8000",
	"embedding.mode=mock",
	"embedding.dimension=384"
})
class E2EPipelineTest {

    @Autowired
    private CodeParserService codeParserService;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private ChromaService chromaService;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setup() {
	server = MockRestServiceServer.createServer(restTemplate);
    }

    @AfterEach
    void tearDown() {
	server.verify();
    }

    @Test
	void pipeline_parse_embed_store_query() throws Exception {
	// Arrange HTTP mocks for Chroma v2 endpoints
	server.expect(once(), requestTo("http://localhost:8000/api/v2/collections"))
		.andExpect(method(HttpMethod.POST))
		.andRespond(withSuccess("{\"id\":\"col-xyz\"}", MediaType.APPLICATION_JSON));

	server.expect(once(), requestTo("http://localhost:8000/api/v2/collections/test-pipeline/add"))
		.andExpect(method(HttpMethod.POST))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

	String responseJson = "{\n" +
		"  \"ids\": [[\"id1\", \"id2\"]],\n" +
		"  \"documents\": [[\"public class HelloWorld {\\n    public static void main(String[] args) {\\n        System.out.println(\\\"Hello, world!\\\");\\n    }\\n}\",\n" +
		"                 \"public class Calculator {\\n    public static int add(int a, int b) {\\n        return a + b;\\n    }\\n    public static void main(String[] args) {\\n        int result = add(19, 23);\\n        System.out.println(\\\"Sum is 42\\\");\\n    }\\n}\"]],\n" +
		"  \"distances\": [[0.01, 0.02]]\n" +
		"}";
	server.expect(once(), requestTo("http://localhost:8000/api/v2/collections/test-pipeline/query"))
		.andExpect(method(HttpMethod.POST))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

	// 1) Parse sample Java files
	Path hwPath = Path.of("src/test/resources/test-data/sample-code/HelloWorld.java");
	Path calcPath = Path.of("src/test/resources/test-data/sample-code/Calculator.java");
	assertThat(Files.exists(hwPath)).as("HelloWorld.java should exist").isTrue();
	assertThat(Files.exists(calcPath)).as("Calculator.java should exist").isTrue();

	CodeFile hwFile = codeParserService.parseFile(hwPath.toString());
	CodeFile calcFile = codeParserService.parseFile(calcPath.toString());
	assertThat(hwFile.getChunks()).isNotEmpty();
	assertThat(calcFile.getChunks()).isNotEmpty();

	// 2) Generate embedding for first chunk of each
	String hwChunk = hwFile.getChunks().get(0).getContent();
	String calcChunk = calcFile.getChunks().get(0).getContent();
	float[] emb1 = embeddingService.getEmbedding(hwChunk);
	float[] emb2 = embeddingService.getEmbedding(calcChunk);
	assertThat(emb1.length).isEqualTo(384);
	assertThat(emb2.length).isEqualTo(384);

	// 3) Store in Chroma and 4) Query
	String collection = "test-pipeline";
	chromaService.createCollection(collection);
	chromaService.addEmbeddings(collection, List.of(emb1, emb2), List.of(hwChunk, calcChunk));
	List<String> results = chromaService.query(collection, emb1, 2);

	// 5) Verify
	assertThat(results).isNotEmpty();
	assertThat(results.size()).isGreaterThanOrEqualTo(2);
	assertThat(String.join("\n", results)).contains("Hello, world!");
	assertThat(String.join("\n", results)).contains("class Calculator");
    }
}
