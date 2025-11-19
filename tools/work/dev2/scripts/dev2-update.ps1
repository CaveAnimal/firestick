# dev2-update.ps1
# Updates the Dev2 task summary and the master task summary.

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
# FIX: Correct directory traversal to reach /tools and proper file targets.
$dev2Dir   = Split-Path -Parent $scriptDir          # .../tools/work/dev2
$workDir   = Split-Path -Parent $dev2Dir            # .../tools/work
$toolsDir  = Split-Path -Parent $workDir            # .../tools

# Paths (previous logic incorrectly doubled 'work' and misplaced 'plans')
$tasks       = Join-Path $dev2Dir 'tasksDEV2.md'
$update      = Join-Path $toolsDir 'plans\scripts\update-task-summary.ps1'
$masterTasks = Join-Path $toolsDir 'plans\firestickTASKS.md'

if (-not (Test-Path $tasks)) { Write-Error "Dev2 tasks file not found: $tasks"; exit 1 }
if (-not (Test-Path $update)) { Write-Error "Update helper script not found: $update"; exit 1 }
if (-not (Test-Path $masterTasks)) { Write-Error "Master tasks file not found: $masterTasks"; exit 1 }

Write-Host "Running task summary update for Dev2..." -ForegroundColor Cyan
& $update -TasksFilePath $tasks -SummaryHeader '## Task Summary (DEV2)'

Write-Host "Running master task summary update..." -ForegroundColor Cyan
& $update -TasksFilePath $masterTasks -SummaryHeader '## Task Summary'

Write-Host "Dev2 and master task summaries updated." -ForegroundColor Green
