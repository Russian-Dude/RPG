package ru.rdude.rpg.game.logic.time;

import ru.rdude.rpg.game.logic.gameStates.Map;

public final class TimeForMovingCalculator {

    private TimeForMovingCalculator() { }

    public static int calculate(Map.CellProperties from, Map.CellProperties to) {
        if (from == null || to == null) {
            return 0;
        }
        return 5 + cellTime(from) + cellTime(to);
    }

    private static int cellTime(Map.CellProperties cell) {
        return biomTime(cell) + roadTime(cell) + reliefTime(cell);
    }

    private static int biomTime(Map.CellProperties cell) {
        switch (cell.getCell().getBiom().asEnum()) {
            case SAND:
            case DEAD_LAND:
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

    private static int roadTime(Map.CellProperties cell) {
        return cell.getCell().getRoad() != null ? 0 : 3;
    }

    private static int reliefTime(Map.CellProperties cell) {
        switch (cell.getCell().getRelief().asEnum()) {
            case FOREST:
                return 5;
            case MOUNTAINS:
                return 10;
            default:
                return 0;
        }
    }

}
