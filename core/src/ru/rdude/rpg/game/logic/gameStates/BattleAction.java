package ru.rdude.rpg.game.logic.gameStates;

import ru.rdude.rpg.game.logic.entities.beings.Being;

import java.util.Map;

public enum BattleAction {

    WIN, LOSE;

    public Map<Being<?>, Double> lastExpRewards;

}
