<#!
.Rebuild 64-bit Python 3.12 virtual environment with minimal or extended requirements.
#>
[CmdletBinding()]
param(
    [switch]$Force,
    [switch]$Extras,
    [string]$VenvPath = ".venv"
)

function Fail($msg){ Write-Host "ERROR: $msg" -ForegroundColor Red; exit 2 }
function Info($msg){ Write-Host "[INFO] $msg" }

# Detect launcher / python
$py = Get-Command py -ErrorAction SilentlyContinue
if(-not $py){ Fail "Python launcher 'py' not found. Install 64-bit Python 3.12 from python.org or winget." }

# Verify 64-bit 3.12 availability
$verInfo = & py -3.12 -c "import platform,struct,sys; print(platform.python_version()+'|'+str(struct.calcsize('P')*8))" 2>$null
if(-not $verInfo){ Fail "Python 3.12 not available via 'py -3.12'." }
$parts = $verInfo.Split('|')
if($parts.Count -lt 2){ Fail "Unexpected version probe output: $verInfo" }
$pyVersion = $parts[0]; $pyBits = [int]$parts[1]
if($pyBits -ne 64){ Fail "Detected Python $pyVersion $pyBits-bit. Please install 64-bit build before rebuilding." }
Info "Using Python $pyVersion ($pyBits-bit)"

# Handle existing venv
if(Test-Path $VenvPath){
    if($Force){ Info "Removing existing venv at $VenvPath"; Remove-Item -Recurse -Force $VenvPath }
    else { Fail "Virtual env '$VenvPath' already exists. Use -Force to replace." }
}

Info "Creating venv..."
& py -3.12 -m venv $VenvPath
if(-not $?) { Fail "venv creation failed" }

$activate = Join-Path $VenvPath "Scripts/Activate.ps1"
if(-not (Test-Path $activate)){ Fail "Activation script not found: $activate" }
. $activate
Info "Activated venv at $VenvPath"

Info "Upgrading pip tooling"
python -m pip install -q -U pip setuptools wheel || Fail "Failed to upgrade pip/setuptools/wheel"

$reqMain = "tools/requirements-python.txt"
if(-not (Test-Path $reqMain)){ Fail "Requirements file missing: $reqMain" }
Info "Installing minimal requirements"
pip install -q -r $reqMain || Fail "Failed to install minimal requirements"

if($Extras){
    $reqExtras = "tools/requirements-python-extras.txt"
    if(Test-Path $reqExtras){
        Info "Installing extras requirements"
        pip install -q -r $reqExtras || Fail "Failed to install extras requirements"
    } else { Info "Extras file not found ($reqExtras); skipping." }
}

Info "Running wheel/import checks"
if(Test-Path "tools/scripts/check_wheels.py"){
    python tools/scripts/check_wheels.py
    if($LASTEXITCODE -ne 0){ Fail "Import checks failed" }
} else { Info "check_wheels.py not present; skipping import tests" }

Info "Environment ready. To refreeze: pip freeze > tools/requirements-python.txt"
exit 0
# Minimal pinned tooling requirements for 64-bit Python 3.12
# Focus: embedding + vector db + basic server/runtime
chromadb==0.6.3
onnxruntime==1.23.2
optimum==2.0.0
numpy==2.3.4
hnswlib==0.8.0
faiss-cpu==1.12.0
pydantic==2.12.3
httpx==0.28.1
requests==2.32.5
sentence-transformers==5.1.1
transformers==4.55.4
tokenizers==0.21.4
uvicorn==0.38.0
rich==14.2.0
orjson==3.11.3
# Optional heavy deps (move to extras file if desired): torch, scikit-learn, scipy

