package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Label;


public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @FXML
    private AnchorPane nextBrickContainer;

    @FXML
    private Label scoreLabel;

    private Rectangle[][] nextBrickRects;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) { //KEYBIND KEYS TO ACTIONS
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    //SETS UP VISUAL BOARD
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length]; //MAKE GRID
        for (int i = 2; i < boardMatrix.length; i++) { //NUM OF ROWS
            for (int j = 0; j < boardMatrix[i].length; j++) { //NUM OF COLMS
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length]; //GET BRICK
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j])); //COLOURS BRICK
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
        //BRICK POSITIONS SET

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400), //BRICK FALLS EVERY 400MS
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    //Next Shape info
    public void showNextBrick(NextShapeInfo nextShapeInfo) {
        if (nextShapeInfo == null || nextBrickContainer == null) return;

        // Clear previous preview
        nextBrickContainer.getChildren().clear();

        int[][] shape = nextShapeInfo.getShape();
        if (shape == null || shape.length == 0 || shape[0].length == 0) return;

        // UI based on the brick and the container in css ect
        final int rows = shape.length;
        final int cols = shape[0].length;
        final double blockSize = BRICK_SIZE;
        double containerWidth = nextBrickContainer.getWidth();
        double containerHeight = nextBrickContainer.getHeight();
        if (containerWidth <= 0) containerWidth = nextBrickContainer.getPrefWidth();
        if (containerHeight <= 0) containerHeight = nextBrickContainer.getPrefHeight();

        //validation just to make sure it works
        if (containerWidth <= 0 || containerHeight <= 0) {
            Platform.runLater(() -> showNextBrick(nextShapeInfo));
            return;
        }
        // preview is centered
        double totalWidth = cols * blockSize;
        double totalHeight = rows * blockSize;
        double startX = Math.max(0, (containerWidth - totalWidth) / 2.0);
        double startY = Math.max(0, (containerHeight - totalHeight) / 2.0);

        nextBrickRects = new Rectangle[rows][cols];

        //Loopingg to add the colour to correct place for the correct next brick
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (shape[r][c] != 0) {
                    Rectangle rect = new Rectangle(blockSize, blockSize);
                    rect.setFill(getFillColor(shape[r][c]));
                    rect.setArcWidth(6);
                    rect.setArcHeight(6);
                    rect.setStroke(Color.rgb(30, 30, 30, 0.5));
                    rect.setStrokeWidth(1);

                    double x = startX + c * blockSize;
                    double y = startY + r * blockSize;
                    rect.setLayoutX(x);
                    rect.setLayoutY(y);

                    nextBrickContainer.getChildren().add(rect);
                    nextBrickRects[r][c] = rect;
                } else {
                    nextBrickRects[r][c] = null;
                }
            }
        }
    }

    private Paint getFillColor(int i) { //COLOURS BRICK AT RANDOM
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) { //UPDATES BRICKS POSITION AND COLOUR AS IT MOVES
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) { //UPDATES THE BG TO REFLECT BRICK MOVING
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9); //SMOOTH MOVEMENT OF BRICKS
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) { //UPDATE BRICK POSITION AFTER CLEAR ROW
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                //NOTIFICATION OF SCORE BONUS
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    //Edited this part, making bricks fall faster as score increases and display score
    public void bindScore(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString());
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            double newSpeed = Math.max(100, 400 - (newVal.intValue() / 500) * 25);

            javafx.application.Platform.runLater(() -> {
                boolean wasRunning = timeLine != null &&
                        timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;

                timeLine.stop();
                timeLine.getKeyFrames().setAll(new KeyFrame(
                        Duration.millis(newSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));

                if (wasRunning) timeLine.play();
            });
        });
    }
    //CONNECTS TO BINDSCORE

    public void gameOver() {
        timeLine.stop(); //STOPS GAME
        gameOverPanel.setVisible(true); //SHOW GAME OVER PANEL
        isGameOver.setValue(Boolean.TRUE);
    }

    //START NEWGAME
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
