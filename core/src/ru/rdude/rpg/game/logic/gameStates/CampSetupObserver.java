package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface CampSetupObserver {

    void updateBeforeSetUpCampAllowed(CampSetup campSetup, int oldValue, int newValue);

}
