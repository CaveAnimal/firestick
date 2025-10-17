package com.codetalker.firestick.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "embedding.mode=mock",
        "embedding.dimension=384"
})
class EmbeddingServiceTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void returnsDeterministicMockEmbeddings() {
        float[] v1 = embeddingService.getEmbedding("hello world");
        float[] v2 = embeddingService.getEmbedding("hello world");

        assertThat(v1).hasSize(embeddingService.getDimension());
        assertThat(v2).hasSize(embeddingService.getDimension());
        assertThat(v1).containsExactly(v2);

        float[] v3 = embeddingService.getEmbedding("different text");
        // Not necessarily orthogonal, but should differ
        assertThat(v3).isNotEqualTo(v1);
    }
}
