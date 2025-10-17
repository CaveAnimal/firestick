package com.codetalker.firestick.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.CodeFile;

public interface CodeFileRepository extends JpaRepository<CodeFile, Long> {
    Optional<CodeFile> findByFilePath(String filePath);
}
