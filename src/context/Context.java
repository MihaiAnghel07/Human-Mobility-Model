package context;

import entity.CellType;
import entity.GenericCell;
import entity.Node;
import entity.Pub;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public final class Context {
    private final int xDimension;
    private final int yDimension;
    private final int minSpeed;
    private final int maxSpeed;
    private final Set<Node> nodes;
    private final Set<Pub> pubs;
    private final Set<GenericCell> others;
    private final Set<GenericCell> obstacles;
    private final int maximumAllowedLastTimeSeen;
    private final int chancesToBecomeFriends;
    private final GenericCell[][] map;

    public Context(int xDimension, int yDimension, int minSpeed, int maxSpeed, Set<Node> nodes,
                   Set<Pub> pubs, Set<GenericCell> others, Set<GenericCell> obstacles, int maximumAllowedLastTimeSeen,
                   int chancesToBecomeFriends) {
        this.xDimension = xDimension;
        this.yDimension = yDimension;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.nodes = nodes;
        this.pubs = pubs;
        this.others = others;
        this.obstacles = obstacles;
        this.maximumAllowedLastTimeSeen = maximumAllowedLastTimeSeen;
        this.chancesToBecomeFriends = chancesToBecomeFriends;
        this.map = new GenericCell[xDimension][yDimension];

        // set the home and work places on map
        nodes.forEach(node -> {
            map[node.getHomeCell().getXCoordinate()][node.getHomeCell().getYCoordinate()] = node.getHomeCell();
            map[node.getWorkCell().getXCoordinate()][node.getWorkCell().getYCoordinate()] = node.getWorkCell();
        });

        // set pub places on map
        pubs.forEach(pub -> map[pub.getXCoordinate()][pub.getYCoordinate()] = pub);

        // set other places on map
        others.forEach(other -> map[other.getXCoordinate()][other.getYCoordinate()] = other);

        // set obstacles on map
        obstacles.forEach(obstacle -> map[obstacle.getXCoordinate()][obstacle.getYCoordinate()] = obstacle);

        // set empty cells
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == null) {
                    map[i][j] = new GenericCell(i, j, CellType.EMPTY);
                }
            }
        }
    }

    public int getXDimension() {
        return xDimension;
    }

    public int getYDimension() {
        return yDimension;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Pub> getPubs() {
        return pubs;
    }

    public Set<GenericCell> getOthers() {
        return others;
    }

    public Set<GenericCell> getObstacles() {
        return obstacles;
    }

    public int getMaximumAllowedLastTimeSeen() {
        return maximumAllowedLastTimeSeen;
    }

    public int getChancesToBecomeFriends() {
        return chancesToBecomeFriends;
    }

    public GenericCell[][] getMap() {
        return map;
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
        return xDimension == context.xDimension && yDimension == context.yDimension
                && Arrays.deepEquals(map, context.map)
                && minSpeed == context.minSpeed
                && maxSpeed == context.maxSpeed
                && nodes.equals(context.nodes)
                && pubs.equals(context.pubs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(xDimension, yDimension, minSpeed, maxSpeed, nodes, pubs);
        result = 31 * result + Arrays.deepHashCode(map);
        return result;
    }

    @Override
    public String toString() {
        return "Context{" + "\n" +
                "xDimension=" + xDimension + "\n" +
                ", yDimension=" + yDimension + "\n" +
                ", map=" + printMap() + "\n" +
                ", minSpeed=" + minSpeed + "\n" +
                ", maxSpeed=" + maxSpeed + "\n" +
                ", nodes=" + nodes + "\n" +
                ", pubs=" + pubs + "\n" +
                '}';
    }

    private String printMap() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GenericCell[] line : map) {
            stringBuilder.append(Arrays.toString(line)).append("\n");
        }

        return stringBuilder.toString();
    }
}
