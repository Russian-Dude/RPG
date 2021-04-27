package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.logic.map.objects.MapObjectRoadAvailability;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapRoadScorer implements AStarScorer<Cell> {

    @Override
    public int computeCost(Cell from, Cell to) {
        if (from.getRelativeLocation(to) == CellSide.NOT_RELATED) {
            return 500000;
        }
        if (to.getBiom() == Biom.WATER) {
            return from.getBiom() == Biom.WATER ? 25000 : to.getRoad() != null ? 0 : 35;
        }
        if (to.getRoad() != null
                || (to.getObject() != null && to.getBiom() == Biom.WATER && to.getObject().roadAvailability() != MapObjectRoadAvailability.NO ))
            return 0;
        if (to.getRelief() == Relief.MOUNTAINS)
            return 10;
        else if (to.getRelief() == Relief.FOREST)
            return 7;
        else if (to.getRelief() == Relief.PLAIN)
            return 5;
        return 0;
    }
}
