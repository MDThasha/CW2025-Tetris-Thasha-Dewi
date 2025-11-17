package com.comp2042;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ResourceBundle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.fxml.FXML;
import java.net.URL;
import java.awt.*;

public class GuiController implements Initializable {
    // FXML COMPONENTS
    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private AnchorPane nextBrickContainer;
    @FXML private Label scoreLabel;
    @FXML private AnchorPane pauseOverlay;
    @FXML private AnchorPane ghostPane;
    @FXML private Label timerLabel;
    @FXML private Label playerNameLabel;

    //  GAME CONSTANTS
    private static final int BRICK_SIZE = 20;       // Width/height of a single brick block
    private static final int GHOST_Y_OFFSET = 10;   // Vertical offset for ghost brick visualization

    // RECTANGLE ARRAYS
    // Arrays to manage the visual representation of bricks and game board
    private Rectangle[][] ghostRectangles;       // ghost brick preview
    private Rectangle[][] rectangles;            // currently falling brick
    private Rectangle[][] nextBrickRects;        // next brick preview
    private Rectangle[][] displayMatrix;         // fixed bricks on the board

    // GAME STATE AND CONTROL
    private InputEventListener eventListener;     // Interface to handle user/game events
    private Timeline timeLine;                    // Timeline controlling automatic brick falling

    // GAME STATUS FLAGS
    private final BooleanProperty isPause = new SimpleBooleanProperty();    // True if the game is paused
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(); // True if the game has ended

    private String playerName;

    private GameMode currentMode;
    private Timeline gameTimer;
    private IntegerProperty timeLeft;

