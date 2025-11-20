package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Generates bricks randomly and provides a small peek queue for the next bricks. */
public class RandomBrickGenerator implements BrickGenerator {

    /** The master list of brick types available for random selection. */
    private final List<Brick> brickList;

    /** A short FIFO queue holding the upcoming bricks (peekable). */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /** Initialize the generator, populate available bricks and seed the next-bricks queue. */
    public RandomBrickGenerator() { // Class to insert all bricks and rotations
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    } //Gets random 2 next brick as back up

    /** Return and take the next brick from the queue; ensure the queue is replenished.
     * @return next brick,poll which retrieves and remove*/
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) { //if only 1 brick in queue then add another
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    /** Peek at the next brick without removing it from the queue.
     * @return next brick.peek which check what is next*/
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /** Return the runtime class of a randomly selected brick type (useful for tests/tools).
     * @return random brick as next*/
    public Class<? extends Brick> getRandomBrickClass() {
        Brick randomBrick = brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
        return randomBrick.getClass();
    }

}