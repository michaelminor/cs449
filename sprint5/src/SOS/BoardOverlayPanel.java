package SOS;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Transparent overlay panel that draws strikethrough lines for SOS patterns.
 */
public class BoardOverlayPanel extends JPanel {
    private GameLogic gameLogic;
    private int boardSize;

    public BoardOverlayPanel() {
        setOpaque(false); // Transparent background
    }

    public void setGameLogic(GameLogic gameLogic, int boardSize) {
        this.gameLogic = gameLogic;
        this.boardSize = boardSize;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameLogic == null || boardSize == 0) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(3.0f)); // Thick line

        List<SOSLine> lines = gameLogic.getSOSLines();
        if (lines == null) return;

        // Calculate cell dimensions
        int cellWidth = getWidth() / boardSize;
        int cellHeight = getHeight() / boardSize;

        for (SOSLine line : lines) {
            // Calculate center points of start and end cells
            int x1 = line.startCol * cellWidth + cellWidth / 2;
            int y1 = line.startRow * cellHeight + cellHeight / 2;
            int x2 = line.endCol * cellWidth + cellWidth / 2;
            int y2 = line.endRow * cellHeight + cellHeight / 2;

            g2d.setColor(line.color);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}
