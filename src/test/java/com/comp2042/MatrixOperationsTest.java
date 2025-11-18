package com.comp2042;

import com.comp2042.GameBoard.MatrixOperations;
import com.comp2042.GameBoard.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {
    @Test
    void testIntersectWithFilledCell() {
        int[][] matrix = new int[10][20];
        matrix[5][5] = 1;
        int[][] brick = {{1, 1}, {1, 1}};

        boolean intersects = MatrixOperations.intersect(matrix, brick, 4, 4);
        assertTrue(intersects, "Should detect intersection with filled cell");
    }

    @Test
    void testIntersectOutOfBoundsLeft() {
        int[][] matrix = new int[10][20];
        int[][] brick = {{1, 1}, {1, 1}};

        boolean intersects = MatrixOperations.intersect(matrix, brick, -1, 5);
        assertTrue(intersects, "Should detect out of bounds on left");
    }

    @Test
    void testIntersectOutOfBoundsRight() {
        int[][] matrix = new int[10][20];
        int[][] brick = {{1, 1}, {1, 1}};

        boolean intersects = MatrixOperations.intersect(matrix, brick, 19, 5);
        assertTrue(intersects, "Should detect out of bounds on right");
    }

    @Test
    void testIntersectOutOfBoundsBottom() {
        int[][] matrix = new int[10][20];
        int[][] brick = {{1, 1}, {1, 1}};

        boolean intersects = MatrixOperations.intersect(matrix, brick, 5, 9);
        assertTrue(intersects, "Should detect out of bounds on bottom");
    }

    @Test
    void testIntersectValidPlacement() {
        int[][] matrix = new int[10][20];
        int[][] brick = {{1, 1}, {1, 1}};

        boolean intersects = MatrixOperations.intersect(matrix, brick, 4, 4);
        assertFalse(intersects, "Should allow valid placement");
    }

    @Test
    void testGetNewMatrix() {
        int[][] matrix = new int[20][10];
        ClearRow clearRow = new ClearRow(1, matrix, 50);

        assertNotNull(clearRow.getNewMatrix(), "New matrix should not be null");
        assertEquals(20, clearRow.getNewMatrix().length, "New matrix should have 20 rows");
    }

    @Test
    void testNoLinesCleared() {
        int[][] matrix = new int[20][10];
        ClearRow clearRow = new ClearRow(0, matrix, 0);

        assertEquals(0, clearRow.getLinesRemoved(), "Should return 0 lines removed");
        assertEquals(0, clearRow.getScoreBonus(), "Should return 0 score bonus when no lines cleared");
    }

    @Test
    void testScoreBonusCalculation() {
        int[][] matrix = new int[20][10];

        // 1 line = 50 * 1 * 1 = 50
        ClearRow clear1 = new ClearRow(1, matrix, 50);
        assertEquals(50, clear1.getScoreBonus(), "1 line should give 50 points");

        // 2 lines = 50 * 2 * 2 = 200
        ClearRow clear2 = new ClearRow(2, matrix, 200);
        assertEquals(200, clear2.getScoreBonus(), "2 lines should give 200 points");

        // 4 lines = 50 * 4 * 4 = 800
        ClearRow clear4 = new ClearRow(4, matrix, 800);
        assertEquals(800, clear4.getScoreBonus(), "4 lines should give 800 points");
    }
}