package com.codetalker.firestick.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;

public interface CodeChunkRepository extends JpaRepository<CodeChunk, Long> {
    List<CodeChunk> findByFile(CodeFile file);
}
