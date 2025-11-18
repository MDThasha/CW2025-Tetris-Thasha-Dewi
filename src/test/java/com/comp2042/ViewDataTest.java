package com.comp2042;

import com.comp2042.GameBoard.ViewData;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class ViewDataTest {
    @Test
    void testGetCurrentShape() {
        int[][] shape = {{1, 1}, {1, 1}};
        int[][] nextShape = {{0, 1}, {1, 1}};
        Point ghost = new Point(5, 10);

        ViewData viewData = new ViewData(shape, 5, 2, nextShape, ghost);
        assertArrayEquals(shape, viewData.getBrickData());
    }

    @Test
    void testGetX() {
        int[][] shape = {{1, 1}, {1, 1}};
        int[][] nextShape = {{0, 1}, {1, 1}};
        Point ghost = new Point(5, 10);

        ViewData viewData = new ViewData(shape, 5, 2, nextShape, ghost);
        assertEquals(5, viewData.getxPosition());
    }

    @Test
    void testGetY() {
        int[][] shape = {{1, 1}, {1, 1}};
        int[][] nextShape = {{0, 1}, {1, 1}};
        Point ghost = new Point(5, 10);

        ViewData viewData = new ViewData(shape, 5, 2, nextShape, ghost);
        assertEquals(2, viewData.getyPosition());
    }

    @Test
    void testgetGhostOffset() {
        int[][] shape = {{1, 1}, {1, 1}};
        int[][] nextShape = {{0, 1}, {1, 1}};
        Point ghost = new Point(5, 10);

        ViewData viewData = new ViewData(shape, 5, 2, nextShape, ghost);
        assertEquals(ghost, viewData.getGhostOffset());
    }
}