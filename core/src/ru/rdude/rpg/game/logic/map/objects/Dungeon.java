package ru.rdude.rpg.game.logic.map.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.utils.Functions;

public class Dungeon extends MapObject {

    public Dungeon(@JsonProperty("id") long id) {
        super(id);
        setPosition(CellSide.CENTER);
        // TODO: 11.04.2021 get position from dungeon visual data
    }

    @Override
    public MapObjectRoadAvailability roadAvailability() {
        // TODO: 21.04.2021 while not implemented uses random availability
        return Functions.randomBoolean() ? MapObjectRoadAvailability.CAN : Functions.randomBoolean() ? MapObjectRoadAvailability.MUST : MapObjectRoadAvailability.NO;
    }

}
