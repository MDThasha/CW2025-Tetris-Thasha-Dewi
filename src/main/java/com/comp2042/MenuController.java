package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Button startBtn;

    @FXML
    private Button quitBtn;

    @FXML
    public void initialize() {
        startBtn.setOnAction(e -> Main.loadGame());
        quitBtn.setOnAction(e -> System.exit(0));
    }

    @FXML
    public StackPane rootPane;

    @FXML
    public void openControls() {
        try {
            Parent controlsRoot = FXMLLoader.load(getClass().getClassLoader().getResource("controlsLayout.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(controlsRoot, 500, 700));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
