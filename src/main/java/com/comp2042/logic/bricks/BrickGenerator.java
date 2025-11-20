package com.comp2042.logic.bricks;

/** Produces bricks for the game.
 * provide a stream of {@link Brick} instances used by the board.*/
public interface BrickGenerator {

    /** Return the next brick and advance the internal generator state.
     * @return the next {@link Brick} to be placed on the board*/
    Brick getBrick();

    /** Peek at the upcoming brick without advancing the generator.
     * @return the next {@link Brick} that {@link #getBrick()} would return, or null if not available*/
    Brick getNextBrick();
}