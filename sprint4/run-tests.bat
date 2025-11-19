@echo off
echo Running JUnit Tests for Computer Opponent...
echo.

java -cp "bin;lib/json-20240303.jar;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore SOS.AllComputerOpponentTests

echo.
echo Tests complete!
pause
