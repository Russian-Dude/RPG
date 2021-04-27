package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.utils.Functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameMap {

    public final long guid;
    private Cell[][] map;
    private Map<CellProperty, Set<Cell>> cellsByProperty;

    public GameMap(int width, int height) {
        map = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = new Cell(x, y, this);
            }
        }
        guid = Functions.generateGuid();
    }

    public int getWidth() { return map.length; }
    public int getHeight() { return map[0].length; }

    public Cell cell(int x, int y) {
        return map[x][y];
    }

    public Cell cell(Point point) {
        return map[point.x][point.y];
    }

    public Set<Cell> getCellsWithProperty(CellProperty property) {
        if (!cellsByProperty.containsKey(property))
            return new HashSet<>();
        return cellsByProperty.get(property);
    }

    protected void addCellPropertyMap(Cell cell, CellProperty property) {
        if (cellsByProperty == null) {
            cellsByProperty = new HashMap<>();
        }
        if (!cellsByProperty.containsKey(property)) {
            cellsByProperty.put(property, new HashSet<>());
        }
        cellsByProperty.get(property).add(cell);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < map.length; x++) {
            builder.append("\n\r");
            for (int y = 0; y < map[1].length; y++) {
                builder.append(map[x][y]);
            }
        }
        return builder.toString();
    }

    public int nonNullCells(CellProperty.Type cellPropertyType) {
        int realNonNullCells = 0;
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                switch (cellPropertyType) {
                    case BIOM:
                        if (map[x][y].getBiom() != null) realNonNullCells++;
                        break;
                    case ROAD:
                        if (map[x][y].getRoad() != null) realNonNullCells++;
                        break;
                }
            }
        }
        return realNonNullCells;
    }
}
