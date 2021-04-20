package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellSide;

public class City extends MapObject {

    public City(long id) {
        super(id);
        setPosition(CellSide.CENTER);
    }

    @Override
    public MapObjectRoadAvailability roadAvailability() {
        return MapObjectRoadAvailability.MUST;
    }
}
