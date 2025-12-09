package com.codetalker.firestick.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.codetalker.firestick.model.IndexingObject;
import com.codetalker.firestick.model.IndexingJob;
import com.codetalker.firestick.repository.IndexingJobRepository;

@WebMvcTest(IndexingJobController.class)
class IndexingJobControllerObjectsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IndexingJobRepository repository;

    @MockBean
    private com.codetalker.firestick.repository.CodeFileRepository codeFileRepository;

    @MockBean
    private com.codetalker.firestick.repository.CodeChunkRepository codeChunkRepository;

    @MockBean
    private com.codetalker.firestick.repository.FolderSummaryRepository folderSummaryRepository;

    @MockBean
    private com.codetalker.firestick.repository.IndexingObjectRepository indexingObjectRepository;

    @Test
    void objects_endpoint_returnsList() throws Exception {
        IndexingJob j = new IndexingJob();
        j.setId(123L);
        j.setRootPath("/tmp");
        j.setStatus(IndexingJob.Status.SUCCESS);
        j.setStartedAt(Instant.now());

        when(repository.findById(123L)).thenReturn(java.util.Optional.of(j));
        when(repository.existsById(123L)).thenReturn(true);
        var o = new IndexingObject(); o.setId(1L); o.setJobId(123L); o.setObjectType("FILE"); o.setObjectName("/tmp/A.java");
        when(indexingObjectRepository.findByJobId(123L)).thenReturn(List.of(o));

        mockMvc.perform(get("/api/indexing/jobs/123/objects")).andExpect(status().isOk());
    }
}
