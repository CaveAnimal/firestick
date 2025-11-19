package com.codetalker.firestick.graph.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.codetalker.firestick.graph.entity.CodeNode;

/**
 * Spring Data JPA repository for CodeNode persistence operations.
 */
@Repository
public interface CodeNodeRepository extends JpaRepository<CodeNode, Long> {
    
    /**
     * Find all nodes for a given application.
     */
    List<CodeNode> findByAppName(String appName);
    
    /**
     * Find a node by app name and full name (unique constraint).
     */
    Optional<CodeNode> findByAppNameAndFullName(String appName, String fullName);
    
    /**
     * Find all nodes of a specific type (method, class, field).
     */
    List<CodeNode> findByAppNameAndType(String appName, String type);
    
    /**
     * Find all methods in a specific class.
     */
    List<CodeNode> findByAppNameAndClassNameAndType(String appName, String className, String type);
    
    /**
     * Find nodes by file path.
     */
    List<CodeNode> findByAppNameAndFilePath(String appName, String filePath);
    
    /**
     * Count nodes in an application.
     */
    long countByAppName(String appName);
    
    /**
     * Count nodes of a specific type.
     */
    long countByAppNameAndType(String appName, String type);
    
    /**
     * Delete all nodes for an application.
     */
    long deleteByAppName(String appName);
    
    /**
     * Find public methods only.
     */
    @Query("SELECT n FROM CodeNode n WHERE n.appName = :appName " +
           "AND n.type = 'method' AND n.isPublic = true")
    List<CodeNode> findPublicMethods(@Param("appName") String appName);
    
    /**
     * Find nodes by partial name match (for search).
     */
    @Query("SELECT n FROM CodeNode n WHERE n.appName = :appName " +
           "AND n.fullName LIKE CONCAT('%', :namePattern, '%')")
    List<CodeNode> findByNamePattern(@Param("appName") String appName, @Param("namePattern") String namePattern);
}
