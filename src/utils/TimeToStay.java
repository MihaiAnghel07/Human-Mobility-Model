package utils;

import entity.CellType;
import entity.GenericCell;

public enum TimeToStay {
    HOME(360),
    WORK(540),
    PUB(120),
    OTHER(60);

    private final int minutes;

    TimeToStay(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public static int getMinutes(GenericCell cell) {
        if (cell == null || cell.getCellType() == CellType.EMPTY)
            return 0;

        switch (cell.getCellType()) {
            case HOME: return HOME.minutes;
            case WORK: return WORK.minutes;
            case PUB: return PUB.minutes;
            case OTHER: return OTHER.minutes;
            default: return 0;
        }
    }

}
