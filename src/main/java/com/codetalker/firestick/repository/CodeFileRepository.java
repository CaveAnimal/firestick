package com.codetalker.firestick.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codetalker.firestick.model.CodeFile;

public interface CodeFileRepository extends JpaRepository<CodeFile, Long> {
    Optional<CodeFile> findByFilePath(String filePath); // legacy
    Optional<CodeFile> findByFilePathAndAppName(String filePath, String appName);
    List<CodeFile> findByAppName(String appName);

    @Query("select cf from CodeFile cf where cf.appName = :appName and cf.filePath like concat('%', :contains, '%')")
    List<CodeFile> searchByAppAndPathContains(@Param("appName") String appName, @Param("contains") String contains);
}
