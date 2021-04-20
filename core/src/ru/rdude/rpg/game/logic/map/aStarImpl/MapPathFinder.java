package ru.rdude.rpg.game.logic.map.aStarImpl;

import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.utils.aStar.AStarGraph;
import ru.rdude.rpg.game.utils.aStar.AStarRouteFinder;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class MapPathFinder {

    private final AStarRouteFinder<Cell> routeFinder;
    private final AStarGraph<Cell> graph;

    public MapPathFinder(GameMap gameMap, AStarScorer<Cell> scorer) {
        this(gameMap, scorer, null);
    }

    public MapPathFinder(GameMap gameMap, AStarScorer<Cell> scorer, BiPredicate<Cell, Cell> connectionRestriction) {
        Set<Cell> nodes = new HashSet<>();
        Map<Cell, Set<Cell>> connections = new HashMap<>();
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Cell cell = gameMap.cell(x, y);
                nodes.add(cell);
                Set<Cell> aroundCells = cell.getAroundCells(1).stream()
                        .filter(c -> connectionRestriction == null || connectionRestriction.test(cell, c))
                        .collect(Collectors.toSet());
                connections.put(cell, aroundCells);
            }
        }
        graph = new AStarGraph<>(nodes, connections);
        routeFinder = new AStarRouteFinder<>(graph, scorer, scorer);
    }

    public Optional<List<Cell>> find(Cell from, Cell to) {
        return routeFinder.findRoute(from, to);
    }

    public void changeConnections(Cell node, Set<Cell> connections) {
        graph.changeConnections(node, connections);
    }
}
