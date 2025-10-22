# PowerShell wrapper to run tools/scripts/check_wheels.py
# Activates repo .venv if present, then runs the Python script.
$ErrorActionPreference = 'Stop'

# Resolve repo root (two levels up from this script: tools/scripts -> repo root)
$repoRoot = Resolve-Path "$PSScriptRoot\..\.."
$repoRoot = $repoRoot.Path

$activate = Join-Path $repoRoot ".venv\Scripts\Activate.ps1"
if (Test-Path $activate) {
    Write-Output "Activating venv: $activate"
    & $activate
} else {
    Write-Output "No .venv Activate.ps1 found at $activate - will use system python"
}

# Determine python command
$pythonCmd = $null
if (Get-Command python -ErrorAction SilentlyContinue) {
    $pythonCmd = @('python')
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    $pythonCmd = @('py','-3.12')
}

if (-not $pythonCmd) {
    Write-Error "No 'python' or 'py' launcher found on PATH. Install Python 3.12 or ensure 'py'/'python' is on PATH."
    exit 3
}

$scriptPath = Join-Path $PSScriptRoot 'check_wheels.py'
if (-not (Test-Path $scriptPath)) {
    Write-Error "check_wheels.py not found at $scriptPath"
    exit 4
}

Write-Output "Running import checks with: $($pythonCmd -join ' ')"
try {
    if ($pythonCmd.Length -eq 1) {
        & $pythonCmd[0] $scriptPath
    } else {
        & $pythonCmd[0] $pythonCmd[1] $scriptPath
    }
    $rc = $LASTEXITCODE
} catch {
    Write-Error "Execution failed: $($_.Exception.Message)"
    $rc = 5
}

Write-Output "Exit code: $rc"
exit $rc
