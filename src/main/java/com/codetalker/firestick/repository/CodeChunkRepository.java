package com.codetalker.firestick.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;

public interface CodeChunkRepository extends JpaRepository<CodeChunk, Long> {
    List<CodeChunk> findByFile(CodeFile file);

    java.util.Optional<CodeChunk> findByFileAndStartLineAndEndLine(CodeFile file, int startLine, int endLine);

    // Derived query: count by chunk type
    long countByType(String type);

    // App-scoped counts
    long countByAppName(String appName);
    long countByAppNameAndType(String appName, String type);
}
