package com.comp2042.logic.bricks;

public interface BrickGenerator {
    Brick getBrick();
    Brick getNextBrick();
} // Defines how game creates and manages Bricks