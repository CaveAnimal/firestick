package com.codetalker.firestick.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.IndexingJob;

public interface IndexingJobRepository extends JpaRepository<IndexingJob, Long> {
    Optional<IndexingJob> findTopByOrderByStartedAtDesc();

    java.util.List<IndexingJob> findTop10ByOrderByStartedAtDesc();

    Optional<IndexingJob> findTopByStatusOrderByStartedAtDesc(IndexingJob.Status status);

    // App-scoped helpers
    Optional<IndexingJob> findTopByAppNameOrderByStartedAtDesc(String appName);
    List<IndexingJob> findTop10ByAppNameOrderByStartedAtDesc(String appName);
    Optional<IndexingJob> findTopByAppNameAndStatusOrderByStartedAtDesc(String appName, IndexingJob.Status status);
}
