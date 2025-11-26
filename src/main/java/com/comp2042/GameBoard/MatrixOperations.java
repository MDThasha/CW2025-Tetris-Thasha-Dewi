package com.comp2042.GameBoard;

import java.util.stream.Collectors;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Utility operations for 2D board matrices and brick shapes.
 * <p>All matrices are expected as int[row][col] where the first index is the row (Y)
 * and the second index is the column (X).</p>*/
public final class MatrixOperations {
    private MatrixOperations(){ }

    /** Base score multiplier per cleared line (used in score bonus calculation). */
    private static final int SCORE_PER_LINE_BASE = 50;

    /** Check whether a brick placed at board coordinates (x,y) would intersect the board or be out of bounds.
     * @param matrix the board matrix (int[row][col])
     * @param brick the brick shape matrix (int[brickRows][brickCols])
     * @param x target column on the board where brick's column 0 will be placed
     * @param y target row on the board where brick's row 0 will be placed
     * @return true if the brick would collide with existing filled cells or be out of bounds */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Check whether the given target coordinates are outside the provided matrix.
     * @param matrix the board matrix
     * @param targetX column index to check (must be >= 0 and < matrix[row].length)
     * @param targetY row index to check (must be >= 0 and < matrix.length)
     * @return true if out of bounds, false otherwise */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        return targetX < 0 || targetY >= matrix.length || targetX >= matrix[targetY].length;
    }

    /** Create a deep copy of a 2D int matrix.
     * @param original the original matrix to copy
     * @return a new matrix equal to the original but independent */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /** Merge a brick into a copy of the filledFields matrix at position (x,y).
     * @param filledFields the board matrix containing static/placed cells (int[row][col])
     * @param brick the brick shape (int[brickRows][brickCols])
     * @param x target column to place brick's column 0
     * @param y target row to place brick's row 0
     * @return a new board matrix with the brick merged in (does not mutate the original)*/
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /** Detect and remove complete rows from the board.
     * <p>Returns a ClearRow containing the number of cleared lines, the new matrix after
     * the removal (rows dropped down), and a score bonus computed as
     * SCORE_PER_LINE_BASE * (linesRemoved)^2.</p>
     * @param matrix the board matrix to check (int[row][col])
     * @return a ClearRow describing the result (linesRemoved may be zero)*/
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            } if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        } for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = SCORE_PER_LINE_BASE * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus); // Calculates Score Bonus
    }

    /** Deep-copy a list of 2D matrices.
     * @param list list of int[][] matrices
     * @return a new list where each matrix is a deep copy*/
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}
