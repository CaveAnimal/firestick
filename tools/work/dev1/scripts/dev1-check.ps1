$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
# Resolve directories relative to this script location
$devDir = Split-Path -Parent $scriptDir              # tools\work\dev1
$workDir = Split-Path -Parent $devDir                # tools\work
$toolsRoot = Split-Path -Parent $workDir             # tools

# Paths
$tasks = Join-Path $devDir 'tasksDEV1.md'
$check = Join-Path $toolsRoot 'plans\scripts\check-task-status.ps1'

# Execute
& $check -TasksFilePath $tasks
