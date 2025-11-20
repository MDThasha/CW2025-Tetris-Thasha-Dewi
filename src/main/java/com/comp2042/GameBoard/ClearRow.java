package com.comp2042.GameBoard;

/** for rows cleared from the board.
 * <p> how many lines were removed, the new board matrix after the clear,
 * and the score bonus awarded for the clear.</p>*/
public final class ClearRow {
    /** lines removed and score bonus*/
    private final int linesRemoved, scoreBonus;

    /** new matrix*/
    private final int[][] newMatrix;

    /**Create a ClearRow result.
     * @param linesRemoved number of lines removed by the clear
     * @param newMatrix the resulting board matrix after rows were removed
     * @param scoreBonus the score bonus awarded for this clear*/
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /** gte how many lines cleared
     * @return the number of lines removed */
    public int getLinesRemoved() { return linesRemoved; }

    /**Return a defensive copy of the resulting board matrix.
     * @return a copy of the new board matrix after the clear*/
    public int[][] getNewMatrix() { return MatrixOperations.copy(newMatrix); }

    /** gets score bonus
     * @return the score bonus awarded for the cleared rows */
    public int getScoreBonus() {
        return scoreBonus;
    }
}