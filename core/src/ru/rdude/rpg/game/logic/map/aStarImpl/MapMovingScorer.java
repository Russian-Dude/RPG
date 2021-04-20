package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Forest;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapMovingScorer implements AStarScorer<Cell> {

    private final Map map;

    public MapMovingScorer(Map map) {
        this.map = map;
    }

    @Override
    public int computeCost(Cell from, Cell to) {
        if (map.cellHasMonster(to) || to.getObject() != null) {
            return 5000000;
        }
        if (to.getRoad() != null && from.getRoad() != null) {
            return 0;
        }
        if (to.getBiom() instanceof Water) {
            switch (to.getDeepProperty()) {
                case RIVER:
                case SMALL:
                    return 50;
                case NORMAL:
                    return 100;
                case DEEP:
                    return 500;
            }
        }
        if (to.getRelief() instanceof Plain) {
            return 1;
        }
        if (to.getRelief() instanceof Forest || to.getRelief() instanceof Hills) {
            return 2;
        }
        if (to.getRelief() instanceof Mountains) {
            return 3;
        }
        return 0;
    }

}
