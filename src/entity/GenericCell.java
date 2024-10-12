package entity;

import java.util.Objects;

public class GenericCell {
    private int xCoordinate;
    private int yCoordinate;
    private final CellType cellType;

    public GenericCell(int xCoordinate, int yCoordinate, CellType cellType) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.cellType = cellType;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public CellType getCellType() {
        return cellType;
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
        return xCoordinate == genericCell.xCoordinate
                && yCoordinate == genericCell.yCoordinate
                && cellType == genericCell.cellType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xCoordinate, yCoordinate, cellType);
    }

    @Override
    public String toString() {
        return "GenericCell{" +
                "xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", cellType=" + cellType +
                '}';
    }
}
