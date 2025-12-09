<#
run-tests.ps1 - A PowerShell wrapper to run Maven tests with a timeout

Usage:
  .\run-tests.ps1 -TestName LLMServiceLiveTest -TimeoutSeconds 120
  .\run-tests.ps1 -TimeoutSeconds 600  # run full test suite with 10m timeout

This script will start Maven with the provided `-Dtest` filter and wait up to the
given timeout. If the process exceeds the timeout it will be terminated.
#>

param(
  [string]$TestName = "",
  [int]$TimeoutSeconds = 600,
  [switch]$UiTests
)

$ErrorActionPreference = 'Stop'

function Start-TestProcess {
  param([string]$Command, [string[]]$ArgumentList)
  Write-Host "Starting: $Command $($ArgumentList -join ' ')" -ForegroundColor Cyan
  $proc = Start-Process -FilePath $Command -ArgumentList $ArgumentList -NoNewWindow -PassThru
  return $proc
}

if ($UiTests) {
  # Run UI tests using npm (Vite / Playwright / Vitest)
  $uiDir = Join-Path $PSScriptRoot 'ui'
  if (-not (Test-Path $uiDir)) {
    Write-Host "UI folder not found: $uiDir" -ForegroundColor Yellow
    exit 1
  }

  Push-Location $uiDir
  try {
  $argList = @('test')
  $proc = Start-TestProcess -Command 'npm' -ArgumentList $argList
    $done = Wait-Process -Id $proc.Id -Timeout $TimeoutSeconds
    if (-not $done) {
      Write-Host "UI tests timed out after $TimeoutSeconds seconds; killing process" -ForegroundColor Yellow
      Stop-Process -Id $proc.Id -Force
      exit 2
    }
  } finally { Pop-Location }
  exit 0
}

# Default: Run Maven tests
$mvn = 'mvn'
if (-not (Get-Command $mvn -ErrorAction SilentlyContinue)) {
  Write-Host "Maven not found in path" -ForegroundColor Red
  exit 1
}

 $mvnArgs = @('-DskipTests=false', 'test')
 $mvnArgList = $mvnArgs
if ($TestName -and $TestName.Trim().Length -gt 0) {
  $mvnArgList = @("-Dtest=$TestName", 'test')
}

$proc = Start-TestProcess -Command $mvn -ArgumentList $mvnArgList
$done = Wait-Process -Id $proc.Id -Timeout $TimeoutSeconds
if (-not $done) {
  Write-Host "Maven tests timed out after $TimeoutSeconds seconds; killing process" -ForegroundColor Yellow
  Stop-Process -Id $proc.Id -Force
  exit 2
}

Write-Host "Maven tests completed." -ForegroundColor Green
exit 0
