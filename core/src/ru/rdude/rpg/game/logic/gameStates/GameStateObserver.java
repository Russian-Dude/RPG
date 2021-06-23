package ru.rdude.rpg.game.logic.gameStates;

public interface GameStateObserver {

    void update(GameStateBase oldValue, GameStateBase newValue);

}
