package ru.rdude.rpg.game.utils.aStar;

import java.util.*;

public class AStarRouteFinder<T extends AStarNode> {

    private final AStarGraph<T> graph;
    private final AStarScorer<T> nextNodeScorer;
    private final AStarScorer<T> targetScorer;


    public AStarRouteFinder(AStarGraph<T> graph, AStarScorer<T> nextNodeScorer, AStarScorer<T> targetScorer) {
        this.graph = graph;
        this.nextNodeScorer = nextNodeScorer;
        this.targetScorer = targetScorer;
    }

    public Optional<List<T>> findRoute(T from, T to) {

        Queue<AStarRouteNode<T>> openSet = new PriorityQueue<>();
        Map<T, AStarRouteNode<T>> allNodes = new HashMap<>();

        AStarRouteNode<T> start = new AStarRouteNode<>(from, null, 0, targetScorer.computeCost(from, to));
        openSet.add(start);
        allNodes.put(from, start);

        while (!openSet.isEmpty()) {
            AStarRouteNode<T> next = openSet.poll();
            if (next.getCurrent().equals(to)) {
                List<T> route = new ArrayList<>();
                AStarRouteNode<T> current = next;
                do {
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current != null);
                return Optional.of(route);
            }

            graph.getConnections(next.getCurrent()).forEach(connection -> {
                AStarRouteNode<T> nextNode = allNodes.getOrDefault(connection, new AStarRouteNode<>(connection));
                allNodes.put(connection, nextNode);

                int newScore = next.getRouteScore() + nextNodeScorer.computeCost(next.getCurrent(), connection);
                if (newScore < nextNode.getRouteScore()) {
                    nextNode.setPrevious(next.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
                    openSet.add(nextNode);
                }
            });
        }
        return Optional.empty();
    }
}