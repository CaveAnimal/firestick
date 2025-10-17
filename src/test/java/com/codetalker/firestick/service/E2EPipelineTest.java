package com.codetalker.firestick.service;

import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.service.CodeParserService;
import com.codetalker.firestick.service.EmbeddingService;
import com.codetalker.firestick.service.ChromaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class E2EPipelineTest {
    @Autowired
    private CodeParserService codeParserService;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private ChromaService chromaService;

    @Test
    void testPipelineIntegration() throws Exception {
        // 1. Parse sample Java file
        Path samplePath = Path.of("src/test/resources/test-data/sample-code/HelloWorld.java");
        assertTrue(Files.exists(samplePath), "Sample file should exist");
        CodeFile codeFile = codeParserService.parseFile(samplePath.toString());
        assertNotNull(codeFile);
        List<CodeChunk> chunks = codeFile.getChunks();
        assertFalse(chunks.isEmpty(), "Code chunks should be extracted");

        // 2. Generate embedding for first chunk
        String chunkText = chunks.get(0).getContent();
        float[] embedding = embeddingService.getEmbedding(chunkText);
        assertEquals(384, embedding.length, "Embedding should be 384-dim");

        // 3. Store in Chroma
        String collection = "test-pipeline";
        chromaService.createCollection(collection);
        chromaService.addEmbeddings(collection, List.of(embedding), List.of(chunkText));

        // 4. Query Chroma
        List<String> results = chromaService.query(collection, embedding, 1);
        assertFalse(results.isEmpty(), "Chroma should return results");
        assertTrue(results.get(0).contains("Hello, world!"), "Result should contain expected text");
    }
}
