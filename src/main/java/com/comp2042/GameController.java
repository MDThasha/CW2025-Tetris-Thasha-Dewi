package com.comp2042;

public class GameController implements InputEventListener {

    private final Board board = new SimpleBoard(25, 10); // Creats the game board
    private final GuiController viewGuiController; // Make ui and listen to events
    public static GuiController currentController;

    public GameController(GuiController c) {
        viewGuiController = c; // Stores gui reference
        board.createNewBrick(); // Gets first brick
        viewGuiController.setEventListener(this); // Sends all events to this class
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData()); // Start display
        viewGuiController.bindScore(board.getScore().scoreProperty()); // Binds score to gui
        viewGuiController.showNextBrick(board.getNextShapeInfo()); // Next brick preview

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

        // clear rows and award score
        ClearRow cleared = board.clearRows();
        if (cleared.getLinesRemoved() > 0) {
            board.getScore().add(cleared.getScoreBonus());
        }

        // spawn next brick (and check spawn collision -> game over)
        boolean spawnCollision = board.createNewBrick();
        // refresh GUI background + next preview because board changed
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.showNextBrick(board.getNextShapeInfo());

        if (spawnCollision) {
            viewGuiController.gameOver();
        }

        // return ClearRow then ViewData (same order used elsewhere)
        return new DownData(cleared, board.getViewData());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) { // Trigger when brick moves down
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground(); // Brick has landed ten merge into backgrond
            clearRow = board.clearRows(); // Check for and clear completed rows

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }

            if (board.createNewBrick()) { // Create new brick then if collision on spawn, game over
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix()); // Refresh game board visuals
            viewGuiController.showNextBrick(board.getNextShapeInfo()); // Update the next brick preview after a new brick spawns
        }

        else {
            if (event.getEventSource() == EventSource.USER) { // Add score for user-controlled drop (not auto drop)
                board.getScore().add(1);
            }
        }

        return new DownData(clearRow, board.getViewData()); // Return updated view data
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
    }
}
