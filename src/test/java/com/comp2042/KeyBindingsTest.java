package com.comp2042;

import com.comp2042.Event.KeyBindings;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class KeyBindingsTest {
    private KeyBindings keyBindings;

    @BeforeEach
    void setUp() {
        keyBindings = new KeyBindings();
    }

    @Test
    void testDefaultMoveLeftBinding() {
        Set<KeyCode> moveLeft = keyBindings.getMoveLeft();
        assertNotNull(moveLeft, "Move left binding should not be null");
        assertTrue(moveLeft.contains(KeyCode.LEFT), "Default move left should be LEFT arrow");
    }

    @Test
    void testDefaultMoveRightBinding() {
        Set<KeyCode> moveRight = keyBindings.getMoveRight();
        assertNotNull(moveRight, "Move right binding should not be null");
        assertTrue(moveRight.contains(KeyCode.RIGHT), "Default move right should be RIGHT arrow");
    }

    @Test
    void testDefaultRotateBinding() {
        Set<KeyCode> rotate = keyBindings.getRotate();
        assertNotNull(rotate, "Rotate binding should not be null");
        assertTrue(rotate.contains(KeyCode.UP), "Default rotate should be UP arrow");
    }

    @Test
    void testDefaultMoveDownBinding() {
        Set<KeyCode> moveDown = keyBindings.getMoveDown();
        assertNotNull(moveDown, "Move down binding should not be null");
        assertTrue(moveDown.contains(KeyCode.DOWN), "Default move down should be DOWN arrow");
    }

    @Test
    void testDefaultHardDropBinding() {
        Set<KeyCode> hardDrop = keyBindings.getHardDrop();
        assertNotNull(hardDrop, "Hard drop binding should not be null");
        assertTrue(hardDrop.contains(KeyCode.SPACE), "Default hard drop should be SPACE");
    }

    @Test
    void testDefaultRestartBinding() {
        Set<KeyCode> restart = keyBindings.getRestart();
        assertNotNull(restart, "Restart binding should not be null");
        assertTrue(restart.contains(KeyCode.N), "Default restart should be N");
    }

    @Test
    void testDefaultPauseBinding() {
        Set<KeyCode> pause = keyBindings.getPause();
        assertNotNull(pause, "Pause binding should not be null");
        assertTrue(pause.contains(KeyCode.TAB), "Default pause should be TAB");
    }

    @Test
    void testDefaultMainMenuBinding() {
        Set<KeyCode> mainMenu = keyBindings.getMainMenu();
        assertNotNull(mainMenu, "Main menu binding should not be null");
        assertTrue(mainMenu.contains(KeyCode.ESCAPE), "Default main menu should be ESCAPE");
    }

    @Test
    void testDefaultHoldBinding() {
        Set<KeyCode> hold = keyBindings.getHold();
        assertNotNull(hold, "Hold binding should not be null");
        assertTrue(hold.contains(KeyCode.C), "Default hold should be C");
    }

    @Test
    void testDefaultSwapBinding() {
        Set<KeyCode> swap = keyBindings.getSwap();
        assertNotNull(swap, "Swap binding should not be null");
        assertTrue(swap.contains(KeyCode.V), "Default swap should be V");
    }

    @Test
    void testChangeKeybinding() {
        Set<KeyCode> moveLeft = keyBindings.getMoveLeft();
        moveLeft.clear();
        moveLeft.add(KeyCode.A);

        assertTrue(moveLeft.contains(KeyCode.A), "Move left should now be A");
        assertFalse(moveLeft.contains(KeyCode.LEFT), "Move left should no longer be LEFT");
    }

    @Test
    void testSingletonPattern() {
        KeyBindings instance1 = KeyBindings.getInstance();
        KeyBindings instance2 = KeyBindings.getInstance();

        assertSame(instance1, instance2, "Should return the same instance");
    }

    @Test
    void testSingletonPersistsChanges() {
        KeyBindings instance1 = KeyBindings.getInstance();
        instance1.getMoveLeft().clear();
        instance1.getMoveLeft().add(KeyCode.A);

        KeyBindings instance2 = KeyBindings.getInstance();
        assertTrue(instance2.getMoveLeft().contains(KeyCode.A), "Changes should persist across getInstance calls");
    }

    @Test
    void testAllDefaultBindingsExist() {
        assertFalse(keyBindings.getMoveLeft().isEmpty(), "Move left should have default binding");
        assertFalse(keyBindings.getMoveRight().isEmpty(), "Move right should have default binding");
        assertFalse(keyBindings.getRotate().isEmpty(), "Rotate should have default binding");
        assertFalse(keyBindings.getMoveDown().isEmpty(), "Move down should have default binding");
        assertFalse(keyBindings.getHardDrop().isEmpty(), "Hard drop should have default binding");
        assertFalse(keyBindings.getRestart().isEmpty(), "Restart should have default binding");
        assertFalse(keyBindings.getPause().isEmpty(), "Pause should have default binding");
        assertFalse(keyBindings.getMainMenu().isEmpty(), "Main menu should have default binding");
        assertFalse(keyBindings.getHold().isEmpty(), "Hold should have default binding");
        assertFalse(keyBindings.getSwap().isEmpty(), "Swap should have default binding");
    }

    @Test
    void testDetectKeyAlreadyInUse() {
        KeyCode testKey = KeyCode.A;
        keyBindings.getMoveLeft().clear();
        keyBindings.getMoveLeft().add(testKey);
        boolean isUsedElsewhere = isKeyUsedInOtherBindings(testKey, keyBindings.getMoveRight());

        assertTrue(isUsedElsewhere, "Key A should be detected as already in use");
    }

    @Test
    void testKeyNotInUseCanBeAdded() {
        KeyCode testKey = KeyCode.Z;
        boolean isUsedElsewhere = isKeyUsedInOtherBindings(testKey, keyBindings.getMoveLeft());

        assertFalse(isUsedElsewhere, "Key Z should not be in use");
    }

    // Helper method
    private boolean isKeyUsedInOtherBindings(KeyCode key, Set<KeyCode> currentSet) {
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
}