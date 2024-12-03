package PacMan;

import java.util.Random;

public enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN;

    private static final Random PRNG = new Random();

    public static Direction randomDirection() {
        Direction[] directions = values();
        return directions[PRNG.nextInt(directions.length)];
    }
}
