package com.codetalker.firestick.service;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Guarded smoke test for ONNX mode. Skips automatically if model files are missing.
 */
@SpringBootTest(properties = {
        "embedding.mode=onnx",
        "embedding.dimension=384",
        "embedding.max-length=128",
        // Default model/tokenizer locations within repo
        "embedding.model-path=models/model_onnx/onnx/model.onnx",
        "embedding.tokenizer-path=models/model_onnx"
})
class EmbeddingServiceOnnxSmokeTest {

    @Autowired
    private EmbeddingService embeddingService;

    @Test
    void onnxProducesEmbedding_whenModelAvailable() {
        // Skip test if files do not exist (e.g., CI or env without models)
        Path model = Path.of("models/model_onnx/onnx/model.onnx");
        Path vocab = Path.of("models/model_onnx/vocab.txt");
        boolean enabled = Boolean.parseBoolean(System.getProperty("firestick.onnx.smoke", "false"));
        Assumptions.assumeTrue(enabled && Files.exists(model) && Files.exists(vocab),
                "Skipping ONNX smoke test: set -Dfirestick.onnx.smoke=true and ensure model/vocab files exist");

        float[] vec = embeddingService.getEmbedding("Hello from ONNX embedding test.");
        assertThat(vec).isNotNull();
        assertThat(vec.length).isGreaterThan(0);
        // Typically 384 for all-MiniLM-L6-v2
        assertThat(vec.length).isEqualTo(embeddingService.getDimension());
    }
}
