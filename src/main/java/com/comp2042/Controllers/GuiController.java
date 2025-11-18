package com.comp2042.Controllers;

import com.comp2042.*;
import com.comp2042.Event.*;
import com.comp2042.GameBoard.DownData;
import com.comp2042.GameBoard.ViewData;
import com.comp2042.Helper.PlayerUtils;
import com.comp2042.Panels.GameOverPanel;
import com.comp2042.Panels.NotificationPanel;
import com.comp2042.PlayerData.HighScoreManager;
import com.comp2042.logic.bricks.NextShapeInfo;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.util.Set;

public class GuiController implements Initializable {

    // GAME PANELS
    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private AnchorPane ghostPane;
    @FXML private AnchorPane pauseOverlay;
    @FXML private GameOverPanel gameOverPanel;

    // BRICK PREVIEW UI
    @FXML private AnchorPane nextBrickContainer;
    @FXML private AnchorPane holdBrickContainer;

    // LABELS
    @FXML private Label scoreLabel;
    @FXML private Label timerLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label pauseKeybindLabel;

    // CONTROLS
    @FXML private Label restartLabel, rotateLabel, moveLeftLabel, moveRightLabel;
    @FXML private Label moveDownLabel, hardDropLabel, mainMenuLabel, pauseLabel;
    @FXML private Label holdLabel, swapLabel;

    // BONUS NOTIFICATIONS
    @FXML private Group groupNotification;
    @FXML private Group TimeBonus;
    @FXML private Label bonusTimeLabel;

    // SPEED/LEVEL DISPLAY
    @FXML private Label speedLabel;
    private int currentSpeed = 400;  // Starting speed in ms
    private int currentLevel = 1;

    // CONSTANTS
    private static final int brickSize = 20;
    private static final int ghostYOffset = 10;
    private static final int brickPanelYOffset = -42;
    
    public static final int initialSpeed = 400;            // Starting speed in ms
    private static final int minSpeed = 100;               // Maximum speed (fastest)
    private static final int speedDecPerLvl = 25; // Speed increase per level
    private static final int pointsPerLvl = 500;        // Points needed per level

    // STATIC REFERENCE
    public static GuiController currentController;

    // RECTANGLE ARRAYS (Visuals)
    private Rectangle[][] displayMatrix;      // Fixed bricks on board
    private Rectangle[][] rectangles;         // Currently falling brick
    private Rectangle[][] ghostRectangles;    // Ghost brick preview

    // GAME STATE
    private InputEventListener eventListener;
    private Timeline timeLine;
    private Timeline gameTimer;
    private IntegerProperty timeLeft;

    // GAME STATUS FLAGS
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // GAME INFO
    private String playerName;
    private GameMode currentMode;

    // KEYBINDINGS
    private KeyBindings keyBindings = KeyBindings.getInstance();
    public KeyBindings getKeyBindings() { return keyBindings; }
    public void setKeyBindings(KeyBindings bindings) { this.keyBindings = bindings; }

    // SET EVENT LISTENER
    public void setEventListener(InputEventListener eventListener) { this.eventListener = eventListener; }

    // HELPERS
    private GameController getGameController() { return (GameController) eventListener; }

    private double calculateBrickLayoutX(int xPosition) {
        return gamePanel.getLayoutX() + xPosition * brickPanel.getVgap() + xPosition * brickSize; }

    private double calculateBrickLayoutY(int yPosition) {
        return brickPanelYOffset + gamePanel.getLayoutY() + yPosition * brickPanel.getHgap() + yPosition * brickSize; }

