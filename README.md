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

            javafx.application.Platform.runLater(() -> {
                boolean wasRunning = timeLine != null &&
                        timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;

                timeLine.stop();
                timeLine.getKeyFrames().setAll(new KeyFrame(
                        Duration.millis(newSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                )); 
                if (wasRunning) timeLine.play();
            });
        });
    }

**The above shows the original code given and the modified code.** 

I wanted to make the bricks fall faster as the player plays the game, to do that I decided to use the score as a way to measure when the speed increases.

Thus, I made an equation to calculate the initial speed of 400ms to be increased by 25ms every time the user gets 500 score, to make sure the falling of bricks dont become to fast to the point of the game not being playable I made it so the max speed cap is not less than 100ms.

Everything below the equation is used to overwrite the initial speed with new speed, by first checking if the animation is running and not null, by checking it updates the timeline by pausing and restarting it to replace with the new speed. 

****

    public boolean createNewBrick() {
    Brick currentBrick = brickGenerator.getBrick();
    brickRotator.setBrick(currentBrick);
    currentOffset = new Point(4, 2); 
    return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

The Only changes I made above was to make the bricks spawn at y=2 instead of y=10. This makes it so the bricks fall from a higher place and allows users more room to play in. The gameover event will also only trigger at y=2 with this change.
****

Unexpected Problems:

