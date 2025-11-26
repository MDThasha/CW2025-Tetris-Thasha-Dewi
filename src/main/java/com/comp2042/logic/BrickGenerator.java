package com.comp2042.logic;

import com.comp2042.logic.bricks.Brick;

/** Produces bricks for the game.
 * provide a stream of {@link Brick} instances used by the board.*/
public interface BrickGenerator {

    /** Return the next brick and advance the internal generator state.
     * @return the next {@link Brick} to be placed on the board*/
    Brick getBrick();

}