package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellSide;

public abstract class MapObject {

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
