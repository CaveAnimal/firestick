# PowerShell script to set up and start Chroma vector database
# Usage: .\start-chroma.ps1

# Stop on any error
$ErrorActionPreference = "Stop"

Write-Host "================================" -ForegroundColor Cyan
Write-Host "Chroma Vector Database Setup" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Get the script directory
$scriptDir = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
Write-Host "Working directory: $scriptDir" -ForegroundColor Yellow

# Path to virtual environment
$venvPath = Join-Path $scriptDir ".venv"
$activateScript = Join-Path $venvPath "Scripts\Activate.ps1"

# Check if virtual environment exists
if (-not (Test-Path $venvPath)) {
    Write-Host "[ERROR] Virtual environment not found at: $venvPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Creating Python virtual environment..." -ForegroundColor Yellow
    python -m venv $venvPath
    Write-Host "[OK] Virtual environment created" -ForegroundColor Green
}

# Activate virtual environment
Write-Host ""
Write-Host "Activating virtual environment..." -ForegroundColor Yellow
& $activateScript
Write-Host "[OK] Virtual environment activated" -ForegroundColor Green

# Check if chroma is installed
Write-Host ""
Write-Host "Checking if chroma is installed..." -ForegroundColor Yellow
$chromaInstalled = pip list | Select-String "chromadb"

if (-not $chromaInstalled) {
    Write-Host "Installing Chroma..." -ForegroundColor Yellow
    pip install chromadb
    Write-Host "[OK] Chroma installed" -ForegroundColor Green
} else {
    Write-Host "[OK] Chroma is already installed" -ForegroundColor Green
}

# Create chroma_data directory if it doesn't exist
$chromaDataDir = Join-Path $scriptDir "chroma_data"
if (-not (Test-Path $chromaDataDir)) {
    Write-Host ""
    Write-Host "Creating chroma_data directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $chromaDataDir | Out-Null
    Write-Host "[OK] Created: $chromaDataDir" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[OK] chroma_data directory already exists" -ForegroundColor Green
}

# Start Chroma server
Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Starting Chroma Server" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Chroma will store data in: $chromaDataDir" -ForegroundColor Yellow
Write-Host ""
Write-Host "Starting Chroma on http://localhost:8000" -ForegroundColor Yellow
Write-Host ""

# Start Chroma with the specified data directory and config
$configFile = Join-Path $scriptDir "chroma_config.yaml"
if (Test-Path $configFile) {
    Write-Host "Using config file: $configFile" -ForegroundColor Yellow

    # The chroma CLI treats the positional CONFIG_PATH as mutually exclusive with --path
    # so avoid calling both simultaneously. If the config file already defines 'path',
    # run with the config file directly. Otherwise create a small temporary config
    # that injects the path and use that to start chroma.
    $configContent = Get-Content $configFile -Raw
    $hasPath = $configContent -match '(^|\n)\s*path\s*:'

    if ($hasPath) {
        Write-Host "Config already declares 'path' - starting chroma using config file only" -ForegroundColor Yellow
        chroma run $configFile
    } else {
        # Create a temporary config that includes the required path value so we can
        # use the positional config file without supplying the --path option
        $tmp = Join-Path $env:TEMP ("chroma_config_{0}.yaml" -f ([System.Guid]::NewGuid().ToString()))
        # Use single-quoted YAML value for path to avoid quoting/escape issues
        $injected = $configContent + "`npath: '$chromaDataDir'`n"
        $injected | Out-File -FilePath $tmp -Encoding utf8
        Write-Host "Created temporary config with injected path: $tmp" -ForegroundColor Yellow
        try {
            chroma run $tmp
        } finally {
            Remove-Item -Path $tmp -ErrorAction SilentlyContinue
        }
    }
} else {
    Write-Host "Config file not found, using defaults" -ForegroundColor Yellow
    chroma run --path $chromaDataDir --port 8000 --host 0.0.0.0
}

Write-Host ""
Write-Host "[ERROR] Chroma server stopped" -ForegroundColor Red
