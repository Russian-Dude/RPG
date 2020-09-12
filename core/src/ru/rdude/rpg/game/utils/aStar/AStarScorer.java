package ru.rdude.rpg.game.utils.aStar;

public interface AStarScorer<T extends AStarNode> {
    double computeCost(T from, T to);
}
