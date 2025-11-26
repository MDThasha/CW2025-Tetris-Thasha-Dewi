package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.RandomBrickGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RandomBrickGeneratorTest {
    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    @Test
    void testGetBrick() {
        Brick brick = generator.getBrick();
        assertNotNull(brick, "Generated brick should not be null");
    }

    @Test
    void testGetBrickReturnsValidBrick() {
        Brick brick = generator.getBrick();
        assertNotNull(brick.getShapeMatrix(), "Brick should have shape matrix");
        assertFalse(brick.getShapeMatrix().isEmpty(), "Shape matrix should not be empty");
    }

    @Test
    void testMultipleBricksGenerated() {
        Brick brick1 = generator.getBrick();
        Brick brick2 = generator.getBrick();
        Brick brick3 = generator.getBrick();

        assertNotNull(brick1);
        assertNotNull(brick2);
        assertNotNull(brick3);
    }

    @Test
    void testGetRandomBrickClass() {
        Class<? extends Brick> brickClass = generator.getRandomBrickClass();
        assertNotNull(brickClass, "Random brick class should not be null");
    }

    @Test
    void testRandomnessOfBricks() {
        // Generate 20 bricks and check if we get different types
        boolean foundDifferent = false;
        Brick firstBrick = generator.getBrick();

        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            if (!brick.getClass().equals(firstBrick.getClass())) {
                foundDifferent = true;
                break;
            }
        }

        assertTrue(foundDifferent, "Should generate different brick types");
    }
}