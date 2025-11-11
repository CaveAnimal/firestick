package com.codetalker.firestick.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/**
 * Tracks cancellation requests for indexing jobs.
 */
@Component
public class IndexingJobControl {
    private final Map<Long, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    /** Request cancellation for the given job id. */
    public void requestCancel(long jobId) {
        cancelFlags.computeIfAbsent(jobId, k -> new AtomicBoolean(false)).set(true);
    }

    /** Returns true if cancellation has been requested. */
    public boolean isCancelled(long jobId) {
        AtomicBoolean flag = cancelFlags.get(jobId);
        return flag != null && flag.get();
    }

    /** Clears any stored flag for the job (called when job completes). */
    public void clear(long jobId) {
        cancelFlags.remove(jobId);
    }
}
