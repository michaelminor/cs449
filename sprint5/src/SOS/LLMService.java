package SOS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service for communicating with an LLM API to get move suggestions.
 * This implementation uses Ollama (local LLM - FREE!).
 *
 * To use:
 * 1. Install Ollama from https://ollama.com/download
 * 2. Run: ollama pull llama3.2
 * 3. Start Ollama (it runs as a service)
 * 4. Play the game!
 */
public class LLMService {
    private static final String API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "llama3.2";
    private final boolean ollamaAvailable;

    public LLMService() {
        // Check if Ollama is available
        this.ollamaAvailable = checkOllamaAvailable();
        if (!ollamaAvailable) {
            System.err.println("WARNING: Ollama not available. Computer player will use random moves.");
            System.err.println("To enable AI moves:");
            System.err.println("  1. Install Ollama from https://ollama.com/download");
            System.err.println("  2. Run: ollama pull llama3.2");
            System.err.println("  3. Restart the game");
        } else {
            System.out.println("LLM Service initialized with Ollama (FREE local AI)");
        }
    }

    private boolean checkOllamaAvailable() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.disconnect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get a move suggestion from the LLM.
     * @param gameLogic current game state
     * @param isBluePlayer true if this is the blue player
     * @return suggested Move, or null if LLM fails
     */
    public Move suggestMove(GameLogic gameLogic, boolean isBluePlayer) {
        if (!ollamaAvailable) {
            return null; // Fall back to random if Ollama not available
        }

        try {
            String prompt = buildPrompt(gameLogic, isBluePlayer);
            String response = callLLM(prompt);
            Move move = parseMove(response, gameLogic.getBoardSize());

            // Validate that the suggested cell is actually empty
            if (move != null && !gameLogic.isCellEmpty(move.row, move.col)) {
                System.err.println("LLM suggested occupied cell (" + move.row + "," + move.col + "), using random");
                return null; // Fall back to random
            }

            return move;
        } catch (Exception e) {
            System.err.println("LLM error: " + e.getMessage());
            return null;
        }
    }

    private String buildPrompt(GameLogic gameLogic, boolean isBluePlayer) {
        StringBuilder sb = new StringBuilder();
        char[][] board = gameLogic.getBoard();
        int size = gameLogic.getBoardSize();

        // Collect ALL scoring opportunities FIRST
        StringBuilder scoringMoves = new StringBuilder();
        int scoringCount = 0;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == '\0') {
                    // Check if placing O here would score (S_S pattern)
                    if (checkSOS(board, r, c, 'O', size)) {
                        scoringMoves.append("MOVE: ").append(r).append(" ").append(c).append(" O\n");
                        scoringCount++;
                    }
                    // Check if placing S here would score
                    if (checkSOS(board, r, c, 'S', size)) {
                        scoringMoves.append("MOVE: ").append(r).append(" ").append(c).append(" S\n");
                        scoringCount++;
                    }
                }
            }
        }

        if (scoringCount > 0) {
            // SHORT, DIRECT PROMPT FOR SCORING
            sb.append("Pick ONE move. Copy it exactly:\n\n");
            sb.append(scoringMoves.toString());
        } else {
            // No scoring - minimal prompt
            sb.append("Board:\n");
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    char cell = board[r][c];
                    sb.append(cell == '\0' ? '.' : cell).append(" ");
                }
                sb.append("\n");
            }
            sb.append("\nPlace S or O at empty cell (.).\n");
            sb.append("Reply format: row col S\n");
            sb.append("Example: 2 3 S");
        }

        return sb.toString();
    }

    /**
     * Check if placing a letter at position would complete an SOS pattern.
     */
    private boolean checkSOS(char[][] board, int r, int c, char letter, int size) {
        // 8 directions to check
        int[][] directions = {
            {-1, 0}, {1, 0},   // vertical
            {0, -1}, {0, 1},   // horizontal
            {-1, -1}, {1, 1},  // diagonal \
            {-1, 1}, {1, -1}   // diagonal /
        };

        if (letter == 'O') {
            // Check if O completes S-O-S (need S on both sides)
            for (int[] dir : directions) {
                int r1 = r - dir[0], c1 = c - dir[1];
                int r2 = r + dir[0], c2 = c + dir[1];

                if (r1 >= 0 && r1 < size && c1 >= 0 && c1 < size &&
                    r2 >= 0 && r2 < size && c2 >= 0 && c2 < size) {
                    if (board[r1][c1] == 'S' && board[r2][c2] == 'S') {
                        return true; // Found S-O-S
                    }
                }
            }
        } else if (letter == 'S') {
            // Check if S completes S-O-S (as start or end)
            for (int[] dir : directions) {
                // S as end: check if there's O-S in this direction
                int r1 = r - dir[0], c1 = c - dir[1];
                int r2 = r - 2 * dir[0], c2 = c - 2 * dir[1];

                if (r1 >= 0 && r1 < size && c1 >= 0 && c1 < size &&
                    r2 >= 0 && r2 < size && c2 >= 0 && c2 < size) {
                    if (board[r1][c1] == 'O' && board[r2][c2] == 'S') {
                        return true; // Found S-O-S
                    }
                }

                // S as start: check if there's S-O in this direction
                int r3 = r + dir[0], c3 = c + dir[1];
                int r4 = r + 2 * dir[0], c4 = c + 2 * dir[1];

                if (r3 >= 0 && r3 < size && c3 >= 0 && c3 < size &&
                    r4 >= 0 && r4 < size && c4 >= 0 && c4 < size) {
                    if (board[r3][c3] == 'O' && board[r4][c4] == 'S') {
                        return true; // Found S-O-S
                    }
                }
            }
        }

        return false;
    }

    private String callLLM(String prompt) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000); // 30 second timeout
        conn.setReadTimeout(30000);

        // Ollama API request format
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.toString().getBytes());
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Ollama API returned " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("response");
    }

    private Move parseMove(String response, int boardSize) {
        System.out.println("LLM Response: " + response); // Debug

        // Look for pattern: row col letter (with or without "MOVE:" prefix)
        String[] lines = response.split("\n");
        for (String line : lines) {
            String originalLine = line;
            line = line.trim().toUpperCase();

            // Extract numbers and letter from the line
            // Try to find pattern: digit(s) digit(s) letter
            String[] parts = line.split("[\\s,:.]+");

            // Look through all parts to find a valid move pattern
            for (int i = 0; i <= parts.length - 3; i++) {
                try {
                    int row = Integer.parseInt(parts[i]);
                    int col = Integer.parseInt(parts[i + 1]);

                    // Get letter - handle both 'O' and '0' (zero)
                    String letterStr = parts[i + 2];
                    char letter = letterStr.charAt(0);

                    // Convert '0' (zero) to 'O' (letter O)
                    if (letter == '0') {
                        letter = 'O';
                    }

                    // Validate
                    if (row >= 0 && row < boardSize &&
                        col >= 0 && col < boardSize &&
                        (letter == 'S' || letter == 'O')) {
                        System.out.println("Parsed move: " + row + " " + col + " " + letter);
                        return new Move(row, col, letter);
                    }
                } catch (Exception e) {
                    // Try next combination
                }
            }
        }

        System.err.println("Failed to parse valid move from LLM response");
        return null;
    }
}
