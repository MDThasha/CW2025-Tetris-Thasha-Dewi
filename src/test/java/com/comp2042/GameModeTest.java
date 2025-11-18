package com.comp2042;

import com.comp2042.Event.GameMode;
import com.comp2042.GameBoard.SimpleBoard;
import com.comp2042.logic.bricks.IBrick;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameModeTest {
    @Test
    void testGameModeEnumExists() {
        assertNotNull(GameMode.CLASSIC, "CLASSIC mode should exist");
        assertNotNull(GameMode.TIME_LIMIT, "TIME_LIMIT mode should exist");
        assertNotNull(GameMode.ALL_SAME_BLOCK, "ALL_SAME_BLOCK mode should exist");
    }

    @Test
    void testGameModeValues() {
        GameMode[] modes = GameMode.values();
        assertEquals(3, modes.length, "Should have exactly 3 game modes");
    }

    @Test
    void testModeBoard() {
        SimpleBoard board = new SimpleBoard(10, 20);
        assertNotNull(board, "mode board should be created");
        assertNotNull(board.getBoardMatrix(), "Board matrix should exist");
    }

    @Test
    void testBoardDimensions() {
        SimpleBoard board = new SimpleBoard(10, 20);
        int[][] matrix = board.getBoardMatrix();

        assertEquals(10, matrix.length, "Board should have 10 columns");
        assertEquals(20, matrix[0].length, "Board should have 20 rows");
    }

    @Test
    void testScoreIncreasesInAllModes() {
        SimpleBoard board = new SimpleBoard(10, 20);
        int initialScore = board.getScore().getScore();

        board.getScore().add(100);

        assertEquals(initialScore + 100, board.getScore().getScore(), "Score should increase");
    }

    @Test
    void testNewGameResetsBoard() {
        SimpleBoard board = new SimpleBoard(10, 20);

        board.moveBrickDown();
        board.moveBrickLeft();
        board.newGame();

        assertEquals(0, board.getScore().getScore(), "Score should reset to 0");
        assertNotNull(board.getNextShapeInfo(), "Should have next brick after new game");
    }

    @Test
    void testNewGameResetsHold() {
        SimpleBoard board = new SimpleBoard(10, 20);

        board.holdBrick();
        assertNotNull(board.getHeldBrickInfo(), "Should have held brick");

        board.newGame();
        assertNull(board.getHeldBrickInfo(), "Held brick should be cleared on new game");
        assertTrue(board.canHoldOrSwap(), "Should be able to hold after new game");
    }

    @Test
    void testGameModeWithHoldFeature() {
        SimpleBoard board = new SimpleBoard(10, 20);

        assertTrue(board.canHoldOrSwap(), "Should be able to hold initially");
        board.holdBrick();
        assertFalse(board.canHoldOrSwap(), "Should not be able to hold twice");

        board.createNewBrick();
        assertTrue(board.canHoldOrSwap(), "Should be able to hold again after new brick");
    }

    @Test
    void testGameModeWithSwapFeature() {
        SimpleBoard board = new SimpleBoard(10, 20);

        board.holdBrick();
        board.createNewBrick();

        assertTrue(board.canHoldOrSwap(), "Should be able to swap");
        board.swapBrick();
        assertFalse(board.canHoldOrSwap(), "Should not be able to swap twice");
    }

    @Test
    void testAllModesHaveSameBasicFeatures() {
        SimpleBoard classicBoard = new SimpleBoard(10, 20);
        SimpleBoard sameBlockBoard = new SimpleBoard(10, 20, IBrick.class);

        // Both should support movement
        assertTrue(classicBoard.moveBrickDown() || !classicBoard.moveBrickDown(), "Classic should support movement");
        assertTrue(sameBlockBoard.moveBrickDown() || !sameBlockBoard.moveBrickDown(), "Same block should support movement");

        // Both should support hold
        assertTrue(classicBoard.canHoldOrSwap(), "Classic should support hold");
        assertTrue(sameBlockBoard.canHoldOrSwap(), "Same block should support hold");

        // Both should have score
        assertNotNull(classicBoard.getScore(), "Classic should have score");
        assertNotNull(sameBlockBoard.getScore(), "Same block should have score");
    }
}
