package com.codetalker.firestick.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.llm.LLMServiceClient;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.FolderSummary;
import com.codetalker.firestick.repository.CodeFileRepository;
import com.codetalker.firestick.repository.FolderSummaryRepository;

@Service
public class SummaryAggregationService {
    private static final Logger log = LoggerFactory.getLogger(SummaryAggregationService.class);

    private final CodeFileRepository codeFileRepository;
    private final FolderSummaryRepository folderSummaryRepository;
    private final LLMServiceClient llmServiceClient;
    private final CodeSearchService codeSearchService;

    public SummaryAggregationService(CodeFileRepository codeFileRepository,
                                     FolderSummaryRepository folderSummaryRepository,
                                     LLMServiceClient llmServiceClient,
                                     CodeSearchService codeSearchService) {
        this.codeFileRepository = codeFileRepository;
        this.folderSummaryRepository = folderSummaryRepository;
        this.llmServiceClient = llmServiceClient;
        this.codeSearchService = codeSearchService;
    }

    public int aggregateSummaries(String appName) {
        log.info("Starting summary aggregation for app: {}", appName);
        List<CodeFile> files = codeFileRepository.findByAppName(appName);
        // Group files by folder
        Map<String, List<CodeFile>> filesByFolder = files.stream()
            .collect(Collectors.groupingBy(f -> Path.of(f.getFilePath()).getParent().toString()));

        // Get all folders and sort by depth descending (deepest first)
        List<String> folders = filesByFolder.keySet().stream()
            .sorted((p1, p2) -> Integer.compare(Path.of(p2).getNameCount(), Path.of(p1).getNameCount()))
            .collect(Collectors.toList());

        int summarizedCount = 0;
        for (String folder : folders) {
            processFolder(folder, appName, filesByFolder.get(folder));
            summarizedCount++;
        }
        return summarizedCount;
    }

    private void processFolder(String folderPath, String appName, List<CodeFile> files) {
        try {
            StringBuilder context = new StringBuilder();
            context.append("Folder: ").append(folderPath).append("\n\n");
            context.append("Files:\n");
            for (CodeFile file : files) {
                context.append("- ").append(Path.of(file.getFilePath()).getFileName()).append(": ");
                context.append(file.getSummary() != null ? file.getSummary() : "No summary").append("\n");
            }

            // TODO: Include sub-folder summaries if any (requires finding folders where parent == folderPath)
            // For now, just file aggregation.

            String prompt = "Summarize the purpose and architecture of this folder based on the file summaries above.";
            String summary = llmServiceClient.answerQuestion(prompt, context.toString());

            if (summary == null || summary.isBlank()) {
                log.warn("LLM returned null/empty summary for folder: {}. Skipping indexing of summary.", folderPath);
                summary = "Summary unavailable (LLM service error)";
            }

            FolderSummary folderSummary = folderSummaryRepository.findByFolderPathAndAppName(folderPath, appName)
                .orElse(new FolderSummary(appName, folderPath, java.time.Instant.now(), ""));
            
            folderSummary.setSummary(summary);
            folderSummary.setLastModified(java.time.Instant.now());
            folderSummary = folderSummaryRepository.save(folderSummary);
            
            codeSearchService.indexSummary("folder_" + folderSummary.getId(), appName, summary, "folder_summary");
            
            log.info("Generated summary for folder: {}", folderPath);

        } catch (Exception e) {
            log.error("Failed to aggregate summary for folder: {}", folderPath, e);
        }
    }
}
