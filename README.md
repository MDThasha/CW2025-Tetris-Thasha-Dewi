## CW2025 Software Maintenance Tetris Thasha Dewi 🎮

**maintaining and extending a re-implementation of a classic retro
game (Tetris).** 👨‍💻 

GitHub repository: https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi

Compilation:

Implemented and Working Properly: 

Implemented but Not Working Properly:

Features Not Implemented: 

New Java Classes:

Modified Java Classes:

****

    //Original Code
    //    public void bindScore(IntegerProperty integerProperty) {
    //    }

    //Edited code, making bricks fall faster as score increases
    public void bindScore(IntegerProperty scoreProperty) {
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            double newSpeed = Math.max(100, 400 - (newVal.intValue() / 500) * 25);
            //initial speed of 400ms increases by 25ms every 500 score increase, not less than 100ms

            javafx.application.Platform.runLater(() -> {
                boolean wasRunning = timeLine != null &&
                        timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;

                timeLine.stop();
                timeLine.getKeyFrames().setAll(new KeyFrame(
                        Duration.millis(newSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                )); //overwrite the initial speed dropping code with new speed

                if (wasRunning) timeLine.play();
            });
        });
    }
****

    public boolean createNewBrick() {
    Brick currentBrick = brickGenerator.getBrick();
    brickRotator.setBrick(currentBrick);
    currentOffset = new Point(4, 2); 
    //CHANGED THE SPAWN LOCATION OF THE BRICKS TO BE HIGHER (originally was y:10)
    return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }
****

Unexpected Problems:

