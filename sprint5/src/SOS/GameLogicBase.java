package SOS;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for game logic implementations, containing shared logic.
 */
public abstract class GameLogicBase implements GameLogic {
    protected final int size;
    protected final char[][] grid;
    protected int filled = 0;

    protected int blueScore = 0;
    protected int redScore = 0;

    protected boolean gameOver = false;
    protected final Consumer<String> announcer;
    protected final List<SOSLine> sosLines = new ArrayList<>();

    protected GameLogicBase(int size, Consumer<String> announcer) {
        this.size = size;
        this.grid = new char[size][size];
        this.announcer = announcer;
    }

    @Override
    public int getBlueScore() {
        return blueScore;
    }

    @Override
    public int getRedScore() {
        return redScore;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public int getBoardSize() {
        return size;
    }

    @Override
    public char[][] getBoard() {
        // Return a copy to prevent external modification
        char[][] copy = new char[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    @Override
    public boolean isCellEmpty(int r, int c) {
        return inBounds(r, c) && grid[r][c] == '\0';
    }

    @Override
    public List<SOSLine> getSOSLines() {
        return new ArrayList<>(sosLines);
    }

    protected boolean inBounds(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    /**
     * Place letter and count all SOS created that include (r,c).
     * Counts across 8 directions. An SOS can be formed in three patterns relative to the placed tile:
     * - If placed letter == 'S': acts as start or end of "S O S"
     * - If placed letter == 'O': must have 'S' on both sides along a line
     * Also records the SOS lines for visualization.
     */
    protected int countNewSOS(int r, int c, char letter, Color playerColor) {
        int[][] dirs = {
            {-1, 0}, {1, 0},  // vertical
            {0, -1}, {0, 1},  // horizontal
            {-1, -1}, {1, 1}, // diag \
            {-1, 1}, {1, -1}  // diag /
        };

        int count = 0;
        if (letter == 'S') {
            // S as start: (S at 0) (O at +1) (S at +2)
            for (int[] d : dirs) {
                int r1 = r + d[0], c1 = c + d[1];
                int r2 = r + 2 * d[0], c2 = c + 2 * d[1];
                if (inBounds(r1, c1) && inBounds(r2, c2)) {
                    if (grid[r1][c1] == 'O' && grid[r2][c2] == 'S') {
                        count++;
                        sosLines.add(new SOSLine(r, c, r2, c2, playerColor));
                    }
                }
            }
            // S as end: (S at -2) (O at -1) (S at 0)
            for (int[] d : dirs) {
                int r_1 = r - d[0], c_1 = c - d[1];
                int r_2 = r - 2 * d[0], c_2 = c - 2 * d[1];
                if (inBounds(r_1, c_1) && inBounds(r_2, c_2)) {
                    if (grid[r_2][c_2] == 'S' && grid[r_1][c_1] == 'O') {
                        count++;
                        sosLines.add(new SOSLine(r_2, c_2, r, c, playerColor));
                    }
                }
            }
        } else if (letter == 'O') {
            // O in the middle: (S at -1) (O at 0) (S at +1)
            for (int[] d : dirs) {
                int r_1 = r - d[0], c_1 = c - d[1];
                int r1 = r + d[0], c1 = c + d[1];
                if (inBounds(r_1, c_1) && inBounds(r1, c1)) {
                    if (grid[r_1][c_1] == 'S' && grid[r1][c1] == 'S') {
                        count++;
                        sosLines.add(new SOSLine(r_1, c_1, r1, c1, playerColor));
                    }
                }
            }
        }
        return count;
    }

    protected void place(int r, int c, char letter) {
        grid[r][c] = letter;
        filled++;
    }

    protected boolean boardFull() {
        return filled >= size * size;
    }
}
