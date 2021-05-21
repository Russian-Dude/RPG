package ru.rdude.rpg.game.logic.map.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.map.CellSide;

public class City extends MapObject {

    public City(@JsonProperty("id") long id) {
        super(id);
        setPosition(CellSide.CENTER);
    }

    @Override
    public MapObjectRoadAvailability roadAvailability() {
        return MapObjectRoadAvailability.MUST;
    }
}
