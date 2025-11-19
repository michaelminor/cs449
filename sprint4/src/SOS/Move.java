package SOS;

/**
 * Represents a move in the SOS game.
 */
public class Move {
    public final int row;
    public final int col;
    public final char letter; // 'S' or 'O'

    public Move(int row, int col, char letter) {
        this.row = row;
        this.col = col;
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "Move{row=" + row + ", col=" + col + ", letter=" + letter + "}";
    }
}
