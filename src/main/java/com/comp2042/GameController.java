package com.comp2042;

public class GameController implements InputEventListener {

    private final Board board = new SimpleBoard(25, 10);
    // Creats the game board

    private final GuiController viewGuiController;
    // Make ui and listen to events

    public GameController(GuiController c) {
        viewGuiController = c; // stores gui reference

        board.createNewBrick(); // gets first brick

        viewGuiController.setEventListener(this); // sends all events to this class
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData()); // start display
        viewGuiController.bindScore(board.getScore().scoreProperty()); // binds score to gui

        // Next brick preview
        viewGuiController.showNextBrick(board.getNextShapeInfo());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Trigger when brick moves down

        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            // Brick has landed ten merge into backgrond
            board.mergeBrickToBackground();

            // Check for and clear completed rows
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }

            // Create new brick then if collision on spawn, game over
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            // Refresh game board visuals
            viewGuiController.refreshGameBackground(board.getBoardMatrix());

            // Update the next brick preview after a new brick spawns
            viewGuiController.showNextBrick(board.getNextShapeInfo());

        } else {
            // Add score for user-controlled drop (not auto drop)
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        // Return updated view data
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
        board.newGame(); // resets the board
        viewGuiController.refreshGameBackground(board.getBoardMatrix()); // restart the game when restartng
        viewGuiController.showNextBrick(board.getNextShapeInfo()); // show initial next brick
    }
}
