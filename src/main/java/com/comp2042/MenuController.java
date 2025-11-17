package com.comp2042;

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

    // Returns the singleton instance of this controller
    private static MenuController instance;
    public static MenuController getInstance() { return instance; }


    private String playerName;
    public void setPlayerName(String name) { this.playerName = name; }
    public String getPlayerName() {
        // If field is empty, fallback to stored playerName, otherwise "Unknown"
        String fieldName = playerNameField.getText().trim();
        if (!fieldName.isEmpty()) return fieldName;
        if (playerName != null && !playerName.isEmpty()) return playerName;
        return "Unknown";
    }

    @FXML public void initialize() {
        instance = this;
        startBtn.setOnAction(e -> {
            String playerName = playerNameField.getText().trim();

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

    @FXML public void openControls() {                   // Open the controls menu
        try { Parent controlsRoot = FXMLLoader.load(getClass().getClassLoader().getResource("controlsLayout.fxml"));
            rootPane.getScene().setRoot(controlsRoot); } // swap root, keep same scene
        catch (Exception e) { e.printStackTrace(); }     // Print any loading errors
    }

    @FXML public void openLeaderBoard() { // Open the leaderboard menu
        try { Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("leaderBoard.fxml"));
            rootPane.getScene().setRoot(root); }
        catch (Exception e) {e.printStackTrace(); }
    }
}

