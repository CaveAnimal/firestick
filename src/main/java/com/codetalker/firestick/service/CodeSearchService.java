package com.codetalker.firestick.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.exception.IndexingException;

import jakarta.annotation.PreDestroy;

/**
 * Service for indexing and searching code using Apache Lucene.
 * 
 * Uses persistent disk-based indices with separate index per application.
 * Index directory structure:
 *   - lucene-indices/
 *     - default/
 *     - app1/
 *     - app2/
 *     etc.
 */
@Service
public class CodeSearchService {

    private final StandardAnalyzer analyzer;
    private final Map<String, Directory> indexDirectories;
    private final Map<String, IndexWriter> indexWriters;
    private final Path baseIndexPath;
    private static final Logger log = LoggerFactory.getLogger(CodeSearchService.class);
    // lucene.index.base property allows overriding the base index path in tests

    public CodeSearchService(@org.springframework.beans.factory.annotation.Value("${lucene.index.base:lucene-indices}") String luceneIndexBase) {
        this.analyzer = new StandardAnalyzer();
        this.indexDirectories = new HashMap<>();
        this.indexWriters = new HashMap<>();
        
        // Set up base path for Lucene indices; allow tests to override via property
        this.baseIndexPath = Paths.get(luceneIndexBase).toAbsolutePath();
        
        try {
            // Create base directory if it doesn't exist
            Files.createDirectories(baseIndexPath);
            log.info("Lucene index base path: {}", baseIndexPath);
        } catch (IOException e) {
            log.error("Failed to create Lucene index base directory", e);
            throw new IndexingException("Failed to initialize Lucene index directories", e);
        }
    }

    @PreDestroy
    public void close() {
        indexWriters.forEach((k, w) -> {
            try {
                w.close();
            } catch (IOException e) {
                log.warn("Failed to close index writer for app {}", k, e);
            }
        });
        indexDirectories.forEach((k, d) -> {
            try {
                d.close();
            } catch (IOException e) {
                log.warn("Failed to close index directory for app {}", k, e);
            }
        });
        indexWriters.clear();
        indexDirectories.clear();
    }

    /**
     * Get or create the directory for an application's index.
     * Each app has its own separate index directory on disk.
     */
    private Directory getIndexDirectory(String appName) throws IOException {
        // No-op: directory path exists per app
        
        // Check if already loaded
        if (indexDirectories.containsKey(appName)) {
            return indexDirectories.get(appName);
        }
        
        // Create app-specific index directory path
        Path appIndexPath = baseIndexPath.resolve(appName);
        
        try {
            Files.createDirectories(appIndexPath);
            Directory directory = FSDirectory.open(appIndexPath);
            indexDirectories.put(appName, directory);
            
            log.debug("Initialized index directory for app '{}' at {}", appName, appIndexPath);
            return directory;
        } catch (IOException e) {
            log.error("Failed to create index directory for app: {}", appName, e);
            throw e;
        }
    }

