package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10); //SIZE OF THE BOARD
//CREATES BOARD
    private final GuiController viewGuiController;
//MAKES UI AND LISTENS TO EVENT INPUTS
        public GameController(GuiController c) {
            viewGuiController = c; // Store reference to GUI controller
            board.createNewBrick(); // Generate and display the first brick
            viewGuiController.setEventListener(this); // Register GameController as input listener
            viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData()); // Initialize game display
            viewGuiController.bindScore(board.getScore().scoreProperty()); // Link score logic to GUI score label
        }


    @Override
    public DownData onDownEvent(MoveEvent event) { //TRIGGER WHEN BRICK MOVES DOWN
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            } //IF CANT SPAWN BRICK GAME OVER

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
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
    //RESTARTS THE GAME WHEN RESTARTING
    }
}
