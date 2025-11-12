package com.codetalker.firestick.embedding;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Minimal WordPiece tokenizer for BERT-like models using a vocab.txt file.
 * - Performs basic lowercasing
 * - Splits on whitespace and punctuation
 * - Applies greedy longest-match WordPiece with "##" continuation
 */
public class WordPieceTokenizer {
    private final Map<String, Integer> vocab;
    private final int maxLength;

    private final int unkId;
    private final int clsId;
    private final int sepId;
    private final int padId;

    public WordPieceTokenizer(Path vocabTxt,
                              String unkToken,
                              String clsToken,
                              String sepToken,
                              String padToken,
                              int maxLength) throws IOException {
        Objects.requireNonNull(vocabTxt, "vocabTxt");
        this.vocab = loadVocab(vocabTxt);
        this.maxLength = maxLength;

        this.unkId = idOrThrow(unkToken);
        this.clsId = idOrThrow(clsToken);
        this.sepId = idOrThrow(sepToken);
        this.padId = idOrThrow(padToken);
    }

    private Map<String, Integer> loadVocab(Path path) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    map.put(line, idx++);
                }
            }
        }
        return map;
    }

    private int idOrThrow(String token) {
        Integer id = vocab.get(token);
        if (id == null) throw new IllegalArgumentException("Token not in vocab: " + token);
        return id;
    }

    public Result encode(String text) {
        List<Integer> ids = new ArrayList<>();
        List<Integer> mask = new ArrayList<>();

        // reserve space: [CLS] ... tokens ... [SEP]
        ids.add(clsId); mask.add(1);

        for (String word : basicTokenize(text)) {
            // Greedy longest-match for wordpiece
            int start = 0;
            while (start < word.length()) {
                int end = word.length();
                Integer subId = null;
                String subToken = null;
                while (start < end) {
                    String piece = word.substring(start, end);
                    if (start > 0) piece = "##" + piece;
                    Integer vid = vocab.get(piece);
                    if (vid != null) { subId = vid; subToken = piece; break; }
                    end -= 1;
                }
                if (subId == null) { // fallback to [UNK] and break this word
                    ids.add(unkId); mask.add(1);
                    break;
                } else {
                    ids.add(subId); mask.add(1);
                    int stepLen = subToken.startsWith("##") ? subToken.length() - 2 : subToken.length();
                    start += stepLen;
                }
            }
        }

        // add [SEP]
        ids.add(sepId); mask.add(1);

        // Truncate
        if (ids.size() > maxLength) {
            ids = ids.subList(0, maxLength);
            mask = mask.subList(0, maxLength);
        }

        // Pad
        while (ids.size() < maxLength) {
            ids.add(padId); mask.add(0);
        }

        int[] inputIds = ids.stream().mapToInt(Integer::intValue).toArray();
        int[] attentionMask = mask.stream().mapToInt(Integer::intValue).toArray();
        return new Result(inputIds, attentionMask);
    }

    private List<String> basicTokenize(String text) {
        List<String> toks = new ArrayList<>();
        String lower = text == null ? "" : text.toLowerCase(Locale.ROOT);
        // Split on non-word characters to approximate basic tokenization
        for (String t : lower.split("[^a-z0-9_]+")) {
            if (!t.isEmpty()) toks.add(t);
        }
        return toks;
    }

    public record Result(int[] inputIds, int[] attentionMask) {}
}
