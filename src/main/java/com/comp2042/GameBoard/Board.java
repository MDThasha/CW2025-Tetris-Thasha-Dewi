package com.comp2042.GameBoard;

import com.comp2042.logic.bricks.NextShapeInfo;
import com.comp2042.PlayerData.Score;

public interface Board {

    boolean moveBrickDown();
    boolean moveBrickLeft();
    boolean moveBrickRight();
    boolean rotateLeftBrick();
    boolean createNewBrick();
    int[][] getBoardMatrix();
    ViewData getViewData();
    void mergeBrickToBackground();
    ClearRow clearRows();
    Score getScore();
    void newGame();

    /** FOR NEXT SHAPE */ //test comment for javadoc
    NextShapeInfo getNextShapeInfo();

    // FOR HARD DROP
    boolean hardDrop();

    // FOR HOLD AND SWAPPING
    boolean holdBrick();
    boolean swapBrick();
    NextShapeInfo getHeldBrickInfo();
    boolean canHoldOrSwap();
}