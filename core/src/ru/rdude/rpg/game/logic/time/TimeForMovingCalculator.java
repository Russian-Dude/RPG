package ru.rdude.rpg.game.logic.time;

import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;

public final class TimeForMovingCalculator {

    private TimeForMovingCalculator() { }

    public static int calculate(Cell from, Cell to) {
        if (from == null || to == null) {
            return 0;
        }
        return 5 + cellTime(from) + cellTime(to);
    }

    private static int cellTime(Cell cell) {
        return biomTime(cell) + roadTime(cell) + reliefTime(cell);
    }

    private static int biomTime(Cell cell) {
        switch (cell.getBiom()) {
            case SAND:
            case DEADLAND:
            case VOLCANIC:
                return 2;
            case SNOW:
            case SWAMP:
            case WATER:
            case JUNGLE:
                return 3;
            default:
                return 1;
        }
    }

    private static int roadTime(Cell cell) {
        return cell.getRoad() != null ? 0 : 3;
    }

    private static int reliefTime(Cell cell) {
        switch (cell.getRelief()) {
            case FOREST:
                return 5;
            case MOUNTAINS:
                return 10;
            default:
                return 0;
        }
    }

}
