package sosgui;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.geom.*;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SosRdGui extends JFrame{
	
	public SosRdGui() {
	
		JFrame frame = new JFrame();
		
		JButton startButton = new JButton("Start Game");
		startButton.setVerticalAlignment(JButton.BOTTOM);
		startButton.setSize(100, 100);
		
		JCheckBox sCheckBox = new JCheckBox("S");
		
		JCheckBox oCheckBox = new JCheckBox("O");
		
		JRadioButton player1RadioButton = new JRadioButton("Player");
		
		JRadioButton player2RadioButton = new JRadioButton("Player");
		
		
		
		JLabel player1Label = new JLabel ("Player1");
		player1Label.setHorizontalAlignment(JLabel.LEFT);
		player1Label.setVerticalAlignment(0);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(500, 500, 500, 500));
		panel.setLayout(new GridLayout());
		panel.add(startButton);
		panel.add(player1Label);
		panel.add(sCheckBox);
		panel.add(oCheckBox);
		panel.add(player1RadioButton);
		panel.add(player2RadioButton);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("SOS");
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	
	}
	
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2 = (Graphics2D) g;
			Line2D lin = new Line2D.Float(70, 70, 70, 70);
			g2.draw(lin);
		}

	public static void main(String[] args) {
		new SosRdGui();

	}

}
