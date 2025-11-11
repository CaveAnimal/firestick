$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$devDir = Split-Path -Parent $scriptDir              # tools\work\dev3
$workDir = Split-Path -Parent $devDir                # tools\work
$toolsRoot = Split-Path -Parent $workDir             # tools

$tasks = Join-Path $devDir 'tasksDEV3.md'
$master = Join-Path $toolsRoot 'plans\firestickTASKS.md'
$update = Join-Path $toolsRoot 'plans\scripts\update-task-summary.ps1'

& $update -TasksFilePath $tasks -SummaryHeader '## Task Summary (DEV3)'
# Also refresh master summary
& $update -TasksFilePath $master -SummaryHeader '## Task Summary'
