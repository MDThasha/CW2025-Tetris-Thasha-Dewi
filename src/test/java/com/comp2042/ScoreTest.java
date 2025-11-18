package com.comp2042;

import com.comp2042.PlayerData.Score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {
    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testInitialScore() {
        assertEquals(0, score.getScore(), "Initial score should be 0");
    }

    @Test
    void testAdd() {
        score.add(100);
        assertEquals(100, score.getScore(), "Score should be 100 after adding 100");
    }

    @Test
    void testAddMultiple() {
        score.add(50);
        score.add(75);
        score.add(25);
        assertEquals(150, score.getScore(), "Score should be 150 after adding 50, 75, and 25");
    }

    @Test
    void testReset() {
        score.add(500);
        score.reset();
        assertEquals(0, score.getScore(), "Score should be 0 after reset");
    }

    @Test
    void testMultipleResets() {
        score.add(100);
        score.reset();
        score.add(200);
        score.reset();
        assertEquals(0, score.getScore(), "Score should be 0 after multiple resets");
    }

    @Test
    void testScoreProperty() {
        assertNotNull(score.scoreProperty(), "Score property should not be null");
        assertEquals(0, score.scoreProperty().get(), "Score property initial value should be 0");
    }

    @Test
    void testScorePropertyUpdates() {
        score.add(250);
        assertEquals(250, score.scoreProperty().get(), "Score property should reflect current score");
    }

    @Test
    void testLargeScoreValues() {
        score.add(1000000);
        assertEquals(1000000, score.getScore(), "Score should handle large values");
    }
}