    /**
     * Get or create the index writer for an application.
     * Uses Analyzer configured in IndexWriterConfig.
     */
    private IndexWriter getIndexWriter(String appName) throws IOException {
        boolean aggregate = (appName == null || appName.isBlank());
        if (!aggregate && (appName == null || appName.isBlank())) {
            appName = "default";
        }
        
        // Check if already created
        if (indexWriters.containsKey(appName)) {
            return indexWriters.get(appName);
        }
        
        Directory directory = getIndexDirectory(appName);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(directory, config);
            indexWriters.put(appName, writer);
            
            log.debug("Created index writer for app '{}'", appName);
            return writer;
        } catch (org.apache.lucene.store.LockObtainFailedException lofe) {
            // In test environments we sometimes encounter stale locks from previous runs.
            // Try to remove the lock file and retry once.
            try {
                Path lockFile = baseIndexPath.resolve(appName).resolve("write.lock");
                if (Files.exists(lockFile)) {
                    log.warn("LockObtainFailed for app='{}'. Attempting to remove stale lock at {}", appName, lockFile);
                    Files.deleteIfExists(lockFile);
                    IndexWriter writer = new IndexWriter(directory, config);
                    indexWriters.put(appName, writer);
                    return writer;
                } else {
                    throw lofe;
                }
            } catch (IOException retryEx) {
                log.error("Failed to obtain index writer for app '{}' after retry", appName, retryEx);
                throw retryEx;
            }
        }
    }

    /**
     * Index a code snippet.
     *
     * @param id Unique identifier for the code snippet
     * @param app Application/tenant name
     * @param content The code content to index
     * @throws IOException if indexing fails
     */
    public void indexCode(String id, String app, String content) {
        if (app == null || app.isBlank()) {
            app = "default";
        }
        
        try {
            Directory directory = getIndexDirectory(app);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                Document doc = new Document();
                // Use StringField for id and app to index them as exact string values (no tokenization)
                doc.add(new StringField("id", id, Field.Store.YES));
                doc.add(new StringField("app", app, Field.Store.YES));
                doc.add(new TextField("content", content, Field.Store.YES));
                writer.addDocument(doc);
                writer.commit(); // Ensure data is written to disk
            }

            log.debug("Indexed document id={} app={} (content length={})", id, app, 
                     content != null ? content.length() : 0);
        } catch (IllegalArgumentException e) {
            // Handle schema/index mapping mismatch between existing index and new documents
            // (e.g., "cannot change field 'id' from index options=DOCS_AND_FREQS_AND_POSITIONS to inconsistent index options=DOCS").
            log.warn("Schema mismatch while indexing id={} app={}. Will reset index and retry. Error: {}", id, app, e.getMessage());
            // Reset writer and index directory and re-attempt indexing once
            try {
                resetIndexForApp(app);
                IndexWriter writer = getIndexWriter(app);
                Document doc = new Document();
                doc.add(new StringField("id", id, Field.Store.YES));
                doc.add(new StringField("app", app, Field.Store.YES));
                doc.add(new TextField("content", content, Field.Store.YES));
                writer.addDocument(doc);
                writer.commit();
                log.debug("Indexed document after reset id={} app={} (content len={})", id, app, content != null ? content.length() : 0);
                return;
            } catch (IOException ex) {
                log.error("Index reset attempt failed for app={}", app, ex);
                throw new IndexingException("Failed to index document after resetting index: " + id, ex);
            }
        } catch (IOException e) {
            log.error("Indexing failed for id={} app={}", id, app, e);
            throw new IndexingException("Failed to index document: " + id, e);
        }
    }

    private void resetIndexForApp(String app) throws IOException {
        // Close and remove any existing writer for the app
        var writer = indexWriters.remove(app);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ignored) {
                // continue to attempt to delete
            }
        }

        // Close and remove directory mapping
        var dir = indexDirectories.remove(app);
        if (dir != null) {
            try {
                dir.close();
            } catch (IOException ignored) {
                // continue to attempt to delete
            }
        }

        // Delete index path on disk
        Path appIndexPath = baseIndexPath.resolve(app);
        if (Files.exists(appIndexPath)) {
            Files.walk(appIndexPath)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
            log.info("Deleted existing index data for app {} at {}", app, appIndexPath);
        }
    }

    /**
     * Search for code snippets in an application's index.
     *
     * @param queryString The search query
     * @param appName Application/tenant name (uses "default" if null/blank)
     * @return List of matching document IDs
     * @throws Exception if search fails
     */
    public List<String> searchCode(String queryString, String appName) throws Exception {
        return searchCodeInternal(queryString, appName, true);
    }

    /**
     * Internal search implementation.
     *
     * @param queryString The search query
     * @param appName Application/tenant name or null to aggregate across apps
     * @param treatDefaultAsAggregate When true, an explicit "default" app is treated
     *                                 as a request to aggregate across all apps (backward compatible).
     *                                 When false, "default" is treated as an ordinary app name.
     */
    private List<String> searchCodeInternal(String queryString, String appName, boolean treatDefaultAsAggregate) throws Exception {
    java.util.Set<String> uniqueResults = new java.util.LinkedHashSet<>();
        
    // Treat null or blank as aggregation across all apps. If treatDefaultAsAggregate is true,
    // explicit "default" will also be treated as aggregation to preserve backward compatibility.
    boolean aggregate = (appName == null || appName.isBlank()) || (treatDefaultAsAggregate && "default".equalsIgnoreCase(appName));
        
        try {
            // If no specific app requested, aggregate across all app indices found under baseIndexPath
            if (aggregate) {
                java.util.Set<String> uniq = new java.util.LinkedHashSet<>();
                try (var dirs = java.nio.file.Files.list(baseIndexPath)) {
                    dirs.filter(java.nio.file.Files::isDirectory).forEach(p -> {
                        try {
                            // When aggregating across apps, make sure the recursive per-app searches do NOT
                            // treat "default" as another aggregation request. This avoids infinite recursion
                            // when an index folder named "default" exists.
                            List<String> sub = searchCodeInternal(queryString, p.getFileName().toString(), false);
                            uniq.addAll(sub);
                        } catch (Exception ignored) {
                            // ignore per-app search errors during aggregation
                        }
                    });
                } catch (java.io.IOException e) {
                    log.warn("Failed to list index base directory for aggregation", e);
                }
                return new java.util.ArrayList<>(uniq);
            }

            Directory directory = getIndexDirectory(appName);
            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                // Check if index is empty
                if (reader.numDocs() == 0) {
                    log.warn("Search index is empty for app='{}' query='{}'", appName, queryString);
                    throw new org.apache.lucene.index.IndexNotFoundException(
                        "Search index is empty for app '" + appName + "'. " +
                        "Please rebuild the index: POST /api/search/index/rebuild");
                }
            
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser contentParser = new QueryParser("content", analyzer);
            Query contentQuery = contentParser.parse(queryString);
            
            // Always filter by app name in multi-tenant environment
            // Use a Term query on the `app` field so we only match the exact application name
            org.apache.lucene.index.Term appTerm = new org.apache.lucene.index.Term("app", appName);
            Query appQuery = new org.apache.lucene.search.TermQuery(appTerm);
            
            Query query = new org.apache.lucene.search.BooleanQuery.Builder()
                    .add(appQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .add(contentQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .build();
            
            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = reader.storedFields().document(scoreDoc.doc);
                uniqueResults.add(doc.get("id"));
            }
            
            }
            log.debug("Search app='{}' query='{}' -> {} hits", appName, queryString, uniqueResults.size());
        } catch (org.apache.lucene.index.IndexNotFoundException e) {
            // No index yet for this app: treat as empty result (multi-tenant isolation fallback)
            log.warn("Index not found for app='{}' (returning empty results): {}", appName, e.getMessage());
            return new ArrayList<>(uniqueResults);
        } catch (java.io.IOException e) {
            log.error("Search failed for app='{}' query='{}'", appName, queryString, e);
            throw e;
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            log.error("Search query parse failed for app='{}' query='{}'", appName, queryString, e);
            throw e;
        } catch (RuntimeException e) {
            // Unexpected runtime errors: rethrow but log for debugging
            log.error("Unexpected runtime error during search for app='{}' query='{}'", appName, queryString, e);
            throw e;
        }
        
    // Preserve insertion order but remove duplicates if any
    return new ArrayList<>(uniqueResults);
    }

    // Backward compatibility overloads (pre-multi-tenant signature)
    public void indexCode(String id, String content) {
        indexCode(id, null, content);
    }

    public List<String> searchCode(String queryString) throws Exception {
        return searchCode(queryString, null);
    }
}
