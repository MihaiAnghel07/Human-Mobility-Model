package entity;

import org.apache.commons.math3.util.Pair;

import java.util.*;

public class Node {
    private final int id;
    private final int speed;
    private final GenericCell homeCell;
    private final GenericCell workCell;
    private final Map<CellType, Integer> activityWeight;
    private GenericCell currentCell;
    private GenericCell targetCell;
    private Map<Node, Integer> friends;
    private int timeToStay;
    private Queue<Pair<Integer, Integer>> path;

    public Node(int id, GenericCell homeCell, GenericCell work, int speed) {
        this.id = id;
        this.homeCell = homeCell;
        this.speed = speed;
        this.currentCell = homeCell;
        this.workCell = work;
        this.targetCell = null;
        this.activityWeight = new HashMap<>();
        this.activityWeight.put(CellType.HOME, 40);
        this.activityWeight.put(CellType.WORK, 40);
        this.activityWeight.put(CellType.PUB, 10);
        this.activityWeight.put(CellType.OTHER, 10);
        this.friends = new HashMap<>();
        this.timeToStay = 0;
    }

    public int getId() {
        return id;
    }

    public GenericCell getHomeCell() {
        return homeCell;
    }

    public int getSpeed() {
        return speed;
    }

    public Map<Node, Integer> getFriends() {
        return friends;
    }

    public void setFriends(Map<Node, Integer> friendsId) {
        this.friends = friendsId;
    }

    public GenericCell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(GenericCell currentCell) {
        this.currentCell = currentCell;
    }

    public GenericCell getTargetCell() {
        return targetCell;
    }

    public GenericCell getWorkCell() {
        return workCell;
    }

    public void setTargetCell(GenericCell targetCell) {
        this.targetCell = targetCell;
    }

    public Map<CellType, Integer> getActivityWeight() {
        return activityWeight;
    }

    public int getTimeToStay() {
        return timeToStay;
    }

    public void setTimeToStay(int timeToStay) {
        this.timeToStay = timeToStay;
    }

    public void addFriend(Node friend) {
        friends.put(friend, 0);
        friend.friends.put(this, 0);
    }
    public void removeFriend(Node friend) {
        friends.remove(friend);
        friend.friends.remove(this);
    }

    public Queue<Pair<Integer, Integer>> getPath() {
        return path;
    }

    public void setPath(Queue<Pair<Integer, Integer>> path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return id == node.id
                && speed == node.speed
                && Objects.equals(homeCell, node.homeCell)
                && Objects.equals(workCell, node.workCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, speed, homeCell, workCell);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", homeCell=" + homeCell +
//                ", speed=" + speed +
//                ", friends=" + friends +
//                ", currentCell=" + currentCell +
//                ", targetCell=" + targetCell +
//                ", activityWeight=" + activityWeight +
//                ", timeToStay=" + timeToStay +
                '}';
    }
}
