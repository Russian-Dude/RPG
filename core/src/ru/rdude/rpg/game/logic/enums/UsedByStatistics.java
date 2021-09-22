package ru.rdude.rpg.game.logic.enums;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface UsedByStatistics {

    default UsedByStatistics[] enumValues() {
        return this.getClass().isEnum() ? this.getClass().getEnumConstants() : null;
    }

}
