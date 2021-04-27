package ru.rdude.rpg.game.logic.map;

public interface MapGenerationObserver {

    void update(GenerationProcess process, float current, float max);

}
