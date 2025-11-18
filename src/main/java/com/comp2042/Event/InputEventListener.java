package com.comp2042.Event;

import com.comp2042.GameBoard.DownData;
import com.comp2042.GameBoard.ViewData;

public interface InputEventListener {
    DownData onDownEvent(MoveEvent event);
    ViewData onLeftEvent(MoveEvent event);
    ViewData onRightEvent(MoveEvent event);
    ViewData onRotateEvent(MoveEvent event);
    void createNewGame();
    DownData onHardDropEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);
    ViewData onSwapEvent(MoveEvent event);
} // HOW GAME REACTS TO USER INPUT
