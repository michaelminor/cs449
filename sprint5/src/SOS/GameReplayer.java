package SOS;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and replaying recorded games.
 */
public class GameReplayer {
    private List<GameRecorder.MoveRecord> moves;
    private String gameMode;
    private int boardSize;
    private String bluePlayerType;
    private String redPlayerType;
    private int currentMoveIndex;

    public GameReplayer() {
        this.moves = new ArrayList<>();
        this.currentMoveIndex = 0;
    }

    /**
     * Load a game from a file.
     * @return true if loaded successfully, false otherwise
     */
    public boolean loadFromFile(String filename) {
        moves.clear();
        currentMoveIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingMoves = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("---MOVES---")) {
                    readingMoves = true;
                    continue;
                }

                if (!readingMoves) {
                    // Parse header
                    if (line.startsWith("Game Mode:")) {
                        gameMode = line.substring("Game Mode:".length()).trim();
                    } else if (line.startsWith("Board Size:")) {
                        boardSize = Integer.parseInt(line.substring("Board Size:".length()).trim());
                    } else if (line.startsWith("Blue Player:")) {
                        bluePlayerType = line.substring("Blue Player:".length()).trim();
                    } else if (line.startsWith("Red Player:")) {
                        redPlayerType = line.substring("Red Player:".length()).trim();
                    }
                } else {
                    // Parse moves
                    if (!line.isEmpty()) {
                        try {
                            GameRecorder.MoveRecord move = GameRecorder.MoveRecord.fromString(line);
                            moves.add(move);
                        } catch (Exception e) {
                            System.err.println("Failed to parse move: " + line);
                        }
                    }
                }
            }

            System.out.println("Loaded game: " + gameMode + ", " + boardSize + "x" + boardSize +
                             ", " + moves.size() + " moves");
            return true;

        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the next move in the replay sequence.
     * @return next move, or null if replay is complete
     */
    public GameRecorder.MoveRecord getNextMove() {
        if (currentMoveIndex >= moves.size()) {
            return null;
        }
        return moves.get(currentMoveIndex++);
    }

    /**
     * Check if there are more moves to replay.
     */
    public boolean hasMoreMoves() {
        return currentMoveIndex < moves.size();
    }

    /**
     * Reset replay to the beginning.
     */
    public void reset() {
        currentMoveIndex = 0;
    }

    /**
     * Get total number of moves.
     */
    public int getTotalMoves() {
        return moves.size();
    }

    /**
     * Get current move index (for progress tracking).
     */
    public int getCurrentMoveIndex() {
        return currentMoveIndex;
    }

    // Getters
    public String getGameMode() {
        return gameMode;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public String getBluePlayerType() {
        return bluePlayerType;
    }

    public String getRedPlayerType() {
        return redPlayerType;
    }
}
