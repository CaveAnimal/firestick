package com.codetalker.firestick.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.IndexingObject;

public interface IndexingObjectRepository extends JpaRepository<IndexingObject, Long> {
    List<IndexingObject> findByJobId(Long jobId);
    Page<IndexingObject> findByJobId(Long jobId, Pageable pageable);
    java.util.Optional<IndexingObject> findFirstByJobIdAndObjectName(Long jobId, String objectName);
    List<IndexingObject> findByJobIdAndObjectType(Long jobId, String objectType);
    Page<IndexingObject> findByJobIdAndObjectType(Long jobId, String objectType, Pageable pageable);
    Page<IndexingObject> findByJobIdAndObjectNameContainingIgnoreCase(Long jobId, String namePart, Pageable pageable);
    Page<IndexingObject> findByJobIdAndObjectTypeAndObjectNameContainingIgnoreCase(Long jobId, String objectType, String namePart, Pageable pageable);
}
