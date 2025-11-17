package com.comp2042.logic.bricks;

import java.util.List;

public interface Brick {
    List<int[][]> getShapeMatrix();
}
// Any class that Implements Brick will need to getShapeMatrix