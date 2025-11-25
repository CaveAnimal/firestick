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
    Write-Host "❌ Virtual environment not found at: $venvPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Creating Python virtual environment..." -ForegroundColor Yellow
    python -m venv $venvPath
    Write-Host "✅ Virtual environment created" -ForegroundColor Green
}

# Activate virtual environment
Write-Host ""
Write-Host "Activating virtual environment..." -ForegroundColor Yellow
& $activateScript
Write-Host "✅ Virtual environment activated" -ForegroundColor Green

# Check if chroma is installed
Write-Host ""
Write-Host "Checking if chroma is installed..." -ForegroundColor Yellow
$chromaInstalled = pip list | Select-String "chromadb"

if (-not $chromaInstalled) {
    Write-Host "Installing Chroma..." -ForegroundColor Yellow
    pip install chromadb
    Write-Host "✅ Chroma installed" -ForegroundColor Green
} else {
    Write-Host "✅ Chroma is already installed" -ForegroundColor Green
}

# Create chroma_data directory if it doesn't exist
$chromaDataDir = Join-Path $scriptDir "chroma_data"
if (-not (Test-Path $chromaDataDir)) {
    Write-Host ""
    Write-Host "Creating chroma_data directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $chromaDataDir | Out-Null
    Write-Host "✅ Created: $chromaDataDir" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "✅ chroma_data directory already exists" -ForegroundColor Green
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
    chroma run --path $chromaDataDir --port 8000 --host 0.0.0.0 $configFile
} else {
    Write-Host "Config file not found, using defaults" -ForegroundColor Yellow
    chroma run --path $chromaDataDir --port 8000 --host 0.0.0.0
}

Write-Host ""
Write-Host "❌ Chroma server stopped" -ForegroundColor Red
