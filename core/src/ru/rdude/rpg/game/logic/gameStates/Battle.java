package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;

public class Battle extends GameState implements TurnChangeObserver {

    private Party playerSide;
    private Party enemySide;
    private Party turnOf;

    public Party getPlayerSide() {
        return playerSide;
    }

    public Party getEnemySide() {
        return enemySide;
    }

    @Override
    public void turnUpdate() {
        // switch side:
        if (turnOf == playerSide)
            turnOf = enemySide;
        else turnOf = playerSide;
        // buffs' actions:

        // buffs duration update:
      /*  turnOf.stream()
                .map(Being::buffs)
                .flatMap(Collection::stream)
                .forEach(Buff::turnUpdate);
        // global time update:
        Game.getCurrentGame().getTimeManager().turnUpdate();*/
    }
}
