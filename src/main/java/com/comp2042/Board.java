package com.comp2042;

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

    // FOR NEXT SHAPE
    NextShapeInfo getNextShapeInfo();

    // FOR HARD DROP
    boolean hardDrop();

    // FOR HOLD AND SWAPPING
    boolean holdBrick();
    boolean swapBrick();
    NextShapeInfo getHeldBrickInfo();
    boolean canHoldOrSwap();
}