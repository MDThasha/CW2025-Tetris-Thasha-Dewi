package com.comp2042.GameBoard;

import com.comp2042.logic.bricks.NextShapeInfo;
import com.comp2042.PlayerData.Score;

/** Updates game board model
 * <p>Provides operations to move and rotate the active brick, manage the board matrix,
 * detect/clear completed rows, and access score and preview/hold information.</p>*/
public interface Board {

    /**Attempt to move the active brick one cell down.
     * @return true if the brick moved down successfully; false if it could not (collision or landed) */
    boolean moveBrickDown();

    /**Attempt to move the active brick one cell left.
     * @return true if the brick moved left successfully; false if it could not (collision or landed) */
    boolean moveBrickLeft();

    /**Attempt to move the active brick one cell right.
     * @return true if the brick moved right successfully; false if it could not (collision or landed) */
    boolean moveBrickRight();

    /**Attempt to move the active brick one rotate.
     * @return true if the brick rotate successfully; false if it could not (collision or landed) */
    boolean rotateLeftBrick();

    /**Create and place a new active brick on the board (called when previous brick is merged).
     * @return true if a new brick was created successfully; false if no space (game over) */
    boolean createNewBrick();

    /**Return the internal board matrix representing static/placed blocks and empty cells.
     * @return a 2D int array representing board cell values */
    int[][] getBoardMatrix();

    /**Return a ViewData snapshot describing the currently falling brick (positions and cell data).
     * @return the ViewData for the active brick */
    ViewData getViewData();

    /**Merge the active brick into the static background (called when the brick lands).
     * <p>After this call, the board matrix should contain the brick's cells as part of the background.</p>*/
    void mergeBrickToBackground();

    /**Detect and clear any completed rows on the board.
     * @return a ClearRow object describing which rows were removed and any scoring info, or null if none*/
    ClearRow clearRows();

    /**Access the board's Score object which tracks the player's current score.
     * @return the Score instance for this board*/
    Score getScore();

    /**Reset the board state to start a new game */
    void newGame();

    /**Return information about the next shape that will be spawned (used for preview UI).
     * @return NextShapeInfo for the upcoming brick*/
    NextShapeInfo getNextShapeInfo();

    /**Perform a hard drop: move the active brick straight down to its resting position immediately.
     * @return true if the hard drop placed the brick (and may trigger line clears); false otherwise*/
    boolean hardDrop();

    /** Hold the current active brick (store it to the hold slot) and fetch the next brick as active.
     * @return true if hold succeeded; false if holding is not allowed at this time*/
    boolean holdBrick();

    /**Swap the held brick with the current active brick.
     * @return true if the swap succeeded; false if swapping is not allowed*/
    boolean swapBrick();

    /**Return preview information for the currently held brick (if any).
     * @return NextShapeInfo for the held brick, or null if none held*/
    NextShapeInfo getHeldBrickInfo();

    /**Check whether hold or swap operations are currently allowed (some games restrict frequency).
     *@return true if hold/swap is permitted; false otherwise*/
    boolean canHoldOrSwap();
}