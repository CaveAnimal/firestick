package com.codetalker.firestick.service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for generating text embeddings. Supports a 'mock' mode for tests and an 'onnx' mode scaffold.
 */
@Service
public class EmbeddingService {

    public enum Mode { MOCK, ONNX }

    private final Mode mode;
    private final int dimension;
    private final String modelPath;
    private final String tokenizerPath;

    public EmbeddingService(
            @Value("${embedding.mode:mock}") String mode,
            @Value("${embedding.dimension:384}") int dimension,
            @Value("${embedding.model-path:}") String modelPath,
            @Value("${embedding.tokenizer-path:}") String tokenizerPath
    ) {
        this.mode = Mode.valueOf(mode.trim().toUpperCase());
        this.dimension = dimension;
        this.modelPath = modelPath;
        this.tokenizerPath = tokenizerPath;
    }

    /**
     * Return an embedding vector for the given text.
     * In MOCK mode, returns a deterministic pseudo-embedding without external dependencies.
     * In ONNX mode, this method is intended to run the ONNX model (not enabled by default here).
     */
    public float[] getEmbedding(String text) {
        Objects.requireNonNull(text, "text");
        return switch (mode) {
            case MOCK -> mockEmbedding(text, dimension);
            case ONNX -> throw new UnsupportedOperationException("ONNX mode not yet configured. Provide model/tokenizer and implementation.");
        };
    }

    private static float[] mockEmbedding(String text, int dim) {
        // Deterministic: seed from text bytes
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        long seed = 1125899906842597L; // a prime-ish seed
        for (byte b : bytes) seed = 31L * seed + b;
        Random rnd = new Random(seed);

        float[] vec = new float[dim];
        // Simple hashed bag-of-words into fixed-size vector
        String[] tokens = text.toLowerCase().split("\\s+");
        for (String tok : tokens) {
            int idx = Math.floorMod(tok.hashCode(), dim);
            // bounded random magnitude per token for variety but deterministic per token position
            vec[idx] += (float) (rnd.nextGaussian() * 0.1);
        }
        // L2 normalize for stability
        double norm = 0.0;
        for (float v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < vec.length; i++) vec[i] /= (float) norm;
        }
        return vec;
    }

    public Mode getMode() { return mode; }
    public int getDimension() { return dimension; }
    public String getModelPath() { return modelPath; }
    public String getTokenizerPath() { return tokenizerPath; }
}
