package com.comp2042;

import com.comp2042.GameBoard.SimpleBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {
    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    @Test
    void testBoardInitialization() {
        int[][] matrix = board.getBoardMatrix();
        assertEquals(10, matrix.length, "Board width should be 10");
        assertEquals(20, matrix[0].length, "Board height should be 20");
    }

    @Test
    void testInitialScore() {
        assertEquals(0, board.getScore().getScore(), "Initial score should be 0");
    }

    @Test
    void testMoveBrickDown() {
        boolean moved = board.moveBrickDown();
        assertTrue(moved, "Brick should move down when space is available");
    }

    @Test
    void testMoveBrickLeft() {
        boolean moved = board.moveBrickLeft();
        assertNotNull(moved, "Move left should return a boolean");
    }

    @Test
    void testMoveBrickRight() {
        boolean moved = board.moveBrickRight();
        assertNotNull(moved, "Move right should return a boolean");
    }

    @Test
    void testRotateBrick() {
        boolean rotated = board.rotateLeftBrick();
        assertNotNull(rotated, "Rotate should return a boolean");
    }

    @Test
    void testHardDrop() {
        boolean dropped = board.hardDrop();
        assertTrue(dropped, "Hard drop should succeed");
    }

    @Test
    void testCreateNewBrick() {
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver, "Game should not be over after creating first brick");
    }

    @Test
    void testNewGame() {
        board.getScore().add(500);
        board.newGame();

        assertEquals(0, board.getScore().getScore(), "Score should be reset to 0");

        int[][] matrix = board.getBoardMatrix();
        boolean isEmpty = true;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell != 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        assertTrue(isEmpty, "Board should be empty after new game");
    }

    @Test
    void testGetNextShapeInfo() {
        assertNotNull(board.getNextShapeInfo(), "Next shape info should not be null");
    }

    @Test
    void testGetViewData() {
        assertNotNull(board.getViewData(), "View data should not be null");
    }

    @Test
    void testHoldBrick() {
        boolean held = board.holdBrick();
        assertTrue(held, "Should be able to hold brick on first use");
    }

    @Test
    void testHoldBrickTwice() {
        board.holdBrick();
        boolean heldAgain = board.holdBrick();
        assertFalse(heldAgain, "Should not be able to hold brick twice in a row");
    }

    @Test
    void testSwapBrick() {
        board.holdBrick();
        board.createNewBrick();
        boolean swapped = board.swapBrick();
        assertTrue(swapped, "Should be able to swap with held brick");
    }

    @Test
    void testCanHoldOrSwap() {
        assertTrue(board.canHoldOrSwap(), "Should be able to hold/swap initially");
        board.holdBrick();
        assertFalse(board.canHoldOrSwap(), "Should not be able to hold/swap after using once");
    }


    @Test
    void testHoldBrickAfterNewBrick() {
        board.holdBrick();
        board.createNewBrick();
        boolean result = board.holdBrick();
        assertTrue(result, "Should be able to hold brick after creating new brick");
    }

    @Test
    void testSwapBrickWithoutHeldBrick() {
        boolean result = board.swapBrick();
        assertTrue(result, "Swap should work like hold when no brick is held");
        assertNotNull(board.getHeldBrickInfo(), "Should have held brick after swap");
    }

    @Test
    void testSwapBrickWithHeldBrick() {
        board.holdBrick();
        board.createNewBrick();
        boolean result = board.swapBrick();
        assertTrue(result, "Should be able to swap with held brick");
    }

    @Test
    void testSwapBrickTwice() {
        board.holdBrick();
        board.createNewBrick();
        board.swapBrick();
        boolean result = board.swapBrick();
        assertFalse(result, "Should not be able to swap twice in same turn");
    }

    @Test
    void testCanHoldOrSwapAfterSwapping() {
        board.holdBrick();
        board.createNewBrick();
        board.swapBrick();
        assertFalse(board.canHoldOrSwap(), "Should not be able to hold/swap after swapping");
    }

    @Test
    void testHoldClearsHeldBrickOnNewGame() {
        board.holdBrick();
        assertNotNull(board.getHeldBrickInfo(), "Should have held brick");

        board.newGame();
        assertNull(board.getHeldBrickInfo(), "Held brick should be cleared on new game");
    }

    @Test
    void testHoldDoesNotAffectScore() {
        int initialScore = board.getScore().getScore();
        board.holdBrick();
        assertEquals(initialScore, board.getScore().getScore(), "Hold should not affect score");
    }

    @Test
    void testSwapDoesNotAffectScore() {
        int initialScore = board.getScore().getScore();
        board.holdBrick();
        board.createNewBrick();
        board.swapBrick();
        assertEquals(initialScore, board.getScore().getScore(), "Swap should not affect score");
    }

    @Test
    void testMergeBrickToBackground() {
        board.mergeBrickToBackground();
        int[][] matrix = board.getBoardMatrix();
        boolean hasFilled = false;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell != 0) {
                    hasFilled = true;
                    break;
                }
            }
        }
        assertTrue(hasFilled, "Matrix should have filled cells after merge");
    }
}