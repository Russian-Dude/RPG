package ru.rdude.rpg.game.visual;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;

import java.util.Optional;
import java.util.stream.Stream;

public class VisualBeingFinder {

    public Optional<? extends VisualBeing<?>> find(Being<?> being) {
        return Game.getGameVisual().getCurrentGameStateStage().getVisualBeings()
                .stream()
                .filter(visualBeing -> visualBeing.getBeing() == being)
                .findAny();
/*        final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
        switch (currentGameState.getEnumValue()) {
            case MAP:
            case CAMP:
                return Game.getGameVisual().getUi().getPlayerVisuals().stream()
                        .filter(playerVisual -> playerVisual.getBeing().equals(being))
                        .findFirst();
            case BATTLE:
                return Stream.of(Game.getGameVisual().getUi().getPlayerVisuals()
                        , Game.getCurrentGame().)
            default:
                return Optional.empty();
        }*/
    }
}
