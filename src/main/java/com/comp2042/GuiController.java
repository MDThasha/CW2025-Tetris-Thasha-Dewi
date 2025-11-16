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

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GuiController implements Initializable {

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private AnchorPane nextBrickContainer;
    @FXML private Label scoreLabel;
    @FXML private AnchorPane pauseOverlay;
    @FXML private AnchorPane ghostPane;

    private static final int BRICK_SIZE = 20;
    private Rectangle[][] nextBrickRects;
    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Timeline timeLine;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    private Rectangle[][] ghostRectangles;
    private static final int GHOST_Y_OFFSET = 10;

    @Override // Start
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) { // Keybinds key to actions
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }

                if (keyEvent.getCode() == KeyCode.SPACE) {
                    DownData result = eventListener.onHardDropEvent(
                            new MoveEvent(EventType.DOWN, EventSource.USER)
                    );
                    // GameController already refreshed board and next preview.
                    // Update moving brick visuals from returned ViewData:
                    refreshBrick(result.getViewData(),
                            ((GameController) eventListener).getBoard().getBoardMatrix()
                    );
                    // show notification if rows were cleared by the hard drop
                    if (result.getClearRow() != null && result.getClearRow().getLinesRemoved() > 0) {
                        NotificationPanel notificationPanel = new NotificationPanel("+" + result.getClearRow().getScoreBonus());
                        groupNotification.getChildren().add(notificationPanel);
                        notificationPanel.showScore(groupNotification.getChildren());
                    }
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.TAB) {
                    togglePause();
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    Main.loadMenu();
                    keyEvent.consume();
                }

                if (keyEvent.getCode() == KeyCode.N) { // Restart
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

    // SETS UP VISUAL BOARD
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length]; // MAKE GRID
        for (int i = 2; i < boardMatrix.length; i++) { // NUM OF ROWS
            for (int j = 0; j < boardMatrix[i].length; j++) { // NUM OF COLMS
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length]; // GET BRICK
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j])); // COLOURS BRICK
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setArcWidth(9);
                rect.setArcHeight(9);
                rect.setFill(Color.TRANSPARENT); // start invisible
                ghostRectangles[i][j] = rect;
                ghostPane.getChildren().add(rect);
            }
        }

        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400), // BRICK FALLS EVERY 400MS
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    //Next Shape info
    public void showNextBrick(NextShapeInfo nextShapeInfo) {
        if (nextShapeInfo == null || nextBrickContainer == null) return;
        nextBrickContainer.getChildren().clear(); // Clear previous preview
        int[][] shape = nextShapeInfo.getShape();
        if (shape == null || shape.length == 0 || shape[0].length == 0) return;
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

        //Looping to add the colour to correct place for the correct next brick
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
                }

                else {
                    nextBrickRects[r][c] = null;
                }
            }
        }
    }

    private Paint getFillColor(int i) { // COLOURS BRICK AT RANDOM
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

    private void refreshBrick(ViewData brick, int[][] board) {
        if (!isPause.get()) {
            Point ghost = brick.getGhostOffset();

            // Update ghost rectangles
            for (int i = 0; i < ghostRectangles.length; i++) {
                for (int j = 0; j < ghostRectangles[i].length; j++) {
                    Rectangle ghostRect = ghostRectangles[i][j];
                    if (brick.getBrickData()[i][j] != 0 && ghost != null) {
                        ghostRect.setFill(Color.GRAY.deriveColor(0, 1, 1, 0.3));
                        ghostRect.setArcWidth(9);
                        ghostRect.setArcHeight(9);
                        ghostRect.setLayoutX(gamePanel.getLayoutX() + (brick.getxPosition() + j) * brickPanel.getVgap() + (brick.getxPosition() + j) * BRICK_SIZE);
                        ghostRect.setLayoutY(-42 + gamePanel.getLayoutY() + (i + ghost.y) * brickPanel.getHgap() + (i + ghost.y) * BRICK_SIZE + GHOST_Y_OFFSET);

                    } else {
                        ghostRect.setFill(Color.TRANSPARENT);
                    }
                }
            }

            // Update moving brick rectangles
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

            for (int i = 0; i < rectangles.length; i++) {
                for (int j = 0; j < rectangles[i].length; j++) {
                    Rectangle rect = rectangles[i][j];
                    if (brick.getBrickData()[i][j] != 0) {
                        rect.setFill(getFillColor(brick.getBrickData()[i][j]));
                        rect.setArcWidth(9);  // ensure rounded
                        rect.setArcHeight(9); // ensure rounded
                    } else {
                        rect.setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
        // Refresh
        refreshGameBackground(board);
    }

    public void refreshGameBackground(int[][] board) { // UPDATES THE BG TO REFLECT BRICK MOVING
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9); // SMOOTH MOVEMENT OF BRICKS
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) { // UPDATE BRICK POSITION AFTER CLEAR ROW
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel); //NOTIFICATION OF SCORE BONUS
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(
                    downData.getViewData(),
                    ((GameController) eventListener).getBoard().getBoardMatrix()
            );
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    // making bricks fall faster as score increases and display score
    public void bindScore(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString());
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            double newSpeed = Math.max(100, 400 - (newVal.intValue() / 500) * 25);
            //Initial speed of 400ms and increases by 25 every 500 points, limits at 100ms

            javafx.application.Platform.runLater(() -> {
                boolean wasRunning = timeLine != null && timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;
                timeLine.stop();
                timeLine.getKeyFrames().setAll(new KeyFrame(
                        Duration.millis(newSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));

                if (wasRunning) timeLine.play();
            });
        });
    }

    public void gameOver() {
        timeLine.stop(); // Stop the game

        //get finalScore to display in gameoverpanel
        int finalScore = 0;
        try { finalScore = Integer.parseInt(scoreLabel.getText()); }
        catch (NumberFormatException e) { finalScore = 0; }
        gameOverPanel.showGameOver(finalScore, 0); // Show final score in Game Over panel
        isGameOver.setValue(Boolean.TRUE);
    }

    //START NEWGAME
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        gameOverPanel.reset();
    }

    public void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    public void togglePause() {
        if (isGameOver.get()) return; // Can't pause on game over
        boolean paused = isPause.get();
        isPause.set(!paused);
        if (!paused) {
            // Pause ON
            timeLine.stop();
            pauseOverlay.setVisible(true);
        } else {
            // Pause OFF
            pauseOverlay.setVisible(false);
            timeLine.play();
        }
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
