@echo off
REM Start LLM Service with proper venv activation
setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Activate virtual environment
call .\.venv\Scripts\activate.bat

REM Start the service
python llm_service_gguf.py

pause
