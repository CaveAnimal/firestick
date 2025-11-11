package com.codetalker.firestick.service;

/** Utility helpers for Chroma collection naming across apps. */
public final class ChromaUtil {
    private ChromaUtil() {}

    /**
     * Produce a stable, safe collection name for a given app. Pattern: "code_{sanitizedApp}".
     * Sanitization: lower-case, replace non-alphanumeric with underscore, collapse repeats, trim underscores.
     */
    public static String collectionForApp(String appName) {
        String raw = appName == null || appName.isBlank() ? "default" : appName.trim().toLowerCase();
        String sanitized = raw.replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_");
        sanitized = trimUnderscore(sanitized);
        return "code_" + sanitized;
    }

    private static String trimUnderscore(String s) {
        int start = 0, end = s.length();
        while (start < end && s.charAt(start) == '_') start++;
        while (end > start && s.charAt(end - 1) == '_') end--;
        return s.substring(start, end);
    }
}
