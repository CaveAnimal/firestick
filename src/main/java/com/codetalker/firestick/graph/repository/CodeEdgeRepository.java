package com.codetalker.firestick.graph.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codetalker.firestick.graph.entity.CodeEdge;

/**
 * Spring Data JPA repository for CodeEdge persistence operations.
 */
@Repository
public interface CodeEdgeRepository extends JpaRepository<CodeEdge, Long> {
    
    /**
     * Find all edges for a given application.
     */
    List<CodeEdge> findByAppName(String appName);
    
    /**
     * Find all edges where source node is the given node (calls FROM this node).
     */
    List<CodeEdge> findByAppNameAndSourceId(String appName, Long sourceId);
    
    /**
     * Find all edges where target node is the given node (calls TO this node).
     */
    List<CodeEdge> findByAppNameAndTargetId(String appName, Long targetId);
    
    /**
     * Find edges of a specific type.
     */
    List<CodeEdge> findByAppNameAndEdgeType(String appName, String edgeType);
    
    /**
     * Find all edges between two specific nodes.
     */
    @Query("SELECT e FROM CodeEdge e WHERE e.appName = :appName " +
           "AND e.source.id = :sourceId AND e.target.id = :targetId")
    List<CodeEdge> findByAppNameAndSourceIdAndTargetId(
        @Param("appName") String appName,
        @Param("sourceId") Long sourceId,
        @Param("targetId") Long targetId
    );
    
    /**
     * Count edges for a given application.
     */
    long countByAppName(String appName);
    
    /**
     * Count edges of a specific type.
     */
    long countByAppNameAndEdgeType(String appName, String edgeType);
    
    /**
     * Delete all edges for an application.
     */
    long deleteByAppName(String appName);
    
    /**
     * Find edges with highest weight (most frequently called).
     */
    @Query(value = "SELECT * FROM code_edges WHERE app_name = :appName " +
                   "ORDER BY weight DESC LIMIT :limit", nativeQuery = true)
    List<CodeEdge> findMostFrequentEdges(@Param("appName") String appName, @Param("limit") int limit);
}
