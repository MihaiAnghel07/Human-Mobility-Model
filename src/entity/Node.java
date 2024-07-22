package entity;

import java.util.Set;

public class Node {
    private final Home home;
    private final int speed;
    private final Set<Node> friends;
    private GenericCell currentCell;

    public Node(Home home, int speed, Set<Node> friends) {
        this.home = home;
        this.speed = speed;
        this.friends = friends;
        this.currentCell = home;
    }

    public Home getHome() {
        return home;
    }

    public int getSpeed() {
        return speed;
    }

    public Set<Node> getFriends() {
        return friends;
    }

    public GenericCell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(GenericCell currentCell) {
        this.currentCell = currentCell;
    }
}
