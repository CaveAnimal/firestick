Start-up script for local development

This repository includes a PowerShell script to start the major services used during development:

- Java backend (Spring Boot)
- Chroma vector database
- Python LLM microservice
- React UI (Vite)

Usage:

1. From a PowerShell prompt open repository root:

   cd E:\MyProjects\MyGitHubCopilot\firestick\fstk-001

2. Run the script (it will open new PowerShell windows and leave them running):

   .\start-all.ps1

3. Options are available to skip starting some services (useful if you already run them):

   .\start-all.ps1 -SkipChroma -SkipLLM

What the script does:

- Starts Chroma via `start-chroma.ps1` (bundles activation of the `.venv` and runs `chroma run`).
- Starts the LLM microservice in the Python `.venv` then runs `llm_service_gguf.py`.
- Starts the backend using `mvn -DskipTests spring-boot:run` (or falls back to java -jar). If you prefer the jar approach, build the backend first.
- Starts UI in `ui` with `npm run dev`.
- Polls health endpoints for each service to verify they started within the default timeout.

Health endpoints used by the script:

- Backend: http://127.0.0.1:8080/api/health
   - To run Spring Boot with the ONNX profile (loads ONNX models and config), pass the flag to the script or Maven/JAR:
   - `.\start-all.ps1 -WithOnnx` — runs Maven as `mvn -DskipTests spring-boot:run -Dspring-boot.run.profiles=onnx` or sets `SPRING_PROFILES_ACTIVE=onnx` for the jar fallback, which uses `application-onnx.properties` and the ONNX port 8081. When `-WithOnnx` is used the script will additionally call `/api/embedding/info` after health checks to verify the embedding mode is `ONNX` and will flag a problem if it isn't.
   - Backend (default): http://127.0.0.1:8080/api/health
   - Backend (ONNX profile): http://127.0.0.1:8081/api/health
- LLM: http://127.0.0.1:8001/health
- Chroma: TCP port 8000 connectivity is checked
- UI: http://127.0.0.1:5173 (Vite default)

If the script cannot find `pwsh` (PowerShell 7), it uses the Windows PowerShell fallback.

Verification after starting with ONNX profile
--------------------------------------------

If you start the services with `-WithOnnx`, the backend should be running on port 8081 and the ONNX embedding mode will be active. You can verify it with the following PowerShell commands:

- Check the health endpoint:

   Invoke-WebRequest -Uri 'http://127.0.0.1:8081/api/health' -UseBasicParsing

- Confirm the embedding mode and active profiles:

   # Option 1 - using Invoke-RestMethod (returns deserialized JSON)
   Invoke-RestMethod -Uri 'http://127.0.0.1:8081/api/embedding/info' -UseBasicParsing

   # Option 2 - using curl.exe and ConvertFrom-Json
   curl.exe http://127.0.0.1:8081/api/embedding/info | ConvertFrom-Json

You should see `mode: "ONNX"` and `activeProfiles` should include `onnx`.

Notes:

- The script uses `Start-Process` to open services in separate windows so logs are visible. Remove `-NoExit` in start-all if you want the windows to close automatically.
- The script tries to create a `.venv` if it does not exist; it does not install heavy dependencies automatically.
- Use `stop-all.ps1` to shut down dev processes started by start-all. This script asks for confirmation and stops processes commonly used during development: `mvn`, `java`, `node`, `python`. If you prefer non-interactive use, pass `-Force`.
- If you're using a different port for Vite, or you want a different health URL for backend/LLM, update the `start-all.ps1` script accordingly.

If you want I can extend this to start the services in background jobs (and capture their output to log files) rather than new windows.

Quick verification
------------------
After running `start-all.ps1 -WithOnnx` you can check everything with these commands:

```powershell
# Backend on ONNX profile
Invoke-WebRequest -Uri 'http://127.0.0.1:8081/api/health' -UseBasicParsing
Invoke-RestMethod -Uri 'http://127.0.0.1:8081/api/embedding/info' -UseBasicParsing

# LLM
Invoke-WebRequest -Uri 'http://127.0.0.1:8001/health' -UseBasicParsing

# Chroma (TCP)
Test-NetConnection -ComputerName '127.0.0.1' -Port 8000

# UI
Invoke-WebRequest -Uri 'http://127.0.0.1:5173' -UseBasicParsing

# When done, stop
.\stop-all.ps1
```