package ru.rdude.rpg.game.utils.aStar;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AStarGraph<T extends AStarNode> {

    private final Set<T> nodes;
    private final Map<T, Set<T>> connections;

    public AStarGraph(Set<T> nodes, Map<T, Set<T>> connections) {
        this.nodes = nodes;
        this.connections = connections;
    }

    public T getNode (long id) {
        return nodes.stream()
                .filter(node -> node.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no such id"));
    }

    public Set<T> getConnections(T node) {
        return connections.get(node);
    }
}