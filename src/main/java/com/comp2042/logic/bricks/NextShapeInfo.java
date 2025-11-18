package com.comp2042.logic.bricks;

import com.comp2042.GameBoard.MatrixOperations;

public final class NextShapeInfo {  // Holds info about a brick's shape and its rotation position
    private final int[][] shape;
    private final int position;
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }
    public int getPosition() {
        return position;
    }
}

