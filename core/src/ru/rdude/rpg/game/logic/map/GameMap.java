package ru.rdude.rpg.game.logic.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.utils.Functions;

public class GameMap {

    @JsonProperty
    public final long guid;

    private String name = "";

    @JsonProperty
    private final Cell[][] map;
    private Point startPoint;

    public GameMap(@JsonProperty("guid") long guid, @JsonProperty("map") Cell[][] map) {
        this.guid = guid;
        this.map = map;
        for (Cell[] cx : map) {
            for (Cell cy : cx) {
                cy.setGameMap(this);
            }
        }
    }

    public GameMap(int width, int height) {
        map = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = new Cell(x, y, this);
            }
        }
        guid = Functions.generateGuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    @JsonIgnore
    public int getWidth() { return map.length; }
    @JsonIgnore
    public int getHeight() { return map[0].length; }

    public Cell cell(int x, int y) {
        return map[x][y];
    }

    public Cell cell(Point point) {
        return map[point.x][point.y];
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

    public int nonNullCells(CellProperty cellProperty) {
        int realNonNullCells = 0;
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y].has(cellProperty)) {
                    realNonNullCells++;
                }
            }
        }
        return realNonNullCells;
    }
}
