package com.comp2042;

import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;

public class MenuController {

    private static MenuController instance;

    @FXML private Button startBtn;                                  // Button to start the game
    @FXML private Button quitBtn;                                   // Button to quit the application
    @FXML public StackPane rootPane;                                // Root container of the menu scene
    @FXML private TextField playerNameField;                        // Player name text field

    // Returns the singleton instance of this controller
    public static MenuController getInstance() {
        return instance;   // Assign this for global access
    }

    @FXML public void initialize() {
        instance = this;
        startBtn.setOnAction(e -> {                       // Start the game
            String playerName = getPlayerName();                     // Grab from textfield
            Main.loadGame(playerName);                               // pass it to the game
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

    public String getPlayerName() {                         // Get Player name from text box
        String name = playerNameField.getText().trim();
        return name.isEmpty() ? "Unknown" : name;           // Default to "Unknown" if field is empty
    }
}

