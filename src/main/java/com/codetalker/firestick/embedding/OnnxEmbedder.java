package com.codetalker.firestick.embedding;

import java.io.Closeable;
import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

/**
 * Lightweight ONNX embedder for BERT-like sentence-transformers.
 * Inputs: input_ids, attention_mask (and token_type_ids if required; zeros).
 * Output: last_hidden_state [1, seq_len, hidden] -> mean-pool via attention.
 */
public class OnnxEmbedder implements Closeable {
    private final OrtEnvironment env;
    private final OrtSession session;
    private final WordPieceTokenizer tokenizer;
    private final int hiddenSize;

    public OnnxEmbedder(Path onnxModel,
                        Path vocabTxt,
                        int maxSeqLen,
                        int hiddenSize) throws IOException, OrtException {
        Objects.requireNonNull(onnxModel, "onnxModel");
        Objects.requireNonNull(vocabTxt, "vocabTxt");
        if (!Files.exists(onnxModel)) throw new IOException("ONNX model not found: " + onnxModel);
        if (!Files.exists(vocabTxt)) throw new IOException("vocab.txt not found: " + vocabTxt);

        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(onnxModel.toString(), new OrtSession.SessionOptions());
        this.tokenizer = new WordPieceTokenizer(
                vocabTxt,
                "[UNK]", "[CLS]", "[SEP]", "[PAD]",
                maxSeqLen
        );
        this.hiddenSize = hiddenSize;
    }

    public float[] embed(String text) throws OrtException {
        WordPieceTokenizer.Result tok = tokenizer.encode(text);
        long[] shape = new long[] {1, tok.inputIds().length};

        // input_ids
        long[] idsLong = new long[tok.inputIds().length];
        for (int i = 0; i < idsLong.length; i++) idsLong[i] = tok.inputIds()[i];
        OnnxTensor inputIds = OnnxTensor.createTensor(env, LongBuffer.wrap(idsLong), shape);

        // attention_mask
        long[] maskLong = new long[tok.attentionMask().length];
        for (int i = 0; i < maskLong.length; i++) maskLong[i] = tok.attentionMask()[i];
        OnnxTensor attentionMask = OnnxTensor.createTensor(env, LongBuffer.wrap(maskLong), shape);

        Map<String, OnnxTensor> inputs = new HashMap<>();
        // Try common input names; models may vary. We'll add both and let the model pick.
        inputs.put("input_ids", inputIds);
        inputs.put("attention_mask", attentionMask);
        if (hasInput("token_type_ids")) {
            // all zeros
            long[] zeros = new long[(int) shape[1]];
            OnnxTensor tokenType = OnnxTensor.createTensor(env, LongBuffer.wrap(zeros), shape);
            inputs.put("token_type_ids", tokenType);
        }

        try (OrtSession.Result result = session.run(inputs)) {
            // Take first output (commonly last_hidden_state)
            Object out0 = result.get(0).getValue();
            if (out0 instanceof float[][][] lastHidden) {
                return meanPool(lastHidden, tok.attentionMask());
            } else if (out0 instanceof float[][] pooled) {
                // Some models output pooled [1, hidden]
                return pooled[0];
            } else {
                throw new OrtException("Unexpected ONNX output type: " + out0.getClass());
            }
        }
    }

    private boolean hasInput(String name) {
        try {
            for (var i : session.getInputInfo().entrySet()) {
                if (i.getKey().equals(name)) return true;
            }
        } catch (OrtException e) {
            return false;
        }
        return false;
    }

    private float[] meanPool(float[][][] lastHidden, int[] attentionMask) {
        int seqLen = lastHidden[0].length;
        int dim = lastHidden[0][0].length;
        float[] sum = new float[dim];
        int tokCount = 0;
        for (int t = 0; t < seqLen; t++) {
            if (t < attentionMask.length && attentionMask[t] == 1) {
                tokCount++;
                float[] tokenVec = lastHidden[0][t];
                for (int d = 0; d < dim; d++) sum[d] += tokenVec[d];
            }
        }
        if (tokCount == 0) tokCount = 1;
        for (int d = 0; d < sum.length; d++) sum[d] /= tokCount;
        // Optionally L2 normalize
        double norm = 0.0;
        for (float v : sum) norm += v*v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < sum.length; i++) sum[i] /= (float) norm;
        }
        return sum;
    }

    public int getHiddenSize() { return hiddenSize; }

    @Override
    public void close() throws IOException {
        try {
            session.close();
        } catch (OrtException ignored) {}
        env.close();
    }
}
