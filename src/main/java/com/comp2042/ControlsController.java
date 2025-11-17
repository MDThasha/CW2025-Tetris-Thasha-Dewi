package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Set;

public class ControlsController {

    @FXML private Button moveLeftBtn, moveRightBtn, rotateBtn, moveDownBtn,
                         hardDropBtn, restartBtn, pauseBtn, mainMenuBtn, holdBtn, swapBtn;

    @FXML public void initialize() { }

    private KeyBindings keyBindings;
    public void setKeyBindings(KeyBindings bindings) { this.keyBindings = bindings; }

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

    private void setupButton(Button btn, String actionName, Set<KeyCode> keySet) {
        updateButtonText(btn, actionName, keySet);

        btn.setOnAction(e -> {
            btn.setText(actionName + " - Press a key...");
            btn.setStyle("-fx-background-color: #555; -fx-text-fill: yellow;");

            // Filter
            final javafx.event.EventHandler<KeyEvent>[] filterHolder = new javafx.event.EventHandler[1];

            javafx.event.EventHandler<KeyEvent> keyFilter = (KeyEvent keyEvent) -> {
                KeyCode newKey = keyEvent.getCode();

                // Ignore ESC to cancel, so can set as a keybind
                if (newKey == KeyCode.ESCAPE || newKey == KeyCode.UNDEFINED) {
                    updateButtonText(btn, actionName, keySet);
                    btn.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
                    btn.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, filterHolder[0]);
                    keyEvent.consume();
                    return;
                }

                // Check if key is already used
                if (isKeyUsedElsewhere(newKey, keySet)) {
                    btn.setText(actionName + " - Key already used!");
                    btn.setStyle("-fx-background-color: #333; -fx-text-fill: red;");

                    // Reset after 1 second
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                updateButtonText(btn, actionName, keySet);
                                btn.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
                            });
                        } catch (InterruptedException ex) {}
                    }).start();
                }

                // New key add
                else {
                    keySet.clear();
                    keySet.add(newKey);
                    updateButtonText(btn, actionName, keySet);
                    btn.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
                }

                // Remove the filter
                btn.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, filterHolder[0]);
                keyEvent.consume();
            };

            filterHolder[0] = keyFilter;

            // Add event filter to capture keys BEFORE they're consumed by the scene
            btn.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyFilter);
        });
    }

    // Update UI
    private void updateButtonText(Button btn, String actionName, Set<KeyCode> keySet) {
        if (keySet.isEmpty()) {
            btn.setText(actionName + " - ?");
        } else {
            btn.setText(actionName + " - " + keySet.iterator().next().getName());
        }
    }

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

    @FXML public void goBack() {
        // Update the game's control labels if game is running
        if (GuiController.currentController != null) {
            GuiController.currentController.updateControlLabels();
        } Main.loadMenu();
    }
}