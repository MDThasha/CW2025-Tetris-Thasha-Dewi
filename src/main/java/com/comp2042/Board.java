package com.comp2042;

public interface Board {//all methods are abstract cause interface, need to be used in another class

    boolean moveBrickDown();
    boolean moveBrickLeft();
    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();
//CAN USE FOR UI
    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();
//CAN USE FOR UI
    void newGame();
}
//Defines how brick interacts with board, and Gameplay loop