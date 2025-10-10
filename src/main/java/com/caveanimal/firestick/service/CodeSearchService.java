package com.caveanimal.firestick.service;

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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for indexing and searching code using Apache Lucene.
 */
@Service
public class CodeSearchService {

    private final Directory directory;
    private final StandardAnalyzer analyzer;

    public CodeSearchService() {
        this.directory = new ByteBuffersDirectory();
        this.analyzer = new StandardAnalyzer();
    }

    /**
     * Index a code snippet.
     *
     * @param id Unique identifier for the code snippet
     * @param content The code content to index
     * @throws IOException if indexing fails
     */
    public void indexCode(String id, String content) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            Document doc = new Document();
            doc.add(new TextField("id", id, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        }
    }

    /**
     * Search for code snippets.
     *
     * @param queryString The search query
     * @return List of matching document IDs
     * @throws Exception if search fails
     */
    public List<String> searchCode(String queryString) throws Exception {
        List<String> results = new ArrayList<>();
        
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(queryString);
            
            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                results.add(doc.get("id"));
            }
        }
        
        return results;
    }
}
