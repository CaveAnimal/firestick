package com.codetalker.firestick.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codetalker.firestick.model.IndexingJob;

public interface IndexingJobRepository extends JpaRepository<IndexingJob, Long> {
    Optional<IndexingJob> findTopByOrderByStartedAtDesc();

    java.util.List<IndexingJob> findTop10ByOrderByStartedAtDesc();

    Optional<IndexingJob> findTopByStatusOrderByStartedAtDesc(IndexingJob.Status status);

    // App-scoped helpers
    Optional<IndexingJob> findTopByAppNameOrderByStartedAtDesc(String appName);
    List<IndexingJob> findTop10ByAppNameOrderByStartedAtDesc(String appName);
    Optional<IndexingJob> findTopByAppNameAndStatusOrderByStartedAtDesc(String appName, IndexingJob.Status status);

    @Modifying
    @Query("update IndexingJob ij set ij.appName = :newAppName where ij.appName = :oldAppName")
    long updateAppName(@Param("oldAppName") String oldAppName, @Param("newAppName") String newAppName);

    @Query("select distinct ij.appName from IndexingJob ij where ij.appName is not null order by ij.appName asc")
    List<String> findDistinctAppNames();
}
