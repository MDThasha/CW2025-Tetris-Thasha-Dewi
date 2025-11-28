package com.comp2042.logic.bricks;

import com.comp2042.GameBoard.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/** I-shaped implementation.*/
public final class IBrick implements Brick {

    /** Internal list of rotation matrices for this brick (int[row][col]). */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /** Construct an IBrick and initialize its rotation matrices.*/
    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    /**Return a deep-copied list of rotation matrices for this brick.
     @return a List of int[][] matrices representing each rotation (deep copies)*/
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}