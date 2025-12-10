#!/bin/bash
# SOS Game Launcher Script
# Make sure ANTHROPIC_API_KEY is set for computer players to work with LLM

echo "Starting SOS Game..."
echo ""
echo "Note: Set ANTHROPIC_API_KEY environment variable for LLM-powered computer players"
echo "Without it, computer players will use random moves"
echo ""

java -cp "bin:lib/json-20240303.jar" SOS.launcher_SOS
