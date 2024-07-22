package context;

import entity.GenericCell;

import java.util.Arrays;
import java.util.Objects;

public final class Context {
    private final int x;
    private final int y;
    private final int minSpeed;
    private final int maxSpeed;
    private final GenericCell[][] map;

    public Context(int x, int y, int minSpeed, int maxSpeed) {
        this.x = x;
        this.y = y;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        map = new GenericCell[x][y];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GenericCell[][] getMap() {
        return map;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Context context = (Context) o;
        return x == context.x && y == context.y
                && Arrays.deepEquals(map, context.map)
                && minSpeed == context.minSpeed
                && maxSpeed == context.maxSpeed;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(x, y, minSpeed, maxSpeed);
        result = 31 * result + Arrays.deepHashCode(map);
        return result;
    }

    @Override
    public String toString() {
        return "Context{" +
                "x=" + x +
                ", y=" + y +
                ", map=" + Arrays.deepToString(map) +
                ", minSpeed=" + minSpeed +
                ", maxSpeed=" + maxSpeed +
                '}';
    }
}
