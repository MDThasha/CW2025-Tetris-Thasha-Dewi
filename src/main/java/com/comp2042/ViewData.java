package com.comp2042;

import java.awt.Point;

public final class ViewData {
    private final int[][] brickData;       // Current brick's shape matrix
    private final int xPosition;           // Current X position of the brick on the board
    private final int yPosition;           // Current Y position of the brick on the board
    private final int[][] nextBrickData;   // Next brick's shape matrix for preview
    private final Point ghostOffset;       // Offset for ghost preview (where brick would land)

    // Initializes all brick data, positions, and ghost info
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, Point ghostOffset) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostOffset = ghostOffset;
    }

    public Point getGhostOffset() { return ghostOffset; }                                   // Get the ghost offset for the current brick
    public int[][] getBrickData() { return MatrixOperations.copy(brickData); }              // Get a copy of the current brick's shape matrix
    public int getxPosition() { return xPosition; }                                         // Get the X position of the brick
    public int getyPosition() { return yPosition; }                                         // Get the Y position of the brick
    public int[][] getNextBrickData() { return MatrixOperations.copy(nextBrickData); }      // Get a copy of the next brick's shape matrix for preview
}
