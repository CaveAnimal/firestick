@echo off
REM Start LLM Service with Mistral Nemo (Transformers)
setlocal enabledelayedexpansion

cd /d "%~dp0"

REM Activate virtual environment
call .\.venv\Scripts\activate.bat

REM Set the model path to the local Mistral Nemo model
set MODEL_PATH=models/mistral/models/mistral-nemo

REM Start the service using the transformers-based script
python llm_service.py

pause
