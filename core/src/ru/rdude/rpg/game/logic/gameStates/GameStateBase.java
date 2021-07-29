package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.enums.GameState;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public abstract class GameStateBase {

    public abstract GameState getEnumValue();

}
