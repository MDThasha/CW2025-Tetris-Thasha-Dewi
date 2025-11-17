package com.comp2042;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public final class Score {
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    public IntegerProperty scoreProperty() {
        return score;
    }
    public void add(int i){
        score.setValue(score.getValue() + i);
    }        // Add Value to current score
    public void reset() {
        score.setValue(0);
    }                              // reset score to 0
}


