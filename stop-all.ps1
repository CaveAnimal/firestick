<#
Stop-All: Stop background development services started by start-all.ps1

This script is intentionally broad — it kills java/mvn/node/python processes used by
the local dev flow (backend, Vite, Python LLM, and Chroma). It asks for confirmation
before stopping services. Use with caution.
#>

param(
  [switch]$Force
)

function Confirm-And-Kill {
  param([string]$Name)
  $processes = Get-Process -Name $Name -ErrorAction SilentlyContinue
  if ($processes) {
    Write-Host "Found $($processes.Count) process(es) with name '$Name'" -ForegroundColor Cyan
    if ($Force -or (Read-Host "Kill these processes? (y/N)") -match '^[yY]') {
      Stop-Process -InputObject $processes -Force -ErrorAction SilentlyContinue
      Write-Host "Killed $Name" -ForegroundColor Green
    } else {
      Write-Host "Skipped killing $Name" -ForegroundColor Yellow
    }
  } else {
    Write-Host "No process named $Name found" -ForegroundColor Gray
  }
}

# Standard dev processes
Confirm-And-Kill -Name 'node'
Confirm-And-Kill -Name 'mvn'
Confirm-And-Kill -Name 'java'
Confirm-And-Kill -Name 'python'

Write-Host "Note: Chroma or Vite may run as generic python/node processes. If you rely on process naming, use the -Force flag to kill without prompts." -ForegroundColor Yellow
