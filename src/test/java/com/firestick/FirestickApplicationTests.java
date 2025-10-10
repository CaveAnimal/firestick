package com.firestick;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FirestickApplicationTests {

    @Test
    void contextLoads() {
        // Verify Spring Boot application context loads successfully
        assertNotNull(FirestickApplication.class);
    }

    @Test
    void testJavaParserIntegration() {
        // Test JavaParser for code parsing and AST analysis
        JavaParser javaParser = new JavaParser();
        String code = "public class Test { public void method() {} }";
        
        CompilationUnit cu = javaParser.parse(code).getResult().orElse(null);
        
        assertNotNull(cu, "JavaParser should parse the code successfully");
        assertEquals(1, cu.getTypes().size(), "Should have one type declaration");
        assertEquals("Test", cu.getType(0).getNameAsString(), "Class name should be Test");
    }

    @Test
    void testLuceneIntegration() throws IOException {
        // Test Apache Lucene indexing and searching
        ByteBuffersDirectory directory = new ByteBuffersDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        
        // Create index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            Document doc = new Document();
            doc.add(new TextField("content", "This is a test document", Field.Store.YES));
            writer.addDocument(doc);
            writer.commit();
        }
        
        // Search index
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs results = searcher.search(new MatchAllDocsQuery(), 10);
            
            assertEquals(1, results.totalHits.value, "Should find one document");
            Document doc = searcher.storedFields().document(results.scoreDocs[0].doc);
            assertEquals("This is a test document", doc.get("content"));
        }
    }

    @Test
    void testJGraphTIntegration() {
        // Test JGraphT for dependency graph analysis
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        
        // Add vertices
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        
        // Add edges
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        
        assertEquals(3, graph.vertexSet().size(), "Graph should have 3 vertices");
        assertEquals(2, graph.edgeSet().size(), "Graph should have 2 edges");
        assertTrue(graph.containsEdge("A", "B"), "Should contain edge A->B");
        assertTrue(graph.containsEdge("B", "C"), "Should contain edge B->C");
    }

    @Test
    void testOnnxRuntimeAvailable() {
        // Test that ONNX Runtime is available
        try {
            Class<?> ortEnvironmentClass = Class.forName("ai.onnxruntime.OrtEnvironment");
            assertNotNull(ortEnvironmentClass, "ONNX Runtime should be available");
        } catch (ClassNotFoundException e) {
            fail("ONNX Runtime classes should be available: " + e.getMessage());
        }
    }

    @Test
    void testDJLAvailable() {
        // Test that DJL is available
        try {
            Class<?> djlEngineClass = Class.forName("ai.djl.engine.Engine");
            assertNotNull(djlEngineClass, "DJL should be available");
        } catch (ClassNotFoundException e) {
            fail("DJL classes should be available: " + e.getMessage());
        }
    }

    @Test
    void testChromaClientAvailable() {
        // Test that Chroma Java client is available
        try {
            Class<?> chromaClientClass = Class.forName("tech.amikos.chromadb.Client");
            assertNotNull(chromaClientClass, "Chroma client should be available");
        } catch (ClassNotFoundException e) {
            fail("Chroma client classes should be available: " + e.getMessage());
        }
    }

    @Test
    void testH2DatabaseAvailable() {
        // Test that H2 Database is available
        try {
            Class<?> h2DriverClass = Class.forName("org.h2.Driver");
            assertNotNull(h2DriverClass, "H2 Database driver should be available");
        } catch (ClassNotFoundException e) {
            fail("H2 Database driver should be available: " + e.getMessage());
        }
    }
}
