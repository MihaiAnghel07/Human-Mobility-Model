package entity;

import org.apache.commons.math3.util.Pair;

import java.util.*;

public class Node {
    private final int id;
    private final int speed;
    private final GenericCell homeCell;
    private final GenericCell workCell;
    private GenericCell currentCell;
    private GenericCell targetCell;
    private Set<Node> friends;
    private int timeToStay;
    private Map<CellType, Integer> activityWeight;
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
        this.friends = new HashSet<>();
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

    public Set<Node> getFriends() {
        return friends;
    }

    public void setFriends(Set<Node> friendsId) {
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

    public void setActivityWeight(Map<CellType, Integer> activityWeight) {
        this.activityWeight = activityWeight;
    }

    public int getTimeToStay() {
        return timeToStay;
    }

    public void setTimeToStay(int timeToStay) {
        this.timeToStay = timeToStay;
    }

    public void addFriend(Node friend) {
        friends.add(friend);
        friend.friends.add(this);
    }
    public void removeFriend(Node friend) {
        friends.remove(friend);
        friend.removeFriend(this);
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
        return id == node.id &&
                speed == node.speed &&
                Objects.equals(homeCell, node.homeCell) &&
//                Objects.equals(friends, node.friends) &&
                Objects.equals(currentCell, node.currentCell) &&
                Objects.equals(workCell, node.workCell) &&
                Objects.equals(targetCell, node.targetCell) &&
                Objects.equals(activityWeight, node.activityWeight) &&
                Objects.equals(timeToStay, node.timeToStay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, homeCell, speed, currentCell, workCell, targetCell, activityWeight, timeToStay);
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
