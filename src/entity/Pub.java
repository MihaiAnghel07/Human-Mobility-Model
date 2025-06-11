package entity;

import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Pub pub = (Pub) o;
        return id == pub.id && Objects.equals(presentNodes, pub.presentNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, presentNodes);
    }

    @Override
    public String toString() {
        return "Pub{" +
                super.toString() +
                "id=" + id +
                ", presentNodes=" + presentNodes +
                '}';
    }
}
