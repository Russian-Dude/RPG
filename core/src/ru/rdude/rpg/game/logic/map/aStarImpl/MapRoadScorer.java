package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.objects.MapObjectRoadAvailability;
import ru.rdude.rpg.game.logic.map.reliefs.Forest;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapRoadScorer implements AStarScorer<Cell> {

    @Override
    public int computeCost(Cell from, Cell to) {
        if (to.getBiom() == Water.getInstance()) {
            return from.getBiom() == Water.getInstance() ? 25000 : to.getRoad() != null ? 0 : 35;
        }
        if (to.getRoad() != null
                || (to.getObject() != null && to.getObject().roadAvailability() != MapObjectRoadAvailability.NO ))
            return 0;
        if (to.getRelief() instanceof Mountains)
            return 10;
        else if (to.getRelief() instanceof Hills)
            return 7;
        else if (to.getRelief() instanceof Forest)
            return 7;
        else if (to.getRelief() instanceof Plain)
            return 5;
        return 0;
    }
}
