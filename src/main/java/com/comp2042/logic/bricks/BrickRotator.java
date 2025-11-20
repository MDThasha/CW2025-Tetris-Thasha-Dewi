package com.comp2042.logic.bricks;

/** manages the current brick and its rotation index. */
public class BrickRotator {

    /** The brick whose rotation states are being managed. */
    private Brick brick;

    /** The currently selected rotation within the brick's shape list. */
    private int currentShape = 0;

    /** Returns information about the next rotation state without applying it.
     * <p> The returned NextShapeInfo contains the shape matrix for the next rotation
     * and the rotation index that would become current if applied. </p>
     * @return NextShapeInfo containing the next rotation matrix and its index */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size(); // Increment by 1 to get next rotation
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /** Get the currently selected rotation matrix for the managed brick. */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**Set the current rotation index.
     * @param currentShape the index into the brick's rotation list to use as current*/
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**Assign a brick to this rotator and reset the rotation index to the default (0).
     * @param brick the Brick instance to manage*/
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
}