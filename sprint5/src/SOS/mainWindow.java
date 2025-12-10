package SOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainWindow implements ActionListener {

    private JFrame frame;

    private enum GameMode { SIMPLE, GENERAL }

    JPanel boardPanel = new JPanel();
    JButton[][] board;
    BoardOverlayPanel overlayPanel;

    private JRadioButton blueSButton, blueOButton;
    private JRadioButton redSButton, redOButton;
    private JRadioButton simpleGameButton;
    JRadioButton generalGameButton;
    private JRadioButton blueHumanButton, blueComputerButton;
    private JRadioButton redHumanButton, redComputerButton;
    private JLabel currentTurnLabel;
    private JLabel gameModeLabel;
    private JLabel scoreLabel;

    private GameMode gameMode = GameMode.SIMPLE;

    String bluePlayer = "Blue Player";
    String redPlayer = "Red Player";
    String currentPlayer = bluePlayer;

    /** Game logic strategy (simple or general) */
    private GameLogic gameLogic;

    /** Players */
    private Player bluePlayerObj;
    private Player redPlayerObj;

    /** Flag to prevent multiple computer moves */
    private boolean processingComputerMove = false;

    /** Game recorder for recording games */
    private GameRecorder recorder;

    /** Game replayer for replaying games */
    private GameReplayer replayer;

    /** Flag indicating if we're in replay mode */
    private boolean replayMode = false;

    /** UI components for record/replay */
    private JCheckBox recordCheckBox;
    private JButton replayButton;

    public mainWindow() {
        frame = new JFrame();
        frame.setTitle("SOS Game");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize recorder and replayer
        recorder = new GameRecorder();
        replayer = new GameReplayer();

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
        scoreLabel = new JLabel("Score - Blue: 0 | Red: 0");

        // Record/Replay controls
        recordCheckBox = new JCheckBox("Record game");
        replayButton = new JButton("Replay");
        replayButton.addActionListener(e -> startReplay());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(recordCheckBox);
        bottomPanel.add(currentTurnLabel);
        bottomPanel.add(gameModeLabel);
        bottomPanel.add(scoreLabel);
        bottomPanel.add(replayButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Left panel (Blue)
        blueSButton = new JRadioButton("S");
        blueOButton = new JRadioButton("O");
        blueHumanButton = new JRadioButton("Human");
        blueComputerButton = new JRadioButton("Computer");

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(bluePlayerLabel);

        // Player type selection
        ButtonGroup blueTypeGroup = new ButtonGroup();
        blueTypeGroup.add(blueHumanButton);
        blueTypeGroup.add(blueComputerButton);
        blueHumanButton.setSelected(true);
        leftPanel.add(blueHumanButton);
        leftPanel.add(blueComputerButton);

        leftPanel.add(Box.createVerticalStrut(10));

        // Letter selection
        leftPanel.add(new JLabel("Letter:"));
        leftPanel.add(blueSButton);
        leftPanel.add(blueOButton);
        ButtonGroup bluePlayerGroup = new ButtonGroup();
        bluePlayerGroup.add(blueSButton);
        bluePlayerGroup.add(blueOButton);
        blueSButton.setSelected(true);

        // Add listeners for letter selection
        blueSButton.addActionListener(e -> {
            if (bluePlayerObj != null) bluePlayerObj.setSelectedLetter('S');
        });
        blueOButton.addActionListener(e -> {
            if (bluePlayerObj != null) bluePlayerObj.setSelectedLetter('O');
        });

        frame.add(leftPanel, BorderLayout.WEST);

        // Right panel (Red)
        redSButton = new JRadioButton("S");
        redOButton = new JRadioButton("O");
        redHumanButton = new JRadioButton("Human");
        redComputerButton = new JRadioButton("Computer");

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(redPlayerLabel);

        // Player type selection
        ButtonGroup redTypeGroup = new ButtonGroup();
        redTypeGroup.add(redHumanButton);
        redTypeGroup.add(redComputerButton);
        redHumanButton.setSelected(true);
        rightPanel.add(redHumanButton);
        rightPanel.add(redComputerButton);

        rightPanel.add(Box.createVerticalStrut(10));

        // Letter selection
        rightPanel.add(new JLabel("Letter:"));
        rightPanel.add(redSButton);
        rightPanel.add(redOButton);
        ButtonGroup redPlayerGroup = new ButtonGroup();
        redPlayerGroup.add(redSButton);
        redPlayerGroup.add(redOButton);
        redSButton.setSelected(true);

        // Add listeners for letter selection
        redSButton.addActionListener(e -> {
            if (redPlayerObj != null) redPlayerObj.setSelectedLetter('S');
        });
        redOButton.addActionListener(e -> {
            if (redPlayerObj != null) redPlayerObj.setSelectedLetter('O');
        });

        frame.add(rightPanel, BorderLayout.EAST);

        // Create layered panel for board + overlay
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // Use absolute positioning

        // Board panel on bottom layer
        boardPanel.setBounds(0, 0, 600, 600);
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        // Overlay panel on top layer
        overlayPanel = new BoardOverlayPanel();
        overlayPanel.setBounds(0, 0, 600, 600);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        layeredPane.setPreferredSize(new Dimension(600, 600));
        frame.add(layeredPane, BorderLayout.CENTER);

        // Start button handler
        startGameButton.addActionListener(e -> {
            String input = boardSizeInput.getText().trim();
            try {
                int size = Integer.parseInt(input);
                if (size < 3 || size > 9) {
                    JOptionPane.showMessageDialog(frame, "Board size must be between 3 and 9");
                    return;
                }
                startNewGame(size);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid integer.");
            }
        });

        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
    }

    private void startNewGame(int size) {
        // Reset replay mode
        replayMode = false;

        // Reset players/labels
        currentPlayer = bluePlayer;
        currentTurnLabel.setText("Current Player's turn: " + currentPlayer + "   ");

        // Create correct game logic
        if (gameMode == GameMode.SIMPLE) {
            gameLogic = new SimpleGameLogic(size, this::announce);
        } else {
            gameLogic = new GeneralGameLogic(size, this::announce);
        }
        scoreLabel.setText("Score - Blue: 0 | Red: 0");

        // Create players based on selection
        if (blueHumanButton.isSelected()) {
            bluePlayerObj = new HumanPlayer();
        } else {
            bluePlayerObj = new ComputerPlayer();
        }

        if (redHumanButton.isSelected()) {
            redPlayerObj = new HumanPlayer();
        } else {
            redPlayerObj = new ComputerPlayer();
        }

        // Initialize selected letters
        bluePlayerObj.setSelectedLetter(blueSButton.isSelected() ? 'S' : 'O');
        redPlayerObj.setSelectedLetter(redSButton.isSelected() ? 'S' : 'O');

        createBoard(size);

        // Initialize overlay with game logic
        overlayPanel.setGameLogic(gameLogic, size);

        // Start recording if checkbox is selected
        if (recordCheckBox.isSelected()) {
            String gameModeStr = (gameMode == GameMode.SIMPLE) ? "Simple" : "General";
            String blueType = blueHumanButton.isSelected() ? "Human" : "Computer";
            String redType = redHumanButton.isSelected() ? "Human" : "Computer";
            recorder.startRecording(gameModeStr, size, blueType, redType);
        }

        // If blue player is computer, start the first move
        if (bluePlayerObj.getType() == PlayerType.COMPUTER) {
            SwingUtilities.invokeLater(this::executeComputerMove);
        }
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

        boolean isBlueTurn = currentPlayer.equals(bluePlayer);
        Player currentPlayerObj = isBlueTurn ? bluePlayerObj : redPlayerObj;

        // Only allow human players to click
        if (currentPlayerObj.getType() != PlayerType.HUMAN) return;

        executeMove(r, c, currentPlayerObj.getSelectedLetter(), isBlueTurn, tile);
    }

    private void executeMove(int r, int c, char letter, boolean isBlueTurn, JButton tile) {
        // Apply to UI
        tile.setText(String.valueOf(letter));
        tile.setForeground(isBlueTurn ? Color.BLUE : Color.RED);

        // Record move if recording is enabled
        if (!replayMode && recorder.isRecording()) {
            recorder.recordMove(r, c, letter, isBlueTurn);
        }

        // Notify game logic
        MoveResult result = gameLogic.onMove(r, c, letter, isBlueTurn);

        // Update score label
        scoreLabel.setText("Score - Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());

        // Repaint overlay to show any new SOS lines
        overlayPanel.repaint();

        if (result.gameEnded) {
            disableBoard();

            // Stop recording if active
            if (recorder.isRecording()) {
                String filename = recorder.stopRecording();
                if (filename != null) {
                    announce("Game saved to: " + filename);
                }
            }

            if (result.draw) {
                announce("Draw! Final score - Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());
            } else {
                announce(result.winner + " wins! Final score - Blue: " + gameLogic.getBlueScore() + " | Red: " + gameLogic.getRedScore());
            }
            return;
        }

        // Alternate turn
        currentPlayer = isBlueTurn ? redPlayer : bluePlayer;
        currentTurnLabel.setText("Current Player's turn: " + currentPlayer + "   ");

        // If next player is computer, execute their move
        Player nextPlayer = isBlueTurn ? redPlayerObj : bluePlayerObj;
        if (nextPlayer.getType() == PlayerType.COMPUTER && !replayMode) {
            // Add small delay for better UX
            Timer timer = new Timer(500, e -> executeComputerMove());
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void executeComputerMove() {
        if (processingComputerMove || gameLogic == null || gameLogic.isGameOver()) {
            return;
        }

        processingComputerMove = true;

        // Run in background thread to avoid blocking UI
        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() throws Exception {
                boolean isBlueTurn = currentPlayer.equals(bluePlayer);
                Player currentPlayerObj = isBlueTurn ? bluePlayerObj : redPlayerObj;
                return currentPlayerObj.getMove(gameLogic, isBlueTurn);
            }

            @Override
            protected void done() {
                try {
                    Move move = get();
                    if (move != null) {
                        boolean isBlueTurn = currentPlayer.equals(bluePlayer);
                        JButton tile = board[move.row][move.col];
                        executeMove(move.row, move.col, move.letter, isBlueTurn, tile);
                    }
                } catch (Exception e) {
                    System.err.println("Computer move error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    processingComputerMove = false;
                }
            }
        };

        worker.execute();
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

    /**
     * Start replaying a recorded game.
     */
    private void startReplay() {
        // Open file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Select Game Recording");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Game Recording Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            if (replayer.loadFromFile(selectedFile.getAbsolutePath())) {
                setupReplayGame();
            } else {
                announce("Failed to load game file.");
            }
        }
    }

    /**
     * Setup the game board for replay.
     */
    private void setupReplayGame() {
        replayMode = true;

        // Set game mode based on loaded game
        if (replayer.getGameMode().equals("Simple")) {
            gameMode = GameMode.SIMPLE;
            simpleGameButton.setSelected(true);
            gameLogic = new SimpleGameLogic(replayer.getBoardSize(), this::announce);
        } else {
            gameMode = GameMode.GENERAL;
            generalGameButton.setSelected(true);
            gameLogic = new GeneralGameLogic(replayer.getBoardSize(), this::announce);
        }

        gameModeLabel.setText("Game Mode: " + replayer.getGameMode() + " (REPLAY)");
        scoreLabel.setText("Score - Blue: 0 | Red: 0");

        // Reset current player
        currentPlayer = bluePlayer;
        currentTurnLabel.setText("Replaying... Move 0/" + replayer.getTotalMoves());

        // Create board
        createBoard(replayer.getBoardSize());
        overlayPanel.setGameLogic(gameLogic, replayer.getBoardSize());

        // Start replay with delay
        Timer timer = new Timer(1000, e -> replayNextMove());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Replay the next move in the sequence.
     */
    private void replayNextMove() {
        if (!replayer.hasMoreMoves()) {
            currentTurnLabel.setText("Replay complete!");
            replayMode = false;
            return;
        }

        GameRecorder.MoveRecord move = replayer.getNextMove();
        if (move != null) {
            JButton tile = board[move.row][move.col];
            executeMove(move.row, move.col, move.letter, move.isBlueTurn, tile);

            // Update replay progress
            currentTurnLabel.setText("Replaying... Move " + replayer.getCurrentMoveIndex() + "/" + replayer.getTotalMoves());

            // Schedule next move
            Timer timer = new Timer(1000, e -> replayNextMove());
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new mainWindow().show());
    }
}
