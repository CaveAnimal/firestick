# check-task-status.ps1
# Quick status check of tasks without modifying the file

<#
.SYNOPSIS
    Displays current task statistics without modifying files

.DESCRIPTION
    Reads firestickTASKS.md and displays current statistics
    Useful for quick status checks without updating the file

.EXAMPLE
    .\check-task-status.ps1
    Displays current task statistics

.NOTES
    File Name      : check-task-status.ps1
    Author         : Firestick Development Team
    Prerequisite   : PowerShell 5.1 or higher
    Location       : /tools/plans/scripts/
#>

param(
    [string]$TasksFilePath
)

# Define file paths
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$defaultTasksFile = Join-Path (Split-Path -Parent $scriptDir) "firestickTASKS.md"
$tasksFile = if ([string]::IsNullOrWhiteSpace($TasksFilePath)) { $defaultTasksFile } else { $TasksFilePath }

# Verify the tasks file exists
if (-not (Test-Path $tasksFile)) {
    Write-Error "Tasks file not found at: $tasksFile"
    exit 1
}

# Read the file content
$content = Get-Content $tasksFile -Raw

# Count tasks using regex patterns
$allTasksPattern = '^\s*-\s*`?\[[^\]]*\]`?'
$allTasksMatches = [regex]::Matches($content, $allTasksPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$totalTasks = $allTasksMatches.Count

$completedPattern = '^\s*-\s*`?\[[VX]\]`?'
$completedMatches = [regex]::Matches($content, $completedPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$completedTasks = $completedMatches.Count

$inProgressPattern = '^\s*-\s*`?\[-\]`?'
$inProgressMatches = [regex]::Matches($content, $inProgressPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$inProgressTasks = $inProgressMatches.Count

$blockedPattern = '^\s*-\s*`?\[!\]`?'
$blockedMatches = [regex]::Matches($content, $blockedPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$blockedTasks = $blockedMatches.Count

$deferredPattern = '^\s*-\s*`?\[>\]`?'
$deferredMatches = [regex]::Matches($content, $deferredPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$deferredTasks = $deferredMatches.Count

$notStarted = $totalTasks - $completedTasks - $inProgressTasks - $blockedTasks - $deferredTasks

# Calculate percentage
if ($totalTasks -gt 0) {
    $percentComplete = [math]::Round(($completedTasks / $totalTasks) * 100, 2)
    $percentProgress = [math]::Round((($completedTasks + $inProgressTasks) / $totalTasks) * 100, 2)
} else {
    $percentComplete = 0.00
    $percentProgress = 0.00
}

# Display statistics
Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║          FIRESTICK TASK STATUS SUMMARY                   ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Write-Host "Total Tasks:          " -NoNewline -ForegroundColor Gray
Write-Host "$($totalTasks.ToString('N0'))" -ForegroundColor White

Write-Host ""
Write-Host "✓ Completed/Tested:   " -NoNewline -ForegroundColor Green
Write-Host "$completedTasks tasks ($percentComplete%)" -ForegroundColor Green

Write-Host "⚙ In Progress:        " -NoNewline -ForegroundColor Yellow
Write-Host "$inProgressTasks tasks" -ForegroundColor Yellow

Write-Host "⊗ Blocked:            " -NoNewline -ForegroundColor Red
Write-Host "$blockedTasks tasks" -ForegroundColor Red

Write-Host "▶ Deferred:           " -NoNewline -ForegroundColor Magenta
Write-Host "$deferredTasks tasks" -ForegroundColor Magenta

Write-Host "○ Not Started:        " -NoNewline -ForegroundColor DarkGray
Write-Host "$notStarted tasks" -ForegroundColor DarkGray

Write-Host ""
Write-Host "Overall Progress:     " -NoNewline -ForegroundColor Cyan
Write-Host "$percentProgress% " -NoNewline -ForegroundColor Cyan
Write-Host "(including in-progress)" -ForegroundColor DarkGray

# Progress bar
$barWidth = 50
$completedBars = [math]::Floor($barWidth * $percentComplete / 100)
$progressBars = [math]::Floor($barWidth * $percentProgress / 100) - $completedBars
$emptyBars = $barWidth - $completedBars - $progressBars

Write-Host ""
Write-Host "Progress Bar: [" -NoNewline
Write-Host ("█" * $completedBars) -NoNewline -ForegroundColor Green
Write-Host ("▒" * $progressBars) -NoNewline -ForegroundColor Yellow
Write-Host ("░" * $emptyBars) -NoNewline -ForegroundColor DarkGray
Write-Host "]"

Write-Host ""
Write-Host "File: $tasksFile" -ForegroundColor DarkGray
Write-Host ""
