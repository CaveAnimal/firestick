$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
# Navigate up to the top-level tools directory
$toolsDir = Split-Path -Parent (Split-Path -Parent (Split-Path -Parent $scriptDir))
$tasks = Join-Path $toolsDir 'work\dev2\tasksDEV2.md'
$check = Join-Path $toolsDir 'plans\scripts\check-task-status.ps1'
& $check -TasksFilePath $tasks
