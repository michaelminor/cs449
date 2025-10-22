package SOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainWindow implements ActionListener{
	
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
	
	private GameMode gameMode = GameMode.SIMPLE;
	
	String bluePlayer = "Blue Player";
	String redPlayer = "Red Player";
	String currentPlayer = bluePlayer;
	
	public mainWindow() {
		frame = new JFrame();
		frame.setTitle("SOS Game");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		//Button creation
		
		JButton startGameButton = new JButton("Start Game");
		
		
		
		//radio button creation
		simpleGameButton = new JRadioButton("Simple game");
		generalGameButton = new JRadioButton("General game");
		ButtonGroup gameModeGroup = new ButtonGroup();
		gameModeGroup.add(generalGameButton);
		gameModeGroup.add(simpleGameButton);
		simpleGameButton.setSelected(true);
		
		simpleGameButton.addActionListener(e -> {
			gameMode = GameMode.SIMPLE;
			gameModeLabel.setText("   Game Mode: Simple");
		});
		
		generalGameButton.addActionListener(e -> {
			gameMode = GameMode.GENERAL;
			gameModeLabel.setText("   Game Mode: General");
		});
		
		
		
		//Label creation
		JLabel boardSizeLabel = new JLabel("Enter board size between 3 and 9 (6 will be 6x6 game board)");
		JLabel bluePlayerLabel = new JLabel("Blue Player");
		JLabel redPlayerLabel = new JLabel("Red Player");
		
		
		//Text panel creation
		JTextField boardSizeInput = new JTextField("6");
		boardSizeInput.setPreferredSize(new Dimension(20,20));
		
		//top panel creation
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20 ,10));
		topPanel.add(simpleGameButton);
		topPanel.add(generalGameButton);
		topPanel.add(boardSizeLabel);
		topPanel.add(boardSizeInput);
		topPanel.add(startGameButton);
		
		frame.add(topPanel, BorderLayout.NORTH);
		
		//bottom panel creation
		currentTurnLabel = new JLabel("Current Player's turn: " + currentPlayer);
		gameModeLabel = new JLabel("Game Mode: " + gameMode);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(currentTurnLabel);
		bottomPanel.add(gameModeLabel);
		
		frame.add(bottomPanel, BorderLayout.SOUTH);		
		
		//left panel creation
		blueSButton = new JRadioButton("S");
		blueOButton = new JRadioButton("O");
		JPanel leftPanel = new JPanel();
		leftPanel.add(bluePlayerLabel);
		leftPanel.add(blueSButton);
		leftPanel.add(blueOButton);
		ButtonGroup bluePlayerGroup = new ButtonGroup();
		bluePlayerGroup.add(blueSButton);
		bluePlayerGroup.add(blueOButton);
		blueSButton.setSelected(true);
	
		frame.add(leftPanel, BorderLayout.WEST);
		
		//right panel creation
		redSButton = new JRadioButton("S");
		redOButton = new JRadioButton("O");
		JPanel rightPanel = new JPanel();
		rightPanel.add(redPlayerLabel);
		rightPanel.add(redSButton);
		rightPanel.add(redOButton);
		ButtonGroup redPlayerGroup = new ButtonGroup();
		redPlayerGroup.add(redSButton);
		redPlayerGroup.add(redOButton);
		redSButton.setSelected(true);
		
		frame.add(rightPanel, BorderLayout.EAST);
		
		frame.add(boardPanel, BorderLayout.CENTER);
		
		//Button listeners
				startGameButton.addActionListener(e -> {
					String input = boardSizeInput.getText().trim();
					try {
						int size = Integer.parseInt(input);
						if (size <3 || size > 9) {
							JOptionPane.showMessageDialog(frame,"Board size must be between 3 and 9");
							return;
						}
						
						createBoard(size);
						
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(frame, "Please enter a valid integer.");
					}
				});
				
				frame.setSize(800,800);
				frame.setLocationRelativeTo(null);
		
		
		
	}
	
	private void createBoard(int size) {
		boardPanel.removeAll();
		boardPanel.setLayout(new GridLayout(size, size));
		board = new JButton[size][size];
		
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				JButton tile = new JButton();
				tile.setFont(new Font("Arial", Font.BOLD, 20));
				board[r][c] = tile;
				tile.addActionListener(e -> handleTileClick(tile));
				boardPanel.add(tile);
			}
		}
		
		boardPanel.revalidate();
		boardPanel.repaint();
	}
	
	private void handleTileClick(JButton tile) {
		if (!tile.getText().isEmpty()) return;
		
		String letter = "";
		if (currentPlayer.equals(bluePlayer)) {
			if (blueSButton.isSelected()) letter = "S";
			else if (blueOButton.isSelected()) letter = "O";
			tile.setForeground(Color.BLUE);
		} else {
			if (redSButton.isSelected()) letter = "S";
			else if (redOButton.isSelected()) letter = "O";
			tile.setForeground(Color.RED);
		}
		tile.setText(letter);
		
		currentPlayer = currentPlayer.equals(bluePlayer) ? redPlayer : bluePlayer;
		currentTurnLabel.setText("current Player's turn: " + currentPlayer);
		
	}
	

	
	
	public void show() {
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
