#!/usr/bin/env pwsh
param(
    [int]$Port = 9001,
    [int]$JobId = 123
)

Write-Host "Starting synthetic SSE server on port $Port (jobId=$JobId)" -ForegroundColor Cyan
python .\tools\work\dev5\scripts\synthetic_indexing_server.py --port $Port --job-id $JobId
