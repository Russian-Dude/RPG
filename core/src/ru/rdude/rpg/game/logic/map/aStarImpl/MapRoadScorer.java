package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.reliefs.Forest;
import ru.rdude.rpg.game.logic.map.reliefs.Hills;
import ru.rdude.rpg.game.logic.map.reliefs.Mountains;
import ru.rdude.rpg.game.logic.map.reliefs.Plain;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapRoadScorer implements AStarScorer<Cell> {

    @Override
    public double computeCost(Cell from, Cell to) {
        if (to.getBiom() == Water.getInstance())
            return 2500d;
        if (to.getRoad() != null)
            return 0d;
        if (to.getRelief() instanceof Mountains)
            return Functions.random(5d, 7d);
        else if (to.getRelief() instanceof Hills)
            return Functions.random(5d, 6d);
        else if (to.getRelief() instanceof Forest)
            return Functions.random(5d, 5.5d);
        else if (to.getRelief() instanceof Plain)
            return Functions.random(4.5d, 6d);
        return 0;
    }
}
