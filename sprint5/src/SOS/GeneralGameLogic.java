package SOS;

import java.awt.Color;
import java.util.function.Consumer;

/**
 * General game mode logic: game continues until board is full, highest score wins.
 */
public class GeneralGameLogic extends GameLogicBase {

    public GeneralGameLogic(int size, Consumer<String> announcer) {
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
        }

        if (boardFull()) {
            gameOver = true;
            if (blueScore > redScore) return MoveResult.endedWithWinner("Blue Player");
            if (redScore > blueScore) return MoveResult.endedWithWinner("Red Player");
            return MoveResult.endedDraw();
        }

        return MoveResult.ongoing();
    }
}
