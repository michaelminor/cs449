# JUnit Tests for SOS Game - Computer Opponent

This directory contains JUnit 4 tests for the computer opponent functionality in the SOS game.

## Test Files

### ComputerPlayerTest.java
Tests for the `ComputerPlayer` class that implements computer-controlled players.

**Test Cases:**
- `testComputerPlayerType()` - Verifies player type is COMPUTER
- `testSetAndGetSelectedLetter()` - Tests letter selection (S/O)
- `testGetMoveReturnsValidMove()` - Ensures moves are within valid board bounds
- `testGetMoveSelectsEmptyCell()` - Verifies computer selects only empty cells
- `testMultipleMovesAreDifferent()` - Checks that consecutive moves are to different cells
- `testComputerPlayerWithGeneralGame()` - Tests compatibility with General game mode
- `testComputerPlayerOnNearlyFullBoard()` - Tests behavior when board is nearly full
- `testComputerPlayerWithDifferentLetters()` - Tests with different letter selections
- `testComputerPlayerRespectsGameLogic()` - Verifies proper handling of game-over state

### LLMServiceTest.java
Tests for the `LLMService` class that provides LLM-based move suggestions.

**Test Cases:**
- `testLLMServiceReturnsValidMoveOrNull()` - Validates move format when LLM returns a move
- `testLLMServiceSelectsEmptyCellWhenAvailable()` - Ensures empty cell selection
- `testLLMServiceWithScoringOpportunity()` - Tests behavior with scoring patterns available
- `testLLMServiceWorksWithDifferentBoardSizes()` - Tests 3x3 and 9x9 board compatibility
- `testLLMServiceConsistency()` - Verifies consistent valid move generation
- `testLLMServiceWithNearlyFullBoard()` - Tests with limited empty cells
- `testLLMServiceWithGeneralGame()` - Tests compatibility with General game mode
- `testLLMServiceRespondsToMethodCalls()` - Verifies LLMService can be called without exceptions

### AllComputerOpponentTests.java
Test suite that runs all computer opponent tests together.

## Running the Tests

### Using the batch script (Windows):
```batch
run-tests.bat
```

### Using command line:
```bash
cd C:\cs449\sprint3
java -cp "bin;lib/json-20240303.jar;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore SOS.AllComputerOpponentTests
```

### Running individual test classes:
```bash
# Run only ComputerPlayer tests
java -cp "bin;lib/json-20240303.jar;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore SOS.ComputerPlayerTest

# Run only LLMService tests
java -cp "bin;lib/json-20240303.jar;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore SOS.LLMServiceTest
```

## Dependencies

The tests require the following JAR files in the `lib/` directory:
- `junit-4.13.2.jar` - JUnit testing framework
- `hamcrest-core-1.3.jar` - Matcher library for JUnit
- `json-20240303.jar` - JSON library for LLM API communication

All dependencies are already included in the project.

## Notes

### LLM Availability
The LLMService tests are designed to work whether or not Ollama is running:
- If Ollama is available, tests verify LLM responses are valid
- If Ollama is unavailable, tests verify the service handles failures gracefully (returns null)
- The ComputerPlayer has fallback logic to use random moves when LLM returns null

### Test Philosophy
- **ComputerPlayer tests** focus on the player's behavior and move generation
- **LLMService tests** focus on LLM integration and response parsing
- Both test suites verify that the system degrades gracefully when LLM is unavailable

## Test Results

All 17 tests should pass:
- 9 ComputerPlayerTest cases
- 8 LLMServiceTest cases

Expected output:
```
OK (17 tests)
```
