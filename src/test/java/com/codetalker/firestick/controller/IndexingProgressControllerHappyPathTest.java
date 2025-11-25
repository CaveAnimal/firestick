package com.codetalker.firestick.controller;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.codetalker.firestick.config.SseProperties;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.ProgressBus;

@WebMvcTest(IndexingProgressController.class)
@SuppressWarnings({"removal"})
class IndexingProgressControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ProgressBus progressBus;
    @MockBean private IndexingJobRepository jobRepository;
    // New controller dependencies introduced for SSE heartbeat
    @MockBean private ScheduledExecutorService sseHeartbeatExecutor;
    @MockBean private SseProperties sseProperties;

    @Test
    void stream_withoutJobId_usesLatestJobAndReturnsSseEmitter() throws Exception {
        IndexingJob j = new IndexingJob();
        j.setId(42L);
        when(jobRepository.findTopByOrderByStartedAtDesc()).thenReturn(Optional.of(j));
        // Use a small custom emitter to capture sends
        class TestEmitter extends SseEmitter {
            volatile Object lastSent = null;
            TestEmitter(long timeout) { super(timeout); }
            @Override
            public synchronized void send(Object object, org.springframework.http.MediaType mediaType) throws java.io.IOException {
                this.lastSent = object;
                // skip calling super to avoid trying to write to a real output stream in test
            }
            @Override
            public synchronized void send(Object object) throws java.io.IOException {
                this.lastSent = object;
                // skip calling super to avoid IO in test
            }
            @Override
            public synchronized void send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder event) throws java.io.IOException {
                this.lastSent = event;
                // no super call
            }
        }
        TestEmitter emitter = new TestEmitter(1000L);
        when(progressBus.register(42L)).thenReturn(emitter);
        // Keep heartbeat interval small and deterministic
        when(sseProperties.getHeartbeatMs()).thenReturn(1000L);

        mockMvc.perform(get("/api/indexing/stream").accept(MediaType.TEXT_EVENT_STREAM))
            .andExpect(status().isOk());

        // controller should have sent an initial event (open comment) to the emitter
        assert emitter.lastSent != null;
    }
}
