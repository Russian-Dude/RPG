package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.time.TurnChangeObserver;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.VisualTarget;

import java.util.Collection;
import java.util.stream.Stream;

@JsonPolymorphicSubType("battle")
public class Battle extends GameStateBase implements TurnChangeObserver {

    private final Party playerSide = Game.getCurrentGame().getCurrentPlayers();
    private Party enemySide;
    private Party turnOf;


    public Party getEnemySide() {
        return enemySide;
    }

    public Party getAllySide(Being<?> of) {
        for (Being<?> being : playerSide.getBeings()) {
            if (of == being)
                return playerSide;
        }
        for (Being<?> being : enemySide.getBeings()) {
            if (of == being)
                return enemySide;
        }
        return null;
    }

    public Party getEnemySide(Being<?> of) {
        return getAllySide(of) == playerSide ? enemySide : playerSide;
    }

    @Override
    public void turnUpdate() {
        // switch side:
        if (turnOf == playerSide)
            turnOf = enemySide;
        else turnOf = playerSide;
    }

    @Override
    public GameState getEnumValue() {
        return GameState.BATTLE;
    }
}
