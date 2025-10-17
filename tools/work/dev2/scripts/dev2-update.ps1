$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$toolsDir = Split-Path -Parent (Split-Path -Parent $scriptDir)
$tasks = Join-Path $toolsDir 'work\dev2\tasksDEV2.md'
$update = Join-Path $toolsDir 'plans\scripts\update-task-summary.ps1'
& $update -TasksFilePath $tasks -SummaryHeader '## Task Summary (DEV2)'

# Also update the master task summary in one step
$masterTasks = Join-Path $toolsDir 'plans\firestickTASKS.md'
& $update -TasksFilePath $masterTasks -SummaryHeader '## Task Summary'
