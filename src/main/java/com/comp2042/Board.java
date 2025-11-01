package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public interface Board {//all methods are abstract cause interface, need to be used in another class

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

    NextShapeInfo getNextShapeInfo();


}
//Defines how brick interacts with board, and Gameplay loop


