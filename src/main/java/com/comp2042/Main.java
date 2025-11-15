package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage mainStage;
    private static Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        mainStage.setResizable(true);
        mainStage.setMinHeight(700);
        mainStage.setMinWidth(500);
        mainStage.setTitle("TetrisJFX");

        Parent menuRoot = FXMLLoader.load(getClass().getClassLoader().getResource("mainMenu.fxml"));
        mainScene = new Scene(menuRoot); // single scene
        mainStage.setScene(mainScene);
        mainStage.show();
    }

    // Load Main Menu, from Gamescene.
    public static void loadMenu() {
        try {
            // Stop the game if it's running
            if (GameController.currentController != null) {
                GameController.currentController.stopTimeline();
            }

            Parent menuRoot = FXMLLoader.load(Main.class.getClassLoader().getResource("mainMenu.fxml"));
            mainScene.setRoot(menuRoot); // just swap root, scene stays the same

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load the Game, From Menu scene. Start Game
    public static void loadGame() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();
            GuiController c = loader.getController();
            new GameController(c);
            mainScene.setRoot(gameRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
