<#
Start-All: Start backend (Spring Boot), UI (Vite), Chroma vector DB, Python LLM service

Usage: .\start-all.ps1 [-SkipChroma] [-SkipLLM] [-SkipBackend] [-TimeoutSeconds 120]

# Notes:
# - Starting profile: ONNX is the default now. To start without ONNX, pass `-NoOnnx`.
# - If you still want forced ONNX, the `-WithOnnx` flag remains supported for compatibility.

This script will start processes in new PowerShell windows and then poll health endpoints to verify they started.
#>

param(
  [switch]$SkipChroma,
  # [switch]$SkipUI,  # UI is now served by the backend (server-side templates) - no separate Vite dev server
  [switch]$SkipLLM,
  [switch]$SkipBackend,
  [switch]$WithOnnx,
  [switch]$NoOnnx,
  [int]$TimeoutSeconds = 120
)

$ErrorActionPreference = 'Stop'

# Make ONNX the default mode unless the user explicitly requests no-onnx
if ($PSBoundParameters.ContainsKey('NoOnnx')) {
  $UseOnnx = $false
} elseif ($PSBoundParameters.ContainsKey('WithOnnx')) {
  $UseOnnx = $true
} else {
  $UseOnnx = $true
}

function Get-LogFileName {
  param([string]$ServiceName)
  $date = Get-Date -Format 'yyyy-MM-dd'
  $logDir = Join-Path $PSScriptRoot "logs\$ServiceName"
  if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Force -Path $logDir | Out-Null }
  
  $baseName = "$ServiceName-$date"
  $charIndex = 97 # 'a'
  
  $logFile = Join-Path $logDir "$baseName.log"
  if (Test-Path $logFile) {
      while (Test-Path (Join-Path $logDir "$baseName$([char]$charIndex).log")) {
        $charIndex++
      }
      $logFile = Join-Path $logDir "$baseName$([char]$charIndex).log"
  }
  
  return $logFile
}

function Wait-ForHttp {
  param(
    [string]$Url,
    # Local function-level timeout (default 60s). Script-level $TimeoutSeconds is passed
    # into this function calls to enforce a consistent global timeout.
    [int]$HttpTimeoutSeconds = 60,
    [int]$IntervalSeconds = 2
  )

  Write-Host "Waiting for $Url (timeout=${HttpTimeoutSeconds}s) ..." -ForegroundColor Cyan
  $deadline = [DateTime]::UtcNow.AddSeconds($HttpTimeoutSeconds)
  while ([DateTime]::UtcNow -lt $deadline) {
    try {
      $r = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
      if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 400) {
        Write-Host "OK: $Url responded: $($r.StatusCode)" -ForegroundColor Green
        return $true
      }
    } catch {
      # ignore and retry
    }
    Start-Sleep -Seconds $IntervalSeconds
  }
  Write-Host "Timed out waiting for $Url" -ForegroundColor Yellow
  return $false
}

# End Wait-ForHttp

function Start-NewWindow {
  param(
    [string]$Name,
    [string]$Command,
    [string]$LogFile,
    [string]$WorkingDirectory = $PSScriptRoot
  )
  Write-Host "Starting: $Name -> $Command" -ForegroundColor Cyan
  if ($LogFile) { Write-Host "Logging to: $LogFile" -ForegroundColor Gray }

  if (Get-Command pwsh.exe -ErrorAction SilentlyContinue) { $shell = 'pwsh' } else { $shell = 'powershell' }
  
  if ($LogFile) {
     # Wrap command to capture stdout and stderr to file
     $finalCmd = "& { $Command } 2>&1 | Tee-Object -FilePath '$LogFile'"
  } else {
     $finalCmd = $Command
  }

  Start-Process -FilePath $shell -ArgumentList '-NoProfile','-NoExit','-Command',$finalCmd -WorkingDirectory $WorkingDirectory
}

# End Start-NewWindow

