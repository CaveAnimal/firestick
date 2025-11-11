param(
    [string]$TasksFilePath,
    [string]$SummaryHeader = '## Task Summary'
)

# update-task-summary.ps1
# Updates the task summary in firestickTASKS.md with current completion statistics

<#
.SYNOPSIS
    Updates task completion statistics in firestickTASKS.md

.DESCRIPTION
    Counts total tasks, completed tasks, calculates percentage, and updates
    the Task Summary section with current statistics and timestamp.

.EXAMPLE
    .\update-task-summary.ps1
    Updates the task summary with current statistics

.NOTES
    File Name      : update-task-summary.ps1
    Author         : Firestick Development Team
    Prerequisite   : PowerShell 5.1 or higher
    Location       : /tools/plans/scripts/
#>

# Set strict mode for better error handling
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Define file paths
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$defaultTasksFile = Join-Path (Split-Path -Parent $scriptDir) "firestickTASKS.md"
$tasksFile = if ([string]::IsNullOrWhiteSpace($TasksFilePath)) { $defaultTasksFile } else { $TasksFilePath }

# Verify the tasks file exists
if (-not (Test-Path $tasksFile)) {
    Write-Error "Tasks file not found at: $tasksFile"
    exit 1
}

Write-Host ""
Write-Host "=== Task Summary Update Script ===" -ForegroundColor Cyan
Write-Host "Processing: $tasksFile" -ForegroundColor Gray

# Read the file content
$content = Get-Content $tasksFile -Raw

# Pre-process: remove any explicitly skipped regions so they do not affect counts.
# Usage in markdown:
# <!-- @SKIP-COUNT:START optional-label -->
#    - `[ ]` Some deferred task
# <!-- @SKIP-COUNT:END optional-label -->
# All task list items inside these markers are ignored for percent calculations.
$skipPattern = '(?ms)<!--\s*@SKIP-COUNT:START[^>]*-->.*?<!--\s*@SKIP-COUNT:END[^>]*-->'
$processedContent = [regex]::Replace($content, $skipPattern, '')

# Count tasks using regex patterns
Write-Host ""
Write-Host "Counting tasks..." -ForegroundColor Yellow

<#
 We purposely exclude deferred/backlog markers `[>]` from the total task count so that
 deferring large sections does not dilute current phase percent complete.
 Negative lookahead (?!>) accomplishes this.
#>
${allTasksPattern} = '^\s*-\s*`?\[(?!>)[^\]]*\]`?'
$allTasksMatches = [regex]::Matches($processedContent, ${allTasksPattern}, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$totalTasks = $allTasksMatches.Count

# Count completed and tested tasks ([X] or [V]) with or without backticks
$completedPattern = '^\s*-\s*`?\[[VX]\]`?'
$completedMatches = [regex]::Matches($processedContent, $completedPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$completedTasks = $completedMatches.Count

# Count in-progress tasks ([-]) with or without backticks
$inProgressPattern = '^\s*-\s*`?\[-\]`?'
$inProgressMatches = [regex]::Matches($processedContent, $inProgressPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$inProgressTasks = $inProgressMatches.Count

# Count blocked tasks ([!]) with or without backticks
$blockedPattern = '^\s*-\s*`?\[!\]`?'
$blockedMatches = [regex]::Matches($processedContent, $blockedPattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)
$blockedTasks = $blockedMatches.Count

# Calculate percentage
if ($totalTasks -gt 0) {
    $percentComplete = [math]::Round(($completedTasks / $totalTasks) * 100, 2)
} else {
    $percentComplete = 0.00
}

# Get current timestamp in Central Standard Time
$cstTimeZone = [System.TimeZoneInfo]::FindSystemTimeZoneById('Central Standard Time')
$cstTime = [System.TimeZoneInfo]::ConvertTimeFromUtc((Get-Date).ToUniversalTime(), $cstTimeZone)
$timestamp = $cstTime.ToString('MMMM d, yyyy    h:mm tt') + ' Central Standard Time'

# Display statistics
Write-Host ""
Write-Host "Task Statistics:" -ForegroundColor Green
Write-Host "  Total Tasks:        $totalTasks" -ForegroundColor White
Write-Host "  Completed/Tested:   $completedTasks ([V] or [X])" -ForegroundColor Green
Write-Host "  In Progress:        $inProgressTasks ([-])" -ForegroundColor Yellow
Write-Host "  Blocked:            $blockedTasks ([!])" -ForegroundColor Red
Write-Host "  Not Started:        $($totalTasks - $completedTasks - $inProgressTasks - $blockedTasks) ([ ])" -ForegroundColor Gray
Write-Host "  Percent Complete:   $percentComplete%" -ForegroundColor Cyan
Write-Host "  Timestamp:          $timestamp" -ForegroundColor Gray

# Create the new summary section
$newSummary = @"
$SummaryHeader

**Total Tasks:** $($totalTasks.ToString('N0')) tasks (including main tasks and sub-tasks)  
**Completed/Tested:** $completedTasks tasks  
**In Progress:** $inProgressTasks tasks  
**Blocked:** $blockedTasks tasks  
**Percent Complete:** $percentComplete%  
**Last Updated:** $timestamp
"@

# Find and replace the Task Summary section
$escapedHeader = [regex]::Escape($SummaryHeader)
$summaryPattern = "(?ms)^$escapedHeader.*?^(?=##|\z)"

if ($content -match $summaryPattern) {
    Write-Host ""
    Write-Host "Updating Task Summary section..." -ForegroundColor Yellow
    
    # Replace the summary section
    $updatedContent = $content -replace $summaryPattern, ($newSummary + "`n`n")
    
    # Write back to file
    Set-Content -Path $tasksFile -Value $updatedContent -NoNewline
    
    Write-Host "Task summary updated successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "File updated: $tasksFile" -ForegroundColor Gray
} else {
    Write-Warning "Could not find Task Summary section in the file."
    Write-Host "Expected to find a section starting with '## Task Summary'" -ForegroundColor Yellow
    exit 1
}

# Create a backup with timestamp
$backupDir = Join-Path (Split-Path -Parent $scriptDir) "backups"
if (-not (Test-Path $backupDir)) {
    New-Item -Path $backupDir -ItemType Directory -Force | Out-Null
}
$backupFile = Join-Path $backupDir "firestickTASKS_$($cstTime.ToString('yyyyMMdd_HHmmss')).md"
Copy-Item -Path $tasksFile -Destination $backupFile -Force
Write-Host ""
Write-Host "Backup created: $backupFile" -ForegroundColor Gray

Write-Host ""
Write-Host "=== Update Complete ===" -ForegroundColor Cyan
Write-Host ""
