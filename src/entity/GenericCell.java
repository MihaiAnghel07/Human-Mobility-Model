package entity;

import java.util.Objects;

public class GenericCell {
    private final int xCoordinate;
    private final int yCoordinate;

    public GenericCell(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GenericCell genericCell = (GenericCell) o;
        return xCoordinate == genericCell.xCoordinate && yCoordinate == genericCell.yCoordinate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCoordinate, yCoordinate);
    }

    @Override
    public String toString() {
        return "GenericCell{" +
                "xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                '}';
    }
}
