package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameOverPanel extends StackPane {
    public final Label gameOverLabel, scoreLabelGO, highScoreLabelGO, RnMMLabelGO;
    private final Rectangle overlay;
    @FXML private Rectangle blackScreen;

    public GameOverPanel() {
        // Transparent overlay
        overlay = new Rectangle();overlay.setFill(Color.BLACK);
        overlay.setOpacity(0); // start fully transparent
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());

        // Game over label
        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle1"); // start with red
        gameOverLabel.setAlignment(Pos.CENTER);
        StackPane.setAlignment(gameOverLabel, Pos.CENTER);

        // Score label
        scoreLabelGO = new Label();
        scoreLabelGO.getStyleClass().add("gameOverStyle2");
        scoreLabelGO.setAlignment(Pos.CENTER);
        scoreLabelGO.setOpacity(0);
        scoreLabelGO.setTranslateY(-150);
        StackPane.setAlignment(scoreLabelGO, Pos.CENTER);

        // High score label
        highScoreLabelGO = new Label();
        highScoreLabelGO.getStyleClass().add("gameOverStyle2");
        highScoreLabelGO.setAlignment(Pos.CENTER);
        highScoreLabelGO.setOpacity(0);
        highScoreLabelGO.setTranslateY(-100);
        StackPane.setAlignment(highScoreLabelGO, Pos.CENTER);

        // Restart and main menu label
        RnMMLabelGO = new Label("PRESS N TO RESTART, ESC FOR MAIN MENU");
        RnMMLabelGO.getStyleClass().add("gameOverStyle3");
        RnMMLabelGO.setAlignment(Pos.CENTER);
        RnMMLabelGO.setOpacity(0);
        RnMMLabelGO.setTranslateY(-50);
        StackPane.setAlignment(RnMMLabelGO, Pos.CENTER);

        // Add overlay and all labels to panel
        getChildren().addAll(overlay, gameOverLabel, scoreLabelGO, highScoreLabelGO, RnMMLabelGO);

        // Hide by default
        setVisible(false);
    }

    public void showGameOver(int score, int highScore) {
        setVisible(true);
        // Fade in dark overlay
        FadeTransition fade = new FadeTransition(Duration.seconds(1), overlay);
        fade.setFromValue(0.0);
        fade.setToValue(0.7);
        fade.play();

        // Make sure start with original GameOver Label Style
        gameOverLabel.getStyleClass().remove("gameOverStyleWhite");
        if (!gameOverLabel.getStyleClass().contains("gameOverStyle1")) {
            gameOverLabel.getStyleClass().add("gameOverStyle1");
        }

        // Update score labels
        scoreLabelGO.setText("SCORE: " + score);
        highScoreLabelGO.setText("HIGHSCORE: " + highScore);

        scoreLabelGO.setOpacity(0);
        highScoreLabelGO.setOpacity(0);
        RnMMLabelGO.setOpacity(0);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> {
            // Change label to white
            gameOverLabel.getStyleClass().remove("gameOverStyle1");
            gameOverLabel.getStyleClass().add("gameOverStyleWhite");

            // Move label up
            TranslateTransition moveUp = new TranslateTransition(Duration.seconds(2), gameOverLabel);
            moveUp.setFromY(0);
            moveUp.setToY(-225);
            moveUp.play();

            // Fade in score
            FadeTransition fadeScore = new FadeTransition(Duration.seconds(1.5), scoreLabelGO);
            fadeScore.setFromValue(0);
            fadeScore.setToValue(1);
            fadeScore.setDelay(Duration.seconds(1.5));
            fadeScore.play();

            // Fade in high score
            FadeTransition fadeHigh = new FadeTransition(Duration.seconds(1.5), highScoreLabelGO);
            fadeHigh.setFromValue(0);
            fadeHigh.setToValue(1);
            fadeHigh.setDelay(Duration.seconds(2));
            fadeHigh.play();

            // Fade in Restart and main menu stuff
            FadeTransition fadeRnMM = new FadeTransition(Duration.seconds(1.5), RnMMLabelGO);
            fadeRnMM.setFromValue(0);
            fadeRnMM.setToValue(1);
            fadeRnMM.setDelay(Duration.seconds(2.5));
            fadeRnMM.play();

        });
        pause.play();
    }

    public void reset() {
        // Reset label position
        gameOverLabel.setTranslateY(0);

        // Reset style to red
        gameOverLabel.getStyleClass().remove("gameOverStyleWhite");
        if (!gameOverLabel.getStyleClass().contains("gameOverStyle1")) {
            gameOverLabel.getStyleClass().add("gameOverStyle1");
        }

        // Hide panel and overlay
        overlay.setFill(Color.rgb(0, 0, 0, 0));
        setVisible(false);
    }
}
