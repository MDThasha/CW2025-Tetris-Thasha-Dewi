package com.comp2042.Controllers;

import com.comp2042.Event.GameMode;
import com.comp2042.Main;
import com.comp2042.PlayerData.HighScoreManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class LeaderBoardController {

    @FXML private VBox leaderboardContainer;

    @FXML private void showClassic() { showLeaderboardForMode(GameMode.CLASSIC, "CLASSIC MODE"); }
    @FXML private void showTimer() { showLeaderboardForMode(GameMode.TIME_LIMIT, "TIMER MODE"); }
    @FXML private void showAllSame() { showLeaderboardForMode(GameMode.ALL_SAME_BLOCK, "ALL SAME BLOCK"); }

    private void showLeaderboardForMode(GameMode mode, String titleText) {
        leaderboardContainer.getChildren().clear();

        Label modeTitle = new Label(titleText);
        modeTitle.setStyle("-fx-font-size: 32; -fx-text-fill: yellow; -fx-font-weight: bold;");
        leaderboardContainer.getChildren().add(modeTitle);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(mode);

        if (scores.isEmpty()) {
            Label empty = new Label("No scores yet");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
            leaderboardContainer.getChildren().add(empty);
            return;
        }

        int rank = 1;
        for (HighScoreManager.ScoreEntry entry : scores) {
            Label scoreLabel = new Label(rank + ". " + entry.name() + " - " + entry.score());
            scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
            leaderboardContainer.getChildren().add(scoreLabel);
            rank++;
        }
    }

    @FXML
    private void backToMenu() {
        Main.loadMenu();
    }
}