    private void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);
        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    private String formatKeys(Set<KeyCode> keys) {
        if (keys == null || keys.size() == 0) return "?";

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (KeyCode key : keys) {
            if (count > 0) sb.append(" / ");
            sb.append(formatKeyName(key));
            count++;
            if (count >= 2) break; // Only show first 2 keys to avoid clutter
        }
        return sb.toString();
    }

    // INITIALIZATION
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        currentController = this;
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // KEYBOARD KEYBIND EVENT HANDLER
        gamePanel.setOnKeyPressed(this::handleKeyPress);

        // GAME OVER PANEL
        gameOverPanel.setVisible(false);

        updateControlLabels();
        updateSpeedDisplay(initialSpeed, 1);
    }

    // HANDLE KEYBOARD INPUT
    private void handleKeyPress(KeyEvent keyEvent) {
        if (!isPause.get() && !isGameOver.get()) {

            // MOVE BRICK LEFT
            if (keyBindings.getMoveLeft().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                ); keyEvent.consume();
            }

            // MOVE BRICK RIGHT
            if (keyBindings.getMoveRight().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                ); keyEvent.consume();
            }

            // MOVE BRICK ROTATE
            if (keyBindings.getRotate().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                ); keyEvent.consume();
            }

            // MOVE BRICK DOWN
            if (keyBindings.getMoveDown().contains(keyEvent.getCode())) {
                moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                keyEvent.consume();
            }

            // PLACE BRICK AT BOTTOM WITHOUT FALLING ALL THE WAY DOWN
            if (keyBindings.getHardDrop().contains(keyEvent.getCode())) {
                DownData result = eventListener.onHardDropEvent(
                        new MoveEvent(EventType.DOWN, EventSource.USER)
                );

                // Update moving brick visuals from returned ViewData
                refreshBrick(result.getViewData(), getGameController().getBoard().getBoardMatrix());

                // show notification if rows were cleared by the hard drop
                if (result.getClearRow() != null && result.getClearRow().getLinesRemoved() > 0) {
                    showScoreNotification(result.getClearRow().getScoreBonus());
                }
                keyEvent.consume();
            }

            // HOLD BRICK
            if (keyBindings.getHold().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                );
                keyEvent.consume();
            }

            // SWAP BRICK
            if (keyBindings.getSwap().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onSwapEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                );
                keyEvent.consume();
            }
        }

        // PAUSE THE GAME
        if (keyBindings.getPause().contains(keyEvent.getCode())) {
            togglePause();
            keyEvent.consume();
        }

        // MAIN MENU FROM GAME
        if (keyBindings.getMainMenu().contains(keyEvent.getCode())) {
            Main.loadMenu();
            keyEvent.consume();
        }

        // RESTART GAME
        if (keyBindings.getRestart().contains(keyEvent.getCode())) {
            newGame(null);
        }
    }

    // Format individual key names with arrows for arrow keys
    private String formatKeyName(KeyCode key) {
        switch (key) {
            case UP: return "↑";
            case DOWN: return "↓";
            case LEFT: return "←";
            case RIGHT: return "→";
            case SPACE: return "SPACE";
            case TAB: return "TAB";
            case ESCAPE: return "ESC";
            default: return key.getName();
        }
    }

    // KEYBINDING REFLECT IN GAME VIEW
    public void updateControlLabels() {
        if (keyBindings == null) return;

        restartLabel.setText("Restart   - " + formatKeys(keyBindings.getRestart()));
        rotateLabel.setText("Rotate    - " + formatKeys(keyBindings.getRotate()));
        moveLeftLabel.setText("Move Left - " + formatKeys(keyBindings.getMoveLeft()));
        moveRightLabel.setText("Move Right- " + formatKeys(keyBindings.getMoveRight()));
        moveDownLabel.setText("Move Down - " + formatKeys(keyBindings.getMoveDown()));
        hardDropLabel.setText("DROP      - " + formatKeys(keyBindings.getHardDrop()));
        mainMenuLabel.setText("MAIN MENU - " + formatKeys(keyBindings.getMainMenu()));
        pauseLabel.setText("PAUSE     - " + formatKeys(keyBindings.getPause()));

        if (holdLabel != null) holdLabel.setText("HOLD      - " + formatKeys(keyBindings.getHold()));
        if (swapLabel != null) swapLabel.setText("SWAP      - " + formatKeys(keyBindings.getSwap()));

        if (pauseKeybindLabel != null) {
            pauseKeybindLabel.setText("Press " + formatKeys(keyBindings.getPause()) + " to unpause");
        }
    }

    // SETS UP VISUAL BOARD / INITIALIZE GAME VIEW
    public void initGameView(int[][] boardMatrix, ViewData brick) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length]; // Create bg grid
        for (int i = 2; i < boardMatrix.length; i++) {                            // Skip hidden top rows
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);      // Set Bricks
                rectangle.setFill(Color.TRANSPARENT);                             // Empty cell initially
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);                            // Add bricks to GridPane
            }
        }

        // CREATE FALLING BRICKS
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length]; // Get Brick
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));                     // Set brick color
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);                                                 // Add brick to panel
            }
        }

        // CREATE GHOST BRICKS FOR PREVIEW ON WHERE ITS GONNA PLACE
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rect = new Rectangle(brickSize, brickSize);
                rect.setArcWidth(6);                              // round corners like the falling bricks
                rect.setArcHeight(6);
                rect.setFill(Color.TRANSPARENT);                  // start invisible
                ghostRectangles[i][j] = rect;
                ghostPane.getChildren().add(rect);                // Add to ghost pane
            }
        }

        // POSITION BRICK PANEL FOR ALIGNMENT
        brickPanel.setLayoutX(calculateBrickLayoutX(brick.getxPosition()));
        brickPanel.setLayoutY(calculateBrickLayoutY(brick.getyPosition()));

        // SET UP GAME TIMELINE
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(initialSpeed),                                                 // Brick falls at initial
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))   // Move brick down each tick
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);  // Repeat indefinitely
        timeLine.play();                              // Start the game loop
    }

    // COLOR MANAGEMENT OF BRICKS
    private static final Color[] COLORS = {
            Color.TRANSPARENT, Color.AQUA, Color.BLUEVIOLET, Color.DARKGREEN, Color.YELLOW, Color.RED, Color.BEIGE, Color.BURLYWOOD
    };

    private Paint getFillColor(int i) { return (i >= 0 && i < COLORS.length) ? COLORS[i] : Color.WHITE; }

    // SET RECTANGLE DATA
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(6); // Rounded edges
        rectangle.setArcWidth(6);
    }

    // HELPER FOR SHOWING NEXT AND HEL BRICK PREVIEW IN CONTAINER
    private void showBrickPreview(NextShapeInfo shapeInfo, AnchorPane container) {
        if (container == null) return;
        container.getChildren().clear();

        if (shapeInfo == null) return;  // No brick to show

        int[][] shape = shapeInfo.getShape();
        if (shape == null || shape.length == 0 || shape[0].length == 0) return;

        final int rows = shape.length;
        final int cols = shape[0].length;
        final double blockSize = brickSize;

        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();

        // Use preferred size if actual width/height not available yet
        if (containerWidth <= 0) containerWidth = container.getPrefWidth();
        if (containerHeight <= 0) containerHeight = container.getPrefHeight();

        // Validation retry if dimensions still invalid
        if (containerWidth <= 0 || containerHeight <= 0) {
            Platform.runLater(() -> showBrickPreview(shapeInfo, container));
            return;
        }

        // Calculate centering
        double totalWidth = cols * blockSize;
        double totalHeight = rows * blockSize;
        double startX = Math.max(0, (containerWidth - totalWidth) / 2.0);
        double startY = Math.max(0, (containerHeight - totalHeight) / 2.0);

        // Create and add brick rectangles
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

                    container.getChildren().add(rect);
                }
            }
        }
    }

    // SHOW NEXT BRICK PREVIEW IN PREVIEW PANEL
    public void showNextBrick(NextShapeInfo nextShapeInfo) {
        showBrickPreview(nextShapeInfo, nextBrickContainer);
    }

    // SHOW HELD BRICK PREVIEW IN HOLD PANEL
    public void showHeldBrick(NextShapeInfo heldShapeInfo) {
        showBrickPreview(heldShapeInfo, holdBrickContainer);
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
                        ghostRect.setLayoutX(gamePanel.getLayoutX() + (brick.getxPosition() + j) * brickPanel.getVgap() + (brick.getxPosition() + j) * brickSize);
                        ghostRect.setLayoutY(brickPanelYOffset + gamePanel.getLayoutY() + (i + ghost.y) * brickPanel.getHgap() + (i + ghost.y) * brickSize + ghostYOffset);
                    } else {
                        ghostRect.setFill(Color.TRANSPARENT); // Hide ghost if no block here
                    }
                }
            }

            // UPDATE CURRENT MOVING BRICK RECTANGLES
            brickPanel.setLayoutX(calculateBrickLayoutX(brick.getxPosition()));
            brickPanel.setLayoutY(calculateBrickLayoutY(brick.getyPosition()));

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
        updateGameSpeed();
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

    // MOVE BRICK DOWN
    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {                                                   // Only move brick down if game is not paused
            DownData downData = eventListener.onDownEvent(event);                                    // Process movement through game logic

            // CHECK FOR CLEARED ROWS
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                showScoreNotification(downData.getClearRow().getScoreBonus());
            }

            // REFRESH MOVING BRICK AND BOARD
            refreshBrick( downData.getViewData(), getGameController().getBoard().getBoardMatrix() );
        }
        gamePanel.requestFocus();  // Ensure the game panel retains focus for input
    }

    // BIND SCORE PROPERTY
    public void bindScore(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString());    // Bind score label text

        scoreProperty.addListener((obs, oldVal, newVal) -> {
            int level = (newVal.intValue() / pointsPerLvl) + 1;
            double newSpeed = Math.max(minSpeed, initialSpeed - ((level - 1) * speedDecPerLvl));
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
    public void setPlayerName(String name) { this.playerName = PlayerUtils.validatePlayerName(name); }
    public void setPlayerNameLabel(String name) { playerNameLabel.setText(name); }

    public void setGameMode(GameMode mode) { this.currentMode = mode; }
    public GameMode getGameMode() { return currentMode; }

    // Return the remaining seconds
    public int getTimeLeft() {
        return timeLeft.get(); // SimpleIntegerProperty
    }

    // Update the timer
    public void setTimeLeft(int seconds) {
        timeLeft.set(seconds);
    }

    // FOR TIME_LIMIT MODE BONUS TIME IF 4 CLEAR SAME TIME
    public void showBonusTimeLabel(String text) {
        bonusTimeLabel.setText(text);
        bonusTimeLabel.setVisible(true);

        bonusTimeLabel.setOpacity(1);
        bonusTimeLabel.setTranslateY(-100);

        FadeTransition fade = new FadeTransition(Duration.seconds(2), bonusTimeLabel);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> bonusTimeLabel.setVisible(false));
        fade.play();
    }

    //FOR COUNTDOWN TIME LIMIT MODE
    public void startCountDownTimer(int seconds) {
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

    //NORMAL TIMER FOR COUNTING UP
    public void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        timeLeft = new SimpleIntegerProperty(0); // start from 0
        timerLabel.textProperty().bind(timeLeft.asString());

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            timeLeft.set(timeLeft.get() + 1); // increment every second
        }));

        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    // SPEED AND LEVEL DISPLAY STUFF
    public void updateSpeedDisplay(int speed, int level) {
        currentSpeed = speed;
        currentLevel = level;

        if (speedLabel != null) {
            if (speed <= 100) { speedLabel.setText("Level MAX"); }
            else { speedLabel.setText("Level " + level + " - " + speed + "ms"); }
        }
    }

    public int getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(int speed) { this.currentSpeed = speed; }

    private void updateGameSpeed() {
        int score = eventListener != null ?
                getGameController().getBoard().getScore().getScore() : 0;

        // Calculate level based on score (every 500 points)
        int level = (score / pointsPerLvl) + 1;

        // Calculate speed: 400ms - (level-1)*25ms, minimum 100ms
        int newSpeed = Math.max(minSpeed, initialSpeed - ((level - 1) * speedDecPerLvl));

        // Only update if speed changed
        if (newSpeed != currentSpeed) {
            currentSpeed = newSpeed;
            currentLevel = level;
            updateSpeedDisplay(currentSpeed, currentLevel);

            // Update the timeline speed
            if (timeLine != null) {
                timeLine.stop();
                timeLine.getKeyFrames().clear();
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.millis(currentSpeed), e -> {
                            if (!isPause.get() && !isGameOver.get()) {
                                moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
                            }
                        })
                );
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.play();
            }
        }
    }

    // GAME OVER
    public void gameOver() {
        timeLine.stop();                                                     // Stop brick movement
        if (gameTimer != null) gameTimer.stop();                             // Stop timer
        int finalScore = getGameController().getBoard().getScore().getScore();

        HighScoreManager.addScore(playerName, finalScore, currentMode);                     // Add Score to HS with name
        int highScore = HighScoreManager.getHighScore(currentMode);                         // Get HS
        String highScorePlayerName = HighScoreManager.getHighScorePlayerName(currentMode);  // Get HS player name

        // Update keybinding text in game over panel
        String restartKey = formatKeys(keyBindings.getRestart());
        String menuKey = formatKeys(keyBindings.getMainMenu());
        gameOverPanel.updateKeybindingText(restartKey, menuKey);

        gameOverPanel.showGameOver(finalScore, highScore, highScorePlayerName);             // Show final score and HS with the GO panel
        isGameOver.set(true);                                                               // Mark game as over
    }

    // START NEW GAME
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        eventListener.createNewGame();            // Reset game logic
        gamePanel.requestFocus();
        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
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
        if (isGameOver.get()) return;    // Can't pause on game over
        boolean paused = isPause.get();
        isPause.set(!paused);
        if (!paused) {
            // Pause ON
            timeLine.stop();
            if (gameTimer != null) gameTimer.stop();  // Stop the timer
            pauseOverlay.setVisible(true);
        } else {
            // Pause OFF
            pauseOverlay.setVisible(false);
            timeLine.play();
            if (gameTimer != null) gameTimer.play();  // Resume the timer
        }
    }

    // PAUSE GAME
    public void pauseGame(ActionEvent actionEvent) { gamePanel.requestFocus(); }
}
