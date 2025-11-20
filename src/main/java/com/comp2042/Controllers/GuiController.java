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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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

/** Main GUI controller for the Tetris game.
 *
 * <p>Manages the JavaFX view (grid, previews, overlays), keyboard input, timers,
 * visual notifications, and coordinates with the game logic via an InputEventListener.</p>*/
public class GuiController implements Initializable {

    // GAME PANELS
    /** GridPane that shows the static game board cells (visual background).*/
    @FXML private GridPane gamePanel;

    /** GridPane for the currently falling brick (moving brick visuals).*/
    @FXML private GridPane brickPanel;

    /** Pane used to draw ghost-preview rectangles.*/
    @FXML private AnchorPane ghostPane;

    /** Overlay displayed when the game is paused.*/
    @FXML private AnchorPane pauseOverlay;

    /** Panel shown on game over (injected custom control).*/
    @FXML private GameOverPanel gameOverPanel;

    // BRICK PREVIEW UI
    /** Container for next / held brick previews.*/
    @FXML private AnchorPane nextBrickContainer, holdBrickContainer;

    // LABELS
    /** Labels displayed in the UI: score, timer, player name, pause, etc.*/
    @FXML private Label scoreLabel, timerLabel, playerNameLabel, pauseKeybindLabel;

    // CONTROLS
    /** Labels showing keybinding names for controls.*/
    @FXML private Label restartLabel, rotateLabel, moveLeftLabel, moveRightLabel,
                        moveDownLabel, hardDropLabel, mainMenuLabel, pauseLabel,
                        holdLabel, swapLabel;

    // BONUS NOTIFICATIONS
    /** Pane used as the absolute-position container for notifications (popups).*/
    @FXML private Pane groupNotification;

    /** Group used for time-bonus UI (in FXML).*/
    @FXML private Group TimeBonus;

    /** Label used to show bonus time text (e.g. "+15s").*/
    @FXML private Label bonusTimeLabel;

    // SPEED/LEVEL DISPLAY
    /** Label that shows speed and level.*/
    @FXML private Label speedLabel;

    /** Starting Speed in ms*/
    private int currentSpeed = 400;

    /** Starting Level*/
    private int currentLevel = 1;

    // CONSTANTS
    /** Brick pixel size on screen*/
    private static final int brickSize = 20;

    /** Ghost brick offset so it looks aligned*/
    private static final int ghostYOffset = 10;

    /** Brick panel offset so it looks aligned*/
    private static final int brickPanelYOffset = -42;

    /** Starting speed in ms*/
    public static final int initialSpeed = 400;

    /** Maximum speed (fastest)*/
    private static final int minSpeed = 100;

    /** Speed increase per level*/
    private static final int speedDecPerLvl = 25;
    
    /** Additional points needed per level*/
    private static final int pointsPerLvl = 500;

    /** Random event manager that triggers game events.*/
    private RandomEventManager eventManager;

    /** Random event Blackout overlay in game*/
    private Rectangle blackoutOverlay;

    /** Random event will sometimes change
     * the speed this is to save the speed and level so that random event wont effect player level and base speed*/
    private int savedSpeed, savedLevel;

    /** temporary speed for a random event*/
    private boolean isTemporarySpeed = false;

    // STATIC REFERENCE
    /** current Controller*/
    public static GuiController currentController;

    // RECTANGLE ARRAYS (Visuals)
    /** Fixed bricks on board*/
    private Rectangle[][] displayMatrix;

    /** Currently falling brick*/
    private Rectangle[][] rectangles;

    /** Ghost brick preview*/
    private Rectangle[][] ghostRectangles;

    // GAME STATE

    /** Listens for random events*/
    private InputEventListener eventListener;

    /** Main timeline*/
    private Timeline timeLine;

    /** Gameplay timer*/
    private Timeline gameTimer;

    /** Time left for Time limit mode*/
    private IntegerProperty timeLeft;

    // GAME STATUS FLAGS

    /** Flag if game is paused*/
    private final BooleanProperty isPause = new SimpleBooleanProperty();

