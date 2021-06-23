package ru.rdude.rpg.game.logic.time;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface DurationObserver {
    void update(Duration duration, boolean ends);
}
