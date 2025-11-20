package com.codetalker.firestick.graph.controller;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codetalker.firestick.graph.service.GraphAnalysisService;

@WebMvcTest(DependencyGraphController.class)
@ActiveProfiles("test")
@DisplayName("DependencyGraphController Tests")
public class GraphControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private GraphAnalysisService graphService;
    
    @BeforeEach
    public void setUp() {
    }
    
    @Test
    @DisplayName("Get dependencies endpoint returns 200")
    public void testGetDependencies() throws Exception {
        String appName = "testapp";
        String methodName = "TestClass.testMethod";
        
        when(graphService.getTransitiveDeps(appName, methodName, 1))
            .thenReturn(new HashSet<>());
        
        mockMvc.perform(get("/api/graph/dependencies/{appName}/{methodName}", appName, methodName)
                .param("depth", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.edges").isArray());
    }
    
    @Test
    @DisplayName("Get callers endpoint returns 200")
    public void testGetCallers() throws Exception {
        String appName = "testapp";
        String methodName = "TestClass.testMethod";
        
        when(graphService.getCallers(appName, methodName))
            .thenReturn(new HashSet<>());
        
        mockMvc.perform(get("/api/graph/callers/{appName}/{methodName}", appName, methodName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray());
    }
    
    @Test
    @DisplayName("Get callees endpoint returns 200")
    public void testGetCallees() throws Exception {
        String appName = "testapp";
        String methodName = "TestClass.testMethod";
        
        when(graphService.getCallees(appName, methodName))
            .thenReturn(new HashSet<>());
        
        mockMvc.perform(get("/api/graph/callees/{appName}/{methodName}", appName, methodName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray());
    }
    
    @Test
    @DisplayName("Detect cycles endpoint returns 200")
    public void testDetectCycles() throws Exception {
        String appName = "testapp";
        
        when(graphService.detectCircularDeps(appName))
            .thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/graph/cycles/{appName}", appName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cycles").isArray());
    }
    
    @Test
    @DisplayName("Get stats endpoint returns 200")
    public void testGetStats() throws Exception {
        String appName = "testapp";
        
        GraphAnalysisService.GraphStats stats = 
            new GraphAnalysisService.GraphStats(10, 15, 3, 7, 1.5, 5);
        
        when(graphService.getGraphStats(appName)).thenReturn(stats);
        
        mockMvc.perform(get("/api/graph/stats/{appName}", appName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodeCount").value(10))
            .andExpect(jsonPath("$.edgeCount").value(15));
    }
    
    @Test
    @DisplayName("Get entry points endpoint returns 200")
    public void testGetEntryPoints() throws Exception {
        String appName = "testapp";
        
        when(graphService.getEntryPoints(appName))
            .thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/graph/entry-points/{appName}", appName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray());
    }
    
    @Test
    @DisplayName("Get leaf nodes endpoint returns 200")
    public void testGetLeafNodes() throws Exception {
        String appName = "testapp";
        
        when(graphService.getLeafNodes(appName))
            .thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/graph/leaf-nodes/{appName}", appName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray());
    }
    
    @Test
    @DisplayName("Get call chain endpoint returns 200")
    public void testGetCallChain() throws Exception {
        String appName = "testapp";
        
        when(graphService.getCallChain(appName, "A.method", "B.method"))
            .thenReturn(new ArrayList<>());
        
        mockMvc.perform(get("/api/graph/call-chain/{appName}", appName)
                .param("from", "A.method")
                .param("to", "B.method"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray());
    }
}
