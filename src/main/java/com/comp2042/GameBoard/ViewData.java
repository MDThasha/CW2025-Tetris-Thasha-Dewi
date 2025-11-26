package com.comp2042.GameBoard;

import java.awt.Point;

/** current falling brick and related preview data for rendering.*/
public final class ViewData {
    /** Current brick's shape matrix*/
    private final int[][] brickData;

    /** Current X position of the brick on the board*/
    private final int xPosition;

    /** Current Y position of the brick on the board*/
    private final int yPosition;


    /** Offset for ghost preview (where brick would land)*/
    private final Point ghostOffset;


    /** Initializes all brick data, positions, and ghost info
     * @param brickData     the current brick shape matrix (int[row][col])
     * @param xPosition     current X (column) position of the brick on the board
     * @param yPosition     current Y (row) position of the brick on the board
     * @param nextBrickData next brick shape matrix used for preview
     * @param ghostOffset   position where the brick would land (may be null)*/
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, Point ghostOffset) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostOffset = ghostOffset;
    }

    /** Get the ghost offset for the current brick
     * @return a defensive copy of the ghost offset Point (maybe null)*/
    public Point getGhostOffset() { return ghostOffset; }

    /** Get a copy of the current brick's shape matrix
     * @return a deep copy of the current brick's shape matrix (int[row][col])*/
    public int[][] getBrickData() { return MatrixOperations.copy(brickData); }

    /** Get the X position of the brick
     * @return the current X (column) position of the brick on the board*/
    public int getxPosition() { return xPosition; }

    /** Get the Y position of the brick
     * @return the current Y (row) position of the brick on the board*/
    public int getyPosition() { return yPosition; }

}
