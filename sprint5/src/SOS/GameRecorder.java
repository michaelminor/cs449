package SOS;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles recording game moves to a file for later replay.
 */
public class GameRecorder {
    private List<MoveRecord> moves;
    private String gameMode;
    private int boardSize;
    private String bluePlayerType;
    private String redPlayerType;
    private boolean recording;

    public GameRecorder() {
        this.moves = new ArrayList<>();
        this.recording = false;
    }

    /**
     * Start recording a new game.
     */
    public void startRecording(String gameMode, int boardSize, String bluePlayerType, String redPlayerType) {
        this.moves.clear();
        this.gameMode = gameMode;
        this.boardSize = boardSize;
        this.bluePlayerType = bluePlayerType;
        this.redPlayerType = redPlayerType;
        this.recording = true;
        System.out.println("Recording started: " + gameMode + " game, board size " + boardSize);
    }

    /**
     * Record a move.
     */
    public void recordMove(int row, int col, char letter, boolean isBlueTurn) {
        if (!recording) return;

        MoveRecord move = new MoveRecord(row, col, letter, isBlueTurn);
        moves.add(move);
        System.out.println("Move recorded: " + move);
    }

    /**
     * Stop recording and save to file.
     */
    public String stopRecording() {
        if (!recording) return null;

        recording = false;

        // Generate filename with timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = "SOS_Game_" + timestamp + ".txt";

        try {
            saveToFile(filename);
            System.out.println("Game saved to: " + filename);
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            return null;
        }
    }

    /**
     * Save the recorded game to a file.
     */
    private void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            writer.write("SOS Game Recording\n");
            writer.write("Game Mode: " + gameMode + "\n");
            writer.write("Board Size: " + boardSize + "\n");
            writer.write("Blue Player: " + bluePlayerType + "\n");
            writer.write("Red Player: " + redPlayerType + "\n");
            writer.write("Total Moves: " + moves.size() + "\n");
            writer.write("---MOVES---\n");

            // Write moves
            for (MoveRecord move : moves) {
                writer.write(move.toString() + "\n");
            }
        }
    }

    /**
     * Check if currently recording.
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Represents a single move in the game.
     */
    public static class MoveRecord {
        public final int row;
        public final int col;
        public final char letter;
        public final boolean isBlueTurn;

        public MoveRecord(int row, int col, char letter, boolean isBlueTurn) {
            this.row = row;
            this.col = col;
            this.letter = letter;
            this.isBlueTurn = isBlueTurn;
        }

        @Override
        public String toString() {
            String player = isBlueTurn ? "BLUE" : "RED";
            return row + "," + col + "," + letter + "," + player;
        }

        /**
         * Parse a move from a string.
         */
        public static MoveRecord fromString(String line) {
            String[] parts = line.split(",");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid move format: " + line);
            }

            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());
            char letter = parts[2].trim().charAt(0);
            boolean isBlueTurn = parts[3].trim().equals("BLUE");

            return new MoveRecord(row, col, letter, isBlueTurn);
        }
    }
}
