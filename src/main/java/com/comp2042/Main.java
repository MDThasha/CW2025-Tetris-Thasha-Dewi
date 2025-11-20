package com.comp2042;

import com.comp2042.Controllers.GameController;
import com.comp2042.Controllers.GuiController;
import com.comp2042.Event.GameMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX application entry point and helper methods for switching scenes (menu and game). */
public class Main extends Application {

    /** Primary Stage used by the application. */
    private static Stage mainStage;
    /** Single Scene instance reused; roots are swapped to change screens. so full screen dont effect game layout */
    private static Scene mainScene;

    /** Start the JavaFX application and show the main menu. */
    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        mainStage.setResizable(true);     // Allow resizing
        mainStage.setMinHeight(700);      // Minimum height of window
        mainStage.setMinWidth(500);       // Minimum width of window
        mainStage.setTitle("TetrisJFX");  // Window title

        // Load main menu FXML as initial screen
        Parent menuRoot = FXMLLoader.load(getClass().getClassLoader().getResource("mainMenu.fxml"));
        mainScene = new Scene(menuRoot); // Set single scene so if full screen it ddont change
        mainStage.setScene(mainScene);   // Show menu scene
        mainStage.show();
    }

    /** Return the primary application Stage. */
    public static Stage getStage() { return mainStage; }

    /** Return the shared Scene instance. */
    public static Scene getScene() { return mainScene; }

    /**  Load the main menu UI into the existing scene. Stops any running game timelines first.*/
    public static void loadMenu() {
        try {
            // Stop the game if it's running
            if (GuiController.currentController != null) {
                GuiController.currentController.stopTimeline();
            }
            Parent menuRoot = FXMLLoader.load(Main.class.getClassLoader().getResource("mainMenu.fxml"));
            mainScene.setRoot(menuRoot); // just swap root, scene stays the same
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Load the game UI, initialise a GameController and start the game for the given player and mode.
     * @param playerName the player's name
     * @param mode the selected GameMode*/
    public static void loadGame(String playerName, GameMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();

            GuiController guiController = loader.getController();
            guiController.setPlayerName(playerName);
            new GameController(guiController, playerName, mode); // Pass player name + mode
            mainScene.setRoot(gameRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Launch the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
