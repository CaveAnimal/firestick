@echo off
REM Install CodeLlama LLM service dependencies (Windows)

echo Installing LLM service dependencies...
pip install -r llm_requirements.txt

echo.
echo ✓ Dependencies installed successfully
echo.
echo To start the LLM service:
echo   python llm_service.py
echo.
echo The service will:
echo   1. Download CodeLlama 7B model (~13GB - first run only)
echo   2. Initialize the model
echo   3. Listen on http://127.0.0.1:8001
echo.
echo For GPU acceleration (optional, NVIDIA):
echo   pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
