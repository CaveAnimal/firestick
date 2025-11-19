<#
Python Environment Setup & Diagnostics Script
-------------------------------------------------
Purpose:
  Rebuild a clean Python virtual environment and diagnose/mitigate
  package download issues in a restricted network (Zscaler, proxy, VPN).

Usage (PowerShell 5/7):
  1. OPTIONAL: Connect VPN first if required for package downloads.
  2. From project root:  .\tools\scripts\python_env_setup.ps1 -Action Full

Actions:
  -Action Full        : Full rebuild + diagnostics + attempt installs.
  -Action Minimal     : Only create venv and install minimal base packages.
  -Action Diagnostics : Run network/package tests without modifying venv.
  -Action Stubs       : Create stub modules to allow development without packages.

Parameters:
  -PythonExe          : Explicit Python executable (default 'py').
  -PythonVersion      : Version spec for venv (default '3.12').
  -RequirementsFile   : Path to full requirements (default 'requirements.txt').
  -MinimalReqFile     : Path to minimal requirements (default 'requirements-minimal.txt').
  -OfflineWheelDir    : Directory containing offline wheels (optional).
  -Force              : Remove existing venv even if in use (attempt).

Notes:
  - Creates a log file: ./.python_env_setup.log
  - Safe to re-run; idempotent operations guarded.
  - Stub modules placed under ./python_stubs/ if -Action Stubs or Full (no packages found).
-------------------------------------------------
#>
param(
    [ValidateSet('Full','Minimal','Diagnostics','Stubs')]
    [string]$Action = 'Full',
    [string]$PythonExe = 'py',
    [string]$PythonVersion = '3.12',
    [string]$VenvPath = '.venv',
    [string]$RequirementsFile = 'requirements.txt',
    [string]$MinimalReqFile = 'requirements-minimal.txt',
    [string]$OfflineWheelDir = '',
    [switch]$Force
)

$ErrorActionPreference = 'Stop'
$logFile = Join-Path (Get-Location) '.python_env_setup.log'
function Write-Log {
    param([string]$Message,[string]$Level = 'INFO')
    $timestamp = (Get-Date).ToString('u')
    $line = "[$timestamp][$Level] $Message"
    Write-Host $line
    Add-Content -Path $logFile -Value $line
}

function Test-PythonVersion {
    Write-Log "Checking Python version/arch..."
    $code = 'import platform,struct; print(platform.python_version(), platform.architecture()[0], struct.calcsize("P")*8)'
    $result = & $PythonExe -$PythonVersion -c $code 2>$null
    Write-Log "Python info: $result"
}

function Remove-Venv {
    param([string]$Path)
    if(Test-Path $Path){
        Write-Log "Removing existing venv at $Path" 'WARN'
    try{ Remove-Item -Recurse -Force $Path } catch { Write-Log "Failed to remove ${Path}: $($_.Exception.Message)" 'ERROR'; if(-not $Force){ throw } }
    } else {
        Write-Log "No existing venv at $Path to remove"
    }
}

function Create-Venv {
    Write-Log "Creating venv $VenvPath with Python $PythonVersion"
    & $PythonExe -$PythonVersion -m venv $VenvPath
    if(-not (Test-Path "$VenvPath/Scripts/Activate.ps1")){ throw "Venv activation script missing." }
}

function Activate-Venv {
    Write-Log "Activating venv $VenvPath"
    & "$VenvPath/Scripts/Activate.ps1"
}

function Upgrade-BuildTools {
    Write-Log "Upgrading pip/setuptools/wheel"
    try {
        python -m pip install --upgrade pip setuptools wheel
    } catch {
        Write-Log "Upgrade failed: $($_.Exception.Message)" 'ERROR'
    }
}

function Show-PipConfig { pip config list | ForEach-Object { Write-Log "pip config: $_" } }

function Test-ArtifactAccess {
    Write-Log "Testing HEAD access to sample wheel..."
    $wheelUrl = 'https://files.pythonhosted.org/packages/78/b6/6307fbef88d9b5ee7421e68d78a9f162e0da4900bc5f5793f6d3d0e34fb8/annotated_types-0.7.0-py3-none-any.whl'
    try { (Invoke-WebRequest -Uri $wheelUrl -Method Head -UseBasicParsing).StatusCode | ForEach-Object { Write-Log "HEAD status: $_" } } catch { Write-Log "HEAD failed: $($_.Exception.Message)" 'ERROR' }
    Write-Log "Testing GET wheel download (expect block if policy)" 'WARN'
    try {
        Invoke-WebRequest -Uri $wheelUrl -OutFile 'annotated_types-0.7.0-py3-none-any.whl' -UseBasicParsing
        if(Test-Path 'annotated_types-0.7.0-py3-none-any.whl'){ Write-Log "Wheel downloaded successfully" } else { Write-Log "Wheel not present post-download" 'ERROR' }
    } catch { Write-Log "Wheel GET blocked: $($_.Exception.Message)" 'ERROR' }
    Write-Log "Testing GET source tarball..." 'WARN'
    $tarUrl = 'https://files.pythonhosted.org/packages/ee/67/531ea369ba64dcff5ec9c3402f9f51bf748cec26dde048a2f973a4eea7f5/annotated_types-0.7.0.tar.gz'
    try { Invoke-WebRequest -Uri $tarUrl -OutFile 'annotated_types-0.7.0.tar.gz' -UseBasicParsing; if(Test-Path 'annotated_types-0.7.0.tar.gz'){ Write-Log "Tarball downloaded successfully" } } catch { Write-Log "Tarball GET blocked: $($_.Exception.Message)" 'ERROR' }
}

