package com.codetalker.firestick.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;
import com.codetalker.firestick.service.IndexingJobControl;

@SpringBootTest
@AutoConfigureMockMvc
class IndexingControllerCancelTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IndexingJobRepository jobRepository;

    @Autowired
    private IndexingJobControl jobControl;

    @Test
    void cancel_withJobId_setsCancelFlag_andReturns202() throws Exception {
        IndexingJob j = new IndexingJob();
        j.setRootPath("/tmp");
        j.setStatus(IndexingJob.Status.RUNNING);
        j.setStartedAt(java.time.Instant.now());
        j = jobRepository.save(j);

        mockMvc.perform(post("/api/indexing/cancel").param("jobId", String.valueOf(j.getId())))
                .andExpect(status().isAccepted());

        assert jobControl.isCancelled(j.getId());
    }

    @Test
    void cancel_withoutJobId_picksLatestRunningJob_andReturns202() throws Exception {
        IndexingJob completed = new IndexingJob();
        completed.setRootPath("/tmp");
        completed.setStatus(IndexingJob.Status.SUCCESS);
        completed.setStartedAt(java.time.Instant.now().minusSeconds(60));
        jobRepository.save(completed);

        IndexingJob running = new IndexingJob();
        running.setRootPath("/tmp");
        running.setStatus(IndexingJob.Status.RUNNING);
        running.setStartedAt(java.time.Instant.now());
        running = jobRepository.save(running);

        mockMvc.perform(post("/api/indexing/cancel"))
                .andExpect(status().isAccepted());

        assert jobControl.isCancelled(running.getId());
    }

    @Test
    void cancel_withoutRunningJob_returns409() throws Exception {
        // Ensure no RUNNING jobs exist in the DB for this assertion by not creating any.
        mockMvc.perform(post("/api/indexing/cancel"))
                .andExpect(status().isConflict());
    }
}
