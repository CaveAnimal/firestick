Param(
    [string]$Host = "localhost",
    [int]$Port = 8000
)

$base = "http://$Host:$Port"
Write-Host "Checking Chroma at $base" -ForegroundColor Cyan

try {
    $hb = (Invoke-WebRequest -UseBasicParsing "$base/api/v2/heartbeat").Content
    Write-Host "Heartbeat OK:" -ForegroundColor Green
    Write-Output $hb
} catch {
    Write-Host "Heartbeat FAILED:" -ForegroundColor Red
    Write-Warning $_.Exception.Message
}

try {
    $cols = (Invoke-WebRequest -UseBasicParsing "$base/api/v2/collections").Content
    Write-Host "Collections:" -ForegroundColor Green
    Write-Output $cols
} catch {
    Write-Host "List collections FAILED (may be empty or server starting):" -ForegroundColor Yellow
    Write-Warning $_.Exception.Message
}

Write-Host "Done." -ForegroundColor Cyan
