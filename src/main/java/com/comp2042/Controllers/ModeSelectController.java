package com.comp2042.Controllers;

import com.comp2042.Event.GameMode;
import com.comp2042.Helper.PlayerUtils;
import com.comp2042.Helper.UIHover;
import com.comp2042.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**Controller for the mode selection screen selectMode.fxml.
 * <p>Provides UI handlers for selecting a game mode (Classic, Time Limit, All-Same-Block)
 * and starting the game with the chosen player name.</p> */
public class ModeSelectController {
    /** Button that starts a Classic mode game. */
    @FXML private Button classicBtn;

    /** Button that starts a Time Limit mode game. */
    @FXML private Button timeBtn;

    /** Button that starts an All-Same-Block mode game. */
    @FXML private Button sameBtn;

    /** Button that returns to the main menu. */
    @FXML private Button backBtn;

    /** player name as string*/
    private String playerName;

    /**Set the player name to use when starting a game.
     * @param name the player name (validated before storage) */
    public void setPlayerName(String name) {
        this.playerName = PlayerUtils.validatePlayerName(name);
    }

    /**JavaFX initialize hook called after FXML injection.
     * <p>Registers action handlers for each button: selects the corresponding game mode
     * or returns to the main menu.</p> */
    @FXML
    public void initialize() {
        classicBtn.setOnAction(e -> startGame(GameMode.CLASSIC));
        timeBtn.setOnAction(e -> startGame(GameMode.TIME_LIMIT));
        sameBtn.setOnAction(e -> startGame(GameMode.ALL_SAME_BLOCK));
        backBtn.setOnAction(e -> Main.loadMenu());

        UIHover.apply(true,"menu-button",classicBtn, timeBtn, sameBtn, backBtn);

    }

    /**Load the main game FXML, create the GameController and start a new game.
     * @param mode the GameMode to start
     * <p>Loads {@code gameLayout.fxml}, obtains its GuiController, constructs a new
     * GameController (which sets up the model/view), and replaces the scene root with the game UI.
     * Exceptions during loading are logged to stderr.</p> */
    private void startGame(GameMode mode) {
        try {
            System.out.println("Loading gameLayout.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();
            System.out.println("FXML loaded successfully!");

            GuiController gui = loader.getController();
            gui.setGameMode(mode);
            GameController controller = new GameController(gui, this.playerName, mode);

            Main.getScene().setRoot(gameRoot);
        }
        catch (Exception ex) { System.err.println("ERROR loading game:"); ex.printStackTrace(); }
    }
}
