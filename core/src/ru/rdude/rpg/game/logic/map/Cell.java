package ru.rdude.rpg.game.logic.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.enums.WaterDepth;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.aStar.AStarNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Cell implements AStarNode {

    @JsonIgnore
    private GameMap gameMap;
    private long id;

    private int x;
    private int y;

    private Biom biom;
    private WaterDepth waterDepth;
    private Relief relief;
    private MapObject object;
    private Road road;

    private int lvl = 1;

    private Cell() {
    }

    public Cell(int x, int y, GameMap map) {
        this.x = x;
        this.y = y;
        this.gameMap = map;
        this.id = Functions.generateGuid();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Biom getBiom() {
        return biom;
    }

    public void setBiom(Biom biom) {
        this.biom = biom;
    }

    public Relief getRelief() {
        return relief;
    }

    public void setRelief(Relief relief) {
        this.relief = relief;
    }

    public MapObject getObject() {
        return object;
    }

    public void setObject(MapObject object) {
        this.object = object;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public WaterDepth getWaterDepth() {
        return waterDepth;
    }

    public void setWaterDepth(WaterDepth waterDepth) {
        this.waterDepth = waterDepth;
    }

    public boolean has(CellProperty property) {
        switch (property) {
            case ROAD:
                return road != null;
            case BIOM:
                return biom != null;
            case OBJECT:
                return object != null;
            case RELIEF:
                return relief != null;
            default:
                throw new IllegalArgumentException(property.name() + " not implemented yet");
        }
    }

    public boolean hasNot(CellProperty property) {
        return !has(property);
    }

    public long getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    // from which side of cell another cell locates
    public CellSide getRelativeLocation(Cell with) {
        if (!gameMap.equals(with.gameMap)) {
            return CellSide.NOT_RELATED;
        }
        if (x == with.x && y + 1 == with.y)
            return CellSide.NN;
        if (x == with.x && y - 1 == with.y)
            return CellSide.SS;

        if (x % 2 == 0) {
            if (x + 1 == with.x && y + 1 == with.y)
                return CellSide.NE;
            if (x + 1 == with.x && y == with.y)
                return CellSide.SE;
            if (x - 1 == with.x && y == with.y)
                return CellSide.SW;
            if (x - 1 == with.x && y + 1 == with.y)
                return CellSide.NW;
        } else {
            if (x + 1 == with.x && y == with.y)
                return CellSide.NE;
            if (x + 1 == with.x && y - 1 == with.y)
                return CellSide.SE;
            if (x - 1 == with.x && y - 1 == with.y)
                return CellSide.SW;
            if (x - 1 == with.x && y == with.y)
                return CellSide.NW;
        }
        return CellSide.NOT_RELATED;
    }

    // get cells in closeness radius. Closeness 1 and 2 implemented by
    // old implementation. This method still uses old one with closeness 1 though
    public List<Cell> getAroundCells(int closeness) {
        if (closeness == 1 || closeness == 2)
            return getCloseAroundCells(closeness);

        Set<Cell> checkedCells = new HashSet<>();
        Set<Cell> nextCheckingCells = new HashSet<>();
        checkedCells.add(this);
        nextCheckingCells.add(this);

        for (int i = 0; i < closeness; i++) {
            Set<Cell> currentCells = new HashSet<>(nextCheckingCells);
            nextCheckingCells.clear();
            for (Cell currentCell : currentCells) {
                List<Cell> aroundCells = currentCell.getAroundCells(1).stream()
                        .filter(p -> !checkedCells.contains(p))
                        .collect(Collectors.toList());
                checkedCells.addAll(aroundCells);
                nextCheckingCells.addAll(aroundCells);
            }
            if (i == closeness - 1)
                return new ArrayList<>(nextCheckingCells);
        }
        return new ArrayList<>();
    }

    // get points around given point
    // closeness - how close this points to given cell (1 - short radius, 2 - huge radius)
    private List<Cell> getCloseAroundCells(int closeness) {
        if (closeness > 2 || closeness < 1)
            throw new IllegalArgumentException("closeness in AroundCells() method is wrong");
        List<Cell> aroundCells = new ArrayList<>();
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        if (closeness == 1) {
            if (x > 0)
                aroundCells.add(gameMap.cell(x - 1, y));
            if (y > 0)
                aroundCells.add(gameMap.cell(x, y - 1));
            if (x < width - 1)
                aroundCells.add(gameMap.cell(x + 1, y));
            if (y < height - 1)
                aroundCells.add(gameMap.cell(x, y + 1));
            if (x % 2 == 0 && (x < width - 1) && (y < height - 1))
                aroundCells.add(gameMap.cell(x + 1, y + 1));
            else if ((x < width - 1) && y > 0)
                aroundCells.add(gameMap.cell(x + 1, y - 1));
            if (x % 2 == 0 && x > 0 && y < height - 1)
                aroundCells.add(gameMap.cell(x - 1, y + 1));
            else if (x > 0 && y > 0)
                aroundCells.add(gameMap.cell(x - 1, y - 1));
        } else if (closeness == 2) {
            if (x % 2 == 0 && x > 0 && y > 0)
                aroundCells.add(gameMap.cell(x - 1, y - 1));
            else if (x > 0 && y > 1)
                aroundCells.add(gameMap.cell(x - 1, y - 2));
            if (y > 1)
                aroundCells.add(gameMap.cell(x, y - 2));
            if (x % 2 == 0 && x < width - 1 && y > 1)
                aroundCells.add(gameMap.cell(x + 1, y - 1));
            else if (x < width - 1 && y > 2)
                aroundCells.add(gameMap.cell(x + 1, y - 2));
            if (x > 1 && y > 0)
                aroundCells.add(gameMap.cell(x - 2, y - 1));
            if (x < width - 2 && y > 0)
                aroundCells.add(gameMap.cell(x + 2, y - 1));
            if (x > 1)
                aroundCells.add(gameMap.cell(x - 2, y));
            if (x < width - 2)
                aroundCells.add(gameMap.cell(x + 2, y));
            if (x > 1 && y < height - 1)
                aroundCells.add(gameMap.cell(x - 2, y + 1));
            if (x < width - 2 && y < height - 1)
                aroundCells.add(gameMap.cell(x + 2, y + 1));
            if (y < height - 2)
                aroundCells.add(gameMap.cell(x, y + 2));
            if (x % 2 == 0 && x < width - 1 && y < height - 2)
                aroundCells.add(gameMap.cell(x + 1, y + 2));
            else if (x < width - 1 && y < height - 1)
                aroundCells.add(gameMap.cell(x + 1, y + 1));
            if (x % 2 == 0 && x > 0 && y < height - 2)
                aroundCells.add(gameMap.cell(x - 1, y + 2));
            else if (x > 0 && y < height - 1)
                aroundCells.add(gameMap.cell(x - 1, y + 1));
        }
        return aroundCells;
    }

    public List<Cell> getArea(int radius) {
        List<Cell> result = new ArrayList<>();
        for (int i = 1; i <= radius; i++) {
            result.addAll(getAroundCells(i));
        }
        return result;
    }

    public List<Cell> getArea(int radius, boolean include) {
        List<Cell> result = getArea(radius);
        if (include) {
            result.add(this);
        }
        return result;
    }

    public Point point() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return biom.toString();
    }
}
