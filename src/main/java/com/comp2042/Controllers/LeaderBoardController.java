package com.comp2042.Controllers;

import com.comp2042.Event.GameMode;
import com.comp2042.Helper.UIHover;
import com.comp2042.Main;
import com.comp2042.Managers.HighScoreManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.util.List;
/**Controller for the leaderboard UI (leaderBoard.fxml).
 * Displays top 10 scores for different game modes and allows returning to the main menu.*/
public class LeaderBoardController {

    /** Container VBox that holds the leaderboard title and score entries (injected from FXML).*/
    @FXML private VBox leaderboardContainer;

    /** Mode buttons.*/
    @FXML private Button classicBtn, timerBtn, allSameBtn, backBtn;

    /** Show scores for Classic mode.
     * Bound to the "Classic" button in the leaderboard FXML.*/
    @FXML private void showClassic() { showLeaderboardForMode(GameMode.CLASSIC, "CLASSIC MODE"); }
    /** Show scores for Time Limit mode.
     * Bound to the "Time Limit" button in the leaderboard FXML.*/
    @FXML private void showTimer() { showLeaderboardForMode(GameMode.TIME_LIMIT, "TIMER MODE"); }
    /** Show scores for All Same Block mode.
     * Bound to the "All Same Block" button in the leaderboard FXML.*/
    @FXML private void showAllSame() { showLeaderboardForMode(GameMode.ALL_SAME_BLOCK, "ALL SAME BLOCK"); }


    /** Populate the leaderboardContainer with the top scores for the given mode.
     * @param mode the GameMode to display scores for
     * @param titleText the title text to display for this leaderboard view
     * <p>Clears previous entries, adds a title label, and then adds labels for each score entry.
     * If there are no scores, displays a "No scores yet" message.</p>*/
    private void showLeaderboardForMode(GameMode mode, String titleText) {
        leaderboardContainer.getChildren().clear();

        Label modeTitle = new Label(titleText);
        if (!modeTitle.getStyleClass().contains("text1")) {
            modeTitle.getStyleClass().add("text1");
        }
        leaderboardContainer.getChildren().add(modeTitle);

        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores(mode);

        if (scores.isEmpty()) {
            Label empty = new Label("No scores yet");
            if (!empty.getStyleClass().contains("text3")) {
                empty.getStyleClass().add("text3");
            }
            leaderboardContainer.getChildren().add(empty);
            return;
        }

        int rank = 1;
        for (HighScoreManager.ScoreEntry entry : scores) {
            Label scoreLabel = new Label(rank + ". " + entry.name() + " - " + entry.score());
            if (!scoreLabel.getStyleClass().contains("text3")) {
                scoreLabel.getStyleClass().add("text3");
            }
            leaderboardContainer.getChildren().add(scoreLabel);
            rank++;
        }
    }

    /**Initialize button hovers and smooth animation*/
    @FXML
    public void initialize() {
        // Add smooth hover/scale to mode buttons and back button
        UIHover.apply(true,"mode-button",classicBtn, timerBtn, allSameBtn, backBtn);
    }

    /**Return to the main menu.
     * Bound to the "Back" button in the leaderboard UI. Calls {@code Main.loadMenu()}.*/
    @FXML
    private void backToMenu() {
        Main.loadMenu();
    }
}
