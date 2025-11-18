package com.comp2042.Controllers;

import com.comp2042.Event.GameMode;
import com.comp2042.Helper.PlayerUtils;
import com.comp2042.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ModeSelectController {
    @FXML private Button classicBtn;
    @FXML private Button timeBtn;
    @FXML private Button sameBtn;
    @FXML private Button backBtn;

    private String playerName;
    public void setPlayerName(String name) {
        this.playerName = PlayerUtils.validatePlayerName(name);
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
            System.out.println("Loading gameLayout.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();
            System.out.println("FXML loaded successfully!");

            GuiController gui = loader.getController();
            gui.setGameMode(mode);
            GameController controller = new GameController(gui, this.playerName, mode);

            Main.getScene().setRoot(gameRoot);
        }
        catch (Exception ex) { System.err.println("ERROR loading game:"); ex.printStackTrace(); }
    }
}
