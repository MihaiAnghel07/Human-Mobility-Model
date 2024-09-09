package entity;

import java.util.HashSet;
import java.util.Set;

public final class Pub extends GenericCell {
    private final int id;
    private final Set<Node> presentNodes;

    public Pub(int id, int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate, CellType.PUB);
        this.id = id;
        this.presentNodes = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public Set<Node> getPresentNodes() {
        return presentNodes;
    }

    public void addNode(Node node) {
        presentNodes.add(node);
    }

    public boolean removeNode(Node node) {
        return presentNodes.remove(node);
    }

    public void clearPresentNodes() {
        presentNodes.clear();
    }

}
