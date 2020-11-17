package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;

public class Battle extends GameStateBase implements TurnChangeObserver {

    private Party enemySide;
    private Party turnOf;


    public Party getEnemySide() {
        return enemySide;
    }

    public Party getAllySide(Being of) {
        for (Being being : playerSide.getBeings()) {
            if (of == being)
                return playerSide;
        }
        for (Being being : enemySide.getBeings()) {
            if (of == being)
                return enemySide;
        }
        return null;
    }

    public Party getEnemySide(Being of) {
        return getAllySide(of) == playerSide ? enemySide : playerSide;
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
