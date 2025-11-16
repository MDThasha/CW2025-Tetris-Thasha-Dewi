package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class LeaderBoardController {

    @FXML private VBox leaderboardContainer;  // Container VBox in FXML where score entries will be displayed

    @FXML public void initialize() {
        List<HighScoreManager.ScoreEntry> topScores = HighScoreManager.getTopScores();            // Get the top scores from HighScoreManager
        leaderboardContainer.getChildren().clear();                                               // Clear old scores

        // Add the scores dynamically
        for (int i = 0; i < topScores.size(); i++) {
            HighScoreManager.ScoreEntry entry = topScores.get(i);
            Label lbl = new Label((i + 1) + ". " + entry.name() + " - " + entry.score());
            lbl.setStyle("-fx-font-size: 24; -fx-text-fill: white;");
            leaderboardContainer.getChildren().add(lbl);
        }
    }

    @FXML public void backToMenu() {
        Main.loadMenu();
    }
}