    /** Flag if game is over*/
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // GAME INFO

    /** Player name for the game used for score and highscores*/
    private String playerName;

    /** Current mode of the game*/
    private GameMode currentMode;

    // KEYBINDINGS

    /** Set instance for keybind*/
    private KeyBindings keyBindings = KeyBindings.getInstance();

    /** Get current keybinds
     * @return keyBindings*/
    public KeyBindings getKeyBindings() { return keyBindings; }

    /** Set the keybindings */
    public void setKeyBindings(KeyBindings bindings) { this.keyBindings = bindings; }

    // SET EVENT LISTENER

    /** Listens for random events to happen*/
    public void setEventListener(InputEventListener eventListener) { this.eventListener = eventListener; }

    // HELPERS

    /** Helper for getting the game controller
     * @returns (GameController) eventlistener*/
    private GameController getGameController() { return (GameController) eventListener; }

    /** Finds out the game layout without vgap
     * @param xPosition
     * @returns layout X*/
    private double calculateBrickLayoutX(int xPosition) {
        return gamePanel.getLayoutX() + xPosition * brickPanel.getVgap() + xPosition * brickSize; }

    /** Finds out the game layout without vgap
     * @param yPosition
     * @returns layout Y*/
    private double calculateBrickLayoutY(int yPosition) {
        return brickPanelYOffset + gamePanel.getLayoutY() + yPosition * brickPanel.getHgap() + yPosition * brickSize; }

