package ru.rdude.rpg.game.logic.game;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.data.io.GameJsonSerializer;
import ru.rdude.rpg.game.logic.data.io.ModuleFileLoader;
import ru.rdude.rpg.game.logic.entities.beings.MonsterFactory;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateHolder;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.mapVisual.MapRenderer;
import ru.rdude.rpg.game.ui.AvatarCreator;
import ru.rdude.rpg.game.ui.ItemImageFactory;
import ru.rdude.rpg.game.visual.GameVisual;

public class Game {

    private static Game currentGame = new Game();

    private static final GameVisual gameVisual = new GameVisual();


    private final DragAndDrop itemsDragAndDrop;
    private final GameLogger gameLogger;
    private TimeManager timeManager;
    private SkillUser skillUser;
    private final GameStateHolder gameStateHolder;
    private Map gameMap;
    private final AvatarCreator avatarCreator;
    private final ItemImageFactory itemImageFactory;
    private final MonsterFactory monsterFactory;

    // io
    private ModuleFileLoader moduleFileLoader;
    private GameJsonSerializer gameJsonSerializer;

    public Game() {
        this.itemsDragAndDrop = new DragAndDrop();
        this.gameLogger = new GameLogger();
        this.avatarCreator = new AvatarCreator();
        this.itemImageFactory = new ItemImageFactory();
        this.gameJsonSerializer = new GameJsonSerializer();
        this.moduleFileLoader = new ModuleFileLoader(gameJsonSerializer, itemImageFactory);
        this.monsterFactory = new MonsterFactory();
        this.gameStateHolder = new GameStateHolder();
        this.timeManager = new TimeManager();
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public static GameVisual getGameVisual() {
        return gameVisual;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public SkillUser getSkillUser() {
        return skillUser;
    }

    public GameStateHolder getGameStateHolder() {
        return gameStateHolder;
    }

    public GameStateBase getCurrentGameState() {
        return gameStateHolder.getGameState();
    }

    public Map getGameMap() {
        return gameMap;
    }

    public void setGameMap(Map gameMap) {
        this.gameMap = gameMap;
    }

    public GameLogger getGameLogger() {
        return gameLogger;
    }

    public DragAndDrop getItemsDragAndDrop() {
        return itemsDragAndDrop;
    }

    public AvatarCreator getAvatarCreator() {
        return avatarCreator;
    }

    public ItemImageFactory getItemImageFactory() {
        return itemImageFactory;
    }

    public GameJsonSerializer getGameJsonSerializer() {
        return gameJsonSerializer;
    }

    public MonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    public ModuleFileLoader getModuleFileLoader() {
        return moduleFileLoader;
    }
}