function Install-Requirements {
    param([string]$ReqFile,[string]$Mode)
    if(-not (Test-Path $ReqFile)){ Write-Log "$ReqFile not found, skipping $Mode install" 'ERROR'; return }
    Write-Log "Attempting $Mode requirements install from $ReqFile"
    try {
        if($OfflineWheelDir -and (Test-Path $OfflineWheelDir)){
            Write-Log "Using offline wheels in $OfflineWheelDir" 'INFO'
            pip install --no-index --find-links $OfflineWheelDir -r $ReqFile
        } else {
            pip install -r $ReqFile
        }
    } catch {
        Write-Log "$Mode install failed: $($_.Exception.Message)" 'ERROR'
    }
}

function Create-MinimalReqFile {
    if(Test-Path $MinimalReqFile){ Write-Log "Minimal requirements file already exists: $MinimalReqFile"; return }
    @(
        'annotated-types==0.7.0',
        'typing-extensions==4.12.2',
        'pydantic==2.9.2',
        'numpy==2.1.2'
    ) | Set-Content $MinimalReqFile
    Write-Log "Created minimal requirements file $MinimalReqFile"
}

function Create-Stubs {
    $stubRoot = 'python_stubs'
    if(-not (Test-Path $stubRoot)){ New-Item -ItemType Directory -Path $stubRoot | Out-Null }
    $stubs = @{
        'sentence_transformers/__init__.py' = @'
class SentenceTransformer:
    def __init__(self, model_name:str):
        self.model_name = model_name
    def encode(self, texts, **kwargs):
        # Return deterministic zero vectors
        return [[0.0]*384 for _ in texts]
'@;
        'faiss/__init__.py' = "def IndexFlatL2(d): return object()";
        'chromadb/__init__.py' = "class Client: pass";
        'onnxruntime/__init__.py' = "def InferenceSession(path): return object()";
        'torch/__init__.py' = "def tensor(x): return x";
        'transformers/__init__.py' = @'
class AutoTokenizer:
    @classmethod
    def from_pretrained(cls, name): return cls()
    def encode(self, text): return [0]
class AutoModel:
    @classmethod
    def from_pretrained(cls, name): return cls()
'@
    }
    foreach($relPath in $stubs.Keys){
        $fullPath = Join-Path $stubRoot $relPath
        $dir = Split-Path $fullPath -Parent
        if(-not (Test-Path $dir)){ New-Item -ItemType Directory -Path $dir | Out-Null }
        if(-not (Test-Path $fullPath)){ $stubs[$relPath] | Set-Content $fullPath; Write-Log "Created stub: $relPath" } else { Write-Log "Stub exists: $relPath" }
    }
    Write-Log "Stub modules ready. Use try/except ImportError to fallback." 'INFO'
}

function Summary {
    Write-Log "pip list (top 15):"; try { pip list | Select-Object -First 15 | ForEach-Object { Write-Log $_ } } catch { Write-Log "pip list failed" 'ERROR' }
    Write-Log "python -V:"; try { python -V | ForEach-Object { Write-Log $_ } } catch { }
}

Write-Log "---- Python Environment Setup Script Started (Action=$Action) ----"
switch($Action){
    'Diagnostics' {
        Test-PythonVersion
        Show-PipConfig
        Test-ArtifactAccess
    }
    'Minimal' {
        Test-PythonVersion
        Remove-Venv -Path $VenvPath
        Create-Venv
        Activate-Venv
        Upgrade-BuildTools
        Create-MinimalReqFile
        Install-Requirements -ReqFile $MinimalReqFile -Mode 'Minimal'
        Summary
    }
    'Full' {
        Test-PythonVersion
        Remove-Venv -Path $VenvPath
        Create-Venv
        Activate-Venv
        Upgrade-BuildTools
        Create-MinimalReqFile
        Install-Requirements -ReqFile $MinimalReqFile -Mode 'Minimal'
        Install-Requirements -ReqFile $RequirementsFile -Mode 'Full'
        Test-ArtifactAccess
        if((pip list | Select-String 'annotated-types') -eq $null){
            Write-Log "Core packages missing; generating stubs" 'WARN'
            Create-Stubs
        }
        Summary
    }
    'Stubs' {
        Create-Stubs
    }
}
Write-Log "---- Script Completed ----"
