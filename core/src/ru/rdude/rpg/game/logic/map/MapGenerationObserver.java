package ru.rdude.rpg.game.logic.map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface MapGenerationObserver {

    void update(GenerationProcess process, float current, float max);

}
