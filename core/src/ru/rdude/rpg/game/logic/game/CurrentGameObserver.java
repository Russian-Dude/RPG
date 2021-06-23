package ru.rdude.rpg.game.logic.game;

public interface CurrentGameObserver {

    enum Action { CREATED, BECOME_CURRENT }

    void update(Game game, Action action);

}
