package SOS;

import java.util.List;

/**
 * Interface for SOS game logic implementations.
 */
public interface GameLogic {
    /**
     * Process a move at the given position.
     * @param r row index
     * @param c column index
     * @param letter 'S' or 'O'
     * @param blueTurn true if blue player's turn, false if red player's turn
     * @return MoveResult indicating if game continues or ends
     */
    MoveResult onMove(int r, int c, char letter, boolean blueTurn);

    /**
     * @return blue player's current score
     */
    int getBlueScore();

    /**
     * @return red player's current score
     */
    int getRedScore();

    /**
     * @return true if game is over
     */
    boolean isGameOver();

    /**
     * @return board size
     */
    int getBoardSize();

    /**
     * @return current state of the board
     */
    char[][] getBoard();

    /**
     * Check if a cell is empty
     * @param r row index
     * @param c column index
     * @return true if cell is empty
     */
    boolean isCellEmpty(int r, int c);

    /**
     * @return list of all SOS lines formed during the game
     */
    List<SOSLine> getSOSLines();
}
