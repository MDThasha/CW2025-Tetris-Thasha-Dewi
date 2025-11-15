package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        mainStage.setResizable(true);
        mainStage.setMinHeight(700);
        mainStage.setMinWidth(500);
        mainStage.setTitle("TetrisJFX");

        loadMenu();  // Start at the menu, ESC
        mainStage.show();
    }

    // Load Main Menu
    public static void loadMenu() {
        try {
            // Stop the game if it's running
            if (GameController.currentController != null) {
                GameController.currentController.stopTimeline();
            }

            Parent root = FXMLLoader.load(Main.class.getClassLoader().getResource("mainMenu.fxml"));
            Scene scene = new Scene(root, 500, 700);
            mainStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Load the Game, Start Game (cant use newgame in gui cause that only works if we in the game already)
    public static void loadGame() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("gameLayout.fxml"));
            Parent root = loader.load();

            GuiController c = loader.getController();
            new GameController(c);

            Scene scene = new Scene(root, 500, 700);
            mainStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
