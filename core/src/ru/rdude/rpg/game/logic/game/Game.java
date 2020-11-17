package ru.rdude.rpg.game.logic.game;

import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeManager;

public class Game {

    private static Game currentGame;

    private TimeManager timeManager;
    private SkillUser skillUser;
    private GameStateBase currentGameState;
    private Map gameMap;

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public SkillUser getSkillUser() {
        return skillUser;
    }

    public GameStateBase getCurrentGameState() {
        return currentGameState;
    }

    public Map getGameMap() {
        return gameMap;
    }

    public static Game getCurrentGame() {
        return currentGame;
    }
}