# Find the backend port by probing a range of ports for a healthy /api/health endpoint.
# Tries ports in the provided range sequentially; if none respond it waits 60s and retries.
function Find-BackendPort {
  param(
    [int[]]$Ports = @(8081,8080,8082,8083,8084,8085),
    [int]$TimeoutSec = 3
  )

  while ($true) {
    foreach ($p in $Ports) {
      try {
        $probeUrl = "http://127.0.0.1:$p/api/health"
        # Quick probe ??? succeed for any 2xx/3xx response
        $r = Invoke-WebRequest -Uri $probeUrl -UseBasicParsing -TimeoutSec $TimeoutSec -ErrorAction Stop
        if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 400) {
          Write-Host "Found backend on port $p (probe: $probeUrl)" -ForegroundColor Green
          return $p
        }
      } catch {
        # ignore and move to next port
      }
    }

    Write-Host "No backend found on ports $($Ports -join ', ') ??? retrying in 60s..." -ForegroundColor Yellow
    Start-Sleep -Seconds 60
  }
}

  # End Find-BackendPort

$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $root

## Note: Chroma and LLM are intentionally not started until after the
## backend starts and we sleep for a minute; see workflow below.

if (-not $SkipBackend) {
  $logFile = Get-LogFileName -ServiceName "backend"
  # Try running via mvn; fallback to running jar if present
  $mvn = 'mvn'
  $jar = Get-ChildItem -Path "$root\target" -Filter "*.jar" -File -ErrorAction SilentlyContinue | Select-Object -First 1
  if (Get-Command $mvn -ErrorAction SilentlyContinue) {
    # Use direct invocation to allow piping to log file
    $mvnArgs = @('-DskipTests')
    if ($UseOnnx) { $mvnArgs += '-Dspring-boot.run.profiles=onnx' }
    $mvnArgs += 'spring-boot:run'
    # Use single quotes for arguments to avoid issues with Start-Process argument parsing
    $mvnArgStr = ($mvnArgs | ForEach-Object { "'$_'" }) -join ' '
    $mvnCmd = "cd '$root'; & mvn $mvnArgStr"
    Start-NewWindow -Name 'Backend' -Command $mvnCmd -LogFile $logFile
  } elseif ($jar) {
    if ($UseOnnx) {
      # Ensure the app runs with the ONNX spring profile
      Start-NewWindow -Name 'Backend' -Command "`$env:SPRING_PROFILES_ACTIVE='onnx'; java -jar '$($jar.FullName)'" -LogFile $logFile
    } else {
      Start-NewWindow -Name 'Backend' -Command "java -jar '$($jar.FullName)'" -LogFile $logFile
    }
  } else {
    Write-Host "Maven not found and no runnable jar in target/ - Backend not started" -ForegroundColor Yellow
  }

  # Wait for backend to complete startup before starting next services (UI/Llm/Chroma)
  Write-Host "Waiting for backend to be ready before continuing to next service..." -ForegroundColor Cyan
  if ($UseOnnx) {
    $backendUrl = 'http://127.0.0.1:8081/api/health'
  } else {
    $backendUrl = 'http://127.0.0.1:8080/api/health'
  }
  $backendOk = Wait-ForHttp -Url $backendUrl -HttpTimeoutSeconds $TimeoutSeconds
  # If backend didn't become healthy in the expected port, attempt to auto-detect which
  # backend port is active (useful when developers run with different profiles).
  $detectedBackendPort = $null
  if (-not $backendOk) {
    Write-Host "Backend not healthy at expected URL; trying to detect backend port automatically..." -ForegroundColor Yellow
    $detectedBackendPort = Find-BackendPort
    if ($detectedBackendPort) {
      Write-Host "Backend detected on port $detectedBackendPort; continuing startup" -ForegroundColor Green
      $backendOk = $true
      # Use the detected port for subsequent steps
      $backendUrl = "http://127.0.0.1:$detectedBackendPort/api/health"
    }
  } else {
    # Backend responded at the expected endpoint; set detected port for later use
      if ($UseOnnx) { $detectedBackendPort = 8081 } else { $detectedBackendPort = 8080 }
  }
  if (-not $backendOk) {
    Write-Host "WARN: Backend did not become healthy in time. Proceeding to start remaining services but UI may fail to connect." -ForegroundColor Yellow
  } else {
    # If ONNX is active verify embedding mode via /api/embedding/info
    if ($UseOnnx) {
      try {
        $info = Invoke-RestMethod -Uri 'http://127.0.0.1:8081/api/embedding/info' -UseBasicParsing -TimeoutSec 5
        if ($info -and $info.mode -eq 'ONNX') { Write-Host "Backend ONNX mode OK" -ForegroundColor Green } else { Write-Host "WARN: Backend expected ONNX mode, but embedding.mode was '$($info.mode)'" -ForegroundColor Yellow }
      } catch { Write-Host "WARN: Could not fetch embedding info (ONNX verify) - $($_.Exception.Message)" -ForegroundColor Yellow }
    }
  }

  # Wait at least a minute after backend is started before launching other services
  # Write-Host "Waiting 60s to let the backend finish initialization before starting other services..." -ForegroundColor Cyan
  # Start-Sleep -Seconds 60
}

