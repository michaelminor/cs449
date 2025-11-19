package SOS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Computer player implementation - uses LLM for decision making with fallback to random moves.
 */
public class ComputerPlayer implements Player {
    private final LLMService llmService;
    private final Random random;
    private char selectedLetter = 'S'; // Default

    public ComputerPlayer() {
        this.llmService = new LLMService();
        this.random = new Random();
    }

    @Override
    public PlayerType getType() {
        return PlayerType.COMPUTER;
    }

    @Override
    public Move getMove(GameLogic gameLogic, boolean isBluePlayer) {
        // First try LLM
        Move llmMove = llmService.suggestMove(gameLogic, isBluePlayer);

        // Validate LLM move
        if (llmMove != null && gameLogic.isCellEmpty(llmMove.row, llmMove.col)) {
            selectedLetter = llmMove.letter;
            return llmMove;
        }

        // Fall back to random valid move
        System.out.println("LLM failed or returned invalid move, using random move");
        return getRandomMove(gameLogic);
    }

    private Move getRandomMove(GameLogic gameLogic) {
        List<int[]> emptyCells = new ArrayList<>();
        int size = gameLogic.getBoardSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (gameLogic.isCellEmpty(r, c)) {
                    emptyCells.add(new int[]{r, c});
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return null;
        }

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        char letter = random.nextBoolean() ? 'S' : 'O';
        selectedLetter = letter;

        return new Move(cell[0], cell[1], letter);
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
