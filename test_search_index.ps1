#!/usr/bin/env powershell
# Quick test to check and rebuild search index

Write-Host "==================================================================="
Write-Host "Search Index Diagnostics and Rebuild"
Write-Host "==================================================================="
Write-Host ""

$baseUrl = "http://127.0.0.1:8081"

# Step 1: Check status
Write-Host "[1] Checking search index status..."
try {
    $status = Invoke-RestMethod "$baseUrl/api/search/index/status" -ErrorAction Stop
    Write-Host "✓ Database Status:"
    Write-Host "  - Total Files: $($status.totalFiles)"
    Write-Host "  - Total Chunks: $($status.totalChunks)"
    Write-Host "  - Has Indexed Data: $($status.hasIndexedData)"
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get status: $_"
    Write-Host ""
    exit 1
}

# Step 2: If data exists, rebuild index
if ($status.hasIndexedData) {
    Write-Host "[2] Data exists in database! Rebuilding search index..."
    try {
        $rebuild = Invoke-RestMethod -Uri "$baseUrl/api/search/index/rebuild" `
            -Method POST -ErrorAction Stop
        
        Write-Host "✓ Index Rebuild Complete:"
        Write-Host "  - Status: $($rebuild.status)"
        Write-Host "  - Chunks Indexed: $($rebuild.indexedCount)"
        Write-Host "  - Duration: $($rebuild.durationMs) ms"
        Write-Host ""
    } catch {
        Write-Host "✗ Rebuild failed: $_"
        Write-Host ""
        exit 1
    }
} else {
    Write-Host "[2] No data in database. Need to run indexing first:"
    Write-Host "  POST /api/indexing/run with root path"
    Write-Host ""
    exit 0
}

# Step 3: Try a search
Write-Host "[3] Testing search functionality..."
try {
    $search = Invoke-RestMethod "$baseUrl/api/search?q=class&app=default" `
        -ErrorAction Stop
    
    Write-Host "✓ Search works!"
    Write-Host "  - Results: $($search.results.Count) found"
    Write-Host ""
} catch {
    Write-Host "✗ Search failed: $_"
    Write-Host ""
    exit 1
}

Write-Host "==================================================================="
Write-Host "✓ All systems operational!"
Write-Host "==================================================================="
