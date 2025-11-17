package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import javafx.application.Platform;

public class GameController implements InputEventListener {

    private final Board board;
    private final GuiController viewGuiController;
    public static GuiController currentController;

    private final String playerName;
    private final GameMode gameMode;

    public String getPlayerName() {
        return playerName;
    }

    public GameController(GuiController c, String playerName, GameMode mode) {

        this.viewGuiController = c;
        this.playerName = (playerName == null || playerName.isEmpty()) ? "Unknown" : playerName;
        this.gameMode = mode;

        if (mode == GameMode.ALL_SAME_BLOCK) {
            RandomBrickGenerator gen = new RandomBrickGenerator();
            Class<? extends Brick> randomBrickClass = gen.getRandomBrickClass();
            this.board = new SimpleBoard(25, 10, randomBrickClass);
        } else {
            this.board = new SimpleBoard(25, 10);
        }

        viewGuiController.setEventListener(this); // Sends all events to this class
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData()); // Start display
        viewGuiController.bindScore(board.getScore().scoreProperty()); // Binds score to gui
        viewGuiController.showNextBrick(board.getNextShapeInfo()); // Next brick preview

        viewGuiController.setPlayerName(playerName);
        currentController = c;
    }

    public Board getBoard() {
        return board;
    }

    public void stopGame() {
        viewGuiController.stopTimeline();
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        // drop until blocked and lock
        board.hardDrop();
        board.getScore().add(3);  // Add score

        ClearRow cleared = board.clearRows(); // clear Row
        if (cleared.getLinesRemoved() > 0) {
            board.getScore().add(cleared.getScoreBonus());

            // TIMER MODE: Add 15s if player clears 4 rows at once
            if (gameMode == GameMode.TIME_LIMIT && cleared.getLinesRemoved() == 4) {
                Platform.runLater(() -> {
                    int newTime = viewGuiController.getTimeLeft() + 15;
                    viewGuiController.setTimeLeft(newTime);
                    viewGuiController.showBonusTimeLabel("+15s");
                });
            }
        }

        // spawn next brick
        boolean spawnCollision = board.createNewBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.showNextBrick(board.getNextShapeInfo());

        if (spawnCollision) {
            viewGuiController.gameOver();
        }

        return new DownData(cleared, board.getViewData());
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

                if (gameMode == GameMode.TIME_LIMIT && clearRow.getLinesRemoved() == 4) {
                    Platform.runLater(() -> {
                        int newTime = viewGuiController.getTimeLeft() + 15;
                        viewGuiController.setTimeLeft(newTime);
                        viewGuiController.showBonusTimeLabel("+15s1");
                    });
                }
            }

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            viewGuiController.showNextBrick(board.getNextShapeInfo());
        } else {
            if (event.getEventSource() == EventSource.USER) { // Add score
                board.getScore().add(1);
            }
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
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.showNextBrick(board.getNextShapeInfo());

        if (gameMode == GameMode.TIME_LIMIT) {
            Platform.runLater(() -> viewGuiController.startTimer(120));  // Time limit for Time_limit mode
        }
    }
}
