package com.codetalker.firestick.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "folder_summaries",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_folder_summaries_app_path", columnNames = {"app_name", "folder_path"})
       })
public class FolderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folder_path", nullable = false, length = 1024)
    private String folderPath;

    @Column(name = "app_name", nullable = true, length = 64)
    private String appName;

    @Column(nullable = false)
    private Instant lastModified;

    @Column(name = "summary", columnDefinition = "CLOB")
    private String summary;

    public FolderSummary() {}

    public FolderSummary(String appName, String folderPath, Instant lastModified, String summary) {
        this.appName = appName;
        this.folderPath = folderPath;
        this.lastModified = lastModified;
        this.summary = summary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
