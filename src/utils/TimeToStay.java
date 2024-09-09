package utils;

import entity.GenericCell;

public enum TimeToStay {
    HOME(6),
    WORK(9),
    PUB(2),
    OTHER(1);

    private final int hours;

    TimeToStay(int hours) {
        this.hours = hours;
    }

    public int getHours() {
        return hours;
    }

    public static int getHours(GenericCell cell) {
        if (cell == null)
            return 0;

        switch (cell.getCellType()) {
            case HOME: return HOME.hours;
            case WORK: return WORK.hours;
            case PUB: return PUB.hours;
            case OTHER: return OTHER.hours;
            default: return 0;
        }
    }

}
