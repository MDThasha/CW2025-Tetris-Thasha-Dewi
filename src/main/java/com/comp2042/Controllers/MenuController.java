package com.comp2042.Controllers;

import com.comp2042.Event.KeyBindings;
import com.comp2042.Helper.PlayerUtils;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MenuController {

    @FXML private Button startBtn;                                  // Button to start the game
    @FXML private Button quitBtn;                                   // Button to quit the application
    @FXML public StackPane rootPane;                                // Root container of the menu scene
    @FXML private TextField playerNameField;                        // Player name text field

    private static MenuController instance;
    public static MenuController getInstance() { return instance; }

    // PLAYER NAME
    private String playerName;
    public void setPlayerName(String name) { this.playerName = name; }
    public String getPlayerName() {
        // If field is empty, fallback to stored playerName, otherwise validate and return
        String fieldName = playerNameField.getText().trim();
        if (!fieldName.isEmpty()) return PlayerUtils.validatePlayerName(fieldName);
        if (playerName != null && !playerName.isEmpty()) return PlayerUtils.validatePlayerName(playerName);
        return "Unknown";
    }

    @FXML public void initialize() {
        instance = this;
        startBtn.setOnAction(e -> {
            String playerName = PlayerUtils.validatePlayerName(playerNameField.getText().trim());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("selectMode.fxml"));
                Parent modeRoot = loader.load(); // Load FXML

                // Get the controller of the loaded FXML
                ModeSelectController modeController = loader.getController();
                modeController.setPlayerName(playerName); // Pass the player name

                rootPane.getScene().setRoot(modeRoot); // Swap scene root
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        quitBtn.setOnAction(e -> System.exit(0));  // Quit the application
    }

    @FXML public void openControls() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("controlsLayout.fxml"));
            Parent controlsRoot = loader.load();

            ControlsController controller = loader.getController();

            KeyBindings bindings;
            if (GuiController.currentController != null) {
                bindings = GuiController.currentController.getKeyBindings();
            } else {
                bindings = KeyBindings.getInstance();
            }

            controller.setKeyBindings(bindings);
            controller.setupButtons();

            rootPane.getScene().setRoot(controlsRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void openLeaderBoard() { // Open the leaderboard menu
        try { Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("leaderBoard.fxml"));
            rootPane.getScene().setRoot(root); }
        catch (Exception e) {e.printStackTrace(); }
    }
}

