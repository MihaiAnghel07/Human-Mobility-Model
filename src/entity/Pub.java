package entity;

import java.util.HashSet;
import java.util.Set;

public final class Pub extends GenericCell {

    private final Set<Node> presentNodes;

    public Pub(int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate);
        presentNodes = new HashSet<>();
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
