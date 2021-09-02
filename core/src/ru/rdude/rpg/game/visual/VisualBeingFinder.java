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
    }
}
