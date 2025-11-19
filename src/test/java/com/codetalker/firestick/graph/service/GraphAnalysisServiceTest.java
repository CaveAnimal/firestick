package com.codetalker.firestick.graph.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.codetalker.firestick.graph.entity.CodeEdge;
import com.codetalker.firestick.graph.entity.CodeNode;
import com.codetalker.firestick.graph.repository.CodeEdgeRepository;
import com.codetalker.firestick.graph.repository.CodeNodeRepository;

@DisplayName("GraphAnalysisService Tests")
public class GraphAnalysisServiceTest {
    
    @Mock
    private CodeNodeRepository nodeRepository;
    
    @Mock
    private CodeEdgeRepository edgeRepository;
    
    private GraphAnalysisService graphService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        graphService = new GraphAnalysisService(nodeRepository, edgeRepository);
    }
    
    @Test
    @DisplayName("Load graph from DB with nodes and edges")
    public void testLoadGraphFromDB() {
        String appName = "testapp";
        
        CodeNode node1 = new CodeNode(appName, "PaymentProcessor", "process", "method");
        node1.setId(1L);
        CodeNode node2 = new CodeNode(appName, "BankConnector", "transfer", "method");
        node2.setId(2L);
        
        CodeEdge edge = new CodeEdge(node1, node2, "calls");
        edge.setId(1L);
        
        when(nodeRepository.findByAppName(appName)).thenReturn(Arrays.asList(node1, node2));
        when(edgeRepository.findByAppName(appName)).thenReturn(Collections.singletonList(edge));
        
        var graph = graphService.loadGraphFromDB(appName);
        
        assertNotNull(graph);
        assertEquals(2, graph.vertexSet().size());
        assertEquals(1, graph.edgeSet().size());
    }
    
    @Test
    @DisplayName("Get callees of a method")
    public void testGetCallees() {
        String appName = "testapp";
        String methodName = "PaymentProcessor.process";
        
        CodeNode caller = new CodeNode(appName, "PaymentProcessor", "process", "method");
        caller.setId(1L);
        CodeNode callee = new CodeNode(appName, "BankConnector", "transfer", "method");
        callee.setId(2L);
        
        CodeEdge edge = new CodeEdge(caller, callee, "calls");
        
        when(nodeRepository.findByAppNameAndFullName(appName, methodName))
            .thenReturn(Optional.of(caller));
        when(edgeRepository.findByAppNameAndSourceId(appName, 1L))
            .thenReturn(Collections.singletonList(edge));
        
        Set<CodeNode> callees = graphService.getCallees(appName, methodName);
        
        assertNotNull(callees);
        assertEquals(1, callees.size());
        assertTrue(callees.contains(callee));
    }
    
    @Test
    @DisplayName("Get callers of a method")
    public void testGetCallers() {
        String appName = "testapp";
        String methodName = "BankConnector.transfer";
        
        CodeNode caller = new CodeNode(appName, "PaymentProcessor", "process", "method");
        caller.setId(1L);
        CodeNode callee = new CodeNode(appName, "BankConnector", "transfer", "method");
        callee.setId(2L);
        
        CodeEdge edge = new CodeEdge(caller, callee, "calls");
        
        when(nodeRepository.findByAppNameAndFullName(appName, methodName))
            .thenReturn(Optional.of(callee));
        when(edgeRepository.findByAppNameAndTargetId(appName, 2L))
            .thenReturn(Collections.singletonList(edge));
        
        Set<CodeNode> callers = graphService.getCallers(appName, methodName);
        
        assertNotNull(callers);
        assertEquals(1, callers.size());
        assertTrue(callers.contains(caller));
    }
    
    @Test
    @DisplayName("Build and persist graph")
    public void testBuildAndPersistGraph() {
        String appName = "testapp";
        
        CodeNode node = new CodeNode(appName, "TestClass", "testMethod", "method");
        CodeEdge edge = new CodeEdge();
        
        assertDoesNotThrow(() -> {
            graphService.buildAndPersistGraph(appName, 
                Collections.singletonList(node), 
                Collections.singletonList(edge));
        });
    }
    
    @Test
    @DisplayName("Get graph statistics")
    public void testGetGraphStats() {
        String appName = "testapp";
        
        when(nodeRepository.countByAppName(appName)).thenReturn(10L);
        when(edgeRepository.countByAppName(appName)).thenReturn(15L);
        when(nodeRepository.countByAppNameAndType(appName, "class")).thenReturn(3L);
        when(nodeRepository.countByAppNameAndType(appName, "method")).thenReturn(7L);
        when(edgeRepository.findMostFrequentEdges(appName, 10)).thenReturn(new ArrayList<>());
        
        GraphAnalysisService.GraphStats stats = graphService.getGraphStats(appName);
        
        assertNotNull(stats);
        assertEquals(10L, stats.nodeCount);
        assertEquals(15L, stats.edgeCount);
        assertEquals(3L, stats.classCount);
        assertEquals(7L, stats.methodCount);
    }
    
    @Test
    @DisplayName("Get entry points (methods with no callers)")
    public void testGetEntryPoints() {
        String appName = "testapp";
        
        CodeNode node1 = new CodeNode(appName, "Main", "main", "method");
        node1.setId(1L);
        
        when(nodeRepository.findByAppName(appName)).thenReturn(Collections.singletonList(node1));
        when(edgeRepository.findByAppName(appName)).thenReturn(new ArrayList<>());
        
        List<CodeNode> entryPoints = graphService.getEntryPoints(appName);
        
        assertNotNull(entryPoints);
        assertEquals(1, entryPoints.size());
    }
    
    @Test
    @DisplayName("Get leaf nodes (methods with no callees)")
    public void testGetLeafNodes() {
        String appName = "testapp";
        
        CodeNode node = new CodeNode(appName, "Logger", "log", "method");
        node.setId(1L);
        
        when(nodeRepository.findByAppName(appName)).thenReturn(Collections.singletonList(node));
        when(edgeRepository.findByAppName(appName)).thenReturn(new ArrayList<>());
        
        List<CodeNode> leafNodes = graphService.getLeafNodes(appName);
        
        assertNotNull(leafNodes);
        assertEquals(1, leafNodes.size());
    }
    
    @Test
    @DisplayName("Get call chain between two methods")
    public void testGetCallChain() {
        String appName = "testapp";
        String from = "A.method";
        String to = "C.method";
        
        CodeNode nodeA = new CodeNode(appName, "A", "method", "method");
        nodeA.setId(1L);
        CodeNode nodeC = new CodeNode(appName, "C", "method", "method");
        nodeC.setId(3L);
        
        when(nodeRepository.findByAppNameAndFullName(appName, from))
            .thenReturn(Optional.of(nodeA));
        when(nodeRepository.findByAppNameAndFullName(appName, to))
            .thenReturn(Optional.of(nodeC));
        when(nodeRepository.findByAppName(appName))
            .thenReturn(Arrays.asList(nodeA, nodeC));
        when(edgeRepository.findByAppName(appName)).thenReturn(new ArrayList<>());
        
        List<CodeNode> chain = graphService.getCallChain(appName, from, to);
        
        assertNotNull(chain);
    }
}
