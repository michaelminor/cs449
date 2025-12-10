package SOS;

/**
 * Represents the result of a move in the SOS game.
 */
public class MoveResult {
    public final boolean gameEnded;
    public final boolean draw;
    public final String winner; // non-null only if !draw && gameEnded

    private MoveResult(boolean gameEnded, boolean draw, String winner) {
        this.gameEnded = gameEnded;
        this.draw = draw;
        this.winner = winner;
    }

    public static MoveResult ongoing() {
        return new MoveResult(false, false, null);
    }

    public static MoveResult endedWithWinner(String winner) {
        return new MoveResult(true, false, winner);
    }

    public static MoveResult endedDraw() {
        return new MoveResult(true, true, null);
    }
}
