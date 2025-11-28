package com.comp2042;

import com.comp2042.Controllers.GuiController;
import com.comp2042.Managers.AudioManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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
        mainStage.setMinHeight(800);      // Minimum height of window
        mainStage.setMinWidth(500);       // Minimum width of window
        mainStage.setTitle("TetrisJFX");  // Window title
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        // Load main menu FXML as initial screen
        Parent menuRoot = FXMLLoader.load(getClass().getClassLoader().getResource("mainMenu.fxml"));
        mainScene = new Scene(menuRoot); // Set single scene so if full screen it ddont change
        mainStage.setScene(mainScene);   // Show menu scene
        mainStage.show();

        AudioManager.loadSound("land", "/sounds/land.wav");
        AudioManager.loadSound("clear", "/sounds/clear.wav");
        AudioManager.loadSound("gameover", "/sounds/gameover.wav");
        AudioManager.loadBackgroundMusic("/sounds/TetrisMusic.wav");
    }

    /** Return the shared Scene instance. */
    public static Scene getScene() { return mainScene; }

    /**  Load the main menu UI into the existing scene. Stops any running game timelines first.*/
    public static void loadMenu() {
        try {
            // Stop the game if it's running
            if (GuiController.currentController != null) {
                GuiController.currentController.stopTimeline();
                AudioManager.playBackgroundMusic();
            }
            Parent menuRoot = FXMLLoader.load(Main.class.getClassLoader().getResource("mainMenu.fxml"));
            mainScene.setRoot(menuRoot); // just swap root, scene stays the same
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Launch the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
