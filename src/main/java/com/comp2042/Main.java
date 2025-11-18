package com.comp2042;

import com.comp2042.Controllers.GameController;
import com.comp2042.Controllers.GuiController;
import com.comp2042.Event.GameMode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage mainStage;  // Primary stage of the application
    private static Scene mainScene;  // Single scene used throughout


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

    public static Stage getStage() { return mainStage; }
    public static Scene getScene() { return mainScene; }

    // Load Main Menu from the game scene
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

    // Load the Game, From Menu scene. Start Game
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

    public static void main(String[] args) {
        launch(args);
    }
}
