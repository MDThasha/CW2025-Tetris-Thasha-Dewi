package com.comp2042.Controllers;

import com.comp2042.Event.KeyBindings;
import com.comp2042.Helper.PlayerUtils;
import com.comp2042.Managers.AudioManager;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;

import java.io.IOException;

/** Controller for the main menu UI (mainMenu.fxml).
 * <p>Handles starting the game, opening control and leaderboard screens, and managing the
 * player's display name.</p> */
public class MenuController {

    /** Button to start the game*/
    @FXML private Button startBtn;

    /** Button to quit the application*/
    @FXML private Button quitBtn;

    /** Root container of the menu scene*/
    @FXML public StackPane rootPane;

    /** Player name text field*/
    @FXML private TextField playerNameField;

    /** Singleton-like instance reference for easy access from other controllers.*/
    private static MenuController instance;

    /** Singleton-like instance reference for easy access from other controllers.
     * @return instance*/
    public static MenuController getInstance() { return instance; }

    /** Stored player name*/
    private String playerName;

    /** Set Player name*/
    public void setPlayerName(String name) { this.playerName = name; }

    /** gets player name and If field is empty, fallback to stored playerName (UNKNOWN), otherwise validate and return*/
    public String getPlayerName() {
        String fieldName = playerNameField.getText().trim();
        if (!fieldName.isEmpty()) return PlayerUtils.validatePlayerName(fieldName);
        if (playerName != null && !playerName.isEmpty()) return PlayerUtils.validatePlayerName(playerName);
        return "Unknown";
    }

    /** JavaFX initialize called after FXML injection.
     * <p>Registers event handlers for the Start and Quit buttons, sets the static instance
     * reference, and passes the player name to the mode select controller when starting a game adn start audio*/
    @FXML public void initialize() {
        instance = this;
        volumeSlider.setValue(AudioManager.getVolume());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            AudioManager.setVolume(newValue.floatValue());
            AudioManager.setMusicVolume(newValue.floatValue() * 0.6f);
        });

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

    /** Open the controls configuration screen.
     * <p>Loads controlsLayout.fxml, injects the current KeyBindings (from the active GuiController
     * if available or defaults), calls setupButtons() on the ControlsController, and swaps the scene root.</p>
     * Exceptions are logged via stack trace if loading fails.*/
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

    /** Open the leaderboard screen.
     * Loads leaderBoard.fxml and swaps it into the scene root. Errors are printed to the console.*/
    @FXML public void openLeaderBoard() { // Open the leaderboard menu
        try { Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("leaderBoard.fxml"));
            rootPane.getScene().setRoot(root); }
        catch (Exception e) {e.printStackTrace(); }
    }
    //
    @FXML private Slider volumeSlider;


}

