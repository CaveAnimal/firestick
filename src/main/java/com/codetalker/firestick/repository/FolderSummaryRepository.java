package com.codetalker.firestick.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.FolderSummary;

public interface FolderSummaryRepository extends JpaRepository<FolderSummary, Long> {
    Optional<FolderSummary> findByFolderPathAndAppName(String folderPath, String appName);
    List<FolderSummary> findByAppName(String appName);
}