    // INITIALIZATION
    // Called automatically when the FXML is loaded to set up the UI and game
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38); // Load custom font for score and UI text

        // Set up game panel to receive keyboard input
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // KEYBOARD KEYBIND EVENT HANDLER
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {  // Only handle movement keys if game is not paused or over

                    // MOVE BRICK LEFT
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    // MOVE BRICK RIGHT
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    // MOVE BRICK ROTATE
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)),
                                ((GameController) eventListener).getBoard().getBoardMatrix()
                        );
                        keyEvent.consume();
                    }

                    // MOVE BRICK DOWN
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }

                    // PLACE BRICK AT BOTTOM WITHOUT FALLING ALL THE WAY DOWN
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        DownData result = eventListener.onHardDropEvent(
                                new MoveEvent(EventType.DOWN, EventSource.USER)
                        );

                        // Update moving brick visuals from returned ViewData
                        refreshBrick(result.getViewData(), ((GameController) eventListener).getBoard().getBoardMatrix());

                        // show notification if rows were cleared by the hard drop
                        if (result.getClearRow() != null && result.getClearRow().getLinesRemoved() > 0) {
                            NotificationPanel notificationPanel = new NotificationPanel("+" + result.getClearRow().getScoreBonus());
                            groupNotification.getChildren().add(notificationPanel);
                            notificationPanel.showScore(groupNotification.getChildren());
                        }
                        keyEvent.consume();
                    }
                }

                // PAUSE THE GAME
                if (keyEvent.getCode() == KeyCode.TAB) {
                    togglePause();
                    keyEvent.consume();
                }

                // MAIN MENU FROM GAME
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    Main.loadMenu();
                    keyEvent.consume();
                }

                // RESTART GAME
                if (keyEvent.getCode() == KeyCode.N) { // Restart
                    newGame(null);
                }
            }
        });

        // GAME OVER PANEL INITIAL SETUP
        gameOverPanel.setVisible(false); // Hide game over panel initially
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    // SETS UP VISUAL BOARD / INITIALIZE GAME VIEW
    public void initGameView(int[][] boardMatrix, ViewData brick) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length]; // Create bg grid
        for (int i = 2; i < boardMatrix.length; i++) {                            // Skip hidden top rows
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);      // Set Bricks
                rectangle.setFill(Color.TRANSPARENT);                             // Empty cell initially
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);                            // Add bricks to GridPane
            }
        }

        // CREATE FALLING BRICKS
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length]; // Get Brick
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));                     // Set brick color
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);                                                 // Add brick to panel
            }
        }

        // CREATE GHOST BRICKS FOR PREVIEW ON WHERE ITS GONNA PLACE
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setArcWidth(6);                              // round corners like the falling bricks
                rect.setArcHeight(6);
                rect.setFill(Color.TRANSPARENT);                  // start invisible
                ghostRectangles[i][j] = rect;
                ghostPane.getChildren().add(rect);                // Add to ghost pane
            }
        }

        // POSITION BRICK PANEL FOR ALIGNMENT
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // SET UP GAME TIMELINE
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),                                                            // Brick falls every 400ms
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))        // Move brick down each tick
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);  // Repeat indefinitely
        timeLine.play();                              // Start the game loop
    }

    // SHOW NEXT BRICK PREVIEW IN PREVIEW PANEL
    public void showNextBrick(NextShapeInfo nextShapeInfo) {

        // VALIDATE INPUT
        if (nextShapeInfo == null || nextBrickContainer == null) return;
        nextBrickContainer.getChildren().clear(); // Clear previous preview

        int[][] shape = nextShapeInfo.getShape();
        if (shape == null || shape.length == 0 || shape[0].length == 0) return;

        final int rows = shape.length;
        final int cols = shape[0].length;
        final double blockSize = BRICK_SIZE;

        double containerWidth = nextBrickContainer.getWidth();
        double containerHeight = nextBrickContainer.getHeight();

        // Use preferred size if actual width/height not available yet
        if (containerWidth <= 0) containerWidth = nextBrickContainer.getPrefWidth();
        if (containerHeight <= 0) containerHeight = nextBrickContainer.getPrefHeight();

        // Validation retry if dimensions still invalid
        if (containerWidth <= 0 || containerHeight <= 0) {
            Platform.runLater(() -> showNextBrick(nextShapeInfo));
            return;
        }

        // CALCULATE CENTERING OF RPEVIEW
        double totalWidth = cols * blockSize;
        double totalHeight = rows * blockSize;
        double startX = Math.max(0, (containerWidth - totalWidth) / 2.0);
        double startY = Math.max(0, (containerHeight - totalHeight) / 2.0);
        nextBrickRects = new Rectangle[rows][cols]; // Array to track preview rectangles

        // CREATE AND ADD BRICKS
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (shape[r][c] != 0) {
                    Rectangle rect = new Rectangle(blockSize, blockSize);      // Create rectangle for filled cell
                    rect.setFill(getFillColor(shape[r][c]));                   // Set color based on shape value
                    rect.setArcWidth(6);                                       // Rounded corners
                    rect.setArcHeight(6);
                    rect.setStroke(Color.rgb(30, 30, 30, 0.5));    // Subtle border
                    rect.setStrokeWidth(1);

                    // Position rectangle in container
                    double x = startX + c * blockSize;
                    double y = startY + r * blockSize;
                    rect.setLayoutX(x);
                    rect.setLayoutY(y);

                    // Add to preview container and array
                    nextBrickContainer.getChildren().add(rect);
                    nextBrickRects[r][c] = rect;
                } else {
                    nextBrickRects[r][c] = null;                                // Empty cells stored as null
                }
            }
        }
    }

    // COLOR MANAGEMENT OF BRICKS
    private static final Color[] COLORS = {
            Color.TRANSPARENT, Color.AQUA, Color.BLUEVIOLET, Color.DARKGREEN, Color.YELLOW, Color.RED, Color.BEIGE, Color.BURLYWOOD
    };

    // Returns the corresponding color for a given brick value
    private Paint getFillColor(int i) {
        return (i >= 0 && i < COLORS.length) ? COLORS[i] : Color.WHITE;
    }

    // REFRESH MOVING BRICKS AND GHOST BRICKS
    private void refreshBrick(ViewData brick, int[][] board) {
        if (!isPause.get()) {
            Point ghost = brick.getGhostOffset(); // Get the current ghost offset

            //  UPDATE GHOST RECTANGLES
            for (int i = 0; i < ghostRectangles.length; i++) {
                for (int j = 0; j < ghostRectangles[i].length; j++) {
                    Rectangle ghostRect = ghostRectangles[i][j];
                    if (brick.getBrickData()[i][j] != 0 && ghost != null) {
                        // Set ghost appearance
                        ghostRect.setFill(Color.GRAY.deriveColor(0, 1, 1, 0.3));
                        ghostRect.setArcWidth(9);
                        ghostRect.setArcHeight(9);

                        // Position ghost based on brick position + ghost offset
                        ghostRect.setLayoutX(gamePanel.getLayoutX() + (brick.getxPosition() + j) * brickPanel.getVgap() + (brick.getxPosition() + j) * BRICK_SIZE);
                        ghostRect.setLayoutY(-42 + gamePanel.getLayoutY() + (i + ghost.y) * brickPanel.getHgap() + (i + ghost.y) * BRICK_SIZE + GHOST_Y_OFFSET);
                    } else {
                        ghostRect.setFill(Color.TRANSPARENT); // Hide ghost if no block here
                    }
                }
            }

            // UPDATE CURRENT MOVING BRICK RECTANGLES
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

            for (int i = 0; i < rectangles.length; i++) {
                for (int j = 0; j < rectangles[i].length; j++) {
                    Rectangle rect = rectangles[i][j];
                    if (brick.getBrickData()[i][j] != 0) {
                        // Set brick block appearance
                        rect.setFill(getFillColor(brick.getBrickData()[i][j]));
                        rect.setArcWidth(6);              // Rounded Corners
                        rect.setArcHeight(6);
                    } else {
                        rect.setFill(Color.TRANSPARENT);  // Empty cells are transparent
                    }
                }
            }
        }
        refreshGameBackground(board); // refresh everytime
    }

    // REFRESH BACKGROUND GRID - Updates the static board blocks that are already placed
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    // SET RECTANGLE DATA
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(6); // Rounded edges
        rectangle.setArcWidth(6);
    }

    // MOVE BRICK DOWN
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {                                                   // Only move brick down if game is not paused
            DownData downData = eventListener.onDownEvent(event);                                    // Process movement through game logic

            // CHECK FOR CLEARED ROWS
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);                              // Add score notification
                notificationPanel.showScore(groupNotification.getChildren());                        // Animate the notification
            }

            // REFRESH MOVING BRICK AND BOARD
            refreshBrick( downData.getViewData(), ((GameController) eventListener).getBoard().getBoardMatrix() );
        }
        gamePanel.requestFocus();  // Ensure the game panel retains focus for input
    }

    // SET EVENT LISTENER
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    // BIND SCORE PROPERTY
    public void bindScore(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString());    // Bind score label text

        scoreProperty.addListener((obs, oldVal, newVal) -> {
            double newSpeed = Math.max(100, 400 - (newVal.intValue() / 500) * 25);
            // Calculate new speed as score increases: starts at 400ms, speeds up every 500 points, minimum 100ms

            javafx.application.Platform.runLater(() -> {
                boolean wasRunning = timeLine != null && timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;
                timeLine.stop();

                // Create new timeline with updated speed
                timeLine.getKeyFrames().setAll(new KeyFrame( Duration.millis(newSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));

                // Resume if it was running
                if (wasRunning) timeLine.play();
            });
        });
    }

    // SET PLAYER NAME FOR HS
    public void setPlayerName(String name) {
        this.playerName = (name == null || name.isEmpty()) ? "Unknown" : name;
    }
    public void setPlayerNameLabel(String name) {
        playerNameLabel.setText(name);
    }

    public void setGameMode(GameMode mode) {
        this.currentMode = mode;
    }

    public GameMode getGameMode() {
        return currentMode;
    }

    public void startTimer(int seconds) {
        if (gameTimer != null) gameTimer.stop();

        timeLeft = new SimpleIntegerProperty(seconds);

        timerLabel.textProperty().bind(timeLeft.asString());

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            if (timeLeft.get() > 0) {
                timeLeft.set(timeLeft.get() - 1);
            } else {
                gameTimer.stop();
                gameOver();
            }
        }));

        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }



    // GAME OVER
    public void gameOver() {
        timeLine.stop();                                                     // Stop brick movement
        int finalScore = 0;                                                  // Get final score
        try { finalScore = Integer.parseInt(scoreLabel.getText()); }
        catch (NumberFormatException e) { finalScore = 0; }

        HighScoreManager.addScore(playerName, finalScore, currentMode);                   // Add Score to HS with name
        int highScore = HighScoreManager.getHighScore(currentMode);                     // Get HS

        gameOverPanel.showGameOver(finalScore, highScore);                   // Show final score and HS with the GO panel
        isGameOver.setValue(Boolean.TRUE);                                   // Mark game as over
    }

    // START NEW GAME
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        eventListener.createNewGame();            // Reset game logic
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        gameOverPanel.reset();                    // Reset Game Over UI
    }

    // STOP TIMELINE
    public void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
    }

    // TOGGLE PAUSE
    public void togglePause() {
        if (isGameOver.get()) return;        // Can't pause on game over
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

    // PAUSE GAME
    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
