package com.codetalker.firestick.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.codetalker.firestick.model.CodeChunk;
import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.FileInfo;

/**
 * Legacy wrapper service for backward compatibility.
 * Provides simple JGraphT graph building from FileInfo/CodeChunk entities.
 * For new code, use GraphAnalysisService in the graph.service package.
 * NOT a Spring bean - use GraphAnalysisService instead.
 */
public class DependencyGraphService {

    /**
     * Build a simple containment graph from parsed files and code chunks.
     * Vertices are prefixed: "F:" for File, "C:" for Class, "M:" for Method, "I:" for Import.
     * 
     * @param parsedFiles List of FileInfo entities
     * @param allChunks List of CodeChunk entities
     * @return A directed graph with vertices and edges representing containment relationships
     */
    public Graph<String, DefaultEdge> buildFromParsedFiles(List<FileInfo> parsedFiles, List<CodeChunk> allChunks) {
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        
        // Add file vertices from FileInfo
        Map<String, String> filePathToVertex = new HashMap<>();
        if (parsedFiles != null) {
            for (FileInfo fileInfo : parsedFiles) {
                String filePath = fileInfo.getFilePath() != null ? fileInfo.getFilePath() : "file_unknown";
                String vertex = "F:" + filePath;
                graph.addVertex(vertex);
                filePathToVertex.put(filePath, vertex);
            }
        }
        
        // Add chunk vertices (classes/methods) and edges to containing file
        if (allChunks != null) {
            for (CodeChunk chunk : allChunks) {
                String chunkType = chunk.getType() != null ? chunk.getType() : "unknown";
                String chunkName = chunk.getName() != null ? chunk.getName() : "chunk_" + chunk.getId();
                String chunkVertex = (chunkType.equals("class") ? "C:" : "M:") + chunkName;
                
                graph.addVertex(chunkVertex);
                
                // Add edge from file to chunk (containment)
                CodeFile file = chunk.getFile();
                if (file != null && file.getFilePath() != null) {
                    String filePath = file.getFilePath();
                    if (filePathToVertex.containsKey(filePath)) {
                        String fileVertex = filePathToVertex.get(filePath);
                        graph.addEdge(fileVertex, chunkVertex);
                    }
                }
            }
        }
        
        return graph;
    }

    /**
     * Get metadata about the graph.
     * 
     * @return Map containing metadata (empty by default)
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>();
    }
}

