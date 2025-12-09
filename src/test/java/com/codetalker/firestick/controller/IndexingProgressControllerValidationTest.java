package com.codetalker.firestick.controller;

import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.config.SseProperties;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.ProgressBus;

@WebMvcTest(IndexingProgressController.class)
@SuppressWarnings({"removal"})
class IndexingProgressControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ProgressBus progressBus;
    @MockBean private IndexingJobRepository jobRepository;
    // New controller dependencies introduced for SSE heartbeat
    @MockBean private ScheduledExecutorService sseHeartbeatExecutor;
    @MockBean private SseProperties sseProperties;

    @Test
    void stream_withInvalidJobId_ShouldReturnBadRequest() throws Exception {
        when(sseProperties.getHeartbeatMs()).thenReturn(1000L);
        mockMvc.perform(get("/api/indexing/stream").param("jobId", "0"))
            .andExpect(status().isBadRequest());
    }
}
