package ru.rdude.rpg.game.logic.map.objects;

import ru.rdude.rpg.game.logic.map.CellSide;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Set;

public class Dungeon extends MapObject {

    public Dungeon(long id) {
        super(id);
        setPosition(CellSide.CENTER);
        // TODO: 11.04.2021 get position from dungeon visual data
    }

    @Override
    public MapObjectRoadAvailability roadAvailability() {
        // while not implemented uses random availability
        return Functions.randomBoolean() ? MapObjectRoadAvailability.CAN : Functions.randomBoolean() ? MapObjectRoadAvailability.MUST : MapObjectRoadAvailability.NO;
    }

}