    /** When player clears row this shows te score bonus in the center of the game
     * @param scoreBonus */
    private void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);
        notificationPanel.setManaged(false);

        // compute gamePanel center in scene, convert to groupNotification local coords
        javafx.geometry.Bounds boardBounds = gamePanel.localToScene(gamePanel.getBoundsInLocal());
        double sceneCenterX = boardBounds.getMinX() + boardBounds.getWidth() / 2.0;
        double sceneCenterY = boardBounds.getMinY() + boardBounds.getHeight() / 2.0;
        javafx.geometry.Point2D local = groupNotification.sceneToLocal(sceneCenterX, sceneCenterY);

        // center the notification on the game board and offset a bit to the right/down
        double w = notificationPanel.prefWidth(-1);
        double h = notificationPanel.prefHeight(-1);
        double offsetRight = -120; // tweak
        double offsetDown = -180;  // tweak

        notificationPanel.setLayoutX(local.getX() - w / 2.0 + offsetRight);
        notificationPanel.setLayoutY(local.getY() - h / 2.0 + offsetDown);

        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    /** Format the keybindings so no clutter
     * @param keys */
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

    /** JavaFX initialization hook called after FXML injection.
     * Loads fonts, sets up focus and key handler, initializes UI labels and speed display.
     * @param location FXML location
     * @param resources resources bundle */
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

    /** Handle keyboard input for gameplay.
     * <p>Maps key presses to game actions (move/rotate/drop/hold/pause/etc.) using KeyBindings,
     * calls eventListener methods, and consumes the event on handling.</p>
     * @param keyEvent the key event from the scene*/
    private void handleKeyPress(KeyEvent keyEvent) {
        if (!isPause.get() && !isGameOver.get()) {

            // MOVE BRICK LEFT
            if (keyBindings.getMoveLeft().contains(keyEvent.getCode())) {
                // Check if controls are reversed
                if (eventManager != null && eventManager.areControlsReversed()) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)),
                            getGameController().getBoard().getBoardMatrix()
                    );
                } else {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                            getGameController().getBoard().getBoardMatrix()
                    );
                }
                keyEvent.consume();
            }

            // MOVE BRICK RIGHT
            if (keyBindings.getMoveRight().contains(keyEvent.getCode())) {
                // Check if controls are reversed
                if (eventManager != null && eventManager.areControlsReversed()) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)),
                            getGameController().getBoard().getBoardMatrix()
                    );
                } else {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)),
                            getGameController().getBoard().getBoardMatrix()
                    );
                }
                keyEvent.consume();
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
                        new MoveEvent(EventType.DROP, EventSource.USER)
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
                refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)),
                        getGameController().getBoard().getBoardMatrix()
                );
                keyEvent.consume();
            }

            // SWAP BRICK
            if (keyBindings.getSwap().contains(keyEvent.getCode())) {
                refreshBrick(eventListener.onSwapEvent(new MoveEvent(EventType.SWAP, EventSource.USER)),
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

    /** Return a short, user-friendly name for a KeyCode (arrows, space, tab, etc.).*/
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

    /** Update UI labels to reflect current keybindings.
     * Reads KeyBindings and updates each control label text shown in the in-game UI.*/
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

    /** Initialize the visual game board and start the main game loop.
     * <p>Creates rectangles for the static board, falling brick, and ghost preview, positions
     * the brick panel, sets up the Timeline for automatic falling and starts the RandomEventManager.</p>
     * @param boardMatrix the model board matrix used to populate the background grid
     * @param brick view data for the current falling brick*/
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
        initEventManager();
    }

    /** Colours for bricks*/
    private static final Color[] COLORS = {
            Color.TRANSPARENT, Color.AQUA, Color.BLUEVIOLET, Color.DARKGREEN, Color.YELLOW, Color.RED, Color.BEIGE, Color.BURLYWOOD
    };

    /** Get a random colour for brick*/
    private Paint getFillColor(int i) { return (i >= 0 && i < COLORS.length) ? COLORS[i] : Color.WHITE; }

    /** Set colour for the brick
     * @param color the colour
     * @param rectangle the brick*/
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(6); // Rounded edges
        rectangle.setArcWidth(6);
    }


    /** Render a brick preview (next/hold) inside a container AnchorPane.
     * @param shapeInfo next/held shape information (may be null)
     * @param container the AnchorPane to draw the preview into */
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

    /** Shows next brick in panel*/
    public void showNextBrick(NextShapeInfo nextShapeInfo) {
        showBrickPreview(nextShapeInfo, nextBrickContainer);
    }

    /** Show held brick in panel*/
    public void showHeldBrick(NextShapeInfo heldShapeInfo) {
        showBrickPreview(heldShapeInfo, holdBrickContainer);
    }

    /** Refresh moving brick visuals and ghost preview, then update background.
     * <p>Updates rectangles for the falling brick and ghost preview based on ViewData and
     * repositions the brickPanel.</p>
     * @param brick the ViewData for the current falling brick
     * @param board the board matrix for refreshing the background*/
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

    /** Update the displayMatrix rectangles to match the board model.
     * @param board the model board matrix*/
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /** Request a downward move via the eventListener and handle cleared rows and UI refresh.
     * @param event the MoveEvent describing the source (USER or THREAD)*/
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

    /** binds score to game and add a UI for the score in the game
     * @param scoreProperty */
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

    /** set player name as valid name*/
    public void setPlayerName(String name) { this.playerName = PlayerUtils.validatePlayerName(name); }

    /** sets player name as text*/
    public void setPlayerNameLabel(String name) { playerNameLabel.setText(name); }

    /** sets the game mode*/
    public void setGameMode(GameMode mode) { this.currentMode = mode; }

    /** gets game mode
     * @return the current mode*/
    public GameMode getGameMode() { return currentMode; }


    /** gets time left
     * @return remianing time*/
    public int getTimeLeft() {
        return timeLeft.get(); // SimpleIntegerProperty
    }

    /** Sets the timer
     * @param seconds*/
    public void setTimeLeft(int seconds) {
        timeLeft.set(seconds);
    }

    /** Display a time-bonus notification (e.g. "+15s") centered over the game board is 4 row clear.
     * @param text the text to display
     * The notification is added to the notification pane and removed after animation.*/
    public void showBonusTimeLabel(String text) {
        NotificationPanel notificationPanel = new NotificationPanel(text);
        notificationPanel.setManaged(false);

        javafx.geometry.Bounds boardBounds = gamePanel.localToScene(gamePanel.getBoundsInLocal());
        double sceneCenterX = boardBounds.getMinX() + boardBounds.getWidth() / 2.0;
        double sceneCenterY = boardBounds.getMinY() + boardBounds.getHeight() / 2.0;
        javafx.geometry.Point2D local = groupNotification.sceneToLocal(sceneCenterX, sceneCenterY);

        // center the notification on the game board and offset a bit to the right/down
        double w = notificationPanel.prefWidth(-1);
        double h = notificationPanel.prefHeight(-1);
        double offsetRight = -120; // tweak
        double offsetDown = -140;  // tweak

        notificationPanel.setLayoutX(local.getX() - w / 2.0 + offsetRight);
        notificationPanel.setLayoutY(local.getY() - h / 2.0 + offsetDown);

        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }

    /** Start a countdown timer for TIME_LIMIT mode.
     * @param seconds initial countdown seconds*/
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

    /** Start timer for count up for all modes but Time_Limit*/
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

    /** Compute level and speed from current score and update timeline if speed changed.
     * Does nothing when a temporary speed event is active.*/
    public void updateSpeedDisplay(int speed, int level) {
        currentSpeed = speed;
        currentLevel = level;

        if (speed <= minSpeed && !isTemporarySpeed) {
            speedLabel.setText("Level MAX");
        } else {
            speedLabel.setText("Level " + level + " - " + speed + "ms");
        }
    }

    /** get current speed to save if random event changes it
     * @return currentSpeed*/
    public int getCurrentSpeed() { return currentSpeed; }

    /** Set the current speed after event
     * @param speed */
    public void setCurrentSpeed(int speed) { this.currentSpeed = speed; }

    /** for speed event it updates the game speed atfer the event (bricks fall as same speed as before event)*/
    private void updateGameSpeed() {
        if (isTemporarySpeed) {
            return;
        }

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

    /** Initialize and start the random event manager used during gameplay.*/
    public void initEventManager() {
        eventManager = new RandomEventManager(this);
        eventManager.start();
    }

    /** Show event notification to player*/
    public void showEventNotification(String message, String type) {
        showSimplePopup(message, type);
    }

    /** Show event pop up when event is triggered.
     * @param message the message to display
     * @param type "warning" or "success" (affects color)
     * Runs on the JavaFX Application Thread via Platform.runLater.*/
    public void showSimplePopup(String message, String type) {
        javafx.application.Platform.runLater(() -> {
            // Create popup label
            Label popup = new Label(message);
            popup.getStyleClass().add("popupStyle"); // optional CSS class
            popup.setTextFill(Color.WHITE);

            // color by type
            if ("warning".equals(type)) popup.setTextFill(Color.web("#FF6B6B"));
            else if ("success".equals(type)) popup.setTextFill(Color.web("#51CF66"));

            // Make sized to content
            popup.setWrapText(true);
            popup.setMaxWidth(400);

            // Add to the groupNotification (already in your FXML)
            groupNotification.getChildren().add(popup);

            // Ensure layout passes so pref sizes are available, then center in scene
            popup.applyCss();
            popup.layout();

            // Center in Scene (fallback to parent if Scene not ready)
            if (popup.getScene() != null) {
                double w = popup.prefWidth(-1);
                double h = popup.prefHeight(-1);
                double sceneW = popup.getScene().getWidth();
                double sceneH = popup.getScene().getHeight();
                popup.setLayoutX((sceneW - w) / 2.0);
                popup.setLayoutY((sceneH - h) / 2.0);
            } else if (popup.getParent() != null) {
                double pw = popup.getParent().getLayoutBounds().getWidth();
                double ph = popup.getParent().getLayoutBounds().getHeight();
                double w = popup.prefWidth(-1);
                double h = popup.prefHeight(-1);
                popup.setLayoutX((pw - w) / 2.0);
                popup.setLayoutY((ph - h) / 2.0);
            }

            // Blink timeline: show, hide, show, hide -> remove
            Timeline blink = new Timeline(
                    new KeyFrame(Duration.ZERO,            e -> popup.setVisible(true)),
                    new KeyFrame(Duration.seconds(0.35),   e -> popup.setVisible(false)),
                    new KeyFrame(Duration.seconds(0.7),    e -> popup.setVisible(true)),
                    new KeyFrame(Duration.seconds(1.05),   e -> popup.setVisible(false)),
                    new KeyFrame(Duration.seconds(1.1),    e -> groupNotification.getChildren().remove(popup))
            );
            blink.setCycleCount(1);
            blink.play();
        });
    }

    /** Activate blackout effect - darkens bottom portion of screen*/
    public void activateBlackout() {
        if (blackoutOverlay == null) {
            blackoutOverlay = new Rectangle();
            blackoutOverlay.setFill(Color.BLACK);
            blackoutOverlay.setOpacity(0.95);

            // Position at bottom half of game panel
            double panelHeight = gamePanel.getHeight();
            double panelWidth = gamePanel.getWidth();

            blackoutOverlay.setWidth(panelWidth > 0 ? panelWidth : 200);
            blackoutOverlay.setHeight((panelHeight > 0 ? panelHeight : 500) / 2);
            blackoutOverlay.setLayoutX(gamePanel.getLayoutX());
            blackoutOverlay.setLayoutY(gamePanel.getLayoutY() + (panelHeight > 0 ? panelHeight : 500) / 2);

            // Add to the parent of gamePanel
            if (gamePanel.getParent() instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) gamePanel.getParent()).getChildren().add(blackoutOverlay);
            }
        }
        blackoutOverlay.setVisible(true);
    }

    /** Deactivate blackout effect*/
    public void deactivateBlackout() {
        if (blackoutOverlay != null) {
            blackoutOverlay.setVisible(false);
        }
    }

    /** Set temporary speed (for speed boost event
     * @param speed new temporary speed as 90 ms)*/
    public void setTemporarySpeed(int speed) {
        if (!isTemporarySpeed) {
            savedSpeed = currentSpeed;
            isTemporarySpeed = true;
        }

        currentSpeed = speed;

        if (timeLine != null) {
            timeLine.stop();
            timeLine.getKeyFrames().clear();
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.millis(speed), e -> {
                        if (!isPause.get() && !isGameOver.get()) {
                            moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
                        }
                    })
            );
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        }

        updateSpeedDisplay(speed, currentLevel);
    }

    /** Restore normal speed after event*/
    public void restoreNormalSpeed() {
        if (isTemporarySpeed) {
            isTemporarySpeed = false;
            currentSpeed = savedSpeed;

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

            updateSpeedDisplay(currentSpeed, currentLevel);
        }
    }

    /** Get the event manager
     * @return random event*/
    public RandomEventManager getEventManager() {
        return eventManager;
    }

    /** GAME OVER marks gameover flag*/
    public void gameOver() {
        timeLine.stop();                                                     // Stop brick movement
        if (gameTimer != null) gameTimer.stop();                             // Stop timer
        if (eventManager != null) eventManager.stop();
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

    /** new game resets game
     * @param actionEvent */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        if (eventManager != null) eventManager.stop();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
        gameOverPanel.reset();
        initEventManager(); // Restart event system
    }

    /** stops the timeline when leaving game*/
    public void stopTimeline() {
        if (timeLine != null) {
            timeLine.stop();
        }
        if (eventManager != null) {
            eventManager.stop();
        }
    }

    /** pauses game on pause*/
    public void togglePause() {
        if (isGameOver.get()) return;
        boolean paused = isPause.get();
        isPause.set(!paused);
        if (!paused) {
            // Pause ON
            timeLine.stop();
            if (gameTimer != null) gameTimer.stop();
            if (eventManager != null) eventManager.pause();
            pauseOverlay.setVisible(true);
        } else {
            // Pause OFF
            pauseOverlay.setVisible(false);
            timeLine.play();
            if (gameTimer != null) gameTimer.play();
            if (eventManager != null) eventManager.resume();
        }
    }

    /** Handler to ensure the game panel regains focus after a pause UI action.
     * @param actionEvent the UI event (unused)*/
    public void pauseGame(ActionEvent actionEvent) { gamePanel.requestFocus(); }
}
