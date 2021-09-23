package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.visual.GameStateStage;

public class MainMenuGameState extends GameStateBase {
    @Override
    public GameState getEnumValue() {
        return null;
    }

    @Override
    public GameStateStage getStage() {
        return null;
    }

    @Override
    public Party getAllySide(Being<?> of) {
        return null;
    }

    @Override
    public void lose() {

    }
}
