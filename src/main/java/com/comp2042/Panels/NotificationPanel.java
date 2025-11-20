package com.comp2042.Panels;

import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.collections.ObservableList;
import javafx.animation.FadeTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.event.EventHandler;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.scene.Node;

/** Panel used to display a short animated notification (e.g. bonus score). */
public class NotificationPanel extends BorderPane { // Animation and design of bonus score
    /** Create a NotificationPanel showing the text. */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);
    }

    /** Play the notification animation and remove this panel from the list when done.
     * @param list the ObservableList<Node> */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.seconds(2), this);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(2.5), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
