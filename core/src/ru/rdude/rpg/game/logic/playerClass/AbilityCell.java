package ru.rdude.rpg.game.logic.playerClass;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface AbilityCell<T extends AbilityCell> {

    default T getThis() {
        return (T) this;
    }

}
