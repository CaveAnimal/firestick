package com.codetalker.firestick.controller;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.codetalker.firestick.repository.CodeChunkRepository;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.service.CodeSearchService;
import com.codetalker.firestick.service.SearchIndexRebuildService;

class SearchIndexDiagnosticsControllerTest {

    private CodeFileRepository fileRepository;
    private CodeChunkRepository chunkRepository;
    private SearchIndexRebuildService indexRebuildService;
    private CodeSearchService codeSearchService;
    private SearchIndexDiagnosticsController controller;

    @BeforeEach
    void setUp() {
        fileRepository = mock(CodeFileRepository.class);
        chunkRepository = mock(CodeChunkRepository.class);
        indexRebuildService = mock(SearchIndexRebuildService.class);
        codeSearchService = mock(CodeSearchService.class);
        
        controller = new SearchIndexDiagnosticsController(
            fileRepository, 
            chunkRepository, 
            indexRebuildService, 
            codeSearchService
        );
    }

    @Test
    void status_shouldReturnTrue_whenDbHasData() {
        when(fileRepository.count()).thenReturn(10L);
        when(chunkRepository.count()).thenReturn(50L);
        when(codeSearchService.getAvailableApps()).thenReturn(Collections.emptyList());

        ResponseEntity<SearchIndexDiagnosticsController.IndexStatusResponse> response = controller.status();
        
        assertTrue(response.getBody().hasIndexedData);
    }

    @Test
    void status_shouldReturnTrue_whenLuceneHasData_evenIfDbEmpty() {
        when(fileRepository.count()).thenReturn(0L);
        when(chunkRepository.count()).thenReturn(0L);
        when(codeSearchService.getAvailableApps()).thenReturn(List.of("app1"));

        ResponseEntity<SearchIndexDiagnosticsController.IndexStatusResponse> response = controller.status();
        
        assertTrue(response.getBody().hasIndexedData);
    }

    @Test
    void status_shouldReturnFalse_whenBothEmpty() {
        when(fileRepository.count()).thenReturn(0L);
        when(chunkRepository.count()).thenReturn(0L);
        when(codeSearchService.getAvailableApps()).thenReturn(Collections.emptyList());

        ResponseEntity<SearchIndexDiagnosticsController.IndexStatusResponse> response = controller.status();
        
        assertFalse(response.getBody().hasIndexedData);
    }
}
