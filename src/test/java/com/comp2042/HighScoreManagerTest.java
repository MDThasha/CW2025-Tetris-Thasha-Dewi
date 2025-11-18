package com.comp2042;

import com.comp2042.Event.GameMode;
import com.comp2042.PlayerData.HighScoreManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {
    private static final String CLASSIC_FILE = "highscores_classic.txt";
    private static final String TIME_LIMIT_FILE = "highscores_timelimit.txt";
    private static final String SAME_BLOCK_FILE = "highscores_sameshape.txt";

    @BeforeEach
    void setUp() {
        deleteTestFiles();
    }

    @AfterEach
    void tearDown() {
        deleteTestFiles();
    }

    private void deleteTestFiles() {
        new File(CLASSIC_FILE).delete();
        new File(TIME_LIMIT_FILE).delete();
        new File(SAME_BLOCK_FILE).delete();
    }

    @Test
    void testGetTopScoresWhenEmpty() {
        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertNotNull(scores, "Score list should not be null");
        assertTrue(scores.isEmpty(), "Score list should be empty when no scores exist");
    }

    @Test
    void testAddSingleScore() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(1, scores.size(), "Should have 1 score");
        assertEquals("A", scores.get(0).name(), "Name should be A");
        assertEquals(1000, scores.get(0).score(), "Score should be 1000");
    }

    @Test
    void testAddMultipleScores() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("B", 1500, GameMode.CLASSIC);
        HighScoreManager.addScore("C", 800, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(3, scores.size(), "Should have 3 scores");
    }

    @Test
    void testScoresAreSortedDescending() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("B", 1500, GameMode.CLASSIC);
        HighScoreManager.addScore("C", 800, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals("B", scores.get(0).name(), "Highest score should be first");
        assertEquals(1500, scores.get(0).score(), "Highest score should be 1500");
        assertEquals("A", scores.get(1).name(), "Second highest should be A");
        assertEquals("C", scores.get(2).name(), "Lowest score should be last");
    }

    @Test
    void testMaxEntriesLimit() {
        // Add 15 scores
        for (int i = 1; i <= 15; i++) {
            HighScoreManager.addScore("Player" + i, i * 100, GameMode.CLASSIC);
        }

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(10, scores.size(), "Should only keep top 10 scores");
    }

    @Test
    void testGetHighScoreWhenEmpty() {
        int highScore = HighScoreManager.getHighScore(GameMode.CLASSIC);
        assertEquals(0, highScore, "High score should be 0 when no scores exist");
    }

    @Test
    void testGetHighScore() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("B", 1500, GameMode.CLASSIC);
        HighScoreManager.addScore("C", 800, GameMode.CLASSIC);

        int highScore = HighScoreManager.getHighScore(GameMode.CLASSIC);
        assertEquals(1500, highScore, "High score should be 1500");
    }

    @Test
    void testDifferentGameModes() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("B", 2000, GameMode.TIME_LIMIT);
        HighScoreManager.addScore("C", 3000, GameMode.ALL_SAME_BLOCK);

        assertEquals(1000, HighScoreManager.getHighScore(GameMode.CLASSIC), "Classic high score should be 1000");
        assertEquals(2000, HighScoreManager.getHighScore(GameMode.TIME_LIMIT), "Time limit high score should be 2000");
        assertEquals(3000, HighScoreManager.getHighScore(GameMode.ALL_SAME_BLOCK), "Same block high score should be 3000");
    }

    @Test
    void testGameModesAreIndependent() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> classicScores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        List<HighScoreManager.ScoreEntry> timeLimitScores = HighScoreManager.getTopScores(GameMode.TIME_LIMIT);

        assertEquals(1, classicScores.size(), "Classic should have 1 score");
        assertEquals(0, timeLimitScores.size(), "Time limit should have 0 scores");
    }

    @Test
    void testAddScoreWithNullName() {
        HighScoreManager.addScore(null, 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(1, scores.size(), "Should have 1 score");
        assertEquals("Unknown", scores.get(0).name(), "Null name should become 'Unknown'");
    }

    @Test
    void testAddScoreWithEmptyName() {
        HighScoreManager.addScore("", 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(1, scores.size(), "Should have 1 score");
        assertEquals("Unknown", scores.get(0).name(), "Empty name should become 'Unknown'");
    }

    @Test
    void testAddScoreWithWhitespaceName() {
        HighScoreManager.addScore("   ", 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(1, scores.size(), "Should have 1 score");
        assertEquals("Unknown", scores.get(0).name(), "Whitespace name should become 'Unknown'");
    }

    @Test
    void testAddZeroScore() {
        HighScoreManager.addScore("A", 0, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(1, scores.size(), "Should have 1 score");
        assertEquals(0, scores.get(0).score(), "Score should be 0");
    }

    @Test
    void testDuplicateScores() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("B", 1000, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(2, scores.size(), "Should have 2 scores");
        assertEquals(1000, scores.get(0).score(), "First score should be 1000");
        assertEquals(1000, scores.get(1).score(), "Second score should be 1000");
    }

    @Test
    void testSamePlayerMultipleScores() {
        HighScoreManager.addScore("A", 1000, GameMode.CLASSIC);
        HighScoreManager.addScore("A", 1500, GameMode.CLASSIC);
        HighScoreManager.addScore("A", 800, GameMode.CLASSIC);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(GameMode.CLASSIC);
        assertEquals(3, scores.size(), "Should have 3 scores");
        assertEquals("A", scores.get(0).name(), "Highest should be A");
        assertEquals(1500, scores.get(0).score(), "Highest score should be 1500");
    }
}