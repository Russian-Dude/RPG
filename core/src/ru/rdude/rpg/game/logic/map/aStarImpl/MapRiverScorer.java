package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

public class MapRiverScorer implements AStarScorer<Cell> {

/*    @Override
    public double computeCost(Cell from, Cell to) {
        return Functions.random(2);
    }*/

    @Override
    public int computeCost(Cell from, Cell to) {
        return Functions.random(0, 2);
    }

}
