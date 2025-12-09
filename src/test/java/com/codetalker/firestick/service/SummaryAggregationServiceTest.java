package com.codetalker.firestick.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codetalker.firestick.llm.LLMServiceClient;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.FolderSummary;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.FolderSummaryRepository;

class SummaryAggregationServiceTest {

    private CodeFileRepository codeFileRepository;
    private FolderSummaryRepository folderSummaryRepository;
    private LLMServiceClient llmServiceClient;
    private CodeSearchService codeSearchService;
    private SummaryAggregationService service;

    @BeforeEach
    void setUp() {
        codeFileRepository = mock(CodeFileRepository.class);
        folderSummaryRepository = mock(FolderSummaryRepository.class);
        llmServiceClient = mock(LLMServiceClient.class);
        codeSearchService = mock(CodeSearchService.class);
        service = new SummaryAggregationService(codeFileRepository, folderSummaryRepository, llmServiceClient, codeSearchService);
    }

    @Test
    void testAggregateSummaries() throws Exception {
        String appName = "test-app";
        CodeFile file1 = new CodeFile("src/main/java/Test.java", java.time.Instant.now(), "hash1");
        file1.setSummary("Summary of Test.java");
        
        CodeFile file2 = new CodeFile("src/main/java/Utils.java", java.time.Instant.now(), "hash2");
        file2.setSummary("Summary of Utils.java");

        when(codeFileRepository.findByAppName(appName)).thenReturn(List.of(file1, file2));
        when(llmServiceClient.answerQuestion(anyString(), anyString())).thenReturn("Folder Summary");
        when(folderSummaryRepository.findByFolderPathAndAppName(anyString(), anyString())).thenReturn(Optional.empty());
        when(folderSummaryRepository.save(any(FolderSummary.class))).thenAnswer(i -> {
            FolderSummary fs = i.getArgument(0);
            fs.setId(1L);
            return fs;
        });

        service.aggregateSummaries(appName);

        verify(llmServiceClient).answerQuestion(anyString(), anyString());
        verify(folderSummaryRepository).save(any(FolderSummary.class));
        verify(codeSearchService).indexSummary(anyString(), anyString(), anyString(), anyString());
    }
}
