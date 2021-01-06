package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellSide;

import java.util.Set;

public class City extends MapObject {

    public City(long id) {
        super(id);
        setPositions(Set.of(CellSide.CENTER));
    }

    @Override
    public MapObjectRoadAvailability roadAvailability() {
        return MapObjectRoadAvailability.MUST;
    }
}
