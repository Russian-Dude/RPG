package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.map.bioms.BiomCellProperty;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.logic.map.reliefs.ReliefCellProperty;
import ru.rdude.rpg.game.utils.aStar.AStarNode;

import java.util.Set;

public class Cell implements AStarNode {

    private static long count = 0;
    private GameMap gameMap;
    private long id;

    private int x;
    private int y;

    private Set<Cell> connectedByRoads;

    private BiomCellProperty biom;
    private Water.DeepProperty deepProperty;
    private ReliefCellProperty relief;
    private MapObject object;
    private Road road;

    public Cell(int x, int y, GameMap map) {
        this.x = x;
        this.y = y;
        this.gameMap = map;
        this.id = count++;
    }


    public BiomCellProperty getBiom() {
        return biom;
    }

    public void setBiom(BiomCellProperty biom) {
        this.biom = biom;
        if (gameMap != null)
            gameMap.addCellPropertyMap(this, biom);
    }

    public ReliefCellProperty getRelief() {
        return relief;
    }

    public void setRelief(ReliefCellProperty relief) {
        this.relief = relief;
        if (gameMap != null)
            gameMap.addCellPropertyMap(this, relief);
    }

    public MapObject getObject() {
        return object;
    }

    public void setObject(MapObject object) {
        this.object = object;
        if (gameMap != null)
            gameMap.addCellPropertyMap(this, object);
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public Water.DeepProperty getDeepProperty() {
        return deepProperty;
    }

    public void setDeepProperty(Water.DeepProperty deepProperty) {
        this.deepProperty = deepProperty;
    }

    public CellProperty get(CellProperty.Type property) {
        switch (property) {
            case ROAD:
                return road;
            case BIOM:
                return biom;
            case OBJECT:
                return object;
            case RELIEF:
                return relief;
            default:
                throw new IllegalArgumentException(property.name() + " not implemented yet");
        }
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

    public boolean isConnectedByRoadWith(Cell cell) {
        return connectedByRoads.contains(cell);
    }

    public Set<Cell> getConnectedByRoads() {
        return connectedByRoads;
    }

    @Override
    public String toString() {
        return biom.toString();
    }
}