## UI will be started after backend wait and LLM/Chroma ??? see below

if (-not $SkipLLM) {
  $logFile = Get-LogFileName -ServiceName "llm"
  # Activate venv in a new PowerShell and run the LLM script
  $venvActivate = "$root\.venv\Scripts\Activate.ps1"
  if (-not (Test-Path $venvActivate)) {
    Write-Host "Virtual environment not found at $venvActivate - Attempting to create..." -ForegroundColor Yellow
    python -m venv "$root\.venv"
  }
  # Use GGUF model for better performance on Windows/CPU
  $llmCmd = "& '$venvActivate'; `$env:MODEL_PATH='models/mistral/models/mistral-7b-instruct-v0.2.Q4_K_M.gguf'; python '$root\llm_service_gguf.py'"
  Start-NewWindow -Name 'LLM' -Command $llmCmd -LogFile $logFile
}

if (-not $SkipChroma) {
  $logFile = Get-LogFileName -ServiceName "chroma"
  if (Test-Path "$root\start-chroma.ps1") {
    Start-NewWindow -Name 'Chroma' -Command "& '$root\start-chroma.ps1'" -LogFile $logFile
  } else {
    Write-Host "Chroma start script not found; skipping" -ForegroundColor Yellow
  }
}

# UI is now part of the backend; no separate dev server will be started.

Write-Host "All start commands issued. Verifying services..." -ForegroundColor Cyan

$allOk = $true

if (-not $SkipBackend) {
  # If we detected the backend port earlier, use it for verification, otherwise fall back to defaults
  if ($detectedBackendPort) {
    $ok = Wait-ForHttp -Url "http://127.0.0.1:$detectedBackendPort/api/health" -HttpTimeoutSeconds $TimeoutSeconds
  } else {
    if ($UseOnnx) {
      # ONNX profile already verified earlier during startup; here we only check the health endpoint again
      $ok = Wait-ForHttp -Url 'http://127.0.0.1:8081/api/health' -HttpTimeoutSeconds $TimeoutSeconds
    } else {
      $ok = Wait-ForHttp -Url 'http://127.0.0.1:8080/api/health' -HttpTimeoutSeconds $TimeoutSeconds
    }
  }
  if (-not $ok) { $allOk = $false }
}

if (-not $SkipLLM) {
  $ok = Wait-ForHttp -Url 'http://localhost:8001/health' -HttpTimeoutSeconds $TimeoutSeconds
  if (-not $ok) { $allOk = $false }
}

if (-not $SkipChroma) {
  # Chroma exposes a heartbeat endpoint
  $ok = Wait-ForHttp -Url 'http://localhost:8000/api/v1/heartbeat' -HttpTimeoutSeconds $TimeoutSeconds
  if ($ok) { Write-Host 'Chroma: listening on port 8000' -ForegroundColor Green } else { Write-Host 'Chroma: not reachable on port 8000' -ForegroundColor Yellow; $allOk = $false }
}

 # UI is served by the backend; no separate UI check required

if ($allOk) { Write-Host '??? All services started successfully' -ForegroundColor Green } else { Write-Host '??????  Some services failed to start or did not respond in time' -ForegroundColor Yellow }

Pop-Location

