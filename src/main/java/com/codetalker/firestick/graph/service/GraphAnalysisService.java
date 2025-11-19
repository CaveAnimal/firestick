package com.codetalker.firestick.graph.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codetalker.firestick.graph.entity.CodeEdge;
import com.codetalker.firestick.graph.entity.CodeNode;
import com.codetalker.firestick.graph.repository.CodeEdgeRepository;
import com.codetalker.firestick.graph.repository.CodeNodeRepository;

/**
 * Service for managing dependency graphs with H2 persistence.
 * Uses JGraphT for in-memory algorithms and H2 for persistent storage.
 */
@Service
public class GraphAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(GraphAnalysisService.class);
    
    private final CodeNodeRepository nodeRepository;
    private final CodeEdgeRepository edgeRepository;
    
    public GraphAnalysisService(CodeNodeRepository nodeRepository, CodeEdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }
    
    /**
     * Load graph structure from H2 database into in-memory JGraphT graph.
     */
    @Transactional(readOnly = true)
    public Graph<CodeNode, CodeEdge> loadGraphFromDB(String appName) {
        logger.debug("Loading graph for app: {}", appName);
        
        Graph<CodeNode, CodeEdge> graph = new DefaultDirectedGraph<>(CodeEdge.class);
        
        // Load all nodes
        List<CodeNode> nodes = nodeRepository.findByAppName(appName);
        nodes.forEach(graph::addVertex);
        
        // Load all edges
        List<CodeEdge> edges = edgeRepository.findByAppName(appName);
        edges.forEach(edge -> graph.addEdge(edge.getSource(), edge.getTarget(), edge));
        
        logger.info("Loaded graph with {} nodes and {} edges", nodes.size(), edges.size());
        return graph;
    }
    
    /**
     * Build and persist graph from nodes and edges.
     */
    @Transactional
    public void buildAndPersistGraph(String appName, List<CodeNode> nodes, List<CodeEdge> edges) {
        logger.info("Building graph for app: {} with {} nodes and {} edges", appName, nodes.size(), edges.size());
        
        // Clear existing data (ignore return values to avoid AOP proxy issues)
        try {
            edgeRepository.deleteByAppName(appName);
        } catch (Exception e) {
            logger.debug("No edges to delete for app: {}", appName);
        }
        try {
            nodeRepository.deleteByAppName(appName);
        } catch (Exception e) {
            logger.debug("No nodes to delete for app: {}", appName);
        }
        
        // Set app name and save nodes
        nodes.forEach(node -> node.setAppName(appName));
        nodeRepository.saveAll(nodes);
        
        // Set app name and save edges
        edges.forEach(edge -> edge.setAppName(appName));
        edgeRepository.saveAll(edges);
        
        logger.info("Graph persisted successfully for app: {}", appName);
    }
    
    /**
     * Get all methods called BY a specific method (callees).
     */
    @Transactional(readOnly = true)
    public Set<CodeNode> getCallees(String appName, String methodName) {
        logger.debug("Getting callees for: {}", methodName);
        
        Optional<CodeNode> nodeOpt = nodeRepository.findByAppNameAndFullName(appName, methodName);
        if (nodeOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        CodeNode node = nodeOpt.get();
        List<CodeEdge> outgoing = edgeRepository.findByAppNameAndSourceId(appName, node.getId());
        
        return outgoing.stream()
            .map(CodeEdge::getTarget)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get all methods that CALL a specific method (callers).
     */
    @Transactional(readOnly = true)
    public Set<CodeNode> getCallers(String appName, String methodName) {
        logger.debug("Getting callers for: {}", methodName);
        
        Optional<CodeNode> nodeOpt = nodeRepository.findByAppNameAndFullName(appName, methodName);
        if (nodeOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        CodeNode node = nodeOpt.get();
        List<CodeEdge> incoming = edgeRepository.findByAppNameAndTargetId(appName, node.getId());
        
        return incoming.stream()
            .map(CodeEdge::getSource)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get transitive dependencies (all methods reachable from a starting point up to depth N).
     */
    @Transactional(readOnly = true)
    public Set<CodeEdge> getTransitiveDeps(String appName, String startingMethod, int maxDepth) {
        logger.debug("Getting transitive dependencies for: {} (depth: {})", startingMethod, maxDepth);
        
        Graph<CodeNode, CodeEdge> graph = loadGraphFromDB(appName);
        
        Optional<CodeNode> nodeOpt = nodeRepository.findByAppNameAndFullName(appName, startingMethod);
        if (nodeOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<CodeEdge> result = new HashSet<>();
        Set<CodeNode> visited = new HashSet<>();
        Queue<CodeNode> queue = new LinkedList<>();
        Map<CodeNode, Integer> depth = new HashMap<>();
        
        CodeNode start = nodeOpt.get();
        queue.offer(start);
        depth.put(start, 0);
        
        while (!queue.isEmpty()) {
            CodeNode current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            
            int currentDepth = depth.get(current);
            if (currentDepth >= maxDepth) continue;
            
            // Get all outgoing edges (methods called by current)
            Set<CodeEdge> outgoing = graph.outgoingEdgesOf(current);
            outgoing.forEach(edge -> {
                result.add(edge);
                CodeNode target = graph.getEdgeTarget(edge);
                if (!visited.contains(target)) {
                    queue.offer(target);
                    depth.put(target, currentDepth + 1);
                }
            });
        }
        
        logger.debug("Found {} transitive dependencies", result.size());
        return result;
    }
    
    /**
     * Detect circular dependencies (cycles in the graph).
     */
    @Transactional(readOnly = true)
    public List<List<CodeNode>> detectCircularDeps(String appName) {
        logger.debug("Detecting circular dependencies for app: {}", appName);
        
        Graph<CodeNode, CodeEdge> graph = loadGraphFromDB(appName);
        CycleDetector<CodeNode, CodeEdge> cycleDetector = new CycleDetector<>(graph);
        
        Set<CodeNode> cycleNodes = cycleDetector.findCycles();
        logger.info("Found {} nodes involved in cycles", cycleNodes.size());
        
        List<List<CodeNode>> cycles = new ArrayList<>();
        for (CodeNode node : cycleNodes) {
            List<CodeNode> cycle = traceCyclePath(graph, node, cycleDetector);
            if (!cycle.isEmpty()) {
                cycles.add(cycle);
            }
        }
        
        return cycles;
    }
    
    /**
     * Trace a cycle path starting from a node.
     */
    private List<CodeNode> traceCyclePath(Graph<CodeNode, CodeEdge> graph, CodeNode start, 
                                          CycleDetector<CodeNode, CodeEdge> cycleDetector) {
        List<CodeNode> path = new ArrayList<>();
        Set<CodeNode> visited = new HashSet<>();
        
        if (dfs(graph, start, start, path, visited, cycleDetector)) {
            return path;
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Depth-first search for cycle tracing.
     */
    private boolean dfs(Graph<CodeNode, CodeEdge> graph, CodeNode current, CodeNode target,
                       List<CodeNode> path, Set<CodeNode> visited,
                       CycleDetector<CodeNode, CodeEdge> cycleDetector) {
        
        if (visited.contains(current) && !path.isEmpty()) {
            return current.equals(target);
        }
        
        if (visited.contains(current)) {
            return false;
        }
        
        visited.add(current);
        path.add(current);
        
        for (CodeEdge edge : graph.outgoingEdgesOf(current)) {
            CodeNode next = graph.getEdgeTarget(edge);
            if (dfs(graph, next, target, path, new HashSet<>(visited), cycleDetector)) {
                return true;
            }
        }
        
        path.remove(path.size() - 1);
        return false;
    }
    
    /**
     * Get graph statistics.
     */
    @Transactional(readOnly = true)
    public GraphStats getGraphStats(String appName) {
        logger.debug("Computing graph statistics for app: {}", appName);
        
        long nodeCount = nodeRepository.countByAppName(appName);
        long edgeCount = edgeRepository.countByAppName(appName);
        long classCount = nodeRepository.countByAppNameAndType(appName, "class");
        long methodCount = nodeRepository.countByAppNameAndType(appName, "method");
        
        List<CodeEdge> frequentEdges = edgeRepository.findMostFrequentEdges(appName, 10);
        double avgWeight = frequentEdges.stream()
            .mapToInt(CodeEdge::getWeight)
            .average()
            .orElse(0.0);
        
        return new GraphStats(nodeCount, edgeCount, classCount, methodCount, avgWeight, frequentEdges.size());
    }
    
    /**
     * Get all entry points (methods with no callers).
     */
    @Transactional(readOnly = true)
    public List<CodeNode> getEntryPoints(String appName) {
        logger.debug("Finding entry points for app: {}", appName);
        
        Graph<CodeNode, CodeEdge> graph = loadGraphFromDB(appName);
        
        return graph.vertexSet().stream()
            .filter(node -> graph.inDegreeOf(node) == 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all leaf nodes (methods with no callees).
     */
    @Transactional(readOnly = true)
    public List<CodeNode> getLeafNodes(String appName) {
        logger.debug("Finding leaf nodes for app: {}", appName);
        
        Graph<CodeNode, CodeEdge> graph = loadGraphFromDB(appName);
        
        return graph.vertexSet().stream()
            .filter(node -> graph.outDegreeOf(node) == 0)
            .collect(Collectors.toList());
    }
    
    /**
     * Get call chain from one method to another (shortest path).
     */
    @Transactional(readOnly = true)
    public List<CodeNode> getCallChain(String appName, String from, String to) {
        logger.debug("Finding call chain from {} to {}", from, to);
        
        Optional<CodeNode> fromNode = nodeRepository.findByAppNameAndFullName(appName, from);
        Optional<CodeNode> toNode = nodeRepository.findByAppNameAndFullName(appName, to);
        
        if (fromNode.isEmpty() || toNode.isEmpty()) {
            return new ArrayList<>();
        }
        
        Graph<CodeNode, CodeEdge> graph = loadGraphFromDB(appName);
        return findShortestPath(graph, fromNode.get(), toNode.get());
    }
    
    /**
     * Find shortest path between two nodes using BFS.
     */
    private List<CodeNode> findShortestPath(Graph<CodeNode, CodeEdge> graph, CodeNode start, CodeNode end) {
        Map<CodeNode, CodeNode> parent = new HashMap<>();
        Queue<CodeNode> queue = new LinkedList<>();
        Set<CodeNode> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        while (!queue.isEmpty()) {
            CodeNode current = queue.poll();
            if (current.equals(end)) {
                // Reconstruct path
                List<CodeNode> path = new ArrayList<>();
                CodeNode node = end;
                while (node != null) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                return path;
            }
            
            for (CodeEdge edge : graph.outgoingEdgesOf(current)) {
                CodeNode target = graph.getEdgeTarget(edge);
                if (!visited.contains(target)) {
                    visited.add(target);
                    parent.put(target, current);
                    queue.offer(target);
                }
            }
        }
        
        return new ArrayList<>();  // No path found
    }
    
    /**
     * Graph statistics DTO.
     */
    public static class GraphStats {
        public final long nodeCount;
        public final long edgeCount;
        public final long classCount;
        public final long methodCount;
        public final double avgEdgeWeight;
        public final int topEdgesCount;
        
        public GraphStats(long nodeCount, long edgeCount, long classCount, long methodCount,
                         double avgEdgeWeight, int topEdgesCount) {
            this.nodeCount = nodeCount;
            this.edgeCount = edgeCount;
            this.classCount = classCount;
            this.methodCount = methodCount;
            this.avgEdgeWeight = avgEdgeWeight;
            this.topEdgesCount = topEdgesCount;
        }
    }
}
