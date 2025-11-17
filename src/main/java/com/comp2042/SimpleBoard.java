package com.comp2042;

import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.Brick;
import java.awt.*;

public class SimpleBoard implements Board {

    // BOARD DIMENSIONS
    private final int width, height;

    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;

    private int[][] currentGameMatrix;     // Holds the background n locked bricks
    private Point currentOffset;           // Current brick position
    private final Score score;             // Player score

    private Brick nextBrick;
    private Brick currentBrick;

    private Brick heldBrick = null;
    private boolean hasSwappedThisTurn = false;

    private Class<? extends Brick> forcedBrickClass = null;
    public void setNextBrickType(Class<? extends Brick> brickClass) { this.forcedBrickClass = brickClass; }

    // INITIALISE BOARD AND BRICKS
    public SimpleBoard(int width, int height) {
        this(width, height, null);  // calls the 3-argument constructor with no forced block
    }

    public SimpleBoard(int width, int height, Class<? extends Brick> forcedBrickClass) {
        this.width = width;
        this.height = height;
        this.forcedBrickClass = forcedBrickClass;

        currentGameMatrix = new int[width][height];         // Empty game matrix
        brickGenerator = new RandomBrickGenerator();        // Random brick generator
        brickRotator = new BrickRotator();                  // Handles rotation of current brick
        score = new Score();                                // Initialise score

        if (forcedBrickClass != null) {
            try {
                currentBrick = forcedBrickClass.getDeclaredConstructor().newInstance();
                nextBrick    = forcedBrickClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            currentBrick = brickGenerator.getBrick();
            nextBrick    = brickGenerator.getBrick();
        }

        brickRotator.setBrick(currentBrick);                // Set current brick for rotation
        currentOffset = new Point(4, 2);              // Default spawn position
    }

    // MOVE BRICK DOWN ONE STEP; RETURN FALSE IF BLOCKED
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1); // Move down by 1
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false; // Cannot move down
        } else {
            currentOffset = p;        // Update position
            return true;
        }
    }

    // MOVE BRICK LEFT ONE STEP; RETURN FALSE IF BLOCKED
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);  // Move left by 1
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    // MOVE BRICK RIGHT ONE STEP; RETURN FALSE IF BLOCKED
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0); // Move right by 1
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    // ROTATE CURRENT BRICK LEFT; RETURN FALSE IF BLOCKED
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape(); // Get next rotation
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) { return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition()); // Apply rotation
            return true;
        }
    }

    // SPAWN NEW BRICK
    @Override
    public boolean createNewBrick() {
        currentBrick = nextBrick;                   // Move nextBrick into play
        brickRotator.setBrick(currentBrick);
        hasSwappedThisTurn = false;                 // TO CHECK SWAP OR NOT
        currentOffset = new Point(4, 2);
        if (forcedBrickClass != null) {
            try {
                nextBrick = forcedBrickClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nextBrick = brickGenerator.getBrick();
        }
        return MatrixOperations.intersect(          // Return true if new brick collides immediately (GAME OVER)
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    // GET CURRENT BOARD MATRIX (INCLUDES LOCKED BRICKS)
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        Point ghost = getHardDropPosition();    // Calculate ghost position
        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y,
                getNextShapeInfo().getShape(),
                ghost
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        heldBrick = null;
        hasSwappedThisTurn = false;
        createNewBrick();
    }

    // GET INFO ABOUT NEXT BRICK (FOR GUI PREVIEW)
    @Override
    public NextShapeInfo getNextShapeInfo() {
        int[][] shape = nextBrick.getShapeMatrix().get(0); // Get the default orientation
        return new NextShapeInfo(shape, 0);
    }

    // HARD DROP CURRENT BRICK TO LOWEST POSITION AND MERGE
    @Override
    public boolean hardDrop() {
        while (moveBrickDown()) { }     // Keep moving down until blocked
        mergeBrickToBackground();       // Lock brick into the background
        return true;
    }

    // CALCULATE GHOST POSITION (WHERE BRICK WOULD LAND)
    public Point getHardDropPosition() {
        Point p = new Point(currentOffset);
        while (!MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), p.x, p.y + 1)) {
            p.y += 1; // Move down until collision
        }
        return p;
    }

    //HELD BRICK
    @Override
    public boolean holdBrick() {
        if (hasSwappedThisTurn) return false;  // Can't hold/swap twice per brick

        if (heldBrick == null) {
            heldBrick = currentBrick;
            currentBrick = nextBrick;
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(4, 2);

            if (forcedBrickClass != null) {
                try {
                    nextBrick = forcedBrickClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                nextBrick = brickGenerator.getBrick();
            }
        } else {
            // Already have a held brick swap with current
            Brick temp = currentBrick;
            currentBrick = heldBrick;
            heldBrick = temp;
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(4, 2);
        }

        hasSwappedThisTurn = true;
        return true;
    }

    // SWAP WITH HELD
    @Override
    public boolean swapBrick() {
        if (hasSwappedThisTurn) return false;       // Can't hold/swap twice per brick
        if (heldBrick == null) return holdBrick();  // If no held brick, act like hold

        Brick temp = currentBrick;
        currentBrick = heldBrick;
        heldBrick = temp;
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 2);

        hasSwappedThisTurn = true;
        return true;
    }

    // GET HELD FOR GUI
    @Override
    public NextShapeInfo getHeldBrickInfo() {
        if (heldBrick == null) return null;
        int[][] shape = heldBrick.getShapeMatrix().get(0);
        return new NextShapeInfo(shape, 0);
    }

    // CHECK IF CAN HOLD OR SWAP
    @Override
    public boolean canHoldOrSwap() {
        return !hasSwappedThisTurn;
    }
}
