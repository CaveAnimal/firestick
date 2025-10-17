$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$toolsDir = Split-Path -Parent (Split-Path -Parent $scriptDir)
$tasks = Join-Path $toolsDir 'work\dev1\tasksDEV1.md'
$check = Join-Path $toolsDir 'plans\scripts\check-task-status.ps1'
& $check -TasksFilePath $tasks
