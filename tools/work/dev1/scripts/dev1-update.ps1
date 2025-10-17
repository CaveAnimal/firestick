$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$devDir = Split-Path -Parent $scriptDir              # tools\work\dev1
$workDir = Split-Path -Parent $devDir                # tools\work
$toolsRoot = Split-Path -Parent $workDir             # tools

$tasks = Join-Path $devDir 'tasksDEV1.md'
$update = Join-Path $toolsRoot 'plans\scripts\update-task-summary.ps1'
& $update -TasksFilePath $tasks -SummaryHeader '## Task Summary (DEV1)'

# Also update the master task summary in one step
$masterTasks = Join-Path $toolsRoot 'plans\firestickTASKS.md'
& $update -TasksFilePath $masterTasks -SummaryHeader '## Task Summary'
