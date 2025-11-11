package com.codetalker.firestick.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.embedding.OnnxEmbedder;

/**
 * Service for generating text embeddings. Supports a 'mock' mode for tests and an 'onnx' mode scaffold.
 */
@Service
public class EmbeddingService {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    public enum Mode { MOCK, ONNX }

    private final Mode mode;
    private final int dimension;
    private final String modelPath;
    private final String tokenizerPath;
    private final int maxSeqLen;

    private volatile OnnxEmbedder onnx;

    public EmbeddingService(
            @Value("${embedding.mode:mock}") String mode,
            @Value("${embedding.dimension:384}") int dimension,
            @Value("${embedding.model-path:}") String modelPath,
            @Value("${embedding.tokenizer-path:}") String tokenizerPath,
            @Value("${embedding.max-length:128}") int maxSeqLen
    ) {
        this.mode = Mode.valueOf(mode.trim().toUpperCase());
        this.dimension = dimension;
        this.modelPath = modelPath;
        this.tokenizerPath = tokenizerPath;
        this.maxSeqLen = maxSeqLen;
    log.info("[Embedding] Initialized mode={}, dim={}, modelPath={}, tokenizerPath={}", this.mode, this.dimension, 
        (this.modelPath == null || this.modelPath.isBlank() ? "<unset>" : this.modelPath),
        (this.tokenizerPath == null || this.tokenizerPath.isBlank() ? "<unset>" : this.tokenizerPath));
    }

    /**
     * Generate embeddings for a list of texts. Order is preserved; each output vector corresponds
     * to the text at the same index in {@code texts}. For MOCK mode this simply loops over
     * {@link #getEmbedding(String)}; for ONNX mode this currently performs one-by-one inference
     * (batching can be optimized later).
     */
    public List<float[]> getEmbeddings(List<String> texts) {
        if (texts == null) return Collections.emptyList();
        List<float[]> out = new ArrayList<>(texts.size());
        for (String t : texts) {
            out.add(getEmbedding(Objects.requireNonNullElse(t, "")));
        }
        return out;
    }

    /**
     * Return an embedding vector for the given text.
     * In MOCK mode, returns a deterministic pseudo-embedding without external dependencies.
     * In ONNX mode, this method is intended to run the ONNX model (not enabled by default here).
     */
    public float[] getEmbedding(String text) {
        Objects.requireNonNull(text, "text");
        switch (mode) {
            case MOCK -> {
                float[] vec = mockEmbedding(text, dimension);
                log.debug("[Embedding] Generated MOCK embedding (len={}, dim={})", text.length(), dimension);
                return vec;
            }
            case ONNX -> {
                try {
                    ensureOnnx();
                    float[] vec = onnx.embed(text);
                    if (vec.length != dimension) {
                        log.warn("[Embedding] ONNX produced dim={}, configured dim={}. Continuing.", vec.length, dimension);
                    }
                    return vec;
                } catch (Exception e) {
                    log.error("[Embedding] ONNX inference error: {}", e.getMessage(), e);
                    throw new RuntimeException("ONNX inference failed", e);
                }
            }
            default -> {
                log.error("[Embedding] Unknown mode: {}", mode);
                throw new IllegalStateException("Unknown embedding mode: " + mode);
            }
        }
    }

    private void ensureOnnx() throws Exception {
        if (onnx != null) return;
        synchronized (this) {
            if (onnx == null) {
                if (modelPath == null || modelPath.isBlank()) {
                    throw new IllegalStateException("embedding.model-path is required for ONNX mode");
                }
                if (tokenizerPath == null || tokenizerPath.isBlank()) {
                    throw new IllegalStateException("embedding.tokenizer-path is required for ONNX mode (folder containing vocab.txt)");
                }
                Path model = Path.of(modelPath);
                Path vocab = Path.of(tokenizerPath).resolve("vocab.txt");
                this.onnx = new OnnxEmbedder(model, vocab, maxSeqLen, dimension);
                log.info("[Embedding] ONNX initialized. model={}, vocab={}, maxLen={}, dim={}", model, vocab, maxSeqLen, dimension);
            }
        }
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
    public int getMaxSeqLen() { return maxSeqLen; }
}
