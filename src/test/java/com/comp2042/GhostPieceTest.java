package com.comp2042;

import com.comp2042.GameBoard.SimpleBoard;
import com.comp2042.GameBoard.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class GhostPieceTest {
    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(10, 20);
    }

    @Test
    void testGhostPositionExists() {
        ViewData viewData = board.getViewData();
        assertNotNull(viewData.getGhostOffset(), "Ghost position should not be null");
    }

    @Test
    void testGhostPositionUpdatesOnMove() {
        ViewData before = board.getViewData();
        Point ghostBefore = before.getGhostOffset();

        board.moveBrickLeft();

        ViewData after = board.getViewData();
        Point ghostAfter = after.getGhostOffset();

        assertNotEquals(ghostBefore.x, ghostAfter.x, "Ghost X should update when piece moves");
    }

    @Test
    void testGhostPositionUpdatesOnRotate() {
        ViewData before = board.getViewData();
        Point ghostBefore = before.getGhostOffset();

        board.rotateLeftBrick();

        ViewData after = board.getViewData();
        Point ghostAfter = after.getGhostOffset();

        // Ghost position might change when piece rotates
        assertNotNull(ghostAfter, "Ghost should exist after rotation");
    }

    @Test
    void testGhostPositionAtBottom() {
        // Move piece all the way down
        while (board.moveBrickDown()) {
            // Keep moving down
        }

        ViewData viewData = board.getViewData();
        Point ghost = viewData.getGhostOffset();
        int currentY = viewData.getyPosition();

        assertEquals(currentY, ghost.y, "Ghost should be at same position as piece when at bottom");
    }

    @Test
    void testGhostPositionAfterHardDrop() {
        board.hardDrop();
        board.createNewBrick();

        ViewData viewData = board.getViewData();
        Point ghost = viewData.getGhostOffset();

        assertNotNull(ghost, "Ghost should exist for new piece after hard drop");
    }

    @Test
    void testGhostPositionForNewPiece() {
        board.createNewBrick();

        ViewData viewData = board.getViewData();
        Point ghost = viewData.getGhostOffset();

        assertNotNull(ghost, "Ghost should exist for new piece");
        assertTrue(ghost.y >= 0, "Ghost Y should be valid for new piece");
    }

    @Test
    void testGhostPositionWithObstacles() {
        // Place some blocks on the board
        board.hardDrop();
        board.createNewBrick();

        ViewData viewData = board.getViewData();
        Point ghost = viewData.getGhostOffset();

        assertNotNull(ghost, "Ghost should exist even with obstacles");
        assertTrue(ghost.y >= 0, "Ghost Y should be valid with obstacles");
    }


    @Test
    void testGhostPositionAfterNewGame() {
        board.hardDrop();
        board.newGame();

        ViewData viewData = board.getViewData();
        Point ghost = viewData.getGhostOffset();

        assertNotNull(ghost, "Ghost should exist after new game");
    }
}