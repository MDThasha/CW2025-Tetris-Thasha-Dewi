package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameOverPanel extends StackPane {

    public final Label gameOverLabel, scoreLabelGO, highScoreLabelGO;
    private final Rectangle overlay;
    @FXML private Rectangle blackScreen;


    public GameOverPanel() {
        // Transparent overlay
        overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0)); // start fully transparent
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());

        // Game over label
        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle"); // start with red
        gameOverLabel.setAlignment(Pos.CENTER);
        StackPane.setAlignment(gameOverLabel, Pos.CENTER);

        // Score label
        scoreLabelGO = new Label();
        scoreLabelGO.getStyleClass().add("gameOverScoreStyle");
        scoreLabelGO.setAlignment(Pos.CENTER);
        scoreLabelGO.setOpacity(0);
        scoreLabelGO.setTranslateY(-150);
        StackPane.setAlignment(scoreLabelGO, Pos.CENTER);

        // High score label
        highScoreLabelGO = new Label();
        highScoreLabelGO.getStyleClass().add("gameOverScoreStyle");
        highScoreLabelGO.setAlignment(Pos.CENTER);
        highScoreLabelGO.setOpacity(0);
        highScoreLabelGO.setTranslateY(-100);
        StackPane.setAlignment(highScoreLabelGO, Pos.CENTER);

        // Add overlay and all labels to panel
        getChildren().addAll(overlay, gameOverLabel, scoreLabelGO, highScoreLabelGO);

        // Hide by default
        setVisible(false);
    }

    public void showGameOver(int score, int highScore) {
        setVisible(true);

        gameOverLabel.getStyleClass().remove("gameOverStyleWhite");
        if (!gameOverLabel.getStyleClass().contains("gameOverStyle")) {
            gameOverLabel.getStyleClass().add("gameOverStyle");
        }

        // Update score labels
        scoreLabelGO.setText("SCORE: " + score);
        highScoreLabelGO.setText("HIGHSCORE: " + highScore);

        scoreLabelGO.setOpacity(0);
        highScoreLabelGO.setOpacity(0);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> {
            // Change label to white
            gameOverLabel.getStyleClass().remove("gameOverStyle");
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

        });
        pause.play();
    }

    public void reset() {
        // Reset label position
        gameOverLabel.setTranslateY(0);

        // Reset style to red
        gameOverLabel.getStyleClass().remove("gameOverStyleWhite");
        if (!gameOverLabel.getStyleClass().contains("gameOverStyle")) {
            gameOverLabel.getStyleClass().add("gameOverStyle");
        }

        // Hide panel and overlay
        setVisible(false);

    }




}
