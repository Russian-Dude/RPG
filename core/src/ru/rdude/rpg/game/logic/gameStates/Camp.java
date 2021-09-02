package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.GameStateStage;

@JsonPolymorphicSubType("camp")
public class Camp extends GameStateBase {
    @Override
    public GameState getEnumValue() {
        return GameState.CAMP;
    }

    @Override
    public GameStateStage getStage() {
        return null;
    }

    @Override
    public Party getAllySide(Being<?> of) {
        Party party = Game.getCurrentGame().getCurrentPlayers();
        return party.getBeings().contains(of) ? party : null;
    }
}
