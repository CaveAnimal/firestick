package com.codetalker.firestick.graph.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.graph.entity.CodeEdge;
import com.codetalker.firestick.graph.entity.CodeNode;
import com.codetalker.firestick.graph.service.GraphAnalysisService;

/**
 * REST controller for dependency graph operations.
 */
@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DependencyGraphController {
    
    private static final Logger logger = LoggerFactory.getLogger(DependencyGraphController.class);
    
    private final GraphAnalysisService graphService;
    
    public DependencyGraphController(GraphAnalysisService graphService) {
        this.graphService = graphService;
    }
    
    /**
     * Get dependencies (callees) of a method.
     * GET /api/graph/dependencies/{appName}/{methodName}?depth=2
     */
    @GetMapping("/dependencies/{appName}/{methodName}")
    public ResponseEntity<?> getDependencies(
            @PathVariable String appName,
            @PathVariable String methodName,
            @RequestParam(defaultValue = "1") int depth) {
        
        try {
            Set<CodeEdge> dependencies = graphService.getTransitiveDeps(appName, methodName, depth);
            
            Set<CodeNode> nodes = new HashSet<>();
            dependencies.forEach(edge -> {
                nodes.add(edge.getSource());
                nodes.add(edge.getTarget());
            });
            
            GraphResponse response = new GraphResponse(
                nodes.stream().map(NodeDTO::from).collect(Collectors.toList()),
                dependencies.stream().map(EdgeDTO::from).collect(Collectors.toList())
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting dependencies", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get callers of a method (methods that call the target method).
     * GET /api/graph/callers/{appName}/{methodName}
     */
    @GetMapping("/callers/{appName}/{methodName}")
    public ResponseEntity<?> getCallers(
            @PathVariable String appName,
            @PathVariable String methodName) {
        
        try {
            Set<CodeNode> callers = graphService.getCallers(appName, methodName);
            
            List<NodeDTO> nodeList = callers.stream()
                .map(NodeDTO::from)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new NodesResponse(nodeList));
        } catch (Exception e) {
            logger.error("Error getting callers", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get callees of a method (methods called by the target method).
     * GET /api/graph/callees/{appName}/{methodName}
     */
    @GetMapping("/callees/{appName}/{methodName}")
    public ResponseEntity<?> getCallees(
            @PathVariable String appName,
            @PathVariable String methodName) {
        
        try {
            Set<CodeNode> callees = graphService.getCallees(appName, methodName);
            
            List<NodeDTO> nodeList = callees.stream()
                .map(NodeDTO::from)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new NodesResponse(nodeList));
        } catch (Exception e) {
            logger.error("Error getting callees", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Detect circular dependencies.
     * GET /api/graph/cycles/{appName}
     */
    @GetMapping("/cycles/{appName}")
    public ResponseEntity<?> detectCycles(@PathVariable String appName) {
        try {
            List<List<CodeNode>> cycles = graphService.detectCircularDeps(appName);
            
            List<List<NodeDTO>> cycleList = cycles.stream()
                .map(cycle -> cycle.stream().map(NodeDTO::from).collect(Collectors.toList()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new CyclesResponse(cycleList));
        } catch (Exception e) {
            logger.error("Error detecting cycles", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get graph statistics.
     * GET /api/graph/stats/{appName}
     */
    @GetMapping("/stats/{appName}")
    public ResponseEntity<?> getStats(@PathVariable String appName) {
        try {
            GraphAnalysisService.GraphStats stats = graphService.getGraphStats(appName);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting stats", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get entry points (methods with no callers).
     * GET /api/graph/entry-points/{appName}
     */
    @GetMapping("/entry-points/{appName}")
    public ResponseEntity<?> getEntryPoints(@PathVariable String appName) {
        try {
            List<CodeNode> entryPoints = graphService.getEntryPoints(appName);
            
            List<NodeDTO> nodeList = entryPoints.stream()
                .map(NodeDTO::from)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new NodesResponse(nodeList));
        } catch (Exception e) {
            logger.error("Error getting entry points", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get leaf nodes (methods with no callees).
     * GET /api/graph/leaf-nodes/{appName}
     */
    @GetMapping("/leaf-nodes/{appName}")
    public ResponseEntity<?> getLeafNodes(@PathVariable String appName) {
        try {
            List<CodeNode> leafNodes = graphService.getLeafNodes(appName);
            
            List<NodeDTO> nodeList = leafNodes.stream()
                .map(NodeDTO::from)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new NodesResponse(nodeList));
        } catch (Exception e) {
            logger.error("Error getting leaf nodes", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get call chain from one method to another.
     * GET /api/graph/call-chain/{appName}?from=A&to=B
     */
    @GetMapping("/call-chain/{appName}")
    public ResponseEntity<?> getCallChain(
            @PathVariable String appName,
            @RequestParam String from,
            @RequestParam String to) {
        
        try {
            List<CodeNode> chain = graphService.getCallChain(appName, from, to);
            
            List<NodeDTO> nodeList = chain.stream()
                .map(NodeDTO::from)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new NodesResponse(nodeList));
        } catch (Exception e) {
            logger.error("Error getting call chain", e);
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // ============ DTOs ============
    
    public static class GraphResponse {
        public List<NodeDTO> nodes;
        public List<EdgeDTO> edges;
        
        public GraphResponse(List<NodeDTO> nodes, List<EdgeDTO> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }
    
    public static class NodesResponse {
        public List<NodeDTO> nodes;
        
        public NodesResponse(List<NodeDTO> nodes) {
            this.nodes = nodes;
        }
    }
    
    public static class CyclesResponse {
        public List<List<NodeDTO>> cycles;
        
        public CyclesResponse(List<List<NodeDTO>> cycles) {
            this.cycles = cycles;
        }
    }
    
    public static class NodeDTO {
        public Long id;
        public String appName;
        public String fullName;
        public String type;
        public String className;
        public String methodName;
        public Integer lineNumber;
        public String filePath;
        
        public NodeDTO(Long id, String appName, String fullName, String type,
                      String className, String methodName, Integer lineNumber, String filePath) {
            this.id = id;
            this.appName = appName;
            this.fullName = fullName;
            this.type = type;
            this.className = className;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
            this.filePath = filePath;
        }
        
        public static NodeDTO from(CodeNode node) {
            return new NodeDTO(node.getId(), node.getAppName(), node.getFullName(),
                    node.getType(), node.getClassName(), node.getMethodName(),
                    node.getLineNumber(), node.getFilePath());
        }
    }
    
    public static class EdgeDTO {
        public Long id;
        public String appName;
        public String sourceFullName;
        public String targetFullName;
        public String edgeType;
        public Integer weight;
        
        public EdgeDTO(Long id, String appName, String sourceFullName, String targetFullName,
                      String edgeType, Integer weight) {
            this.id = id;
            this.appName = appName;
            this.sourceFullName = sourceFullName;
            this.targetFullName = targetFullName;
            this.edgeType = edgeType;
            this.weight = weight;
        }
        
        public static EdgeDTO from(CodeEdge edge) {
            return new EdgeDTO(edge.getId(), edge.getAppName(),
                    edge.getSource().getFullName(), edge.getTarget().getFullName(),
                    edge.getEdgeType(), edge.getWeight());
        }
    }
    
    public static class ErrorResponse {
        public String error;
        public long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
