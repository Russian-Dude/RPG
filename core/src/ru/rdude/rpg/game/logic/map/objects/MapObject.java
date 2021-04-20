package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellProperty;
import ru.rdude.rpg.game.logic.map.CellSide;

import java.util.Set;

public abstract class MapObject extends CellProperty {

    private long id;
    private CellSide position;

    public MapObject(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CellSide getPosition() {
        return position;
    }

    public void setPosition(CellSide position) {
        this.position = position;
    }

    public abstract MapObjectRoadAvailability roadAvailability();
}
