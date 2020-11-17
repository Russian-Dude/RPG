package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Party;

public abstract class GameStateBase {

    protected Party playerSide;

    public Party getPlayerSide() {
        return playerSide;
    }
}
