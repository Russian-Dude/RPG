package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface PlayerReadyObserver {

    void update(Player player, boolean ready);

}
