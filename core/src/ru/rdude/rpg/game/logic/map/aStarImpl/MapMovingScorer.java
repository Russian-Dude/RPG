package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapMovingScorer implements AStarScorer<Cell> {

    private final Map map;

    public MapMovingScorer(Map map) {
        this.map = map;
    }

    @Override
    public int computeCost(Cell from, Cell to) {
        if (map.cellHasMonster(to) || to.getObject() != null) {
            return 25000;
        }
        if (to.getRoad() != null && from.getRoad() != null) {
            return 0;
        }
        if (to.getBiom() == Biom.WATER) {
            switch (to.getWaterDepth()) {
                case RIVER:
                case SMALL:
                    return 50;
                case NORMAL:
                    return 100;
                case DEEP:
                    return 500;
            }
        }
        if (to.getRelief() == Relief.PLAIN) {
            return 1;
        }
        if (to.getRelief() == Relief.FOREST) {
            return 2;
        }
        if (to.getRelief() == Relief.MOUNTAINS) {
            return 3;
        }
        return 0;
    }

}
