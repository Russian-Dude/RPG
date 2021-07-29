package ru.rdude.rpg.game.visual;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;

import java.util.Optional;

public class VisualBeingFinder {

    public Optional<? extends VisualBeing<?>> find(Being<?> being) {
        final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
        switch (currentGameState.getEnumValue()) {
            case MAP:
            case CAMP:
                return Game.getGameVisual().getUi().getPlayerVisuals().stream()
                        .filter(playerVisual -> playerVisual.getBeing().equals(being))
                        .findFirst();
            case BATTLE:
                // TODO: 22.07.2021 return visual being if it is battle
            default:
                return Optional.empty();
        }
    }
}
