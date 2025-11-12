package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "embedding.mode=mock",
    "embedding.dimension=384"
})
class EmbeddingServiceBatchTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void getEmbeddings_returnsOnePerInput_andPreservesOrder() {
        List<String> texts = List.of("alpha beta", "gamma delta");
        List<float[]> embeddings = embeddingService.getEmbeddings(texts);

        assertThat(embeddings).hasSize(2);
        assertThat(embeddings.get(0)).hasSize(embeddingService.getDimension());
        assertThat(embeddings.get(1)).hasSize(embeddingService.getDimension());

        // Deterministic in MOCK mode: multiple calls should match exactly
        List<float[]> embeddings2 = embeddingService.getEmbeddings(texts);
        assertThat(embeddings2).hasSize(2);
        assertThat(embeddings2.get(0)).containsExactly(embeddings.get(0));
        assertThat(embeddings2.get(1)).containsExactly(embeddings.get(1));
    }

    @Test
    void getEmbeddings_emptyInput_returnsEmptyList() {
        List<float[]> embeddings = embeddingService.getEmbeddings(List.of());
        assertThat(embeddings).isEmpty();
    }
}
