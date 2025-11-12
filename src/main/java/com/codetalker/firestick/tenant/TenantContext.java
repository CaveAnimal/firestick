package com.codetalker.firestick.tenant;

/**
 * Simple per-request tenant context using ThreadLocal to carry the selected application name.
 * Controllers can still accept an explicit `app` parameter; this context is mainly for
 * cross-cutting concerns and downstream services that prefer implicit access.
 */
public final class TenantContext {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
    public static final String DEFAULT_TENANT = "default";

    private TenantContext() {}

    public static void set(String appName) {
        if (appName == null || appName.isBlank()) {
            CURRENT.set(DEFAULT_TENANT);
        } else {
            CURRENT.set(appName.trim());
        }
    }

    public static String get() {
        String v = CURRENT.get();
        return v == null || v.isBlank() ? DEFAULT_TENANT : v;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
