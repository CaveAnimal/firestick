#!/usr/bin/env pwsh
# Wrapper for running compute_percent.py on Windows/PowerShell
param(
    [Switch]$Apply
)

$script = Join-Path $PSScriptRoot 'compute_percent.py'
if (-Not (Test-Path $script)) {
    Write-Error "Missing script: $script"
    exit 1
}

$python = 'python'
if ($Apply) {
    & $python $script --apply
} else {
    & $python $script
}
