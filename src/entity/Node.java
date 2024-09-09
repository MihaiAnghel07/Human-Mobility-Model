package entity;

import java.util.*;

public class Node {
    private final int id;
    private final GenericCell home;
    private final int speed;
    private Set<Node> friends;
    private GenericCell currentCell;
    private GenericCell targetCell;
    private int timeToStay;
    private Map<String, Integer> activityWeight;

    public Node(int id, GenericCell home, int speed) {
        this.id = id;
        this.home = home;
        this.speed = speed;
        this.currentCell = home;
        this.targetCell = null;
        this.activityWeight = new HashMap<>();
        this.activityWeight.put("home", 40);
        this.activityWeight.put("pub", 10);
        this.activityWeight.put("work", 40);
        this.activityWeight.put("other", 10);
        this.friends = new HashSet<>();
        this.timeToStay = 0;
    }

    public int getId() {
        return id;
    }

    public GenericCell getHome() {
        return home;
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

    public void setTargetCell(GenericCell targetCell) {
        this.targetCell = targetCell;
    }

    public Map<String, Integer> getActivityWeight() {
        return activityWeight;
    }

    public void setActivityWeight(Map<String, Integer> activityWeight) {
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
                Objects.equals(home, node.home) &&
//                Objects.equals(friends, node.friends) &&
                Objects.equals(currentCell, node.currentCell) &&
                Objects.equals(targetCell, node.targetCell) &&
                Objects.equals(activityWeight, node.activityWeight) &&
                Objects.equals(timeToStay, node.timeToStay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, home, speed, currentCell, targetCell, activityWeight, timeToStay);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", home=" + home +
//                ", speed=" + speed +
//                ", friends=" + friends +
//                ", currentCell=" + currentCell +
//                ", targetCell=" + targetCell +
//                ", activityWeight=" + activityWeight +
//                ", timeToStay=" + timeToStay +
                '}';
    }
}
