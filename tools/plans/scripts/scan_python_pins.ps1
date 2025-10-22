param(
  [string]$Root = '.',
  [string]$OutPath = '',
  [string[]]$Exts = @('ps1','psm1','bat','cmd','sh','py','yml','yaml','toml','cfg','ini','md','txt'),
  [string[]]$ExcludeDirs = @('.venv','target','.git','.idea','.mvn','node_modules','build','dist','out','.gradle')
)

$ErrorActionPreference = 'Stop'

# Patterns to flag (simple string matches to avoid regex/escape issues)
$patterns = @(
  'py -3.14',
  'python3.14',
  'python-version: 3.14',
  "python-version: '3.14'",
  'python-version: "3.14"',
  'pip install ',          # prefer python -m pip
  'python -m pip',         # still list it so we can see current usage
  'distutils',             # deprecated
  ' imp '                  # suspicious: legacy module name
)

# Collect candidate files
$files = Get-ChildItem -Path $Root -Recurse -File -ErrorAction SilentlyContinue |
  Where-Object {
    $parts = $_.FullName -split '[\\/]'
    -not ($ExcludeDirs | Where-Object { $parts -contains $_ })
  } |
  Where-Object { $Exts -contains $_.Extension.TrimStart('.') -or $_.Name -like 'Dockerfile*' }

if (-not $files) {
  Write-Host "No candidate files found under $Root"
  if ($OutPath) { "No candidate files found under $Root" | Set-Content -Path $OutPath -Encoding UTF8 }
  exit 0
}

# Search files for patterns (simple match)
$hits = Select-String -Path ($files | Select-Object -ExpandProperty FullName) -Pattern $patterns -AllMatches -SimpleMatch -ErrorAction SilentlyContinue

if ($hits) {
  $lines = $hits |
    Sort-Object Path, LineNumber |
    ForEach-Object { '{0}:{1}: {2}' -f $_.Path, $_.LineNumber, $_.Line.Trim() }
  $lines | Out-Host
  if ($OutPath) { $lines | Set-Content -Path $OutPath -Encoding UTF8 }
} else {
  Write-Host 'No matches found.'
  if ($OutPath) { 'No matches found.' | Set-Content -Path $OutPath -Encoding UTF8 }
}

exit 0
