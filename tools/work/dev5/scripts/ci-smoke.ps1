#!/usr/bin/env pwsh
param(
    [int]$Port = 9001,
    [int]$JobId = 123
)

# start the synthetic server in background, run a small Node check and exit with its status
Write-Host "Starting synthetic server in background on port $Port"
$python = (Get-Command python -ErrorAction SilentlyContinue).Path
if(-not $python){ Write-Error 'python not found on PATH'; exit 2 }

$proc = Start-Process -FilePath $python -ArgumentList ".\tools\work\dev5\scripts\synthetic_indexing_server.py --port $Port --job-id $JobId" -NoNewWindow -PassThru
Start-Sleep -Seconds 1

Write-Host 'Running check script...'
$node = (Get-Command node -ErrorAction SilentlyContinue).Path
if(-not $node){ Write-Error 'node not found on PATH'; Stop-Process -Id $proc.Id -Force; exit 2 }

$check = Start-Process -FilePath $node -ArgumentList '.\tools\work\dev5\tests\check_synthetic_sse.js', "http://127.0.0.1:$Port/sse?jobId=$JobId" -NoNewWindow -Wait -PassThru
$status = $check.ExitCode

Write-Host "Check script exited with status: $status"

Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
exit $status
