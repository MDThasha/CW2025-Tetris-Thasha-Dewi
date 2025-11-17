package com.comp2042;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ModeSelectController {
    @FXML private Button classicBtn;
    @FXML private Button timeBtn;
    @FXML private Button sameBtn;
    @FXML private Button backBtn;
    @FXML private StackPane rootPane;

    private String playerName;

    public void setPlayerName(String name) {
        this.playerName = (name == null || name.isEmpty()) ? "Unknown" : name;
    }

    @FXML
    public void initialize() {
        classicBtn.setOnAction(e -> startGame(GameMode.CLASSIC));
        timeBtn.setOnAction(e -> startGame(GameMode.TIME_LIMIT));
        sameBtn.setOnAction(e -> startGame(GameMode.ALL_SAME_BLOCK));
        backBtn.setOnAction(e -> Main.loadMenu());
    }

    private void startGame(GameMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();

            GuiController gui = loader.getController();
            gui.setGameMode(mode);  // set the mode first
            GameController controller = new GameController(gui, this.playerName, mode);

            // Immediately start the timer if mode is TIME_LIMIT
            Platform.runLater(() -> {
                if (mode == GameMode.TIME_LIMIT) {
                    gui.startCountDownTimer(120); // countdown for Time Limit
                } else {
                    gui.startTimer(); // count-up for other modes
                }
            });

            Main.getScene().setRoot(gameRoot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
