package com.codetalker.firestick.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.codetalker.firestick.graph.entity.CodeEdge;
import com.codetalker.firestick.graph.entity.CodeNode;
import com.codetalker.firestick.graph.repository.CodeEdgeRepository;
import com.codetalker.firestick.graph.repository.CodeNodeRepository;
import com.codetalker.firestick.graph.service.GraphAnalysisService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("GraphAnalysisService Integration Tests")
class DependencyGraphIntegrationTest {
    
    @Autowired
    private GraphAnalysisService graphService;
    
    @Autowired
    private CodeNodeRepository nodeRepository;
    
    @Autowired
    private CodeEdgeRepository edgeRepository;
    
    private final String appName = "testapp";
    
    @BeforeEach
    public void setUp() {
        // Each test uses a unique app name, so no need to delete previous data
        
        // Create test nodes
        CodeNode node1 = new CodeNode(appName, "Main", "main", "method");
        CodeNode node2 = new CodeNode(appName, "PaymentProcessor", "process", "method");
        CodeNode node3 = new CodeNode(appName, "BankConnector", "transfer", "method");
        CodeNode node4 = new CodeNode(appName, "Logger", "log", "method");
        
        List<CodeNode> nodes = nodeRepository.saveAll(Arrays.asList(node1, node2, node3, node4));
        
        // Create test edges: main -> process -> transfer, process -> log, transfer -> log
        CodeEdge edge1 = new CodeEdge(nodes.get(0), nodes.get(1), "calls");
        CodeEdge edge2 = new CodeEdge(nodes.get(1), nodes.get(2), "calls");
        CodeEdge edge3 = new CodeEdge(nodes.get(1), nodes.get(3), "calls");
        CodeEdge edge4 = new CodeEdge(nodes.get(2), nodes.get(3), "calls");
        
        List<CodeEdge> edges = edgeRepository.saveAll(Arrays.asList(edge1, edge2, edge3, edge4));
        
        // Update edges with app name
        edges.forEach(e -> e.setAppName(appName));
        edgeRepository.saveAll(edges);
    }
    
    @Test
    @DisplayName("Load graph from database")
    public void testLoadGraphFromDB() {
        var graph = graphService.loadGraphFromDB(appName);
        
        assertNotNull(graph);
        assertEquals(4, graph.vertexSet().size());
        assertEquals(4, graph.edgeSet().size());
    }
    
    @Test
    @DisplayName("Get callees of a method")
    public void testGetCallees() {
        Set<CodeNode> callees = graphService.getCallees(appName, "PaymentProcessor.process");
        
        assertNotNull(callees);
        assertEquals(2, callees.size());
        
        boolean hasTransfer = callees.stream().anyMatch(n -> n.getMethodName().equals("transfer"));
        boolean hasLog = callees.stream().anyMatch(n -> n.getMethodName().equals("log"));
        
        assertTrue(hasTransfer);
        assertTrue(hasLog);
    }
    
    @Test
    @DisplayName("Get callers of a method")
    public void testGetCallers() {
        Set<CodeNode> callers = graphService.getCallers(appName, "BankConnector.transfer");
        
        assertNotNull(callers);
        assertEquals(1, callers.size());
        
        CodeNode caller = callers.iterator().next();
        assertEquals("PaymentProcessor", caller.getClassName());
        assertEquals("process", caller.getMethodName());
    }
    
    @Test
    @DisplayName("Get transitive dependencies")
    public void testGetTransitiveDeps() {
        Set<CodeEdge> deps = graphService.getTransitiveDeps(appName, "Main.main", 2);
        
        assertNotNull(deps);
    assertTrue(!deps.isEmpty());
    }
    
    @Test
    @DisplayName("Get entry points")
    public void testGetEntryPoints() {
        List<CodeNode> entryPoints = graphService.getEntryPoints(appName);
        
        assertNotNull(entryPoints);
        assertEquals(1, entryPoints.size());
        assertEquals("Main", entryPoints.get(0).getClassName());
    }
    
    @Test
    @DisplayName("Get leaf nodes")
    public void testGetLeafNodes() {
        List<CodeNode> leafNodes = graphService.getLeafNodes(appName);
        
        assertNotNull(leafNodes);
        assertEquals(1, leafNodes.size());
        assertEquals("Logger", leafNodes.get(0).getClassName());
    }
    
    @Test
    @DisplayName("Get graph statistics")
    public void testGetGraphStats() {
        GraphAnalysisService.GraphStats stats = graphService.getGraphStats(appName);
        
        assertNotNull(stats);
        assertEquals(4, stats.nodeCount);
        assertEquals(4, stats.edgeCount);
        assertEquals(4, stats.methodCount);
    }
    
    @Test
    @DisplayName("Build and persist new graph")
    public void testBuildAndPersistGraph() {
        String newApp = "newapp";
        
        CodeNode node1 = new CodeNode(newApp, "ClassA", "methodA", "method");
        CodeNode node2 = new CodeNode(newApp, "ClassB", "methodB", "method");
        
        CodeEdge edge = new CodeEdge(node1, node2, "calls");
        
        graphService.buildAndPersistGraph(newApp, 
            Arrays.asList(node1, node2), 
            Collections.singletonList(edge));
        
        long nodeCount = nodeRepository.countByAppName(newApp);
        long edgeCount = edgeRepository.countByAppName(newApp);
        
        assertEquals(2, nodeCount);
        assertEquals(1, edgeCount);
    }
    
    @Test
    @DisplayName("Get call chain between methods")
    public void testGetCallChain() {
        List<CodeNode> chain = graphService.getCallChain(appName, "Main.main", "Logger.log");
        
        assertNotNull(chain);
    assertTrue(!chain.isEmpty());
    }
}
