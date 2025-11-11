package com.codetalker.firestick.controller;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;

@WebMvcTest(IndexingJobController.class)
@SuppressWarnings({"removal"})
class IndexingJobControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingJobRepository repository;

    @Test
    void recent_withValidLimit_ShouldReturn200() throws Exception {
        IndexingJob j1 = new IndexingJob();
        j1.setId(1L);
        j1.setRootPath("C:/repo");
        j1.setStatus(IndexingJob.Status.SUCCESS);
        j1.setStartedAt(Instant.now());

        IndexingJob j2 = new IndexingJob();
        j2.setId(2L);
        j2.setRootPath("C:/repo2");
        j2.setStatus(IndexingJob.Status.RUNNING);
        j2.setStartedAt(Instant.now());

    Page<IndexingJob> page = new PageImpl<>(List.of(j1, j2));
    when(repository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/indexing/jobs").param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }
}
