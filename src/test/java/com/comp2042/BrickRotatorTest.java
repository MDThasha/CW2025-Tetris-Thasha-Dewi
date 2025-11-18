package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickRotator;
import com.comp2042.logic.bricks.IBrick;
import com.comp2042.logic.bricks.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {
    private BrickRotator rotator;
    private Brick testBrick;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        testBrick = new IBrick();
        rotator.setBrick(testBrick);
    }

    @Test
    void testSetBrick() {
        rotator.setBrick(new IBrick());

        int[][] currentShape = rotator.getCurrentShape();
        assertNotNull(currentShape, "Current shape should not be null");
    }

    @Test
    void testGetCurrentShape() {
        int[][] shape = rotator.getCurrentShape();
        assertNotNull(shape, "Current shape should not be null");
        assertTrue(shape.length > 0, "Current shape should have rows");
    }

    @Test
    void testGetNextShape() {
        NextShapeInfo nextShape = rotator.getNextShape();
        assertNotNull(nextShape, "Next shape should not be null");
        assertNotNull(nextShape.getShape(), "Next shape matrix should not be null");
    }

    @Test
    void testSetCurrentShapeChangesPosition() {
        rotator.setCurrentShape(0);

        NextShapeInfo nextShape = rotator.getNextShape();
        assertEquals(1, nextShape.getPosition(), "Next position should be 1 after setting to 0");
    }

    @Test
    void testGetNextShapeDoesNotChangePosition() {
        rotator.setCurrentShape(0);

        // Call getNextShape multiple times
        rotator.getNextShape();
        rotator.getNextShape();

        // Next shape should still show position 1
        NextShapeInfo nextShape = rotator.getNextShape();
        assertEquals(1, nextShape.getPosition(), "Position should still be 1");
    }

    @Test
    void testRotationCycle() {
        int numRotations = testBrick.getShapeMatrix().size();

        // Test each position
        for (int i = 0; i < numRotations; i++) {
            rotator.setCurrentShape(i);
            NextShapeInfo nextShape = rotator.getNextShape();

            int expectedNext = (i + 1) % numRotations;
            assertEquals(expectedNext, nextShape.getPosition(),
                    "Wrong next position");
        }
    }

    @Test
    void testRotationWrapsAround() {
        int numRotations = testBrick.getShapeMatrix().size();

        // Set to last position
        rotator.setCurrentShape(numRotations - 1);

        // Next should be 0
        NextShapeInfo nextShape = rotator.getNextShape();
        assertEquals(0, nextShape.getPosition(), "Should wrap back to 0");
    }

    @Test
    void testSetBrickResetsToZero() {
        rotator.setCurrentShape(2);
        rotator.setBrick(new IBrick());

        // After setting new brick, next should be position 1 (meaning current is 0)
        NextShapeInfo nextShape = rotator.getNextShape();
        assertEquals(1, nextShape.getPosition(), "Should reset to position 0");
    }
}