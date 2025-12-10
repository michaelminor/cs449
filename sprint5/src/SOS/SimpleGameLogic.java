package SOS;

import java.awt.Color;
import java.util.function.Consumer;

/**
 * Simple game mode logic: first player to form an SOS wins immediately.
 */
public class SimpleGameLogic extends GameLogicBase {

    public SimpleGameLogic(int size, Consumer<String> announcer) {
        super(size, announcer);
    }

    @Override
    public MoveResult onMove(int r, int c, char letter, boolean blueTurn) {
        if (gameOver) return MoveResult.ongoing();
        place(r, c, letter);

        Color playerColor = blueTurn ? Color.BLUE : Color.RED;
        int gained = countNewSOS(r, c, letter, playerColor);
        if (gained > 0) {
            if (blueTurn) blueScore += gained;
            else redScore += gained;

            gameOver = true;
            String winner = blueTurn ? "Blue Player" : "Red Player";
            return MoveResult.endedWithWinner(winner);
        }

        // No score and still playing
        return MoveResult.ongoing();
    }
}
