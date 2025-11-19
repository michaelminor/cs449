package SOS;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests for ComputerPlayer functionality.
 */
public class ComputerPlayerTest {

    private ComputerPlayer computerPlayer;
    private SimpleGameLogic simpleGame;
    private GeneralGameLogic generalGame;

    @Before
    public void setUp() {
        computerPlayer = new ComputerPlayer();
        simpleGame = new SimpleGameLogic(6, msg -> {});
        generalGame = new GeneralGameLogic(6, msg -> {});
    }

    @Test
    public void testComputerPlayerType() {
        assertEquals("Computer player should have COMPUTER type",
                     PlayerType.COMPUTER, computerPlayer.getType());
    }

    @Test
    public void testSetAndGetSelectedLetter() {
        computerPlayer.setSelectedLetter('S');
        assertEquals("Selected letter should be 'S'", 'S', computerPlayer.getSelectedLetter());

        computerPlayer.setSelectedLetter('O');
        assertEquals("Selected letter should be 'O'", 'O', computerPlayer.getSelectedLetter());
    }

    @Test
    public void testGetMoveReturnsValidMove() {
        Move move = computerPlayer.getMove(simpleGame, true);

        assertNotNull("Computer player should return a move", move);
        assertTrue("Row should be within board bounds",
                   move.row >= 0 && move.row < simpleGame.getBoardSize());
        assertTrue("Column should be within board bounds",
                   move.col >= 0 && move.col < simpleGame.getBoardSize());
        assertTrue("Letter should be 'S' or 'O'",
                   move.letter == 'S' || move.letter == 'O');
    }

    @Test
    public void testGetMoveSelectsEmptyCell() {
        // Place some moves
        simpleGame.onMove(0, 0, 'S', true);
        simpleGame.onMove(1, 1, 'O', false);
        simpleGame.onMove(2, 2, 'S', true);

        Move move = computerPlayer.getMove(simpleGame, false);

        assertNotNull("Computer should return a move", move);
        assertTrue("Computer should select an empty cell",
                   simpleGame.isCellEmpty(move.row, move.col));
    }

    @Test
    public void testMultipleMovesAreDifferent() {
        // Get multiple moves and ensure they're selecting empty cells
        Move move1 = computerPlayer.getMove(simpleGame, true);
        assertNotNull(move1);
        simpleGame.onMove(move1.row, move1.col, move1.letter, true);

        Move move2 = computerPlayer.getMove(simpleGame, false);
        assertNotNull(move2);

        // Moves should be different (not the same cell)
        assertFalse("Second move should be to a different cell than first",
                    move1.row == move2.row && move1.col == move2.col);
    }

    @Test
    public void testComputerPlayerWithGeneralGame() {
        Move move = computerPlayer.getMove(generalGame, true);

        assertNotNull("Computer should work with general game mode", move);
        assertTrue("Row should be valid",
                   move.row >= 0 && move.row < generalGame.getBoardSize());
        assertTrue("Column should be valid",
                   move.col >= 0 && move.col < generalGame.getBoardSize());
    }

    @Test
    public void testComputerPlayerOnNearlyFullBoard() {
        // Fill most of the board
        int size = simpleGame.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (r == size - 1 && c == size - 1) {
                    // Leave one cell empty
                    continue;
                }
                simpleGame.onMove(r, c, (r + c) % 2 == 0 ? 'S' : 'O', r % 2 == 0);
                if (simpleGame.isGameOver()) {
                    return; // Game ended early in simple mode
                }
            }
        }

        if (!simpleGame.isGameOver()) {
            Move move = computerPlayer.getMove(simpleGame, true);
            assertNotNull("Computer should find the last empty cell", move);
            assertEquals("Should select last row", size - 1, move.row);
            assertEquals("Should select last column", size - 1, move.col);
        }
    }

    @Test
    public void testComputerPlayerWithDifferentLetters() {
        computerPlayer.setSelectedLetter('S');
        Move moveS = computerPlayer.getMove(simpleGame, true);
        assertNotNull("Should get move when letter is S", moveS);

        // Start fresh game
        simpleGame = new SimpleGameLogic(6, msg -> {});
        computerPlayer.setSelectedLetter('O');
        Move moveO = computerPlayer.getMove(simpleGame, false);
        assertNotNull("Should get move when letter is O", moveO);
    }

    @Test
    public void testComputerPlayerRespectsGameLogic() {
        // Make a move that would end the game in simple mode
        simpleGame.onMove(0, 0, 'S', true);
        simpleGame.onMove(0, 1, 'O', false);
        simpleGame.onMove(0, 2, 'S', true);  // This completes SOS, game over

        assertTrue("Game should be over", simpleGame.isGameOver());

        // Computer should handle game over state gracefully
        Move move = computerPlayer.getMove(simpleGame, true);
        // Move might be null or valid - either is acceptable for ended game
        if (move != null) {
            assertTrue("If move returned, should be valid position",
                       move.row >= 0 && move.col >= 0);
        }
    }
}
