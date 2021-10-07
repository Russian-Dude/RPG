package ru.rdude.rpg.game.logic.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface CurrentGameObserver {

    enum Action { CREATED, BECOME_CURRENT, STARTED, ENDED }

    void update(Game game, Action action);

}
