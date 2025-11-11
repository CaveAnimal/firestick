$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$tasks = Join-Path (Split-Path -Parent $scriptDir) "tasksDEV3.md"
$update = Join-Path (Split-Path -Parent (Split-Path -Parent $scriptDir)) "plans" "scripts" "update-task-summary.ps1"

& $update -TasksFilePath $tasks -SummaryHeader '## Task Summary (DEV3)'
