package ru.rdude.rpg.game.commands;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.rdude.rpg.game.battleVisual.BattleVisual;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.mapVisual.MapStage;
import ru.rdude.rpg.game.ui.PlayerVisual;

import java.util.function.Consumer;

public enum Commands {

    NO_SUCH_COMMAND
            (string -> Game.getCurrentGame().getGameLogger().log("Command \"" + string + "\" does not exist")),

    WIN_BATTLE
            (string -> {
                final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
                if (currentGameState instanceof Battle) {
                    ((BattleVisual) Game.getGameVisual().getCurrentGameStateStage()).getVisualBeings()
                            .stream()
                            .filter(visualBeing -> ((Battle) currentGameState).getEnemySide().getBeings().contains(visualBeing.getBeing()))
                            .forEach(visualBeing -> ((Actor) visualBeing).remove());
                    ((Battle) currentGameState).win();
                } }),

    LOSE_BATTLE
            (string -> {
                final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
                if (currentGameState instanceof Battle) {
                    ((Battle) currentGameState).lose();
                } }),
    ;

    private final Consumer<String> action;

    Commands(Consumer<String> action) {
        this.action = action;
    }

    public void accept(String string) {
        action.accept(string);
    }
}
