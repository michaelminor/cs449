package SOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class mainWindow implements ActionListener {

    private JFrame frame;

    private enum GameMode { SIMPLE, GENERAL }

    JPanel boardPanel = new JPanel();
    JButton[][] board;

    private JRadioButton blueSButton, blueOButton;
    private JRadioButton redSButton, redOButton;
    private JRadioButton simpleGameButton;
    JRadioButton generalGameButton;
    private JLabel currentTurnLabel;
    private JLabel gameModeLabel;
    private JLabel scoreLabel;

    private GameMode gameMode = GameMode.SIMPLE;

    String bluePlayer = "Blue Player";
    String redPlayer = "Red Player";
    String currentPlayer = bluePlayer;

    /** Game logic strategy (simple or general) */
    private GameLogic gameLogic;

    public mainWindow() {
        frame = new JFrame();
        frame.setTitle("SOS Game");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Buttons
        JButton startGameButton = new JButton("Start Game");

        // Game mode radio buttons
        simpleGameButton = new JRadioButton("Simple game");
        generalGameButton = new JRadioButton("General game");
        ButtonGroup gameModeGroup = new ButtonGroup();
        gameModeGroup.add(generalGameButton);
        gameModeGroup.add(simpleGameButton);
        simpleGameButton.setSelected(true);

        simpleGameButton.addActionListener(e -> {
            gameMode = GameMode.SIMPLE;
            gameModeLabel.setText("Game Mode: Simple");
        });

        generalGameButton.addActionListener(e -> {
            gameMode = GameMode.GENERAL;
            gameModeLabel.setText("Game Mode: General");
        });

        // Labels
        JLabel boardSizeLabel = new JLabel("Enter board size between 3 and 9 (6 will be 6x6 game board)");
        JLabel bluePlayerLabel = new JLabel("Blue Player");
        JLabel redPlayerLabel = new JLabel("Red Player");

        // Input
        JTextField boardSizeInput = new JTextField("6");
        boardSizeInput.setPreferredSize(new Dimension(40, 24));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.add(simpleGameButton);
        topPanel.add(generalGameButton);
        topPanel.add(boardSizeLabel);
        topPanel.add(boardSizeInput);
        topPanel.add(startGameButton);
        frame.add(topPanel, BorderLayout.NORTH);

        // Bottom panel
        currentTurnLabel = new JLabel("Current Player's turn: " + currentPlayer + "   ");
        gameModeLabel = new JLabel("Game Mode: " + (gameMode == GameMode.SIMPLE ? "Simple" : "General") + "   ");
        scoreLabel = new JLabel("Score — Blue: 0 | Red: 0");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(currentTurnLabel);
        bottomPanel.add(gameModeLabel);
        bottomPanel.add(scoreLabel);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Left panel (Blue)
        blueSButton = new JRadioButton("S");
        blueOButton = new JRadioButton("O");
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(bluePlayerLabel);
        leftPanel.add(blueSButton);
        leftPanel.add(blueOButton);
        ButtonGroup bluePlayerGroup = new ButtonGroup();
        bluePlayerGroup.add(blueSButton);
        bluePlayerGroup.add(blueOButton);
        blueSButton.setSelected(true);
        frame.add(leftPanel, BorderLayout.WEST);

        // Right panel (Red)
        redSButton = new JRadioButton("S");
        redOButton = new JRadioButton("O");
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(redPlayerLabel);
        rightPanel.add(redSButton);
        rightPanel.add(redOButton);
        ButtonGroup redPlayerGroup = new ButtonGroup();
        redPlayerGroup.add(redSButton);
        redPlayerGroup.add(redOButton);
        redSButton.setSelected(true);
        frame.add(rightPanel, BorderLayout.EAST);

        frame.add(boardPanel, BorderLayout.CENTER);

        // Start button handler
        startGameButton.addActionListener(e -> {
            String input = boardSizeInput.getText().trim();
            try {
                int size = Integer.parseInt(input);
                if (size < 3 || size > 9) {
                    JOptionPane.showMessageDialog(frame, "Board size must be between 3 and 9");
                    return;
                }
                // Reset players/labels
                currentPlayer = bluePlayer;
                currentTurnLabel.setText("Current Player's turn: " + currentPlayer + "   ");

                // Create correct game logic
                if (gameMode == GameMode.SIMPLE) {
                    gameLogic = new PlaySimpleGame(size, this::announce);
                } else {
                    gameLogic = new PlayGeneralGame(size, this::announce);
                }
                scoreLabel.setText("Score — Blue: 0 | Red: 0");

                createBoard(size);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid integer.");
            }
        });

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
    }

    private void createBoard(int size) {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(size, size));
        board = new JButton[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                final int rr = r, cc = c;
                JButton tile = new JButton();
                tile.setFont(new Font("Arial", Font.BOLD, 20));
                board[r][c] = tile;
                tile.addActionListener(e -> handleTileClick(rr, cc, tile));
                boardPanel.add(tile);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void handleTileClick(int r, int c, JButton tile) {
        if (gameLogic == null || gameLogic.isGameOver()) return;
        if (!tile.getText().isEmpty()) return;

        // Determine letter based on current player selection
        String letter;
        boolean isBlueTurn = currentPlayer.equals(bluePlayer);

        if (isBlueTurn) {
            if (blueSButton.isSelected()) letter = "S";
            else if (blueOButton.isSelected()) letter = "O";
            else return;
            tile.setForeground(Color.BLUE);
        } else {
            if (redSButton.isSelected()) letter = "S";
            else if (redOButton.isSelected()) letter = "O";
            else return;
            tile.setForeground(Color.RED);
        }

        // Apply to UI
        tile.setText(letter);

        // Notify game logic
        MoveResult result = gameLogic.onMove(r, c, letter.charAt(0), isBlueTurn);

        // Update score label
        scoreLabel.setText("Score — Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());

        if (result.gameEnded) {
            disableBoard();
            if (result.draw) {
                announce("Draw! Final score — Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());
            } else {
                announce(result.winner + " wins! Final score — Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());
            }
            return;
        }

        // Alternate turn (no extra turn on scoring)
        currentPlayer = isBlueTurn ? redPlayer : bluePlayer;
        currentTurnLabel.setText("Current Player's turn: " + currentPlayer + "   ");
    }

    private void disableBoard() {
        if (board == null) return;
        for (JButton[] row : board) {
            for (JButton b : row) b.setEnabled(false);
        }
    }

    private void announce(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    // game logic

    
    private static class MoveResult {
        final boolean gameEnded;
        final boolean draw;
        final String winner; // non-null only if !draw && gameEnded

        private MoveResult(boolean gameEnded, boolean draw, String winner) {
            this.gameEnded = gameEnded;
            this.draw = draw;
            this.winner = winner;
        }

        static MoveResult ongoing() { return new MoveResult(false, false, null); }
        static MoveResult endedWithWinner(String winner) { return new MoveResult(true, false, winner); }
        static MoveResult endedDraw() { return new MoveResult(true, true, null); }
    }

    //interface
    private interface GameLogic {
        MoveResult onMove(int r, int c, char letter, boolean blueTurn);
        int getBlueScore();
        int getRedScore();
        boolean isGameOver();
    }

    //shared logic
    private static abstract class GameLogicBase implements GameLogic {
        protected final int size;
        protected final char[][] grid;
        protected int filled = 0;

        protected int blueScore = 0;
        protected int redScore = 0;

        protected boolean gameOver = false;
        protected final Consumer<String> announcer;

        protected GameLogicBase(int size, Consumer<String> announcer) {
            this.size = size;
            this.grid = new char[size][size];
            this.announcer = announcer;
        }

        @Override
        public int getBlueScore() { return blueScore; }

        @Override
        public int getRedScore() { return redScore; }

        @Override
        public boolean isGameOver() { return gameOver; }

        protected boolean inBounds(int r, int c) {
            return r >= 0 && r < size && c >= 0 && c < size;
        }

        /**
         * Place letter and count all SOS created that include (r,c).
         * Counts across 8 directions. An SOS can be formed in three patterns relative to the placed tile:
         * - If placed letter == 'S': acts as start or end of "S O S"
         * - If placed letter == 'O': must have 'S' on both sides along a line
         */
        protected int countNewSOS(int r, int c, char letter) {
            int[][] dirs = {
                {-1, 0}, {1, 0},  // vertical
                {0, -1}, {0, 1},  // horizontal
                {-1, -1}, {1, 1}, // diag \
                {-1, 1}, {1, -1}  // diag /
            };

            int count = 0;
            if (letter == 'S') {
                // S as start: (S at 0) (O at +1) (S at +2)
                for (int[] d : dirs) {
                    int r1 = r + d[0], c1 = c + d[1];
                    int r2 = r + 2 * d[0], c2 = c + 2 * d[1];
                    if (inBounds(r1, c1) && inBounds(r2, c2)) {
                        if (grid[r1][c1] == 'O' && grid[r2][c2] == 'S') count++;
                    }
                }
                // S as end: (S at -2) (O at -1) (S at 0)
                for (int[] d : dirs) {
                    int r_1 = r - d[0], c_1 = c - d[1];
                    int r_2 = r - 2 * d[0], c_2 = c - 2 * d[1];
                    if (inBounds(r_1, c_1) && inBounds(r_2, c_2)) {
                        if (grid[r_2][c_2] == 'S' && grid[r_1][c_1] == 'O') count++;
                    }
                }
            } else if (letter == 'O') {
                // O in the middle: (S at -1) (O at 0) (S at +1)
                for (int[] d : dirs) {
                    int r_1 = r - d[0], c_1 = c - d[1];
                    int r1 = r + d[0], c1 = c + d[1];
                    if (inBounds(r_1, c_1) && inBounds(r1, c1)) {
                        if (grid[r_1][c_1] == 'S' && grid[r1][c1] == 'S') count++;
                    }
                }
            }
            return count;
        }

        protected void place(int r, int c, char letter) {
            grid[r][c] = letter;
            filled++;
        }

        protected boolean boardFull() {
            return filled >= size * size;
        }
    }

    //simple game logic
    private static class PlaySimpleGame extends GameLogicBase {

        public PlaySimpleGame(int size, Consumer<String> announcer) {
            super(size, announcer);
        }

        @Override
        public MoveResult onMove(int r, int c, char letter, boolean blueTurn) {
            if (gameOver) return MoveResult.ongoing();
            place(r, c, letter);

            int gained = countNewSOS(r, c, letter);
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

    // general game logic
    private static class PlayGeneralGame extends GameLogicBase {

        public PlayGeneralGame(int size, Consumer<String> announcer) {
            super(size, announcer);
        }

        @Override
        public MoveResult onMove(int r, int c, char letter, boolean blueTurn) {
            if (gameOver) return MoveResult.ongoing();
            place(r, c, letter);

            int gained = countNewSOS(r, c, letter);
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

    //end game

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new mainWindow().show());
    }
}
