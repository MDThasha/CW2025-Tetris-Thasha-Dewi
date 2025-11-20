package com.comp2042.PlayerData;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

/** Wrapper around an IntegerProperty used to track and update the player's score. */
public final class Score {
    /** JavaFX property storing the current score.*/
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /** Return the score property so UI controls can bind to it.*/
    public IntegerProperty scoreProperty() {return score; }

    /** Add the provided value to the current score.
     * @param i amount to add (may be zero or positive)*/
    public void add(int i){ score.setValue(score.getValue() + i); }

    /** Reset the score back to zero. */
    public void reset() { score.setValue(0);}

    /** Return the current score as a primitive int. */
    public int getScore() { return score.getValue(); }
}


