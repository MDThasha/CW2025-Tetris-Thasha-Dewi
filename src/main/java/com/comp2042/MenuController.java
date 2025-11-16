package com.comp2042;

import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;

public class MenuController {

    @FXML private Button startBtn;                                  // Button to start the game
    @FXML private Button quitBtn;                                   // Button to quit the application
    @FXML public void initialize() {                                // Root pane for swapping scenes
        startBtn.setOnAction(e -> Main.loadGame());      // Load the game scene
        quitBtn.setOnAction(e -> System.exit(0)); // Quit the application
    }
    @FXML public StackPane rootPane;
    @FXML public void openControls() {   // Open the controls menu
        try {
            Parent controlsRoot = FXMLLoader.load(
                    getClass().getClassLoader().getResource("controlsLayout.fxml"));
            rootPane.getScene().setRoot(controlsRoot); // swap root, keep same scene
        } catch (Exception e) {
            e.printStackTrace(); // Print any loading errors
        }
    }
}
