package ru.rdude.rpg.game.utils.aStar;

public interface AStarScorer<T extends AStarNode> {
    int computeCost(T from, T to);
}
