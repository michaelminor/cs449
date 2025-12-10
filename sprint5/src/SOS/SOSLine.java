package SOS;

import java.awt.Color;

/**
 * Represents a strikethrough line for a completed SOS pattern.
 */
public class SOSLine {
    public final int startRow;
    public final int startCol;
    public final int endRow;
    public final int endCol;
    public final Color color;

    public SOSLine(int startRow, int startCol, int endRow, int endCol, Color color) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.color = color;
    }
}
