package com.comp2042.logic.bricks;

import java.util.List;

/** Represents a brick shape and its rotation states.
 * <p>provide a list of 2D integer matrices where each matrix is a
 * rotation of the brick. Matrices are expected in the format int[row][col] (row = Y,
 * col = X). The first element in the list should be the default/spawn orientation.</p>*/
public interface Brick {
    /**Return the rotation matrices for this brick.
     * @return a list of int[][] matrices representing the brick in each rotation, the first entry is the default orientation.*/
    List<int[][]> getShapeMatrix();
}