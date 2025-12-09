param(
    [switch]$OpenInVSCode
)

$ErrorActionPreference = 'Stop'

# Locate JSON bookmark next to this script
$dev2Dir = Resolve-Path (Join-Path $PSScriptRoot '..')
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\\..\\..\\..')
$bookmarkJsonPath = Join-Path $dev2Dir 'tasksDEV2_bookmark.json'
if (-not (Test-Path $bookmarkJsonPath)) {
    Write-Error "Bookmark JSON not found: $bookmarkJsonPath"
}

# Load bookmark
$bookmark = Get-Content $bookmarkJsonPath -Raw | ConvertFrom-Json
$tasksRelPath = $bookmark.source
$tasksPath = Resolve-Path (Join-Path $repoRoot $tasksRelPath)

if (-not (Test-Path $tasksPath)) {
    Write-Error "Tasks file not found: $tasksPath"
}

# Resolve markers using patterns first (robust to line shifts)
$lines = Get-Content $tasksPath

function Find-LineNumberByPattern([string]$pattern) {
    $match = Select-String -Path $tasksPath -Pattern $pattern -CaseSensitive:$false -SimpleMatch:$false | Select-Object -First 1
    if ($null -ne $match) { return $match.LineNumber }
    return $null
}

# Preferred target order: most recent sub-task -> CI ttask -> section heading
$openApiLine   = Find-LineNumberByPattern ($bookmark.markers | Where-Object { $_.id -eq 'openapi-swagger-ui-subtask' } | Select-Object -ExpandProperty pattern)
$ciArtifacts   = Find-LineNumberByPattern ($bookmark.markers | Where-Object { $_.id -eq 'ci-artifacts-ttask' } | Select-Object -ExpandProperty pattern)
$sectionAnchor = Find-LineNumberByPattern ($bookmark.markers | Where-Object { $_.id -eq 'recommendations-backlog-heading' } | Select-Object -ExpandProperty pattern)
# Fallback to HTML anchor comment if heading text changes
if (-not $sectionAnchor) {
    $sectionAnchor = Find-LineNumberByPattern '^<!--\s*@anchor:dev2-recommendations-backlog\s*-->$'
}

$target = $openApiLine
if (-not $target) { $target = $ciArtifacts }
if (-not $target) { $target = $sectionAnchor }
if (-not $target) { $target = $bookmark.resume.preferredStartLine }
if (-not $target) { $target = 1 }

$display = "${tasksRelPath}:$target"
Write-Host $display

if ($OpenInVSCode) {
    # Try launching VS Code at file:line if 'code' is on PATH
    $codeCmd = Get-Command code -ErrorAction SilentlyContinue
    if ($codeCmd) {
        & code -g "$tasksPath`:$target" | Out-Null
    } else {
        Write-Host "Tip: Install VS Code CLI or add 'code' to PATH to open directly." -ForegroundColor Yellow
    }
}
