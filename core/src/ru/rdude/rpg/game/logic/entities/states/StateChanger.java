package ru.rdude.rpg.game.logic.entities.states;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

// interface for classes which change some states
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface StateChanger {
    boolean isStateOverlay();
}
