package com.comp2042.GameBoard;

import com.comp2042.logic.bricks.NextShapeInfo;
import com.comp2042.PlayerData.Score;
import com.comp2042.logic.bricks.BrickRotator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.Brick;
import java.awt.*;

/** Simple board implementation of {@link Board}.
 * <p>Manages the board matrix, the active/next/held bricks, score, and basic operations
 * such as movement, rotation, hard drop, hold/swap and row clearing.</p>*/
public class SimpleBoard implements Board {

    /** Board dimensions*/
    private final int width, height;

    /** Brick generator*/
    private final BrickGenerator brickGenerator;

    /** Brick rotator*/
    private final BrickRotator brickRotator;

    /** Holds the background n locked bricks*/
    private int[][] currentGameMatrix;

    /** Current brick position*/
    private Point currentOffset;

    /** Player score*/
    private final Score score;

    /** next brick*/
    private Brick nextBrick;

    /** current brick*/
    private Brick currentBrick;

    /** set held brick to null at start*/
    private Brick heldBrick = null;

    /** set swaped to null at start*/
    private boolean hasSwappedThisTurn = false;

    /** stet brick spawn location*/
    private static final Point SPAWN_POSITION = new Point(4, 2);

    /** helper for next brick*/
    private Brick generateNextBrick() {
        if (forcedBrickClass != null) {
            try {
                return forcedBrickClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return brickGenerator.getBrick(); // Fallback to random if reflection fails
            }
        }
        return brickGenerator.getBrick();
    }

    /** used for testing*/
    private Class<? extends Brick> forcedBrickClass = null;

    /** Set or clear the forced next-brick type.
     *  When a non-null class is provided, calls to {@code generateNextBrick()} will attempt to
     *  instantiate the provided class via reflection. Pass {@code null} to restore normal random generation.
     *  @param brickClass the Brick subclass to force for the next generated bricks, or {@code null} to disable*/
    public void setNextBrickType(Class<? extends Brick> brickClass) { this.forcedBrickClass = brickClass; }

    /** Create a SimpleBoard with given width and height.
     * @param width number of columns
     * @param height number of rows*/
    public SimpleBoard(int width, int height) {
        this(width, height, null);  // calls the 3-argument constructor with no forced block
    }

    /**Create a SimpleBoard, optionally forcing the next bricks to a specific class.
     * @param width number of columns
     * @param height number of rows
     * @param forcedBrickClass optional brick class to force for generated bricks (may be null)*/
    public SimpleBoard(int width, int height, Class<? extends Brick> forcedBrickClass) {
        this.width = width;
        this.height = height;
        this.forcedBrickClass = forcedBrickClass;

        currentGameMatrix = new int[width][height];         // Empty game matrix
        brickGenerator = new RandomBrickGenerator();        // Random brick generator
        brickRotator = new BrickRotator();                  // Handles rotation of current brick
        score = new Score();                                // Initialise score

        currentBrick = generateNextBrick();
        nextBrick = generateNextBrick();

        brickRotator.setBrick(currentBrick);          // Set current brick for rotation
        currentOffset = new Point(SPAWN_POSITION);    // Default spawn position
    }

    /** MOVE BRICK DOWN ONE STEP;
     * @return FALSE IF BLOCKED else tru*/
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

    /** MOVE BRICK LEFT ONE STEP;
     * @return FALSE IF BLOCKED else true*/
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

    /** MOVE BRICK RIGHT ONE STEP;
     * @return FALSE IF BLOCKED else true*/
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

    /** ROTATE CURRENT BRICK LEFT;
     * @return FALSE IF BLOCKED else true*/
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

    /** SPAWN NEW BRICK*/
    @Override
    public boolean createNewBrick() {
        currentBrick = nextBrick;                   // Move nextBrick into play
        brickRotator.setBrick(currentBrick);
        hasSwappedThisTurn = false;                 // TO CHECK SWAP OR NOT
        currentOffset = new Point(SPAWN_POSITION);

        nextBrick = generateNextBrick();

        return MatrixOperations.intersect(          // Return true if new brick collides immediately (GAME OVER)
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    /** Return a defensive copy of the internal board matrix.
     * @return copy of the current board matrix (int[row][col])*/
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /** create new game*/
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        heldBrick = null;
        hasSwappedThisTurn = false;
        createNewBrick();
    }

    /** gets th veiw data*/
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

    /** merges placed bricks to bg*/
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
    }

    /** clears row
     * @return clearRow*/
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /** gets current score
     * @return score*/
    @Override
    public Score getScore() {return score; }

    /** GET INFO ABOUT NEXT BRICK (FOR GUI PREVIEW)
     * @return next shap info*/
    @Override
    public NextShapeInfo getNextShapeInfo() {
        int[][] shape = nextBrick.getShapeMatrix().get(0); // Get the default orientation
        return new NextShapeInfo(shape, 0);
    }

    /** HARD DROP CURRENT BRICK TO LOWEST POSITION AND MERGE
     * @return true*/
    @Override
    public boolean hardDrop() {
        while (moveBrickDown()) { }     // Keep moving down until blocked
        mergeBrickToBackground();       // Lock brick into the background
        return true;
    }

    /** CALCULATE GHOST POSITION (WHERE BRICK WOULD LAND)
     * @return p positions*/
    public Point getHardDropPosition() {
        Point p = new Point(currentOffset);
        while (!MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), p.x, p.y + 1)) {
            p.y += 1; // Move down until collision
        }
        return p;
    }

    /** HELD BRICK
     * @return true*/
    @Override
    public boolean holdBrick() {
        if (hasSwappedThisTurn) return false;  // Can't hold/swap twice per brick

        if (heldBrick == null) {
            heldBrick = currentBrick;
            currentBrick = nextBrick;
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(SPAWN_POSITION);

            nextBrick = generateNextBrick();

        } else {
            // Already have a held brick swap with current
            Brick temp = currentBrick;
            currentBrick = heldBrick;
            heldBrick = temp;
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(SPAWN_POSITION);
        }

        hasSwappedThisTurn = true;
        return true;
    }

    /** SWAP WITH HELD
     * @return true*/
    @Override
    public boolean swapBrick() {
        if (hasSwappedThisTurn) return false;       // Can't hold/swap twice per brick
        if (heldBrick == null) return holdBrick();  // If no held brick, act like hold

        Brick temp = currentBrick;
        currentBrick = heldBrick;
        heldBrick = temp;
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(SPAWN_POSITION);

        hasSwappedThisTurn = true;
        return true;
    }

    /** GET HELD FOR GUI
     * @return GUI of next brick*/
    @Override
    public NextShapeInfo getHeldBrickInfo() {
        if (heldBrick == null) return null;
        int[][] shape = heldBrick.getShapeMatrix().get(0);
        return new NextShapeInfo(shape, 0);
    }


    /** CHECK IF CAN HOLD OR SWAP
     * @return not swaped yet*/
    @Override
    public boolean canHoldOrSwap() {
        return !hasSwappedThisTurn;
    }
}
