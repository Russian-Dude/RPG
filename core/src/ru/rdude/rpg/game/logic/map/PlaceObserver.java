package ru.rdude.rpg.game.logic.map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface PlaceObserver {
    void update(Cell oldPosition, Cell newPosition);
}
