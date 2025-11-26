package com.comp2042;

import com.comp2042.GameBoard.SimpleBoard;
import com.comp2042.logic.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NextBrickPreviewTest {
    private SimpleBoard board;

    @BeforeEach
    void setUp() { // Create a new board before each test
        board = new SimpleBoard(25, 10);
    }

    @Test
    void TestNextBrickNotNull() {
        // Test that getNextShapeInfo returns a non-null shape
        NextShapeInfo nextShapeInfo = board.getNextShapeInfo();

        assertNotNull(nextShapeInfo, "Next shape info should not be null");
        assertNotNull(nextShapeInfo.getShape(), "Next shape should not be null");
    }

    @Test
    void TestNextBrickValidShape() {
        // Test that the next shape has valid dimensions
        NextShapeInfo nextShapeInfo = board.getNextShapeInfo();
        int[][] shape = nextShapeInfo.getShape();

        assertTrue(shape.length > 0, "Shape should have rows");
        assertTrue(shape[0].length > 0, "Shape should have columns");
    }

    @Test
    void TestNewNextBrick() {
        // Get the first next brick
        NextShapeInfo firstNext = board.getNextShapeInfo();
        int[][] firstShape = firstNext.getShape();

        // Create a new brick (this should move nextBrick to currentBrick)
        board.createNewBrick();

        // Get the new next brick
        NextShapeInfo secondNext = board.getNextShapeInfo();
        int[][] secondShape = secondNext.getShape();

        // The shapes should exist (they might be the same type by chance, so we just check they exist)
        assertNotNull(firstShape, "First next shape should not be null");
        assertNotNull(secondShape, "Second next shape should not be null");
    }

    @Test
    void TestRotationisDefault() {
        // Test that the default position is 0 (no rotation) so that when next brick enters game the rotation is constant
        NextShapeInfo nextShapeInfo = board.getNextShapeInfo();

        assertEquals(0, nextShapeInfo.getPosition(), "Default position should be 0");
    }

    @Test
    void TestNextBrickisBrick() {
        // Test that the shape contains at least one non-zero value (actual brick blocks)
        NextShapeInfo nextShapeInfo = board.getNextShapeInfo();
        int[][] shape = nextShapeInfo.getShape();

        boolean hasNonZero = false;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell != 0) {
                    hasNonZero = true;
                    break;
                }
            }
            if (hasNonZero) break;
        }

        assertTrue(hasNonZero, "Shape should contain at least one non-zero value");
    }

    @Test
    void testNextShapeInfoCopiesMatrix() {
        // Test that getShape() returns a copy, not the original
        NextShapeInfo nextShapeInfo = board.getNextShapeInfo();
        int[][] shape1 = nextShapeInfo.getShape();
        int[][] shape2 = nextShapeInfo.getShape();

        // They should have the same values but be different objects (defensive copy)
        assertNotSame(shape1, shape2, "getShape() should return a copy, not the same reference");
        assertArrayEquals(shape1, shape2, "Both copies should have the same values");
    }

    @Test
    void TestNextBrickisFixed() {
        // Test that calling getNextShapeInfo multiple times returns the same brick
        NextShapeInfo first = board.getNextShapeInfo();
        NextShapeInfo second = board.getNextShapeInfo();

        assertArrayEquals(first.getShape(), second.getShape(),
                "Multiple calls should return the same next brick");
    }
}