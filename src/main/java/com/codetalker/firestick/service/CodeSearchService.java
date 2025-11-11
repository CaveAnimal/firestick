package com.codetalker.firestick.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.codetalker.firestick.exception.IndexingException;

/**
 * Service for indexing and searching code using Apache Lucene.
 */
@Service
public class CodeSearchService {

    private final Directory directory;
    private final StandardAnalyzer analyzer;
    private static final Logger log = LoggerFactory.getLogger(CodeSearchService.class);

    public CodeSearchService() {
        this.directory = new ByteBuffersDirectory();
        this.analyzer = new StandardAnalyzer();
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
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            Document doc = new Document();
            doc.add(new TextField("id", id, Field.Store.YES));
            if (app != null) {
                doc.add(new TextField("app", app, Field.Store.YES));
            }
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
            log.debug("Indexed document id={} app={} (content length={})", id, app, content != null ? content.length() : 0);
        } catch (IOException e) {
            log.error("Indexing failed for id={}", id, e);
            throw new IndexingException("Failed to index document: " + id, e);
        }
    }

    /**
     * Search for code snippets.
     *
     * @param queryString The search query
     * @return List of matching document IDs
     * @throws Exception if search fails
     */
    public List<String> searchCode(String queryString, String appName) throws Exception {
        List<String> results = new ArrayList<>();
        
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            // Build a combined query: app:"appName" AND content:queryString
            QueryParser contentParser = new QueryParser("content", analyzer);
            Query contentQuery = contentParser.parse(queryString);
            Query appQuery = null;
            // Backward-compat: when app is null/blank or 'default', don't restrict by app
            if (appName != null && !appName.isBlank() && !"default".equalsIgnoreCase(appName)) {
                QueryParser appParser = new QueryParser("app", analyzer);
                appQuery = appParser.parse('"' + appName + '"');
            }
            Query query = (appQuery == null) ? contentQuery : new org.apache.lucene.search.BooleanQuery.Builder()
                    .add(appQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .add(contentQuery, org.apache.lucene.search.BooleanClause.Occur.MUST)
                    .build();
            
            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                // Use StoredFields API instead of deprecated IndexSearcher#doc(int)
                Document doc = searcher.getIndexReader().storedFields().document(scoreDoc.doc);
                results.add(doc.get("id"));
            }
            log.debug("Search query '{}' app='{}' -> {} hits", queryString, appName, results.size());
        }
        
        return results;
    }

    // Backward compatibility overloads (pre-multi-tenant signature)
    public void indexCode(String id, String content) {
        indexCode(id, null, content);
    }

    public List<String> searchCode(String queryString) throws Exception {
        return searchCode(queryString, null);
    }
}
