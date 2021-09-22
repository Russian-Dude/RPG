package ru.rdude.rpg.game.logic.playerClass;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface CurrentPlayerClassObserver {

    void currentPlayerClassUpdate(PlayerClass oldValue, PlayerClass newValue);

}
