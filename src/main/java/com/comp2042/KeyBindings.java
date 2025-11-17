package com.comp2042;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

public class KeyBindings {

    private static KeyBindings instance;

    public static KeyBindings getInstance() {
        if (instance == null) {
            instance = new KeyBindings();
        }
        return instance;
    }

    private Set<KeyCode> moveLeft = new HashSet<>();
    private Set<KeyCode> moveRight = new HashSet<>();
    private Set<KeyCode> rotate = new HashSet<>();
    private Set<KeyCode> moveDown = new HashSet<>();
    private Set<KeyCode> hardDrop = new HashSet<>();
    private Set<KeyCode> restart = new HashSet<>();
    private Set<KeyCode> pause = new HashSet<>();
    private Set<KeyCode> mainMenu = new HashSet<>();

    public KeyBindings() {
        // Default bindings
        moveLeft.add(KeyCode.LEFT);
        moveRight.add(KeyCode.RIGHT);
        rotate.add(KeyCode.UP);
        moveDown.add(KeyCode.DOWN);

        hardDrop.add(KeyCode.SPACE);
        restart.add(KeyCode.N);
        pause.add(KeyCode.TAB);
        mainMenu.add(KeyCode.ESCAPE);
    }

    public Set<KeyCode> getMoveLeft() { return moveLeft; }
    public Set<KeyCode> getMoveRight() { return moveRight; }
    public Set<KeyCode> getRotate() { return rotate; }
    public Set<KeyCode> getMoveDown() { return moveDown; }
    public Set<KeyCode> getHardDrop() { return hardDrop; }
    public Set<KeyCode> getRestart() { return restart; }
    public Set<KeyCode> getPause() { return pause; }
    public Set<KeyCode> getMainMenu() { return mainMenu; }
}