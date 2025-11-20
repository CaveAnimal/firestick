@echo off
REM Quick LLM Log Viewer Script
REM Shows the most recent LLM logs

setlocal enabledelayedexpansion
cd /d "E:\MyProjects\MyGitHubCopilot\firestick\fstk-001"

echo ======================================================================
echo LLM Service Log Viewer
echo ======================================================================
echo.
echo Looking for logs in: logs\LLMlogs\
echo.

REM Find the most recent log files for today
for /f "delims=" %%f in ('powershell -NoProfile -Command "Get-ChildItem 'logs/LLMlogs' -Filter 'llm_*' | Sort-Object LastWriteTime -Descending | Select-Object -First 1 | ForEach-Object {$_.Name}"') do set LATEST=%%f

if "%LATEST%"=="" (
    echo No LLM logs found yet.
    echo.
    echo Start the LLM service first:
    echo   python llm_service_gguf.py
    echo.
    pause
    exit /b 1
)

echo Found most recent logs:
echo %LATEST%
echo.

echo ======================================================================
echo REQUESTS LOG (Recent Activity)
echo ======================================================================
powershell -NoProfile -Command "Get-Content 'logs/LLMlogs/llm_requests_*.log' -Tail 30"
echo.
echo ======================================================================
echo To watch logs in real-time:
echo   Get-Content logs/LLMlogs/llm_requests_*.log -Wait
echo.
echo To view specific log:
echo   Get-Content logs/LLMlogs/llm_service_2025-11-14.log
echo   Get-Content logs/LLMlogs/llm_requests_2025-11-14.log
echo ======================================================================
pause
