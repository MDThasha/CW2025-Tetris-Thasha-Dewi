package com.comp2042.Event;

import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/** Used for updating and saving keybinds to controls*/
public class KeyBindings {

    /** make instance*/
    private static KeyBindings instance;

    /** get instance cant be null
     * @return instance*/
    public static KeyBindings getInstance() {
        if (instance == null) {
            instance = new KeyBindings();
        }
        return instance;
    }

    /** modifiable key for Left move*/
    private Set<KeyCode> moveLeft = new HashSet<>();
    /** modifiable key for Right move*/
    private Set<KeyCode> moveRight = new HashSet<>();
    /** modifiable key for Rotate*/
    private Set<KeyCode> rotate = new HashSet<>();
    /** modifiable key for Down move*/
    private Set<KeyCode> moveDown = new HashSet<>();
    /** modifiable key for Drop*/
    private Set<KeyCode> hardDrop = new HashSet<>();
    /** modifiable key for Restart*/
    private Set<KeyCode> restart = new HashSet<>();
    /** modifiable key for Pause*/
    private Set<KeyCode> pause = new HashSet<>();
    /** modifiable key for Mainmenu*/
    private Set<KeyCode> mainMenu = new HashSet<>();
    /** modifiable key for Hold*/
    private Set<KeyCode> hold = new HashSet<>();
    /** modifiable key for Swap*/
    private Set<KeyCode> swap = new HashSet<>();

    /** holds default bindings*/
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

        hold.add(KeyCode.C);
        swap.add(KeyCode.V);
    }

    /** getter for left*/
    public Set<KeyCode> getMoveLeft() { return moveLeft; }
    /** getter for right*/
    public Set<KeyCode> getMoveRight() { return moveRight; }
    /** getter for rotate*/
    public Set<KeyCode> getRotate() { return rotate; }
    /** getter for down*/
    public Set<KeyCode> getMoveDown() { return moveDown; }
    /** getter for drop*/
    public Set<KeyCode> getHardDrop() { return hardDrop; }
    /** getter for restart*/
    public Set<KeyCode> getRestart() { return restart; }
    /** getter for pause*/
    public Set<KeyCode> getPause() { return pause; }
    /** getter for mainmenu */
    public Set<KeyCode> getMainMenu() { return mainMenu; }
    /** getter for hold*/
    public Set<KeyCode> getHold() { return hold; }
    /** getter for swap*/
    public Set<KeyCode> getSwap() { return swap; }
}