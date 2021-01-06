package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellProperty;
import ru.rdude.rpg.game.logic.map.CellSide;

import java.util.Set;

public abstract class MapObject extends CellProperty {

    private long id;
    private Set<CellSide> positions;

    public MapObject(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<CellSide> getPositions() {
        return positions;
    }

    public void setPositions(Set<CellSide> positions) {
        this.positions = positions;
    }

    public abstract MapObjectRoadAvailability roadAvailability();
}
