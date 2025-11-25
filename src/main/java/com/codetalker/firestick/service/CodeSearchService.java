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
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
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
    private final Path baseIndexPath;
    private static final Logger log = LoggerFactory.getLogger(CodeSearchService.class);
    // lucene.index.base property allows overriding the base index path in tests

    public CodeSearchService(@org.springframework.beans.factory.annotation.Value("${lucene.index.base:lucene-indices}") String luceneIndexBase) {
        this.analyzer = new StandardAnalyzer();
        this.indexDirectories = new HashMap<>();
        
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
        indexDirectories.forEach((k, d) -> {
            try {
                d.close();
            } catch (IOException e) {
                log.warn("Failed to close index directory for app {}", k, e);
            }
        });
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
                Directory directory = getIndexDirectory(app);
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                try (IndexWriter writer = new IndexWriter(directory, config)) {
                    Document doc = new Document();
                    doc.add(new StringField("id", id, Field.Store.YES));
                    doc.add(new StringField("app", app, Field.Store.YES));
                    doc.add(new TextField("content", content, Field.Store.YES));
                    writer.addDocument(doc);
                    writer.commit();
                }
                log.debug("Indexed document after reset id={} app={} (content len={})", id, app, content != null ? content.length() : 0);
            } catch (IOException ex) {
                log.error("Index reset attempt failed for app={}", app, ex);
                throw new IndexingException("Failed to index document after resetting index: " + id, ex);
            }
        } catch (IOException e) {
            log.error("Indexing failed for id={} app={}", id, app, e);
            throw new IndexingException("Failed to index document: " + id, e);
        }
    }

    /**
     * Index a summary (file or folder).
     *
     * @param id Unique identifier for the summary
     * @param app Application/tenant name
     * @param summary The summary content to index
     * @param type The type of summary ("file_summary" or "folder_summary")
     */
    public void indexSummary(String id, String app, String summary, String type) {
        if (app == null || app.isBlank()) {
            app = "default";
        }
        
        try {
            Directory directory = getIndexDirectory(app);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                Document doc = new Document();
                doc.add(new StringField("id", id, Field.Store.YES));
                doc.add(new StringField("app", app, Field.Store.YES));
                doc.add(new StringField("type", type, Field.Store.YES));
                doc.add(new TextField("content", summary, Field.Store.YES));
                writer.addDocument(doc);
                writer.commit();
            }
            log.debug("Indexed summary id={} app={} type={}", id, app, type);
        } catch (IOException e) {
            log.error("Failed to index summary id={} app={}", id, app, e);
            throw new IndexingException("Failed to index summary", e);
        }
    }

    private void resetIndexForApp(String app) throws IOException {
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

    /**
     * List all apps that have a Lucene index directory.
     */
    public List<String> getAvailableApps() {
        List<String> apps = new ArrayList<>();
        if (Files.exists(baseIndexPath) && Files.isDirectory(baseIndexPath)) {
            try (java.util.stream.Stream<Path> stream = Files.list(baseIndexPath)) {
                stream.filter(Files::isDirectory)
                      .map(Path::getFileName)
                      .map(Path::toString)
                      .forEach(apps::add);
            } catch (IOException e) {
                log.warn("Failed to list Lucene index directories", e);
            }
        }
        return apps;
    }

    // Backward compatibility overloads (pre-multi-tenant signature)
    public void indexCode(String id, String content) {
        indexCode(id, null, content);
    }

    public List<String> searchCode(String queryString) throws Exception {
        return searchCode(queryString, null);
    }

    public record IndexDocument(String id, String app, String content, String type) {}

    /**
     * Search for documents (code or summaries) and return full details.
     */
    public List<IndexDocument> searchDocuments(String queryString, String appName) throws Exception {
        return searchDocumentsInternal(queryString, appName, true);
    }

    private List<IndexDocument> searchDocumentsInternal(String queryString, String appName, boolean treatDefaultAsAggregate) throws Exception {
        List<IndexDocument> results = new ArrayList<>();
        boolean aggregate = (appName == null || appName.isBlank()) || (treatDefaultAsAggregate && "default".equalsIgnoreCase(appName));

        if (aggregate) {
            try (var dirs = Files.list(baseIndexPath)) {
                dirs.filter(Files::isDirectory).forEach(p -> {
                    try {
                        List<IndexDocument> sub = searchDocumentsInternal(queryString, p.getFileName().toString(), false);
                        results.addAll(sub);
                    } catch (Exception ignored) { }
                });
            }
            return results;
        }

        Directory directory = getIndexDirectory(appName);
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            if (reader.numDocs() == 0) return List.of();

            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser contentParser = new QueryParser("content", analyzer);
            Query contentQuery = contentParser.parse(queryString);

            org.apache.lucene.index.Term appTerm = new org.apache.lucene.index.Term("app", appName);
            Query appQuery = new org.apache.lucene.search.TermQuery(appTerm);

            Query query = new org.apache.lucene.search.BooleanQuery.Builder()
                    .add(appQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .add(contentQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .build();

            TopDocs topDocs = searcher.search(query, 20); // Fetch more for context
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = reader.storedFields().document(scoreDoc.doc);
                results.add(new IndexDocument(
                    doc.get("id"),
                    doc.get("app"),
                    doc.get("content"),
                    doc.get("type") // Might be null for code chunks
                ));
            }
        } catch (org.apache.lucene.index.IndexNotFoundException e) {
            // ignore
        }
        return results;
    }

    /**
     * Attempt to find the README file for the application.
     */
    public String findReadme(String appName) {
        if (appName == null || appName.isBlank()) return null;
        try {
            Directory directory = getIndexDirectory(appName);
            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                
                org.apache.lucene.search.BooleanQuery.Builder builder = new org.apache.lucene.search.BooleanQuery.Builder();
                builder.add(new org.apache.lucene.search.TermQuery(new Term("app", appName)), org.apache.lucene.search.BooleanClause.Occur.MUST);
                
                org.apache.lucene.search.BooleanQuery.Builder fileQuery = new org.apache.lucene.search.BooleanQuery.Builder();
                fileQuery.add(new WildcardQuery(new Term("id", "*README.md")), org.apache.lucene.search.BooleanClause.Occur.SHOULD);
                fileQuery.add(new WildcardQuery(new Term("id", "*readme.md")), org.apache.lucene.search.BooleanClause.Occur.SHOULD);
                fileQuery.add(new WildcardQuery(new Term("id", "*Readme.md")), org.apache.lucene.search.BooleanClause.Occur.SHOULD);
                
                builder.add(fileQuery.build(), org.apache.lucene.search.BooleanClause.Occur.MUST);
                
                TopDocs docs = searcher.search(builder.build(), 1);
                if (docs.totalHits.value > 0) {
                    Document doc = reader.storedFields().document(docs.scoreDocs[0].doc);
                    return doc.get("content");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to find README for app {}", appName, e);
        }
        return null;
    }
}
