package com.comp2042;

public final class ClearRow {
    private final int linesRemoved, scoreBonus;
    private final int[][] newMatrix;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }
    public int getScoreBonus() {
        return scoreBonus;
    }
}