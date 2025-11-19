package SOS;

/**
 * Human player implementation - moves are made through UI interaction.
 */
public class HumanPlayer implements Player {
    private char selectedLetter = 'S';

    @Override
    public PlayerType getType() {
        return PlayerType.HUMAN;
    }

    @Override
    public Move getMove(GameLogic gameLogic, boolean isBluePlayer) {
        // Human players don't auto-generate moves
        // Moves come from UI clicks
        return null;
    }

    @Override
    public void setSelectedLetter(char letter) {
        this.selectedLetter = letter;
    }

    @Override
    public char getSelectedLetter() {
        return selectedLetter;
    }
}
