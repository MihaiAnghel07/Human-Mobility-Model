package entity;

import java.util.Set;

public class Node {
    private final int id;
    private final Home home;
    private final int speed;
    private final Set<Integer> friendsId;
    private GenericCell currentCell;

    public Node(int id, Home home, int speed, Set<Integer> friendsId) {
        this.id = id;
        this.home = home;
        this.speed = speed;
        this.friendsId = friendsId;
        this.currentCell = home;
    }

    public int getId() {
        return id;
    }

    public Home getHome() {
        return home;
    }

    public int getSpeed() {
        return speed;
    }

    public Set<Integer> getFriendsId() {
        return friendsId;
    }

    public GenericCell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(GenericCell currentCell) {
        this.currentCell = currentCell;
    }
}
