package com.comp2042.Event;

import com.comp2042.GameBoard.DownData;
import com.comp2042.GameBoard.ViewData;

/** Listener for event types, how the game reacts to brick moving events*/
public interface InputEventListener {
    /** Handle a single-step downward move initiated by the given event.
     * @param event the MoveEvent describing the source and type of the action
     * @return a DownData object containing the updated ViewData and any ClearRow info (may be null)*/
    DownData onDownEvent(MoveEvent event);

    /** Handle a left-move action initiated by the given event.
     * @param event the MoveEvent describing the source and type of the action
     * @return a ViewData snapshot after attempting the left move (position/shape may be unchanged on collision)*/
    ViewData onLeftEvent(MoveEvent event);

    /** Handle a right-move action initiated by the given event.
     * @param event the MoveEvent describing the source and type of the action
     * @return a ViewData snapshot after attempting the right move (position/shape may be unchanged on collision)*/
    ViewData onRightEvent(MoveEvent event);

    /** Handle a rotate (typically left/counter-clockwise) action initiated by the given event.
     * @param event the MoveEvent describing the source and type of the action
     * @return a ViewData snapshot after attempting the rotation (rotation may be rejected if it collides)*/
    ViewData onRotateEvent(MoveEvent event);

    /** Handle a hard-drop action: drop the active brick to its resting position and lock it into the board.
     * @param event the MoveEvent describing the source and type of the action
     * @return a DownData object containing the final ViewData and any ClearRow info produced by the hard drop*/
    DownData onHardDropEvent(MoveEvent event);

    /** Handle a hold action: place the current brick into the hold slot (or swap with held brick if present).
     * @param event the MoveEvent describing the source and type of the action
     * @return a ViewData snapshot after the hold operation (may show a newly active brick or unchanged state)*/
    ViewData onHoldEvent(MoveEvent event);

    /** Handle an explicit swap action between the held brick and the current active brick.
     * @param event the MoveEvent describing the source and type of the action
     * @return a ViewData snapshot after the swap operation (may be identical to onHoldEvent behavior if no held brick)*/
    ViewData onSwapEvent(MoveEvent event);

    /** Creates new game*/
    void createNewGame();
}
