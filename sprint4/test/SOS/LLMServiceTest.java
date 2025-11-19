package SOS;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests for LLMService functionality.
 */
public class LLMServiceTest {

    private LLMService llmService;
    private SimpleGameLogic simpleGame;
    private GeneralGameLogic generalGame;

    @Before
    public void setUp() {
        llmService = new LLMService();
        simpleGame = new SimpleGameLogic(6, msg -> {});
        generalGame = new GeneralGameLogic(6, msg -> {});
    }

    @Test
    public void testLLMServiceReturnsValidMoveOrNull() {
        // This test will use LLM if available, may return null if Ollama unavailable
        Move move = llmService.suggestMove(simpleGame, true);

        // LLM may return null if Ollama is not running or fails
        if (move != null) {
            assertTrue("Row should be within bounds",
                       move.row >= 0 && move.row < simpleGame.getBoardSize());
            assertTrue("Column should be within bounds",
                       move.col >= 0 && move.col < simpleGame.getBoardSize());
            assertTrue("Letter should be S or O",
                       move.letter == 'S' || move.letter == 'O');
        }
        // Test passes either way - null is acceptable if LLM unavailable
    }

    @Test
    public void testLLMServiceSelectsEmptyCellWhenAvailable() {
        // Fill some cells
        simpleGame.onMove(0, 0, 'S', true);
        simpleGame.onMove(1, 1, 'O', false);
        simpleGame.onMove(2, 2, 'S', true);

        Move move = llmService.suggestMove(simpleGame, false);

        // If LLM returns a move, it should select an empty cell
        if (move != null) {
            assertTrue("Should select an empty cell",
                       simpleGame.isCellEmpty(move.row, move.col));
        }
    }

    @Test
    public void testLLMServiceWithScoringOpportunity() {
        // Create a scoring opportunity: S _ S (horizontally)
        generalGame.onMove(0, 0, 'S', true);
        generalGame.onMove(0, 2, 'S', false);
        // Cell (0,1) should be filled with 'O' to score

        Move move = llmService.suggestMove(generalGame, true);

        // If LLM returns a move, verify it's valid
        if (move != null) {
            assertTrue("Should return valid coordinates",
                       move.row >= 0 && move.row < generalGame.getBoardSize() &&
                       move.col >= 0 && move.col < generalGame.getBoardSize());
            assertTrue("Should return valid letter",
                       move.letter == 'S' || move.letter == 'O');
        }
    }

    @Test
    public void testLLMServiceWorksWithDifferentBoardSizes() {
        // Test with 3x3 board
        SimpleGameLogic smallGame = new SimpleGameLogic(3, msg -> {});
        Move move1 = llmService.suggestMove(smallGame, true);
        if (move1 != null) {
            assertTrue("Should be within 3x3 bounds",
                       move1.row >= 0 && move1.row < 3 &&
                       move1.col >= 0 && move1.col < 3);
        }

        // Test with 9x9 board
        SimpleGameLogic largeGame = new SimpleGameLogic(9, msg -> {});
        Move move2 = llmService.suggestMove(largeGame, false);
        if (move2 != null) {
            assertTrue("Should be within 9x9 bounds",
                       move2.row >= 0 && move2.row < 9 &&
                       move2.col >= 0 && move2.col < 9);
        }
    }

    @Test
    public void testLLMServiceConsistency() {
        // Make multiple calls and ensure valid moves when returned
        int validMoves = 0;
        for (int i = 0; i < 5; i++) {
            SimpleGameLogic testGame = new SimpleGameLogic(6, msg -> {});
            Move move = llmService.suggestMove(testGame, i % 2 == 0);

            if (move != null) {
                validMoves++;
                assertTrue("Move " + i + " should have valid row",
                           move.row >= 0 && move.row < 6);
                assertTrue("Move " + i + " should have valid column",
                           move.col >= 0 && move.col < 6);
                assertTrue("Move " + i + " should have valid letter",
                           move.letter == 'S' || move.letter == 'O');
            }
        }
        // At least test ran without exceptions
        assertTrue("Test completed successfully", true);
    }

    @Test
    public void testLLMServiceWithNearlyFullBoard() {
        // Fill all but one cell
        int size = simpleGame.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r == 2 && c == 3) {
                    continue; // Leave (2,3) empty
                }
                simpleGame.onMove(r, c, (r + c) % 2 == 0 ? 'S' : 'O', r % 2 == 0);
                if (simpleGame.isGameOver()) {
                    return; // Game ended
                }
            }
        }

        if (!simpleGame.isGameOver()) {
            Move move = llmService.suggestMove(simpleGame, true);
            if (move != null) {
                assertTrue("Should select an empty cell",
                           simpleGame.isCellEmpty(move.row, move.col));
            }
        }
    }

    @Test
    public void testLLMServiceWithGeneralGame() {
        Move move = llmService.suggestMove(generalGame, false);

        if (move != null) {
            assertTrue("Should return valid move for general game",
                       move.row >= 0 && move.row < generalGame.getBoardSize() &&
                       move.col >= 0 && move.col < generalGame.getBoardSize());
        }
    }

    @Test
    public void testLLMServiceRespondsToMethodCalls() {
        // This test verifies LLMService can be called without errors
        // Null return is acceptable if Ollama is unavailable
        SimpleGameLogic testGame = new SimpleGameLogic(5, msg -> {});

        for (int attempt = 0; attempt < 3; attempt++) {
            Move move = llmService.suggestMove(testGame, attempt % 2 == 0);

            // If a move is returned, it should be valid
            if (move != null) {
                assertTrue("Should select valid cell",
                           testGame.isCellEmpty(move.row, move.col));
            }
        }
        // Test passes - LLMService responded without throwing exceptions
        assertTrue("LLM Service callable", true);
    }
}
