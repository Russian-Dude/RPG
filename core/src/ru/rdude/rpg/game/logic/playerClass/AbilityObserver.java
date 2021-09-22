package ru.rdude.rpg.game.logic.playerClass;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface AbilityObserver {

    void updateAbility(Ability ability, boolean open, int oldLvl, int newLvl);

}
