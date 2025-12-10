@echo off
REM SOS Game Launcher Script
REM Uses Ollama (FREE local AI) for computer players

echo Starting SOS Game...
echo.
echo For AI-powered computer players, make sure Ollama is installed:
echo   1. Download from: https://ollama.com/download
echo   2. Run: ollama pull llama3.2
echo.
echo Without Ollama, computer players will use random moves (still playable!)
echo.

java -cp "bin;lib/json-20240303.jar" SOS.launcher_SOS
