package com.codetalker.firestick.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class ProgressBus {
    private static final Logger log = LoggerFactory.getLogger(ProgressBus.class);

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long jobId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout, client controls lifecycle
        emitters.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(jobId, emitter));
        emitter.onTimeout(() -> remove(jobId, emitter));
        emitter.onError((ex) -> remove(jobId, emitter));
        return emitter;
    }

    public void publish(Long jobId, Object event) {
        List<SseEmitter> list = emitters.get(jobId);
        if (list == null || list.isEmpty()) return;
        for (SseEmitter emitter : list) {
            try {
                emitter.send(event, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.debug("SSE send failed for job {}: {}", jobId, e.getMessage());
                remove(jobId, emitter);
            }
        }
    }

    public void complete(Long jobId) {
        List<SseEmitter> list = emitters.remove(jobId);
        if (list == null) return;
        for (SseEmitter emitter : list) {
            try { emitter.complete(); } catch (Exception ignored) {}
        }
    }

    private void remove(Long jobId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(jobId);
        if (list != null) list.remove(emitter);
        if (list != null && list.isEmpty()) emitters.remove(jobId);
    }
}
