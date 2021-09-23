package ru.rdude.rpg.game.logic.gold;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface GoldObserver {

    void updateGold(int oldValue, int newValue);

}
