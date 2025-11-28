package com.comp2042.Helper;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;
import java.util.Objects;

/** Helper to add hover/scale behaviour and style-class for Buttons. */
public final class UIHover {

    private UIHover() {} // no instances

    /** Ensure a style class exists on the button (safe: ignores null). */
    public static void ensureStyleClass(Button b, String className) {
        if (b == null || className == null) return;
        if (!b.getStyleClass().contains(className)) {
            b.getStyleClass().add(className);
        }
    }

    /** Add smooth hover scale animation to a single button. */
    public static void addSmoothHover(Button b) {
        if (b == null) return;
        // Create transitions once and reuse them for this button
        ScaleTransition enter = new ScaleTransition(Duration.millis(120), b);
        enter.setToX(1.03);
        enter.setToY(1.03);

        ScaleTransition exit = new ScaleTransition(Duration.millis(120), b);
        exit.setToX(1.0);
        exit.setToY(1.0);

        b.setOnMouseEntered(e -> { exit.stop(); enter.playFromStart(); });
        b.setOnMouseExited(e -> { enter.stop(); exit.playFromStart(); });
    }

    /** Convenience: apply hover and style class to many buttons at once. */
    public static void apply(boolean ensureClass, String className, Button... buttons) {
        if (buttons == null) return;
        for (Button b : buttons) {
            if (b == null) continue;
            if (ensureClass && className != null) ensureStyleClass(b, className);
            addSmoothHover(b);
        }
    }

    /** Convenience overload: apply only hover to many buttons. */
    public static void apply(Button... buttons) {
        apply(false, null, buttons);
    }
}