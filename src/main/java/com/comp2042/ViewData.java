package com.comp2042;

import java.awt.Point;

public final class ViewData {
    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final Point ghostOffset;

    // Updated constructor
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, Point ghostOffset) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostOffset = ghostOffset;
    }

    public Point getGhostOffset() { return ghostOffset; }
    public int[][] getBrickData() { return MatrixOperations.copy(brickData); }
    public int getxPosition() { return xPosition; }
    public int getyPosition() { return yPosition; }
    public int[][] getNextBrickData() { return MatrixOperations.copy(nextBrickData); }
}