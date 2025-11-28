package com.comp2042.Controllers;

import com.comp2042.Event.KeyBindings;
import com.comp2042.Helper.UIHover;
import com.comp2042.Main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import java.util.Set;

/** This is the File Used to connect the controlsLayout.fxml, which is the Controls page in the game, to the game
 * Manages the UI buttons that display and let the player rebind keys for game actions.*/
public class ControlsController {

    /** Buttons used in the controls UI. Each button shows the current keybinding and can be clicked to rebind its action.*/
    @FXML private Button moveLeftBtn, moveRightBtn, rotateBtn, moveDownBtn, hardDropBtn, restartBtn, pauseBtn, mainMenuBtn, holdBtn, swapBtn, back;

    /** The KeyBindings model containing the current key mappings for all actions.*/
    private KeyBindings keyBindings;

    /** Set the KeyBindings model used by this controller.
     * @param bindings the KeyBindings instance to use for displaying and updating bindings*/
    public void setKeyBindings(KeyBindings bindings) { this.keyBindings = bindings; }

    /** Populate and initialize all control buttons with their current key names.
     * Adds event handlers so clicking a button will enter key-capture mode for rebinding.*/
    public void setupButtons() {
        if (keyBindings == null) {
            keyBindings = new KeyBindings();
        }

        setupButton(moveLeftBtn, "Move Left", keyBindings.getMoveLeft());
        setupButton(moveRightBtn, "Move Right", keyBindings.getMoveRight());
        setupButton(rotateBtn, "Rotate", keyBindings.getRotate());
        setupButton(moveDownBtn, "Down", keyBindings.getMoveDown());
        setupButton(hardDropBtn, "Drop", keyBindings.getHardDrop());
        setupButton(restartBtn, "Restart", keyBindings.getRestart());
        setupButton(pauseBtn, "Pause", keyBindings.getPause());
        setupButton(mainMenuBtn, "Main Menu", keyBindings.getMainMenu());
        setupButton(holdBtn, "Hold", keyBindings.getHold());
        setupButton(swapBtn, "Swap", keyBindings.getSwap());
    }

    /** Configure a single Button to display its action name and capture a new keybinding when clicked.
     *
     * <p>updates the button label, installs a temporary key event filter on the scene
     * to capture the next pressed key, validates that the key is not already used, and updates
     * the provided key set. it also ignores the functions for esc, space,
     * tab and arrow keys are pressed so they can be added as keybinds</p>
     *
     * @param btn the Button to configure
     * @param actionName a readable name for the action (e.g., "Move Left")
     * @param keySet the Set<KeyCode> where the selected binding is stored */
    private void setupButton(Button btn, String actionName, Set<KeyCode> keySet) {
        updateButtonText(btn, actionName, keySet);

        btn.setOnAction(e -> {
            btn.setText(actionName + " - Press a key...");
            if (!btn.getStyleClass().contains("key-capturing")) {
                btn.getStyleClass().add("key-capturing");
            }

            final javafx.event.EventHandler<KeyEvent>[] filterHolder = new javafx.event.EventHandler[1];

            javafx.event.EventHandler<KeyEvent> keyFilter = (KeyEvent keyEvent) -> {
                KeyCode newKey = keyEvent.getCode();

                // Cancel capture if ESC or undefined (treat ESC as cancel)
                if (newKey == KeyCode.ESCAPE || newKey == KeyCode.UNDEFINED) {
                    Platform.runLater(() -> {
                        updateButtonText(btn, actionName, keySet);
                        btn.getStyleClass().remove("key-capturing");
                    });
                    btn.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, filterHolder[0]);
                    keyEvent.consume();
                    return;
                }

                // Check if key already used
                if (isKeyUsedElsewhere(newKey, keySet)) {
                    Platform.runLater(() -> {
                        btn.setText(actionName + " - Key already used!");
                        btn.setStyle("-fx-background-color: #333; -fx-text-fill: red;");
                    });

                    // Reset after 1 second (on FX thread)
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                        Platform.runLater(() -> {
                            updateButtonText(btn, actionName, keySet);
                            btn.getStyleClass().remove("key-capturing");
                            btn.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
                        });
                    }).start();
                } else {
                    // Accept the new key
                    keySet.clear();
                    keySet.add(newKey);
                    Platform.runLater(() -> {
                        updateButtonText(btn, actionName, keySet);
                        btn.getStyleClass().remove("key-capturing");
                    });
                }

                // Remove filter and consume the event
                btn.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, filterHolder[0]);
                keyEvent.consume();
            };

            filterHolder[0] = keyFilter;
            // Add event filter to capture the next key press
            btn.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyFilter);
        });
    }

    /** to start UI effects */
    @FXML
    public void initialize() {
        setupButtons();

        // Add smooth hover to each button
        UIHover.apply(true, "control-button", moveLeftBtn, moveRightBtn, rotateBtn, moveDownBtn, hardDropBtn, holdBtn, swapBtn, restartBtn, pauseBtn, mainMenuBtn, back);

    }

    // Update UI
    /** Update the button's label to show the action name and currently bound key.
     *
     * @param btn the button to update
     * @param actionName readable action name
     * @param keySet the set containing the currently bound KeyCode (maybe empty)*/
    private void updateButtonText(Button btn, String actionName, Set<KeyCode> keySet) {
        if (keySet.isEmpty()) {
            btn.setText(actionName + " - ?");
        } else {
            btn.setText(actionName + " - " + keySet.iterator().next().getName());
        }
    }

    /** Check whether the given key is already bound to any other action.
     *
     * @param key the KeyCode to check
     * @param currentSet the key set for the action being changed (excluded from the check)
     * @return true if the key is used by another binding, false otherwise*/
    private boolean isKeyUsedElsewhere(KeyCode key, Set<KeyCode> currentSet) {
        // Check if key is used in any other binding
        return (keyBindings.getMoveLeft() != currentSet && keyBindings.getMoveLeft().contains(key)) ||
                (keyBindings.getMoveRight() != currentSet && keyBindings.getMoveRight().contains(key)) ||
                (keyBindings.getRotate() != currentSet && keyBindings.getRotate().contains(key)) ||
                (keyBindings.getMoveDown() != currentSet && keyBindings.getMoveDown().contains(key)) ||
                (keyBindings.getHardDrop() != currentSet && keyBindings.getHardDrop().contains(key)) ||
                (keyBindings.getRestart() != currentSet && keyBindings.getRestart().contains(key)) ||
                (keyBindings.getPause() != currentSet && keyBindings.getPause().contains(key)) ||
                (keyBindings.getMainMenu() != currentSet && keyBindings.getMainMenu().contains(key)) ||
                (keyBindings.getHold() != currentSet && keyBindings.getHold().contains(key)) ||
                (keyBindings.getSwap() != currentSet && keyBindings.getSwap().contains(key));
    }

    /** Handler for the "Back" action in the controls UI.
     * Updates in-game control labels if a game is active and returns to the main menu.*/
    @FXML public void goBack() {
        // Update the game's control labels if game is running
        if (GuiController.currentController != null) {
            GuiController.currentController.updateControlLabels();
        } Main.loadMenu();
    }

}