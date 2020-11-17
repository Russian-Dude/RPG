package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.Camp;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;

import java.util.Arrays;

public enum GameState {

    BATTLE(Battle.class),
    MAP(Map.class),
    CAMP(Camp.class);

    private Class<? extends GameStateBase> cl;

    GameState(Class<? extends GameStateBase> cl) {
        this.cl = cl;
    }

    public Class<? extends GameStateBase> getClazz() {
        return cl;
    }

    public static GameState get(Class<? extends GameStateBase> cl) {
        return Arrays.stream(GameState.values())
                .filter(gameState -> gameState.cl == cl)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
