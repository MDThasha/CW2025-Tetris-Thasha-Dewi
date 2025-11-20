package com.comp2042.logic.bricks;

import com.comp2042.GameBoard.MatrixOperations;

/** Holds info about a brick's shape matrix and its rotation index/position.*/
public final class NextShapeInfo {
    /** The shape matrix for this rotation (int[row][col]).*/
    private final int[][] shape;

    /** The rotation index/position within the brick's rotation list.*/
    private final int position;

    /** Create a NextShapeInfo containing a shape matrix and its rotation index.
     * @param shape
     * @param position */
    public NextShapeInfo(final int[][] shape, final int position) { this.shape = shape; this.position = position; }

    /** Return a defensive copy of the shape matrix.
     * @return copy of matix*/
    public int[][] getShape() { return MatrixOperations.copy(shape); }

    /** Return the rotation index/position.
     * @return position*/
    public int getPosition() { return position; }
}

