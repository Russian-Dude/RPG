package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.map.bioms.Biom;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.logic.map.reliefs.Relief;

public class Cell {

    private Biom biom;
    private Relief relief;
    private MapObject object;
    private Road road;

    public Biom getBiom() { return biom; }
    public void setBiom(Biom biom) { this.biom = biom; }

    public Relief getRelief() { return relief; }
    public void setRelief(Relief relief) { this.relief = relief; }

    public MapObject getObject() { return object; }
    public void setObject(MapObject object) { this.object = object; }

    public Road getRoad() { return road; }
    public void setRoad(Road road) { this.road = road; }

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

    @Override
    public String toString() {
        return biom.toString();
    }
}
