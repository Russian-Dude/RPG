package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface BuffObserver {
    void update(Buff buff, boolean ends);
}
