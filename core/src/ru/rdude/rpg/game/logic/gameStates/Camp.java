package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("camp")
public class Camp extends GameStateBase {
    @Override
    public GameState getEnumValue() {
        return GameState.CAMP;
    }
}
