#!/usr/bin/env pwsh
Set-StrictMode -Version Latest
param(
  [string]$HookPath = '.git/hooks/pre-commit'
)

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$preventScript = Join-Path $scriptRoot 'prevent_large_commit.sh'

if(-not (Test-Path $preventScript)){
  Write-Error "prevent_large_commit.sh not found under $scriptRoot"
  exit 2
}

Write-Host "Installing pre-commit hook to $HookPath"
Copy-Item -Path $preventScript -Destination $HookPath -Force
# Ensure executable bit for non-Windows
if($IsLinux -or $IsMacOS){
  & chmod +x $HookPath
}

Write-Host "Hook installed. Edit $HookPath to tune max size or wrap other checks as needed."
