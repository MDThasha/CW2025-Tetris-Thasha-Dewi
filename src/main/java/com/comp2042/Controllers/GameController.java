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

public class GameController implements InputEventListener {

    private final Board board;
    private final GuiController viewGuiController;
    private final GameMode gameMode;

    // CONSTANTS
    private static final int TimeLimitDuration = 120;  // How long in sec for TIME_LIMIT mode

    // HELPER
    private void CheckAndApplyBonusTime(ClearRow clearRow) {
        if (gameMode == GameMode.TIME_LIMIT && clearRow != null && clearRow.getLinesRemoved() == 4) {
            Platform.runLater(() -> {
                int newTime = viewGuiController.getTimeLeft() + 15;
                viewGuiController.setTimeLeft(newTime);
                viewGuiController.showBonusTimeLabel("+15s");
            });
        }
    }

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

    public Board getBoard() {
        return board;
    }

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

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

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

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (board.holdBrick()) { viewGuiController.showHeldBrick(board.getHeldBrickInfo()); }
        return board.getViewData();
    }

    @Override
    public ViewData onSwapEvent(MoveEvent event) {
        if (board.swapBrick()) { viewGuiController.showHeldBrick(board.getHeldBrickInfo()); }
        return board.getViewData();
    }

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
