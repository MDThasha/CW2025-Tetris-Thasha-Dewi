package com.comp2042.Controllers;

import com.comp2042.Event.EventSource;
import com.comp2042.Event.InputEventListener;
import com.comp2042.Event.MoveEvent;
import com.comp2042.GameBoard.*;
import com.comp2042.Event.GameMode;
import com.comp2042.GameBoard.ViewData;
import com.comp2042.Helper.PlayerUtils;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import javafx.application.Platform;

/** This File is used to control the main game logic and is mediator between the Board and the GUI
 * <p>Handles player input (implements {@code InputEventListener}), updates the board
 * state, applies scoring and bonuses, and instructs {@code GuiController} to refresh
 * the view and manage timers.</p>*/
public class GameController implements InputEventListener {

    /** The game board model holding bricks and background state.*/
    private final Board board;

    /** connects to guicontroller*/
    private final GuiController viewGuiController;

    /** Connects to the gamemode enum which has all the diff gamemode types*/
    private final GameMode gameMode;

    // CONSTANTS
    /** Duration (seconds) used for TIME_LIMIT game mode.*/
    private static final int TimeLimitDuration = 120;

    // HELPER
    /** Apply a bonus time of +15s when a Tetris (4-line clear) occurs in TIME_LIMIT mode.
     * @param clearRow the ClearRow result from the board; may be null
     * <p>This method posts GUI updates to the JavaFX application thread (via {@code Platform.runLater}).</p>*/
    private void CheckAndApplyBonusTime(ClearRow clearRow) {
        if (gameMode == GameMode.TIME_LIMIT && clearRow != null && clearRow.getLinesRemoved() == 4) {
            Platform.runLater(() -> {
                int newTime = viewGuiController.getTimeLeft() + 15;
                viewGuiController.setTimeLeft(newTime);
                viewGuiController.showBonusTimeLabel("+15s");
            });
        }
    }

    /** Create a new GameController and initialize the model and view for a new game.
     * @param c the GUI controller used to update the view and timers
     * @param playerName the player's name (validated before display)
     * @param mode the selected GameMode (affects board generation and timers)
     * <p>Sets up the Board (special handling for ALL_SAME_BLOCK as that need to only get 1 brick to be played), binds score updates,
     * shows the next brick preview and starts either a count-up timer or the TIME_LIMIT mode countdown timer.</p>*/
    public GameController(GuiController c, String playerName, GameMode mode) {
        this.viewGuiController = c;
        this.gameMode = mode;

        if (mode == GameMode.ALL_SAME_BLOCK) {
            RandomBrickGenerator gen = new RandomBrickGenerator();
            Class<? extends Brick> randomBrickClass = gen.getRandomBrickClass();
            this.board = new SimpleBoard(25, 10, randomBrickClass);
        }

        else { this.board = new SimpleBoard(25, 10);}

        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.showNextBrick(board.getNextShapeInfo());                   // Next brick preview
        viewGuiController.setPlayerName(PlayerUtils.validatePlayerName(playerName)); // Set player name unknown if no name

        Platform.runLater(() -> {
            if (gameMode == GameMode.TIME_LIMIT) {
                viewGuiController.startCountDownTimer(TimeLimitDuration);
            } else {
                viewGuiController.startTimer();
            }
        });
    }

    /** returns the board in managed by this controller*/
    public Board getBoard() { return board; }

    /** Handle "move down" event.
     * <p>If the current brick can move down, it advances and
     * awards 1 point if the source is the user, user manually moves it down.
     * If it cannot move down, the brick is merged into the background, (placed)
     * rows are cleared and scored, bonus time may be applied if it is TIME_LIMIT mode,
     * and a new brick is spawned (which may trigger game over if it collides with placed brick (the background).</p>
     * @param event the MoveEvent that triggered the action
     * @return a {@code DownData} object containing any cleared rows and the current view data*/
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                CheckAndApplyBonusTime(clearRow);  // Apply bonus time
            }

            if (board.createNewBrick()) { viewGuiController.gameOver(); }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            viewGuiController.showNextBrick(board.getNextShapeInfo());
        } else {
            if (event.getEventSource() == EventSource.USER) { board.getScore().add(1); }   // Add score
        }

        return new DownData(clearRow, board.getViewData());
    }

    /** Move the current brick one cell to the left and updates the render.
     * @param event the triggering MoveEvent
     * @return the updated {@code ViewData} to render*/
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /** Move the current brick one cell to the right and updates the render.
     * @param event the triggering MoveEvent
     * @return the updated {@code ViewData} to render*/
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /** Move the current brick one rotation and updates the render.
     * @param event the triggering MoveEvent
     * @return the updated {@code ViewData} to render*/
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /** Perform a hard drop: drop the current brick until it locks, then +3 score and clear rows if its triggered.
     * Updates the view and may trigger game over if spawning collides.
     * @param event the triggering MoveEvent
     * @return a {@code DownData} containing cleared rows (if any) and view data*/
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        board.hardDrop();         // drop until blocked and lock
        board.getScore().add(3);  // Add score

        ClearRow cleared = board.clearRows();
        if (cleared.getLinesRemoved() > 0) {
            board.getScore().add(cleared.getScoreBonus());
            CheckAndApplyBonusTime(cleared);  // Apply bonus time if applicable
        }

        boolean spawnCollision = board.createNewBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.showNextBrick(board.getNextShapeInfo());
        if (spawnCollision) { viewGuiController.gameOver(); }
        return new DownData(cleared, board.getViewData());
    }

    /** Hold the current brick (or swap it with the held brick) and update the view.
     * @param event the triggering MoveEvent
     * @return the updated {@code ViewData}*/
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (board.holdBrick()) { viewGuiController.showHeldBrick(board.getHeldBrickInfo()); }
        return board.getViewData();
    }

    /** Swap the current brick with held brick and update the view.
     * @param event the triggering MoveEvent
     * @return the updated {@code ViewData}*/
    @Override
    public ViewData onSwapEvent(MoveEvent event) {
        if (board.swapBrick()) { viewGuiController.showHeldBrick(board.getHeldBrickInfo()); }
        return board.getViewData();
    }

    /** Start a brand-new game by resetting the board state and updating the GUI.
     * <p>Resets model state, refreshes the background, shows the next/held bricks,
     * resets speed display and restarts the appropriate timer for the current game mode.</p>*/
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.showNextBrick(board.getNextShapeInfo());
        viewGuiController.showHeldBrick(null);
        viewGuiController.updateSpeedDisplay(GuiController.initialSpeed, 1);

        Platform.runLater(() -> {
            if (gameMode == GameMode.TIME_LIMIT) {
                viewGuiController.startCountDownTimer(TimeLimitDuration);    // countdown for Time_limit mode
            } else {
                viewGuiController.startTimer();                              // count-up for other modes
            }
        });
    }
}
