package SOS;

/**
 * Interface for a player in the SOS game.
 */
public interface Player {
    /**
     * Get the type of player.
     * @return PlayerType enum value
     */
    PlayerType getType();

    /**
     * Request a move from this player.
     * For human players, this may return null and wait for UI interaction.
     * For computer players, this will calculate and return a move.
     *
     * @param gameLogic current game state
     * @param isBluePlayer true if this is the blue player
     * @return Move object or null if waiting for user input
     */
    Move getMove(GameLogic gameLogic, boolean isBluePlayer);

    /**
     * For human players: set the letter they want to play ('S' or 'O')
     * @param letter 'S' or 'O'
     */
    void setSelectedLetter(char letter);

    /**
     * Get the currently selected letter for human players.
     * @return 'S' or 'O'
     */
    char getSelectedLetter();
}
